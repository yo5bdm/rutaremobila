/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.table.AbstractTableModel;
import static main.MainFrame.*;

/**
 *
 * @author yo5bdm
 */
class ModelTabel extends AbstractTableModel {
    
    
    
    String[] coloane = {"Distanta","Ocupat","Opriri","OK"};
    private final Class[] columnClass = new Class[] {
        Double.class, Double.class, Integer.class, Boolean.class
    };
    
    @Override
    public int getRowCount() {
        if(m==null || m.best ==null) return 0;
        return m.best.camioane.size();
    }

    @Override
    public int getColumnCount() {
        return coloane.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Camion c = m.best.camioane.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return c.distanta;
            case 1:
                return c.ocupat(); 
            case 2:
                return c.opriri;
            default:
                return c.ok;
        }
    }
    @Override
    public String getColumnName(int nr) {
        return coloane[nr];
    }
    
}
