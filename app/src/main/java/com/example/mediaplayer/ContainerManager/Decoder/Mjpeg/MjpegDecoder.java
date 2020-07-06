package com.example.mediaplayer.ContainerManager.Decoder.Mjpeg;


import android.graphics.Color;

import com.example.mediaplayer.ContainerManager.Decoder.Decoder;
import com.example.mediaplayer.Data.Frame.JFrame.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import static com.example.mediaplayer.ContainerManager.Decoder.Mjpeg.MjpegParser.*;

public class MjpegDecoder  {


    public static File SourceImage = new File("mjpg.Mjpg");
    public static FileInputStream inFile;
    public static int BytesToRead = 99999999;
    public static byte Buffer[] = new byte[BytesToRead];
    public static int completeafter = 0;

    public MjpegDecoder(byte [] jpeg)
    {
        Buffer=jpeg;
    }
    // generate all Huffman codes based on symbols from a Huffman table
    static void generateCodes(HuffmanTable hTable) {
        int code = 0;
        for (int i = 0; i < 16; ++i) {
            for (int j = hTable.offsets[i]; j < hTable.offsets[i + 1]; ++j) {
                hTable.codes[j] = code;
                code += 1;
            }
            code <<= 1;
        }
    }


    // helper class to read bits from a byte vector
    static class BitReader {
        int nextByte = 0;
        int nextBit = 0;
        Vector<Integer> data = new Vector<>();

        BitReader(Vector<Integer> data) {
            this.data = data;
        }

        // read one bit (0 or 1) or return -1 if all bits have already been read
        int readBit() {
            if (nextByte >= data.size()) {
                return -1;
            }
            int bit = (data.get(nextByte) >> (7 - nextBit)) & 1;
            nextBit += 1;
            if (nextBit == 8) {
                nextBit = 0;
                nextByte += 1;
            }
            return bit;
        }

        // read a variable number of bits
        // first read bit is most significant bit
        // return -1 if at any point all bits have already been read
        int readBits(final int length) {
            int bits = 0;
            for (int i = 0; i < length; ++i) {
                int bit = readBit();
                if (bit == -1) {
                    bits = -1;
                    break;
                }
                bits = (bits << 1) | bit;
            }
            return bits;
        }

        // if there are bits remaining,
        //   advance to the 0th bit of the next byte
        void align() {
            if (nextByte >= data.size()) {
                return;
            }
            if (nextBit != 0) {
                nextBit = 0;
                nextByte += 1;
            }
        }
    }

    ;
// return the symbol from the Huffman table that corresponds to
//   the next Huffman code read from the BitReader

    static int getNextSymbol(BitReader b, final HuffmanTable hTable) {
        int currentCode = 0;
        for (int i = 0; i < 16; ++i) {
            int bit = b.readBit();
            if (bit == -1) {
                return -1;
            }
            currentCode = (currentCode << 1) | bit;
            for (int j = hTable.offsets[i]; j < hTable.offsets[i + 1]; ++j) {
                if (currentCode == hTable.codes[j]) {
                    return hTable.symbols[j];
                }
            }
        }
        return -1;
    }

    static int[] previousDCs = new int[3];

