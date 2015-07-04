package com.fluent.etrading.strategy.internal;

import static com.fluent.etrading.util.ExecUtil.*;
import static com.fluent.framework.util.FluentUtil.*;


public final class LegStateVariables{
	
	private double pendingAddQty;
	private double ackedQty;
	private double filledQty;
	private double pendingCancelQty;
	private double cancelledQty;
	private double rejectedQty;
	
	private LegOrderStatus state;

	
	public LegStateVariables( LegStateVariables original ){
		this( original.pendingAddQty, original.ackedQty, original.filledQty,
			  original.pendingCancelQty, original.cancelledQty, original.rejectedQty );
	}
	

	public LegStateVariables( double paQty, double ackQty, double fillQty, 
							  double pCxlQty, double cxlQty, double rejQty ){
		
		this.pendingAddQty 		= paQty;
		this.ackedQty 			= ackQty;
		this.filledQty 			= fillQty;
		this.pendingCancelQty 	= pCxlQty;
		this.cancelledQty 		= cxlQty;
		this.rejectedQty 		= rejQty;
		this.state 				= LegOrderStatus.NEW;
	
	}
	
	
	protected final void addChanges( LegStateVariables changes ){
		this.pendingAddQty 		+= changes.pendingAddQty;
		this.ackedQty 			+= changes.ackedQty;
		this.filledQty 			+= changes.filledQty;
		this.pendingCancelQty 	+= changes.pendingCancelQty;
		this.cancelledQty 		+= changes.cancelledQty;
		this.rejectedQty 		+= changes.rejectedQty;
		
	}
	
	
	protected final double getOpenQuantity( ){
		double debit 	= pendingAddQty + ackedQty;
		double crebit 	= filledQty + pendingCancelQty + cancelledQty + rejectedQty;
		double openQty	= debit - crebit;
		
		return openQty;
	}
	
	
	protected final double getMaxOpenQuantity( ){
		double debit 	= pendingAddQty + ackedQty;
		double crebit 	= filledQty + cancelledQty + rejectedQty;
		double openQty	= debit - crebit;
		
		return openQty;
	}
	
	
	protected final boolean anythingChanged( ){
		
		if( !doubleEquals(pendingAddQty, ZERO_DOUBLE) ) return true;
		if( !doubleEquals(ackedQty, ZERO_DOUBLE) ) return true;
		if( !doubleEquals(pendingCancelQty, ZERO_DOUBLE) ) return true;
		if( !doubleEquals(filledQty, ZERO_DOUBLE) ) return true;
		if( !doubleEquals(cancelledQty, ZERO_DOUBLE) ) return true;
		if( !doubleEquals(rejectedQty, ZERO_DOUBLE) ) return true;
		
		return false;
	
	}


	@Override
	public final String toString( ){
		
		StringBuilder builder = new StringBuilder( THIRTY_TWO );
		
		builder.append( state );
		builder.append(", PA = ").append( pendingAddQty );
		builder.append(", ACK = ").append( ackedQty );
		builder.append(", FILL = ").append( filledQty );
		builder.append(", PCXL = ").append( pendingCancelQty );
		builder.append(", CXL = ").append( cancelledQty );
		builder.append(", RJ = ").append( rejectedQty );
		
		return builder.toString();
	}
	

}
