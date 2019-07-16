package org.gitprof.alcolil.database;

public class DBException extends Exception {

	private String detailMessage;
	
	public DBException(String msg, Exception e) {
		detailMessage = msg;
	}
	
	public DBException(String msg) {
		detailMessage = msg;
	}
}
