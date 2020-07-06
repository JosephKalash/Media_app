package com.example.mediaplayer.ContainerManager.Decoder.Mjpeg;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mediaplayer.Data.Frame.JFrame.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.example.mediaplayer.Data.Frame.*;

import static com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder.Buffer;
import static com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder.SourceImage;
import static com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder.completeafter;
import static com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegDecoder.previousDCs;

public class MjpegParser {


    // Start of Frame markers, non-differential, Huffman coding
    public static final int SOF0 = 0xC0; // Baseline DCT
    public static final int SOF1 = 0xC1; // Extended sequential DCT
    public static final int SOF2 = 0xC2; // Progressive DCT
    public static final int SOF3 = 0xC3; // Lossless (sequential)

    // Start of Frame markers, differential, Huffman coding
    public static final int SOF5 = 0xC5; // Differential sequential DCT
    public static final int SOF6 = 0xC6; // Differential progressive DCT
    public static final int SOF7 = 0xC7; // Differential lossless (sequential)

     // Start of Frame markers, non-differential, arithmetic coding
    public static final int SOF9 = 0xC9; // Extended sequential DCT
    public static final int SOF10 = 0xCA; // Progressive DCT
    public static final int SOF11 = 0xCB; // Lossless (sequential)

    // Start of Frame markers, differential, arithmetic coding
    public static final int SOF13 = 0xCD; // Differential sequential DCT
    public static final int SOF14 = 0xCE; // Differential progressive DCT
    public static final int SOF15 = 0xCF; // Differential lossless (sequential)

    // Define Huffman Table(s)
    public static final int DHT = 0xC4;

    // JPEG extensions
    public static final int JPG = 0xC8;

    // Define Arithmetic Coding Conditioning(s)
    public static final int DAC = 0xCC;

    // Restart interval Markers
    public static final int RST0 = 0xD0;
    public static final int RST1 = 0xD1;
    public static final int RST2 = 0xD2;
    public static final int RST3 = 0xD3;
    public static final int RST4 = 0xD4;
    public static final int RST5 = 0xD5;
    public static final int RST6 = 0xD6;
    public static final int RST7 = 0xD7;

    // Other Markers
    public static final int SOI = 0xD8; // Start of Image
    public static final int EOI = 0xD9; // End of Image
    public static final int SOS = 0xDA; // Start of Scan
    public static final int DQT = 0xDB; // Define Quantization Table(s)
    public  static final int DNL = 0xDC; // Define Number of Lines
    public static final int DRI = 0xDD; // Define Restart Interval
    public static final int DHP = 0xDE; // Define Hierarchical Progression
    public static final int EXP = 0xDF; // Expand Reference Component(s)

    // APPN Markers
    public static final int APP0 = 0xE0;
    public  static final int APP1 = 0xE1;
    public static final int APP2 = 0xE2;
    public static final int APP3 = 0xE3;
    public static final int APP4 = 0xE4;
    public static final int APP5 = 0xE5;
    public static final int APP6 = 0xE6;
    public static final int APP7 = 0xE7;
    public static final int APP8 = 0xE8;
    public static final int APP9 = 0xE9;
    public static final int APP10 = 0xEA;
    public static final int APP11 = 0xEB;
    public static final int APP12 = 0xEC;
    public static final int APP13 = 0xED;
    public static final int APP14 = 0xEE;
    public static final int APP15 = 0xEF;

    // Misc Markers
   public  static final int JPG0 = 0xF0;
   public  static final int JPG1 = 0xF1;
   public  static final int JPG2 = 0xF2;
   public  static final int JPG3 = 0xF3;
   public  static final int JPG4 = 0xF4;
   public  static final int JPG5 = 0xF5;
   public  static final int JPG6 = 0xF6;
   public  static final int JPG7 = 0xF7;
   public  static final int JPG8 = 0xF8;
   public  static final int JPG9 = 0xF9;
   public  static final int JPG10 = 0xFA;
   public  static final int JPG11 = 0xFB;
   public  static final int JPG12 = 0xFC;
   public  static final int JPG13 = 0xFD;
   public  static final int COM = 0xFE;
   public  static final int TEM = 0x01;

