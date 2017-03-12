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

    public static String ATTR_NAME              = "name";
    public static String ATTR_ADDRESS           = "address";
    public static String ATTR_NEAR_LOCATION     = "nearLocation";
    public static String ATTR_COST              = "cost";
    public static String ATTR_DISTANCE          = "distance";
    public static String ATTR_TIME              = "time";
    public static String ATTR_QUALITY           = "quality";

    public static String FIELD_TYPE             = "type";
    public static String FIELD_NAME             = "name";
    public static String FIELD_ADDRESS          = "address";
    public static String FIELD_NEAR_LOCATION    = "nearLocation";
    public static String FIELD_DISTANCE         = "distance";
    public static String FIELD_TIME             = "time";
    public static String FIELD_QUALITY          = "quality";

    private HashMap<String, String> attrsMap = new HashMap<>();

    public ContextNLP(HashMap<String, String> names, String query){

        System.out.println("Names: " + names);
        attrsMap.put(FIELD_NAME, "");
        attrsMap.put(FIELD_TYPE, "");
        attrsMap.put(FIELD_ADDRESS, "");
        attrsMap.put(FIELD_NEAR_LOCATION, "");
        attrsMap.put(FIELD_DISTANCE, "");
        attrsMap.put(FIELD_TIME, "");
        attrsMap.put(FIELD_QUALITY, "");

        if (names.containsKey(ATTR_NAME)){
            attrsMap.put(FIELD_NAME, names.get(ATTR_NAME));
        }

        if (names.containsKey(ATTR_NEAR_LOCATION)){
            attrsMap.put(FIELD_NEAR_LOCATION, names.get(ATTR_NEAR_LOCATION));
        }

        if (names.containsKey(ATTR_COST)){
            String cost = names.get(ATTR_COST).toLowerCase();
            if (cost.equals("sinh viên") || cost.equals("rẻ")){
                attrsMap.put(FIELD_TYPE, "Sinh viên");
            }
        }

        if (names.containsKey(ATTR_DISTANCE)){
            attrsMap.put(FIELD_DISTANCE, names.get(ATTR_DISTANCE)+"km");
        }

        if (names.containsKey(ATTR_QUALITY)){
            attrsMap.put(FIELD_QUALITY, names.get(ATTR_QUALITY));
        }

        if (names.containsKey(ATTR_TIME)){
            attrsMap.put(FIELD_TIME, names.get(ATTR_TIME));
        }
    }

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
