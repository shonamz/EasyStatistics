package statistics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

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
import resources.Config;
import resources.Constants;
import statistics.VariableList.Variable;

public class ModelList extends DefaultListModel<ModelList.AbstractModel> {
	private static final long serialVersionUID = 1L;
	protected ProjectArea projectArea;
	public JList<AbstractModel> modelList;
	protected boolean hasUpdates;
	
	// Constructor
	public ModelList(ProjectArea projectArea) {
		this.projectArea = projectArea;
		modelList = new JList<AbstractModel>(this);
		modelList.setCellRenderer(new ModelRenderer());
		modelList.addMouseListener(new ListMouseAdapter(modelList));
		hasUpdates = false;

		
	}
	
	// Construct Model by Model type
	public AbstractModel newByModelType(String modelType, String depVarName, List<String> indepVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
		if(Constants.LINEAR_MODELS.contains(modelType)) {
			return new LinearModel(depVarName, indepVarNames, name);
		} else if (Constants.QUADRATIC_MODELS.contains(modelType)) {
			return new QuadraticModel(depVarName, indepVarNames, name);
		} else if (Constants.LOGLINEAR_MODELS.contains(modelType)) {
			return new PoissonModel(depVarName, indepVarNames, name);
		} else if (Constants.LOG_MODELS.contains(modelType)) {
			return new LogModel(depVarName, indepVarNames, name);
		}
		
		
		return null;
	}

	// Getters and setters
	public AbstractModel findByName(String name) throws NotFoundException {
		if (name != null) {
			int index = this.getModelNames().indexOf(name);
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

	public ArrayList<String> getModelNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < this.getSize(); i++) {
			names.add(get(i).getName());
		}
		return names;
	}

	// Show AbstractModel panel
	public void show(int index) {
		if (index >= 0 && index < size()) get(index).show();
	}

	public void show(AbstractModel var) {
		if(contains(var)) var.show(); 
	}

	/*
	 * AbstractModel Class
	 *
	 * Warning: Any public method not taking arguments that returns a Double or
	 * String can be called by the user
	 */
	public abstract class AbstractModel implements BasicStatisticObject, ListShowable {
		protected ArrayList<Variable> indVars;
		protected Variable depVar;
		protected ArrayList<Double> fittedValues;
		protected ArrayList<Double> residuals;
		protected ArrayList<Double> coefficients;
		protected ArrayList<Double> pvalues;
		protected ArrayList<Double> tstats;
		protected Double fstat;
		protected Double adjrsquared;
		protected Double rsquared;
		protected Double pvalue;
		protected String name;
		protected String relationString;
		protected NumberFormat numFormatter;
		
		protected AbstractModel(String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
			validateName(name);
			this.name = name;
			numFormatter = NumberFormat.getNumberInstance();
			numFormatter.setMinimumFractionDigits(2);
			numFormatter.setMaximumFractionDigits(5);
			
			depVar = projectArea.variables.findByName(depVarName);
			indVars = new ArrayList<Variable>();
			for(String ind: indVarNames) {
				indVars.add(projectArea.variables.findByName(ind));
			}
			// Create and set relation string
			setRelationString();
			
			// add to AbstractModel list
			ModelList.this.addElement(this);
			
		
		}
		
		protected abstract void calculateModel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;
			
		protected JPanel createShowPanel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			// Make sure to calcualte model
			calculateModel();
			
			// create show window panel
			JPanel panel = new JPanel(new BorderLayout());
	
			// Close show bar
			/*
			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					// Close action
					projectArea.remove(panel);
					projectArea.revalidate();
				}

			}); 
			panel.add(close, BorderLayout.SOUTH);
			 */

			panel.setBorder(new TitledBorder("Summary of "+ this.toString()));
			panel.setPreferredSize(new Dimension(175, 600));
			return panel;
		}
		
		ArrayList<Double> valuesToUse(Hashtable<String,String> optionsHash) {
			if(optionsHash != null && !optionsHash.isEmpty()) {
				String toUse = optionsHash.get("resid");
				if(toUse == null) toUse = optionsHash.get("residuals");
				if(toUse == null) toUse = optionsHash.get("residual");
				 
				if(toUse != null && (toUse.equals("t") || toUse.equals("true"))) {
					return residuals;
				} 
			} 
			return fittedValues;
		}
		
