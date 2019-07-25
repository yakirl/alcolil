package org.yakirl.alcolil.account;

import org.yakirl.alcolil.common.*;

public class AlertingSystem {

	public void alert(Alert alert) {
		//TODO: send order to orderingsystem if needed
		// send to GUI
		// write to file
	}
	
	public void alert(Quote quote) {
		Alert alert = new Alert();
	}
	
	private void showAlert(Alert alert) {
		// print to terminal and to GUI
	}
}
