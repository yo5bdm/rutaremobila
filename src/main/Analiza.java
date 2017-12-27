/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.MainFrame.s;

/**
 * Clasa pentru analizarea functionarii algoritmului.
 * @author yo5bd
 */
public class Analiza {
    private final ArrayList<Obiect> obiecte;
    private final ArrayList<String> evolutie;
    private final String D = ";";
    private final String formatDataText = "yyyy/MM/dd HH:mm";
    private final String formatDataFisier = "yyyy-MM-dd-HH-mm";
    private final String numeFisier = "Analiza";
    private final String extensieFisier = ".csv";
    /**
     * Constructorul clasei. Initializeaza lista de obiecte.
     */
    public Analiza() {
        obiecte = new ArrayList();
        evolutie = new ArrayList();
        evolutie.add("Generatia"+D+"Nr indivizi"+D+"Viata"+D+"Fitness");
    }
    /**
     * Metoda de adaugare date in analiza.
     * @param generatia int generatia in care s-a gasit rezultatul
     * @param viata int viata generatiei
     * @param nrIndivizi int numarul de indivizi ai firului
     * @param bestSolutie double fitness-ul celei mai bune solutii gasite
     */
    public void adauga(int generatia, int viata, int nrIndivizi, double bestSolutie) {
        Obiect c = new Obiect(viata,nrIndivizi,D);
        boolean mod=false;
        for(Obiect o:obiecte) { //daca exista, actualizam
            if(c.equals(o)) {
                o.actualizeaza(generatia, bestSolutie);
                mod = true;
            }
        }
        if(!mod) { //daca nu, adaugam obiectul nou in lista
            c.actualizeaza(generatia, bestSolutie);
            obiecte.add(c);
        }
        evolutie.add(+generatia+D+nrIndivizi+D+viata+D+(int)bestSolutie);
        System.out.println("G"+generatia+";I"+nrIndivizi+";V"+viata+";F"+(int)bestSolutie);
    }
    /**
     * Genereaza formatul textului pentru export.
     * @return 
     */
    private ArrayList genereazaFisier() {
        ArrayList<String> fis = new ArrayList();
        DateFormat dateFormat = new SimpleDateFormat(formatDataText);
        Date date = new Date();
        fis.add(s.toString());
        fis.add("Fisier generat in "+dateFormat.format(date));
        fis.add("Probabilitate Mutatie"+D+"Nr Indivizi"+D+"Viata"+D+"Nr Solutii Gasite"+D+"Generatia"+D+"Best Solutie");
        for(Obiect o:obiecte) fis.add(o.toString());
        for(String s:evolutie) fis.add(s);
        return fis;
    }
    /**
     * Salveaza fisierul generat de clasa.
     */
    public void saveFile() {
        DateFormat dateFormat = new SimpleDateFormat(formatDataFisier); //2016/11/16 12:08:43
        Date date = new Date();
        try {
            File file = new File(numeFisier+"-"+dateFormat.format(date)+extensieFisier);
            List<String> lines = genereazaFisier();
            Path f = Paths.get(file.getAbsolutePath());
            Files.write(f, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(Analiza.class.getName()).log(Level.SEVERE, null, ex);
        }
        obiecte.clear();
        evolutie.clear();
    }
            
}

class Obiect {
    //nu se schimba, folosite la identificarea obiectului.
    public int viata;
    public int nrIndivizi;
    //se actualizeaza la fiecare inserare
    public int generatia;
    public int nrSolutiiGasite;
    public double bestSolutie;
    //intern, separatorul folosit
    private final String D;

    public Obiect(int viata, int nrIndivizi, String separator) {
        this.D = separator;
        this.viata = viata;
        this.nrIndivizi = nrIndivizi;
        this.nrSolutiiGasite =0;
    }
    
    public void actualizeaza(int generatia, double bestSolutie) {
        this.generatia = generatia;
        this.bestSolutie = bestSolutie;
        nrSolutiiGasite++;
    }

    @Override
    public String toString() {
        return nrIndivizi+D+viata+D+nrSolutiiGasite+D+generatia+D+(int)bestSolutie;
    }

    @Override
    public boolean equals(Object o) {
        Obiect c = (Obiect) o;
        return this.viata == c.viata && this.nrIndivizi == c.nrIndivizi;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.viata;
        hash = 97 * hash + this.nrIndivizi;
        return hash;
    }
    
    
}
