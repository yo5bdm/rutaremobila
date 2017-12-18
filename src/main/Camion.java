/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Comparator;
import static main.MainFrame.*;

/**
 *
 * @author yo5bd
 * 
 */
public class Camion {
    public double capacitate; //capacitatea camionului
    public double ocupat; //cat e ocupat din camion
    public boolean ok;
    public int opriri; //numarul de opriri efectuate (numarul de clienti
    public ArrayList<Integer> pachete = new ArrayList();
    public double distanta;
    public ArrayList<Integer> solutia;
    
    public Camion(int capacitate) {
        this.capacitate = capacitate;
        numara_opriri();
    }
    
    public void reset() {
        pachete = new ArrayList();
        calc();
    }
    
    
    
    public double calc() {
        sort();
        calc_partial();
        calculeaza_distanta();
        return distanta;
    }
    
    private void sort() {
        Comparator<Integer> cmprtr = (Integer u, Integer d) -> {
            if(MainFrame.clienti.get(u).volum < MainFrame.clienti.get(d).volum) return 1;
            else if(MainFrame.clienti.get(u).volum > MainFrame.clienti.get(d).volum) return -1;
            else return 0;
        };
        pachete.sort(cmprtr);
    }
    
    private void calc_partial() {
        numara_opriri();
        calculeaza_ocupat();
        if(opriri<=15 && (ocupat/capacitate)<=1) 
            ok = true; 
        else 
            ok = false;
    }
    
    public int pop() {
        sort();
        int ret = pachete.remove(pachete.size()-1);
        calc_partial();
        return ret;
    }
    
    public Double ocupat() {
        return (ocupat/capacitate)*100;
    }
    
    public void add(int i) {
        pachete.add(i);
        calc_partial();
    }
    
    private void numara_opriri() {
        opriri = pachete.size();
        if(opriri<15) ok = true;
    }
    
    private void calculeaza_ocupat() {
        double volum=0;
        for(int i=0;i<pachete.size();i++) {
            int p = pachete.get(i);
            volum += MainFrame.clienti.get(p).volum;
        }
        ocupat = volum;
    }

    @Override
    public String toString() {
        String obiecte="";
        for(int i:pachete) {
            obiecte += " "+i+":"+MainFrame.clienti.get(i).volum+",";
        }
        return "Camion{ ok=" +ok+ ", capacitate=" + capacitate + ", ocupat=" + ocupat + ", opriri=" + opriri + ", distanta=" + distanta + ", obiecte= "+obiecte+" }";
    }

    private void calculeaza_distanta() { //varianta greedy
        distanta = 0.0;
        solutia = new ArrayList();
        if(pachete.isEmpty()) { //daca nu avem pachete in camion
            return;
        }
        ArrayList<Integer> pachete_bak = new ArrayList(); //altfel
        pachete_bak.addAll(pachete);
        distanta=Double.MAX_VALUE;
        int closest;
        Double min_dist, cur_dist = 0.0;
        int i=0; //pozitia din cadrul solutiei
        while(pachete_bak.isEmpty()==false) {
            closest = -1; //reset general
            min_dist = Double.MAX_VALUE;
            if(i==0) { //calculam cel mai apropiat de casa
                min_dist = Double.MAX_VALUE;
                for(int j=0;j<pachete_bak.size();j++){
                    cur_dist = distanta(casa,clienti.get(pachete_bak.get(j)));
                    if(cur_dist<min_dist) {
                        min_dist = cur_dist;
                        closest = j;
                    }     
                }  
            } else { //pachetul cel mai apropiat de cel curent
                i = solutia.size()-1;
                for(int j=0;j<pachete_bak.size();j++) {
                    cur_dist = distante[solutia.get(i)][pachete.get(j)];
                    if(cur_dist<min_dist) {
                        closest = j;
                        min_dist = cur_dist;
                    }
                }
            }
            solutia.add(pachete_bak.get(closest));
            pachete_bak.remove(closest);
        }
        Double dist = distanta(casa,clienti.get(solutia.get(0))); //dsitanta initiala de la casa
        for(int j=0;j<solutia.size()-1;j++) { //restul distantei
            dist += distante[solutia.get(j)][solutia.get(j+1)];
        }
        distanta = dist;
    }
    
    
    
    
    /*private void calculeaza_distanta() { //varianta backtracking
        //backtracking pe solutie
        solutie = new int[pachete.size()];
        for(int i:solutie) i=-1; //initializare
        distanta=Double.MAX_VALUE;
        backtr(0);
    }
    
    //start backtracking functions
    private void backtr(int pos) {
        if(pos>=solutie.size()) return;
        for(int i=0;i<pachete.size();i++) {
            solutie.get(pos) = pachete.get(i);
            if(verifica(pos)) backtr(++pos);
        }
    }
    private boolean verifica(int pos) {
        double dist = 0;
        if(pos==0) return true;
        if(pos==solutie.length) return false;
        for(int i=0;i<pos;i++) {
            if(solutie[pos]==solutie[i]) return false;
        }
        if(pos==(solutie.length-1)) { //daca solutia e completa
            for(int i=0;i<solutie.length-1;i++) {
                dist += distante[solutie[i]][solutie[i+1]];
            }
            if(dist<distanta) {
                distanta = dist;
                solutia = solutie;
            }
            return false; //am ajuns la capat, nu mai continuam
        }
        return true;
    }*/
    //end backtracking
}
