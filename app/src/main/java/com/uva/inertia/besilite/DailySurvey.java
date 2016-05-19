package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;


public class DailySurvey extends AppCompatActivity implements ConfirmFragment.OnConfirmClickedListener{

    String base_url;
    String api_token;
    String deploy_id;
    String complete_endpoint;
    String PWDEmotionSubsurvey_endpoint;
    String CaregiverEmotionSubsurvey_endpoint;
    String PWDSleepSubsurvey_endpoint;

    SharedPreferences sharedPref;
    RequestQueue netQueue;

    HashMap<String, Boolean> pwdEmotions;
    HashMap<String, Boolean> caregiverEmotions;
    HashMap<String, Boolean> pwdSleepQal;

    int pwdEmotionSurveyPK;
    int pwdSleepSurveyPK;
    int careEmotionSurveyPK;

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
    private noSwipeViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_survey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        netQueue = NetworkSingleton.getInstance(getApplicationContext()).getRequestQueue();

        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        deploy_id = sharedPref.getString("pref_key_deploy_id", "");
        complete_endpoint = "/api/v1/survey/daily/smart/";
        PWDEmotionSubsurvey_endpoint = "/api/v1/survey/emo/create/";
        CaregiverEmotionSubsurvey_endpoint = "/api/v1/survey/care-emo/create/";
        PWDSleepSubsurvey_endpoint = "/api/v1/survey/slp/create/";

        pwdEmotions = new HashMap<>();
        pwdSleepQal = new HashMap<>();
        caregiverEmotions = new HashMap<>();


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (noSwipeViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabTextColors(Color.BLACK,Color.BLACK);
    }



    @Override
    public void OnConfirmClicked() {
        createSurveys();
    }

    private String dumpSurveyToFile(){
        String uuid = UUID.randomUUID().toString();
        JSONObject surveyDump = new JSONObject();
        JSONObject pwdEmoSubsurvey = new JSONObject(pwdEmotions);
        JSONObject careEmoSubsurvey = new JSONObject(caregiverEmotions);
        JSONObject pwdSleepSubsurvey = new JSONObject(pwdSleepQal);
        try {
            //survey data
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyDump.put("timestamp", timestamp);
            surveyDump.put("pwdEmoSubsurvey", pwdEmoSubsurvey);
            surveyDump.put("careEmoSubsurvey",careEmoSubsurvey);
            surveyDump.put("pwdSleepSubsurvey", pwdSleepSubsurvey);

            //file type for parser
            surveyDump.put("filetype", "dailyReport");
        } catch (JSONException e){
            Log.e("ERROR", e.getMessage());
        }

        String filename = uuid;
        FileHelpers.writeStringToInternalStorage(surveyDump.toString(),"offline",filename,getApplicationContext());

        return uuid;
    }

    public void createSurveys(){
        String uuid = dumpSurveyToFile();
        createSubsurveys_PWDEmotions(uuid);
        finish();
    }

