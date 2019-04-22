package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jfree.chart.ChartPanel;

import client.HelpMenuGUI;
import client.ProjectArea;
import exceptions.DuplicateNameException;
import exceptions.InconsistentModelDimensionException;
import exceptions.InvalidNameException;
import exceptions.ModelCannotHaveNegativeValues;
import exceptions.ModelMustHaveCategoricalDependentVariable;
import exceptions.NotFoundException;
import exceptions.VariableContainsNonNumericException;
import exceptions.VariableMustContainValuesException;
import resources.Constants;
import resources.Util;
import statistics.ListShowable;
import statistics.VariableList.Variable;

public class CommandController {
	// Member variables
	// Options delimiter for commands
	public static char OPTIONS_DELIMITER = ',';
	public static char OPTIONS_SEPARATOR = '|';

	private ProjectArea projectArea;

	public CommandController(ProjectArea projectArea) {
		this.projectArea = projectArea;
	}

	// Helper for the getResults method
	public JPanel getResultsHelper(String command, Hashtable<String,String> optionsHash) throws NotFoundException, VariableMustContainValuesException,
			DuplicateNameException, InvalidNameException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, CommandNotFoundException {
		if (command == null || command.trim().length() == 0) {
			return null;
		}
		command = command.trim();
		// Extract options before splitting command
		//  if we haven't already been passed the options
		if (optionsHash == null) {
			String[] commandSep = command.split("\\"+OPTIONS_SEPARATOR);
			if(commandSep.length > 1) {
				// we have a options
				String paramString = commandSep[1];
				command = commandSep[0].trim();
				optionsHash = Util.stringToHash(paramString);
			}
		}
		// Get command and arguments into an array
		String[] words = command.split("\\s+");

		if (words[0].equals("new")) {
			// Special case since "new" is a Java token
			if (words.length > 1) { 
				if(words.length == 2) {
					// Nothing else to do
					projectArea.variables.new Variable(new ArrayList<Double>(), words[1]);
					return null; 
				} else {
					// Check if creating new model
					if(Constants.MODEL_TYPES.contains(words[1])) {
						
						// Must have at least 1 ind and 1 dep variable
						String name = words[2];
						String indVarName = null;
						List<String> depVarNames = null;
						
						if(words.length >= 4) indVarName = words[3];
						if(words.length >= 5) depVarNames = Arrays.asList(words).subList(4, words.length);
						
						projectArea.models.newByModelType(words[1],indVarName,depVarNames,name);
						return null;
					} 
					
					String varName = words[1];
					projectArea.variables.new Variable(new ArrayList<Double>(), varName);

					// Get the right command to call
					String method = words[2];
					command = method + " " + varName;
					for(int i = 3; i < words.length; i++) {
						command += " " + words[i];
					}

					// Recursively call helper method
					return getResultsHelper(command, optionsHash);
				}
			} else {
				return errorPanel(command, new InvalidNameException(null,false,false,false));
			}
			
		} else if (words.length == 1) {
			// Show the variable
			ListShowable ls = projectArea.findByName(words[0]);
			// Throws error if not found
		    if(ls instanceof Variable) {
		    	ls.show(); 
				return null;

		    } else {
		    	return ls.panel();
		    }

		} else if (words.length >= 2) {
			// These methods should be able for the project area to update them
			
			// Check if we have a variable or a model
			String name = words[1];
			ListShowable target = projectArea.findByName(name); // Throws error if not found
			
			// Get the class
			Class<? extends Object> classToCall = target.getClass();
			
			// Try to find method in variable class
			Method method = null;
			boolean error = false;
			String methodString = words[0];

			try {
				method = classToCall.getMethod(methodString, ArrayList.class, Hashtable.class);
				
			} catch (SecurityException e) {
				// Privacy issue (not a valid command to return)
				error = true;
			} catch (NoSuchMethodException e) {
				// Method does not exist
				error = true;
				
			}
			// Make sure the method is declared in the Variable class or Model Class
			// ** SECURITY RELIES ON THIS **
			if(method == null || method.getDeclaringClass().isInstance(classToCall)) {
				error = true;
			}
			
			if (!error) {
				// get other mandatory arguments
				ArrayList<String> otherVars = new ArrayList<String>();
				
				for (String s : Arrays.copyOfRange(words, 2, words.length)) {
					otherVars.add(s);
				} 
			
				// Try to invoke method
				try {
					Object results = method.invoke(target, otherVars, optionsHash);
					if (results != null) {
						// Check return type to see if we are getting a chart or a panel
						if (method.getReturnType() == ChartPanel.class || method.getReturnType() == JPanel.class) {
							return (JPanel) results;
						} else {
							return defaultPanel(command, results.toString());
						}
					} else {
						// Not a value to return
						return null;
					}

				} catch (IllegalAccessException e) {
					// Error
				} catch (IllegalArgumentException e) {
					// Error

				} catch (InvocationTargetException e) {
					// Exception raised so let's reraise the actual exception
					// if the exception is important
					if (e.getTargetException() instanceof VariableMustContainValuesException) {
						throw (VariableMustContainValuesException)e.getTargetException();
						
					} else if (e.getTargetException() instanceof DuplicateNameException) {
						throw (DuplicateNameException)e.getTargetException();

					} else if (e.getTargetException() instanceof InvalidNameException) {
						throw (InvalidNameException)e.getTargetException();

					} else if (e.getTargetException() instanceof VariableContainsNonNumericException) {
						throw (VariableContainsNonNumericException)e.getTargetException();

					} else if (e.getTargetException() instanceof NotFoundException) {
						throw (NotFoundException)e.getTargetException();

					} else if (e.getTargetException() instanceof InconsistentModelDimensionException) {
						throw (InconsistentModelDimensionException)e.getTargetException();
					} else if (e.getTargetException() instanceof ModelMustHaveCategoricalDependentVariable) {
						throw (ModelMustHaveCategoricalDependentVariable)e.getTargetException();
					} else if(e.getTargetException() instanceof ModelCannotHaveNegativeValues) {
						throw (ModelCannotHaveNegativeValues)e.getTargetException();
					}
				}
			} else {
				throw new CommandNotFoundException();
			}

		} 

		// Command not found
		// 	return null to prevent refreshing
		return null;
	}

