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
 * @author yo5bdm
 */
public class CamionDisponibil {
    ArrayList<CamDisp> capacitati;
    
    public CamionDisponibil() {
        capacitati = new ArrayList();
    }
    
    public void adaugaCapacitate(int cap, int nrCamioane) {
        capacitati.add(new CamDisp(cap,nrCamioane));
        Collections.sort(capacitati);
    }
    
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
}

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
