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

import yahoofinance.histquotes.HistoricalQuote;

public class ChartMaker {

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

	public ChartMaker(String title) {
		// Create new chart
		final JFreeChart candlestickChart = createChart(title);
		// Create new chart panel
		final ChartPanel chartPanel = new ChartPanel(candlestickChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		//add(chartPanel, BorderLayout.CENTER);
	}

	@SuppressWarnings("serial")
	public class CustomHighLowItemLabelGenerator extends HighLowItemLabelGenerator {

		/** The date formatter. */
		private DateFormat dateFormatter;

		/** The number formatter. */
		private NumberFormat numberFormatter;

		/**
		 * Creates a tool tip generator using the supplied date formatter.
		 *
		 * @param dateFormatter
		 *            the date formatter (<code>null</code> not permitted).
		 * @param numberFormatter
		 *            the number formatter (<code>null</code> not permitted).
		 */
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

		/**
		 * Generates a tooltip text item for a particular item within a series.
		 *
		 * @param dataset
		 *            the dataset.
		 * @param series
		 *            the series (zero-based index).
		 * @param item
		 *            the item (zero-based index).
		 *
		 * @return The tooltip text.
		 */
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
	
	private JFreeChart createChart(String chartTitle) {

		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		ohlcSeries = new OHLCSeries("Price");
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

	/**
	 * Fill series with data.
	 *
	 * @param t the t
	 */
	public void addCandel(long time, double o, double h, double l, double c, long v) {
		try {
			// Add bar to the data. Let's repeat the same bar
			//System.out.format(Util.convertToReadableTime(time));
			FixedMillisecond t = new FixedMillisecond(time);
					//READABLE_TIME_FORMAT.parse(Util.convertToReadableTime(time)));
			System.out.println("T = " + t.getFirstMillisecond());
			ohlcSeries.add(t, o, h, l, c);
			volumeSeries.add(t, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fillCandles() {
		//DBManager dbManager = new DBManager();
		List<HistoricalQuote> quotes = null; //dbManager.readDataset();
		for (HistoricalQuote quote : quotes) {
			System.out.println(quote.getDate().getTimeInMillis());
			addCandel(quote.getDate().getTimeInMillis(), 
					quote.getOpen().doubleValue(),
					quote.getHigh().doubleValue(),
					quote.getLow().doubleValue(),
					quote.getClose().doubleValue(),
					quote.getVolume().longValue());
		}
		
		//GraphAnalyzer analyzer = new AlphaGraphAnalyzer();
		//analyzer.findSupports();
	}
	
	/**
	 * Aggregate the (open, high, low, close, volume) based on the predefined time interval (1 minute)
	 *
	 * @param t the t
	 */
	public void onTrade() {
	/*	double price = t.getPrice();
		if (candelChartIntervalFirstPrint != null) {
			long time = t.getTime();
			if (timeInterval == (int) ((time / MIN) - (candelChartIntervalFirstPrint.getTime() / MIN))) {
				// Set the period close price
				close = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);
				// Add new candle
				addCandel(time, open, high, low, close, volume);
				// Reset the intervalFirstPrint to null
				candelChartIntervalFirstPrint = null;
			} else {
				// Set the current low price
				if (Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT) < low)
					low = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);

				// Set the current high price
				if (Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT) > high)
					high = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);

				volume += t.getSize();
			}
		} else {
			// Set intervalFirstPrint
			candelChartIntervalFirstPrint = t;
			// the first trade price in the day (day open price)
			open = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);
			// the interval low
			low = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);
			// the interval high
			high = Util.roundDouble(price, Util.TWO_DEC_DOUBLE_FORMAT);
			// set the initial volume
			volume = t.getSize();
		}
		*/
	}

}

