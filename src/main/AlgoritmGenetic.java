/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import static main.MainFrame.*;

/**
 * Algoritmul genetic.
 * Initializarea se face cu nr de clienti, viteza (reprezentand numarul de 
 * generatii) si probabilitatea de mutatie.
 * Din teste, am observat ca probabilitatea de mutatie de 0% ofera cei mai 
 * buni indivizi, per total.
 * Clasa este implementata ca fir de executie, putandu-se rula mai multe fire 
 * simultan, cu probabilitati de mutatie diferite.
 * @author yo5bdm
 */
public class AlgoritmGenetic extends Thread {
    
    private int nrClienti;
    private int nrIndivizi=s.memorie;
    private int probabilitateMutatie; //5 = 5%
    private int maxGeneratii;
    private int nrCamioane=48; //pentru initializare
    private boolean stop;
    
    public static final Random R = new Random();
    public final ArrayList<Individ> populatie = new ArrayList();
    private final ArrayList<Individ> popTemp = new ArrayList();
     
    /**
     * Constructorul clasei.
     * Se intializeaza clasa, dar nu se porneste algoritmul.
     * Pornirea se face prin .start()
     * @param size Int numarul de clienti.
     * @param viteza Int intre 0 si 3, numarul de generatii (500, 2000, 10.000, Infinit)
     * @param probMutatie Int probabilitatea de mutatie 5 reprezinta 5%
     */
    public AlgoritmGenetic(int size,int viteza,int probMutatie) {
        this.nrClienti = size;
        this.probabilitateMutatie = probMutatie;
        populatie.clear();
        popTemp.clear();
        stop = false;
        switch(viteza) {
            case 0: //rapid
                maxGeneratii = 500;
                break;
            case 1: //mediu
                maxGeneratii = 2000;
                break;
            case 2: //lent
                maxGeneratii = 10000;
                break;
            default: // case 3: infinit
                maxGeneratii = Integer.MAX_VALUE;
                break;
        }
        m.initProgres(maxGeneratii);
    }
    /**
     * Rularea algoritmului.
     * Pornirea se face prin metoda .start() daca se doresc fire de executie.
     */
    public void run() {
        double total;
        genereaza_pop_initiala(nrIndivizi,nrCamioane);//
        for(Individ c:populatie) {
            c.calculeaza(true);
        }
        Individ best_fit;
        Collections.sort(populatie);
        best_fit = populatie.get(0);
        if(Individ.best == null) Individ.best=best_fit;
        for(int g=0;g<maxGeneratii;g++) {//main loop
            if(stop) break; //stop? atunci iesi din bucla
            if(g%5==0) {
                m.setProgres(g);
                System.out.println("Firul "+probabilitateMutatie+" gen "+g);
            }
            recombinare();//
            if(probabilitateMutatie > 0) mutatie();//
            for(Individ i:popTemp) i.calculeaza(true); //calculam fitnessul pentru populatia temporara
            selectie();//
            Collections.sort(populatie);
            best_fit = populatie.get(0);
            synchronized(O) {
                if(best_fit.getFitness()<Individ.best.getFitness() && best_fit.ok()==true) { //
                    System.out.println("Mutatie "+probabilitateMutatie+", Generatia "+g);
                    System.out.println(best_fit);
                    m.setBest(best_fit,g,probabilitateMutatie);
                }
                O.notifyAll();
            }
        }
        m.setProgres(maxGeneratii);
        System.out.println("Firul "+probabilitateMutatie+"% a finalizat");
    }
    /**
     * Generarea populatiei initiale
     * @param nr_indivizi Int numarul de indivizi doriti
     * @param nr_camioane Int numarul initial de camioane
     */
    private void genereaza_pop_initiala(int nr_indivizi, int nr_camioane) {
        for(int i=0;i<nr_indivizi;i++) {            
            populatie.add(new Individ(nrClienti,nr_camioane,true));
        }        
    }
    /**
     * Recombinarea.
     * Avand in vedere ca se folosesc algoritmi diploizi, exista 2 cromozomi
     * unul optimizat (.cromozom) si unul neoptimizat (.cromozom2)
     * combinarea se face folosind acelasi punct de taiere si generand toate
     * combinatiile posibile
     * optimizat - optimizat
     * neoptimizat - neoptimizat
     * optimizat - neoptimizat
     */
    private void recombinare() {
        popTemp.clear();
        if(probabilitateMutatie>20) { //nu mai facem recombinare in cazul asta
            popTemp.addAll(populatie);
            return;
        }        
        while(popTemp.size()<nrIndivizi){
            Individ p1 = populatie.get(R.nextInt(populatie.size()));
            Individ p2 = populatie.get(R.nextInt(populatie.size()));
            
            //copiii din cromozomii strict optimizati
            Individ a = new Individ(nrClienti,nrCamioane,false); //false = nu generam cromozomul
            Individ b = new Individ(nrClienti,nrCamioane,false);
            //copiii din cromozomii strict neoptimizati
            Individ c = new Individ(nrClienti,nrCamioane,false);
            Individ d = new Individ(nrClienti,nrCamioane,false);
            //copii din cromozomii combinati
            Individ p1p2_1 = new Individ(nrClienti,nrCamioane,false);
            Individ p1p2_2 = new Individ(nrClienti,nrCamioane,false);
            Individ p2p1_1 = new Individ(nrClienti,nrCamioane,false);
            Individ p2p1_2 = new Individ(nrClienti,nrCamioane,false);
            int pct_taiere = R.nextInt(nrClienti-1);
            for(int i=0;i<nrClienti;i++) {
                if(i<pct_taiere) {
                    a.cromozom[i] = p1.cromozom[i];
                    b.cromozom[i] = p2.cromozom[i];
                    c.cromozom[i] = p1.cromozom2[i];
                    d.cromozom[i] = p2.cromozom2[i];
                    p1p2_1.cromozom[i] = p1.cromozom[i];
                    p1p2_2.cromozom[i] = p1.cromozom2[i];
                    p2p1_1.cromozom[i] = p2.cromozom[i];
                    p2p1_2.cromozom[i] = p2.cromozom2[i];
                    
                } else {
                    b.cromozom[i] = p1.cromozom[i];
                    a.cromozom[i] = p2.cromozom[i];
                    c.cromozom[i] = p1.cromozom2[i];
                    d.cromozom[i] = p2.cromozom2[i];
                    p1p2_1.cromozom[i] = p2.cromozom[i];
                    p1p2_2.cromozom[i] = p2.cromozom2[i];
                    p2p1_1.cromozom[i] = p1.cromozom[i];
                    p2p1_2.cromozom[i] = p1.cromozom2[i];
                }
            }
            a.copiaza();
            b.copiaza();
            c.copiaza();
            d.copiaza();
            p1p2_1.copiaza();
            p1p2_2.copiaza();
            p2p1_1.copiaza();
            p2p1_2.copiaza();
            popTemp.add(a);
            popTemp.add(b);
            popTemp.add(c);
            popTemp.add(d);
            popTemp.add(p1p2_1);
            popTemp.add(p1p2_2);
            popTemp.add(p2p1_1);
            popTemp.add(p2p1_2);
        }       
    }
    /**
     * Mutatia.
     * Se obtine facand swap random intre 2 pachete.
     */
    private void mutatie() {
        for(Individ c:popTemp) { //pentru fiecare individ
            for(int i=0;i<nrClienti;i++) { //se ia fiecare cromozom
                if(R.nextInt(100)<probabilitateMutatie) {
                    //se selecteaza random o alta pozitie din cromozom
                    int pos2 = R.nextInt(nrClienti-1); 
                    //si se face swap
                    int temp = c.cromozom[i];
                    c.cromozom[i] = c.cromozom[pos2];
                    c.cromozom[pos2] = temp;
                    //System.out.println("Mutatie...");
                }
            }
        }
    }
    /**
     * Selectia. 
     * Se face Random Gaussian absolut (intre 0 si nr max de clienti). In
     * prealabil populatia a fost ordonata descrescator in functie de fitness
     * Prin random Gaussian se favorizeaza indivizii cu fitness mai mare.
     */
    private void selectie() {
        popTemp.addAll(populatie); //includem si parintii?
        populatie.clear();
        Individ selectat;
        boolean sel;
        Collections.sort(popTemp);
//        for(int i=0;i<nrIndivizi;i++){
//            populatie.add(popTemp.get(i));
//        }
        for(int i=0;i<this.nrIndivizi;i++) {
            sel=false;
            while(sel == false) {
                //random gausian absolut intre 0 si nrIndivizi
                int rnd = (int)Math.abs((double)popTemp.size()*R.nextGaussian())%popTemp.size(); 
                //selecteaza individul dupa randomul de mai sus si adauga in pop temp
                selectat = popTemp.get(rnd);
                if(selectat.selectabil()==true) { //mai are viata disponibila
                    selectat.selecteaza(); //va fi selectat
                    populatie.add(selectat);
                    popTemp.remove(rnd);
                    sel=true;
                }
            }
        }
    }
    /**
     * Se calculeaza fitness-ul total al populatiei curente.
     * @return Double fitness-ul total.
     */
    private double fitnes_total() {
        //System.out.println("Calculez calculeaza total");
        double tot=0.0;
        for(Individ i:populatie) {
            tot += i.calculeaza(true);
        }
        return tot;
    }

    void opreste() {
        stop = true;
    }
}
