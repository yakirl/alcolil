package org.gitprof.alcolil.ordering;

import org.gitprof.alcolil.common.*;

public abstract class BaseOrderingSystem {

	public abstract void makeOrder(AOrder order);
	
	public abstract void cancelOrder(int orderID);
}
