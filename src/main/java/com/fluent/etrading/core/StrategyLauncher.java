package com.fluent.etrading.core;

import org.slf4j.*;
import java.lang.Thread.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class StrategyLauncher{
	
    private final static String CFG_KEY = "fluent.configFile";
    
    private final static String NAME	= StrategyLauncher.class.getSimpleName();
	private final static Logger LOGGER	= LoggerFactory.getLogger( NAME );

    static{
        Thread.setDefaultUncaughtExceptionHandler( new UncaughtExceptionHandler(){
            @Override
            public void uncaughtException( Thread thread, Throwable ex ){
                LOGGER.warn("CAUGHT unhandled exception in Thread [{}]", thread.getName() );
                LOGGER.warn("Exception: ", ex );
            }
        });
    }
    

	public static void main( String args [] ){
		
		try{
		
			String cfgFileName               = getConfigFile(CFG_KEY);
			StrategyController controller	 = new StrategyController(cfgFileName);
			
		    LOGGER.debug("Attempting to START {}.", controller );
	        LOGGER.debug("Using Configuration {}{}", controller.getServices().getCfgManager(), NEWLINE );
	    
	        Runtime.getRuntime().addShutdownHook( new FluentShutdownThread(controller) );
	        controller.start();
						
		}catch( Exception e ){
			LOGGER.warn("ERROR starting application!", e);
			System.exit( ONE );
		}

    }
	
	
	public final static class FluentShutdownThread extends Thread{

        private final StrategyController controller;
        
        public FluentShutdownThread( StrategyController controller ){
            this.controller = controller;
        }

        @Override
        public final void run( ){

            try{
            
                LOGGER.info("Shutdown hook called, will attempt to stop all services in the controller.");
                controller.stop();
                
                LOGGER.info("Shutdown hook executed successfully.");
                LOGGER.info("---------------------------------------");
                
            }catch( Exception e ){
                LOGGER.warn("Exception while running shut-down hook.", e);
            }
    
        }
    
    }

	
}

