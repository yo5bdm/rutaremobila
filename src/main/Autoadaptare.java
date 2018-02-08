/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author yo5bd
 */
public class Autoadaptare {
    /**
     * Numarul de indivizi per generatie.
     */
    public int nrIndivizi;
    /**
     * Metoda de selectie.
     * true = se iau primii cei mai buni indivizi,
     * false = random gaussian dintre cei mai buni indivizi
     */
    public boolean rapid;
    public double fs=0.5;
    private int maxGeneratii;
    private final double procEvolutie = 1.3; //1.6
    private final double procRegres = 1.1;
    private int faraEvolutie=0;
    private int cuEvolutie=0;
    private int generatia;
    private int minIndivizi;
    private int maxIndivizi;

    Autoadaptare(int nrClienti, boolean b) {
        this.nrIndivizi = nrClienti;
        minIndivizi = 100;
        maxIndivizi = nrClienti * 2;
        rapid = b;
        maxGeneratii = 20000;
    }

    void boost() {
        if(generatia%100==0) {
            if(generatia!=0 && nrIndivizi < maxIndivizi) {
                nrIndivizi *= 4;
            }
            if(generatia%1000 == 0) {
                minIndivizi *= 1.2;
                maxIndivizi *= 1.5;
            }
        }
    }

    void adaptare() {
        if(faraEvolutie > 20) {
            rapid = !rapid;
            faraEvolutie = -80;
            cuEvolutie=0;
        } else if(maxGeneratii!=Integer.MAX_VALUE && cuEvolutie>20 && generatia>(0.8*maxGeneratii)) {
            maxGeneratii *=1.1;
            cuEvolutie = 0;
        }
        
        if(rapid) {
            if(nrIndivizi > minIndivizi){
                nrIndivizi /= procRegres;
            } else {
                nrIndivizi = minIndivizi;
            }
        } else {
            if(nrIndivizi < maxIndivizi){
                nrIndivizi *= procEvolutie;
            } else {
                nrIndivizi = maxIndivizi;
            }
        }
        
        if(generatia == maxGeneratii/2) {
            minIndivizi *= 2;
            nrIndivizi *= 2;
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
        if(cuEvolutie > 3){
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

    int getMaxGeneratii() {
        return maxGeneratii;
    }
}
