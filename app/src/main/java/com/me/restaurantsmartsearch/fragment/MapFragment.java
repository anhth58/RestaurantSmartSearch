package com.me.restaurantsmartsearch.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.async.FetchUrl;
import com.me.restaurantsmartsearch.async.ParserTask;
import com.me.restaurantsmartsearch.async.SearchMapAsyncTask;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener {

    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    Location currentLocation;
    List<Marker> markers = new ArrayList<>();
    List<Marker> markersResult = new ArrayList<>();
    List<Marker> directMakers = new ArrayList<>();
    Marker myMarker, tempMarker, selectedMarker;
    LatLng selectedPlace;
    LatLngBounds curScreen;
    View vDirect;
    Polyline line;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment supportMapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.map));
        vDirect = view.findViewById(R.id.im_direct);
        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mGoogleMap != null && currentLocation != null) {
                Utils.currentLat = currentLocation.getLatitude();
                Utils.currentLong = currentLocation.getLongitude();
                mGoogleMap.setOnMarkerClickListener(this);
                Log.d("currentLocation",Utils.currentLat+" "+Utils.currentLong);
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15));
               if(myMarker == null) myMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).visible(false));
                mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        curScreen = mGoogleMap.getProjection()
                                .getVisibleRegion().latLngBounds;
                        SearchMapAsyncTask searchMapAsyncTask = new SearchMapAsyncTask(curScreen.southwest, curScreen.northeast, new SearchMapAsyncTask.OnSearchComplete() {
                            @Override
                            public void onSearchComplete(String s) {
                                try {
                                    for (int i = 0; i< markersResult.size(); i++){
                                        markersResult.get(i).remove();
                                    }
                                    markersResult.clear();
                                    JSONObject jsonObject = new JSONObject(s);
                                    JSONObject result = jsonObject.getJSONObject("result");
                                    JSONArray poi = result.getJSONArray("poi");
                                    for(int i = 0; i< poi.length(); i++){
                                        JSONObject res = poi.getJSONObject(i);
                                        JSONObject gps = res.getJSONObject("gps");
                                        if(res.optString("title") != null && res.optString("title").length() > 0){
                                            //big marker here
                                            tempMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(gps.optDouble("latitude"), gps.optDouble("longitude"))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_map)).title(res.optString("title")));
                                            markersResult.add(tempMarker);
                                        }
                                        else {
                                            // small marker here
                                            tempMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(gps.optDouble("latitude"), gps.optDouble("longitude"))).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_point)));
                                            markersResult.add(tempMarker);
                                        }
                                        if(tempMarker != null && selectedPlace != null && tempMarker.getPosition().latitude == selectedPlace.latitude && tempMarker.getPosition().longitude == selectedPlace.longitude){
                                            selectedMarker = tempMarker;
                                            selectedMarker.showInfoWindow();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        searchMapAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });
            }
            if(mGoogleMap != null){
                Realm.init(getActivity());
                Realm realm = Realm.getDefaultInstance();
                ArrayList<Restaurant> listResult = new ArrayList<>(realm.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25520).findAll());
                addSearchResult(listResult);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void addSearchResult(List<Restaurant> list) {
        markers.clear();
        for (Restaurant restaurant : list) {
            markers.add(mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_food_map)).title(restaurant.getName())));
        }
    }

    public void moveCamera(int pos) {
        if (markers != null && markers.size() > pos) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markers.get(pos).getPosition(), 15));
            markers.get(pos).showInfoWindow();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedPlace = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
        selectedMarker = marker;
        Log.d("selected",selectedPlace.latitude+" "+selectedPlace.longitude);
        return false;
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

    public void drawDirection(){
        if(selectedPlace != null){
            LatLng origin = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            LatLng dest = new LatLng(selectedPlace.latitude,selectedPlace.longitude);

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
                            if(line != null){
                                line.remove();
                            }
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
                                line = mGoogleMap.addPolyline(lineOptions);
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
            directMakers.add(myMarker);
            directMakers.add(selectedMarker);
            for (Marker marker : directMakers) {
                builder.include(marker.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mGoogleMap.animateCamera(cu);
        }
    }
}
