package com.fluent.etrading.market;

import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;

import com.fluent.etrading.util.ExecUtil;
import com.fluent.framework.collection.FluentThreadFactory;
import com.fluent.framework.market.*;
import com.fluent.framework.transport.core.AbstractTransport;
import com.fluent.framework.transport.core.TransportType;

import static com.fluent.framework.util.FluentUtil.*;


public final class GeneratedMarketDataTransport extends AbstractTransport implements Runnable{

    private final Exchange exchange;
    private final String[] instruments;
    private final int frequency;
    private final TimeUnit timeUnit;
    private final ScheduledExecutorService executor;

    private final static String NAME    	= GeneratedMarketDataTransport.class.getSimpleName();
    private final static Logger LOGGER  	= LoggerFactory.getLogger( NAME );
    
    //Should read instruments
    //Frequency and TimeUnit etc from the config file.
    public GeneratedMarketDataTransport( Exchange exchange, String[] instruments ){
    	this( ONE, TimeUnit.SECONDS, exchange, instruments );
    }
    
    
    public GeneratedMarketDataTransport( int frequency, TimeUnit timeUnit, Exchange exchange, String[] instruments ){
        super( TransportType.FILE );
        
        this.frequency		= frequency;
        this.timeUnit		= timeUnit;
    	this.exchange		= exchange;
    	this.instruments	= instruments;
        this.executor   	= Executors.newSingleThreadScheduledExecutor( new FluentThreadFactory(NAME) );
        
    }
    

    @Override
    public final String name( ){
        return NAME;
    }

    
	@Override
	public final boolean isConnected( ){
		return true;
	}
	
    
    @Override
    public final void start( ){
        executor.scheduleAtFixedRate( this, frequency, frequency, timeUnit );
        LOGGER.warn( "Successfully started will publish FAKE prices for {} every {} {}.",  Arrays.deepToString(instruments), frequency, timeUnit );
    }


    @Override
    public final void run( ){

    	StringBuilder builder	= new StringBuilder( SIXTY_FOUR );
    	
        for( String instrument : instruments ){

            double bidSeed  = 98.0;
            double bidTick  = Math.random();
            bidTick			= ( bidTick == 0 ) ? (0.005) : bidTick;
            
            double bid      = ExecUtil.bgRound(TWO, (bidSeed - bidTick));
            int bidSize     = 1000;
            double ask      = ExecUtil.bgRound(TWO, (bidSeed + bidTick));
            int askSize		= 1200;

            builder.append( exchange.name() ).append( COLON );
            builder.append( instrument ).append( COLON );
            builder.append( bid ).append( COLON );
            builder.append( bidSize).append( COLON );
            builder.append( ask ).append( COLON );
            builder.append( askSize );
            
            String message		= builder.toString( );
            //distribute(message);
            System.err.println( message );
            builder.setLength( ZERO );

        }

    }

   
    @Override
    public final void stop( ){
        executor.shutdown();
        LOGGER.info( "Executor successfully shut down." );
    }


    public final static double between( final double upper, final double lower ){
        return (Math.random() * (upper - lower)) + lower;
    }

    
    public static void main( String ... args ) throws Exception{
    	
    	if( args.length != 3){
    		System.err.println("Usage <runtimeInSeconds> <filelocation> <exchange>");
    		System.exit(1);
    	}
    	
    	int howLongInSeconds			= Integer.parseInt( args[0]);
    	String fileName					= args[1];
    	Exchange exchange				= Exchange.valueOf(args[2]);
    	
    	String[] instruments			= {"EDZ5", "EDH6", "EDM6", "EDU6", "EDZ6"};
    	
    	AbstractTransport tport			= new GeneratedMarketDataTransport( exchange, instruments );
    	
    	/*
    	final BufferedWriter bWriter 	= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
    	tport.register( new FluentDataListener(){
			
			@Override
			public void onMessage( String message ){
				try{
					
					bWriter.write( message );
					bWriter.newLine();
					
				}catch( IOException e ){
					e.printStackTrace();
				}
			}
		});
    	*/
    	tport.start();
    	Thread.sleep( howLongInSeconds * 1000 );
    	
    	tport.stop();
    	Thread.sleep( 1000 );
    	
    	//bWriter.close();
    }


}
