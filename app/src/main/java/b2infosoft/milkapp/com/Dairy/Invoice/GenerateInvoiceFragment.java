package b2infosoft.milkapp.com.Dairy.Invoice;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class GenerateInvoiceFragment extends Fragment implements View.OnClickListener {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView imgStartDate;
    TextView tvStartDate, tvEndDate;
    Button btnSubmit;
    Spinner spinUser;
    int selectUserSpinPos = 0;


    ArrayList<CustomerListPojo> userList = new ArrayList<>();
    SessionManager sessionManager;
    View view, lvStartDate, lvEndDate;
    String strFrom = "", userGroupId = "", type = "", dairyId = "", customerId = "", fromDate = "", endDate = "";
    DatabaseHandler db;
    Bundle bundle;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_generate_invoice, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        db = DatabaseHandler.getDbHelper(mContext);
        bundle = getArguments();
        if (bundle != null) {
            strFrom = bundle.getString("from");
            userGroupId = bundle.getString("user_group_id");
            type = bundle.getString("type");
            customerId = bundle.getString("CustomerId");

        }
        userList.add(new CustomerListPojo("all", "", "", "",
                "", mContext.getString(R.string.all), "", "", "",
                "", "", "", "", "",
                "", "", "", ""));
        ArrayList<CustomerListPojo> CustomerList = db.getCustomerListByGroupId(userGroupId);
        userList.addAll(CustomerList);
        initView(view);

        initSpinUser();
        return view;
    }

    private void initView(View view) {


        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        lvStartDate = view.findViewById(R.id.lvStartDate);
        lvEndDate = view.findViewById(R.id.lvEndDate);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        spinUser = view.findViewById(R.id.spinUser);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar_title.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.invoice));

        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        lvStartDate.setOnClickListener(this);
        lvEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);


    }

    private void initSpinUser() {
        if (userGroupId.equals("4")&&!userList.isEmpty()){
            Collections.sort(userList, new Comparator<CustomerListPojo>() {
                @Override
                public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                    return object1.unic_customer.compareTo(object2.unic_customer);
                }
            });
        }
        ArrayList<String> listItem = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId().equals(customerId)) {
                selectUserSpinPos = i;
            }
            listItem.add(userList.get(i).getUnic_customer()+" "+userList.get(i).getName());
        }

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);

        spinUser.setAdapter(spinAdapter);
        spinUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                customerId = userList.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (selectUserSpinPos > 0) {
            spinUser.setSelection(selectUserSpinPos);
            spinUser.setEnabled(false);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                fromDate = tvStartDate.getText().toString();
                endDate = tvEndDate.getText().toString();
                if (fromDate.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Date));
                } else if (endDate.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Date));
                } else if (customerId.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Customer));
                } else {
                    generateInvoice();

                }
                break;
            case R.id.lvStartDate:
                getDate("StartDate");
                break;
            case R.id.lvEndDate:
                getDate("EndDate");
                break;
        }
    }

    public void generateInvoice() {

        if (isNetworkAvaliable(mContext)) {

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            getActivity().onBackPressed();
                        } else {
                            if (jsonObject.has("user_status_message")) {
                                showAlertWithButton(mContext, jsonObject.getString("user_status_message"));

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("type", "add")
                    .addEncoded("from_date", fromDate)
                    .addEncoded("to_date", endDate)
                    .addEncoded("customer_id", customerId)
                    .build();
            serviceCaller.addRequestBody(body);
            if (userGroupId.equalsIgnoreCase("4")) {
                serviceCaller.execute(Constant.generateBuyerInvoiceAPI);
            } else {
                serviceCaller.execute(Constant.generateSellerInvoiceAPI);
            }

        } else {
            showAlertWithButton(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        int mYear = c2.get(Calendar.YEAR);
        int mMonth = c2.get(Calendar.MONTH);
        int mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";
                        System.out.println("Month====>>>" + month);
                        day = checkDigit(dayOfMonth);
                        String formattedDate = day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                        if (from.equals("StartDate")) {
                            tvStartDate.setText(formattedDate);
                        } else {
                            tvEndDate.setText(formattedDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }
}
