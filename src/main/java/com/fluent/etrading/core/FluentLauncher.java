package com.fluent.etrading.core;

import org.slf4j.*;

import java.lang.Thread.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.FluentToolkit.*;


public final class FluentLauncher{
	
	private final FluentController controller;

	private final static String CFG_KEY	= "fluent.framework.configFile";
	private final static String NAME	= FluentLauncher.class.getSimpleName();
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

	
	protected FluentLauncher( String cfgFileName ) throws Exception{
		this.controller		= new FluentController( cfgFileName );
	}
	
    
	public static void main( String args [] ){
		
		try{
		
			String cfgFileLocation	= getConfigFile(CFG_KEY);
			FluentLauncher launcher	= new FluentLauncher( cfgFileLocation );
       	
			Runtime.getRuntime().addShutdownHook( new FluentShutdownThread(launcher.controller) );
			launcher.controller.start();
			
		}catch( Exception e ){
			LOGGER.warn("ERROR starting application!", e);
			System.exit( ONE );
		}

    }


	private final static String getConfigFile( String key ){
		
		String cfgFile	= System.getProperty(key);
		if( !isBlank(cfgFile) ) return cfgFile;

		String message 	= "Config file must be specified as a system parameter [" + CFG_KEY +"]";
		throw new RuntimeException( message );
				
	}
	
	
	public final static class FluentShutdownThread extends Thread{

		private final FluentController controller;
		
		public FluentShutdownThread( FluentController controller ){
			this.controller = controller;
		}
	
		
		@Override
		public final void run( ){

			try{
			
				LOGGER.info("Shutdown hook called, will attempt to stop all services");
				controller.stop();
				
				LOGGER.info("Shutdown hook executed successfully.");
				LOGGER.info("---------------------------------------");
				
			}catch( Exception e ){
				LOGGER.warn("Exception while running shut-down hook.", e);
			}
	
		}
	
	}

	
}

