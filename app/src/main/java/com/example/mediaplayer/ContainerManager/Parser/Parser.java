package com.example.mediaplayer.ContainerManager.Parser;

import com.example.mediaplayer.Data.Container.Container;

import java.io.File;

abstract public class Parser {

    Container container;

    protected Container mContainer;

    public Parser(Container container) {
        this.mContainer = container;
    }

    abstract public void Parse();

    public Container getContainer(){
        return mContainer;
    }

    protected File getRawFile (){
        return null;
    }
}
