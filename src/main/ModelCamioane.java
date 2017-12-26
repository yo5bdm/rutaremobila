/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author yo5bd
 */
public class ModelCamioane extends AbstractTableModel {
    
    String[] coloane = {"Capacitate","Disponibile"};
    private final Class[] columnClass = new Class[] {
        Integer.class, Integer.class
    };
    @Override
    public int getRowCount() {
        if(CamionDisponibil.dispBak == null) return 0;
        return CamionDisponibil.dispBak.size();
    }
    @Override
    public int getColumnCount() {
        return coloane.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CamDisp c = CamionDisponibil.dispBak.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return c.capacitate;
            default:
                return c.disponibile;
        }
    }
    @Override
    public String getColumnName(int nr) {
        return coloane[nr];
    }
}
