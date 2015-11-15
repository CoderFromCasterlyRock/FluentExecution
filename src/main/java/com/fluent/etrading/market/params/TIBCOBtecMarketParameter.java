package com.fluent.etrading.market.params;

import java.util.*;

import com.fluent.framework.market.*;
import com.fluent.framework.market.core.Exchange;
import com.fluent.etrading.order.OrderEvent;



public class TIBCOBtecMarketParameter extends TIBCOMarketParameter{


    public TIBCOBtecMarketParameter( ){
        super( Exchange.BTEC );
    }


    @Override
    public Map<String, String> newOrderParams( OrderEvent event ){
        return null;
    }


    @Override
    public Map<String, String> amendOrderParams( OrderEvent event ){
        return null;
    }


    @Override
    public Map<String, String> cancelOrderParams( OrderEvent event ){
        return null;
    }

}
