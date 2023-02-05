package b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfDashboard_Adapter;
import b2infosoft.milkapp.com.Dairy.Setting.RateUpdateTabFragment;
import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;

import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.SMSPERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class PlantCollectionAndOutLetFragment extends Fragment implements OnClickInDashboardAdapter {

    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    Context mContext;

    Toolbar toolbar;

    SessionManager sessionManager;
    String screenFrom = "";

    ArrayList<Dashboard_item> dashboard_items;
    SnfDashboard_Adapter snfDashboard_adapter;
    View view;
    Fragment fragment;
    Bundle bundle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, SMSPERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, SMSPERMISSIONS, PERMISSION_ALL);
        }

        initView();


        SelectedDate = getSimpleDate();
        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle(mContext.getString(R.string.plantMilkCollection));


        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);


        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                pullToRefresh.setEnabled(false);
            }
        });
        recyclerViewUI();
    }

    private void recyclerViewUI() {
        dashboard_items = new ArrayList<>();
        dashboard_items.add(new Dashboard_item("1", mContext.getResources().getString(R.string.Milk_Buy), "",
                R.drawable.add_entry, "#2CBF7B"));
        dashboard_items.add(new Dashboard_item("2", mContext.getResources().getString(R.string.MILK_Sale), "",
                R.drawable.sale_milk, "#FF0800"));
        dashboard_items.add(new Dashboard_item("3", mContext.getResources().getString(R.string.Rate) + " " + mContext.getResources().getString(R.string.Setting), "", R.drawable.ic_settings, "#FF0800"));


        snfDashboard_adapter = new SnfDashboard_Adapter(mContext, dashboard_items, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(snfDashboard_adapter);

    }

    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onClickEditInAdapter(int pos) {

        if (pos == 0) {
            fragment = new PlantBuyMilkFragment();
        } else if (pos == 1) {
            fragment = new PlantMilkSaleFragment();
        } else if (pos == 2) {
            fragment = new RateUpdateTabFragment();
        }
        goNextFragmentWithBackStack(mContext, fragment);

    }

}
