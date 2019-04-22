package statistics;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVReader;

import client.ProjectArea;
import exceptions.DuplicateNameException;
import exceptions.InvalidNameException;
import resources.Config;
import statistics.VariableList.Variable;

public class CSVParser {
	
	private static void parseVariablesFromFileHelper(ProjectArea projectArea, CSVReader r) throws IOException {
		
		VariableList variables = projectArea.variables;
		// First row contains variable names
		String[] varNames = r.readNext();
		
		Variable[] newVars = new Variable[varNames.length];
		
		// create the variables and handle variable name errors
		for (int i = 0; i < varNames.length; i++) {
			// Handle duplicate/Invalid variable names on the fly
			try {
				newVars[i] = variables.new Variable(new ArrayList<Double>(), varNames[i]);
				
			} catch (DuplicateNameException e) {
				// Duplicate variable name found
				projectArea.display(projectArea.commandController.defaultPanel("Parse Error", e.getMessage(), true));
				return;
			} catch (InvalidNameException e) {
				// Invliad variable name found
				projectArea.display(projectArea.commandController.defaultPanel("Parse Error", e.getMessage(), true));
				return;
			} 
		}

		// get these values
		String[] nextValues;

		while ((nextValues = r.readNext()) != null) {
			for (int i = 0; i < varNames.length; i++) {
				if (i < nextValues.length && nextValues[i] != null && newVars[i] != null) {
					if(!Config.isNullValuePolicyStrict()) {
						newVars[i].add(nextValues[i]);
					} else {
						try{
							Double.parseDouble(nextValues[i]);
							newVars[i].add(nextValues[i]);
						} catch (NumberFormatException nfe) {
							// Do nothing
						}
					}
				
				}
			}
		}
	
	}
	
	// The method that actually does calls the import parser
	public static void parseVariablesFromFile(File file, ProjectArea projectArea) {
		//  Check the file
		if (file == null) {
			// no file
			projectArea.display(projectArea.commandController.defaultPanel("Parse Error", "Please enter a valid file", true));
			return;
		}
		if (!file.getName().endsWith(".csv")) {
			// bad file extension
			projectArea.display(projectArea.commandController.defaultPanel("Parse Error", file.getName()+" is not a csv file.", true));
			return;
		}
		// import variables
		CSVReader r = null;
		try {
			r = new CSVReader(new FileReader(file));
			CSVParser.parseVariablesFromFileHelper(projectArea, r);
		} catch (IOException e) {
			// File exception
			projectArea.display(projectArea.commandController.defaultPanel("Parse Error", "Error reading from "+file.getName()+".", true));
			return;
		} finally {
			if (r!=null) {
				try {
					r.close();
				} catch (IOException e) {
					// Error closing file reader
				}
			}
		}
	}

}
