package com.example.mediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediaplayer.ContainerManager.ContainerManager;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Container.WavContainer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    WavContainer wavContainer;
    private Context mContext;
    private List<File> mediaFiles;
    int mImageResourceId;
ContainerManager containerManager;
    RecyclerViewAdapter(List<File> mediaFiles , Context mContext, int thumbnailImage){
        this.mContext = mContext;
        this.mediaFiles = mediaFiles;
        mImageResourceId = thumbnailImage;
        wavContainer = new WavContainer();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list,
                parent,false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileLayoutHolder)holder).videoTitle.setText(mediaFiles.get(position).getName().toString());
        ((FileLayoutHolder)holder).thumbnail.setImageResource(mImageResourceId);
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;
        TextView videoTitle;
        ImageView ic_more_btn;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.videotitle);
            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);

            videoTitle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(videoTitle.getText().toString().endsWith(".wav")){
               File file = MainActivity.getContainer(videoTitle.getText().toString());
                InputStream istream;
                try {
                istream = new FileInputStream(file);
                    wavContainer.setIStream(istream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                containerManager=new ContainerManager(wavContainer);
                try {
                    containerManager.Parse();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WavFileException e) {
                    e.printStackTrace();
                }

            }
            }


        }
    }

