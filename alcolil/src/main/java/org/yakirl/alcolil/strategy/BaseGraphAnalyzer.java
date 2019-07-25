package org.yakirl.alcolil.strategy;

import org.yakirl.alcolil.common.*;

public abstract class BaseGraphAnalyzer {

	Interval barInterval;
	
	public Interval getInterval() {
		return barInterval;
	}
}
