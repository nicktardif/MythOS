package com.nicktardif.seniorproject.universalhackathon;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.radiusnetworks.proximity.ProximityKitBeacon;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";
    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AndroidProximityKitReferenceApplication) getApplication()).setMainActivity(this);
        AndroidProximityKitReferenceApplication app = (AndroidProximityKitReferenceApplication) getApplication();
        app.startManager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void detectedBeacon(final ProximityKitBeacon beacon, final String displayString, final boolean updateIfExists) {
        final String uuid = beacon.getId1().toUuidString(); //UUID
        final String id2 = beacon.getId2().toString();
        final String id3 = beacon.getId3().toString();
        final String display = displayString;

        final TextView uuidTV = (TextView) findViewById(R.id.uuid_value);
        final TextView id2TV = (TextView) findViewById(R.id.id2_value);
        final TextView id3TV = (TextView) findViewById(R.id.id3_value);
        final TextView timeTV = (TextView) findViewById(R.id.time_value);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        final String time_hms = Integer.toString(hour) + ":" + Integer.toString(minutes) + "." + Integer.toString(seconds);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                uuidTV.setText(uuid);
                id2TV.setText(id2);
                id3TV.setText(id3);
                timeTV.setText(time_hms);
            }
        });
    }
}
