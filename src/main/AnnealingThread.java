/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;

/**
 *
 * @author yo5bdm
 */
public class AnnealingThread extends Thread {
    public Individ lucru;
    private int nrClienti;
    private final Random R = new Random();
    private boolean merge = true;
    
    public AnnealingThread(Individ n, int nrClienti){
        lucru = n;
        this.nrClienti = nrClienti;
        start();
    }
    
    public Individ opreste() {
        merge = false;
        return lucru;
    }
    
    @Override
    public void run() {
        System.out.println("Start Ann Thread...");
        Individ nou;
        int pos1, pos2; 
        double pastEn, solEn;
        int pasi=0;
        int i=0;
        int dx=1;
        boolean random = false;
        while(merge) {
            if(random == false && i>=Client.nrClienti()-dx) {
                i=0;
                pasi++;
                if(pasi > 5) {
                    dx++;
                    if(dx>10) {
                        random = true;
                    }
                }
            }
            nou = new Individ(lucru); //copiem individul curent cel mai bun

            //--- mutatia
            if(random) {
                pos1 = R.nextInt(nrClienti - 1);
                pos2 = R.nextInt(nrClienti - 1);
                nou.swap(pos1,pos2);
            } else{
                nou.swap(i,i+1);
            }
            //---
            nou.calculeaza(true);
            pastEn = lucru.getFitness();
            solEn = nou.getFitness();
            if (solEn < pastEn-1 && nou.ok()==true) {
                lucru = nou;
                pasi=0;
            }
            if(!random) i++;
        }
    }
    
}
