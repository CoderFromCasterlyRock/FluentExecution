package com.fluent.etrading.strategy.core;

import com.fluent.framework.core.*;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.util.FluentUtil.*;


public abstract class AbstractAlgo implements FluentService{

    private final String strategyId;
    private final String strategyName;
    private final String strategyOwner;
        

    public AbstractAlgo( String strategyId, String strategyName, String strategyOwner ){
        
    	this.strategyId         = strategyId;
        this.strategyName       = strategyName;
        this.strategyOwner      = strategyOwner;
        
    }


    public abstract void update( InboundEvent event );

    public final String getStrategyId( ){
        return strategyId;
    }


    public final String getStrategyOwner( ){
        return strategyOwner;
    }


    public final String getStrategyName( ){
        return strategyName;
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
