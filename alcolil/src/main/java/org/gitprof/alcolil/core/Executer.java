package org.gitprof.alcolil.core;

public class Executer {

	   public static void main( String[] args ) {
	        System.out.println( "Executer" );
	        Executer executer = new Executer();
	        try {
	        	//System.in.read();
	        	executer.runCore();
	        } catch(Exception e) {
	        	
	        }
	        System.out.println( "finished" );
	    }
	   
	   private void runCore() {
		   Core core = new Core();
		   core.start();
	   }
}
