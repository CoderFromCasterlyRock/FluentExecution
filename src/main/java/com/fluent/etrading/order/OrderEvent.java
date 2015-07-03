package com.fluent.etrading.order;

import com.eclipsesource.json.JsonObject;
import com.fluent.framework.events.out.OutboundEvent;
import com.fluent.framework.market.*;

import static com.fluent.framework.events.core.FluentJsonTags.*;
import static com.fluent.framework.events.out.OutboundType.*;


public final class OrderEvent extends OutboundEvent{

	private final String eventId;
    private final String strategyId;
    private final String orderId;
    private final String symbol;
    private final OrderType orderType;
    private final Exchange exchange;

    private final Side side;
    private final double price;
    private final int sendQuantity;
    private final int showQuantity;
    private final String traderId;
    private final String traderName;
    private final String portfolio;

    private final static String PREFIX = "ORDER_";
    private final static long serialVersionUID = 1l;
    


    public OrderEvent( String strategyId, String orderId, Exchange exchange, OrderType orderType,
                       Side side, String symbol, double price, int sendQuantity, int showQuantity, String traderId,
                       String traderName, String portfolio ){

        super( ORDER_TO_MARKET );

        this.eventId     	= PREFIX + getSequenceId();
        this.strategyId     = strategyId;
        this.orderId     	= orderId;
        this.exchange	    = exchange;
        this.orderType      = orderType;
        this.side           = side;
        this.symbol   		= symbol;
        this.price          = price;
        this.sendQuantity   = sendQuantity;
        this.showQuantity   = showQuantity;
        this.traderId       = traderId;
        this.traderName     = traderName;
        this.portfolio      = portfolio;

    }


    @Override
    public final String getEventId(){
        return eventId;
    }

    
    public final String getStrategyId( ){
        return strategyId;
    }

    
    public final String getOrderId( ){
        return orderId;
    }

    
    public final String getSymbol( ){
        return symbol;
    }
    

    public final OrderType getOrderType( ){
        return orderType;
    }
    

    public final Exchange getExchange( ){
        return exchange;
    }
    

    public final Side getSide(){
        return side;
    }
    

    public final double getPrice( ){
        return price;
    }

    
    public final int getSendQuantity( ){
        return sendQuantity;
    }

    public final String getTraderId( ){
        return traderId;
    }
    

    public final int getShowQuantity(){
        return showQuantity;
    }


    public final String getTraderName(){
        return traderName;
    }


    public final String getPortfolio(){
        return portfolio;
    }

    
    @Override
    protected final void toJSON( final JsonObject object ){

        object.add( STRATEGY_ID.field(),        getStrategyId() );
        object.add( ORDER_ID.field(),           getOrderId() );
        object.add( EXCHANGE.field(),           getExchange().name() );
        object.add( ORDER_TYPE.field(),         getOrderType().name() );
        object.add( SIDE.field(),               getSide().name() );
        object.add( SYMBOL.field(),    			getSymbol() );
        object.add( PRICE.field(),              getPrice() );
        object.add( SEND_QUANTITY.field(),      getSendQuantity() );
        object.add( SHOW_QUANTITY.field(),      getShowQuantity() );
        object.add( TRADER_ID.field(),          getTraderId() );
        object.add( TRADER_NAME.field(),        getTraderName() );
        object.add( PORTFOLIO.field(),          getPortfolio());

    }

    
}
