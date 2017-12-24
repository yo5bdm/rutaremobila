/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.MainFrame.O;

/**
 * Clasa cuprinde toate datele clientului.
 * 
 * @author yo5bdm
 */
public class Client {

    
    //lista de clienti/puncte unde trebuie sa ajunga marfa
    public static ArrayList<Client> clienti = new ArrayList();
    public static ArrayList<Client> clientiBak = new ArrayList();
    //punctul de unde vor pleca camioanele
    public static Client casa = new Client("ACASA", 47.075866, 21.901441, 0.0);
    //distantele de la casa la fiecare punct
    private static Double[] spreCasa;
    //distantele intre puncte
    private static Double[][] distante;
    

    
    public String cod_client;
    public String ship_to;
    public Double latitudine;
    public Double longitudine;
    public Double volum;

    public Client(Client a) {
        this.cod_client = a.cod_client;
        this.ship_to = a.ship_to;
        this.latitudine = a.latitudine;
        this.longitudine = a.longitudine;
        this.volum = a.volum;
    }
    
    public Client(String cod_client, String ship_to, Double GPS1, Double GPS2, Double volum) {
        this.cod_client = cod_client;
        this.ship_to = ship_to;
        this.latitudine = GPS1;
        this.longitudine = GPS2;
        this.volum = volum;
    }

    public Client(String cod_client, Double GPS1, Double GPS2, Double volum) {
        this.cod_client = cod_client;
        this.latitudine = GPS1;
        this.longitudine = GPS2;
        this.volum = volum;
    }
    

    @Override
    public String toString() {
        return "Client{" + "cod_client=" + cod_client + ", GPS1=" + latitudine + "," + longitudine + ", volum=" + volum + '}';
    }
    
    /**
     * Metoda de incarcare clienti din fisier.
     */
    public static void incarcaClienti(String fisier) {
        BufferedReader fin;
        try {
            fin = new BufferedReader(new FileReader(fisier));
            clientiBak.clear();
            String linie;
            while ((linie = fin.readLine()) != null) {
                //fiecare elev pe o linie, separat prin virgula
                StringTokenizer t = new StringTokenizer(linie, ";");
                Client c = new Client(t.nextToken(), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken()));
                clienti.add(c);
                clientiBak.add(c);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Daca exista clienti cu volum mai mare decat capacitatea camioanelor, aici se trateaza.
     */
    public static void rezolvaCeleMari() {
        ArrayList<Client> temp = new ArrayList();
        Individ.celeMari.clear();
        Individ.celeMariDist = 0.0;
        Individ.celeMariNrCamioane = 0;
        double dist;
        System.out.println("Nr Clienti " + clienti.size());
        for (Client c : clienti) {
            if (c.volum > CamionDisponibil.getMaxSize()) {
                System.out.println("Rezolv " + c);
                while (c.volum > CamionDisponibil.getMaxSize()) {
                    int volum = CamionDisponibil.scadeLiber();
                    //System.out.println("Camion "+volum);
                    c.volum -= volum;
                    //System.out.println("Ramas"+c.volum);
                    dist = Calcule.distanta(casa, c);
                    Individ.celeMariDist += dist;
                    Individ.celeMariNrCamioane++;
                    Individ.celeMari.add(" ");
                    Individ.celeMari.add("======================================");
                    Individ.celeMari.add("Camion volum " + volum + ", ocupat " + volum + ", opriri 1, distanta totala " + (int) dist + " km;");
                    Individ.celeMari.add(c.cod_client + " " + c.ship_to + ", GPS=" + c.latitudine + "," + c.longitudine + " vol=" + volum);
                }
                temp.add(c);
            }
        }
        System.out.println("Cele mari distnata " + Individ.celeMariDist);
        System.out.println("Cele mari nr camioane " + Individ.celeMariNrCamioane);
    }
    
    public static double distanta(Integer a, Integer b) {
        return distante[a][b];
    }
    
    public static double catreCasa(Integer a) {
        return spreCasa[a];
    }
    public static void calculeazaTablouDistante() {
        distante = new Double[clienti.size()][clienti.size()];
        for (int i = 0; i < clienti.size(); i++) {
            for (int j = 0; j < clienti.size(); j++) {
                distante[i][j] = Calcule.distanta(clienti.get(i), clienti.get(j));
            }
        }
        spreCasa = new Double[clienti.size()];
        for (int i = 0; i < clienti.size(); i++) {
            spreCasa[i] = Calcule.distanta(casa, clienti.get(i));
        }
    }
}
