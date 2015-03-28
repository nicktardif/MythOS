package com.nicktardif.seniorproject.universalhackathon;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.radiusnetworks.proximity.ProximityKitBeacon;


public class MainActivity extends ActionBarActivity {
    public static final String TAG = "MainActivity";
    public static boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((AndroidProximityKitReferenceApplication) getApplication()).setMainActivity(this);
        if (isRunning) {
            startManager();
        } else {
            stopManager();
        }
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

    public void displayTableRow(final ProximityKitBeacon beacon, final String displayString, final boolean updateIfExists) {
        String uuid = beacon.getId1().toUuidString(); //UUID
        String id2 = beacon.getId2().toString();
        String id3 = beacon.getId3().toString();
        String display = displayString;
        Log.d("ticknardif", "UUID string: " + uuid);
        Log.d("ticknardif", "ID2 string: " + id2);
        Log.d("ticknardif", "ID3 string: " + id3);
        Log.d("ticknardif", "Display string: " + display);
    }

    /**
     * Button action which turn the Proximity Kit manager service on and off.
     *
     * @param view  button object which was pressed
     */
    public void toggleManager(View view) {
        if (view.getId() != R.id.manager_toggle) { return; }

        if (isRunning) {
            stopManager();
            isRunning = false;
        } else {
            startManager();
            isRunning = true;
        }
    }

    /**
     * Turn the Proximity Kit manager on and update the UI accordingly.
     */
    private void startManager() {
        AndroidProximityKitReferenceApplication app = (AndroidProximityKitReferenceApplication) getApplication();
        Button btn = (Button) findViewById(R.id.manager_toggle);

        app.startManager();
        btn.setText(R.string.manager_toggle_stop);
    }

    /**
     * Turn the Proximity Kit manager off and update the UI accordingly.
     */
    private void stopManager() {
        AndroidProximityKitReferenceApplication app = (AndroidProximityKitReferenceApplication) getApplication();
        Button btn = (Button) findViewById(R.id.manager_toggle);

        app.stopManager();
        btn.setText(R.string.manager_toggle_start);
    }
}
