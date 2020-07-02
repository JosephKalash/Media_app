package com.example.mediaplayer.Data.Container;

import java.io.InputStream;

abstract public class Container {

    InputStream mInputStream;

    public Container (InputStream in) {
        this.mInputStream = in;
    }



    public InputStream getInputStream () {
        return mInputStream;
    }
    public void setInputStream(InputStream in)  {
        mInputStream = in;
    }
}
