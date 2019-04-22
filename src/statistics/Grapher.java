package statistics;

import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import exceptions.VariableMustContainValuesException;
import resources.Util;

public class Grapher {
	public static JFreeChart histogram(String name, ArrayList<Double> values, boolean frequency)
			throws VariableMustContainValuesException {
		HistogramDataset dataset = new HistogramDataset();
		HistogramType type;
		String yaxis;
		if (frequency) {
			type = HistogramType.FREQUENCY;
			yaxis = "Frequency";
		} else {
			type = HistogramType.RELATIVE_FREQUENCY;
			yaxis = "Density";
		}
		// Make sure we have values to graph
		if (values.size() == 0)
			throw new VariableMustContainValuesException(name);

		Double[] arrayValues = new Double[values.size()];
		arrayValues = values.toArray(arrayValues);

		dataset.setType(type);
		dataset.addSeries(name, Util.convertToPrimitive(arrayValues), 10);
		String plotTitle = "Histogram of " + name;
		String xaxis = "Value";
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips,
				urls);

		return chart;
	}


	// Scatter chart
	public static JFreeChart scatter(String Y, ArrayList<Double> yValues) {
        
		String plotTitle = "Scatterplot of " + Y;
		
	    XYSeriesCollection data = new XYSeriesCollection();
	    XYSeries series = new XYSeries(Y);
	    for (int i = 0; i < yValues.size(); i++) {
	        series.add(i, yValues.get(i));
	    }
	    data.addSeries(series);
        
        JFreeChart chart = ChartFactory.createScatterPlot(plotTitle,"X","Values", data);

        return chart;

	}
	
	// Scatter chart
	public static JFreeChart scatter(ArrayList<String> Y, String X, ArrayList<ArrayList<Double>> yValues, ArrayList<Double> xValues) {
        
		String plotTitle = "Scatterplot";
		
		int min = xValues.size();
		for(ArrayList<Double> a: yValues) {
			if(min > a.size()) min = a.size();
		}
		
	    XYSeriesCollection data = new XYSeriesCollection();
	    int k = 0;
	    for(ArrayList<Double> ys: yValues) {
		    XYSeries series = new XYSeries(Y.get(k));
		    for (int i = 0; i < min; i++) {
		        series.add(xValues.get(i), ys.get(i));
		    }
		    data.addSeries(series);
		    k++;
	    }

        
        JFreeChart chart = ChartFactory.createScatterPlot(plotTitle,X,"Values", data);

        return chart;

	}
	
}
