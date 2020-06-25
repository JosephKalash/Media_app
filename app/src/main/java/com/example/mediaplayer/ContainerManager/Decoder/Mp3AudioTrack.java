package com.example.mediaplayer.ContainerManager.Decoder;

import com.example.mediaplayer.Data.Frame.mp3Frame.Mp3Data;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class Mp3AudioTrack extends FilterInputStream {
    private Mp3Data mp3Data;
    private int index;

    public Mp3AudioTrack(InputStream in) throws IOException {
        super(in);
        mp3Data = Mp3Decoder.decode(in);
    }

    public int decodeFullyInto(OutputStream os) throws IOException {
        Objects.requireNonNull(os);
        if(index == -1)
            return 0;
        int remaining = mp3Data.getSamplesBuffer().length - index;
        if(remaining > 0) {
            os.write(mp3Data.getSamplesBuffer(), index, remaining);
        }
        int read = remaining;
        while(!Mp3Decoder.startDecode(mp3Data)) {
            os.write(mp3Data.getSamplesBuffer());
            read += mp3Data.getSamplesBuffer().length;
        }
        mp3Data.setSamplesBuffer(null);
        index = -1;
        return read;
    }
    @Override
    public void close() throws IOException {
        if(in != null) {
            in.close();
            in = null;
            mp3Data.setSamplesBuffer(null);
        }
        index = -1;
    }

}
