package com.example.mediaplayer.Data.Container;

import com.example.mediaplayer.Data.Frame.Frame;

public class MB3Container extends Container {
    Frame[] AudioStream;
    public MB3Container(Frame[] audioStream){
        AudioStream = audioStream;
    }

    public void setAudioStream(Frame[] audioStream) {
        AudioStream = audioStream;
    }

    public Frame[] getAudioStream() {
        return AudioStream;
    }
}
