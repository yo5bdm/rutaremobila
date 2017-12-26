/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.ArrayList;
import java.util.Arrays;
import static main.AlgoritmGenetic.R;

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
    //individul cel mai bun ajunge aici
    public static Individ best;
    public static int celeMariNrCamioane;
    public static double celeMariDist;
    //pachetele mari care nu incap in camioane
    public static ArrayList<String> celeMari = new ArrayList(); //String-urile folosite la imprimare in fisier
    
    /* PUBLIC NESTATIC */
    public Integer[] cromozom; //indexul e obiectul, valoarea e camionul pe care e incarcat
    public Integer[] cromozom2;
    public ArrayList<Camion> camioane = new ArrayList();
    public int viataGen;
    
    /* PRIVATE */
    //private Double distanta_totala;
    private Double fitness; //distanta totala parcursa
    private Integer nr_camioane;
    private int viata = 50; //50 de generatii
    private CamionDisponibil camionDisponibil; //obiectul folosit pentru aflarea a ce camion e disponibil
            
    /**
     * Constructor.
     * @param nrClienti int nr de clienti
     * @param viataIndivid int numarul de generatii ce va fi selectabil individul
     * @param cam numarul de camioane
     * @param generare Boolean true daca se doreste generarea random a individului
     */    
    public Individ(int nrClienti, int cam, int viataIndivid, boolean generare) {
        this.viata = viataIndivid;
        this.viataGen = viataIndivid;
        cromozom = new Integer[nrClienti];
        cromozom2 = new Integer[nrClienti];
        camionDisponibil = new CamionDisponibil();
        nr_camioane = cam;
        if(generare == true) {
            for(int j=0;j<nrClienti;j++) {
            cromozom[j] = R.nextInt(nr_camioane-1); //random in camioanele care pot duce marfa respectiva
            cromozom2[j] = cromozom[j];
            //clienti.get(j).volum
            }
        }
        for(int i=0;i<nr_camioane;i++) {
              camioane.add(new Camion(camionDisponibil.cautaLiber()));
        }
        if(generare == true) optimize_loads();
    }
    /**
     * Constructorul de copiere.
     * @param b Individul (obiectul) de copiat
     */
    Individ(Individ b) {
        this(b.cromozom.length, b.nr_camioane, b.viataGen, false);
        //cromozom = new Integer[b.cromozom.length];
        //cromozom2 = new Integer[b.cromozom.length];
        //camionDisponibil = new CamionDisponibil();
        for(int i=0;i<b.cromozom.length;i++) {
            this.cromozom[i] = b.cromozom[i];
            this.cromozom2[i] = b.cromozom2[i];
        }
        //this.calculeaza(true);
    }
    /**
     * Metoda pregateste o noua copiere din cromozom. Goleste toate camioanele 
     * si ajusteaza numarul total, daca e necesar
     */
    private void reset_camioane() {
        for(Camion c:camioane) c.reset();
        if(camioane.size()<nr_camioane) {
            int nr = nr_camioane-camioane.size();
            for(int i=0;i<nr;i++) {
                camioane.add(new Camion(camionDisponibil.cautaLiber()));
            }
        }
        nr_camioane = camioane.size();
    }
    /**
     * face o copie a cromozomului in varianta neoptimizata
     */
    public void copiaza() { //copiaza cromozomul curent in cromozomul copie
        for(int i=0;i<cromozom.length;i++){
            cromozom2[i]=cromozom[i];
        }
    }
    /**
     * Returneaza fitness-ul populatiei.
     * @return Double, valoarea fitness-ului total al populatiei
     */
    public double getFitness() {
        return fitness;
    }
    /**
     * Calculeaza fitness-ul individului curent.
     * @param optimizeaza Boolean true daca se doreste optimizarea
     * @return Double fitness-ul individului
     */
    public double calculeaza(boolean optimizeaza) {
//        Nod n = hashDb.iaNod(this.hashCode());
//        if(n!=null) {
//            this.fitness = n.fitness;
//            return fitness;
//        }
//        int hash = this.hashCode();
        int max=0;
        for(int i:cromozom) if(i>max) max=i; //gasim maxim, sa stim cate camioane avem nevoie.
        nr_camioane = max+1;
        reset_camioane();
        //incercam sa plasam si neplasabilele
        for(int i=0;i<cromozom.length;i++) { 
            if(cromozom[i]==-2) cromozom[i]=-1;
        }
        //se adauga pachetul curent in camionul aferent
        for(int i=0;i<cromozom.length;i++) {
            if(cromozom[i]==-1) continue;
            camioane.get(cromozom[i]).add(i); //luat camionul cu nr gasit la indexul i si adaugam produsul in el
        }
        //se optimizeaza incarcarile
        if(optimizeaza) optimize_loads();
        fitness = 0.0;
        //calculam datele fiecarui camion si fitness-ul total al generatiei
        for (Camion camion : camioane) {
            fitness += camion.calc(); //distanta totala parcursa de toate camioanele
        } 
        //fiecare neincarcabil scade fitnesul total cu 10k
        for(int i=0;i<neincarcabile();i++){
            fitness += 9999.0;
        }
        //hashDb.adauga(hash,fitness);
        return fitness;
    }
    /**
     * Numara pachetele neincarcate (cele cu -1)
     * @return int - numarul de pachete neincarcate
     */
    private int neincarcate() {
        int ret=0;
        for(int i:cromozom) {
            if(i==-1) {
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
        for(int i:cromozom) {
            if(i==-2) {
                ret++;
            }
        }
        return ret;
    }
    /**
     * Elimina pachetele ce nu incap intr-un camion anume si le incarca in alt camion mai gol.
     * Daca nu reuseste cu un anumit pachet, il marcheaza ca neincarcabil (-2)
     */
    private void optimize_loads() {
        //golim camioanele suprapline
        for(int i=0;i<camioane.size();i++) {
            Camion c = camioane.get(i);
            while(c.ok != true) {
                int obiect = c.pop(); //pop last element
                cromozom[obiect] = -1; //il marcam si in cromozom ca fiind nefolosit
            }
        }
        //punem pachetele in plus in alt camion care nu e plin
        while(neincarcate()!=0) {
            //prima data incercam sa incarcam pe un camion existent
            int incarcat=0;
            for(int i=0;i<cromozom.length;i++) {
                if(cromozom[i] == -1) { //clientul nu e incarcat
                    incarcat=0;
                    camioane: for(int j=0;j<camioane.size();j++) {
                        Camion c = camioane.get(j);
                        if(c.opriri>=15) continue;
                        if((c.capacitate - c.ocupat)>=Client.clienti.get(i).volum) {
                            cromozom[i] = j;
                            c.add(i);
                            incarcat=1;
                            break camioane; //iesi din bucla for
                        }
                    }
                    //nu am reusit sa incarc produsul, volumul e mai mare decat camioanele disponibile
                    if(incarcat==0 && Client.clienti.get(i).volum>80) {
                        cromozom[i]=-2; //il marcam ca atare
                    }
                }
            }
            //daca totusi nu reusim sa le incarcam toate si volumele sunt 
            //mai mici decat camioanele disponibile, mai punem un camion 
            //in lista si mai incercam odata sa optimizam
            if(neincarcate()!=0) {
                Camion c = new Camion(camionDisponibil.cautaLiber());
                c.calc();
                nr_camioane++;
                camioane.add(c);
            }
        }
    }
    /**
     * Metoda optimizeaza toate camioanele.
     */
    public void optimizare() {
        for(Camion c:camioane) c.optimizare();
    }
    
    @Override
    public boolean equals(Object obj) {
        Individ o = (Individ)obj;
        return (this.fitness == o.fitness); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Arrays.deepHashCode(this.cromozom);
        hash = 59 * hash + Arrays.deepHashCode(this.cromozom2);
        hash = 59 * hash - fitness.intValue();
        return hash;
    }
    
    @Override
    public int compareTo(Object o) {
        Individ c = (Individ) o;
        return this.fitness.compareTo(c.fitness);
    }

    @Override
    public String toString() {
        System.out.println("Individ{ neincarcabile: " +neincarcabile()+", neincarcate: " +neincarcate()+ ", nr camioane=" + camioane.size() + ", fitness=" + fitness + '}');
        //for(Camion c:camioane) System.out.println(c);
        return "";
    }
    
    /**
     * Metoda de verificare a individului. 
     * @return true daca nu exista pachete neincarcate, neincarcabile si toate camioanele sunt ok.
     */
    public boolean ok() {
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
}
