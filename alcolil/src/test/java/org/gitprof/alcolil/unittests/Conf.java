package org.gitprof.alcolil.unittests;

import java.nio.file.Paths;


public class Conf {

	public static String rootDir  = getCWD();
	public static String srcDir = Paths.get(rootDir, "src").toString();
	public static String testDir = Paths.get(srcDir, "test").toString();
	public static String testResourcesDir = Paths.get(testDir, "resources").toString(); 
	
	private static String getCWD() {
	    String cwd = Paths.get("").toAbsolutePath().toString();
	  //  LogManager.getLogger(Conf.class).info("current working directory: " + cwd);
	    return cwd;	    
	}
	
	public static String appendToTestResources(String... subpath) {	    
	    return append(testResourcesDir, subpath);
	}	  
	
	private static String append(String dir, String... subpath) {
	    return Paths.get(dir, subpath).toString();
	}
}