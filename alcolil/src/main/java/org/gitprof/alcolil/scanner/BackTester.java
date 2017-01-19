package org.gitprof.alcolil.scanner;

//import org.gitprof.alcolil.strategy.BaseAnalyzer;
import org.gitprof.alcolil.common.ATime;
import org.gitprof.alcolil.common.AStockCollection;
import org.gitprof.alcolil.scanner.CoreScanner;

public class BackTester {
	
	public void backTest(ATime from, ATime to) {
		// load stockCollection from default file, 
		// load default analyzer
	}
	
	public void backTest(AStockCollection stocks, ATime from, ATime to) {
		CoreScanner coreScanner = new CoreScanner(stocks, from, to);
		coreScanner.backtest(stocks, from, to);
	}
	
}
