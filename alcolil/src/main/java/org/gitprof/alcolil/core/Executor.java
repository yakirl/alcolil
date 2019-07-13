package org.gitprof.alcolil.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Executor {

    protected static final Logger LOG = LogManager.getLogger(Executor.class);
    
    public static void main(String[] args) {
        LOG.info("Executor start");
        Executor executer = new Executor();
        try {
        	String[] parsedArgs = executer.parseArgs(args);
        	executer.runCore(parsedArgs);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        LOG.info( "Executor finished" );
    }

    private void runCore(String[] args) throws Exception {
	   Core core = new Core();
	   core.start(args);
    }
   
    private String[] parseArgs(String[] args) {
    	return args;
    }
}
