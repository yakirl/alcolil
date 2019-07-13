package org.gitprof.alcolil.core;

import java.util.List;
import java.util.Map;

import org.gitprof.alcolil.common.*;

public class Command {

	public Opcode opcode; // DO_NOTHING, RUN_BACKTEST, START_REALTIME, STOP_REALTIME, OPTIMIZE, UPDATE_DB,
	public boolean startStop;
	public Time from;
	public Time to;
	public Interval intetrval;
	public List<String> symbols;
	public boolean debug;
	public QuoteObserver observer;
	public String symbolToObserve;
	
	public enum Opcode {
		DO_NOTHING, BACKTEST, REALTIME, OPTIMIZE, UPDATE_DB
	}
	
	public Command(Opcode opcode) {
		this.opcode = opcode;		
	}
}
