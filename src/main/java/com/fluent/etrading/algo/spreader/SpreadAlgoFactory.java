package com.fluent.etrading.algo.spreader;

import com.fluent.etrading.algo.core.*;
import com.fluent.etrading.requests.*;
import com.fluent.etrading.framework.dispatcher.out.*;


public final class SpreadAlgoFactory extends AbstractAlgoFactory{

    
    public SpreadAlgoFactory( OutputEventDispatcher dispatcher ){
        super( dispatcher );
    }


    //TraderEvent Should be the input to create
    public final SpreadAlgo create( long strategyId, CreateStrategyEvent tEvent ){
        return new SpreadAlgo( strategyId, getDispatcher(), tEvent );

    }


}
