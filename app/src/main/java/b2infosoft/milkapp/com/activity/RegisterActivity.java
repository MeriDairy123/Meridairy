package b2infosoft.milkapp.com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.SmsListener;
import b2infosoft.milkapp.com.Model.CityPojo;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.SearchPlaceActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.AppSignatureHashHelper;
import b2infosoft.milkapp.com.useful.SMSReceiver;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.loginAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.registrationAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Address;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Adhar;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AdvtStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Email;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_FatherName;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_PlantId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_QRCode;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_User_Type;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_VehicleType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_center_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.user_token;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.enableSMSReciver;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextClass;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.reverseTimer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBoxwithIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;

public class RegisterActivity extends AppCompatActivity implements SmsListener {

    String refreshedToken = "";
    SessionManager sessionManager;

    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;


    Button btnSave;
    EditText etCollectionCenterName, etDairy, etPhoneNumber, etName, etOtp,
            etUpdateName, etFatherName, etStreetAddress, etAdharNumber;
    Spinner spState, spCity;
    String CollectionCenterName = "", Dairy = "", PhoneNumber = "", Name = "", Otp = "",
            FatherName = "", StreetAddress = "", AdharNumber = "", stateId = "", CityID = "";
    String strHashKey = "", CheckImage = "";

    View layout_phone, layout_otp, layout_details;
    ProgressDialog pDlg;
    TextView tvTimeCounter;

    int intTimeOtp = 60000;
    SMSReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = RegisterActivity.this;
        sessionManager = new SessionManager(mContext);

        Firebase.setAndroidContext(mContext);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(RegisterActivity.this);
        strHashKey = appSignatureHashHelper.getAppSignatures().get(0);
    printLog("refreshedToken",refreshedToken);

