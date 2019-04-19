/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author yo5bd
 */
public class Autoadaptare {

    /**
     * Algoritm Genetic Autoadaptiv (AGA).
     * Algoritmul foloseste un procent mai mic de mutatie pentru indivizii buni
     * si unul mai mare pentru indivizii mai putin buni
     */
    private static final int[] PROBABILITATI_MUTATIE = new int[]{2, 4, 8, 15, 25, 40, 60, 95, 150, 230}; //{2, 4, 8, 15, 20, 35, 45, 60, 150, 200};
    /**
     * Numarul de indivizi per generatie.
     */
    public int nrIndivizi;
    public boolean rapid;
    public double mutationAdjust; 
    public int selectionType=3; //2
    public final int viataIndivid = 20; //5
    public int nrPuncteTaiere=200; //150
    private final double minMutationAdjust = 0.6; //0.5
    private final double maxMutationAdjust = 1;
    private int maxGeneratii=5000; //3000 pentru teste
    private final double procEvolutie = 1.3; //1.6
    private final double procRegres = 1.1;
    private int faraEvolutie=0;
    private int cuEvolutie=0;
    private int generatia;
    private int minIndivizi=100; //100
    private int maxIndivizi=200; //400
    private double dx=0; //aici va fi media diferentei intre cel mai bun si cel mai slab individ din fiecare generatie
    public double lastDx=0;
    private ImmigrantThread immigrant;
    private Individ immigrantBest;
    private final AlgoritmGenetic parinte;
    private boolean enableImmigrantsThread = false;
    private int stopImmigrantsThreadAfter = 999; //opreste threadul dupa nr asta de generatii
    
    //analizarea rezultatelor generatiei
    private double mean;
    private double sigma;
    private double suma;

    Autoadaptare(int nrClienti, boolean b, AlgoritmGenetic parinte) {
        this.parinte = parinte;
        this.nrIndivizi = nrClienti; 
        rapid = b;
        this.mutationAdjust = minMutationAdjust;
    }
    void boost() {
        selectionType = 2;
        processImmigrants();
        if(generatia%100 == 0 && rapid == true) { 
            restartImmigrantsThread();    
            if(generatia%1000==0) {
                minIndivizi *= 1.2;
                maxIndivizi *= 1.2;
            }
        }
        dx /= 10; //media pe 10 generatii
        if(rapid==true){
            if(dx < 100){
                nrIndivizi *= 2;
                selectionType = 3;
            }
        }
        lastDx = dx;
        dx=0;
    }
    void adaptare(double dx) {
        this.dx += dx;
        nrIndivizi = minIndivizi;
        if(true) return; //disable autoadaptare
        if(faraEvolutie > 20) {
            schimba();
            faraEvolutie = -80;
            cuEvolutie=0;
        } else if(maxGeneratii!=Integer.MAX_VALUE && cuEvolutie>20 && generatia>(0.8*maxGeneratii)) {
            maxGeneratii *=1.1;
            cuEvolutie = 0;
        }
        if(rapid) {
            mutationAdjust = minMutationAdjust;
            if(nrIndivizi > minIndivizi){
                nrIndivizi /= procRegres;
            } else {
                nrIndivizi = minIndivizi;
            }
        } else {
            mutationAdjust = maxMutationAdjust;
            selectionType = 2; //2
            if(nrIndivizi < maxIndivizi){
                nrIndivizi *= procEvolutie;
            } else {
                nrIndivizi = maxIndivizi;
            }
        }
        if(generatia == maxGeneratii/2) {
            minIndivizi *= 1.2;
            maxIndivizi *= 1.2;
        }
    }    
    private void schimba() {
        rapid = !rapid;
        if(enableImmigrantsThread) 
            restartImmigrantsThread();
    }    
    public boolean continua() {
        generatia++;
        if(generatia < maxGeneratii) {
            return true;
        } else if(cuEvolutie>50) {
            maxGeneratii += 200;
            cuEvolutie = 0;
            return true;
        }        
        return false;
    }    
    public void evolutie() {
        faraEvolutie=0;
        cuEvolutie++;
        if(cuEvolutie > 5){
            rapid = true;
        }
    }
    public void faraEvolutie() {
        faraEvolutie++;
    }
    public boolean zeceGeneratii() {
        return generatia%10==0;
    }    
    public int getGeneratia() {
        return generatia;
    }    
    public void setIndivizi(int fs) {
        nrIndivizi = minIndivizi * fs;
    }
    public void setMaxGen() {
        maxGeneratii = Integer.MAX_VALUE;
    }
    public int getMaxGeneratii() {
        return maxGeneratii;
    }    
    public double getDx() {
        return dx;
    }
    
    /**
     * Returneaza procentul de mutatie pentru indivizul curent.
     * Algoritm Genetic Autoadaptiv (AGA).
     *
     * @param i int Indexul elementului curent
     * @param size dimensiunea populatiei pentru care se calculeaza
     * @return int procentul de mutatie
     */
    public int procMut(int i, int size) {
        int lung = PROBABILITATI_MUTATIE.length;
        return (int)(mutationAdjust*PROBABILITATI_MUTATIE[((i / size) * lung) % lung]);
    }
    
    //calcul sigma
    public void calc_std_dev() {
        parinte.lock.readLock().lock();
        int n = parinte.populatie.size();
        suma = 0;
        parinte.populatie.forEach((i) -> {
            suma += i.getFitness();
        });
        mean = suma / n;
        double total_var=0.0, var;
        for(Individ i:parinte.populatie) {
            var = i.getFitness() - mean;
            total_var = var*var;
        }
        parinte.lock.readLock().unlock();
        var = total_var / (n-1);
        sigma = Math.sqrt(var);        
    }
    
    public double deltaSigma(Individ i) {
        return (i.getFitness()-mean)/sigma;
    }

    private void processImmigrants() {
        if(enableImmigrantsThread) {
            if(stopImmigrantsThread()) return;
            if(immigrant!=null) {
            immigrantBest = immigrant.lucru;
            if(immigrantBest.getFitness()<Individ.best.getFitness()) {
                immigrantBest.setViata(40);
                parinte.lock.writeLock().lock();
                try {
                    parinte.populatie.add(immigrantBest);
                    System.out.println("Added immigrant "+immigrantBest.getFitness()+" at gen "+generatia);
                } finally {
                    parinte.lock.writeLock().unlock();
                }
            }
            } else 
                immigrant = new ImmigrantThread(Individ.best,Client.clienti.size());
        }
    }

    private void restartImmigrantsThread() {
        if(enableImmigrantsThread && immigrant != null) {
            immigrant.opreste();
            //try { immigrant.join(); } catch (InterruptedException ex) { Logger.getLogger(Autoadaptare.class.getName()).log(Level.SEVERE, null, ex); }
            immigrant = new ImmigrantThread(Individ.best,Client.clienti.size());
        }
    }

    private boolean stopImmigrantsThread() {
        if(stopImmigrantsThreadAfter <= this.generatia){
            enableImmigrantsThread = false;
            if(immigrant!=null) immigrant.opreste();
            return true;
        } 
        return false;    
    }
}
