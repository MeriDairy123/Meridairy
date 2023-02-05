package b2infosoft.milkapp.com.ShareAds_Animal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.useful.GPSTracker;

import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;


public class SearchPlaceActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private static final String API_KEY = "AIzaSyDKZk2naZ8z8iEahpq_GhwwzX-l5yDwjoc";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    AutoCompleteTextView autoCompView;
    LinearLayout layoutUseGPS;
    TextView tvCurrentLocation;
    Context mContext;
    GPSTracker gpsTracker;
    ImageView iconSearch, backArrow;
    boolean boolean_permission = false;
    double firstLatitude;
    double firstLongitude;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    GPSTracker gps;
    String address = "";
    String city = "";
    String state = "";
    String postCode = "";
    String country = "";
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_place);
        mContext = SearchPlaceActivity.this;

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        initView();
    }

    private void initView() {
        autoCompView = findViewById(R.id.autoCompleteTextView);
        autoCompView.setVisibility(View.VISIBLE);
        iconSearch = findViewById(R.id.iconSearch);
        backArrow = findViewById(R.id.backArrow);
        tvCurrentLocation = findViewById(R.id.tvCurrentLocation);
        layoutUseGPS = findViewById(R.id.layoutUseGPS);

        layoutUseGPS.setOnClickListener(this);
        iconSearch.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        tvCurrentLocation.setOnClickListener(this);
        autoCompView.setCursorVisible(false);
        autoCompView.setClickable(false);
        autoCompView.setEnabled(false);
        getPermissions();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iconSearch:
                autoCompView.setCursorVisible(true);
                autoCompView.setClickable(true);
                autoCompView.setEnabled(true);
                autoCompView.setHint("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(autoCompView, InputMethodManager.SHOW_IMPLICIT);
                autoCompView.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.list_location_item));
                autoCompView.setOnItemClickListener(this);
                break;
            case R.id.tvCurrentLocation:

                if (tvCurrentLocation.getText().toString().length() > 0) {

                    System.out.println("========str_location_address======search===current========" + str_location_address);
                    finish();
                }

                break;
            case R.id.backArrow:
                finish();
                break;
        }
    }


    public ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            // sb.append("&components=country:US");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            URL url = new URL(sb.toString());
            System.out.println("URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("=========================");
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            //Log.e(LOG_TAG, "Cannot process JSON results", e);
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String location = (String) adapterView.getItemAtPosition(position);
        System.out.println("address======>>>" + location);
        if (Geocoder.isPresent()) {
            try {

                Geocoder gc = new Geocoder(mContext);
                List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                //List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        // ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                        firstLatitude = Double.parseDouble(String.valueOf(a.getLatitude()));
                        firstLongitude = Double.parseDouble(String.valueOf(a.getLongitude()));
                        str_location_Latitude = String.valueOf(a.getLatitude());
                        str_location_Longitude = String.valueOf(a.getLongitude());

                        str_location_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        str_location_city = addresses.get(0).getLocality();
                        str_location_state = addresses.get(0).getAdminArea();
                        str_location_postCode = addresses.get(0).getPostalCode();
                        country = addresses.get(0).getCountryName();

                        System.out.println("str_location_address====" + str_location_address);
                        System.out.println("CountryName" + addresses.get(0).getCountryName());
                        System.out.println("StateName" + addresses.get(0).getAdminArea());
                        System.out.println("CityName" + addresses.get(0).getLocality());

                        System.out.println("Lat>>" + firstLatitude);
                        System.out.println("Long>>" + firstLongitude);
                        finish();
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }

    }

    private void getPermissions() {
        //You already have the permission, just go ahead.
        gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()) {
            //SharedPreferences.Editor editor = permissionStatus.edit();
            //editor.putBoolean(permissionsRequired[0], true);
        } else {
            gpsTracker.showSettingsAlert();
        }

        proceedAfterPermission();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                boolean_permission = true;
                proceedAfterPermission();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ActivityCompat.checkSelfPermission(SearchPlaceActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_PERMISSION_SETTING) {
                boolean_permission = true;
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
        boolean_permission = true;
        gps = new GPSTracker(SearchPlaceActivity.this);
        if (gps.canGetLocation()) {
            firstLatitude = gps.getLatitude();
            firstLongitude = gps.getLongitude();
            str_location_Latitude = String.valueOf(gps.getLatitude());
            str_location_Longitude = String.valueOf(gps.getLongitude());

            System.out.println("Location======>>>" + firstLatitude + "," + firstLongitude);
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(firstLatitude, firstLongitude, 1);
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea();
                state = addresses.get(0).getAdminArea();
                postCode = addresses.get(0).getPostalCode();
                country = addresses.get(0).getCountryName();

                str_location_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                str_location_city = addresses.get(0).getLocality();
                str_location_state = addresses.get(0).getAdminArea();
                str_location_postCode = addresses.get(0).getPostalCode();

                System.out.println("current Address=======>>" + str_location_address);
                System.out.println("address=======>>" + address);
                tvCurrentLocation.setText(address);

                System.out.println("city===>>" + city);
                System.out.println("state===>>" + state);
                System.out.println("country===>>" + country);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            gpsTracker.showSettingsAlert();
        }

    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}
