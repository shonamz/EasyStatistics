package statistics;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.TDistribution;

import Jama.Matrix;
import exceptions.DuplicateNameException;
import exceptions.InconsistentModelDimensionException;
import exceptions.InvalidNameException;
import exceptions.ModelCannotHaveNegativeValues;
import exceptions.ModelMustHaveCategoricalDependentVariable;
import exceptions.NotFoundException;
import exceptions.VariableContainsNonNumericException;
import exceptions.VariableMustContainValuesException;
import statistics.ModelList.AbstractModel;
import statistics.ModelList.LogModel;
import statistics.ModelList.PoissonModel;
import statistics.ModelList.QuadraticModel;
import statistics.VariableList.Variable;

public abstract class AbstractLinearModel extends AbstractModel {
	
	protected AbstractLinearModel(ModelList modelList, String depVarName, List<String> indVarNames, String name) throws DuplicateNameException, InvalidNameException, NotFoundException {
		modelList.super(depVarName, indVarNames, name);
	}
	
	protected void calculateModel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
		// check for errors in model
		validateVariables();
		
		// find smallest variable size
		int min = depVar.count();
		for(Variable v: indVars) {
			min = Math.min(min, v.count());
		}
		
		// Get fresh set of values from the variables
		
		int N = min;
		
		// Y is a N by 1 column vector
		double[][] yValuesArray = new double[N][1];
		ArrayList<Double> yValues = depVar.getValues();
		
		for(int i = 0; i < N; i++) {
			// check actual type of model
			if(this instanceof LogModel || this instanceof PoissonModel) {
				yValuesArray[i][0] = Math.log(yValues.get(i));
			} else {
				yValuesArray[i][0] = yValues.get(i);
			}
		}
		
		Matrix Y = new Matrix(yValuesArray);
		
		// X is a N by K + 1 MyMatrix
		double[][] xValuesArray = new double[N][indVars.size()+1];
		for(int i = 0; i < N; i++) {
			xValuesArray[i][0] = 1;
		}	
		for(int k = 0; k < indVars.size(); k++) {
			ArrayList<Double> xValues = indVars.get(k).getValues();
			for(int i = 0; i < N; i++) {
				//Check the actual model type
				if (this instanceof LogModel) {
					xValuesArray[i][k+1] = Math.log(xValues.get(i));
				} else if(this instanceof QuadraticModel) {
					xValuesArray[i][k+1] = Math.pow(xValues.get(i),2);
				} else {
					xValuesArray[i][k+1] = xValues.get(i);
				}
			}
		}
		
		Matrix X = new Matrix(xValuesArray);
		
		// We need to solve the system Y = XB + U
		//  The formula for B is (X'X)^(-1) * X'Y    (where ' denotes transpose)

		Matrix Xt = X.transpose();
		Matrix XtX = Xt.times(X);		
		Matrix XtXinverse = XtX.inverse();
		Matrix XtY = Xt.times(Y);
		Matrix Beta = XtXinverse.times(XtY);
		coefficients = new ArrayList<Double>();
		for(double[] b: Beta.getArray()) {
			coefficients.add(b[0]);
		}
		
		Matrix XB = X.times(Beta);
		fittedValues = new ArrayList<Double>();
		fittedValues.addAll(Arrays.asList(MatrixHelper.getColumn(XB,0)));
		
		Matrix U = Y.minus(XB);
		residuals = new ArrayList<Double>();
		residuals.addAll(Arrays.asList(MatrixHelper.getColumn(U,0)));
		
		// get rsquared
		Double SSres = MathHelper.sum_of_squares(residuals);
		Double SStot = MathHelper.var(depVar.getValues().subList(0, N))*(N-1);
		rsquared = 1.0 - (SSres/SStot);
		adjrsquared = 1.0 - (SSres/(N-coefficients.size()))/((SStot/(N-1)));
		
		// get t-statistics and pvalues
		pvalues = new ArrayList<Double>();
		tstats = new ArrayList<Double>();
		Double MSSres = SSres/(N-coefficients.size());
		Double MSSreg = (SStot-SSres)/coefficients.size();
		
