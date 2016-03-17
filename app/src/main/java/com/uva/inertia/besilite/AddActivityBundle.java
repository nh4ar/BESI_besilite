package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class AddActivityBundle extends AppCompatActivity{

    Button addNew;
    Button submit;

    SharedPreferences sharedPref;

    //Allows us to add more items
    CustomAdapter adapter;
    ArrayList<String> tempList;
    //Holds all activity name strings
    CaseInsensitiveArrayList ActivityList;
    ArrayList<CheckboxListViewItem> ConvertedList;

    Map<String,String> ActivityMap = new HashMap<>();
    Map<String,String> RevActivityMap = new HashMap<>();
    String selectedActivity;

    String base_url;
    String endpoint;
    String activityEndpoint;
    String api_token;
    String deploy_id;
    RequestQueue netQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_activity_bundle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        addNew = (Button)findViewById(R.id.add_new_activity_button);
        submit = (Button)findViewById(R.id.submit_activity_bundle);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewActivityDialogFrag newAct = AddNewActivityDialogFrag.newInstance("newAct");
                newAct.show(getFragmentManager(), "fragment_add_activity");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        final ListView mListView = (ListView) findViewById(R.id.activity_type_listview);
        tempList = new ArrayList<>();

//      Create our adapter to add items
        ActivityList  = new CaseInsensitiveArrayList();
        ConvertedList = generateCheckboxes(ActivityList);
        adapter       = new CustomAdapter(this, ConvertedList);

        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mListView.setSelector(R.color.pressed_color);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                mListView.setItemChecked(position, true);
                selectedActivity = RevActivityMap.get(adapter.getItem(position));
            }
        });

        mListView.setAdapter(adapter);
        getActivityList();
    }

    public ArrayList<CheckboxListViewItem> generateCheckboxes(CaseInsensitiveArrayList ar){
        ArrayList<CheckboxListViewItem> ret = new ArrayList<>();
        for (String s: ar){
            ret.add(new CheckboxListViewItem(s, 0));
        }
        return ret;
    }

    public void onFinishNewActDialog(String temp){
        if (ActivityList.contains(temp)){
            Context context = getApplicationContext();
            CharSequence text = "This field already exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else if (temp.trim().length() == 0){
            Context context = getApplicationContext();
            CharSequence text = "There is nothing typed in";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            submitNewActivity(temp);
        }
        Log.v("ACTIVITY", temp);
    }



    void submitNewActivity(String newActivity){
        endpoint = "/api/v1/survey/fields/smart/a/";
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        try{
            JSONObject surveyObject  = new JSONObject();
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyObject.put("timestamp", timestamp);
            surveyObject.put("value", newActivity);

            JsonObjectRequestWithToken requestNewActivitySurvey =
                    new JsonObjectRequestWithToken(
                            Request.Method.POST,
                            base_url+endpoint,
                            surveyObject, api_token, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                int pk = response.getInt("pk");
                                Log.v("TEST","pk for new complete survey is: "+pk);
                                getActivityList();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "New Activity Added", Toast.LENGTH_SHORT);
                                toast.show();
                                //newActivVal.setText("");
                                //finish();
                            } catch (org.json.JSONException e){
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Server failed to return a PK for complete survey",
                                        Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

            this.netQueue.add(requestNewActivitySurvey);

        } catch (org.json.JSONException e){
            Log.e("TEST","Something went very wrong creating survey object");
        }
    }

    void getActivityList(){
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token","");
        activityEndpoint="/api/v1/survey/fields/smart/a/";

        adapter.clear();
        tempList.clear();

        JsonArrayRequestWithToken activityListRequestArray = new JsonArrayRequestWithToken(base_url+activityEndpoint, api_token, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("Test", resp.toString());
                try {
                    for (int i = 0; i < resp.length(); i++) {
                        JSONObject o = (JSONObject) resp.get(i);
                        ActivityMap.put(o.getString("pk"),o.getString("value"));
                        RevActivityMap.put(o.getString("value"), o.getString("pk"));
                        tempList.add(o.getString("value"));
                    }

                    Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
                    Log.v("MAPS", tempList.toString());
                    for (String s: tempList){
                        //adapter.add(s);
                    }
                } catch (JSONException e) {
                    Log.e("ERROR", "Server responded with incorrect JSON");
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERROR", "Unable to connect to server. Please check your internet connection");
            }
        });

        this.netQueue.add(activityListRequestArray);
    }

}