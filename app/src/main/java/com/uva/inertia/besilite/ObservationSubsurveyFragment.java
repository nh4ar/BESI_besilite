package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import java.util.HashMap;


public class ObservationSubsurveyFragment extends android.support.v4.app.Fragment {

    AgitationReports ar;
    HashMap<String, Boolean> pwdObs;

    CheckBox Restlessness;
    CheckBox Ambulating;
    CheckBox Touching;
    CheckBox Clothing;
    CheckBox Physical1;
    CheckBox Physical2;
    CheckBox OralFixation;
    CheckBox Repetition;
    CheckBox Vocal1;
    CheckBox Vocal2;
    CheckBox Lost;
    CheckBox Withdrawn;
    CheckBox Annoying;
    CheckBox Shadowing;
    CheckBox Communication;

    ConfirmFragment.OnConfirmClickedListener mListener;

    SharedPreferences sharedPref;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ObservationSubsurveyFragment newInstance() {
        ObservationSubsurveyFragment fragment = new ObservationSubsurveyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ObservationSubsurveyFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_observation_subsurvey, container, false);
        ar = (AgitationReports) getActivity();

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                // Optional - Defines what protocol used to send events
                .build();


        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "agiRepObservations", "com.uva.inertia.besilite", getContext())
                .base64(false)
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Agitation Report Observation Survey")
                .id("agiReportObsSurvey")
                .build());


        ///////////////////////////////////////////////////////////////////////////////////////////

        Button backBtn = (Button) rootView.findViewById(R.id.backFromObs);

        backBtn.setOnClickListener(new View.OnClickListener()   {
            @Override
            public void onClick(View v)
            {
                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject subject = new Subject.SubjectBuilder().build();
                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportObservations", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Observation -> Back")
                        .id("agitationReportObsBackButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                //
                ((AgitationReports) getActivity()).selectPage(0);
            }
        });

        Button confirmBtn = (Button) rootView.findViewById(R.id.submitOnObs);

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////////////////////////Android Analytics Tracking Code////////////////////////////////////
                // Create an Emitter
                Emitter emitter = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                        .method(HttpMethod.POST) // Optional - Defines how we send the request
                        .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST
                        // Optional - Defines what protocol used to send events
                        .build();

                Subject subject = new Subject.SubjectBuilder().build();
                subject.setUserId(sharedPref.getString("pref_key_api_token", ""));
                // Make and return the Tracker object
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportObservations", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Observation -> Submit")
                        .id("agitationReportObsSubmitButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
            }
        });

        pwdObs = ar.pwdObs;

        Restlessness = (CheckBox) rootView.findViewById(R.id.checkRestlessness);
        Restlessness.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Frustration"));
        Ambulating = (CheckBox) rootView.findViewById(R.id.checkAmbulating);
        Ambulating.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Ambulation"));
        Touching = (CheckBox) rootView.findViewById(R.id.checkTouching);
        Touching.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Touching"));
        Clothing = (CheckBox) rootView.findViewById(R.id.checkClothing);
        Clothing.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Clothing"));
        Physical1 = (CheckBox) rootView.findViewById(R.id.checkPhysical1);
        Physical1.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Physical1"));
        Physical2 = (CheckBox) rootView.findViewById(R.id.checkPhysical2);
        Physical2.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Physical2"));
        OralFixation = (CheckBox) rootView.findViewById(R.id.checkOralFixation);
        OralFixation.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "OralFixation"));
        Repetition = (CheckBox) rootView.findViewById(R.id.checkRepetition);
        Repetition.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Repetition"));
        Repetition = (CheckBox) rootView.findViewById(R.id.checkRepetition);
        Repetition.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Repetition"));
        Vocal1 = (CheckBox) rootView.findViewById(R.id.checkVocal1);
        Vocal1.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Vocal1"));
        Vocal2 = (CheckBox) rootView.findViewById(R.id.checkVocal2);
        Vocal2.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Vocal2"));
        Lost = (CheckBox) rootView.findViewById(R.id.checkLost);
        Lost.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Lost"));
        Withdrawn = (CheckBox) rootView.findViewById(R.id.checkWithdrawn);
        Withdrawn.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Withdrawn"));
        Annoying = (CheckBox) rootView.findViewById(R.id.checkAnnoying);
        Annoying.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Annoying"));
        Shadowing = (CheckBox) rootView.findViewById(R.id.checkShadowing);
        Shadowing.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Shadowing"));
        Communication = (CheckBox) rootView.findViewById(R.id.checkCommunication);
        Communication.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdObs, "Communication"));

        return rootView;
    }
}
