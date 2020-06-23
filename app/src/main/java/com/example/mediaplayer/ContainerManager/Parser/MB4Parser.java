package com.example.mediaplayer.ContainerManager.Parser;


import com.example.mediaplayer.ContainerManager.Parser.Parser;
import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Container.Stco;
import com.example.mediaplayer.Data.Container.Stsc;
import com.example.mediaplayer.Data.Container.Stsz;
import com.example.mediaplayer.Data.Container.TrakFormat;

import java.io.InputStream;

public class MB4Parser extends Parser {

    private byte[] mAllBytes;
    private InputStream in;

    public MB4Parser(Container container) {
        super(container);
    }




    @Override
    public void Parse() {

    }

    private class TrakBuilder {

        private BoxAnalyzer mAnalyzer;
        private int mCurrentPosition;
        private int mTrakDuration;
        private int mModificationTime;
        private int mTrakId;
        private TrakFormat mFormat;
        private int mCreattionTime;
        private byte[] trakData;

        TrakBuilder() {
            mAnalyzer = new BoxAnalyzer();
            mCurrentPosition = 0;
        }


        private int updateCurentPosition(int mCurrentPosition, int value, String boxType) {
            return 0;
        }

        private int updateSize(int position) {
            return 0;
        }

        private String updateType(int position) {
            return null;
        }

        private boolean isFullBox(String type) {
            return false;
        }

        private int searchForBoxInSameLevel(String boxName, int startingPosition) {
            return 0;
        }

        private int calculateBoxPositionAfterEntering(String boxName, int mCurrentPosition) {
            return 0;
        }

        private int calculateBoxPositionBeforeEntering(String boxName, int mCurrentPosition) {
            return 0;
        }

        private byte[] read4Bytes(int position) {
            return null;
        }

        private class BoxAnalyzer {
            private Stco readInfoFromStco() {
                return null;
            }

            private Stsc readInfoFromStsc() {
                return null;
            }

            private Stsz readInfoFromStsz() {
                return null;
            }

            private void readInfoFromTkhd() {
            }

            private void readInfoFromHdlr() {
            }

            private int calculateBaseReference(int chunkOffset, int sampleNumber,
                                               int sampleInChunk, Stsz stsz) {
                return 0;
            }

            private byte[] calculateFrame(int startingReference, int size) {
                return null;
            }

            private byte[] extractTrakData(Stco stco, Stsz stsz, Stsc stsc, int id) {
                return null;
            }

        }
    }


}