    private void createSubsurveys_PWDEmotions(final String uuid){
        Log.v("DAILYSURVEY", pwdEmotions.toString());
        JSONObject subsurveyObject = new JSONObject(pwdEmotions);
        Log.v("TEST",subsurveyObject.toString());
        Log.v("DAILYSURVEY", pwdEmotions.toString());
        JsonObjectRequestWithToken requestNewPWDEmotionSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+PWDEmotionSubsurvey_endpoint,subsurveyObject,
                api_token, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    pwdEmotionSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created pwd emotions!");
                    createSubsurveys_CaregiverEmotions(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for PWDEmotions", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDEmotionSub);
    }

    private void createSubsurveys_CaregiverEmotions(final String uuid){
        JSONObject subsurveyObject = new JSONObject(caregiverEmotions);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewCareEmotionSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+CaregiverEmotionSubsurvey_endpoint,
                subsurveyObject,
                api_token,
                new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    careEmotionSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created care emotions!");
                    createSubsurveys_PWDSleep(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for Caregiver Emotions", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewCareEmotionSub);
    }

    private void createSubsurveys_PWDSleep(final String uuid){
        JSONObject subsurveyObject = new JSONObject(pwdSleepQal);
        Log.v("TEST",subsurveyObject.toString());
        JsonObjectRequestWithToken requestNewPWDSleepSub = new JsonObjectRequestWithToken(
                Request.Method.POST, base_url+PWDSleepSubsurvey_endpoint,subsurveyObject, api_token,
        new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    pwdSleepSurveyPK = response.getInt("id");
                    Log.v("TEST","woo created pwd sleep!");
                    createCompleteSurvey(uuid);
                } catch (org.json.JSONException e){
                    Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for PWDSleep", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

        this.netQueue.add(requestNewPWDSleepSub);
    }

    private void createCompleteSurvey(final String file_uuid){
        try{
            JSONObject surveyObject  = new JSONObject();
            //get current time in iso8601
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            df.setTimeZone(tz);
            String timestamp = df.format(new Date());
            surveyObject.put("timestamp", timestamp);
            surveyObject.put("caregiverEmotions",careEmotionSurveyPK);
            surveyObject.put("PWDEmotions",pwdEmotionSurveyPK);
            surveyObject.put("PWDSleepEvents",pwdSleepSurveyPK);

            Log.v("DAILYSURVEY", pwdEmotions.toString());

            JsonObjectRequestWithToken requestNewCompleteSurvey = new JsonObjectRequestWithToken( Request.Method.POST, base_url+complete_endpoint,surveyObject, api_token, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try{
                        int pk = response.getInt("pk");
                        Log.v("TEST","pk for new complete survey is: "+pk);
                        Toast toast = Toast.makeText(getApplicationContext(), "Daily Report Submitted", Toast.LENGTH_SHORT);
                        toast.show();

                        FileHelpers.deleteFileFromInternalStorage("offline",file_uuid,getApplicationContext());

                        finish();
                    } catch (org.json.JSONException e){
                        Toast toast = Toast.makeText(getApplicationContext(), "Server failed to return a PK for complete survey", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }, NetworkErrorHandlers.toastHandler(getApplicationContext()));

            this.netQueue.add(requestNewCompleteSurvey);

        } catch (org.json.JSONException e){
            Log.e("TEST","Something went very wrong creating survey object");
        }


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
                //case 3:
                  //  return ConfirmFragment.newInstance(position + 1);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Caregiver";
                case 1:
                    return "Participant Mood";
                case 2:
                    return "Participant Sleep";
                //case 3:
                  //  return "Submit";
            }
            return null;
        }
    }


    public static View.OnClickListener updateMapOnClick(final HashMap<String, Boolean> hm, final String key){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox c = (CheckBox)v;
                hm.put(key,c.isChecked());
                Log.v("DAILYSURVEY","Click handler called from: " + hm.toString());
            }
        };
    }

