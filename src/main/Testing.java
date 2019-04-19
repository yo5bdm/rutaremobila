/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;
import static main.MainFrame.*;

/**
 * Clasa de testare a functionalitatilor.
 * @author yo5bdm
 */
public class Testing {

    /**
     * Teste.
     */
    public Camion c = new Camion(110);
    private final Random r = new Random();
    /**
     * Rularea testelor.
     */
    public void run() {
        System.out.println("System testing:");
        //CamionTest2(true);
        //IndividTest(true);
        System.out.println("TEstare genealogie");
        genealogieTest();
    }
    /**
     * Test camion.
     * @param executa true daca se executa, false daca nu.
     */
    //private void CamionTest(boolean executa)
    /**
     * Testul individului.
     * @param executa true daca se executa.
     */
    private void IndividTest(boolean executa) {
        Individ n = new Individ(Client.clienti.size(),48,100,false);
        for(int i=0;i<Client.clienti.size();i++) {
            n.setCromozomVal(i,-1);
        }
        //AnnealingThread ann = new AnnealingThread(n,Client.clienti.size());
        System.out.println("Fitnes total"+n.calculeaza(true));
        System.out.println("Neincarcabile = "+n.neincarcabile());
        m.setBest(n,0,0,"TESTING");
    }
    private void genealogieTest() {
        Genealogie a, b;
        System.out.println("Creem genealogiile A si B: ");
        a = new Genealogie();
        b = new Genealogie();
        int col = 2;   //parintii pe linia 0 au 2 coloane
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < col; j++) { //se copiaza parintii parintilor la bunici, bunicii la strabunici, etc
                a.tablou[i][j] = (long) (Math.random()*100); //ordinea nu conteaza
                b.tablou[i][j] = (long) (Math.random()*100); //ordinea nu conteaza
            }
            col *= 2; //in nivelul urmator avem dublu nr de coloane
        }
        System.out.println(a);
        System.out.println(b);
        System.out.println("Testam constructorul de copiere:");
        Genealogie c = new Genealogie();
        c.copiaza(a);
        System.out.println(c);
        System.out.println("Testam setarea parintilor:");
        Genealogie d = new Genealogie();
        d.setParinti(a, b);
        System.out.println(d);
    }
}
