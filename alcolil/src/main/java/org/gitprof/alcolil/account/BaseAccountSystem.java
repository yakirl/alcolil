package org.gitprof.alcolil.account;

import org.gitprof.alcolil.common.*;

public abstract class BaseAccountSystem {

	String ordersFilename;
	boolean orderOnAlert;
	AlertingSystem alertingSystem;
	
	public BaseAccountSystem() {
		//this.ordersFilename = ordersFilename;
		this.orderOnAlert = false;
		this.alertingSystem = new AlertingSystem();
	}
	
	private void updateOrdersFile(AOrder order) {
		// write to file
	}
	
	public abstract int makeOrder(AOrder order);
	
	public abstract void cancelOrder(int orderID);
	
	public void setAlert(Alert alert) {
		alertingSystem.alert(alert);
		if (orderOnAlert) {
			makeOrder(new AOrder(alert));
		}
	}
}
