package b2infosoft.milkapp.com.Dairy.Customer.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleMilkEntryFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleMilkEntryFullScreenFragment;
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
 * Created by u on 27-Dec-17.
 */

public class FragmentCustomerBuyerList extends Fragment implements View.OnClickListener,
        View.OnKeyListener, OnCustomerListener, FragmentBackPressListener {
    public static Fragment newInstance(Fragment targetFragment,String from) {
        Fragment fragment = new FragmentCustomerBuyerList();

        Bundle args = new Bundle();
        args.putString("from",from);
        fragment.setArguments(args);
        fragment.setTargetFragment(targetFragment,125566);
        return fragment;
    }

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
    String userGroupId = "4";

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
        toolbar_title.setText(mContext.getString(R.string.BUYER_LIST));
        et_Search = view.findViewById(R.id.et_Seller_Search);
        recycler_customerList = view.findViewById(R.id.recycler_sellerList);
        screenFrom = FromWhere;
        if (screenFrom.equalsIgnoreCase("SaleEntry") || screenFrom.equalsIgnoreCase("SaleEntryCustom")) {
            toolbar_title.setText(mContext.getString(R.string.BUYER_LIST));
            userGroupId = "4";
        }else {
            toolbar_title.setText(mContext.getString(R.string.USER_LIST));

        }
        getCustomerList();


        tvId = view.findViewById(R.id.tvSellerId);
        tvName = view.findViewById(R.id.tvSellerName);
        tvFatherName = view.findViewById(R.id.tvFatherName);
        tvAmount = view.findViewById(R.id.tvSellerAmount);

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
        System.out.println("customerList==>>" + customerList.size());

        for (int i = 0; i < customerList.size(); i++) {
            if (customerList.get(i).user_group_id.equals(userGroupId)) {
                CustomerListPojo customerListPojo = customerList.get(i);
                mainCustomerList.add(customerListPojo);
            }
        }
        System.out.println("mainCustomerList  size========>>" + mainCustomerList.size());
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

                    System.out.print("Up>>>" + "" + backgroundId);
                } else {
                    System.out.print("Down>>>" + "" + backgroundId2);
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
                backgroundId = R.drawable.ic_arrow_drop_up;
                backgroundId2 = R.drawable.ic_arrow_drop_down;
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
                backgroundId = R.drawable.ic_arrow_drop_up;
                backgroundId2 = R.drawable.ic_arrow_drop_down;
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

        setCustomerList(mainCustomerList);
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


    public void getCustomerList() {
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerList();
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {

       /* fragment = new SaleMilkEntryFragment();
        if (Constant.FromWhere.equals("SaleEntryCustom")) {
            fragment = new SaleMilkEntryFullScreenFragment();
        }*/

        Bundle bundle = new Bundle();
        bundle.putString("unic_customer", customerList.unic_customer);
        bundle.putString("CustomerId", customerList.id);
        bundle.putString("CustomerName", customerList.name);
        bundle.putString("CustomerFatherName", customerList.father_name);

        bundle.putString("FromWhere", "CustomerList");
        bundle.putString("entry_type", customerList.entry_type);
        bundle.putString("entry_price", customerList.entry_price);
      //  fragment.setArguments(bundle);

        Intent i=new Intent();
        i.putExtras(bundle);

        getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,i);
        // goNextFragmentReplace(mContext, fragment);
        getActivity().onBackPressed();
       // goNextFragmentReplace(mContext, fragment);


    }

    @Override
    public void OnFragmentBackPressListener() {

        Bundle bundle = new Bundle();

        /*if (Constant.FromWhere.equals("SaleEntry")) {
            fragment = new SaleMilkEntryFragment();
            bundle.putString("FromWhere", "SaleMilkEntry");
            fragment.setArguments(bundle);
            goNextFragmentReplace(mContext, fragment);

        } else if (Constant.FromWhere.equals("SaleEntryCustom")) {
            fragment = new SaleMilkEntryFullScreenFragment();
            bundle.putString("FromWhere", "SaleMilkEntry");
            fragment.setArguments(bundle);
            goNextFragmentReplace(mContext, fragment);

        }*/
        getActivity().onBackPressed();
    }
}