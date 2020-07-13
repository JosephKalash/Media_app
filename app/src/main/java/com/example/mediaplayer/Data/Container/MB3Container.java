package com.example.mediaplayer.Data.Container;

import com.example.mediaplayer.Data.Frame.Frame;
import com.example.mediaplayer.MediaControl.PlaybackThread;

import java.io.InputStream;

public class MB3Container extends Container {
    Frame[] AudioStream;
    private PlaybackThread playback;
    public MB3Container(InputStream in){
        super(in);
    }
    public MB3Container(InputStream in , PlaybackThread playback){
        super(in);
        this.playback = playback;
    }

    public PlaybackThread getPlayback() {
        return playback;
    }

    public void setAudioStream(Frame[] audioStream) {
        AudioStream = audioStream;
    }

    public InputStream getAudioStream() {
        return mInputStream;
    }
}
