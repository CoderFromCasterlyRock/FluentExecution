package com.fluent.etrading.algo.core;

import com.fluent.etrading.requests.CreateStrategyEvent;
import com.fluent.etrading.framework.dispatcher.out.OutputEventDispatcher;



public abstract class AbstractAlgoFactory{

    private final OutputEventDispatcher dispatcher;


    public AbstractAlgoFactory( OutputEventDispatcher dispatcher ){
        this.dispatcher     = dispatcher;
    }

    public abstract AbstractAlgo create( long strategyId, CreateStrategyEvent tEvent );

    protected final OutputEventDispatcher getDispatcher( ){
        return dispatcher;
    }

}
