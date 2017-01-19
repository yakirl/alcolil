package org.gitprof.alcolil.core;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.lang.Thread;

import org.gitprof.alcolil.scanner.BackTester;
import org.gitprof.alcolil.ui.UserInterface;
import org.gitprof.alcolil.stats.StatsCalculator;
import org.gitprof.alcolil.scanner.ParamOptimizer;
import org.gitprof.alcolil.common.ATime;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.marketdata.BaseFetcher;


public class Core 
{
	
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	
	
    public void start() {
    
    }
    
    private void mainLoop() {
    	
    }
    
    private void runInterface() {
    	UserInterface.startInterface();
    }
    
    private void calcStats() {
    	StatsCalculator statsCalculator = new StatsCalculator();
    	statsCalculator.calc();
    }
    
    private void backtest(ATime from, ATime to) {
    	BackTester backTester = new BackTester();
    	backTester.backTest(from, to);
    }
    
    private void optimizeParameters() {
    	ParamOptimizer paramOptimizer = new ParamOptimizer();
    	paramOptimizer.optimize();
    }
    
    private void realTimeScan() {
    	// run as thread
    }
    
    private void updateHistoricalDataDB(String stockFilename, BaseFetcher fetcher) {
    	HistoricalDataUpdater updater = new HistoricalDataUpdater(stockFilename, fetcher);
    	
    }
}
