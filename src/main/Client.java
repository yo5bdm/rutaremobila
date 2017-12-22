/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * Clasa cuprinde toate datele clientului.
 * 
 * @author yo5bdm
 */
public class Client {
    public String cod_client;
    public String ship_to;
    public Double latitudine;
    public Double longitudine;
    public Double volum;

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
    
}
