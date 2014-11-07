package com.fluent.etrading.aggregator.data;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class MutableAggregateData implements AggregateData{

    private double pivotPrice;
    private final int pivotIndex;

    private final int rowCount;
    private final int colCount;
    private final String commonId;

    private final double minPriceTick;
    private final int[][] sizeArray;


    public MutableAggregateData( String commonId, int pivotIndex, double pivotPrice, int rowCount, int colCount, double minPriceTick ){

        this.commonId       = commonId;
        this.pivotIndex     = pivotIndex;
        this.pivotPrice     = pivotPrice;

        this.rowCount       = rowCount;
        this.colCount       = colCount;
        this.minPriceTick   = minPriceTick;
        this.sizeArray      = new int[rowCount][colCount];

    }


    @Override
    public final String getCommonId(){
        return commonId;
    }


    @Override
    public final int getPivotIndex(){
        return pivotIndex;
    }

    @Override
    public final double getPivotPrice(){
        return pivotPrice;
    }

    @Override
    public final int getRowCount(){
        return rowCount;
    }

    @Override
    public final int getColumnCount(){
        return colCount;
    }

    @Override
    public final double getMinPriceTick(){
        return minPriceTick;
    }

    @Override
    public final int[][] getUnderlyingArray(){
        return sizeArray;
    }


    @Override
    public void store( int rowIndex,
                       int col0, double price0, int size0,
                       int col1, double price1, int size1,
                       int col2, double price2, int size2,
                       int col3, double price3, int size3,
                       int col4, double price4, int size4 ){

        //Before you store, wipe out all the data for this Market on Bid and Ask Row
        int[] rowArray = sizeArray[ rowIndex ];
        for( int colIndex=ZERO; colIndex<rowArray.length; colIndex++){
            rowArray[colIndex] = ZERO;
        }

        if( price0 != ZERO || size0 != ZERO ) store( rowIndex, col0, size0 );
        if( price1 != ZERO || size1 != ZERO ) store( rowIndex, col1, size1 );
        if( price2 != ZERO || size2 != ZERO ) store( rowIndex, col2, size2 );
        if( price3 != ZERO || size3 != ZERO ) store( rowIndex, col3, size3 );
        if( price4 != ZERO || size4 != ZERO ) store( rowIndex, col4, size4 );

    }


    private final void store( int rowIndex, int colIndex, int size ){
        if( colIndex < ZERO || colIndex >= colCount ) return;

        sizeArray[rowIndex][colIndex] = size;
    }


    @Override
    public final void shift( final double newPivotPrice ) throws Exception{

        int tickDifference  = getRoundedTickDifference( pivotPrice, newPivotPrice, minPriceTick );
        int shiftDirection  = NEGATIVE_ONE * tickDifference;
        int absoluteShift   = ( shiftDirection < ZERO ) ? (-shiftDirection) : shiftDirection;

        if( shiftDirection == ZERO ) return;

        for( int row = ZERO; row <sizeArray.length; row ++){
            int[] colArray  = sizeArray[row];

            //Price shift is greater or equal to the number of columns in the array.
            //Fill the array with default value and return;
            if( absoluteShift > colCount ){
                fillArray( colArray, ZERO, (colCount-ONE), ZERO );
                continue;
            }

        int copyLength      = ( colCount - absoluteShift );

        if( shiftDirection > ZERO ){
            System.arraycopy( colArray, ZERO, colArray, absoluteShift, copyLength );
            fillArray( colArray, ZERO, absoluteShift, ZERO );
        }else{
            System.arraycopy( colArray, absoluteShift, colArray, ZERO, copyLength );
            fillArray( colArray, copyLength, absoluteShift, ZERO );
        }

        this.pivotPrice = newPivotPrice;

        }

    }


    private final static void fillArray( int[] array, int fromIndex, int toIndex, int value ){
        for( int i=fromIndex; i<toIndex; i++ ){
            array[i]    = value;
        }
    }



}