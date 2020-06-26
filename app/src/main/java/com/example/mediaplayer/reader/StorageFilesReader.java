package com.example.mediaplayer.reader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageFilesReader {
    private static final String[] MEDIA_EXTENSIONS = {".mp4",".mp3",".wav", ".jpeg"};
    private static final String[] AUDIO_EXTENSIONS = {".mp3",".wav"};
    private static final String[] VIDEO_EXTENSIONS = {".mp4"};

    private List audio = Arrays.asList(AUDIO_EXTENSIONS);
    private List video = Arrays.asList(VIDEO_EXTENSIONS);

    private ArrayList<Audio> mAudios = new ArrayList<>();
    private ArrayList<Video> mVideos = new ArrayList<>();


    public void initializeReader(Context context) {
        loadAudioFiles(context);
        loadVideoFiles(context);
    }

    // TODO: search for a way to get files by extension here, not in the get method
    private void loadVideoFiles(Context context){

        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE
        };

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
               null
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                mVideos.add(new Video(contentUri, name, 0, size));
            }
        }

    }
    private void loadAudioFiles(Context context) {


        String[] projection = new String[] {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.SIZE
        };

        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                null
        )) {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int size = cursor.getInt(sizeColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                mAudios.add(new Audio(contentUri, name, 0, size));
            }
        }
    }

    public ArrayList<MediaFile> getAudioFies() {
        ArrayList<MediaFile> supportedAudios = new ArrayList<>();

        for (int i = 0; i < mAudios.size(); i ++) {

            for (String extension : AUDIO_EXTENSIONS) {
                if (mAudios.get(i).getName().endsWith(extension)) {
                    supportedAudios.add(mAudios.get(i));
                }
            }

        }
        return supportedAudios;
    }

    public ArrayList<MediaFile> getVideoFies() {
        ArrayList<MediaFile> supportedVideos = new ArrayList<>();

        for (int i = 0; i < mVideos.size(); i++) {

            for (String extension : VIDEO_EXTENSIONS) {
                if (mVideos.get(i).getName().endsWith(extension)) {
                    supportedVideos.add(mVideos.get(i));
                }
            }

        }
        return supportedVideos;
    }

    public MediaFile getAudioFileByName(String name){
        for (int i = 0; i < mAudios.size(); i ++) {
            if (mAudios.get(i).getName().equals(name)) {
                return mAudios.get(i);
            }
        }
        return null;
    }

    public MediaFile getVideoFileByName(String name){
        for (int i = 0; i < mVideos.size(); i ++) {
            if (mVideos.get(i).getName().equals(name)) {
                return mVideos.get(i);
            }
        }
        return null;
    }

}