    // fill the coefficients of an MCU component based on Huffman codes
//   read from the BitReader
    static boolean decodeMCUComponent(BitReader b, int[] component, int pindex,
                                      HuffmanTable dcTable,
                                      HuffmanTable acTable) {
        // get the DC value for this MCU component
        int length = getNextSymbol(b, dcTable);
        if (length == (byte) -1) {
            System.out.println("Error - Invalid DC value");
            return false;
        }
        if (length > 11) {
            System.out.println("Error - DC coefficient length greater than 11");
            return false;
        }

        int coeff = b.readBits(length);
        if (coeff == -1) {
            System.out.println("Error - Invalid DC value");

            return false;
        }
        if (length != 0 && coeff < (1 << (length - 1))) {
            coeff -= (1 << length) - 1;
        }

        component[0] = coeff + previousDCs[pindex];
        previousDCs[pindex] = component[0];

        // get the AC values for this MCU component
        int i = 1;
        while (i < 64) {
            int symbol = getNextSymbol(b, acTable);
            if (symbol == (byte) -1) {
                System.out.println("Error - Invalid AC value");

                return false;
            }

            // symbol 0x00 means fill remainder of component with 0
            if (symbol == 0x00) {
                for (; i < 64; ++i) {
                    component[MjpegParser.zigZagMap[i]] = 0;
                }
                return true;
            }

            // otherwise, read next component coefficient
            int numZeroes = symbol >> 4;
            int coeffLength = symbol & 0x0F;
            coeff = 0;

            // symbol 0xF0 means skip 16 0's
            if (symbol == 0xF0) {
                numZeroes = 16;
            }

            if (i + numZeroes >= 64) {
                System.out.println("Error - Zero run-length exceeded MCU");

                return false;
            }
            for (int j = 0; j < numZeroes; ++j, ++i) {
                component[MjpegParser.zigZagMap[i]] = 0;
            }

            if (coeffLength > 10) {
                System.out.println("Error - AC coefficient length greater than 10");
                return false;
            }
            if (coeffLength != 0) {
                coeff = b.readBits(coeffLength);
                if (coeff == -1) {
                    System.out.println("Error - Invalid AC value");

                    return false;
                }
                if (coeff < (1 << (coeffLength - 1))) {
                    coeff -= (1 << coeffLength) - 1;
                }
                component[MjpegParser.zigZagMap[i]] = coeff;
                i += 1;
            }
        }
        return true;
    }

    // decode all the Huffman data and fill all MCUs
    static MCU[] decodeHuffmanData(Header header) {
        MCU[] mcus = new MCU[header.mcuHeightReal * header.mcuWidthReal];
        if (mcus == null) {
            System.out.println("Error - Memory error");
            return null;
        }
        for (int i = 0; i < mcus.length; i++)
            mcus[i] = new MCU();
        for (int i = 0; i < 4; ++i) {
            if (header.huffmanDCTables[i].set) {
                generateCodes(header.huffmanDCTables[i]);
            }
            if (header.huffmanACTables[i].set) {
                generateCodes(header.huffmanACTables[i]);
            }
        }

        BitReader b = new BitReader(header.huffmanData);

        int restartInterval = header.restartInterval * header.horizontalSamplingFactor * header.verticalSamplingFactor;
        for (int y = 0; y < header.mcuHeight; y += header.verticalSamplingFactor) {
            for (int x = 0; x < header.mcuWidth; x += header.horizontalSamplingFactor) {
                if (restartInterval != 0 && (y * header.mcuWidthReal + x) % restartInterval == 0) {
                    previousDCs[0] = 0;
                    previousDCs[1] = 0;
                    previousDCs[2] = 0;
                    b.align();
                }

                for (int i = 0; i < header.numComponents; ++i) {
                    for (int v = 0; v < header.colorComponents[i].verticalSamplingFactor; ++v) {
                        for (int h = 0; h < header.colorComponents[i].horizontalSamplingFactor; ++h) {
                            if (!decodeMCUComponent(
                                    b,
                                    mcus[(y + v) * header.mcuWidthReal + (x + h)].getComponent(i), i,
                                    header.huffmanDCTables[header.colorComponents[i].huffmanDCTableID],
                                    header.huffmanACTables[header.colorComponents[i].huffmanACTableID])) {
                                return null;
                            }
                        }
                    }
                }
            }
        }
        return mcus;
    }


    static void dequantizeMCUComponent(final QuantizationTable qTable, int[] component) {
        for (int i = 0; i < 64; ++i) {
            component[i] *= qTable.table[i];
        }
    }

