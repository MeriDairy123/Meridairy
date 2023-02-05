package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.List;
import java.util.Map;

import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter.Invoice_Item_adapter;
import b2infosoft.milkapp.com.customer_app.Interface.ItemPositionListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanInvoiceItem;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFragment.checkrating;
import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.appglobal.Constant.getUserInvoicAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerMainActivity.mDrawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bundleToJson;
import static b2infosoft.milkapp.com.useful.UtilityMethod.initOrderId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragment_ViewInvoice extends Fragment implements ItemPositionListner, PaytmPaymentTransactionCallback, PaytmCheckSumListener {


    public String amount = "0", order_id = "",
            checksum = "", invoiceId = "", type = "invoice";
    Context mContext;
    SessionManager sessionManager;
    View view;
    Toolbar toolbar;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    List<BeanInvoiceItem> invoiceItemList;
    Invoice_Item_adapter adapter;
    String customer_id = "", dairyId = "", STATUS = "TXN_FAILURE", data = "";
    boolean isSwitchFragment = false;
    PaytmPGService Service;
    Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        invoiceItemList = new ArrayList<>();
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_dairy_id);
        customer_id = sessionManager.getValueSesion(SessionManager.KEY_CustomerUserID);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer(GravityCompat.START);
            }
        });

        toolbar.setTitle(R.string.ViewInvoice);
        initRecyclerView();
        getUserdata();
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getUserdata();
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }
    private void getUserdata() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) throws JSONException {
                    invoiceItemList = new ArrayList<>();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jsonObj = jsonData.getJSONObject(i);

                                invoiceItemList.add(new BeanInvoiceItem(jsonObj.getString("id"),
                                        jsonObj.getString("dairy_id"), jsonObj.getString("user_id"),
                                        jsonObj.getString("month"), jsonObj.getString("month_no"), jsonObj.getString("year"),
                                        jsonObj.getString("details"), jsonObj.getString("type"),
                                        jsonObj.getString("amount"), jsonObj.getString("weight"),
                                        jsonObj.getString("status")

                                ));

                            }

                            initRecyclerView();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("user_id", customer_id)
                    .addEncoded("dairy_id", dairyId)
                    .build();
            webServiceCaller.addRequestBody(body);
            webServiceCaller.execute(getUserInvoicAPI);
        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }
    }
    private void initRecyclerView() {
        adapter = new Invoice_Item_adapter(mContext, invoiceItemList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemPosition(int position) {
        invoiceId = invoiceItemList.get(position).getId();
        amount = invoiceItemList.get(position).getAmount();
        order_id = initOrderId() + "U" + customer_id;
        checkSumGenerate();
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
        System.out.println("LOG=" + "Payment Transaction :====" + inResponse.toString());
        data = bundleToJson(inResponse).toString();
        isSwitchFragment = true;
        checkrating(getActivity());

    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        System.out.println("LOG=" + "clientAuth :====" + inErrorMessage);
        showToast(mContext, "" + inErrorMessage);
        isSwitchFragment = true;
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
        System.out.println("LOG=" + "moveNextFragment :====" + STATUS);
        Bundle bundle = new Bundle();
        bundle.putString("invoice_id", invoiceId);
        bundle.putString("order_id", order_id);
        bundle.putString("amount", amount);
        bundle.putString("STATUS", STATUS);
        bundle.putString("type", type);
        bundle.putString("data", data);
        fragment = new fragment_PaymentStatus();
        fragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, fragment).commit();


    }
}



