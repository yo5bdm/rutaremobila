/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.CSVUtils.*;
import static main.MainFrame.*;

/**
 * Clasa de lucru cu fisierele
 * @author yo5bdm
 */
public class Fisier {

    /**
     * Genereaza un ArrayList din individul dat pentru a putea fi salvat.
     * @param ind Individul ce se doreste a fi salvat in fisier
     * @return ArrayList de formatul String
     */
    public static ArrayList genereazaFisier(Individ ind) {
        //todo Optiune scriere fisier text, csv sau html.
        //todo imbunatatire aspect txt
        Client cli;
        ArrayList<String> ret = new ArrayList();
        ret.add("======================================");
        ret.add("Distanta totala de parcurs: " + (int) (ind.getFitness() + Individ.celeMariDist) + " km");
        ret.add("Numar total de camioane folosite: " + (ind.camioane.size() + Individ.celeMariNrCamioane));
        ret.addAll(Individ.celeMari);
        for (Camion cam : ind.camioane) {
            ret.add(" ");
            ret.add("======================================");
            ret.add("Camion volum " + (cam.capacitate - 1) + ", ocupat " + cam.ocupat + ", opriri " + cam.opriri + ", distanta totala " + (int) cam.distanta + " km;");
            ret.add("Pachetele de incarcat:");
            for (Integer i : cam.pachete) {
                cli = Client.clienti.get(i);
                ret.add(cli.codClient + " " + cli.shipTo + ", GPS=" + cli.latitudine + "," + cli.longitudine + " vol=" + cli.volum);
            }
        }
        ret.add(" ");
        ret.add("======================================");
        ret.add("Fisier generat  " + LocalDateTime.now());
        ret.add("======================================");
        return ret;
    }

    /**
     * Metoda de incarcare a unui fisier in ArrayListurile statice din clasa Clienti.
     * @param fisier String cu numele fisierului ce contine datele
     * @return Boolean true daca fisierul a fost incarcat cu succes, false daca nu.
     */
    public static boolean incarcaClienti(String fisier) {
        /*
        @todo Eventual dam clientului ocazia sa aleaga ce tip de fisier este, ce inseamna coloanele
        https://stackoverflow.com/questions/14274259/read-csv-with-scanner
        https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
         */
        Scanner scanner;
        try {
            scanner = new Scanner(new File(fisier));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Fisier.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        Client c;
        while (scanner.hasNext()) {
            ArrayList<String> line = parseLine(scanner.nextLine());
            switch (line.size()) {
                case 4:
                    //constructorul cu 4
                    c = new Client(line.get(0),Double.parseDouble(line.get(1)),Double.parseDouble(line.get(2)),Double.parseDouble(line.get(3)));
                    Client.clienti.add(c);
                    break;
                case 5:
                    //constructorul cu 5
                    c = new Client(line.get(0),line.get(1),Double.parseDouble(line.get(2)),Double.parseDouble(line.get(3)),Double.parseDouble(line.get(4)));
                    Client.clienti.add(c);
                    break;
                default:
                    mesajEroare("Fisierul CSV nu are un format suportat!");
                    return false;
            }
        }
        scanner.close();
        Client.sorteaza();
        Client.numara();
        Client.clientiBak.addAll(Client.clienti);
        return true;
    }
    
}