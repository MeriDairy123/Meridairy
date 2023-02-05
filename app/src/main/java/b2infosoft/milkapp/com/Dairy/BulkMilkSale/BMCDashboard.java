package b2infosoft.milkapp.com.Dairy.BulkMilkSale;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter.BMC_Menu_Adapter;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerTransactionActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryDateActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.ProductListActivity;
import b2infosoft.milkapp.com.customer_app.customer_adapters.MonthlyEntryListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.customer_app.customer_pojo.MonthsEntryListPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.useful.RecyclerTouchListener;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getAllDairyNameAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_UserGroupID;

/**
 * Created by Choudhary on 21-Sept-19.
 */
public class BMCDashboard extends Fragment {

    public AlertDialog.Builder dialogBuilder;
    public AlertDialog builder;
    public ArrayList<DairyNamePojo> mList = new ArrayList<>();
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvTotalWeight, tvTotalAmt;
    int mDay = 0, month = 0, mYear = 0, mMaxDay = 0;
    BMC_Menu_Adapter menuAdapter;
    ArrayList<MonthsEntryListPojo> entryListPojos = new ArrayList<>();
    SessionManager sessionManager;
    RecyclerView recyclerviewMenu, recyclerViewEntry;
    String userId = "", dairyCustomerId = "", dairyId = "", dairyName = "";
    String userGroupId = "3";
    View view;
    private List<Dashboard_item> dashboard_items;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bmc_dashboard, container, false);
        mContext = getActivity();

        initView();

        return view;
    }


    private void initView() {
        sessionManager = new SessionManager(getActivity());
        userId = sessionManager.getValueSesion(KEY_UserID);
        userGroupId = sessionManager.getValueSesion(Key_UserGroupID);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbarTitle);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStateDialog();
            }
        });

        toolbar.setTitle(mContext.getString(R.string.bulkMilkHistory));
        toolbar_title.setText(mContext.getString(R.string.select) + " " + mContext.getString(R.string.Dairy));
        recyclerViewEntry = view.findViewById(R.id.recyclerMilkEntry);
        recyclerviewMenu = view.findViewById(R.id.recyclerviewMenu);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvTotalAmt = view.findViewById(R.id.tvTotalAmt);


        getDairyNameList();
        menuRecyclerViewUI();

    }

    private void menuRecyclerViewUI() {

        dashboard_items = getSallerMenu();

        int spaceCount = dashboard_items.size();

        menuAdapter = new BMC_Menu_Adapter(mContext, dashboard_items);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, spaceCount);
        recyclerviewMenu.setLayoutManager(mLayoutManager);
        recyclerviewMenu.addItemDecoration(new GridSpacingItemDecoration(spaceCount, dpToPx(0), true));
        recyclerviewMenu.setItemAnimator(new DefaultItemAnimator());
        recyclerviewMenu.setAdapter(menuAdapter);
        recyclerviewMenu.addOnItemTouchListener(new RecyclerTouchListener(mContext, recyclerViewEntry,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Constant.DairyNameID = dairyId;
                        Constant.DairyName = dairyName;
                        Constant.DairySize = "Dairy";
                        Constant.UserID = dairyCustomerId;
                        Constant.SessionUserGroupID = userGroupId;

                        if (dashboard_items.get(position).getName().equals(mContext.getResources().getString(R.string.Transaction))) {
                            mContext.startActivity(new Intent(mContext, CustomerTransactionActivity.class));
                        } else if (dashboard_items.get(position).getName().equals(mContext.getResources().getString(R.string.Product))) {
                            mContext.startActivity(new Intent(mContext, ProductListActivity.class));
                        } else if (dashboard_items.get(position).getName().equals(mContext.getResources().getString(R.string.entry))) {
                            mContext.startActivity(new Intent(mContext, MilkEntryDateActivity.class));
                        }

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));


    }


    public List<Dashboard_item> getSallerMenu() {
        List<Dashboard_item> buyerMenu = new ArrayList<>();
        buyerMenu.add(new Dashboard_item("1", mContext.getResources().getString(R.string.Transaction), "", R.drawable.tranasctions_icon, "#00BADB"));
        buyerMenu.add(new Dashboard_item("2", mContext.getResources().getString(R.string.entry), "", R.drawable.view_entry, "#35CE8E"));
        buyerMenu.add(new Dashboard_item("3", mContext.getResources().getString(R.string.Product), "", R.drawable.products, "#FF8400"));
        return buyerMenu;
    }

    private void initRecyclerView() {

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
        MonthlyEntryListAdapter monthsListAdapter = new MonthlyEntryListAdapter(mContext, entryListPojos);
        recyclerViewEntry.setLayoutManager(mLayoutManager);
        recyclerViewEntry.setAdapter(monthsListAdapter);
        monthsListAdapter.notifyDataSetChanged();


    }

    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    public void getDairyNameList() {
        mList = new ArrayList<>();


        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        mList.add(new DairyNamePojo(jsonObject1.getString("id"), jsonObject1.getString("name")
                                , jsonObject1.getString("dairy_name"), jsonObject1.getString("center_name"), jsonObject1.getString("unic_customer_for_mobile"), jsonObject1.getString("unic_customer")
                                , jsonObject1.getString("phone_number"), jsonObject1.getString("firebase_tocan")
                                , jsonObject1.getString("customer_id")
                                , jsonObject1.getString("user_group_id"), false));
                    }
                    selectStateDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("userid", userId)
                .addEncoded("user_group_id", userGroupId)
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(getAllDairyNameAPI);

    }

    private void selectStateDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_list_item, null);
        dialogBuilder.setView(dialogView);
        TextView dialogTitle, tvCancel;
        ListView listView;

        listView = dialogView.findViewById(R.id.list);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(mContext.getResources().getString(R.string.select) + " " + mContext.getResources().getString(R.string.Dairy));
        SpinnerDialogAdapter spinnerAdapter = new SpinnerDialogAdapter(getActivity(), mList);
        listView.setAdapter(spinnerAdapter);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder = dialogBuilder.create();
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.show();
    }

    private void getEntryList() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    entryListPojos = new ArrayList<>();
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        JSONArray morningArray = jsonObject1.getJSONArray("morning");
                        if (morningArray.length() != 0) {
                            for (int j = 0; j < morningArray.length(); j++) {
                                JSONObject jsonObj = morningArray.getJSONObject(j);
                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));

                            }
                        } else {
                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
                                    , "", "", ""
                                    , "", "", ""
                                    , "","","", "morning"));

                        }
                        JSONArray eveningArray = jsonObject1.getJSONArray("evening");
                        if (eveningArray.length() != 0) {
                            for (int j = 0; j < eveningArray.length(); j++) {
                                JSONObject jsonObj = eveningArray.getJSONObject(j);
                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));

                            }
                        } else {
                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
                                    , "", "", ""
                                    , "", "", ""
                                    , "","","", "evening"));
                        }
                    }
                    if (!entryListPojos.isEmpty()) {

                        double totalWeight = 0d, totalPrice = 0d;
                        ArrayList<MonthsEntryListPojo> newEntryList = new ArrayList<>();
                        if (mDay >= 1 && mDay <= 10) {
                            System.out.println("Days Data==getTenDaysData:== " + "b/w 1-10");

                            //  int size = mMaxDay * 2;
                            for (int i = 0; i < 20; i++) {
                                if (!entryListPojos.get(i).total_milk.equals("")) {
                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
                                }
                                if (!entryListPojos.get(i).total_price.equals("")) {
                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
                                }
                                newEntryList.add(new MonthsEntryListPojo(
                                        entryListPojos.get(i).entry_date, entryListPojos.get(i).id,
                                        entryListPojos.get(i).customer_id,
                                        entryListPojos.get(i).dairy_id,
                                        entryListPojos.get(i).fat,
                                        entryListPojos.get(i).snf,
                                        entryListPojos.get(i).entry_date2,
                                        entryListPojos.get(i).per_kg_price,
                                        entryListPojos.get(i).total_price,
                                        entryListPojos.get(i).total_milk,
                                        entryListPojos.get(i).total_bonus,
                                        entryListPojos.get(i).shift
                                ));

                            }


                        } else if (mDay >= 11 && mDay <= 20) {

                            System.out.println("==getTenDaysData: " + "b/w 11-20");

                            for (int i = 21; i < 40; i++) {
                                if (!entryListPojos.get(i).total_milk.equals("")) {
                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
                                }
                                if (!entryListPojos.get(i).total_price.equals("")) {
                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
                                }
                                newEntryList.add(new MonthsEntryListPojo(
                                        entryListPojos.get(i).entry_date,
                                        entryListPojos.get(i).id, entryListPojos.get(i).customer_id,
                                        entryListPojos.get(i).dairy_id, entryListPojos.get(i).fat, entryListPojos.get(i).snf,
                                        entryListPojos.get(i).entry_date2, entryListPojos.get(i).per_kg_price,
                                        entryListPojos.get(i).total_price, entryListPojos.get(i).total_bonus,
                                        entryListPojos.get(i).total_milk, entryListPojos.get(i).shift
                                ));

                            }


                        } else if (mDay >= 21 && mDay <= mMaxDay) {


                            for (int i = 41; i < entryListPojos.size(); i++) {
                                if (!entryListPojos.get(i).total_milk.equals("")) {
                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
                                }
                                if (!entryListPojos.get(i).total_price.equals("")) {
                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
                                }
                                newEntryList.add(new MonthsEntryListPojo(
                                        entryListPojos.get(i).entry_date, entryListPojos.get(i).id,
                                        entryListPojos.get(i).customer_id, entryListPojos.get(i).dairy_id,
                                        entryListPojos.get(i).fat, entryListPojos.get(i).snf, entryListPojos.get(i).entry_date2,
                                        entryListPojos.get(i).per_kg_price, entryListPojos.get(i).total_price,
                                        entryListPojos.get(i).total_bonus,  entryListPojos.get(i).total_milk, entryListPojos.get(i).shift
                                ));
                            }
                        }


                        entryListPojos = new ArrayList<>();
                        entryListPojos.addAll(newEntryList);
                        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + ": \n" + String.format("%.2f", totalWeight) + " " + mContext.getString(R.string.Ltr));
                        tvTotalAmt.setText(mContext.getString(R.string.Total_Amount) + ": \n" + String.format("%.2f", totalPrice) + " " + mContext.getString(R.string.Rs));

                        initRecyclerView();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", dairyCustomerId)
                .addEncoded("month", "" + month)
                .addEncoded("year", "" + mYear)
                .addEncoded("dairy_id", dairyId)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);

        caller.execute(Constant.getMonthMilkEntry);


    }

    public class SpinnerDialogAdapter extends BaseAdapter {
        Context context;
        List<DairyNamePojo> rowItems;


        public SpinnerDialogAdapter(Context context, List<DairyNamePojo> items) {
            this.context = context;
            this.rowItems = items;
            dairyCustomerId = "";
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            SpinnerDialogAdapter.ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cardview_common_row_iteam, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (SpinnerDialogAdapter.ViewHolder) convertView.getTag();

            }
            holder.textName.setText(rowItems.get(position).dairy_name);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    toolbar_title.setText(rowItems.get(position).dairy_name);
                    Constant.dairyID = rowItems.get(position).id;
                    Constant.DairyName = rowItems.get(position).dairy_name;
                    dairyCustomerId = rowItems.get(position).customer_id;
                    Constant.UserID = dairyCustomerId;
                    dairyId = rowItems.get(position).id;
                    dairyName = rowItems.get(position).dairy_name;
                    Calendar cal = Calendar.getInstance();
                    mYear = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH) + 1;
                    mDay = cal.get(Calendar.DAY_OF_MONTH);
                    mMaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                    getEntryList();
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItems.indexOf(getItem(position));
        }

        public class ViewHolder {
            TextView textName;

        }


    }
}
