package io.paperplane.rajb.cleanvironment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

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

    }
}
