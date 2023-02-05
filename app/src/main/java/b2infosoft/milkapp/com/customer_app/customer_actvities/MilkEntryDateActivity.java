package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.MonthsListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyMonthlyDataPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

public class MilkEntryDateActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Spinner SpinnerYear;
    LinearLayout LayoutSpinnerYear;
    RecyclerView recyclerViewMonthsList;
    ArrayList<String> yearList = new ArrayList<>();
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_milk_entry__date);
        mContext = MilkEntryDateActivity.this;
        inittView();
    }

    private void inittView() {
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(Constant.DairyName.toUpperCase());
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sessionManager = new SessionManager(mContext);
        LayoutSpinnerYear = findViewById(R.id.LayoutSpinnerYear);
        SpinnerYear = findViewById(R.id.SpinnerYear);
        recyclerViewMonthsList = findViewById(R.id.recyclerViewMonthsList);

        yearList.add("Select Year");
        // yearList.add("2016");
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        for (long i = 2016; i <= year; i++) {
            yearList.add("" + i);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(mContext, android.R.layout.simple_spinner_item, yearList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerYear.setAdapter(arrayAdapter);
        SpinnerYear.setSelection(yearList.size() - 1);
        SpinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String year = "", txt = "";
                txt = adapterView.getItemAtPosition(i).toString();
                System.out.println("txt==>>>" + txt);
                if (!txt.equals("Select Year")) {
                    year = yearList.get(i);
                    System.out.println("yearSpinenr==>>>" + year);
                    System.out.println("userID==>>>" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                    System.out.println("DairyID==>>>" + Constant.DairyNameID);

                    DairyMonthlyDataPojo.getDairyMonthData(mContext, Constant.UserID, year, Constant.DairyNameID);
                } else {
                    year = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void setDairyMonthDataList(ArrayList<DairyMonthlyDataPojo> mothlyDataList) {
        if (mothlyDataList.size() != 0) {
            recyclerViewMonthsList.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
            MonthsListAdapter monthsListAdapter = new MonthsListAdapter(mContext, mothlyDataList);
            recyclerViewMonthsList.setLayoutManager(mLayoutManager);
            recyclerViewMonthsList.setAdapter(monthsListAdapter);
            monthsListAdapter.notifyDataSetChanged();
        } else {
            recyclerViewMonthsList.setVisibility(View.INVISIBLE);
            UtilityMethod.showAlertWithButton(mContext, getString(R.string.No_Data_Found));
        }
    }


}
