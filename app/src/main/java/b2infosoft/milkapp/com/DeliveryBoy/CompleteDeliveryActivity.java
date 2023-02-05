package b2infosoft.milkapp.com.DeliveryBoy;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import b2infosoft.milkapp.com.GoogleMap.DirectionsJSONParser;
import b2infosoft.milkapp.com.R;

import static com.google.android.gms.maps.model.JointType.ROUND;

public class CompleteDeliveryActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final long START_TIME_IN_MILLIS = 10000;
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private final int PERMISSION_REQUEST_CODE = 5;
    public Polyline polylineObj;
    SupportMapFragment mapFragment;
    boolean flag = true;
    Polyline greyPolyLine;
    TextView tv_distance, tv_time;
    String duration = "";
    String distance = "";
    Button btnAbandoneOrder;
    TextView tvTitle;
    ImageView ivBack;
    LinearLayout ll_second, ll_first, ll_popup, ll_popup1;
    Button btnScanParcel, btnScanParcel1;
    double origin_lat = 26.8700628;
    double origin_long = 75.765306;
    double dest_lat = 26.8553218;
    double dest_long = 75.7564754;
    Timer timer1;
    CountDownTimer timer;
    TextView tvArriveTime;
    CountDownTimer countDownTimer;
    Button button3;
    long mTimeLeftInMillies = START_TIME_IN_MILLIS;
    String type = "t";
    private Marker userMarker;
    private Marker providerMarker;
    private LocationManager locationManager;
    private GoogleMap mGoogleMap;
    public LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LatLng dest = new LatLng(dest_lat, dest_long);
            LatLng origin = new LatLng(origin_lat, origin_long);


            int height = 80;
            int width = 80;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_milk_home_marker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            int height1 = 80;
            int width1 = 80;
            BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_vehicle);
            Bitmap b1 = bitmapdraw1.getBitmap();
            Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width1, height1, false);
            if (mGoogleMap != null) {

                if (providerMarker != null) {
                    providerMarker.setPosition(origin);
                } else {
                    providerMarker = mGoogleMap.addMarker(new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Origin"));
                }

                if (userMarker != null) {
                    userMarker.setPosition(dest);
                } else {
                    userMarker = mGoogleMap.addMarker(new MarkerOptions().position(dest).icon(BitmapDescriptorFactory.fromBitmap(smallMarker1)).title("Destination"));
                }

                if (flag) {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest, 14));
                    flag = false;
                }

                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);
                Log.d("url for poli", "------>" + url);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

            }


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }


    };
    private GoogleApiClient mGoogleApiClient;
    private List<LatLng> listLatLng = new ArrayList<>();
    private boolean mTimerRunning;
    private boolean isCanceled = false;
    private int REQUEST_CHECK_SETTINGS = 12;
    private boolean isPaused = false;

    public static void showSettingsAlert(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Permission Required");
        alertDialog.setMessage("Please Enable the Required Permission to continue using Dating App");
        alertDialog.setPositiveButton("Enable Permission", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_delivery_map);
        Intent intent = getIntent();
        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
        }


        if (type.equals("new")) {
            countDownTimer();
        }

        initView();
        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                CheckifLocationEnabled1();
            }
        }, 0, 2000);

    }

    void countDownTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (isPaused || isCanceled) {
                    //If the user request to cancel or paused the
                    //CountDownTimer we will cancel the current instance
                    cancel();
                } else {
                    mTimeLeftInMillies = millisUntilFinished;
                    tvArriveTime.setVisibility(View.VISIBLE);
                    updateCoundDownText();
                }

            }

            public void onFinish() {
                mTimerRunning = false;
                tvArriveTime.setVisibility(View.GONE);
                countDownTimer.cancel();

            }

        };
        countDownTimer.start();
    }

    private void updateCoundDownText() {
        int minutes = (int) (mTimeLeftInMillies / 1000) / 60;
        int secound = (int) (mTimeLeftInMillies / 1000) % 60;
        String timeformate = String.format(Locale.getDefault(), "%02d:%02d", minutes, secound) + " s";
        tvArriveTime.setText("Arrival time:- " + timeformate);
    }

    private void initView() {
       /* registerLocationUpdates();
        ll_second=findViewById(R.id.ll_second);
        ll_first=findViewById(R.id.ll_first);
        ll_popup=findViewById(R.id.ll_popup);
        ll_popup1=findViewById(R.id.ll_popup1);
        ivBack=findViewById(R.id.ivBack);
        btnScanParcel=findViewById(R.id.btnScanParcel);
        btnScanParcel1=findViewById(R.id.btnScanParcel1);
        ivBack.setVisibility(View.GONE);
        tvArriveTime=findViewById(R.id.tvArriveTime);
        dailogTool();
        button3=findViewById(R.id.button3);
        tv_distance = findViewById(R.id.tvDistance);
        tv_time = findViewById(R.id.tvTime);
        tvTitle=findViewById(R.id.tvTitle);
        btnAbandoneOrder=findViewById(R.id.btnAbandoneOrder);
        btnAbandoneOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AbandonOrderActivity.class));
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AbandonOrderActivity.class));
            }
        });
        tvTitle.setText(getResources().getString(R.string.complete_delivery));
        checkLocationPermission();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        CheckifLocationEnabled();

        btnScanParcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ParcelScaningDelivery2Activity.class));
            }
        });

        btnScanParcel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ParcelScaningDelivery2Activity.class));
            }
        });

        ll_popup1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ParcelScaningDelivery2Activity.class));
            }
        });



        timer();*/
    }

    void dailogTool() {
        /*LinearLayout ll_emg;
        ll_emg=findViewById(R.id.ll_emg);
        ll_emg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonUtils.dailogToolTrip(CompleteDeliveryActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }
        });*/
    }

    void timer() {
        timer = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (isPaused || isCanceled) {
                    //If the user request to cancel or paused the
                    //CountDownTimer we will cancel the current instance
                    cancel();
                } else {
                    mTimeLeftInMillies = millisUntilFinished;

                }

            }

            public void onFinish() {
                timer.cancel();
                ll_first.setVisibility(View.GONE);
                ll_popup.setVisibility(View.GONE);
                ll_popup1.setVisibility(View.VISIBLE);
                tvTitle.setText(getResources().getString(R.string.recieved));
                ll_second.setVisibility(View.VISIBLE);
                origin_lat = 26.8553218;
                origin_long = 75.7564754;
                dest_lat = 26.8553218;
                dest_long = 75.7564754;
                registerLocationUpdates();


            }

        };
        timer.start();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

