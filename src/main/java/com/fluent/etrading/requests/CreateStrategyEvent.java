package com.fluent.etrading.requests;

import com.eclipsesource.json.*;

import com.fluent.etrading.framework.order.*;
import com.fluent.etrading.framework.events.in.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.framework.events.core.*;

import static com.fluent.etrading.framework.utility.JSONUtil.*;
import static com.fluent.etrading.framework.events.core.FluentJsonTags.*;


public final class CreateStrategyEvent extends TraderDataEvent{

    private final String strategyName;
    private final Side strategySide;
    private final int strategyLegCount;
    private final double spread;
    private final int meq;

    private final String[] instrumentArray;
    private final MarketType[] marketTypes;
    private final Side[] sideArray;
    private final int[] quantityArray;


    public CreateStrategyEvent( long eventId, FluentInputEventType type,
                                String strategyOwner, String strategyName, Side strategySide, int strategyLegCount, double spread,
                                String[] instrumentArray, MarketType[] marketTypes, int[] quantityArray, Side[] sideArray, int meq ){

        super( eventId, type, strategyOwner );

        this.strategyName       = strategyName;
        this.strategySide       = strategySide;
        this.strategyLegCount   = strategyLegCount;

        this.instrumentArray    = instrumentArray;
        this.quantityArray      = quantityArray;
        this.sideArray          = sideArray;
        this.marketTypes        = marketTypes;
        this.spread             = spread;
        this.meq                = meq;

    }

    public final long getStrategyId(){
        return getEventId();
    }

    public final String getStrategyName(){
        return strategyName;
    }

    public final Side getStrategySide(){
        return strategySide;
    }

    public final int getStrategyLegCount(){
        return strategyLegCount;
    }


    public final MarketType[] getMarketTypes( ){
        return marketTypes;
    }


    public final String[] getInstrumentArray(){
        return instrumentArray;
    }

    public final int[] getQuantityArray(){
        return quantityArray;
    }

    public final Side[] getSideArray(){
        return sideArray;
    }

    public final double getSpread(){
        return spread;
    }

    public final int getMeq(){
        return meq;
    }

    
    @Override
    protected final String toJSON( final JsonObject object ){

        object.add( STRATEGY_ID.field(),        getStrategyId() );
        object.add( STRATEGY_NAME.field(),      getStrategyName() );
        object.add( STRATEGY_OWNER.field(),     getStrategyOwner() );
        object.add( STRATEGY_SIDE.field(),      getStrategySide().name() );
        object.add( STRATEGY_LEG_COUNT.field(), getStrategyLegCount() );
        object.add( INSTRUMENTS.field(),        convertFromStringArray( getInstrumentArray() ) );
        object.add( MARKETS.field(),            convertFromMarketArray( getMarketTypes()) );
        object.add( QUANTITIES.field(),         convertFromIntArray( getQuantityArray() ) );
        object.add( SIDES.field(),              convertFromSideArray( getSideArray() ) );
        object.add( SPREAD.field(),             getSpread() );
        object.add( MEQ.field(),                getMeq() );

        return object.toString();
    }
    

    
    public final static CreateStrategyEvent convert( final String jsonString, final JsonObject object ){

        return new CreateStrategyEvent(
                valueAsLong(STRATEGY_ID, object),
                valueAsInputType( object ),
                valueAsString( STRATEGY_OWNER, object ),
                valueAsString( STRATEGY_NAME, object ),
                Side.valueOf( valueAsString( STRATEGY_SIDE, object ) ),
                valueAsInt( STRATEGY_LEG_COUNT, object ),
                valueAsDouble( SPREAD, object ),
                convertToStringArray( valueAsArray( INSTRUMENTS, object ) ),
                convertToMarketArray( valueAsArray( MARKETS, object ) ),
                convertToIntArray( valueAsArray( QUANTITIES, object ) ),
                convertToSideArray( valueAsArray( SIDES, object ) ),
                valueAsInt( MEQ, object ));

    }

}
