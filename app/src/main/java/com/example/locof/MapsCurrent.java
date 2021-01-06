package com.example.locof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class MapsCurrent  extends FragmentActivity implements OnMapReadyCallback {

        private GoogleMap mMap;
        private Button refresh;
        TextView tv;
        FusedLocationProviderClient fusedLocationProviderClient;
        DatabaseReference reff;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_SHORT).show();
                mapFragment.getMapAsync(com.example.locof.MapsCurrent.this);
            }
            refresh=(Button)findViewById(R.id.btrefresh);
            String path = getIntent().getStringExtra("path");
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsCurrent.this);
            // TODO: Start a background thread to receive location result.
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            SettingsClient client = LocationServices.getSettingsClient(MapsCurrent.this);
            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
            LocationServices.getFusedLocationProviderClient(MapsCurrent.this).
                    requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(final LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(MapsCurrent.this)
                                    .removeLocationUpdates(this);
                            if (locationRequest != null && locationResult.getLocations().size() > 0) {
                                int latestLocationIndex = locationResult.getLocations().size() - 1;
                                Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                LatLng locate = new LatLng(latitude, longitude);
                                mMap.addMarker(new MarkerOptions().position(locate).title("you"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(locate));
                                mMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                                Location location = new Location("providerNA");
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                            }}}, Looper.getMainLooper());
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recreate();
                }
            });
        }
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
        }}

