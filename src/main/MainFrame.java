/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author yo5bdm
 */
public class MainFrame extends javax.swing.JFrame {
    //lista de clienti/puncte unde trebuie sa ajunga marfa
    public static ArrayList<Client> clienti = new ArrayList();
    //punctul de unde vor pleca camioanele
    public static Client casa = new Client("ACASA", 47.075866, 21.901441, 0.0);
    //distantele intre puncte
    public static Double[][] distante;
    //distantele de la casa la fiecare punct
    public static Double[] catre_casa;
    public static MainFrame m;
    public static ModelTabel model;
    //individul cel mai bun ajunge aici
    public static Individ best;
    //folosit la accestul individului best din firele de executie
    public static final Object O = new Object(); //syncronized
    //public static HashDB hashDb = new HashDB();
    //pachetele mari care nu incap in camioane
    public static ArrayList<String> celeMari = new ArrayList();
    public static double celeMariDist;
    public static int celeMariNrCamioane;
    
    //privati
    private Testing t = new Testing();
    private Timer paint;
    private Camion camion;
    //pentru desenare
    private double dx, dy;
    private double fs;
    private Graphics g;
    private int[] probabilitati;
    
    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        //setLocationRelativeTo(null); //center
        initComponents();
        incarca_clienti();
        
        m = this;
        model = new ModelTabel();
        setBest(null,-1,-1);
        jTable1.setModel(model);
        camion = null;
        for(Client c:clienti) { 
            dx += (c.longitudine);
            dy += (c.latitudine);
        }
        dx /= clienti.size();
        dy /= clienti.size();
        g = Panel1.getGraphics();
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panel1 = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                redraw();

            }

        };
        jSlider1 = new javax.swing.JSlider();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        PornesteGenerarea = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        BSDistanta = new javax.swing.JLabel();
        BSNrCamioane = new javax.swing.JLabel();
        BSGeneratia = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        VitezaAlgoritm = new javax.swing.JComboBox<>();
        Progres = new javax.swing.JProgressBar();
        SalveazaSolutia = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rutare pachete");
        setLocation(new java.awt.Point(0, 0));
        setLocationByPlatform(true);

        Panel1.setBackground(new java.awt.Color(255, 255, 255));
        Panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        PornesteGenerarea.setText("Porneste generarea");
        PornesteGenerarea.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PornesteGenerareaActionPerformed(evt);
            }
        });

        jLabel3.setText("Cea mai buna solutie:");

        jLabel4.setText("Distanta totala:");

        jLabel5.setText("Nr Camioane folosite:");

        BSDistanta.setText("jLabel6");

        BSNrCamioane.setText("jLabel6");

        BSGeneratia.setText("jLabel6");

        jLabel6.setText("Tip generare:");

        VitezaAlgoritm.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Rapid", "Mediu", "Lent", "Infinit" }));
        VitezaAlgoritm.setSelectedIndex(2);

        SalveazaSolutia.setText("Salveaza solutia");
        SalveazaSolutia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalveazaSolutiaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(PornesteGenerarea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(VitezaAlgoritm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(Progres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                        .addComponent(BSGeneratia))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BSDistanta))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BSNrCamioane))
                    .addComponent(SalveazaSolutia, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(VitezaAlgoritm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PornesteGenerarea)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Progres, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(BSGeneratia))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(BSDistanta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(BSNrCamioane))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SalveazaSolutia)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 1096, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        redraw();
    }//GEN-LAST:event_jSlider1StateChanged

    private void PornesteGenerareaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PornesteGenerareaActionPerformed
        //initializarea camioanelor disponibile aici
        calculeaza_tablou_distante();
        CamionDisponibil.adaugaCapacitate(101,10);
        CamionDisponibil.adaugaCapacitate(91,15);
        CamionDisponibil.adaugaCapacitate(81,9999);
        //t.run(); //testele
        rezolvaCeleMari();
        int viteza = VitezaAlgoritm.getSelectedIndex();
        redraw();
        AlgoritmGenetic v[] = new AlgoritmGenetic[6];
        probabilitati = new int[8];
        for(int i:probabilitati) i=0;
        for(int i=2;i<=7;i++) { //probabilitatea de mutatie intre 2 si 7
            v[i-2] = new AlgoritmGenetic(clienti.size(),viteza,i);
            //v[i-2].setPriority(Thread.MAX_PRIORITY);
            v[i-2].start();
        }
    }//GEN-LAST:event_PornesteGenerareaActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int[] selection = jTable1.getSelectedRows();
        ArrayList<String> listaCnp = new ArrayList();
        selection[0] = jTable1.convertRowIndexToModel(selection[0]);
        
        camion = best.camioane.get(selection[0]);
        redraw();
    }//GEN-LAST:event_jTable1MouseClicked

    private void SalveazaSolutiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalveazaSolutiaActionPerformed
        JFileChooser fileChooser = new JFileChooser(); //File curDir = din setari
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File","txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                System.out.println("salveaza "+file.getAbsolutePath()+" "+file.getName()+" "+file.toString());
                List<String> lines = genereazaFisier();//Arrays.asList("The first line", "The second line");
                Path f = Paths.get(file.getAbsolutePath());
                Files.write(f, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_SalveazaSolutiaActionPerformed
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel BSDistanta;
    private javax.swing.JLabel BSGeneratia;
    private javax.swing.JLabel BSNrCamioane;
    private javax.swing.JPanel Panel1;
    private javax.swing.JButton PornesteGenerarea;
    private javax.swing.JProgressBar Progres;
    private javax.swing.JButton SalveazaSolutia;
    private javax.swing.JComboBox<String> VitezaAlgoritm;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    /**
     * Initializeaza ProgressBar-ul de pe pagina principala.
     * @param max Numarul maxim de generatii ce vor fi completate
     */
    public void initProgres(int max) {
        Progres.setMaximum(max);
        Progres.setValue(0);
        Progres.setStringPainted(true);
    }
    /**
     * Seteaza valoarea curenta a progresului pe ProgressBar-ul de pe pagina principala.
     * @param val Valoarea curenta a progresului
     */
    public void setProgres(int val) {
        Progres.setValue(val);
    }
    /**
     * Seteaza cea mai buna solutie din firul de executie, pentru afisare.
     * @param i individul ce urmeaza sa fie setat ca solutie noua cea mai buna
     * @param generatia
     * @param mutatie 
     */
    public void setBest(Individ i, int generatia, int mutatie) {
        if(i!=null) {
            int delta = Integer.MAX_VALUE;
            if(best!=null) delta = (int)(best.getFitness()-i.getFitness());
            best = i;
            BSDistanta.setText((int)((double)best.getFitness()+celeMariDist)+" km (-"+delta+")");
            BSNrCamioane.setText((best.camioane.size()+celeMariNrCamioane)+"");
            BSGeneratia.setText("(G"+generatia+"M"+mutatie+")");
            probabilitati[mutatie]++;
        } else {
            best = null;
            BSDistanta.setText("");
            BSNrCamioane.setText("");
            BSGeneratia.setText("");
        }
        model.fireTableDataChanged();
        System.out.println("Procentele ce au gasit indivizi:");
        System.out.println(Arrays.toString(probabilitati));
    }
    
    private static void calculeaza_tablou_distante() {
        distante = new Double[clienti.size()][clienti.size()];
        for (int i = 0; i < clienti.size(); i++) {
            for (int j = 0; j < clienti.size(); j++) {
                distante[i][j] = Calcule.distanta(clienti.get(i), clienti.get(j));
            }
        }
        catre_casa = new Double[clienti.size()];
        for(int i=0;i<clienti.size();i++) {
            catre_casa[i] = Calcule.distanta(casa,clienti.get(i));
        }
    }
    private static void incarca_clienti() {
        BufferedReader fin;
        try {
            fin = new BufferedReader(new FileReader("clienti.csv"));
            MainFrame.clienti.clear();
            String linie;
            while ((linie = fin.readLine()) != null) {
                //fiecare elev pe o linie, separat prin virgula
                StringTokenizer t = new StringTokenizer(linie, ";");
                MainFrame.clienti.add(new Client(t.nextToken(), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken()), Double.parseDouble(t.nextToken())));
            }
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void rezolvaCeleMari() {
        ArrayList<Client> temp = new ArrayList();
        celeMari.clear();
        celeMariDist = 0.0;
        celeMariNrCamioane=0;
        double dist;
        System.out.println("Nr Clienti "+clienti.size());
        for (Client c:clienti) {
            if(c.volum > CamionDisponibil.getMaxSize()) {
                System.out.println("Rezolv " + c);
                while(c.volum>CamionDisponibil.getMaxSize()) {
                    int volum = CamionDisponibil.scadeLiber();
                    //System.out.println("Camion "+volum);
                    c.volum -= volum;
                    //System.out.println("Ramas"+c.volum);
                    dist = Calcule.distanta(casa,c);
                    celeMariDist += dist;
                    celeMariNrCamioane++;
                    celeMari.add(" ");
                    celeMari.add("======================================");
                    celeMari.add("Camion volum "+volum+", ocupat "+volum+", opriri 1, distanta totala "+(int)dist+" km;");
                    celeMari.add(c.cod_client+" "+c.ship_to+", GPS="+c.latitudine+","+c.longitudine+" vol="+volum);
                }
                temp.add(c);
            }
        }
    }
    private ArrayList genereazaFisier() {
        Individ ind = best;
        Client cli;
        ArrayList<String> ret = new ArrayList();
        
        ret.add("======================================");
        ret.add("Distanta totala de parcurs: "+(int)(ind.getFitness()+celeMariDist)+" km");
        ret.add("Numar total de camioane folosite: "+(ind.camioane.size()+celeMariNrCamioane));
        ret.addAll(celeMari);
        for(Camion cam:ind.camioane) {
            ret.add(" ");
            ret.add("======================================");
            ret.add("Camion volum "+(cam.capacitate-1)+", ocupat "+cam.ocupat+", opriri "+cam.opriri+", distanta totala "+(int)cam.distanta+" km;");
            ret.add("Pachetele de incarcat:");
            for(Integer i:cam.solutia) {
                cli = clienti.get(i);
                ret.add(cli.cod_client+" "+cli.ship_to+", GPS="+cli.latitudine+","+cli.longitudine+" vol="+cli.volum);
            }
        }
        ret.add(" ");
        ret.add("======================================");
        ret.add("Fisier generat  "+LocalDateTime.now());
        ret.add("======================================");
        return ret;
    }
    
    public void redraw() {
        fs = (double)jSlider1.getValue();
        ploteaza_punctele();
    }
    private void ploteaza_punctele() {
        int max_x = Panel1.getWidth();
        int max_y = Panel1.getHeight();
        
//        g.setColor(Color.white);
//        g.fillRect(0,0,max_x,max_y);
        
        g.setColor(Color.black);
        int x, y;
        for(Client c:clienti) {
            x = max_x/2 + ((int)(((c.longitudine-dx)*fs))); 
            y = max_y/2 - ((int)(((c.latitudine-dy)*fs)));
            g.drawOval(x,y,3,3);
        }
        // ---------- partea de desenare drumuri ---------------
        if(camion!=null) {
            int x2,y2;
            //linia de acasa pana la primul:
            g.setColor(Color.BLUE);
            x = max_x/2 + ((int)(((casa.longitudine-dx)*fs))); 
            y = max_y/2 - ((int)(((casa.latitudine-dy)*fs)));
            x2 = max_x/2 + ((int)(((clienti.get(camion.solutia.get(0)).longitudine-dx)*fs))); 
            y2 = max_y/2 - ((int)(((clienti.get(camion.solutia.get(0)).latitudine-dy)*fs)));
            g.drawLine(x,y,x2,y2);
            g.setColor(Color.black);
            for(int i=0;i<camion.solutia.size()-1;i++) {
                int i1 = camion.solutia.get(i);
                int i2 = camion.solutia.get(i+1);
                x = max_x/2 + ((int)(((clienti.get(i1).longitudine-dx)*fs))); 
                y = max_y/2 - ((int)(((clienti.get(i1).latitudine-dy)*fs)));
                x2 = max_x/2 + ((int)(((clienti.get(i2).longitudine-dx)*fs))); 
                y2 = max_y/2 - ((int)(((clienti.get(i2).latitudine-dy)*fs)));
                g.drawLine(x,y,x2,y2);
            }
        }
    }
    
}
