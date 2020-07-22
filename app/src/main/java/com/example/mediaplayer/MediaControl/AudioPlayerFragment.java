package com.example.mediaplayer.MediaControl;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mediaplayer.MainActivity;
import com.example.mediaplayer.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class AudioPlayerFragment extends Fragment implements Runnable{
    private ImageButton nextButton;
    private ImageButton playButton;
    private ImageButton previousButton;
    private ImageButton shuffleButton;
    private ImageButton repeatButton;
    private SeekBar timeProgress;
    private TextView fullDuration;
    private TextView currentDuration;
    private TextView songName;
    private Thread timeThread;

    public AudioPlayerFragment(){}

    public static AudioPlayerFragment newInstance(){return new AudioPlayerFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.audio_control_layout, container, false);

        nextButton = view.findViewById(R.id.next);
        playButton = view.findViewById(R.id.play);
        previousButton = view.findViewById(R.id.previous);
        shuffleButton = view.findViewById(R.id.shuffle);
        repeatButton = view.findViewById(R.id.repeat);
        timeProgress = view.findViewById(R.id.time_seek_bar);
        fullDuration = view.findViewById(R.id.tv_full_time);
        currentDuration = view.findViewById(R.id.tv_current_time);
        songName = view.findViewById(R.id.tv_song);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        nextButton.setOnClickListener(v -> {
            MainActivity.playNextSong(getContext());
            timeProgress.setMax(Integer.parseInt(MainActivity.getCurrentFileDuration()));
        });

        playButton.setOnClickListener(v -> {
            MainActivity.playSong(getContext());
        });

        previousButton.setOnClickListener(v -> {
            MainActivity.playPreviousSong(getContext());
            timeProgress.setMax(Integer.parseInt(MainActivity.getCurrentFileDuration()));
        });

        shuffleButton.setOnClickListener(v -> {
            Toast.makeText(getContext(),"hello motherfuck",Toast.LENGTH_SHORT).show();
        });

        repeatButton.setOnClickListener(v -> {
            Toast.makeText(getContext(),"hello motherfuck",Toast.LENGTH_SHORT).show();
        });

        songName.setText(MainActivity.getCurrentFileName());

       String time = getTime(Long.parseLong(MainActivity.getCurrentFileDuration()));
        fullDuration.setText(time);

        timeProgress.setEnabled(true);
        timeProgress.setMax(Integer.parseInt(MainActivity.getCurrentFileDuration()));
        timeThread = new Thread(this);
        timeThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        timeThread.interrupt();
        timeThread = null;
    }

    @Override
    public void run() {
        AtomicInteger currentDur = new AtomicInteger();
        int fullDur = Integer.parseInt(MainActivity.getCurrentFileDuration());

        try {
            while (currentDur.get() <= fullDur && !Thread.currentThread().isInterrupted() && MainActivity.shouldPlay()){
                getActivity().runOnUiThread(() -> {

                    timeProgress.setProgress(currentDur.getAndIncrement() * 1000);
                     String time = getTime(currentDur.longValue()*1000);
                    currentDuration.setText(time);
                });
                Thread.sleep(1000);
            }
        } catch (InterruptedException ignored) {
        }
    }
    public String getTime(Long time){
        return String.format("%d:%d%d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time),
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );
    }

}
