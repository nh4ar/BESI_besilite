package com.uva.inertia.besilite;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
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

    RadioGroup col1;
    RadioGroup col2;
    RadioGroup col3;

    boolean clearCol1;
    boolean clearCol2;
    boolean clearCol3;

    HashMap<String, Boolean> pwdEmo = new HashMap<>();

    ArrayList<RadioGroup> columns = new ArrayList<>();

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


    public RadioGroup.OnCheckedChangeListener resetOtherCols(){
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
//            TODO: THERE IS A BUG HERE WE NEED TO FIX IT
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1) {
                    for (RadioGroup r : columns) {
                        if (!r.equals(group)) {
                            r.clearCheck();
                        }
                    }
                }
            }
        };
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radio_emotion_subsurvey, container, false);

        ar = (AgitationReports) getActivity();

        RadioGroup col1 = (RadioGroup)rootView.findViewById(R.id.radioCol1);
        columns.add(col1);

        RadioGroup col2 = (RadioGroup)rootView.findViewById(R.id.radioCol2);
        columns.add(col2);

        RadioGroup col3 = (RadioGroup)rootView.findViewById(R.id.radioCol3);
        columns.add(col3);

        col1.setOnCheckedChangeListener(resetOtherCols());
        col2.setOnCheckedChangeListener(resetOtherCols());
        col3.setOnCheckedChangeListener(resetOtherCols());


        pwdEmo = ar.pwdEmo;

        SadVoice = (RadioButton)rootView.findViewById(R.id.radioSadVoice);
        SadVoice.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "SadVoice"));

        Tearful = (RadioButton)rootView.findViewById(R.id.radioTearfulness);
        Tearful.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "Tearfulness"));

        LackReact = (RadioButton)rootView.findViewById(R.id.radioLackOfReact);
        LackReact.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "LackReactionToPleasantEvents"));

        VeryWorried = (RadioButton)rootView.findViewById(R.id.radioVeryWorried);
        VeryWorried.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "VeryWorried"));

        Frightened = (RadioButton)rootView.findViewById(R.id.radioFrightened);
        Frightened.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "Frightened"));

        TalkLess = (RadioButton)rootView.findViewById(R.id.radioLessTalk);
        TalkLess.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "TalkLess"));

        AppetiteLoss = (RadioButton)rootView.findViewById(R.id.radioAppetiteLoss);
        AppetiteLoss.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "AppetiteLoss"));

        LessInterestInHobbies = (RadioButton)rootView.findViewById(R.id.radioLessIntrest);
        LessInterestInHobbies.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "LessInterestInHobbies"));

        SadExpression = (RadioButton)rootView.findViewById(R.id.radioSadExpression);
        SadExpression.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "SadExpression"));

        LackOfInterest = (RadioButton)rootView.findViewById(R.id.radioLackOfInterest);
        LackOfInterest.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "LackOfInterest"));

        TroubleConcentrating = (RadioButton)rootView.findViewById(R.id.radioTroubleConcen);
        TroubleConcentrating.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "TroubleConcentrating"));

        BotheredByUsualActivities = (RadioButton)rootView.findViewById(R.id.radioBotheredByUsual);
        BotheredByUsualActivities.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "BotheredByUsualActivities"));

        SlowMove = (RadioButton)rootView.findViewById(R.id.radioSlowMove);
        SlowMove.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "SlowMovement"));

        SlowSpeech =(RadioButton)rootView.findViewById(R.id.radioSlowSpeech);
        SlowSpeech.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "SlowSpeech"));

        SlowReaction =  (RadioButton)rootView.findViewById(R.id.radioSlowReact);
        SlowReaction.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdEmo, "SlowReaction"));

        return rootView;

    }

}
