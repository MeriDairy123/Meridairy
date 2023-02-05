package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BuyerMilkCustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.CheckBuyerMobile;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.NewBuyerRegister;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getCustomerListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerMilkRateAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerMobileNumberAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext;

    EditText etCName, etCFatherName, etCPhoneNumber, etCAddress,
            etCAdharNumber, etCid, etunic_customer, etRatePerKg;

    Button btnSave, btnSaveRate;


    String UserID = "", CName = "", CFatherName = "", CPhoneNumber = "", CAddress = "",
            CAdharNumber = "", user_group_id = "", RatePerKg;
    // DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    Toolbar toolbar;

    String fromWhere = "";
    TextInputLayout et_CID, Textetunic_customer;

    LinearLayout layoutProfile, layoutRate, layoutEdit;
    RadioButton radioButtonProfile, radioButtonRate;
    DatabaseHandler databaseHandler;

    ArrayList<String> customerListType = new ArrayList<>();
    String type = "", txt = "";
    ArrayList<CustomerListPojo> mList = new ArrayList<>();
    String oldMob = "";
    String tempCustomerType = "";
    Intent intent;
    private RadioGroup radioType;
    private RadioButton radioSaller, radioBuyer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_customer);
        mContext = AddCustomerActivity.this;
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        Firebase.setAndroidContext(mContext);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sessionManager = new SessionManager(mContext);
        radioButtonProfile = findViewById(R.id.radioButtonProfile);
        radioButtonRate = findViewById(R.id.radioButtonRate);

        et_CID = findViewById(R.id.et_CID);
        Textetunic_customer = findViewById(R.id.tvInputUnic_customer);
        etCName = findViewById(R.id.ediName);
        etunic_customer = findViewById(R.id.ediUniqCustomer);
        etCid = findViewById(R.id.ediCid);
        etCFatherName = findViewById(R.id.ediFatherName);
        etCPhoneNumber = findViewById(R.id.ediPhoneNumber);
        etCAddress = findViewById(R.id.ediAddress);
        etCAdharNumber = findViewById(R.id.ediAdharNumber);
        etRatePerKg = findViewById(R.id.ediRatePerKg);
        btnSave = findViewById(R.id.btnSave);
        btnSaveRate = findViewById(R.id.btnSaveRate);
        layoutProfile = findViewById(R.id.layoutProfile);
        layoutEdit = findViewById(R.id.layoutEdit);
        layoutRate = findViewById(R.id.layoutRate);

        radioType = findViewById(R.id.radioType);
        radioSaller = findViewById(R.id.radioSaller);
        radioBuyer = findViewById(R.id.radioBuyer);
        radioType.clearCheck();

        radioButtonProfile.setOnClickListener(this);
        radioButtonRate.setOnClickListener(this);
        btnSaveRate.setOnClickListener(this);
        customerListType.add("Select Customer Type");
        customerListType.add(getString(R.string.Buyer));
        customerListType.add(getString(R.string.Seller));


        if (FromWhere.equals("Dashboard")) {
            toolbar.setTitle(getString(R.string.ADD_Customer));
            layoutProfile.setVisibility(View.VISIBLE);
        } else if (
                FromWhere.equals("AddCustomer")) {
            toolbar.setTitle(getString(R.string.ADD_Customer));
            etCid.setVisibility(View.VISIBLE);

            et_CID.setVisibility(View.VISIBLE);
            etCid.setEnabled(false);
            etCid.setClickable(false);
            etCid.setText(intent.getStringExtra("CustomerId"));
            etCName.setText(intent.getStringExtra("CustomerName"));
            etCFatherName.setText(intent.getStringExtra("CustomerFatherName"));
            etCAddress.setText(intent.getStringExtra("CustomerAddress"));
            etCPhoneNumber.setText(intent.getStringExtra("CustomerMobile"));
            etCAdharNumber.setText(intent.getStringExtra("CustomerAdharNo"));

        } else if (FromWhere.equals("UserListAdapter")) {
            //Edit Case
            radioButtonProfile.setVisibility(View.VISIBLE);
            layoutEdit.setVisibility(View.VISIBLE);
            layoutProfile.setVisibility(View.GONE);
            toolbar.setTitle(getString(R.string.Edit_Customer));
            intent = getIntent();
            RatePerKg = intent.getStringExtra("rate");

            UserID = intent.getStringExtra("CustomerID");
            etCName.setText(intent.getStringExtra("Name"));
            etCFatherName.setText(intent.getStringExtra("FatherName"));
            etCAddress.setText(intent.getStringExtra("Address"));
            etCPhoneNumber.setText(intent.getStringExtra("Mobile"));
            etRatePerKg.setText(RatePerKg);

            type = intent.getStringExtra("user_group_id");

            //  etCPhoneNumber.setEnabled(false);
            oldMob = etCPhoneNumber.getText().toString();
            System.out.println("Rate====>>>" + intent.getStringExtra("rate"));
            System.out.println("type====>>>" + type);
            //CAdharNumber = intent.getStringExtra("CustomerAdharNo");
            if (intent.getStringExtra("CustomerAdharNo") != null) {
                System.out.println("etCAdharNumber====>>>" + intent.getStringExtra("CustomerAdharNo"));
                if (intent.getStringExtra("CustomerAdharNo").equals("0")) {

                } else {
                    etCAdharNumber.setText(CAdharNumber);
                }
            }
            etunic_customer.setText(intent.getStringExtra("unic_customer"));
            Textetunic_customer.setVisibility(View.VISIBLE);
            etunic_customer.setVisibility(View.VISIBLE);

            btnSave.setText(getString(R.string.UPDATE));
        }

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    // Toast.makeText(MainActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
                    System.out.println("Txt=====>>>" + rb.getText().toString());
                    type = rb.getText().toString();
                }
            }
        });


        if (type.equals("")) {

        } else {
            if (type.equals("3")) {
                radioSaller.setChecked(true);
                tempCustomerType = radioSaller.getText().toString();

            } else {
                radioBuyer.setChecked(true);
                tempCustomerType = radioBuyer.getText().toString();
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("type====>>>" + type);
                System.out.println("btnSave Txt==>>>" + btnSave.getText().toString());
                CName = etCName.getText().toString();
                CFatherName = etCFatherName.getText().toString();
                CPhoneNumber = etCPhoneNumber.getText().toString();
                CAddress = etCAddress.getText().toString();
                CAdharNumber = etCAdharNumber.getText().toString();
                if (CName.trim().equals("")) {
                    showAlertWithButton(mContext, getString(R.string.Please_Enter_Customer_Name));
                    etCName.requestFocus();
                } else if (CFatherName.trim().equals("")) {
                    showAlertWithButton(mContext, getString(R.string.Please_Enter_Father_Name));
                    etCFatherName.requestFocus();
                } else if (CPhoneNumber.trim().equals("") || CPhoneNumber.length() != 10) {
                    showAlertWithButton(mContext, getString(R.string.please_fill_correct_mobile_number));
                    etCPhoneNumber.requestFocus();
                } else {
                    if (FromWhere.equals("Dashboard") || FromWhere.equals("btnCustomer")) {

                        if (type.equals("")) {
                            showAlertWithButton(mContext, getString(R.string.PleaseSelectCustomerType));
                        } else {
                            addCustomer();
                        }

                    } else {
                        //   updateCustomerMobileNumber();
                        updateCustomer();

                        //  }
                    }
                }
            }
        });
        etCPhoneNumber.addTextChangedListener(new TextWatcher() {
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
                    NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                        @Override
                        public void handleResponse(String response) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                if (jsonArray.length() == 0) {
                                   /* etCName.setText("");
                                    etCFatherName.setText("");
                                    etCAdharNumber.setText("");
                                    etCAddress.setText("");*/
                                } else {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    //cID = jsonObject.getString("id");
                                    etCName.setText(jsonObject.getString("name"));
                                    etCFatherName.setText(jsonObject.getString("father_name"));
                                    etCAdharNumber.setText(jsonObject.getString("adhar"));
                                    etCAddress.setText(jsonObject.getString("address"));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };


                    RequestBody body = new FormEncodingBuilder()
                            .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                            .addEncoded("phone_number", etCPhoneNumber.getText().toString())
                            .build();
                    serviceCaller.addRequestBody(body);

                    serviceCaller.execute(CheckBuyerMobile);
                }
            }
        });


    }


    public void updateCustomerMobileNumber() {

        System.out.println("oldMob=====>>" + "" + oldMob);
        System.out.println("newMobile=====>>" + etCPhoneNumber.getText().toString());

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // addCustomerListInDatabase();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", UserID)
                .addEncoded("old_number", "" + oldMob)
                .addEncoded("phone_number", etCPhoneNumber.getText().toString())

                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(updateCustomerMobileNumberAPI);

    }


    private void updateCustomer() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String staus = jsonObject.getString("status");
                    if (staus.equalsIgnoreCase("success")) {
                        System.out.println("Custome Type==update Customer===>>" + type);
                        if (type.equals(getString(R.string.Buyer))) {

                            reloadBuyerCustomer();
                        } else if (!tempCustomerType.equalsIgnoreCase(type)) {
                            reloadBuyerCustomer();
                        }

                        addCustomerListInDatabase();
                        radioType.clearCheck();
                        type = "";
                    } else {
                        JSONObject errorObj = jsonObject.getJSONObject("error");
                        showAlertBox(mContext, errorObj.getString("unic_customer"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        String c_name = UtilityMethod.nameFirstLatterCapitalize(CName);
        System.out.println("nameCapitalized=====>>" + c_name);
        String c_Fname = UtilityMethod.nameFirstLatterCapitalize(CFatherName);
        System.out.println("nameCapitalized====>>" + c_Fname);
        String c_Address = "";
        if (!CAddress.equals("")) {
            c_Address = UtilityMethod.nameFirstLatterCapitalize(CAddress);
            System.out.println("nameCapitalized====>>" + c_Address);
        }

        System.out.println("type  web===>>" + type);
        String userGroup = "3";
        if (type.equals(getString(R.string.Buyer))) {
            userGroup = "4";
        }

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_group_id", userGroup)
                .addEncoded("id", UserID)
                .addEncoded("phone_number", CPhoneNumber)
                .addEncoded("name", c_name)
                .addEncoded("father_name", c_Fname)
                .addEncoded("adhar", CAdharNumber)
                .addEncoded("address", c_Address)

                .addEncoded("unic_customer", etunic_customer.getText().toString())
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(updateCustomerAPI);
    }

    private void reloadBuyerCustomer() {
        final ArrayList<BuyerMilkCustomerListPojo> mList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                System.out.println("reloadBuyerCustomer>>>>>" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            databaseHandler.deleteBuyerCustomer();
                        }
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject obj = mainJsonArray.getJSONObject(i);
                            mList.add(new BuyerMilkCustomerListPojo(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan")));

                            databaseHandler.addBuyerCustomer(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan"));


                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()

                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();

        caller.addRequestBody(body);
        caller.execute(getBuyerMilkListAPI);
    }

    private void addCustomerListInDatabase() {
        mList = new ArrayList<>();
        @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            if (databaseHandler.getCustomerList().size() != 0) {
                                databaseHandler.deleteCustomer();
                            }

                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject obj = mainJsonArray.getJSONObject(i);
                                mList.add(new CustomerListPojo(obj.getString("Userid"),
                                        obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                        obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                        obj.getString("name"), obj.getString("father_name"),
                                        obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                        obj.getString("address"), obj.getString("remaing_amount"), obj.getString("entry_type"),
                                        obj.getString("entry_price"),
                                        obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                        obj.getString("firebase_tocan")));
                                databaseHandler.addCustomer(obj.getString("Userid"),
                                        obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                        obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                        obj.getString("name"), obj.getString("father_name"),
                                        obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                        obj.getString("address"), obj.getString("remaing_amount"), obj.getString("entry_type"),
                                        obj.getString("entry_price"),
                                        obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                        obj.getString("firebase_tocan"));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_group_id", "3")
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        caller.addRequestBody(body);
        caller.execute(getCustomerListAPI);
    }

    public void addCustomer() {

        String c_name = UtilityMethod.nameFirstLatterCapitalize(CName);
        System.out.println("nameCapitalized==>>" + c_name);
        String c_Fname = UtilityMethod.nameFirstLatterCapitalize(CFatherName);
        System.out.println("nameCapitalized==>>" + c_Fname);
        String c_Address = "";
        if (!CAddress.equals("")) {
            c_Address = UtilityMethod.nameFirstLatterCapitalize(CAddress);
            System.out.println("nameCapitalized===>>" + c_Address);
        }
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        radioType.clearCheck();
                        etCPhoneNumber.requestFocus();
                        etCName.setText("");
                        etCFatherName.setText("");
                        etCPhoneNumber.setText("");
                        etCAddress.setText("");
                        etCAdharNumber.setText("");
                        type = "";
                        FromWhere = "btnCustomer";
                        reloadBuyerCustomer();
                        addCustomerListInDatabase();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println("type final===>>>" + type);
        String userGroup = "3";
        if (type.equals(getString(R.string.Buyer))) {
            userGroup = "4";
        }

        RequestBody body = new FormEncodingBuilder()

                .addEncoded("user_group_id", userGroup)
                .addEncoded("id", UserID)
                .addEncoded("phone_number", CPhoneNumber)
                .addEncoded("name", c_name)
                .addEncoded("father_name", c_Fname)
                .addEncoded("adhar", CAdharNumber)
                .addEncoded("address", c_Address)
                .addEncoded("price_per_ltr", "0")
                .addEncoded("morning_milk", "0")
                .addEncoded("evening_milk", "0")
                .addEncoded("unic_customer", etunic_customer.getText().toString())
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();

        serviceCaller.addRequestBody(body);

        serviceCaller.execute(NewBuyerRegister);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.radioButtonRate:
                radioButtonRate.setChecked(true);
                radioButtonProfile.setChecked(false);
                layoutRate.setVisibility(View.VISIBLE);
                layoutProfile.setVisibility(View.GONE);
                break;
            case R.id.radioButtonProfile:
                radioButtonRate.setChecked(false);
                radioButtonProfile.setChecked(true);
                layoutRate.setVisibility(View.GONE);
                layoutProfile.setVisibility(View.VISIBLE);
                break;
            case R.id.btnSaveRate:
                RatePerKg = etRatePerKg.getText().toString();
                if (!RatePerKg.equals("")) {
                    saveRate();
                } else {
                    showAlertWithButton(mContext, getString(R.string.Please_Enter_Milk_Rate));
                }
                break;
        }
    }

    private void saveRate() {
        System.out.println("Customer ==type=====" + type);
        System.out.println("Customer ==RatePerKg=====" + RatePerKg);

        @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        //etRatePerKg.setText("");
                        addCustomerListInDatabase();
                        if (type.equals(getString(R.string.Buyer))) {

                            reloadBuyerCustomer();
                        } else if (!tempCustomerType.equalsIgnoreCase(type)) {
                            reloadBuyerCustomer();
                        }
                        showToast(mContext, getString(R.string.Rate_Updated_Successfully));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", UserID)
                .addEncoded("entry_type", "3")
                .addEncoded("price", RatePerKg)
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(updateCustomerMilkRateAPI);
    }


}
