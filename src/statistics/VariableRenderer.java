package statistics;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import resources.Constants;
import statistics.VariableList.Variable;
/*
 * This class renders the variable list
 */
public class VariableRenderer extends JLabel implements ListCellRenderer<Variable> {
	private static final long serialVersionUID = 1L;
	
	public VariableRenderer() { 
	    setOpaque(true); 
	}


	@Override
	public Component getListCellRendererComponent(JList<? extends Variable> list, Variable variable, int index, boolean isSelected,
			boolean cellHasFocus) {

		if(variable.hasNull()) {
			setForeground(Constants.NULL_VALUES_COLOR);
		} else if (variable.getValues().isEmpty()) {
			setForeground(Constants.NO_VALUES_COLOR);
		} else {
			setForeground(list.getForeground());
		}
		
		if (isSelected) {
		    setBackground(list.getSelectionBackground()); 
		} else { 
		    setBackground(list.getBackground()); 

		}
		
		setText("  "+variable.toString());
		return this;
	}
}
