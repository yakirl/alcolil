package org.gitprof.alcolil.filter;

import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.XMLConfiguration;

import org.gitprof.alcolil.common.*;

public class StockFilter {
	
	long minAvgVol;
	int lastXDaysForAvg;
	int minMarketCap;
	APrice minLastPrice;
	
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
		//setConf(config.getLong("StockFilter.minAvgVol"))
	}
	
	public void setConf(long minAvgVol,
			int lastXDaysForAvg,
			int marketCap,
			APrice lastPrice) {
		this.minAvgVol = minAvgVol;
		this.lastXDaysForAvg = lastXDaysForAvg;
		this.minMarketCap = marketCap;
		this.minLastPrice = lastPrice;
	}
	
	private long avgVolofLastXDays(AStock stock) {
		long avgVol = 0;
		// calc
		return avgVol;
	}
	
	private boolean isMatch(AStock stock){
		boolean ret = true;
		if ((avgVolofLastXDays(stock) < minAvgVol) ||
				(stock.getMarketCap() < minMarketCap) ||
				(stock.getLastPrice().getDouble() < minLastPrice.getDouble()))
			ret = false;
		return ret;
	}
	
	public AStockCollection filter(AStockCollection stockCollection) {
		AStockCollection filteredCollection = new AStockCollection();
		for (AStock stock : stockCollection) {
			if(isMatch(stock)) {
				filteredCollection.add(stock);
			}
		}
		return filteredCollection;
	}
}
