package org.gitprof.alcolil.core;

import java.io.IOException;
import java.lang.Thread;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.gitprof.alcolil.scanner.BackTester;
import org.gitprof.alcolil.scanner.BackTestPipe;
import org.gitprof.alcolil.ui.UserInterface;
import org.gitprof.alcolil.stats.StatsCalculator;
import org.gitprof.alcolil.scanner.ParamOptimizer;
import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.IntegratedDBManager;
import org.gitprof.alcolil.database.DBManagerAPI;
import org.gitprof.alcolil.marketdata.HistoricalDataUpdater;
import org.gitprof.alcolil.marketdata.FetcherAPI;
import org.gitprof.alcolil.marketdata.YahooFetcher;

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
    		backtest(cmd.from, cmd.to);
    	} else if (Command.Opcode.REALTIME == cmd.opcode) {
    		realTimeScanStart();
    	} else if (Command.Opcode.UPDATE_DB == cmd.opcode) {
    		updateLocalDB();    	
    	} else if (Command.Opcode.DO_NOTHING == cmd.opcode) {
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

    private void backtest(Time from, Time to) throws IOException {
    	List<String> symbols = dbManager.getStockCollection().getSymbols();
    	Interval interval = Interval.ONE_MIN;
    	BackTester backTester = new BackTester(dbManager, null);
    	Thread t = new Thread() {
    		public void run() {
    			backTester.backtest(symbols, interval, from, to, true, new HashMap<String, QuoteObserver>());
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
    
    private void updateLocalDB() throws Exception {
    	FetcherAPI fetcher = new YahooFetcher();
    	BackTestPipe pipe = new BackTestPipe(dbManager, fetcher, BackTestPipe.PipeSource.LOCAL, Interval.ONE_MIN);
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