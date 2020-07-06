/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.mediaplayer.Data.Frame.JFrame;
/**
 *
 * @author DELL3567
 */
public class HuffmanTable {
    public int [] offsets=new int[17];
    public  int [] symbols=new int[162];
    public int [] codes =new int[162] ;
    public boolean set = false;

    public int[] getOffsets() {
        return offsets;
    }

    public void setOffsets(int[] offsets) {
        this.offsets = offsets;
    }

    public int[] getSymbols() {
        return symbols;
    }

    public void setSymbols(int[] symbols) {
        this.symbols = symbols;
    }

    public int[] getCodes() {
        return codes;
    }

    public void setCodes(int[] codes) {
        this.codes = codes;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }

    public HuffmanTable() {
    }

}
