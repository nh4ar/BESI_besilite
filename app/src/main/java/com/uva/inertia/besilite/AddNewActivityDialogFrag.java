package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class AddNewActivityDialogFrag extends DialogFragment{

    AddActivityBundle addbundle;
    String newAct;

    public AddNewActivityDialogFrag() {
        // Empty constructor required for DialogFragment
    }

    public static AddNewActivityDialogFrag newInstance(String title) {
        AddNewActivityDialogFrag frag = new AddNewActivityDialogFrag();
        Bundle args = new Bundle();
        args.putString("Add New Activity Type", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        addbundle = (AddActivityBundle)getActivity();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        final EditText input = new EditText(getActivity());
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passItAlong(input.getText().toString());
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    public void passItAlong(String text){
        addbundle.onFinishNewActDialog(text);
    }
}

