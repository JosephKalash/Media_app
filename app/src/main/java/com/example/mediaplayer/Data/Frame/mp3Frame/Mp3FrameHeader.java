package com.example.mediaplayer.Data.Frame.mp3Frame;

public class Mp3FrameHeader {
    private int ID;
    private int layer;
    private int protBit;
    private int bitRate;
    private Frequancy frequancy;
    private int padBit;
    private int privBit;
    private int mode;
    private int modeExtesion;
    private int home;
    private int emphasis;

    public Mp3FrameHeader(){}
    public Mp3FrameHeader( int ID, int layer, int protBit, int bitRate, Frequancy frequancy,
                          int padBit, int privBit, int mode, int modeExtesion, int home, int emphasis) {

        this.ID = ID;
        this.layer = layer;
        this.protBit = protBit;
        this.bitRate = bitRate;
        this.frequancy = frequancy;
        this.padBit = padBit;
        this.privBit = privBit;
        this.mode = mode;
        this.modeExtesion = modeExtesion;
        this.home = home;
        this.emphasis = emphasis;
    }














    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public int getProtBit() {
        return protBit;
    }

    public void setProtBit(int protBit) {
        this.protBit = protBit;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public Frequancy getFrequancy() {
        return frequancy;
    }

    public void setFrequancy(Frequancy frequancy) {
        this.frequancy = frequancy;
    }

    public int getPadBit() {
        return padBit;
    }

    public void setPadBit(int padBit) {
        this.padBit = padBit;
    }

    public int getPrivBit() {
        return privBit;
    }

    public void setPrivBit(int privBit) {
        this.privBit = privBit;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getModeExtesion() {
        return modeExtesion;
    }

    public void setModeExtesion(int modeExtesion) {
        this.modeExtesion = modeExtesion;
    }

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }

    public int getEmphasis() {
        return emphasis;
    }

    public void setEmphasis(int emphasis) {
        this.emphasis = emphasis;
    }
}





