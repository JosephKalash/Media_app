package com.example.mediaplayer.ContainerManager;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.ContainerManager.Parser.Parser;
import com.example.mediaplayer.Data.Container.Container;

import java.io.File;

public class ContainerManager {
    Container container;
    Parser parser;
    Decoder decoder;

    public ContainerManager(Container container) {
        this.container = container;
    }
    public Container getContainer(){
        return container;
    }
    public File getCurrentRawFile() {
        return null;
    }

    public void Decode(){ }
    public void Parse(){}
    public void StartManaging(){}
}
