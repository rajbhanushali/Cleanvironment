package io.paperplane.rajb.cleanvironment;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Location;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.provider.ContactsContract;
        import android.support.design.widget.FloatingActionButton;
        import android.support.multidex.MultiDex;
        import android.support.v4.app.FragmentActivity;
        import android.util.Log;
        import android.view.View;

        import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int RC_LOCATION = 101;
    private static LatLng userLoc;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        userLoc = new LatLng(0,0);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton addAlert = (FloatingActionButton)findViewById(R.id.mapViewFAB);
        addAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MapsActivity.this,AddAlert.class);
//                startActivity(intent);
                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION);

                    return;
                }

                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                userLoc = null;
                userLoc = new LatLng(latitude,longitude);

                Log.d(TAG, "onClick: User Location (Lat,Long):" + latitude + ", " + longitude);



                Intent i = new Intent(MapsActivity.this, HazardFormActivity.class);

                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);

                startActivity(i);

            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RC_LOCATION);

            return;
        }
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        Log.d(TAG, "onClick: User Location (Lat,Long):" + latitude + ", " + longitude);
        userLoc = null;
        userLoc = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));
        CameraUpdate center = CameraUpdateFactory.newLatLng(userLoc);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);


        mMap.addMarker(new MarkerOptions().position(userLoc).title("User Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLoc));

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    for(DataSnapshot childProps: snapshot.getChildren()){

                        Log.d("TAG", String.valueOf(childProps.getValue()));
                        Log.d("TAG", String.valueOf(childProps.child("latitude").getValue()));



                        double latitude =  Double.parseDouble( String.valueOf( childProps.child("latitude").getValue() ) );
                        double longitude = Double.parseDouble( String.valueOf( childProps.child("longitude").getValue() ) );

                        String haztype = String.valueOf( childProps.child("hazardtype").getValue() );

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(haztype).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));


                        //Log.d("TAG", "lat: " + latitude + ", lon: " + longitude);
                    }

                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.addValueEventListener(postListener);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}