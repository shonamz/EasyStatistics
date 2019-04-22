package statistics;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;

public class ListMouseAdapter extends MouseAdapter {
	private JList<? extends ListShowable> list;
	
	public ListMouseAdapter(JList<? extends ListShowable> list) {
		super();
		this.list = list;
	}
	
	public void mouseClicked(MouseEvent evt) { 
		if (evt.getClickCount() >= 2) {
			// Double-click detected
			// Make sure list has a selection and the cursor is inside this list
			if(!list.isSelectionEmpty() && list.contains(evt.getPoint())) {
				list.getSelectedValue().show();
			}
		}
	}
}