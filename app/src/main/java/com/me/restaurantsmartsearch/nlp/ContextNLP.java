package com.me.restaurantsmartsearch.nlp;

import java.util.HashMap;

/**
 * Created by SinhBlack on 2/19/2017.
 */

public class ContextNLP {
    public static String TYPE_NEAR        = "gần_đây";
    public static String TYPE_CHEAP       = "giá_rẻ";
    public static String TYPE_LOCATION    = "theo_nơi";

    public static String ATTR_LOCATION = "nơi";

    private String type;
    private HashMap attrsMap = new HashMap();

    public ContextNLP(String type){
        this.type = type;
    }

    public String getLocation(){
        return getAttribute(ATTR_LOCATION);
    }

    //using for later
    //val change to object Attribute later
    public void putAttribute(String name, String val){
        attrsMap.put(name, val);
    }

    //return object Attribute later
    private String getAttribute(String name){
        return (String)attrsMap.get(name);
    }

    public String getType(){
        return type;
    }
}
