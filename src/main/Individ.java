/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */ 
package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static java.util.stream.IntStream.range;
import static main.AlgoritmGenetic.R;
import static main.MainFrame.setari;

/**
 * Individul folosit in algoritmul genetic.
 * Functionarea: cromozomul tine toate datele astfel:
 * Indexul reprezinta pachetul/clientul ce urmeaza sa fie livrat.
 * Valoarea de la un anumit index reprezinta camionul in care este incarcat pachetul.
 * Reprezentarea aceasta permite ca mai multe obiecte sa fie incarcate pe un camion, 
 * dar un obiect nu poate fi incarcat pe mai multe camioane.
 * cromozom2 - copia neoptimizata a cromozomului principal. Folosindu-se cromozomi
 * diploizi, se ajunge la indivizi mai buni.
 * Am implementat limitarea vietii indivizilor buni, fiecare poate "trai"
 * maxim <i>"viata"</i> generatii.
 * Valori posibile:
 * - orice valoare pozitiva, inclusiv zero: numarul camionului pe care urmeaza sa fie incarcat
 * - -1: pachet neincarcat
 * - -2: pachet neincarcabil, depaseste volumul camioanelor disponibile
 * @author yo5bdm
 */
public class Individ implements Comparable {
    /* PUBLIC STATIC */
    /**
     * Individul cel mai bun ajunge aici.
     */
    public static Individ best;
    /**
     * Numarul de camioane trimise cu pachetele mari.
     * Se foloseste la calculul final
     */
    public static int celeMariNrCamioane;
    /**
     * Distanta totala parcursa de cele mari.
     */
    public static double celeMariDist;
    /**
     * Pachetele mari care nu incap in camioane.
     */
    public static ArrayList<String> celeMari = new ArrayList(); //String-urile folosite la imprimare in fisier
    
