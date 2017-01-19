package org.gitprof.alcolil.ui;

import javax.swing.SwingUtilities;

public class UserInterface implements Runnable {

	static UserInterface intf = null;
	
	private UserInterface() {
		
	}
	
	private void initializeAndRun() {
		GController gcontroller = new GController();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				(new GUI(gcontroller)).startGUI();
			}
		});
		gcontroller.go();
	}
	
	public void run() {
			
	}
	
	private static UserInterface getInstance() {
		if (null == intf)
			intf = new UserInterface();
		return intf;
	}
	public static void startInterface() {
		UserInterface intf = getInstance();
		try {
			SwingUtilities.invokeLater(intf);
			//SwingUtilities.invokeAndWait(runnable);
		} catch (Exception e) {}
		
		//Thread t = new Thread(runnable);
		//System.out.println("Executing thread for " + runnable.toString());
		//t.start();
		//threads.;
		//Util.threadsStatus(Util.lineNumber());
	}

}
