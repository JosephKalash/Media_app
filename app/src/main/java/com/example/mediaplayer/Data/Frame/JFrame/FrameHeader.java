package com.example.mediaplayer.Data.Frame.JFrame;

public class FrameHeader {
    long FrameHeaderLength;
    long Level;
    long FrameHeight;
    long FrameWidth;
    long NumberOfComponents;
    FrameComponent[]FrameComponents;

    public long getFrameHeaderLength() {
        return FrameHeaderLength;
    }

    public void setFrameHeaderLength(long frameHeaderLength) {
        FrameHeaderLength = frameHeaderLength;
    }

    public long getLevel() {
        return Level;
    }

    public void setLevel(long level) {
        Level = level;
    }

    public long getFrameHeight() {
        return FrameHeight;
    }

    public void setFrameHeight(long frameHeight) {
        FrameHeight = frameHeight;
    }

    public long getFrameWidth() {
        return FrameWidth;
    }

    public void setFrameWidth(long frameWidth) {
        FrameWidth = frameWidth;
    }

    public long getNumberOfComponents() {
        return NumberOfComponents;
    }

    public void setNumberOfComponents(long numberOfComponents) {
        NumberOfComponents = numberOfComponents;
    }

    public FrameComponent[] getFrameComponents() {
        return FrameComponents;
    }

    public void setFrameComponents(FrameComponent[] frameComponents) {
        FrameComponents = frameComponents;
    }

    public FrameHeader(long frameHeaderLength, long level, long frameHeight, long frameWidth,
                       long numberOfComponents, FrameComponent[] frameComponents) {
        FrameHeaderLength = frameHeaderLength;
        Level = level;
        FrameHeight = frameHeight;
        FrameWidth = frameWidth;
        NumberOfComponents = numberOfComponents;
        FrameComponents = frameComponents;
    }
}
