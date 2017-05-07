package org.gitprof.alcolil.core;

import org.gitprof.alcolil.common.*;

public class Command {

	private String opcode; // DO_NOTHING, RUN_BACKTEST, START_REALTIME, STOP_REALTIME, OPTIMIZE, UPDATE_DB,
	public ATime from;
	public ATime to;
	
	public Command(String opcode) {
		this.opcode = opcode;
		
	}
	
	public String opcode() {
		return opcode;
	}
}
