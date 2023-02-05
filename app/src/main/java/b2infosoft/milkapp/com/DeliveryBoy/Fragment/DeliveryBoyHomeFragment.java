package b2infosoft.milkapp.com.DeliveryBoy.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.BuyPlan.FragmentMembershipUpgrade;
import b2infosoft.milkapp.com.BuyPlan.FragmentMessagePlan;
import b2infosoft.milkapp.com.Dairy.Bhugtan.SallerBhugtanFragment;
import b2infosoft.milkapp.com.Dairy.BulkMilkSale.BMCDashboard;
import b2infosoft.milkapp.com.Dairy.Customer.BuyerSellerCustomerListFragment;
import b2infosoft.milkapp.com.Dairy.Customer.CustomerSallerListFragment;
import b2infosoft.milkapp.com.Dairy.FatSnf.ChartTabFragment;
import b2infosoft.milkapp.com.Dairy.Invoice.InvoiceTabFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentTabFragment;
import b2infosoft.milkapp.com.Dairy.PaymentVoucher.VoucherTabFragment;
import b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Fragment.PlantCollectionAndOutLetFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment.ProductDashboard;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFullScreenFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleEntryDateTimeFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.SaleMilkEntryFullScreenFragment;
import b2infosoft.milkapp.com.Dairy.SendNotification.DairyAllCustomerlist;
import b2infosoft.milkapp.com.Dairy.Setting.SettingTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewEntryBothShiftTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewMilkEntryTabFragment;
import b2infosoft.milkapp.com.DeliveryBoy.Adapter.Deliveryboy_dashboard_Item;
import b2infosoft.milkapp.com.DeliveryBoy.Adapter.UserWithPlan_Item_Apdater;
import b2infosoft.milkapp.com.DeliveryBoy.MapMarkerActivity;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanExtraOrder;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserItem;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserPlan;
import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.ShoppingFragment;
import b2infosoft.milkapp.com.adapter.DashboardItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GPSTracker;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.FatSnf.SnfFatChartFragment.getBonusPrice;
import static b2infosoft.milkapp.com.Dairy.MainActivity.getBuyMilkRateTypeSetting;
import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.setMessageSetting;
import static b2infosoft.milkapp.com.Model.BeanVehicleDairyItem.getVehicleDairyList;
import static b2infosoft.milkapp.com.Model.CustomerListPojo.addCustomerInDbfromDeliveryboy;
import static b2infosoft.milkapp.com.Model.CustomerListPojo.addCustomerListInDatabase;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere2;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.deliverUserListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getCheckExpirePlanAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_id;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_Expire_Date;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_Start_Date;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_User_Status;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentFromDeliveryBoy;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.GET_TASK;

