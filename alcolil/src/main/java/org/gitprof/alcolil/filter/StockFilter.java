package org.gitprof.alcolil.filter;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.XMLConfiguration;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.strategy.AlphaGraphAnalyzer;

public class StockFilter {
	
	long minAvgVol;
	int lastXDaysForAvg;
	int minMarketCap;
	BigDecimal minLastPrice;
	
	public StockFilter(String filename) {
		//getConfFromFile(filename);
	}
	
	public void getConfFromFile(String confFileName) {
		Configurations configs = new Configurations();
		XMLConfiguration config = null;
		try {
			config = configs.xml(confFileName);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		setConf(config.getLong("StockFilter.minAvgVol"),
				config.getInt("StockFilter.lastXDaysForAvg"),
				config.getInt("StockFilter.minMarketCap"),
				new BigDecimal(config.getDouble("StockFilter.minLastPRice")));
	}
	
	public void setConf(long minAvgVol,
						int lastXDaysForAvg,
						int marketCap,
						BigDecimal lastPrice) {
		this.minAvgVol = minAvgVol;
		this.lastXDaysForAvg = lastXDaysForAvg;
		this.minMarketCap = marketCap;
		this.minLastPrice = lastPrice;
	}
	
	private long avgVolofLastXDays(AStock stock) throws IOException {
		ATimeSeries timeSeries = DBManager.getInstance().readFromQuoteDB(stock.getSymbol());
		long avgVol = new AlphaGraphAnalyzer(timeSeries.getBarSeries(AInterval.DAILY)).avgVolofXDays(lastXDaysForAvg, null);
		return avgVol;
	}
	
	private boolean isMatch(AStock stock) throws IOException{
		boolean ret = true;
		if ((avgVolofLastXDays(stock) < minAvgVol) ||
				(stock.getMarketCap() < minMarketCap) ||
				(stock.getLastPrice().doubleValue() < minLastPrice.doubleValue()))
			ret = false;
		return ret;
	}
	
	public AStockCollection filter(AStockCollection stockCollection) throws IOException {
		AStockCollection filteredCollection = new AStockCollection();
		for (AStock stock : stockCollection) {
			if(isMatch(stock)) {
				filteredCollection.add(stock);
			}
		}
		return filteredCollection;
	}
}
