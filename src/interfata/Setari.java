/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfata;

import algoritm.Client;
import algoritm.CamDisp;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author yo5bd
 */
public class Setari implements Serializable {

    /**
     * Punctul de unde vor pleca camioanele.
     */
    public static Client casa = new Client("ACASA", 47.080, 21.890, 0.0);
    /**
     * Lista de camioane disponibile. Necesar pentru resetare si salvare in fisier.
     */
    public ArrayList<CamDisp> camDisponibile = new ArrayList();
    /**
     * Camionul, la finalul descarcarilor trebuie sa ajunga acasa sau nu. OVRP vs TVRP.
     */
    public boolean ajungeAcasa = false;
    /**
     * Procentul de incarcare al camioanelor. 0.8 = 80%;
     */
    public int procentIncarcare = 100;
    /**
     * Numarul de thread-uri folosite la generare.
     */
    public int memorie = 1;
    /**
     * Prioritatea folosita de thread-uri.
     */
    public int prioritate = 5;
    /**
     * Maximul numarului de descarcari.
     */
    public int nrDescarcari = 17;

    private static final String FILENAME = "Setarile.ser";
    public Setari() {
        //CamionDisponibil.adaugaCapacitate(101,10);
        //CamionDisponibil.adaugaCapacitate(91,15);
        //CamionDisponibil.adaugaCapacitate(81,9999);
    }
    
    @Override
    public String toString() {
        return "Setari{" + "prioritate=" + prioritate + " memorie=" + memorie + " nrDescarcari=" + nrDescarcari + " ajungeAcasa=" + ajungeAcasa + '}';
    }
    
    public static void salveaza(Setari e) {
        try {
           FileOutputStream fileOut = new FileOutputStream(Setari.FILENAME);
           ObjectOutputStream out = new ObjectOutputStream(fileOut);
           out.writeObject(e);
           out.close();
           fileOut.close();
        }catch(Exception i) {
           i.printStackTrace();
        }
    }
    
    public static Setari incarca(){
        try {
            FileInputStream fileIn = new FileInputStream(Setari.FILENAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Setari e = (Setari) in.readObject();
            in.close();
            fileIn.close();
            return e;
         }catch(Exception i) {
            i.printStackTrace();
            return null;
         }
    }
    
    public double procentIncarcare() {
        return (((double)procentIncarcare)/100.0);
    }
}
