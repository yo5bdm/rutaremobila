/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yo5bd
 */
public class Autoadaptare {

    /**
     * Algoritm Genetic Autoadaptiv (AGA).
     * Algoritmul foloseste un procent mai mic de mutatie pentru indivizii buni
     * si unul mai mare pentru indivizii mai putin buni
     */
    public static final int[] procMutatie = new int[]{2, 4, 8, 15, 25, 40, 60, 95, 150, 230}; //{2, 4, 8, 15, 20, 35, 45, 60, 150, 200};
    /**
     * Numarul de indivizi per generatie.
     */
    public int nrIndivizi;
    public boolean rapid;
    public double fs; 
    public int selection=2;
    public final int viataIndivid = 20; //5
    public boolean enableAnnealing;
    public int annDx; //diferenta de fitness dupa annealing (media)
    public int pasiAnnealing;
    public int indiviziAnnealing;
    public int nrPuncteTaiere=200; //150
    
    //private
    private int maxPasiAnnealing;
    private int maxIndiviziAnnealing=6;
    private double minFs = 0.5; //0.5
    private double maxFs = 0.5;
    private int maxGeneratii;
    private final double procEvolutie = 1.3; //1.6
    private final double procRegres = 1.1;
    private int faraEvolutie=0;
    private int cuEvolutie=0;
    private int generatia;
    private int minIndivizi;
    private int maxIndivizi;
    private double dx; //aici va fi media diferentei intre cel mai bun si cel mai slab individ din fiecare generatie
    public double lastDx;
    private AnnealingThread ant;
    private Individ antBest;
    private AlgoritmGenetic parinte;
    private boolean enableAnt = true;

    Autoadaptare(int nrClienti, boolean b, AlgoritmGenetic parinte) {
        this.parinte = parinte;
        this.nrIndivizi = nrClienti;
        minIndivizi = 100; //100
        maxIndivizi = 400; //x2
        rapid = b;
        maxGeneratii = 10000;
        dx = 0;
        lastDx =0;
        this.fs = minFs;
        annDx = 9999;
    }

    void boost() {
        selection = 2;
        if(enableAnt) {
            if(ant!=null) {
            antBest = ant.lucru;
            if(antBest.getFitness()<Individ.best.getFitness()) {
                antBest.setViata(40);
                parinte.lock.writeLock().lock();
                parinte.populatie.add(antBest);
                parinte.lock.writeLock().unlock();
            }
            } else {
                ant = new AnnealingThread(Individ.best,Client.clienti.size());
            }
        }
        if(generatia%100 == 0 && rapid == true) { 
            if(generatia%500==0) {
                if(generatia%1000==0) {
                    minIndivizi *= 1.2;
                    maxIndivizi *= 1.2;
                }
                if(enableAnt) {
                    ant.opreste();
                    try {
                        ant.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Autoadaptare.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ant = new AnnealingThread(Individ.best,Client.clienti.size());
                }
            }
        }
        
        
        dx /= 10; //media pe 10 generatii
        if(rapid==true){
            if(dx <200){
                nrIndivizi *= 2;
                selection = 3;
            }
        }
        lastDx = dx;
        dx=0;
    }

    void adaptare(double dx) {
        this.dx += dx;
        if(faraEvolutie > 20) {
            schimba();
            faraEvolutie = -80;
            cuEvolutie=0;
        } else if(maxGeneratii!=Integer.MAX_VALUE && cuEvolutie>20 && generatia>(0.8*maxGeneratii)) {
            maxGeneratii *=1.1;
            cuEvolutie = 0;
        }
        
        if(rapid) {
            fs = minFs;
            if(nrIndivizi > minIndivizi){
                nrIndivizi /= procRegres;
            } else {
                nrIndivizi = minIndivizi;
            }
        } else {
            fs = maxFs;
            selection = 2;
            if(nrIndivizi < maxIndivizi){
                nrIndivizi *= procEvolutie;
            } else {
                nrIndivizi = maxIndivizi;
            }
        }
        
        if(generatia == maxGeneratii/2) {
            minIndivizi *= 2;
            maxIndivizi *= 2;
        }
    }
    
    private void schimba() {
        rapid = !rapid;
        if(enableAnt) {
            ant.opreste();
            try {
                ant.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Autoadaptare.class.getName()).log(Level.SEVERE, null, ex);
            }
            ant = new AnnealingThread(Individ.best,Client.clienti.size());
        }
    }
    
    public boolean continua() {
        generatia++;
        if(generatia < maxGeneratii) {
            return true;
        } else if(cuEvolutie>50) {
            maxGeneratii += 200;
            cuEvolutie = 0;
            return true;
        }        
        return false;
    }
    
    public void evolutie() {
        faraEvolutie=0;
        cuEvolutie++;
        if(cuEvolutie > 5){
            rapid = true;
        }
    }

    public void faraEvolutie() {
        faraEvolutie++;
    }

    public boolean zeceGeneratii() {
        return generatia%10==0;
    }
    
    public int getGeneratia() {
        return generatia;
    }
    
    public void setIndivizi(int fs) {
        nrIndivizi = minIndivizi * fs;
    }
    public void setMaxGen() {
        maxGeneratii = Integer.MAX_VALUE;
    }

    public int getMaxGeneratii() {
        return maxGeneratii;
    }
    
    public double getDx() {
        return dx;
    }

    /**
     * Returneaza procentul de mutatie pentru indivizul curent.
     * Algoritm Genetic Autoadaptiv (AGA).
     *
     * @param i int Indexul elementului curent
     * @param size dimensiunea populatiei pentru care se calculeaza
     * @return int procentul de mutatie
     */
    public int procMut(int i, int size) {                 //%9
        int lung = procMutatie.length;
        return (int)(fs*procMutatie[((i / size) * lung) % lung]);
    }
}
