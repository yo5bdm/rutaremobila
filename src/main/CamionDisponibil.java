/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Clasa genereaza camioane disponibile, returneaza capacitatea acestuia.
 * Initializarea se face static, instantele putand fi folosite in firele de executie separat unele de altele
 * @author yo5bdm
 */
public class CamionDisponibil {
    private static ArrayList<CamDisp> disponibil=new ArrayList();
    private ArrayList<CamDisp> capacitati;
    private static double maxSize=0;
    //metodele obiectuale
    /**
     * Constructorul clasei. Isi ia datele din proprietatile statice ale clasei
     * Genereaza un obiect cu camioanele ce au mai ramas dupa eliminarea pachetelor mari
     */
    public CamionDisponibil() {
        capacitati = new ArrayList();
        for(CamDisp c:disponibil) {
            capacitati.add(new CamDisp(c.capacitate,c.disponibile));
        }
        Collections.sort(capacitati);
    }
    /**
     * Metoda de returnare a unui camion disponibil.
     * Metoda cauta in lista proprie a obiectului ce camion este disponibil, returneaza volumul acestuia si decrementeaza numarul total disponibil
     * @return intreg reprezentand volumul camionului disponibil ce a fost atribuit
     */
    public int cautaLiber() {
        for(CamDisp i: capacitati) {
            if(i.disponibile>0) {
                //System.out.println("Disponibil "+i.capacitate+", buc "+i.disponibile);
                i.disponibile--;
                return i.capacitate;
            }
            //System.out.println(""+i.capacitate+" Nu e disponibil."+i.disponibile);
        }
        return -1;
    }
    //metode statice
    /**
     * Metoda statica de adaugat camioane disponibile
     * @param cap Capacitatea camionului disponibil
     * @param nrCamioane Numarul de camioane disponibile
     */
    public static void adaugaCapacitate(int cap, int nrCamioane) {
        disponibil.add(new CamDisp(cap,nrCamioane));
        Collections.sort(disponibil);
        if(cap>maxSize) maxSize = (double)cap;
    }
    /**
     * Metoda statica de returnare a unui camion disponibil.
     * Metoda este folosita pentru rezolvarea pachetele mai mari decat volumul disponibil a camionului, inainte de-a porni algortimul 
     * @return intreg reprezentand volumul camionului disponibil ce a fost atribuit
     */
    public static int scadeLiber() {
        for(CamDisp i: disponibil) {
            if(i.disponibile>0) {
                //System.out.println("Disponibil "+i.capacitate+", buc "+i.disponibile);
                i.disponibile--;
                return i.capacitate;
            }
            //System.out.println(""+i.capacitate+" Nu e disponibil."+i.disponibile);
        }
        return -1;
    }
    /**
     * Returneaza capacitatea cea mai mare dintre camioanele disponibile.
     * @return Double - valoarea capacitatii camionului.
     */
    public static double getMaxSize() {
        return maxSize;
    }
}


//clasa obiect cu care lucreaza clasa publica
class CamDisp implements Comparable {
    Integer capacitate;
    Integer disponibile;
    public CamDisp(int capacitate, int disponibile) {
        this.capacitate = capacitate;
        this.disponibile = disponibile;
    }
    public int compareTo(Object o) {
        CamDisp c = (CamDisp) o;
        return c.capacitate.compareTo(this.capacitate);
    }
}
