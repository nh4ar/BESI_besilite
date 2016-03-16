package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class AddActivityBundle extends AppCompatActivity{

    Button addNew;
    Button submit;
    String newActivityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity_bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addNew = (Button)findViewById(R.id.add_new_activity_button);
        submit = (Button)findViewById(R.id.submit_activity_bundle);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewActivityDialogFrag newAct = AddNewActivityDialogFrag.newInstance("newAct");
                newAct.show(getFragmentManager(), "fragment_add_activity");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onFinishNewActDialog(String text){
        if (text.length() < 1){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Cannot submit empty field", Toast.LENGTH_SHORT);
            toast.show();
        }
        Log.v("ACTIVITY", text);
    }
}