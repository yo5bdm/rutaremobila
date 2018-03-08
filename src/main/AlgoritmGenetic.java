/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    public final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final Autoadaptare adapt;
    private double dx;
    /**
     * Constructorul clasei.
     * Se intializeaza clasa, dar nu se porneste algoritmul.
     * Pornirea se face prin .start()
     * @param id Int id-ul firului de executie, util pentru afisare si analize
     * @param viteza Int intre 0 si 3, numarul de generatii (500, 2000, 10.000, Infinit)
     * @param viataIndivid int numarul de generatii cat va fi selectabil individul
     */
    public AlgoritmGenetic(int id, int viteza) {
        adapt = new Autoadaptare(nrClienti, true, this);
        this.nrCamioane = s.nrCamioane();
        this.popTemp = new ArrayList();
        this.populatie = new ArrayList();
        this.id = id;
        this.viataIndivid = adapt.viataIndivid;
        this.viataInd = viataIndivid; //folosit pentru teste/printare
        this.nrClienti = Client.clienti.size();
        this.stop = false;
        switch(viteza) {
            case 0: //rapid
                adapt.setIndivizi(2);
                break;
            case 1: //mediu
                adapt.setIndivizi(3);
                break;
            case 2: //lent
                adapt.setIndivizi(4);
                break;
            default: // case 3: infinit
                adapt.setMaxGen();
                adapt.setIndivizi(5);
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
        try {
            long start = System.currentTimeMillis();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            m.setStatus("Genereaza populatia initiala "+dateFormat.format(new Date()));
            genereazaPopulatiaInitiala();
            m.setStatus("Terminat generarea populatiei initiale;");
            Collections.sort(populatie);
            Individ localBestFit = populatie.get(0);
            Individ.best=localBestFit;
            double totFitness10gen=localBestFit.getFitness();
            String rapid="";
            m.setStatus("Incepe algoritmul...");
            while(adapt.continua()) {//main loop
                if(stop) break; //stop? atunci iesi din bucla
                if(adapt.zeceGeneratii()) {
                    System.gc();
                    totFitness10gen = localBestFit.getFitness();
                    m.setProgres(id, adapt.getGeneratia());
                    adapt.boost();
                    if(adapt.rapid) {
                        rapid = "rapid";
                    } else {
                        rapid = "lent";
                    }
                    m.setStatus("G"+adapt.getGeneratia()+";P"+adapt.nrIndivizi+"; "+rapid+"; "+((System.currentTimeMillis()-start)/1000)+"s; Best "+(int)populatie.get(0).getFitness()+"; dx="+(int)adapt.lastDx);
                    start = System.currentTimeMillis();
                    m.modMax(adapt.getMaxGeneratii());
//                    long startOpt = System.currentTimeMillis();
//                    dx=0;
//                    range(0, populatie.size()).parallel().forEach((int ppp)->{
//                        Individ ind = new Individ(populatie.get(ppp));
//                        lock.writeLock().lock();
//                        dx+=ind.optimizeaza();
//                        lock.writeLock().unlock();
//                    });
//                    System.out.println("Opt> "+(System.currentTimeMillis()-startOpt)+" "+(int)dx);
                }
                adapt.adaptare(populatie.get(populatie.size()-1).getFitness()-populatie.get(0).getFitness());
                //algoritmul efectiv
                recombinare();
                mutatie();
                selectie();
                //verificarea celui mai bun
                localBestFit = populatie.get(0);
                //System.out.println(""+localBestFit);
                if(localBestFit.getFitness()<Individ.best.getFitness()-1 && localBestFit.ok()==true) {
                    m.setBest(localBestFit,id,adapt.getGeneratia(),id+"G"+adapt.getGeneratia());
                    analiza.adauga(adapt.getGeneratia(),viataInd,adapt.nrIndivizi,localBestFit.getFitness());
                    adapt.evolutie();
                } else {
                    adapt.faraEvolutie();
                }
            }
            m.setProgres(id,adapt.getMaxGeneratii());
            m.setStatus("Algoritmul a finalizat");
        } catch(IllegalArgumentException e){
            MainFrame.mesajEroare("A fost intampinata o eroare in algoritm "+e);
            System.out.println("Aici "+e);
        }
    }
    /**
     * Generarea populatiei initiale.
     */
    protected void genereazaPopulatiaInitiala() {       
        range(0,adapt.nrIndivizi).parallel().forEach((int i) -> { //
            Individ n = new Individ(nrClienti,nrCamioane,viataIndivid,true);
            n.calculeaza(true);
            //n = Individ.annealing(n,1000);
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
        int nrPuncteTaiere = adapt.nrPuncteTaiere;
        range(0,adapt.nrIndivizi/2).parallel().forEach((int x) -> {
            int[] puncteTaiere = new int[nrPuncteTaiere];
            int rndGet1, rndGet2;
            while(true) {
                switch(x%3) {
                    case 0:
                        rndGet1 = R.nextInt(popSize/2);
                        rndGet2 = popSize - rndGet1; //ia al doilea din partea opusa primului
                        break;
                    case 1:
                        rndGet1 = R.nextInt(popSize);
                        int nextGaussian = (int)(R.nextGaussian()*10);
                        if(nextGaussian == 0) {
                            nextGaussian = 1;
                        }
                        rndGet2 = rndGet1+nextGaussian; //ia al doilea din vecinatatea primului
                        break;
                    case 2:
                    default:
                        rndGet1 = R.nextInt(popSize);
                        rndGet2 = R.nextInt(popSize); //full random
                        break;
                }
                if(rndGet1>=0 && rndGet1 <=popSize && rndGet2>=0 && rndGet2 <=popSize) {
                    break;
                }
            }
            if(x<5) {
                rndGet1 = 0;
            }
            lock.readLock().lock();
            Individ parinte1 = populatie.get(rndGet1);
            Individ parinte2 = populatie.get(rndGet2);
            lock.readLock().unlock();
            
            Individ[] copii = new Individ[8];
            for(int i=0;i<8;i++){
                copii[i] = new Individ(nrClienti,nrCamioane,viataIndivid,false); //false = nu generam cromozomul
            }
            //punctele de taiere
            for(int i=0;i<nrPuncteTaiere;i++) {
                puncteTaiere[i] = R.nextInt(nrClienti-1);
            }
            Arrays.sort(puncteTaiere);
            boolean direct = true;
            int punctulSelectat=0;
            for(int i=0;i<nrClienti;i++) { //crossoverul efectiv
                if(punctulSelectat<nrPuncteTaiere && i>=puncteTaiere[punctulSelectat]) {
                    direct=!direct;
                    punctulSelectat++;
                }
                if(direct) { 
                    copii[0].setCromozomVal(i,parinte1.getCromozom1Val(i));
                    copii[1].setCromozomVal(i,parinte2.getCromozom1Val(i));
                    
                    copii[2].setCromozomVal(i,parinte1.getCromozom2Val(i));
                    copii[3].setCromozomVal(i,parinte2.getCromozom2Val(i));
                    
                    copii[4].setCromozomVal(i,parinte1.getCromozom1Val(i));
                    copii[5].setCromozomVal(i,parinte1.getCromozom2Val(i));
                    copii[6].setCromozomVal(i,parinte2.getCromozom1Val(i));
                    copii[7].setCromozomVal(i,parinte2.getCromozom2Val(i));
                } else {
                    copii[0].setCromozomVal(i,parinte2.getCromozom1Val(i));
                    copii[1].setCromozomVal(i,parinte1.getCromozom1Val(i));
                    
                    copii[2].setCromozomVal(i,parinte2.getCromozom2Val(i));
                    copii[3].setCromozomVal(i,parinte1.getCromozom2Val(i));
                    
                    copii[4].setCromozomVal(i,parinte2.getCromozom2Val(i));
                    copii[5].setCromozomVal(i,parinte2.getCromozom1Val(i));
                    copii[6].setCromozomVal(i,parinte1.getCromozom2Val(i));
                    copii[7].setCromozomVal(i,parinte1.getCromozom1Val(i));
                }
            }
            for(int i=0;i<8;i++){ //calculam fitness la toate
                copii[i].calculeaza(true);
            }
            Arrays.sort(copii);
            lock.writeLock().lock();
            popTemp.add(copii[0]);
            popTemp.add(copii[1]);
            lock.writeLock().unlock();
        });       
    }
    /**
     * Mutatia.
     * Se obtine facand swap random intre 2 pachete.
     */
    protected void mutatie() { //mutatia se aplica pe o copie a individului, raman ambele variante
        ArrayList<Individ> temps = new ArrayList();
        range(0,popTemp.size()).parallel().forEach(i -> { //pentru fiecare individ din populatia temporara
            lock.readLock().lock();
            Individ n = new Individ(popTemp.get(i)),nou,bak;
            lock.readLock().unlock();
            double startFitness = n.getFitness();
            int pm = adapt.procMut(i, popTemp.size()); //probabilitatea de mutatie
            bak = new Individ(n);
            for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                int pos1 = R.nextInt(nrClienti-1);
                int pos2 = R.nextInt(nrClienti-1);
                nou = new Individ(bak);
                nou.swap(pos1, pos2);
                n.swap(pos1,pos2);
                nou.calculeaza(true);
                if(nou.getFitness()<bak.getFitness()) {
                    bak = nou;
                }
            }
            n.calculeaza(true);
            lock.writeLock().lock();
            temps.add(n);
            if(bak.getFitness()<startFitness){
                temps.add(bak);
            }
            lock.writeLock().unlock();
        });
        if(!adapt.rapid) range(0,populatie.size()).parallel().forEach(i -> {
            lock.readLock().lock();
            Individ n = new Individ(populatie.get(i)), nou;
            lock.readLock().unlock();
            int pm = 100; //numarul de mutatii aplicate
            for(int j=0;j<pm;j++) { //nr de mutatii depind de probabilitate
                int pos1 = R.nextInt(nrClienti-1);
                int pos2 = R.nextInt(nrClienti-1);
                nou = new Individ(n);
                nou.swap(pos1,pos2);
                if(nou.getFitness()<n.getFitness()){
                    n = nou;
                }
            }
            if(n.getFitness()<populatie.get(i).getFitness()) {
                lock.writeLock().lock();
                temps.add(n);
                lock.writeLock().unlock();
            }
        });
        popTemp.addAll(temps); //ii adaugam in lista de temporari
    }
    /**
     * Selectia. 
     * Se face Random Gaussian absolut (intre 0 si nr max de indivizi). In
     * prealabil populatia a fost ordonata descrescator in functie de fitness
     * Prin random Gaussian se favorizeaza indivizii cu fitness mai mare.
     */
    protected void selectie() {
        popTemp.addAll(populatie);
        populatie.clear();
        int nrAdaugareDirecta = 20;
        Collections.sort(popTemp);
        for(int i=0;i<nrAdaugareDirecta;) {
            if(popTemp.get(0).selectabil()==true){
                populatie.add(popTemp.get(0));
                i++;
            }
            popTemp.remove(0);
        }
        if(adapt.selection == 1) {
            int i=0, size=0; //i->individul curent din populatia temporara, size = populatie.size()
            Individ c;
            while(size < adapt.nrIndivizi-nrAdaugareDirecta) {
                c = popTemp.get(i);
                if(c.selectabil()==true) { //mai are viata disponibila
                    c.selecteaza(); //va fi selectat
                    populatie.add(c);
                    size++;
                }
                i++;
            }
            popTemp.clear();
        } else if(adapt.selection == 2) {
            Individ selectat;
            boolean sel;
            for(int i=0;i<adapt.nrIndivizi-nrAdaugareDirecta;i++) {
                if(popTemp.isEmpty()) {
                    break;
                }
                sel=false;
                while(sel == false) {
                    //random gausian absolut intre 0 si nrIndivizi
                    int rnd = (int)Math.abs((double)popTemp.size()*(R.nextGaussian()/5))%popTemp.size(); 
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
        } else {
            int i, size=0; //i->individul curent din populatia temporara, size = populatie.size()
            Individ c;
            while(size < adapt.nrIndivizi-nrAdaugareDirecta) {
                i = R.nextInt(popTemp.size()-1);
                c = popTemp.get(i);
                if(c.selectabil()==true) { //mai are viata disponibila
                    c.selecteaza(); //va fi selectat
                    populatie.add(c);
                    popTemp.remove(i);
                    size++;
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
    
    private void anneal(int pasi, int nrAnn) {
        long start = System.currentTimeMillis();
        ArrayList<Integer> tabAnn = new ArrayList();
        range(0,nrAnn).parallel().forEach(ann->{ //de la inceputul listei (adica cei mai buni)
            lock.readLock().lock();
            Individ n = populatie.get(ann);
            lock.readLock().unlock();
            Individ nou = Individ.annealing(n,pasi);
            if(nou != null) {
                lock.writeLock().lock();
                populatie.add(nou);
                lock.writeLock().unlock();
                int rez = (int)(n.getFitness()-nou.getFitness());
                tabAnn.add(new Integer(rez));
            }
        });
        Collections.sort(populatie);
        adapt.annDx =0;
        if(tabAnn.size()>0){
            for(int i:tabAnn) {
                adapt.annDx += i;
            }
            adapt.annDx /= tabAnn.size();
        }
        m.setStatus("ann> "+adapt.annDx+" "+((System.currentTimeMillis()-start)/1000)+"s");
        adapt.enableAnnealing=false;
    }
    
}
