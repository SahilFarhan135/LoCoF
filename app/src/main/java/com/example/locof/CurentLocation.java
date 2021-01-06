package com.example.locof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class CurentLocation extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView tvLat,tvLong,tvCountry,tvLocality,tvRoadLine,tvSubLocality,tvPostalCode;
    Button Refresh,ViewMap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curent_location);

        tvLat=findViewById(R.id.tvLat);
        tvLong=findViewById(R.id.tvLong);
        tvCountry=findViewById(R.id.tvCountry);
        tvLocality=findViewById(R.id.tvLocality);
        tvRoadLine=findViewById(R.id.tvRoadLine);
        tvSubLocality=findViewById(R.id.tvSubLocality);
        tvPostalCode=findViewById(R.id.tvPostalCode);
        ViewMap=findViewById(R.id.btcurrentMap);

        ViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CurentLocation.this,MapsCurrent.class);
                startActivity(intent);
            }
        });




        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(CurentLocation.this);

        // TODO: Start a background thread to receive location result.


        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(CurentLocation.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        LocationServices.getFusedLocationProviderClient(CurentLocation.this).
                requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(final LocationResult locationResult) {


                        super.onLocationResult(locationResult);

                        LocationServices.getFusedLocationProviderClient(CurentLocation.this)
                                .removeLocationUpdates(this);
                        if (locationRequest != null && locationResult.getLocations().size() > 0) {
                             int latestLocationIndex = locationResult.getLocations().size() - 1;
                             Double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                             Double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            String Latitude=String.valueOf(latitude);
                            String Longitude=String.valueOf(longitude);
                            tvLat.setText("Latitude :- " + Latitude);
                            tvLong.setText("Longitude :- " + Longitude);

                                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Location> task) {
                                        Location location = task.getResult();
                                        if (location != null) {
                                            try {
                                                //initialize geocodor which is a
                                                Geocoder geocoder = new Geocoder(CurentLocation.this);

                                                List<Address> addressList = geocoder.getFromLocation(
                                                        location.getLatitude(), location.getLongitude(), 1);
                                                String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                                                String Country= addressList.get(0).getCountryName();
                                                tvCountry.setText("Country : - " + Country+"\n"+"Date :- "+mydate);
                                                String Locality=addressList.get(0).getLocality();
                                                tvLocality.setText("Locality : - " + Locality);
                                                String Roadline=addressList.get(0).getAddressLine(0);
                                                tvRoadLine.setText("AddressLine : - " + Roadline);
                                                String SubLocality=addressList.get(0).getSubLocality();
                                                tvSubLocality.setText("Sub-Locality :- "+ SubLocality);
                                                String PostalCode=addressList.get(0).getPostalCode();
                                                tvPostalCode.setText("Postal Code :- "+ PostalCode);
                                                String Addresss = addressList.get(0).getCountryName() + "\n"
                                                        + "\n" + " locality :-" + addressList.get(0).getLocality()
                                                        + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0)+addressList.get(0).getSubLocality()+addressList.get(0).getPostalCode();
                                                Toast.makeText(CurentLocation.this,Addresss,Toast.LENGTH_SHORT).show();





                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    }
                                });








                            Location location = new Location("providerNA");
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);

                        }}}, Looper.getMainLooper());



    }
}
