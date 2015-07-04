package com.fluent.etrading.execution;

import com.fluent.etrading.events.out.ExecutionReportEvent;



public interface ExecutionEventProvider{
	
	public boolean addExecutionEvent( ExecutionReportEvent event );

}
