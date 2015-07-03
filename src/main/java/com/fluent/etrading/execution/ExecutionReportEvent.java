package com.fluent.etrading.execution;

import com.eclipsesource.json.*;
import com.fluent.etrading.order.*;
import com.fluent.framework.market.*;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.in.InboundType.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.TimeUtil.*;


public final class ExecutionReportEvent extends InboundEvent{

    private final boolean isRejected;

    private final String eventId;
	private final String strategyId;
    private final String externalId;
    private final String orderId;
    
    private final String rejectionReason;
    private final OrderFillStatus fillStatus;

    private final Side side;
    private final Exchange marketType;
    private final OrderType orderType;
    private final String symbol;
    private final double executionPrice;
    private final double executionQuantity;
    private final long executionTime;
    
    private final static long serialVersionUID = 1l;
    
    public ExecutionReportEvent( String strategyId, String orderId, OrderFillStatus oStatus, String reason,
                                 OrderType oType, Side side, Exchange mType, String symbol ){

        this( true, strategyId, orderId, EMPTY, currentMillis(), oStatus, reason, oType, side, mType, symbol, ZERO_DOUBLE, ZERO_DOUBLE );

    }


    public ExecutionReportEvent( boolean isRejected, String strategyId, String orderId, String externalId,
                                 long executionTime, OrderFillStatus fillStatus, String rejectionReason,
                                 OrderType orderType, Side side, Exchange marketType,
                                 String symbol, double executionPrice, double executionQuantity ){

        super( EXECUTION_REPORT );

        this.eventId			= orderId + UNDERSCORE + getSequenceId();
        this.isRejected			= isRejected;
        this.strategyId         = strategyId;
        this.externalId         = externalId;
        this.orderId         	= orderId;
        this.fillStatus         = fillStatus;
        this.rejectionReason    = rejectionReason;
        this.orderType          = orderType;
        this.side               = side;
        this.marketType         = marketType;
        this.symbol       		= symbol;
        this.executionTime		= executionTime;
        this.executionPrice     = executionPrice;
        this.executionQuantity  = executionQuantity;

    }

    
    @Override
    public final String getEventId( ){
        return eventId;
    }

    public final String getOrderId( ){
        return orderId;
    }

    public final String getStrategyId( ){
        return strategyId;
    }

    public final String getOrderExternalId( ){
        return externalId;
    }

    public final OrderFillStatus getFillStatus( ){
        return fillStatus;
    }

    public final boolean isRejected( ){
        return isRejected;
    }

    public final String getRejectionReason( ){
        return rejectionReason;
    }

    public final OrderType getOrderType( ){
        return orderType;
    }

    public final Side getSide( ){
        return side;
    }

    public final Exchange getMarketType( ){
        return marketType;
    }
    
    public final long getExecutionTime( ){
        return executionTime;
    }

    public final String getSymbol( ){
        return symbol;
    }

    public final double getExecutionPrice( ){
        return executionPrice;
    }

    public final double getExecutionQuantity( ){
        return executionQuantity;
    }


    @Override
    protected final void toJSON( final JsonObject object ){

        object.add( STRATEGY_ID.field(),        getStrategyId() );
        object.add( ORDER_ID.field(),           getOrderId() );
        object.add( ORDER_EXTERNAL_ID.field(),  getOrderExternalId() );
        object.add( FILL_STATUS.field(),        getFillStatus().name() );
        object.add( SYMBOL.field(),    			getSymbol() );
        object.add( EXECUTION_PRICE.field(),    getExecutionPrice() );
        object.add( EXECUTION_QUANTITY.field(), getExecutionQuantity() );
        object.add( SIDE.field(),               getSide().name() );
        object.add( ORDER_TYPE.field(),         getOrderType().name() );
        object.add( EXCHANGE.field(),           getMarketType().name() );
        object.add( IS_REJECTED.field(),        isRejected() );
        object.add( REJECTED_REASON.field(),    getRejectionReason() );
        
    }

    
}
