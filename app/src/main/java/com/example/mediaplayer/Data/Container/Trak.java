package com.example.mediaplayer.Data.Container;

public class Trak {

    private int trakDuration;
    private int modificationTime;
    private int timeScale;
    private int trakId;
    private boolean enabled;
    private TrakFormat format;
    private int creattionTime;
    byte[] trakData;

    public Trak(int trakDuration, int modificationTime,
                int timeScale, int trakId, boolean enabled,
                TrakFormat format, int creattionTime, byte[] trakData) {

        this.trakDuration = trakDuration;
        this.modificationTime = modificationTime;
        this.timeScale = timeScale;
        this.trakId = trakId;
        this.enabled = enabled;
        this.format = format;
        this.creattionTime = creattionTime;
        this.trakData = trakData;
    }

    public int getModificationTime() {
        return modificationTime;
    }

    public void setModificationTime(int modificationTime) {
        this.modificationTime = modificationTime;
    }

    public int getCreattionTime() {
        return creattionTime;
    }

    public void setCreattionTime(int creattionTime) {
        this.creattionTime = creattionTime;
    }

    public int getTrakDuration() {
        return trakDuration;
    }

    public int getTimeScale() {
        return timeScale;
    }

    public int getTrakId() {
        return trakId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public TrakFormat getFormat() {
        return format;
    }

    public byte[] getTrakData() {
        return trakData;
    }

    public void setTrakDuration(int trakDuration) {
        this.trakDuration = trakDuration;
    }

    public void setTimeScale(int timeScale) {
        this.timeScale = timeScale;
    }

    public void setTrakId(int trakId) {
        this.trakId = trakId;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setFormat(TrakFormat format) {
        this.format = format;
    }

    public void setTrakData(byte[] trakData) {
        this.trakData = trakData;
    }

}