//        checkPermission();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
//        checkPermission();
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        buildGoogleApiClient();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            Toast.makeText(this, "no permission granted", Toast.LENGTH_LONG).show();
        }


    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void registerLocationUpdates() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.removeUpdates(listener);
            locationManager = null;
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin/destination of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String parameters = str_origin + "&" + str_dest;
        String output = "json";
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyBFD1uYq5FgfLP6ebm6cQZKtfmJvfyeCRc";

    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception downloading:", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void CheckifLocationEnabled() {
        LocationManager lm = (LocationManager) CompleteDeliveryActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            continueloadmap();
        } else {
            enableLoc();
        }
    }

    private void enableLoc() {

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(CompleteDeliveryActivity.this).addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("", "All location settings are satisfied.");
                        timer1.cancel();
                        continueloadmap();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        timer1.cancel();
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(CompleteDeliveryActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("", "PendingIntent unable to execute request.");
                        }
                        break;
                }
            }
        });
    }

    private void CheckifLocationEnabled1() {
        locationManager = (LocationManager) CompleteDeliveryActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        } else {
            enableLoc1();
        }
    }

    private void enableLoc1() {

        mGoogleApiClient = new GoogleApiClient.Builder(CompleteDeliveryActivity.this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d("", "All location settings are satisfied.");
                        Toast.makeText(getApplicationContext(), "enableLoc1 Success", Toast.LENGTH_SHORT).show();
                        //  continueloadmap();
                        timer1.cancel();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Toast.makeText(getApplicationContext(), "enableLoc1 Failed", Toast.LENGTH_SHORT).show();
                        timer1.cancel();
                        Log.i("", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(CompleteDeliveryActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("", "PendingIntent unable to execute request.");
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {

                timer1 = new Timer();
                timer1.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {


                        CheckifLocationEnabled1();
                    }
                }, 0, 2000);
                continueloadmap();
            }
        } catch (Exception ex) {
            Toast.makeText(CompleteDeliveryActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckifLocationEnabled();
    }

    private void continueloadmap() {
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);

        }

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                continueloadmap();


            } else
                showSettingsAlert(this);
        }
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();


                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            if (result != null) {
                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points;

                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    lineOptions.width(13);
                    lineOptions.color(getResources().getColor(R.color.colorBlueDark));
                    lineOptions.startCap(new SquareCap());
                    lineOptions.endCap(new SquareCap());
                    lineOptions.jointType(ROUND);

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {

                        HashMap<String, String> point = path.get(j);

                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");

                            continue;
                        } else if (j == 1) { // Get duration from the list
                            duration = (String) point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);

                    }

                    if (!listLatLng.isEmpty()) {
                        listLatLng.clear();
                    } else {
                        listLatLng.addAll(points);

                    }


                    lineOptions.addAll(points);

                    //    lineOptions.geodesic(true);

                    tv_distance.setText(distance + " away");
                    tv_time.setText(duration);


                    if (lineOptions != null) {

                        if (polylineObj != null) {
                            polylineObj.setPoints(lineOptions.getPoints());
                        } else {
                            polylineObj = mGoogleMap.addPolyline(lineOptions);

                        }

                    }
                    PolylineOptions greyOptions = new PolylineOptions();
                    greyOptions.width(13);
                    greyOptions.color(getResources().getColor(R.color.colorBlueDark));
                    greyOptions.startCap(new SquareCap());
                    greyOptions.endCap(new SquareCap());
                    greyOptions.jointType(ROUND);
                    greyPolyLine = mGoogleMap.addPolyline(greyOptions);

                    // animatePolyLine();


                    // Drawing polyline in the Google Map for the i-th route
                    // if (points.size() != 0) mGoogleMap.addPolyline(lineOptions);
                    //  mGoogleMap.addPolyline(lineOptions);


                }
            }

        }
    }


}
