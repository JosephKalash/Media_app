package com.example.mediaplayer.MediaControl;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.mediaplayer.MainActivity;
import com.example.mediaplayer.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AudioPlayerFragment extends Fragment {
    private ImageButton nextButton;

    public AudioPlayerFragment(){}

    public static AudioPlayerFragment newInstance(){return new AudioPlayerFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_control_layout, container, false);

        nextButton = view.findViewById(R.id.next);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        nextButton.setOnClickListener(v -> {
            Toast.makeText(getContext(),"hello motherfuck",Toast.LENGTH_SHORT).show();
        });
    }
}
