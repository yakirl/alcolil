package org.gitprof.alcolil.core;

import java.io.IOException;
import java.lang.Thread;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.gitprof.alcolil.scanner.BackTester;
import org.gitprof.alcolil.scanner.RealTimeScanner;
import org.gitprof.alcolil.ui.UserInterface;
import org.gitprof.alcolil.stats.StatsCalculator;
import org.gitprof.alcolil.scanner.ParamOptimizer;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.DBManager;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.marketdata.FetcherAPI;


public class Core 
{
	
	//protected static final Logger LOG = LogManager.getLogger(Core.class);
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	private Map<Module, Thread> threads;
	private boolean toClose = false;
	private AtomicReference<Command> waitingCommand;

	public Core() {
		waitingCommand = new AtomicReference<Command>();
		waitingCommand.set(new Command("DO_NOTHING"));
	}
	
	private enum  Module {
		BACKTEST_SCANNER, REALTIME_SCANNER, ACCOUNT, INTERFACE 
	}
	
	private void setUpEnv() throws Exception {
	    DBManager.validateDBStructure();
	}
	
	public void start(String[] args) {	    
		// runInterface();
		// mainLoop();
		try {
		    setUpEnv();	
			postCommand(new Command("DO_NOTHING"));
			commandDispatcher();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public void postCommand(Command cmd) {
		System.out.println( "core posting command: " + cmd.opcode());
		LOG.info("Core posting command");
    	if ("DO_NOTHING" == waitingCommand.get().opcode())
    		waitingCommand.set(cmd);
    }
	
	private void commandDispatcher() throws IOException {
    	Command cmd = waitingCommand.get();
    	LOG.info("Core dispatching command: " + cmd.opcode());
    	if ("RUN_BACKTEST" == cmd.opcode()) {
    		backtest(cmd.from, cmd.to);
    	} else if ("START_REALTIME" == cmd.opcode()) {
    		realTimeScanStart();
    	} else if ("STOP_REALTIME" == cmd.opcode()) {
    		realTimeScanStop();
    	} else if ("UPDATE_DB" == cmd.opcode()) {
    		updateLocalDB();
    	} else if ("OPTIMIZE" == cmd.opcode()) {
    		optimizeParameters();
    	} else if ("CALC_STATS" == cmd.opcode()) {
    		calcStats();
    	} else if ("DO_NOTHING" == cmd.opcode()) {
    		LOG.info("DO_NOTHING");
    	} else {
    		LOG.error("found unrecognized command!");
    	}
    }
    
    private void mainLoop() {
    	while(true) {
    		try {
    			commandDispatcher();
    			if (toClose) {
    				break;
    			}
    			Thread.sleep(1000);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    private void runInterface() {
    	UserInterface.startInterface();
    }
	
    /***************************
     * Main Core Functionalities: 
     *  1. backtest
     *  2. realtime scan
     *  3. update local DB
     *  4. show statistics
     *  5. run parameter optimizer 
     ****************************/

    private void backtest(ATime from, ATime to) throws IOException {
    	List<String> symbols = DBManager.getInstance().getStockCollection().getSymbols();
    	AInterval interval = AInterval.ONE_MIN;
    	BackTester backTester = new BackTester();
    	backTester.backtest(symbols, interval, from, to);
    }
    
    private void realTimeScanStart() {
    	// run as thread
        RealTimeScanner realtime = new RealTimeScanner();
        // realtime.startRealtime();
    }
    
    private void realTimeScanStop() {
    	
    }
    
    private void updateLocalDB() throws IOException {
    	HistoricalDataUpdater updater = new HistoricalDataUpdater();
    	updater.updateQuoteDB();
    }
    
    private void calcStats() {
    	StatsCalculator statsCalculator = new StatsCalculator();
    	statsCalculator.calc();
    }
    
    private void optimizeParameters() {
    	ParamOptimizer paramOptimizer = new ParamOptimizer();
    	paramOptimizer.optimize();
    }
      
    
}
