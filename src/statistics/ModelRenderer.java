package statistics;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import resources.Constants;
import statistics.ModelList.AbstractModel;

public class ModelRenderer extends JLabel implements ListCellRenderer<AbstractModel> {
	private static final long serialVersionUID = 1L;
	
	public ModelRenderer() { 
	    setOpaque(true); 
	}


	@Override
	public Component getListCellRendererComponent(JList<? extends AbstractModel> list, AbstractModel model, int index, boolean isSelected,
			boolean cellHasFocus) {

		if(model.hasNull()) {
			setForeground(Constants.NULL_VALUES_COLOR);
		} else if (model.hasDifferentVariableLengths()) {
			setForeground(Constants.INCONSISTENT_MODEL_COLOR);
		} else {
			setForeground(list.getForeground());
		}
		
		if (isSelected) {
		    setBackground(list.getSelectionBackground()); 
		} else { 
		    setBackground(list.getBackground()); 

		}
		
		setText("  "+model.toString());
		return this;
	}
}
