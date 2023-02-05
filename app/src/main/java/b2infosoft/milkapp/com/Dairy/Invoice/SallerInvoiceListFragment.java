package b2infosoft.milkapp.com.Dairy.Invoice;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.BuyerGenerateInvoice;
import b2infosoft.milkapp.com.Interface.CommonOnClickListener;
import b2infosoft.milkapp.com.Model.BeanDairyCustomerInvoiceItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthNameList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;


public class SallerInvoiceListFragment extends Fragment implements CommonOnClickListener {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    TextView tvAdd;
    EditText et_Search;
    SessionManager sessionManager;
    RecyclerView rvInvoiceList;
    ArrayList<String> monthList;

    ArrayList<BeanDairyCustomerInvoiceItem> CustomerInvoiceList;
    CustomerInvoiceItemAdapter adapter;


    Fragment fragment = null;
    Bundle bundle = null;
    View view;
    String dairyId = "", strMonth = "";
    Spinner spinMonth;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_invoice_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        CustomerInvoiceList = new ArrayList<>();
        monthList = new ArrayList<>();
        monthList.add(mContext.getString(R.string.select) + " " + mContext.getString(R.string.Months));
        monthList.addAll(getMonthNameList());
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView(view);

        return view;
    }

    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        spinMonth = view.findViewById(R.id.spinMonth);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        et_Search = view.findViewById(R.id.et_Search);
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.invoice));
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        bundle = getArguments();
        if (bundle.getString("from").equalsIgnoreCase("tab")) {
            toolbar.setVisibility(View.GONE);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        toolbar_title.setText(mContext.getString(R.string.invoice));

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new BuyerGenerateInvoice();
                goNextFragmentWithBackStack(mContext, fragment);

            }
        });

        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, monthList);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinMonth.setAdapter(spinAdapter);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        int month = calendar.get(Calendar.MONTH) + 1;
        spinMonth.setSelection(month);
        spinMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    strMonth = String.valueOf(position);
                    getCustomersInvoice();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void setCustomersInvoice() {
        rvInvoiceList = view.findViewById(R.id.rvInvoiceList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        rvInvoiceList.setLayoutManager(mLayoutManager);
        adapter = new CustomerInvoiceItemAdapter(mContext, CustomerInvoiceList, this);
        rvInvoiceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                adapter.filterSearch(query.toString());


            }
        });
    }


    public void getCustomersInvoice() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    CustomerInvoiceList = new ArrayList<>();
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        CustomerInvoiceList = new ArrayList<>();

                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        int srNo = 0;
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);
                            srNo = i + 1;
                            String fromDate = dateDDMMYY(object.getString("from_date"));
                            String toDate = dateDDMMYY(object.getString("to_date"));
                            CustomerInvoiceList.add(new BeanDairyCustomerInvoiceItem(object.getString("id"),
                                    object.getString("user_id"), object.getString("unic_customer"), object.getString("user_group_id"),
                                    object.getString("name"), object.getString("status"),
                                    object.getString("month"), fromDate, toDate,
                                    object.getString("type"), object.getString("details"),
                                    object.getDouble("weight"), object.getDouble("amount"), srNo--));
                        }

                        setCustomersInvoice();
                    } else {
                        showAlertWithButton(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyId)
                .addEncoded("type", "list")
                .addEncoded("month", strMonth)
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(Constant.generateSellerInvoiceAPI);

    }


    @Override
    public void onAdapterItemClick(int position, String from) {
        Fragment fragment;
        Bundle bundle = new Bundle();
        FromWhere = "Invoice";
        if (from.equals("viewEntry")) {
            fragment = new ViewUserMilkEntryFragment();
            bundle.putString("FromWhere", FromWhere);
            bundle.putString("CustomerId", CustomerInvoiceList.get(position).getCustomerId());
            bundle.putString("unic_customer", CustomerInvoiceList.get(position).getUniqCustomerId());
            bundle.putString("user_group_id", CustomerInvoiceList.get(position).getUserGroupId());
            bundle.putString("CustomerName", CustomerInvoiceList.get(position).getName());
            bundle.putString("startDate", CustomerInvoiceList.get(position).getStartDate());
            bundle.putString("endDate", CustomerInvoiceList.get(position).getEndDate());
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }else if (from.equals("delete")) {
           CustomerInvoiceList.remove(position);
           adapter.notifyDataSetChanged();
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(CustomerInvoiceList.get(position));
            bundle.putString("from", FromWhere);
            bundle.putString("album", json);
            fragment = new CustomerInvoiceDetailsFragment();
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }


    }
}
