package org.gitprof.alcolil.ui;

import java.awt.event.*;

import org.gitprof.alcolil.core.Core;
import org.gitprof.alcolil.core.Command;

/*
 * The controller part of the MVC arch.
 */

public class GController implements ActionListener {

	Core model;
	public void go() {
		
	}
	
	public void listenToGUI() {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd  = e.getActionCommand();
		if ("RUN_BACKTEST" == cmd) {
			model.postCommand(new Command("RUN_BACKTEST"));
		} else if ("UPDATE_DB" == cmd) {
			
		} else {
			
		}
		
	}
	
	
}
