package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.snowplowanalytics.snowplow.tracker.Emitter;
import com.snowplowanalytics.snowplow.tracker.Subject;
import com.snowplowanalytics.snowplow.tracker.Tracker;
import com.snowplowanalytics.snowplow.tracker.emitter.BufferOption;
import com.snowplowanalytics.snowplow.tracker.emitter.HttpMethod;
import com.snowplowanalytics.snowplow.tracker.events.ScreenView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NotificationsFragment extends android.support.v4.app.Fragment
{
    final int NUMBER_OF_QUESTIONS = 3;

    AgitationReports ar;
    ConfirmFragment.OnConfirmClickedListener mListener;

    SharedPreferences sharedPref;

    Button backBtn, confirmBtn;
    Question[] questions;
    HashMap<Integer, Boolean> pwdNotif;

    RequestQueue netQueue;

    private String deploy_id, base_url, api_token, notificationsEndpoint, endpoint;

    private ArrayList<String> notifEventsFromServer, notifEventsFromCache;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationsFragment newInstance()
    {
        Log.v("jjp5nw", "NotificationsFragment newInstance() called");
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private void pullNotificationsEventListFromServer()
    {
        Log.v("jjp5nw", "NotificationsFragment pullNotificationsEventListFromServer() called");
        ArrayList<String> ret = new ArrayList<String>();
        ArrayList<String> events;

        deploy_id = sharedPref.getString("pref_key_deploy_id", "");
        base_url = sharedPref.getString("pref_key_base_url", "");
        api_token = sharedPref.getString("pref_key_api_token", "");
        notificationsEndpoint = "/api/v1/athena/notify/smart/";

        JsonArrayRequestWithToken eventsListRequestArray = new JsonArrayRequestWithToken(base_url + notificationsEndpoint, api_token, new Response.Listener<JSONArray>()    {

            @Override
            public void onResponse(JSONArray resp) {
                Log.v("jjp5nw", "onResponse notifications = " + resp.toString());
//                buildCheckBoxList(resp);
//                Log.v("onResponse jjp5nw", "before calling getMementoEventsArray");
//                notifEventsFromServer = getMementoEventsArray(resp);
//                Log.v("onResponse jjp5nw", "after calling getMementoEventsArray");
//                Log.v("onResponse jjp5nw", "ArrayList events = " + mementoEventsFromServer);

                // dynamically add the events to the adapter that is attached to the listview.
//                adapter1.clear();
//                for(String event : mementoEventsFromServer) {
//                    adapter1.add(event);
//                    Log.v("adapter1 jjp5nw", event + " added to adapter1");
//                }

//                FileHelpers.writeStringToInternalStorage(resp.toString(), "cache", "mementoCache", getContext());


//                notifEventsFromServer = get
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("jjp5nw", "onErrorResponse " + error.toString());
//                String cachedJson = FileHelpers.readStringFromInternalStorage("cache", "mementoCache", getContext());
//                try {
//                    JSONArray cacheArray = new JSONArray(cachedJson);
//
//                    mementoEventsFromCache = getMementoEventsArray(cacheArray);
//                    adapter1.clear();
//                    for(String event : mementoEventsFromCache) {
//                        adapter1.add(event);
//                        Log.v("adapter1 jjp5nw", event + " added to adapter1");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        this.netQueue.add(eventsListRequestArray);

    }

    public NotificationsFragment()
    {
        Log.v("jjp5nw", "NotificationsFragment constructor called");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.v("jjp5nw", "onCreate(" + savedInstanceState + ") called.");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        netQueue = NetworkSingleton.getInstance(this.getContext()).getRequestQueue();

        pullNotificationsEventListFromServer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.v("jjp5nw", "NotificationsFragment onCreateView() called");
        final View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        ar = (AgitationReports)getActivity();
        pwdNotif = ar.pwdNotif;



        ///*

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
        Tracker t1 = Tracker.init(new Tracker.TrackerBuilder(e1, "agiRepNotifications", "com.uva.inertia.besilite", getContext())
                .base64(false)
                .subject(s1)
                .build()
        );

        t1.track(ScreenView.builder()
                .name("Agitation Report Notifications")
                .id("agiReportNotifs")
                .build());


        ///////////////////////////////////////////////////////////////////////////////////////////

        backBtn = (Button) rootView.findViewById(R.id.backFromNotifs);

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportNotifications", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Notifications -> Back")
                        .id("agitationReportNotifsBackButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                //
                ((AgitationReports) getActivity()).selectPage(1);
            }
        });

        confirmBtn = (Button) rootView.findViewById(R.id.submitOnNotifs);

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
                Tracker tracker = Tracker.init(new Tracker.TrackerBuilder(emitter, "agitationReportNotifications", "com.uva.inertia.besilite", getContext())
                        .base64(false)
                        .subject(subject)
                        .build()
                );

                tracker.track(ScreenView.builder()
                        .name("Agitation Report / Notifications -> Submit")
                        .id("agitationReportNotifsSubmitButton")
                        .build());
                ///////////////////////////////////////////////////////////////////////////////////////////

                mListener = (ConfirmFragment.OnConfirmClickedListener) getActivity();
                mListener.OnConfirmClicked();
            }
        });

        confirmBtn.setEnabled(false);
