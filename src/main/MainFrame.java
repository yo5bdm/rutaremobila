/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Pagina principala a aplicatiei.
 * @author yo5bdm
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Permite accesul la MainFrame.
     */
    public static MainFrame m;

    /**
     * Permite accesul la model.
     */
    public static ModelTabel model;

    /**
     * Folosit la accestul individului best din firele de executie. 
     * Obiectul monitor pe care se face sincronizarea.
     */
    public static final Object O = new Object(); //syncronized

    /**
     * Dialogul cu setarile aplicatiei.
     */
    public static SetariDialog s; //setarile aplicate
    /**
     * Clasa de setari.
     */
    public static Setari setari;
    /**
     * Aici se salveaza numarul de generatii la care a ajuns un anumit fir.
     * Calculul procentului de pe MainFrame se face facand media acestor valori.
     */
    public static int[] procente;

    /**
     * Clasa Analiza.
     */
    public static Analiza analiza;
    
//privati
    private final Testing t = new Testing();
    private Timer paint;
    //pentru desenare
    private Camion camion;
    private double startFitness;
    private double dx, dy;
    private double cenX, cenY;
    private int mX, mY;
    private double factorScalare;
    //runtime
    private boolean ruleaza=false;
    private ArrayList<AlgoritmGenetic> listaFire;
    private final ArrayList<Grafic> grafic = new ArrayList();
    private final Timer timer = new Timer(50, new ActionListener() { // 50ms, adica vreo 20fps
        @Override
        public void actionPerformed(ActionEvent e) {
            m.repaint(); //main frame repaint
        }
    });
    private final Timer timer2 = new Timer(1000, new ActionListener() { // 50ms, adica vreo 20fps
        @Override
        public void actionPerformed(ActionEvent e) {
            if(ruleaza) {
                printDifference();
            }
        }
    });
    private int maxgen;
    private long start;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        this.cenY = -1.0;
        this.cenX = -1.0;
        setLocationRelativeTo(null); //center
        initComponents();
        s = new SetariDialog(this,true);
        setari = s.setari;
        m = this;
        analiza = new Analiza();
        model = new ModelTabel();
        setBest(null,0,0,"");
        jTable1.setModel(model);
        jTable1.setAutoCreateRowSorter(true); //
        camion = null;
        PornesteGenerarea.setEnabled(false);
        disableSalveaza();
        VitezaAlgoritm.setEnabled(false);
        timer.start();
        timer2.start();
        //salveaza setarile la iesirea din aplicatie
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                Setari.salveaza(setari);
            }
        });
        
        //debug distanta              lat   long
        Client unu = new Client("unu",47.080,21.890,1.0);
        Client doi = new Client("doi",48.63835378,2.32086182,1.0);
        System.out.println("Distanta debug: "+Calcule.distanta(unu,doi));
        //end debug
        
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
                Graphics2D g2 = (Graphics2D)g;
                deseneaza(g2);
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
        FisierIncarcat = new javax.swing.JLabel();
        jElapsed = new javax.swing.JLabel();
        jStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        Grila = new javax.swing.JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                grila(g2);
            }
        };
        jMenuBar1 = new javax.swing.JMenuBar();
        MeniuFisier = new javax.swing.JMenu();
        MeniuIncarcaCSV = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        SalveazaCSV = new javax.swing.JMenuItem();
        SalveazaTXT = new javax.swing.JMenuItem();
        SalveazaHTML = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        MeniuSetari = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rutare pachete");
        setLocation(new java.awt.Point(0, 0));

        Panel1.setBackground(new java.awt.Color(255, 255, 255));
        Panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Panel1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                Panel1MouseDragged(evt);
            }
        });
        Panel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                Panel1MousePressed(evt);
            }
        });

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 288, Short.MAX_VALUE)
        );

        jSlider1.setMaximum(300);
        jSlider1.setMinimum(1);

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
        VitezaAlgoritm.setSelectedIndex(1);
        VitezaAlgoritm.setToolTipText("<htmll>Selecteaza modul de lucru al algoritmului.<br>\nRapid - algoritmul merge o perioada scurta de timp inainte sa se opreasca (500 de pasi)<br>\nMediu - algoritmul functioneaza o perioada medie de timp (2000 de pasi)<br>\nLent - algoritmul functioneaza o perioada lunga de timp (10.000 de pasi)<br>\nInfinit - algoritmul functioneaza o perioada extrem de lunga de timp. Ideal daca doriti sa lasati calculatorul pornit o perioada lunga de timp</html>");

        Progres.setStringPainted(true);

        FisierIncarcat.setText("Nici un fisier incarcat.");

        jElapsed.setText("00:00:00");

        jStatus.setText("[ status ]");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(Progres, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BSGeneratia))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BSDistanta))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BSNrCamioane))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(VitezaAlgoritm, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(FisierIncarcat, javax.swing.GroupLayout.PREFERRED_SIZE, 123, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PornesteGenerarea))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jElapsed))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jStatus)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(FisierIncarcat)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(VitezaAlgoritm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(PornesteGenerarea, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)))
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
                .addComponent(jElapsed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jStatus)
                .addContainerGap())
        );

        jLabel1.setText("Zoom harta");

        Grila.setBackground(new java.awt.Color(255, 255, 255));
        Grila.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout GrilaLayout = new javax.swing.GroupLayout(Grila);
        Grila.setLayout(GrilaLayout);
        GrilaLayout.setHorizontalGroup(
            GrilaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        GrilaLayout.setVerticalGroup(
            GrilaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );

        MeniuFisier.setText("Fisier");

        MeniuIncarcaCSV.setText("Incarca fisier CSV");
        MeniuIncarcaCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MeniuIncarcaCSVActionPerformed(evt);
            }
        });
        MeniuFisier.add(MeniuIncarcaCSV);
        MeniuFisier.add(jSeparator1);

        SalveazaCSV.setText("Salveaza solutia (CSV)");
        MeniuFisier.add(SalveazaCSV);

        SalveazaTXT.setText("Salveaza solutia (TXT)");
        SalveazaTXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalveazaTXTActionPerformed(evt);
            }
        });
        MeniuFisier.add(SalveazaTXT);

        SalveazaHTML.setText("Salveaza solutia (HTML)");
        MeniuFisier.add(SalveazaHTML);

        jMenuBar1.add(MeniuFisier);

        jMenu2.setText("Setari");

        MeniuSetari.setText("Setari");
        MeniuSetari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MeniuSetariActionPerformed(evt);
            }
        });
        jMenu2.add(MeniuSetari);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

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
                    .addComponent(Grila, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(Grila, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        setSize(new java.awt.Dimension(1153, 764));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void PornesteGenerareaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PornesteGenerareaActionPerformed
        if(!ruleaza) {
            new Thread(){
                @Override
                public void run() {
                    MeniuIncarcaCSV.setEnabled(false);
                    MeniuSetari.setEnabled(false);
                    VitezaAlgoritm.setEnabled(false);
                    CamionDisponibil.resetDisponibile();
                    Client.restore();
                    Client.calculeazaTablouDistante();
                    Client.rezolvaCeleMari();
                    int viteza = VitezaAlgoritm.getSelectedIndex();
                    //t.run(); //testarile
                    listaFire = new ArrayList();
                    procente = new int[setari.memorie];
                    AlgoritmGenetic a;
                    a = new AlgoritmGenetic(0,viteza);
                    a.setPriority(setari.prioritate);
                    a.start();
                    listaFire.add(a);
                    PornesteGenerarea.setText("Opreste generarea");
                    ruleaza = true;
                    start = System.currentTimeMillis();
                    for(Thread t:listaFire) {
                        try {
                            t.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    analiza.saveFile();
                    MeniuIncarcaCSV.setEnabled(true);
                    MeniuSetari.setEnabled(true);
                    VitezaAlgoritm.setEnabled(true);
                    PornesteGenerarea.setText("Porneste generarea");
                    ruleaza = false;
                }
            }.start();
            
        } else  {
            try {
                for(AlgoritmGenetic a:listaFire) {
                    a.opreste();
                    a.join();
                }
                analiza.saveFile();
                MeniuIncarcaCSV.setEnabled(true);
                MeniuSetari.setEnabled(true);
                VitezaAlgoritm.setEnabled(true);
                PornesteGenerarea.setText("Porneste generarea");
                ruleaza = false;
            } catch (InterruptedException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_PornesteGenerareaActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int[] selection = jTable1.getSelectedRows();
        ArrayList<String> listaCnp = new ArrayList();
        selection[0] = jTable1.convertRowIndexToModel(selection[0]);
        //luam in seama doar primul selectat
        camion = Individ.best.camioane.get(selection[0]);
    }//GEN-LAST:event_jTable1MouseClicked

    private void Panel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel1MousePressed
        //salvam coordonatele mouseului
        mX = evt.getX();
        mY = evt.getY();
    }//GEN-LAST:event_Panel1MousePressed

    private void Panel1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Panel1MouseDragged
        //modificam centrul
        cenX += (double)(evt.getX()-mX);
        cenY += (double)(evt.getY()-mY);
        //salvam noile coordonate ale mouse-ului
        mX = evt.getX();
        mY = evt.getY();
    }//GEN-LAST:event_Panel1MouseDragged

    private void MeniuIncarcaCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MeniuIncarcaCSVActionPerformed
        //todo de adaugat cod de selectie fisier
        JFileChooser fileChooser = new JFileChooser(); //File curDir = din setari
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV File","csv");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            if(Fisier.incarcaClienti(fileChooser.getSelectedFile().getAbsolutePath())) {
                if(Client.clienti.size()>0) {
                    Individ.best = null;
                    camion = null;
                    PornesteGenerarea.setEnabled(true);
                    enableSalveaza();
                    VitezaAlgoritm.setEnabled(true);
                    for(Client c:Client.clienti) { 
                        dx += (c.longitudine);
                        dy += (c.latitudine);
                    }
                    dx /= Client.clienti.size();
                    dy /= Client.clienti.size();
                } else {
                    PornesteGenerarea.setEnabled(false);
                    disableSalveaza();
                    VitezaAlgoritm.setEnabled(false);
                }
                FisierIncarcat.setText("Nr clienti = "+Client.clienti.size());
            }
        }
        
    }//GEN-LAST:event_MeniuIncarcaCSVActionPerformed

    private void MeniuSetariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MeniuSetariActionPerformed
        s.setVisible(true);
    }//GEN-LAST:event_MeniuSetariActionPerformed

    private void SalveazaTXTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalveazaTXTActionPerformed
        JFileChooser fileChooser = new JFileChooser(); //File curDir = din setari
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text File","txt");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                List<String> lines = Fisier.genereazaFisier(Individ.best);//Arrays.asList("The first line", "The second line");
                Path f = Paths.get(file.getAbsolutePath());
                Files.write(f, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }//GEN-LAST:event_SalveazaTXTActionPerformed
    
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
    private javax.swing.JLabel FisierIncarcat;
    private javax.swing.JPanel Grila;
    private javax.swing.JMenu MeniuFisier;
    private javax.swing.JMenuItem MeniuIncarcaCSV;
    private javax.swing.JMenuItem MeniuSetari;
    private javax.swing.JPanel Panel1;
    private javax.swing.JButton PornesteGenerarea;
    private javax.swing.JProgressBar Progres;
    private javax.swing.JMenuItem SalveazaCSV;
    private javax.swing.JMenuItem SalveazaHTML;
    private javax.swing.JMenuItem SalveazaTXT;
    private javax.swing.JComboBox<String> VitezaAlgoritm;
    private javax.swing.JLabel jElapsed;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JLabel jStatus;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    /**
     * Initializeaza ProgressBar-ul de pe pagina principala.
     * @param max Numarul maxim de generatii ce vor fi completate
     */
    public void initProgres(int max) {
        maxgen = max;
        Progres.setMaximum(max);
        Progres.setValue(0);
        for(int i=0;i<procente.length;i++) procente[i]=0;
        Progres.setStringPainted(true);
    }
    
    public void modMax(int max) {
        Progres.setMaximum(max);
        maxgen = max;
    }
    /**
     * Seteaza valoarea curenta a progresului pe ProgressBar-ul de pe pagina principala.
     * @param index Int - id-ul thread-ului care face actualizarea
     * @param val Valoarea curenta a progresului
     */
    public void setProgres(int index, int val) {
        procente[index] = val;
        double media=0;
        for(int i:procente) media+=i;
        media/=procente.length;
        //todo https://stackoverflow.com/questions/6579789/jprogressbar-with-double-value
        //Progres.setString(NumberFormat.getPercentInstance().format(media/Progres.getMaximum()));
        Progres.setValue((int)media);
    }
    /**
     * Seteaza cea mai buna solutie din firul de executie, pentru afisare.
     * @param i individul ce urmeaza sa fie setat ca solutie noua cea mai buna
     * @param id int id-ul thread-ului care face actualizarea
     * @param text String cu textul ce urmeaza sa fie afisat, cod de debugging
     */
    public void setBest(Individ i, int id, int gen, String text) {
        if(i!=null) {
            grafic.add(new Grafic(gen,i.getFitness()));
            Double delta=0.0;
            if(Individ.best!=null) {
                delta = ((startFitness-i.getFitness())/startFitness)*100;
                if(delta == Double.POSITIVE_INFINITY || delta == Double.NEGATIVE_INFINITY) {
                    startFitness = i.getFitness();
                    delta = 0.0;
                }
            } else {
                startFitness = i.getFitness();
            }
            Individ.best = i;
            BSDistanta.setText((int)((double)Individ.best.getFitness()+Individ.celeMariDist)+" km (-"+delta.intValue()+"%)");
            BSNrCamioane.setText((Individ.best.camioane.size()+Individ.celeMariNrCamioane)+"");
            BSGeneratia.setText("("+text+")");
            enableSalveaza();
        } else {
            Individ.best = null;
            BSDistanta.setText("");
            BSNrCamioane.setText("");
            BSGeneratia.setText("");
            disableSalveaza();
        }
        model.fireTableDataChanged();
    }
    
    public void grila(Graphics2D g) {
        int h, w;
        w = Grila.getWidth();
        h = Grila.getHeight();
        double ox=10, oy=h-10;
        double lx = w-20, ly = h-20;
        //axele de baza
        g.setColor(Color.black);
        g.setStroke(new BasicStroke(3));
        g.draw(new Line2D.Double(ox, oy, ox, 10)); //axa oy
        g.draw(new Line2D.Double(ox, oy, lx, oy)); //axa ox
        
        //graficul efectiv
        ox+=2; //incrementare sa depaseasca valoarea stroke-ului
        if(grafic.size()>1) {
            g.setColor(Color.blue);
            g.setStroke(new BasicStroke(1));
            double x=ox, fx = lx/maxgen;
            double fy = ly / grafic.get(0).fitness;
            for(int i=1;i<grafic.size();i++) {
                Grafic v1 = grafic.get(i-1);
                Grafic v2 = grafic.get(i);
                g.draw(new Line2D.Double(v1.generatia*fx+ox, h-v1.fitness*fy, v2.generatia*fx+ox, h-v2.fitness*fy));
            }
        }
        
    }
    
    /**
     * Metoda de redesenare a jPanel-ului de pe MainFrame.
     * @param g Graphics2D trimis de catre jPanel.repaint()
     */
    public void deseneaza(Graphics2D g)  {
        factorScalare = (double)jSlider1.getValue();
        double max_x = Panel1.getWidth();
        double max_y = Panel1.getHeight();
        if(cenX==-1 || cenY ==-1) {
            cenX = max_x/2;
            cenY = max_y/2;
        }
        //deseneaza casa;
        g.setColor(Color.blue);
        g.draw(new Ellipse2D.Double((cenX + ((Setari.casa.longitudine-dx)*factorScalare))-3, (cenY - ((Setari.casa.latitudine-dy)*factorScalare))-3,6,6)); 
        //deseneaza restul clientilor
        g.setColor(Color.black);
        double x, y, x2,y2;
        for(Client c:Client.clienti) {
            x = cenX + ((c.longitudine-dx)*factorScalare); 
            y = cenY - ((c.latitudine-dy)*factorScalare);
            //g.drawOval((int)(x-1.5),(int)(y-1.5), 3, 3);
            g.draw(new Ellipse2D.Double(x-1.5, y-1.5,3,3));
        }
        // partea de desenare drumuri pt camionul selectat
        if(camion!=null) {
            //linia de acasa pana la primul:
            g.setColor(Color.BLUE);
            x = cenX + ((Setari.casa.longitudine-dx)*factorScalare); 
            y = cenY - ((Setari.casa.latitudine-dy)*factorScalare);
            x2 = cenX + ((Client.clienti.get(camion.pachete.get(0)).longitudine-dx)*factorScalare); 
            y2 = cenY - ((Client.clienti.get(camion.pachete.get(0)).latitudine-dy)*factorScalare);
            g.draw(new Line2D.Double(x, y, x2, y2));
            g.setColor(Color.black);
            for(int i=0;i<camion.pachete.size()-1;i++) {
                int i1 = camion.pachete.get(i);
                int i2 = camion.pachete.get(i+1);
                x = cenX + ((Client.clienti.get(i1).longitudine-dx)*factorScalare); 
                y = cenY - ((Client.clienti.get(i1).latitudine-dy)*factorScalare);
                x2 = cenX + ((Client.clienti.get(i2).longitudine-dx)*factorScalare); 
                y2 = cenY - ((Client.clienti.get(i2).latitudine-dy)*factorScalare);
                g.draw(new Line2D.Double(x, y, x2, y2));
            }
        }
    }
    /**
     * Afisare mesaj eroare.
     * Metoda statica pentru afisare mesaj de eroare de oriunde
     * @param msg String cu mesajul dorit
     */
    public static void mesajEroare(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Eroare!", JOptionPane.ERROR_MESSAGE);
    }
    /**
     * Afisare mesaj de atentionare.
     * Metoda statica pentru afisare mesaj de atentionare
     * @param msg String cu mesajul dorit
     */
    public static void mesajAtentie(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Atentie!", JOptionPane.WARNING_MESSAGE);
    }
    /**
     * Activeaza optiunile de salvare din meniu.
     */
    private void enableSalveaza() {
        SalveazaCSV.setEnabled(true);
        SalveazaTXT.setEnabled(true);
        SalveazaHTML.setEnabled(true);
    }
    /**
     * Dezactiveaza optiunile de salvare din meniu.
     */
    private void disableSalveaza() {
        SalveazaCSV.setEnabled(false);
        SalveazaTXT.setEnabled(false);
        SalveazaHTML.setEnabled(false);
    }
    
    public void printDifference(){
		//milliseconds
		long different = System.currentTimeMillis()-start;

		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;
		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;
		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;
		long elapsedSeconds = different / secondsInMilli;
                
                jElapsed.setText(cZ(elapsedHours)+":"+cZ(elapsedMinutes)+":"+cZ(elapsedSeconds));
    }
    
    private String cZ(long nr) {
        if(nr<10) {
            return "0"+nr;
        } else {
            return ""+nr;
        }
    }
    public void setStatus(String text) {
        jStatus.setText("[ "+text+" ]");
    }
}
