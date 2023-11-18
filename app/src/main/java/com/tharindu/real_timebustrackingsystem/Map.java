package com.tharindu.real_timebustrackingsystem;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tharindu.real_timebustrackingsystem.databinding.ActivityMapBinding;
import com.tharindu.real_timebustrackingsystem.directionhelpers.FetchURL;
import com.tharindu.real_timebustrackingsystem.directionhelpers.TaskLoadedCallback;


import android.Manifest;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Locale;

public class Map extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private GoogleMap mMap;
    private ActivityMapBinding binding;
    MarkerOptions place1, place2;
    Polyline currentPolyline;
    private TextView speed;
    private TextView distance;
    private TextView name;
    private TextView route;
    private TextView no;
    private TextView arrowTime;
    private Button stop;

    private FusedLocationProviderClient fusedLocationClient; // Declare at the class level
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        speed=findViewById(R.id.speedTextView);
        distance=findViewById(R.id.distanceTextView);
        name=findViewById(R.id.nameTextView);
        route=findViewById(R.id.routeTextView);
        no=findViewById(R.id.noTextView);
        arrowTime=findViewById(R.id.arrowTimeTextView);
        stop=findViewById(R.id.btnStop);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        createLocationRequest();
        checkLocationPermission();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCurrentLocation();
            }
        },1);

        Intent intent=getIntent();
        String userId=intent.getStringExtra("ID");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(userId); // Change "userId" to the actual user ID reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                double currentLatitude = dataSnapshot.child("currentLatitude").getValue(Double.class);
                double currentLongitude = dataSnapshot.child("currentLongitude").getValue(Double.class);
                String nameValue=dataSnapshot.child("busName").getValue(String.class);
                String noValue=dataSnapshot.child("busNo").getValue(String.class);
                String routeValue=dataSnapshot.child("busRoute").getValue(String.class);
                double speedValue=Double.valueOf(df.format(dataSnapshot.child("speed").getValue(Double.class)));


                name.setText(nameValue);
                no.setText(noValue);
                route.setText(routeValue);
                speed.setText(String.valueOf(speedValue)+ "Kmh");
                //distance
                Location location1=new Location("location1");
                location1.setLatitude(latitude);
                location1.setLongitude(longitude);
                Location location2=new Location("location2");
                location2.setLatitude(currentLatitude);
                location2.setLongitude(currentLongitude);

                float distanceInMeters = location1.distanceTo(location2);
                double distanceInKm=Double.valueOf(df.format(distanceInMeters/1000.0));
                String distanceText = String.format(Locale.getDefault(), "%.2f meters", distanceInMeters);
                double time=Double.valueOf(df.format(distanceInKm/speedValue));

                distance.setText(String.valueOf(distanceInKm)+" Km");
                arrowTime.setText(String.valueOf(time)+" h");

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(userId); // Change "userId" to the actual user ID reference
                        myRef.child("isStop").setValue(true);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        } else {
            getCurrentLocation();
        }
    }

    private void startLocationUpdates() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Intent intent=getIntent();
                String userId=intent.getStringExtra("ID");
                super.onLocationResult(locationResult);
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    if (place1 == null) {
                        place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()));
                        // You can add the marker or use the location data as needed
                    } else {
                        // Update Firebase with the new location data
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(userId); // Change "userId" to the actual user ID reference
                        myRef.child("currentLatitude").setValue(location.getLatitude());
                        myRef.child("currentLongitude").setValue(location.getLongitude());
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent intent=getIntent();
        String userId=intent.getStringExtra("ID");
        mMap = googleMap;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(userId); // Change "userId" to the actual user ID reference
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                double currentLatitude = dataSnapshot.child("currentLatitude").getValue(Double.class);
                double currentLongitude = dataSnapshot.child("currentLongitude").getValue(Double.class);
                String name=dataSnapshot.child("busName").getValue(String.class);
                String no=dataSnapshot.child("busNo").getValue(String.class);
                String route=dataSnapshot.child("busRoute").getValue(String.class);
                Double speed=dataSnapshot.child("speed").getValue(Double.class);


                mMap.clear();
                place2 = new MarkerOptions().position(new LatLng(latitude+0.3, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Name: "+name).snippet("hi");
                mMap.addMarker(place2).showInfoWindow();

                place1 = new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude));
                mMap.addMarker(place1);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(place1.getPosition());
                builder.include(place2.getPosition());
                LatLngBounds bounds = builder.build();
                int padding = 410;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                String url = getUrl(place1.getPosition(), place2.getPosition(), "driving");
                new FetchURL(Map.this).execute(url, "driving");

                //distance
                Location location1=new Location("location1");
                location1.setLatitude(latitude);
                location1.setLongitude(longitude);
                Location location2=new Location("location2");
                location2.setLatitude(currentLatitude);
                location2.setLongitude(currentLongitude);

                float distanceInMeters = location1.distanceTo(location2);
                double distanceInKm=distanceInMeters/1000.0;
                String distanceText = String.format(Locale.getDefault(), "%.2f meters", distanceInMeters);

                if(distanceInMeters<5){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(userId); // Change "userId" to the actual user ID reference
                    myRef.child("isStop").setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }
}