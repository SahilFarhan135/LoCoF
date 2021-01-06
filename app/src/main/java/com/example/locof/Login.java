package com.example.locof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class Login extends AppCompatActivity {
        DatabaseReference reff;
        TextView SignUp,Notice;

        FusedLocationProviderClient fusedLocationProviderClient;
        SharedPreferences sharedPreferences;
        private  static  final String SHARED_PREF_NAME="mypref";
        private static final String FIREBASE_UNIQUEID="Uid";
        private static final String FIREBASE_UNIQUENAME="Uname";
        private static final int REQUEST_PERMISSIONS = 1;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            //checking permission till here

            if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                if ((ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) && (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION))) {

                } else {
                    ActivityCompat.requestPermissions(Login.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            REQUEST_PERMISSIONS);
                }
            }


            //cheking permissins

            SignUp=(TextView)findViewById(R.id.SignUp);
            final Button FbaseFamily=(Button)findViewById(R.id.btFBaseFmaily);
             EditText UniqueName=(EditText)findViewById(R.id.EdUniqueName);
             EditText  UniqueId=(EditText)findViewById(R.id.EdUniqueId);
             Notice=(TextView)findViewById(R.id.notice);
            sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);




            Notice.setVisibility(View.INVISIBLE);
            String Uniqueid=UniqueId.getText().toString().trim();
            String Uniquename=UniqueName.getText().toString().trim();



                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(FIREBASE_UNIQUENAME, UniqueName.getText().toString().trim());
                    editor.putString(FIREBASE_UNIQUEID, UniqueId.getText().toString().trim());
                    editor.apply();


                    //checking internet connection


                    final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    //Internet is off then,disable button and set notice
                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||cm.getActiveNetworkInfo() == null) {

                        FbaseFamily.setBackgroundColor(getResources().getColor(R.color.black));
                        FbaseFamily.setError("No Internet");
                        FbaseFamily.setText("No Internet");
                        Toast.makeText(Login.this,"No Internet! Please Connect to internet and Restart the App",Toast.LENGTH_LONG).show();
                        FbaseFamily.setEnabled(true);
                    } else{
                        Toast.makeText(Login.this," Welcome",Toast.LENGTH_SHORT).show();


            FbaseFamily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText UniqueName=(EditText)findViewById(R.id.EdUniqueName);
                    EditText  UniqueId=(EditText)findViewById(R.id.EdUniqueId);

                    String Uniqueid=UniqueId.getText().toString().trim();
                    String Uniquename=UniqueName.getText().toString().trim();
                    Toast.makeText(Login.this,"Uni"+Uniqueid+Uniquename,Toast.LENGTH_SHORT).show();




                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(FIREBASE_UNIQUENAME, UniqueName.getText().toString().trim());
                    editor.putString(FIREBASE_UNIQUEID, UniqueId.getText().toString().trim());
                    editor.apply();

                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        buildAlertMessageNoGps();
                    }


                    if (Uniqueid.isEmpty() || Uniquename.isEmpty()) {
                        UniqueName.setError("Plaese Enter NAme");
                        UniqueId.setError("please Enter Unique Family Id");
                        Toast.makeText(Login.this, "Please Enter All details", Toast.LENGTH_SHORT).show();
                    }else{
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Login.this);

                        // TODO: Start a background thread to receive location result.


                        final LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(3000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(locationRequest);

                        SettingsClient client = LocationServices.getSettingsClient(Login.this);
                        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                        Toast.makeText(Login.this,"fused :- 2 before",Toast.LENGTH_SHORT).show();



                        LocationServices.getFusedLocationProviderClient(Login.this).
                                requestLocationUpdates(locationRequest, new LocationCallback() {
                                    @Override
                                    public void onLocationResult(final LocationResult locationResult) {


                                        super.onLocationResult(locationResult);
                                        Toast.makeText(Login.this,"Location services initiated",Toast.LENGTH_SHORT).show();


                                        LocationServices.getFusedLocationProviderClient(Login.this)
                                                .removeLocationUpdates(this);
                                        if (locationRequest != null && locationResult.getLocations().size() > 0) {
                                            Toast.makeText(Login.this,"Location acqiured",Toast.LENGTH_SHORT).show();

                                            final int latestLocationIndex = locationResult.getLocations().size() - 1;
                                            final Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                            final Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();




                                            String Latitude = String.valueOf(latitude);
                                            String Longitude = String.valueOf(longitude);


                                            getlocation(Latitude, Longitude);

                                            Toast.makeText(Login.this,"Location :- "+ Latitude+ Longitude,Toast.LENGTH_SHORT).show();





                                            Location location = new Location("providerNA");
                                            location.setLatitude(latitude);
                                            location.setLongitude(longitude);
                                            // fetchAddressFromLatLong(location);
                                            Intent intent = new Intent(Login.this, MainActivity.class);
                                            Toast.makeText(Login.this,"Intent method",Toast.LENGTH_SHORT).show();

                                            startActivity(intent);



                                        }
                                        Toast.makeText(Login.this,"Looper inside",Toast.LENGTH_SHORT).show();


                                    }
                                }, Looper.getMainLooper());




                    }
                }
            });
        }}





                                            private void getlocation( final String LAtitude,final String LOngitude) {
                                                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Location> task) {
                                                        Location location = task.getResult();
                                                        if (location != null) {
                                                            try {
                                                                //initialize geocodor which is a
                                                                Geocoder geocoder = new Geocoder(Login.this);

                                                                List<Address> addressList = geocoder.getFromLocation(
                                                                        location.getLatitude(), location.getLongitude(), 1);
                                                                String Addresss = addressList.get(0).getCountryName() + "\n"
                                                                        + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                                        + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);
                                                                EditText UniqueName=(EditText)findViewById(R.id.EdUniqueName);
                                                                 EditText  UniqueId=(EditText)findViewById(R.id.EdUniqueId);
                                                                String Uniqueid = UniqueId.getText().toString().trim();
                                                                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                                                String Uniquename = UniqueName.getText().toString().trim();

                                                                Member member = new Member();
                                                                reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");
                                                                member.setLatitude(LAtitude);
                                                                member.setLongitude(LOngitude);
                                                                member.setDate(mydate);
                                                                member.setAddressLocale(Addresss);
                                                                reff.setValue(member);


                                                                Toast.makeText(Login.this,"geoloacation method"+ LAtitude+LOngitude+Addresss,Toast.LENGTH_SHORT).show();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }


                                                        }
                                                    }
                                                });


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

                    reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");
                    reff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                buildAlertMessageNoGps();
                            } else {


                                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Login.this);

                                // TODO: Start a background thread to receive location result.


                                final LocationRequest locationRequest = new LocationRequest();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(3000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                        .addLocationRequest(locationRequest);

                                SettingsClient client = LocationServices.getSettingsClient(Login.this);
                                Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

                                LocationServices.getFusedLocationProviderClient(Login.this).
                                        requestLocationUpdates(locationRequest, new LocationCallback() {
                                            @Override
                                            public void onLocationResult(LocationResult locationResult) {


                                                super.onLocationResult(locationResult);

                                                LocationServices.getFusedLocationProviderClient(Login.this)
                                                        .removeLocationUpdates(this);
                                                if (locationRequest != null && locationResult.getLocations().size() > 0) {
                                                    int latestLocationIndex = locationResult.getLocations().size() - 1;
                                                    Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                                    Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();


                                                    fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Location> task) {
                                                            Location location = task.getResult();
                                                            if (location != null) {
                                                                try {
                                                                    //initialize geocodor which is a
                                                                    Geocoder geocoder = new Geocoder(Login.this);
                                                                    Toast.makeText(Login.this, "geocodor", Toast.LENGTH_SHORT).show();

                                                                    List<Address> addressList = geocoder.getFromLocation(
                                                                            location.getLatitude(), location.getLongitude(), 1);
                                                                    String Addresss = addressList.get(0).getCountryName() + "\n"
                                                                            + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                                            + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);
                                                                    //   int random = new Random().nextInt(26) + 75;
                                                                    String Uniqueid = UniqueId.getText().toString().trim();
                                                                    String Uniquename = UniqueName.getText().toString().trim();

                                                                    reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");
                                                                    Member member = new Member();
                                                                    member.setAddressLocale(Addresss);
                                                                    reff.setValue(member);


                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }


                                                            }
                                                        }
                                                    });


                                                    Member member = new Member();
                                                    String Latitude = String.valueOf(latitude);
                                                    String Longitude = String.valueOf(longitude);
                                                    String Uniqueid = UniqueId.getText().toString().trim();
                                                    String Uniquename = UniqueName.getText().toString().trim();


                                                    reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");
                                                    member.setLatitude(Latitude);
                                                    member.setLongitude(Longitude);
                                                    reff.setValue(member);
                                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                                    startActivity(intent);


                                                    Location location = new Location("providerNA");
                                                    location.setLatitude(latitude);
                                                    location.setLongitude(longitude);
                                                    // fetchAddressFromLatLong(location);
                                                }

                                            }
                                        }, Looper.getMainLooper());


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }});}

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

}*/

