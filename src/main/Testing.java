/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;
import static main.MainFrame.*;

/**
 * Clasa de testare a functionalitatilor.
 * @author yo5bdm
 */
public class Testing {
    public Camion c = new Camion(110);
    private Random r = new Random();
    public void run() {
        System.out.println("System testing:");
        CamionTest(true);
        //IndividTest(false);
        //HashTest(false);
    }
    
    private void CamionTest(boolean executa) {
        if(executa = false) return;
        Individ n=new Individ(clienti.size(),0,false);
        Camion c1, c2;
        c1 = new Camion(100);
        c2 = new Camion(100);
        System.out.println("Adaug 10 pachete...");
        for(int i=0;i<10;i++) {
            c1.add(i*50+i);
            c2.add(i*50+i);
        }
        System.out.println(c1);
        System.out.println(c2);
        for(int i=0;i<10000;i++) {  //warmup
            c1.calculeazaDistanta();
            c2.calculeazaDistanta2();
        }
        long start = System.nanoTime(); //time benchmark
        for(int i=0;i<10000;i++) c1.calculeazaDistanta();
        System.out.println("Greedy a terminat in "+((System.nanoTime() - start)/1000000)+" ms");
        start = System.nanoTime();
        for(int i=0;i<10000;i++) c2.calculeazaDistanta2();
        System.out.println("Metoda 2 a terminat in "+((System.nanoTime() - start)/1000000)+" ms");
        n.camioane.add(c1);
        n.camioane.add(c2);
        n.setFitness(130.0);
        m.setBest(n,-20,-30); 
    }
    
    private void IndividTest(boolean executa) {
        if(executa = false) return;
        Individ n = new Individ(clienti.size(),48,true);
        System.out.println("Fitnes total"+n.calculeaza(true)); 
        for(Camion c:n.camioane) System.out.println(c);
        System.out.println("Neincarcabile = "+n.neincarcabile());
        //m.setBest(n,-10);
    }

    private void HashTest(boolean executa) {
        if(executa = false) return;
        Individ n = new Individ(clienti.size(),48,true);
        Individ s = new Individ(clienti.size(),48,true);
        System.out.println("n "+n.hashCode());
        System.out.println("s "+s.hashCode());
    }
}
