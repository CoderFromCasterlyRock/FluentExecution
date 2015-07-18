package com.fluent.etrading.events.in;

import com.eclipsesource.json.JsonObject;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.in.InEvent;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.in.InType.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class ReferenceDataEvent extends InEvent{

	private final String eventId;
	private final String message;
	
	private final static String PREFIX = "REFERENCE_";
	private final static long serialVersionUID = 1l;
	
	public ReferenceDataEvent( String message ){

        super( REFERENCE_DATA );

        this.message	= message;
        this.eventId	= PREFIX + UNDERSCORE + getSequenceId();
    }


	@Override
	public final String getEventId( ){
        return eventId;
    }
	
	
    public final String getData( ){
        return message;
    }

    
    @Override
    protected final void toJSON( final JsonObject object ){
        object.add( REF_DATA_TAG.field(), getData() );
        
    }


}

