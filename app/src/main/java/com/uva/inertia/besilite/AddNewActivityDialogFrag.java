package com.uva.inertia.besilite;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class AddNewActivityDialogFrag extends DialogFragment implements passActivityDataInterface{
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        LinearLayout linLayout=
                new LinearLayout(getActivity());
        Button b = new Button(getActivity());
        b.setText("Add new activity type");
        linLayout.addView(b);return linLayout;
    }

    public void passData(String activity){

    }
}


interface passActivityDataInterface{
    void passData(String activity);
}