package b2infosoft.milkapp.com.Shopping;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.CartOnClickListner;
import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.Payment_Status_activity;
import b2infosoft.milkapp.com.adapter.CartList_ItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bundleToJson;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.initOrderId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class CartFragment extends Fragment implements CartOnClickListner, PaytmPaymentTransactionCallback, PaytmCheckSumListener {


    public ArrayList<BeanCartItem> mCartList;
    public String amount = "", order_id = "", customer_id = "", checksum = "";
    Context mContext;
    Toolbar toolbar;
    CartList_ItemAdapter adapter;
    RecyclerView recyclerView;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    View layoutProductAmt, tvEmptyMessage;
    TextView tvBasicPrice, tvGstAmount, tvGrossTotalAmount;
    Button btnPayNow;
    double vat = 18, totlePrice = 0, price = 0, gstAmt = 0, grossAmt = 0;
    String rupeeSymbl = "", STATUS = "TXN_FAILURE", data = "", type = "product", validity = "Unlimited";

    JSONObject jsonObject;
    JSONArray jsonArrayOrder;

    PaytmPGService Service;


    boolean isSwitchFragment = false;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_cart, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        mCartList = new ArrayList<>();

        customer_id = sessionManager.getValueSesion(KEY_UserID);
        initView();
        return view;

    }


    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage);
        layoutProductAmt = view.findViewById(R.id.layoutProductAmt);
        tvBasicPrice = view.findViewById(R.id.tvBasicPrice);
        tvGstAmount = view.findViewById(R.id.tvGstAmount);
        tvGrossTotalAmount = view.findViewById(R.id.tvGrossTotalAmount);
        btnPayNow = view.findViewById(R.id.btnPayNow);
        rupeeSymbl = mContext.getResources().getString(R.string.Rupee_symbol);
        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSumGenerate();
            }
        });
        jsonObject = new JSONObject();
        jsonArrayOrder = new JSONArray();

        toolbar.setTitle(mContext.getString(R.string.MyCart));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        onCartUpdate(0);

        initRecyclerView();
    }


    private void initRecyclerView() {
        adapter = new CartList_ItemAdapter(mContext, mCartList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCartUpdate(int position) {
        mCartList = new ArrayList<>();

        mCartList = databaseHandler.getCartList();
        price = 0;
        int productId = 0;
        totlePrice = 0;
        gstAmt = 0;
        grossAmt = 0;
        jsonObject = new JSONObject();
        jsonArrayOrder = new JSONArray();

        try {

            if (!mCartList.isEmpty()) {
                order_id = initOrderId() + "U" + customer_id;
                JSONObject jsonObjectOrder = new JSONObject();
                tvEmptyMessage.setVisibility(View.GONE);
                btnPayNow.setVisibility(View.VISIBLE);
                layoutProductAmt.setVisibility(View.VISIBLE);
                for (int i = 0; i < mCartList.size(); i++) {
                    price = mCartList.get(i).totalPrice;
                    vat = mCartList.get(i).gst;
                    //gstAmt=(price*vat)/100;
                    price = price / (100 + vat) * 100;
                    double prodtGst = price * vat / 100;
                    double productTotalAmt = price + prodtGst;
                    gstAmt = gstAmt + (price * vat) / 100;
                    totlePrice = totlePrice + price;
                    productId = mCartList.get(i).id;

                    jsonObjectOrder.put("order_id", order_id);
                    jsonObjectOrder.put("user_id", customer_id);
                    jsonObjectOrder.put("product_id", productId);
                    jsonObjectOrder.put("product_name", mCartList.get(i).title);
                    jsonObjectOrder.put("qty", mCartList.get(i).itemQnt);
                    jsonObjectOrder.put("price", mCartList.get(i).price);
                    jsonObjectOrder.put("total_price", productTotalAmt);
                    jsonObjectOrder.put("tax", prodtGst);
                    jsonObjectOrder.put("payment_mode", "paytm");
                    jsonObjectOrder.put("status", "complete");
                    jsonObjectOrder.put("description", mCartList.get(i).description);
                    jsonObjectOrder.put("image", mCartList.get(i).image);
                    jsonObjectOrder.put("thumb", mCartList.get(i).thumb);
                    jsonArrayOrder.put(jsonObjectOrder);
                }

                grossAmt = totlePrice + gstAmt;
                totlePrice = Double.parseDouble(String.format("%.2f", totlePrice));
                gstAmt = Double.parseDouble(String.format("%.2f", gstAmt));
                grossAmt = Double.parseDouble(String.format("%.2f", grossAmt));

                tvBasicPrice.setText(rupeeSymbl + " " + totlePrice);
                tvGstAmount.setText(rupeeSymbl + " " + gstAmt);
                tvGrossTotalAmount.setText(Double.toString(grossAmt));
                amount = String.valueOf(grossAmt);
                jsonObject.put("make_array", jsonArrayOrder);
            } else {
                tvEmptyMessage.setVisibility(View.VISIBLE);
                btnPayNow.setVisibility(View.GONE);
                layoutProductAmt.setVisibility(View.GONE);
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
        Map<String, String> paramMap = new HashMap<String, String>();
        //   paramMap.put("type", type);
        paramMap.put("MID", MID);
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("CUST_ID", customer_id);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", amount);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", CALLBACK_URL + order_id);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("CHECKSUMHASH", checksum);

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
        System.out.println("LOG>>>>>" + inErrorMessage);
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        System.out.println("LOG==someUIError >>>>" + inErrorMessage);
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        System.out.println("checksum " + " cancel call back respon>>>>" + inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {

        STATUS = "TXN_FAILURE";
        isSwitchFragment = true;
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        System.out.println("Transaction Cancel :>>>>" + inResponse);
        data = bundleToJson(inResponse).toString();
        STATUS = "TXN_FAILURE";
        isSwitchFragment = true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        System.out.println("isSwitchFragment===" + isSwitchFragment);
        if (isSwitchFragment) {
            moveNextActivity();
        }
    }

    public void moveNextActivity() {

        if (data.length() > 0) {
            try {
                JSONObject object = new JSONObject(data);
                STATUS = object.getString("STATUS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(mContext, Payment_Status_activity.class);
        i.putExtra("order_id", order_id);
        i.putExtra("amount", amount);
        i.putExtra("STATUS", STATUS);
        i.putExtra("type", type);
        i.putExtra("data", data);
        i.putExtra("validity", validity);
        i.putExtra("jsonObject", jsonObject.toString());
        startActivity(i);
    }


}
