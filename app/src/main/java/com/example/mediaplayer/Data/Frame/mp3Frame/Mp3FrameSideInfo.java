package com.example.mediaplayer.Data.Frame.mp3Frame;

public class Mp3FrameSideInfo {

    private int mainDataBegin;
    private int privateBits;
    private int scfsi;
    private SideInfoGr sideInfoGr_0;
    private SideInfoGr sideInfoGr_1;

    public Mp3FrameSideInfo(){}
    public Mp3FrameSideInfo(int mainDataBegin, int privateBits, int scfsi,
                            SideInfoGr sideInfoGr_0, SideInfoGr sideInfoGr_1) {
        this.mainDataBegin = mainDataBegin;
        this.privateBits = privateBits;
        this.scfsi = scfsi;
        this.sideInfoGr_0 = sideInfoGr_0;
        this.sideInfoGr_1 = sideInfoGr_1;
    }















    public int getMainDataBegin() {
        return mainDataBegin;
    }

    public void setMainDataBegin(int mainDataBegin) {
        this.mainDataBegin = mainDataBegin;
    }

    public int getPrivateBits() {
        return privateBits;
    }

    public void setPrivateBits(int privateBits) {
        this.privateBits = privateBits;
    }

    public int getScfsi() {
        return scfsi;
    }

    public void setScfsi(int scfsi) {
        this.scfsi = scfsi;
    }

    public SideInfoGr getSideInfoGr_0() {
        return sideInfoGr_0;
    }

    public void setSideInfoGr_0(SideInfoGr sideInfoGr_0) {
        this.sideInfoGr_0 = sideInfoGr_0;
    }

    public SideInfoGr getSideInfoGr_1() {
        return sideInfoGr_1;
    }

    public void setSideInfoGr_1(SideInfoGr sideInfoGr_1) {
        this.sideInfoGr_1 = sideInfoGr_1;
    }
}

