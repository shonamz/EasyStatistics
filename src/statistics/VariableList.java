package statistics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.jfree.chart.ChartPanel;

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
import statistics.ModelList.AbstractModel;

public class VariableList extends DefaultListModel<VariableList.Variable> {
	private static final long serialVersionUID = 1L;
	private ProjectArea projectArea;
	public JList<Variable> variableList;
	private boolean hasUpdates;
	
	// Constructor
	public VariableList(ProjectArea projectArea) {
		this.projectArea = projectArea;
		variableList = new JList<Variable>(this);
		variableList.setCellRenderer(new VariableRenderer());
		variableList.addMouseListener(new ListMouseAdapter(variableList));
		hasUpdates = false;
		
	}

	// Getters and setters
	public Variable findByName(String name) throws NotFoundException {
		if (name != null) {
			int index = this.getVariableNames().indexOf(name);
			if (index >= 0) {
				return get(index);
			}
		}
		throw new NotFoundException(name);
	}

	public synchronized boolean hasUpdates() {
		return hasUpdates;
	}
	
	public synchronized void notifyUpdated() {
		hasUpdates = false;
	}

	public ArrayList<String> getVariableNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < this.getSize(); i++) {
			names.add(get(i).getName());
		}
		return names;
	}

	// Show variable panel
	public void show(int index) {
		if (index >= 0 && index < size()) get(index).show();
	}

	public void show(Variable var) {
		if(contains(var)) var.show(); 
	}
	
	// Override Remove to close the show window just in case it's open
	@Override
	public Variable remove(int index) {
		if(index >= 0 && index < getSize()) get(index).hide();
		return super.remove(index);
	}

	/*
	 * Variable Class
	 *
	 * Warning: Any public method not taking arguments that returns a Double or
	 * String can be called by the user
	 */
	public class Variable extends AbstractTableModel implements ListShowable, BasicStatisticObject {
		private static final long serialVersionUID = 1L;
		private ArrayList<Double> values;
		private Hashtable<Integer,String> nullValues;
		private String name;

		public Variable(ArrayList<Double> values, String name) throws DuplicateNameException, InvalidNameException {
			super();
			validateName(name);
			this.values = values;
			this.name = name;
			// set up null values
			nullValues = new Hashtable<Integer,String>();
			
			// add to variable list
			VariableList.this.addElement(this);
			
		}
		
		private JPanel createShowPanel() {
			// create show window panel
			JPanel panel = new JPanel(new BorderLayout());

			// JTable
			VariableTable table = new VariableTable(this);
			this.addTableModelListener(table);

			// Set cell renderer so we can edit stuff
			TableColumn column = table.getColumnModel().getColumn(0);
			column.setCellRenderer(new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected,
						boolean hasFocus, int row, int column) {

					Component cell = super.getTableCellRendererComponent(table, obj, isSelected, hasFocus, row, column);
					if (isSelected && row != values.size()) {
						cell.setBackground(Color.CYAN);
					} else {
						if (row == values.size()) {
							cell.setBackground(Color.WHITE);
						} else {
							// check if a valid cell
							try {
								Double.parseDouble(obj.toString());
								cell.setBackground(Color.WHITE);
							} catch (Exception e) {
								cell.setBackground(Color.RED);
							}
						}
					}
					return cell;
				}

			});

			// Table Listener to update Variable
			JButton deleteSelected = new JButton("Delete Selected");
			deleteSelected.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ae) {
					// check if there is a selection
					int[] rows = table.getSelectedRows();
					int col = table.getSelectedColumn();
					if (col == 0 && rows.length > 0) {

						// sort and reverse array to avoid index bugs
						Arrays.sort(rows);
						for (int i = 0; i < rows.length / 2; i++) {
							int tmp = rows[i];
							rows[i] = rows[rows.length - i - 1];
							rows[rows.length - i - 1] = tmp;
						}

						// remove selected values
						for (int i : rows) {
							removeValueAt(i);
						}
						// redraw
						table.revalidate();
					}
				}
			});

			// Close show bar
			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Close action
					projectArea.remove(panel);
					projectArea.revalidate();
				}

			});

			JScrollPane scroller = new JScrollPane(table);
			panel.add(scroller, BorderLayout.CENTER);
			panel.add(deleteSelected, BorderLayout.NORTH);
			panel.add(close, BorderLayout.SOUTH);
			panel.setBorder(new TitledBorder(toString()));
			panel.setPreferredSize(new Dimension(175, 600));
			
			return panel;
		}
		

		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<String,String> instance

		// Graphing Methods

		// histogram
		public ChartPanel hist(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			validateVariableValues();
			boolean frequency = true;
			// check options
			if (optionsHash != null) {
				boolean density = optionsHash.containsKey("density") && optionsHash.get("density").equals("true");
				if (density || (optionsHash.containsKey("frequency") && optionsHash.get("frequency").equals("false"))) {
					frequency = false;
				}
			} 
			return new ChartPanel(Grapher.histogram(this.name, this.values, frequency), frequency);

		}
		
		public ChartPanel scatter(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			validateVariableValues();
			
			// Check if args are present
			if(argsList != null && argsList.size() > 0) {
				ArrayList<ArrayList<Double>> allValues = new ArrayList<ArrayList<Double>>();
				for(String n: argsList) {
					ListShowable ls = projectArea.findByName(n);

					if(ls instanceof Variable) {
						allValues.add(((Variable) ls).getValues());

					} else {
						allValues.add(((AbstractModel) ls).valuesToUse(optionsHash));
					}

				}

				return new ChartPanel(Grapher.scatter(argsList,this.name, allValues, getValues()), true);
				
			} else {
				return new ChartPanel(Grapher.scatter(this.name, getValues()), true);
			}
		}

		// alternative command 
		public ChartPanel histogram(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			return hist(argsList,optionsHash);
		}

		// Other Statistical Methods
		public Double mean(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			//  validate variable
			validateVariableValues();
			
			return MathHelper.mean(values);
		}

		public Integer count() {
			return values.size();
		}
		
		public String normal(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the option 'n'
				int n = Integer.parseInt(optionsHash.get("n"));
				if(n > 0) {
					double mean;
					double stddev;
					// try to find mean and stddev in options
					try {
						mean = Double.parseDouble(optionsHash.get("mean"));
					} catch (Exception e) {
						mean = 0d;
					}
					try {
						stddev = Double.parseDouble(optionsHash.get("var"));
						stddev = Math.sqrt(stddev);
					} catch (Exception e) {
						try {
							stddev = Double.parseDouble(optionsHash.get("stddev"));
						} catch (Exception e2) {
							stddev = 1d;
						}
					}
					setValues(getNormal(n,mean,stddev));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String chisquared(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n' and 'df'
				int n = Integer.parseInt(optionsHash.get("n"));
				int df = Integer.parseInt(optionsHash.get("df"));
				if(n > 0 && df > 0) {
					setValues(getChiSquared(n,df));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String t(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n' and 'df'
				int n = Integer.parseInt(optionsHash.get("n"));
				int df = Integer.parseInt(optionsHash.get("df"));
				if(n > 0 && df > 0) {
					setValues(getTDistribution(n,df));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String f(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n' and 'df1' and 'df2'
				int n = Integer.parseInt(optionsHash.get("n"));
				int df1 = Integer.parseInt(optionsHash.get("df1"));
				int df2 = Integer.parseInt(optionsHash.get("df2"));
				if(n > 0 && df1 > 0 && df2 > 0) {
					setValues(getFDistribution(n,df1,df2));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String exp(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n' and 'mean'
				int n = Integer.parseInt(optionsHash.get("n"));
				Double mean = Double.parseDouble(optionsHash.get("mean"));
				if(n > 0 && mean > 0) {
					setValues(getExpDistribution(n,mean));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String pois(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n' and 'mean'
				int n = Integer.parseInt(optionsHash.get("n"));
				Double mean = Double.parseDouble(optionsHash.get("mean"));
				if(n > 0 && mean > 0) {
					setValues(getPoisDistribution(n,mean));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		public String uniform(ArrayList<String> argsList, Hashtable<String,String> optionsHash) {
			try {
				// Make sure we have the options 'n'
				int n = Integer.parseInt(optionsHash.get("n"));
				// Try to get options 'a' and 'b'
				double a, b;
				try {
					a = Double.parseDouble(optionsHash.get("a"));
				} catch (Exception e) {
					a = 0;
				}
				try {
					b = Double.parseDouble(optionsHash.get("b"));
				} catch (Exception e) {
					b = 1;
				}
				if(n > 0) {
					setValues(getUniform(n,a,b));
				}
			} catch (NumberFormatException nfe) {
				// number exception
			}
		
			return null;
		}
		
		

		public Double median(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			validateVariableValues();
			return MathHelper.median(values);
		}

		public Double mode(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			validateVariableValues();
			return MathHelper.mode(values);
		}

		public Double stddev(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			validateVariableValues();
			return MathHelper.stddev(values);
		}

		public Double var(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException {
			validateVariableValues();
			return MathHelper.var(values);
		}

		public Double cov(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
				throws VariableMustContainValuesException, NotFoundException, VariableContainsNonNumericException { // covariance
			Variable other = findByName(argsList.get(0));
			// Check values
			validateVariableValues();
			other.validateVariableValues();
			// good to calculate
			return MathHelper.cov(values, other.values);
		}

		public Double cor(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
				throws VariableMustContainValuesException, NotFoundException, VariableContainsNonNumericException {
			validateVariableValues();
			Variable other = findByName(argsList.get(0));
			other.validateVariableValues();
			
			return MathHelper.cor(values, other.getValues());
		}

		// Other available public methods for the command line

		public String delete(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
				throws DuplicateNameException, InvalidNameException {
			// Delete a variable if found
			VariableList.this.remove(indexOf(this));
			projectArea.variables.variableList.repaint();
			projectArea.variables.variableList.revalidate();
			return null;
		}

		public Object rename(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
				throws DuplicateNameException, InvalidNameException {
			String varName = null;
			if (argsList.size() > 0) {
				varName = argsList.get(0);
			}
			setName(varName);
			return null;
		}
		
		
		public void add(Object value) {
			setValueAt(value,count(),0);
		}
		
		@Override
		public String toString() {
			return getName()+" ("+count()+")";
		}
		
		@Override
		// Display variable data (when variable is double clicked)
		// Accessible through VariableList
		public void show() {
			projectArea.setCurrentShowPanel(this);
		}
		
		public void hide() {
			projectArea.hideShowPanel(this);
		}
		
		@Override
		public JPanel panel() {
			return createShowPanel();
		}

		// END OF ALL PUBLIC METHODS

		// Check validations and Exceptions
		private void validateName(String new_name) throws DuplicateNameException, InvalidNameException {
			// check for validity of name
			if (new_name == null || new_name.length() == 0) {
				throw new InvalidNameException(null,false,false,false);
			}
			
			// check for spaces and other bad chars
			for (Character s : new_name.toCharArray()) {
				if (Character.isWhitespace(s)) throw new InvalidNameException(new_name,false,false,true);
				if(Constants.INVALID_CHARS.contains(s)) throw new InvalidNameException(new_name,false,true,false);
			}
			// Check for keywords
			if(new_name.equals("new") || Constants.MODEL_TYPES.contains(new_name)) {
				throw new InvalidNameException(new_name,true,false,false);
			}
			// check for duplicate names
			if (projectArea.getAllNames().contains(new_name)) {
				throw new DuplicateNameException(new_name);
			}
		}
		
		void validateVariableValues() throws VariableMustContainValuesException, VariableContainsNonNumericException {
			int n = count();
			if (n == 0) throw new VariableMustContainValuesException(getName());
			if (hasNull()) throw new VariableContainsNonNumericException(getName());
		}

		// Getters and Setters not available to command line
		// PACKAGE ACCESS ONLY (DO NOT SET TO PUBLIC)
		
		

		String[] getValuesAsStrings() {
			String[] string_values = new String[values.size()];
			for (int i = 0; i < values.size(); i++) {
				string_values[i] = String.valueOf(values.get(i));
			}
			return string_values;
		}

		String getCSV() {
			return String.join(",", getValuesAsStrings());
		}

		void setName(String name) throws DuplicateNameException, InvalidNameException {
			validateName(name);
			this.name = name;
			hasUpdates = true;
		}
		
		
		// Check to see if variable has any null values
		boolean hasNull() {
			return (!nullValues.isEmpty());
		}
		
		String getName() {
			return name;
		}
		
		void setValues(ArrayList<? extends Object> newValues) {
			values.clear();
			for(Object obj: newValues)  {
				setValueAt(obj, count(), 0);
			}
		}

		ArrayList<Double> getValues() {
			ArrayList<Double> toReturn = new ArrayList<Double>();
			for(int i = 0; i < values.size(); i++) {
				try {
					toReturn.add(Double.parseDouble(getValueAt(i,0).toString()));
				} catch (Exception e) {
					toReturn.add(null);
				}
			}
			return toReturn;
		}
		
		ArrayList<Double> getNormal(int n, double mean, double stddev) {
			Random r = new Random();
			ArrayList<Double> newVals = new ArrayList<Double>();
			for(int i = 0; i < n; i++) {
				newVals.add(mean+stddev*r.nextGaussian());
			}
			return newVals;
		}
		
		ArrayList<Double> getChiSquared(int n, int df) {
			ArrayList<Double> chiSquared = new ArrayList<Double>();
			// init array
			for(int i = 0; i < n; i++) {
				chiSquared.add(0d);
			}
			for(int i = 0; i < df; i++) {
				ArrayList<Double> gaussian = getNormal(n, 0, 1);
				for(int j = 0; j < n; j++) {
					chiSquared.set(j, chiSquared.get(j)+Math.pow(gaussian.get(j),2));
				}
			}
			return chiSquared;
		}
		
		ArrayList<Double> getTDistribution(int n, int df) {
			ArrayList<Double> t = new ArrayList<Double>();
			// init array
			double[] tdist = new TDistribution(df).sample(n);

			for(int i = 0; i < n; i++) {
				t.add(tdist[i]);
			}

			return t;
		}
		
		ArrayList<Double> getExpDistribution(int n, Double mean) {
			ArrayList<Double> exp = new ArrayList<Double>();
			// init array
			double[] expDist = new ExponentialDistribution(mean).sample(n);

			for(int i = 0; i < n; i++) {
				exp.add(expDist[i]);
			}

			return exp;
		}
		
		ArrayList<Double> getPoisDistribution(int n, Double mean) {
			ArrayList<Double> pois = new ArrayList<Double>();
			// init array
			int[] poisDist = new PoissonDistribution(mean).sample(n);

			for(int i = 0; i < n; i++) {
				pois.add(new Double(poisDist[i]));
			}

			return pois;
		}
		
		ArrayList<Double> getFDistribution(int n, int df1, int df2) {
			ArrayList<Double> f = new ArrayList<Double>();
			// init array
			double[] fdist = new FDistribution(df1,df2).sample(n);

			for(int i = 0; i < n; i++) {
				f.add(fdist[i]);
			}

			return f;
		}
		
		ArrayList<Double> getUniform(int n, double a, double b) {
			Random r = new Random();
			ArrayList<Double> newVals = new ArrayList<Double>();
			for(int i = 0; i < n; i++) {
				newVals.add(a+r.nextDouble()*(b-a));
			}
			return newVals;
		}
		
		// What happens when the parser runs into something non numeric
		void addNull(String invalid) {
			nullValues.put(values.size(),invalid);
			values.add(null);
			hasUpdates = true;
		}
		
		// What happens when the parser runs into something non numeric
		void setNull(int index, String invalid) {
			nullValues.put(index,invalid);
			values.set(index, null);
			hasUpdates = true;
		}
		
		// Variable values Table Class
		private class VariableTable extends JTable implements TableModelListener {
			private static final long serialVersionUID = 1L;

			public VariableTable(TableModel model) {
				super(model);
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return true;
			}

		} // end of variable table class

		@Override
		public String getColumnName(int column) {
			return "Values";
		}

		@Override
		public int getColumnCount() {
			return 1;
		}

		@Override
		public int getRowCount() {
			return count() + 1;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex >= 0 && rowIndex < values.size()) {
				if(values.get(rowIndex) != null) return values.get(rowIndex);
				// get nullValue
				return nullValues.get(rowIndex);
			} else {
				return "";
			}
		}
		
		public void removeValueAt(int rowIndex) {
			nullValues.remove(rowIndex);
			if(rowIndex >= 0 && rowIndex < values.size()) {
				values.remove(rowIndex);
				variableList.clearSelection();
				variableList.setSelectedIndex(VariableList.this.indexOf(this));
			}
		}

		@Override
		public void setValueAt(Object sValue, int rowIndex, int columnIndex) {
			try {
				Double value = Double.parseDouble(sValue.toString()); // could throw error
				if (rowIndex < values.size()) {
					values.set(rowIndex, value);
				} else {
					values.add(value);
				}
				nullValues.remove(rowIndex);
				
			} catch (Exception e) {				
				// set nulls
				if (rowIndex < values.size()) {
					setNull(rowIndex, sValue.toString());
				} else {
					addNull(sValue.toString());
				}
			}
			hasUpdates = true;
			// Reset list selection so the number of values updates
			variableList.clearSelection();
			variableList.setSelectedIndex(VariableList.this.indexOf(this));
		}			
		

	} // END of Variable Class
	
} // END of Variable List Class
