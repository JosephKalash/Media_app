package com.example.mediaplayer.Data.Container;

import java.io.InputStream;

abstract public class Container {

    InputStream mInputStream;




    public InputStream getInputStream () {
        return mInputStream;
    }
    public void setInputStream(InputStream in)  {
        mInputStream = in;
    }
}
