package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import controller.CommandController;
import resources.Config;

public class GlobalOptionsGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private MainGUI parent;
	
	class Option {
		private String title;
		String val;
		Option(String title, String val) {
			this.title = title;
			this.val = val;
		}
		
		@Override
		public String toString() {
			return title;
		}
	}
	
	class BooleanOption {
		private String title;
		Boolean val;
		BooleanOption(String title, Boolean val) {
			this.title = title;
			this.val = val;
		}
		
		@Override
		public String toString() {
			return title;
		}
	}
	public GlobalOptionsGUI(MainGUI mainGUI) {
		super("Global Settings");
		parent = mainGUI;
		JPanel pageWrapper = new JPanel(new BorderLayout());
		JPanel innerWrapper = new JPanel(new GridLayout(4,2));
		pageWrapper.setBorder(new EmptyBorder(10,10,10,10));
		pageWrapper.add(innerWrapper, BorderLayout.CENTER);
		innerWrapper.setBorder(new TitledBorder("Options"));
		
		// options separator option
		innerWrapper.add(new JLabel("Options Separator"));
		
		JComboBox<Option> optionsSeparator = new JComboBox<Option>();
		optionsSeparator.addItem(new Option("|","|"));
		optionsSeparator.addItem(new Option("~","~"));
		optionsSeparator.addItem(new Option(";",";"));
		optionsSeparator.addItem(new Option(":",":"));
		optionsSeparator.addItem(new Option("?","?"));
		optionsSeparator.setSelectedIndex(0);
		optionsSeparator.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				Option o = (Option) optionsSeparator.getSelectedItem();
				CommandController.setOptionsSeparator(o.val);
			}
			
		});
		
		innerWrapper.add(optionsSeparator);

		
		// auto remove null policy
		innerWrapper.add(new JLabel("Null/Non-Numeric Import Policy"));
		
		JComboBox<BooleanOption> nullValuePolicy = new JComboBox<BooleanOption>();
		nullValuePolicy.addItem(new BooleanOption("Allow",false));
		nullValuePolicy.addItem(new BooleanOption("Auto-Remove",true));
		nullValuePolicy.setSelectedIndex(0);
		nullValuePolicy.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				BooleanOption o = (BooleanOption) nullValuePolicy.getSelectedItem();
				Config.setNullValuePolicyStrict(o.val);
			}
			
		});
		innerWrapper.add(nullValuePolicy);


		// model dimension policy
		innerWrapper.add(new JLabel("Model Dimension Policy"));

		JComboBox<BooleanOption> modelDimensionError = new JComboBox<BooleanOption>();
		modelDimensionError.addItem(new BooleanOption("Auto-Determine Range",false));
		modelDimensionError.addItem(new BooleanOption("Strict",true));
		modelDimensionError.setSelectedIndex(1);
		modelDimensionError.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent arg0) {
				BooleanOption o = (BooleanOption) modelDimensionError.getSelectedItem();
				Config.setModelDimensionPolicyStrict(o.val);
			}
			
		});
		
		innerWrapper.add(modelDimensionError);
		
		
		// Auto refresh option
		
		// Auto-refresh display
		JToggleButton refreshDisplayAction = new JToggleButton("Auto-Refresh", new ImageIcon("resources/icons/refresh.png"));
		refreshDisplayAction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				if(MainGUI.tabbedPane.getTabCount() > 0) {
					ProjectArea pa = (ProjectArea)MainGUI.tabbedPane.getSelectedComponent();
					if(pa != null) pa.autoRefreshDisplay = !pa.autoRefreshDisplay;
					refreshDisplayAction.setSelected(pa.autoRefreshDisplay);
				}
			}
			
		});
		refreshDisplayAction.setToolTipText("Auto-Refresh Display Area");
		innerWrapper.add(new JLabel("Automatically refresh the display area"));
		innerWrapper.add(refreshDisplayAction);
		
		add(pageWrapper);

		// Default close
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				hideMenu();
			}
			
		});
		this.setSize(500, 250);
	}

	public void displayMenu() {
		setLocationRelativeTo(parent);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	

	public void hideMenu() {
		setVisible(false);
	}

}
