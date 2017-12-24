/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;

/**
 * Clasa cuprinde toate datele clientului.
 * @author yo5bdm
 */
public class Client {

    /* STATICE */
    //lista de clienti/puncte unde trebuie sa ajunga marfa
    public static ArrayList<Client> clienti = new ArrayList();
    //backupul listei de clienti, va fi folosita pentru a restaura lista la restartarea algoritmului
    public static ArrayList<Client> clientiBak = new ArrayList();
    //punctul de unde vor pleca camioanele
    public static Client casa = new Client("ACASA", 47.075866, 21.901441, 0.0);
    //distantele de la casa la fiecare punct
    private static Double[] spreCasa;
    //distantele intre puncte
    private static Double[][] distante;
    
    /* NESTATICE */
    public String cod_client;
    public String ship_to;
    public Double latitudine;
    public Double longitudine;
    public Double volum;
    /**
     * Constructorul de copiere
     * @param a Clientul ce va fi copiat
     */
    public Client(Client a) {
        this.cod_client = a.cod_client;
        this.ship_to = a.ship_to;
        this.latitudine = a.latitudine;
        this.longitudine = a.longitudine;
        this.volum = a.volum;
    }
    /**
     * Constructorul complet.
     * @param cod_client Codul clientului
     * @param ship_to Codul locatiei la care se trimit pachetele
     * @param GPS1 Latitudine
     * @param GPS2 Longitudine
     * @param volum Volumul pachetelor ce se vor incarca pe camion
     */
    public Client(String cod_client, String ship_to, Double GPS1, Double GPS2, Double volum) {
        this.cod_client = cod_client;
        this.ship_to = ship_to;
        this.latitudine = GPS1;
        this.longitudine = GPS2;
        this.volum = volum;
    }
    /**
     * Constructorul incomplet.
     * @param cod_client Codul clietului
     * @param GPS1 Latitudine
     * @param GPS2 Longitudine
     * @param volum Volumul pachetelor ce se vor incarca pe camion
     */
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
     * Daca exista clienti cu volum mai mare decat capacitatea camioanelor, aici se trateaza.
     */
    public static void rezolvaCeleMari() {
        ArrayList<Client> temp = new ArrayList();
        Individ.celeMari.clear();
        Individ.celeMariDist = 0.0;
        Individ.celeMariNrCamioane = 0;
        double dist;
        for (Client c : clienti) {
            if (c.volum > CamionDisponibil.getMaxSize()) {
                while (c.volum > CamionDisponibil.getMaxSize()) {
                    int volum = CamionDisponibil.scadeLiber();
                    c.volum -= volum;
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
    }
    /**
     * Returneaza distanta dintre 2 clienti.
     * @param a Int indexul clientului 1
     * @param b Int indexul clientului 2
     * @return Double distanta dintre cei doi clienti
     */
    public static double distanta(Integer a, Integer b) {
        return distante[a][b];
    }
    /**
     * Returneaza distanta de la orice punct catre casa
     * @param a Int indexul clientului
     * @return Double distanta dintre client si casa
     */
    public static double catreCasa(Integer a) {
        return spreCasa[a];
    }
    /**
     * Calculeaza tablourile de distante din care se vor lua datele.
     * Aplicatia lucreaza cu 2 tablouri:
     * 1 - distanta de la fiecare punct catre casa (metoda catreCasa())
     * 2 - distanta dintre oricare doua puncte (metoda distanta())
     */
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
    /**
     * Restaureaza lista de clienti din lista de backup.
     * Utila pentru resetarea listei la restartarea algoritmului 
     * cu aceeasi clienti, fara o reincarcare a fisierului.
     */
    public static void restore() {
        clienti.clear();
        for(Client c:clientiBak) { //copiaza datele din backup
            clienti.add(new Client(c));
        }
    }
}
