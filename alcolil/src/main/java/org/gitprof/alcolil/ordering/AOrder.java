package org.gitprof.alcolil.ordering;

import org.gitprof.alcolil.common.*;
import org.gitprof.alcolil.database.CSVable;

public class AOrder implements CSVable {

	String symbol;
	ExecutionPoint entrance;
	ExecutionPoint takeProfit1;
	ExecutionPoint takeProfit2;
	ExecutionPoint stopLoss;
	
	public AOrder(String symbol, ExecutionPoint entrance, ExecutionPoint takeProfit1, ExecutionPoint takeProfit2, ExecutionPoint stopLoss) {
		this.symbol = symbol;
		this.entrance = entrance;
		this.takeProfit1 = takeProfit1;
		this.takeProfit2 = takeProfit2;
		this.stopLoss = stopLoss;
	}

	@Override
	public String[] convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initFromCSV(String[] csvs) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
