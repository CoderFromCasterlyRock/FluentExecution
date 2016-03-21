package com.fluent.etrading.events.in;

import com.fluent.etrading.order.Side;
import com.fluent.framework.events.in.*;
import com.fluent.framework.market.core.Exchange;
import com.fluent.framework.market.core.InstrumentType;
import com.fluent.framework.strategy.StrategyType;

import static com.fluent.etrading.util.JSONUtil.*;
import static com.fluent.framework.events.core.FluentJsonTags.*;


public final class NewStrategyEvent extends FluentInEvent{

	private final String strategyId;
	private final String strategyName;
	private final String strategyTrader;
	private final StrategyType strategyType;
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
        
    
    public NewStrategyEvent( String strategyId, String strategyName, String strategyTrader, StrategyType strategyType,
    						 Side strategySide, int strategyLegCount, Exchange strategyExchange, double strategySpread,
                             int[] legQtys, Side[] legSides, String[] legInstruments, boolean[] legWorking, int[] legSlices, InstrumentType[] legTypes ){

        super( FluentInType.NEW_STRATEGY );

        this.strategyId			= strategyId;
        this.strategyName       = strategyName;
        this.strategyTrader     = strategyTrader;
        this.strategyType		= strategyType;
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


    public final StrategyType getStrategyType(){
        return strategyType;
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
	public final void toEventString( StringBuilder object ){
		
		object.append( STRATEGY_TYPE.field()).append(strategyTrader );
		object.append( STRATEGY_ID.field()).append(strategyId );
        object.append( STRATEGY_NAME.field()).append(strategyName );
        object.append( STRATEGY_OWNER.field()).append(strategyTrader );
        object.append( STRATEGY_SIDE.field()).append(strategySide.name() );
        object.append( EXCHANGE.field()).append(strategyExchange.name() );
        object.append( SPREAD.field()).append(strategySpread );
        object.append( STRATEGY_LEG_COUNT.field()).append( strategyLegCount );
        
        object.append( QUANTITIES.field()).append(convertFromIntArray( legQtys ) );
        object.append( INSTRUMENTS.field()).append(convertFromStringArray( legInstruments ) );
        object.append( WORKING.field()).append(convertFromBoolArray( legWorking ) );
        object.append( SIDES.field()).append(convertFromSideArray( legSides ) );
        object.append( SLICES.field()).append(convertFromIntArray( legSlices ) );
        object.append( INSTRUMENT_TYPE.field()).append(convertFromInstrumentTypesArray( legTypes ) );
        
	}
    

}


