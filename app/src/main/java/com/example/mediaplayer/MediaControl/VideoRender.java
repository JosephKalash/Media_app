package com.example.mediaplayer.MediaControl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Container.mp4.MB4Container;
import com.example.mediaplayer.Data.Frame.JFrame.MCU;

import java.util.ArrayList;

public class VideoRender {
    ArrayList<MCU> MCUs;

    public VideoRender(Container container, long FrameWidth, ArrayList<MCU> MCUS) {

        MCUs = new ArrayList<>();
        this.MCUs = MCUS;
        ((MB4Container) container).getTraks().get(0);

    }

    public void draw() {


    }
}
