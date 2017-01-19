package org.gitprof.alcolil.ordering;

import org.gitprof.alcolil.common.*;
import java.math.BigDecimal;

public class ExecutionPoint {

	ATime time;
	BigDecimal price;
	int shares;
	ExecutionType type;
	boolean isExecuted;
	boolean isCancled;
	
}
