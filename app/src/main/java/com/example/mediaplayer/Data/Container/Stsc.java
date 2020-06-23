package com.example.mediaplayer.Data.Container;

import java.util.List;

public class Stsc {
    private int mEntriesCount;
    private List<Integer> mFirstChunk;
    private List<Integer> mSamplesPerChunk;

    Stsc(int entriesCount, List<Integer> firstChunk, List<Integer> samplesPerChuk) {
        mEntriesCount = entriesCount;
        mFirstChunk = firstChunk;
        mSamplesPerChunk = samplesPerChuk;
    }

    public int getmEntriesCount() {
        return mEntriesCount;
    }

    public int getmFirstChunk(int i) {
        return mFirstChunk.get(i);
    }

    public int getmSamplesPerChunk(int i) {
        return mSamplesPerChunk.get(i);
    }

}
