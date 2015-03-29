package com.nicktardif.seniorproject.universalhackathon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.radiusnetworks.proximity.ProximityKitBeacon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;


public class MainActivity extends ActionBarActivity {
    public RestAdapter restAdapter;
    public MythosService mythosService;
    public String UUID;
    public ArrayList<LastConnectedBeacon> connectedBeacons;

    private Callback<GetUUIDResponse> getUUIDCallback = new Callback<GetUUIDResponse>() {
        @Override
        public void success(GetUUIDResponse uuidResponse, Response response) {
            boolean success = uuidResponse.success;
            String uuid =  uuidResponse.uuid;
            int length = uuidResponse.length;

            // Store the UUID in the SharedPreferences
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.shared_prefs_uuid), uuid);
            editor.commit();

            Log.d("ticknardif", "Stored " + uuid + " as the UUID");

            UUID = uuid;
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d("ticknardif", error.getMessage());
        }
    };

    private Callback<SeenBeaconResponse> seenBeaconCallback = new Callback<SeenBeaconResponse>() {
        @Override
        public void success(SeenBeaconResponse seenBeaconResponse, Response response) {
            Log.d("ticknardif", "GOT HEREEREERERER");
            if(seenBeaconResponse.success) {
                String fresh = seenBeaconResponse.fresh;
                String old = seenBeaconResponse.old;

                Log.d("ticknardif", "fresh is : " + fresh);
                Log.d("ticknardif", "old is : " + old);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e("ticknardif", error.getMessage());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectedBeacons = new ArrayList<LastConnectedBeacon>();

        ((AndroidProximityKitReferenceApplication) getApplication()).setMainActivity(this);
        AndroidProximityKitReferenceApplication app = (AndroidProximityKitReferenceApplication) getApplication();
        app.startManager();

        // Establish our web server
        Gson gson = new GsonBuilder().create();
        restAdapter = new RestAdapter.Builder().setEndpoint("http://www.swamphacks.io").setConverter(new GsonConverter(gson)).build();
        mythosService = restAdapter.create(MythosService.class);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.shared_prefs_file), Context.MODE_PRIVATE);
        UUID = sharedPreferences.getString(getString(R.string.shared_prefs_uuid), "");

        // If we don't have a saved UUID, this is the first time they have opened the app
        // Make them get a UUID
        if (UUID.equals("")) {
            Log.d("ticknardif", "Requesting a UUID");
            // Request a unique identifier from the API
            mythosService.getUUID("uf2015", getUUIDCallback);
        } else {
            Log.d("ticknardif", "Already have a UUID: " + UUID);
        }

        showQRImage(400, 400);
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
        final int TIME_INTERVAL = 1;
        final String uuid = beacon.getId1().toUuidString(); //UUID
        final String id2 = beacon.getId2().toString();
        final String id3 = beacon.getId3().toString();

        final TextView uuidTV = (TextView) findViewById(R.id.uuid_value);
        final TextView id2TV = (TextView) findViewById(R.id.id2_value);
        final TextView id3TV = (TextView) findViewById(R.id.id3_value);
        final TextView timeTV = (TextView) findViewById(R.id.time_value);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        final String time_hms = Integer.toString(hour) + ":" + Integer.toString(minutes) + "." + Integer.toString(seconds);

        int UPDATE_INTERVAL = 1;
        LastConnectedBeacon newBeacon = new LastConnectedBeacon(uuid, Integer.parseInt(id2), Integer.parseInt(id3), System.currentTimeMillis() / 1000);
        boolean exists = false;

        for(LastConnectedBeacon existingBeacon : connectedBeacons) {
            if(existingBeacon.UUID.equals(newBeacon.UUID) && existingBeacon.major == newBeacon.major && existingBeacon.minor == newBeacon.minor) {

                // This beacon already exists in the phone
                exists = true;
                Log.d("ticknardif", "Beacon " + existingBeacon.UUID + " exists in the list already");
                long secondsDifference = newBeacon.lastConnectedTime - existingBeacon.lastConnectedTime;
                Log.d("ticknardif", "Seconds difference is " + Long.toString(secondsDifference));

                // If it was accessed more than 10 seconds ago, update the list
                if(secondsDifference > TIME_INTERVAL) {
                    existingBeacon.lastConnectedTime = newBeacon.lastConnectedTime;
                    Log.d("ticknardif", "Updated last connection time of beacon " + existingBeacon.UUID);
                    String bID = existingBeacon.createBeaconID();
                    Log.d("ticknardif", "ID: " + bID);
                    mythosService.seenBeacon("uf2015", bID, UUID, seenBeaconCallback);
                }
            }
        }

        // If this beacon doesn't exist, add it immediately
        if(!exists) {
            Log.d("ticknardif", "Adding beacon " + newBeacon.UUID + " to the list");
            connectedBeacons.add(newBeacon);
            //TODO: New beacon API call
        }

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

    public void showQRImage(int width, int height){

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = null;
        try {
            matrix = writer.encode(UUID, BarcodeFormat.QR_CODE, width, height);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                bmp.setPixel(x, y, matrix.get(x,y) ? Color.BLACK : Color.WHITE);
            }
        }

        ImageView qrIV = (ImageView) findViewById(R.id.qr_image);
        qrIV.setImageBitmap(bmp);
    }
}
