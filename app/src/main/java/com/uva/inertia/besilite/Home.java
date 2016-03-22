package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    SharedPreferences sharedPref;

    //Holds all activites
    ArrayList<String> agiSurveyList = new ArrayList<>();

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
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        netQueue = Volley.newRequestQueue(this);

        final ImageView goToEmotions = (ImageView) findViewById(R.id.daily_reports);
        final ImageView goToActivities = (ImageView) findViewById(R.id.go_to_activities);
        final Button goToAgiReports = (Button) findViewById(R.id.go_to_agi_report);


        goToEmotions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DailySurvey.class);
                startActivity(intent);
            }
        });

        goToAgiReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AgitationReports.class);
                startActivity(intent);
            }
        });

        goToActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddActivityBundle.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this.getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    void updateAgiList(){
//        deploy_id = sharedPref.getString("pref_key_deploy_id","");
//        base_url = sharedPref.getString("pref_key_base_url", "");
//        api_token = sharedPref.getString("pref_key_api_token","");
//        endpoint ="/api/v1/survey/agi/"+deploy_id+"/";
//
//
//        JsonArrayRequestWithToken agiSurveyRequestArray = new JsonArrayRequestWithToken(base_url+endpoint, api_token, new Response.Listener<JSONArray>() {
//
//            @Override
//            public void onResponse(JSONArray response) {
//                adapter.clear();
//                try {
//                    for (int i = 0; i < response.length(); i++) {
//                        JSONObject o = (JSONObject) response.get(i);
//                        adapter.add(o.getString("timestamp") + " | Level: " + o.getString("level") + " |  Activity: " + o.getString("activity"));
//                    }
//                } catch (JSONException e){
//                    adapter.add("Server responded with incorrect JSON");
//                }
//            }
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                adapter.clear();
//                adapter.add("Server responded with error: " + error.getLocalizedMessage());
//
//            }
//        });
//
//        this.netQueue.add(agiSurveyRequestArray);



    }
}