/*

            FbaseFamily.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String Uniqueid = UniqueId.getText().toString().trim();
                    String Uniquename = UniqueName.getText().toString().trim();

                    if (UniqueId.getText().toString().isEmpty() || UniqueName.getText().toString().isEmpty()) {

                        UniqueName.setError("Enter Name");
                        UniqueId.setError("Enter your Family Id");
                        Toast.makeText(getApplicationContext(), "Please fill All the details", Toast.LENGTH_SHORT).show();

                    } else {


                            reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");
                            reff.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot datasnapshot) {


                                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Login.this);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(FIREBASE_UNIQUENAME, UniqueName.getText().toString().trim());
                                    editor.putString(FIREBASE_UNIQUEID, UniqueId.getText().toString().trim());
                                    editor.apply();


                                    //Firebase data manueviring

                                    final LocationRequest locationRequest = new LocationRequest();
                                    locationRequest.setInterval(10000);
                                    locationRequest.setFastestInterval(3000);
                                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                            .addLocationRequest(locationRequest);

                                    SettingsClient client = LocationServices.getSettingsClient(Login.this);
                                    Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
                                    Toast.makeText(Login.this,"Location",Toast.LENGTH_SHORT).show();


                                    LocationServices.getFusedLocationProviderClient(Login.this).
                                            requestLocationUpdates(locationRequest, new LocationCallback() {
                                                @Override
                                                public void onLocationResult(LocationResult locationResult) {
                                                    Toast.makeText(Login.this,"LocationResults",Toast.LENGTH_SHORT).show();


                                                    super.onLocationResult(locationResult);

                                                    LocationServices.getFusedLocationProviderClient(Login.this)
                                                            .removeLocationUpdates(this);
                                                    if (locationRequest != null && locationResult.getLocations().size() > 0) {
                                                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                                                        Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                                        Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                                                        String Latitude = String.valueOf(latitude);
                                                        String Longitude = String.valueOf(longitude);
                                                        String Uniqueid = UniqueId.getText().toString().trim();
                                                        String Uniquename = UniqueName.getText().toString().trim();

                                                        // TODO: Start a background thread to receive location result.


                                                        Member member = new Member();

                                                        reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");

                                                        member.setLatitude(Latitude);
                                                        member.setLongitude(Longitude);
                                                        reff.setValue(member);


                                                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Location> task) {
                                                                Location location = task.getResult();
                                                                if (location != null) {
                                                                    try {
                                                                        //initialize geocodor which is a
                                                                        Geocoder geocoder = new Geocoder(Login.this);

                                                                        List<Address> addressList = geocoder.getFromLocation(
                                                                                location.getLatitude(), location.getLongitude(), 1);
                                                                        String Addresss = addressList.get(0).getCountryName() + "\n"
                                                                                + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                                                + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);
                                                                        //   int random = new Random().nextInt(26) + 75;
                                                                        String Uniqueid = UniqueId.getText().toString().trim();
                                                                        String Uniquename = UniqueName.getText().toString().trim();

                                                                        reff = FirebaseDatabase.getInstance().getReference().child(Uniqueid).child(Uniquename).child("Location");

                                                                        Member member = new Member();
                                                                        member.setAddressLocale(Addresss);
                                                                        reff.setValue(member);
                                                                        Toast.makeText(Login.this, "Un" + Uniqueid + " " + Uniquename + " " + reff.child("LocaleAddress").setValue(member), Toast.LENGTH_SHORT).show();


                                                                    } catch (IOException e) {
                                                                        e.printStackTrace();
                                                                    }


                                                                }
                                                            }
                                                        });


                                                        Location location = new Location("providerNA");
                                                        location.setLatitude(latitude);
                                                        location.setLongitude(longitude);
                                                        // fetchAddressFromLatLong(location);
                                                        Toast.makeText(Login.this,"Datasnapshot",Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                                        startActivity(intent);

                                                    }

                                                }


                                            }, Looper.getMainLooper());



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    }






            });}
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
    }}*/