		fstat = MSSreg/MSSres;
		
		// Calculate pvalue
		if(N-coefficients.size() > 0) {
			FDistribution f = new FDistribution(coefficients.size(),N-coefficients.size());
			pvalue = 1.0 - f.cumulativeProbability(fstat);

			// find matrix C = var^2*(XtX)^-1
			Matrix C = XtXinverse.times(MSSres);
			TDistribution t = new TDistribution(N-coefficients.size());
			int i = 0;
			for(Double coef: coefficients) {
				// get Standard Error
				Double se = Math.sqrt(C.get(i, i));
				Double tstat = coef/se;
				tstats.add(tstat);
				// add p value
				pvalues.add(2*t.cumulativeProbability(Math.abs(tstat)*-1.0));
				i++;
			}
		} else {
			pvalue = Double.NaN;
			for(int i = 0; i < coefficients.size(); i++) {
				tstats.add(Double.NaN);
				pvalues.add(Double.NaN);
			}
		}

	}
	
	protected JPanel createShowPanel() throws VariableContainsNonNumericException, VariableMustContainValuesException, InconsistentModelDimensionException, ModelMustHaveCategoricalDependentVariable, ModelCannotHaveNegativeValues {
		// create show window panel
		JPanel panel = super.createShowPanel();
		
		// Coefficients table
		JTable coefTable = new JTable(new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 4;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return coefficients.size();
			}

			@Override
			public Object getValueAt(int row, int col) {
				// TODO Auto-generated method stub
				if(col == 0) {
					if(row == 0) {
						return "(Int)";
					} else {
						return indVars.get(row-1).getName();
					}
				} else if (col == 1) {
					return numFormatter.format(coefficients.get(row));
				} else if (col == 2) {
					return numFormatter.format(tstats.get(row));
				} else if (col == 3) {
					return numFormatter.format(pvalues.get(row));
				} else {
					return null;
				}
			}
			
		});
		coefTable.getColumnModel().getColumn(0).setHeaderValue("Variable");
		coefTable.getColumnModel().getColumn(1).setHeaderValue("Est. Coeff.");
		coefTable.getColumnModel().getColumn(2).setHeaderValue("T-stat");
		coefTable.getColumnModel().getColumn(3).setHeaderValue("P-value");
				
		JTable modelStatsTable = new JTable(new AbstractTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				// TODO Auto-generated method stub
				return 4;
			}

			@Override
			public int getRowCount() {
				// TODO Auto-generated method stub
				return 1;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				if(columnIndex == 0) {
					// r squared
					return numFormatter.format(rsquared);
				} else if(columnIndex == 1) {
					// adj r squared
					return numFormatter.format(adjrsquared);
				} else if (columnIndex == 2) {
					// f stat
					return numFormatter.format(fstat);
				} else if (columnIndex == 3) {
					// p value
					return numFormatter.format(pvalue);
				}
				return null;
				
			}
			
		});
		
		modelStatsTable.getColumnModel().getColumn(0).setHeaderValue("R-squared");
		modelStatsTable.getColumnModel().getColumn(1).setHeaderValue("Adj. R-squared");
		modelStatsTable.getColumnModel().getColumn(2).setHeaderValue("F-stat");
		modelStatsTable.getColumnModel().getColumn(3).setHeaderValue("P-value");	
		
		JPanel top = new JPanel(new BorderLayout());
		top.add(new JScrollPane(modelStatsTable));
		top.setPreferredSize(new Dimension(300,50));
		top.setBorder(new EmptyBorder(5,5,5,5));		


		JPanel bottom = new JPanel(new BorderLayout());
		bottom.add(new JScrollPane(coefTable));
		bottom.setPreferredSize(new Dimension(300,300));
		bottom.setBorder(new EmptyBorder(5,5,5,5));		
		
		panel.add(top,BorderLayout.NORTH);
		panel.add(bottom,BorderLayout.CENTER);
		
		return panel;
	}
	
}
