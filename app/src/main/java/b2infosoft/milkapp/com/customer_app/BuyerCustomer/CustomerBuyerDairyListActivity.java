package b2infosoft.milkapp.com.customer_app.BuyerCustomer;

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
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerDairyListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.RecyclerTouchListener;

public class CustomerBuyerDairyListActivity extends AppCompatActivity {

    RecyclerView recycler_customerList;
    Context mContext;
    Toolbar toolbar;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dairy_list);
        mContext = CustomerBuyerDairyListActivity.this;
        sessionManager = new SessionManager(mContext);
        initView();
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.Buyer) + " " + mContext.getString(R.string.DAIRY_LIST));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        DairyNamePojo.getDairyNameList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), "BuyerCustomerDairyList");
    }

    public void setDairyNameList(ArrayList<DairyNamePojo> dairyNamePojos) {

        if (dairyNamePojos.size() > 0) {
            recycler_customerList = findViewById(R.id.rvInvoiceList);
            GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 1);
            recycler_customerList.setLayoutManager(linearLayoutManager);
            recycler_customerList.setHasFixedSize(true);
            CustomerDairyListAdapter dairyNameListAdapter = new CustomerDairyListAdapter(mContext, dairyNamePojos);
            recycler_customerList.setAdapter(dairyNameListAdapter);
            dairyNameListAdapter.notifyDataSetChanged();
            recycler_customerList.addOnItemTouchListener(new RecyclerTouchListener(mContext, recycler_customerList,
                    new RecyclerTouchListener.ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            DairyNamePojo album = dairyNamePojos.get(position);
                            sessionManager.setValueSession(SessionManager.KEY_CustomerUserID, album.customer_id);
                            sessionManager.setValueSession(SessionManager.KEY_dairy_id, album.id);
                            sessionManager.setValueSession(SessionManager.KEY_dairy_name, album.name);
                            Intent i = new Intent(mContext, CustomerBuyerMainActivity.class);
                            startActivity(i);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));
        } else {
            Intent i = new Intent(mContext, CustomerBuyerMainActivity.class);
            startActivity(i);
        }

    }


}
