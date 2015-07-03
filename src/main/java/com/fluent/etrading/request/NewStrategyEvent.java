package com.fluent.etrading.request;

import com.eclipsesource.json.*;
import com.fluent.etrading.order.Side;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.Exchange;

import static com.fluent.etrading.util.JSONUtil.*;
import static com.fluent.framework.events.core.FluentJsonTags.*;


public final class NewStrategyEvent extends InboundEvent{

	private final String eventId;
    private final String strategyOwner;
	private final String strategyName;
    private final Side strategySide;
    private final int strategyLegCount;
    private final double spread;
    
    private final String[] legInstruments;
    private final Exchange[] legExchanges;
    private final int[] legQtys;
    private final Side[] legSides;
    
    private final static long serialVersionUID 	= 1L;
    private final static String PREFIX 			= "NewStrategy-";
    
    
    public NewStrategyEvent( InboundType type,
                             String strategyOwner, String strategyName, Side strategySide, int strategyLegCount, 
                             double spread, String[] legInstruments, Exchange[] legExchanges, int[] legQtys, Side[] legSides ){

        super( type );

        this.eventId			= PREFIX + getSequenceId();
        this.strategyOwner      = strategyOwner;
        this.strategyName       = strategyName;
        this.strategySide       = strategySide;
        this.strategyLegCount   = strategyLegCount;
        this.spread             = spread;

        this.legInstruments    	= legInstruments;
        this.legExchanges       = legExchanges;
        this.legQtys      		= legQtys;
        this.legSides          	= legSides;
   
    }

   
    @Override
    public final String getEventId( ){
        return eventId;
    }

    
    public final String getOwner(){
        return strategyOwner;
    }
    

    public final String getStrategyName(){
        return strategyName;
    }

    
    public final double getSpread(){
        return spread;
    }

    
    public final Side getStrategySide(){
        return strategySide;
    }

    
    public final int getStrategyLegCount(){
        return strategyLegCount;
    }


    public final Exchange[] getMarketTypes( ){
        return legExchanges;
    }


    public final String[] getInstrumentArray(){
        return legInstruments;
    }

    
    public final int[] getQuantityArray(){
        return legQtys;
    }

    
    public final Side[] getSideArray(){
        return legSides;
    }

    
    
    @Override
    protected final void toJSON( final JsonObject object ){

        object.add( STRATEGY_ID.field(),        getEventId() );
        object.add( STRATEGY_NAME.field(),      getStrategyName() );
        object.add( STRATEGY_OWNER.field(),     getOwner() );
        object.add( STRATEGY_SIDE.field(),      getStrategySide().name() );
        object.add( STRATEGY_LEG_COUNT.field(), getStrategyLegCount() );
        object.add( INSTRUMENTS.field(),        convertFromStringArray( getInstrumentArray() ) );
        object.add( MARKETS.field(),            convertFromMarketArray( getMarketTypes()) );
        object.add( QUANTITIES.field(),         convertFromIntArray( getQuantityArray() ) );
        object.add( SIDES.field(),              convertFromSideArray( getSideArray() ) );
        object.add( SPREAD.field(),             getSpread() );
        
    }
    

}

