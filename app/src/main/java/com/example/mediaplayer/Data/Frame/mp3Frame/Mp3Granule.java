package com.example.mediaplayer.Data.Frame.mp3Frame;

public class Mp3Granule {
    private double[] region0;
    private double[] region1;
    private double[] region2;
    private double[] count1;
    private double[] rZero;
    private int[] numberOfChannel;
    private int[] scaleFactor;
    private int[] scaleFactorChannel2;

    public Mp3Granule(int[] numberOfChannel) {
        this.numberOfChannel = numberOfChannel;
    }


    public double[] getRegion0() {
        return region0;
    }

    public void setRegion0(double[] region0) {
        this.region0 = region0;
    }

    public double[] getRegion1() {
        return region1;
    }

    public void setRegion1(double[] region1) {
        this.region1 = region1;
    }

    public double[] getRegion2() {
        return region2;
    }

    public void setRegion2(double[] region2) {
        this.region2 = region2;
    }

    public double[] getCount1() {
        return count1;
    }

    public void setCount1(double[] count1) {
        this.count1 = count1;
    }

    public double[] getrZero() {
        return rZero;
    }

    public void setrZero(double[] rZero) {
        this.rZero = rZero;
    }

    public int[] getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(int[] scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public int[] getScaleFactorChannel2() {
        return scaleFactorChannel2;
    }

    public void setScaleFactorChannel2(int[] scaleFactorChannel2) {
        this.scaleFactorChannel2 = scaleFactorChannel2;
    }
}
