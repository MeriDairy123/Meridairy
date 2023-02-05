package b2infosoft.milkapp.com.Dairy.Customer.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import b2infosoft.milkapp.com.Dairy.Customer.Adapter.UserListAdapter;
import b2infosoft.milkapp.com.Dairy.Invoice.ViewUserMilkEntryFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase.AddPurchaseInvoiceSingleProdFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

/**
 * Created by 9166900279 on 27-Dec-19.
 */

public class AllCustomerListFragment extends
        Fragment implements View.OnClickListener,
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
    UserListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Object tag;
    int backgroundId;
    int backgroundId2;
    ArrayList<CustomerListPojo> filterList;
    String screenFrom = "";
    String userGroupId = "3";
    Bundle bundle;

    View view;
    Fragment fragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seller_customer_list, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        backgroundId = R.drawable.ic_arrow_drop_up;
        backgroundId2 = R.drawable.ic_arrow_drop_down;
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        sessionManager = new SessionManager(mContext);
        mainCustomerList = new ArrayList<>();
        filterList = new ArrayList<>();
        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);

        return view;
    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.USER_LIST));
        et_Search = view.findViewById(R.id.et_Seller_Search);
        recycler_customerList = view.findViewById(R.id.recycler_sellerList);
        screenFrom = FromWhere;
        bundle = getArguments();
        if (bundle != null) {
            screenFrom = bundle.getString("FromWhere");
            userGroupId = bundle.getString("user_group_id");
        }
        getCustomerList();


        tvId = view.findViewById(R.id.tvSellerId);
        tvName = view.findViewById(R.id.tvSellerName);
        tvFatherName = view.findViewById(R.id.tvFatherName);
        tvAmount = view.findViewById(R.id.tvSellerAmount);

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


        setCustomerList(mainCustomerList);

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

        mainCustomerList = new ArrayList<>();


        for (int i = 0; i < customerList.size(); i++) {
            if (customerList.get(i).user_group_id.equals(userGroupId)) {
                CustomerListPojo customerListPojo = customerList.get(i);
                mainCustomerList.add(customerListPojo);
            }
        }

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
        adapter = new UserListAdapter(view.getContext(), mainCustomerList, FromWhere, this);
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

                    System.out.print("Up===>>>" + "" + backgroundId);
                } else {
                    System.out.print("Down===>>>" + "" + backgroundId2);
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

        if (adapter != null) {
            adapter.updateAdapter(mainCustomerList);
        }
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:
                OnFragmentBackPressListener();
                return true;

        }

        return false;
    }


    public void getCustomerList() {

        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerList();
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {

        Bundle bundle = new Bundle();
        bundle.putString("unic_customer", customerList.getUnic_customer());
        bundle.putString("CustomerId", customerList.getId());
        bundle.putString("CustomerName", customerList.getName());
        bundle.putString("CustomerFatherName", customerList.getFather_name());
        bundle.putString("category_chart_id", customerList.getCategorychart_id());

        if (screenFrom.equalsIgnoreCase("product")) {

            fragment = new AddPurchaseInvoiceSingleProdFragment();
            bundle.putString("FromWhere", "CustomerList");
            bundle.putString("entry_type", customerList.getEntry_type());
            bundle.putString("entry_price", customerList.getEntry_price());
            fragment.setArguments(bundle);


        } else {
            fragment = new ViewUserMilkEntryFragment();
        }
        goNextFragmentReplace(mContext, fragment);

    }

    @Override
    public void OnFragmentBackPressListener() {

        if (Constant.FromWhere.equals("Product")) {
            fragment = new AddPurchaseInvoiceSingleProdFragment();
            goNextFragmentReplace(mContext, fragment);
        }
    }
}