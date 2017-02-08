package com.uva.inertia.besilite;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
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
    int level;

    int lvposition;

    int maxposition;

    ListView listView1;

    Button next, scrollup, scrolldown;

    RadioGroup agiLevelGroup;
    SharedPreferences sharedPref;

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
        final View rootView =  inflater.inflate(R.layout.fragment_agi_gen_info, container, false);

        Button dater = (Button)rootView.findViewById(R.id.agi_date);
        Button timer = (Button)rootView.findViewById(R.id.agi_time);
        Spinner loc_spinner =  (Spinner)rootView.findViewById(R.id.loc_spinner);

        ar = (AgitationReports) getActivity();
        pwdGen = ar.pwdGen;

        dater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgiDatePickerDialog(v);

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReport", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report -> Date Picker")
                        .id("agitationReportDatePickerButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
                Log.v("ONCLICK", "dater clicked");
            }
        });

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAgiTimePickerDialog(v);

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReport", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report -> Time Picker")
                        .id("agitationReportTimePickerButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////
                Log.v("ONCLICK", "timer clicked");
            }
        });

        final ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getContext(), R.array.room_names, android.R.layout.simple_spinner_item);

        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        loc_spinner.setAdapter(spinner_adapter);

        loc_spinner.setSelection(0);

        loc_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pwdGen.put("agiloc", spinner_adapter.getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                pwdGen.put("agiloc", "NA");
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ////////////////////////Android Analytics Tracking Code////////////////////////////////////
        // Create an Emitter
        Emitter e1 = new Emitter.EmitterBuilder("besisnowplow.us-east-1.elasticbeanstalk.com", getContext())
                .method(HttpMethod.POST) // Optional - Defines how we send the request
                .option(BufferOption.Single) // Optional - Defines how many events we bundle in a POST\
                .build();

        Subject s1 = new Subject.SubjectBuilder().build();
        s1.setUserId(sharedPref.getString("pref_key_api_token", ""));

        // Make and return the Tracker object
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "addAgiReportGenInfo", "com.uva.inertia.besilite", getContext())
                .base64(false) // Optional - Defines what protocol used to send events
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Add Agi Gen Info")
                .id("addAgiGenInfo")
                .build());


//        t1.getSubject().setUserId(sharedPref.getString("pref_key_api_token", ""));
        ///////////////////////////////////////////////////////////////////////////////////////////


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

        ///CODE TO VIEW MEMENTO LOGS
        listView1 = (ListView)( rootView.findViewById(R.id.listView1) );

        //DISABLE SCROLLING BY TOUCH
        listView1.setFastScrollEnabled(false);
        listView1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
            //http://stackoverflow.com/questions/4338185/how-to-get-a-non-scrollable-listview
        });
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
            }
        });

        // NEXT 33 LINES FROM http://stackoverflow.com/questions/4432261/list-view-snap-to-item
        // SCROLLING WITH SNAPPING TO NEAREST ITEM
        listView1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                if (scrollState == SCROLL_STATE_IDLE) {
                    View itemView = view.getChildAt(0);
                    int top = Math.abs(itemView.getTop());
                    int bottom = Math.abs(itemView.getBottom());
                    int scrollBy = top >= bottom ? bottom : -top;
                    if (scrollBy == 0) {
                        return;
                    }
                    smoothScrollDeferred(scrollBy, (ListView)view);
                }
            }

            private void smoothScrollDeferred(final int scrollByF, final ListView viewF) {
                final Handler h = new Handler();
                h.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        viewF.smoothScrollBy(scrollByF, 200);
                    }
                });
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        ArrayList<String> mementoData1 = new ArrayList<String>();
//        LinkedList<String> inListView = new LinkedList<String>();
        int listViewPos = 0;
        for(int test = 0; test < 20; test++) {
            mementoData1.add("sample data " + test);
        }

//        Log.v("inListView", .toArray().toString());
        //SET POSITION VARIABLES
        lvposition = 0;
        maxposition = mementoData1.size();


        String test = FileHelpers.readStringFromInternalStorage("Download/", "memento.txt", rootView.getContext());
        Log.v("T3st", "read test: " + test);
        //create adapter for listView1
        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, mementoData1);
//        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, inListView);
        listView1.setAdapter(adapter1);
        // http://stackoverflow.com/questions/8215308/using-context-in-a-fragment

        scrollup = (Button)rootView.findViewById(R.id.button_scrollup);
        scrollup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                listView1.smoothScrollByOffset(-1);
