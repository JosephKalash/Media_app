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
public class QuantizationTable {
    public int [] table=new int [64] ;

    public QuantizationTable() {
    }
    
    public int[] getTable() {
        return table;
    }

    public void setTable(int[] table) {
        this.table = table;
    }

    public boolean isSet() {
        return set;
    }

    public void setSet(boolean set) {
        this.set = set;
    }
    public boolean set = false;
    
    
}
