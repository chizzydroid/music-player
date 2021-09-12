package com.chizzy.musicplayer;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.sql.Wrapper;
import java.util.ArrayList;
public class MusicAdapter  extends RecyclerView.Adapter<MusicAdapter.MyVieHolder> {
    private Context mContext;
  static ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext,ArrayList<MusicFiles> mFiles){
        this.mFiles = mFiles;
        this.mContext = mContext;

    }
        @NonNull
        @Override
        public MyVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
            return new MyVieHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull MyVieHolder holder, int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if (image != null){

            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);

        }else {
            Glide.with(mContext)
                    .load(R.drawable.my)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, playerActivity2.class);
                intent.putExtra("Position", position);
                mContext.startActivity(intent);
            }
        });
        holder.menuDelete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.getMenuInflater().inflate(R.menu.popup,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item) -> {
                    switch (item.getItemId()) {
                        case R.id.delete:
                            Toast toast = Toast.makeText(mContext, "Delete Clicked!!", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            deleteFile(position, v);
                            break;
                    }
                    return true;
                });

            }

        });
    }

    private void deleteFile(int position,View v){ Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            Long.parseLong(mFiles.get(position).getId()));// content://
            File file = new File(mFiles.get(position).getPath());
            boolean deleted = file.delete(); // delete file
            if (deleted) {
                mContext.getContentResolver().delete(contentUri,null,null);
                mFiles.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, mFiles.size());
                Snackbar.make(v, "File Deleted :", Snackbar.LENGTH_LONG)
                        .show();

                //file in the Sd card
            }else {
                Snackbar.make(v, "File can't Delete:", Snackbar.LENGTH_LONG)
                        .show();

            }
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class  MyVieHolder extends RecyclerView.ViewHolder{
        TextView file_name;
        ImageView album_art,menuDelete;
        public MyVieHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
          album_art = itemView.findViewById(R.id.music_imeg);
           menuDelete= itemView.findViewById(R.id.menuDelete);

        }

    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
    void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
