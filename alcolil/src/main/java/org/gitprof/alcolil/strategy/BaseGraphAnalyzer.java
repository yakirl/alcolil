package org.gitprof.alcolil.strategy;

import org.gitprof.alcolil.common.*;

public abstract class BaseGraphAnalyzer {

	Enums.GraphInterval barInterval;
	
	public Enums.GraphInterval getInterval() {
		return barInterval;
	}
}
