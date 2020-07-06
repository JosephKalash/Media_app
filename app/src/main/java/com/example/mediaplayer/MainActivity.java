package com.example.mediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Container.MB3Container;
import com.example.mediaplayer.Data.Container.WavContainer;
import com.example.mediaplayer.Data.Container.mp4.MB4Container;
import com.example.mediaplayer.reader.MediaFile;
import com.example.mediaplayer.reader.StorageFilesReader;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.mediaplayer.ContainerManager.ContainerManager;
import com.example.mediaplayer.Data.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Data data;
    List<String> Format;
    ViewPager viewPager;
    private TabLayout mTabLayout;
    private static final int REQUEST_STORAGE = 1;

    static StorageFilesReader stReader;
    private static Container mContainer;
    private static ContainerManager containerManager;


    // this methods will be called from recycler view holder after clicking on an item
    public static void doActionToFile(String name, Context context) {
        MediaFile file = stReader.getFileByName(name);

        ContentResolver resolver = context.getContentResolver();
        try (InputStream stream = resolver.openInputStream(file.getUri())) {

            if (file.getName().endsWith("wav")) {
                mContainer = new WavContainer(stream);
            }

            if (file.getName().endsWith("mp3")) {
                mContainer = new MB3Container(stream);
            }
            if (file.getName().endsWith("mp4")) {
                mContainer = new MB4Container(stream);
            }
            containerManager = new ContainerManager(mContainer);
            containerManager.StartManaging();

        } catch (IOException | WavFileException e) {
            e.printStackTrace();
        }
        /*
        *
            MyCanavas=new MyCanvas(this);
            MyCanavas.setBackgroundColor(Color.RED);
            setContentView(MyCanavas);
        */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);

        stReader = new StorageFilesReader(getApplicationContext());
        checkStorageAccessPermission();

        viewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(viewPager);

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
        public Fragment getItem(int i) {
            PageFragment fragment = null;

            if (i == 0) {
                fragment = PageFragment.newInstance(i);
            }
            else if (i == 1) {
                fragment = PageFragment.newInstance(i);
            }
            else {
                fragment = PageFragment.newInstance(i);
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


}
