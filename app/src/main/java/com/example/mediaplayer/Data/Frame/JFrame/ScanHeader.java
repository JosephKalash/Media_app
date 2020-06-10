package com.example.mediaplayer.Data.Frame.JFrame;

public class ScanHeader {
long ScanHeaderLength;
long NumberOfComponents;
ScanComponents[] ScanComponents;

    public ScanHeader(long scanHeaderLength, long numberOfComponents,ScanComponents[] scanComponents) {
        ScanHeaderLength = scanHeaderLength;
        NumberOfComponents = numberOfComponents;
        ScanComponents = scanComponents;
    }

    public long getScanHeaderLength() {
        return ScanHeaderLength;
    }

    public void setScanHeaderLength(long scanHeaderLength) {
        ScanHeaderLength = scanHeaderLength;
    }

    public long getNumberOfComponents() {
        return NumberOfComponents;
    }

    public void setNumberOfComponents(long numberOfComponents) {
        NumberOfComponents = numberOfComponents;
    }

    public ScanComponents[] getScanComponents() {
        return ScanComponents;
    }

    public void setScanComponents(ScanComponents[] scanComponents) {
        ScanComponents = scanComponents;
    }
}
