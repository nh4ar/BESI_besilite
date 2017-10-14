package com.uva.inertia.besilite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    private ArrayAdapter<String> notifEventsAdapter;

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
                notifEventsFromServer = getNotifEventsArray(resp);
                Log.v("jjp5nw", "after calling getNotifEventsArray(resp)");
                Log.v("jjp5nw", "ArrayList events = " + notifEventsFromServer);

                // dynamically add the events to the adapter that is attached to the listview.
                notifEventsAdapter.clear();
                for(String event : notifEventsFromServer)   {
                    notifEventsAdapter.add(event);
                    Log.v("jjp5nw", "notifEventsAdapter: " + event + " added to notifEventsAdapter");
                }

                FileHelpers.writeStringToInternalStorage(resp.toString(), "cache", "notificationsCache", getContext());
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("jjp5nw", "onErrorResponse " + error.toString());

                String cachedJson = FileHelpers.readStringFromInternalStorage("cache", "notificationsCache", getContext());

                try {
                    JSONArray cacheArray = new JSONArray(cachedJson);

                    notifEventsFromCache = getNotifEventsArray(cacheArray);
                    notifEventsAdapter.clear();
                    for(String event : notifEventsFromCache)    {
                        notifEventsAdapter.add(event);
                        Log.v("jjp5nw", "notifEventsAdapter: " + event + " added to notifEventsAdapter from cache");
                    }
                }   catch(JSONException e)  {
                    e.printStackTrace();
                }
            }
        });
        this.netQueue.add(eventsListRequestArray);

    }

    private ArrayList<String> getNotifEventsArray(JSONArray jArray)
    {
        Log.v("jjp5nw", "NotificationsFragment getNotifEventsArray called");

        ArrayList<String> ret = new ArrayList<String>();

        // sample entry: {"pk":1,"deployment":"testuser","event_time":"2017-08-17T21:44:08Z","time_created":"2017-08-18T21:44:38.046548Z","ack_time":null,"nottype":1}
        SimpleDateFormat fileSDF;   // = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        SimpleDateFormat viewSDF = new SimpleDateFormat("M/dd/yyyy h:mm a EEE");

        String[] sdfArgs = {
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'"
        };

        for(int i = 0; i < jArray.length(); i++)    {
            for(int j = 0; j < sdfArgs.length; j++) {
                try {
                    fileSDF = new SimpleDateFormat(sdfArgs[j]);
//                    Log.v("jjp5nw", "getNEArray created fileSDF with sdfArgs[" + j + "] = " + sdfArgs[j]);
                    JSONObject event = (JSONObject) (jArray.get(i));
//                    Log.v("jjp5nw", "getNEArray event.getString(\"event_time\") = " + event.getString("event_time"));
                    Date eventTime = fileSDF.parse(event.getString("event_time"));
                    String formattedEventTime = viewSDF.format(eventTime);
//                    Log.v("jjp5nw", "getNEArray " + formattedEventTime);
                    ret.add(formattedEventTime);
                    break;
                } catch (JSONException e) {
                    Log.e("jjp5nw", "ERROR: Server responded with incorrect JSON");
                    e.printStackTrace();
                    break;
                } catch (ParseException e) {
//                    Log.e("jjp5nw", "ERROR: ParseEception in parse call.");
                }
            }
        }

        Collections.sort(ret, String.CASE_INSENSITIVE_ORDER);
        Collections.reverse(ret);
        return ret;
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


        notifEventsAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.custom_list_item_1);

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