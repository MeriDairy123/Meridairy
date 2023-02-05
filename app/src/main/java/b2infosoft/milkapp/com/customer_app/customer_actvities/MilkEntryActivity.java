package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.MonthlyEntryListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.MonthsEntryListPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

public class MilkEntryActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    TextView tvTotalWeight, tvTotalFat, tvFatRat, tvTotalAmt;
    RecyclerView recycler_allEntry;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    Intent intent;
    String entryID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_milk_entry);
        mContext = MilkEntryActivity.this;
        initView();
    }

    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.viewEntry));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.drawable.back_arrow);
        upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTotalWeight = findViewById(R.id.tvTotalWeight);
        tvTotalFat = findViewById(R.id.tvTotalFat);
        tvFatRat = findViewById(R.id.tvFatRat);
        tvTotalAmt = findViewById(R.id.tvTotalAmt);
        recycler_allEntry = findViewById(R.id.recycler_allEntry);
        MonthsEntryListPojo.getMonthsEntryList(mContext, Constant.DairyNameID, Constant.UserID, Constant.Month, Constant.Year, "EntryMilkActivity", true);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void setMonthEntryList(ArrayList<MonthsEntryListPojo> entryListPojos) {
        recycler_allEntry.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
        MonthlyEntryListAdapter monthsListAdapter = new MonthlyEntryListAdapter(mContext, entryListPojos);
        recycler_allEntry.setLayoutManager(mLayoutManager);
        recycler_allEntry.setAdapter(monthsListAdapter);
        monthsListAdapter.notifyDataSetChanged();
        double totalWeight = 0, totalPrice = 0;
        if (entryListPojos.size() != 0) {
            for (int i = 0; i < entryListPojos.size(); i++) {
                if (!entryListPojos.get(i).total_milk.equals("")) {
                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
                }
                if (!entryListPojos.get(i).total_price.equals("")) {
                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
                }
            }
        }
        tvTotalWeight.setText(getString(R.string.Total_Weight) + ": \n" + String.format("%.2f", totalWeight) + " " + getString(R.string.Ltr));
        tvTotalAmt.setText(getString(R.string.Total_Amount) + ": \n" + String.format("%.2f", totalPrice) + " " + getString(R.string.Rs));
    }
}
