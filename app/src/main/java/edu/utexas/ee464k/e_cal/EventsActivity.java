package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        mWeekView.goToHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
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
                userChild.removeValue();
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
                Toast.makeText(EventsActivity.this,"Events have been uploaded", Toast.LENGTH_LONG).show();
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
            case R.id.action_add_event:
                final CalendarEvent event = new CalendarEvent();
                LayoutInflater inflater = LayoutInflater.from(EventsActivity.this);
                final View calendarEventsDialogView = inflater.inflate(R.layout.fragment_edit_event, null);
                final EditText eventDescription = (EditText) calendarEventsDialogView.findViewById(R.id.eventDescription);
                final EditText eventName = (EditText) calendarEventsDialogView.findViewById(R.id.eventName);
                final EditText eventLocation = (EditText) calendarEventsDialogView.findViewById(R.id.eventLocation);
                final EditText startDate = (EditText) calendarEventsDialogView.findViewById(R.id.startDate);
                final EditText startTime = (EditText) calendarEventsDialogView.findViewById(R.id.startTime);
                final EditText endDate = (EditText) calendarEventsDialogView.findViewById(R.id.endDate);
                final EditText endTime = (EditText) calendarEventsDialogView.findViewById(R.id.endTime);
                Calendar Cal = Calendar.getInstance();
                event.setStartYear(Cal.get(Calendar.YEAR));
                event.setStartMonth(Cal.get(Calendar.MONTH)+1);
                event.setStartDay(Cal.get(Calendar.DAY_OF_MONTH));
                event.setStartHour_Of_Day(Cal.get(Calendar.HOUR_OF_DAY));
                event.setStartMinute(Cal.get(Calendar.MINUTE));
                Cal.add(Calendar.HOUR_OF_DAY,1);
                event.setEndMonth(Cal.get(Calendar.MONTH)+1);
                event.setEndMinute(Cal.get(Calendar.MINUTE));
                event.setEndYear(Cal.get(Calendar.YEAR));
                event.setEndDay(Cal.get(Calendar.DAY_OF_MONTH));
                event.setEndHour_Of_Day(Cal.get(Calendar.HOUR_OF_DAY));
                startDate.setText(event.getStartMonth()+"/"+event.getStartDay()+"/"+event.getStartYear());
                startTime.setText(event.getStartHour_Of_Day()+":"+event.getStartMinute());
                endDate.setText(event.getEndMonth()+"/"+event.getEndDay()+"/"+event.getEndYear());
                endTime.setText(event.getEndHour_Of_Day()+":"+event.getEndMinute());
                final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar startTimeCal = Calendar.getInstance();
                        startTimeCal.set(Integer.parseInt(event.getStartYear()),Integer.parseInt(event.getStartMonth()) - 1,
                                Integer.parseInt(event.getStartDay()),hourOfDay,minute);
                        Calendar endTimeCal = Calendar.getInstance();
                        endTimeCal.set(Integer.parseInt(event.getEndYear()),Integer.parseInt(event.getEndMonth()) - 1,
                                Integer.parseInt(event.getEndDay()),Integer.parseInt(event.getEndHour_Of_Day()),Integer.parseInt(event.getEndMinute()));
                        if(startTimeCal.after(endTimeCal)){
                            event.setEndDay(event.getStartDay());
                            event.setEndMonth(event.getStartMonth());
                            event.setEndYear(event.getStartYear());
                            event.setEndHour_Of_Day(hourOfDay + 1);
                            event.setEndMinute(minute);
                            endTime.setText(event.getEndHour_Of_Day()+":"+event.getEndMinute());
                        }
                        event.setStartHour_Of_Day(hourOfDay);
                        event.setStartMinute(minute);
                        startTime.setText(hourOfDay+":"+minute);
                    }
                };
                startTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(EventsActivity.this,timeStartListener,Integer.parseInt(event.getStartHour_Of_Day()),
                                Integer.parseInt(event.getStartMinute()),true).show();
                    }
                });
                final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar startDateCal = Calendar.getInstance();
                        startDateCal.set(year,monthOfYear,dayOfMonth);
                        Calendar endDateCal = Calendar.getInstance();
                        endDateCal.set(Integer.parseInt(event.getEndYear()),Integer.parseInt(event.getEndMonth()) - 1,Integer.parseInt(event.getEndDay()));
                        if(startDateCal.after(endDateCal)){
                            event.setEndDay(dayOfMonth);
                            event.setEndMonth(monthOfYear + 1);
                            event.setEndYear(year);
                            endDate.setText(event.getEndMonth()+"/"+event.getEndDay()+"/"+event.getEndYear());
                        }
                        event.setStartDay(dayOfMonth);
                        event.setStartMonth(monthOfYear + 1);
                        event.setStartYear(year);
                        startDate.setText(event.getStartMonth()+"/"+event.getStartDay()+"/"+event.getStartYear());
                    }
                };
                startDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(EventsActivity.this, startDateListener,Integer.parseInt(event.getStartYear()),
                                Integer.parseInt(event.getStartMonth()) - 1,Integer.parseInt(event.getStartDay())).show();
                    }
                });
                final TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Calendar startTimeCal = Calendar.getInstance();
                        Calendar endTimeCal = Calendar.getInstance();
                        endTimeCal.set(Integer.parseInt(event.getEndYear()),Integer.parseInt(event.getEndMonth()) - 1,
                                Integer.parseInt(event.getEndDay()),hourOfDay,minute);
                        startTimeCal.set(Integer.parseInt(event.getStartYear()),Integer.parseInt(event.getStartMonth()) - 1,
                                Integer.parseInt(event.getStartDay()),Integer.parseInt(event.getStartHour_Of_Day()),Integer.parseInt(event.getStartMinute()));
                        if(startTimeCal.after(endTimeCal)){
                            event.setStartDay(event.getEndDay());
                            event.setStartMonth(event.getEndMonth());
                            event.setStartYear(event.getEndYear());
                            event.setStartHour_Of_Day(hourOfDay - 1);
                            event.setStartMinute(minute);
                            startTime.setText(event.getStartHour_Of_Day()+":"+event.getStartMinute());
                        }
                        event.setEndHour_Of_Day(hourOfDay);
                        event.setEndMinute(minute);
                        endTime.setText(hourOfDay+":"+minute);
                    }
                };
                endTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(EventsActivity.this,endTimeListener,Integer.parseInt(event.getEndHour_Of_Day()),
                                Integer.parseInt(event.getEndMinute()),true).show();
                    }
                });
                final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar startDateCal = Calendar.getInstance();
                        Calendar endDateCal = Calendar.getInstance();
                        endDateCal.set(year,monthOfYear,dayOfMonth);
                        startDateCal.set(Integer.parseInt(event.getStartYear()),Integer.parseInt(event.getStartMonth()) - 1,Integer.parseInt(event.getStartDay()));
                        if(startDateCal.after(endDateCal)){
                            event.setStartDay(dayOfMonth);
                            event.setStartMonth(monthOfYear + 1);
                            event.setStartYear(year);
                            startDate.setText(event.getStartMonth()+"/"+event.getStartDay()+"/"+event.getStartYear());
                        }
                        event.setEndDay(dayOfMonth);
                        event.setEndMonth(monthOfYear + 1);
                        event.setEndYear(year);
                        endDate.setText(event.getEndMonth()+"/"+event.getEndDay()+"/"+event.getEndYear());
                    }
                };
                endDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DatePickerDialog(EventsActivity.this,endDateListener,Integer.parseInt(event.getEndYear()),
                                Integer.parseInt(event.getEndMonth()) - 1,Integer.parseInt(event.getEndDay())).show();
                    }
                });
                AlertDialog dialog = new AlertDialog.Builder(EventsActivity.this)
                        .setTitle("Add Event")
                        .setView(calendarEventsDialogView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                event.setName(eventName.getText().toString());
                                event.setDescription(eventDescription.getText().toString());
                                event.setLocation(eventLocation.getText().toString());
                                Events.add(event);
                                mWeekView.notifyDatasetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();
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
                switch(i % 6){
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
                    case 4:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_05));
                        break;
                    case 5:
                        weekViewEvent.setColor(getResources().getColor(R.color.event_color_06));
                }
                events.add(weekViewEvent);
            }
        }
        numRun = (numRun + 1) % 3;
        return events;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        final CalendarEvent cEvent = Events.get((int) event.getId());

        LayoutInflater inflater = LayoutInflater.from(EventsActivity.this);
        final View calendarEventDialogView = inflater.inflate(R.layout.fragment_edit_event, null);
        final EditText eventDescription = (EditText) calendarEventDialogView.findViewById(R.id.eventDescription);
        eventDescription.setText(cEvent.getDescription());
        final EditText eventLocation = (EditText) calendarEventDialogView.findViewById(R.id.eventLocation);
        eventLocation.setText(cEvent.getLocation());
        final EditText startTime = (EditText) calendarEventDialogView.findViewById(R.id.startTime);
        startTime.setText(cEvent.getStartHour_Of_Day()+":"+cEvent.getStartMinute());
        final EditText startDate = (EditText) calendarEventDialogView.findViewById(R.id.startDate);
        final EditText endTime = (EditText) calendarEventDialogView.findViewById(R.id.endTime);
        final EditText endDate = (EditText) calendarEventDialogView.findViewById(R.id.endDate);
        final EditText eventName = (EditText) calendarEventDialogView.findViewById(R.id.eventName);
        eventName.setText(cEvent.getName());
        final TimePickerDialog.OnTimeSetListener timeStartListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar startTimeCal = Calendar.getInstance();
                startTimeCal.set(Integer.parseInt(cEvent.getStartYear()),Integer.parseInt(cEvent.getStartMonth()) - 1,
                        Integer.parseInt(cEvent.getStartDay()),hourOfDay,minute);
                Calendar endTimeCal = Calendar.getInstance();
                endTimeCal.set(Integer.parseInt(cEvent.getEndYear()),Integer.parseInt(cEvent.getEndMonth()) - 1,
                        Integer.parseInt(cEvent.getEndDay()),Integer.parseInt(cEvent.getEndHour_Of_Day()),Integer.parseInt(cEvent.getEndMinute()));
                if(startTimeCal.after(endTimeCal)){
                    cEvent.setEndDay(cEvent.getStartDay());
                    cEvent.setEndMonth(cEvent.getStartMonth());
                    cEvent.setEndYear(cEvent.getStartYear());
                    cEvent.setEndHour_Of_Day(hourOfDay + 1);
                    cEvent.setEndMinute(minute);
                    endTime.setText(cEvent.getEndHour_Of_Day()+":"+cEvent.getEndMinute());
                }
                cEvent.setStartHour_Of_Day(hourOfDay);
                cEvent.setStartMinute(minute);
                startTime.setText(hourOfDay+":"+minute);
            }
        };
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EventsActivity.this,timeStartListener,Integer.parseInt(cEvent.getStartHour_Of_Day()),
                        Integer.parseInt(cEvent.getStartMinute()),true).show();
            }
        });
        startDate.setText(cEvent.getStartMonth()+"/"+cEvent.getStartDay()+"/"+cEvent.getStartYear());
        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar startDateCal = Calendar.getInstance();
                startDateCal.set(year,monthOfYear,dayOfMonth);
                Calendar endDateCal = Calendar.getInstance();
                endDateCal.set(Integer.parseInt(cEvent.getEndYear()),Integer.parseInt(cEvent.getEndMonth()) - 1,Integer.parseInt(cEvent.getEndDay()));
                if(startDateCal.after(endDateCal)){
                    cEvent.setEndDay(dayOfMonth);
                    cEvent.setEndMonth(monthOfYear + 1);
                    cEvent.setEndYear(year);
                    endDate.setText(cEvent.getEndMonth()+"/"+cEvent.getEndDay()+"/"+cEvent.getEndYear());
                }
                cEvent.setStartDay(dayOfMonth);
                cEvent.setStartMonth(monthOfYear + 1);
                cEvent.setStartYear(year);
                startDate.setText(cEvent.getStartMonth()+"/"+cEvent.getStartDay()+"/"+cEvent.getStartYear());
            }
        };
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventsActivity.this, startDateListener,Integer.parseInt(cEvent.getStartYear()),
                        Integer.parseInt(cEvent.getStartMonth()) - 1,Integer.parseInt(cEvent.getStartDay())).show();
            }
        });
        endTime.setText(cEvent.getEndHour_Of_Day()+":"+cEvent.getEndMinute());
        final TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar startTimeCal = Calendar.getInstance();
                Calendar endTimeCal = Calendar.getInstance();
                endTimeCal.set(Integer.parseInt(cEvent.getEndYear()),Integer.parseInt(cEvent.getEndMonth()) - 1,
                        Integer.parseInt(cEvent.getEndDay()),hourOfDay,minute);
                startTimeCal.set(Integer.parseInt(cEvent.getStartYear()),Integer.parseInt(cEvent.getStartMonth()) - 1,
                        Integer.parseInt(cEvent.getStartDay()),Integer.parseInt(cEvent.getStartHour_Of_Day()),Integer.parseInt(cEvent.getStartMinute()));
                if(startTimeCal.after(endTimeCal)){
                    cEvent.setStartDay(cEvent.getEndDay());
                    cEvent.setStartMonth(cEvent.getEndMonth());
                    cEvent.setStartYear(cEvent.getEndYear());
                    cEvent.setStartHour_Of_Day(hourOfDay - 1);
                    cEvent.setStartMinute(minute);
                    startTime.setText(cEvent.getStartHour_Of_Day()+":"+cEvent.getStartMinute());
                }
                cEvent.setEndHour_Of_Day(hourOfDay);
                cEvent.setEndMinute(minute);
                endTime.setText(hourOfDay+":"+minute);
            }
        };
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(EventsActivity.this,endTimeListener,Integer.parseInt(cEvent.getEndHour_Of_Day()),
                        Integer.parseInt(cEvent.getEndMinute()),true).show();
            }
        });
        endDate.setText(cEvent.getEndMonth()+"/"+cEvent.getEndDay()+"/"+cEvent.getEndYear());
        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar startDateCal = Calendar.getInstance();
                Calendar endDateCal = Calendar.getInstance();
                endDateCal.set(year,monthOfYear,dayOfMonth);
                startDateCal.set(Integer.parseInt(cEvent.getStartYear()),Integer.parseInt(cEvent.getStartMonth()) - 1,Integer.parseInt(cEvent.getStartDay()));
                if(startDateCal.after(endDateCal)){
                    cEvent.setStartDay(dayOfMonth);
                    cEvent.setStartMonth(monthOfYear + 1);
                    cEvent.setStartYear(year);
                    startDate.setText(cEvent.getStartMonth()+"/"+cEvent.getStartDay()+"/"+cEvent.getStartYear());
                }
                cEvent.setEndDay(dayOfMonth);
                cEvent.setEndMonth(monthOfYear + 1);
                cEvent.setEndYear(year);
                endDate.setText(cEvent.getEndMonth()+"/"+cEvent.getEndDay()+"/"+cEvent.getEndYear());
            }
        };
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventsActivity.this,endDateListener,Integer.parseInt(cEvent.getEndYear()),
                        Integer.parseInt(cEvent.getEndMonth()) - 1,Integer.parseInt(cEvent.getEndDay())).show();
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(EventsActivity.this)
                .setTitle("Edit Event")
                .setView(calendarEventDialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cEvent.setName(eventName.getText().toString());
                        cEvent.setDescription(eventDescription.getText().toString());
                        cEvent.setLocation(eventLocation.getText().toString());
                        mWeekView.notifyDatasetChanged();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Events.remove(cEvent);
                        mWeekView.notifyDatasetChanged();
                    }
                }).create();
        dialog.show();

    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

    }

}
