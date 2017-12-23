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
 * Clasa de lucru cu camioanele.
 * Include metoda de calcul a traseului optim intre puncte.
 * In varianta actuala, calculul traseului optim se face dupa metoda OVRT,
 * camionul porneste de la baza dar nu se intoarce la baza.
 * @author yo5bdm
 * 
 */
public class Camion {
    public double capacitate; //capacitatea camionului
    public double ocupat; //cat e ocupat din camion
    public boolean ok; //satisface camionul toate cerintele?
    public int opriri; //numarul de opriri efectuate (numarul de clienti)
    public ArrayList<Integer> pachete = new ArrayList(); //pachetele din camion
    public double distanta; //distnata totala parcursa
    public ArrayList<Integer> solutia; //ruta parcursa de camion. nu include pornirea (casa)
    /**
     * Constructor.
     * @param capacitate Int capacitatea ce o va avea camionul
     */
    public Camion(int capacitate) {
        this.capacitate = capacitate;
        numara_opriri();
    }
    /**
     * Resetarea camionului.
     */
    public void reset() {
        pachete = new ArrayList();
        calc();
    }
    /**
     * Neimplementat
     */
    public void optimizare() {
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
            if(MainFrame.clienti.get(u).volum < MainFrame.clienti.get(d).volum) return 1;
            else if(MainFrame.clienti.get(u).volum > MainFrame.clienti.get(d).volum) return -1;
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
        if(opriri<=15 && (ocupat/capacitate)<=1) 
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
     * Numara opririle programate
     */
    private void numara_opriri() {
        opriri = pachete.size();
        if(opriri<15) ok = true;
    }    
    /**
     * calculeaza volumul total al pachetelor din camion
     */
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
    // metodele de calculare a distantei optime dintre puncte
    
    /**
     * Metoda de calcul distanta totala optima si traseu cu metoda Greedy.
     * OVRP, camionul nu se intoarce la baza. Se porneste de la baza si se 
     * cauta cel mai apropiat punct. Procesul se reia pentru toate punctele.
     */
    public void calculeazaDistanta() { //varianta greedy
        distanta = 0.0;
        solutia = new ArrayList();
        if(pachete.isEmpty()) { //daca nu avem pachete in camion
            return;
        }
        ArrayList<Integer> pacheteBak = new ArrayList(); //altfel
        pacheteBak.addAll(pachete);
        int closest;
        Double min_dist, cur_dist=0.0;
        int i=0; //pozitia din cadrul solutiei
        while(pacheteBak.isEmpty()==false) {
            closest = -1; //reset general
            min_dist = Double.MAX_VALUE;
            if(solutia.isEmpty()) { //calculam cel mai apropiat de casa
                //System.out.println("Se alege distanta cea mai scurta de acasa... "+pacheteBak.size());
                for(int j=0;j<pacheteBak.size();j++){
                    cur_dist = catre_casa[pacheteBak.get(j)]; 
                    //System.out.println(cur_dist);
                    if(cur_dist < min_dist) {
                        min_dist = cur_dist;
                        closest = j;
                    }  
                }  
            } else { //pachetul cel mai apropiat de cel curent
                i = solutia.size()-1;
                //System.out.println("Se alege urmatorul punct... "+i+" size "+solutia.size());
                for(int j=0;j<pacheteBak.size();j++) {
                    cur_dist = distante[solutia.get(i)][pacheteBak.get(j)];
                    //if(cur_dist==0.0) System.out.println("solutia "+solutia.get(i)+", pachete "+pachete.get(j)+", distanta "+cur_dist);
                    //System.out.println(cur_dist);
                    if(cur_dist<min_dist) {
                        closest = j;
                        min_dist = cur_dist;
                    }
                }
            }
            //System.out.println("Ales "+closest+", la distanta "+min_dist);
            solutia.add(pacheteBak.get(closest));
            pacheteBak.remove(closest);
        }
        Double dist = catre_casa[solutia.get(0)]; //distanta(casa,clienti.get(solutia.get(0))); //dsitanta initiala de la casa
        for(int j=0;j<solutia.size()-1;j++) { //restul distantei
            //System.out.println(solutia.get(j)+" "+solutia.get(j+1)+" = "+distante[solutia.get(j)][solutia.get(j+1)]);
            dist += distante[solutia.get(j)][solutia.get(j+1)];
        }
        distanta = dist;
        //System.out.println(solutia);
    }
    
    /**
     * Metoda de calcul distanta totala optima si traseu cu o metoda modificata a algoritmului Clarke-Wright.
     * OVRP, camionul nu se intoarce la baza. Se porneste de la baza si se adauga 
     * pachetele pe rand. La fiecare pas se calculeaza pozitia optima a pachetului de inserat
     * astfel incat ruta totala sa fie cat mai scurta.
     */
    public void calculeazaDistanta2() { //varianta clarke-wright modificat
        distanta = 0.0; //initializare
        solutia = new ArrayList();
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
            if(solutia.isEmpty()) solutia.add(pachet);
            else {
                //calculeaza pozitia perfecta
                for(int i=0;i<=solutia.size();i++) {
                    solTemp.clear();
                    solTemp.addAll(solutia);
                    solTemp.add(i,pachet);
                    for(int j=0;j<solTemp.size();j++) {
                        if(j==0) dist = catre_casa[solTemp.get(j)];
                        else {
                            dist += distante[solTemp.get(j-1)][solTemp.get(j)];
                        }
                    }
                    if(minDist > dist) {
                        pos = i;
                        minDist = dist;
                    }
                }
                //insereaza la pozitia perfecta
                solutia.add(pos,pachet);
            }
        }
        distanta = catre_casa[solutia.get(0)];
        for(int i=0;i<solutia.size()-1;i++) {
            distanta+=distante[solutia.get(i)][solutia.get(i+1)];
        }
        //System.out.println(solutia);
    }
}