//*/



        //////////////////////////////////////////////////////////////////
        Button btnQ1Yes, btnQ1No, btnQ2Yes, btnQ2No, btnQ3Yes, btnQ3No;

        btnQ1Yes = (Button) rootView.findViewById(R.id.btn_notif_q1_yes);
        btnQ1No = (Button) rootView.findViewById(R.id.btn_notif_q1_no);
        btnQ2Yes = (Button) rootView.findViewById(R.id.btn_notif_q2_yes);
        btnQ2No = (Button) rootView.findViewById(R.id.btn_notif_q2_no);
        btnQ3Yes = (Button) rootView.findViewById(R.id.btn_notif_q3_yes);
        btnQ3No = (Button) rootView.findViewById(R.id.btn_notif_q3_no);


        questions = new Question[NUMBER_OF_QUESTIONS];

        questions[0] = new Question(1, "event", (new ArrayList<Button>()), (TextView)rootView.findViewById(R.id.textView_notif_question1));
        questions[1] = new Question(2, "accurate", (new ArrayList<Button>()), (TextView)rootView.findViewById(R.id.textView_notif_question2));
        questions[2] = new Question(3, "helpful", (new ArrayList<Button>()), (TextView)rootView.findViewById(R.id.textView_notif_question3));

        questions[0].addButton(btnQ1Yes);
        questions[0].addButton(btnQ1No);
        questions[1].addButton(btnQ2Yes);
        questions[1].addButton(btnQ2No);
        questions[2].addButton(btnQ3Yes);
        questions[2].addButton(btnQ3No);

        for(int i = 1; i < questions.length; i++)   {
            setQuestionState(questions[i], false);
        }


        btnQ1Yes.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ1Yes clicked");
                questions[0].setAnswer(true);
                setQuestionState(questions[1], true);
                setQuestionState(questions[2], true);

                v.setSelected(true);
                questions[0].getButtons().get(1).setSelected(false);

//                Log.v("jjp5nw", questions[0].getQuestionId() + "");
//                Log.v("jjp5nw", questions[0].getAnswer() + "");
                for(int i = 0; i < questions.length; i++)   {
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
                }


                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        btnQ1No.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ1No clicked");
                questions[0].setAnswer(false);
                setQuestionState(questions[1], false);
                setQuestionState(questions[2], false);

                v.setSelected(true);
                questions[0].getButtons().get(0).setSelected(false);

                for(int i = 0; i < questions.length; i++)   {
//                    Log.v("jjp5nw", "pwdNotif putting " + questions[i] + " at i = " + i + " into the map.");
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
//                    Log.v("jjp5nw", "pwdNotif put successful.");
                }

                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        btnQ2Yes.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ2Yes clicked");
                questions[1].setAnswer(true);

                v.setSelected(true);
                questions[1].getButtons().get(1).setSelected(false);

                for(int i = 0; i < questions.length; i++)   {
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
                }

                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        btnQ2No.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ2No clicked");
                questions[1].setAnswer(false);

                v.setSelected(true);
                questions[1].getButtons().get(0).setSelected(false);

                for(int i = 0; i < questions.length; i++)   {
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
                }

                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        btnQ3Yes.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ3Yes clicked");
                questions[2].setAnswer(true);

                v.setSelected(true);
                questions[2].getButtons().get(1).setSelected(false);

                for(int i = 0; i < questions.length; i++)   {
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
                }

                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        btnQ3No.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)
            {
                Log.v("jjp5nw", "btnQ3No clicked");
                questions[2].setAnswer(false);

                v.setSelected(true);
                questions[2].getButtons().get(0).setSelected(false);

                for(int i = 0; i < questions.length; i++)   {
                    pwdNotif.put(questions[i].getQuestionId(), questions[i].getAnswer());
                }

                // for submit button enabled
                confirmBtn.setEnabled(allQuestionsAnswered());
            }
        });

        return rootView;
    }


    private void setButtonState(Button button, boolean enabled)
    {
        button.setEnabled(enabled);
    }

    private void setButtonState(List<Button> listOfButtons, boolean enabled)
    {
        for(Button button : listOfButtons)  {
            button.setEnabled(enabled);
            button.setSelected(false);
        }
    }

    private void setButtonSelected(Button button, boolean selected)
    {

    }

    private void setQuestionState(Question question, boolean enabled)
    {
        Log.v("jjp5nw", "setQuestionState(" + question + ", " + enabled + ") called");
        setButtonState(question.getButtons(), enabled);
        question.getTextView().setTextColor(getResources().getColor((enabled) ? (R.color.question_textview_enabled) : (R.color.question_textview_disabled)));
        question.setUnanswered();
    }

    private boolean allQuestionsAnswered()
    {
        if(questions[0].isAnswered())   {
            if(!questions[0].getAnswer())   {
                return true;
            }
        }
        for(int i = 1; i < questions.length; i++)  {
            if(!questions[i].isAnswered())  {
                return false;
            }
        }
        return true;
    }



}

class Question
{
    private List<Button> buttons;
    private boolean answer, answered;
    private int id;
    private String name;

    private TextView textView;

    public Question(int id, String name, List<Button> buttons, TextView textView)
    {
        this(id, name, false, buttons, textView);
    }

    public Question(int id, String name, boolean answer, List<Button> buttons, TextView textView)
    {
        this.id = id;
        this.name = name;
        this.answer = answer;
        this.buttons = buttons;
        this.textView = textView;

        this.answered = false;
    }

    public int getQuestionId()
    {
        return this.id;
    }


    public void setAnswer(boolean answer)
    {
        this.answer = answer;
        this.answered = true;
    }

    public boolean getAnswer()
    {
        return this.answer;
    }

    public void setUnanswered()
    {
        this.setAnswer(false);
        this.answered = false;
    }

    public boolean isAnswered()
    {
        return this.answered;
    }

    public boolean addButton(Button button)
    {
        return this.buttons.add(button);
    }

    public TextView getTextView()
    {
        return this.textView;
    }

    public List<Button> getButtons()
    {
        return this.buttons;
    }

    public String toString()
    {
        return "(Question, {id: " + this.id + " , name: " + this.name + "})";
    }

}