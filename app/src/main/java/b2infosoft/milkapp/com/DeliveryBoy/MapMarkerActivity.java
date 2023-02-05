package b2infosoft.milkapp.com.DeliveryBoy;


import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GPSTracker;

import static b2infosoft.milkapp.com.appglobal.Constant.DEFAULT_ZOOM;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getLocationPoint;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMarkerIconFromDrawable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;


public class MapMarkerActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "Map Route====";
    Context mContext;
    Toolbar toolbar;

    GPSTracker gpsTracker;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> mMarkerPoints;
    List<BeanUserItem> userItemsList;

    View view;
    SessionManager sessionManager;
    String provider = "";
    double latitude = 26.3335405;
    double longtitude = 75.0832827;

    Location endPoint;
    LatLng currentlatLng, userlatLng;
    int position = 0;
    double distance = 0;

    Criteria criteria;
    Drawable iconDriver, iconCustomer;
    BitmapDescriptor markerDriverIcon, markerCustomer;
    int height1 = 80;
    int width1 = 80;
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_route);
        mContext = MapMarkerActivity.this;
        toolbar = findViewById(R.id.toolbar);

        sessionManager = new SessionManager(mContext);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(mContext.getString(R.string.MapRoute));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Enable MyLocation Button in the Map
        gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longtitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }

        iconDriver = mContext.getResources().getDrawable(R.drawable.ic_bike);
        iconCustomer = mContext.getResources().getDrawable(R.drawable.ic_milk_home_marker);
        markerDriverIcon = getMarkerIconFromDrawable(iconDriver);
        markerCustomer = getMarkerIconFromDrawable(iconCustomer);


        String json = "";
        userItemsList = new ArrayList<>();
        json = sessionManager.getValueSesion("userLists");
        userItemsList = new Gson().fromJson(json, new TypeToken<List<BeanUserItem>>() {
        }.getType());
        isGooglePlayServicesAvailable(mContext);
        System.out.println("userItemsList====" + json);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    mContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.google_map_style));
        //  mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

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

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
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
        // showToast(mContext, "Location Changed");
        currentlatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentlatLng);
        markerOptions.icon(markerDriverIcon);
        mCurrLocationMarker = mMap.addMarker(markerOptions);

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
        System.out.println("mLastLocation==location=====" + mLastLocation.getLatitude());
        if (mLastLocation != null) {


            currentlatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
            latitude = mLastLocation.getLatitude();
            longtitude = mLastLocation.getLongitude();
            mMap.setOnMarkerClickListener(this);
            System.out.println("mOrigin==location=====" + currentlatLng);
            if (mMap != null) {
                for (int i = 0; i < userItemsList.size(); i++) {
                    double mLat = userItemsList.get(i).latitude;
                    double mLng = userItemsList.get(i).longitude;
                    endPoint = getLocationPoint(mLat, mLng);
                    distance = mLastLocation.distanceTo(endPoint);
                    distance = distance / 1000;
                    LatLng userLoc = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());
                    System.out.println("  Name==" + userItemsList.get(i).getUser_name() + "==distance=====" + distance);
                    Marker marker = createMarker(userLoc);

                    marker.setTag(i);
                }
            }
        }

    }

    protected Marker createMarker(LatLng latLng) {


        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(markerCustomer);

        return mMap.addMarker(markerOptions);


    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        userlatLng = marker.getPosition();
        endPoint = getLocationPoint(marker.getPosition().latitude, marker.getPosition().longitude);

        distance = mLastLocation.distanceTo(endPoint);
        if (marker.getPosition().longitude != currentlatLng.longitude) {
            position = (int) (marker.getTag());
            dialogMarkerCustomer();
        }
        return false;
    }

    private void dialogMarkerCustomer() {


        Dialog dialog = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_marker_deliveryboy_customer);

        ImageView imgClosed;
        TextView tvCustomerName, tvAddress;
        ImageView btnStart;
        BeanUserItem beanUserItem = userItemsList.get(position);

        // set the custom dialog components - text, image and button
        btnStart = dialog.findViewById(R.id.btnStart);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvCustomerName = dialog.findViewById(R.id.tvCustomerName);
        tvAddress = dialog.findViewById(R.id.tvAddress);

        tvCustomerName.setText(beanUserItem.getUser_name());
        String address = getAddressFromLatLng(userlatLng);
        tvAddress.setText(address);

        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                System.out.println("currentlatLng===lat==" + currentlatLng);
                System.out.println("userlatLng===lat==" + userlatLng);

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + userlatLng.latitude + "," + userlatLng.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    mContext.startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mContext.startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        showToast(mContext, "Please install a maps application");
                    }
                }

            }
        });

        dialog.show();
    }

    public String getAddressFromLatLng(LatLng latLng) {

        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            StringBuilder sb = new StringBuilder();

            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            sb.append(addresses.get(0).getAddressLine(0)).append("\n"); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            // sb.append(addresses.get(0).getLocality()).append("\n");
            //  sb.append( addresses.get(0).getAdminArea()).append("\n");
            //  sb.append( addresses.get(0).getPostalCode()).append("\n");


            address = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return address;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
