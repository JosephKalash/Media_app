package com.example.mediaplayer.ContainerManager;

import android.util.Log;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.ContainerManager.Parser.Parser;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Container.Container;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ContainerManager {
    Parser parser;
    Decoder decoder;
    Container mContainer;

    public ContainerManager(Container container) {
        this.mContainer = container;
        Log.d("FUCKERS", "Fucker");
    }

    public void Decode(){ }
    public void Parse() throws IOException, WavFileException {
        parser.parse();
    }
    public void StartManaging(){}
}
