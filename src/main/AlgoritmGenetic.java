/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static main.MainFrame.*;

/**
 *
 * @author yo5bd
 */
public class AlgoritmGenetic extends Thread {
    
    private int nr_clienti;
    private final int nr_indivizi=1000;
    private final double probabilitate_mutatie = 0.03; //5%
    private int max_generatii;
    private final int nr_camioane=48;
    
    public static final Random r = new Random();
    public final ArrayList<Individ> populatie = new ArrayList();
    private final ArrayList<Individ> pop_temp = new ArrayList();
     
    
    public AlgoritmGenetic(int size,int viteza) {
        nr_clienti = size;
        populatie.clear();
        pop_temp.clear();
        switch(viteza) {
            case 0: //rapid
                max_generatii = 500;
            case 1: //mediu
                max_generatii = 2000;
            case 2: //lent
                max_generatii = 10000;
            default: // case 3: infinit
                max_generatii = Integer.MAX_VALUE;
        }
    }

    public void run() {
        double total;
        genereaza_pop_initiala(nr_indivizi,nr_camioane);//
        int x=0;
        for(Individ c:populatie) {
            c.fitnes();
        }
        Individ best_fit, best;
        Collections.sort(populatie);
        best_fit = populatie.get(0);
        for(int g=0;g<max_generatii;g++) {//
            m.setGen(g);
            recombinare();//
            mutatie();//
            for(Individ i:pop_temp) i.fitnes(); //calculam fitnessul pentru populatia temporara
            selectie();//
            total = fitnes_total();
            m.setFit(total);
            Collections.sort(populatie);
            best = populatie.get(0);
            if(best.fitnes()<best_fit.fitnes() && best.ok()==true) { //&& best.ok()==true
                System.out.println("Am gasit unul mai bun...");
                System.out.println(best);
                best_fit = best;
                m.setBest(best_fit,g);
            }
        }
        System.out.println(best_fit);
    }

    private void genereaza_pop_initiala(int nr_indivizi, int nr_camioane) {
        for(int i=0;i<nr_indivizi;i++) {            
            populatie.add(new Individ(nr_clienti,nr_camioane,true));
        }
    }

    private void recombinare() {
        pop_temp.clear();
        for(int k=0;k<nr_indivizi;k++){
            Individ p1 = populatie.get(r.nextInt(populatie.size()));
            Individ p2 = populatie.get(r.nextInt(populatie.size()));
            
            Individ a = new Individ(nr_clienti,nr_camioane,false); //false = nu generam cromozomul
            Individ b = new Individ(nr_clienti,nr_camioane,false);
            int pct_taiere = r.nextInt(nr_clienti-1);
            for(int i=0;i<nr_clienti;i++) {
                if(i<pct_taiere) {
                    a.cromozom[i] = p1.cromozom[i];
                    b.cromozom[i] = p2.cromozom[i];
                } else {
                    b.cromozom[i] = p1.cromozom[i];
                    a.cromozom[i] = p2.cromozom[i];
                }
            }
            pop_temp.add(a);
            pop_temp.add(b);
        }       
    }

    private void mutatie() {
        for(Individ c:pop_temp) { //pentru fiecare individ
            for(int i=0;i<c.cromozom.length;i++) { //se ia fiecare cromozom
                if(r.nextDouble()<probabilitate_mutatie) { //random < probabilitatea de mutatie
                    int pos2 = r.nextInt(c.cromozom.length-1); //se selecteaza random o alta pozitie din cromozom
                    while(pos2==i) {
                        pos2 = r.nextInt(c.cromozom.length-1);
                    }
                    //si se face swap
                    int temp = c.cromozom[i];
                    c.cromozom[i] = c.cromozom[pos2];
                    c.cromozom[pos2] = temp;
                }
            }
        }
    }

    private void selectie() {
        pop_temp.addAll(populatie); //includem si parintii?
        populatie.clear();
        Collections.sort(pop_temp);
        for(int i=0;i<this.nr_indivizi;i++) {
            //random gausian absolut intre 0 si nr_indivizi
            int rnd = (int)Math.abs((double)pop_temp.size()*r.nextGaussian())%pop_temp.size(); 
            //selecteaza individul dupa randomul de mai sus si adauga in pop temp
            populatie.add(pop_temp.get(rnd));
            pop_temp.remove(rnd);
        }    
    }

    private double fitnes_total() {
        //System.out.println("Calculez fitnes total");
        double tot=0.0;
        for(Individ i:populatie) {
            tot += i.fitnes();
        }
        return tot;
    }
    
}
