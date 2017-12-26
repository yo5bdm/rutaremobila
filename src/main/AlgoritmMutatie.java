/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import static main.MainFrame.O;
import static main.MainFrame.m;

/**
 *
 * @author yo5bd
 */
public class AlgoritmMutatie extends AlgoritmGenetic {
    ArrayList<Individ> temp;
    
    public AlgoritmMutatie(int id, int viteza, int probMutatie) {
        super(id, viteza, probMutatie, Integer.MAX_VALUE, 0);
        temp = new ArrayList();
    }
    @Override
    public void run() {
        double total;
        Individ best_fit;
        temp = new ArrayList();
        populatie.clear();
        popTemp.clear();
        int g=0;
        while(true) {//main loop
            if(populatie.isEmpty()) try {
                synchronized(O){
                    O.wait();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(AlgoritmMutatie.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(g%10==0) {
                m.setProgres(id,g);
                //System.out.println("F "+probabilitateMutatie+"; G "+g);
            }
            populatie.addAll(temp);
            temp.clear();
            nrIndivizi = populatie.size();
            if(populatie.isEmpty()) continue;
            if(stop) break; //stop? atunci iesi din bucla
            if(g>maxGeneratii) break;
            
            mutatie();//
            for(Individ i:popTemp) i.calculeaza(true); //calculam fitnessul pentru populatia temporara
            selectie();//
            Collections.sort(populatie);
            best_fit = populatie.get(0);
            synchronized(O) {
                if(best_fit.getFitness()<Individ.best.getFitness() && best_fit.ok()==true) { //
                    System.out.println("MUTATIE: V "+viataInd+"; F "+(int)best_fit.getFitness());
                    m.setBest(best_fit,"M"+probabilitateMutatie+"V"+viataInd);
                }
                O.notifyAll();
            }
            if(g%10==0) {
                m.setProgres(id,g);
                //System.out.println("M "+probabilitateMutatie+"; G "+g);
            }
            g++;
        }
        m.setProgres(id,maxGeneratii);
        System.out.println("Firul "+probabilitateMutatie+"% a finalizat");
    }

    @Override
    protected void selectie() {
        //popTemp.addAll(populatie); //includem si parintii
        if(popTemp.isEmpty()) return;
        populatie.clear();
        Collections.sort(popTemp); //sortam descrescator
        for(int i=0;i<popTemp.size();i++){ 
            populatie.add(popTemp.get(i)); //pastram doar cei mai buni
        }
    }
    
    @Override
    protected void mutatie() {
        if(populatie.isEmpty()) return;
        popTemp.clear();
        for(Individ c:populatie) { //pentru fiecare individ
            Individ n = new Individ(c);
            for(int i=0;i<nrClienti;i++) { //se ia fiecare cromozom
                if(R.nextInt(100)<probabilitateMutatie) {
                    //se selecteaza random o alta pozitie din cromozom
                    int pos2 = Math.abs(R.nextInt(nrClienti-1)); 
                    //si se face swap
                    int temp = c.cromozom[i];
                    n.cromozom[i] = c.cromozom[pos2];
                    n.cromozom[pos2] = temp;
                }
            }
            popTemp.add(n);
        }
    }
    
    void adauga(Individ best_fit) {
        temp.add(new Individ(best_fit));
    }
    
}
