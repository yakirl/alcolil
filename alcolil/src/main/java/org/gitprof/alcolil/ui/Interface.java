package org.gitprof.alcolil.ui;

import javax.swing.SwingUtilities;

public class Interface implements Runnable {

	static Interface intf = null;
	
	private Interface() {
		
	}
	
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	private static Interface getInstance() {
		if (null == intf)
			intf = new Interface();
		return intf;
	}
	public static void startInterface() {
		Interface intf = getInstance();
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
