package org.gitprof.alcolil.common;

public class Enums {
	public enum OrderType {
		BUY_STP, BUY_LMT, SELL_STP, SELL_LMT
	}
	
	public enum ExitType {
		SL, TP
	}

	public boolean isLong(OrderType orderType) {
		return (OrderType.BUY_STP == orderType) || (OrderType.BUY_LMT == orderType);
	}
	
	public boolean isShort(OrderType orderType) {
		return !isLong(orderType);
	}
	
	public enum GraphInterval {
		ONE_MIN, FIVE_MIN,  DAILY
	}
}
