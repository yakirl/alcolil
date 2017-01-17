package org.gitprof.alcolil.common;

public class APrice {
	double val;
	
	public APrice(double val) {
		this.val = val;
	}
	
	public APrice(String str) {
		val = Double.parseDouble(str);
	}
	
	public double getDouble() {
		return val;
	}
}
