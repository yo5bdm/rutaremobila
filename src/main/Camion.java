/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static main.MainFrame.*;

/**
 * Clasa de lucru cu camioanele.
 * Include metoda de calcul a traseului optim intre puncte.
 * In varianta actuala, calculul traseului optim se face dupa metoda OVRT,
 * camionul porneste de la baza dar nu se intoarce la baza.
 * @author yo5bdm
 * 
 */
public class Camion {

    /**
     * Capacitatea camionului.
     */
    public double capacitate; 

    /**
     * Volumul total ocupat din camion. Nu e procentul, e volumul total.
     */
    public double ocupat; 

    /**
     * True daca satisface camionul toate cerintele.
     */
    public boolean ok; 

    /**
     * Numarul de opriri/descarcari efectuate (numarul de clienti).
     */
    public int opriri; 

    /**
     * Pachetele din camion.
     */
    public ArrayList<Integer> pachete = new ArrayList(); 

    /**
     * Distanta totala parcursa de camion. Depinde de modul de calcul, daca se intoarce acasa sau nu.
     */
    public double distanta; //distnata totala parcursa

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    
    public Camion(int capacitate) {
        this.capacitate = capacitate;
        numara_opriri();
    }
    /**
     * Constructorul de copiere.
     * @param c Camionul (obiectul) de copiat
     */
    public Camion(Camion c) {
        this.capacitate = c.capacitate;
        this.ocupat = c.ocupat;
        this.ok = c.ok;
        this.opriri = c.opriri;
        this.distanta = c.distanta;
        this.pachete = new ArrayList();
        if(c.pachete!=null && c.pachete.size()>0) {
            for(int i:c.pachete) {
                this.pachete.add(i);
            }
        }
    }
    /**
     * Resetarea camionului.
     */
    public void reset() {
        pachete.clear();
        calc();
    }  
    /**
     * Metoda de calcul complet al camionului.
     * Calculeaza numarul de opriri, volumul total ocupat de pachete, 
     * traseul optim si distanta totala parcursa de camion.
     * @return Double distanta totala parcursa de camion, incepand de la baza
     */
    public double calc() {
        sort();
        calc_partial();
        calculeazaDistanta2();
        return distanta;
    }    
    /**
     * Sorteaza descendent dupa volum pachetele din camion.
     */
    private void sort() {
        Comparator<Integer> cmprtr = (Integer u, Integer d) -> {
            if(Client.clienti.get(u).volum < Client.clienti.get(d).volum) return 1;
            else if(Client.clienti.get(u).volum > Client.clienti.get(d).volum) return -1;
            else return 0;
        };
        pachete.sort(cmprtr);
    }    
    /**
     * Calculeaza numarul de opriri si volumul ocupat al pachetelor din camion.
     */
    private void calc_partial() {
        numara_opriri();
        calculeaza_ocupat();
        if(opriri<=setari.nrDescarcari && (ocupat/capacitate)<=setari.procentIncarcare()) 
            ok = true; 
        else 
            ok = false;
    }    
    /**
     * Sterge ultimul pachet din camion si returneaza indexul acestuia.
     * In prealabil, pachetele au fost ordonate descrescator dupa volum, 
     * deci ultimul pachet va fi cel mai mic ca volum.
     * @return Int indexul din <i>MainFrame clienti</i> a pachetului eliminat.
     */
    public int pop() {
        if(pachete.size()==0) return -1;
        sort();
        int ret = pachete.remove(pachete.size()-1);
        calc_partial();
        return ret;
    }    
    /**
     * Returneaza procentul de ocupare al camionului.
     * Util pentru afisare in lista din MainFrame
     * @return Double procentul de ocupare.
     */
    public Double ocupat() {
        calculeaza_ocupat();
        return (ocupat/capacitate)*100;
    }    
    /**
     * Adauga un pachet in camion si actualizeaza volumul ocupat si numarul de opriri.
     * @param i Indexul pachetului de adaugat. Corespunde pozitiei din <i>ArrayList clienti</i> din MainFrame
     */
    public void add(int i) {
        pachete.add(i);
        calc_partial();
    }    
    /**
     * Numara opririle programate.
     */
    private void numara_opriri() {
        opriri = pachete.size();
    }    
    /**
     * calculeaza volumul total al pachetelor din camion.
     */
    private void calculeaza_ocupat() {
        ocupat = 0.0;
        for(int p:pachete) {
            ocupat += Client.clienti.get(p).volum;
        }
    }

    /**
     * Metoda toString().
     * @return String.
     */
    @Override
    public String toString() {
        String obiecte="";
        for(int i:pachete) {
            obiecte += " "+i+":"+Client.clienti.get(i).volum+",";
        }
        return "Camion{ ok=" +ok+ ", capacitate=" + capacitate + ", ocupat=" + ocupat + ", opriri=" + opriri + ", distanta=" + distanta + ", obiecte= "+obiecte+" }";
    }
      
    /**
     * Metoda de calcul distanta totala optima si traseu.
     * OVRP, camionul nu se intoarce la baza. Se porneste de la baza si se adauga 
     * pachetele pe rand. La fiecare pas se calculeaza pozitia optima a pachetului de inserat
     * astfel incat ruta totala sa fie cat mai scurta.
     */
    public void calculeazaDistanta2() { //varianta clarke-wright modificat
        distanta = 0.0; //initializare
        ArrayList<Integer> solutia = new ArrayList();
        if(pachete.isEmpty()) { //daca nu avem pachete in camion
            return;
        }
        ArrayList<Integer> pacheteBak = new ArrayList(); //altfel
        ArrayList<Integer> solTemp = new ArrayList();
        pacheteBak.addAll(pachete);
        int pos, pachet, bestPos;
        double minDist, dist;
        while(!pacheteBak.isEmpty()) {
            pos = 0;
            minDist=Double.MAX_VALUE; 
            dist=0.0;
            pachet = pacheteBak.get(pacheteBak.size()-1);
            pacheteBak.remove(pacheteBak.size()-1);
            if(solutia.isEmpty()) {
                solutia.add(pachet);
            } else {
                //calculeaza pozitia perfecta
                for(int i=0;i<=solutia.size();i++) {
                    solTemp.clear();
                    solTemp.addAll(solutia);
                    solTemp.add(i,pachet);
                    for(int j=0;j<solTemp.size();j++) {
                        if(j==0) dist = Client.catreCasa(solTemp.get(j));
                        else {
                            dist += Client.distanta(solTemp.get(j-1),solTemp.get(j));
                        }
                    }
                    if(minDist > dist) {
                        pos = i;
                        minDist = dist;
                    }
                }
                solutia.add(pos,pachet);
            }
        }
        distanta = Client.catreCasa(solutia.get(0));
        for(int i=0;i<solutia.size()-1;i++) {
            distanta+=Client.distanta(solutia.get(i),solutia.get(i+1));
        }
        pachete.clear();
        pachete.addAll(solutia);
    }
}
