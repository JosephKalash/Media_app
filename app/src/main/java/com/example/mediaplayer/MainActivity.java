package com.example.mediaplayer;

import androidx.annotation.NonNull;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;

import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Container.MB3Container;
import com.example.mediaplayer.Data.Container.WavContainer;
import com.example.mediaplayer.Data.Container.mp4.MB4Container;
import com.example.mediaplayer.MediaControl.AudioPlayerFragment;
import com.example.mediaplayer.MediaControl.PlaybackListener;
import com.example.mediaplayer.MediaControl.PlaybackAudio;
import com.example.mediaplayer.reader.MediaFile;
import com.example.mediaplayer.reader.StorageFilesReader;
import com.example.mediaplayer.services.OnClearFromRecentService;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.mediaplayer.ContainerManager.ContainerManager;
import com.example.mediaplayer.Data.Data;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnClickFileListener {
    Data data;
    List<String> Format;
    ViewPager viewPager;
    private TabLayout mTabLayout;
    private static final int REQUEST_STORAGE = 1;
    private static PlaybackAudio playback;
    static StorageFilesReader stReader;
    private static Container mContainer;
    private static ContainerManager containerManager;
    private AudioManager mAudioManager;
    NotificationManager notifiManager;
    static int currentPosition;
    static String currentFileName;

/*

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.
                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                // Stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };
*/

    // this methods will be called from recycler view holder after clicking on an item
    @Override
    public void onClick(MediaFile file) {
        currentFileName = file.getName();
        ContentResolver resolver = getContentResolver();
        try (InputStream stream = resolver.openInputStream(file.getUri())) {

            if (file.getName().endsWith("wav")) {
                mContainer = new WavContainer(stream);
            }

            if (file.getName().toLowerCase().endsWith("mp3")) {
                currentPosition = stReader.getAudioFies().indexOf(file);
                InputStream in = getInputStreamFromUri(this,file.getUri());
                if(playback.playing())
                    playback.stop();
                mContainer = new MB3Container(in,playback);

                setNotification(this,currentFileName,R.drawable.pause_notifi , stReader.getAudioFies().size()-1);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, AudioPlayerFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().hide();
                }
            }

            if (file.getName().endsWith("mp4")) {
                mContainer = new MB4Container(stream);
            }
            containerManager = new ContainerManager(mContainer);
            containerManager.StartManaging();

        } catch (IOException | WavFileException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        stReader = new StorageFilesReader(getApplicationContext());
        checkStorageAccessPermission();

        viewPager = findViewById(R.id.pager);
        viewPager.setId(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(viewPager);
        playback = new PlaybackAudio(new PlaybackListener() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onCompletion() {

            }
        });

        //for notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
            registerReceiver(broadcast, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        }

    }

    public void createChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(MediaNotificatioin.CHANNEL_ID,"track",
                    NotificationManager.IMPORTANCE_LOW);

            notifiManager = getSystemService(NotificationManager.class);
            if(notifiManager != null)
                notifiManager.createNotificationChannel(channel);
        }
    }
    //deal with buttons on notification
    BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            switch (action){
                case MediaNotificatioin.PREVIOUS:
                    playPreviousSong();
                    break;
                case MediaNotificatioin.PLAY:
                    if(playback.playing()) {
                        setNotification(MainActivity.this,currentFileName,R.drawable.play_notifi
                                , stReader.getAudioFies().size()-1);
                        playback.pause();
                    }
                    else{
                        setNotification(MainActivity.this,currentFileName,R.drawable.pause_notifi
                                , stReader.getAudioFies().size()-1);
                        playback.resume();
                    }
                    break;
                case MediaNotificatioin.NEXT:
                    playNextSong();
                    break;
            }
        }
    };
    public void playNextSong(){
        if(playback.playing())
            playback.stop();
        MediaFile file = stReader.getAudioFies().get(currentPosition-1);
        currentPosition-=1;
        currentFileName = file.getName();
        setNotification(MainActivity.this,currentFileName,R.drawable.pause_notifi ,stReader.getAudioFies().size()-1);
       InputStream in = getInputStreamFromUri(MainActivity.this,file.getUri());
        playback.next(in);

    }
    public void playPreviousSong(){
        if(playback.playing())
            playback.stop();
        MediaFile file = stReader.getAudioFies().get(currentPosition+1);
        currentPosition+=1;
        currentFileName = file.getName();
        setNotification(MainActivity.this,currentFileName,R.drawable.pause_notifi,stReader.getAudioFies().size()-1);
        InputStream in = getInputStreamFromUri(MainActivity.this,file.getUri());
        playback.previous(in);
    }

    public static void setNotification(Context context, String name,int playButton , int size){
        MediaNotificatioin.createNotification(context,name.replace(".mp3",""),
                playButton,currentPosition,size);
    }

    public static InputStream getInputStreamFromUri(Context context, Uri uri){
        try {
            return new BufferedInputStream(
                    new FileInputStream(
                            new File(Objects.requireNonNull(getPathFromUri(context, uri)))));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.fragment_container).setVisibility(View.INVISIBLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(playback.playing())
            playback.stop();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifiManager.cancelAll();
            unregisterReceiver(broadcast);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //for first time data will be loaded here
                //then it will be loaded in splash screen
                //because if we could not have permission then we could not load data in splash screen window
                stReader.initializeReader();
            }
        }
    }

    private void checkStorageAccessPermission() {

        //ContextCompat use to retrieve resources. It provide uniform interface to access resources.
        //check if permission wasn't granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //ask for permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);

        } else{//load files if permission was granted
            stReader.initializeReader();
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        @NonNull
        public Fragment getItem(int i) {
            PageFragment fragment;

            if (i == 0) {
                fragment = PageFragment.newInstance(i);
                fragment.setOnClickListener(MainActivity.this);
            }
            else if (i == 1) {
                fragment = PageFragment.newInstance(i);
                fragment.setOnClickListener(MainActivity.this);
            }
            else {
                fragment = PageFragment.newInstance(i);
                fragment.setOnClickListener(MainActivity.this);
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Music";
            }
            if (position == 1) {
                return "Video";
            }
            if (position == 2) {
                return "Playlist";
            }
            return "";
        }
    }
    public static String getPathFromUri(final Context context, final Uri uri) {


        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
