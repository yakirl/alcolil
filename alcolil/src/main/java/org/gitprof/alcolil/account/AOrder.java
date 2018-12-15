package org.gitprof.alcolil.account;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.CSVable;

/*
 * AOrder represents 4 logical different entities: alert, order, open trade, closed trade
 */
public class AOrder implements CSVable {

	String symbol;
	ExecutionPoint entrance;
	ExecutionPoint takeProfit1;
	ExecutionPoint takeProfit2;
	ExecutionPoint stopLoss;
	
	Quote trigerringQuote;
	String description;
	
	public AOrder(String symbol, ExecutionPoint entrance, ExecutionPoint takeProfit1, ExecutionPoint takeProfit2, ExecutionPoint stopLoss) {
		this.symbol = symbol;
		this.entrance = entrance;
		this.takeProfit1 = takeProfit1;
		this.takeProfit2 = takeProfit2;
		this.stopLoss = stopLoss;
	}

	public AOrder(Alert alert) {
		// TODO
	}
	
	@Override
	public String[] convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CSVable initFromCSV(String[] csvs) {
		// TODO Auto-generated method stub
		return this;
	}
	

	
	
	

}