   public  static final int zigZagMap[] = {
            0,   1,  8, 16,  9,  2,  3, 10,
            17, 24, 32, 25, 18, 11,  4,  5,
            12, 19, 26, 33, 40, 48, 41, 34,
            27, 20, 13,  6,  7, 14, 21, 28,
            35, 42, 49, 56, 57, 50, 43, 36,
            29, 22, 15, 23, 30, 37, 44, 51,
            58, 59, 52, 45, 38, 31, 39, 46,
            53, 60, 61, 54, 47, 55, 62, 63
    };

    // IDCT scaling factors
    public static final float m0 = (float)1.84776 ;
    public static final float m1 = (float)1.41421;
    public static final float m3 = (float)1.41421;
    public static final float m5 = (float)0.765367;
    public static final float m2 = (float)1.08239;
    public static final float m4 = (float)2.61313;

    public static final float s0 = (float)0.353553;
    public static final float s1 = (float)0.490393;
    public static final float s2 = (float)0.46194;
    public static final float s3 = (float)0.415735;
    public static final float s4 = (float)0.353553;
    public static final float s5 = (float)0.277785;
    public static final float s6 = (float)0.191342;
    public static final float s7 = (float)0.0975452;





    private static int BigEndianRead(byte[] buffer, int pos, int numBytes) {
        numBytes--;
        int val = buffer[pos] & 0xFF;
        for (int b = 0; b < numBytes; b++) {
            val = (val << 8) + (buffer[++pos] & 0xFF);
        }

        return val;

    }

    static void readStartOfFrame(final Header header) {
        System.out.println("Reading SOF Marker");
        if (header.numComponents != 0) {
            System.out.println("Error - Multiple SOFs detected");
            header.valid = false;
            return;
        }

        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;

        int precision = BigEndianRead(Buffer,completeafter,1);completeafter++;
        if (precision != 8) {
            System.out.println("Error - Invalid precision: " + precision );
            header.valid = false;
            return;
        }

        header.height = BigEndianRead(Buffer,completeafter,2);completeafter+=2;
        header.width = BigEndianRead(Buffer,completeafter,2);completeafter+=2;
        if (header.height == 0 || header.width == 0) {
            System.out.println("Error - Invalid dimensions");
            header.valid = false;
            return;
        }
        header.mcuHeight = (header.height + 7) / 8;
        header.mcuWidth = (header.width + 7) / 8;
        header.mcuHeightReal = header.mcuHeight;
        header.mcuWidthReal = header.mcuWidth;

        header.numComponents = BigEndianRead(Buffer,completeafter,1);completeafter++;
        if (header.numComponents == 4) {
            System.out.println("Error - CMYK color mode not supported");
            header.valid = false;
            return;
        }
        if (header.numComponents == 0) {
            System.out.println("Error - Number of color components must not be zero");
            header.valid = false;
            return;
        }
        for (int i = 0; i < header.numComponents; ++i) {
            int componentID = BigEndianRead(Buffer,completeafter,1);completeafter++;
            // component IDs are usually 1, 2, 3 but rarely can be seen as 0, 1, 2
            // always force them into 1, 2, 3 for consistency
            if (componentID == 0) {
                header.zeroBased = true;
            }
            if (header.zeroBased) {
                componentID += 1;
            }
            if (componentID == 4 || componentID == 5) {
                System.out.println("Error - YIQ color mode not supported");
                header.valid = false;
                return;
            }
            if (componentID == 0 || componentID > 3) {
                System.out.println("Error - Invalid component ID: " + (int)componentID );
                header.valid = false;
                return;
            }
            ColorComponent component = header.colorComponents[componentID - 1];
            if (component.used) {
                System.out.println("Error - Duplicate color component ID");
                header.valid = false;
                return;
            }
            component.used = true;
            int samplingFactor =BigEndianRead(Buffer,completeafter,1);completeafter++;
            component.horizontalSamplingFactor = samplingFactor >> 4;
            component.verticalSamplingFactor = (samplingFactor & 0x0F);
            if (componentID == 1) {
                if ((component.horizontalSamplingFactor != 1 && component.horizontalSamplingFactor != 2) ||
                        (component.verticalSamplingFactor != 1 && component.verticalSamplingFactor != 2)) {
                    System.out.println("Error - Sampling factors not supported");
                    header.valid = false;
                    return;
                }
                if (component.horizontalSamplingFactor == 2 && header.mcuWidth % 2 == 1) {
                    header.mcuWidthReal += 1;
                }
                if (component.verticalSamplingFactor == 2 && header.mcuHeight % 2 == 1) {
                    header.mcuHeightReal += 1;
                }
                header.horizontalSamplingFactor = component.horizontalSamplingFactor;
                header.verticalSamplingFactor = component.verticalSamplingFactor;
            }
            else {
                if (component.horizontalSamplingFactor != 1 || component.verticalSamplingFactor != 1) {
                    System.out.println("Error - Sampling factors not supported");
                    header.valid = false;
                    return;
                }
            }
            component.quantizationTableID = BigEndianRead(Buffer,completeafter,1);completeafter++;
            if (component.quantizationTableID > 3) {
                System.out.println("Error - Invalid quantization table ID in frame components");
                header.valid = false;
                return;
            }
        }
        if (length - 8 - (3 * header.numComponents) != 0) {
            System.out.println("Error - SOF invalid");
            header.valid = false;
        }
    }

