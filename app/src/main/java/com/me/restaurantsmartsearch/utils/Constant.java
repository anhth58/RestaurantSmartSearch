package com.me.restaurantsmartsearch.utils;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class Constant {
    // config server
    public static final String IP_SERVER_HTTP = "https://5d9bf36518aa7ffd7ef1ee6b060d3e45.us-east-1.aws.found.io:9243";
    public static final String IP_SERVER_HTTPS = "https://5d9bf36518aa7ffd7ef1ee6b060d3e45.us-east-1.aws.found.io:9243";
    public static final String API_IMPORT_DATA_HTTP = IP_SERVER_HTTP + "/mydb/restaurant/";
    public static final String API_IMPORT_DATA_HTTPS = IP_SERVER_HTTPS + "/mydb/restaurant/";
    public static final String INDEX_NAME = "mydb";
    public static final String DOCUMENT_NAME = "restaurant";
    public static final String AUTHORIZATION = "Basic ZWxhc3RpYzpjQXUzdXRZY0hPaDg0MjhTc1EzdDRINGc=";

    //fields name

    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String IMAGE = "image";
    public static final String PRICE = "price";
    public static final String POINT = "point";
    public static final String P_COST = "pCost";
    public static final String P_LOCATION = "pLocation";
    public static final String P_SPACE = "pSpace";
    public static final String P_SERVE = "pServe";
    public static final String P_QUALITY = "pQuality";

    //query constant

    public static final String INPUT = "input";
    public static final String WEIGHT = "weight";
    public static final String SUGGEST = "suggest";
    public static final String FIELD = "field";
    public static final String PREFIX = "prefix";
    public static final String COMPLETION = "completion";
    public static final String RESTAURANT_SUGGEST = "restaurant-suggest";
}
