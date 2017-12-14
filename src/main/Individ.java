/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import static main.MainFrame.clienti;

/**
 *
 * @author yo5bd
 */
public class Individ implements Comparable {
    private Double distanta_totala;
    private Double fitness; // media procentul de incarcare impartit la nr de camioane
    private Integer nr_camioane;
    
    public Integer[] cromozom; //indexul e obiectul, valoarea e camionul pe care e incarcat

    ArrayList<Camion> camioane = new ArrayList();
    public Individ(int nr, int cam) {
        cromozom = new Integer[nr];
        nr_camioane = cam;
        int capacitate;
        for(int i=0;i<nr_camioane;i++) {
            if(i<=10) {
                capacitate = 101;
            } else if(i<=25) {
                capacitate = 91;
            } else {
                capacitate = 81;
            }
            camioane.add(new Camion(capacitate));
        }
        optimize_loads();
    }
    /**
     * Calculeaza fitnes-ul individului curent.
     * @return 
     */
    public double fitnes() {
        optimize_loads();
        Camion c;
        fitness = 0.0;
        for(int i=0;i<cromozom.length;i++) {
            if(cromozom[i]==-1) continue;
            c = camioane.get(cromozom[i]); //luat camionul cu nr gasit la indexul i
            c.pachete.add(i); //adaugam produsul in camion
        }
        for (Camion camion : camioane) {
            fitness += camion.calc(); //distanta totala parcursa de toate camioanele
        } 
        return fitness;
    }
    /**
     * Elimina pachetele ce nu incap intr-un camion anume si le incarca in alt camion mai gol.
     */
    void optimize_loads() {
        //golim camioanele suprapline
        //System.out.println("Golim camioanele suprapline...");
        for(int i=0;i<camioane.size();i++) {
            Camion c = camioane.get(i);
            while(c.ocupat > 100) {
                int obiect = c.pachete.remove(c.pachete.size()-1); //pop last element
                cromozom[obiect] = -1; //il marcam si in cromozom ca fiind nefolosit
                c.calc();
            }
            camioane.set(i,c);
        }
        //System.out.println("Punem pachetele in alt camion care nu e plin...");
        //punem pachetele in plus in alt camion care nu e plin
        for(int i=0;i<cromozom.length;i++) {
            if(cromozom[i]==null) continue;
            if(cromozom[i] == -1) { //clientul nu e incarcat
                camioane: for(int j=0;j<camioane.size();j++) {
                    Camion c = camioane.get(j);
                    if((c.capacitate - c.ocupat)>=clienti.get(i).volum) {
                        cromozom[i] = j;
                        c.pachete.add(i);
                        c.calc();
                        camioane.set(j,c);
                        break camioane; //iesi din bucla for
                    }
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        Individ o = (Individ)obj;
        return (this.fitness == o.fitness); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public int compareTo(Object o) {
        Individ c = (Individ) o;
        return c.fitness.compareTo(this.fitness);
    }

    @Override
    public String toString() {
        System.out.println("Individ{" + "distanta_totala=" + distanta_totala + ", fitness=" + fitness + '}');
        for(Camion c:camioane) System.out.println(c);
        return "";
    }

    Object getFitnes() {
        return fitness;
    }
    
    
}
