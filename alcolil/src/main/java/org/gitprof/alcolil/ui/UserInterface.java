package org.gitprof.alcolil.ui;

import javax.swing.SwingUtilities;

import org.gitprof.alcolil.core.Core;

public class UserInterface {
	
	public static void startInterface(Core core) {
		final GController gcontroller = new GController(core);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				(new GUI(gcontroller)).showGUI();
			}
		});		
	}
}
