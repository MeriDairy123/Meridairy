package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.ChatActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerDairyListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

public class CustomerDairyListActivity extends AppCompatActivity {

    RecyclerView recycler_customerList;
    Context mContext;
    Toolbar toolbar;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dairy_list);
        mContext = CustomerDairyListActivity.this;
        initView();
    }

    private void initView() {

        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(getString(R.string.DAIRY_LIST));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DairyNamePojo.getDairyNameList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "CustomerDairyList");
    }

    public void setDairyNameList(ArrayList<DairyNamePojo> dairyNamePojos) {
        if (dairyNamePojos.size() == 1) {
            Constant.DairySize = "One";
            Constant.DairyNameID = dairyNamePojos.get(0).id;
            Constant.DairyName = dairyNamePojos.get(0).dairy_name;
            Constant.UserID = dairyNamePojos.get(0).customer_id;
            Constant.SessionUserGroupID = dairyNamePojos.get(0).customer_user_group_id;
            if (Constant.FromWhere.equals("TransactionList")) {
                mContext.startActivity(new Intent(mContext, CustomerTransactionDataActivity.class));
            } else if (Constant.FromWhere.equals("Product")) {
                mContext.startActivity(new Intent(mContext, ProductListActivity.class));
            } else if (Constant.FromWhere.equals("MilkEntry")) {
                mContext.startActivity(new Intent(mContext, MilkEntryDateActivity.class));
            } else if (Constant.FromWhere.equals("Chat")) {
                Constant.FromWhere2 = "CustomerApp";
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("FRIEND_NAME", dairyNamePojos.get(0).name);
                // intent.putExtra("IMAGE_PATH", morningList.get(pos).profile_image);
                intent.putExtra("unic_customer_for_mobile", dairyNamePojos.get(0).unic_customer_for_mobile);
                intent.putExtra("FRIEND_id", dairyNamePojos.get(0).id);
                intent.putExtra("firebase_tocan", dairyNamePojos.get(0).firebase_tocan);
                intent.putExtra("FRIEND_mob", dairyNamePojos.get(0).phone_number);
                startActivity(intent);
            }
        } else {
            Constant.DairySize = "More";
            recycler_customerList = findViewById(R.id.rvInvoiceList);
            GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 1);
            recycler_customerList.setLayoutManager(linearLayoutManager);
            recycler_customerList.setHasFixedSize(true);
            CustomerDairyListAdapter dairyNameListAdapter = new CustomerDairyListAdapter(mContext, dairyNamePojos);
            recycler_customerList.setAdapter(dairyNameListAdapter);
            dairyNameListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        UtilityMethod.goNextClass(mContext, CustomerDeshBoardActivity.class);
    }
}