    static void dequantize(Header header, MCU[] mcus) {
        for (int y = 0; y < header.mcuHeight; y += header.verticalSamplingFactor) {
            for (int x = 0; x < header.mcuWidth; x += header.horizontalSamplingFactor) {
                for (int i = 0; i < header.numComponents; ++i) {
                    for (int v = 0; v < header.colorComponents[i].verticalSamplingFactor; ++v) {
                        for (int h = 0; h < header.colorComponents[i].horizontalSamplingFactor; ++h) {
                            dequantizeMCUComponent(header.quantizationTables[header.colorComponents[i].quantizationTableID],
                                    mcus[(y + v) * header.mcuWidthReal + (x + h)].getComponent(i));
                        }
                    }
                }
            }
        }
    }

    static void inverseDCTComponent(int[] component) {
        for (int i = 0; i < 8; ++i) {
            final float g0 = component[0 * 8 + i] * s0;

            final float g1 = component[4 * 8 + i] * s4;
            final float g2 = component[2 * 8 + i] * s2;
            final float g3 = component[6 * 8 + i] * s6;
            final float g4 = component[5 * 8 + i] * s5;
            final float g5 = component[1 * 8 + i] * s1;
            final float g6 = component[7 * 8 + i] * s7;
            final float g7 = component[3 * 8 + i] * s3;

            final float f0 = g0;
            final float f1 = g1;
            final float f2 = g2;
            final float f3 = g3;
            final float f4 = g4 - g7;
            final float f5 = g5 + g6;
            final float f6 = g5 - g6;
            final float f7 = g4 + g7;

            final float e0 = f0;
            final float e1 = f1;
            final float e2 = f2 - f3;
            final float e3 = f2 + f3;
            final float e4 = f4;
            final float e5 = f5 - f7;
            final float e6 = f6;
            final float e7 = f5 + f7;
            final float e8 = f4 + f6;

            final float d0 = e0;
            final float d1 = e1;
            final float d2 = e2 * m1;
            final float d3 = e3;
            final float d4 = e4 * m2;
            final float d5 = e5 * m3;
            final float d6 = e6 * m4;
            final float d7 = e7;
            final float d8 = e8 * m5;

            final float c0 = d0 + d1;
            final float c1 = d0 - d1;
            final float c2 = d2 - d3;
            final float c3 = d3;
            final float c4 = d4 + d8;
            final float c5 = d5 + d7;
            final float c6 = d6 - d8;
            final float c7 = d7;
            final float c8 = c5 - c6;

            final float b0 = c0 + c3;
            final float b1 = c1 + c2;
            final float b2 = c1 - c2;
            final float b3 = c0 - c3;
            final float b4 = c4 - c8;
            final float b5 = c8;
            final float b6 = c6 - c7;
            final float b7 = c7;

            component[0 * 8 + i] = (int) (b0 + b7);
            component[1 * 8 + i] = (int) (b1 + b6);
            component[2 * 8 + i] = (int) (b2 + b5);
            component[3 * 8 + i] = (int) (b3 + b4);
            component[4 * 8 + i] = (int) (b3 - b4);
            component[5 * 8 + i] = (int) (b2 - b5);
            component[6 * 8 + i] = (int) (b1 - b6);
            component[7 * 8 + i] = (int) (b0 - b7);
        }
        for (int i = 0; i < 8; ++i) {
            final float g0 = component[i * 8 + 0] * s0;
            final float g1 = component[i * 8 + 4] * s4;
            final float g2 = component[i * 8 + 2] * s2;
            final float g3 = component[i * 8 + 6] * s6;
            final float g4 = component[i * 8 + 5] * s5;
            final float g5 = component[i * 8 + 1] * s1;
            final float g6 = component[i * 8 + 7] * s7;
            final float g7 = component[i * 8 + 3] * s3;

            final float f0 = g0;
            final float f1 = g1;
            final float f2 = g2;
            final float f3 = g3;
            final float f4 = g4 - g7;
            final float f5 = g5 + g6;
            final float f6 = g5 - g6;
            final float f7 = g4 + g7;

            final float e0 = f0;
            final float e1 = f1;
            final float e2 = f2 - f3;
            final float e3 = f2 + f3;
            final float e4 = f4;
            final float e5 = f5 - f7;
            final float e6 = f6;
            final float e7 = f5 + f7;
            final float e8 = f4 + f6;

            final float d0 = e0;
            final float d1 = e1;
            final float d2 = e2 * m1;
            final float d3 = e3;
            final float d4 = e4 * m2;
            final float d5 = e5 * m3;
            final float d6 = e6 * m4;
            final float d7 = e7;
            final float d8 = e8 * m5;

            final float c0 = d0 + d1;
            final float c1 = d0 - d1;
            final float c2 = d2 - d3;
            final float c3 = d3;
            final float c4 = d4 + d8;
            final float c5 = d5 + d7;
            final float c6 = d6 - d8;
            final float c7 = d7;
            final float c8 = c5 - c6;

            final float b0 = c0 + c3;
            final float b1 = c1 + c2;
            final float b2 = c1 - c2;
            final float b3 = c0 - c3;
            final float b4 = c4 - c8;
            final float b5 = c8;
            final float b6 = c6 - c7;
            final float b7 = c7;

            component[i * 8 + 0] = (int) (b0 + b7);
            component[i * 8 + 1] = (int) (b1 + b6);
            component[i * 8 + 2] = (int) (b2 + b5);
            component[i * 8 + 3] = (int) (b3 + b4);
            component[i * 8 + 4] = (int) (b3 - b4);
            component[i * 8 + 5] = (int) (b2 - b5);
            component[i * 8 + 6] = (int) (b1 - b6);
            component[i * 8 + 7] = (int) (b0 - b7);

        }


    }

