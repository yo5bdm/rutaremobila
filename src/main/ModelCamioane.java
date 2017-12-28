/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.table.AbstractTableModel;
import static main.MainFrame.*;

/**
 * Modelul folosit pentru afisarea datelor in tabelul de pe pagina de setari.
 * @author yo5bdm
 */
public class ModelCamioane extends AbstractTableModel {
    
    private final String[] coloane = {"Capacitate","Disponibile"};
    private final Class[] columnClass = new Class[] {
        Integer.class, Integer.class
    };

    /**
     * Returneaza numarul de linii a listei.
     * @return numarul de linii
     */
    @Override
    public int getRowCount() {
        if(setari.camDisponibile == null) return 0;
        return setari.camDisponibile.size();
    }
    /**
     * Returneaza numarul de coloane a listei.
     * @return numarul de coloane
     */
    @Override
    public int getColumnCount() {
        return coloane.length;
    }
    /**
     * Returneaza valoarea de pe o anumita pozitie.
     * @param rowIndex index-ul liniei
     * @param columnIndex indexul coloanei
     * @return valoarea de la acea pozitie
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CamDisp c = setari.camDisponibile.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return c.capacitate;
            default:
                return c.disponibile;
        }
    }
    /**
     * Returneaza numele coloanei.
     * @param nr indexul coloanei
     * @return String cu numele coloanei
     */
    @Override
    public String getColumnName(int nr) {
        return coloane[nr];
    }
}
