package com.fluent.etrading.core;

import org.slf4j.*;
import org.springframework.context.support.*;

import com.fluent.etrading.framework.core.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class FluentExecutionLauncher{


    public static void main( String args [] ){

    	AbstractApplicationContext context	= null; 
        Logger logger                       = LoggerFactory.getLogger( FluentExecutionLauncher.class.getSimpleName() );

        try{

            if( args.length == ZERO ){
                System.err.println("ERROR: Please provide a list of Application Context files.");
                System.exit( ZERO );
            }

            long initialTime                = System.currentTimeMillis();
            logger.info( "Attempting to START Fluent Framework {}.", FluentContext.getContainerInfo() );

            context 						= new ClassPathXmlApplicationContext( args[ZERO] );
            context.registerShutdownHook();
                        
            logger.info( "Successfully STARTED Fluent Framework in [{}] ms.", ( System.currentTimeMillis() - initialTime) );
            logger.info( "************************************************************** {}", NEWLINE );


        }catch( Exception e ){
            logger.error( "Fatal error while starting Fluent Framework." );
            logger.error( "Exception: ", e );
            logger.info( "************************************************************** {}", NEWLINE );

        	if( context != null  ) context.close();
        	
            System.exit( ZERO );
            
        }

    }


}
