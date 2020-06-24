package com.example.mediaplayer.ContainerManager.Parser;

import com.example.mediaplayer.Data.Container.Container;

import java.io.File;

public abstract class Parser {


    protected Container mContainer;

    public Parser(Container container) {
        this.mContainer = container;
    }

    abstract public void parse();

    public Container getContainer(){
        return mContainer;
    }

}
