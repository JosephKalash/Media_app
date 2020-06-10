package com.example.mediaplayer.Data.Frame.JFrame;

public class QuantizationTable {
    long   TableLength;
    long   TableKind;
    long[] Values;

    public QuantizationTable(long tableLength, long tableKind, long[] values) {
        TableLength = tableLength;
        TableKind = tableKind;
        Values = values;
    }

    public long getTableLength() {
        return TableLength;
    }

    public void setTableLength(long tableLength) {
        TableLength = tableLength;
    }

    public long getTableKind() {
        return TableKind;
    }

    public void setTableKind(long tableKind) {
        TableKind = tableKind;
    }

    public long[] getValues() {
        return Values;
    }

    public void setValues(long[] values) {
        Values = values;
    }

}
