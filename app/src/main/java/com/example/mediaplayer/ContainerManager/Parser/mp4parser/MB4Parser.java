package com.example.mediaplayer.ContainerManager.Parser.mp4parser;


import com.example.mediaplayer.ContainerManager.Parser.Parser;
import com.example.mediaplayer.Data.Container.Container;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MB4Parser extends Parser {

    private Container mContainer;
    private InputStream in;

    public MB4Parser(Container container) {
        super(container);
    }


    @Override
    public void Parse() {


    }



}
