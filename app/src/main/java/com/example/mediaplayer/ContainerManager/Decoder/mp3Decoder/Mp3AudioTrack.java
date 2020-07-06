package com.example.mediaplayer.ContainerManager.Decoder.mp3Decoder;

import android.media.AudioTrack;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.ContainerManager.Decoder.mp3Decoder.Mp3Decoder;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Frame.mp3Frame.Mp3Data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Mp3AudioTrack  {
    Mp3Decoder mp3Decoder;
    public Mp3AudioTrack(InputStream in) {
        mp3Decoder = new Mp3Decoder(in);
        try {
            mp3Decoder.decode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Thread decodeFullyInto(AudioTrack audioTrack) {
        Thread thread  = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true)
                {
                    try {
                        if (!Mp3Decoder.decodeFrame()) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    audioTrack.write(Mp3Decoder.getTrackData().samplesBuffer , 0
                            , Mp3Decoder.getTrackData().samplesBuffer.length);

                }
            }
        });
        thread.start();
        return thread;
    }

    public boolean isStereo() {
        return Mp3Decoder.getTrackData().stereo == 1;
    }


}
