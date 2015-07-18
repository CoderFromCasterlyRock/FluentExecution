package com.fluent.etrading.events.in;

import com.eclipsesource.json.*;
import com.fluent.etrading.order.Side;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.Exchange;
import com.fluent.framework.market.InstrumentType;

import static com.fluent.etrading.util.JSONUtil.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.core.FluentJsonTags.*;


public final class NewStrategyEvent extends InEvent{

	private final String eventId;
	private final String strategyId;
	private final String strategyName;
	private final String strategyTrader;
	private final Side strategySide;
    private final int strategyLegCount;
    private final Exchange strategyExchange;
    private final double strategySpread;
    
    private final int[] legQtys;
    private final Side[] legSides;
    private final String[] legInstruments;
    private final boolean[] legWorking;
    private final int[] legSlices;
    private final InstrumentType[] legTypes;
    
    private final static long serialVersionUID 	= 1L;
    private final static String PREFIX 			= "NewStrategy-";
    
    
    public NewStrategyEvent( String strategyId, String strategyName, String strategyTrader, Side strategySide, int strategyLegCount, Exchange strategyExchange, double strategySpread,
                             int[] legQtys, Side[] legSides, String[] legInstruments, boolean[] legWorking, int[] legSlices, InstrumentType[] legTypes ){

        super( InType.NEW_STRATEGY );

        this.strategyId			= strategyId;
        this.strategyTrader     = strategyTrader;
        this.strategyName       = strategyName;
        this.strategySide       = strategySide;
        this.strategyLegCount   = strategyLegCount;
        this.strategyExchange	= strategyExchange;
        this.strategySpread     = strategySpread;

        this.legQtys      		= legQtys;
        this.legSides          	= legSides;
        this.legInstruments    	= legInstruments;
        this.legWorking       	= legWorking;
        this.legSlices       	= legSlices;
        this.legTypes       	= legTypes;
        
        this.eventId			= PREFIX + strategyId + UNDERSCORE + getSequenceId();
   
    }

   
    @Override
    public final String getEventId( ){
        return eventId;
    }
    
    
    public final String getStrategyId( ){
        return strategyId;
    }
    

    
    public final String getStrategyTrader(){
        return strategyTrader;
    }
    

    public final String getStrategyName(){
        return strategyName;
    }

    
    public final double getStrategySpread(){
        return strategySpread;
    }

    
    public final Side getStrategySide(){
        return strategySide;
    }

    
    public final int getStrategyLegCount(){
        return strategyLegCount;
    }


    public final Exchange getStrategyExchange( ){
        return strategyExchange;
    }

    
    public final int[] getLegQuantities(){
        return legQtys;
    }
    

    public final Side[] getLegSides(){
        return legSides;
    }

    
    public final String[] getInstruments(){
        return legInstruments;
    }

    
    public final boolean[] getLegWorkings(){
        return legWorking;
    }

      
    public final int[] getLegSlices( ){
        return legSlices;
    }

    
    public final InstrumentType[] getLegTypes(){
        return legTypes;
    }
    
    
    @Override
    protected final void toJSON( final JsonObject object ){

        object.add( STRATEGY_ID.field(),        strategyId );
        object.add( STRATEGY_NAME.field(),      strategyName );
        object.add( STRATEGY_OWNER.field(),     strategyTrader );
        object.add( STRATEGY_SIDE.field(),      strategySide.name() );
        object.add( EXCHANGE.field(),      		strategyExchange.name() );
        object.add( SPREAD.field(), 			strategySpread );
        object.add( STRATEGY_LEG_COUNT.field(), strategyLegCount );
        
        object.add( QUANTITIES.field(),         convertFromIntArray( legQtys ) );
        object.add( INSTRUMENTS.field(),        convertFromStringArray( legInstruments ) );
        object.add( WORKING.field(),        	convertFromBoolArray( legWorking ) );
        object.add( SIDES.field(),          	convertFromSideArray( legSides ) );
        object.add( SLICES.field(),         	convertFromIntArray( legSlices ) );
        object.add( INSTRUMENT_TYPE.field(),   	convertFromInstrumentTypesArray( legTypes ) );
        
    }
    

}


