package org.yakirl.alcolil.ui;

import java.util.Arrays;

import org.yakirl.alcolil.common.*;
import org.yakirl.alcolil.core.Command;
import org.yakirl.alcolil.core.Core;

/*
 * The controller part of the MVC arch.
 */

public class GController implements GUIHandlers {

	Core model;
	GUI view;
	
	public GController(Core core) {
		model = core;
	}
	
	@Override
	public void backtest(boolean startStop, String symbolsCSV, GUI.CandleUpdater updater) {
		Command cmd = new Command(Command.Opcode.BACKTEST);
		cmd.startStop = true;
		cmd.symbols = Arrays.asList(symbolsCSV.split("\\s*,\\s*"));
		cmd.symbolToObserve = cmd.symbols.get(0);
		cmd.interval = Interval.ONE_MIN;
		cmd.observer = new QuoteObserver() {
			public void observe(Quote quote) {
				updater.update(quote.time().getSeconds(), quote.open().doubleValue(), quote.high().doubleValue(),
						quote.low().doubleValue(), quote.close().doubleValue(), quote.volume());				
			}
		};
		model.postCommand(cmd);
	}
}
