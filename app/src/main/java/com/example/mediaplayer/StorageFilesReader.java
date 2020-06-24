package com.example.mediaplayer;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;

public class StorageFilesReader {
    public static final String[] mediaExtensions = {".mp4","mp3",".wav"};

    //all loaded files will be here
    private ArrayList<File> allMediaFiles = new ArrayList<>();


    public void loadMediaFiles(Context context){
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
    private void loadDirectoryFiles(File directory){
        File[] filesList = directory.listFiles();
        String name;

        if(filesList != null && filesList.length > 0)
            for (int i=0; i<filesList.length; i++){

                if(filesList[i].isDirectory())
                    loadDirectoryFiles(filesList[i]);

                else { //it's a file
                    name = filesList[i].getName().toLowerCase();
                    for (String extension: mediaExtensions)
                        //check the type of file
                        if(name.endsWith(extension)){
                            allMediaFiles.add(filesList[i]);
                            //when we found file
                            break;
                        }
                }
            }
    }

    public ArrayList<File> getAllMediaFiles() {
        return allMediaFiles;
    }

    public void setAllMediaFiles(ArrayList<File> allMediaFiles) {
        this.allMediaFiles = allMediaFiles;
    }
}
