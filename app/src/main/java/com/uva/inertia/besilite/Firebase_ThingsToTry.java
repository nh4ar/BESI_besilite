package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Firebase_ThingsToTry extends AppCompatActivity {

    TextView intervText1,intervText2,intervText3,intervText4;
    String base_url, api_token, intervention_endpoint;
    SharedPreferences sharedPref;
    RequestQueue netQueue;

    String[] intervention_string = new String[4];
    private void pullInterventionListFromServer(){
        Log.v("nh4ar", "InterventionFragment pullInterventionListFromServer() called");

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        intervention_endpoint = "/api/v1/interventions/intervention/create/";


        JsonArrayRequestWithToken interventionListRequestArray = new JsonArrayRequestWithToken(base_url + intervention_endpoint, api_token, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("nh4ar","OnResponse Intervention: " +response.toString());
                if (response.length()>=4){ //need at least 4 interventions
                    for(int i=0; i<4; i++){
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            intervention_string[i] = jsonObject.getString("value");
                            Log.v("nh4ar","OnResponse Intervention: " +intervention_string[i]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } //end for
//                    Log.v("nh4ar","****string = "+ intervention_string[0]);
                    intervText1.setText("\u2022 " +intervention_string[0]);
                    intervText2.setText("\u2022 " +intervention_string[1]);
                    intervText3.setText("\u2022 " +intervention_string[2]);
                    intervText4.setText("\u2022 " +intervention_string[3]);
                }


//                Log.v("nh4ar","Intervention#1: " +interv1_string);
//                Log.v("nh4ar","Intervention#1: " +response.length());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("nh4ar","InterventionList"+ error.toString());
            }
        });
        this.netQueue.add(interventionListRequestArray);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase__things_to_try);

        intervText1 = findViewById(R.id.thingstotry1);
        intervText2 = findViewById(R.id.thingstotry2);
        intervText3 = findViewById(R.id.thingstotry3);
        intervText4 = findViewById(R.id.thingstotry4);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        pullInterventionListFromServer();

        final Button okBtn = (Button) findViewById(R.id.okBtnOnThingstotry);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
