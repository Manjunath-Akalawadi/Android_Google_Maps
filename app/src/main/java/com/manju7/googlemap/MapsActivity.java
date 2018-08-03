package com.manju7.googlemap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastlocation;
    private Marker marker;
    private static final int User_Location_Code = 77;

    private EditText editTextSearch;
    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

            checkPermission();

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


      @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            boolean isSuccess = googleMap.setMapStyle
                    (MapStyleOptions.loadRawResourceStyle(this,R.raw.style_map));

            if (!isSuccess){

                Toast.makeText(this,"Maps style loads failed",Toast.LENGTH_LONG);
            }

        }catch (Resources.NotFoundException ex){
                ex.printStackTrace();
        }


          if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                  ==PackageManager.PERMISSION_GRANTED) {

            buildApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setTiltGesturesEnabled(true);
        }

    }



    public boolean checkPermission(){


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)

        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
            {

                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION},User_Location_Code);

            }
            else
                {

                ActivityCompat.requestPermissions(this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION},User_Location_Code);

                }

            return false;

        }else{

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode){

            case User_Location_Code:
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.
                            ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                        if (googleApiClient == null){

                            buildApiClient();
                        }
                        mMap.setMyLocationEnabled(true);

                    }
                }else {

                    Toast.makeText(this,"Permission Denied...",Toast.LENGTH_LONG).show();
                }
                return;

        }
    }

    protected synchronized void buildApiClient(){

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();


    }

    @Override
    public void onLocationChanged(Location location) {

        lastlocation = location;

        if (marker != null){

            marker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker
                (BitmapDescriptorFactory.HUE_BLUE));

        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(-5));


        if(googleApiClient != null){

            LocationServices.FusedLocationApi.
                    removeLocationUpdates(googleApiClient,this);
        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest =new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                ==PackageManager.PERMISSION_GRANTED){

            LocationServices.FusedLocationApi.requestLocationUpdates
                    (googleApiClient,locationRequest,this);

        }



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
