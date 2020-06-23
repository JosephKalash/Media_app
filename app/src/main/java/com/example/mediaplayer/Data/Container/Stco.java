package com.example.mediaplayer.Data.Container;

import java.util.List;

public class Stco {

    private int mEntryCount;
    private List<Integer> mEntries;

    Stco(int entryCount, List<Integer> list) {
        this.mEntryCount = entryCount;
        this.mEntries = list;
    }

    int getChunkOffset(int i) {
        return mEntries.get(i);
    }

    int getEntriesCount() {
        return mEntryCount;
    }
}
