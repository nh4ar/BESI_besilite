package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ScheduleActivity extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> ScheduleList = new ArrayList<>();
    ArrayList<Integer> pkList = new ArrayList<>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;


    //Holds all activity name strings
    ArrayList<String> ActivityList = new ArrayList<>();

    Map<String,String> ActivityIndexer = new HashMap<>();
    Map<String,String> ActivityMap = new HashMap<>();
    Map<String,String> TimeMap = new HashMap<>();

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.AppTheme_Theme_Styled_ActionBar_TitleTextStyle);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();


        final ListView mListView = (ListView) findViewById(R.id.scheduleEvents);

//      Create our adapter to add items
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ScheduleList);

        mListView.setAdapter(adapter);

        updateEventList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddNewActivity.class);
                startActivityForResult(intent, 90);
            }
        });

    }

    /* Called when the second activity's finished */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch(requestCode) {
//            case 90:
//                if (resultCode == RESULT_OK) {\
        Log.v("CHECK POSITION", "" + requestCode);
        updateEventList();
//                }
//                break;
//        }
    }

    void updateEventList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint ="/api/v1/survey/activ/smart/recent/";
        activityEndpoint="/api/v1/survey/fields/smart/a/";

//        Toast toast = Toast.makeText(getApplicationContext(),
//                "New Event", Toast.LENGTH_SHORT);
//        toast.show();

        JsonArrayRequestWithToken activitySurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                adapter.clear();
                pkList.clear();
                TimeMap.clear();
                ActivityIndexer.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = (JSONObject) response.get(i);
                        Log.v("Maps", o.toString());
                        TimeMap.put(o.getString("pk"), o.getString("acttimestamp"));
                        ActivityIndexer.put(o.getString("pk"), o.getString("activity"));
                        pkList.add(o.getInt("pk"));
                    }
                    Log.v("Maps", TimeMap.toString());
                    Log.v("Maps", ActivityIndexer.toString());
                    JsonArrayRequestWithToken activityListRequestArray = callActivityAdapterPull();
                    netQueue.add(activityListRequestArray);
                }
                catch (JSONException e){
                    Log.v("ERROR", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.clear();
                adapter.add("Unable to connect to server. Please check your internet connection");
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
                    Log.v("RESP", "" + resp.length());
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject o = (JSONObject) resp.get(i);
                        Log.v("Activity", o.toString());
                        ActivityMap.put(o.getString("pk"),o.getString("value"));
                        Log.v("Activity", ActivityMap.toString());
                    }
                    Log.v("Maps", ActivityMap.toString());
                    postProcess();
                } catch (JSONException e) {
                    ActivityList.add("Server responded with incorrect JSON");
                }
            }

        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));
    }

    public void postProcess() {
        try
        {//2016-02-23T07:16:00Z

            java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            String dateString;
            java.text.DateFormat dprint = java.text.DateFormat.getDateTimeInstance(
                    DateFormat.SHORT,DateFormat.SHORT);
            dprint.setTimeZone(TimeZone.getDefault());
            Date d;

            for(int i: pkList) {
                try {
                    dateString = TimeMap.get(""+i).replace("Z", "GMT+00:00");
                    d = df.parse(dateString);
                    adapter.add(dprint.format(d) + " |  Activity: " +
                            ActivityMap.get(ActivityIndexer.get(""+i)));
                } catch(java.text.ParseException e){
                    Log.e("PARSING ERROR", e.getMessage());
                }
            }
        }
        catch(Exception e)
        {
            adapter.add(e.getMessage());
        }
    }

}
