package com.uva.inertia.besilite;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> agiSurveyList = new ArrayList<String>();

    //Allows us to add more items
    ArrayAdapter<String> adapter;

    String base_url;
    String endpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = Volley.newRequestQueue(this);


        final ListView mListView = (ListView) findViewById(R.id.agiSurveys);

//      Create our adapter to add items
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                agiSurveyList);
        mListView.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddAgiSurvey.class);
                startActivity(intent);
            }
        });

        final Button goToEmotions = (Button) findViewById(R.id.button);
        final Button goToActivities = (Button) findViewById(R.id.button2);
        final Button goToSettings = (Button) findViewById(R.id.settingsBtn);
        final Button refresh = (Button) findViewById(R.id.refreshBtn);


        goToEmotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GetEmotions.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAgiList();
            }
        });

        goToActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GetActivities.class);
                startActivity(intent);
            }
        });

        goToSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });




    }


    void updateAgiList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        endpoint ="api/v1/survey/agi/"+deploy_id+"/";


        JsonArrayRequestWithToken agiSurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                adapter.clear();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject o = (JSONObject) response.get(i);
                        adapter.add(o.getString("timestamp") + " | Level: " + o.getString("level") + " |  Activity: " + o.getString("activity"));
                    }
                } catch (JSONException e){
                    adapter.add("Server responded with incorrect JSON");
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                adapter.clear();
                adapter.add("Server responded with error: " + error.getLocalizedMessage());

            }
        });

        this.netQueue.add(agiSurveyRequestArray);



    }
}
