package b2infosoft.milkapp.com.Dairy.Customer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.firebase.client.Firebase;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
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
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
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

public class AddCustomerFragmentold extends Fragment implements View.OnClickListener {

    Context mContext;
    Spinner spinChartCat;

    EditText etCName, etCFatherName, etCPhoneNumber, etCAddress,
            etCAdharNumber, etCid, etunic_customer, etRatePerKg;

    Button btnSave, btnSaveRate;

    String customerId = "", CName = "", CFatherName = "", CPhoneNumber = "", CAddress = "",
            CAdharNumber = "", catChartId = "", ratePerKg;
    // DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    Toolbar toolbar;
    TextView toolbar_title;
    String fromWhere = "";
    TextInputLayout et_CID, Textetunic_customer;

    LinearLayout layoutProfile, layoutSpinChart, layoutEditRate, layoutRate;
    RadioButton radioButtonProfile, radioButtonRate;
    DatabaseHandler databaseHandler;


    ArrayList<BeanCatChartItem> beanCatChartItemList = new ArrayList<>();

    String userGroupId = "", oldMob = "", tempCustomerType = "", strfrom = "";
    Bundle bundle = null;
    View view;
    private RadioGroup radioType;
    private RadioButton radioSaller, radioBuyer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        Firebase.setAndroidContext(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getActivity()).onBackPressed();

            }
        });
        // geCLRChartList();
        sessionManager = new SessionManager(mContext);
        radioButtonProfile = view.findViewById(R.id.radioButtonProfile);
        radioButtonRate = view.findViewById(R.id.radioButtonRate);

        et_CID = view.findViewById(R.id.et_CID);
        Textetunic_customer = view.findViewById(R.id.tvInputUnic_customer);
        spinChartCat = view.findViewById(R.id.spinChartCat);
        etCName = view.findViewById(R.id.ediName);
        etunic_customer = view.findViewById(R.id.ediUniqCustomer);
        etCid = view.findViewById(R.id.ediCid);
        etCFatherName = view.findViewById(R.id.ediFatherName);
        etCPhoneNumber = view.findViewById(R.id.ediPhoneNumber);
        etCAddress = view.findViewById(R.id.ediAddress);
        etCAdharNumber = view.findViewById(R.id.ediAdharNumber);
        etRatePerKg = view.findViewById(R.id.ediRatePerKg);
        btnSave = view.findViewById(R.id.btnSave);
        btnSaveRate = view.findViewById(R.id.btnSaveRate);
        layoutSpinChart = view.findViewById(R.id.layoutSpinChart);
        layoutProfile = view.findViewById(R.id.layoutProfile);

        layoutEditRate = view.findViewById(R.id.layoutEditRate);
        layoutRate = view.findViewById(R.id.layoutRate);


        radioType = view.findViewById(R.id.radioType);
        radioSaller = view.findViewById(R.id.radioSaller);
        radioBuyer = view.findViewById(R.id.radioBuyer);
        radioType.clearCheck();

        radioButtonProfile.setOnClickListener(this);
        radioButtonRate.setOnClickListener(this);
        btnSaveRate.setOnClickListener(this);

        toolbar_title.setText(mContext.getString(R.string.ADD_Customer));

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioSaller:

                        layoutSpinChart.setVisibility(View.VISIBLE);
                        userGroupId = "3";
                        if (customerId.length() > 0) {
                            layoutEditRate.setVisibility(View.VISIBLE);
                            layoutRate.setVisibility(View.GONE);

                        }
                        geCLRChartList();

                        break;
                    case R.id.radioBuyer:
                        userGroupId = "4";
                        layoutSpinChart.setVisibility(View.GONE);
                        if (customerId.length() > 0) {
                            layoutEditRate.setVisibility(View.GONE);
                            layoutRate.setVisibility(View.GONE);
                        }
                        break;
                }


            }
        });
        bundle = getArguments();
        if (bundle == null) {
            toolbar_title.setText(mContext.getString(R.string.ADD_Customer));
            layoutProfile.setVisibility(View.VISIBLE);
        } else {
            //Edit Case
            radioButtonProfile.setVisibility(View.VISIBLE);
            layoutEditRate.setVisibility(View.VISIBLE);

            layoutProfile.setVisibility(View.GONE);
            layoutRate.setVisibility(View.GONE);
            toolbar_title.setText(getString(R.string.Edit_Customer));
            fromWhere = bundle.getString("from");
            customerId = bundle.getString("CustomerID");
            etCName.setText(bundle.getString("Name"));
            etCFatherName.setText(bundle.getString("FatherName"));
            etCAddress.setText(bundle.getString("Address"));
            etCPhoneNumber.setText(bundle.getString("Mobile"));
            ratePerKg = bundle.getString("rate");
            etRatePerKg.setText(ratePerKg);
            userGroupId = bundle.getString("user_group_id");
            catChartId = bundle.getString("category_chart_id");
            oldMob = etCPhoneNumber.getText().toString();
            System.out.println("Rate>>>" + bundle.getString("rate"));
            System.out.println("userGroupId>>>>" + userGroupId);
            CAdharNumber = bundle.getString("Aadhar");
            etCAdharNumber.setText(CAdharNumber);
            etunic_customer.setText(bundle.getString("unic_customer"));
            Textetunic_customer.setVisibility(View.VISIBLE);
            etunic_customer.setVisibility(View.VISIBLE);

            btnSave.setText(mContext.getString(R.string.UPDATE));
        }
        if (userGroupId.equals("3")) {
            radioSaller.setChecked(true);
            tempCustomerType = radioSaller.getText().toString();
        } else if (userGroupId.equals("4")) {
            radioBuyer.setChecked(true);
            tempCustomerType = radioBuyer.getText().toString();
        }


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CName = etCName.getText().toString();
                CFatherName = etCFatherName.getText().toString();
                CPhoneNumber = etCPhoneNumber.getText().toString();
                CAddress = etCAddress.getText().toString();
                CAdharNumber = etCAdharNumber.getText().toString();
                System.out.println("user_group_id==" + userGroupId);
                if (userGroupId.equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.PleaseSelectCustomerType));
                } else if (userGroupId.equals("3") && catChartId.length() == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.selectChartCategory));
                } else if (CName.trim().equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Customer_Name));
                    etCName.requestFocus();
                } else if (CFatherName.trim().equals("")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Father_Name));
                    etCFatherName.requestFocus();
                } else if (CPhoneNumber.trim().equals("") || CPhoneNumber.length() != 10) {
                    showAlertWithButton(mContext, mContext.getString(R.string.please_fill_correct_mobile_number));
                    etCPhoneNumber.requestFocus();
                } else {
                    if (fromWhere.equalsIgnoreCase("UserListAdapter")) {

                        updateCustomer();
                    } else {
                        addCustomer();
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
                            .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                            .addEncoded("phone_number", etCPhoneNumber.getText().toString())
                            .build();
                    serviceCaller.addRequestBody(body);

                    serviceCaller.execute(CheckBuyerMobile);
                }
            }
        });

        return view;
    }


    public void updateCustomerMobileNumber() {
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


                .addEncoded("old_number", "" + oldMob)
                .addEncoded("phone_number", etCPhoneNumber.getText().toString())
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(updateCustomerMobileNumberAPI);

    }

    public void addCustomer() {

        String c_name = UtilityMethod.nameFirstLatterCapitalize(CName);
        String c_Fname = UtilityMethod.nameFirstLatterCapitalize(CFatherName);

        String c_Address = "";
        if (!CAddress.equals("")) {
            c_Address = UtilityMethod.nameFirstLatterCapitalize(CAddress);
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

                        FromWhere = "btnCustomer";
                        if (userGroupId.equals("4")) {
                            reloadBuyerCustomer();
                        } else {
                            addCustomerListInDatabase(mContext, true);
                        }
                        showAlertBox(mContext, mContext.getString(R.string.Customer_Added_Successfully));
                        getActivity().onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("name", c_name)
                .addEncoded("father_name", c_Fname)
                .addEncoded("address", c_Address)
                .addEncoded("phone_number", CPhoneNumber)
                .addEncoded("price_per_ltr", "0")
                .addEncoded("morning_milk", "0")
                .addEncoded("evening_milk", "0")
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("adhar", CAdharNumber)
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
                        radioType.clearCheck();
                        if (userGroupId.equals("4")) {
                            reloadBuyerCustomer();
                        } else {
                            addCustomerListInDatabase(mContext, true);
                        }
                        showAlertBox(mContext, mContext.getString(R.string.Customer_Added_Successfully));
                        getActivity().onBackPressed();

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
        String c_Fname = UtilityMethod.nameFirstLatterCapitalize(CFatherName);
        String c_Address = "";
        if (!CAddress.equals("")) {
            c_Address = UtilityMethod.nameFirstLatterCapitalize(CAddress);
        }

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("phone_number", CPhoneNumber)
                .addEncoded("name", c_name)
                .addEncoded("address", c_Address)
                .addEncoded("father_name", c_Fname)
                .addEncoded("adhar", CAdharNumber)
                .addEncoded("id", customerId)
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("unic_customer", etunic_customer.getText().toString())

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
                        if (databaseHandler.getBuyerList().size() != 0) {
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
                ratePerKg = etRatePerKg.getText().toString();

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
                        } else {
                            addCustomerListInDatabase(mContext, true);
                        }
                        showToast(mContext, mContext.getString(R.string.Rate_Updated_Successfully));
                        getActivity().onBackPressed();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", customerId)
                .addEncoded("entry_type", "3")
                .addEncoded("price", ratePerKg)
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
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
        if (getArguments() != null && radioSaller.isChecked()) {
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
}
