package com.fluent.etrading.market.params;

import java.util.*;

import com.fluent.framework.market.*;
import com.fluent.etrading.order.OrderEvent;


public abstract class TIBCOMarketParameter extends MarketParameter<Map<String, String>, OrderEvent>{

    public TIBCOMarketParameter( Exchange type ){
        super( type );
    }

}
