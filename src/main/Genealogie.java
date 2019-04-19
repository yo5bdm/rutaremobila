/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 * Aceasta clasa tine evidenta genealogiei unui anumit obiect. In tablou se
 * retine genealogia efectiva, de forma: linia 0 - parintii obiectului (2
 * elemente) linia 1 - bunicii (parintii parintilor), 4 elemente linia 2 -
 * strabunicii, 8 elemente etc... Controlul nivelurilor se face din LEVELS, acum
 * este 5.
 *
 * @author yo5bd
 */
public class Genealogie {

    private static long newId = 1;
    private static final int LEVELS = 5;
    private static int coloane;

    /**
     * Verifica compatibilitatea a doua genealogii, practic verifica sa nu aiba
     * nici un element comun in genealogie
     *
     * @param a individul 1
     * @param b individul 2
     * @return true daca sunt compabili, false daca nu
     */
    public static boolean compatibile(Genealogie a, Genealogie b) {
        int col = 2;
        for (int i = 0; i < LEVELS; i++) {
            for (int j = 0; j < col; j++) {
                if (a.tablou[i][j] != 0 && b.tablou[i][j] != 0) {
                    if (b.exista(a.tablou[i][j])) {
                        return false;
                    }
                }
            }
            col *= 2;
        }
        return true;
    }

    private static long getId() { //genereaza un ID unic pentru fiecare genealogie
        return newId++;
    }

    // ----------- OBJECT ------------------------
    long[][] tablou;
    private long id;

    Genealogie() {
        id = getId();
        coloane = (int) Math.pow(2, LEVELS);
        tablou = new long[LEVELS][coloane];
    }

    public void copiaza(Genealogie genealogie) {
        int col = 2;   //parintii pe linia 0 au 2 coloane
        for (int i = 0; i < LEVELS; i++) {
            for (int j = 0; j < col; j++) { //se copiaza parintii parintilor la bunici, bunicii la strabunici, etc
                tablou[i][j] = genealogie.tablou[i][j]; //ordinea nu conteaza
            }
            return; //copiem doar parintii
            //col *= 2; //in nivelul urmator avem dublu nr de coloane
        }
    }

    /**
     * Copiaza genealogia parintilor in genealogia copilului
     *
     * @param a parinte 1
     * @param b parinte 2
     */
    public void setParinti(Genealogie a, Genealogie b) {
        this.tablou[0][0] = a.id; //cei doi indivizi devin parintii
        this.tablou[0][1] = b.id;
        int col = 2;   //parintii pe linia 0 au 2 coloane
        for (int i = 1; i < LEVELS; i++) {
            for (int j = 0; j < col; j++) { //se copiaza parintii parintilor la bunici, bunicii la strabunici, etc
                tablou[i][2 * j] = a.tablou[i - 1][j]; //ordinea nu conteaza
                tablou[i][2 * j + 1] = b.tablou[i - 1][j];
            }
            col *= 2; //in nivelul urmator avem dublu nr de coloane
        }
    }

    /**
     * Cauta un element in genealogia obiectului.
     *
     * @param nr - elementul cautat
     * @return true daca exista, false daca nu
     */
    public boolean exista(long nr) {
        //nu se poate incrucisa un element cu el insusi
        if (this.id == nr) {
            return true;
        }
        int col = 2;
        for (int i = 0; i < LEVELS; i++) {
            for (int j = 0; j < col; j++) {
                if (tablou[i][j] == nr) {
                    return true;
                }
            }
            col *= 2;
        }
        return false;
    }

    @Override
    public String toString() {
        System.out.println("--------------------------------------");
        System.out.println("ID = " + id);
        int col = 2;
        for (int i = 0; i < LEVELS; i++) {
            for (int j = 0; j < col; j++) { //se copiaza parintii parintilor la bunici, bunicii la strabunici, etc
                System.out.print(tablou[i][j] + " ");
            }
            System.out.println("");
            col *=2;
        }
        return "";
    }

}