    static void readHuffmanTable(final Header header) {
        System.out.println("Reading DHT Marker");
        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;
        length -= 2;

        while (length > 0) {
            int tableInfo = BigEndianRead(Buffer,completeafter,1);completeafter++;
            int tableID = tableInfo & 0x0F;
            int num = tableInfo >> 4;
            boolean ACTable=false;
            if(num==1)
                ACTable = true;

            if (tableID > 3) {
                System.out.println("Error - Invalid Huffman table ID: " + (int)tableID );
                header.valid = false;
                return;
            }
            HuffmanTable hTable;
            if (ACTable) {
                hTable = header.huffmanACTables[tableID];
            }
            else {
                hTable = header.huffmanDCTables[tableID];
            }
            hTable.set = true;

            hTable.offsets[0] = 0;
            int allSymbols = 0;
            for (int i = 1; i <= 16; ++i) {
                allSymbols +=BigEndianRead(Buffer,completeafter,1);completeafter++;
                hTable.offsets[i] = allSymbols;
            }
            if (allSymbols > 162) {
                System.out.println("Error - Too many symbols in Huffman table");
                header.valid = false;
                return;
            }

            for (int i = 0; i < allSymbols; ++i) {
                hTable.symbols[i] = BigEndianRead(Buffer,completeafter,1);completeafter++;
            }

            length -= 17 + allSymbols;
        }
        if (length != 0) {
            System.out.println("Error - DHT invalid");
            header.valid = false;
        }
    }


