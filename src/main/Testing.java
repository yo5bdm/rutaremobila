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

    /**
     * Teste.
     */
    public Camion c = new Camion(110);
    private final Random r = new Random();
    /**
     * Rularea testelor.
     */
    public void run() {
        System.out.println("System testing:");
        //CamionTest2(true);
        IndividTest(true);
    }
    /**
     * Test camion.
     * @param executa true daca se executa, false daca nu.
     */
    private void CamionTest(boolean executa) {
        if(executa = false) return;
        Individ n=new Individ(Client.clienti.size(),0,100,false);
        Camion c1, c2, c3;
        //c1 = new Camion(100);
        c2 = new Camion(100);
        c3 = new Camion(100);
        System.out.println("Adaug 10 pachete...");
        for(int i=0;i<10;i++) {
            //c1.add(i*50+i);
            c2.add(i*50+i);
            c3.add(i*50+i);
        }
        //System.out.println(c1);
        System.out.println(c2);
        for(int i=0;i<10000;i++) {  //warmup
            //c1.calculeazaDistanta();
            c2.calculeazaDistanta2();
            c3.calculeazaDistanta3();
        }
        long start = System.nanoTime(); //time benchmark
        //for(int i=0;i<10000;i++) c1.calculeazaDistanta();
        System.out.println("Greedy a terminat in "+((System.nanoTime() - start)/1000000)+" ms");
        start = System.nanoTime();
        for(int i=0;i<10000;i++) c2.calculeazaDistanta2();
        System.out.println("Metoda 2 a terminat in "+((System.nanoTime() - start)/1000000)+" ms");
        //n.camioane.add(c1);
        n.camioane.add(c2);
        n.camioane.add(c3);
        n.calculeaza(true);
        m.setBest(n,0,0,"TESTING"); 
    }
    /**
     * Testul individului.
     * @param executa true daca se executa.
     */
    private void IndividTest(boolean executa) {
        if(executa == false) return;
        Individ n = new Individ(Client.clienti.size(),48,100,true);
        System.out.println("Fitnes total"+n.calculeaza(true));
        System.out.println("Neincarcabile = "+n.neincarcabile());
        m.setBest(n,0,0,"TESTING");
    }
}
