package io.paperplane.rajb.cleanvironment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HazardFormActivity extends Activity {

    private double latitude, longitude;
    Spinner spinner;
    EditText description;

    private String problemType;
    private String desc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hazard_form);

        Intent i = getIntent();

        latitude = i.getDoubleExtra("latitude", 37.5635290);
        longitude = i.getDoubleExtra("longitude", -122.3249980);

        spinner = (Spinner) findViewById(R.id.spinner);
        description = (EditText) findViewById(R.id.editText);


    }

    public void submitData(View v){

        problemType = spinner.getSelectedItem().toString();
        desc = description.getText().toString();

        Toast.makeText(getApplicationContext(), latitude + ", " + longitude + ", " + problemType + ", " + desc, Toast.LENGTH_LONG).show();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(GoogleSignIn.USER_ID);
        String hazardID = myRef.push().getKey();

        myRef.child(hazardID).child("latitude").setValue(latitude);
        myRef.child(hazardID).child("longitude").setValue(longitude);
        myRef.child(hazardID).child("description").setValue(desc);
        myRef.child(hazardID).child("hazardtype").setValue(problemType);

        Intent i = new Intent(HazardFormActivity.this, MapsActivity.class);
        startActivity(i);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}
