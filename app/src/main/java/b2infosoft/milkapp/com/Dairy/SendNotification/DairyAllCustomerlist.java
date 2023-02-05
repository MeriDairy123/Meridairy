package b2infosoft.milkapp.com.Dairy.SendNotification;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Model.CustomerListPojo.addCustomerListInDatabase;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentAddBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

/**
 * Created by u on 27-Dec-17.
 */

public class DairyAllCustomerlist extends
        Fragment {

    Button btnNext;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;

    Context mContext;

    Toolbar toolbar;
    SessionManager sessionManager;


    DairyCustomerListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;

    Fragment fragment;
    View view;
    String updatedList = "";
    //updatedList = TextUtils.join(", ", updateVacList);
    ArrayList<CustomerListPojo> beanUserItems = new ArrayList<>();
    ArrayList<CustomerListPojo> beanSelectedUser = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);


        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();


        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle(mContext.getString(R.string.Select) + "  " + mContext.getString(R.string.Customer));
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnNext = view.findViewById(R.id.btnNext);
        getCustomerList();
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
                if (beanUserItems.isEmpty()) {
                    addCustomerListInDatabase(mContext, true);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            getCustomerList();
                            pullToRefresh.setRefreshing(false);
                        }
                    }, 1000);
                } else {
                    getCustomerList();
                    pullToRefresh.setRefreshing(false);
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String json = gson.toJson(beanSelectedUser);
                sessionManager.setValueSession("album", json);
                fragment = new AddMessageNotification();
                Bundle bundle = new Bundle();
                bundle.putString("userList", updatedList);
                bundle.putString("from", "userList");
                fragment.setArguments(bundle);
                goNextFragmentAddBackStack(mContext, fragment);
            }
        });

    }

    public void getCustomerList() {

        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        beanUserItems = databaseHandler.getCustomerList();
        System.out.println("customerList=From Local=>>" + beanUserItems.size());
        initRecyclerView();

    }

    public void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new DairyCustomerListAdapter(mContext, beanUserItems);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void selectedUser(ArrayList<CustomerListPojo> userList) {
        beanSelectedUser = new ArrayList<>();
        updatedList = "";
        List<String> selectedItem = new ArrayList<>();

        for (int i = 0; i < userList.size(); i++) {
            String strStatus = nullCheckFunction(userList.get(i).getAdhar());
            if (strStatus.equalsIgnoreCase("1")) {
                selectedItem.add(userList.get(i).getId());
                beanSelectedUser.add(userList.get(i));
            }
        }
        System.out.println("Selected Customer===" + selectedItem.size());


        if (selectedItem.isEmpty()) {
            btnNext.setVisibility(View.GONE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            updatedList = TextUtils.join(", ", selectedItem);
        }
        System.out.println(" Customer updatedList=====" + updatedList);
    }


    public class DairyCustomerListAdapter extends RecyclerView.Adapter<DairyCustomerListAdapter.MyViewHolder> {

        public Context mContext;
        public ArrayList<CustomerListPojo> mList = new ArrayList<>();
        public ArrayList<CustomerListPojo> mListFilter = new ArrayList<>();
        int pos = 0;
        RecyclerView.ViewHolder holderPosition = null;

        public DairyCustomerListAdapter(Context mContext, ArrayList<CustomerListPojo> entryList) {
            this.mContext = mContext;
            this.mList = entryList;
            mListFilter = new ArrayList<>();
            this.mListFilter.addAll(mList);

        }

        public void updaterAdapter(ArrayList<CustomerListPojo> entryList) {

            this.mList = entryList;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_buyer_customer_list_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holderPosition = holder;
            pos = position;
            holder.setIsRecyclable(false);
            int srNo = position + 1;
            holder.tvS_no.setText("" + srNo + ".");
            holder.tvId.setText(mList.get(position).getUnic_customer());
            holder.txtCustomerName.setText(mList.get(position).getName());
            String status = nullCheckFunction(mList.get(position).getAdhar());
            if (status.equals("1")) {
                holder.chkStatus.setChecked(true);
            } else {
                holder.chkStatus.setChecked(false);
            }

            holder.chkStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.chkStatus.isChecked()) {
                        mList.get(position).setAdhar("1");
                    } else {
                        mList.get(position).setAdhar("0");

                    }
                    selectedUser(mList);

                }
            });

            holder.txtMobileNo.setText(mList.get(position).phone_number);

        }

        public void filterSearch(String charText) {

            charText = charText.toLowerCase(Locale.getDefault());
            mList.clear();
            if (charText.length() == 0) {
                mList.addAll(mListFilter);
            } else {
                for (CustomerListPojo wp : mListFilter) {
                    if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                        mList.add(wp);
                    } else if (wp.unic_customer.toLowerCase(Locale.getDefault()).contains(charText)) {
                        mList.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }


        public int getItemCount() {
            return mList.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView txtCustomerName, tvId, tvS_no, tvStatus, txtMobileNo;
            public CheckBox chkEvening, chkStatus;

            ImageView imgMoreDetail;

            public MyViewHolder(View view) {
                super(view);

                txtCustomerName = view.findViewById(R.id.txtCustomerName);
                tvId = view.findViewById(R.id.tvId);
                txtMobileNo = view.findViewById(R.id.txtMobileNo);
                tvS_no = view.findViewById(R.id.tvS_no);
                tvStatus = view.findViewById(R.id.tvStatus);
                chkStatus = view.findViewById(R.id.chkStatus);
                chkEvening = view.findViewById(R.id.chkEvening);
                imgMoreDetail = view.findViewById(R.id.imgMoreDetail);
                imgMoreDetail.setVisibility(View.GONE);

            }

        }
    }
}