package b2infosoft.milkapp.com.Dairy.Customer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.BluetoothPrinter.Reciept_Item;
import b2infosoft.milkapp.com.Dairy.Customer.Adapter.BuyerCustomerListAdapter;
import b2infosoft.milkapp.com.Dairy.Customer.Adapter.UserListAdapter;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentTransactionHistoryFragment;
import b2infosoft.milkapp.com.Dairy.Product.SaleItemFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Interface.RefreshEntryList;
import b2infosoft.milkapp.com.Model.BuyerMilkCustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printCustomer;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getCustomerListAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getOneMonthBackDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class BuyerSellerCustomerListFragment
        extends Fragment implements View.OnClickListener, RefreshEntryList,
        OnCustomerListener {

    Context mContext;
    ImageView imgPrint;
    TextView btnAddCustomer;
    View mainView;
    Fragment fragment = null;
    SessionManager sessionManager;
    Button btnSellerList, btnBuyerList;
    EditText et_Search;
    Drawable leftDrawable;
    TextView tvId, tvName, tvFatherName, tvAmount, tvPhone, tvStatus;
    SwipeRefreshLayout pullToRefreshBuyer;
    RecyclerView recyclerCustomerList;
    String dairyID = "";
    boolean isBuyer = false;
    Object tag;
    int backgroundId;
    int backgroundId2;
    //seller
    ArrayList<CustomerListPojo> sellerCustomerList;
    UserListAdapter userListAdapter;
    BuyerCustomerListAdapter mAdapter;
    ArrayList<BuyerMilkCustomerListPojo> buyerMilkCustomerList;

    // Buyer
    private DatabaseHandler databaseHandler;
    private Toolbar toolbar;
    ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_buyer_seller_customer_list, container, false);
        mContext = getActivity();
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        sessionManager = new SessionManager(mContext);
        leftDrawable = AppCompatResources.getDrawable(mContext, R.drawable.ic_arrow_drop_down);
        sellerCustomerList = new ArrayList<>();
        buyerMilkCustomerList = new ArrayList<>();
        dairyID = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        backgroundId = R.drawable.ic_arrow_drop_up;
        backgroundId2 = R.drawable.ic_arrow_drop_down;

        initView(mainView);
        return mainView;
    }

    private void initView(View mainView) {

        toolbar = mainView.findViewById(R.id.toolbar);
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        btnBuyerList = mainView.findViewById(R.id.btnBuyerList);
        btnSellerList = mainView.findViewById(R.id.btnSellerList);
        et_Search = mainView.findViewById(R.id.et_Search);
        tvId = mainView.findViewById(R.id.tvId);
        tvName = mainView.findViewById(R.id.tvName);
        tvFatherName = mainView.findViewById(R.id.tvFatherName);
        tvAmount = mainView.findViewById(R.id.tvAmount);
        tvPhone = mainView.findViewById(R.id.tvPhone);
        tvStatus = mainView.findViewById(R.id.tvStatus);
        pullToRefreshBuyer = mainView.findViewById(R.id.pullToRefresh);
        recyclerCustomerList = mainView.findViewById(R.id.recyclerCustomerList);
        progressBar = mainView.findViewById(R.id.progressBar);
        btnAddCustomer = mainView.findViewById(R.id.btnAddCustomer);
        if (FromWhere.equals("ViewUserEntry")) {
            btnAddCustomer.setVisibility(View.GONE);
        } else {
            btnAddCustomer.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.VISIBLE);
        imgPrint.setOnClickListener(this);
        btnSellerList.setOnClickListener(this);
        btnBuyerList.setOnClickListener(this);
        btnAddCustomer.setOnClickListener(this);


        toolbar.setTitle(mContext.getString(R.string.USER_LIST));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        pullToRefreshBuyer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.VISIBLE);
                recyclerCustomerList.invalidate();
                if (isBuyer) {
                    getBuyerMilkCustomerList();
                } else {
                    getSellerList();
                }
                pullToRefreshBuyer.setRefreshing(false);
            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();

        if (isBuyer) {
            btnBuyerClick();
        } else {
            btnSellerClick();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAddCustomer:
                FromWhere = "Dashboard";
                fragment = new AddCustomerFragment();
                goNextFragmentWithBackStack(mContext, fragment);
                break;
            case R.id.imgPrint:
                if (sellerCustomerList.size() != 0 && !isBuyer) {
                    printUser();
                } else if (isBuyer || buyerMilkCustomerList.size() != 0) {
                    printUser();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.You_have_no_entry_to_Print));
                }
                break;
            case R.id.btnSellerList:
                isBuyer = false;
                btnSellerClick();

                break;
            case R.id.btnBuyerList:
                isBuyer = true;
                btnBuyerClick();
                // btnSellerList.setBackgroundResource(R.drawable.border_shape_rectangle_black);
                break;


            case R.id.tvId:
                tag = tvId.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId2) {

                    tvId.setTag(backgroundId);
                    tvId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
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
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);

                                return u_ID1 - u_ID2;
                                //  return object1.unic_customer.compareTo(object2.unic_customer);
                            }
                        });
                        Collections.reverse(sellerCustomerList);
                    }
                }

                intitSellerRecycleView();
                break;
            case R.id.tvName:
                tag = tvName.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvName.setTag(backgroundId2);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                        Collections.reverse(sellerCustomerList);
                    }
                } else {
                    tvName.setTag(backgroundId);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                    }
                }
                intitSellerRecycleView();
                break;
            case R.id.tvFatherName:
                tag = tvFatherName.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvFatherName.setTag(backgroundId2);
                    tvFatherName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.father_name.compareTo(object2.father_name);
                            }
                        });
                        Collections.reverse(sellerCustomerList);
                    }
                } else {
                    tvFatherName.setTag(backgroundId);
                    tvFatherName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.father_name.compareTo(object2.father_name);
                            }
                        });
                    }
                }
                intitSellerRecycleView();
                break;

            case R.id.tvAmount:
                tag = tvAmount.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvAmount.setTag(backgroundId2);
                    tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.amount.compareTo(object2.amount);
                            }
                        });
                        Collections.reverse(sellerCustomerList);
                    }
                } else {
                    tvAmount.setTag(backgroundId);
                    tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (sellerCustomerList.size() > 0) {
                        Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                            @Override
                            public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                                return object1.amount.compareTo(object2.amount);
                            }
                        });
                    }
                }
                intitSellerRecycleView();
                break;
        }


    }

    private void btnBuyerClick() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerCustomerList.invalidate();
        btnBuyerList.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_global));
        btnBuyerList.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        btnSellerList.setBackground(mContext.getResources().getDrawable(R.drawable.border_shape_rectangle_black));
        btnSellerList.setTextColor(mContext.getResources().getColor(R.color.color_blue));
        if (isNetworkAvaliable(mContext)) {
            getBuyerMilkCustomerList();
        } else {
            buyerMilkCustomerList = databaseHandler.getBuyerListByGroupId("4");
            setBuyerCustomerList();
        }


    }

    private void btnSellerClick() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerCustomerList.invalidate();
        btnSellerList.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_global));
        btnSellerList.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
        btnBuyerList.setBackground(mContext.getResources().getDrawable(R.drawable.border_shape_rectangle_black));
        btnBuyerList.setTextColor(mContext.getResources().getColor(R.color.color_blue));
        tvAmount.setVisibility(View.VISIBLE);
        tvFatherName.setVisibility(View.VISIBLE);
        tvId.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvFatherName.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvAmount.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvPhone.setVisibility(View.GONE);
        tvStatus.setVisibility(View.GONE);

        tvId.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvFatherName.setOnClickListener(this);
        tvAmount.setOnClickListener(this);
        if (isNetworkAvaliable(mContext)) {
            getSellerList();
        } else {
            sellerCustomerList = databaseHandler.getCustomerListByGroupId("3");
            setSellerData();
        }

    }

    private void setSellerData() {


        if (sellerCustomerList.size() > 0) {
            Collections.sort(sellerCustomerList, new Comparator<CustomerListPojo>() {
                @Override
                public int compare(final CustomerListPojo object1, final CustomerListPojo object2) {
                    //return object1.unic_customer.compareTo(object2.unic_customer);
                    int u_ID1 = Integer.parseInt(object1.unic_customer);
                    int u_ID2 = Integer.parseInt(object2.unic_customer);
                    return u_ID1 - u_ID2;
                }
            });
        }

        intitSellerRecycleView();
    }

    private void intitSellerRecycleView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        userListAdapter = new UserListAdapter(mContext, sellerCustomerList, Constant.FromWhere, this);
        recyclerCustomerList.setLayoutManager(mLayoutManager);
        recyclerCustomerList.setAdapter(userListAdapter);
        userListAdapter.notifyDataSetChanged();
        addTextListener();
        progressBar.setVisibility(View.GONE);
    }


    public void addTextListener() {
        et_Search = mainView.findViewById(R.id.et_Search);
        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                String querText = query.toString().toLowerCase();
                System.out.println("==isBuyer==" + isBuyer);
                System.out.println("==isBuyer==" + querText);
                if (isBuyer) {
                    mAdapter.filterSearch(querText);
                } else {
                    userListAdapter.filterSearch(querText);
                }
            }
        });


    }


    @Override
    public void refreshList(ArrayList<CustomerEntryListPojo> viewEntryPojoArrayList) {

    }

    @Override
    public void refreshCustomerList(ArrayList<CustomerListPojo> customerListPojos) {

    }

    public void setBuyerCustomerList() {

        tvId.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvName.setCompoundDrawablesWithIntrinsicBounds(null, null, leftDrawable, null);
        tvPhone.setVisibility(View.VISIBLE);
        tvStatus.setVisibility(View.VISIBLE);
        tvAmount.setVisibility(View.GONE);
        tvFatherName.setVisibility(View.GONE);
        intitBuyerRecycleView(buyerMilkCustomerList);
        progressBar.setVisibility(View.GONE);
    }


    private void intitBuyerRecycleView(ArrayList<BuyerMilkCustomerListPojo> buyerMilkCustomerList) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mAdapter = new BuyerCustomerListAdapter(mContext, buyerMilkCustomerList);
        recyclerCustomerList.setLayoutManager(mLayoutManager);
        recyclerCustomerList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        addTextListener();


        tvId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tag = tvId.getTag();
                if (tag != null && ((Integer) tag).intValue() == backgroundId2) {
                    tvId.setTag(backgroundId);
                    tvId.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (buyerMilkCustomerList.size() > 0) {
                        Collections.sort(buyerMilkCustomerList, new Comparator<BuyerMilkCustomerListPojo>() {
                            @Override
                            public int compare(final BuyerMilkCustomerListPojo object1, final BuyerMilkCustomerListPojo object2) {
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
                    if (buyerMilkCustomerList.size() > 0) {
                        Collections.sort(buyerMilkCustomerList, new Comparator<BuyerMilkCustomerListPojo>() {
                            @Override
                            public int compare(final BuyerMilkCustomerListPojo object1, final BuyerMilkCustomerListPojo object2) {
                                int u_ID1 = Integer.parseInt(object1.unic_customer);
                                int u_ID2 = Integer.parseInt(object2.unic_customer);

                                return u_ID1 - u_ID2;
                                //  return object1.unic_customer.compareTo(object2.unic_customer);
                            }
                        });
                        Collections.reverse(buyerMilkCustomerList);
                    }
                }

                mAdapter.updaterAdapter(buyerMilkCustomerList);


            }
        });

        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tag = tvName.getTag();

                if (tag != null && ((Integer) tag).intValue() == backgroundId) {
                    tvName.setTag(backgroundId2);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                    //Dsc
                    if (buyerMilkCustomerList.size() > 0) {
                        Collections.sort(buyerMilkCustomerList, new Comparator<BuyerMilkCustomerListPojo>() {
                            @Override
                            public int compare(final BuyerMilkCustomerListPojo object1, final BuyerMilkCustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                        Collections.reverse(buyerMilkCustomerList);
                    }
                } else {
                    tvName.setTag(backgroundId);
                    tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (buyerMilkCustomerList.size() > 0) {
                        Collections.sort(buyerMilkCustomerList, new Comparator<BuyerMilkCustomerListPojo>() {
                            @Override
                            public int compare(final BuyerMilkCustomerListPojo object1, final BuyerMilkCustomerListPojo object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                    }
                }
                mAdapter.updaterAdapter(buyerMilkCustomerList);
            }
        });


    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {
        String endDate = getSimpleDate();
        String startDate = getOneMonthBackDate();
        Bundle bundle = new Bundle();
        bundle.putString("unic_customer", customerList.getUnic_customer());
        bundle.putString("CustomerId", customerList.getId());
        bundle.putString("CustomerName", customerList.getName());
        bundle.putString("CustomerFatherName", customerList.getFather_name());
        bundle.putString("user_group_id", customerList.getUser_group_id());
        if (FromWhere.equals("ExistUser")) {
            fragment = new SaleItemFragment();
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }else {
            bundle.putString("FromWhere", "btnCustomer");
            bundle.putString("startDate", startDate);
            bundle.putString("endDate", endDate);
            fragment = new PaymentTransactionHistoryFragment();
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }

    }

    private void getSellerList() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, getActivity(), "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    sellerCustomerList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            if (databaseHandler.getCustomerList().size() != 0) {
                                databaseHandler.deleteCustomer();
                            }
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject obj = mainJsonArray.getJSONObject(i);
                                if (obj.getString("user_group_id").equalsIgnoreCase("3")) {
                                    sellerCustomerList.add(new CustomerListPojo(obj.getString("Userid"), obj.getString("user_group_id"),
                                            obj.getString("categorychart_id"), obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                            obj.getString("name"), obj.getString("father_name"), obj.getString("phone_number"), obj.getString("adhar"),
                                            obj.getString("village"), obj.getString("address"), obj.getString("remaing_amount"), obj.getString("entry_type"), obj.getString("entry_price"),
                                            obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                            obj.getString("firebase_tocan")));
                                }
                                databaseHandler.addCustomer(obj.getString("Userid"),
                                        obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                        obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                        obj.getString("name"), obj.getString("father_name"),
                                        obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                        obj.getString("address"), obj.getString("remaing_amount"), obj.getString("entry_type"),
                                        obj.getString("entry_price"),
                                        obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                        obj.getString("firebase_tocan"));
                            }
                        }


                        setSellerData();

                    } else {
                        setSellerData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyID)
                .addEncoded("user_group_id", "3")
                .build();
        caller.addRequestBody(body);
        caller.execute(getCustomerListAPI);

    }

    public void getBuyerMilkCustomerList() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    System.out.println("=buyerMilkCustomer+buyer=>>>" + response);
                    buyerMilkCustomerList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    progressBar.setVisibility(View.VISIBLE);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            databaseHandler.deleteBuyerCustomer();
                        }

                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject obj = mainJsonArray.getJSONObject(i);

                            buyerMilkCustomerList.add(new BuyerMilkCustomerListPojo(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan")));

                            databaseHandler.addBuyerCustomer(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan"));

                        }
                        setBuyerCustomerList();

                    } else {
                        setBuyerCustomerList();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyID)
                .build();
        caller.addRequestBody(body);

        caller.execute(getBuyerMilkListAPI);
    }

    public void printUser() {

        if (isBluetoothHeadsetConnected()) {
            if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                dialogBluetooth(mContext);
            } else {
                List<Reciept_Item> userList = new ArrayList<>();
                if (isBuyer && !buyerMilkCustomerList.isEmpty()) {

                    for (int i = 0; i < buyerMilkCustomerList.size(); i++) {
                        String name = buyerMilkCustomerList.get(i).name;
                        if (name.length() > 12) {
                            name = name.substring(0, 11);
                        }

                        userList.add(new Reciept_Item(
                                buyerMilkCustomerList.get(i).unic_customer, name,
                                buyerMilkCustomerList.get(i).phone_number, ""));
                    }
                    printCustomer(mContext, isBuyer, userList);
                } else if (!sellerCustomerList.isEmpty()) {
                    for (int i = 0; i < sellerCustomerList.size(); i++) {
                        String name = sellerCustomerList.get(i).name;
                        if (name.length() > 12) {
                            name = name.substring(0, 11);
                        }

                        userList.add(new Reciept_Item(
                                sellerCustomerList.get(i).unic_customer, name,
                                sellerCustomerList.get(i).phone_number, ""));
                    }
                    printCustomer(mContext, isBuyer, userList);

                }
            }
        } else {
            showAlertWithTitle("Please enable Bluetooth device", mContext);
            enableBluetooth();
            dialogBluetooth(mContext);

        }
    }

    public boolean enableBluetooth() {
        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
