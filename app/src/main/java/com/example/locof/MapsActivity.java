package com.example.locof;

import androidx.annotation.NonNull;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.fragment.app.FragmentActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button refresh;
    DatabaseReference reff;
    TextView Address,Date;
    SharedPreferences sharedPreferences;
    private  static  final String SHARED_PREF_NAME="mypref";
    private static final String FIREBASE_UNIQUEID="Uid";
    private static final String FIREBASE_UNIQUENAME="Uname";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            Toast.makeText(getApplicationContext(), "not null", Toast.LENGTH_SHORT).show();
            mapFragment.getMapAsync(MapsActivity.this);
        }
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String UniqueName = sharedPreferences.getString(FIREBASE_UNIQUENAME, null);
        final String UniqueId = sharedPreferences.getString(FIREBASE_UNIQUEID, null);
        refresh=(Button)findViewById(R.id.btrefresh);
        Date=findViewById(R.id.date);
        Address=findViewById(R.id.address);
        String path = getIntent().getStringExtra("path");


        try {
            reff = FirebaseDatabase.getInstance().getReference().child(UniqueId).child(path).child("Location");
            reff.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if (dataSnapshot.getValue().toString().trim() != " ") {
                            String family = String.valueOf(dataSnapshot.getValue());
                            double latitude = Double.parseDouble(String.valueOf(dataSnapshot.child("latitude").getValue()));
                            double longitude = Double.parseDouble(String.valueOf(dataSnapshot.child("longitude").getValue()));
                            String date= String.valueOf(dataSnapshot.child("date").getValue());
                            Date.setText("Last Updated at : -" +date);
                            String Loc = String.valueOf(dataSnapshot.child("addressLocale").getValue());
                            Address.setText(Loc);
                            LatLng locate = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(locate).title("you"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(locate));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(19));
                        } else{
                            Toast.makeText(MapsActivity.this,"No Location",Toast.LENGTH_LONG).show();
                        }}}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }});}
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }}