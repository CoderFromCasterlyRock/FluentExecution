package com.fluent.etrading.aggregator.core;

import com.fluent.etrading.framework.market.core.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;
import static com.fluent.etrading.framework.market.core.MarketType.*;
import static com.fluent.etrading.aggregator.core.SwapRowOrdinal.Ordinal.*;


public final class SwapRowOrdinal implements AggregateRowOrdinal{


    public enum Ordinal{

        DWEB_BID_ROW        (  ZERO         ),
        ISWAP_BID_ROW       (  ONE          ),
        TRADITION_BID_ROW   (  TWO          ),

        DWEB_ASK_ROW        (  THREE        ),
        ISWAP_ASK_ROW       (  FOUR         ),
        TRADITION_ASK_ROW   (  FIVE         ),

        INVALID_SWAP_ROW    ( NEGATIVE_ONE  );

        private final int ordinal;

        private Ordinal( int ordinal ){
            this.ordinal = ordinal;
        }

    }

    private static final MarketType AGG_TYPE = SWAP_SMART_ROUTER;


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
            case THREE:
                return DWEB;

            case ONE:
            case FOUR:
                return ISWAP;

            case TWO:
            case FIVE:
                return TRADITION;

            default:
                return UNSUPPORTED;
        }

    }


    @Override
    public final int getRowIndex( final boolean isBid, final MarketType type ){

        switch( type ){

            case DWEB:
                return ( isBid ) ? DWEB_BID_ROW.ordinal     : DWEB_ASK_ROW.ordinal;

            case ISWAP:
                return ( isBid ) ? ISWAP_BID_ROW.ordinal    : ISWAP_ASK_ROW.ordinal;

            case TRADITION:
                return ( isBid ) ? TRADITION_BID_ROW.ordinal: TRADITION_ASK_ROW.ordinal;

            default:
                return NEGATIVE_ONE;
        }

    }


}