public class DeliveryBoyHomeFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnClickInDashboardAdapter,
        LocationListener {
    View view, viewMap;
    GPSTracker gpsTracker;

    Toolbar toolbar;
    Context mContext;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView,recyclerview_addmilk_sell;

    UserWithPlan_Item_Apdater userListApdater;
    Deliveryboy_dashboard_Item dashboard_itemAdapter;
    List<BeanUserItem> userLists;
    List<BeanExtraOrder> extraOrderDetailsList;
    List<BeanUserPlan> userPlanLists;
    SessionManager sessionManager;
    String deliverId = "", dairyId = "";
    EditText edtSearch;
    Fragment fragment;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double latitude = 26.8385702;
    double longtitude = 75.7897532;
    int listViewCount = 2;
    int userRemainingDay = 0;
    String srcAdd = "&origin=" + latitude + "," + longtitude;
    String desAdd = "&destination=" + 26.8503328 + "," + 75.7713659;
    String wayPoints = 26.8419805 + "," + 75.7965668;
    List<Dashboard_item> dashboard_items = new ArrayList<>();
    String link = "https://www.google.com/maps/dir/?api=1&travelmode=driving" + srcAdd + desAdd + wayPoints;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_delivery_boy_home, container, false);

        mContext = getActivity();
        userLists = new ArrayList<>();
        gpsTracker = new GPSTracker(mContext);

        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        viewMap = view.findViewById(R.id.viewMap);
        edtSearch = view.findViewById(R.id.edtSearch);
        swipeRefreshLayout = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerview_addmilk_sell = view.findViewById(R.id.recyclerview_addmilk_sell);
        recyclerview_addmilk_sell.setVisibility(View.GONE);
        toolbar.setTitle(R.string.Home);


        deliverId = sessionManager.getValueSesion(KEY_UserID);
        dairyId = sessionManager.getValueSesion(KEY_dairy_id);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        gpsTracker = new GPSTracker(mContext);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longtitude = gpsTracker.getLongitude();
        } else {
            gpsTracker.showSettingsAlert();
        }


        wayPoints = "&waypoints=" + wayPoints;
        link = "https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate" + srcAdd + desAdd;
        viewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("link===" + link);
               /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(link));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);*/
                Gson gson = new Gson();
                String json = gson.toJson(userLists);
                sessionManager.setValueSession("userLists", json);
                Intent i = new Intent(mContext, MapMarkerActivity.class);
                startActivity(i);
                /*fragment = new MapMarkerPalaceFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(null)
                        .commit();*/
            }
        });

        initRecycleView();
        dashboardRecyclerViewUI();
       // getBuyMilkRateTypeSetting(mContext);

      //  addCustomerInDbfromDeliveryboy(mContext,true);



        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence query, int arg1, int arg2,
                                      int arg3) {

                userListApdater.filterSearch(String.valueOf(query));
            }

        });

        getUserList();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int Refreshcounter = 1;

            @Override
            public void onRefresh() {

                getUserList();

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        buildGoogleApiClient();
        return view;
    }




    private void dashboardRecyclerViewUI() {
        dashboard_items = new ArrayList<>();

        dashboard_items.add(new Dashboard_item("buy_Milk", mContext.getResources().getString(R.string.Milk_Buy), "", R.drawable.add_entry, "#0698D4"));
        dashboard_items.add(new Dashboard_item("sale_milk", mContext.getResources().getString(R.string.MILK_Sale), "", R.drawable.sale_milk, "#F67570"));

        dashboard_itemAdapter = new Deliveryboy_dashboard_Item(mContext, dashboard_items, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, listViewCount);
        recyclerview_addmilk_sell.setLayoutManager(mLayoutManager);
        recyclerview_addmilk_sell.addItemDecoration(new GridSpacingItemDecoration(listViewCount, dpToPx(0), true));
        recyclerview_addmilk_sell.setItemAnimator(new DefaultItemAnimator());
        recyclerview_addmilk_sell.setAdapter(dashboard_itemAdapter);
    }

    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void initRecycleView() {

        viewMap.setVisibility(View.VISIBLE);

        recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(linearLayoutManager);
        userListApdater = new UserWithPlan_Item_Apdater(mContext, userLists, "m");
        recyclerView.setAdapter(userListApdater);
    }

    public void getUserList() {
        if (isNetworkAvaliable(mContext)) {

            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        userLists = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        double lat = 26.3335405;
                        double lng = 75.0832827;

                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            BaseImageUrl = jsonObject.getString("path");

                            JSONArray mainJsonArray = jsonObject.getJSONArray("data");

                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                Integer milk_weight = 0;
                                Integer milk_price = 0;
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                if (object.getString("milk_weight").equalsIgnoreCase("null")) {
                                    milk_weight = 0;
                                } else {
                                    // milk_weight = Integer.valueOf(getIntegerValue(object.getString("milk_weight")));
                                    milk_weight = object.getInt("milk_weight");
                                }

                                if (object.getString("milk_price").equalsIgnoreCase("null")) {
                                    milk_price = 0;
                                } else {
                                    milk_price = object.getInt("milk_price");
                                }
                                JSONArray orderJSonArray = object.getJSONArray("order_details");
                                extraOrderDetailsList = new ArrayList<>();
                                for (int j = 0; j < orderJSonArray.length(); j++) {
                                    JSONObject orderObject = orderJSonArray.getJSONObject(j);
                                    {
                                        extraOrderDetailsList.add(new BeanExtraOrder(
                                                nullCheckFunction(orderObject.getString("id")),
                                                nullCheckFunction(orderObject.getString("user_id")),
                                                nullCheckFunction(orderObject.getString("order_id")),
                                                nullCheckFunction(orderObject.getString("order_date")),
                                                nullCheckFunction(orderObject.getString("product_id")),
                                                nullCheckFunction(orderObject.getString("product_name")),
                                                orderObject.getInt("qty"),
                                                orderObject.getInt("price"),
                                                orderObject.getInt("total_price"),
                                                nullCheckFunction(orderObject.getString("payment_mode")),
                                                nullCheckFunction(orderObject.getString("status")),
                                                nullCheckFunction(orderObject.getString("image"))
                                        ));
                                    }
                                }
                                JSONArray plannJsonArray = object.getJSONArray("plans");
                                userPlanLists = new ArrayList<>();
                                String weight = "";
                                String strPrice = "";
                                double price = 0;


                                for (int planList = 0; planList < plannJsonArray.length(); planList++) {
                                    JSONObject planObject = plannJsonArray.getJSONObject(planList);
                                    {
                                        weight = nullCheckFunction(planObject.getString("weight"));
                                        strPrice = nullCheckFunction(planObject.getString("price"));
                                        if (strPrice.length() == 0) {
                                            price = 0;
                                        } else {
                                            price = planObject.getDouble("price");
                                        }
                                        userPlanLists.add(new BeanUserPlan(nullCheckFunction(planObject.getString("id")),
                                                nullCheckFunction(planObject.getString("plan_name")), weight, price));
                                    }
                                }
                                userLists.add(new BeanUserItem(nullCheckFunction(object.getString("id")),
                                        nullCheckFunction(object.getString("name")),
                                        nullCheckFunction(object.getString("phone_number")),
                                        nullCheckFunction(object.getString("email")),
                                        nullCheckFunction(object.getString("address")),
                                        nullCheckFunction(object.getString("user_plan_id")),
                                        nullCheckFunction(object.getString("created_by")),
                                        nullCheckFunction(object.getString("milk_plan_id")),
                                        nullCheckFunction(object.getString("milk_plan_name")),
                                        nullCheckFunction(object.getString("milk_entry_date")),
                                        nullCheckFunction(object.getString("milk_shift")),
                                        nullCheckFunction(object.getString("milk_status")),
                                        nullCheckFunction(object.getString("milk_qty")), milk_weight,
                                        milk_price, object.getDouble("lat"), object.getDouble("lng"),
                                        extraOrderDetailsList, userPlanLists
                                ));
                            }
                            lat = lat + 0.50;
                            lng = lng + 0.10;

                        }
                        initRecycleView();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("deliveryboy_id", deliverId)
                    .addEncoded("dairy_id", dairyId)
                    .build();
            caller.addRequestBody(body);
            caller.execute(deliverUserListAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();


    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            latitude = location.getLatitude();
            longtitude = location.getLongitude();
        }
    }

    public boolean checkUserIsActive() {
        boolean isActive = true;
        userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
        if (userRemainingDay <= 0) {
            isActive = false;
            fragment = new FragmentMembershipUpgrade();
            Bundle bundle = new Bundle();
            bundle.putString("from", "dashboard");
            bundle.putString("type", "membership");
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }
        return isActive;
    }

    @Override
    public void onClickEditInAdapter(int position) {
        Dashboard_item dashboard_item = dashboard_items.get(position);
        String title = dashboard_item.getId();
        Fragment fragment = null;
        Bundle bundle = null;
        String userStatus = sessionManager.getValueSesion(Key_User_Status);
        System.out.println("title===>>" + title);
        switch (title) {
            case "buy_Milk":
                if (isNetworkAvaliable(mContext)) {
                    checkUserPlanExpiryStatus(mContext);
                    getSMSBalance(mContext, GET_TASK);
                }

               // if (checkUserIsActive()) {
//                    if (sessionManager.getValueSesion(Key_BuyMilkScreen).equalsIgnoreCase(ONE)) {
//                        SelectedDate = getSimpleDate();
//                        sessionManager.db.deleteMilkEntryShiftWise("buy", SelectedDate, strSession);
//                        fragment = new PurchaseMilkEntryFullScreenFragment();
//                    } else {
//                        fragment = new PurchaseMilkDateTimeFragment();
//                    }

                    fragment = new DeliveryBoyPurchaseMilkDateTimeFragment();
                    goNextFragmentFromDeliveryBoy(mContext, fragment);
               // }
                break;
            case "sale_milk":
                if (isNetworkAvaliable(mContext)) {
                    getBonusPrice(mContext);
                    checkUserPlanExpiryStatus(mContext);
                    setMessageSetting(mContext, GET_TASK);
                    getSMSBalance(mContext, GET_TASK);
                }
             //   if (checkUserIsActive()) {
                    if (sessionManager.getValueSesion(Key_SaleMilkScreen).equals(ONE)) {
                        sessionManager.db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
                        SelectedDate = getSimpleDate();
                        fragment = new SaleMilkEntryFullScreenFragment();

                    } else {
                        fragment = new SaleEntryDateTimeFragment();
                    }

                    goNextFragmentWithBackStack(mContext, fragment);
            //    }
                break;
        }
    }
}