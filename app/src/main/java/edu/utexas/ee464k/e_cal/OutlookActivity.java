package edu.utexas.ee464k.e_cal;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveStatus;

import java.util.Arrays;


public class OutlookActivity extends Activity implements LiveAuthListener {

    private LiveAuthClient auth;
    private LiveConnectClient client;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlook);
        this.resultTextView = (TextView)findViewById(R.id.resultTextView);
        auth = new LiveAuthClient(this, MyConstants.APP_CLIENT_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Iterable<String> scopes = Arrays.asList("wl.signin", "wl.basic", "wl.calendars");
        auth.login(this, scopes, this);
    }

    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
        if(status == LiveStatus.CONNECTED) {
            this.resultTextView.setText("Signed in.");
            client = new LiveConnectClient(session);
        }
        else {
            this.resultTextView.setText("Not signed in.");
            client = null;
        }
    }

    public void onAuthError(LiveAuthException exception, Object userState) {
        this.resultTextView.setText("Error signing in: " + exception.getMessage());
        client = null;
    }
}
