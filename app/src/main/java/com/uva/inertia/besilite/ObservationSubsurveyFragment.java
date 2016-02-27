package com.uva.inertia.besilite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.HashMap;


public class ObservationSubsurveyFragment extends android.support.v4.app.Fragment {

    AgitationReports ar;
    HashMap<String, Boolean> pwdObs;

    CheckBox Kicking;
    CheckBox Hitting;
    CheckBox Pacing;
    CheckBox Wandering;
    CheckBox Disrobing;
    CheckBox Scratching;
    CheckBox ScreamingYelling;
    CheckBox StrangeNoises;
    CheckBox Repetitive;
    CheckBox Biting;
    CheckBox Spitting;
    CheckBox EatingNonFood;
    CheckBox BreakingThings;
    CheckBox UnableToFindCommonPlaces;

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

        pwdObs = ar.pwdObs;

        Kicking = (CheckBox) rootView.findViewById(R.id.checkKicking);
        Kicking.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Kicking"));
        Hitting = (CheckBox) rootView.findViewById(R.id.checkHitting);
        Hitting.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Hitting"));
        Pacing = (CheckBox) rootView.findViewById(R.id.checkPacing);
        Pacing.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Pacing"));
        Wandering = (CheckBox) rootView.findViewById(R.id.checkWandering);
        Wandering.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Wandering"));
        Disrobing = (CheckBox) rootView.findViewById(R.id.checkDisrobing);
        Disrobing.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Disrobing"));
        Scratching = (CheckBox) rootView.findViewById(R.id.checkScratching);
        Scratching.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Scratching"));
        ScreamingYelling = (CheckBox) rootView.findViewById(R.id.checkScreamingYelling);
        ScreamingYelling.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "ScreamingYelling"));
        StrangeNoises = (CheckBox) rootView.findViewById(R.id.checkStrangeNoises);
        StrangeNoises.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "StrangeNoises"));
        Repetitive = (CheckBox) rootView.findViewById(R.id.checkRepetitive);
        Repetitive.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Repetitive"));
        Biting = (CheckBox) rootView.findViewById(R.id.checkBiting);
        Biting.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Biting"));
        Spitting = (CheckBox) rootView.findViewById(R.id.checkSpitting);
        Spitting.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "Spitting"));
        EatingNonFood = (CheckBox) rootView.findViewById(R.id.checkEatingNonFood);
        EatingNonFood.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "EatingNonFood"));
        BreakingThings = (CheckBox) rootView.findViewById(R.id.checkBreakingThings);
        BreakingThings.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "BreakingThings"));
        UnableToFindCommonPlaces = (CheckBox) rootView.findViewById(R.id.checkUnableToFindCommonPlaces);
        UnableToFindCommonPlaces.setOnClickListener(CustomClickHandlers.updateMapOnRadio(pwdObs, "UnableToFindCommonPlaces"));


        return rootView;
    }
}
