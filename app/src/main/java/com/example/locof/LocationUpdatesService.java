package com.example.locof;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
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
import com.google.android.gms.maps.LocationSource;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 *
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 *
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification associated with that service is removed.
 */
public class LocationUpdatesService extends Service {
  /*  SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);



    private  static  final String SHARED_PREF_NAME="mypref";
    private static final String FIREBASE_UNIQUEID="Uid";
    private static final String FIREBASE_UNIQUENAME="Uname";

    String UniqueName=sharedPreferences.getString(FIREBASE_UNIQUENAME,null);
    String UniqueId=sharedPreferences.getString(FIREBASE_UNIQUEID,null);*/

  FusedLocationProviderClient fusedLocationProviderClient;
    DatabaseReference reff;
    Button FbaseFamily;
    TextView tvUid,tvUname,tvsignOut;
    CardView cardLocate,cardAddFamily,cardWanderer,cardMyLocation;

    SharedPreferences sharedPreferences;
    private  static  final String SHARED_PREF_NAME="mypref";
    private static final String FIREBASE_UNIQUEID="Uid";
    private static final String FIREBASE_UNIQUENAME="Uname";

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId)  {



        try {


            String UniqueName = sharedPreferences.getString(FIREBASE_UNIQUENAME, null);
            String UniqueId = sharedPreferences.getString(FIREBASE_UNIQUEID, null);


                reff = FirebaseDatabase.getInstance().getReference().child(UniqueId).child(UniqueName).child("Request");

                reff.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.exists()) {

                            final FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(LocationUpdatesService.this);

                            // TODO: Start a background thread to receive location result.


                            final LocationRequest locationRequest = new LocationRequest();
                            locationRequest.setInterval(5000);
                            locationRequest.setFastestInterval(2000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


                            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                    .addLocationRequest(locationRequest);

                            SettingsClient client = LocationServices.getSettingsClient(LocationUpdatesService.this);
                            Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


                            LocationServices.getFusedLocationProviderClient(LocationUpdatesService.this).
                                    requestLocationUpdates(locationRequest, new LocationCallback() {
                                        @Override
                                        public void onLocationResult(final LocationResult locationResult) {


                                            super.onLocationResult(locationResult);


                                            LocationServices.getFusedLocationProviderClient(LocationUpdatesService.this)
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
                                                                Geocoder geocoder = new Geocoder(LocationUpdatesService.this);

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
                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })
                ;
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Intent intent1 = new Intent(LocationUpdatesService.this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(LocationUpdatesService.this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(LocationUpdatesService.this, App.CHANNEL_ID)
                .setContentTitle("LoCoF")
                .setContentText("see Your Family Location")
                .setSmallIcon(R.drawable.locof)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

