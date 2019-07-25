package org.yakirl.alcolil.account;

import java.math.BigDecimal;

import org.yakirl.alcolil.common.*;

public class ExecutionPoint {

	Time time;
	BigDecimal price;
	int shares;
	ExecutionType type;
	boolean isExecuted;
	boolean isCancled;
	
}
