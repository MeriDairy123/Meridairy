package b2infosoft.milkapp.com.DeliveryBoy.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserItem;
import b2infosoft.milkapp.com.GoogleMap.GoogleMapHelper;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GPSTracker;

import static android.content.Context.LOCATION_SERVICE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getLocationPoint;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMarkerIconFromDrawable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static com.google.android.gms.common.GooglePlayServicesUtil.isGooglePlayServicesAvailable;


public class MapMarkerPalaceFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "Map Route====";
    Toolbar toolbar;
    Context mContext;

    GPSTracker gpsTracker;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    ArrayList<LatLng> mMarkerPoints;
    List<BeanUserItem> userItemsList;

    View view;
    SessionManager sessionManager;
    String provider = "";
    /*double latitude = 26.8385702;
    double longtitude = 75.7897532;*/
    //Jheerota
    double latitude = 26.3145388;
    double longtitude = 75.0828837;
    LatLng currentlatLng;
    Location mLastLocation, endPoint;
    double distance = 0;

    Criteria criteria;
    Drawable iconDriver, iconCustomer;
    BitmapDescriptor markerDriverIcon, markerCustomer;
    int height1 = 80;
    int width1 = 80;
    int position = 0;
    Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=Jheerota,Rajasthan&destination=Kishangarh,Rajasthan&travelmode=driving&waypoints=Joraverpura,Akodiya,ChhotaLamba,Katsura,Rari");
    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private LocationManager mLocationManager;
    private MarkerOptions mMarkerOptions;
    /*Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
    startActivity(mapIntent);*/
    /*String uri = "http://maps.google.com/maps?saddr=" + "9982878"+","+"76285774"+"&daddr="+"9992084"+","+"76286455";
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
    startActivity(intent);*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_map, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(mContext.getString(R.string.MapRoute));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
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
        // Getting currentlatLng Location From GPS

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
        showToast(mContext, "Location Changed");
        currentlatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentlatLng);

        markerOptions.icon(markerDriverIcon);
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //     mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
        //  mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
      /*if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } */


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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, GoogleMapHelper.ZOOM_LEVEL));
            latitude = mLastLocation.getLatitude();
            longtitude = mLastLocation.getLongitude();
            mMap.setOnMarkerClickListener(this);

            System.out.println("currentlatLng==location=====" + currentlatLng);
            if (mMap != null) {
                for (int i = 0; i < userItemsList.size(); i++) {

                    double mLat = userItemsList.get(i).latitude;
                    double mLng = userItemsList.get(i).longitude;
                    endPoint = getLocationPoint(mLat, mLng);
                    distance = mLastLocation.distanceTo(endPoint);
                    distance = distance / 1000;
                    LatLng userLoc = new LatLng(endPoint.getLatitude(), endPoint.getLongitude());

                    System.out.println("currentlatLng==location=====" + currentlatLng);
                    System.out.println("userLoc==location=====" + userLoc);
                    System.out.println("endPoint===lat==" + endPoint.getLatitude());
                    System.out.println("endPoint==lng=====" + endPoint.getLongitude());
                    System.out.println("  Name==" + userItemsList.get(i).getUser_name() + "==distance=====" + distance);
                    Marker marker = createMarker(userLoc, userItemsList.get(i).getUser_name(), userItemsList.get(i).getAddress(), userItemsList.get(i).getMilk_plan_name());
                    marker.setTag(i);
                }
            }
        }

    }

    protected Marker createMarker(LatLng latLng, String title, String address, String snippet) {

        return mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(markerCustomer));
        //   .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        endPoint = getLocationPoint(marker.getPosition().latitude, marker.getPosition().longitude);

        distance = mLastLocation.distanceTo(endPoint);
        if (marker.getPosition() != currentlatLng) {
            position = (int) (marker.getTag());
            dialogMarkerCustomer();
        }
        return false;
    }

    private void dialogMarkerCustomer() {


        Dialog dialog = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.dialog_marker_deliveryboy_customer);

        ImageView imgClosed;
        TextView tvCustomerName, tvAddress;
        Button btnStart;
        BeanUserItem beanUserItem = userItemsList.get(position);

        // set the custom dialog components - text, image and button
        btnStart = dialog.findViewById(R.id.btnStart);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvCustomerName = dialog.findViewById(R.id.tvCustomerName);
        tvAddress = dialog.findViewById(R.id.tvAddress);
        tvCustomerName.setText(beanUserItem.getUser_name());
        tvAddress.setText(beanUserItem.getAddress());

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
                System.out.println("endPoint===lat==" + endPoint.getLatitude());
                System.out.println("endPoint==lng=====" + endPoint.getLongitude());
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentlatLng + "&daddr=" + endPoint.getLatitude() + "," + endPoint.getLongitude()));
                startActivity(intent);
            }
        });

        dialog.show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
