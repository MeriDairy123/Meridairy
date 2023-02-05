package b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Dairy.SellMilk.SearchPlaceFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.addDeliverBoyAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.appglobal.Constant.updateDeliverBoyAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 27-July-19.
 */

public class AddOrEditDeliveryBoyFragment extends Fragment {


    Context mContext;

    Toolbar toolbar;
    SessionManager sessionManager;
    EditText ediName, ediMobileno, ediFatherName;
    EditText ediAddress;
    Button btnSubmit;
    String strId = "", strName = "", strFname = "", strMobile = "", strAddress = "";
    String latitude = "0.0", longitude = "0.0";
    String dairyid = "";
    View view;
    Bundle bundle;
    String strKeyAddress = "address_refress_notification";
    BroadcastReceiver receiver;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_deliveryboy, container, false);

        mContext = getActivity();

        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(KEY_UserID);

        bundle = getArguments();

        initView();
        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        ediName = view.findViewById(R.id.ediName);
        ediMobileno = view.findViewById(R.id.ediMobileno);
        ediFatherName = view.findViewById(R.id.ediFatherName);
        ediAddress = view.findViewById(R.id.ediAddress);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        toolbar.setTitle(mContext.getString(R.string.Add));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ediAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_location_address = "";
                str_location_city = "";
                str_location_state = "";
                str_location_postCode = "";
                str_location_Latitude = "";
                str_location_Longitude = "";

                Fragment fragment = new SearchPlaceFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.add(R.id.dairy_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        if (bundle != null) {
            toolbar.setTitle(mContext.getString(R.string.Edit));
            strId = bundle.getString("id");
            strName = bundle.getString("name");
            //  strFname=bundle.getString("fatherName");
            strMobile = bundle.getString("mobile");
            strAddress = bundle.getString("address");

            ediName.setText(strName);
            //  ediFatherName.setText(strFname);
            ediMobileno.setText(strMobile);
            ediAddress.setText(strAddress);
            btnSubmit.setText(mContext.getString(R.string.UPDATE));
            ediMobileno.setVisibility(View.GONE);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setAddress();

            }
        };
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = ediName.getText().toString().trim();
                strMobile = ediMobileno.getText().toString().trim();
                strFname = ediFatherName.getText().toString().trim();

                strAddress = ediAddress.getText().toString().trim();


                if (strName.length() == 0) {
                    ediName.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.PleaseEnterName));

                } else if (strMobile.length() < 10) {
                    ediName.clearFocus();
                    ediMobileno.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_enter_valid_mobile_number));
                } else if (strAddress.length() == 0) {
                    ediFatherName.clearFocus();
                    ediAddress.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Street_Address));
                } else {
                    ediName.clearFocus();
                    ediMobileno.clearFocus();
                    ediFatherName.clearFocus();
                    ediAddress.clearFocus();
                    if (isNetworkAvaliable(mContext)) {
                        addUpdateEmployee();
                    }


                }

            }
        });

    }


    public void setAddress() {
        if (str_location_address.length() > 0) {
            latitude = str_location_Latitude;
            longitude = str_location_Longitude;
            ediAddress.setText(str_location_address);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(strKeyAddress));
        setAddress();
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    private void addUpdateEmployee() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {


                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            if (jsonObject.has("error")) {
                                showToast(mContext, jsonObject.getString("error"));
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            //.addEncoded("father_name", strFname);


            if (strId.length() == 0) {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", dairyid)
                        .addEncoded("phone_number", strMobile)
                        .addEncoded("name", strName)
                        .addEncoded("address", strAddress)
                        .addEncoded("lat", latitude)
                        .addEncoded("lng", longitude)
                        .build();
                webServiceCaller.addRequestBody(body);
                webServiceCaller.execute(addDeliverBoyAPI);
            } else {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("id", strId)
                        .addEncoded("status", "1")
                        .addEncoded("dairy_id", dairyid)
                        .addEncoded("phone_number", strMobile)
                        .addEncoded("name", strName)
                        .addEncoded("address", strAddress)
                        .addEncoded("lat", latitude)
                        .addEncoded("lng", longitude)
                        .build();
                webServiceCaller.addRequestBody(body);
                webServiceCaller.execute(updateDeliverBoyAPI);
            }
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }


}