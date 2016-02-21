package com.uva.inertia.besilite;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.zip.CheckedInputStream;


public class DailySurvey extends AppCompatActivity implements ConfirmFragment.OnConfirmClickedListener{

    String base_url;
    String api_token;
    String deploy_id;
    String complete_endpoint;
    String PWDEmotionSubsurvey_endpoint;


    SharedPreferences sharedPref;
    RequestQueue netQueue;

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
        setContentView(R.layout.activity_daily_survey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        deploy_id = sharedPref.getString("pref_key_deploy_id","");
        complete_endpoint = "/api/v1/survey/daily/smart";
        PWDEmotionSubsurvey_endpoint = "/api/v1/survey/emo/create/";


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void OnConfirmClicked() {
        PWDEmotionsFragment PWDEmoteFrag = (PWDEmotionsFragment)getSupportFragmentManager().findFragmentById(R.id.PWDEmotionsFrag);
        Log.v("TEST", "got a confirm click");
        HashMap<String, Boolean> PWDEmotions = PWDEmoteFrag.getEmotions();
        createPWDEmotionsSubsurvey(PWDEmotions);
        Log.v("TEST", PWDEmotions.toString());
    }


    private void createPWDEmotionsSubsurvey(HashMap<String, Boolean> hm){
        JSONObject subsurveyObject = new JSONObject(hm);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken agiSurveyCreateResponse = new JsonObjectRequestWithToken( Request.Method.POST, base_url+PWDEmotionSubsurvey_endpoint,subsurveyObject, api_token, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.v("TEST","woo it worked!");
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
                return CaregiverEmotionsFragment.newInstance();
                case 1:
                    return PWDEmotionsFragment.newInstance();
                case 2:
                    return PWDSleepFragment.newInstance();
                case 3:
                    return ConfirmFragment.newInstance(position + 1);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Caregiver Emotions";
                case 1:
                    return "PWD Emotions";
                case 2:
                    return "PWD Sleep";
                case 3:
                    return "Submit";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CaregiverEmotionsFragment extends Fragment {


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CaregiverEmotionsFragment newInstance() {
            CaregiverEmotionsFragment fragment = new CaregiverEmotionsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public CaregiverEmotionsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_caregiver_emotions_survey, container, false);

            return rootView;
        }
    }

    public static class PWDEmotionsFragment extends Fragment {

        CheckBox SadVoice;
        CheckBox Tearful;
        CheckBox LackReact;
        CheckBox VeryWorried;
        CheckBox Frightened;
        CheckBox TalkLess;
        CheckBox AppetiteLoss;
        CheckBox LessInterestInHobbies;
        CheckBox SadExpression;
        CheckBox LackOfInterest;
        CheckBox TroubleConcentrating;
        CheckBox BotheredByUsualActivities;
        CheckBox SlowMove;
        CheckBox SlowSpeech;
        CheckBox SlowReaction;
        HashMap<String, Boolean> hp = new HashMap<>();


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PWDEmotionsFragment newInstance() {
            PWDEmotionsFragment fragment = new PWDEmotionsFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public PWDEmotionsFragment() {
        }

        public HashMap<String, Boolean> getEmotions(){
            hp.put("SadVoice",SadVoice.isChecked());
            hp.put("Tearfulness",Tearful.isChecked());
            hp.put("LackReactionToPleasantEvents",LackReact.isChecked());
            hp.put("VeryWorried",VeryWorried.isChecked());
            hp.put("Frightened",Frightened.isChecked());
            hp.put("TalkLess",TalkLess.isChecked());
            hp.put("AppetiteLoss",AppetiteLoss.isChecked());
            hp.put("LessInterestInHobbies",LessInterestInHobbies.isChecked());
            hp.put("SadExpression",SadExpression.isChecked());
            hp.put("LackOfInterest",LackOfInterest.isChecked());
            hp.put("TroubleConcentrating",TroubleConcentrating.isChecked());
            hp.put("BotheredByUsualActivities",BotheredByUsualActivities.isChecked());
            hp.put("SlowMovement",SlowMove.isChecked());
            hp.put("SlowSpeech",SlowSpeech.isChecked());
            hp.put("SlowReaction",SlowReaction.isChecked());
            return hp;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pwd_emotions_survey, container, false);

            SadVoice = (CheckBox)rootView.findViewById(R.id.checkSadVoice);
            Tearful = (CheckBox)rootView.findViewById(R.id.checkTearfulness);
            LackReact = (CheckBox)rootView.findViewById(R.id.checkLackOfReact);
            VeryWorried = (CheckBox)rootView.findViewById(R.id.checkVeryWorried);
            Frightened = (CheckBox)rootView.findViewById(R.id.checkFrightened);
            TalkLess = (CheckBox)rootView.findViewById(R.id.checkLessTalk);
            AppetiteLoss = (CheckBox)rootView.findViewById(R.id.checkAppetiteLoss);
            LessInterestInHobbies = (CheckBox)rootView.findViewById(R.id.checkLessIntrest);
            SadExpression = (CheckBox)rootView.findViewById(R.id.checkSadExpression);
            LackOfInterest = (CheckBox)rootView.findViewById(R.id.checkLackOfInterest);
            TroubleConcentrating = (CheckBox)rootView.findViewById(R.id.checkTroubleConcen);
            BotheredByUsualActivities = (CheckBox)rootView.findViewById(R.id.checkBotheredByUsual);
            SlowMove = (CheckBox)rootView.findViewById(R.id.checkSlowMove);
            SlowSpeech =(CheckBox)rootView.findViewById(R.id.checkSlowSpeech);
            SlowReaction =  (CheckBox)rootView.findViewById(R.id.checkSlowReact);

            return rootView;
        }

    }

    public static class PWDSleepFragment extends Fragment {


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PWDSleepFragment newInstance() {
            PWDSleepFragment fragment = new PWDSleepFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public PWDSleepFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pwd_sleep_survey, container, false);

            return rootView;
        }
    }
}
