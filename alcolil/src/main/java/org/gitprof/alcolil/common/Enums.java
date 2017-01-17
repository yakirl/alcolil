package org.gitprof.alcolil.common;

public class Enums {
	public enum OrderType {
		BUY_MKT, BUY_STP, BUY_LMT, SELL_MKT, SELL_STP, SELL_LMT
	}
	
	public enum ExitType {
		SL, TP
	}

	public boolean isLong(OrderType orderType) {
		return (OrderType.BUY_STP == orderType)  || 
				(OrderType.BUY_LMT == orderType) ||
				(OrderType.BUY_MKT == orderType);
	}
	
	public boolean isShort(OrderType orderType) {
		return !isLong(orderType);
	}
	
	public enum GraphInterval {
		ONE_MIN, FIVE_MIN,  DAILY
	}
}
