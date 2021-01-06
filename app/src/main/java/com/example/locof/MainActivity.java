package com.example.locof;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.MissingFormatArgumentException;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_PERMISSIONS = 1;
    DatabaseReference reff;
    Button FbaseFamily;
    TextView tvUid, tvUname, tvsignOut;
    CardView cardLocate, cardAddFamily, cardWanderer, cardMyLocation;

    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String FIREBASE_UNIQUEID = "Uid";
    private static final String FIREBASE_UNIQUENAME = "Uname";

    FusedLocationProviderClient fusedLocationProviderClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  startService(new Intent(this,MyService));

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }


        if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_PERMISSIONS);
            }
        }


        tvUid = (TextView) findViewById(R.id.TvUniqueId);
        tvUname = (TextView) findViewById(R.id.TvUniqueName);
        tvsignOut = (TextView) findViewById(R.id.tvSignout);
        cardLocate = (CardView) findViewById(R.id.cardLocate);
        cardMyLocation = (CardView) findViewById(R.id.cardMylocation);
        cardAddFamily = (CardView) findViewById(R.id.cardAddFamily);
        cardWanderer = (CardView) findViewById(R.id.cardwander);


        cardLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocateFamily.class);
                startActivity(intent);
            }
        });
        cardMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CurentLocation.class);
                startActivity(intent);
            }
        });
        tvsignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();

            }
        });


        //setting firebase child nodes to sent and receive data


        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String UniqueName = sharedPreferences.getString(FIREBASE_UNIQUENAME, null);
        String UniqueId = sharedPreferences.getString(FIREBASE_UNIQUEID, null);
        Toast.makeText(this, "Share prefrence" + UniqueName + UniqueId, Toast.LENGTH_SHORT).show();


        Intent intent1 = new Intent(MainActivity.this, LocationUpdatesService.class);
        intent1.putExtra("UniqueId", UniqueId);
        intent1.putExtra("UniqueName", UniqueName);
        ContextCompat.startForegroundService(MainActivity.this, intent1);


        tvUname.setText(UniqueName);
        tvUid.setText(UniqueId);


        ///till here

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        // TODO: Start a background thread to receive location result.


        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        LocationServices.getFusedLocationProviderClient(MainActivity.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(final LocationResult locationResult) {


                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationRequest != null && locationResult.getLocations().size() > 0) {
                            final int latestLocationIndex = locationResult.getLocations().size() - 1;
                            final Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            final Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            String UniqueName = sharedPreferences.getString(FIREBASE_UNIQUENAME, null);
                            String UniqueId = sharedPreferences.getString(FIREBASE_UNIQUEID, null);


                            if (UniqueId.isEmpty() || UniqueName.isEmpty()) {


                                Toast.makeText(MainActivity.this, "Please logout put all details again", Toast.LENGTH_SHORT).show();


                            } else {
                                Member member = new Member();
                                String Latitude = String.valueOf(latitude);
                                String Longitude = String.valueOf(longitude);

                                final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

                                // TODO: Start a background thread to receive location result.


                                final LocationRequest locationRequest = new LocationRequest();
                                locationRequest.setInterval(5000);
                                locationRequest.setFastestInterval(2000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                        .addLocationRequest(locationRequest);

                                SettingsClient client = LocationServices.getSettingsClient(MainActivity.this);
                                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                                Toast.makeText(MainActivity.this, "Location service start", Toast.LENGTH_SHORT).show();

                                LocationServices.getFusedLocationProviderClient(MainActivity.this).
                                        requestLocationUpdates(locationRequest, new LocationCallback() {
                                            @Override
                                            public void onLocationResult(final LocationResult locationResult) {


                                                super.onLocationResult(locationResult);


                                                LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                                        .removeLocationUpdates(this);
                                                if (locationRequest != null && locationResult.getLocations().size() > 0) {

                                                    final int latestLocationIndex = locationResult.getLocations().size() - 1;
                                                    final Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                                    final Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();


                                                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Location> task) {
                                                            Location location = task.getResult();
                                                            if (location != null) {
                                                                try {
                                                                    //initialize geocodor which is a
                                                                    Geocoder geocoder = new Geocoder(MainActivity.this);

                                                                    List<Address> addressList = geocoder.getFromLocation(
                                                                            location.getLatitude(), location.getLongitude(), 1);
                                                                    String Addresss = addressList.get(0).getCountryName() + "\n"
                                                                            + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                                            + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);

                                                                    final String Latitude = String.valueOf(latitude);
                                                                    final String Longitude = String.valueOf(longitude);
                                                                    String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                                                    String UniqueName = sharedPreferences.getString(FIREBASE_UNIQUENAME, null);
                                                                    String UniqueId = sharedPreferences.getString(FIREBASE_UNIQUEID, null);


                                                                    reff = FirebaseDatabase.getInstance().getReference().child(UniqueId).child(UniqueName).child("Location");

                                                                    Member member = new Member();
                                                                    member.setLatitude(Latitude);
                                                                    member.setLongitude(Longitude);
                                                                    member.setDate(mydate);
                                                                    member.setAddressLocale(Addresss);
                                                                    reff.setValue(member);
                                                                    Toast.makeText(MainActivity.this, "Location service complete", Toast.LENGTH_SHORT).show();


                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }


                                                            }
                                                        }
                                                    });


                                                    Location location = new Location("providerNA");
                                                    location.setLatitude(latitude);
                                                    location.setLongitude(longitude);


                                                }


                                            }
                                        }, Looper.getMainLooper());


                            }
                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);

                        }
                    }
                }, Looper.getMainLooper());


    }



    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
/*
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(new LatLng(22.8383533,86.1957369)).title("MyHome"));
    }*/
