package com.me.restaurantsmartsearch.utils;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class Constant {
    // config server
    public static final String IP_SERVER_HTTP = "https://5d9bf36518aa7ffd7ef1ee6b060d3e45.us-east-1.aws.found.io:9243";
    public static final String IP_SERVER_HTTPS = "https://5d9bf36518aa7ffd7ef1ee6b060d3e45.us-east-1.aws.found.io:9243";
    public static final String API_IMPORT_DATA_HTTP = IP_SERVER_HTTP + "/mydb3/restaurant/";
    public static final String API_IMPORT_DATA_HTTPS = IP_SERVER_HTTPS + "/mydb3/restaurant/";
    public static final String INDEX_NAME = "mydb3";
    public static final String DOCUMENT_NAME = "restaurant";
    public static final String AUTHORIZATION = "Basic ZWxhc3RpYzpjQXUzdXRZY0hPaDg0MjhTc1EzdDRINGc=";

    //fields name

    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String _ID = "_id";
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
    public static final String OPTIONS = "options";
    public static final String _SOURCE = "_source";
    public static final String HITS = "hits";
    public static final String PIN = "pin";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String LOCATION = "location";
    public static final String PIN_LOCATION = "pin.location";
    public static final String GEO_DISTANCE = "geo_distance";
    public static final String DISTANCE = "distance";
    public static final String QUERY = "query";
    public static final String CROSS_FIELDS = "cross_fields";
    public static final String MOST_FIELDS = "most_fields";
    public static final String FIELDS = "fields";
    public static final String OPERATOR = "operator";
    public static final String OR = "or";
    public static final String AND = "and";
    public static final String MULTI_MATCH = "multi_match";
    public static final String MUST = "must";
    public static final String BOOL = "bool";
    public static final String FILTER = "filter";
    public static final String MATCH = "match";

    //near by radius
    public static final String DISTANCE_RADIUS = "2km";

    //constant

    public static final String DAY = "day";
    public static final String HERE = "here";

}
