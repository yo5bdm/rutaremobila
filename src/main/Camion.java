/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
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
    public int[] solutie;
    
    public Camion(int capacitate) {
        this.capacitate = capacitate;
        numara_opriri();
    }
    public void reset() {
        pachete = new ArrayList();
        calc();
    }
    public void optimizare() {
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
        ocupat = 0.0;
        for(int i=0;i<pachete.size();i++) {
            int p = pachete.get(i);
            ocupat += clienti.get(p).volum;
        }
    }

    @Override
    public String toString() {
        String obiecte="";
        for(int i:pachete) {
            obiecte += " "+i+":"+clienti.get(i).volum+",";
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
        int closest;
        Double min_dist, cur_dist=0.0;
        int i=0; //pozitia din cadrul solutiei
        while(pachete_bak.isEmpty()==false) {
            closest = -1; //reset general
            min_dist = Double.MAX_VALUE;
            if(solutia.isEmpty()) { //calculam cel mai apropiat de casa
                //System.out.println("Se alege distanta cea mai scurta de acasa... "+solutia.size());
                for(int j=0;j<pachete_bak.size();j++){
                    cur_dist = catre_casa[pachete_bak.get(j)]; 
                    //System.out.println(cur_dist);
                    if(cur_dist < min_dist) {
                        min_dist = cur_dist;
                        closest = j;
                    }  
                }  
            } else { //pachetul cel mai apropiat de cel curent
                i = solutia.size()-1;
                //System.out.println("Se alege urmatorul punct... "+i+" size "+solutia.size());
                for(int j=0;j<pachete_bak.size();j++) {
                    cur_dist = distante[solutia.get(i)][pachete_bak.get(j)];
                    //if(cur_dist==0.0) System.out.println("solutia "+solutia.get(i)+", pachete "+pachete.get(j)+", distanta "+cur_dist);
                    //System.out.println(cur_dist);
                    if(cur_dist<min_dist) {
                        closest = j;
                        min_dist = cur_dist;
                    }
                }
            }
            //System.out.println("Ales "+closest+", la distanta "+min_dist);
            solutia.add(pachete_bak.get(closest));
            pachete_bak.remove(closest);
        }
        Double dist = catre_casa[solutia.get(0)]; //distanta(casa,clienti.get(solutia.get(0))); //dsitanta initiala de la casa
        for(int j=0;j<solutia.size()-1;j++) { //restul distantei
            //System.out.println(solutia.get(j)+" "+solutia.get(j+1)+" = "+distante[solutia.get(j)][solutia.get(j+1)]);
            dist += distante[solutia.get(j)][solutia.get(j+1)];
        }
        distanta = dist;
    }
}
