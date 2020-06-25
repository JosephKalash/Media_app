package com.example.mediaplayer.ContainerManager.Decoder;

import com.example.mediaplayer.Data.Container.Container;
import com.example.mediaplayer.Data.Frame.mp3Frame.Mp3Data;
import com.example.mediaplayer.Data.Frame.mp3Frame.Mp3StandardData;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class Mp3Decoder extends Decoder {
    public Mp3Decoder(Container container) {
        super(container);
    }

    @Override
    public void Decode() {

    }
    private static int[] count1;
    private static int[] scalefac_l ;
    private static int[] scalefac_s;
    private static float[] tempDecodedData;



    public static Mp3Data decode(InputStream inSt) throws IOException {
        Mp3Data mp3Data = new Mp3Data(inSt);
        mp3Data.buffer.lastByte = inSt.read();
        if(!startDecode(mp3Data))
            return null;
        return mp3Data;
    }
    public static boolean startDecode(Mp3Data mp3Data) throws IOException {
        if(mp3Data.buffer.lastByte == -1) //end of file
            return false;

        if(!findFrame(mp3Data)) ///find frame
            return false;

        mp3Data.buffer.current = 4;

        readFrameHeader(mp3Data); //read header info

        read(mp3Data.buffer, 4); //unused data

        if (mp3Data.getMp3Header().getLayer() == 0b01 ) {//mp3
            int frequency = Mp3StandardData.SAMPLING_FREQUENCY[mp3Data.getMp3Header().getFrequancy()];
            int frameLength = (144 * mp3Data.getMp3Header().getBitRate())
                    / frequency + mp3Data.getMp3Header().getPadBit();

            int numOfChannel =  mp3Data.getStereo() == 1 ? 2 : 1;

            decode_sideInfo(mp3Data , mp3Data.buffer , numOfChannel);//sideInfo

            decode_MainData(mp3Data , mp3Data.buffer, numOfChannel , mp3Data.getMainDataReader(),
                    frameLength, mp3Data.getMp3Header().getFrequancy());//mainData
        }

        if (mp3Data.buffer.current != 0) {
            read(mp3Data.buffer, 8 - mp3Data.buffer.current);
        }
        return true;
    }

    private static void decode_MainData(Mp3Data mp3Data ,Mp3Data.Buffer buffer, int stereo , Mp3Data.MainDataReader mainDataReader,
                                        int frameLength, int samplingFrequency) throws IOException {

        count1 = new int[stereo * 2];
        scalefac_l = new int[stereo * 2 * 21];
        scalefac_s = new int[stereo * 2 * 12 * 3];
        tempDecodedData = new float[stereo * 2 * 576];
        int mainDataBegin = mp3Data.mp3SideInfo.getMainDataBegin();

        System.arraycopy(mainDataReader.mainData, mainDataReader.top - mainDataBegin,
                mainDataReader.mainData, 0, mainDataBegin);

        int mainDataSize = frameLength - (stereo == 2 ? 32 : 17/*side_info*/) - 4/*header*/;
        readInto(buffer, mainDataReader.mainData, mainDataBegin, mainDataSize);
        mainDataReader.index = 0;
        mainDataReader.current = 0;
        mainDataReader.top = mainDataBegin + mainDataSize;


        for (int granule = 0; granule < 2; granule++)
            for (int channel = 0; channel < stereo; channel++) {
                int part_2_start = mainDataReader.index * 8 + mainDataReader.current;

                //start decoding scalefactor
                int slen1 = Mp3StandardData.SCALEFACTOR_SIZES_LAYER_III
                        [mp3Data.mp3SideInfo.scalefac_compress[channel * 2 + granule] * 2];
                int slen2 = Mp3StandardData.SCALEFACTOR_SIZES_LAYER_III
                        [mp3Data.mp3SideInfo.scalefac_compress[channel * 2 + granule] * 2 + 1];

                if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] != 0) &&
                        (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2)) {
                    if (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] != 0) {
                        for (int sfband = 0; sfband < 8; sfband++)
                            scalefac_l[channel * 2 * 21 + granule * 21 + sfband] = read(mainDataReader, slen1);

                        for (int sfband = 3; sfband < 12; sfband++) {
                            int nbits;
                            if (sfband < 6)
                                nbits = slen1;
                            else
                                nbits = slen2;

                            for (int window = 0; window < 3; window++)
                                scalefac_s[channel * 2 * 12 * 3 + granule * 12 * 3 + sfband * 3 + window] =
                                        read(mainDataReader, nbits);
                        }
                    } else {
                        for (int sfband = 0; sfband < 12; sfband++) {
                            int nbits;
                            if (sfband < 6) {
                                nbits = slen1;
                            } else {
                                nbits = slen2;
                            }

                            for (int window = 0; window < 3; window++)
                                scalefac_s[channel * 2 * 12 * 3 + granule * 12 * 3 + sfband * 3 + window] =
                                        read(mainDataReader, nbits);
                        }
                    }
                } else { /* block_type == 0 if winswitch == 0 */

                    /* Scale factor bands 0-5 */
                    if ((mp3Data.mp3SideInfo.scfsi[channel * 4] == 0) || (granule == 0)) {
                        for (int sfband = 0; sfband < 6; sfband++)
                            scalefac_l[channel * 2 * 21 + granule * 21 + sfband] = read(mainDataReader, slen1);

                    } else if ((mp3Data.mp3SideInfo.scfsi[channel * 4] == 1) && (granule == 1)) {
                        /* Copy scalefactors from granule 0 to granule 1 */
                        for (int sfb = 0; sfb < 6; sfb++) {
                            scalefac_l[channel * 2 * 21 + 21 + sfb] =
                                    scalefac_l[channel * 2 * 21 + sfb];
                        }
                    }

                    /* Scale factor bands 6-10 */
                    if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 1] == 0) || (granule == 0)) {
                        for (int sfb = 6; sfb < 11; sfb++) {
                            scalefac_l[channel * 2 * 21 + granule * 21 + sfb] = read(mainDataReader, slen1);
                        }
                    } else if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 1] == 1) && (granule == 1)) {
                        /* Copy scalefactors from granule 0 to granule 1 */
                        for (int sfb = 6; sfb < 11; sfb++) {
                            scalefac_l[channel * 2 * 21 + 21 + sfb] =
                                    scalefac_l[channel * 2 * 21 + sfb];
                        }
                    }

                    /* Scale factor bands 11-15 */
                    if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 2] == 0) || (granule == 0)) {
                        for (int sfb = 11; sfb < 16; sfb++) {
                            scalefac_l[channel * 2 * 21 + granule * 21 + sfb] = read(mainDataReader, slen2);
                        }
                    } else if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 2] == 1) && (granule == 1)) {
                        /* Copy scalefactors from granule 0 to granule 1 */
                        for (int sfb = 11; sfb < 16; sfb++) {
                            scalefac_l[channel * 2 * 21 + 21 + sfb] =
                                    scalefac_l[channel * 2 * 21 + sfb];
                        }
                    }

                    /* Scale factor bands 16-20 */
                    if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 3] == 0) || (granule == 0)) {
                        for (int sfb = 16; sfb < 21; sfb++) {
                            scalefac_l[channel * 2 * 21 + granule * 21 + sfb] = read(mainDataReader, slen2);
                        }
                    } else if ((mp3Data.mp3SideInfo.scfsi[channel * 4 + 3] == 1) && (granule == 1)) {
                        /* Copy scalefactors from granule 0 to granule 1 */
                        for (int sfb = 16; sfb < 21; sfb++) {
                            scalefac_l[channel * 2 * 21 + 21 + sfb] =
                                    scalefac_l[channel * 2 * 21 + sfb];
                        }
                    }
                }

                // Check if there tempDecodedData mainData to decode
                if (mp3Data.mp3SideInfo.part2_3_length[channel * 2 + granule] != 0) {

                    //the index of the last bit for this part.
                    int bit_pos_end = part_2_start + mp3Data.mp3SideInfo.part2_3_length[channel * 2 + granule] - 1;

                    int region_0_start;
                    int region_1_start;
                    int table_num;
                    int inPos;
                    int[] huffman = new int[4];

                    //Determine region boundaries
                    if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                            (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2)) {

                        region_0_start = 36;
                        region_1_start = 576; // No Region2 for short block case.
                    } else {
                        region_0_start =
                                Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                        [samplingFrequency * (23 + 14) +
                                        mp3Data.mp3SideInfo.region0_count[channel * 2 + granule] + 1];
                        region_1_start =
                                Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                        [samplingFrequency * (23 + 14) +
                                        mp3Data.mp3SideInfo.region0_count[channel * 2 + granule]
                                        + mp3Data.mp3SideInfo.region1_count[channel * 2 + granule] + 2];
                    }

                    // Read big_values using tables according to region_x_start
                    for (inPos = 0; inPos < mp3Data.mp3SideInfo.big_values[channel * 2 + granule] * 2; inPos++) {

                        if (inPos < region_0_start)
                            table_num = mp3Data.mp3SideInfo.table_select[channel * 2 * 3 + granule * 3];
                        else if (inPos < region_1_start)
                            table_num = mp3Data.mp3SideInfo.table_select[channel * 2 * 3 + granule * 3 + 1];
                        else
                            table_num = mp3Data.mp3SideInfo.table_select[channel * 2 * 3 + granule * 3 + 2];

                        // decode Huffman coded words
                        decode_huffmanCode(mainDataReader, table_num, huffman);

                        // In the big_values area there are two freq lines per Huffman word
                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos++] = huffman[0];
                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] = huffman[1];
                    }

                    // Read count1
                    table_num = mp3Data.mp3SideInfo.count1table_select[channel * 2 + granule] + 32;/*start at 32th table*/
                    for (inPos = mp3Data.mp3SideInfo.big_values[channel * 2 + granule] * 2;
                         (inPos <= 572) && (mainDataReader.index * 8 + mainDataReader.current <= bit_pos_end); inPos++) {

                        decode_huffmanCode(mainDataReader, table_num, huffman);

                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos++] = huffman[2];
                        if (inPos >= 576) break;

                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos++] = huffman[3];
                        if (inPos >= 576) break;

                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos++] = huffman[0];
                        if (inPos >= 576) break;

                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] = huffman[1];
                    }

                    // Check that we didn't read past the end of this section
                    if (mainDataReader.index * 8 + mainDataReader.current > (bit_pos_end + 1))
                        inPos -= 4;// Remove last words read

                    // Setup count1 which tempDecodedData the index of the first sample in the rzero region.
                    count1[channel * 2 + granule] = inPos;

                    for (/* inPos comes from last for */; inPos < 576; inPos++) {
                        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] = 0.0f;
                    }

                    // Set the bitpos to point to the next part to read
                    mainDataReader.index = (bit_pos_end + 1) / 8;
                    mainDataReader.current = (bit_pos_end + 1) % 8;
                }
            }
        if(mp3Data.getSamplesBuffer() == null)
            mp3Data.initSampleBuffer(18 * 32 * 2 * stereo * 2);

        for (int gr = 0; gr < 2; gr++ ){
            for (int ch = 0; ch < stereo; ch++) {

                requantize(mp3Data,ch, gr);
                reorder(mp3Data,ch, gr);
            }
            stereo(mp3Data,gr);
            for (int ch = 0; ch < stereo; ch++) {
                antiAliasing(mp3Data,ch , gr);
                hybirdSynthesis(mp3Data,ch , gr);
                frequencyInversion(ch , gr);
                polyphaseSubbandSynthesis(mp3Data,ch , gr);
            }
        }

    }
    private static boolean findFrame(Mp3Data mp3Data) throws IOException {
        int currBits = 0;
        while (true) {
            while (currBits != 0b11111111){   //8-bits is 1s
                currBits = mp3Data.buffer.lastByte;
                mp3Data.buffer.lastByte = mp3Data.buffer.in.read();
                if (mp3Data.buffer.lastByte == -1)
                    return false;
            }
            if ((mp3Data.buffer.lastByte >>> 4) != 0b1111) {//most lift bits is 1s
                mp3Data.buffer.lastByte = mp3Data.buffer.in.read();
                if (mp3Data.buffer.lastByte == -1)
                    return false;
            } else
                return true;
        }
    }
    private static void readFrameHeader(Mp3Data mp3Data) throws IOException {
        mp3Data.getMp3Header().setID(read(mp3Data.buffer, 1));
        int layer = read(mp3Data.buffer, 2);
        mp3Data.getMp3Header().setLayer(layer);
        int protBit = read(mp3Data.buffer, 1);
        int bitRateInd = read(mp3Data.buffer, 4);
        mp3Data.getMp3Header().setFrequancy(read(mp3Data.buffer, 2));
        mp3Data.getMp3Header().setPadBit( read(mp3Data.buffer, 1));
        mp3Data.getMp3Header().setPrivBit(read(mp3Data.buffer, 1));
        int mode = read(mp3Data.buffer, 2);
        mp3Data.getMp3Header().setMode(mode);

        mp3Data.getMp3Header().setModeExtesion(read(mp3Data.buffer, 2));

        setupDataBasedOnHeader(mp3Data ,layer , mode , protBit , bitRateInd);
    }
    private static void setupDataBasedOnHeader(Mp3Data mp3Data,int layer , int mode , int protBit , int bitRateInd) throws IOException {

        mp3Data.getMp3Header().setBitRate(Mp3StandardData.BITRATE_LAYER_III[bitRateInd]);
        if(mp3Data.getStereo() == -1){
            if (mode == 0b11 ) // one channel
                mp3Data.setStereo(0);
            else //two
                mp3Data.setStereo(1);

            if (layer == 0b01 ) {// layer 3 (mp3)
                if (mode == 0b11) { // one channel
                    mp3Data.initMainData(1024);
                    mp3Data.initStore(32 * 18);
                    mp3Data.initV(1024);
                } else { //two
                    mp3Data.initMainData(2 * 1024);
                    mp3Data.initStore(2 * 32 * 18);
                    mp3Data.initV(2 * 1024);
                }
                mp3Data.initMainDataReader();
            }
        }

        if (protBit == 0)//on progress
            read(mp3Data.buffer, 16);
    }
    private static void decode_sideInfo(Mp3Data mp3Data,Mp3Data.Buffer buffer, int numOfChannel) throws IOException {

        mp3Data.mp3SideInfo.setMainDataBegin(read(buffer, 9));

        read(buffer, numOfChannel == 1 ? 5 : 3);//this for private_bit but it unused


        for (int channel = 0; channel < numOfChannel; channel++) //4-bits for each channel
            for (int scaleband = 0; scaleband < 4; scaleband++)
                mp3Data.mp3SideInfo.scfsi[channel * 4 + scaleband] = read(buffer, 1);

        //just reading info for each channel per granule
        for (int granule = 0; granule < 2; granule++)
            for (int channel = 0; channel < numOfChannel; channel++) {
                mp3Data.mp3SideInfo.part2_3_length[channel * 2 + granule] = read(buffer, 12);
                mp3Data.mp3SideInfo.big_values[channel * 2 + granule] = read(buffer, 9);
                mp3Data.mp3SideInfo.global_gain[channel * 2 + granule] = read(buffer, 8);
                mp3Data.mp3SideInfo.scalefac_compress[channel * 2 + granule] = read(buffer, 4);

                mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] = read(buffer, 1);

                if (mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) {
                    mp3Data.mp3SideInfo.block_type[channel * 2 + granule] = read(buffer, 2);
                    mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] = read(buffer, 1);
                    for (int region = 0; region < 2; region++) {
                        mp3Data.mp3SideInfo.table_select[channel * 2 * 3 + granule * 3 + region] = read(buffer, 5);
                    }
                    for (int window = 0; window < 3; window++) {
                        mp3Data.mp3SideInfo.subblock_gain[channel * 2 * 3 + granule * 3 + window] = read(buffer, 3);
                    }
                    if ((mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2) &&
                            (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] == 0)) {
                        mp3Data.mp3SideInfo.region0_count[channel * 2 + granule] = 8;
                    } else {
                        mp3Data.mp3SideInfo.region0_count[channel * 2 + granule] = 7;
                    }

                    mp3Data.mp3SideInfo.region1_count[channel * 2 + granule] =
                            20 - mp3Data.mp3SideInfo.region0_count[channel * 2 + granule];
                } else {
                    for (int region = 0; region < 3; region++) {
                        mp3Data.mp3SideInfo.table_select[channel * 2 * 3 + granule * 3 + region] = read(buffer, 5);
                    }
                    mp3Data.mp3SideInfo.region0_count[channel * 2 + granule] = read(buffer, 4);
                    mp3Data.mp3SideInfo.region1_count[channel * 2 + granule] = read(buffer, 3);
                    mp3Data.mp3SideInfo.block_type[channel * 2 + granule] = 0;
                }
                mp3Data.mp3SideInfo.preflag[channel * 2 + granule] = read(buffer, 1);
                mp3Data.mp3SideInfo.scalefac_scale[channel * 2 + granule] = read(buffer, 1);
                mp3Data.mp3SideInfo.count1table_select[channel * 2 + granule] = read(buffer, 1);
            }
    }
    private static void decode_huffmanCode(Mp3Data.MainDataReader mainDataReader, int table_num, int[] array) {
        int point = 0;

        if (Mp3StandardData.HUFFMAN_TREELEN_LAYER_III[table_num] == 0) {
            array[0] = array[1] = array[2] = array[3] = 0;
            return;
        }

        int treelen = Mp3StandardData.HUFFMAN_TREELEN_LAYER_III[table_num];
        int linbits = Mp3StandardData.HUFFMAN_LINBITS_LAYER_III[table_num];
        int offset = Mp3StandardData.HUFFMAN_TABLE_OFFSET_LAYER_III[table_num];

        int error = 1;
        int bitsleft = 32;

        do {
            if ((Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] & 0xff00) == 0) {
                error = 0;
                array[0] = (Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] >> 4) & 0xf;
                array[1] = Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] & 0xf;
                break;
            }
            if (read(mainDataReader, 1) != 0) { /* Go right in tree */
                while ((Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] & 0xff) >= 250) {
                    point += Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] & 0xff;
                }
                point += Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] & 0xff;
            } else {
                while ((Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] >> 8) >= 250) {
                    point += Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] >> 8;
                }
                point += Mp3StandardData.HUFFMAN_TABLE_LAYER_III[offset + point] >> 8;
            }
        } while ((--bitsleft > 0) && (point < treelen));
        if (error != 0) {
            array[0] = array[1] = 0;
            throw new IllegalStateException("Illegal Huff code in data. bleft = %d,point = %d. tab = %d." +
                    bitsleft + " " + point + " " + table_num);
        }
        if (table_num > 31) {
            array[2] = (array[1] >> 3) & 1;
            array[3] = (array[1] >> 2) & 1;
            array[0] = (array[1] >> 1) & 1;
            array[1] = array[1] & 1;

            if (array[2] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[2] = -array[2];

            if (array[3] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[3] = -array[3];

            if (array[0] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[0] = -array[0];

            if (array[1] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[1] = -array[1];

        } else {
            if ((linbits > 0) && (array[0] == 15))
                array[0] += read(mainDataReader, linbits);

            if (array[0] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[0] = -array[0];

            if ((linbits > 0) && (array[1] == 15))
                array[1] += read(mainDataReader, linbits);

            if (array[1] > 0)
                if (read(mainDataReader, 1) == 1)
                    array[1] = -array[1];
        }
    }
    private static void requantize(Mp3Data mp3Data,int channel , int granule){

        if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2)) {

            if (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] != 0) {

                int sfb = 0;
                int next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb + 1];
                for (int i = 0; i < 36; i++) {
                    if (i == next_sfb) {
                        sfb++;
                        next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb + 1];
                    }
                    requantize_long_III(granule, channel, mp3Data.mp3SideInfo.scalefac_scale,
                            mp3Data.mp3SideInfo.preflag, mp3Data.mp3SideInfo.global_gain,
                            i, sfb);
                }

                sfb = 3;
                next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
                int win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                        Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];

                for (int i = 36; i < count1[channel * 2 + granule];) {

                    if (i == next_sfb) {
                        sfb++;
                        next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
                        win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                                Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];
                    }
                    for (int win = 0; win < 3; win++) {
                        for (int j = 0; j < win_len; j++) {
                            requantize_short_III(granule, channel, mp3Data.mp3SideInfo.scalefac_scale,
                                    mp3Data.mp3SideInfo.subblock_gain,mp3Data.mp3SideInfo.global_gain,
                                    i, sfb, win);
                            i++;
                        }
                    }
                }
            } else {//short_block

                int sfb = 0;
                int next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
                int win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                        Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];

                for (int i = 0; i < count1[channel * 2 + granule]; /* i++ done below! */) {

                    if (i == next_sfb) {
                        sfb++;
                        next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
                        win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                                Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];
                    }

                    for (int win = 0; win < 3; win++)
                        for (int j = 0; j < win_len; j++) {
                            requantize_short_III(granule, channel, mp3Data.mp3SideInfo.scalefac_scale,
                                    mp3Data.mp3SideInfo.subblock_gain,
                                    mp3Data.mp3SideInfo.global_gain,i, sfb, win);
                            i++;
                        }
                }
            }
        } else {
            int sfb = 0;
            int next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                    [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb + 1];
            for (int i = 0; i < count1[channel * 2 + granule]; i++) {
                if (i == next_sfb) {
                    sfb++;
                    next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                            [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb + 1];
                }
                requantize_long_III(granule, channel, mp3Data.mp3SideInfo.scalefac_scale,
                        mp3Data.mp3SideInfo.preflag, mp3Data.mp3SideInfo.global_gain,i, sfb);
            }
        }
    }
    private static void reorder(Mp3Data mp3Data,int channel , int granule){
        outer:

        if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2)) { //short_block

            float[] re = new float[576];

            int i = 0;
            int sfb = 0;
            int next_sfb;
            int win_len;

            if (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] != 0) {
                sfb = 3;
                i = 36;
            }
            next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                    [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
            win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                    [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                    Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                            [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];

            for (; i < 576; ) {

                if (i == next_sfb) {    /* Yes */

                    for (int j = 0; j < 3 * win_len; j++) {
                        tempDecodedData[channel * 2 * 576 + granule * 576 + 3 *
                                (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb]) + j] =
                                re[j];
                    }

                    if (i >= count1[channel * 2 + granule]) {
                        /* Done */
                        break outer;
                    }
                    sfb++;
                    next_sfb = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                            [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] * 3;
                    win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                            [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb + 1] -
                            Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                    [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb];
                }

                for (int win = 0; win < 3; win++) {
                    for (int j = 0; j < win_len; j++) {
                        re[j * 3 + win] = tempDecodedData[channel * 2 * 576 + granule * 576 + i];
                        i++;
                    }
                }
            }

            for (int j = 0; j < 3 * win_len; j++)
                tempDecodedData[channel * 2 * 576 + granule * 576 + 3 * (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + 12]) + j] = re[j];
        }


    }
    private static void stereo(Mp3Data mp3Data ,int granule){

        if ((mp3Data.getMp3Header().getMode() == 1) && (mp3Data.getMp3Header().getModeExtesion() != 0))
            if ((mp3Data.getMp3Header().getModeExtesion() & 0x2) != 0) {

                int max_pos;
                if (count1[granule] > count1[2 + granule])
                    max_pos = count1[granule];
                else
                    max_pos = count1[2 + granule];


                for (int i = 0; i < max_pos; i++) {
                    float left = (tempDecodedData[granule * 576 + i] + tempDecodedData[2 * 576 + granule * 576 + i])
                            * (Mp3StandardData.INV_SQUARE_2);
                    float right = (tempDecodedData[granule * 576 + i] - tempDecodedData[2 * 576 + granule * 576 + i])
                            * (Mp3StandardData.INV_SQUARE_2);
                    tempDecodedData[granule * 576 + i] = left;
                    tempDecodedData[2 * 576 + granule * 576 + i] = right;
                }
            }

        if ((mp3Data.getMp3Header().getModeExtesion() & 0x1) != 0) {

            if ((mp3Data.mp3SideInfo.win_switch_flag[granule] == 1) &&
                    (mp3Data.mp3SideInfo.block_type[granule] == 2)) { //short_block

                if (mp3Data.mp3SideInfo.mixed_block_flag[granule] != 0) {

                    for (int sfb = 0; sfb < 8; sfb++)

                        if (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb] >= count1[2 + granule])
                            stereo_long_III(granule, sfb, mp3Data.getMp3Header().getFrequancy());

                    for (int sfb = 3; sfb < 12; sfb++)
                        if (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb]
                                * 3 >= count1[2 + granule])
                            stereo_short_III(granule, sfb, mp3Data.getMp3Header().getFrequancy());


                } else

                    for (int sfb = 0; sfb < 12; sfb++)

                        if (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                                [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + 23 + sfb] * 3
                                >= count1[2 + granule])

                            stereo_short_III(granule, sfb, mp3Data.getMp3Header().getFrequancy());

            } else

                for (int sfb = 0; sfb < 21; sfb++)
                    if (Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                            [mp3Data.getMp3Header().getFrequancy() * (23 + 14) + sfb] >= count1[2 + granule])

                        stereo_long_III(granule, sfb, mp3Data.getMp3Header().getFrequancy());

        }
    }

    private static void antiAliasing(Mp3Data mp3Data ,int channel ,int granule){

        if (!((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2) &&
                (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule]) == 0)) {

            int sblim;

            if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                    (mp3Data.mp3SideInfo.block_type[channel * 2 + granule] == 2) &&
                    (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule]) == 1)
                sblim = 2;
            else
                sblim = 32;

            for (int sb = 1; sb < sblim; sb++)
                for (int i = 0; i < 8; i++) {
                    int li = 18 * sb - 1 - i;
                    int ui = 18 * sb + i;
                    float lb = tempDecodedData[channel * 2 * 576 + granule * 576 + li] * Mp3StandardData.CS_ALIASING_LAYER_III
                            [i] - tempDecodedData[channel * 2 * 576 + granule * 576 + ui] * Mp3StandardData.CA_ALIASING_LAYER_III[i];
                    float ub = tempDecodedData[channel * 2 * 576 + granule * 576 + ui] * Mp3StandardData.CS_ALIASING_LAYER_III
                            [i] + tempDecodedData[channel * 2 * 576 + granule * 576 + li] * Mp3StandardData.CA_ALIASING_LAYER_III[i];
                    tempDecodedData[channel * 2 * 576 + granule * 576 + li] = lb;
                    tempDecodedData[channel * 2 * 576 + granule * 576 + ui] = ub;
                }
        }
    }

    private static void hybirdSynthesis(Mp3Data mp3Data,int channel , int granule){

        int bt;
        //32 subbands
        for (int sb = 0; sb < 32; sb++) {

            if ((mp3Data.mp3SideInfo.win_switch_flag[channel * 2 + granule] == 1) &&
                    (mp3Data.mp3SideInfo.mixed_block_flag[channel * 2 + granule] == 1) && (sb < 2))
                bt = 0;
            else
                bt = mp3Data.mp3SideInfo.block_type[channel * 2 + granule];

            float[] rawout = new float[36];
            //IMDCT
            int offset = channel * 2 * 576 + granule * 576 + sb * 18;

            if (bt == 2)
                for (int j = 0; j < 3; j++)
                    for (int p = 0; p < 12; p++) {
                        float sum = 0;
                        for (int m = 0; m < 6; m++) {
                            sum += tempDecodedData[offset + j+3*m] * Mp3StandardData.COS_12_LAYER_III[m * 12 + p];
                        }
                        rawout[6*j+p+6] += sum * Mp3StandardData.IMDCT_WINDOW_LAYER_III[bt * 36 + p];
                    }
            else
                for (int p = 0; p < 36; p++) {
                    float sum = 0;
                    for (int m = 0; m < 18; m++) {
                        sum += tempDecodedData[offset + m] * Mp3StandardData.COS_36_LAYER_III[m * 36 + p];
                    }
                    rawout[p] = sum * Mp3StandardData.IMDCT_WINDOW_LAYER_III[bt * 36 + p];
                }

            for (int i = 0; i < 18; i++) {

                tempDecodedData[channel * 2 * 576 + granule * 576 + sb * 18 + i]
                        = rawout[i] + mp3Data.getStore()[channel * 32 * 18 + sb * 18 + i];
                mp3Data.getStore()[channel * 32 * 18 + sb * 18 + i] = rawout[i + 18];
            }
        }

    }

    private static void frequencyInversion(int channel , int granule){
        for (int sb = 1; sb < 32; sb += 2) {
            for (int i = 1; i < 18; i += 2) {
                tempDecodedData[channel * 2 * 576 + granule * 576 + sb * 18 + i]
                        = -tempDecodedData[channel * 2 * 576 + granule * 576 + sb * 18 + i];
            }
        }
    }

    private static void polyphaseSubbandSynthesis(Mp3Data mp3Data,int channel , int granule){

        float[] u = new float[512];
        float[] subband = new float[32];

        for (int ss = 0; ss < 18; ss++) {
            for (int i = 1023; i > 63; i--)
                mp3Data.getVector()[channel * 1024 + i] = mp3Data.getVector()[channel * 1024 + i - 64];

            for (int i = 0; i < 32; i++)
                subband[i] = tempDecodedData[channel * 2 * 576 + granule * 576 + i * 18 + ss];

            for (int i = 0; i < 64; i++) {
                float sum = 0.0f;
                for (int j = 0; j < 32; j++)
                    sum += Mp3StandardData.SYNTH_WINDOW_TABLE_LAYER_III[i * 32 + j] * subband[j];

                mp3Data.getVector()[channel * 1024 + i] = sum;
            }

            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 32; j++) {
                    u[i * 64 + j] = mp3Data.getVector()[channel * 1024 + i * 128 + j];
                    u[i * 64 + j + 32] = mp3Data.getVector()[channel * 1024 + i * 128 + j + 96];
                }

            for (int i = 0; i < 512; i++)
                u[i] *= Mp3StandardData.DI_COEFFICIENTS[i];

            for (int i = 0; i < 32; i++) {
                float sum = 0.0f;
                for (int j = 0; j < 16; j++)
                    sum += u[j * 32 + i];

                int samp = (int) (sum * 32767.0f);
                if (samp > 32767)
                    samp = 32767;
                else if (samp < -32767)
                    samp = -32767;

                samp &= 0xffff;

                if (mp3Data.getStereo() > 1) {
                    mp3Data.getSamplesBuffer()[granule * 18 * 32 * 2 * 2 + ss * 32 * 2 * 2 + i * 2 * 2 + channel * 2]
                            = (byte) samp;
                    mp3Data.getSamplesBuffer()[granule * 18 * 32 * 2 * 2 + ss * 32 * 2 * 2 + i * 2 * 2 + channel * 2 + 1]
                            = (byte) (samp >>> 8);
                } else {
                    mp3Data.getSamplesBuffer()[granule * 18 * 32 * 2 + ss * 32 * 2 + i * 2] = (byte) samp;
                    mp3Data.getSamplesBuffer()[granule * 18 * 32 * 2 + ss * 32 * 2 + i * 2 + 1] = (byte) (samp >>> 8);
                }
            }
        }
    }

    private static void requantize_short_III(int granule, int channel, int[] scalefac_scale, int[] subblock_gain,
                                             int[] global_gain, int inPos, int sfb, int win) {

        float sf_mult = scalefac_scale[channel * 2 + granule] != 0 ? 1.0f : 0.5f;
        float tmp1;

        if (sfb < 12)
            tmp1 = (float) Math.pow(2, -(sf_mult * scalefac_s[channel * 2 * 12 * 3 + granule * 12 * 3 + sfb * 3 + win]));
        else
            tmp1 = 1.0f;

        float tmp2 = (float) Math.pow(2, 0.25f * (global_gain[channel * 2 + granule] - 210.0f -
                8.0f * (subblock_gain[channel * 2 * 3 + granule * 3 + win])));

        float tmp3;
        if (tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] < 0.0) {
            tmp3 = -Mp3StandardData.POWTAB_LAYER_III[(int) -tempDecodedData[channel * 2 * 576 + granule * 576 + inPos]];
        } else {
            tmp3 = Mp3StandardData.POWTAB_LAYER_III[(int) tempDecodedData[channel * 2 * 576 + granule * 576 + inPos]];
        }

        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] = tmp1 * tmp2 * tmp3;
    }

    private static void requantize_long_III(int granule, int channel, int[] scalefac_scale, int[] preflag,
                                            int[] global_gain,int inPos, int sfb) {

        float sf_mult = scalefac_scale[channel * 2 + granule] != 0 ? 1.0f : 0.5f;
        float tmp1;

        if (sfb < 21) {
            float pf_x_pt = preflag[channel * 2 + granule] * Mp3StandardData.REQUANTIZE_LONG_PRETAB_LAYER_III[sfb];

            tmp1 = (float) Math.pow(2, -(sf_mult * (scalefac_l[channel * 2 * 21 + granule * 21 + sfb] + pf_x_pt)));
        } else
            tmp1 = 1.0f;

        float tmp2 = (float) Math.pow(2, 0.25f * (global_gain[channel * 2 + granule] - 210));
        float tmp3;
        if (tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] < 0.0)
            tmp3 = -Mp3StandardData.POWTAB_LAYER_III[(int) -tempDecodedData[channel * 2 * 576 + granule * 576 + inPos]];
        else
            tmp3 = Mp3StandardData.POWTAB_LAYER_III[(int) tempDecodedData[channel * 2 * 576 + granule * 576 + inPos]];

        tempDecodedData[channel * 2 * 576 + granule * 576 + inPos] = tmp1 * tmp2 * tmp3;
    }

    private static void stereo_short_III(int granule, int sfb, int samplingFrequency) {
        int win_len = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III[samplingFrequency * (23 + 14) + 23 + sfb + 1]
                - Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III[samplingFrequency * (23 + 14) + 23 + sfb];

        for (int win = 0; win < 3; win++) {
            int is_pos;

            if ((is_pos = scalefac_s[granule * 12 * 3 + sfb * 3 + win]) != 7) {
                int sfb_start = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III
                        [samplingFrequency * (23 + 14) + 23 + sfb] * 3 + win_len * win;
                int sfb_stop = sfb_start + win_len;
                float is_ratio_l;
                float is_ratio_r;

                if (is_pos == 6) {
                    is_ratio_l = 1.0f;
                    is_ratio_r = 0.0f;
                } else {
                    is_ratio_l = Mp3StandardData.IS_RATIOS_LAYER_III[is_pos]
                            / (1.0f + Mp3StandardData.IS_RATIOS_LAYER_III[is_pos]);
                    is_ratio_r = 1.0f / (1.0f + Mp3StandardData.IS_RATIOS_LAYER_III[is_pos]);
                }

                for (int i = sfb_start; i < sfb_stop; i++) {
                    tempDecodedData[granule * 576 + i] *= is_ratio_l;
                    tempDecodedData[2 * 576 + granule * 576 + i] *= is_ratio_r;
                }
            }
        }
    }

    private static void stereo_long_III(int granule, int sfb, int samplingFrequency) {
        int is_pos;
        if ((is_pos = scalefac_l[granule * 21 + sfb]) != 7) {

            int sfb_start = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III[samplingFrequency * (23 + 14) + sfb];
            int sfb_stop = Mp3StandardData.SCALEFACTOR_BAND_INDICES_LAYER_III[samplingFrequency * (23 + 14) + sfb + 1];
            float is_ratio_l;
            float is_ratio_r;

            if (is_pos == 6) {
                is_ratio_l = 1.0f;
                is_ratio_r = 0.0f;
            } else {
                is_ratio_l = Mp3StandardData.IS_RATIOS_LAYER_III[is_pos] / (1.0f + Mp3StandardData.IS_RATIOS_LAYER_III[is_pos]);
                is_ratio_r = 1.0f / (1.0f + Mp3StandardData.IS_RATIOS_LAYER_III[is_pos]);
            }

            for (int i = sfb_start; i < sfb_stop; i++) {
                tempDecodedData[granule * 576 + i] *= is_ratio_l;
                tempDecodedData[2 * 576 + granule * 576 + i] *= is_ratio_r;
            }
        }
    }


    private static void readInto(Mp3Data.Buffer buffer, byte[] array, int offset, int length) throws IOException {
        if (buffer.current != 0)

            throw new IllegalStateException("buffer current tempDecodedData " + buffer.current);

        if (length == 0)
            return;

        if (buffer.lastByte == -1)
            throw new EOFException("Unexpected EOF reached in MPEG data");

        array[offset] = (byte) buffer.lastByte;
        int read = 1;
        while (read < length)
            read += buffer.in.read(array, offset + read, length - read);

        buffer.lastByte = buffer.in.read();
    }

    private static int read(Mp3Data.Buffer buffer, int numBits) throws IOException {
        int number = 0;
        while (numBits > 0) {
            int advance = Math.min(numBits, 8 - buffer.current);
            numBits -= advance;
            buffer.current += advance;
            if (numBits != 0 && buffer.lastByte == -1)
                throw new EOFException("Unexpected EOF reached in MPEG data");

            number |= ((buffer.lastByte >>> (8 - buffer.current)) & (0xFF >>> (8 - advance))) << numBits;
            if (buffer.current == 8) {
                buffer.current = 0;
                buffer.lastByte = buffer.in.read();
            }
        }
        return number;
    }
    private static int read(Mp3Data.MainDataReader reader, int bits) {
        int number = 0;
        while (bits > 0) {
            int advance = Math.min(bits, 8 - reader.current);
            bits -= advance;
            reader.current += advance;
            number |= ((reader.mainData[reader.index] >>> (8 - reader.current)) & (0xFF >>> (8 - advance))) << bits;
            if (reader.current == 8) {
                reader.current = 0;
                reader.index++;
            }
        }
        return number;
    }

}
