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

    
    String[] coloane = {"Fitnes"};
    private final Class[] columnClass = new Class[] {
        Double.class
    };
    
    @Override
    public int getRowCount() {
        if(a==null || a.populatie ==null) return 0;
        return a.populatie.size();
    }

    @Override
    public int getColumnCount() {
        return coloane.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return a.populatie.get(rowIndex).getFitnes();
    }
    @Override
    public String getColumnName(int nr) {
        return coloane[nr];
    }
    
}