    static void readStartOfScan(final Header header) {
        System.out.println("Reading SOS Marker");
        if (header.numComponents == 0) {
            System.out.println("Error - SOS detected before SOF");
            header.valid = false;
            return;
        }

        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;

        for (int i = 0; i < header.numComponents; ++i) {
            header.colorComponents[i].used = false;
        }

        // the number of components in the next scan might not be all
        //   components in the image
        int numComponents =  BigEndianRead(Buffer,completeafter,1);completeafter++;
        for (int i = 0; i < numComponents; ++i) {
            int componentID = BigEndianRead(Buffer,completeafter,1);completeafter++;
            // component IDs are usually 1, 2, 3 but rarely can be seen as 0, 1, 2
            if (header.zeroBased) {
                componentID += 1;
            }
            if (componentID > header.numComponents) {
                System.out.println("Error - Invalid color component ID: " + (int)componentID);
                header.valid = false;
                return;
            }
            ColorComponent component = header.colorComponents[componentID - 1];
            if (component.used) {
                System.out.println("Error - "
                        + "Duplicate color component ID: " + (int)componentID );
                header.valid = false;
                return;
            }
            component.used = true;

            int huffmanTableIDs =  BigEndianRead(Buffer,completeafter,1);completeafter++;
            component.huffmanDCTableID = huffmanTableIDs >> 4;
            component.huffmanACTableID = huffmanTableIDs & 0x0F;
            if (component.huffmanDCTableID > 3) {
                System.out.println("Error - Invalid Huffman DC table ID: " + (int)component.huffmanDCTableID);
                header.valid = false;
                return;
            }
            if (component.huffmanACTableID > 3) {
                System.out.println("Error - Invalid Huffman AC table ID: " + (int)component.huffmanACTableID);
                header.valid = false;
                return;
            }
        }

        header.startOfSelection =  BigEndianRead(Buffer,completeafter,1);completeafter++;
        header.endOfSelection =  BigEndianRead(Buffer,completeafter,1);completeafter++;
        int successiveApproximation =  BigEndianRead(Buffer,completeafter,1);completeafter++;
        header.successiveApproximationHigh = successiveApproximation >> 4;
        header.successiveApproximationLow = successiveApproximation & 0x0F;

        // Baseline JPGs don't use spectral selection or successive approximtion
        if (header.startOfSelection != 0 || header.endOfSelection != 63) {
            System.out.println("Error - Invalid spectral selection");
            header.valid = false;
            return;
        }
        if (header.successiveApproximationHigh != 0 || header.successiveApproximationLow != 0) {
            System.out.println("Error - Invalid successive approximation");
            header.valid = false;
            return;
        }

        if (length - 6 - (2 * numComponents) != 0) {
            System.out.println("Error - SOS invalid");
            header.valid = false;
        }
    }

    static void readRestartInterval(final Header header) {
        System.out.println("Reading DRI Marker");
        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;

        header.restartInterval = BigEndianRead(Buffer,completeafter,2);completeafter+=2;
        if (length - 4 != 0) {
            System.out.println("Error - DRI invalid");
            header.valid = false;
        }
    }

    static void readAPPN(final Header header) {
        System.out.println("Reading APPN Marker");
        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;

        for (int i = 0; i < length - 2; ++i) {
            BigEndianRead(Buffer,completeafter,1);completeafter++;
        }
    }

    static void readComment(final Header header) {
        System.out.println("Reading COM Marker");
        int length = BigEndianRead(Buffer,completeafter,2);completeafter+=2;

        for (int i = 0; i < length - 2; ++i) {
            BigEndianRead(Buffer,completeafter,1);completeafter++;
        }
    }


    // DQT contains one or more quantization tables
    static void readQuantizationTable(Header header) {
        System.out.println("Reading DQT Marker\n");
        int length = BigEndianRead(Buffer,completeafter,2);

        completeafter+=2;
        length -= 2;

        while (length > 0) {
            int tableInfo = BigEndianRead(Buffer,completeafter,1);completeafter++;
            length -= 1;
            int tableID = tableInfo & 0x0F;

            if (tableID > 3) {
                System.out.println("Error - Invalid quantization table ID: ");

                header.valid = false;
                return;
            }
            header.quantizationTables[tableID].set = true;
            if (tableInfo >> 4 != 0) {

                for (int i = 0; i < 64; ++i) {
                    header.quantizationTables[tableID].table[zigZagMap[i]] = BigEndianRead(Buffer,completeafter,2);
                    completeafter+=2;
                }
                length -= 128;
            }
            else {

                for (int i = 0; i < 64; ++i) {
                    header.quantizationTables[tableID].table[zigZagMap[i]] = BigEndianRead(Buffer,completeafter,1);
                    completeafter++;
                }
                length -= 64;
            }
        }

        if (length != 0) {
            System.out.println("Error - DQT invalid\n");
            header.valid = false;}
    }


