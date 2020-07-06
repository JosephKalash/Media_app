package com.example.mediaplayer.Data.Container;

import com.example.mediaplayer.Data.Frame.Frame;

import java.io.InputStream;

public class MB3Container extends Container {
    Frame[] AudioStream;

    public MB3Container(InputStream in){
        super(in);
    }

    public void setAudioStream(Frame[] audioStream) {
        AudioStream = audioStream;
    }

    public InputStream getAudioStream() {
        return mInputStream;
    }
}
