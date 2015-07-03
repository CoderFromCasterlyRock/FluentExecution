package com.fluent.etrading.strategy.core;

import com.fluent.framework.collection.*;
import com.fluent.framework.core.*;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.in.InboundListener;
import com.fluent.framework.events.in.InboundType;

import static com.fluent.framework.util.FluentUtil.*;


public abstract class AbstractAlgoManager implements InboundListener, FluentService{

    private final static FluentAtomicLong ID	= new FluentAtomicLong( ONE );

    public AbstractAlgoManager(){
    }

    
    protected final long nextOutputEventId( ){
    	return ID.getAndIncrement();
    }
    
    
    @Override
    public final boolean isSupported( final InboundType type ){
        return ( FluentEventCategory.INPUT.contains(type.getCategory()) );
    }


}

