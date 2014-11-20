package com.fluent.etrading.aggregator.core;

import com.fluent.etrading.framework.market.core.Marketplace;


public interface AggregateRowOrdinal{

    public int getMarketCount();
    public int getValidRowCount();
    public Marketplace[] getUnderlyingMarkets();
    public Marketplace getAggregateMarketType();

    public Marketplace getMarketFrom( int rowIndex );
    public int getRowIndex( boolean isBid, Marketplace type );

}
