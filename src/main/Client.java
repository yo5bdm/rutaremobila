/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import static main.MainFrame.setari;

/**
 * Clasa cuprinde toate datele clientului.
 * @author yo5bdm
 */
public class Client implements Comparable {
    /* STATICE */
    /**
     * Lista de clienti/puncte unde trebuie sa ajunga marfa.
     */
    public static ArrayList<Client> clienti = new ArrayList();
    /**
     * Backupul listei de clienti. Va fi folosita pentru a restaura lista la restartarea algoritmului
     */
    public static ArrayList<Client> clientiBak = new ArrayList();
    //distantele de la casa la fiecare punct
    private static Double[] spreCasa;
    //distantele intre puncte
    private static Double[][] distante;
    private static int nrClienti;

    public static void sorteaza() {
        Collections.sort(clienti);
        Collections.reverse(clienti);
    }

    static int nrClienti() {
        return nrClienti;
    }

    static void numara() {
        nrClienti = clienti.size();
    }
    
    
    
    /* NESTATICE */
    /**
     * Codul clientului.
     */
    public String codClient;
    /**
     * Codul locatiei de descarcare.
     */
    public String shipTo;
    /**
     * Latitudinea GPS a locatiei de descarcare.
     */
    public Double latitudine;
    /**
     * Longitudinea GPS a locatiei de descarcare.
     */
    public Double longitudine;
    /**
     * Volumul pachetelor de livrat clientului.
     */
    public Double volum;
    /**
     * Constructorul de copiere
     * @param a Clientul ce va fi copiat
     */
    public Client(Client a) {
        this(a.codClient,a.shipTo,a.latitudine,a.longitudine,a.volum);
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
        this.codClient = cod_client;
        this.shipTo = ship_to;
        this.latitudine = GPS1;
        this.longitudine = GPS2;
        this.volum = volum;
    }
    /**
     * Constructorul incomplet. Nu include codul de descarcare.
     * @param cod_client Codul clietului
     * @param GPS1 Latitudine
     * @param GPS2 Longitudine
     * @param volum Volumul pachetelor ce se vor incarca pe camion
     */
    public Client(String cod_client, Double GPS1, Double GPS2, Double volum) {
        this.codClient = cod_client;
        this.shipTo = "";
        this.latitudine = GPS1;
        this.longitudine = GPS2;
        this.volum = volum;
    }
    /**
     * Metoda toString().
     * @return
     */
    public String toString() {
        if(codClient == null) return "Client{" + "ShipTo=" + shipTo + ", GPS=" + latitudine + "," + longitudine + ", volum=" + volum + '}';
        else if(shipTo == null) return "Client{" + "codClient=" + codClient + ", GPS=" + latitudine + "," + longitudine + ", volum=" + volum + '}';
        else return "Client{" + "codClient=" + codClient +", shipTo="+shipTo+", GPS=" + latitudine + "," + longitudine + ", volum=" + volum + '}';
    }
    /**
     * Daca exista clienti cu volum mai mare decat capacitatea camioanelor, aici se trateaza.
     * Se iau camioane disponibile (prima data cele mai mari), se umplu cu marfa si se trimit. 
     * Ce ramane, va fi lasat in algoritm pentru a se gasi o varianta optima de incarcare.
     * Ex: 270:
     * 1 Camion 100 se trimite. Ramane 170.
     * 1 Camion 100 se trimite. Ramane 70.
     * Se lasa restul de 70 in algoritm pentru a se incarca complet camionul respectiv.
     */
    public static void rezolvaCeleMari() {
        ArrayList<Client> temp = new ArrayList();
        Individ.celeMari.clear();
        Individ.celeMariDist = 0.0;
        Individ.celeMariNrCamioane = 0;
        double dist;
        for (Client c : clienti) {
            if (c.volum > (CamionDisponibil.getMaxSize()*setari.procentIncarcare())) {
                while (c.volum > (CamionDisponibil.getMaxSize()*setari.procentIncarcare())) {
                    int vol = CamionDisponibil.scadeLiber();
                    c.volum -= vol*setari.procentIncarcare();
                    dist = Calcule.distanta(Setari.casa, c);
                    Individ.celeMariDist += dist;
                    Individ.celeMariNrCamioane++;
                    Individ.celeMari.add(" ");
                    Individ.celeMari.add("======================================");
                    Individ.celeMari.add("Camion volum " + vol + ", ocupat " + vol + ", opriri 1, distanta totala " + (int) dist + " km;");
                    Individ.celeMari.add(c.codClient + " " + c.shipTo + ", GPS=" + c.latitudine + "," + c.longitudine + " vol=" + vol);
                //break;
                }
                temp.add(c);
            }
        }
//        for(Client c: temp) { //se incarca un singur camion, restul se elimina
//            clienti.remove(c);
//        }
    }
    /**
     * Returneaza distanta dintre 2 clienti.
     * Parametrul se ia din tabloul de distante.
     * @param a Int indexul clientului 1
     * @param b Int indexul clientului 2
     * @return Double distanta dintre cei doi clienti
     */
    public static double distanta(Integer a, Integer b) {
        return distante[a][b];
    }
    /**
     * Returneaza distanta de la orice punct catre casa.
     * Datele se iau dintr-un tablou calculat inainte.
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
            spreCasa[i] = Calcule.distanta(Setari.casa, clienti.get(i));
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

    @Override
    public int compareTo(Object t) {
        Client c = (Client) t;
        return this.volum.compareTo(c.volum);
    }
}
