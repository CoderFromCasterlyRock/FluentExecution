package com.fluent.etrading.aggregator.data;


public interface AggregateData{

    public String getCommonId();

    public int getPivotIndex();
    public double getPivotPrice();
    public int getRowCount();
    public int getColumnCount();

    public double getMinPriceTick();
    public int[][] getUnderlyingArray();
    public void shift( double newPivotPrice ) throws Exception;

    public void store( int row,
                       int col0, double price0, int size0,
                       int col1, double price1, int size1,
                       int col2, double price2, int size2,
                       int col3, double price3, int size3,
                       int col4, double price4, int size4 );


}

