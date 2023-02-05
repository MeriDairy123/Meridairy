package b2infosoft.milkapp.com.Dairy.Customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.Model.BeanCatChartItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.CustomerListPojo.addCustomerListInDatabase;
import static b2infosoft.milkapp.com.appglobal.Constant.CheckBuyerMobile;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.NewBuyerRegister;
import static b2infosoft.milkapp.com.appglobal.Constant.PICK_CONTACT;
import static b2infosoft.milkapp.com.appglobal.Constant.TAG;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerMilkRateAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateCustomerMobileNumberAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.pickContactList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddCustomerFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    Toolbar toolbar;

    TextInputLayout tvInputUniqCustomer, tvInputUnicRatePerKg;
    Spinner spinChartCat, spinOpeningBal;

    EditText ediName, ediFatherName, ediPhoneNumber, ediAddress, ediAdharNumber, ediCid,
            ediUniqCustomer, ediRatePerKg, ediAccountNo, ediIFSC, ediBankName, ediVillage, ediOpeningAmount;
    RecyclerView recyclerView;
    Button btnSave, btnSaveRate;
    String customerId = "", oldUniqCustomer = "", strName = "", strFatherName = "", strPhoneNumber = "", strAddress = "",
            strAdharNumber = "", strAccountNo = "", strIFSC = "", strBankName = "", strOpenbalType = "", strOpeningAmt = "0", strVillage = "",
            catChartId = "", strEntryType = "", ratePerKg = "0", strPricePerKg = "0", strMorningMilk = "0", strEveningMilk = "0",
            oldUserGroupId = "", userGroupId = "", oldMob = "", tempCustomerType = "", strPlanArray = "", fromWhere = "";
    Bundle bundle = null;
    ArrayList<BeanAddProductItem> mPlanList;
    private View view, layoutProfile, layoutEditRate, layoutRate, layoutOpenBal, layoutPlan;
    private RadioGroup rgCustomerType, rgRateType;
    private RadioButton radioSaller, radioBuyer, radioButtonProfile, radioButtonRate;
    private RadioButton rbFat, rbFixRate;
    private ArrayList<BeanCatChartItem> beanCatChartItemList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        mContext = getActivity();
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        Firebase.setAndroidContext(mContext);
        toolbar = view.findViewById(R.id.toolbar);

        rgCustomerType = view.findViewById(R.id.radioType);
        rgRateType = view.findViewById(R.id.rgRateType);
        radioSaller = view.findViewById(R.id.radioSaller);
        radioBuyer = view.findViewById(R.id.radioBuyer);
        radioButtonProfile = view.findViewById(R.id.radioButtonProfile);
        radioButtonRate = view.findViewById(R.id.radioButtonRate);
        tvInputUniqCustomer = view.findViewById(R.id.tvInputUnic_customer);
        tvInputUnicRatePerKg = view.findViewById(R.id.tvInputUnicRatePerKg);
        spinChartCat = view.findViewById(R.id.spinChartCat);
        ediName = view.findViewById(R.id.ediName);
        ediUniqCustomer = view.findViewById(R.id.ediUniqCustomer);
        ediCid = view.findViewById(R.id.ediCid);
        ediFatherName = view.findViewById(R.id.ediFatherName);
        ediPhoneNumber = view.findViewById(R.id.ediPhoneNumber);
        ediAddress = view.findViewById(R.id.ediAddress);
        ediAdharNumber = view.findViewById(R.id.ediAdharNumber);
        ediAccountNo = view.findViewById(R.id.ediAccountNo);
        ediIFSC = view.findViewById(R.id.ediIFSC);
        ediBankName = view.findViewById(R.id.ediBankName);
        ediVillage = view.findViewById(R.id.ediVillage);

        spinOpeningBal = view.findViewById(R.id.spinOpeningBal);
        ediOpeningAmount = view.findViewById(R.id.ediOpeningAmount);

        ediRatePerKg = view.findViewById(R.id.ediRatePerKg);
        btnSave = view.findViewById(R.id.btnSave);
        btnSaveRate = view.findViewById(R.id.btnSaveRate);
        layoutOpenBal = view.findViewById(R.id.layoutOpenBal);
        layoutProfile = view.findViewById(R.id.layoutProfile);
        layoutEditRate = view.findViewById(R.id.layoutEditRate);
        layoutRate = view.findViewById(R.id.layoutRate);

        layoutPlan = view.findViewById(R.id.layoutPlan);
        recyclerView = view.findViewById(R.id.recyclerView);

        rgRateType = view.findViewById(R.id.rgRateType);

        rbFat = view.findViewById(R.id.rbFat);
        rbFixRate = view.findViewById(R.id.rbFixRate);

        rgCustomerType.clearCheck();

        radioButtonProfile.setOnClickListener(this);
        radioButtonRate.setOnClickListener(this);

        rbFat.setOnClickListener(this);
        rbFixRate.setOnClickListener(this);
        btnSaveRate.setOnClickListener(this);

        toolbar.setTitle(mContext.getString(R.string.ADD_Customer));

        layoutOpenBal.setVisibility(View.VISIBLE);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();

            }
        });
        geCLRChartList();
        rgCustomerType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (customerId.length() > 0) {
                    layoutEditRate.setVisibility(View.VISIBLE);
                    layoutRate.setVisibility(View.GONE);
                    layoutOpenBal.setVisibility(View.GONE);
                }
                switch (checkedId) {
                    case R.id.radioSaller:
                        strPlanArray = "";
                        userGroupId = "3";
                        layoutPlan.setVisibility(View.GONE);
                        break;
                    case R.id.radioBuyer:
                        userGroupId = "4";
                        layoutPlan.setVisibility(View.VISIBLE);
                        getProductList();
                        break;
                }
            }
        });
        rgRateType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbFat:
                        strEntryType = "2";
                        tvInputUnicRatePerKg.setVisibility(View.VISIBLE);

                        break;
                    case R.id.rbFixRate:
                        strEntryType = "3";
                        tvInputUnicRatePerKg.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
        oldUniqCustomer = "";
        bundle = getArguments();
        if (bundle == null) {
            toolbar.setTitle(mContext.getString(R.string.ADD_Customer));
            layoutProfile.setVisibility(View.VISIBLE);
        } else {
            //Edit Case
            radioButtonProfile.setVisibility(View.VISIBLE);
            layoutEditRate.setVisibility(View.VISIBLE);
            layoutProfile.setVisibility(View.GONE);
            layoutRate.setVisibility(View.GONE);
            toolbar.setTitle(getString(R.string.Edit_Customer));
            fromWhere = bundle.getString("from");
            customerId = bundle.getString("CustomerID");
            ediName.setText(bundle.getString("Name"));
            ediFatherName.setText(bundle.getString("FatherName"));
            ediVillage.setText(bundle.getString("Village"));
            ediAddress.setText(bundle.getString("Address"));
            ediPhoneNumber.setText(bundle.getString("Mobile"));
            ratePerKg = bundle.getString("rate");
            strEntryType = bundle.getString("entry_type");
            ediRatePerKg.setText(ratePerKg);
            oldUserGroupId = bundle.getString("user_group_id");
            userGroupId = bundle.getString("user_group_id");
            catChartId = bundle.getString("category_chart_id");
            oldMob = ediPhoneNumber.getText().toString();
            printLog("Rate>>>", ratePerKg);
            printLog("userGroupId>>>>", userGroupId);

            strAdharNumber = bundle.getString("Aadhar");
            oldUniqCustomer = bundle.getString("unic_customer");
            strAccountNo = bundle.getString("accno");
            strIFSC = bundle.getString("ifsc_code");
            strBankName = bundle.getString("bank_name");
            ediUniqCustomer.setText(oldUniqCustomer);
            ediAdharNumber.setText(strAdharNumber);
            ediAccountNo.setText(strAccountNo);
            ediIFSC.setText(strIFSC);
            ediBankName.setText(strBankName);
            tvInputUniqCustomer.setVisibility(View.VISIBLE);
            ediUniqCustomer.setVisibility(View.VISIBLE);

            btnSave.setText(mContext.getString(R.string.UPDATE));
            initRateType();
        }
        if (userGroupId.equals("3")) {
            radioSaller.setChecked(true);
            tempCustomerType = radioSaller.getText().toString();
        } else if (userGroupId.equals("4")) {
            radioBuyer.setChecked(true);
            tempCustomerType = radioBuyer.getText().toString();
        }
        ediPhoneNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ediPhoneNumber.getRight() - ediPhoneNumber.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        pickContactList(mContext);

                        return true;
                    }
                }
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strName = ediName.getText().toString();
                strFatherName = ediFatherName.getText().toString();
                strPhoneNumber = ediPhoneNumber.getText().toString();
                strVillage = ediVillage.getText().toString();
                strAddress = ediAddress.getText().toString();
                strAdharNumber = ediAdharNumber.getText().toString();
                strAccountNo = ediAccountNo.getText().toString();
                strIFSC = ediIFSC.getText().toString();
                strBankName = ediBankName.getText().toString();
                strOpeningAmt = ediOpeningAmount.getText().toString();
                String uniqCustomerId = ediUniqCustomer.getText().toString();
                int uniqCustomer = 0;
                if (uniqCustomerId.length() > 0) {
                    uniqCustomer = Integer.parseInt(uniqCustomerId);
                }
                printLog("user_group_id==", userGroupId);
                printLog("user_group_id==", strPlanArray);
                if (userGroupId.equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.PleaseSelectCustomerType));
                } else if (catChartId.length() == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.selectChartCategory));
                } else if (strName.trim().equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Customer_Name));
                    ediName.requestFocus();
                } else if (strFatherName.trim().equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Father_Name));
                    ediFatherName.requestFocus();
                } else if (strPhoneNumber.trim().equals("") || strPhoneNumber.length() != 10) {
                    showAlertWithButton(mContext, mContext.getString(R.string.please_fill_correct_mobile_number));
                    ediPhoneNumber.requestFocus();
                } else if (oldUniqCustomer.length() > 0 && uniqCustomer == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Customer_ID));
                    ediPhoneNumber.requestFocus();
                } else {

                    strName = UtilityMethod.nameFirstLatterCapitalize(strName);
                    strFatherName = UtilityMethod.nameFirstLatterCapitalize(strFatherName);
                    if (!strAddress.equals("")) {
                        strAddress = UtilityMethod.nameFirstLatterCapitalize(strAddress);
                    }

                    if (fromWhere.equalsIgnoreCase("UserListAdapter")) {

                        updateCustomer();
                    } else {
                        addCustomer();
                    }
                }
            }
        });
        ediPhoneNumber.addTextChangedListener(new TextWatcher() {
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
                    @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                        @Override
                        public void handleResponse(String response) {

                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                if (jsonArray.length() == 0) {

                                } else {
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                                    ediName.setText(nullCheckFunction(jsonObject.getString("name")));
                                    ediFatherName.setText(nullCheckFunction(jsonObject.getString("father_name")));
                                    ediAdharNumber.setText(nullCheckFunction(jsonObject.getString("adhar")));
                                    ediAddress.setText(nullCheckFunction(jsonObject.getString("address")));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RequestBody body = new FormEncodingBuilder()
                            .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                            .addEncoded("phone_number", ediPhoneNumber.getText().toString())
                            .build();
                    serviceCaller.addRequestBody(body);
                    serviceCaller.execute(CheckBuyerMobile);
                }
            }
        });
        initSpinOpenBalance();
        getProductList();
        return view;
    }

    private void initRateType() {
        if (strEntryType.equals("2")) {
            rbFat.setChecked(true);
        } else if (strEntryType.equals("3")) {
            rbFixRate.setChecked(true);
        }
    }


    public void updateCustomerMobileNumber() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("old_number", "" + oldMob)
                .addEncoded("phone_number", ediPhoneNumber.getText().toString())
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(updateCustomerMobileNumberAPI);

    }

    public void addCustomer() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        rgCustomerType.clearCheck();
                        ediPhoneNumber.requestFocus();
                        ediName.setText("");
                        ediFatherName.setText("");
                        ediPhoneNumber.setText("");
                        ediAddress.setText("");
                        ediAdharNumber.setText("");

                        FromWhere = "btnCustomer";
                        if (userGroupId.equals("4")) {
                            reloadBuyerCustomer();
                        }
                        addCustomerListInDatabase(mContext, true);
                        showAlertBox(mContext, jsonObject.getString("user_status_message"));
                        getActivity().onBackPressed();
                    } else {
                        showAlertBox(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("phone_number", strPhoneNumber)
                .addEncoded("name", strName)
                .addEncoded("father_name", strFatherName)
                .addEncoded("adhar", strAdharNumber)
                .addEncoded("village", strVillage)
                .addEncoded("address", strAddress)
                .addEncoded("price_per_ltr", strPricePerKg)
                .addEncoded("morning_milk", strMorningMilk)
                .addEncoded("evening_milk", strEveningMilk)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("acno", strAccountNo)
                .addEncoded("bank_name", strBankName)
                .addEncoded("ifsc_code", strIFSC)
                .addEncoded("opening_type", strOpenbalType)
                .addEncoded("amount", strOpeningAmt)
                .addEncoded("app_plan_array", strPlanArray)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(NewBuyerRegister);
    }

    private void updateCustomer() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String staus = jsonObject.getString("status");
                    if (staus.equalsIgnoreCase("success")) {
                        rgCustomerType.clearCheck();
                        if (userGroupId.equals("4")) {
                            reloadBuyerCustomer();
                        }
                        addCustomerListInDatabase(mContext, true);
                        showAlertBox(mContext, mContext.getString(R.string.Updating_Success));
                        getActivity().onBackPressed();

                    } else {

                        showAlertBox(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        printLog("customerId>>>>>>", customerId);
        printLog("strPhoneNumber>>>>>>", strPhoneNumber);
        printLog("oldUniqCustomer>>>>>>", oldUniqCustomer);
        printLog("ediUniqCustomer>>>>>>", ediUniqCustomer.getText().toString());


        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("old_user_group_id", oldUserGroupId)
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("id", customerId)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("old_unic_customer", oldUniqCustomer)
                .addEncoded("unic_customer", ediUniqCustomer.getText().toString())
                .addEncoded("phone_number", strPhoneNumber)
                .addEncoded("name", strName)
                .addEncoded("father_name", strFatherName)
                .addEncoded("adhar", strAdharNumber)
                .addEncoded("village", strVillage)
                .addEncoded("address", strAddress)
                .addEncoded("acno", strAccountNo)
                .addEncoded("bank_name", strBankName)
                .addEncoded("ifsc_code", strIFSC)
                .addEncoded("app_plan_array", strPlanArray)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(updateCustomerAPI);
    }

    private void reloadBuyerCustomer() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() != 0) {
                            databaseHandler.deleteBuyerCustomer();
                        }
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject obj = mainJsonArray.getJSONObject(i);

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
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        caller.addRequestBody(body);
        caller.execute(getBuyerMilkListAPI);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.radioButtonRate:
                radioButtonRate.setChecked(true);
                radioButtonProfile.setChecked(false);
                layoutEditRate.setVisibility(View.VISIBLE);
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
                ratePerKg = ediRatePerKg.getText().toString();
                if (!ratePerKg.equals("")) {
                    saveRate();
                } else {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Milk_Rate));
                }
                break;

        }
    }

    private void saveRate() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {


                        if (userGroupId.equals("4")) {
                            reloadBuyerCustomer();
                        }
                        addCustomerListInDatabase(mContext, true);

                        showToast(mContext, mContext.getString(R.string.Rate_Updated_Successfully));
                        Objects.requireNonNull(getActivity()).onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        printLog("strEntryType>>>", strEntryType);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", customerId)
                .addEncoded("entry_type", strEntryType)
                .addEncoded("price", ratePerKg)
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("user_group_id", userGroupId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(updateCustomerMilkRateAPI);
    }

    public void geCLRChartList() {
        beanCatChartItemList = new ArrayList<>();
        ArrayList<String> catChartItem = new ArrayList<>();

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    String selectChart = mContext.getResources().getString(R.string.selectChartCategory);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {

                        catChartItem.add(selectChart);
                        catChartItem.add(mContext.getResources().getString(R.string.generalChart));
                        beanCatChartItemList.add(new BeanCatChartItem("", "", "", selectChart));
                        beanCatChartItemList.add(new BeanCatChartItem("0",
                                mContext.getResources().getString(R.string.generalChart), "", mContext.getResources().getString(R.string.generalChart)));
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            beanCatChartItemList.add(new BeanCatChartItem(jobj.getString("id"),
                                    jobj.getString("name"), "", ""));
                            catChartItem.add(jobj.getString("name"));
                        }
                    }
                    initSpinCatChart(catChartItem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getCategoryChartAPI);
    }

    private void initSpinCatChart(ArrayList<String> catChartItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, catChartItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinChartCat.setAdapter(spinAdapter);
        if (getArguments() != null) {
            for (int i = 0; i < beanCatChartItemList.size(); i++) {
                if (beanCatChartItemList.get(i).id.equals(catChartId)) {
                    spinChartCat.setSelection(i);
                    break;
                }
            }
        }
        spinChartCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    catChartId = beanCatChartItemList.get(position).id;
                } else {
                    catChartId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initSpinOpenBalance() {
        ArrayList<String> balitem = new ArrayList<>();
        balitem.add(mContext.getString(R.string.select) + " ");
        balitem.add(mContext.getString(R.string.credit));
        balitem.add(mContext.getString(R.string.debit));

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, balitem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinOpeningBal.setAdapter(spinAdapter);

        spinOpeningBal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    strOpenbalType = "credit";
                } else if (position == 2) {
                    strOpenbalType = "debit";
                } else {
                    strOpenbalType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void getProductList() {
        mPlanList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        mPlanList = new ArrayList<>();
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            printLog("==Productid>>>", jobj.getString("id")
                                    + " =Prod==Name>>>" + jobj.getString("name"));

                            mPlanList.add(new BeanAddProductItem(jobj.getString("id"),
                                    jobj.getString("name"), nullCheckFunction(jobj.getString("item_weight")),
                                    jobj.getString("discription_product"), jobj.getString("category"),
                                    jobj.getString("category_name"), jobj.getString("item_brand"),
                                    jobj.getString("brand_name"), jobj.getString("item_unit"), jobj.getString("unit_name"),
                                    jobj.getString("item_code"), jobj.getString("images"),
                                    jobj.getString("created_at"), jobj.getInt("low_stock_alert"),
                                    jobj.getInt("initial_quantity"), jobj.getInt("weightc"),
                                    jobj.getInt("tax_check"), jobj.getDouble("opening_rate"),
                                    jobj.getDouble("opening_amt"), jobj.getDouble("price"),
                                    jobj.getDouble("sales_price"), jobj.getDouble("tax"), jobj.getString("plan_status")));

                        }

                    }
                    recyclerViewUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("customer_id", customerId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getUserProductListAPI);

    }

    private void recyclerViewUI() {
        UpdateMilkPlan_ItemAdapter adapter = new UpdateMilkPlan_ItemAdapter(mContext, mPlanList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        if (!mPlanList.isEmpty()) {
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            params.height = 350;
            recyclerView.setLayoutParams(params);
        }

        recyclerView.setAdapter(adapter);

        updatePlan(mPlanList);
    }

    public void updatePlan(ArrayList<BeanAddProductItem> mList) {
        List<String> selectedPlan = new ArrayList<>();
        strPlanArray = "";
        for (int i = 0; i < mList.size(); i++) {

            if (mList.get(i).getPlan_status().equalsIgnoreCase(ONE)) {
                selectedPlan.add(mList.get(i).getId());

            }
        }

        strPlanArray = TextUtils.join(",", selectedPlan);
        printLog("strPlanArray===>>>>", strPlanArray);

    }

    private class UpdateMilkPlan_ItemAdapter extends RecyclerView.Adapter<UpdateMilkPlan_ItemAdapter.MyViewHolder> {

        public Context mContext;
        String one = "1";
        private ArrayList<BeanAddProductItem> mList;


        private UpdateMilkPlan_ItemAdapter(Context mContext, ArrayList<BeanAddProductItem> mlist) {
            this.mContext = mContext;
            this.mList = mlist;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.uplate_milk_plan_row_item, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            BeanAddProductItem albam = mList.get(position);
            holder.tvTitle.setText(albam.getName());
            boolean isSelected = false;
            if (albam.getPlan_status().equals(one)) {
                isSelected = true;
            }
            holder.checkBox.setChecked(isSelected);


            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mList.get(position).getPlan_status().equalsIgnoreCase(one)) {
                        mList.get(position).setPlan_status(ZERO);
                        holder.checkBox.setChecked(false);
                    } else {
                        mList.get(position).setPlan_status(one);
                        holder.checkBox.setChecked(true);
                    }

                    updatePlan(mList);
                }
            });


        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView tvTitle, tvWeight, tvPrice, tvQty;
            public CheckBox checkBox;
            View layoutButton;
            ImageView imgSelected, imgAdd, imgMinus;

            public MyViewHolder(View view) {
                super(view);
                tvTitle = view.findViewById(R.id.tvTitle);
                tvWeight = view.findViewById(R.id.tvWeight);
                tvPrice = view.findViewById(R.id.tvPrice);
                checkBox = view.findViewById(R.id.checkBox);
                imgSelected = view.findViewById(R.id.imgSelected);
                layoutButton = view.findViewById(R.id.layoutButton);
                tvQty = view.findViewById(R.id.tvQty);
                imgAdd = view.findViewById(R.id.imgAdd);
                imgMinus = view.findViewById(R.id.imgMinus);
                layoutButton.setVisibility(View.GONE);
                tvPrice.setVisibility(View.GONE);
                imgSelected.setVisibility(View.GONE);
                tvWeight.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            Uri result = data.getData();

            // get the phone number id from the Uri
            String id = result.getLastPathSegment();
          // query the phone numbers for the selected phone number id
            Cursor c = mContext.getContentResolver().query(
                    result, null,
                    ContactsContract.CommonDataKinds.Phone._ID + "=?",
                    new String[]{id}, null);
            int phoneIdx = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int phoneType = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
            if (c.getCount() == 1) { // contact has a single phone number
                // get the only phone number
                if (c.moveToFirst()) {
                    String phone = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    printLog(" Contact number===", phone);
                    //   phone = c.getString(phoneIdx);
                    printLog(TAG, " Single  contact number: " + phone);
                    phone = phone.replace("+91", "");
                    phone = phone.replace(" ", "");
                    ediPhoneNumber.setText(phone);
                } else {
                    Log.w(TAG, "contact or mobile No results");
                }
            } else if (c.getCount() > 1) {
                final CharSequence[] numbers = new CharSequence[c.getCount()];
                int i = 0;
                if (c.moveToFirst()) {
                    while (!c.isAfterLast()) { // for each phone number, add it to the numbers array
                        String type = (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(this.getResources(), c.getInt(phoneType), ""); // insert a type string in front of the number
                        String phone = c.getString(phoneIdx);
                        printLog(TAG, "List contact number: " + phone);
                        numbers[i++] = phone;
                        c.moveToNext();
                    }
                    // build and show a simple dialog that allows the user to select a number
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.select);
                    builder.setItems(numbers, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String number = (String) numbers[item];
                            int index = number.indexOf(":");
                            number = number.substring(index + 2);
                            number = number.replace("+91", "");
                            number = number.replace(" ", "");

                            // do something with the selected number
                            ediPhoneNumber.setText(number);
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.setOwnerActivity((Activity) mContext);
                    alert.show();
                }
            }
        }
    }

}
