package com.example.mediaplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.mediaplayer.ContainerManager.ContainerManager;
import com.example.mediaplayer.Data.Data;

import java.util.List;

public class MainActivity extends AppCompatActivity {
Data data;
ContainerManager containerManager;
List<String> Format;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private StorageFilesReader stReader;
    private boolean permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stReader = new StorageFilesReader();
        //checking permission
        checkStorageAccessPermission();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //to avoid lack in scrolling
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewAdapter = new RecyclerViewAdapter(stReader.getAllMediaFiles(),this);

        recyclerView.setAdapter(recyclerViewAdapter);

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

                recyclerViewAdapter.notifyDataSetChanged();
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

}