    static Header readJPG( ) {
        // open file
        Header header = new Header();
        if (header == null) {
            System.out.println("Error - Memory error\n");
            return null;
        }

        // first two bytes must be 0xFF, SOI
        int last = BigEndianRead(Buffer,completeafter,1);completeafter++;
        int current = BigEndianRead(Buffer,completeafter,1);completeafter++;
        if (last != 0xFF || current != SOI) {
            header.valid = false;

            return header;
        }
        last = BigEndianRead(Buffer,completeafter,1);completeafter++;
        current =  BigEndianRead(Buffer,completeafter,1);completeafter++;

        // read markers
        while (header.valid) {

            if (last != 0xFF) {

                System.out.println("Error - Expected a marker\n"+Integer.toHexString(last));

                header.valid = false;
                return header;
            }

            if (current == SOF0) {
                header.frameType = SOF0;
                readStartOfFrame( header);
            }
            else if (current == DQT) {
                readQuantizationTable(header);
            }
            else if (current == DHT) {

                readHuffmanTable( header);

            }
            else if (current == SOS) {
                readStartOfScan(header);
                // break from while loop after SOS
                break;
            }
            else if (current == DRI) {
                readRestartInterval(header);
            }
            else if (current >= APP0 && current <= APP15) {
                readAPPN( header);


            }
            else if (current == COM) {
                readComment( header);
            }
            // unused markers that can be skipped
            else if ((current >= JPG0 && current <= JPG13) ||
                    current == DNL ||
                    current == DHP ||
                    current == EXP) {
                readComment( header);
            }
            else if (current == TEM) {
                // TEM has no size
            }
            // any number of 0xFF in a row is allowed and should be ignored
            else if (current == 0xFF) {
                current = BigEndianRead(Buffer, completeafter, 1);completeafter++;

                continue;
            }

            else if (current == SOI) {
                System.out.println("Error - Embedded JPGs not supported");

                header.valid = false;
                return header;
            }
            else if (current == EOI) {
                System.out.println("Error - EOI detected before SOS");
                header.valid = false;
                return header;
            }
            else if (current == DAC) {
                System.out.println("Error - Arithmetic Coding mode not supported");
                header.valid = false;
                return header;
            }
            else if (current >= SOF0 && current <= SOF15) {
                System.out.println("Error - SOF marker not supported: 0x"+current);

                header.valid = false;
                return header;
            }
            else if (current >= RST0 && current <= RST7) {
                System.out.println("Error - RSTN detected before SOS");

                header.valid = false;
                return header;
            }
            else {
                System.out.println("Error - Unknown marker: 0x"+current);
                header.valid = false;
                return header;
            }
            last = BigEndianRead(Buffer, completeafter, 1);completeafter++;
            current = BigEndianRead(Buffer, completeafter, 1);completeafter++;
        }

        // after SOS
        if (header.valid) {
            current =  BigEndianRead(Buffer, completeafter, 1);completeafter++;
            // read compressed image data
            while (true) {


                last = current;
                current = BigEndianRead(Buffer, completeafter, 1);completeafter++;
                // if marker is found
                if (last == 0xFF) {
                    // end of image
                    if (current == EOI) {
                        break;
                    }
                    // 0xFF00 means put a literal 0xFF in image data and ignore 0x00
                    else if (current == 0x00) {
                        header.huffmanData.add(last);
                        // overwrite 0x00 with next byte
                        current = BigEndianRead(Buffer, completeafter, 1);completeafter++;
                    }
                    // restart marker
                    else if (current >= RST0 && current <= RST7) {
                        // overwrite marker with next byte
                        current = BigEndianRead(Buffer, completeafter, 1);completeafter++;
                    }
                    // ignore multiple 0xFF's in a row
                    else if (current == 0xFF) {
                        // do nothing
                        continue;
                    }
                    else {
                        System.out.println("Error - Invalid marker during compressed data scan: 0x" + current);
                        header.valid = false;
                        return header;
                    }
                }
                else {
                    header.huffmanData.add(last);
                }
            }
        }
        return header;
    }


