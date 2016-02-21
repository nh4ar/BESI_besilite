package com.uva.inertia.besilite;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity {

    Date evtDate;
    Time evtTime;


    SharedPreferences sharedPref;

    //Allows us to add more items
    ArrayAdapter<String> adapter;
    //Holds all activity name strings
    ArrayList<String> ActivityList = new ArrayList<String>();

    Map<String,String> ActivityMap = new HashMap<String, String>();

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    JSONArray RawActivityList;
    RequestQueue netQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        final ListView mListView = (ListView) findViewById(R.id.actionList);

//      Create our adapter to add items
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, ActivityList);

        mListView.setAdapter(adapter);
        getActivityList();
    }

    void getActivityList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        activityEndpoint="/api/v1/survey/fields/smart/a";

        JsonArrayRequestWithToken activityListRequestArray = new JsonArrayRequestWithToken(base_url+activityEndpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("Test", resp.toString());
                try {
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject o = (JSONObject) resp.get(i);
                        ActivityMap.put(o.getString("pk"),o.getString("value"));
                        adapter.add(o.getString("value"));
                    }
                } catch (JSONException e) {
                    adapter.add("Server responded with incorrect JSON");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.add("Server responded with error: " + error.getLocalizedMessage());
            }
        });

        this.netQueue.add(activityListRequestArray);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

        }
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

}