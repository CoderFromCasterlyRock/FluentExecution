package com.fluent.etrading.algo.core;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.framework.dispatcher.out.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public abstract class AbstractAlgo implements FluentService{

    private final long strategyId;
    private final String strategyName;
    private final String strategyOwner;
    private final String fullStrategyName;
    private final OutputEventDispatcher dispatcher;

    private final static String STRATEGY_ID     = "StrategyId:";
    private final static String STRATEGY_NAME   = "StrategyName:";


    public AbstractAlgo( long strategyId, String strategyName, String strategyOwner, OutputEventDispatcher dispatcher ){
        this.strategyId         = strategyId;
        this.strategyName       = strategyName;
        this.strategyOwner      = strategyOwner;
        
        this.dispatcher         = dispatcher;
        this.fullStrategyName   = new StringBuilder( STRATEGY_ID ).append( strategyId ).append( SPACE ).append( STRATEGY_NAME ).append( strategyName ).toString();

    }


    public abstract void update( FluentInputEvent event );

    public final long getStrategyId( ){
        return strategyId;
    }


    public final String getStrategyOwner( ){
        return strategyOwner;
    }


    public final String getStrategyName( ){
        return strategyName;
    }


    public final String getFullStrategyName( ){
        return fullStrategyName;
    }


    protected final long nextOutputEventId( ){
        return FluentOutputEventId.nextId();
    }


    protected final OutputEventDispatcher getOutDispatcher( ){
        return dispatcher;
    }


    protected final boolean marketUpdateRequired( String instrument, String[] instruments ){
        for( String strategyInstrument : instruments ){
            if( strategyInstrument.equalsIgnoreCase( instrument )){
                return true;
            }
        }

        return false;
    }


}
