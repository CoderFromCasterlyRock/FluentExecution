package com.fluent.etrading.algo.core;

import com.fluent.etrading.framework.core.*;
import com.fluent.etrading.framework.events.core.*;
import com.fluent.etrading.algo.spreader.*;
import com.fluent.etrading.framework.dispatcher.out.*;


public abstract class AbstractAlgoManager implements FluentInputEventListener, FluentService{

    private final SpreadAlgoFactory factory;
    private final OutputEventDispatcher dispatcher;


    public AbstractAlgoManager( SpreadAlgoFactory factory ){
        this.factory        = factory;
        this.dispatcher     = factory.getDispatcher();

    }


    protected final SpreadAlgoFactory getFactory( ){
        return factory;
    }


    protected final OutputEventDispatcher getOutDispatcher( ){
        return dispatcher;
    }

    
    protected final long nextOutputEventId( ){
    	return FluentOutputEventId.nextId();
    }
    
    
    @Override
    public final boolean isSupported( final FluentInputEventType type ){
        return ( FluentEventCategory.allInput().contains(type.getCategory()) );
    }


}

