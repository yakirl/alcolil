package org.yakirl.alcolil.ui;

public interface GUIHandlers {

	public void backtest(boolean startStop, String symbolsCSV, GUI.CandleUpdater updater);
	
	public void updateDB(String symbolsCSV, String interval, GUI.Notifier notifier);
}
