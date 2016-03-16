package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class AgitationReports extends AppCompatActivity implements ConfirmFragment.OnConfirmClickedListener{

    String base_url;
    String api_token;
    String deploy_id;
    String complete_endpoint;
    String EmotionEndpoint;
    String ObservationEndpoint;
    String AgitationEndpoint;


    SharedPreferences sharedPref;
    RequestQueue netQueue;

    HashMap<String, Boolean> pwdObs;
    HashMap<String, Boolean> pwdEmo;
    HashMap<String, String> pwdGen;

    java.text.DateFormat df;
    TimeZone tz;

    //int agiSurveyPK;
    int obsSurveyPK;
    int emoSurveyPK;

    TabLayout tabLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agitation_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        deploy_id = sharedPref.getString("pref_key_deploy_id", "");
        complete_endpoint = "/api/v1/survey/agi/smart/";
        ObservationEndpoint = "/api/v1/survey/obs/create/";
        EmotionEndpoint = "/api/v1/survey/emo/create/";
        AgitationEndpoint = "/api/v1/survey/agi/smart/";

        // Set up the ViewPager with the sections adapter.
        mViewPager = (noSwipeViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.BLACK,Color.BLACK);

        pwdObs = new HashMap<>();
        pwdEmo = new HashMap<>();
        pwdGen = new HashMap<>();

        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agitation_reports, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void OnConfirmClicked() {
        createReport();
    }

    public void createReport(){
        createSubsurveys_Emo();

    }


    private void createSubsurveys_Emo(){
        JSONObject subsurveyObject = new JSONObject();
        Log.v("TEST", subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewPWDEmoSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+EmotionEndpoint,subsurveyObject, api_token,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            emoSurveyPK = response.getInt("id");
                            Log.v("TEST","woo created emo subsurvey");
                            createSubsurveys_Obs();
                        } catch (org.json.JSONException e){
                            Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for emo", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDEmoSub);
    }

    private void createSubsurveys_Obs(){
        JSONObject subsurveyObject = new JSONObject(pwdObs);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewPWDSleepSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+ObservationEndpoint,subsurveyObject, api_token,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            obsSurveyPK = response.getInt("id");
                            Log.v("TEST","woo created obs subsurvey");
                            createCompleteSurvey();
                        } catch (org.json.JSONException e){
                            Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for obs", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                },NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDSleepSub);

    }

    private void createCompleteSurvey(){

        /////BACKEND DOESN'T CONTAIN PRIOR EMOTION??
            try {
                JSONObject surveyObject = new JSONObject();

                Log.v("MAPS", pwdGen.toString());

                surveyObject.put("timestamp", df.format(new Date()));
                surveyObject.put("observations", obsSurveyPK);
                surveyObject.put("PWDEmotions", emoSurveyPK);
                surveyObject.put("agitimestamp", pwdGen.get("agitimestamp"));
                surveyObject.put("level", pwdGen.get("level"));

                Log.v("TEST", surveyObject.toString());
                JsonObjectRequestWithToken postNewAgiSurvey = new JsonObjectRequestWithToken(
                        Request.Method.POST, base_url + AgitationEndpoint, surveyObject, api_token,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Agitation Report Submitted!", Toast.LENGTH_SHORT);
                                    toast.show();
                                    Log.v("TEST", "full survey made");
                                    finish();
                                } catch (Exception e) {
                                    Toast toast = Toast.makeText(getApplicationContext(),
                                            "Server failed to return a PK for obs",
                                            Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }, NetworkErrorHandlers.toastHandler(getApplicationContext()) );
                this.netQueue.add(postNewAgiSurvey);
            }
            catch(JSONException e) {
                Log.e("ERROR", e.getMessage());
            }

    }


    void selectPage(int pageIndex){
        tabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return AgiGenInfoFragment.newInstance();
                //case 1:
                  //  return RadioPWDEmotionSubsurveyFragment.newInstance();
                case 1:
                    return ObservationSubsurveyFragment.newInstance();
//                case 2:
//                    return ConfirmFragment.newInstance(position + 1);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Agitation Time";
                //case 1:
                  //  return "EMOTION";
                case 1:
                    return "Observations";
//                case 2:
//                    return "Submit";
            }
            return null;
        }
    }
}
