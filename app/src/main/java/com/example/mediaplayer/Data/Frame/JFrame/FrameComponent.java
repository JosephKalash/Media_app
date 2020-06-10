package com.example.mediaplayer.Data.Frame.JFrame;

public class FrameComponent {
long ComponentID;
long HeightFactor;
long WidthFactor;
long QuantizationTableID;

    public long getComponentID() {
        return ComponentID;
    }

    public void setComponentID(long componentID) {
        ComponentID = componentID;
    }

    public long getHeightFactor() {
        return HeightFactor;
    }

    public void setHeightFactor(long heightFactor) {
        HeightFactor = heightFactor;
    }

    public long getWidthFactor() {
        return WidthFactor;
    }

    public void setWidthFactor(long widthFactor) {
        WidthFactor = widthFactor;
    }

    public long getQuantizationTableID() {
        return QuantizationTableID;
    }

    public void setQuantizationTableID(long quantizationTableID) {
        QuantizationTableID = quantizationTableID;
    }

    public FrameComponent(long componentID, long heightFactor, long widthFactor, long quantizationTableID) {
        ComponentID = componentID;
        HeightFactor = heightFactor;
        WidthFactor = widthFactor;
        QuantizationTableID = quantizationTableID;
    }
}
