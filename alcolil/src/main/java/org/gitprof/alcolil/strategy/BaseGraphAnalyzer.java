package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;

public abstract class BaseGraphAnalyzer {

	Interval barInterval;
	
	public Interval getInterval() {
		return barInterval;
	}
}
