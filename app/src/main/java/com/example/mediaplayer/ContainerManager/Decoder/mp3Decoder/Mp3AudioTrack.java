package com.example.mediaplayer.ContainerManager.Decoder.mp3Decoder;

import android.media.AudioTrack;
import android.util.Log;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.ContainerManager.Decoder.mp3Decoder.Mp3Decoder;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Frame.mp3Frame.Mp3Data;
import com.example.mediaplayer.MediaControl.PlaybackThread;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Mp3AudioTrack  {
    Mp3Decoder.SoundData soundData;
    public Mp3AudioTrack(InputStream in) {
        try {
            soundData = Mp3Decoder.init(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(soundData == null)
            Log.d("fuck","null");
    }

    public Thread decodeFullyInto(AudioTrack audioTrack) {
        Thread thread  = new Thread(() -> {

            while (PlaybackThread.mShouldContinue)
            {
                try {
                    if (!Mp3Decoder.decodeFrame(soundData)) break;
                    Log.i("fuck" , soundData.samplesBuffer[100] + "");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(PlaybackThread.mShouldContinue && audioTrack != null )
                    audioTrack.write(soundData.samplesBuffer , 0 , soundData.samplesBuffer.length);
                else break;

            }
        });
        thread.start();
        return thread;
    }

    public boolean isStereo() {
        return soundData.stereo == 1;
    }

}
