package com.example.mediaplayer.ContainerManager;

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
    InputStream in;

    public ContainerManager(InputStream in) {
        this.in = in;
    }

    public void Decode(){ }
    public void Parse() throws IOException, WavFileException {
        parser.parse();
    }
    public void StartManaging(){}
}
