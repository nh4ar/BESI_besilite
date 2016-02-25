package com.uva.inertia.besilite;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class RadioPWDEmotionSubsurveyFragment extends android.support.v4.app.Fragment {
    AgitationReports ar;
    RadioButton SadVoice;
    RadioButton Tearful;
    RadioButton LackReact;
    RadioButton VeryWorried;
    RadioButton Frightened;
    RadioButton TalkLess;
    RadioButton AppetiteLoss;
    RadioButton LessInterestInHobbies;
    RadioButton SadExpression;
    RadioButton LackOfInterest;
    RadioButton TroubleConcentrating;
    RadioButton BotheredByUsualActivities;
    RadioButton SlowMove;
    RadioButton SlowSpeech;
    RadioButton SlowReaction;
    HashMap<String, Boolean> pwdEmo = new HashMap<>();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static RadioPWDEmotionSubsurveyFragment newInstance() {
        RadioPWDEmotionSubsurveyFragment fragment = new RadioPWDEmotionSubsurveyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RadioPWDEmotionSubsurveyFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radio_emotion_subsurvey, container, false);

        ar = (AgitationReports) getActivity();


        pwdEmo = ar.pwdEmo;

        SadVoice = (RadioButton)rootView.findViewById(R.id.radioSadVoice);
        SadVoice.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"SadVoice"));

        Tearful = (RadioButton)rootView.findViewById(R.id.radioTearfulness);
        Tearful.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"Tearfulness"));

        LackReact = (RadioButton)rootView.findViewById(R.id.radioLackOfReact);
        LackReact.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"LackReactionToPleasantEvents"));

        VeryWorried = (RadioButton)rootView.findViewById(R.id.radioVeryWorried);
        VeryWorried.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"VeryWorried"));

        Frightened = (RadioButton)rootView.findViewById(R.id.radioFrightened);
        Frightened.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"Frightened"));

        TalkLess = (RadioButton)rootView.findViewById(R.id.radioLessTalk);
        TalkLess.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"TalkLess"));

        AppetiteLoss = (RadioButton)rootView.findViewById(R.id.radioAppetiteLoss);
        AppetiteLoss.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"AppetiteLoss"));

        LessInterestInHobbies = (RadioButton)rootView.findViewById(R.id.radioLessIntrest);
        LessInterestInHobbies.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"LessInterestInHobbies"));

        SadExpression = (RadioButton)rootView.findViewById(R.id.radioSadExpression);
        SadExpression.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo,"SadExpression"));

        LackOfInterest = (RadioButton)rootView.findViewById(R.id.radioLackOfInterest);
        LackOfInterest.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "LackOfInterest"));

        TroubleConcentrating = (RadioButton)rootView.findViewById(R.id.radioTroubleConcen);
        TroubleConcentrating.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "TroubleConcentrating"));

        BotheredByUsualActivities = (RadioButton)rootView.findViewById(R.id.radioBotheredByUsual);
        BotheredByUsualActivities.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "BotheredByUsualActivities"));

        SlowMove = (RadioButton)rootView.findViewById(R.id.radioSlowMove);
        SlowMove.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "SlowMovement"));

        SlowSpeech =(RadioButton)rootView.findViewById(R.id.radioSlowSpeech);
        SlowSpeech.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "SlowSpeech"));

        SlowReaction =  (RadioButton)rootView.findViewById(R.id.radioSlowReact);
        SlowReaction.setOnClickListener(CustomClickHandlers.updateMapOnClick(pwdEmo, "SlowReaction"));



        return rootView;

    }

}
