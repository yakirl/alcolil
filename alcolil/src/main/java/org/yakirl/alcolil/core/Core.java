package org.yakirl.alcolil.core;

import java.io.IOException;
import java.lang.Thread;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.database.DBManagerAPI;
import org.yakirl.alcolil.database.IntegratedDBManager;
import org.yakirl.alcolil.marketdata.FetcherAPI;
import org.yakirl.alcolil.marketdata.HistoricalDataUpdater;
import org.yakirl.alcolil.marketdata.YahooFetcher;
import org.yakirl.alcolil.scanner.BackTestPipe;
import org.yakirl.alcolil.scanner.BackTester;
import org.yakirl.alcolil.scanner.ParamOptimizer;
import org.yakirl.alcolil.stats.StatsCalculator;
import org.yakirl.alcolil.ui.UserInterface;

/*****
 * @author yakir
 *
 * Core runs the main thread that listen to incoming command and execute the requested task.
 * Object of this class is initialize by the name thread, as the first step of the program, and used by other threads - "Core clients"
 * 	to perform actions.
 * 
 *  Core clients                        Main thread
 *                       ___________           |
 * GUI controller -post->| Core Obj |<-listen--|
 *                       -----------           |
 *                                             |
 *                                             |
 *                                             |
 *                                             \/
 *                                             
 *  The view part of the MVC arch should be implemented by Observer pattern.
 */


public class Core 
{
	
	//protected static final Logger LOG = LogManager.getLogger(Core.class);
	protected static final Logger LOG = LogManager.getLogger(Core.class);
	// private Map<Module, Thread> threads;
	private boolean toClose = false;
	private AtomicReference<Command> waitingCommand;
	private DBManagerAPI dbManager;
	private int commandDispatcherWaitMillis = 1000;

	public Core() throws Exception {
		dbManager = IntegratedDBManager.getInstance();
		waitingCommand = new AtomicReference<Command>();
		resetWaitingCmd();
	}
	
	public void resetWaitingCmd() {
		waitingCommand.set(new Command(Command.Opcode.DO_NOTHING));
	}
	
	private enum  Module {
		BACKTEST_SCANNER, REALTIME_SCANNER, ACCOUNT, INTERFACE 
	}
	
	private void setUp() throws Exception {
	    dbManager.validateDBStructure();
	}
	
	public void start(String[] args) {
		try {
		    runInterface();
		    setUp();				
			mainLoop();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	public void postCommand(Command cmd) {
		LOG.info( "Core posting command: " + cmd.opcode);
    	if (Command.Opcode.DO_NOTHING == waitingCommand.get().opcode)
    		waitingCommand.set(cmd);
    }
	
	private void commandDispatcher() throws Exception {
    	Command cmd = waitingCommand.get();
    	LOG.info("Core dispatching command: " + cmd.opcode);
    	if (Command.Opcode.BACKTEST == cmd.opcode) {
    		backtest(cmd);
    	} else if (Command.Opcode.REALTIME == cmd.opcode) {
    		realTimeScanStart();
    	} else if (Command.Opcode.UPDATE_DB == cmd.opcode) {
    		updateLocalDB(cmd);    	
    	} else if (Command.Opcode.DO_NOTHING == cmd.opcode) {
    		LOG.info("DO_NOTHING");
    	} else {
    		LOG.error("found unrecognized command!");
    	}
    	resetWaitingCmd();
    }
    
    private void mainLoop() {
    	while(true) {
    		try {
    			commandDispatcher();
    			if (toClose) {
    				break;
    			}
    			Thread.sleep(commandDispatcherWaitMillis);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    private void runInterface() {
    	UserInterface.startInterface(this);
    }
	
    /***************************
     * Main Core Functionalities: 
     *  1. backtest
     *  2. realtime scan
     *  3. update local DB
     *  4. show statistics
     *  5. run parameter optimizer 
     ****************************/

    private void backtest(Command cmd) throws IOException {
    	List<String> symbols = cmd.symbols;
    	Interval interval = cmd.interval;
    	BackTester backTester = new BackTester(dbManager, null);
    	HashMap<String, QuoteObserver> observers = new HashMap<String, QuoteObserver>();
    	observers.put(cmd.symbolToObserve, cmd.observer);
    	Thread t = new Thread() {
    		public void run() {
    			backTester.backtest(symbols, interval, cmd.from, cmd.to, true, observers);
    		}
    	};
    	t.start();
    }
    
    private void realTimeScanStart() {
    	// TODO
    	// run as thread
        // RealTimeScanner realtime = new RealTimeScanner();
        // realtime.startRealtime();
    }
    
    private void realTimeScanStop() {
    	
    }
    
    private void updateLocalDB(Command cmd) throws Exception {
    	FetcherAPI fetcher = new YahooFetcher();
    	BackTestPipe pipe = new BackTestPipe(dbManager, fetcher, BackTestPipe.PipeSource.LOCAL, cmd.symbols, cmd.interval);
    	HistoricalDataUpdater updater = new HistoricalDataUpdater(dbManager, pipe);
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