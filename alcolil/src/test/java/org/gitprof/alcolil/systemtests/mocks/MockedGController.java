package org.gitprof.alcolil.systemtests.mocks;

import java.util.Arrays;

import org.gitprof.alcolil.common.Quote;
import org.gitprof.alcolil.common.QuoteObserver;
import org.gitprof.alcolil.core.Command;
import org.gitprof.alcolil.core.Core;
import org.gitprof.alcolil.ui.GUI;
import org.gitprof.alcolil.ui.GUIHandlers;


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
