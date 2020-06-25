package com.example.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.mediaplayer.ContainerManager.ContainerManager;
import com.example.mediaplayer.Data.Data;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Data data;
    ContainerManager containerManager;
    List<String> Format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}