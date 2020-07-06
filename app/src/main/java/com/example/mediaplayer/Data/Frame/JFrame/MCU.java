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
public class MCU {
    public  int [] y=new int [64];
    public  int [] r=new int[64];
    public  int [] cb=new int[64];
    public  int [] g=new int [64];
    public  int [] cr=new int[64];
    public  int [] b=new int [64];

    public MCU() {
    }

    MCU(int []y,int []cb, int []cr)
    {
        this.y=y;
        this.cb=cb;
        this.cr=cr;

    }
    
    public int[] getY() {
        return y;
    }

    public void setY(int[] y) {
        this.y = y;
    }

    public int[] getR() {
        return r;
    }

    public void setR(int[] r) {
        this.r = r;
    }

    public int[] getCb() {
        return cb;
    }

    public void setCb(int[] cb) {
        this.cb = cb;
    }

    public int[] getG() {
        return g;
    }

    public void setG(int[] g) {
        this.g = g;
    }

    public int[] getCr() {
        return cr;
    }

    public void setCr(int[] cr) {
        this.cr = cr;
    }

    public int[] getB() {
        return b;
    }

    public void setB(int[] b) {
        this.b = b;
    }

    public int [] getComponent(int index)
    {
        switch(index){
            case 0:return y;
            case 1:return cb;
            case 2:return cr;
            default:return null;

        }
    }
    public int [] getRGBComponent(int index)

    {
        switch(index){
            case 0:return r;
            case 1:return g;
            case 2:return b;
            default:return null;

        }
    }

    void printYCbCR(){
                System.out.println("");

        System.out.println("Y");
                        System.out.println("");

        for(int i=0;i<64;i++){
                if(i%8==0)System.out.println();
            System.out.print(y[i]+" ");
        }
                System.out.println("");
                System.out.println("CB");
                System.out.println("");

        for(int i=0;i<64;i++){      
            if(i%8==0)System.out.println();

            System.out.print(cb[i]+" ");}   
        
        
        System.out.println("Cr");
                System.out.println("");

        for(int i=0;i<64;i++){
                            if(i%8==0)System.out.println();
            System.out.print(cr[i]+" ");
        }
    } 
    
}
