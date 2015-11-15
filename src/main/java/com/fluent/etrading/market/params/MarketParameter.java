package com.fluent.etrading.market.params;

import com.fluent.etrading.order.OrderEvent;
import com.fluent.etrading.order.OrderType;
import com.fluent.framework.market.*;
import com.fluent.framework.market.core.Exchange;


public abstract class MarketParameter<R, O extends OrderEvent>{

    private final Exchange type;

    public MarketParameter( Exchange type ){
        this.type   = type;
    }

    protected abstract R newOrderParams   ( O event );
    protected abstract R amendOrderParams ( O event );
    protected abstract R cancelOrderParams( O event );


    public final Exchange getMarketType( ){
        return type;
    }


    public final R getParams( O event ){

        OrderType orderType = event.getOrderType();

        switch( orderType ){

            case NEW:
                return newOrderParams( event );

            case AMEND:
                return amendOrderParams( event );

            case CANCEL:
                return cancelOrderParams( event );

            default:
                throw new UnsupportedOperationException( );
        }

    }

}
