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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import static b2infosoft.milkapp.com.appglobal.Constant.NewBuyerRegister;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellMilkPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

public class NewBuyerFragment extends Fragment implements View.OnClickListener {
    Button btnCheckUser, btnNext;
    Context mContext;
    Toolbar toolbar;
    Spinner spinChartCat, spinOpeningBal;
    EditText ediName, ediFatherName, ediPhoneNumber, ediAccountNo, ediIFSC,
            ediBankName, ediVillage, ediAddress, ediOpeningAmount, ediEveningMilk, ediMorningMilk, ediPricePerLtr;

    RecyclerView recyclerView;
    SessionManager sessionManager;

    DatabaseHandler databaseHandler;
    Fragment fragment = null;
    Bundle bundle = null;
    String customerId = "", cID = "", strName = "", strFatherName = "", strPhoneNumber = "", strAddress = "",
            strAdharNumber = "", strAccountNo = "", strIFSC = "", strBankName = "", strOpenbalType = "", strOpeningAmt = "0", strVillage = "",
            catChartId = "", strPricePerKg, strMorningMilk, strEveningMilk, userGroupId = "", strPlanArray = "", tempCustomerType = "", strfrom = "", fromWhere = "";
    ArrayList<BeanAddProductItem> mPlanList;
    View view;
    private ArrayList<BeanCatChartItem> beanCatChartItemList = new ArrayList<>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_new_buyer, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        initView();

        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Buyer));

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        spinChartCat = view.findViewById(R.id.spinChartCat);

        ediPhoneNumber = view.findViewById(R.id.ediPhoneNumber);
        ediFatherName = view.findViewById(R.id.ediFatherName);
        ediName = view.findViewById(R.id.ediName);
        ediMorningMilk = view.findViewById(R.id.ediMorningMilk);
        ediEveningMilk = view.findViewById(R.id.ediEveningMilk);
        ediPricePerLtr = view.findViewById(R.id.ediPricePerLtr);
        ediPhoneNumber = view.findViewById(R.id.ediPhoneNumber);
        ediAddress = view.findViewById(R.id.ediAddress);
        ediAccountNo = view.findViewById(R.id.ediAccountNo);
        ediIFSC = view.findViewById(R.id.ediIFSC);
        ediBankName = view.findViewById(R.id.ediBankName);
        ediVillage = view.findViewById(R.id.ediVillage);
        spinOpeningBal = view.findViewById(R.id.spinOpeningBal);
        ediOpeningAmount = view.findViewById(R.id.ediOpeningAmount);
        btnCheckUser = view.findViewById(R.id.btnCheckUser);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnNext = view.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        btnCheckUser.setOnClickListener(this);

        ediPricePerLtr.setText(sessionManager.getValueSesion(Key_SellMilkPrice));

        ediPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().trim().length() == 10) {
                    checkUser();
                } else {
                    ediFatherName.setText("");
                    ediName.setText("");
                }
            }
        });
        geCLRChartList();
        initSpinOpenBalance();
        getProductList();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                strName = ediName.getText().toString();
                strFatherName = ediFatherName.getText().toString();
                strPhoneNumber = ediPhoneNumber.getText().toString();
                strAccountNo = ediAccountNo.getText().toString();
                strIFSC = ediIFSC.getText().toString();
                strBankName = ediBankName.getText().toString();
                strVillage = ediVillage.getText().toString();
                strAddress = ediAddress.getText().toString();
                strOpeningAmt = ediOpeningAmount.getText().toString();
                strPricePerKg = ediPricePerLtr.getText().toString();
                strMorningMilk = ediMorningMilk.getText().toString();
                strEveningMilk = ediEveningMilk.getText().toString();

                if (!strName.equals("") && !strPhoneNumber.equals("")) {
                    //if (cID.equals("")) {
                    if (!strMorningMilk.equals("") || !strEveningMilk.equals("")) {
                        addCustomer();
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_enter_either_morning_or_evening_session_milk_weight));
                    }

                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Field_Can_be_empty));
                }
                break;

        }
    }

    private void checkUser() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() == 0) {
                        btnNext.setVisibility(View.VISIBLE);
                    } else {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        cID = jsonObject.getString("id");
                        ediName.setText(jsonObject.getString("name"));
                        ediFatherName.setText(jsonObject.getString("father_name"));
                        btnNext.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("phone_number", ediPhoneNumber.getText().toString()).build();
        caller.addRequestBody(body);
        caller.execute(CheckBuyerMobile);
    }

    private void addCustomer() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        cID = jsonObject.getString("id");

                        addCustomerListInDatabase(mContext, true);
                        bundle = new Bundle();
                        fragment = new SaleMilkCustomerListFragment();
                        bundle.putString("CustomerID", cID);
                        bundle.putString("Name", ediName.getText().toString());
                        bundle.putString("FatherName", ediFatherName.getText().toString());
                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        strName = UtilityMethod.nameFirstLatterCapitalize(strName);
        System.out.println("nameCapitalized>>" + strName);
        strFatherName = UtilityMethod.nameFirstLatterCapitalize(strFatherName);
        System.out.println("nameCapitalized=c_Fname>>" + strFatherName);

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("name", strName)
                .addEncoded("father_name", strFatherName)
                .addEncoded("address", strAddress)
                .addEncoded("phone_number", strPhoneNumber)
                .addEncoded("adhar", strAdharNumber)
                .addEncoded("acno", strAccountNo)
                .addEncoded("bank_name", strBankName)
                .addEncoded("ifsc_code", strIFSC)
                .addEncoded("village", strVillage)
                .addEncoded("address", strAddress)
                .addEncoded("opening_type", strOpenbalType)
                .addEncoded("opn_amount", strOpeningAmt)
                .addEncoded("morning_milk", strMorningMilk)
                .addEncoded("evening_milk", strEveningMilk)
                .addEncoded("price_per_ltr", strPricePerKg)
                .build();
        caller.addRequestBody(body);

        caller.execute(NewBuyerRegister);
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
        balitem.add(mContext.getString(R.string.select) + " " + mContext.getString(R.string.paymentType));
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
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);

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

                        recyclerViewUI();
                    }

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
            params.height = 200;
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
                selectedPlan.add(mList.get(i).getPlan_status());

            }
        }

        System.out.println("selectedPlan===>>>>" + selectedPlan.toString());

        strPlanArray = selectedPlan.toString();

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
        public UpdateMilkPlan_ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.uplate_milk_plan_row_item, parent, false);
            return new UpdateMilkPlan_ItemAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final UpdateMilkPlan_ItemAdapter.MyViewHolder holder, final int position) {
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
            }

        }
    }

}