    static void inverseDCT(Header header, MCU[] mcus) {
        for (int y = 0; y < header.mcuHeight; y += header.verticalSamplingFactor) {
            for (int x = 0; x < header.mcuWidth; x += header.horizontalSamplingFactor) {
                for (int i = 0; i < header.numComponents; ++i) {
                    for (int v = 0; v < header.colorComponents[i].verticalSamplingFactor; ++v) {
                        for (int h = 0; h < header.colorComponents[i].horizontalSamplingFactor; ++h) {
                            inverseDCTComponent(mcus[(y + v) * header.mcuWidthReal + (x + h)].getComponent(i));
                        }
                    }
                }
            }
        }
    }


    // convert all pixels in an MCU from YCbCr color space to RGB
    static void YCbCrToRGBMCU(Header header, MCU mcu, MCU cbcr, int v, int h) {
        for (int y = 7; y < 8; --y) {
            if (y < 0) break;

            for (int x = 7; x < 8; --x) {
                if (x < 0) break;

                final int pixel = y * 8 + x;
                final int cbcrPixelRow = y / header.verticalSamplingFactor + 4 * v;
                final int cbcrPixelColumn = x / header.horizontalSamplingFactor + 4 * h;
                final int cbcrPixel = cbcrPixelRow * 8 + cbcrPixelColumn;
                int r = (int) (mcu.y[pixel] + 1.402f * cbcr.cr[cbcrPixel] + 128);
                int g = (int) (mcu.y[pixel] - 0.344f * cbcr.cb[cbcrPixel] - 0.714f * cbcr.cr[cbcrPixel] + 128);
                int b = (int) (mcu.y[pixel] + 1.772f * cbcr.cb[cbcrPixel] + 128);
                if (r < 0) r = 0;
                if (r > 255) r = 255;
                if (g < 0) g = 0;
                if (g > 255) g = 255;
                if (b < 0) b = 0;
                if (b > 255) b = 255;
                mcu.r[pixel] = r;
                mcu.g[pixel] = g;
                mcu.b[pixel] = b;
            }
        }
    }

