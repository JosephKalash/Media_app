package com.example.mediaplayer.ContainerManager.Decoder;

import com.example.mediaplayer.ContainerManager.Parser.WavParser.WavFileException;
import com.example.mediaplayer.Data.Container.Container;

import java.io.IOException;

abstract public class Decoder {
    Container container;


    public Decoder(Container container) {
        this.container = container;
    }
    abstract public void Decode() throws IOException, WavFileException;

    public Container getContainer(){
        return container;
    }
}
