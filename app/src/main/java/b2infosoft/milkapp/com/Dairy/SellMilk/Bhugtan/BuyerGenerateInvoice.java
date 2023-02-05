package b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

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


public class BuyerGenerateInvoice extends Fragment implements View.OnClickListener {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView imgStartDate;
    TextView tvDate;
    Button btnSubmit;

    SessionManager sessionManager;
    View view;
    String dairyId = "", formattedDate = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_generate_invoice, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);


        initView(view);

        return view;
    }

    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);

        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        tvDate = view.findViewById(R.id.tvDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar_title.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.invoice));

        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        tvDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);


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
                    .addEncoded("from_date", formattedDate)
                    .build();
            serviceCaller.addRequestBody(body);

            serviceCaller.execute(Constant.generateBuyerInvoiceAPI);

        } else {
            showAlertWithButton(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                formattedDate = tvDate.getText().toString();
                if (formattedDate.length() > 0) {
                    generateInvoice();
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Date));
                }
                break;


            case R.id.tvDate:

            case R.id.imgStartDate:
                getDate("StartDate");
                break;
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
                        formattedDate = day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                        if (from.equals("StartDate")) {
                            tvDate.setText(formattedDate);
                        } else {

                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }
}
