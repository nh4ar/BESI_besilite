package com.uva.inertia.besilite;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;


public class InterventionFragment extends Fragment {

    String base_url, intervention_endpoint, api_token, intervention_usage_endpoint;
    SharedPreferences sharedPref;
    RequestQueue netQueue;

    CheckBox intervText1,intervText2,intervText3,intervText4;
    Button backBtn, submitBtn;
    ConfirmFragment.OnConfirmClickedListener mListener;

    public static InterventionFragment newInstance() {
        Log.v("nh4ar","InterventionFragment newInstance() called");
        InterventionFragment fragment = new InterventionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public InterventionFragment() {
        // Required empty public constructor
        Log.v("nh4ar", "InterventionFragment constructor called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("nh4ar", "onCreate(" + savedInstanceState + ") called.");
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        netQueue = NetworkSingleton.getInstance(this.getContext()).getRequestQueue();

        pullInterventionListFromServer();

    }

    Boolean[] checkBoxState = new Boolean[4];
    private void readCheckBoxState(){
        Arrays.fill(checkBoxState, Boolean.FALSE);
        checkBoxState[0] = intervText1.isChecked();
        checkBoxState[1] = intervText2.isChecked();
        checkBoxState[2] = intervText3.isChecked();
        checkBoxState[3] = intervText4.isChecked();
    }

    private void submitInterventionUsage() {

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        intervention_usage_endpoint = "/api/v1/interventions/intervention-usage/create/";

        readCheckBoxState();
        JSONObject interventionObject = new JSONObject();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
        String timestamp = df.format(new Date());
        try {
//            interventionObject.put("id",3);
//            interventionObject.put("timestamp","2018-12-21T01:02:00Z");
//            interventionObject.put("Intervention1",true);
//            interventionObject.put("value1","test1");
//            interventionObject.put("Intervention2",true);
//            interventionObject.put("value2","test2");
//            interventionObject.put("Intervention3",true);
//            interventionObject.put("value3","test3");
//            interventionObject.put("Intervention4",true);
//            interventionObject.put("value4","test4");
//            interventionObject.put("deployment",1);
            interventionObject.put("timestamp",timestamp);
            interventionObject.put("Intervention1",checkBoxState[0]);
            interventionObject.put("value1",intervention_string[0]);
            interventionObject.put("Intervention2",checkBoxState[1]);
            interventionObject.put("value2",intervention_string[1]);
            interventionObject.put("Intervention3",checkBoxState[2]);
            interventionObject.put("value3",intervention_string[2]);
            interventionObject.put("Intervention4",checkBoxState[3]);
            interventionObject.put("value4",intervention_string[3]);

            Log.v("nh4ar",interventionObject.toString());
            JsonObjectRequestWithToken requestNewInterventionUsage =
                    new JsonObjectRequestWithToken(
                            Request.Method.POST,
                            base_url + intervention_usage_endpoint,
                            interventionObject, api_token, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            try{
//                                Log.v("nh4ar","***intervention usage PK = "+ response.getInt("id"));
//                            } catch (org.json.JSONException e){
//
//                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.v("nh4ar","Intervention usage "+ error.toString());
                        }
                    }); this.netQueue.add(requestNewInterventionUsage);

        } catch (JSONException e) {
            Log.e("ERROR", e.getMessage());
        }

    }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_intervention, container, false);
        Log.v("nh4ar", "InterventionFragment onCreateView() called");

        intervText1 = rootView.findViewById(R.id.checkBox_inter1);
        intervText2 = rootView.findViewById(R.id.checkBox_inter2);
        intervText3 = rootView.findViewById(R.id.checkBox_inter3);
        intervText4 = rootView.findViewById(R.id.checkBox_inter4);

        backBtn = rootView.findViewById(R.id.backFromInter);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgitationReports) getActivity()).selectPage(3);
            }
        });

        submitBtn = rootView.findViewById(R.id.submitOnInter);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitInterventionUsage();

                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
            }
        });
        return rootView;
    }

}
