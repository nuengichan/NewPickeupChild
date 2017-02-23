package com.example.database;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static com.example.database.R.id.txt;


public class MapActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener ,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{
    private static final String AUTH_KEY = "key=AAAABRiP3KY:APA91bFsU3vuDt9bZkPaD92BlKnTz0beXZDftoypMVdTbvCRFDJ8VtRst54QmOZgDhwEya1A_VlpJEaIEIiwuKoExBOg0hHPmtu7kyJ5St9obFwLomTr4YXZCZjcWSxUJnp74SVcIE5M";
    private TextView mTextView;
    private static final LatLng School = new LatLng(13.7626198, 100.6625916 );
    private Marker mSelectedMarker;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMap , mMap2;

    private Marker add , add2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mTextView = (TextView) findViewById(txt);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                tmp += key + ": " + value + "\n\n";
            }
            mTextView.setText(tmp);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap2 = map;

        mMap.getUiSettings().setZoomControlsEnabled(false);
        addMarkersToMap();
        // Set listener for marker click event.  See the bottom of this class for its behavior.
        mMap.setOnMarkerClickListener(this);

        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);




        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                float[] distance = new float[2];
                float[] distance2 = new float[4];
                float[] distance3 = new float[4];
                    /*
                    Location.distanceBetween( mMarker.getPosition().latitude, mMarker.getPosition().longitude,
                            mCircle.getCenter().latitude, mCircle.getCenter().longitude, distance);
                            */

                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        circle3.getCenter().latitude, circle3.getCenter().longitude, distance3);


                if (distance3[0] > circle3.getRadius()) {
                    Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance2[0] + " radius: " + circle3.getRadius(), Toast.LENGTH_LONG).show();

                }
                else  if (distance3[0] < circle3.getRadius()) {
                    Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance2[0] + " radius: " + circle3.getRadius(), Toast.LENGTH_LONG).show();
                    sendWithOtherThread2("token");


                }


                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        circle2.getCenter().latitude, circle2.getCenter().longitude, distance2);

                if (distance2[0] > circle2.getRadius()) {
                    Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance2[0] + " radius: " + circle2.getRadius(), Toast.LENGTH_LONG).show();

                }
                else  if (distance2[0] < circle2.getRadius()) {
                    Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance2[0] + " radius: " + circle2.getRadius(), Toast.LENGTH_LONG).show();
                    sendWithOtherThread2("token");


                }


                Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                        circle.getCenter().latitude, circle.getCenter().longitude, distance);




              if (distance[0] > circle.getRadius()) {
                    Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + circle.getRadius(), Toast.LENGTH_LONG).show();

                }
              else  {
                    Toast.makeText(getBaseContext(), "Inside, distance from center: " + distance[0] + " radius: " + circle.getRadius(), Toast.LENGTH_LONG).show();
                    sendWithOtherThread("token");
                  mMap.setOnMyLocationChangeListener(null);

                }
            }
        });



    }

    Circle circle ;

    Circle circle2 ;

    Circle circle3 ;
    public void sendTokens(View view) {
        sendWithOtherThread("token");
    }

    public void sendTokens2(View view) {
        sendWithOtherThread2("token");
    }

    private void sendWithOtherThread(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(type);
            }
        }).start();
    }


    private void pushNotification(String type) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "อีก 5 กิโลเมตรถึงโรงเรียน");
            jNotification.put("body", "อีก 5 กิโลเมตรถึงโรงเรียน");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            //jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

            switch(type) {
                case "token":
                    JSONArray ja = new JSONArray();
                    ja.put("eC3Pf6jsBEg:APA91bHeZDIXgnp2vZgIfl20LZ4XsjthyJ2OkWZXypankHgLMhnewn2P1f3QV0aKKxiirvKHJstoWSauNe4pbBFz0JAsssmocBCJYvXzWRb7kbkljBuFLctMHTv8qt_x7EMJcVoqfT6a");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(resp);
                }
            });
        }


        catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    private void sendWithOtherThread2(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification2(type);
            }
        }).start();
    }


    private void pushNotification2(String type) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "อีก 5 กิโลเมตรถึงโรงเรียน");
            jNotification.put("body", "อีก 5 กิโลเมตรถึงโรงเรียน");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            //jData.put("picture_url", "http://opsbug.com/static/google-io.jpg");

            switch(type) {
                case "token":
                    JSONArray ja = new JSONArray();
                    ja.put("eC3Pf6jsBEg:APA91bHeZDIXgnp2vZgIfl20LZ4XsjthyJ2OkWZXypankHgLMhnewn2P1f3QV0aKKxiirvKHJstoWSauNe4pbBFz0JAsssmocBCJYvXzWRb7kbkljBuFLctMHTv8qt_x7EMJcVoqfT6a");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(resp);
                }
            });
        }


        catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }



    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }


    private void addMarkersToMap() {

        add = mMap.addMarker(new MarkerOptions()
                .position(School)
                .title("School")
                .snippet("Population: 2,074,200"));

        add2 = mMap2.addMarker(new MarkerOptions()
                .position(School)
                .title("School")
                .snippet("Population: 2,074,200"));

        circle = drawCircle(new LatLng(13.7626198, 100.6625916) );

        circle2 = drawCircle2(new LatLng(13.763246, 100.649291));

        circle3 = drawCircle3(new LatLng(13.772699, 100.665926));
    }

    private Circle drawCircle3(LatLng latLng) {


        CircleOptions add2 = new CircleOptions()
                .center(latLng)
                .radius(50)
                .fillColor(0x33FF1493)
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        return mMap.addCircle(add2);
    }

    private Circle drawCircle2(LatLng latLng) {


        CircleOptions add2 = new CircleOptions()
                .center(latLng)
                .radius(50)
                .fillColor(0x33FF1493)
                .strokeColor(Color.BLUE)
                .strokeWidth(5);

        return mMap.addCircle(add2);
    }


    private Circle drawCircle(LatLng latLng) {

        CircleOptions add = new CircleOptions()
                .center(latLng)
                .radius(400)
                .fillColor(0x3300BFFF)
                .strokeColor(Color.BLUE)
                .strokeWidth(3);


        return mMap.addCircle(add);



    }









    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION, true);

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }
}

