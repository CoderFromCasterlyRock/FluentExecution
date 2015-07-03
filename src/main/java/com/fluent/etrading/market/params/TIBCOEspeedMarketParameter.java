package com.fluent.etrading.market.params;

import java.util.*;

import com.fluent.framework.market.*;
import com.fluent.etrading.order.OrderEvent;


public class TIBCOEspeedMarketParameter extends TIBCOMarketParameter{


    public TIBCOEspeedMarketParameter(){
        super( Exchange.ESPEED );
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
