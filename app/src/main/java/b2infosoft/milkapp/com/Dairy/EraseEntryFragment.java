package b2infosoft.milkapp.com.Dairy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.PurchaseMilk.Adapter.MilkHistoryAdapter;
import b2infosoft.milkapp.com.Model.MilkHistoryPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class EraseEntryFragment extends Fragment implements View.OnClickListener {

    TextView tvErase;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvStartDate, tvEndDate;
    SessionManager sessionManager;

    ImageView imgStartDate, imgEndDate;
    RecyclerView milk_history;
    String startDate = "";
    String endDate = "";


    String dairy_id = "";
    long days = 0;
    Button btnSubmit;
    String currentDay = "";
    Fragment fragment = null;
    Bundle bundle = null;
    View view;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_erase_entry, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        initView();
        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        toolbar_title.setText(getString(R.string.ERASE_MILK_HISTORY));
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        milk_history = view.findViewById(R.id.milk_history);
        tvErase = view.findViewById(R.id.tvErase);
        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvErase.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        toolBarManage();
        dairy_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        endDate = checkDigit(c.get(Calendar.DATE)) + "-" + checkDigit(c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR);
        currentDay = df.format(c.getTime());
        tvEndDate.setText(endDate);
        getCalculatedDate();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgStartDate:
                getDate("StartDate");
                break;
            case R.id.imgEndDate:
                if (!startDate.equals("")) {
                    getDate("EndDate");
                } else {
                    showToast(mContext, getString(R.string.Please_select_start_date_first));
                }
                break;
            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.tvEndDate:
                if (!startDate.equals("")) {
                    getDate("EndDate");
                } else {
                    showToast(mContext, getString(R.string.Please_select_start_date_first));
                }
                break;
            case R.id.btnSubmit:
                if (startDate.equals("")) {
                    showToast(mContext, getString(R.string.Please_select_start_date_first));
                } else if (endDate.equals("")) {
                    showToast(mContext, getString(R.string.Please_select_end_date_first));
                } else {
                    getMilkHistoryList();
                }
                break;
            case R.id.tvErase:
                final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(mContext);
                myAlertDialog.setTitle(getString(R.string.Are_You_Sure_Want_To_Delete_Entry));

                myAlertDialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                            @Override
                            public void handleResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("success")) {
                                        UtilityMethod.showAlertBox(mContext, getString(R.string.Entry_Deleted_Successfully));
                                        getMilkHistoryList();
                                    } else {
                                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Entry_Deleting_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("dairy_id", dairy_id)
                                .addEncoded("date_from", startDate)
                                .addEncoded("date_to", endDate)
                                .build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.eraseMilkHistoryAPI);
                    }
                });
                myAlertDialog.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int arg1) {

                        dialogInterface.dismiss();
                    }
                });
                myAlertDialog.show();
                break;
        }
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String day = checkDigit((dayOfMonth));
                        String monthStr = checkDigit((monthOfYear) + 1);
                        if (from.equals("StartDate")) {
                            startDate = day + "-" + monthStr + "-" + year;
                            tvStartDate.setText(startDate);
                        } else {
                            endDate = day + "-" + monthStr + "-" + year;
                            tvEndDate.setText(endDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }


    public void setMilkHistoryList(ArrayList<MilkHistoryPojo> milkHistoryListPojos) {
        MilkHistoryAdapter milkHistoryAdapter = new MilkHistoryAdapter(mContext, milkHistoryListPojos);
        milk_history.setLayoutManager(new GridLayoutManager(mContext, 1));
        milk_history.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        milk_history.setAdapter(milkHistoryAdapter);
    }


    public void getCalculatedDate() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DATE, -10);
        startDate = checkDigit(now.get(Calendar.DATE)) + "-" + checkDigit(now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.YEAR);
        Log.d("beforeDays", startDate);

        tvStartDate.setText(startDate);
        getMilkHistoryList();

    }

    public void toolBarManage() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getString("from").equalsIgnoreCase("setting")) {
                toolbar.setVisibility(View.GONE);
            }
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    public void getMilkHistoryList() {
        final ArrayList<MilkHistoryPojo> milkHistoryListPojos = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        JSONArray jsonArray = jsonObject1.getJSONArray("morning");
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(j);

                            milkHistoryListPojos.add(new MilkHistoryPojo(
                                    nullCheckFunction(jsonObject.getString("total_milk")),
                                    nullCheckFunction(jsonObject.getString("total_price")),
                                    nullCheckFunction(jsonObject.getString("total_fat")),
                                    jsonObject.getString("dairy_id"),
                                    nullCheckFunction(jsonObject1.getString("entry_date")),
                                    "Morning"));
                        }
                        JSONArray jsonArray_ = jsonObject1.getJSONArray("evening");
                        for (int j = 0; j < jsonArray_.length(); j++) {
                            JSONObject jsonObject = jsonArray_.getJSONObject(j);
                            milkHistoryListPojos.add(new MilkHistoryPojo(
                                    nullCheckFunction(jsonObject.getString("total_milk")),
                                    nullCheckFunction(jsonObject.getString("total_price")),
                                    nullCheckFunction(jsonObject.getString("total_fat")),
                                    jsonObject.getString("dairy_id"),
                                    nullCheckFunction(jsonObject1.getString("entry_date")),
                                    "Evening"));
                        }
                    }
                    setMilkHistoryList(milkHistoryListPojos);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("date_from", startDate)
                .addEncoded("date_to", endDate)
                .addEncoded("dairy_id", dairy_id)
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMilkHistoryAPI);
    }

}
