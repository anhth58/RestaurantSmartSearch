package com.me.restaurantsmartsearch.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;

import java.util.ArrayList;
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
        imDirect = (ImageView) findViewById(R.id.im_direct);

        imDirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
//                        .add(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
//                        .width(5)
//                        .color(Color.RED));
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
                myMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location)));
                if(getIntent()!= null){
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
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(res.getLatitude(),res.getLongitude()), 17));
        restaurantMarker.showInfoWindow();
    }
}