//                listView1.smoothScrollToPosition(listView1.getSelectedItemPosition() - 1);
                listView1.smoothScrollBy(-50, 300);
                listView1.smoothScrollToPosition(listView1.getFirstVisiblePosition());
                //the smoothScrollToPosition is used to make the elements "snap" to the top & bottom edges

//                listView1.smoothScrollToPositionFromTop((lvposition <= 0) ? lvposition : (--lvposition), 150, 250);

//                listView1.scroll

//                lvposition = (lvposition <= 0) ? lvposition : lvposition - 1;
//                listView1.scroll
//                scrollListView(listView1, mementoData1, inListView, lvposition, -1);

            }

        });
        scrollup.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                listView1.smoothScrollToPosition(0);
                listView1.smoothScrollBy(-50 * adapter1.getCount(), 200 * adapter1.getCount());
                return true;
            }
        });

        scrolldown = (Button)rootView.findViewById(R.id.button_scrolldown);
        scrolldown.setOnClickListener(new View.OnClickListener()    {
            @Override
            public void onClick(View v) {
//                listView1.smoothScrollByOffset(1);
                listView1.smoothScrollBy(50, 300);
                listView1.smoothScrollToPosition(listView1.getLastVisiblePosition());
                //the smoothScrollToPosition is used to make the elements "snap" to the top & bottom edges

//                listView1.smoothScrollToPositionFromTop((lvposition >= maxposition - 1) ? lvposition : (++lvposition), 150, 250);

//                listView1.smoothScrollToPosition(listView1.getSelectedItemPosition() + 1);
//                scrollListView(listView1, mementoData1, inListView, lvposition, 1);

//                listView1.smoothScrollToPosition(lvposition + 1, lvposition);

//                lvposition++;

//                Log.v("inListView", "before removeFirst: " + inListView.toArray().toString());
//                inListView.removeFirst();
//                Log.v("inListView", "after removeFirst: " + inListView.toArray().toString());
//                inListView.addLast("testADD");
//                Log.v("inListView", "after addLast: " + inListView.toArray().toString());
            }
        });
        scrolldown.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                listView1.smoothScrollToPosition(adapter1.getCount());
                listView1.smoothScrollBy(50 * adapter1.getCount(), 200 * adapter1.getCount());
                return true;
            }
        });

        /////////////////////////////////////////////////////////////////////////////////


        next = (Button)rootView.findViewById(R.id.agi_gen_info_next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AgitationReports) getActivity()).selectPage(1);
            }
        });

        agiLevelGroup = (RadioGroup)rootView.findViewById(R.id.agiLevelGrp);

        agiLevelGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int level = 1;
                switch (checkedId) {
                    case R.id.agiLevel1:
                        level = 1;
                        break;
                    case R.id.agiLevel2:
                        level = 2;
                        break;
                    case R.id.agiLevel3:
                        level = 3;
                        break;
                    case R.id.agiLevel4:
                        level = 4;
                        break;
                    case R.id.agiLevel5:
                        level = 5;
                        break;
                    case R.id.agiLevel6:
                        level = 6;
                        break;
                    case R.id.agiLevel7:
                        level = 7;
                        break;
                    case R.id.agiLevel8:
                        level = 8;
                        break;
                    case R.id.agiLevel9:
                        level = 9;
                        break;
                    case R.id.agiLevel10:
                        level = 10;
                        break;
                }
                pwdGen.put("level", "" + (level));
                agiLevelViewer.setText("" + level);


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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReport", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report -> Radio Button Level " + level)
                        .id("agitationReportRadioButtonLevel" + level)
                        .build());

                Log.v("CHECKED", "Radio Button Level " + level + " checked");
                ///////////////////////////////////////////////////////////////////////////////////////////
            }
        });

        level = 1;
        pwdGen.put("level", "" + level);
        agiLevelViewer = (TextView)rootView.findViewById(R.id.agi_level_viewer);
        agiLevelViewer.setText("" + level);

        return rootView;

    }

    /////

    public void scrollListView(ListView lv, List<String> masterList, LinkedList<String> viewList, int position, int offset)
    {
        //check to see if it's out of bounds
        if(position < 0 || position >= masterList.size() || (position + offset) < 0 || (position + offset >= masterList.size()))   {
            return;
        }
        viewList = new LinkedList<String>();

        int elementsShown = 4;

        for(int i = 0; i < elementsShown; i++) {
            viewList.add(masterList.get(position + offset + i));
        }

        lvposition = position + offset;
    }

//    public void loadInitialListView()
    /////



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
        selDate.setText(dateFormats[0].format(agidate)
         );
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