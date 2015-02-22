package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;


public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startGoogleActivity(View v){
        Intent i = new Intent(this,GoogleActivity.class);
        startActivity(i);
    }

    public void startOutlookActivity(View v){
        Intent i = new Intent(this, OutlookActivity.class);
        startActivity(i);

    }

    public void startManualEntry(View v){
        LayoutInflater inflater = LayoutInflater.from(StartActivity.this);
        View startSettingsView = inflater.inflate(R.layout.fragment_manual_start_settings, null);
        final EditText deviceId = (EditText) startSettingsView.findViewById(R.id.manualDeviceId);
        final EditText userName = (EditText) startSettingsView.findViewById(R.id.manualUserName);
        AlertDialog settingsDialog = new AlertDialog.Builder(StartActivity.this)
                .setTitle("Settings")
                .setView(startSettingsView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (deviceId.getText().toString().equals("")) {
                            Toast.makeText(StartActivity.this, "Must Enter a Device ID", Toast.LENGTH_LONG).show();
                            return;
                        }
                        int deviceIdInt = Integer.parseInt(deviceId.getText().toString());
                        if (deviceIdInt != 1) {
                            Toast.makeText(StartActivity.this, "Must Enter a Valid Device Id", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (userName.getText().toString().equals("")) {
                            Toast.makeText(StartActivity.this, "Must Enter User Name", Toast.LENGTH_LONG).show();
                            return;
                        }

                        String userNameString = userName.getText().toString();
                        ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
                        Intent i = new Intent(StartActivity.this,EventsActivity.class);
                        Bundle data = new Bundle();
                        data.putParcelableArrayList("events", events);
                        i.putExtra("data", data);
                        i.putExtra("deviceId",String.valueOf(deviceIdInt));
                        i.putExtra("userName", userNameString);
                        startActivity(i);
                    }
                })
                .show();
    }

}
