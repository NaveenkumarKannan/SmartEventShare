package com.project.naveen.smarteventsshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;

    private static final String TAG = "MapsActivity";
    private static final long INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 1000;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    Double latitude, longitude;
    String currentAddr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startTracking();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void startTracking() {
        Log.w(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.w(TAG, "GooglePlayServicesAvailable");
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                Log.w(TAG, "googleApiClient.connect();");
                googleApiClient.connect();
            }
            //googleApiClient.connect();
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.w(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            Geocoder geoCoder = new Geocoder(MapsActivity.this, Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            try {
                Double latitude,longitude;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i=0; i<maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }

                String finalAddress;
                //finalAddress= builder.toString(); //This is the complete address.
                finalAddress = address.get(0).getAddressLine(0)
                //        +"\n"+ address.get(0).getLocality()+"\n"+
                //      address.get(0).getAdminArea()+"\n"+
                //    address.get(0).getCountryName()+"\n"+
                //  address.get(0).getPostalCode()
                ;
                Log.w("Location", "Latitude: " + latitude + "\nLongitude: " + longitude+"\nFinal Address: "+finalAddress);

                //t.setText("Latitude: " + latitude + "\nLongitude: " + longitude+"\nFinal Address: "+finalAddress); //This will display the final address.

                this.latitude = latitude;
                this.longitude = longitude;
                currentAddr = finalAddress;

                LatLng latLng = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            } catch (IOException e) {
                // Handle IOException
            } catch (NullPointerException e) {
                // Handle NullPointerException
            }

        } else {
            Log.w(TAG, "NO POSITION FOUND");
        }
    }

    private void stopLocationUpdates() {
        Log.w(TAG, "stopLocationUpdates");
        googleApiClient.disconnect();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL); // milliseconds
        locationRequest.setFastestInterval(FASTEST_INTERVAL); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed");

        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "GoogleApiClient connection has been suspend");
    }

}