	public JPanel getResults(String command) {
		// Call getResultsHelper and check for Exceptions
		// if we get a "FATAL" exception, make sure to return null 
		//	to prevent refreshing
		try {
			return getResultsHelper(command,null);
		} catch (NotFoundException e) {
			// Return error panel
			projectArea.display(errorPanel(command, e));
		} catch (VariableMustContainValuesException e) {
			// Command requires values but there are none
			projectArea.display(errorPanel(command, e));

		} catch (DuplicateNameException e) {
			// Variable name already exists
			projectArea.display(errorPanel(command, e));

		} catch (InvalidNameException e) {
			// Variable name already exists
			projectArea.display(errorPanel(command, e));

		} catch (VariableContainsNonNumericException e) {
			// TODO Auto-generated catch block
			projectArea.display(errorPanel(command, e));

		} catch (InconsistentModelDimensionException e) {
			// TODO Auto-generated catch block
			projectArea.display(errorPanel(command, e));

		} catch (ModelMustHaveCategoricalDependentVariable e) {
			projectArea.display(errorPanel(command, e));

		} catch (ModelCannotHaveNegativeValues e) {
			projectArea.display(errorPanel(command, e));
			
		} catch (CommandNotFoundException e) {
			projectArea.display(commandNotFoundPanel(command));
		}
		return null; // prevent refreshing
	}

	// Panel that is displayed when command is not found
	private JPanel commandNotFoundPanel(String command) {
		JPanel panel = new JPanel(new BorderLayout());

		JTextPane text = new JTextPane();
		JScrollPane scroller = new JScrollPane(text);
		scroller.setBorder(new TitledBorder("Invalid Command"));
		String textToAdd = "Command: '" + command + "' not recognized\nPerhaps you want:";

		// find suggestions
		String first = command.split("\\s+")[0];
		boolean foundAny = false;
		for(String s: HelpMenuGUI.getCommands()) {
			if(s.contains(first)) {
				foundAny = true;
				textToAdd += "\n"+s;
			}
		}
		
		if(!foundAny) textToAdd+= "\nNo suggestions found";
		
		text.setText(textToAdd);
		text.setFont(text.getFont().deriveFont(24f));
		text.setForeground(Color.RED);
		
		// center text
		StyledDocument doc = text.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		panel.add(scroller, BorderLayout.CENTER);

		return panel;
	}
	
	// Error Panel helper calls default panel 
	public JPanel errorPanel(String input, Exception e) {
		return defaultPanel(input, e.getMessage(), true);
	}

	// Default Panel to display numeric or text results
	public JPanel defaultPanel(String input, String output, boolean error) {
		JPanel panel = new JPanel(new BorderLayout());
		JTextPane inputText = new JTextPane();
		JScrollPane iScroller = new JScrollPane(inputText);
		JTextPane outputText = new JTextPane();
		JScrollPane oScroller = new JScrollPane(outputText);

		// command entered by user
		inputText.setText(input);
		inputText.setEditable(false);
		inputText.setFont(inputText.getFont().deriveFont(24f));
		iScroller.setBorder(new TitledBorder("Command"));
		inputText.setPreferredSize(new Dimension(projectArea.getWidth(),100));

		// results found by us
		outputText.setText(output);
		outputText.setEditable(false);
		outputText.setFont(outputText.getFont().deriveFont(20f));
		oScroller.setBorder(new TitledBorder("Result"));
		// set font if we had an error
		if (error)
			outputText.setForeground(Color.RED);

		// center text
		StyledDocument doc = inputText.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		doc = outputText.getStyledDocument();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);

		// add elements to panel
		panel.add(iScroller, BorderLayout.NORTH);
		panel.add(oScroller, BorderLayout.CENTER);
		return panel;
	}
	
	// default is no error
	public JPanel defaultPanel(String input, String output) {
		return defaultPanel(input, output, false);
	}

	public static void setOptionsSeparator(String val) {
		OPTIONS_SEPARATOR = val.trim().charAt(0);
	}
	
	public class CommandNotFoundException extends Exception {
		private static final long serialVersionUID = 1L;
		
	}
}
