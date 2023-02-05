package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerDairyListBoxAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

public class CustomerDairyListWithBoxActivity extends AppCompatActivity {

    RecyclerView recycler_dairyList;
    Context mContext;
    Toolbar toolbar;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dairy_list_with_box);
        mContext = CustomerDairyListWithBoxActivity.this;
        initView();
    }

    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setTitle(getString(R.string.DAIRY_LIST));

        DairyNamePojo.getDairyNameList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "CustomerDairyListWithBoxActivity");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }

    @Override
    public void onBackPressed() {
        UtilityMethod.goNextClass(mContext, CustomerDeshBoardActivity.class);
    }

    public void setDairyNameList(ArrayList<DairyNamePojo> dairyNamePojos) {
        if (dairyNamePojos.size() == 1) {
            Constant.DairyNameID = dairyNamePojos.get(0).id;
            Constant.DairyName = dairyNamePojos.get(0).dairy_name;
            Constant.DairySize = "One";
            Constant.UserID = dairyNamePojos.get(0).customer_id;
            Constant.SessionUserGroupID = "3";
            if (Constant.FromWhere.equals("TransactionList")) {
                mContext.startActivity(new Intent(mContext, CustomerTransactionDataActivity.class));
            } else if (Constant.FromWhere.equals("Product")) {
                mContext.startActivity(new Intent(mContext, ProductListActivity.class));
            } else if (Constant.FromWhere.equals("MilkEntry")) {
                mContext.startActivity(new Intent(mContext, MilkEntryDateActivity.class));
            }
        } else {
            Constant.DairySize = "More";
            recycler_dairyList = findViewById(R.id.recycler_dairyList);
            GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 1);
            recycler_dairyList.setLayoutManager(linearLayoutManager);
            recycler_dairyList.setHasFixedSize(true);
            CustomerDairyListBoxAdapter dairyNameListAdapter = new CustomerDairyListBoxAdapter(mContext, dairyNamePojos);
            recycler_dairyList.setAdapter(dairyNameListAdapter);
            dairyNameListAdapter.notifyDataSetChanged();
        }
    }
}
