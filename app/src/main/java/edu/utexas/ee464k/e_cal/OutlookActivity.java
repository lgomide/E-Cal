package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;


public class OutlookActivity extends Activity implements LiveAuthListener {

    private LiveAuthClient auth;
    private LiveConnectClient client;
    private TextView outlookUser;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private EditText startDateField;
    private EditText endDateField;
    private boolean eventName;
    private boolean eventLocation;
    private boolean eventDescription;
    private ArrayList<CalendarEvent> Events = new ArrayList<CalendarEvent>();
    private String deviceId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlook);
        outlookUser = (TextView)findViewById(R.id.outlookUser);
        auth = new LiveAuthClient(this, MyConstants.APP_CLIENT_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic", "wl.calendars");
        auth.login(this, scopes, this);
        startDateField = (EditText) findViewById(R.id.outlookStartDate);
        endDateField = (EditText) findViewById(R.id.outlookEndDate);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        updateEndField();
        updateStartField();
        final DatePickerDialog.OnDateSetListener startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                startCalendar.set(Calendar.YEAR, year);
                startCalendar.set(Calendar.MONTH, monthOfYear);
                startCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateStartField();
            }
        };
        final DatePickerDialog.OnDateSetListener endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endCalendar.set(Calendar.YEAR,year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateEndField();
            }
        };
        startDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OutlookActivity.this, startDate, startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(OutlookActivity.this, endDate, endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateStartField(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDateField.setText(sdf.format(startCalendar.getTime()));
    }

    private void updateEndField(){
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        endDateField.setText(sdf.format(endCalendar.getTime()));
    }

    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
        if(status == LiveStatus.CONNECTED) {
            client = new LiveConnectClient(session);
            client.getAsync("me", new LiveOperationListener() {
                @Override
                public void onComplete(LiveOperation operation) {
                    JSONObject result = operation.getResult();
                    userName = result.optString("name");
                    outlookUser.setText(userName);
                }

                @Override
                public void onError(LiveOperationException exception, LiveOperation operation) {
                    outlookUser.setText("Error getting name: " + exception.getMessage());
                }
            });
        }
        else {
            Toast.makeText(this,"Unable to log int",Toast.LENGTH_LONG).show();
            client = null;
            finish();
        }
    }

    public void onAuthError(LiveAuthException exception, Object userState) {
        Toast.makeText(this,"Unable to log int",Toast.LENGTH_LONG).show();
        client = null;
        finish();
    }

    public void logoutOutlook(View v){
        auth.logout(this);
        finish();
    }

    public void toggleOutlookEventName(View v){
        eventName = !eventName;
    }

    public void toggleOutlookEventDescription(View v){
        eventDescription = !eventDescription;
    }

    public void toggleOutlookEventLocation(View v){
        eventLocation = !eventDescription;
    }

    public void getCalendarDataOutlook(View v){
        if(startCalendar.after(endCalendar)){
            Toast.makeText(this,"Start Date is after End Date", Toast.LENGTH_LONG).show();
            return;
        }
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        String startMonthString;
        if(startMonth/10 == 0){
            startMonthString = "0"+startMonth;
        } else{
            startMonthString = String.valueOf(startMonth);
        }
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        String endMonthString;
        if(endMonth/10 == 0){
            endMonthString = "0" + endMonth;
        }else{
            endMonthString = String.valueOf(endMonth);
        }
        int startDay = startCalendar.get(Calendar.DAY_OF_MONTH);
        String startDayString;
        if(startDay / 10 == 0){
            startDayString = "0" + startDay;
        }else{
            startDayString = String.valueOf(startDay);
        }
        int endDay = endCalendar.get(Calendar.DAY_OF_MONTH);
        String endDayString;
        if(endDay / 10 == 0){
            endDayString = "0" + endDay;
        }else{
            endDayString = String.valueOf(endDay);
        }
        String startDateString = startCalendar.get(Calendar.YEAR)+"-"+startMonthString+"-"+startDayString+"T00:00:00.000";
        String endDateString = endCalendar.get(Calendar.YEAR)+"-"+endMonthString+"-"+endDayString+"T23:59:00.000";
        Toast.makeText(this,eventName+" "+eventLocation+" "+eventDescription+" "+startDateString+" "+endDateString,Toast.LENGTH_LONG).show();
        client.getAsync("me/events?start_time="+startDateString+"Z&end_time="+endDateString+"Z",new LiveOperationListener() {
            @Override
            public void onComplete(LiveOperation operation) {
                JSONObject result = operation.getResult();
                try {
                    JSONArray events = result.getJSONArray("data");
                    for(int i = 0; i < events.length(); i++){
                        JSONObject jsonEvent = events.getJSONObject(i);
                        if(jsonEvent.getString("availability").equals("busy")) {
                            CalendarEvent event = new CalendarEvent();
                            String eventNameString;
                            String eventLocationString;
                            String eventDescriptionString;
                            if (eventName) {
                                eventNameString = jsonEvent.getString("name");
                            } else{
                                eventNameString = "";
                            }
                            if (eventLocation) {
                                eventLocationString = jsonEvent.getString("location");
                            } else{
                                eventLocationString = "";
                            }
                            if (eventDescription) {
                                eventDescriptionString = jsonEvent.getString("description");
                            } else {
                                eventDescriptionString = "";
                            }
                            String endTime = jsonEvent.getString("end_time");
                            String[] splits = endTime.split("-");
                            Calendar endCal = Calendar.getInstance();
                            endCal.set(Calendar.YEAR,Integer.parseInt(splits[0]));
                            endCal.set(Calendar.MONTH,Integer.parseInt(splits[1]));
                            splits = splits[2].split("T");
                            endCal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(splits[0]));
                            splits = splits[1].split(":");
                            endCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splits[0]));
                            endCal.set(Calendar.MINUTE, Integer.parseInt(splits[1]));
                            String startTime = jsonEvent.getString("start_time");
                            splits = startTime.split("-");
                            Calendar startCal = Calendar.getInstance();
                            startCal.set(Calendar.YEAR,Integer.parseInt(splits[0]));
                            startCal.set(Calendar.MONTH, Integer.parseInt(splits[1]));
                            splits = splits[2].split("T");
                            startCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splits[0]));
                            splits = splits[1].split(":");
                            startCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splits[0]));
                            startCal.set(Calendar.MINUTE, Integer.parseInt(splits[1]));
                            while(startCal.get(Calendar.MONTH)!=endCal.get(Calendar.MONTH) || startCal.get(Calendar.DAY_OF_MONTH) != endCal.get(Calendar.DAY_OF_MONTH)){
                                CalendarEvent newEvent = new CalendarEvent();
                                newEvent.setName(eventNameString);
                                newEvent.setDescription(eventDescriptionString);
                                newEvent.setLocation(eventLocationString);
                                newEvent.setStartHour_Of_Day(startCal.get(Calendar.HOUR_OF_DAY));
                                newEvent.setStartMinute(startCal.get(Calendar.MINUTE));
                                newEvent.setStartYear(startCal.get(Calendar.YEAR));
                                newEvent.setStartMonth(startCal.get(Calendar.MONTH)+1);
                                newEvent.setStartDay(startCal.get(Calendar.DAY_OF_MONTH));
                                newEvent.setEndHour_Of_Day(23);
                                newEvent.setEndMinute(59);
                                newEvent.setEndYear(startCal.get(Calendar.YEAR));
                                newEvent.setEndMonth(startCal.get(Calendar.MONTH)+1);
                                newEvent.setEndDay(startCal.get(Calendar.DAY_OF_MONTH));
                                startCal.add(Calendar.DAY_OF_MONTH,1);
                                startCal.set(Calendar.HOUR_OF_DAY,0);
                                startCal.set(Calendar.MINUTE, 0);
                                Events.add(newEvent);
                            }
                            event.setStartHour_Of_Day(startCal.get(Calendar.HOUR_OF_DAY));
                            event.setStartMinute(startCal.get(Calendar.MINUTE));
                            event.setStartYear(startCal.get(Calendar.YEAR));
                            event.setStartMonth(startCal.get(Calendar.MONTH)+1);
                            event.setStartDay(startCal.get(Calendar.DAY_OF_MONTH));
                            event.setEndHour_Of_Day(endCal.get(Calendar.HOUR_OF_DAY));
                            event.setEndMinute(endCal.get(Calendar.MINUTE));
                            event.setEndYear(endCal.get(Calendar.YEAR));
                            event.setEndMonth(endCal.get(Calendar.MONTH)+1);
                            event.setEndDay(endCal.get(Calendar.DAY_OF_MONTH));
                            event.setLocation(eventLocationString);
                            event.setDescription(eventDescriptionString);
                            event.setName(eventNameString);
                            Events.add(event);
                            Events.add(event);
                        }
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
                Intent i = new Intent(OutlookActivity.this,EventsActivity.class);
                Bundle data = new Bundle();
                data.putParcelableArrayList("events", Events);
                i.putExtra("data", data);
                i.putExtra("deviceId",deviceId);
                i.putExtra("userName", userName);
                startActivity(i);
            }

            @Override
            public void onError(LiveOperationException exception, LiveOperation operation) {
                Toast.makeText(OutlookActivity.this,"Unable to get calendar data: "+exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


}
