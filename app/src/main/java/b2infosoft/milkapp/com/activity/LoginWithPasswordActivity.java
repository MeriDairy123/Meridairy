package b2infosoft.milkapp.com.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BeanUserLoginAccount;
import b2infosoft.milkapp.com.Model.CityPojo;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.UserID;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.appglobal.Constant.updateDairyProfile;
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
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextClass;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;

public class LoginWithPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    Button loginButton;
    SessionManager sessionManager;
    EditText etCollectionCenterName, etDairy, etPhoneNumber, etOtp,
            etUpdateName, etFatherName, etStreetAddress, etAdharNumber;
    Spinner spState, spCity;
    String CollectionCenterName = "", Dairy = "", PhoneNumber = "", Name = "", Otp = "",
            FatherName = "", StreetAddress = "", AdharNumber = "", stateId = "", CityID = "";
    String FromWhere = "", CheckImage = "";
    TextView tvLoginOtp;
    View layout_phone, layout_details;
    TextView tvLoginHelpMessage;
    ProgressDialog pDlg;
    String refreshedToken = "";
    Firebase reference;
    DatabaseHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_password);
        layout_phone = findViewById(R.id.layout_phone);
        tvLoginHelpMessage = findViewById(R.id.tvLoginHelpMessage);
        tvLoginOtp = findViewById(R.id.tvLoginOtp);

        layout_details = findViewById(R.id.layout_details);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        mContext = LoginWithPasswordActivity.this;
        dbHandler = DatabaseHandler.getDbHelper(mContext);

        sessionManager = new SessionManager(this);
        Firebase.setAndroidContext(mContext);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();

        tvLoginOtp.setOnClickListener(this);

        loginButton = findViewById(R.id.loginButton);
        etCollectionCenterName = findViewById(R.id.etCollectionCenterName);
        etDairy = findViewById(R.id.etDairy);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        etOtp = findViewById(R.id.et_Otp);
        etUpdateName = findViewById(R.id.etUpdateName);
        etFatherName = findViewById(R.id.etFatherName);
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etAdharNumber = findViewById(R.id.etAdharNumber);
        spState = findViewById(R.id.spState);
        spCity = findViewById(R.id.spCity);
        etPhoneNumber.setText(tempMobileNumber);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    if (layout_phone.getVisibility() == View.VISIBLE) {
                        PhoneNumber = etPhoneNumber.getText().toString();
                        Otp = etOtp.getText().toString().trim();
                        if (PhoneNumber.isEmpty() || PhoneNumber.length() != 10) {
                            showAlertWithButton(mContext, getString(R.string.Please_Enter_Correct_Phone_Number));
                            etPhoneNumber.requestFocus();
                        } else if (Otp.isEmpty()) {
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

    private void updateUserDetails() {
        if (Name.equals("") && stateId.equals("") && CityID.equals("") && CollectionCenterName.isEmpty() && Dairy.isEmpty()
                && FatherName.isEmpty() && StreetAddress.isEmpty()) {
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
        if (layout_details.getVisibility() == View.VISIBLE) {
            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(mContext);
            }
            builder.setMessage(getString(R.string.Are_You_Sure_Want_To_Exit_From_Login))
                    .setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            UtilityMethod.goNextClass(mContext, LoginActivity.class);
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
        } else {
            UtilityMethod.goNextClass(mContext, LoginActivity.class);
        }

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
                    System.out.println("StateID======>>>" + stateId);
                    CityPojo.getCityList(mContext, stateId, "LoginWithPass");
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
        System.out.println("CityList==>>>" + "" + cityList.size());
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
                    System.out.println("CityID===>>>" + CityID);
                } else {
                    CityID = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void verifyOtp() {
        @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                "Logging....", true) {
            @Override
            public void handleResponse(String response) {
                final JSONObject jsonObject;

                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("login_status");
                    if (success.equals("success")) {
                        final ProgressDialog pd = new ProgressDialog(LoginWithPasswordActivity.this);
                        pd.setMessage(mContext.getString(R.string.Please_Wait));
                        pd.show();
                        String url = "https://meridairy-25d2d.firebaseio.com/users.json";
                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                reference = new Firebase("https://meridairy-25d2d.firebaseio.com/users");
                                System.out.println("firebase database response=== " + s);
                                pd.dismiss();
                                if (s.equals("null")) {
                                    try {
                                        if (jsonObject.getString("user_group_id").equals("2")) {
                                            CollectionCenterName = nullCheckFunction(jsonObject.getString("center_name"));
                                            System.out.println("center from web=== " + CollectionCenterName);
                                            pd.dismiss();
                                            CollectionCenterName = nullCheckFunction(jsonObject.getString("center_name"));
                                            System.out.println("center from firebase== " + CollectionCenterName);
                                            if (CollectionCenterName.length() >= 1) {
                                                Constant.FirstTime = "Yes";
                                                Constant.UserID = jsonObject.getString("id");
                                                LoginSessionSucess(jsonObject);
                                            } else {
                                                updateLoginUser(jsonObject);
                                            }


                                        } else if (jsonObject.getString("user_group_id").equals("3") ||
                                                jsonObject.getString("user_group_id").equals("4")) {
                                            pd.dismiss();

                                            PhoneNumber = jsonObject.getString("unic_customer_for_mobile").trim();
                                            reference.child(jsonObject.getString("unic_customer_for_mobile")).child("password").setValue("111111");
                                            sessionManager.createCustomerLoginSession(jsonObject, Otp, "No");
                                        } else if (jsonObject.getString("user_group_id").equals("5")) {
                                            pd.dismiss();

                                            PhoneNumber = jsonObject.getString("unic_customer_for_mobile").trim();
                                            reference.child(PhoneNumber).child("password").setValue("111111");
                                            sessionManager.deliveryBoyLoginSession(jsonObject, Otp, "No");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    try {
                                        JSONObject firebseJson = new JSONObject(s);

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
                                            pd.dismiss();

                                        } else if (jsonObject.getString("user_group_id").equals("5")) {
                                            pd.dismiss();
                                            if (!firebseJson.has(jsonObject.getString("unic_customer_for_mobile"))) {
                                                reference.child(jsonObject.getString("unic_customer_for_mobile").trim()).child("password").setValue("111111");
                                            }
                                            sessionManager.deliveryBoyLoginSession(jsonObject, Otp, "No");
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
                                System.out.println("" + volleyError);
                                pd.dismiss();
                            }
                        });
                        RequestQueue rQueue = Volley.newRequestQueue(mContext);
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
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("email", PhoneNumber)
                .addEncoded("password", Otp)
                .addEncoded("firebase_tocan", refreshedToken)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.loginAPI);

    }

    private void LoginSessionSucess(JSONObject jsonObject) {
        try {
            reference.child(PhoneNumber).child("password").setValue(PhoneNumber);
            sessionManager.createLoginDairyJson(jsonObject, "No");
            List<BeanUserLoginAccount> beanUserLoginAccountList = dbHandler.getLoginUserAccList();

            boolean loginYes = false;
            for (int i = 0; i < beanUserLoginAccountList.size(); i++) {

                if (beanUserLoginAccountList.get(i).user_id.equalsIgnoreCase(UserID)) {
                    loginYes = true;
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.user_already_login_in_this_app_message));
                    ((Activity) mContext).finish();
                }
            }
            if (!loginYes) {
                dbHandler.addLoginAccount(UserID, jsonObject.getString("name"), jsonObject.getString("phone_number"),
                        jsonObject.getString("user_group_id"), jsonObject.getString(user_token));

                UtilityMethod.showToastIntent(mContext, mContext.getString(R.string.Please_Generate_PIN_Number), PinCodeActivity.class);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLoginUser(JSONObject jsonObject) {

        try {
            Name = jsonObject.getString("name");
            layout_details.setVisibility(View.VISIBLE);

            layout_phone.setVisibility(View.GONE);
            tvLoginHelpMessage.setVisibility(View.GONE);
            etUpdateName.setText(Name);
            loginButton.setText(R.string.Save);
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

            StatePojo.getStateList(mContext, "LoginWithPass");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void updateRegistrationDetail() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    if (status.equals("success")) {
                        UtilityMethod.showAlertBoxwithIntent(mContext, jsonObject.getString("user_status_message"), LoginWithPasswordActivity.class);
                        Constant.FirstTime = "Yes";
                        sessionManager.setValueSession(KEY_center_name, CollectionCenterName);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);
                        sessionManager.setValueSession(KEY_FatherName, FatherName);
                        sessionManager.setValueSession(KEY_Adhar, AdharNumber);
                        sessionManager.setValueSession(KEY_dairy_name, Dairy);
                        sessionManager.updateLoginDairySession(
                                sessionManager.getValueSesion(KEY_UserID), Name, sessionManager.getValueSesion(KEY_Email),
                                sessionManager.getValueSesion(KEY_Mobile), Dairy, FatherName,
                                sessionManager.getValueSesion(Key_UserGroupID), CollectionCenterName,
                                sessionManager.getValueSesion(KEY_User_Type), sessionManager.getValueSesion(KEY_VehicleType),
                                sessionManager.getValueSesion(KEY_PlantId), sessionManager.getValueSesion(KEY_AdvtStatus), sessionManager.getValueSesion(user_token), "No");
                        sessionManager.setValueSession(KEY_Address, StreetAddress);
                        UtilityMethod.showToastIntent(mContext, getString(R.string.Please_Generate_PIN_Number), PinCodeActivity.class);

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
        webServiceCaller.addRequestBody(body);
        webServiceCaller.execute(updateDairyProfile);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLoginOtp:
                goNextClass(mContext, LoginActivity.class);
        }
    }
}
