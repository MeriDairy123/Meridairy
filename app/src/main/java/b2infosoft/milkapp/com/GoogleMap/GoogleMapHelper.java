package b2infosoft.milkapp.com.GoogleMap;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import b2infosoft.milkapp.com.R;

public class GoogleMapHelper {
    private static final int API_AVAILABILITY_REQUEST_CODE = 45656;

    public static int ZOOM_LEVEL = 18;
    public int TILT_LEVEL = 25;

    public void isGooglePlayServicesAvailable(Context mContext) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(mContext);
        if (ConnectionResult.SUCCESS == status) {

        } else {
            if (googleApiAvailability.isUserResolvableError(status))
                Toast.makeText(mContext, "Please Install google play services to use this application", Toast.LENGTH_LONG).show();
            else
                googleApiAvailability.getErrorDialog((Activity) mContext, status, API_AVAILABILITY_REQUEST_CODE).show();
        }

    }

    /**
     * @param latLng in which position to Zoom the camera.
     * @return the [CameraUpdate] with Zoom and Tilt level added with the given position.
     */
    public CameraUpdate buildCameraUpdate(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(TILT_LEVEL)
                .zoom(ZOOM_LEVEL)
                .build();
        return CameraUpdateFactory.newCameraPosition(cameraPosition);
    }

    /**
     * @param position where to draw the [com.google.android.gms.maps.model.Marker]
     * @return the [MarkerOptions] with given properties added to it.
     */
    public MarkerOptions getDriverMarkerOptions(LatLng position, Context mContext) {
        MarkerOptions options = getMarkerOptions(R.drawable.ic_bike, position);
        options.flat(true);
        return options;
    }

    private MarkerOptions getMarkerOptions(int resource, LatLng position) {
        return new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(resource))
                .position(position);
    }
}
