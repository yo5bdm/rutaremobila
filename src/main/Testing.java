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
        System.out.println("System testing:");
        //CamionTest();
        IndividTest();
        //HashTest();
    }
    
    private int CamionTest() {
        Individ n=new Individ(clienti.size(),1,false);
        
        System.out.println("Adaug 10 pachete...");
        for(int i=0;i<clienti.size();i++) n.cromozom[i]=-1;
        for(int i=0;i<10;i++) {
            n.cromozom[10*i]=0;
        }
        n.calculeaza(false);
        //m.setBest(n,-20);      
        return 0;
    }
    
    private void IndividTest() {
        Individ n = new Individ(clienti.size(),48,true);
        System.out.println("Fitnes total"+n.calculeaza(true)); 
        for(Camion c:n.camioane) System.out.println(c);
        System.out.println("Neincarcabile = "+n.neincarcabile());
        //m.setBest(n,-10);
    }

    private void HashTest() {
        Individ n = new Individ(clienti.size(),48,true);
        Individ s = new Individ(clienti.size(),48,true);
        System.out.println("n "+n.hashCode());
        System.out.println("s "+s.hashCode());
    }
    
    
    
}
