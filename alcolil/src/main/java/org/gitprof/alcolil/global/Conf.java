package org.gitprof.alcolil.global;

import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;

public class Conf {

	public String rootDir;
	public String DBDir;
	public String logsDir;
	public String confsDir;
 
	public String backtestConfFile;
	public String stockFilterConfFile;
	public String analyzerConfFile;
	
	public Conf() {
		rootDir  = root();
		DBDir    = Paths.get(rootDir, "database").toString();
		logsDir  = Paths.get(rootDir, "logs").toString();
		confsDir = Paths.get(rootDir, "conf").toString();
	 
		backtestConfFile = Paths.get(confsDir, "backtester.properties").toString();
		stockFilterConfFile = Paths.get(confsDir, "stockfilter.properties").toString();
		analyzerConfFile = Paths.get(confsDir, "analyzer.properties").toString();
	}
	
	protected String root() {
		return getCWD();
	}
	
	protected String getCWD() {
	    String cwd = Paths.get("").toAbsolutePath().toString();
	    LogManager.getLogger(Conf.class).info("current working directory: " + cwd);
	    return cwd;	    
	}
	
	public String appendToConfsDir(String... subpath) {      
        return append(confsDir, subpath);
    }   
	
	private String append(String dir, String... subpath) {
	    return Paths.get(dir, subpath).toString();
	}
}
