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
    public int[] solutia;
    
    private int[] solutie;
    
    public Camion(int capacitate) {
        this.capacitate = capacitate;
        numara_opriri();
    }
    
    public double calc() {
        Comparator<Integer> cmprtr = (Integer u, Integer d) -> {
            if(MainFrame.clienti.get(u).volum < MainFrame.clienti.get(d).volum) return 1;
            else if(MainFrame.clienti.get(u).volum > MainFrame.clienti.get(d).volum) return -1;
            else return 0;
        };
        //System.out.println("Sortam produsele in camion...");
        pachete.sort(cmprtr);
        //System.out.println("Se numara opririle...");
        numara_opriri();
        //System.out.println("Calculeaza cat e ocupat...");
        calculeaza_ocupat();
        //System.out.println("Calculeaza distanta totala parcursa de camion...");
        calculeaza_distanta();
        //System.out.println("Gata... Total parcurs="+distanta);
        if(opriri<=15 && (ocupat/capacitate)<=1) 
            ok = true; 
        else 
            ok = false;
        return distanta;
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
        //resetam distanta
        solutie = new int[pachete.size()];
        for(int i:solutie) i=-1; //initializare
        distanta=Double.MAX_VALUE;
        int closest=-1;
        Double min_dist = Double.MAX_VALUE;
        Double cur_dist;
        for(int i=0;i<pachete.size();i++) {
            if(i==0) { //calculam cel mai apropiat de casa
                min_dist = Double.MAX_VALUE;
                for(int j=0;j<pachete.size();j++){
                    cur_dist = distanta(casa,clienti.get(pachete.get(j)));
                    if(cur_dist<min_dist) {
                        min_dist = cur_dist;
                        closest = j;
                    }
                }
                                    
            } else { //pachetul cel mai apropiat de cel curent
                closest = -1;
                min_dist = Double.MAX_VALUE;
                for(int j=0;j<pachete.size();j++) {
                    if(i==j) continue;
                    cur_dist = distante[solutie[i-1]][pachete.get(j)];
                    if(cur_dist<min_dist) {
                        closest = j;
                        min_dist = cur_dist;
                    }
                }
            }
            solutie[i] = pachete.get(closest);
        }
        //System.out.println(solutie);
        //solutie gasita, calculam distanta totala
        Double dist = 0.0; //distanta(casa,clienti.get(solutie[0])); //dsitanta initiala de la casa
        for(int i=0;i<solutie.length-1;i++) { //restul distantei
            dist += distante[solutie[i]][solutie[i+1]];
        }
        distanta = dist;
        solutia = solutie;
    }
    
    
    
    
    /*private void calculeaza_distanta() { //varianta backtracking
        //backtracking pe solutie
        solutie = new int[pachete.size()];
        for(int i:solutie) i=-1; //initializare
        distanta=Double.MAX_VALUE;
        backtr(0);
    }*/
    
    //start backtracking functions
    private void backtr(int pos) {
        if(pos>=solutie.length) return;
        for(int i=0;i<pachete.size();i++) {
            solutie[pos] = pachete.get(i);
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
    }
    //end backtracking
}
