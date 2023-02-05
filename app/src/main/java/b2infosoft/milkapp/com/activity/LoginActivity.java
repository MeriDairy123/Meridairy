package b2infosoft.milkapp.com.activity;

import static b2infosoft.milkapp.com.activity.LoginViaActivity.LoginUsingQrCode;
import static b2infosoft.milkapp.com.appglobal.Constant.TAG;
import static b2infosoft.milkapp.com.appglobal.Constant.loginAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.requestPasswordAPI;
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
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.reverseTimer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.zxing.Result;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.SmsListener;
import b2infosoft.milkapp.com.Model.CityPojo;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.QRCodeScanner.QrScannerActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.SearchPlaceActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.AppSignatureHashHelper;
import b2infosoft.milkapp.com.useful.SMSReceiver;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, SmsListener {

    private static String user_group_id = "", user_id = "", user_tokens = "";
    Context mContext;
    Button loginButton;
    SessionManager sessionManager;
    EditText etCollectionCenterName, etDairy, etPhoneNumber, etOtp, etUpdateName, etFatherName, etStreetAddress, etAdharNumber;
    Spinner spState, spCity;
    String CollectionCenterName = "", Dairy = "", PhoneNumber = "", Name = "", Otp = "", FatherName = "", StreetAddress = "", AdharNumber = "", stateId = "", CityID = "";
    String strHashKey = "", CheckImage = "";
    View layout_phone, layout_otp, layoutBtn, layout_details;
    TextView tvLoginwithPassword, tvQrCodeLogin;
    ProgressDialog pDlg;
    String refreshedToken = "";
    Firebase reference;
    DatabaseHandler dbHandler;
    TextView tvResendOtp;
    TextView tvTimeCounter;
    int intTimeOtp = 60000;
    int screenVisible = 0;
    boolean isOtpVisible = false;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static String[] PERMISSIONS_LOCATIONN = {Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION};


    SMSReceiver smsReceiver;

    public static void onScanResult(Result result, Context mContext) {
        SessionManager sessionMange = new SessionManager(mContext);
        try {
            //converting the data to json
            String scanOutput = result.getText();
            JSONObject obj = new JSONObject(scanOutput);


            File folder = new File(mContext.getExternalFilesDir(null).toString() + "/test/test" );
            folder.mkdir();
            Log.d(TAG, "onScanResult11212: ");



            user_group_id = obj.getString("user_group_id");
            user_id = obj.getString("user_id");
            user_tokens = obj.getString("user_token");
            sessionMange.setValueSession(SessionManager.user_token, user_tokens);
            if (isConnected()) {
                LoginUsingQrCode(mContext, user_group_id, user_id);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = LoginActivity.this;
        layout_phone = findViewById(R.id.layout_phone);
        tvLoginwithPassword = findViewById(R.id.tvLoginwithPassword);
        tvQrCodeLogin = findViewById(R.id.tvQrCodeLogin);

        layout_otp = findViewById(R.id.layout_otp);
        layoutBtn = findViewById(R.id.layoutBtn);
        layout_details = findViewById(R.id.layout_details);
        if (!hasPermissions(LoginActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, PERMISSION_ALL);
        }


        int permission1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN);
        int permission3 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_PRIVILEGED);
        if (permission1 != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_STORAGE, 1);
        } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATION, 1);
        } else if (permission3 != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATIONN, 1);
        }


        AppSignatureHashHelper appSignatureHashHelper = new AppSignatureHashHelper(LoginActivity.this);
        strHashKey = appSignatureHashHelper.getAppSignatures().get(0);
        // This code requires one time to get Hash keys do comment and share key

        tvQrCodeLogin.setOnClickListener(this);
        dbHandler = DatabaseHandler.getDbHelper(mContext);
        // enableSMSReciver(mContext);
        sessionManager = new SessionManager(this);
        Firebase.setAndroidContext(mContext);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvTimeCounter = findViewById(R.id.tvTimeCounter);
        loginButton = findViewById(R.id.loginButton);
        etCollectionCenterName = findViewById(R.id.etCollectionCenterName);
        etDairy = findViewById(R.id.etDairy);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvResendOtp.setVisibility(View.GONE);

        etOtp = findViewById(R.id.et_Otp);
        etUpdateName = findViewById(R.id.etUpdateName);
        etFatherName = findViewById(R.id.etFatherName);
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etAdharNumber = findViewById(R.id.etAdharNumber);
        spState = findViewById(R.id.spState);
        spCity = findViewById(R.id.spCity);
        etPhoneNumber.setText(tempMobileNumber);
        tvLoginwithPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilityMethod.goNextClass(mContext, LoginWithPasswordActivity.class);

            }
        });
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPassword();
                tvResendOtp.setVisibility(View.GONE);

            }
        });
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
// otp fetch Service

        startSMSListener();
        //ReciveSMS();
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
                        //write your code here to be executed after 1 second
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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    if (layout_phone.getVisibility() == View.VISIBLE) {
                        PhoneNumber = etPhoneNumber.getText().toString();
                        if (PhoneNumber.isEmpty() || PhoneNumber.length() != 10) {
                            showAlertWithButton(mContext, getString(R.string.Please_Enter_Correct_Phone_Number));
                            etPhoneNumber.requestFocus();
                        } else {
                            isOtpVisible = true;
                            requestPassword();
                        }
                    } else if (layout_otp.getVisibility() == View.VISIBLE) {
                        Otp = etOtp.getText().toString().trim();
                        if (Otp.length() < 4) {
                            showAlertWithButton(mContext, getString(R.string.error_field_required));

                        } else {

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

    @Override
    public void onResume() {
        // LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
        if (str_location_address.length() > 0) {

            etStreetAddress.setText(str_location_address);
        }
        if (isOtpVisible) {
            layout_otp.setVisibility(View.VISIBLE);
            layout_phone.setVisibility(View.GONE);
        }
    }

    private void updateUserDetails() {
        if (Name.equals("") && stateId.equals("") && CityID.equals("") && CollectionCenterName.isEmpty() && Dairy.isEmpty() && FatherName.isEmpty() && StreetAddress.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Field_Can_be_empty));
        } else if (Name.isEmpty()) {
            showAlertWithButton(mContext, getString(R.string.Please_Enter_Owner_Name));
            etUpdateName.requestFocus();
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

    @Override
    public void onBackPressed() {
        if (layout_otp.getVisibility() == View.VISIBLE || layout_details.getVisibility() == View.VISIBLE) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setMessage(getString(R.string.Are_You_Sure_Want_To_Exit_From_Login)).setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    UtilityMethod.goNextClass(mContext, LoginViaActivity.class);
                }
            }).setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // do nothing
                    dialog.dismiss();
                }
            }).setIcon(android.R.drawable.ic_dialog_alert).show();
        } else {
            UtilityMethod.goNextClass(mContext, LoginViaActivity.class);
        }

    }

    /****************************************State And getCityAPI Adapter Set ont Spinner****************************************************/
    public void setStateAdapter(final ArrayList<StatePojo> stateList) {

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
                    CityPojo.getCityList(mContext, stateId, "loginAPI");
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
                } else {
                    CityID = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void requestPassword() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Logging...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("status")) {
                        String status = jsonObject.getString("status");
                        String message = "";
                        if (status.equalsIgnoreCase("success")) {
                            message = jsonObject.getString("user_status_message");
                            showToast(LoginActivity.this, message);
                            isOtpVisible = true;
                            layout_otp.setVisibility(View.VISIBLE);
                            layout_phone.setVisibility(View.GONE);
                            layoutBtn.setVisibility(View.GONE);
                            layout_details.setVisibility(View.GONE);


                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tvResendOtp.setVisibility(View.VISIBLE);
                                }
                            }, intTimeOtp);
                            reverseTimer(intTimeOtp, tvTimeCounter);

                        } else {
                            message = jsonObject.getString("message");
                            UtilityMethod.showToast(LoginActivity.this, message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("phone_number", PhoneNumber).addEncoded("sms_hash", strHashKey).build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(requestPasswordAPI);


    }

    public void verifyOtp() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Verifying otp.....", true) {
            @Override
            public void handleResponse(String response) {
                final JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getString("login_status").equalsIgnoreCase("success")) {
                        isOtpVisible = false;

                        tvResendOtp.setVisibility(View.GONE);
                        ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                        pd.setMessage(mContext.getString(R.string.Please_Wait));
                        pd.show();
                        String url = "https://meridairy-25d2d.firebaseio.com/users.json";
                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                reference = new Firebase("https://meridairy-25d2d.firebaseio.com/users");

                                JSONObject firebseJson = new JSONObject();
                                pd.dismiss();
                                System.out.println("firebase database response=== " + s);
                                if (s.equals("null")) {
                                    try {

                                        if (jsonObject.getString("user_group_id").equals("2")) {
                                            CollectionCenterName = nullCheckFunction(jsonObject.getString("center_name"));
                                            pd.dismiss();
                                            CollectionCenterName = nullCheckFunction(jsonObject.getString("center_name"));
                                            if (CollectionCenterName.length() >= 1) {
                                                Constant.FirstTime = "Yes";
                                                Constant.UserID = jsonObject.getString("id");
                                                LoginSessionSucess(jsonObject);
                                            } else {
                                                updateLoginUser(jsonObject);
                                            }


                                        } else if (jsonObject.getString("user_group_id").equals("3") || jsonObject.getString("user_group_id").equals("4")) {
                                            reference.child(jsonObject.getString("unic_customer_for_mobile").trim()).child("password").setValue("111111");
                                            pd.dismiss();
                                            sessionManager.createCustomerLoginSession(jsonObject, Otp, "No");

                                        } else if (jsonObject.getString("user_group_id").equals("5")) {
                                            reference.child(jsonObject.getString("unic_customer_for_mobile").trim()).child("password").setValue("111111");
                                            pd.dismiss();
                                            sessionManager.deliveryBoyLoginSession(jsonObject, Otp, "No");

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {
                                        firebseJson = new JSONObject(s);

                                        if (jsonObject.getString("user_group_id").equals("2")) {
                                            pd.dismiss();
                                            if (!firebseJson.has(PhoneNumber)) {
                                                reference.child(PhoneNumber).child("password").setValue(PhoneNumber);
                                            }
                                            CollectionCenterName = nullCheckFunction(jsonObject.getString("center_name"));
                                            if (CollectionCenterName.length() >= 1) {

                                                Constant.FirstTime = "Yes";
                                                Constant.UserID = jsonObject.getString("id");
                                                LoginSessionSucess(jsonObject);
                                            } else {
                                                updateLoginUser(jsonObject);
                                            }


                                        } else if (jsonObject.getString("user_group_id").equals("3") || jsonObject.getString("user_group_id").equals("4")) {
                                            pd.dismiss();

                                            if (!firebseJson.has(jsonObject.getString("unic_customer_for_mobile"))) {
                                                reference.child(jsonObject.getString("unic_customer_for_mobile").trim()).child("password").setValue("111111");
                                            }
                                            sessionManager.createCustomerLoginSession(jsonObject, Otp, "No");
                                        } else if (jsonObject.getString("user_group_id").equals("5")) {
                                            pd.dismiss();

                                            if (!firebseJson.has(jsonObject.getString("unic_customer_for_mobile"))) {
                                                reference.child(jsonObject.getString("unic_customer_for_mobile").trim()).child("password").setValue("111111");
                                            }
                                            sessionManager.deliveryBoyLoginSession(jsonObject, Otp, "No");


                                        } else {
                                            pd.dismiss();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                                pd.dismiss();
                            }
                        });
                        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
                        request.setRetryPolicy(new RetryPolicy() {
                            @Override
                            public int getCurrentTimeout() {
                                return 50000;
                            }

                            @Override
                            public int getCurrentRetryCount() {
                                return 50000;
                            }

                            @Override
                            public void retry(VolleyError error) throws VolleyError {

                            }
                        });
                        rQueue.add(request);
                    } else {
                        if (jsonObject.has("user_status_message")) {
                            String user_status_message = jsonObject.getString("user_status_message");
                            showAlert(user_status_message, mContext);
                        } else {
                            showAlert(getString(R.string.LoginFailed_Enter_Correct_Mobile_Password), mContext);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("email", PhoneNumber).addEncoded("password", Otp).addEncoded("firebase_tocan", refreshedToken).build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(loginAPI);

    }

    private void LoginSessionSucess(JSONObject jsonObject) {

        reference.child(PhoneNumber).child("password").setValue(PhoneNumber);
        sessionManager.createLoginDairyJson(jsonObject, "No");
        sessionManager.checkDairyLoginAccount(jsonObject);


    }

    private void updateLoginUser(JSONObject jsonObject) {

        try {
            Name = jsonObject.getString("name");
            layout_details.setVisibility(View.VISIBLE);
            layout_otp.setVisibility(View.GONE);
            layout_phone.setVisibility(View.GONE);
            layoutBtn.setVisibility(View.GONE);
            etUpdateName.setText(Name);
            loginButton.setText(mContext.getString(R.string.Save));
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

            StatePojo.getStateList(mContext, "loginAPI");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateRegistrationDetail() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equals("success")) {
                        UtilityMethod.showAlertBoxwithIntent(mContext, jsonObject.getString("user_status_message"), LoginActivity.class);
                        Constant.FirstTime = "Yes";
                        sessionManager.setValueSession(KEY_center_name, CollectionCenterName);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);
                        sessionManager.setValueSession(KEY_FatherName, FatherName);
                        sessionManager.setValueSession(KEY_Adhar, AdharNumber);
                        sessionManager.setValueSession(KEY_Address, StreetAddress);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);
                        sessionManager.updateLoginDairySession(sessionManager.getValueSesion(KEY_UserID), Name, sessionManager.getValueSesion(KEY_Email), sessionManager.getValueSesion(KEY_Mobile), Dairy, FatherName, sessionManager.getValueSesion(Key_UserGroupID), CollectionCenterName, sessionManager.getValueSesion(KEY_User_Type), sessionManager.getValueSesion(KEY_VehicleType), sessionManager.getValueSesion(KEY_PlantId), sessionManager.getValueSesion(KEY_AdvtStatus), sessionManager.getValueSesion(user_token), "No");

                        UtilityMethod.showToastIntent(mContext, mContext.getString(R.string.Please_Generate_PIN_Number), PinCodeActivity.class);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder().addEncoded("id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("name", Name)
                .addEncoded("center_name", CollectionCenterName)
                .addEncoded("dairy_name", Dairy)
                .addEncoded("father_name", FatherName)
                .addEncoded("state_id", stateId)
                .addEncoded("city_id", CityID).addEncoded("address", StreetAddress).addEncoded("adhar", AdharNumber).build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.updateDairyProfile);


    }

    private void startSMSListener() {
        try {
            smsReceiver = new SMSReceiver();
            smsReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            getApplicationContext().registerReceiver(smsReceiver, intentFilter);
            SmsRetrieverClient client = SmsRetriever.getClient(mContext);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvQrCodeLogin:
                Constant.FromWhere = "login";
                Intent intent = new Intent(mContext, QrScannerActivity.class);
                intent.putExtra("from", "login");
                startActivity(intent);
                break;
        }
    }

    @Override
    public void messageReceived(String messageText) {
        etOtp.setText(messageText);
        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        }
    }

    @Override
    protected void onDestroy() {

        if (smsReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
        }
        super.onDestroy();
    }
}
