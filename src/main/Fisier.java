/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author yo5bd
 */
public class Fisier {

    /**
     *
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
    
}
