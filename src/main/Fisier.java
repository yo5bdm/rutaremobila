/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yo5bd
 */
public class Fisier {

    /**
     * Genereaza un ArrayList din individul dat pentru a putea fi salvat.
     * @param ind Individul ce se doreste a fi salvat in fisier
     * @return ArrayList de formatul String
     */
    public static ArrayList genereazaFisier(Individ ind) {
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
            for (Integer i : cam.solutia) {
                cli = Client.clienti.get(i);
                ret.add(cli.cod_client + " " + cli.ship_to + ", GPS=" + cli.latitudine + "," + cli.longitudine + " vol=" + cli.volum);
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
     */
    public static void incarcaClienti(String fisier) {
        /*https://stackoverflow.com/questions/14274259/read-csv-with-scanner
        https://www.mkyong.com/java/how-to-read-and-parse-csv-file-in-java/
         */
        BufferedReader fin;
        try {
            fin = new BufferedReader(new FileReader(fisier));
            Client.clientiBak.clear();
            String linie;
            while ((linie = fin.readLine()) != null) {
                //fiecare elev pe o linie, separat prin virgula
                StringTokenizer t = new StringTokenizer(linie, ";");
                Client c = new Client(t.nextToken(), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken()));
                Client.clienti.add(c);
                Client.clientiBak.add(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
