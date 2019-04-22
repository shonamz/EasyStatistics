package statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MathHelper {

	
	public static Double mean(List<Double> list) {
		int n = list.size();
		double sum = 0;
		for (double d : list) {
			sum += d;
		}
		return sum / n;
	}
	
	public static Double cov(ArrayList<Double> values1, ArrayList<Double> values2) {
		int max = Math.min(values1.size(), values2.size());
		double covariance = 0d;
		double mean = mean(values1);
		double otherMean = mean(values2);
		for (int i = 0; i < max; i++) {
			covariance += ((values1.get(i) - mean) * (values2.get(i) - otherMean));
		}
		return covariance / (max - 1);
	}
	
	public static Double median(ArrayList<Double> values) {
		// sort values
		int n = values.size();
		Double[] sorted = new Double[n];
		sorted = values.toArray(sorted);

		Arrays.sort(sorted);

		// check if length is even or odd
		if (n % 2 == 0) {
			return (sorted[n / 2] + sorted[(n / 2) - 1]) / 2;
		} else {
			return sorted[(n - 1) / 2];
		}
	}
	
	public static Double var(List<Double> list) {
		// check if we have a single number
		int n = list.size();
		if (n == 1) return 0d;

		Double mean = mean(list);

		// compute variance
		double variance = 0;
		for (double d : list) {
			variance += Math.pow((d - mean), 2);
		}
		return variance / (n - 1);
	}
	
	public static Double mode(ArrayList<Double> values) {
		// sort values
		int n = values.size();
		Double[] sorted = new Double[n];
		sorted = values.toArray(sorted);

		Arrays.sort(sorted);

		// Find mode
		Double mode = sorted[0];
		Double temp = 1d;
		Double temp2 = 1d;
		for (int i = 1; i < sorted.length; i++) {
			if (sorted[i - 1].compareTo(sorted[i]) == 0) {
				temp++;
			} else {
				temp = 1d;
			}
			if (temp >= temp2) {
				mode = sorted[i];
				temp2 = temp;
			}
		}
		return mode;
	}
	
	public static Double stddev(ArrayList<Double> values) {
		return Math.sqrt(var(values));
	}
	
	public static Double cor(ArrayList<Double> values1, ArrayList<Double> values2) {
		Double cov = cov(values1,values2);
		Double stdDev = stddev(values1);
		Double otherStdDev = stddev(values2);
		return (cov / (stdDev * otherStdDev));
	}

	public static Double sum_of_squares(ArrayList<Double> residuals) {
		Double ss = 0d;
		for(Double d: residuals) {
			ss += (d*d);
		}
		return ss;
	}
	
}