		// This method checks variables and raises the right exception
		protected void validateVariables() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			int length = depVar.count();
			depVar.validateVariableValues();
			
			for(Variable v: indVars) {
				v.validateVariableValues();
				if(v.count() != length && Config.isModelDimensionPolicyStrict()) throw new InconsistentModelDimensionException(getName());
			}
		}
		

		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<String,String> instance

		// Statistics Methods
		
		public ChartPanel scatter(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			calculateModel();
			validateVariables();
			ArrayList<Double> valuesToGraph = valuesToUse(optionsHash);
			String toGraph = ""; 
			
			if(valuesToGraph == residuals) {
				toGraph = " residuals";
			} else {
				toGraph = " predicted";
			}
			 

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

				return new ChartPanel(Grapher.scatter(argsList,this.name+toGraph, allValues, valuesToGraph), true);
				
			} else {
				return new ChartPanel(Grapher.scatter(this.name+toGraph, valuesToGraph), true);
			}
		}
		
		public ChartPanel hist(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			// User must specify what to show the histogram of in the argsList
			// Check to see if they residuals is set to true
			
			ArrayList<Double> valuesToGraph = valuesToUse(optionsHash);

			String toGraph = ""; 
			if(valuesToGraph == residuals) {
				toGraph = " residuals";
			} else {
				toGraph = " predicted";
			}
			
			boolean frequency = true;
			// check options
			if (optionsHash != null) {
				boolean density = optionsHash.containsKey("density") && optionsHash.get("density").equals("true");
				if (density || (optionsHash.containsKey("frequency") && optionsHash.get("frequency").equals("false"))) {
					frequency = false;
				}

			} 
			return new ChartPanel(Grapher.histogram(this.name+toGraph, valuesToGraph, frequency), true);

		}
		
		public ChartPanel histogram(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			return hist(argsList, optionsHash);
		}
		
		@Override
		public Double mean(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			return MathHelper.mean(valuesToUse(optionsHash));
		}


		@Override
		public Double median(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			return MathHelper.median(valuesToUse(optionsHash));
		}

		@Override
		public Double mode(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			return MathHelper.mode(valuesToUse(optionsHash));
		}

		@Override
		public Double stddev(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			return MathHelper.stddev(valuesToUse(optionsHash));
		}

		@Override
		public Double var(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, ModelCannotHaveNegativeValues {
			calculateModel();
			validateVariables();
			return MathHelper.var(valuesToUse(optionsHash));
		}

		@Override
		public String delete(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
				throws DuplicateNameException, InvalidNameException {
			ModelList.this.removeElementAt(indexOf(this));
			return null;
		}

		@Override
		public Object rename(ArrayList<String> argsList, Hashtable<String, String> optionsHash)
				throws DuplicateNameException, InvalidNameException {
			if (argsList != null && !argsList.isEmpty()) {
				validateName(argsList.get(0));
				setName(argsList.get(0));
			}
			return null;
		}
		
		
		@Override
		public JPanel panel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			return createShowPanel();
		}
	
		@Override
		public String toString() {
			return getName()+" ("+getRelationString()+")";
		}
		
		
		public String coefs(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			calculateModel();
			return coefficients.toString();
		}
		
		
		public Double fstat(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			calculateModel();
			return fstat;
		}
		
		public String tstats(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			calculateModel();
			return tstats.toString();
		}
		
		public String pvalues(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			calculateModel();
			return pvalues.toString();
		}
		
		public Double pvalue(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			calculateModel();
			if(argList == null || argList.size() == 0) {
				return pvalue;
			} else {
				return pvalues.get(indVars.indexOf(projectArea.variables.findByName(argList.get(0))));
			}
		
		}
		
		public Double tstat(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			calculateModel();
			return tstats.get(indVars.indexOf(projectArea.variables.findByName(argList.get(0))));		
		}
		
		public Double coef(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			calculateModel();
			return coefficients.get(indVars.indexOf(projectArea.variables.findByName(argList.get(0))));		
		}
		
		public Double beta(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues, NotFoundException {
			return coef(argList, optionsHash);	
		}
		
		public String betas(ArrayList<String> argList, Hashtable<String,String> optionsHash) throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
			return coefs(argList, optionsHash);
		}

		// END OF ALL PUBLIC METHODS
		
		// Make sure an AbstractModelType is set
		abstract protected void setRelationString();

		String getRelationString() {
			// TODO Auto-generated method stub
			return relationString;
		}

		// Check validations and Exceptions
		protected void validateName(String new_name) throws DuplicateNameException, InvalidNameException {
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

		// Display AbstractModel data (when AbstractModel is double clicked)
		// Accessible through ModelList
		public void show() {
			projectArea.setCurrentCommand(getName());
		}
		

		// Getters and Setters not available to command line
		// PACKAGE ACCESS ONLY (DO NOT SET TO PUBLIC)

		void setName(String name) throws DuplicateNameException, InvalidNameException {
			validateName(name);
			this.name = name;
			hasUpdates = true;
		}
		
		
		// Check to see if AbstractModel has any null values
		//  Equivalently, check if any associated variables are null
		boolean hasNull() {
			if(depVar.hasNull()) return true;
			for(Variable var: indVars) {
				if(var.hasNull()) return true;
			}
			return false;
		}
		
		String getName() {
			return name;
		}
		
		boolean hasDifferentVariableLengths() {
			int length = depVar.count();
			for(Variable v: indVars) {
				if(v.count() != length) return true;
			}
			return false;
		}

	} // END of AbstractModel Class

	
	// Basic linear model
	public class LinearModel extends AbstractLinearModel {
		
		public LinearModel(String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
			super(ModelList.this, depVarName, indVarNames, name);
		}
	
		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<Integer,Integer> instance
	
		// Graphing Methods For Linear Model
	

		@Override
		protected void setRelationString() {
			relationString = "Linear Model";
		}
		
		// basic statistic methods 


	
		// END OF ALL PUBLIC METHODS
	

	
	} // END of Linear Model Class
	
	// Basic logic model
	public class QuadraticModel extends AbstractLinearModel {
		
		public QuadraticModel(String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
			super(ModelList.this, depVarName, indVarNames, name);
		}
	
		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<Integer,Integer> instance
			
		// basic statistic methods 


	
		// END OF ALL PUBLIC METHODS
		
		// Make sure an modelType is set
		@Override
		protected void setRelationString() {
			relationString = "Quadratic Model";
		}
	
	} // END of Quadratic Model Class
	
	// Basic logarithmic model
	public class LogModel extends AbstractLinearModel {
		
		public LogModel(String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
			super(ModelList.this, depVarName, indVarNames, name);
		}
	
		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<Integer,Integer> instance
	
		// Graphing Methods For Linear Model
	
		// basic statistic methods 


	
		// END OF ALL PUBLIC METHODS
		
		// Make sure an modelType is set
		@Override
		protected void setRelationString() {
			relationString = "Log-Log Model";
		}
		
		// Make sure we only positive values
		@Override
		protected void validateVariables() throws ModelCannotHaveNegativeValues,VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable {
			super.validateVariables();
			for(Double v: depVar.getValues()) {
				if(v <= 0) {
					throw new ModelCannotHaveNegativeValues(this.getName(),this.getRelationString());
				}
			}
			for(Variable var: indVars) {
				for(Double v: var.getValues()) {
					if(v <= 0) {
						throw new ModelCannotHaveNegativeValues(this.getName(),this.getRelationString());
					}
				}
			}
		}

	
	} // END of log Model Class
	
	// Basic poisson model
	public class PoissonModel extends AbstractLinearModel {
		
		public PoissonModel(String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
			super(ModelList.this, depVarName, indVarNames, name);
		}
	
		// All public Methods (These are accessible by the Command LINE)
		// Arguments must be passed in as one ArrayList<String> instance
		// Options must be passed in as one Hashtable<Integer,Integer> instance
	
		// Graphing Methods For Linear Model
	
		// basic statistic methods 


	
		// END OF ALL PUBLIC METHODS
		
		// Make sure an modelType is set
		@Override
		protected void setRelationString() {
			relationString = "Poisson Model";
		}
		
		@Override
		protected void validateVariables() throws ModelMustHaveCategoricalDependentVariable,VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelCannotHaveNegativeValues {
			super.validateVariables();
			for(Double v: depVar.getValues()) {
				if(v <= 0) {
					throw new ModelCannotHaveNegativeValues(this.getName(),this.getRelationString());
				}
			}
		}

	
	} // END of Probit Model Class
	
} // END of Model List Class
