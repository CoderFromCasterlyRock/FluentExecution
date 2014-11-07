package com.fluent.etrading.aggregator.data;

import static com.fluent.etrading.framework.utility.ContainerUtil.*;


public final class ImmutableAggregateData implements AggregateData{

    private double pivotPrice;
    private final int pivotIndex;

    private final int rowCount;
    private final int colCount;
    private final String commonId;

    private final double minPriceTick;
    private final int[][] sizeArray;


    public ImmutableAggregateData( AggregateData data ){

        this.commonId       = data.getCommonId();
        this.pivotIndex     = data.getPivotIndex();
        this.pivotPrice     = data.getPivotPrice();

        this.rowCount       = data.getRowCount();
        this.colCount       = data.getColumnCount();
        this.minPriceTick   = data.getMinPriceTick();

        int[][] tempArray   = data.getUnderlyingArray();
        this.sizeArray      = new int[tempArray.length][colCount];

        for( int i =ZERO; i< rowCount; i++ ){
            System.arraycopy( tempArray[i], ZERO, sizeArray[i], ZERO, colCount );
        }


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

        throw new UnsupportedOperationException( );
    }


    @Override
    public final void shift( final double newPivotPrice ) throws Exception{
        throw new UnsupportedOperationException( );
    }


}