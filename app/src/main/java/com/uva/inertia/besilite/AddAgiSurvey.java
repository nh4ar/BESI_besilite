package com.uva.inertia.besilite;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddAgiSurvey extends AppCompatActivity {

    String base_url;
    String api_token;
    String deploy_id;
    String endpoint;

    SharedPreferences sharedPref;
    RequestQueue netQueue;

    EditText level;
    EditText date;
    EditText time;
    EditText prior_emote;
    EditText activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agi_survey);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = Volley.newRequestQueue(this);


        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        endpoint = "api/v1/survey/agi/create/";

        final Button submit = (Button) findViewById(R.id.submit);
        level = (EditText) findViewById(R.id.level);
        date = (EditText) findViewById(R.id.date);
        time = (EditText) findViewById(R.id.time);
        prior_emote = (EditText) findViewById(R.id.prior_emote);
        activity = (EditText) findViewById(R.id.activity);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_survey();
            }
        });
    }

    void send_survey(){
        JSONObject surveyObject = new JSONObject();
        try{
            surveyObject.put("level",Integer.decode(level.getText().toString()));
            surveyObject.put("timestamp",date.getText()+"T"+time.getText());
            surveyObject.put("prior_emotion", Integer.decode(prior_emote.getText().toString()));
            surveyObject.put("activity",Integer.decode(activity.getText().toString()));

        } catch (JSONException e){

        }

        JsonObjectRequestWithToken agiSurveyCreateResponse = new JsonObjectRequestWithToken( Request.Method.POST, base_url+endpoint,surveyObject, api_token, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String err_msg = new String(error.networkResponse.data);
                Log.e("ERROR", err_msg);
                Toast toast = Toast.makeText(getApplicationContext(), err_msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        this.netQueue.add(agiSurveyCreateResponse);
    }
}
