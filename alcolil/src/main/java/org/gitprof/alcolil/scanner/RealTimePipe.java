package org.gitprof.alcolil.scanner;

import org.gitprof.alcolil.common.*;

public class RealTimePipe extends BaseQuotePipe {

	ATime start;
	
	public RealTimePipe(AStockCollection stocks, ATime from) {
		start = from;
	}
	
	@Override
	public AQuote getNextQuote() {
		// TODO Auto-generated method stub
		return null;
	}

}
