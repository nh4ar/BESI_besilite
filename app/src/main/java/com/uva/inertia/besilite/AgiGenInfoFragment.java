package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AgiGenInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AgiGenInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgiGenInfoFragment extends Fragment implements passBackInterface{

    AgitationReports ar;
    HashMap<String, String> pwdGen;

    java.text.DateFormat[] dateFormats;
    Calendar calendar;
    Date agidate;
    java.text.DateFormat df;
    TimeZone tz;
    String agitimestamp;

    TextView selDate;
    TextView selTime;
    SeekBar  agiLevlBar;
    TextView agiLevelViewer;
    int agitationLevel;

    public static final int DATEPICKER_FRAGMENT=1; // adding this line
    public static final int TIMEPICKER_FRAGMENT=2; // adding this line



    public AgiGenInfoFragment() {
        // Required empty public constructor
    }


    public static AgiGenInfoFragment newInstance() {
        AgiGenInfoFragment fragment = new AgiGenInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dateFormats = new java.text.DateFormat[] {
                    java.text.DateFormat.getDateInstance(),
                    java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT),
            };
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_agi_gen_info, container, false);

        Button dater = (Button)rootView.findViewById(R.id.agi_date);
        Button timer = (Button)rootView.findViewById(R.id.agi_time);

        ar = (AgitationReports) getActivity();
        pwdGen = ar.pwdGen;

        dater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgiDatePickerDialog(v);
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgiTimePickerDialog(v);
            }
        });

        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        tz = TimeZone.getTimeZone("UTC");
        df.setTimeZone(tz);

        selDate = (TextView) rootView.findViewById(R.id.agi_date_viewer);
        selTime = (TextView) rootView.findViewById(R.id.agi_time_viewer);
        calendar = new GregorianCalendar();
        agidate = new Date();
        calendar.setTime(agidate);
        updateMapDatetime();

        dateFormats[0].setTimeZone(TimeZone.getDefault());
        dateFormats[1].setTimeZone(TimeZone.getDefault());

        selDate.setText(dateFormats[0].format(agidate));
        selTime.setText(dateFormats[1].format(agidate));


        agiLevlBar = (SeekBar)rootView.findViewById(R.id.agi_level_slider);
        agiLevlBar.setMax(9);
        agitationLevel = 1;
        pwdGen.put("level", "" + agitationLevel);
        agiLevelViewer = (TextView)rootView.findViewById(R.id.agi_level_viewer);
        agiLevelViewer.setText("" + agitationLevel);

        agiLevlBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                agitationLevel = progressValue;
                pwdGen.put("level", "" + (agitationLevel+1));
                //Toast.makeText(getActivity().getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
                agiLevelViewer.setText("" + (agitationLevel+1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getActivity().getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getActivity().getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;

    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void updateMapDatetime(){
        pwdGen.put("agitimestamp", df.format(agidate));
    }

    public void setDate(int year, int month, int day){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        agidate = calendar.getTime();
        Log.v("PICKER", agidate.toString());
        selDate.setText(dateFormats[0].format(agidate));
        updateMapDatetime();
    }

    public void setTime( int hourOfDay, int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        agidate = calendar.getTime();
        Log.v("PICKER", agidate.toString());
        selTime.setText(dateFormats[1].format(agidate));
        updateMapDatetime();
    }











    private class DurationTimePickDialog extends TimePickerDialog
    {
        final OnTimeSetListener mCallback;
        TimePicker mTimePicker;
        final int increment;

        public DurationTimePickDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int increment)
        {
            super(context, callBack, hourOfDay, minute/increment, is24HourView);
            this.mCallback = callBack;
            this.increment = increment;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (mCallback != null && mTimePicker!=null) {
                mTimePicker.clearFocus();
                mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                        mTimePicker.getCurrentMinute()*increment);
            }
        }

        @Override
        protected void onStop()
        {
            // override and do nothing
        }


    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar,this, hour, minute,
                    android.text.format.DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Intent i = new Intent();
            i.putExtra("hour",hourOfDay);
            i.putExtra("minute",minute);
            passBackInterface mHost = (passBackInterface)getTargetFragment();
            mHost.passData(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
    public void showAgiTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.setTargetFragment(this, TIMEPICKER_FRAGMENT);
        newFragment.show((getActivity()).getSupportFragmentManager().beginTransaction(), "agiTimePicker");
    }
















    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Intent i = new Intent();
            i.putExtra("year",year);
            i.putExtra("month",month);
            i.putExtra("day",day);
            passBackInterface mHost = (passBackInterface)getTargetFragment();
            mHost.passData(getTargetRequestCode(), Activity.RESULT_OK, i);
        }
    }
    public void showAgiDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.setTargetFragment(this, DATEPICKER_FRAGMENT);
        newFragment.show((getActivity()).getSupportFragmentManager().beginTransaction(), "agiDatePicker");
    }

    public void passData(int code, int status, Intent data){
        if (code == DATEPICKER_FRAGMENT){
            if (status == Activity.RESULT_OK) {
                Bundle bundle=data.getExtras();
                setDate(bundle.getInt("year"), bundle.getInt("month"),bundle.getInt("day"));
            }
        }
        else if (code == TIMEPICKER_FRAGMENT){
            if (status == Activity.RESULT_OK) {
                Bundle bundle=data.getExtras();
                setTime(bundle.getInt("hour"), bundle.getInt("minute"));
            }
        }
    }


}


interface passBackInterface{
    void passData(int code, int status, Intent i);
}