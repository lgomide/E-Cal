package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventsActivity extends Activity implements WeekView.MonthChangeListener, WeekView.EventClickListener, WeekView.EventLongPressListener{
    WeekView mWeekView;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private ArrayList<CalendarEvent> Events;
    private Firebase myFirebaseRef;
    private String userName;
    private String deviceId;
    private int numRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://scorching-fire-5550.firebaseio.com/");
        setContentView(R.layout.activity_events);
        Intent i = getIntent();
        userName = i.getStringExtra("userName");
        deviceId = i.getStringExtra("deviceId");
        Bundle data = i.getBundleExtra("data");
        Events = data.getParcelableArrayList("events");
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);
        mWeekView.notifyDatasetChanged();
        numRun = 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_events, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_upload_events:
                Firebase deviceChild = myFirebaseRef.child(deviceId);
                Firebase userChild = deviceChild.child(userName);
                for(int i = 0; i < Events.size(); i++){
                    CalendarEvent event = Events.get(i);
                    Firebase eventChild = userChild.child(String.valueOf(i));
                    eventChild.child("name").setValue(event.getName());
                    eventChild.child("location").setValue(event.getLocation());
                    eventChild.child("description").setValue(event.getDescription());
                    String startTime = event.getStartYear()+"-"+event.getStartMonth()+"-"+event.getStartDay()+"T"+event.getStartHour_Of_Day()+":"+event.getStartMinute();
                    eventChild.child("start_time").setValue(startTime);
                    String endTime = event.getEndYear()+"-"+event.getEndMonth()+"-"+event.getEndDay()+"T"+event.getEndHour_Of_Day()+":"+event.getEndMinute();
                    eventChild.child("end_time").setValue(endTime);
                }
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        if(numRun == 0){
            for(int i = 0; i < Events.size(); i++){
                CalendarEvent event = Events.get(i);
                Calendar startTime = Calendar.getInstance();
                Calendar endTime = Calendar.getInstance();
                startTime.set(Integer.parseInt(event.getStartYear()),Integer.parseInt(event.getStartMonth()) - 1,Integer.parseInt(event.getStartDay()),
                        Integer.parseInt(event.getStartHour_Of_Day()),Integer.parseInt(event.getStartMinute()));
                endTime.set(Integer.parseInt(event.getEndYear()),Integer.parseInt(event.getEndMonth()) - 1,Integer.parseInt(event.getEndDay()),
                        Integer.parseInt(event.getEndHour_Of_Day()), Integer.parseInt(event.getEndMinute()));
                WeekViewEvent weekViewEvent = new WeekViewEvent(i,event.getName(),startTime,endTime);
                switch(i % 4){
                    case 0:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_01));
                        break;
                    case 1:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_02));
                        break;
                    case 2:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_03));
                        break;
                    case 3:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_04));
                        break;
                }
                events.add(weekViewEvent);
            }
        }
        numRun = (numRun + 1) % 3;
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(EventsActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        CalendarEvent testEvent = new CalendarEvent();
        testEvent.setEndYear("2015");
        testEvent.setStartYear("2015");
        testEvent.setStartMonth("02");
        testEvent.setStartDay("18");
        testEvent.setStartHour_Of_Day("13");
        testEvent.setStartMinute("30");
        testEvent.setEndMonth("02");
        testEvent.setEndDay("18");
        testEvent.setEndHour_Of_Day("14");
        testEvent.setEndMinute("30");
        testEvent.setName("New Test Event");
        testEvent.setDescription("new description");
        testEvent.setLocation("home");
        Events.add(testEvent);
        mWeekView.notifyDatasetChanged();

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(EventsActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
        int id = (int) event.getId();
        Events.remove(id);
        mWeekView.notifyDatasetChanged();
    }

}
