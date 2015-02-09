package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


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

}
