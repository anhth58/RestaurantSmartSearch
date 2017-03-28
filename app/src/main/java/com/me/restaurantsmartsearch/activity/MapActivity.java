package com.me.restaurantsmartsearch.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.async.FetchUrl;
import com.me.restaurantsmartsearch.async.ParserTask;
import com.me.restaurantsmartsearch.model.Restaurant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Laptop88T on 2/16/2017.
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    List<Marker> markers = new ArrayList<>();
    Restaurant restaurant;
    Marker myMarker, restaurantMarker;
    ImageView imDirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fragment);

        initMap();
        initVIew();
        initListener();
    }

    public void initMap() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void initVIew() {
        imDirect = (ImageView) findViewById(R.id.im_direct);
    }

    public void initListener() {
        imDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
//                        .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
//                        .width(5)
//                        .color(Color.RED));
                LatLng origin = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                LatLng dest = new LatLng(restaurant.getLatitude(),restaurant.getLongitude());

                // Getting URL to the Google Directions API
                String url = getUrl(origin, dest);
                Log.d("onMapClick", url.toString());
                FetchUrl FetchUrl = new FetchUrl(new FetchUrl.OnFetchUrlComplete() {
                    @Override
                    public void onFetchUrlComplete(String s) {
                        ParserTask parserTask = new ParserTask(new ParserTask.OnParserTaskCompleted() {
                            @Override
                            public void onParserTaskCompleted(List<List<HashMap<String, String>>> result) {
                                ArrayList<LatLng> points;
                                PolylineOptions lineOptions = null;

                                // Traversing through all the routes
                                for (int i = 0; i < result.size(); i++) {
                                    points = new ArrayList<>();
                                    lineOptions = new PolylineOptions();

                                    // Fetching i-th route
                                    List<HashMap<String, String>> path = result.get(i);

                                    // Fetching all the points in i-th route
                                    for (int j = 0; j < path.size(); j++) {
                                        HashMap<String, String> point = path.get(j);

                                        double lat = Double.parseDouble(point.get("lat"));
                                        double lng = Double.parseDouble(point.get("lng"));
                                        LatLng position = new LatLng(lat, lng);

                                        points.add(position);
                                    }

                                    // Adding all the points in the route to LineOptions
                                    lineOptions.addAll(points);
                                    lineOptions.width(10);
                                    lineOptions.color(Color.RED);

                                    Log.d("onPostExecute","onPostExecute lineoptions decoded");

                                }

                                // Drawing polyline in the Google Map for the i-th route
                                if(lineOptions != null) {
                                    mGoogleMap.addPolyline(lineOptions);
                                }
                                else {
                                    Log.d("onPostExecute","without Polylines drawn");
                                }
                            }
                        });

                        // Invokes the thread for parsing the JSON data
                        parserTask.execute(s);
                    }
                });

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                //move map camera
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                markers.add(myMarker);
                markers.add(restaurantMarker);
                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }
                LatLngBounds bounds = builder.build();
                int padding = 60; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mGoogleMap.animateCamera(cu);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mGoogleMap != null && currentLocation != null) {
                //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
                if (getIntent() != null) {
                    restaurant = getIntent().getParcelableExtra("restaurant");
                    moveCamera(restaurant);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    public void moveCamera(Restaurant res) {
        restaurantMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(res.getLatitude(), res.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_map)).title(res.getName()));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(res.getLatitude(), res.getLongitude()), 17));
        restaurantMarker.showInfoWindow();
    }

    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
}
