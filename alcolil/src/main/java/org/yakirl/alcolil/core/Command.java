package org.yakirl.alcolil.core;

import java.util.List;
import java.util.Map;

import org.yakirl.alcolil.common.*;

public class Command {

	public Opcode opcode;
	public boolean startStop;
	public Time from;
	public Time to;
	public Interval interval;
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
