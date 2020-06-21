package com.example.mediaplayer.Data.Frame.mp3Frame;

public class SideInfoGr {
    private int part2_3length;
    private int bigValue;
    private int golbalGain;
    private int windowSwitchingFlag;
    private int blockType;
    private int mixedBlockFlag;
    private int tableSelect;
    private int blockGain;
    private int region0_Count;
    private int region1_Count;
    private int preFlag;
    private int scaleFactorSelect;
    private int count1_tableSelect;

    public SideInfoGr(int part2_3length, int bigValue, int golbalGain, int windowSwitchingFlag, int blockType, int mixedBlockFlag, int tableSelect, int blockGain,
                      int region0_Count, int region1_Count, int preFlag, int scaleFactorSelect,
                      int count1_tableSelect) {
        this.part2_3length = part2_3length;
        this.bigValue = bigValue;
        this.golbalGain = golbalGain;
        this.windowSwitchingFlag = windowSwitchingFlag;
        this.blockType = blockType;
        this.mixedBlockFlag = mixedBlockFlag;
        this.tableSelect = tableSelect;
        this.blockGain = blockGain;
        this.region0_Count = region0_Count;
        this.region1_Count = region1_Count;
        this.preFlag = preFlag;
        this.scaleFactorSelect = scaleFactorSelect;
        this.count1_tableSelect = count1_tableSelect;
    }
















    public int getPart2_3length() {
        return part2_3length;
    }

    public void setPart2_3length(int part2_3length) {
        this.part2_3length = part2_3length;
    }

    public int getBigValue() {
        return bigValue;
    }

    public void setBigValue(int bigValue) {
        this.bigValue = bigValue;
    }

    public int getGolbalGain() {
        return golbalGain;
    }

    public void setGolbalGain(int golbalGain) {
        this.golbalGain = golbalGain;
    }

    public int getWindowSwitchingFlag() {
        return windowSwitchingFlag;
    }

    public void setWindowSwitchingFlag(int windowSwitchingFlag) {
        this.windowSwitchingFlag = windowSwitchingFlag;
    }

    public int getBlockType() {
        return blockType;
    }

    public void setBlockType(int blockType) {
        this.blockType = blockType;
    }

    public int getMixedBlockFlag() {
        return mixedBlockFlag;
    }

    public void setMixedBlockFlag(int mixedBlockFlag) {
        this.mixedBlockFlag = mixedBlockFlag;
    }

    public int getTableSelect() {
        return tableSelect;
    }

    public void setTableSelect(int tableSelect) {
        this.tableSelect = tableSelect;
    }

    public int getBlockGain() {
        return blockGain;
    }

    public void setBlockGain(int blockGain) {
        this.blockGain = blockGain;
    }

    public int getRegion0_Count() {
        return region0_Count;
    }

    public void setRegion0_Count(int region0_Count) {
        this.region0_Count = region0_Count;
    }

    public int getRegion1_Count() {
        return region1_Count;
    }

    public void setRegion1_Count(int region1_Count) {
        this.region1_Count = region1_Count;
    }

    public int getFlag() {
        return preFlag;
    }

    public void setFlag(int flag) {
        this.preFlag = flag;
    }

    public int getScaleFactorSelect() {
        return scaleFactorSelect;
    }

    public void setScaleFactorSelect(int scaleFactorSelect) {
        this.scaleFactorSelect = scaleFactorSelect;
    }

    public int getCount1_tableSelect() {
        return count1_tableSelect;
    }

    public void setCount1_tableSelect(int count1_tableSelect) {
        this.count1_tableSelect = count1_tableSelect;
    }
}
