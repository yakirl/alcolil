package org.gitprof.alcolil.database;

public class DBException extends Exception {
	
	private static final long serialVersionUID = 7718828512143293558L;
	
	public DBException(String msg, Exception e) {
		super(msg, e);
	}
	
	public DBException(String msg) {
		super(msg);
		// detailMessage = msg;
	}
}
