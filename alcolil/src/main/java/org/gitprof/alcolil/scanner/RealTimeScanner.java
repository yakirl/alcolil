package org.gitprof.alcolil.scanner;

import java.lang.Thread;

import java.util.List;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.scanner.CoreScanner;

public class RealTimeScanner implements Runnable {

    CoreScanner coreScanner;
    Thread scannerThread;
    
    public RealTimeScanner() {
    }
    
    public void startRealtime(List<String> symbols, Interval interval, Time from, Time to) {
            //coreScanner = new CoreScanner(CoreScanner.ScannerMode.REALTIME, symbols, interval);
            //coreScanner.backtest();      
    }
    
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
