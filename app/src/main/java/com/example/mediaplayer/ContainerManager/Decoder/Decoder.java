package com.example.mediaplayer.ContainerManager.Decoder;

import com.example.mediaplayer.Data.Container.Container;

abstract public class Decoder {
    Container container;


    public Decoder(Container container) {
        this.container = container;
    }
    abstract public void Decode();

    public Container getContainer(){
        return container;
    }
}
