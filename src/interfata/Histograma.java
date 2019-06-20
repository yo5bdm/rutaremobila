/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfata;

/**
 *
 * @author yo5bd
 */
public class Histograma {
    private int nrBare = 30;
    private int[] bare;
    private int maxProc=0;
    private double min;
    private double max;
    private double dx;
    
    Histograma(double min, double max) {
        this.min = min;
        this.max = max;
        dx = max - min;
        bare = new int[nrBare];
    }
    
    public void adauga(double val) {
        double proc = (val - min) / dx;
        int pos = ((int) (proc * nrBare))%(nrBare);
        try {
            bare[pos]++;
        } catch(ArrayIndexOutOfBoundsException e) {
            
        }
    }
    
    public double citeste(int pos) {
        if(maxProc == 0) 
            maxProc = getMax();
//        System.out.println(" ");
//        for(int i=0;i<nrBare;i++){
//            System.out.print(bare[i]+" ");
//        }
        if(maxProc != 0) return ((double)bare[pos])/((double)maxProc);
        else return 0.0;
    }
    
    public int getNrBare() {return nrBare;}
    public int getMax() {
        int max = 0;
        for(int i=0;i<nrBare;i++)
            if(bare[i]>max)
                max = bare[i];
        return max;
    }
}
