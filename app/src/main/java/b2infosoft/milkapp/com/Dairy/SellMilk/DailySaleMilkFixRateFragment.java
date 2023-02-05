package b2infosoft.milkapp.com.Dairy.SellMilk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.SellMilk.Adapter.DailySaleMilkAdapter;
import b2infosoft.milkapp.com.Interface.RefreshList;
import b2infosoft.milkapp.com.Model.DailySaleMilkCustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class DailySaleMilkFixRateFragment extends Fragment implements RefreshList {

    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, toolbar_Date;
    ImageView imgSession;
    EditText et_Search;
    SessionManager sessionManager;
    TextView tvTotalRemaining, tvTotalSell, tvTotalWeight, tvTotalAmount;
    RecyclerView recycler_buyerCustomerList;
    TextView id, txtCustomerName;
    DailySaleMilkAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Drawable leftDrawable;

    Double TotSell = 0d;
    Double totalRemaining = 0d;
    double totalWeight = 0.0;
    Double totalAmount = 0.0;

    Object tag = "";
    int backgroundId = 0;
    int backgroundId2 = 0;
    ArrayList<DailySaleMilkCustomerListPojo> customerListPojos;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_daily_sale_milk_customer, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        imgSession = toolbar.findViewById(R.id.imgSession);
        toolbar_Date = toolbar.findViewById(R.id.toolbar_Date);
        imgSession.setVisibility(View.VISIBLE);
        toolbar_Date.setVisibility(View.VISIBLE);
        toolbar_title.setGravity(Gravity.LEFT);
        toolbar_title.setText(mContext.getString(R.string.SALE_MILK_ENTRY));

        tvTotalRemaining = view.findViewById(R.id.tvTotalRemaining);
        tvTotalSell = view.findViewById(R.id.tvTotalSell);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        id = view.findViewById(R.id.id);
        et_Search = view.findViewById(R.id.et_Search);
        leftDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_arrow_drop_down);
        id.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        txtCustomerName = view.findViewById(R.id.txtCustomerName);
        toolbar_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDate();
            }
        });

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requireActivity().onBackPressed();


            }
        });


        imgSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogSelectShift();

            }
        });


        initView();
        return view;
    }

    private void initView() {
        customerListPojos = new ArrayList<>();
        toolbar_Date.setText(Constant.SelectedDate);
        if (strSession.equalsIgnoreCase("evening")) {
            imgSession.setBackgroundResource(R.drawable.evening);
        } else {
            imgSession.setBackgroundResource(R.drawable.sun_icon);
        }


        getDailyCustomerList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSession, Constant.SelectedDate);


    }

    public void getDailyCustomerList(final Context mContext, String dairyID, String shift, String entry_date) {
        final ArrayList<DailySaleMilkCustomerListPojo> mList = new ArrayList<>();
        @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        if (jsonObject1.getString("user_group_id").equals("4")) {
                            mList.add(new DailySaleMilkCustomerListPojo(jsonObject1.getString("id"), jsonObject1.getString("unic_customer")
                                    , jsonObject1.getString("name"), jsonObject1.getString("phone_number"), jsonObject1.getString("morning_milk"),
                                    jsonObject1.getString("evening_milk"), jsonObject1.getString("price_per_ltr"), jsonObject1.getString("is_active"), jsonObject1.getString("sale_status")));
                        }
                    }
                    if (!mList.isEmpty()) {
                        setEntryList(mList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyID)
                .addEncoded("entry_date", entry_date)
                .addEncoded("shift", shift)
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getDailySaleMilkCustomerListAPI);
    }

    @SuppressLint("DefaultLocale")
    public void setEntryList(final ArrayList<DailySaleMilkCustomerListPojo> mList) {

        TotSell = 0d;
        totalRemaining = 0d;
        totalWeight = 0.0;
        totalAmount = 0.0;

        for (int i = 0; i < mList.size(); i++) {
            if (strSession.equals("morning")) {
                //  TotSell = TotSell + Double.parseDouble(morningList.get(i).total_fat);
                if (!mList.get(i).morning_milk.equals("")) {
                    totalWeight = totalWeight + Double.parseDouble(mList.get(i).morning_milk);
                    if (!mList.get(i).sale_status.equals("0")) {
                        TotSell = TotSell + Double.parseDouble(mList.get(i).morning_milk);
                        totalAmount = totalAmount + Double.parseDouble(mList.get(i).price_per_ltr) * Double.parseDouble(mList.get(i).morning_milk);
                    } else {
                        totalRemaining = totalRemaining + Double.parseDouble(mList.get(i).morning_milk);
                    }
                }
            } else {
                if (!mList.get(i).evening_milk.equals("")) {
                    totalWeight = totalWeight + Double.parseDouble(mList.get(i).evening_milk);
                    if (!mList.get(i).sale_status.equals("0")) {
                        TotSell = TotSell + Double.parseDouble(mList.get(i).evening_milk);
                        totalAmount = totalAmount + Double.parseDouble(mList.get(i).price_per_ltr) * Double.parseDouble(mList.get(i).evening_milk);
                    } else {
                        totalRemaining = totalRemaining + Double.parseDouble(mList.get(i).evening_milk);
                    }
                }
            }
        }
        tvTotalRemaining.setText(mContext.getString(R.string.Total_Remaining) + "\n" + String.format("%.3f", totalRemaining) + " " + mContext.getString(R.string.Ltr));
        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalSell.setText(mContext.getString(R.string.Total_Sell) + "\n" + String.format("%.3f", TotSell) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Total_Amount) + "\n" + String.format("%.2f", totalAmount) + " " + mContext.getString(R.string.Rs));

        if (strSession.equals("morning")) {
            Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                @Override
                public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {

                    return object1.morning_milk.compareTo(object2.morning_milk);
                }
            });
        } else {
            Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                @Override
                public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {
                    return object1.evening_milk.compareTo(object2.evening_milk);
                }
            });
        }
        recycler_buyerCustomerList = view.findViewById(R.id.recycler_buyerCustomerList);
        mLayoutManager = new LinearLayoutManager(mContext);
        setAdapter(mList);


        id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tag = id.getTag();
                backgroundId = R.drawable.ic_arrow_drop_up;
                backgroundId2 = R.drawable.ic_arrow_drop_down;
                if (tag != null && (Integer) tag == backgroundId2) {
                    id.setTag(backgroundId);
                    id.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mList.size() > 0) {
                        Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                            @Override
                            public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {
                                //return object1.unic_customer.compareTo(object2.unic_customer);
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);
                                return u_ID1 - u_ID2;
                            }
                        });
                    }
                } else {
                    id.setTag(backgroundId2);
                    id.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (mList.size() > 0) {
                        Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                            @Override
                            public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);
                                return u_ID1 - u_ID2;
                                //  return object1.unic_customer.compareTo(object2.unic_customer);
                            }
                        });
                        Collections.reverse(mList);
                    }
                }

                setAdapter(mList);


            }
        });

        txtCustomerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tag = txtCustomerName.getTag();
                backgroundId = R.drawable.ic_arrow_drop_up;
                backgroundId2 = R.drawable.ic_arrow_drop_down;
                if (tag != null && (Integer) tag == backgroundId) {
                    txtCustomerName.setTag(backgroundId2);
                    txtCustomerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (mList.size() > 0) {
                        Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                            @Override
                            public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                        Collections.reverse(mList);
                    }
                } else {
                    txtCustomerName.setTag(backgroundId);
                    txtCustomerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mList.size() > 0) {
                        Collections.sort(mList, new Comparator<DailySaleMilkCustomerListPojo>() {
                            @Override
                            public int compare(final DailySaleMilkCustomerListPojo object1, final DailySaleMilkCustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                    }
                }
                setAdapter(mList);

            }
        });

        addTextListener(mList);


    }

    private void setAdapter(ArrayList<DailySaleMilkCustomerListPojo> mList) {

        adapter = new DailySaleMilkAdapter(mContext, mList, strSession, Constant.SelectedDate, this::refreshList);
        recycler_buyerCustomerList.setLayoutManager(mLayoutManager);
        recycler_buyerCustomerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void addTextListener(final ArrayList<DailySaleMilkCustomerListPojo> customerList) {

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<DailySaleMilkCustomerListPojo> filterList = new ArrayList<>();
                for (int i = 0; i < customerList.size(); i++) {

                    final String name = customerList.get(i).name.toLowerCase();
                    final String unic_customer = customerList.get(i).unic_customer.toLowerCase();


                    if (name.contains(query) || unic_customer.contains(query)) {
                        DailySaleMilkCustomerListPojo customerListPojo = customerList.get(i);
                        filterList.add(customerListPojo);
                    }
                }
                mLayoutManager = new LinearLayoutManager(mContext);
                setAdapter(filterList);

            }
        });
    }

    @Override
    public void refreshList(ArrayList<DailySaleMilkCustomerListPojo> mList) {
        TotSell = 0d;
        totalRemaining = 0d;
        totalWeight = 0.0;
        totalAmount = 0.0;

        for (int i = 0; i < mList.size(); i++) {
            if (strSession.equals("morning")) {
                //  TotSell = TotSell + Double.parseDouble(morningList.get(i).total_fat);
                if (!mList.get(i).morning_milk.equals("")) {
                    totalWeight = totalWeight + Double.parseDouble(mList.get(i).morning_milk);
                    if (!mList.get(i).sale_status.equals("0")) {
                        TotSell = TotSell + Double.parseDouble(mList.get(i).morning_milk);
                        totalAmount = totalAmount + Double.parseDouble(mList.get(i).price_per_ltr) * Double.parseDouble(mList.get(i).morning_milk);
                    } else {
                        totalRemaining = totalRemaining + Double.parseDouble(mList.get(i).morning_milk);
                    }
                }
            } else {
                if (!mList.get(i).evening_milk.equals("")) {
                    totalWeight = totalWeight + Double.parseDouble(mList.get(i).evening_milk);
                    if (!mList.get(i).sale_status.equals("0")) {
                        TotSell = TotSell + Double.parseDouble(mList.get(i).evening_milk);
                        totalAmount = totalAmount + Double.parseDouble(mList.get(i).price_per_ltr) * Double.parseDouble(mList.get(i).evening_milk);
                    } else {
                        totalRemaining = totalRemaining + Double.parseDouble(mList.get(i).evening_milk);
                    }
                }
            }
        }
        tvTotalRemaining.setText(mContext.getString(R.string.Total_Remaining) + "\n" + String.format("%.3f", totalRemaining) + " " + getString(R.string.Ltr));
        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalSell.setText(mContext.getString(R.string.Total_Sell) + "\n" + String.format("%.3f", TotSell) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Total_Amount) + "\n" + String.format("%.2f", totalAmount) + " " + mContext.getString(R.string.Rs));
    }

    public void getDate() {
        Calendar c2 = Calendar.getInstance();
        int mYear, mMonth, mDay;
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String formattedDate = "";
                        ArrayList<String> monthList = getMonthList();

                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";
                        //  month = checkDigit(monthOfYear + 1);
                        day = checkDigit(dayOfMonth);

                        formattedDate = day + "-" + month + "-" + year;
                        toolbar_Date.setText(formattedDate);
                        System.out.println("formattedDate====>>" + formattedDate);
                        Constant.SelectedDate = formattedDate;
                        initView();
                    }

                }, mYear, mMonth, mDay);

        datePickerDialog2.show();

    }

    private void dialogSelectShift() {
        Dialog dialog = new Dialog( getActivity());
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);
        ImageView imgClosed;
        TextView tvDialogTitle, tvMorning, tvEvening;
        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvMorning = dialog.findViewById(R.id.tvBuyproduct);
        tvEvening = dialog.findViewById(R.id.tvViewEvent);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvDialogTitle.setText(mContext.getString(R.string.select) + " " + mContext.getString(R.string.Shift));
        tvMorning.setText(mContext.getString(R.string.MORNING));
        tvEvening.setText(mContext.getString(R.string.EVENING));

        Drawable imgMon = mContext.getResources().getDrawable(R.drawable.sun_icon);
        Drawable imgEvg = mContext.getResources().getDrawable(R.drawable.evening);


        tvMorning.setCompoundDrawablesWithIntrinsicBounds(imgMon, null, null, null);
        tvEvening.setCompoundDrawablesWithIntrinsicBounds(imgEvg, null, null, null);


        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSession = "morning";
                dialog.dismiss();
                initView();

            }
        });

        tvEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSession = "evening";
                dialog.dismiss();
                initView();
            }
        });

        dialog.show();
    }

}
