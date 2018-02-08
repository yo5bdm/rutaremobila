/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.util.stream.IntStream.range;
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
     * Semnalizeaza firului curent sa se opreasca.
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
     * Lock-ul pentru operatiile paralelizate.
     */ 
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Autoadaptare adapt;
    /**
     * Constructorul clasei.
     * Se intializeaza clasa, dar nu se porneste algoritmul.
     * Pornirea se face prin .start()
     * @param id Int id-ul firului de executie, util pentru afisare si analize
     * @param viteza Int intre 0 si 3, numarul de generatii (500, 2000, 10.000, Infinit)
     * @param viataIndivid int numarul de generatii cat va fi selectabil individul
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
        adapt = new Autoadaptare(nrClienti, true);
        switch(viteza) {
            case 0: //rapid
                adapt.setIndivizi(1);
                break;
            case 1: //mediu
                adapt.setIndivizi(2);
                break;
            case 2: //lent
                adapt.setIndivizi(3);
                break;
            default: // case 3: infinit
                adapt.setMaxGen();
                adapt.setIndivizi(4);
                break;
        }
        m.initProgres(adapt.getMaxGeneratii());
    }
    /**
     * Rularea algoritmului.
     * Pornirea se face prin metoda .start() daca se doresc fire de executie.
     */
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        //fis.add("Fisier generat in "); //yyyy/MM/dd
        System.out.println("Firul "+id+" incepe; "+dateFormat.format(new Date()));
        double total;
        genereazaPopulatiaInitiala();
        System.out.println("Firul "+id+" - terminat generarea populatiei initiale;");
        Individ best_fit;
        Collections.sort(populatie);
        best_fit = populatie.get(0);
        synchronized(O) {
            if(Individ.best == null) Individ.best=best_fit;
            O.notifyAll();
        }
        double totFitness10gen=best_fit.getFitness();
        while(adapt.continua()) {//main loop
            if(stop) break; //stop? atunci iesi din bucla
            if(adapt.zeceGeneratii()) {
                System.gc();
                totFitness10gen = best_fit.getFitness();
                m.setProgres(id, adapt.getGeneratia());
                System.out.println("F "+id+"; G "+adapt.getGeneratia()+"; P "+adapt.nrIndivizi+"; rapid = "+adapt.rapid+"; "+((System.currentTimeMillis()-start)/1000)+"s / 10gen ");
                start = System.currentTimeMillis();
                adapt.boost();
                m.modMax(adapt.getMaxGeneratii());
            }
            adapt.adaptare();
            //algoritmul efectiv
            recombinare();
            mutatie();
            selectie();
            //verificarea celui mai bun
            best_fit = populatie.get(0);
            synchronized(O) {
                if(best_fit.getFitness()<Individ.best.getFitness()-1 && best_fit.ok()==true) { //
                    m.setBest(best_fit,id,adapt.getGeneratia(),id+"G"+adapt.getGeneratia());
                    analiza.adauga(adapt.getGeneratia(),viataInd,adapt.nrIndivizi,best_fit.getFitness());
                    adapt.evolutie();
                }
                O.notifyAll();
            }
            adapt.faraEvolutie();
        }
        m.setProgres(id,adapt.getMaxGeneratii());
        System.out.println("Firul "+id+" a finalizat");
    }
    /**
     * Generarea populatiei initiale.
     */
    protected void genereazaPopulatiaInitiala() {       
        range(0,adapt.nrIndivizi).parallel().forEach(i -> { //
            Individ n;
            n = new Individ(nrClienti,nrCamioane,viataIndivid,true);
            n.calculeaza(true);
            lock.writeLock().lock();
            populatie.add(n);
            lock.writeLock().unlock();
        });        
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
        int popSize = populatie.size()-1;
        range(0, (int) (adapt.nrIndivizi)).parallel().forEach(x -> { //.parallel()//while(popTemp.size()<nrIndivizi){
            int[] puncteTaiere = new int[2];
            int rndGet1, rndGet2;
            if(adapt.rapid) {
                rndGet1 = R.nextInt(popSize/3);
                rndGet2 = popSize - rndGet1; //ia al doilea din partea opusa primului
            } else {
                rndGet1 = R.nextInt(popSize);
                rndGet2 = R.nextInt(popSize); //ia al doilea din vecinatatea primului
            }
            lock.readLock().lock();
            Individ p1 = populatie.get(rndGet1);
            Individ p2 = populatie.get(rndGet2);
            lock.readLock().unlock();
            Individ v[] = new Individ[8];
            for(int i=0;i<8;i++){
                v[i] = new Individ(nrClienti,nrCamioane,viataIndivid,false); //false = nu generam cromozomul
            }
            puncteTaiere[0] = R.nextInt(nrClienti-1);//2 pct de taiere
            puncteTaiere[1] = R.nextInt(nrClienti-1);
            while(puncteTaiere[0] == puncteTaiere[1]) { //avem grija sa nu fie egale
                puncteTaiere[1] = R.nextInt(nrClienti-1);
            }
            if(puncteTaiere[0] > puncteTaiere[1]) { //sa fie in ordine crescatoare
                int temp = puncteTaiere[0];
                puncteTaiere[0] = puncteTaiere[1];
                puncteTaiere[1] = temp;
            }
            for(int i=0;i<nrClienti;i++) { //crossoverul efectiv
                if(i<puncteTaiere[0] || i>puncteTaiere[1] ) { //
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
                lock.writeLock().lock();
                popTemp.add(v[m]);
                lock.writeLock().unlock();
                v[m] = null;
            }
        });       
    }
    /**
     * Mutatia.
     * Se obtine facand swap random intre 2 pachete.
     */
    protected void mutatie() { //mutatia se aplica pe o copie a individului, raman ambele variante
        ArrayList<Individ> temps = new ArrayList();
        range(0,popTemp.size()).parallel().forEach(i -> { //pentru fiecare individ din populatia temporara
            Individ n;
            n = new Individ(popTemp.get(i));
            int pm = (int) (adapt.fs*procMut(i)); //probabilitatea de mutatie
            for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                int pos1 = R.nextInt(nrClienti-1);
                int pos2 = R.nextInt(nrClienti-1);
                int temp1 = n.cromozom[pos1];
                n.cromozom[pos1] = n.cromozom[pos2];
                n.cromozom[pos2] = temp1;
                int temp2 = n.cromozom[pos1];
                n.cromozom2[pos1] = n.cromozom2[pos2];
                n.cromozom2[pos2] = temp1;
            }
            n.calculeaza(true);
            lock.writeLock().lock();
            temps.add(n);
            lock.writeLock().unlock();
        });
        if(!adapt.rapid) range(0,populatie.size()).parallel().forEach(i -> {//pentru fiecare individ din populatia normala
            Individ n = new Individ(populatie.get(i));
            int pm = (int) (procMut(i)); //probabilitatea de mutatie
            for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                int pos1 = R.nextInt(nrClienti-1);
                int pos2 = R.nextInt(nrClienti-1);
                int temp1 = n.cromozom[pos1];
                n.cromozom[pos1] = n.cromozom[pos2];
                n.cromozom[pos2] = temp1;
                int temp2 = n.cromozom[pos1];
                n.cromozom2[pos1] = n.cromozom2[pos2];
                n.cromozom2[pos2] = temp1;
            }
            n.calculeaza(true);
            lock.writeLock().lock();
            temps.add(n);
            lock.writeLock().unlock();
        });
        popTemp.addAll(temps); //ii adaugam in lista de temporari
    }
    /**
     * Selectia. 
     * Se face Random Gaussian absolut (intre 0 si nr max de clienti). In
     * prealabil populatia a fost ordonata descrescator in functie de fitness
     * Prin random Gaussian se favorizeaza indivizii cu fitness mai mare.
     */
    protected void selectie() {
//        if(adapt.rapid){
            popTemp.addAll(populatie);
//        } //includem si parintii?
        populatie.clear();
        Individ selectat;
        boolean sel;
        Collections.sort(popTemp);
        if(adapt.rapid) {
            int i=0, size=0; //i->individul curent din populatia temporara, size = populatie.size()
            while(size < adapt.nrIndivizi) {
                Individ c = popTemp.get(i);
                if(c.selectabil()==true) { //mai are viata disponibila
                    c.selecteaza(); //va fi selectat
                    populatie.add(c);
                    size++;
                }
                i++;
            }
            popTemp.clear();
        } else {
            for(int i=0;i<adapt.nrIndivizi;i++) {
                if(popTemp.isEmpty()) {
                    break;
                }
                sel=false;
                while(sel == false) {
                    //random gausian absolut intre 0 si nrIndivizi
                    int rnd = (int)Math.abs((double)popTemp.size()*(R.nextGaussian()%5))%popTemp.size(); 
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
        return Setari.procMutatie[((i / popTemp.size())*Setari.procMutatie.length)%9];
    }
}