    static void printHeader(final Header header) {
        if (header == null) return;
        System.out.println("DQT=============");
        for (int i = 0; i < 4; ++i) {
            if (header.quantizationTables[i].set) {
                System.out.println( "Table ID: " + i);
                System.out.print("Table Data:");
                for (int j = 0; j < 64; ++j) {
                    if (j % 8 == 0) {
                        System.out.println();
                    }
                    System.out.print(header.quantizationTables[i].table[j]+ "  ");
                }
                System.out.println();
            }
        }
        System.out.println("SOF=============");
        System.out.println("Frame Type: 0x" + (int)header.frameType );
        System.out.println("Height: " + header.height );
        System.out.println("Width: " + header.width);
        System.out.println("Color Components:");
        for (int i = 0; i < header.numComponents; ++i) {
            System.out.println("Component ID: " + (i + 1) );
            System.out.println("Horizontal Sampling Factor: " + (int)header.colorComponents[i].horizontalSamplingFactor);
            System.out.println( "Vertical Sampling Factor: " + (int)header.colorComponents[i].verticalSamplingFactor );
            System.out.println( "Quantization Table ID:: " +header.colorComponents[i].quantizationTableID );
        }

        System.out.println( "DHT=============");
        System.out.println( "DC Tables:");

        for (int i = 0; i < 4; ++i) {
            if (header.huffmanDCTables[i].set) {
                System.out.println( "Table ID:"+i);
                System.out.println( "Symbols:");
                for (int j = 0; j < 16; ++j) {

                    System.out.print( (j + 1) + ": ");

                    for (int k = header.huffmanDCTables[i].offsets[j]; k < header.huffmanDCTables[i].offsets[j + 1];
                         ++k)        System.out.print(( int)header.huffmanDCTables[i].symbols[k] +" ");

                    System.out.println();
                }
            }
        }

        System.out.println("AC Tables:");

        for (int i = 0; i < 4; ++i) {
            if (header.huffmanACTables[i].set) {
                System.out.println( "Table ID: " +i);
                System.out.println( "Symbols: ");

                for (int j = 0; j < 16; ++j) {
                    System.out.print( (j + 1)+":");

                    for (int k = header.huffmanACTables[i].offsets[j]; k < header.huffmanACTables[i].offsets[j + 1]; ++k) {
                        System.out.print((int)header.huffmanACTables[i].symbols[k]+" ");

                    }
                    System.out.println();
                }
            }
        }
        System.out.println( " ");

        System.out.println("SOS=============");
        System.out.println("Start of Selection: " + (int)header.startOfSelection);
        System.out.println("End of Selection: " + (int)header.endOfSelection);
        System.out.println("Successive Approximation High: " + (int)header.successiveApproximationHigh );
        System.out.println("Successive Approximation Low: " + (int)header.successiveApproximationLow );
        System.out.println("Color Components:");
        for (int i = 0; i < header.numComponents; ++i) {
            System.out.println("Component ID: " + (i + 1) );
            System.out.println("Huffman DC Table ID: " + (int)header.colorComponents[i].huffmanDCTableID);
            System.out.println("Huffman AC Table ID: " + (int)header.colorComponents[i].huffmanACTableID );
        }
        System.out.println( "Length of Huffman Data: " + header.huffmanData.size());
        System.out.println( "DRI=============");
        System.out.println("Restart Interval: " + header.restartInterval);
    }


   }


