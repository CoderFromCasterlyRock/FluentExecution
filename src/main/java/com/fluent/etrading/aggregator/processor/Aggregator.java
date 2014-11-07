package com.fluent.etrading.aggregator.processor;

import org.cliffc.high_scale_lib.*;

import com.fluent.etrading.framework.order.*;
import com.fluent.etrading.framework.market.core.*;
import com.fluent.etrading.aggregator.core.*;
import com.fluent.etrading.aggregator.data.*;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class Aggregator{

    private final int capacity;
    private final int columnCount;
    private final int pivotIndex;
    private final int[] bidRowIndicies;
    private final int[] askRowIndicies;
    private final double[][] empty;
    private final MarketType[] markets;

    private final AggregateRowOrdinal ordinal;
    private final NonBlockingHashMap<String, AggregateData> dataMap;


    public Aggregator( int capacity, int columnCount, AggregateRowOrdinal ordinal ){
        this.capacity       = capacity;
        this.columnCount    = (columnCount % TWO == ZERO) ? (columnCount + ONE) : columnCount;
        this.pivotIndex     = ((columnCount - ONE)/TWO);
        this.ordinal        = ordinal;

        this.markets        = ordinal.getUnderlyingMarkets();

        this.bidRowIndicies = new int[ markets.length ];
        this.askRowIndicies = new int[ markets.length ];
        this.empty          = new double[ordinal.getValidRowCount()][columnCount];
        this.dataMap        = new NonBlockingHashMap<String, AggregateData>(  );

        for( int i = ZERO; i<markets.length; i++ ){
            bidRowIndicies[i] = ordinal.getRowIndex( true,  markets[i] );
            askRowIndicies[i] = ordinal.getRowIndex( false, markets[i] );
        }

    }


    public final int getCapacity(){
        return capacity;
    }


    public final int getColumnCount(){
        return columnCount;
    }


    public final int getPivotIndex(){
        return pivotIndex;
    }


    public final AggregateRowOrdinal getOrdinal(){
        return ordinal;
    }


    public final double[][] getEmpty( ){
        return empty;
    }


    public final AggregateData getData( String instrumentId ){
        return dataMap.get( instrumentId );
    }


    public final void setData( String instrumentId, AggregateData data ){
        dataMap.put( instrumentId, data );
    }


    public final double[][] getBestPriceAndSize( AggregateData data, Side side ){
        if( data == null ) return empty;
        return ( Side.BUY == side ) ? getBestAskPriceSize(data) : getBestBidPriceSize( data );
    }


    public final double[][] getBestPriceAndSize( ImmutableAggregateData data, Side side ){
        if( data == null ) return empty;
        return ( Side.BUY == side ) ? getBestAskPriceSize(data) : getBestBidPriceSize( data );
    }


    public final double[][] getBestBidPriceSize( AggregateData data ){

        int pivotIndex      = data.getPivotIndex();
        int columnCount     = data.getColumnCount();
        double pivotPrice   = data.getPivotPrice();
        double priceTick    = data.getMinPriceTick();
        int[][] dataArray   = data.getUnderlyingArray();

        double[][] result   = new double[ONE+bidRowIndicies.length][FIVE];

        int bidCount        = ZERO;
        int columnIndex     = ( columnCount - ONE );

        while( columnIndex >= ZERO && bidCount < FIVE ){
            double totalSize = ZERO;
            for( int rowIndex : bidRowIndicies ){
                totalSize += dataArray[rowIndex][columnIndex];
            }

            if( totalSize > ZERO ){
                int offset      = columnIndex - pivotIndex;
                double price    = pivotPrice + (offset * priceTick);

                result[ZERO][bidCount]  = price;

                for( int rowIndex : bidRowIndicies ){
                    int askSize     = dataArray[rowIndex][columnIndex];
                    int sizeIndex   = ONE + rowIndex;

                    result[sizeIndex][bidCount] = askSize;
                }

                bidCount ++;
            }

            columnIndex --;
        }

        return result;

    }



    public final double[][] getBestAskPriceSize( AggregateData data ){

        int pivotIndex      = data.getPivotIndex();
        int columnCount     = data.getColumnCount();
        double pivotPrice   = data.getPivotPrice();
        double priceTick    = data.getMinPriceTick();
        int[][] dataArray   = data.getUnderlyingArray();

        int askRowCount     = askRowIndicies.length;
        double[][] result   = new double[ONE+askRowCount][FIVE];

        int askCount        = ZERO;
        int columnIndex     = ( columnCount - ONE );

        while( columnIndex >= columnCount && askCount < FIVE ){

            double totalSize    = ZERO;
            for( int rowIndex : askRowIndicies ){
                totalSize += dataArray[rowIndex][columnIndex];
            }

            if( totalSize > ZERO ){
                int offset      = columnIndex - pivotIndex;
                double price    = pivotPrice + (offset * priceTick);

                result[ZERO][askCount]  = price;

                for( int rowIndex : askRowIndicies ){
                    int askSize     = dataArray[rowIndex][columnIndex];
                    int sizeIndex   = ONE + Math.abs(askRowCount-rowIndex);

                    result[sizeIndex][askCount] = askSize;
                }

                askCount ++;
            }

            columnIndex ++;
        }

        return result;

    }


}
