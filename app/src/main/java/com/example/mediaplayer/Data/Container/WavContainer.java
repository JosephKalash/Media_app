package com.example.mediaplayer.Data.Container;

import com.example.mediaplayer.Data.Frame.Frame;

public class WavContainer extends Container  {
    Frame[] AudioStream;
    Long NumberOfFrames;
    Long NumberOfChannels;
    Long SampleRate;
    Long BlockAlign;
    Long ValidBits;
    Long CompressionCode;
    Long ChunkID;
    Long ChunkSize;
    Long BytePerSample;


    public WavContainer(Frame[] audioStream, Long numberOfFrames, Long numberOfChannels, Long sampleRate, Long blockAlign, Long validBits, Long compressionCode, Long chunkID, Long chunkSize, Long bytePerSample) {
        AudioStream = audioStream;
        NumberOfFrames = numberOfFrames;
        NumberOfChannels = numberOfChannels;
        SampleRate = sampleRate;
        BlockAlign = blockAlign;
        ValidBits = validBits;
        CompressionCode = compressionCode;
        ChunkID = chunkID;
        ChunkSize = chunkSize;
        BytePerSample = bytePerSample;
    }

    public Frame[] getAudioStream() {
        return AudioStream;
    }

    public void setAudioStream(Frame[] audioStream) {
        AudioStream = audioStream;
    }

    public Long getNumberOfFrames() {
        return NumberOfFrames;
    }

    public void setNumberOfFrames(Long numberOfFrames) {
        NumberOfFrames = numberOfFrames;
    }

    public Long getNumberOfChannels() {
        return NumberOfChannels;
    }

    public void setNumberOfChannels(Long numberOfChannels) {
        NumberOfChannels = numberOfChannels;
    }

    public Long getSampleRate() {
        return SampleRate;
    }

    public void setSampleRate(Long sampleRate) {
        SampleRate = sampleRate;
    }

    public Long getBlockAlign() {
        return BlockAlign;
    }

    public void setBlockAlign(Long blockAlign) {
        BlockAlign = blockAlign;
    }

    public Long getValidBits() {
        return ValidBits;
    }

    public void setValidBits(Long validBits) {
        ValidBits = validBits;
    }

    public Long getCompressionCode() {
        return CompressionCode;
    }

    public void setCompressionCode(Long compressionCode) {
        CompressionCode = compressionCode;
    }

    public Long getChunkID() {
        return ChunkID;
    }

    public void setChunkID(Long chunkID) {
        ChunkID = chunkID;
    }

    public Long getChunkSize() {
        return ChunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        ChunkSize = chunkSize;
    }

    public Long getBytePerSample() {
        return BytePerSample;
    }

    public void setBytePerSample(Long bytePerSample) {
        BytePerSample = bytePerSample;
    }
}
