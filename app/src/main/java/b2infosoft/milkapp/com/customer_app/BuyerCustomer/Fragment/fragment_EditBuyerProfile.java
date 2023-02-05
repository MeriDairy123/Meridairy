package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Dairy.SellMilk.SearchPlaceFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.CheckBuyerMobile;
import static b2infosoft.milkapp.com.appglobal.Constant.customerUpdateAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Address;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_FatherName;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Latitude;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Longitude;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_id;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_name;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_EditBuyerProfile extends Fragment {

    Context mContext;
    Toolbar toolbar;
    View view;
    EditText ediName, ediMobileno, ediFatherName;
    EditText ediDairyOwnerMobileno, ediDairyOwnerName;
    EditText ediAddress;
    Button btnUpdate;
    String strName = "", strFname = "", strMobile = "", strAddress = "",
            strDairyOwnerName = "", strDairyMobileNo = "";
    String latitude = "", longitude = "";
    String strGroupid = "4";
    SessionManager sessionManager;
    String strKeyAddress = "address_refress_notification";
    BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_customer_profile, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        ediName = view.findViewById(R.id.ediName);
        ediMobileno = view.findViewById(R.id.ediMobileno);
        ediFatherName = view.findViewById(R.id.ediFatherName);
        ediAddress = view.findViewById(R.id.ediAddress);

        ediDairyOwnerMobileno = view.findViewById(R.id.ediDairyOwnerMobileno);
        ediDairyOwnerName = view.findViewById(R.id.ediDairyOwnerName);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.Edit_Profile);


        strName = sessionManager.getValueSesion(KEY_Name);
        strMobile = sessionManager.getValueSesion(KEY_Mobile);
        strFname = sessionManager.getValueSesion(KEY_FatherName);
        strAddress = sessionManager.getValueSesion(KEY_Address);
        latitude = sessionManager.getValueSesion(KEY_Latitude);
        longitude = sessionManager.getValueSesion(KEY_Longitude);

        strDairyMobileNo = sessionManager.getValueSesion(KEY_dairy_Mobile);
        strDairyOwnerName = sessionManager.getValueSesion(KEY_dairy_name);


        ediName.setText(strName);
        ediMobileno.setText(strMobile);
        ediFatherName.setText(strFname);
        ediAddress.setText(strAddress);
        ediDairyOwnerMobileno.setText(strDairyMobileNo);
        ediDairyOwnerName.setText(strDairyOwnerName);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setAddress();

            }
        };
        toolbar.setNavigationIcon(R.drawable.ic_back);
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
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.container_body, fragment)
                        .addToBackStack(null).commit();

            }
        });


        ediDairyOwnerMobileno.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 10) {
                    strDairyMobileNo = s.toString().trim();
                    checkDairy();
                }
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = ediName.getText().toString().trim();
                strMobile = ediMobileno.getText().toString().trim();
                strFname = ediFatherName.getText().toString().trim();

                strAddress = ediAddress.getText().toString().trim();
                strDairyMobileNo = ediDairyOwnerMobileno.getText().toString().trim();
                strDairyOwnerName = ediDairyOwnerName.getText().toString().trim();

                if (strName.length() == 0) {
                    ediName.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.PleaseEnterName));

                } else if (strMobile.length() < 10) {
                    ediName.clearFocus();
                    ediMobileno.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_enter_valid_mobile_number));
                } else if (strFname.length() == 0) {
                    ediMobileno.clearFocus();
                    ediFatherName.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Father_Name));
                } else if (strAddress.length() == 0) {
                    ediFatherName.clearFocus();
                    ediAddress.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Street_Address));
                } else if (strDairyMobileNo.length() < 10) {
                    ediAddress.clearFocus();
                    ediDairyOwnerMobileno.requestFocus();
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_enter_valid_mobile_number));
                } else if (strDairyOwnerName.length() == 0) {
                    ediDairyOwnerMobileno.clearFocus();
                    ediDairyOwnerName.requestFocus();

                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_Enter_Owner_Name));
                } else {
                    ediName.clearFocus();
                    ediMobileno.clearFocus();
                    ediFatherName.clearFocus();
                    ediAddress.clearFocus();
                    ediDairyOwnerMobileno.clearFocus();
                    ediDairyOwnerName.clearFocus();
                    updateProfile();
                }

            }
        });

        return view;
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

    public void checkDairy() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        ediDairyOwnerName.setText("");
                    } else {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        //cID = jsonObject.getString("id");
                        ediDairyOwnerName.setText(jsonObject.getString("name"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("phone_number", strDairyMobileNo)
                .build();
        caller.addRequestBody(body);
        caller.execute(CheckBuyerMobile);


    }

    private void updateProfile() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            sessionManager.setValueSession(KEY_dairy_id, jsonData.getString("created_by"));
                            sessionManager.setValueSession(KEY_dairy_name, jsonData.getString("dairy_name"));
                            sessionManager.setValueSession(KEY_Name, strName);
                            sessionManager.setValueSession(KEY_Mobile, strMobile);
                            sessionManager.setValueSession(KEY_FatherName, strFname);
                            sessionManager.setValueSession(KEY_Address, strAddress);
                            sessionManager.setValueSession(KEY_Latitude, latitude);
                            sessionManager.setValueSession(KEY_Longitude, longitude);
                            sessionManager.setValueSession(KEY_dairy_Mobile, strDairyMobileNo);
                            sessionManager.setValueSession(KEY_dairy_name, strDairyOwnerName);
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            showAlertWithButton(mContext, jsonObject.getString("user_status_message"));

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("name", strName)
                    .addEncoded("phone_number", strMobile)
                    .addEncoded("father_name", strFname)
                    .addEncoded("address", strAddress)
                    .addEncoded("user_group_id", strGroupid)
                    .addEncoded("lat", latitude)
                    .addEncoded("lng", longitude)
                    .addEncoded("dairy_owner_phone_number", strDairyMobileNo)
                    .addEncoded("dairy_owner_name", strDairyOwnerName)
                    .build();
            caller.addRequestBody(body);
            caller.execute(customerUpdateAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }
}
