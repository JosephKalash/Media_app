package com.example.mediaplayer;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.content.ContextCompat;

public class StorageFilesReader {
    private static final String[] MEDIA_EXTENSIONS = {".mp4",".mp3",".wav", ".jpeg"};
    private static final String[] AUDIO_EXTENSIONS = {".mp3",".wav"};
    private static final String[] VIDEO_EXTENSIONS = {".mp4"};

    private List audio = Arrays.asList(AUDIO_EXTENSIONS);
    private List video = Arrays.asList(VIDEO_EXTENSIONS);

    //all loaded files will be here
    private ArrayList<File> allMediaFiles = new ArrayList<>();
    private ArrayList<File> audioFiles = new ArrayList<>();
    private ArrayList<File> videoFiles = new ArrayList<>();



    void loadMediaFiles(Context context){
        File storageDir;
        String path;
        File[] files;
        //load data here

        files = ContextCompat.getExternalFilesDirs(context.getApplicationContext(),null);
        for (File file : files) {
            path = file.getParent().replace("/Android/data/", "")
                    .replace(context.getPackageName(), "");
            storageDir = new File(path);
            loadDirectoryFiles(storageDir);
        }

    }
    void loadDirectoryFiles(File directory){
        File[] filesList = directory.listFiles();
        String name;
        if(filesList != null && filesList.length > 0)
            for (int i=0; i<filesList.length; i++){

                if(filesList[i].isDirectory())
                    loadDirectoryFiles(filesList[i]);

                else { //it's a file
                    name = filesList[i].getName().toLowerCase();
                    for (String extension: MEDIA_EXTENSIONS)
                        //check the type of file
                        if(name.endsWith(extension)){
                            allMediaFiles.add(filesList[i]);
                            //when we found file
                            if (audio.contains(extension)) {
                                audioFiles.add(filesList[i]);
                            }
                            if (video.contains(extension)) {
                                videoFiles.add(filesList[i]);
                            }
                            break;
                        }
                }
            }
    }

    public ArrayList<File> getAllMediaFiles() {
        return allMediaFiles;
    }
    public ArrayList<File> getAudioFies() { return audioFiles;}
    public ArrayList<File> getVideoFies() { return  videoFiles;}

}
