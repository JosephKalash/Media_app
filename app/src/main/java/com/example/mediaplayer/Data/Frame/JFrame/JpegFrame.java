package com.example.mediaplayer.Data.Frame.JFrame;

import com.example.mediaplayer.Data.Frame.Frame;

public class JpegFrame extends Frame {
    QuantizationTable[] QuantizationTables;
    HuffmanTable[] HuffmanTables;
    ScanHeader ScanHeader;
    FrameHeader FrameHeader;
    MCU [] MCUs;

    public JpegFrame(QuantizationTable[] quantizationTables, HuffmanTable[] huffmanTables, ScanHeader scanHeader,
                     FrameHeader frameHeader, MCU[] MCUs) {
        QuantizationTables = quantizationTables;
        HuffmanTables = huffmanTables;
        ScanHeader = scanHeader;
        FrameHeader = frameHeader;
        this.MCUs = MCUs;
    }

    public QuantizationTable[] getQuantizationTables() {
        return QuantizationTables;
    }

    public void setQuantizationTables(QuantizationTable[] quantizationTables) {
        QuantizationTables = quantizationTables;
    }

    public HuffmanTable[] getHuffmanTables() {
        return HuffmanTables;
    }

    public void setHuffmanTables(HuffmanTable[] huffmanTables) {
        HuffmanTables = huffmanTables;
    }

    public com.example.mediaplayer.Data.Frame.JFrame.ScanHeader getScanHeader() {
        return ScanHeader;
    }

    public void setScanHeader(ScanHeader scanHeader) {
        ScanHeader = scanHeader;
    }

    public com.example.mediaplayer.Data.Frame.JFrame.FrameHeader getFrameHeader() {
        return FrameHeader;
    }

    public void setFrameHeader(FrameHeader frameHeader) {
        FrameHeader = frameHeader;
    }

    public MCU[] getMCUs() {
        return MCUs;
    }

    public void setMCUs(MCU[] MCUs) {
        this.MCUs = MCUs;
    }
}
