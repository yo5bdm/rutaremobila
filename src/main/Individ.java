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
    }
    
    private void reset_camioane() {
        for(Camion c:camioane) c.reset();
        if(camioane.size()<nr_camioane) {
            int nr = nr_camioane-camioane.size();
            for(int i=0;i<nr;i++) {
                camioane.add(new Camion(81));
            }
        }
        nr_camioane = camioane.size();
    }
    
    /**
     * Calculeaza fitnes-ul individului curent.
     * @return 
     */
    public double fitnes() {
        int max=0;
        for(int i:cromozom) if(i>max) max=i; //gasim maxim
        nr_camioane = max+1;
        reset_camioane();
        for(int i=0;i<cromozom.length;i++) {
            if(cromozom[i]==-2) cromozom[i]=-1;
        }
        for(int i=0;i<cromozom.length;i++) {
            if(cromozom[i]==-1) continue; //cromozom[i]==-2
            camioane.get(cromozom[i]).add(i); //luat camionul cu nr gasit la indexul i si adaugam produsul in el
        }
        optimize_loads();
        fitness = 0.0;
        for (Camion camion : camioane) {
            fitness += camion.calc(); //distanta totala parcursa de toate camioanele
        } 
        //fiecare neincarcabil scade fitnesul total cu 10k
        for(int i=0;i<neincarcabile();i++){
            fitness += 9999.0;
        }
        return fitness;
    }
    
    private int neincarcate() {
        int ret=0;
        for(int i:cromozom) {
            if(i==-1) {
                ret++;
            }
        }
        return ret;
    }
    
    public int neincarcabile() {
        int ret=0;
        for(int i:cromozom) {
            if(i==-2) {
                ret++;
            }
        }
        return ret;
    }
    /**
     * Elimina pachetele ce nu incap intr-un camion anume si le incarca in alt camion mai gol.
     */
    private void optimize_loads() {
        //golim camioanele suprapline
        for(int i=0;i<camioane.size();i++) {
            Camion c = camioane.get(i);
            while(c.ok != true) {
                int obiect = c.pop(); //pop last element
                cromozom[obiect] = -1; //il marcam si in cromozom ca fiind nefolosit
            }
        }
        //punem pachetele in plus in alt camion care nu e plin
        while(neincarcate()!=0) {
            //prima data incercam sa incarcam pe un camion existent
            int incarcat=0;
            for(int i=0;i<cromozom.length;i++) {
                if(cromozom[i] == -1) { //clientul nu e incarcat
                    incarcat=0;
                    camioane: for(int j=0;j<camioane.size();j++) {
                        Camion c = camioane.get(j);
                        if(c.opriri>=15) continue;
                        if((c.capacitate - c.ocupat)>=clienti.get(i).volum) {
                            cromozom[i] = j;
                            c.add(i);
                            incarcat=1;
                            break camioane; //iesi din bucla for
                        }
                    }
                    //nu am reusit sa incarc produsul, volumul e mai mare decat camioanele disponibile
                    if(incarcat==0 && clienti.get(i).volum>80) {
                        cromozom[i]=-2; //il marcam ca atare
                    }
                }
            }
            //daca totusi nu reusim sa le incarcam toate, mai punem un camion in lista
            
            if(neincarcate()!=0) {
                Camion c = new Camion(81);
                c.calc();
                nr_camioane++;
                camioane.add(c);
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
        return this.fitness.compareTo(c.fitness);
    }

    @Override
    public String toString() {
        System.out.println("Individ{ neincarcabile: " +neincarcabile()+", neincarcate: " +neincarcate()+ ", nr camioane=" + camioane.size() + ", fitness=" + fitness + '}');
        for(Camion c:camioane) System.out.println(c);
        return "";
    }
    Object getFitnes() {
        return fitness;
    }    

    boolean ok() {
        if(neincarcabile()>0 && neincarcate()>=0) return false;
        for(Camion c:camioane) if(c.ok==false) return false;
        return true;
    }
}
