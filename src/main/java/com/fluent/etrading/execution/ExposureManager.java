package com.fluent.etrading.execution;

import java.util.*;

import com.fluent.etrading.framework.order.*;
import com.fluent.etrading.framework.events.out.order.*;
import com.fluent.etrading.aggregator.processor.*;


public abstract class ExposureManager{

	private final long strategyId;
    private final Side side;
    private final String traderId;
    private final String instrumentId;

    private final double traderPrice;
    private final Aggregator aggregator;
    private final AggregateAllocationPolicy policy;

    private final Map<Long, OrderEvent> orderMap;


    protected ExposureManager( long strategyId, Side side, String traderId, String instrumentId, double traderPrice, Aggregator aggregator, AggregateAllocationPolicy policy, Map<Long, OrderEvent> orderMap ){
        this.strategyId 	= strategyId;
        this.side 			= side;
        this.traderId 		= traderId;
        this.instrumentId 	= instrumentId;
        this.traderPrice 	= traderPrice;
        this.aggregator 	= aggregator;
        this.policy 		= policy;
        this.orderMap 		= orderMap;
    }
    
    
    public final long getStrategyId( ){
		return strategyId;
	}


    public final  Side getSide( ){
		return side;
	}


    public final  String getTraderId( ){
		return traderId;
	}


    public final  String getInstrumentId( ){
		return instrumentId;
	}


    public final  double getTraderPrice( ){
		return traderPrice;
	}


    public final  Aggregator getAggregator( ){
		return aggregator;
	}


    public final  AggregateAllocationPolicy getPolicy( ){
		return policy;
	}


    public final  Map<Long, OrderEvent> getOrderMap( ){
		return orderMap;
	}

	
}
