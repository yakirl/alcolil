package org.gitprof.alcolil.core;


import java.io.IOException;
import java.lang.Thread;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


import org.gitprof.alcolil.scanner.BackTester;
import org.gitprof.alcolil.ui.UserInterface;
import org.gitprof.alcolil.stats.StatsCalculator;
import org.gitprof.alcolil.scanner.ParamOptimizer;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.marketdata.BaseFetcher;


public class Core 
{
	
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	private Map<Module, Thread> threads;
	private boolean toClose = false;
	private AtomicReference<Command> waitingCommand;
	
	public Core() {
		waitingCommand = new AtomicReference<Command>();
	}
	
	private enum  Module {
		BACKTEST_SCANNER, REALTIME_SCANNER, ACCOUNT, INTERFACE 
	}
	
    public void runCommand(Command cmd) {
    	if ("DO_NOTHING" == waitingCommand.get().opcode())
    		waitingCommand.set(cmd);
    }
	
	public void start() {
		runInterface();
    }
    
    
    /*
     * Main Core Functionalities: 
     *  1. backtest
     *  2. realtime scan
     *  3. update local DB
     *  4. show statistics
     *  5. run parameter optimizer 
     */

    private void updateLocalDB(String stockFilename, BaseFetcher fetcher) throws IOException {
    	HistoricalDataUpdater updater = new HistoricalDataUpdater(stockFilename);
    	
    }
    
    private AStockCollection getStockCollection() throws IOException {
    	DBManager dbManager = DBManager.getInstance();
    	return dbManager.getStockCollection();
    }
    
    private void backtest(ATime from, ATime to) throws IOException {
    	AStockCollection stocks = getStockCollection();
    	AInterval interval = AInterval.ONE_MIN;
    	BackTester backTester = new BackTester();
    	backTester.backTest(stocks, interval, from, to);
    }
    
    private void realTimeScanStart() {
    	// run as thread
    }
    
    private void realTimeScanStop() {
    	
    }
    
    private void calcStats() {
    	StatsCalculator statsCalculator = new StatsCalculator();
    	statsCalculator.calc();
    }
    
    private void optimizeParameters() {
    	ParamOptimizer paramOptimizer = new ParamOptimizer();
    	paramOptimizer.optimize();
    }
      
    private void checkWaitingCommand() throws IOException {
    	Command cmd = waitingCommand.get();
    	if ("RUN_BACKTEST" == cmd.opcode()) {
    		backtest(cmd.from, cmd.to);
    	} else if ("START_REALTIME" == cmd.opcode()) {
    		
    	} else if ("STOP_REALTIME" == cmd.opcode()) {
    		
    	} else if ("UPDATE_DB" == cmd.opcode()) {
    		updateLocalDB();
    	} else if ("OPTIMIZE" == cmd.opcode()) {
    		
    	} else {
    		LOG.error("found unrecognized command!");
    	}
    }
    
    private void mainLoop() throws IOException {
    	while(true) {
    		checkWaitingCommand();
    		if (toClose) {
    			break;
    		}
    	}
    	
    }
    
    private void runInterface() {
    	UserInterface.startInterface();
    }
    

}
