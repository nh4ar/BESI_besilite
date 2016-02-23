package com.uva.inertia.besilite;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScheduleActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> ScheduleList = new ArrayList<>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;


    //Holds all activity name strings
    ArrayList<String> ActivityList = new ArrayList<>();

    Map<String,String> ActivityMap = new HashMap<>();

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    JSONArray RawEventList;
    RequestQueue netQueue;


    public void postProcess() {
        try
        {//2016-02-23T07:16:00Z

            java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            String dateString;
            java.text.DateFormat dprint = java.text.DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,DateFormat.SHORT);
            Date d;
            for (int i = 0; i < RawEventList.length(); i++) {
                JSONObject o = (JSONObject) RawEventList.get(i);
                try {
                    dateString = o.getString("acttimestamp").replace("Z", "GMT+00:00");
                    d = df.parse(dateString);
                    adapter.add(dprint.format(d) + " |  Activity: " +
                            ActivityMap.get(o.getString("activity")));
                } catch(java.text.ParseException e){
                    Log.e("PARSING ERROR", e.getMessage());
                }
            }
        }
        catch(JSONException e)
        {
            adapter.add("Server responded with incorrect JSON");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        final ListView mListView = (ListView) findViewById(R.id.scheduleEvents);

//      Create our adapter to add items
        adapter=new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ScheduleList);

        mListView.setAdapter(adapter);

        updateEventList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddNewActivity.class);
                startActivity(intent);
            }
        });

    }

    void updateEventList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint ="/api/v1/survey/activ/smart/";
        activityEndpoint="/api/v1/survey/fields/smart/a";

        JsonArrayRequestWithToken activitySurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                adapter.clear();
                JsonArrayRequestWithToken activityListRequestArray = callActivityAdapterPull();
                netQueue.add(activityListRequestArray);
                RawEventList = response;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.clear();
                adapter.add("Server responded with error: " + error.getLocalizedMessage());
            }
        });
        this.netQueue.add(activitySurveyRequestArray);
    }

    JsonArrayRequestWithToken callActivityAdapterPull(){
        return new JsonArrayRequestWithToken(base_url+activityEndpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("Test", resp.toString());
                try {
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject o = (JSONObject) resp.get(i);
                        ActivityMap.put(o.getString("pk"),o.getString("value"));
                    }
                    postProcess();
                } catch (JSONException e) {
                    ActivityList.add("Server responded with incorrect JSON");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ActivityList.add("Server responded with error: " + error.getLocalizedMessage());
            }
        });
    }
}
