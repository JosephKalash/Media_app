package com.example.mediaplayer.Data.Frame.mp3Frame;

import com.example.mediaplayer.Data.Frame.Frame;

public class Mp3Frame extends Frame {
    private Mp3FrameHeader mp3Header;
    private Mp3FrameSideInfo mp3SideInfo;
    private Mp3Granule[] mainData;
    private int ancillaryData;
    private int CRC;

    private double[] decodedMainDataStream;


    public Mp3Frame(int ancillaryData, int CRC) {
        this.mp3Header = new Mp3FrameHeader();
        this.mp3SideInfo = new Mp3FrameSideInfo();
        this.mainData = new Mp3Granule[2];
        this.ancillaryData = ancillaryData;
        this.CRC = CRC;
    }











    public double[] getDecodedMainDataStream() {
        return decodedMainDataStream;
    }

    public void setDecodedMainDataStream(double[] decodedMainDataStream) {
        this.decodedMainDataStream = decodedMainDataStream;
    }

    public Mp3FrameHeader getMp3Header() {
        return mp3Header;
    }

    public void setMp3Header(Mp3FrameHeader mp3Header) {
        this.mp3Header = mp3Header;
    }

    public Mp3FrameSideInfo getMp3SideInfo() {
        return mp3SideInfo;
    }

    public void setMp3SideInfo(Mp3FrameSideInfo mp3SideInfo) {
        this.mp3SideInfo = mp3SideInfo;
    }

    public Mp3Granule[] getMainData() {
        return mainData;
    }

    public void setMainData(Mp3Granule[] mainData) {
        this.mainData = mainData;
    }

    public int getAncillaryData() {
        return ancillaryData;
    }

    public void setAncillaryData(int ancillaryData) {
        this.ancillaryData = ancillaryData;
    }

    public int getCRC() {
        return CRC;
    }

    public void setCRC(int CRC) {
        this.CRC = CRC;
    }
}
