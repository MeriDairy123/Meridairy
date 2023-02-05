package b2infosoft.milkapp.com.Notification;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Dairy.Notification_activity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static android.content.Context.NOTIFICATION_SERVICE;
import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Notification;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;


public class NotificationFragment extends Fragment implements NotificationClickListner {
    View view;
    Toolbar toolbar;
    TextView tvClear;
    Context mContext;
    Notification_item_adapter adapter;
    ArrayList<BeanNotification_Item> mList;
    RecyclerView recyclerView;
    DatabaseHandler db;
    SessionManager sessionManager;
    Bundle bundle;
    boolean backbtnEnable = false;
    String stFrom = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        mList = new ArrayList<>();
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvClear = toolbar.findViewById(R.id.tvClear);
        toolbar.setTitle(mContext.getString(R.string.notification));
        recyclerView = view.findViewById(R.id.recyclerView);

        tvClear.setText(mContext.getString(R.string.Delete) + " " + mContext.getString(R.string.all));
        db = DatabaseHandler.getDbHelper(mContext);
        bundle = getArguments();

        if (bundle != null) {
            stFrom = bundle.getString("from");
            backbtnEnable = true;
            toolbar.setNavigationIcon(R.drawable.back_arrow);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backbtnEnable) {

                    if (stFrom.equalsIgnoreCase("dairyMain")) {
                        Fragment fragment = new DairyDeshboardFragment();
                        goNextFragmentReplace(mContext, fragment);
                    } else {
                        getActivity().onBackPressed();
                    }

                } else {
                    if (stFrom.equalsIgnoreCase("dairy")) {
                        drawer.openDrawer(GravityCompat.START);
                    } else {
                        mDrawer.openDrawer(GravityCompat.START);
                    }
                }
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
        return view;

    }

    public void clearNotification() {

        String ns = NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(ns);
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
            tvClear.setVisibility(View.GONE);
        }


    }


    @Override
    public void onAdapterClick(BeanNotification_Item notification_item) {
        Intent i = new Intent(mContext, Notification_activity.class);
        i.putExtra("messageTitle", notification_item.getTitle());
        i.putExtra("messageBody", notification_item.getDescription());
        startActivity(i);
    }
}
