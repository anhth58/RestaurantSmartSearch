package com.me.restaurantsmartsearch.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by SinhBlack on 2/19/2017.
 */

public class ContextNLP {
    public static String TYPE_CHEAP         = "cheap";
    public static String TYPE_NEAR_HERE     = "near_here";
    public static String TYPE_NEAR_BY       = "near_by";

    public static String ATTR_NAME          = "name";
    public static String ATTR_ADDRESS       = "address";

    public static String FIELD_TYPE             = "type";
    public static String FIELD_NAME             = "name";
    public static String FIELD_ADDRESS          = "address";
    public static String FIELD_NEAR_LOCATION    = "nearLocation";

    private HashMap<String, String> attrsMap = new HashMap<>();
    public ContextNLP(String type, String name, String address, String nearLocation){
        attrsMap.put(FIELD_NAME, name);
        attrsMap.put(FIELD_TYPE, type);
        attrsMap.put(FIELD_ADDRESS, address);
        attrsMap.put(FIELD_NEAR_LOCATION, nearLocation);
    }

    public ContextNLP(Set<String> categoryIn, HashMap<String, String> names, String query){
        //check if in cheap category
        if (categoryIn.isEmpty()){
            attrsMap.put(FIELD_NAME, query);
            attrsMap.put(FIELD_TYPE, "");
            attrsMap.put(FIELD_ADDRESS, "");
            attrsMap.put(FIELD_NEAR_LOCATION, "");
        }else{
            if(categoryIn.contains(TYPE_CHEAP)){
                attrsMap.put(FIELD_TYPE, "Sinh viên");
            }else{
                attrsMap.put(FIELD_TYPE, "");
            }

            //check if name(food name) in query
            if(names.containsKey(ATTR_NAME)){
                attrsMap.put(FIELD_NAME, names.get(ATTR_NAME));
            }else{
                attrsMap.put(FIELD_NAME, query);
            }

            //check if in near_here category
            if(categoryIn.contains(TYPE_NEAR_HERE)){
                attrsMap.put(FIELD_NEAR_LOCATION, "here");
                attrsMap.put(FIELD_ADDRESS, "");
            }else{
                if(categoryIn.contains(TYPE_NEAR_BY)){
                    if(names.containsKey(ATTR_ADDRESS)){
                        attrsMap.put(FIELD_NEAR_LOCATION, names.get(ATTR_ADDRESS));
                    }else{
                        attrsMap.put(FIELD_NEAR_LOCATION, "");
                    }
                    attrsMap.put(FIELD_ADDRESS, "");
                }else{
                    if(names.containsKey(ATTR_ADDRESS)){
                        attrsMap.put(FIELD_ADDRESS, names.get(ATTR_ADDRESS));
                    }else{
                        attrsMap.put(FIELD_ADDRESS, "");
                    }
                    attrsMap.put(FIELD_NEAR_LOCATION, "");
                }
            }
        }
    }

    public HashMap<String, String> getHashMap(){return attrsMap;}
}
