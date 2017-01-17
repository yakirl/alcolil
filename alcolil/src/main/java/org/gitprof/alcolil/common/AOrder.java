package org.gitprof.alcolil.common;

public class AOrder {

	ATime placingTime; 
	APrice stopLoss;
	APrice takeProfit1;
	APrice takeProfit2;
	boolean isExecuted;
	ATime executionTime; // valid only if isExecuted == true
	
}
