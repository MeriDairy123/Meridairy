package b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Fragment;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Adapter.VehicleDairyListAdapter;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanVehicleDairyItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Model.BeanVehicleDairyItem.getVehicleDairyList;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class VehicleDairyFragment extends Fragment implements View.OnClickListener,
        View.OnKeyListener, UpdateList, FragmentBackPressListener {

    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    Context mContext;
    EditText et_Search;
    Toolbar toolbar;

    SessionManager sessionManager;
    TextView tvId, tvName, tvFatherName, tvAmount;
    ArrayList<BeanVehicleDairyItem> mainCustomerList;

    VehicleDairyListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    Object tag;
    int backgroundId;
    int backgroundId2;

    String screenFrom = "", userGroupId = "";


    View view;
    Fragment fragment;
    Bundle bundle;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_vehicle_dairy_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        backgroundId = R.drawable.ic_arrow_drop_up;
        backgroundId2 = R.drawable.ic_arrow_drop_down;
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        bundle = getArguments();


        sessionManager = new SessionManager(mContext);
        mainCustomerList = new ArrayList<>();
        screenFrom = FromWhere;
        if (bundle != null) {
            userGroupId = bundle.getString("userGroupId");
            screenFrom = bundle.getString("from");
        }

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        initView();
        return view;
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

        toolbar = view.findViewById(R.id.toolbar);

        toolbar.setTitle(mContext.getString(R.string.USER_LIST));

        et_Search = view.findViewById(R.id.et_Search);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);


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
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVehicleDairyList(mContext, userGroupId, true);
                getDairyList();
                pullToRefresh.setEnabled(false);
            }
        });

        getDairyList();
    }


    public void getDairyList() {
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanVehicleDairy" + userGroupId);

        mainCustomerList = gson.fromJson(json, new TypeToken<ArrayList<BeanVehicleDairyItem>>() {
        }.getType());
        if (mainCustomerList == null) {
            mainCustomerList = new ArrayList<>();
        }

        setCustomerList();
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


    public void setCustomerList() {
        recyclerView = view.findViewById(R.id.recyclerView);


        if (mainCustomerList.size() > 0) {
            Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                @Override
                public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
                    int u_ID1 = Integer.parseInt(object1.unic_customer);
                    int u_ID2 = Integer.parseInt(object2.unic_customer);
                    return u_ID1 - u_ID2;
                }
            });
        }

        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new VehicleDairyListAdapter(view.getContext(), mainCustomerList, FromWhere, this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
                                return object1.amount.compareTo(object2.amount);
                            }
                        });
                        Collections.reverse(mainCustomerList);
                    }

                    System.out.println("Up==>>>" + "" + backgroundId);
                } else {
                    System.out.println("Down==>>>" + "" + backgroundId2);
                    tvAmount.setTag(backgroundId);
                    tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (mainCustomerList.size() > 0) {
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
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
                        Collections.sort(mainCustomerList, new Comparator<BeanVehicleDairyItem>() {
                            @Override
                            public int compare(final BeanVehicleDairyItem object1, final BeanVehicleDairyItem object2) {
                                return object1.father_name.compareTo(object2.father_name);
                            }
                        });
                    }
                }
                break;
        }

        adapter.updateAdapter(mainCustomerList);
    }


    @Override
    public void OnFragmentBackPressListener() {
        if (screenFrom.equals("PlantBuyEntry")) {
            goNextFragmentReplace(mContext, new PlantBuyMilkFragment());
        } else {
            goNextFragmentReplace(mContext, new PlantMilkSaleFragment());
        }
    }

    @Override
    public void onUpdateList(int position, String from) {
        BeanVehicleDairyItem album = mainCustomerList.get(position);
        bundle = new Bundle();
        bundle.putString("CustomerId", album.id);
        bundle.putString("unic_customer", album.unic_customer);
        bundle.putString("category_chart_id", album.categorychart_id);
        bundle.putString("entry_price", album.entry_price);
        bundle.putString("entry_type", album.entry_type);
        bundle.putString("CustomerName", album.name);
        bundle.putString("CustomerFatherName", album.father_name);


        bundle.putString("FromWhere", "CustomerList");
        fragment = new PlantBuyMilkFragment();


        if (FromWhere.equals("PlantSaleEntry")) {
            fragment = new PlantMilkSaleFragment();
        }
        fragment.setArguments(bundle);
        goNextFragmentReplace(mContext, fragment);

    }
}