        initView();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(getString(R.string.Are_You_Sure_Want_To_Exit_From_Registration))
                .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        UtilityMethod.goNextClass(mContext, ActivitySelectUserType.class);
                    }
                })
                .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void initView() {

        layout_phone = findViewById(R.id.layout_phone);
        layout_otp = findViewById(R.id.layout_otp);
        layout_details = findViewById(R.id.layout_details);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.SIGNUP));

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnSave = findViewById(R.id.btnSave);
        etCollectionCenterName = findViewById(R.id.etCollectionCenterName);
        etDairy = findViewById(R.id.etDairy);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etName = findViewById(R.id.etName);
        tvTimeCounter = findViewById(R.id.tvTimeCounter);
        etOtp = findViewById(R.id.et_Otp);
        etUpdateName = findViewById(R.id.etUpdateName);
        etFatherName = findViewById(R.id.etFatherName);
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etAdharNumber = findViewById(R.id.etAdharNumber);
        spState = findViewById(R.id.spState);
        spCity = findViewById(R.id.spCity);
        // otp fetch Service
        enableSMSReciver(mContext);

        startSMSListener();
        etStreetAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_location_address = "";
                str_location_city = "";
                str_location_state = "";
                str_location_postCode = "";
                str_location_Latitude = "";
                str_location_Longitude = "";
                Intent i = new Intent(mContext, SearchPlaceActivity.class);
                startActivity(i);
            }
        });
        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    if (pDlg != null && pDlg.isShowing()) {
                        pDlg.dismiss();
                    }
                    if (isConnected()) {
                        Otp = etOtp.getText().toString().trim();
                        verifyOtp();
                    } else {
                        showAlert(mContext.getString(R.string.you_are_not_connected_to_internet), mContext);
                    }
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    if (layout_phone.getVisibility() == View.VISIBLE) {
                        PhoneNumber = etPhoneNumber.getText().toString();
                        Name = etName.getText().toString();
                        if (PhoneNumber.isEmpty() || PhoneNumber.length() != 10) {
                            showAlertWithButton(mContext, getString(R.string.Please_Enter_Correct_Phone_Number));
                            etPhoneNumber.requestFocus();
                        } else if (Name.isEmpty()) {
                            showAlertWithButton(mContext, getString(R.string.Please_Enter_Owner_Name));
                            etName.requestFocus();
                        } else {
                            userRegistration();
                        }
                    } else if (layout_otp.getVisibility() == View.VISIBLE) {
                        Otp = etOtp.getText().toString().trim();
                        if (Otp.length() == 4) {
                            verifyOtp();

                        }
                    } else if (layout_details.getVisibility() == View.VISIBLE) {
                        Name = etUpdateName.getText().toString();
                        CollectionCenterName = etCollectionCenterName.getText().toString();
                        Dairy = etDairy.getText().toString();
                        FatherName = etFatherName.getText().toString();
                        StreetAddress = etStreetAddress.getText().toString();
                        AdharNumber = etAdharNumber.getText().toString();
                        updateUserDetails();
                    }
                } else {
                    showAlert(mContext.getString(R.string.you_are_not_connected_to_internet), mContext);
                }
            }
        });

    }


    /****************************************State And getCityAPI Adapter Set ont Spinner****************************************************/
    public void setStateAdapter(final ArrayList<StatePojo> stateList) {
        System.out.println("StateList====>>>" + "" + stateList.size());
        final ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.Please_Select_State));

        for (int i = 0; i < stateList.size(); i++) {

            list.add(stateList.get(i).sate_name);
        }

        ArrayAdapter<String> stateSpinnAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, list);
        stateSpinnAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spState.setAdapter(stateSpinnAdapter);


        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String selectedState = parent.getItemAtPosition(position).toString();
                if (!selectedState.equals(getString(R.string.Please_Select_State))) {
                    stateId = stateList.get(position - 1).state_id;
                    System.out.println("StateID===>>>" + stateId);
                    CityPojo.getCityList(mContext, stateId, "Register");
                } else {
                    stateId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void setCityAdapter(final ArrayList<CityPojo> cityList) {
        System.out.println("CityList====>>>" + "" + cityList.size());
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.Please_Select_City));

        for (int i = 0; i < cityList.size(); i++) {
            list.add(cityList.get(i).city_name);
        }

        ArrayAdapter<String> stateSpinnAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, list);
        stateSpinnAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spCity.setAdapter(stateSpinnAdapter);
        stateSpinnAdapter.notifyDataSetChanged();


        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedCity = parent.getItemAtPosition(position).toString();
                if (!selectedCity.equals(getString(R.string.Please_Select_City))) {

                    CityID = cityList.get(position - 1).city_id;
                    System.out.println("CityID====>>>" + CityID);
                } else {
                    CityID = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void updateUserDetails() {
        if (Name.equals("") && stateId.equals("") && CityID.equals("") && CollectionCenterName.isEmpty() && Dairy.isEmpty()
                && FatherName.isEmpty() && StreetAddress.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Field_Can_be_empty));
        } else if (Name.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Please_Enter_Owner_Name));
            etName.requestFocus();
        } else if (CollectionCenterName.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Please_Enter_Collection_Center_Name));
            etCollectionCenterName.requestFocus();
        } else if (Dairy.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Please_Enter_Dairy_Name));
            etDairy.requestFocus();
        } else if (FatherName.isEmpty()) {

            showAlertWithButton(mContext, getString(R.string.Please_Enter_Father_Name));
            etFatherName.requestFocus();
        } else if (stateId.equals("")) {
            showAlertWithButton(mContext, getString(R.string.Please_Select_State));
            spState.requestFocus();
        } else if (CityID.equals("")) {
            showAlertWithButton(mContext, getString(R.string.Please_Select_City));
            spCity.requestFocus();
        } else if (StreetAddress.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Please_Enter_Street_Address));
            etStreetAddress.requestFocus();
        } else {
            updateRegistrationDetail();
        }
    }

    public void userRegistration() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Registering user ...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        layout_otp.setVisibility(View.VISIBLE);
                        layout_details.setVisibility(View.GONE);
                        layout_phone.setVisibility(View.GONE);
                        reverseTimer(intTimeOtp, tvTimeCounter);
                    } else {
                        String message = jsonObject.getString("user_status_message");
                        if (message.equalsIgnoreCase("The phone number has already been taken.")) {
                            tempMobileNumber = PhoneNumber;
                            showAlertBoxwithIntent(mContext, message, LoginActivity.class);
                        } else {
                            showAlertWithButton(mContext, message);

                            etPhoneNumber.requestFocus();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("phone_number", PhoneNumber)
                .addEncoded("sms_hash", strHashKey)
                .addEncoded("name", Name)
                .addEncoded("center_name", CollectionCenterName)
                .addEncoded("dairy_name", Dairy)
                .addEncoded("father_name", FatherName)
                .addEncoded("state_id", stateId)
                .addEncoded("city_id", CityID)
                .addEncoded("address", StreetAddress)
                .addEncoded("adhar", AdharNumber)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(registrationAPI);


    }

    public void verifyOtp() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                "Verifying otp.....", true) {
            @Override
            public void handleResponse(String response) {
                final JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("login_status");
                    if (success.equalsIgnoreCase("success")) {

                        layout_details.setVisibility(View.VISIBLE);
                        layout_otp.setVisibility(View.GONE);
                        layout_phone.setVisibility(View.GONE);
                        etUpdateName.setText(Name);
                        btnSave.setText(R.string.Save);
                        Constant.FirstTime = "Yes";
                        Constant.UserID = jsonObject.getString("id");
                        sessionManager.setValueSession(KEY_UserID, jsonObject.getString("id"));
                        sessionManager.setValueSession(KEY_Mobile, jsonObject.getString("phone_number"));
                        sessionManager.setValueSession(KEY_Name, jsonObject.getString("name"));
                        sessionManager.setValueSession(KEY_Email, jsonObject.getString("email"));
                        sessionManager.setValueSession(Key_UserGroupID, jsonObject.getString("user_group_id"));
                        sessionManager.setValueSession(KEY_User_Type, jsonObject.getString("type"));
                        sessionManager.setValueSession(KEY_VehicleType, jsonObject.getString("vehicle_type"));
                        sessionManager.setValueSession(KEY_PlantId, jsonObject.getString("plant_id"));
                        sessionManager.setValueSession(KEY_AdvtStatus, jsonObject.getString(KEY_AdvtStatus));
                        sessionManager.setValueSession(user_token, jsonObject.getString(user_token).trim());
                        sessionManager.setValueSession(KEY_QRCode, NO);
                        StatePojo.getStateList(mContext, "Register");
                        if (smsReceiver != null) {
                            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(smsReceiver);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("email", PhoneNumber)
                .addEncoded("password", Otp)
                .addEncoded("firebase_tocan", refreshedToken)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(loginAPI);

    }

    public void updateRegistrationDetail() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        showAlertBoxwithIntent(mContext, getString(R.string.Registration_Successfull), LoginActivity.class);
                        Constant.FirstTime = "Yes";
                        sessionManager.setValueSession(KEY_center_name, CollectionCenterName);
                        sessionManager.setValueSession(KEY_FatherName, FatherName);
                        sessionManager.setValueSession(KEY_Adhar, AdharNumber);
                        sessionManager.setValueSession(KEY_Address, StreetAddress);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);

                        sessionManager.updateLoginDairySession(
                                sessionManager.getValueSesion(KEY_UserID), Name, sessionManager.getValueSesion(KEY_Email),
                                sessionManager.getValueSesion(KEY_Mobile), Dairy, FatherName,
                                sessionManager.getValueSesion(Key_UserGroupID), CollectionCenterName,
                                sessionManager.getValueSesion(KEY_User_Type), sessionManager.getValueSesion(KEY_VehicleType),
                                sessionManager.getValueSesion(KEY_PlantId), sessionManager.getValueSesion(KEY_AdvtStatus), sessionManager.getValueSesion(user_token), "No");
                        sessionManager.setValueSession(KEY_Address, StreetAddress);
                        goNextClass(mContext, PinCodeActivity.class);
                        //  showAlertBoxwithIntent(mContext, getString(R.string.Please_Generate_PIN_Number), PinCodeActivity.class);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("name", Name)
                .addEncoded("center_name", CollectionCenterName)
                .addEncoded("dairy_name", Dairy)
                .addEncoded("father_name", FatherName)
                .addEncoded("state_id", stateId)
                .addEncoded("city_id", CityID)
                .addEncoded("address", StreetAddress)
                .addEncoded("adhar", AdharNumber)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.updateDairyProfile);


    }


    @Override
    public void onResume() {
        // LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
        if (str_location_address.length() > 0) {

            etStreetAddress.setText(str_location_address);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            getApplicationContext().registerReceiver(smsReceiver, intentFilter);
            SmsRetrieverClient client = SmsRetriever.getClient(RegisterActivity.this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // API successfully started
                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Fail to start API
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onDestroy() {

        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(smsReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void messageReceived(String messageText) {
        etOtp.setText(messageText);
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(mContext).unregisterReceiver(smsReceiver);
        }
    }
}




