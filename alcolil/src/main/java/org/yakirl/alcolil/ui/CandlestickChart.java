package org.yakirl.alcolil.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.lang.reflect.Field;
import java.text.DateFormat;
//import java.text.DateFormat.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.HighLowItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;


public class CandlestickChart extends BaseChart {
	
	private static final long serialVersionUID = 6294689542092367723L;
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("mm:ss");

	private ChartPanel chartPanel;
	private OHLCSeries ohlcSeries;
	private TimeSeries volumeSeries;

	private static final int MIN = 60000; 

	public CandlestickChart(String title) {
		setCandlestickChartPanel(title);
	}
	
	@SuppressWarnings("serial")
	public class CustomHighLowItemLabelGenerator extends HighLowItemLabelGenerator {
		private DateFormat dateFormatter;
		private NumberFormat numberFormatter;
		public CustomHighLowItemLabelGenerator(DateFormat dateFormatter, NumberFormat numberFormatter) {
			if (dateFormatter == null) {
				throw new IllegalArgumentException("Null 'dateFormatter' argument.");
			}
			if (numberFormatter == null) {
				throw new IllegalArgumentException("Null 'numberFormatter' argument.");
			}
			this.dateFormatter = dateFormatter;
			this.numberFormatter = numberFormatter;
		}

		@Override
		public String generateToolTip(XYDataset dataset, int series, int item) {

			String result = null;

			if (dataset instanceof OHLCDataset) {
				OHLCDataset d = (OHLCDataset) dataset;
				Number high = d.getHigh(series, item);
				Number low = d.getLow(series, item);
				Number open = d.getOpen(series, item);
				Number close = d.getClose(series, item);
				Number x = d.getX(series, item);

				result = d.getSeriesKey(series).toString();

				if (x != null) {
					Date date = new Date(x.longValue());
					result = result + "--> Time=" + this.dateFormatter.format(date);
					if (high != null) {
						result = result + " High=" + this.numberFormatter.format(high.doubleValue());
					}
					if (low != null) {
						result = result + " Low=" + this.numberFormatter.format(low.doubleValue());
					}
					if (open != null) {
						result = result + " Open=" + this.numberFormatter.format(open.doubleValue());
					}
					if (close != null) {
						result = result + " Close=" + this.numberFormatter.format(close.doubleValue());
					}
				}
			}
			return result;
		}
	}
	
	public void reset(String title) {
		final JFreeChart candlestickChart = createCandlestickChart(title);
		chartPanel.setChart(candlestickChart);
	}
	
	public void setCandlestickChartPanel(String title) {
		final JFreeChart candlestickChart = createCandlestickChart(title);
		chartPanel = new ChartPanel(candlestickChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
	    
		add(chartPanel, BorderLayout.CENTER);
	}
	
	
	
    public void addCandle(long timeMillis, double o, double h, double l, double c, long v) {
    	Date date = new Date(timeMillis);
        FixedMillisecond t = new FixedMillisecond(date);
        ohlcSeries.add(t, o, h, l, c);
        volumeSeries.add(t, v);     
    }
	
	private JFreeChart createCandlestickChart(String chartTitle) {
		// Creating price subplot
		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		ohlcSeries = new OHLCSeries("Price");
		candlestickDataset.addSeries(ohlcSeries);
		NumberAxis priceAxis = new NumberAxis("Price");
		priceAxis.setAutoRangeIncludesZero(false);
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE,
				false, new CustomHighLowItemLabelGenerator(TIME_FORMAT, new DecimalFormat("0.000")));
		XYPlot candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
		candlestickSubplot.setBackgroundPaint(Color.white);

		// Creating volume subplot
		TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
		volumeSeries = new TimeSeries("Volume");
		volumeDataset.addSeries(volumeSeries);
		NumberAxis volumeAxis = new NumberAxis("Volume");
		volumeAxis.setAutoRangeIncludesZero(false);
		volumeAxis.setNumberFormatOverride(new DecimalFormat("0"));
		XYBarRenderer timeRenderer = new XYBarRenderer();
		timeRenderer.setShadowVisible(false);
		timeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("Volume--> Time={1} Size={2}",
				TIME_FORMAT, new DecimalFormat("0")));
		XYPlot volumeSubplot = new XYPlot(volumeDataset, null, volumeAxis, timeRenderer);
		volumeSubplot.setBackgroundPaint(Color.white);

		// main plot of 2 subplots
		DateAxis dateAxis = new DateAxis("Time");
		//dateAxis.setDateFormatOverride(TIME_FORMAT);
		dateAxis.setLowerMargin(0.02);
		dateAxis.setUpperMargin(0.02);
		CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(dateAxis);
		mainPlot.setGap(10.0); 
		mainPlot.add(candlestickSubplot, 3);
		mainPlot.add(volumeSubplot, 1);
		mainPlot.setOrientation(PlotOrientation.VERTICAL);

		JFreeChart chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);
		chart.removeLegend();
		return chart;
	}
}