    /* PUBLIC NESTATIC */
    /**
     * Lista de camioane. Fiecare isi cunoaste pachetele incarcate, distantaTotala totala, etc.
     */
    public ArrayList<Camion> camioane = new ArrayList();
    /**
     * Viata totala cu care a fost initializat individul. 
     * Se foloseste la afisare si analiza.
     */
    public int viataGen;
    /* PRIVATE */
    /**
     * Cromozomul principal, optimizat.
     * Indexul e obiectul de incarcat in camion. 
     * Valoarea e numarul camionului pe care e incarcat obiectul
     */
    private Integer[] cromozom1;
    /**
     * Copia neoptimizata a cromozomului principal
     */
    private Integer[] cromozom2;
    private int nrClienti;
    private Double fitness; //distanta totala parcursa
    private Double distantaIntern;
    private Integer nrCamioane;
    private CamionDisponibil camionDisponibil; //obiectul folosit pentru aflarea a ce camion e disponibil
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); //lock pentru parallel()
    boolean optimizat;
    private static final int NEINCARCAT = -1;
    private static final int NEINCARCABIL = -2;
    private int viata;
    
    public Genealogie genealogie;
            
    /**
     * Constructor.
     * @param nrClienti int nr de clienti
     * @param viataIndivid int numarul de generatii ce va fi selectabil individul
     * @param nrCamioane numarul de camioane
     * @param generare Boolean true daca se doreste generarea random a individului
     */    
    public Individ(int nrClienti, int nrCamioane, int viataIndivid, boolean generare) {
        genealogie = new Genealogie();
        this.nrClienti = nrClienti;
        this.viata = viataIndivid;
        this.viataGen = viataIndivid;
        cromozom1 = new Integer[nrClienti];
        cromozom2 = new Integer[nrClienti];
        camionDisponibil = new CamionDisponibil();
        this.nrCamioane = nrCamioane;
        if(generare == true) {
            for(int j=0;j<nrClienti;j++) {
                setCromozomVal(j,R.nextInt(nrCamioane-1));
            }
        }
        for(int i=0;i<nrCamioane;i++) {
              camioane.add(new Camion(camionDisponibil.cautaLiber()));
        }
        optimizat = false;
        if(generare == true) calculeaza(true);
    }
    /**
     * Constructorul de copiere.
     * @param b Individul (obiectul) de copiat
     */
    Individ(Individ b) {
        this(b.cromozom1.length, b.nrCamioane, b.viataGen, false);
        this.genealogie.copiaza(b.genealogie);
        for(int i=0;i<b.cromozom1.length;i++) {
            this.cromozom1[i] = b.cromozom1[i];
            this.cromozom2[i] = b.cromozom2[i];
        }
        optimizat = false;
    }
    
    public void setParinti(Individ a, Individ b) {
        this.genealogie.setParinti(a.genealogie, b.genealogie);
    }
    
    public void setCromozomVal(int pozitie, int valoare) {
        cromozom1[pozitie] = valoare;
        cromozom2[pozitie] = valoare;
    }
    
    public int getCromozom1Val(int pozitie) {
        return cromozom1[pozitie];
    }
    public int getCromozom2Val(int pozitie) {
        return cromozom2[pozitie];
    }
    /**
     * Metoda pregateste o noua copiere din cromozom1. Goleste toate camioanele 
     * si ajusteaza numarul total, daca e necesar
     */
    private void reset_camioane() {
        for(Camion c:camioane) c.reset();
        if(camioane.size()<nrCamioane) {
            int nr = nrCamioane-camioane.size();
            for(int i=0;i<nr;i++) {
                camioane.add(new Camion(camionDisponibil.cautaLiber()));
            }
        }
        nrCamioane = camioane.size();
        optimizat=false;
    }
    /**
     * Returneaza fitness-ul populatiei.
     * @return Double, valoarea fitness-ului total al populatiei
     */
    public double getFitness() {
        if(optimizat) {
            return fitness;
        } else {
            calculeaza(true);
            return fitness;
        }
    }
    
    public double totalDistantaInterna() {
        double tot=0;
        for(Camion c:camioane) {
            tot += c.distantaInterna();
        }
        return tot;
    }
    /**
     * Calculeaza fitness-ul individului curent.
     * @param optimizeaza Boolean true daca se doreste optimizarea
     * @return Double fitness-ul individului
     */
    public double calculeaza(boolean optimizeaza) {
        int max=0;
        for(int i:cromozom1) { //gasim maxim, sa stim cate camioane avem nevoie.
            if(i>max) {
                max=i;
            }
        } 
        nrCamioane = max+1;
        reset_camioane();
        
        for(int i=0;i<cromozom1.length;i++) {
            switch(cromozom1[i]) {
                case NEINCARCABIL: //incercam sa plasam si neplasabilele
                    cromozom1[i]=NEINCARCAT;
                    break;
                case NEINCARCAT: //nu se trateaza aici
                    break;
                default: //se adauga pachetul curent in camionul aferent
                    camioane.get(cromozom1[i]).add(i); 
            }
        }
        if(optimizeaza) optimizeLoads();//se optimizeaza incarcarile
        fitness = 0.0;
        distantaIntern = 0.0;
        //calculam datele fiecarui camion si fitness-ul total al generatiei
        range(0,camioane.size()).parallel().forEach(i -> {
            lock.readLock().lock();
            Camion c = camioane.get(i);
            lock.readLock().unlock();
            double fit;
            if(c!=null) {
                fit = c.calc();
                lock.writeLock().lock();
                fitness += fit;
                distantaIntern+=c.distantaInterna();
                lock.writeLock().unlock();
            } 
        });
        //fiecare neincarcabil scade fitnesul total cu 10k
        for(int i=0;i<neincarcabile();i++){
            fitness += 9999.0;
        }
        optimizat = true;
        return fitness;
    }
    /**
     * Numara pachetele neincarcate.
     * @return int - numarul de pachete neincarcate
     */
    private int neincarcate() {
        int ret=0;
        for(int i:cromozom1) {
            if(i==NEINCARCAT) {
                ret++;
            }
        }
        return ret;
    }
    /**
     * Numara pachetele neincarcabile (cele cu -2)
     * @return int - numarul de pachete neincarcabile
     */
    public int neincarcabile() {
        int ret=0;
        for(int i:cromozom1) {
            if(i==NEINCARCABIL) {
                ret++;
            }
        }
        return ret;
    }
    
    private void golesteSuprapline() {
        range(0,camioane.size()).parallel().forEach(i -> {
            lock.readLock().lock();
            Camion c = camioane.get(i);
            lock.readLock().unlock();
            if(!c.pachete.isEmpty()){
                while(c.ok != true) {
                    int obiect = c.pop(); //pop last element
                    if(obiect != NEINCARCAT) {
                        lock.writeLock().lock();
                        cromozom1[obiect] = NEINCARCAT;
                        lock.writeLock().unlock();
                    } else {
                        break;
                    }
                }
            }
        });
    }
    /**
     * Elimina pachetele ce nu incap intr-un camion anume si le incarca in alt camion mai gol.
     * Daca nu reuseste cu un anumit pachet, il marcheaza ca neincarcabil (-2)
     */
    private void optimizeLoads() {
        golesteSuprapline();
        //punem pachetele in plus in alt camion care nu e plin
        int neincarcate = neincarcate();
        while(neincarcate!=0) {
            //prima data incercam sa incarcam pe un camion existent
            int incarcat;
            boolean posibilDeIncarcat = false;
            Camion c;
            for(int i=0;i<cromozom1.length;i++) {
                if(cromozom1[i] == NEINCARCAT) {
                    incarcat=0;
                    posibilDeIncarcat = false;
                    int camionMin=0;
                    Double distMin = Double.MAX_VALUE, dist; //;
                    for(int j=0;j<camioane.size();j++) {
                        c = camioane.get(j);
                        if(c.opriri>=setari.nrDescarcari) continue;
                        if((c.capacitate*setari.procentIncarcare() - c.ocupat)>=Client.clienti.get(i).volum) { //max dist
                            dist = c.test(i);
                            if(dist<distMin) {
                                posibilDeIncarcat = true;
                                camionMin = j;
                                distMin = dist;
                            }
                        }
                    }
                    if(posibilDeIncarcat) {
                        cromozom1[i] = camionMin;
                        camioane.get(camionMin).add(i);
                        incarcat=1;
                        neincarcate--; 
                    }
                    //nu am reusit sa incarc produsul, 
                    //volumul e mai mare decat camioanele disponibile
                    if(incarcat==0 && Client.clienti.get(i).volum>(camionDisponibil.cautaMinim()*setari.procentIncarcare())) {
                        cromozom1[i]=NEINCARCABIL; //il marcam ca atare
                        neincarcate--;
                    }
                }
            }
            //daca totusi nu reusim sa le incarcam toate si volumele sunt 
            //mai mici decat camioanele disponibile, mai punem un camion 
            //in lista si mai incercam odata sa optimizam
            if(neincarcate!=0) {
                Camion n = new Camion(camionDisponibil.cautaLiber());
                n.calc();
                nrCamioane++;
                camioane.add(n);
            }
        }
    }
        
    private void optimizeByClosestToHome(){
        golesteSuprapline();
        //punem pachetele in plus in alt camion care nu e plin
        int neincarcate = neincarcate();
        while(neincarcate!=0) {
            //prima data incercam sa incarcam pe un camion existent
            int incarcat;
            boolean posibilDeIncarcat = false;
            Camion c;
            for(int i=0;i<cromozom1.length;i++) {
                if(cromozom1[i] == NEINCARCAT) {
                    incarcat=0;
                    posibilDeIncarcat = false;
                    int camionMin=0;
                    Double distMin = Double.MAX_VALUE, dist;
                    for(int j=0;j<camioane.size();j++) {
                        c = camioane.get(j);
                        if(c.opriri>=setari.nrDescarcari) continue;
                        if((c.capacitate*setari.procentIncarcare() - c.ocupat)>=Client.clienti.get(i).volum) {
                            dist = c.testClosest(i);
                            if(dist<distMin) {
                                posibilDeIncarcat = true;
                                camionMin = j;
                                distMin = dist;
                            }
                        }
                    }
                    if(posibilDeIncarcat) {
                        cromozom1[i] = camionMin;
                        camioane.get(camionMin).add(i);
                        incarcat=1;
                        neincarcate--; 
                    }
                    //nu am reusit sa incarc produsul, 
                    //volumul e mai mare decat camioanele disponibile
                    if(incarcat==0 && Client.clienti.get(i).volum>(camionDisponibil.cautaMinim()*setari.procentIncarcare())) {
                        cromozom1[i]=NEINCARCABIL; //il marcam ca atare
                        neincarcate--;
                    }
                }
            }
            //daca totusi nu reusim sa le incarcam toate si volumele sunt 
            //mai mici decat camioanele disponibile, mai punem un camion 
            //in lista si mai incercam odata sa optimizam
            if(neincarcate!=0) {
                Camion n = new Camion(camionDisponibil.cautaLiber());
                n.calc();
                nrCamioane++;
                camioane.add(n);
            }
        }
    }
    /**
     * Metoda de comparare a 2 indivizi.
     * @param obj Obiectul individ care trebuie comparat
     * @return true daca sunt identice.
     */
    @Override
    public boolean equals(Object obj) {
        Individ o = (Individ)obj;
        if(this.compareTo(0)!= o.compareTo(this)) {
            System.out.println("Eroarea e aici: ");
            System.out.println(""+this);
            System.out.println("\n"+o);
        }
        return (compareTo(o)==0); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda de comparare
     * @param o Obiectul cu care se compara.
     * @return negativ daca e mai mic, 0 la fel, pozitiv mai mare
     */
    @Override
    public int compareTo(Object o) { //http://www.javapractices.com/topic/TopicAction.do?Id=10
        Individ c = (Individ) o;
        if(this == c) return 0;
        if(this.ok()==true && c.ok()==false) return -1;
        if(this.ok()==false && c.ok()==true) return 1;
        int fit = Double.compare(this.fitness,c.fitness);
        if(fit == 0) {
            return Double.compare(
                    this.totalDistantaInterna(),
                    c.totalDistantaInterna()
            );
        } else {
            return fit;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.camioane);
        hash = 89 * hash + this.viataGen;
        hash = 89 * hash + Arrays.deepHashCode(this.cromozom1);
        hash = 89 * hash + Arrays.deepHashCode(this.cromozom2);
        hash = 89 * hash + this.nrClienti;
        hash = 89 * hash + Objects.hashCode(this.fitness);
        hash = 89 * hash + Objects.hashCode(this.nrCamioane);
        hash = 89 * hash + Objects.hashCode(this.camionDisponibil);
        hash = 89 * hash + Objects.hashCode(this.lock);
        hash = 89 * hash + (this.optimizat ? 1 : 0);
        hash = 89 * hash + this.viata;
        return hash;
    }
    
    
    
    /**
     * toStrin().
     * @return String.
     */
    @Override
    public String toString() {
        System.out.println("Individ{ neincarcabile: " +neincarcabile()+", neincarcate: " +neincarcate()+ ", nr camioane=" + camioane.size() + ", fitness=" + fitness + '}');
        
        for(int i=0;i<nrClienti;i++) {
            if(cromozom1[i]==-2) {
                System.out.println(""+Client.clienti.get(i));
            }
        }
        for(Camion c:camioane) System.out.println(c);
        return "";
    }
    
    /**
     * Metoda de verificare a individului. 
     * @return true daca nu exista pachete neincarcate, neincarcabile si toate camioanele sunt ok.
     */
    public boolean ok() {
        if(!optimizat) {
            calculeaza(true);
        }
        if(neincarcabile()>0 && neincarcate()>=0) return false;
        for(Camion c:camioane) if(c.ok==false) return false;
        return true;
    }
    
    /**
     * Indivizii au viata limitata. 
     * @return true daca individul mai are generatii de trait.
     */
    public boolean selectabil() {
        return viata>0;
    }
    
    /**
     * Decrementeaza viata individului. 
     */
    public void selecteaza() {
        viata--;
    }
    /**
     * Pentru clasa testing.
     * @param d fitnessul
     */
    void setFitnessForTesting(double d) {
        this.fitness = d;
    }

    /**
     *
     * @param size the value of size
     * @param algoritmGenetic the value of algoritmGenetic
     */
//    public static Individ annealing(Individ n, int pasi) {
//            Individ nou;
//            int temperatura = pasi;
//            int pos1, pos2;
//            double startEn = n.getFitness();
//            double solEn = 0;
//            double pastEn = 0;
//            while (temperatura>1) {
//                nou = new Individ(n); //copiem individul curent cel mai bun
//                //--- mutatia
//                pos1 = R.nextInt(nou.nrClienti - 1);
//                pos2 = R.nextInt(nou.nrClienti - 1);
//                nou.swap(pos1,pos2);
//                //---
//                nou.calculeaza(true);
//                pastEn = n.getFitness();
//                solEn = nou.getFitness();
//                if (solEn < pastEn) {
//                    n = nou;
//                }
//                temperatura--;
//            }
//            return n;
//    }
    /**
     * Schimba 2 pozitii din cromozom intre ele.
     * Aplica acelasi swap pe ambii cromozomi
     * @param pozitie1
     * @param pozitie2 
     */
    public void swap(int pozitie1, int pozitie2) {
        int tmp = cromozom1[pozitie1];
        cromozom1[pozitie1] = cromozom1[pozitie2];
        cromozom1[pozitie2] = tmp;

        tmp = cromozom2[pozitie1];
        cromozom2[pozitie1] = cromozom2[pozitie2];
        cromozom2[pozitie2] = tmp;
    }

    void setViata(int i) {
        viata = i;
    }
    
    public double optimizeaza() {
        double fits = this.fitness;
//        for(int i=0;i<nrClienti;i++) {
//            if(cromozom1[i]>=0 && Client.clienti.get(cromozom1[i]).volum<(CamionDisponibil.getMinSize()*0.7)) {
//                cromozom1[i] = NEINCARCAT;
//            }
//        }
        int pachet;
        for(Camion c:camioane) {
            while(true) {
                pachet = c.popLast();
                if(pachet>=0) {
                    this.setCromozomVal(pachet, -1);
                } else {
                    break;
                }
            }
        }
        optimizeByClosestToHome();
        fitness = 0.0;
        //calculam datele fiecarui camion si fitness-ul total al generatiei
        range(0,camioane.size()).parallel().forEach(i -> {
            lock.readLock().lock();
            Camion c = camioane.get(i);
            lock.readLock().unlock();
            double fit;
            if(c!=null) {
                fit = c.calc();
                lock.writeLock().lock();
                fitness += fit;
                lock.writeLock().unlock();
            } 
        });
        //fiecare neincarcabil scade fitnesul total cu 10k
        for(int i=0;i<neincarcabile();i++){
            fitness += 9999.0;
        }
        optimizat = true;
        return (fits-fitness);
    }
    
    private void genereaza() {
        Comparator<Integer> cmprtr = (Integer u, Integer d) -> {
            if(Client.clienti.get(u).volum < Client.clienti.get(d).volum) return 1;
            else if(Client.clienti.get(u).volum > Client.clienti.get(d).volum) return -1;
            else return 0;
        };
    }
}