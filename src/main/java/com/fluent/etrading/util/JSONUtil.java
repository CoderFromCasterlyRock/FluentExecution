package com.fluent.etrading.util;

import com.eclipsesource.json.*;
import com.fluent.etrading.order.Side;
import com.fluent.framework.events.core.*;
import com.fluent.framework.events.in.InboundType;
import com.fluent.framework.events.out.OutboundType;
import com.fluent.framework.market.*;

import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.events.core.FluentJsonTags.*;

public final class JSONUtil{

    protected JSONUtil(){}



    public final static InboundType valueAsInputType( JsonObject object ){
        return InboundType.valueOf( object.get(EVENT_TYPE.field()).asString() );
    }

    public final static OutboundType valueAsOutputType( JsonObject object ){
        return OutboundType.valueOf( object.get(EVENT_TYPE.field()).asString() );
    }


    public final static int valueAsInt( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asInt( );
    }


    public final static double valueAsDouble( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asDouble();
    }


    public final static long valueAsLong( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asLong();
    }


    public final static boolean valueAsBoolean( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asBoolean();
    }


    public final static String valueAsString( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asString( );
    }


    public final static JsonArray valueAsArray( FluentJsonTags field, JsonObject object ){
        return object.get( field.field() ).asArray();
    }


    public final static Side[] convertToSideArray( JsonArray array ){
        int size            = array.size();
        Side[] sideArray    = new Side[size];

        for( int i=ZERO; i< size; i++ ){
            JsonValue value = array.get( i );
            sideArray[i]    = Side.valueOf(value.asString());
        }

        return sideArray;
    }



    public final static Exchange[] convertToMarketArray( JsonArray array ){
        int size            = array.size();
        Exchange[] market = new Exchange[size];

        for( int i=ZERO; i< size; i++ ){
            JsonValue value = array.get( i );
            market[i]       = Exchange.valueOf(value.asString());
        }

        return market;
    }


    public final static String[] convertToStringArray( JsonArray array ){
        int size                = array.size();
        String[] stringArray    = new String[size];

        for( int i=ZERO; i< size; i++ ){
            JsonValue value = array.get( i );
            stringArray[i]    = value.asString();
        }

        return stringArray;
    }


    public final static int[] convertToIntArray( JsonArray array ){
        int size                = array.size();
        int[] intArray          = new int[size];

        for( int i=ZERO; i< size; i++ ){
            JsonValue value = array.get( i );
            intArray[i]    = value.asInt();
        }

        return intArray;
    }



    public final static JsonArray convertFromMarketArray( Exchange[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( Exchange m : array ){
            jArray.add( m.name() );
        }
        return jArray;
    }


    public final static JsonArray convertFromSideArray( Side[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( Side s : array ){
            jArray.add( s.name() );
        }
        return jArray;
    }


    public final static JsonArray convertFromStringArray( String[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( String s : array ){
            jArray.add( s );
        }
        return jArray;
    }


    public final static JsonArray convertFromDoubleArray( double[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( double d : array ){
            jArray.add( d );
        }
        return jArray;
    }


    public final static JsonArray convertFromIntArray( int[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( int i : array ){
            jArray.add( i );
        }
        return jArray;
    }

    
    public final static JsonArray convertFromBoolArray( boolean[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( boolean i : array ){
            jArray.add( i );
        }
        return jArray;
    }
    
    
    public final static JsonArray convertFromInstrumentTypesArray( InstrumentType[] array ){
        JsonArray jArray   = new JsonArray(  );
        for( InstrumentType i : array ){
            jArray.add( i.name() );
        }
        return jArray;
    }
    
    
    
}
