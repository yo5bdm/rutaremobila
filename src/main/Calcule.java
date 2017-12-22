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
public class Calcule {

    /**
     *
     * @param u the value of u
     * @param d the value of d
     * @return the double
     */
    public static double distanta(Client u, Client d) {
        //https://www.movable-type.co.uk/scripts/latlong.html
        double delta_lat = toRad(u.latitudine - d.latitudine); //latitudine
        double delta_long = toRad(u.longitudine - d.longitudine);
        double R = 6371; //km
        double kmReal = 1.24;
        double a = Math.sin(delta_lat / 2) * Math.sin(delta_lat / 2) + Math.cos(toRad(u.latitudine)) * Math.cos(toRad(d.latitudine)) * Math.sin(delta_long / 2) * Math.sin(delta_long / 2);
        return kmReal * R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     *
     * @param unghi the value of unghi
     * @return the double
     */
    public static double toRad(double unghi) {
        return unghi * Math.PI / 180;
    }
    
}
