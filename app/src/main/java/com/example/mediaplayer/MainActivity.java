package com.example.mediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder;
import com.example.mediaplayer.MediaControl.VideoRender;
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


import java.util.List;


public class MainActivity extends AppCompatActivity {
    Data data;
    ContainerManager containerManager;
    List<String> Format;
    ViewPager viewPager;
    static StorageFilesReader stReader;
    private TabLayout mTabLayout;
    private boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);


        stReader = new StorageFilesReader();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //load data here
                //for first time data will be loaded here
                //then it will be loaded in splash screen
                //because if we could not have permission then we could not load data in splash screen window
                stReader.loadMediaFiles(this);
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
                    1);

        } else{//load files if permission was granted
            stReader.loadMediaFiles(this);}
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if (resultCode == Activity.RESULT_OK){
                    Uri fileUri = data.getData();
                    getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
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
