package org.gitprof.alcolil.database;

public class CSVObject {
	private String[] fields;
	
	public CSVObject(String[] fields) {
		setFields(fields);
	}
	
	public String getField(int ix) {
		return fields[ix];
	}
	
	public void setFields(String[] fields) {
		this.fields = fields;
	}
}