    // convert all pixels from YCbCr color space to RGB
    static void YCbCrToRGB(Header header, MCU[] mcus) {

        for (int y = 0; y < header.mcuHeight; y += header.verticalSamplingFactor) {
            for (int x = 0; x < header.mcuWidth; x += header.horizontalSamplingFactor) {
                MCU cbcr = mcus[y * header.mcuWidthReal + x];
                for (int v = header.verticalSamplingFactor - 1; v < header.verticalSamplingFactor; --v) {
                    if (v < 0) break;
                    for (int h = header.horizontalSamplingFactor - 1; h < header.horizontalSamplingFactor; --h) {
                        if (h < 0) break;
                        MCU mcu = mcus[(y + v) * header.mcuWidthReal + (x + h)];
                        YCbCrToRGBMCU(header, mcu, cbcr, v, h);
                    }
                }
            }
        }
    }

    // helper function to write a 4-byte integer in little-endian
    static void putInt(FileOutputStream outFile, int v) {
        try {
            outFile.write((v >> 0) & 0xFF);
            outFile.write((v >> 8) & 0xFF);
            outFile.write((v >> 16) & 0xFF);
            outFile.write((v >> 24) & 0xFF);
        } catch (IOException ex) {
          //  Logger.getLogger(decoder_jpeg.Decoder_JPEG.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // helper function to write a 2-byte short integer in little-endian
    static void putShort(FileOutputStream outFile, int v) {
        try {
            outFile.write((v >> 0) & 0xFF);
            outFile.write((v >> 8) & 0xFF);
        } catch (IOException ex) {
   //         Logger.getLogger(decoder_jpeg.Decoder_JPEG.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // write all the pixels in the MCUs to a BMP file
    static void writeBMP(Header header, MCU[] mcus, int X) {
        // open file
        FileOutputStream outFile;
        try {
            outFile = new FileOutputStream(new File("photo" + X + ".bmp"));


            int paddingSize = header.width % 4;

            int size = 14 + 12 + header.height * header.width * 3 + paddingSize * header.height;

            outFile.write('B');
            outFile.write('M');
            putInt(outFile, size);
            putInt(outFile, 0);
            putInt(outFile, 0x1A);
            putInt(outFile, 12);
            putShort(outFile, header.width);
            putShort(outFile, header.height);
            putShort(outFile, 1);
            putShort(outFile, 24);

            for (int y = header.height - 1; y < header.height; --y) {
                if (y < 0) break;

                int mcuRow = y / 8;
                int pixelRow = y % 8;
                for (int x = 0; x < header.width; ++x) {
                    int mcuColumn = x / 8;
                    int pixelColumn = x % 8;
                    int mcuIndex = mcuRow * header.mcuWidthReal + mcuColumn;
                    int pixelIndex = pixelRow * 8 + pixelColumn;

                    outFile.write(mcus[mcuIndex].getB()[pixelIndex]);
                    outFile.write(mcus[mcuIndex].getG()[pixelIndex]);
                    outFile.write(mcus[mcuIndex].getR()[pixelIndex]);

                }
            }


            for (int i = 0; i < paddingSize; ++i)
                outFile.write(0);
        } catch (Exception ex) {
            //      Logger.getLogger(decoder_jpeg.Decoder_JPEG.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public MCU[] decode() {
        try {
            //inFile = new FileInputStream(SourceImage);
            //inFile.read(Buffer, 0, BytesToRead);
        } catch (Exception ex) {
        }
        MjpegParser m = new MjpegParser();
        for (int i = 0; i < 1; i++) {
            for (int x = 0; x < 3; x++)
                previousDCs[x] = 0;
        System.out.println("Entered");
            Header header = m.readJPG();
            // validate header
            if (header == null) {
                System.out.println("NULL");
            }
            if (header.valid == false) {
                System.out.println("Error - Invalid JPG");

            }

            // decode Huffman data
            MCU[] mcus = decodeHuffmanData(header);
            if (mcus == null) {
                System.out.println("MCU NULL");

            }

            // dequantize MCU coefficients
            dequantize(header, mcus);

            // Inverse Discrete Cosine Transform
            inverseDCT(header, mcus);

            // color conversion
            YCbCrToRGB(header, mcus);
            return mcus;
           // writeBMP(header, mcus, i);

        }
        return null;
    }
}
