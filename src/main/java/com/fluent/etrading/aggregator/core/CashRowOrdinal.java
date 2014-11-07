package com.fluent.etrading.aggregator.core;

import com.fluent.etrading.framework.market.core.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;
import static com.fluent.etrading.framework.market.core.MarketType.*;
import static com.fluent.etrading.aggregator.core.CashRowOrdinal.Ordinal.*;


public final class CashRowOrdinal implements AggregateRowOrdinal{


    public enum Ordinal{

        BTEC_BID_ROW        (   ZERO         ),
        ESPEED_BID_ROW      (   ONE          ),

        BTEC_ASK_ROW        (   THREE        ),
        ESPEED_ASK_ROW      (   FOUR         ),

        INVALID_CASH_ROW    (   NEGATIVE_ONE );

        private final int ordinal;

        private Ordinal( int ordinal ){
            this.ordinal = ordinal;
        }

    }

    private static final MarketType AGG_TYPE = CASH_SMART_ROUTER;


    @Override
    public final int getMarketCount(){
        return AGG_TYPE.getUnderlying( ).length;
    }

    @Override
    public final int getValidRowCount(){
        return getMarketCount() * TWO;
    }


    @Override
    public final MarketType[] getUnderlyingMarkets(){
        return AGG_TYPE.getUnderlying( );
    }


    @Override
    public final MarketType getAggregateMarketType(){
        return AGG_TYPE;
    }


    @Override
    public final MarketType getMarketFrom( final int rowIndex ){

        switch( rowIndex ){

            case ZERO:
            case TWO:
                return BTEC;

            case ONE:
            case THREE:
                return ESPEED;

            default:
                return UNSUPPORTED;
        }

    }


    @Override
    public final int getRowIndex( final boolean isBid, final MarketType type ){

        switch( type ){

            case BTEC:
                return ( isBid ) ? BTEC_BID_ROW.ordinal     : BTEC_ASK_ROW.ordinal;

            case ESPEED:
                return ( isBid ) ? ESPEED_BID_ROW.ordinal   : ESPEED_ASK_ROW.ordinal;

            default:
                return NEGATIVE_ONE;
        }

    }


}
