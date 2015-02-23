package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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


public class GoogleActivity extends Activity {

    private boolean eventName;
    private boolean eventLocation;
    private boolean eventDescription;
    private ArrayList<CalendarEvent> Events = new ArrayList<CalendarEvent>();
    private String deviceId;
    private String userName;
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
        EditText deviceIdBox = (EditText) findViewById(R.id.deviceIdGoogle);
        String deviceIdString = deviceIdBox.getText().toString();
        if(deviceIdString.equals("")){
            Toast.makeText(this,"Must enter Device id",Toast.LENGTH_LONG).show();
            return;
        }
        int deviceIdInt = Integer.parseInt(deviceIdBox.getText().toString());
        if(deviceIdInt != 1){
            Toast.makeText(this,"Incorrect device Id",Toast.LENGTH_LONG).show();
            return;
        }
        deviceId = String.valueOf(deviceIdInt);
        if(startCalendar.after(endCalendar)){
            Toast.makeText(this,"Start Date is after End Date", Toast.LENGTH_LONG).show();
            return;
        }
        endCalendar.set(Calendar.HOUR_OF_DAY,23);
        endCalendar.set(Calendar.MINUTE,59);
        ContentResolver cr = v.getContext().getContentResolver();
        Cursor cursor = CalendarContract.Instances.query(cr,
                new String[] {"title", "description", "dtstart", "dtend", "eventLocation", "availability"},
                startCalendar.getTimeInMillis(),endCalendar.getTimeInMillis());
        cursor.moveToFirst();
        for(int i = 0; i < cursor.getCount(); i++){
            if(cursor.getInt(5) == CalendarContract.Events.AVAILABILITY_BUSY){
                CalendarEvent event = new CalendarEvent();
                event.setName(cursor.getString(0));
                event.setDescription(cursor.getString(1));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(cursor.getString(2)));
                event.setStartHour_Of_Day(cal.get(Calendar.HOUR_OF_DAY));
                event.setStartMinute(cal.get(Calendar.MINUTE));
                event.setStartYear(cal.get(Calendar.YEAR));
                event.setStartMonth(cal.get(Calendar.MONTH)+1);
                event.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
                cal.setTimeInMillis(Long.parseLong(cursor.getString(3)));
                event.setEndHour_Of_Day(cal.get(Calendar.HOUR_OF_DAY));
                event.setEndMinute(cal.get(Calendar.MINUTE));
                event.setEndYear(cal.get(Calendar.YEAR));
                event.setEndMonth(cal.get(Calendar.MONTH)+1);
                event.setEndDay(cal.get(Calendar.DAY_OF_MONTH));
                event.setLocation(cursor.getString(4));
                Events.add(event);
            }
            cursor.moveToNext();
        }
        Intent i = new Intent(GoogleActivity.this,EventsActivity.class);
        Bundle data = new Bundle();
        data.putParcelableArrayList("events", Events);
        i.putExtra("data", data);
        i.putExtra("deviceId",deviceId);
        i.putExtra("userName", userName);
        startActivity(i);
    }


}
