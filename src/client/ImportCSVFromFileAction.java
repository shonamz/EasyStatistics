package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import statistics.CSVParser;

public class ImportCSVFromFileAction implements ActionListener {
	private ProjectArea _projectArea;

	ImportCSVFromFileAction(ProjectArea projectArea) {
		super();
		_projectArea = projectArea;
	}

	// Make sure we have a projectArea to talk to
	private ProjectArea getOrSetupProjectArea(ProjectArea projectArea) {
		if (projectArea == null) {
			if (MainGUI.tabbedPane.getTabCount() == 0) {
				return new ProjectArea();
			} else {
				return (ProjectArea) MainGUI.tabbedPane.getSelectedComponent();
			}
		} else {
			return projectArea;
		}
	}

	@Override 
	public void actionPerformed(ActionEvent ae) {
		// Get the right project (or create new one)
		ProjectArea projectArea = getOrSetupProjectArea(_projectArea);
		// Open file chooser
		JFileChooser file_chooser = new JFileChooser();
		file_chooser.setName("Open File...");
		file_chooser.setAcceptAllFileFilterUsed(false);
		file_chooser.setFileFilter(new FileNameExtensionFilter(".csv files (*.csv)", "csv"));
		if (file_chooser.showOpenDialog(MainGUI.tabbedPane) == JFileChooser.APPROVE_OPTION) {
			// Start thread to parse variables
			Thread t = new Thread() {
				@Override
				public void run() {					
					CSVParser.parseVariablesFromFile(file_chooser.getSelectedFile(), projectArea);

				}
			};
			EventQueue.invokeLater(t);

		}

	}
	

}
