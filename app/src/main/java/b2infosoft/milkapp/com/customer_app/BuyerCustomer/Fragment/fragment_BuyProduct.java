package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.BuyerProduct_Item_adapter;
import b2infosoft.milkapp.com.customer_app.Interface.BuyerProductOnClickListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanBuyerCartItem;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanCustomerProductItem;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.appglobal.Constant.getDairyProductAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_ProductAddDate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_id;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bundleToJson;
import static b2infosoft.milkapp.com.useful.UtilityMethod.initOrderId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_BuyProduct extends Fragment implements BuyerProductOnClickListner
        , PaytmPaymentTransactionCallback, PaytmCheckSumListener {

    public String amount = "", order_id = "", customer_id = "",
            checksum = "", type = "product";
    Context mContext;
    Toolbar toolbar;
    TextView btnTotalPrice, btnpayNow;
    View view, layoutCart;
    BuyerProduct_Item_adapter adapter;
    ArrayList<BeanCustomerProductItem> mList;
    ArrayList<BeanBuyerCartItem> mCartList;
    RecyclerView recyclerView;
    String labelinCart = " | item | \u20B9 ";
    String dairyId = "";
    DatabaseHandler databaseHandler;
    JSONObject jsonObject;
    JSONArray jsonArrayOrder;
    String STATUS = "TXN_FAILURE";
    String data = "";
    SessionManager sessionManager;
    Fragment fragment;
    PaytmPGService Service;
    boolean isSwitchFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_customer_product_list, container, false);
        mContext = getActivity();
        isSwitchFragment = false;
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyId = nullCheckFunction(sessionManager.getValueSesion(KEY_dairy_id));
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutCart = view.findViewById(R.id.layoutCart);
        btnTotalPrice = view.findViewById(R.id.btnTotalPrice);
        btnpayNow = view.findViewById(R.id.btnpayNow);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setTitle(R.string.BuyMilkProduct);

        mList = new ArrayList<>();

        customer_id = sessionManager.getValueSesion(KEY_UserID);
        order_id = initOrderId() + "U" + customer_id;
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnpayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkSumGenerate();
            }
        });
        onCartUpdate(0);
        getProductList();
        return view;
    }


    private void initRecyclerView() {
        adapter = new BuyerProduct_Item_adapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void getProductList() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Fetching...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        mList = new ArrayList<>();
                        if (status.equalsIgnoreCase("success")) {
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            BaseImageUrl = jsonObject.getString("path");
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonObj = jsonData.getJSONObject(i);
                                mList.add(new BeanCustomerProductItem(jsonObj.getString("id"),
                                        jsonObj.getString("product_name"),
                                        jsonObj.getString("weight"),
                                        jsonObj.getString("image"),
                                        jsonObj.getDouble("product_price"),
                                        jsonObj.getInt("qty")));

                            }
                        }
                        initRecyclerView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .build();
            caller.addRequestBody(body);
            caller.execute(getDairyProductAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }


    @Override
    public void onCartUpdate(int position) {
        try {
            mCartList = new ArrayList<>();
            mCartList = databaseHandler.getBuyerCartList();
            jsonObject = new JSONObject();
            jsonArrayOrder = new JSONArray();
            String productId = "";
            if (!mCartList.isEmpty()) {
                layoutCart.setVisibility(View.VISIBLE);

                int item = mCartList.size();
                System.out.println("===mCartList.size()==" + item);

                double totalAmount = 0;
                String strOrderdate = sessionManager.getValueSesion(KEY_ProductAddDate);
                for (int i = 0; i < mCartList.size(); i++) {
                    JSONObject jsonObjectOrder = new JSONObject();

                    totalAmount = totalAmount + mCartList.get(i).totalPrice;
                    productId = mCartList.get(i).getId();


                    jsonObjectOrder = new JSONObject();
                    jsonObjectOrder.put("order_id", order_id);
                    jsonObjectOrder.put("order_date", strOrderdate);
                    jsonObjectOrder.put("user_id", customer_id);
                    jsonObjectOrder.put("product_id", productId);
                    jsonObjectOrder.put("product_name", mCartList.get(i).getProduct_name());
                    jsonObjectOrder.put("description", mCartList.get(i).getProduct_name());
                    jsonObjectOrder.put("category", type);
                    jsonObjectOrder.put("qty", mCartList.get(i).getItemQnt());
                    jsonObjectOrder.put("price", mCartList.get(i).getPrice());
                    jsonObjectOrder.put("total_price", mCartList.get(i).totalPrice);
                    jsonObjectOrder.put("payment_mode", "paytm");
                    jsonObjectOrder.put("status", "complete");
                    jsonObjectOrder.put("thumb", mCartList.get(i).getImage());
                    jsonObjectOrder.put("image", mCartList.get(i).getImage());
                    jsonArrayOrder.put(jsonObjectOrder);
                }


                btnTotalPrice.setText(item + labelinCart + String.format("%.2f", totalAmount));
                jsonObject.put("make_array", jsonArrayOrder);

                amount = String.valueOf(totalAmount);
            } else {

                layoutCart.setVisibility(View.GONE);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void checkSumGenerate() {
        Service = PaytmPGService.getProductionService();
        order_id = initOrderId() + "U" + customer_id;
        Service = PaytmPGService.getProductionService();
        generateCheckSum(mContext,order_id,customer_id,amount,this);
    }

    @Override
    public void checkSumResult(String checkSum, JSONObject jsonObject) {
        this.checksum=checkSum;
        onStartTransaction();
    }


    public void onStartTransaction() {
        //   PaytmPGService Service = PaytmPGService.getStagingService();


        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", MID);
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("CUST_ID", customer_id);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", CALLBACK_URL + order_id);
        paramMap.put("CHECKSUMHASH", checksum);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);

        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);

        Service.initialize(Order, null);
        Service.startPaymentTransaction(mContext, true,
                true, this);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        data = bundleToJson(inResponse).toString();
        isSwitchFragment = true;


    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        System.out.println("LOG=" + "clientAuth :====" + inErrorMessage);
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        System.out.println("LOG=" + "someUIError :====" + inErrorMessage);
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        System.out.println("LOG==== " + " onError Loading WebPage ==== " + inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        System.out.println("onBackPressedCancelTransaction===" + " cancel call back respon ==== ");
        STATUS = "TXN_FAILURE";
        isSwitchFragment = true;

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        System.out.println("LOG=" + "Transaction Cancel :====" + inResponse);
        data = bundleToJson(inResponse).toString();
        STATUS = "TXN_FAILURE";
        isSwitchFragment = true;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onResume() {
        super.onResume();


        if (isSwitchFragment) {
            moveNextFragment();
        }
    }

    public void moveNextFragment() {
        try {

            if (data.length() > 0) {
                JSONObject object = new JSONObject(data);
                STATUS = object.getString("STATUS");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Bundle bundle = new Bundle();
        bundle.putString("order_id", order_id);
        bundle.putString("amount", amount);
        bundle.putString("STATUS", STATUS);
        bundle.putString("type", type);
        bundle.putString("data", data);
        bundle.putString("jsonObject", jsonObject.toString());
        fragment = new fragment_PaymentStatus();
        fragment.setArguments(bundle);
        if (STATUS.equalsIgnoreCase("TXN_FAILURE")) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();
        } else {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).commit();
        }

    }
}
