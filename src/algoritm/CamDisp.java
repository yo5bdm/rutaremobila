/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algoritm;

import java.io.Serializable;

/**
 *
 * @author yo5bd
 */
//clasa obiect cu care lucreaza clasa publica
public class CamDisp implements Comparable, Serializable {
    public Integer capacitate;
    public Integer disponibile;
    public CamDisp(int capacitate, int disponibile) {
        this.capacitate = capacitate;
        this.disponibile = disponibile;
    }

    CamDisp(CamDisp i) {
        this.capacitate = i.capacitate;
        this.disponibile = i.disponibile;
    }
    public int compareTo(Object o) {
        CamDisp c = (CamDisp) o;
        return c.capacitate.compareTo(this.capacitate);
    }    
}