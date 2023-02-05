package b2infosoft.milkapp.com.Dairy.Product;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
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

import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Dairy.Product.Adapter.ProductSellingListAdapter;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Model.ProductSellingListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;

public class ViewSellProductListFragment extends Fragment implements View.OnClickListener, FragmentBackPressListener {
    public TextView tvStartDate, tvEndDate, tvTotalSellingPrice;
    public String dairyid = "", formattedDate = "", startDate = "", endDate = "";
    Context mContext;
    LinearLayout layoutStartDate, layoutEndDate;
    Button btnSubmit;
    ArrayList<ProductSellingListPojo> mList;
    ProductSellingListAdapter productSellingListAdapter;
    RecyclerView recycler_SellingProductList;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    Fragment fragment = null;
    View view;
    private int mYear, mMonth, mDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_sell_product_list, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        mList = new ArrayList<>();
        initView();
        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.Selling_History));
        layoutStartDate = view.findViewById(R.id.layoutStartDate);
        layoutEndDate = view.findViewById(R.id.layoutEndDate);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvTotalSellingPrice = view.findViewById(R.id.tvTotalSellingPrice);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        recycler_SellingProductList = view.findViewById(R.id.recycler_SellingProductList);

        endDate = getSimpleDate();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        startDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());

        tvStartDate.setText(endDate);
        tvEndDate.setText(endDate);
        getSaleProductList();

        btnSubmit.setOnClickListener(this);
        layoutStartDate.setOnClickListener(this);
        layoutEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);


        toolbarManage();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                startDate = tvStartDate.getText().toString().trim();
                endDate = tvEndDate.getText().toString().trim();
                if (!startDate.equals("") && !endDate.equals("")) {
                    getSaleProductList();
                } else {
                    UtilityMethod.showAlertWithButton(mContext, getString(R.string.Incomplete_Entry));
                }
                break;

            case R.id.layoutStartDate:
                getDate("StartDate");

                break;
            case R.id.layoutEndDate:
                getDate("EndDate");

                break;

            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.tvEndDate:
                getDate("EndDate");

                break;
        }
    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";

                        day = checkDigit(dayOfMonth);
                        //.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
                        if (from.equals("StartDate")) {
                            tvStartDate.setText(formattedDate);
                        } else {
                            tvEndDate.setText(formattedDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();


    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void setProductList(ArrayList<ProductSellingListPojo> mList) {

        if (mList.isEmpty()) {
            recycler_SellingProductList.setVisibility(View.INVISIBLE);
            tvTotalSellingPrice.setText((getString(R.string.Total_Selling_Price) + " " + (getString(R.string.Rs) + " 0.00")));
        } else {
            recycler_SellingProductList.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recycler_SellingProductList.setHasFixedSize(true);
            productSellingListAdapter = new ProductSellingListAdapter(mContext, mList, startDate, endDate);
            recycler_SellingProductList.setLayoutManager(mLayoutManager);
            recycler_SellingProductList.setAdapter(productSellingListAdapter);
            productSellingListAdapter.notifyDataSetChanged();
            Double TotalSellingPrice = 0d;
            for (int i = 0; i < mList.size(); i++) {
                TotalSellingPrice = TotalSellingPrice + Double.parseDouble(mList.get(i).total_price);
            }
            tvTotalSellingPrice.setText(getString(R.string.Total_Selling_Price) + " " + getString(R.string.rsSymbol) + "  " + String.format("%.2f", TotalSellingPrice));
        }
    }

    public void getSaleProductList() {
        final ArrayList<ProductSellingListPojo> mList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        mList.add(new ProductSellingListPojo
                                (jsonObject1.getString("products_id"),
                                        jsonObject1.getString("products_name"),
                                        jsonObject1.getString("total_qty"),
                                        jsonObject1.getString("total_price")));
                    }

                    setProductList(mList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyid)
                .addEncoded("in_date", startDate)
                .addEncoded("out_date", endDate).build();
        caller.addRequestBody(body);
        caller.execute(Constant.GetSaleProductList);
    }


    @Override
    public void OnFragmentBackPressListener() {
        fragment = new DairyDeshboardFragment();
        UtilityMethod.goNextFragmentWithBackStack(mContext, fragment);
    }
}