    void selectPage(int pageIndex){
        tabLayout.setScrollPosition(pageIndex,0f,true);
        mViewPager.setCurrentItem(pageIndex);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class CaregiverEmotionsFragment extends Fragment {

        CheckBox Isolated;
        CheckBox Exhausted;
        CheckBox Worried;
        CheckBox Frustrated;
        CheckBox Discouraged;
        CheckBox Rested;
        CheckBox Busy;
        CheckBox HangingInThere;
        CheckBox Okay;
        CheckBox Calm;
        CheckBox Satisfied;
        CheckBox Hopeful;
        CheckBox Motivated;
        CheckBox Confident;
        CheckBox InControl;

        DailySurvey ds;

        Button nextButton;


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
            View rootView = inflater.inflate(R.layout.fragment_caregiver_emotions_survey,
                    container, false);

            ds = (DailySurvey) getActivity();
            HashMap<String, Boolean> cEmo = ds.caregiverEmotions;

            Isolated = (CheckBox) rootView.findViewById(R.id.checkIsolated);
            Isolated.setOnClickListener(updateMapOnClick(cEmo,"Isolated"));

            Exhausted = (CheckBox) rootView.findViewById(R.id.checkExhausted);
            Exhausted.setOnClickListener(updateMapOnClick(cEmo,"Exhausted"));

            Worried = (CheckBox) rootView.findViewById(R.id.checkWorried);
            Worried.setOnClickListener(updateMapOnClick(cEmo,"Worried"));

            Frustrated = (CheckBox) rootView.findViewById(R.id.checkFrustrated);
            Frustrated.setOnClickListener(updateMapOnClick(cEmo,"Frustrated"));

            Discouraged = (CheckBox) rootView.findViewById(R.id.checkDiscouraged);
            Discouraged.setOnClickListener(updateMapOnClick(cEmo,"Discouraged"));

            Rested = (CheckBox) rootView.findViewById(R.id.checkRested);
            Rested.setOnClickListener(updateMapOnClick(cEmo,"Rested"));

            Busy = (CheckBox) rootView.findViewById(R.id.checkBusy);
            Busy.setOnClickListener(updateMapOnClick(cEmo,"Busy"));

            HangingInThere = (CheckBox) rootView.findViewById(R.id.checkHangingInThere);
            HangingInThere.setOnClickListener(updateMapOnClick(cEmo,"HangingInThere"));

            Okay = (CheckBox) rootView.findViewById(R.id.checkOkay);
            Okay.setOnClickListener(updateMapOnClick(cEmo,"Okay"));

            Calm = (CheckBox) rootView.findViewById(R.id.checkCalm);
            Calm.setOnClickListener(updateMapOnClick(cEmo,"Calm"));

            Satisfied = (CheckBox) rootView.findViewById(R.id.checkSatisfied);
            Satisfied.setOnClickListener(updateMapOnClick(cEmo,"Satisfied"));

            Hopeful = (CheckBox) rootView.findViewById(R.id.checkHopeful);
            Hopeful.setOnClickListener(updateMapOnClick(cEmo,"Hopeful"));

            Motivated  = (CheckBox) rootView.findViewById(R.id.checkMotivated );
            Motivated.setOnClickListener(updateMapOnClick(cEmo, "Motivated"));

            Confident = (CheckBox) rootView.findViewById(R.id.checkConfident);
            Confident.setOnClickListener(updateMapOnClick(cEmo,"Confident"));

            InControl = (CheckBox) rootView.findViewById(R.id.checkInControl);
            InControl.setOnClickListener(updateMapOnClick(cEmo,"InControl"));

            nextButton = (Button) rootView.findViewById(R.id.caregiver_emo_next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DailySurvey) getActivity()).selectPage(1);
                }
            });


            return rootView;
        }
    }

    public static class PWDEmotionsFragment extends Fragment {

        CheckBox ShortTempered;
        CheckBox Tearful;
        CheckBox LackReact;
        CheckBox VeryWorried;
        CheckBox Frightened;
        CheckBox TalkLess;
        CheckBox AppetiteLoss;
        CheckBox LessInterest;
        CheckBox SadExpression;
        CheckBox Suicidal;
        CheckBox TroubleConcentrating;
        CheckBox LackEnergy;
        CheckBox SlowMove;
        CheckBox SlowSpeech;
        CheckBox PhysicalComplaints;
        CheckBox LowEsteem;
        CheckBox WorstAnticipate;
        Button getChecks;
        Button nextButton;

        HashMap<String, Boolean> hp = new HashMap<>();
        DailySurvey dailysurvey;

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

        public void updateHashMap(){
            hp.put("ShortTempered", ShortTempered.isChecked());
            hp.put("Tearfulness",Tearful.isChecked());
            hp.put("LackReactionToPleasantEvents",LackReact.isChecked());
            hp.put("VeryWorried",VeryWorried.isChecked());
            hp.put("Frightened",Frightened.isChecked());
            hp.put("TalkLess",TalkLess.isChecked());
            hp.put("AppetiteLoss",AppetiteLoss.isChecked());
            hp.put("LessIntrest",LessInterest.isChecked());
            hp.put("SadExpression",SadExpression.isChecked());
            hp.put("Suicidal",Suicidal.isChecked());
            hp.put("TroubleConcentrating",TroubleConcentrating.isChecked());
            hp.put("LackEnergy",LackEnergy.isChecked());
            hp.put("SlowMovement",SlowMove.isChecked());
            hp.put("SlowSpeech",SlowSpeech.isChecked());
            hp.put("PhysicalComplaints", PhysicalComplaints.isChecked());
            hp.put("LowEsteem",LowEsteem.isChecked());
            hp.put("WorstAnticipate",WorstAnticipate.isChecked());
            dailysurvey.pwdEmotions = hp;
        }

        @Override
        public void onPause(){
            Log.v("PAUSE","paused");
            super.onPause();

        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pwd_emotions_survey, container, false);

            dailysurvey = (DailySurvey) getActivity();

            ShortTempered = (CheckBox)rootView.findViewById(R.id.checkShortTempered);
            ShortTempered.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"ShortTempered"));

            Tearful = (CheckBox)rootView.findViewById(R.id.checkTearfulness);
            Tearful.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"Tearfulness"));

            LackReact = (CheckBox)rootView.findViewById(R.id.checkLackOfReact);
            LackReact.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"LackReactionToPleasantEvents"));

            VeryWorried = (CheckBox)rootView.findViewById(R.id.checkVeryWorried);
            VeryWorried.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"VeryWorried"));

            Frightened = (CheckBox)rootView.findViewById(R.id.checkFrightened);
            Frightened.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"Frightened"));

            TalkLess = (CheckBox)rootView.findViewById(R.id.checkLessTalk);
            TalkLess.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"TalkLess"));

            AppetiteLoss = (CheckBox)rootView.findViewById(R.id.checkAppetiteLoss);
            AppetiteLoss.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"AppetiteLoss"));

            LessInterest = (CheckBox)rootView.findViewById(R.id.checkLessInterest);
            LessInterest.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"LessInterestInHobbies"));

            SadExpression = (CheckBox)rootView.findViewById(R.id.checkSadExpression);
            SadExpression.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"SadExpression"));

            Suicidal = (CheckBox)rootView.findViewById(R.id.checkSuicidal);
            Suicidal.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "Suicidal"));

            TroubleConcentrating = (CheckBox)rootView.findViewById(R.id.checkTroubleConcen);
            TroubleConcentrating.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "TroubleConcentrating"));

            LackEnergy = (CheckBox)rootView.findViewById(R.id.checkLackEnergy);
            LackEnergy.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "LackEnergy"));

            SlowMove = (CheckBox)rootView.findViewById(R.id.checkSlowMove);
            SlowMove.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "SlowMovement"));

            SlowSpeech =(CheckBox)rootView.findViewById(R.id.checkSlowSpeech);
            SlowSpeech.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "SlowSpeech"));

            PhysicalComplaints =  (CheckBox)rootView.findViewById(R.id.checkPhysicalComplaints);
            PhysicalComplaints.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "PhysicalComplaints"));

            LowEsteem =(CheckBox)rootView.findViewById(R.id.checkLowEsteem);
            LowEsteem.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions, "LowEsteem"));

            WorstAnticipate = (CheckBox)rootView.findViewById(R.id.checkWorstAnticipate);
            WorstAnticipate.setOnClickListener(updateMapOnClick(dailysurvey.pwdEmotions,"WorstAnticipate"));

            updateHashMap();

            nextButton = (Button) rootView.findViewById(R.id.pwd_mood_next);
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DailySurvey) getActivity()).selectPage(2);
                    Log.v("DAILYSURVEY", ((DailySurvey) getActivity()).pwdEmotions.toString());
                }
            });

            return rootView;

        }

    }

    public static class PWDSleepFragment extends Fragment {

        DailySurvey ds;

        CheckBox multiBathroom;
        CheckBox badDreams;
        CheckBox moreNaps;
        CheckBox diffFallingAsleep;
        CheckBox wakeUpFreq;
        CheckBox wakeUpEarly;
        CheckBox restlessOveractive;
        Button submitButton;

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
            ds = (DailySurvey) getActivity();

            Log.v("DAILYSURVEY", ds.pwdEmotions.toString());

            HashMap<String, Boolean> slpQ = ds.pwdSleepQal;

            multiBathroom =(CheckBox)rootView.findViewById(R.id.checkMultipleBathroomVisits);
            multiBathroom.setOnClickListener(updateMapOnClick(slpQ,"MultipleBathroomVisits"));

            badDreams =(CheckBox)rootView.findViewById(R.id.checkBadDreams);
            badDreams.setOnClickListener(updateMapOnClick(slpQ,"BadDreams"));

            moreNaps =(CheckBox)rootView.findViewById(R.id.checkMoreNaps);
            moreNaps.setOnClickListener(updateMapOnClick(slpQ,"MoreNaps"));

            diffFallingAsleep =(CheckBox)rootView.findViewById(R.id.checkDifficultyFallingAsleep);
            diffFallingAsleep.setOnClickListener(updateMapOnClick(slpQ,"DifficultyFallingAsleep"));

            wakeUpFreq =(CheckBox)rootView.findViewById(R.id.checkWakeUpFrequently);
            wakeUpFreq.setOnClickListener(updateMapOnClick(slpQ,"WakeUpFrequently"));

            wakeUpEarly =(CheckBox)rootView.findViewById(R.id.checkWakeUpEarly);
            wakeUpEarly.setOnClickListener(updateMapOnClick(slpQ,"WakeUpEarly"));

            restlessOveractive =(CheckBox)rootView.findViewById(R.id.checkRestlessOveractive);
            restlessOveractive.setOnClickListener(updateMapOnClick(slpQ,"RestlessOveractive"));

            submitButton = (Button) rootView.findViewById(R.id.daily_survey_submit);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DailySurvey) getActivity()).createSurveys();
                }
            });

            return rootView;
        }
    }



}

