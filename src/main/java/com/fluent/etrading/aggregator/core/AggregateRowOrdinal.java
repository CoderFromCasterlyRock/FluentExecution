package com.fluent.etrading.aggregator.core;

import com.fluent.etrading.framework.market.core.MarketType;


public interface AggregateRowOrdinal{

    public int getMarketCount();
    public int getValidRowCount();
    public MarketType[] getUnderlyingMarkets();
    public MarketType getAggregateMarketType();

    public MarketType getMarketFrom( int rowIndex );
    public int getRowIndex( boolean isBid, MarketType type );

}
