package org.gitprof.alcolil.common;

import org.gitprof.alcolil.database.CSVObject;
import org.gitprof.alcolil.database.CSVable;

public class ATrade implements CSVable{

	private String symbol;
	int num_shares;
	APrice price;
	
	public CSVObject convertToCSV() {
		// TODO Auto-generated method stub
		return null;
	}
	public void initFromCSV(CSVObject csvObject) {
		// TODO Auto-generated method stub
		
	}
}
