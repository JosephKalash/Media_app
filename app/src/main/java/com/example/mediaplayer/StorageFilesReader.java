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
        for (int i=0;i< files.length;i++)
        {
            path = files[i].getParent().replace("/Android/data/","")
                    .replace(context.getPackageName(),"");
            storageDir = new File(path);
            load_Directory_Files(storageDir);
        }

    }
    private void load_Directory_Files(File directory){
        File[] filesList = directory.listFiles();
        String name;

        if(filesList != null && filesList.length > 0)
            for (int i=0; i<filesList.length; i++){

                if(filesList[i].isDirectory())
                    load_Directory_Files(filesList[i]);

                else { //it's file
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
