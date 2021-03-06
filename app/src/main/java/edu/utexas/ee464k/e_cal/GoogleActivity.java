package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class GoogleActivity extends Activity {

    private boolean eventName;
    private boolean eventLocation;
    private boolean eventDescription;
    private ArrayList<CalendarEvent> Events = new ArrayList<CalendarEvent>();
    private String deviceId;
    private TextView userName;
    private EditText startDateField;
    private EditText endDateField;
    private Calendar startCalendar;
    private Calendar endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startDateField = (EditText) findViewById(R.id.googleStartDate);
        endDateField = (EditText) findViewById(R.id.googleEndDate);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        userName = (TextView) findViewById(R.id.googleUser);
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
                new DatePickerDialog(GoogleActivity.this, startDate, startCalendar.get(Calendar.YEAR),
                        startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        endDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(GoogleActivity.this, endDate, endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        String[] projection = new String[] {ContactsContract.Profile.DISPLAY_NAME};
        Uri dataUri = Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        Cursor c = getContentResolver().query(dataUri, projection, null, null, null);
        try{
            if(c.moveToFirst()){
                userName.setText(c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME)));
            } else{
                final EditText input = new EditText(this);
                new AlertDialog.Builder(GoogleActivity.this)
                        .setTitle("User Name")
                        .setMessage("Please enter user name: ")
                        .setView(input)
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                userName.setText(input.getText().toString());
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        }finally {
            c.close();
        }
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


    public void logoutGoogle(View v){
        //logout of google
        finish();
    }

    public void toggleGoogleEventName(View v){
        eventName = !eventName;
    }

    public void toggleGoogleEventDescription(View v){
        eventDescription = !eventDescription;
    }

    public void toggleGoogleEventLocation(View v){
        eventLocation = !eventDescription;
    }

    public void getCalendarDataGoogle(View v){
        if(startCalendar.after(endCalendar)){
            Toast.makeText(this,"Start Date is after End Date", Toast.LENGTH_LONG).show();
            return;
        }
        endCalendar.set(Calendar.HOUR_OF_DAY,23);
        endCalendar.set(Calendar.MINUTE,59);
        ContentResolver cr = v.getContext().getContentResolver();
        Cursor cursor = CalendarContract.Instances.query(cr,
                new String[] {CalendarContract.Events.TITLE,CalendarContract.Events.DESCRIPTION,
                        CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Events.EVENT_LOCATION,
                        CalendarContract.Events.AVAILABILITY, CalendarContract.Events.DURATION,CalendarContract.Events.EXDATE,
                        CalendarContract.Events.RRULE},
                startCalendar.getTimeInMillis(),endCalendar.getTimeInMillis());
        try{
            if(cursor.moveToFirst()){
                for(int i = 0; i < cursor.getCount(); i++){
                    if(cursor.getInt(5) == CalendarContract.Events.AVAILABILITY_BUSY){
                        CalendarEvent event = new CalendarEvent();
                        String eventNameString;
                        String eventLocationString;
                        String eventDescriptionString;
                        if (eventName) {
                            eventNameString = cursor.getString(0);
                        } else{
                            eventNameString = "";
                        }
                        if (eventLocation) {
                            eventLocationString = cursor.getString(4);
                        } else{
                            eventLocationString = "";
                        }
                        if (eventDescription) {
                            eventDescriptionString = cursor.getString(1);
                        } else {
                            eventDescriptionString = "";
                        }
                        event.setName(eventNameString);
                        event.setDescription(eventDescriptionString);
                        Calendar startCal = Calendar.getInstance();
                        startCal.setTimeInMillis(Long.parseLong(cursor.getString(2)));
                        Calendar endCal = Calendar.getInstance();
                        endCal.setTimeInMillis(Long.parseLong(cursor.getString(3)));
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
                        Events.add(event);
                    }
                    cursor.moveToNext();
                }
            }

        }
        finally{
            cursor.close();
        }
        Intent i = new Intent(GoogleActivity.this,EventsActivity.class);
        Bundle data = new Bundle();
        data.putParcelableArrayList("events", Events);
        i.putExtra("data", data);
        i.putExtra("deviceId",deviceId);
        i.putExtra("userName", userName.getText().toString());
        startActivity(i);
    }


}
