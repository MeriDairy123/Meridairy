package b2infosoft.milkapp.com.DeliveryBoy.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

import b2infosoft.milkapp.com.Dairy.Notification_activity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Notification.BeanNotification_Item;
import b2infosoft.milkapp.com.Notification.NotificationClickListner;
import b2infosoft.milkapp.com.Notification.Notification_item_adapter;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;


public class fragmentDeliveryNotification extends Fragment implements NotificationClickListner {
    View view;
    Toolbar toolbar;
    TextView tvClear;
    Context mContext;
    Notification_item_adapter adapter;
    ArrayList<BeanNotification_Item> mList;
    RecyclerView recyclerView;
    DatabaseHandler db;
    SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        toolbar = view.findViewById(R.id.toolbar);
        tvClear = toolbar.findViewById(R.id.tvClear);
        toolbar.setTitle(mContext.getString(R.string.notification));
        recyclerView = view.findViewById(R.id.recyclerView);

        db = DatabaseHandler.getDbHelper(mContext);

        mList = new ArrayList<>();
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.deleteNotificationTable();
                initRecyclerView();


            }
        });
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        mList = new ArrayList<>();
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
            tvClear.setVisibility(View.GONE);
        }
    }


    public void onAdapterClick(BeanNotification_Item notification_item) {
        Intent i = new Intent(mContext, Notification_activity.class);
        i.putExtra("messageTitle", notification_item.getTitle());
        i.putExtra("messageBody", notification_item.getDescription());
        startActivity(i);

    }
}
