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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class InterventionFragment extends Fragment {

    String base_url, intervention_endpoint, api_token;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_intervention, container, false);
        Log.v("nh4ar", "InterventionFragment onCreateView() called");

        intervText1 = (CheckBox) rootView.findViewById(R.id.checkBox_inter1);
        intervText2 = (CheckBox) rootView.findViewById(R.id.checkBox_inter2);
        intervText3 = (CheckBox) rootView.findViewById(R.id.checkBox_inter3);
        intervText4 = (CheckBox) rootView.findViewById(R.id.checkBox_inter4);

        backBtn = (Button) rootView.findViewById(R.id.backFromInter);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgitationReports) getActivity()).selectPage(3);
            }
        });

        submitBtn = (Button) rootView.findViewById(R.id.submitOnInter);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
            }
        });
        return rootView;
    }

}
