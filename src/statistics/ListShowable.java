package statistics;

import javax.swing.JPanel;

import exceptions.InconsistentModelDimensionException;
import exceptions.ModelCannotHaveNegativeValues;
import exceptions.ModelMustHaveCategoricalDependentVariable;
import exceptions.VariableContainsNonNumericException;
import exceptions.VariableMustContainValuesException;

public interface ListShowable {
	abstract void show();
	abstract JPanel panel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues;
}
