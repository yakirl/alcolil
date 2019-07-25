package org.yakirl.alcolil.systemtests.mocks;

import java.util.Arrays;

import org.yakirl.alcolil.common.Quote;
import org.yakirl.alcolil.common.QuoteObserver;
import org.yakirl.alcolil.core.Command;
import org.yakirl.alcolil.core.Core;
import org.yakirl.alcolil.ui.GUI;
import org.yakirl.alcolil.ui.GUIHandlers;


public class MockedGController implements GUIHandlers {
	MockedCore model;
	GUI view;
	
	public MockedGController(MockedCore core) {
		model = core;
	}
	
	@Override
	public void backtest(boolean startStop, String symbolsCSV, GUI.CandleUpdater updater) {
		Command cmd = new Command(Command.Opcode.BACKTEST);
		cmd.startStop = true;
		cmd.symbols = Arrays.asList(symbolsCSV.split("\\s*,\\s*"));
		cmd.observer = new QuoteObserver() {
			public void observe(Quote quote) {
				updater.update(quote.time().getSeconds(), quote.open().doubleValue(), quote.high().doubleValue(),
						quote.low().doubleValue(), quote.close().doubleValue(), quote.volume());				
			}
		};
		model.postCommand(cmd);
	}	
}
