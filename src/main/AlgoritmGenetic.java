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
    
    /**
     * ID-ul thread-ului in care ruleaza algoritmul. In productie, algoritmul va rula in mai multe thread-uri paralele.
     */
    protected int id;
    /**
     * Numarul de clienti. Optimizare, pentru a nu accesa intruna <i>Client.clienti.size()</i>.
     */
    protected int nrClienti;
    /**
     * Numarul de indivizi per generatie.
     */
    protected int nrIndivizi;
    /**
     * Maximul de generatii ce va rula algoritmul.
     */
    protected int maxGeneratii;
    /**
     * Metoda de selectie.
     * true = se iau primii cei mai buni indivizi, 
     * false = random gaussian dintre cei mai buni indivizi
     */
    protected boolean rapid;
    /**
     * Numarul de camioane. Se initializeaza cu o valoare mai mica, calculata in clasa client
     */
    protected int nrCamioane; 

    /**
     * Viata initiala a indivizilor. Fiecare individ va fi selectabil un anumit numar de generatii
     */
    protected int viataIndivid;

    /**
     * Viata initiala a indivizilor. Cu acest camp se initializeaza indivizii si se foloseste la analiza.
     */
    protected int viataInd; //teste/printare

    /**
     * Semnalizeaza firului curent sa se opreasca
     */
    protected boolean stop;
    
    /**
     * Obiectul folosit la obtinerea numerelor random necesare.
     */
    public static final Random R = new Random();
    /**
     * Populatia folosita in algoritm.
     */
    public final ArrayList<Individ> populatie;
    /**
     * Populatie temporara. Folosit in anumite parti a algoritmului.
     */
    protected final ArrayList<Individ> popTemp;
     
    /**
     * Constructorul clasei.
     * Se intializeaza clasa, dar nu se porneste algoritmul.
     * Pornirea se face prin .start()
     * @param id Int id-ul firului de executie, util pentru afisare si analize
     * @param viteza Int intre 0 si 3, numarul de generatii (500, 2000, 10.000, Infinit)
     * @param viataIndivid int numarul de generatii cat va fi selectabil individul
     * @param nrIndivizi int nr de indivizi per generatie
     */
    public AlgoritmGenetic(int id, int viteza, int viataIndivid) {
        this.nrCamioane = s.nrCamioane();
        this.popTemp = new ArrayList();
        this.populatie = new ArrayList();
        this.id = id;
        this.viataIndivid = viataIndivid;
        this.viataInd = viataIndivid; //folosit pentru teste/printare
        this.nrClienti = Client.clienti.size();
        this.stop = false;
        this.rapid = false;
        switch(viteza) {
            case 0: //rapid
                maxGeneratii = 500; 
                rapid = true; 
                this.nrIndivizi = setari.nrIndivizi;
                break;
            case 1: //mediu
                maxGeneratii = 2000; 
                rapid = true; 
                this.nrIndivizi = setari.nrIndivizi*2;
                break;
            case 2: //lent
                maxGeneratii = 10000; 
                rapid = true; 
                this.nrIndivizi = setari.nrIndivizi*3;
                break;
            default: // case 3: infinit
                maxGeneratii = Integer.MAX_VALUE; 
                rapid = true; 
                this.nrIndivizi = setari.nrIndivizi*4;
                break;
        }
        m.initProgres(maxGeneratii);
    }
    /**
     * Rularea algoritmului.
     * Pornirea se face prin metoda .start() daca se doresc fire de executie.
     */
    @Override
    public void run() {
        System.out.println("Firul "+id+" incepe;");
        double total;
        genereaza_pop_initiala();
        for(Individ c:populatie) {
            c.calculeaza(true);
        }
        Individ best_fit;
        Collections.sort(populatie);
        best_fit = populatie.get(0);
        synchronized(O) {
            if(Individ.best == null) Individ.best=best_fit;
            O.notifyAll();
        }
        System.out.println("Firul "+id+" - terminat generarea populatiei initiale;");
        for(int g=0;g<maxGeneratii;g++) {//main loop
            if(stop) break; //stop? atunci iesi din bucla
            if(g%10==0) {
                m.setProgres(id, g);
                //System.out.println("F "+probabilitateMutatie+"; G "+g);
            }
            recombinare();//
            mutatie();//
            for(Individ i:popTemp) i.calculeaza(true); //calculam fitnessul pentru populatia temporara
            selectie();//
            best_fit = populatie.get(0);
            synchronized(O) {
                if(best_fit.getFitness()<Individ.best.getFitness() && best_fit.ok()==true) { //
                    m.setBest(best_fit,id,id+"G"+g);
                    analiza.adauga(g,viataInd,nrIndivizi,best_fit.getFitness());
                }
                O.notifyAll();
            }
        }
        m.setProgres(id,maxGeneratii);
        System.out.println("Firul "+id+" a finalizat");
    }
    /**
     * Generarea populatiei initiale.
     */
    protected void genereaza_pop_initiala() {
        for(int i=0;i<nrIndivizi;i++) {            
            populatie.add(new Individ(nrClienti,nrCamioane,viataIndivid,true));
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
    protected void recombinare() {
        popTemp.clear();   
        while(popTemp.size()<nrIndivizi){
            //varianta random absolut
            Individ p1 = populatie.get(R.nextInt(populatie.size()));
            Individ p2 = populatie.get(R.nextInt(populatie.size()));
            //varianta random gaussian. test de convergenta.
            //Individ p1 = populatie.get((int)Math.abs((double)nrIndivizi*R.nextGaussian())%nrIndivizi);
            //Individ p2 = populatie.get((int)Math.abs((double)nrIndivizi*R.nextGaussian())%nrIndivizi);
            
            Individ v[] = new Individ[8];
            for(int i=0;i<8;i++){
                v[i] = new Individ(nrClienti,nrCamioane,viataIndivid,false); //false = nu generam cromozomul
            }
            int pct_taiere = R.nextInt(nrClienti-1);
            for(int i=0;i<nrClienti;i++) {
                if(i<pct_taiere) {
                    v[0].cromozom[i] = p1.cromozom[i];
                    v[1].cromozom[i] = p2.cromozom[i];
                    v[2].cromozom[i] = p1.cromozom2[i];
                    v[3].cromozom[i] = p2.cromozom2[i];
                    v[4].cromozom[i] = p1.cromozom[i];
                    v[5].cromozom[i] = p1.cromozom2[i];
                    v[6].cromozom[i] = p2.cromozom[i];
                    v[7].cromozom[i] = p2.cromozom2[i];
                    
                } else {
                    v[0].cromozom[i] = p1.cromozom[i];
                    v[1].cromozom[i] = p2.cromozom[i];
                    v[2].cromozom[i] = p1.cromozom2[i];
                    v[3].cromozom[i] = p2.cromozom2[i];
                    v[4].cromozom[i] = p2.cromozom[i];
                    v[5].cromozom[i] = p2.cromozom2[i];
                    v[6].cromozom[i] = p1.cromozom[i];
                    v[7].cromozom[i] = p1.cromozom2[i];
                }
            }
            for(int i=0;i<8;i++){ //calculam fitness la toatw
                v[i].copiaza();
                v[i].calculeaza(true);
            }
            for(int i=0;i<2;i++) { //le punem doar pe primele 2 cele mai bune
                double min=Double.MAX_VALUE; int m=-1;
                for(int j=0;j<8;j++) {
                    if(v[j]!=null && v[j].getFitness()<min) {
                        min = v[j].getFitness();
                        m=j;
                    }
                }
                popTemp.add(v[m]);
                v[m] = null;
            }
        }       
    }
    /**
     * Mutatia.
     * Se obtine facand swap random intre 2 pachete.
     */
    protected void mutatie() { //mutatia se aplica pe o copie a individului, raman ambele variante
        ArrayList<Individ> temps = new ArrayList();
        int pm;
        for(int i=0;i<popTemp.size();i++) { //pentru fiecare individ din populatia temporara
            Individ n = new Individ(popTemp.get(i));
            pm = procMut(i); //probabilitatea de mutatie
            if(R.nextInt(100)<pm) {
                for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                    int pos1 = R.nextInt(nrClienti-1);
                    int pos2 = R.nextInt(nrClienti-1);
                    int temp1 = n.cromozom[pos1];
                    n.cromozom[pos1] = n.cromozom[pos2];
                    n.cromozom[pos2] = temp1;
                    
                    int temp2 = n.cromozom[pos1];
                    n.cromozom2[pos1] = n.cromozom2[pos2];
                    n.cromozom2[pos2] = temp1;
                    
                    temps.add(n);
                }
            }
        }
        for(int i=0;i<populatie.size();i++) { //pentru fiecare individ din populatia normala
            Individ n = new Individ(populatie.get(i));
            pm = procMut(i); //probabilitatea de mutatie
            if(R.nextInt(100)<pm) {
                for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                    int pos1 = R.nextInt(nrClienti-1);
                    int pos2 = R.nextInt(nrClienti-1);
                    int temp1 = n.cromozom[pos1];
                    n.cromozom[pos1] = n.cromozom[pos2];
                    n.cromozom[pos2] = temp1;
                    
                    int temp2 = n.cromozom[pos1];
                    n.cromozom2[pos1] = n.cromozom2[pos2];
                    n.cromozom2[pos2] = temp1;
                    
                    temps.add(n);
                }
            }
        }
        popTemp.addAll(temps); //ii adaugam in lista de temporari
    }
    /**
     * Selectia. 
     * Se face Random Gaussian absolut (intre 0 si nr max de clienti). In
     * prealabil populatia a fost ordonata descrescator in functie de fitness
     * Prin random Gaussian se favorizeaza indivizii cu fitness mai mare.
     */
    protected void selectie() {
        popTemp.addAll(populatie); //includem si parintii?
        populatie.clear();
        Individ selectat;
        boolean sel;
        Collections.sort(popTemp);
        if(rapid) {
            int i=0;
            while(populatie.size()<nrIndivizi) {
                Individ c = popTemp.get(i);
                if(c.selectabil()==true) { //mai are viata disponibila
                    c.selecteaza(); //va fi selectat
                    populatie.add(c);
                }
                i++;
            }
            popTemp.clear();
        } else {
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
                        sel=true;
                    } 
                    popTemp.remove(rnd);
                }
            }
            popTemp.clear();
        }
        Collections.sort(populatie);
    }
    /**
     * Se calculeaza fitness-ul total al populatiei curente.
     * @return Double fitness-ul total.
     */
    protected double fitnes_total() {
        //System.out.println("Calculez calculeaza total");
        double tot=0.0;
        for(Individ i:populatie) {
            tot += i.calculeaza(true);
        }
        return tot;
    }
    /**
     * Semnalizeaza Thread-ului curent sa se opreasca.
     */
    public void opreste() {
        stop = true;
    }
    /**
     * Returneaza procentul de mutatie pentru indivizul curent.
     * Algoritm Genetic Autoadaptiv (AGA).
     * @param i int Indexul elementului curent
     * @return int procentul de mutatie
     */
    private int procMut(int i) {
        return Setari.procMutatie[((i / popTemp.size())*Setari.procMutatie.length)];
    }
}
