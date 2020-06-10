package com.example.mediaplayer.Data.Frame.JFrame;

public class HuffmanTable {
long    TableKind;
long    TableID;
long [] CondesLength=new long [16];
long [] Sympoles=new long [162];
long [] Offset=new long [17];
long [] Codes=new long [162];

    public HuffmanTable() {
    }

    public HuffmanTable(long tableKind, long tableID, long[] condesLength, long[] sympoles,
                        long[] offset, long[] codes) {
        TableKind = tableKind;
        TableID = tableID;
        CondesLength = condesLength;
        Sympoles = sympoles;
        Offset = offset;
        Codes = codes;
    }

    public long getTableKind() {
        return TableKind;
    }

    public void setTableKind(long tableKind) {
        TableKind = tableKind;
    }

    public long getTableID() {
        return TableID;
    }

    public void setTableID(long tableID) {
        TableID = tableID;
    }

    public long[] getCondesLength() {
        return CondesLength;
    }

    public void setCondesLength(long[] condesLength) {
        CondesLength = condesLength;
    }

    public long[] getSympoles() {
        return Sympoles;
    }

    public void setSympoles(long[] sympoles) {
        Sympoles = sympoles;
    }

    public long[] getOffset() {
        return Offset;
    }

    public void setOffset(long[] offset) {
        Offset = offset;
    }

    public long[] getCodes() {
        return Codes;
    }

    public void setCodes(long[] codes) {
        Codes = codes;
    }
}
