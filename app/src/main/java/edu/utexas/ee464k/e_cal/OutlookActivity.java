package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
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
                    outlookUser.setText(result.optString("name"));
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
        EditText deviceIdBox = (EditText) findViewById(R.id.deviceIdOutlook);
        String deviceIdString = deviceIdBox.getText().toString();
        if(deviceIdString.equals("")){
            Toast.makeText(this,"Must enter Device id",Toast.LENGTH_LONG).show();
            return;
        }
        int deviceId = Integer.parseInt(deviceIdBox.getText().toString());
        if(deviceId != 1){
            Toast.makeText(this,"Incorrect device Id",Toast.LENGTH_LONG).show();
            return;
        }
        if(startCalendar.after(endCalendar)){
            Toast.makeText(this,"Start Date is after End Date", Toast.LENGTH_LONG).show();
            return;
        }
        int startMonth = startCalendar.get(Calendar.MONTH) + 1;
        int endMonth = endCalendar.get(Calendar.MONTH) + 1;
        String startDateString = startCalendar.get(Calendar.YEAR)+"-"+startMonth+"-"+startCalendar.get(Calendar.DAY_OF_MONTH)+"T00:00:00.000";
        String endDateString = endCalendar.get(Calendar.YEAR)+"-"+endMonth+"-"+endCalendar.get(Calendar.DAY_OF_MONTH)+"T23:59:00.000";
        Toast.makeText(this,eventName+" "+eventLocation+" "+eventDescription+" "+startDateString+" "+endDateString,Toast.LENGTH_LONG).show();

    }


}
