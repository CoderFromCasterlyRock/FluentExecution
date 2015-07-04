package com.fluent.etrading.strategy.spreader;


public final class SpreadCalculator{


	public final double calculateImpliedSpread( double[] legMarketData,	double[] legScaleFactor, double[] legPxRatio, double[] legFxRate ){

		double totalSpread = 0;

		for( int i =0; i< legMarketData.length; i++ ){
			totalSpread += legMarketData[i] * legScaleFactor[i] * legPxRatio[i] * legFxRate[i];
		}

		return totalSpread;

	}
	
	
	public final double calculateLegPriceFromImpliedSpread( double totalSpread, int legIndexToCalc, double[] legMarketData, 
															double[] legScaleFactor, double[] legPxRatio, double[] legFxRate ){
		
		double spreadExceptThisLeg = 0;
		
		for( int i =0; i< legMarketData.length; i++ ){
			if( i == legIndexToCalc ) continue;
			
			spreadExceptThisLeg += legMarketData[i] * legScaleFactor[i] * legPxRatio[i] * legFxRate[i];
		}

		double spreadForThisLeg 	= (totalSpread - spreadExceptThisLeg);
		double priceForThisLeg		= spreadForThisLeg/(legScaleFactor[legIndexToCalc] * legPxRatio[legIndexToCalc] * legFxRate[legIndexToCalc]);
		
		return priceForThisLeg;
	
	}
	
}
