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
    private final int nr_indivizi=50;
    private final double probabilitate_mutatie = 0.05; //5%
    private final int max_generatii = 1000;
    private final int nr_camioane=48;
    
    private final Random r = new Random();
    public final ArrayList<Individ> populatie = new ArrayList();
    private final ArrayList<Individ> pop_temp = new ArrayList();
     
    
    public AlgoritmGenetic(int size) {
        nr_clienti = size;
        populatie.clear();
        pop_temp.clear();
    }

    public void run() {
        double total;
        System.out.println("Generez populatia initiala: "+nr_indivizi+" indivizi, "+nr_camioane+" camioane...");
        genereaza_pop_initiala(nr_indivizi,nr_camioane);//
        System.out.println("Optimizare incarcaturi...");
        int x=0;
        for(Individ c:populatie) {
            c.fitnes();
        }
        System.out.println("Calculam fitnesul total al generatiei initiale");
        System.out.println(""+fitnes_total());
        Individ best_fit, best;
        Collections.sort(populatie);
        best_fit = populatie.get(0);
        int best_gen=-1; //generatia celui mai bun
        for(int g=0;g<max_generatii;g++) {//
            m.setGen(g);
            System.out.println("Incep generatia "+g+"...");
            recombinare();//
            mutatie();//
            selectie();//
            total = fitnes_total();
            m.setFit(total);
            Collections.sort(populatie);
            best = populatie.get(0);
            if(best.fitnes()<best_fit.fitnes() && best.ok()==true) {
                best_fit = best;
                best_gen = g;
                m.setBest(best,g);
                System.out.println("Gasit mai bun in generatia "+g);
            }
        }
        System.out.println("Cel mai bun a fost gasit in generatia: "+best_gen);
        System.out.println(best_fit);
    }

    private void genereaza_pop_initiala(int nr_indivizi, int nr_camioane) {
        for(int i=0;i<nr_indivizi;i++) {
            Individ n = new Individ(nr_clienti,nr_camioane);
            for(int j=0;j<nr_clienti;j++) {
                n.cromozom[j] = r.nextInt(nr_camioane-1);
            }
            populatie.add(n);
        }
    }

    private void recombinare() {
        pop_temp.clear();
        for(int k=0;k<nr_indivizi/2;k++){
            int i1 = r.nextInt(populatie.size());
            int i2 = r.nextInt(populatie.size());
            Individ p1 = populatie.get(i1);
            Individ p2 = populatie.get(i2);
            
            Individ a = new Individ(nr_clienti,nr_camioane);
            Individ b = new Individ(nr_clienti,nr_camioane);
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
        //System.out.println("Mutatie...");
        for(Individ c:pop_temp) { //pentru fiecare individ
            for(int i=0;i<c.cromozom.length;i++) { //se ia fiecare cromozom
                if(r.nextDouble()<probabilitate_mutatie) { //random < probabilitatea de mutatie
                    int pos2 = r.nextInt(c.cromozom.length-1); //se selecteaza random o alta pozitie din cromozom
                    if(pos2!=i) { //si se face swap
                        int temp = c.cromozom[i];
                        c.cromozom[i] = c.cromozom[pos2];
                        c.cromozom[pos2] = temp;
                    }
                }
            }
            //c.optimize_loads(); //optimizam incarcarile
            c.fitnes(); //calculam fitnes
        }
    }

    private void selectie() {
        pop_temp.addAll(populatie);
        populatie.clear();
        //System.out.println("Selectie...");
//sorteaza descendent dupa fitnes_total
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
