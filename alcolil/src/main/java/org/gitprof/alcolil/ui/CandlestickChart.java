package org.gitprof.alcolil.ui;

import java.util.List;
import java.awt.BorderLayout;
import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;

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

	private static final DateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	private OHLCSeries ohlcSeries;
	private TimeSeries volumeSeries;

	private static final int MIN = 60000;
	// Every minute
	private int timeInterval = 1;
	//private Trade candelChartIntervalFirstPrint = null;
	private double open = 0.0;
	private double close = 0.0;
	private double low = 0.0;
	private double high = 0.0;
	private long volume = 0;

	public CandlestickChart(String title) {
	
	}
	
	public CandlestickChart() {
		
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
	
	public void createCandlestickChart(String title) {
	// Create new chart
	final JFreeChart candlestickChart = fillCandlestickChart(title);
	// Create new chart panel
	final ChartPanel chartPanel = new ChartPanel(candlestickChart);
	chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
	// Enable zooming
	chartPanel.setMouseZoomable(true);
	chartPanel.setMouseWheelEnabled(true);
	//add(chartPanel, BorderLayout.CENTER);
	}
	
	private void fillCandlesticks(OHLCSeries ohlcSeries) {
		// For loop candles
		// do: ohlcSeries.add(t, o, h, l, c);
	}
	
	private void fillVolumes(TimeSeries volumeSeries) {
		// for volumes
		// do: volumeSeries.add(t, v);
		
	}
	
	private JFreeChart fillCandlestickChart(String chartTitle) {
		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		ohlcSeries = new OHLCSeries("Price");
		fillCandlesticks(ohlcSeries);
		candlestickDataset.addSeries(ohlcSeries);
		// Create candlestick chart priceAxis
		NumberAxis priceAxis = new NumberAxis("Price");
		priceAxis.setAutoRangeIncludesZero(false);
		// Create candlestick chart renderer
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE,
				false, new CustomHighLowItemLabelGenerator(new SimpleDateFormat("kk:mm"), new DecimalFormat("0.000")));
		// Create candlestickSubplot
		XYPlot candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
		candlestickSubplot.setBackgroundPaint(Color.white);

		/**
		 * Creating volume subplot
		 */
		// creates TimeSeriesCollection as a volume dataset for volume chart
		TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
		volumeSeries = new TimeSeries("Volume");
		fillVolumes(volumeSeries);
		volumeDataset.addSeries(volumeSeries);
		// Create volume chart volumeAxis
		NumberAxis volumeAxis = new NumberAxis("Volume");
		volumeAxis.setAutoRangeIncludesZero(false);
		// Set to no decimal
		volumeAxis.setNumberFormatOverride(new DecimalFormat("0"));
		// Create volume chart renderer
		XYBarRenderer timeRenderer = new XYBarRenderer();
		timeRenderer.setShadowVisible(false);
		timeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("Volume--> Time={1} Size={2}",
				new SimpleDateFormat("kk:mm"), new DecimalFormat("0")));
		// Create volumeSubplot
		XYPlot volumeSubplot = new XYPlot(volumeDataset, null, volumeAxis, timeRenderer);
		volumeSubplot.setBackgroundPaint(Color.white);

		/**
		 * Create chart main plot with two subplots (candlestickSubplot,
		 * volumeSubplot) and one common dateAxis
		 */
		// Creating charts common dateAxis
		DateAxis dateAxis = new DateAxis("Time");
		//dateAxis.setDateFormatOverride(new SimpleDateFormat("kk:mm"));
		dateAxis.setDateFormatOverride(new SimpleDateFormat("MM:dd"));
		// reduce the default left/right margin from 0.05 to 0.02
		dateAxis.setLowerMargin(0.02);
		dateAxis.setUpperMargin(0.02);
		// Create mainPlot
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

