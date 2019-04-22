package statistics;

import Jama.Matrix;

public class MatrixHelper {
	
	public static Double[] getColumn(Matrix m, int col) {
		Double[] column = new Double[m.getRowDimension()];
		int i = 0;
		for(double[] row: m.getArray()) {
			column[i] = row[col];
			i++;
		}
		return column;
	}

}
