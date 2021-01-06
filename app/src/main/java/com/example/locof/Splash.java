package com.example.locof;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

public class Splash extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private  static  final String SHARED_PREF_NAME="mypref";
    private static final String FIREBASE_UNIQUEID="Uid";
    private static final String FIREBASE_UNIQUENAME="Uname";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_splash);






            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    sharedPreferences=getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
                    String UniqueName=sharedPreferences.getString(FIREBASE_UNIQUENAME,null);
                    String UniqueId=sharedPreferences.getString(FIREBASE_UNIQUEID,null);
                    if(UniqueName==null||UniqueId==null){

                        Intent intent=new Intent(Splash.this,Login.class);

                        Toast.makeText(Splash.this,UniqueId+UniqueName+"if",Toast.LENGTH_LONG).show();
                        startActivity(intent);
                        finish();}
                   else if( UniqueId.isEmpty()||UniqueName.isEmpty()){
                        Intent intent=new Intent(Splash.this,Login.class);
                        startActivity(intent);
                        finish();
                   }

                    else{
                        Intent intent=new Intent(Splash.this,MainActivity.class);

                        Toast.makeText(Splash.this,UniqueId+UniqueName+ "else",Toast.LENGTH_LONG).show();

                        startActivity(intent);
                        finish();
                    }
                }
            },3000);
        }
    }


