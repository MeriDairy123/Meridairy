package b2infosoft.milkapp.com.DeliveryBoy.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserItem;
import b2infosoft.milkapp.com.GoogleMap.DirectionsJSONParser;
import b2infosoft.milkapp.com.GoogleMap.GoogleMapHelper;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GPSTracker;

import static android.content.Context.LOCATION_SERVICE;
import static b2infosoft.milkapp.com.appglobal.Constant.MAPDirectionAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.downloadUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMarkerIconFromDrawable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "Map Route====";
    Context mContext;
    Geocoder geocoder;
    GPSTracker gpsTracker;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> mMarkerPoints;
    List<BeanUserItem> userItemsList;
    String str_WayPoints = "";
    View view;
    SessionManager sessionManager;
    String provider = "";
    double latitude = 26.8385702;
    double longtitude = 75.7897532;
    Criteria criteria;
    Drawable iconDriver, iconCustomer;
    BitmapDescriptor markerDriverIcon, markerCustomer;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private MarkerOptions mMarkerOptions;
    private String str_mOrigin;
    private String str_mDestination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_map, container, false);
        mContext = getActivity();


        sessionManager = new SessionManager(mContext);
        // Enable MyLocation Button in the Map
        gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longtitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        iconDriver = mContext.getResources().getDrawable(R.drawable.ic_bike);
        iconCustomer = mContext.getResources().getDrawable(R.drawable.ic_profile_default);
        markerDriverIcon = getMarkerIconFromDrawable(iconDriver);
        markerCustomer = getMarkerIconFromDrawable(iconCustomer);


        String json = "";
        userItemsList = new ArrayList<>();
        json = sessionManager.getValueSesion("userLists");
        userItemsList = new Gson().fromJson(json, new TypeToken<List<BeanUserItem>>() {
        }.getType());
        isGooglePlayServicesAvailable(mContext);
        System.out.println("userItemsList====" + json);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mMarkerPoints = new ArrayList<>();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.google_map_style));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        initLocation();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    private void initLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        criteria = new Criteria();
        // Getting the name of the best provider
        provider = mLocationManager.getBestProvider(criteria, true);
        // Getting Current Location From GPS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        mLastLocation = mLocationManager.getLastKnownLocation(provider);

        if (mLastLocation != null) {
            onLocationChanged(mLastLocation);
            getMyLocation();
        }

        System.out.println("userItemsList==size=====" + userItemsList.size());
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        showToast(mContext, "Location Changed");
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(current);

        markerOptions.icon(markerDriverIcon);
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }*/


    }


    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
        getMyLocation();
    }


    private void getMyLocation() {
        if (mMap != null) {
            mMap.clear();
        }

        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        LatLng mOrigin = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, GoogleMapHelper.ZOOM_LEVEL));

        System.out.println("mOrigin==location=====" + mOrigin);
        if (mMap != null) {
            for (int i = 0; i < userItemsList.size(); i++) {
                int lat = i + 1 * 5;
                latitude = latitude + lat;
                longtitude = longtitude + lat;
                createMarker(latitude,
                        longtitude,
                        userItemsList.get(i).getUser_name(), userItemsList.get(i).getMilk_plan_name());
            }
        }

        if (mOrigin != null && str_mDestination != null) {
            drawRoute();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void drawRoute() {
        // Getting URL to the Google Directions API
        String url = getDirectionsURL();

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }

    private String getDirectionsURL() {
        // Origin of route
        String str_origin = "origin=" + str_mOrigin;

        String str_waypoints = "&waypoints=" + str_WayPoints;
        // Destination of route
        String str_dest = "&destination=" + str_mDestination;
        // Sensor enabled
        String sensor = "&sensor=false";
        String mode = "&travelmode=driving";
        String key = "&key=" + mContext.getString(R.string.google_location_key1);


        System.out.println("str_origin=======" + str_origin);
        System.out.println("str_dest=======" + str_dest);
        System.out.println("str_waypoints=======" + str_waypoints);


        // Building the parameters to the web service
        String parameters = str_origin + str_waypoints + str_dest + sensor + key + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = MAPDirectionAPI + output + "?" + parameters;
        System.out.println("MAP ==url=====" + url);

        return url;
    }


    private void drawMarker(LatLng point) {

        mMarkerPoints.add(point);
        // Creating MarkerOptions
        mMarkerOptions = new MarkerOptions();
        // Setting the position of the marker
        mMarkerOptions.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (mMarkerPoints.size() == 1) {

            mMarkerOptions.icon(markerDriverIcon);
            mMarkerOptions.title("Driver");
        } else if (mMarkerPoints.size() == 2) {
            mMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        }

        // Add new marker to the Google Map Android API V2
        mMap.addMarker(mMarkerOptions);

    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println("result=====" + result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
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

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            System.out.println("result==Traversing===" + result.toArray().toString());

            ArrayList<LatLng> points = new ArrayList<>();
            PolylineOptions lineOptions = new PolylineOptions();
            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                    drawMarker(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

}
