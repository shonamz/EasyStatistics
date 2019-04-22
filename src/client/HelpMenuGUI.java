package client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import controller.CommandController;

public class HelpMenuGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JList<String> commandList;
	private DefaultListModel<String> listModel;
	private JTextField searchBar;
	private List<String> all_command_names;
	private List<String> current_command_names;
	private JTextPane displayText;
	private MainGUI parent;
	private static final String[] commands = new String[]{
			"coefs",
			"Returns the estimated coefficients of a given model<br/><b>Usage: coeffs [Model]</b>",
			
			"coef",
			"Returns the estimated coefficient of a given independent variable in a model<br/><b>Usage coeff [Model] [Variable]<b/>",
			
			"cor",
			"Returns the correlation between two Variables<br/><b>Usage: cor [Variable] [Variable]<b/>",
			
			"cov",
			"Returns the covariance between two Variables<br/><b>Usage: cov [Variable] [Variable]<b/>",

			"mean",
			"Returns the mean a Variable or Model<br/><b>Usage: mean [Variable/Model]<b/>",

			"median",
			"Returns the median a Variable or Model<br/><b>Usage: median [Variable/Model]<b/>",

			"mode",		
			"Returns the mode a Variable or Model<br/><b>Usage: mode [Variable/Model]<b/>",

			"var",
			"Returns the variance a Variable or Model<br/><b>Usage: var [Variable/Model]<b/>",
			
			"stddev",
			"Returns the standard deviation a Variable or Model<br/><b>Usage: stddev [Variable/Model]<b/>",

			"linmod",
			"Generates a linear Model<br/><b>Usage: new [Model] linmod [Independent Variable] [Dependent Variables]<b/>",

			"lm",
			"Alias for linmod",
			
			"poisson",
			"Generates a poisson Model<br/><b>Usage: new [Model] poisson [Independent Variable] [Dependent Variables]<b/>",

			"semilog",
			"Alias for poisson",
			
			"log",
			"Generates a log-log Model<br/><b>Usage: new [Model] log [Independent Variable] [Dependent Variables]<b/>",

			"log-log",
			"Alias for log",
			
			"loglin",
			"Alias for poisson",
			
			"quad",
			"Generates a quadratic Model<br/><b>Usage: new [Model] quad [Independent Variable] [Dependent Variables]<b/>",
			
			"quadratic",
			"Alias for quad",
			
			"linear",
			"Alias for linmod",
			
			"logm",
			"Alias for loglog",
			
			"histogram",
			"Returns a histogram of a Variable or Model<br/><b>Usage: histogram [Variable/Model] "+CommandController.OPTIONS_SEPARATOR+"[OPTIONS]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">density=[true/false]<br/>frequency=[true/false]</span>",

			"hist",
			"Alias for histogram",
			
			"scatter [Variable/Model]",
			"Returns a scatter plot of a Y Variables or Models plotted over X Variable or Model<br/><b>Usage: scatter [X Variable/Model] [Y Variables/Models]<b/>",

			
			"rename",
			"Renames a Variable or Model<br/><b>Usage: rename [OldName] [NewName]<b/>",

			"normal",
			"Generates a Variable with Normal distribution<br/><b>Usage: new [Variable] normal "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>mean=[Float]<br />stddev=[Float]</span>",

			"chisquared",
			"Generates a Variable with Chi-squared distribution<br/><b>Usage: new [Variable] chisquared "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>df=[Integer]</span>",

			"f",
			"Generates a Variable with F distribution<br/><b>Usage: new [Variable] f "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>df1=[Integer]<br />df2=[Integer]</span>",

			"t",
			"Generates a Variable with Student T distribution<br/><b>Usage: new [Variable] t "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>df=[Integer]</span>",

			"uniform",
			"Generates a Variable with Uniform distribution<br/><b>Usage: new [Variable] uniform "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>mean=[Float]</span>",
			
			"pois",
			"Generates a Variable with Poisson distribution<br/><b>Usage: new [Variable] pois "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>mean=[Float]</span>",

			"exp",
			"Generates a Variable with Exponential distribution<br/><b>Usage: new [Variable] exp "+CommandController.OPTIONS_SEPARATOR+" [Options]<b/>"
					+"<br/><b>Options:<b/><br/><span style=\"font-weight: normal;\">n=[Integer]<br/>mean=[Float]</span>",
			
			"new [ModelType]",
			"Creates a new Model<br/><b>Usage: new [ModelType] [ModelName]<b/>",
			
			"delete",
			"Deletes a Variable or Model<br/><b>Usage: delete [Variable/Model]<b/>",
			
			"new",
			"Creates a new Variable<br/><b>Usage: new [Variable]<b/>"
				
		};
	
	public HelpMenuGUI(MainGUI mainGUI) {
		super("Command List");
		parent = mainGUI;
		JPanel pageWrapper = new JPanel(new BorderLayout());
		JPanel innerWrapper = new JPanel(new BorderLayout());
		pageWrapper.setBorder(new EmptyBorder(10,10,10,10));
		pageWrapper.add(innerWrapper, BorderLayout.CENTER);
		innerWrapper.setBorder(new TitledBorder("Search through available commands"));

		all_command_names = new Vector<String>(Arrays.asList(commands));
				
		listModel = new DefaultListModel<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getElementAt(int i) {
				// TODO Auto-generated method stub
				return current_command_names.get(i);
			}

			@Override
			public int getSize() {
				// TODO Auto-generated method stub
				return current_command_names.size();
			}
			
		};
		
		commandList = new JList<String>(listModel);
		current_command_names = new Vector<String>();
		refreshNames("");
		
		innerWrapper.add(new JScrollPane(commandList), BorderLayout.CENTER);
		
		commandList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent lse) {
				String toShow = commandList.getSelectedValue();
				if(toShow != null) {
					displayText.setText("<html>"+all_command_names.get(all_command_names.indexOf(toShow)+1)+"</html>");
					displayText.setContentType("text/html");
				}
			}
			
		});
		
		// search bar
		
		searchBar = new JTextField();
		innerWrapper.add(searchBar, BorderLayout.NORTH);
		searchBar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String text = searchBar.getText();
				if(text == null) text = "";
				refreshNames(searchBar.getText());
			}
			
		});
		searchBar.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				String text = searchBar.getText();
				if(text == null) text = "";
				refreshNames(searchBar.getText());
			}
			
			@Override
			public void keyReleased(KeyEvent arg0) {
				String text = searchBar.getText();
				if(text == null) text = "";
				refreshNames(searchBar.getText());
			}
			
		});
		

		// result display area
		
		JPanel display = new JPanel(new BorderLayout());
		innerWrapper.add(display,BorderLayout.SOUTH);
		displayText = new JTextPane();
		display.setPreferredSize(new Dimension(200,100));
		displayText.setEditable(false);
		display.add(new JScrollPane(displayText));
		
		add(pageWrapper);

		// Default close
		
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				hideMenu();
			}
			
		});
		this.setSize(400, 500);
	}

	public void displayMenu() {
		setLocationRelativeTo(parent);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	public void refreshNames(String search) {
		current_command_names.clear();
		for(int i = 0; i < all_command_names.size(); i+=2) {
			String c = all_command_names.get(i);
			if(search == null || search.length() == 0 || c.toLowerCase().contains(search.toLowerCase())) current_command_names.add(c);
		}
		current_command_names.sort(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareToIgnoreCase(o2);
			}
			
		});
		
		commandList.repaint(); commandList.revalidate();
	}
	
	public void hideMenu() {
		setVisible(false);
	}
	
	public static String[] getCommands() {
		String[] toReturn = new String[commands.length/2];
		for(int i = 0; i < toReturn.length; i++) {
			toReturn[i] = commands[2*i];
		}
		return toReturn;
	}

}
