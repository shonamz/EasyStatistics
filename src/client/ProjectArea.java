package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import controller.CommandController;
import exceptions.InconsistentModelDimensionException;
import exceptions.ModelCannotHaveNegativeValues;
import exceptions.ModelMustHaveCategoricalDependentVariable;
import exceptions.NotFoundException;
import exceptions.VariableContainsNonNumericException;
import exceptions.VariableMustContainValuesException;
import statistics.ListShowable;
import statistics.ModelList;
import statistics.VariableList;
import statistics.VariableList.Variable;

public class ProjectArea extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel sidePanel;
	private JTextField commandLine;
	private ArrayList<String> commandHistoryModel;
	private String tabName;
	public VariableList variables; // Actual Variables
	public ModelList models; // Actual Models
	public CommandController commandController;
	private JPanel displayArea;
	private DisplayUpdateThread displayUpdateThread;
	private int commandHistoryNumber;
	private JPanel currentShowPanel;
	boolean autoRefreshDisplay;

	// new project constructor
	ProjectArea() {
		super(new BorderLayout());
		
		// Initial options
		autoRefreshDisplay = false;
		commandHistoryNumber = 0;
		tabName = "New Project Area";
		commandController = new CommandController(this);
		// add this to tabbed pane
		MainGUI.tabbedPane.addTab(tabName, this);
		
		// Create command history
		commandHistoryModel = new ArrayList<String>();

		// Create elements for display area
		displayArea = new JPanel();
		add(displayArea, BorderLayout.CENTER);

		// Create elements for side panel
		sidePanel = new JPanel(new BorderLayout());
		sidePanel.setPreferredSize(new Dimension(175, 600));
		add(sidePanel, BorderLayout.EAST);

		// Variable list
		variables = new VariableList(this);
		
		JScrollPane varScroller = new JScrollPane(variables.variableList);
		varScroller.setBorder(new TitledBorder("My Variables"));
		sidePanel.add(varScroller, BorderLayout.CENTER);
		
		// Model list
		models = new ModelList(this);
		
		JScrollPane modelScroller = new JScrollPane(models.modelList);
		modelScroller.setBorder(new TitledBorder("My Models"));
		sidePanel.add(modelScroller, BorderLayout.SOUTH);
		
		// Set up Command Line

		commandLine = new JTextField();
		commandLine.setPreferredSize(new Dimension(this.getWidth(), commandLine.getPreferredSize().height + 5));
		commandLine.addKeyListener(new KeyAdapter() {

			@Override // Toggle through command history
			public void keyPressed(KeyEvent ke) {
				int num = commandHistoryNumber;
				boolean proceed = false;
				if(ke.getKeyCode()==KeyEvent.VK_UP) {
					proceed = true;
					num --;
				} else if (ke.getKeyCode()==KeyEvent.VK_DOWN){
					proceed = true;
					num ++;
				}
				if(proceed && (num >= 0 && num < commandHistoryModel.size())) {
					commandLine.setText(commandHistoryModel.get(num));
					commandHistoryNumber = num;
				} else if (proceed && num < 0) {
					commandHistoryNumber = 0;
				} else if (proceed && num >= commandHistoryModel.size()) {
					commandHistoryNumber = commandHistoryModel.size();
					commandLine.setText("");
				}
			}
			
		});
		
		
		JScrollPane commandPanel = new JScrollPane(commandLine);
		commandPanel.setBorder(new TitledBorder("Command Line"));
		this.add(commandPanel, BorderLayout.SOUTH);

		// Command Line Listener
		commandLine.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Start a new command thread and add command to history
				String command = commandLine.getText();
				commandHistoryModel.add(command);
				commandHistoryNumber = commandHistoryModel.size();
				commandLine.setText("");
				setCurrentCommand(command);

			}

		});
		

		// set index of tabbed pane
		MainGUI.tabbedPane.setSelectedIndex(MainGUI.tabbedPane.getTabCount() - 1);
		
		// set focus in command bar
		commandLine.requestFocus();
	}
	
	public void setCurrentCommand(String command) {
		JPanel toDisplay = commandController.getResults(command);
		if(toDisplay != null) { // Command that requires the display
			ProjectArea.this.display(toDisplay);
			ProjectArea.this.revalidate();
			
			// Start or update thread that checks for updates
			if(displayUpdateThread == null) {
				displayUpdateThread = new DisplayUpdateThread(command);
				displayUpdateThread.start();

			} else {
				displayUpdateThread.setCommand(command);
			}
		}
	}

	
	private class DisplayUpdateThread extends Thread {
		private String currentCommand;
		
		DisplayUpdateThread(String currentCommand) {
			super();
			this.currentCommand = currentCommand;
		}
		
		public void setCommand(String newCommand) {
			currentCommand = newCommand;
		}
		
		@Override
		public void run() {
			while(true) {
				if(variables.hasUpdates() && autoRefreshDisplay) {
					variables.notifyUpdated();
					
					// Check for variable updates
					JPanel toDisplay = commandController.getResults(currentCommand);
					
					if(toDisplay == null) break; // error somewhere
					
					ProjectArea.this.display(toDisplay);
					ProjectArea.this.revalidate();
				}
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// Return
					return;
				}
			}
		}	
	}
	

	public void display(JPanel panel) {
		if (panel != null) {
			remove(displayArea);
			displayArea = panel;
			add(displayArea, BorderLayout.CENTER);
		}
		revalidate();
	}
	
	public ArrayList<String> getAllNames() {
		ArrayList<String> names = variables.getVariableNames();
		names.addAll(models.getModelNames());
		return names;
	}
	
	public void setCurrentShowPanel(ListShowable showable) {
		// Hide current panel
		hideCurrentShowPanel();

		// Get and display the show panel
		// Expensive operation so we do this in a thread
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					currentShowPanel = showable.panel();
				} catch (VariableContainsNonNumericException | VariableMustContainValuesException
						| InconsistentModelDimensionException | ModelMustHaveCategoricalDependentVariable | ModelCannotHaveNegativeValues e) {
					// Suppress errors since the user might edit the variable 
					e.printStackTrace();
				}
				add(currentShowPanel, BorderLayout.WEST);
				currentShowPanel.revalidate();
				revalidate();
			}
		};
		
		t.start();
	}
	
	public void hideCurrentShowPanel() {
		// Check if show panel already exists
		if(currentShowPanel != null) remove(currentShowPanel);
		currentShowPanel = null;
		revalidate();
	}
	
	public void hideShowPanel(Variable showable) {
		if(currentShowPanel != null && currentShowPanel == showable.panel()) {
			hideCurrentShowPanel();
		}
	}
	
	// Finds Variable or Model by name
	public ListShowable findByName(String name) throws NotFoundException {
		ListShowable target = null;
		try {
			target = variables.findByName(name);
		} catch (Exception e) {
			try {
				target = models.findByName(name);
			} catch (Exception e1) {
				// Not found :(
				throw new NotFoundException(name);
			}
		}
		return target;
	}
		
}
