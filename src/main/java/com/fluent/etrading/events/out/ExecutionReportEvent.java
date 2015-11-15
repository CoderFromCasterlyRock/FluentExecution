package com.fluent.etrading.events.out;

import com.eclipsesource.json.*;
import com.fluent.etrading.order.*;
import com.fluent.framework.market.*;
import com.fluent.framework.market.core.Exchange;
import com.fluent.framework.events.in.*;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.in.InType.*;
import static com.fluent.framework.util.FluentUtil.*;
import static com.fluent.framework.util.TimeUtil.*;


public final class ExecutionReportEvent extends InEvent{

    private final boolean isRejected;

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
    public final void toEventString( StringBuilder object ){

        object.append( STRATEGY_ID.field() ).append( getStrategyId() );
        object.append( ORDER_ID.field() ).append(getOrderId() );
        object.append( ORDER_EXTERNAL_ID.field() ).append(getOrderExternalId() );
        object.append( FILL_STATUS.field() ).append(getFillStatus().name() );
        object.append( SYMBOL.field() ).append(getSymbol() );
        object.append( EXECUTION_PRICE.field() ).append( getExecutionPrice() );
        object.append( EXECUTION_QUANTITY.field() ).append(getExecutionQuantity() );
        object.append( SIDE.field() ).append(getSide().name() );
        object.append( ORDER_TYPE.field() ).append(getOrderType().name() );
        object.append( EXCHANGE.field() ).append(getMarketType().name() );
        object.append( IS_REJECTED.field() ).append(isRejected() );
        object.append( REJECTED_REASON.field() ).append(getRejectionReason() );
        
    }

    
}
