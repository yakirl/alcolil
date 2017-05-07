package org.gitprof.alcolil.core;

public class Executor {

	   public static void main( String[] args ) {
	        System.out.println( "Executer" );
	        Executor executer = new Executor();
	        try {
	        	//System.in.read();
	        	executer.parseArgs(args);
	        	executer.runCore();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	        System.out.println( "finished" );
	    }
	   
	   private void runCore() {
		   Core core = new Core();
		   core.runCommand(new Command("UPDATE_DB"));
		   core.start();
	   }
	   
	   private void parseArgs(String[] args) {
		   
	   }
}
