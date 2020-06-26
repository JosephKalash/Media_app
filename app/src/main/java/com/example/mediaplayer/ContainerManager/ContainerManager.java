package com.example.mediaplayer.ContainerManager;

import android.util.Log;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder;
import com.example.mediaplayer.ContainerManager.Decoder.Mp3Decoder;
import com.example.mediaplayer.ContainerManager.Decoder.WavDecoder;
import com.example.mediaplayer.ContainerManager.Parser.MB3Parser;
import com.example.mediaplayer.ContainerManager.Parser.MB4Parser;
import com.example.mediaplayer.ContainerManager.Parser.Parser;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavParser;
import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Container.MB3Container;
import com.example.mediaplayer.Data.Container.WavContainer;
import com.example.mediaplayer.Data.Container.mp4.MB4Container;

import java.io.File;
import java.io.IOException;

public class ContainerManager {
    Container container;
    Parser parser;
    Decoder decoder;
    File file;
    Container mContainer;

    public ContainerManager(Container container) {
        this.mContainer = container;
        Log.d("FUCKERS", "Fucker");
    }

    public void Decode() throws IOException, WavFileException {
        if(container instanceof WavContainer){
            decoder = new WavDecoder(container);
            decoder.Decode();
        }
        else if(container instanceof MB3Container){
            decoder = new MjpegDecoder(container,file);
            decoder.Decode();
        }
        else if(container instanceof MB4Container){
            decoder = new Mp3Decoder(container);
            decoder.Decode();
        }
    }
    public void Parse() throws IOException, WavFileException {
        if(container instanceof WavContainer){
            parser = new WavParser(container);
            parser.parse();
        }
        else if(container instanceof MB3Container){
            parser = new MB3Parser(container);
            parser.parse();
        }
        else if (container instanceof MB4Container){
            parser = new MB4Parser(container);
            parser.parse();
        }
    }
    public void StartManaging() throws IOException, WavFileException {
        Parse();
        Decode();
    }
}
