package com.fluent.etrading.util;

import java.math.*;

import static com.fluent.framework.util.FluentUtil.*;


public final class ExecUtil{
	
	
	public final static double bgRound( int scale, double value ){
		BigDecimal bd = new BigDecimal(value).setScale(scale, RoundingMode.HALF_EVEN);
		return bd.doubleValue();
	}

	
    public final static int getRoundedTickDifference( double pivotPrice, double newPrice, double minPriceTick ){

        double priceDifference      = ( newPrice - pivotPrice );
        double tickDifference       = ( priceDifference / minPriceTick );
        double tickDifferenceAdj    = ( tickDifference >= ZERO ) ? (tickDifference + PRICE_TOLERANCE) : (tickDifference - PRICE_TOLERANCE);
        int roundedTickDifference   = (int) tickDifferenceAdj;

        return roundedTickDifference;

    }
    
}
