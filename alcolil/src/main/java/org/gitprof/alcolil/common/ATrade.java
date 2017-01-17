package org.gitprof.alcolil.common;

import org.gitprof.alcolil.database.CSVObject;
import org.gitprof.alcolil.database.CSVable;

public class ATrade implements CSVable{

	private String symbol;
	int num_shares;
	APrice price;
	AOrder executingOrder;
	
	public String[] convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}
	public void initFromCSV(String[] csvs) {
		// TODO Auto-generated method stub
		
	}
}
