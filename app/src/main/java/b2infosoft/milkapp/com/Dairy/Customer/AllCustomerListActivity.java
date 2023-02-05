package b2infosoft.milkapp.com.Dairy.Customer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import b2infosoft.milkapp.com.Dairy.Customer.Adapter.AllCustomerListAdapter;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewMilkEntryBYUserFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class AllCustomerListActivity extends Activity implements View.OnClickListener,
        View.OnKeyListener, OnCustomerListener, FragmentBackPressListener {

    RecyclerView recycler_customerList;
    Context mContext;
    EditText et_Search;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    TextView tvId, tvName, tvFatherName, tvAmount;
    ArrayList<CustomerListPojo> mainCustomerList;
    DatabaseHandler databaseHandler;
    AllCustomerListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Object tag;
    int backgroundId;
    int backgroundId2;

    String screenFrom = "";
    String userGroupId = "3";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_customer_list);
        mContext = AllCustomerListActivity.this;

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);

        backgroundId = R.drawable.ic_arrow_drop_up;
        backgroundId2 = R.drawable.ic_arrow_drop_down;
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        mainCustomerList = new ArrayList<>();
        Intent intent = getIntent();
        userGroupId = intent.getStringExtra("user_group_id");
        initView();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                OnFragmentBackPressListener();
                return true;
            }
        }

        return false;
    }

    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.USER_LIST));

        et_Search = findViewById(R.id.et_Search);
        recycler_customerList = findViewById(R.id.rvInvoiceList);
        screenFrom = FromWhere;
        getCustomerList();
        tvId = findViewById(R.id.tvSellerId);
        tvName = findViewById(R.id.tvSellerName);
        tvFatherName = findViewById(R.id.tvFatherName);
        tvAmount = findViewById(R.id.tvSellerAmount);
        tvId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        tvFatherName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
        tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);

        tvId.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvFatherName.setOnClickListener(this);
        tvAmount.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentBackPressListener();
            }
        });


    }


    public void addTextListener() {


        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                adapter.filterSearch(query.toString());


            }
        });
    }


    public void setCustomerList(ArrayList<CustomerListPojo> customerList) {
        recycler_customerList = findViewById(R.id.rvInvoiceList);
        mainCustomerList = customerList;

        if (mainCustomerList.size() > 0) {
            Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                @Override
                public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                    int u_ID1 = Integer.parseInt(object1.unic_customer);
                    int u_ID2 = Integer.parseInt(object2.unic_customer);
                    return u_ID1 - u_ID2;
                }
            });
        }

        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new AllCustomerListAdapter(mContext, mainCustomerList, FromWhere, this);
        recycler_customerList.setLayoutManager(mLayoutManager);
        recycler_customerList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        addTextListener();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvAmount:
                tag = tvAmount.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvAmount.setTag(backgroundId2);
                    tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc

                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.amount.compareTo(object2.amount);
                            }
                        });
                        Collections.reverse(mainCustomerList);
                    }


                } else {

                    tvAmount.setTag(backgroundId);
                    tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.amount.compareTo(object2.amount);
                            }
                        });
                    }
                }

                break;
            case R.id.tvId:
                tag = tvId.getTag();

                if (tag != null && ((Integer) tag).intValue() == backgroundId2) {

                    tvId.setTag(backgroundId);
                    tvId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                //return object1.unic_customer.compareTo(object2.unic_customer);
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);
                                return u_ID1 - u_ID2;
                            }
                        });
                    }
                } else {
                    tvId.setTag(backgroundId2);
                    tvId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);

                                return u_ID1 - u_ID2;
                                //  return object1.unic_customer.compareTo(object2.unic_customer);
                            }
                        });
                        Collections.reverse(mainCustomerList);
                    }
                }

                break;
            case R.id.tvName:
                tag = tvName.getTag();

                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvName.setTag(backgroundId2);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                        Collections.reverse(mainCustomerList);
                    }
                } else {
                    tvName.setTag(backgroundId);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                    }
                }

                break;
            case R.id.tvFatherName:
                tag = tvFatherName.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvFatherName.setTag(backgroundId2);
                    tvFatherName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.father_name.compareTo(object2.father_name);
                            }
                        });
                        Collections.reverse(mainCustomerList);
                    }
                } else {
                    tvFatherName.setTag(backgroundId);
                    tvFatherName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.father_name.compareTo(object2.father_name);
                            }
                        });
                    }
                }
                break;
        }

        adapter.updateAdapter(mainCustomerList);
    }


    public void getCustomerList() {

        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerListByGroupId(userGroupId);

        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {


        Intent msgintent = new Intent("CustomerData");
        System.out.println("==CustomerList===");
        msgintent.putExtra("from", "CustomerList");
        msgintent.putExtra("unic_customer", customerList.getUnic_customer());
        msgintent.putExtra("user_group_id", customerList.getUser_group_id());
        msgintent.putExtra("CustomerId", customerList.getId());
        msgintent.putExtra("CustomerName", customerList.getName());
        msgintent.putExtra("CustomerFatherName", customerList.getFather_name());
        new ViewMilkEntryBYUserFragment().onClickUser(customerList);
        onBackPressed();


    }

    @Override
    public void OnFragmentBackPressListener() {
        finish();
    }
}
