package statistics;

import java.util.ArrayList;
import java.util.Hashtable;

import org.jfree.chart.ChartPanel;

import exceptions.DuplicateNameException;
import exceptions.InconsistentModelDimensionException;
import exceptions.InvalidNameException;
import exceptions.ModelCannotHaveNegativeValues;
import exceptions.ModelMustHaveCategoricalDependentVariable;
import exceptions.VariableContainsNonNumericException;
import exceptions.VariableMustContainValuesException;

public interface BasicStatisticObject {

	public ChartPanel hist(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;
	// alternative command 
	public ChartPanel histogram(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;

	// Other Statistical Methods
	public Double mean(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;
	
	public Double median(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;

	public Double mode(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;

	public Double stddev(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;

	public Double var(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws VariableMustContainValuesException, VariableContainsNonNumericException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;

	// Other available public methods for the command line

	public String delete(ArrayList<String> argsList, Hashtable<String,String> optionsHash)
			throws DuplicateNameException, InvalidNameException;

	public Object rename(ArrayList<String> argsList, Hashtable<String,String> optionsHash) throws DuplicateNameException, InvalidNameException;
	
}
