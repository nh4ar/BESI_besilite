package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RatingActivity extends AppCompatActivity {

    String base_url, intervention_endpoint, api_token;
    SharedPreferences sharedPref;
    RequestQueue netQueue;

    int r1,r2,r3,r4;
    TextView rv1, rv2, rv3, rv4;
    String[] answer = {"0","1","2","3","4","5","6","7","8","9","10"};
    TextView intervText1,intervText2,intervText3,intervText4;

    String[] intervention_string = new String[4];
    private void pullInterventionListFromServer(){
        Log.v("nh4ar", "InterventionFragment pullInterventionListFromServer() called");

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        intervention_endpoint = "/api/v1/survey/intervention/create/";


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
                    intervText1.setText(intervention_string[0]);
                    intervText2.setText(intervention_string[1]);
                    intervText3.setText(intervention_string[2]);
                    intervText4.setText(intervention_string[3]);
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
        Log.v("nh4ar", "RatingActivity onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        intervText1 = findViewById(R.id.interventionview1);
        intervText2 = findViewById(R.id.interventionview2);
        intervText3 = findViewById(R.id.interventionview3);
        intervText4 = findViewById(R.id.interventionview4);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();
        pullInterventionListFromServer();

        r1=0;
        r2=0;
        r3=0;
        r4=0;

        rv1 = findViewById(R.id.rateview1);
        rv1.setText(answer[r1%11]);
        rv2 = findViewById(R.id.rateview2);
        rv2.setText(answer[r2%11]);
        rv3 = findViewById(R.id.rateview3);
        rv3.setText(answer[r3%11]);
        rv4 = findViewById(R.id.rateview4);
        rv4.setText(answer[r4%11]);
    }

    public void incriment(View v){
        if (v.getId() == R.id.plusbutton1){
            r1++;
            r1 = checkBoundary(r1);
            rv1.setText(answer[r1%11]);
        }
        if (v.getId() == R.id.plusbutton2){
            r2++;
            r2 = checkBoundary(r2);
            rv2.setText(answer[r2%11]);

        }
        if (v.getId() == R.id.plusbutton3){
            r3++;
            r3 = checkBoundary(r3);
            rv3.setText(answer[r3%11]);

        }
        if (v.getId() == R.id.plusbutton4){
            r4++;
            r4 = checkBoundary(r4);
            rv4.setText(answer[r4%11]);
        }
    }

    public void decriment(View v){
        if (v.getId() == R.id.minusbutton1){
            r1--;
            r1 = checkBoundary(r1);
            rv1.setText(answer[r1%11]);
        }
        if (v.getId() == R.id.minusbutton2){
            r2--;
            r2 = checkBoundary(r2);
            rv2.setText(answer[r2%11]);

        }
        if (v.getId() == R.id.minusbutton3){
            r3--;
            r3 = checkBoundary(r3);
            rv3.setText(answer[r3%11]);

        }
        if (v.getId() == R.id.minusbutton4){
            r4--;
            r4 = checkBoundary(r4);
            rv4.setText(answer[r4%11]);
        }
    }
    public int checkBoundary (int num){
        if (num < 0){
            num = 0;
        } else if (num>10){
            num = 10;
        }
        return num;
    }
    public void backClick(View v){
//        Intent intent = new Intent(this.getApplicationContext(), Home.class);
//        startActivity(intent);
        finish();
    }

    public void submitClick(View v){
//        Intent intent = new Intent(this.getApplicationContext(), Home.class);
//        startActivity(intent);
        finish();

    }
}
