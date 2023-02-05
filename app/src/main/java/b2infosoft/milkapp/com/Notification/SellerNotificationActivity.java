package b2infosoft.milkapp.com.Notification;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.Notification_activity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;

public class SellerNotificationActivity extends Activity implements NotificationClickListner {
    View view;
    Toolbar toolbar;
    TextView tvClear;
    Context mContext;
    Notification_item_adapter adapter;
    ArrayList<BeanNotification_Item> mList;
    RecyclerView recyclerView;
    DatabaseHandler db;
    SessionManager sessionManager;

    boolean backbtnEnable = false;
    String stFrom = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notification);

        mList = new ArrayList<>();

        mContext = SellerNotificationActivity.this;
        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);
        tvClear = findViewById(R.id.tvClear);
        toolbar.setTitle(mContext.getString(R.string.notification));
        recyclerView = findViewById(R.id.recyclerView);

        tvClear.setText(mContext.getString(R.string.Delete) + " " + mContext.getString(R.string.all));
        db = DatabaseHandler.getDbHelper(mContext);
        backbtnEnable = true;
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.deleteNotificationTable();
                initRecyclerView();
            }
        });

        initRecyclerView();
        clearNotification();

    }

    public void clearNotification() {

        String ns = NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) Objects.requireNonNull(this).getApplicationContext().getSystemService(ns);
        Objects.requireNonNull(nMgr).cancelAll();
    }

    private void initRecyclerView() {

        mList = db.getNotificationList();
        Collections.reverse(mList);
        adapter = new Notification_item_adapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        sessionManager.setIntValueSession(KEY_Notification, 0);
        if (!mList.isEmpty()) {
            tvClear.setVisibility(View.VISIBLE);
        } else {
            tvClear.setVisibility(View.VISIBLE);
        }

    }


    public void onAdapterClick(BeanNotification_Item notification_item) {
        Intent i = new Intent(mContext, Notification_activity.class);
        i.putExtra("messageTitle", notification_item.getTitle());
        i.putExtra("messageBody", notification_item.getDescription());
        startActivity(i);


    }
}
