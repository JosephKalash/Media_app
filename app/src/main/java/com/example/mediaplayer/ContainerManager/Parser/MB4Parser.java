package com.example.mediaplayer.ContainerManager.Parser;

import android.content.Context;

import com.example.mediaplayer.Data.Container.Container;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MB4Parser extends Parser {

    Container mContainer;
    Stsz mStsz;
    Stco mStco;
    Stsc mStsc;


    public MB4Parser(Container container) {
        super(container);
        mContainer = container;
    }

    @Override
    public void Parse() {
        File rawFile = getRawFile();


        try(InputStream in = new FileInputStream(rawFile)) {

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private class Stsz {

        double getSampleSize(int i) {
           return 0;
        }
        double getSampleCount() {
            return 0;
        }

        double getEntryCount() {
            return 0;
        }
        double getFirstChunk() {
           return 0;
        }

    }
    private class Stco {

        double getChunkOffset(int i) {
            return  0;
        }
        double getEntryCount() {
            return 0;
        }

    }
    private class Stsc {
        double getNumberOfSamplesInChunk(int i) {
            return 0;
        }
    }



    private byte[] getMdatFile(){

        return null;
    }

    private boolean checkIfParsingIsValid() {
       return false;
    }
}
