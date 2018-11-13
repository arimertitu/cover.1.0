package com.example.project.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.location.LocationListener;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LocationManager locationManager;
    private Context mContext= MainActivity.this;
    private String locationCountry;
    private String provider;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        currentUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        bottomNavigationBar();
        Boolean a = findLocation();
        sendDataShuffle();

        initImageLoader();

    }


    private boolean findLocation() {


        locationManager = (LocationManager) getLayoutInflater().getContext().getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(new Criteria(), false);



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Location Permission")
                        .setMessage("MyApp application needs to your location for shuffle with others!")
                        .setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        99);
                            }
                        })
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }
            return false;
        } else {

            Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);


            try

            {
                Geocoder geocoder = new Geocoder(this);
                List<Address> addresses = null;
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                locationCountry = addresses.get(0).getCountryName();


            } catch (
                    IOException e)

            {

                e.printStackTrace();
            }

            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        long a = 400;
                        float b = (float) 1.0;
                        LocationListener locationlistener = null;
                        locationManager.requestLocationUpdates(provider, a, b, locationlistener);
                    }

                } else {



                }
                return;
            }

        }
    }


    private void bottomNavigationBar() {

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {



                Fragment selectedFragment = null;

                switch (item.getItemId()) {
                    case R.id.bottombaritem_chats:
                        selectedFragment = ChatsFragment.newInstance();
                        break;
                    case R.id.bottombaritem_shuffle:
                        selectedFragment = ShuffleFragment.newInstance();
                        break;
                    case R.id.bottombaritem_profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ChatsFragment.newInstance());
        transaction.commit();

    }


    private void sendDataShuffle() {


        Bundle bundle = new Bundle();
        bundle.putString(country(), locationCountry);
        ShuffleFragment myObj = new ShuffleFragment();
        myObj.setArguments(bundle);

    }

    public String country(){
        return locationCountry;
    }

        private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null ){
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        }else if (currentUser != null){

            databaseReference.child("status").setValue("online");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (currentUser != null){

            databaseReference.child("status").setValue("offline");
        }
    }
}
