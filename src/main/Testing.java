/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;
import static main.MainFrame.*;

/**
 *
 * @author yo5bdm
 */
public class Testing {
    public Camion c = new Camion(110);
    private Random r = new Random();
    public void run() {
        CamionTest();
        IndividTest();
    }
    
    private int CamionTest() {
        System.out.println("System testing:");
        System.out.println("Adaug 10 pachete...");
        for(int i=0;i<10;i++) c.pachete.add(3*i);
        c.calc();
        //if(c.ok==true) 
        System.out.println("ok="+c.ok+" "+c.opriri+" "+c.pachete.size());
        System.out.println("Ocupat "+c.ocupat+" = "+(c.ocupat/c.capacitate));
        System.out.println("Distanta = "+c.distanta);
        System.out.println("Pachetele: ");
        for(Integer i:c.pachete) {
            System.out.println(i+" GPS="+clienti.get(i).latitudine+","+clienti.get(i).longitudine);
        }
        return 0;
    }
    
    private void IndividTest() {
        Individ n = new Individ(clienti.size(),48);
        for(int j=0;j<clienti.size();j++) {
            n.cromozom[j] = r.nextInt(47);
        }
        System.out.println("Fitnes total"+n.fitnes()); 
        for(Camion c:n.camioane) System.out.println(c);
        System.out.println("Neincarcabile = "+n.neincarcabile());
    }
    
    
    
}
