package com.example.locof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class LocateFamily extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
        ListView Familylist;
        DatabaseReference reff;
        ArrayList<String> arr=new ArrayList<>();
        ArrayAdapter adapter;
        SharedPreferences sharedPreferences;
        private  static  final String SHARED_PREF_NAME="mypref";
        private static final String FIREBASE_UNIQUEID="Uid";
        private static final String FIREBASE_UNIQUENAME="Uname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate_family);

            Familylist=(ListView)findViewById(R.id.Familylist);
            TextView tv=(TextView)findViewById(R.id.tvNotice);

            sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
            String UniqueName=sharedPreferences.getString(FIREBASE_UNIQUENAME,null);
             String UniqueId=sharedPreferences.getString(FIREBASE_UNIQUEID,null);

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Internet is off then,disable button and set notice
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)||cm.getActiveNetworkInfo() == null) {
            tv.setText("Can't connect to Internet! Please connect to internet to View Family Location");

        }





            // reff = FirebaseDatabase.getInstance().getReference().child(UniqueId);

            reff = FirebaseDatabase.getInstance().getReference().child(UniqueId);


            reff.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               if(dataSnapshot.exists()){
                                                   for(DataSnapshot ds:dataSnapshot.getChildren()){
                                                       String family=ds.getKey();
                                                       arr.add(family);

                                                   }
                                                   display(arr);



                                                   Familylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                       @Override
                                                       public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                                            final String arr1[] = arr.toArray(new String[arr.size()]);
                                                            final String path1 = arr1[position];
                                                           Toast.makeText(LocateFamily.this,path1,Toast.LENGTH_SHORT).show();
                                                           final String UniqueId=sharedPreferences.getString(FIREBASE_UNIQUEID,null);
                                                           reff=FirebaseDatabase.getInstance().getReference().child(UniqueId).child(path1).child("Request");
                                                           Member member=new Member();
                                                           member.setRequests("Requst");
                                                           reff.push().setValue(member);
                                                           Intent intent=new Intent(LocateFamily.this,MapsActivity.class);
                                                           intent.putExtra("path",path1);
                                                           startActivity(intent);
                                                           arr.clear();
                                                       //    reff=FirebaseDatabase.getInstance().getReference().child(UniqueId).child(path1).child("Location");
                                                       }
                                                   });


                                               }

                                           }



                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }


                                       }

            );





        }



    private void getlocation(final String LAtitude, final String LOngitude, final String UniqueId, final String Path1) {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        //initialize geocodor which is a
                        Geocoder geocoder = new Geocoder(LocateFamily.this);

                        List<Address> addressList = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1);
                        String Addresss = addressList.get(0).getCountryName() + "\n"
                                + "\n" + " locality :-" + addressList.get(0).getLocality()
                                + "\n" + "AddressLine : -" + addressList.get(0).getAddressLine(0);


                        Member member = new Member();
                        reff=FirebaseDatabase.getInstance().getReference().child(UniqueId).child(Path1).child("Request");
                        member.setLatitude(LAtitude);
                        member.setLongitude(LOngitude);
                        member.setAddressLocale(Addresss);
                        reff.setValue(member);


                        Toast.makeText(LocateFamily.this,"geoloacation method"+ LAtitude+LOngitude+Addresss,Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

         class SampleFragment extends Fragment {

            public View onCreate(LayoutInflater inflater, ViewGroup parentViewGroup, Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_sample, parentViewGroup, false);
                return rootView;
            }
        }

    }
        void display(ArrayList<String> str) {
            ArrayAdapter arrayAdapter=new ArrayAdapter(LocateFamily.this,android.R.layout.simple_list_item_1,str);
            Familylist.setAdapter(arrayAdapter);

        }

    }

