package com.example.mediaplayer.ContainerManager.Parser;

import com.example.mediaplayer.Data.Container.Container;

abstract public class Parser {
Container container;


    public Parser(Container container) {
        this.container = container;
    }
    abstract public void Parse();

    public Container getContainer(){
        return container;
    }
}
