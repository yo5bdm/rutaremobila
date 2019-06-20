/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfata;

import algoritm.Individ;
import algoritm.Camion;
import static interfata.MainFrame.*;
import javax.swing.table.AbstractTableModel;

/**
 * Clasa care ofera datele pentru tabelul de pe pagina principala
 * @author yo5bdm
 */
public class ModelTabel extends AbstractTableModel {

    String[] coloane = {"Distanta","Capacitate","Ocupat","Opriri","OK"};
    private final Class[] columnClass = new Class[] {
        String.class, Integer.class, String.class, Integer.class, String.class
    };
    
    @Override
    public int getRowCount() {
        if(m==null || Individ.best ==null) return 0;
        return Individ.best.camioane.size();
    }
    @Override
    public int getColumnCount() {
        return coloane.length;
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Camion c = Individ.best.camioane.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return (int)c.distanta+" km";
            case 1:
                return c.capacitate;
            case 2:
                return c.ocupat().intValue()+" %"; 
            case 3:
                return c.opriri;
            default:
                if(c.ok) return "Da";
                else return "Nu";
        }
    }
    @Override
    public String getColumnName(int nr) {
        return coloane[nr];
    }
}
