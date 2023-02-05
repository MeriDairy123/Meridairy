package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerProductSellingListAdapter;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomerSaleProductItemAdapter;
import b2infosoft.milkapp.com.customer_app.customer_pojo.CustomerProductListPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

public class ProductListActivity extends AppCompatActivity implements UpdateList {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    TextView toolbar_title, tvTotalSellingPrice;
    SessionManager sessionManager;
    ArrayList<BeanPurchaseItem> mList = new ArrayList<>();
    RecyclerView recycler_SellingProductList;
    Context mContext;
    Toolbar toolbar;
     double totalSellingPrice = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_list);
        //dairy_id=2&customer_id=161
        mContext = ProductListActivity.this;

        initView();
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.Selling_History));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tvTotalSellingPrice = findViewById(R.id.tvTotalSellingPrice);
        recycler_SellingProductList = findViewById(R.id.recycler_SellingProductList);
        sessionManager = new SessionManager(mContext);
        getProductSellingList();
        //  CustomerProductListPojo.getProductList(mContext, Constant.DairyNameID, Constant.UserID, Constant.SessionUserGroupID);
    }

    public void setProductDataList(ArrayList<CustomerProductListPojo> arrayList) {

        if (arrayList.isEmpty()) {
            recycler_SellingProductList.setVisibility(View.INVISIBLE);
            tvTotalSellingPrice.setText(getString(R.string.Total_Selling_Price) + "  " + getString(R.string.Rs) + " 0.00");
        } else {
            recycler_SellingProductList.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recycler_SellingProductList.setHasFixedSize(true);
            CustomerProductSellingListAdapter productSellingListAdapter = new CustomerProductSellingListAdapter(mContext, arrayList);
            recycler_SellingProductList.setLayoutManager(mLayoutManager);
            recycler_SellingProductList.setAdapter(productSellingListAdapter);
            productSellingListAdapter.notifyDataSetChanged();
            totalSellingPrice = 0d;
            for (int i = 0; i < arrayList.size(); i++) {
                totalSellingPrice = totalSellingPrice + Double.parseDouble(arrayList.get(i).total_price);
            }
            tvTotalSellingPrice.setText(getString(R.string.Total_Selling_Price) + getString(R.string.Rs) + " " + String.format("%.2f", totalSellingPrice));
        }
    }

    public void initRecyclerView() {

        CustomerSaleProductItemAdapter adapter = new CustomerSaleProductItemAdapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_SellingProductList.setLayoutManager(mLayoutManager);
        recycler_SellingProductList.setAdapter(adapter);
        recycler_SellingProductList.setVisibility(View.VISIBLE);
        recycler_SellingProductList.setHasFixedSize(true);
        tvTotalSellingPrice.setText(getString(R.string.Total_Selling_Price) + getString(R.string.Rs) + " " + String.format("%.2f", totalSellingPrice));

    }

    @Override
    public void onBackPressed() {
        finish();
        if (Constant.DairySize.equals("One")) {
            super.onBackPressed();
            UtilityMethod.goNextClass(mContext, CustomerDeshBoardActivity.class);
        } else if (Constant.DairySize.equals("Dairy")) {
            finish();

        } else {
            super.onBackPressed();
            UtilityMethod.goNextClass(mContext, CustomerDairyListWithBoxActivity.class);
        }
    }

    public void getProductSellingList() {
        mList = new ArrayList<>();
        totalSellingPrice = 0;
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            ArrayList<BeanPurchInvoiceProductItem> invoiceProductList = new ArrayList<>();
                            JSONArray jsonProdArray = jobj.getJSONArray("product_array");

                            for (int j = 0; j < jsonProdArray.length(); j++) {
                                JSONObject objPr = jsonProdArray.getJSONObject(j);
                                invoiceProductList.add(new BeanPurchInvoiceProductItem(
                                        objPr.getString("id"), objPr.getString("category_id"), objPr.getString("name"),
                                        nullCheckFunction(objPr.getString("group_item")), nullCheckFunction(objPr.getString("brand_id")),
                                        objPr.getInt("qty"), objPr.getInt("tax_check"), objPr.getInt("discount_check"),
                                        objPr.getDouble("weight"), objPr.getDouble("net_weight"), objPr.getDouble("price_rate"),
                                        objPr.getDouble("discount"), objPr.getDouble("discount_amt"), objPr.getDouble("tax"),
                                        objPr.getDouble("tax_amt"), objPr.getDouble("taxable_amt"),
                                        objPr.getDouble("gross")));
                            }
                            mList.add(new BeanPurchaseItem(
                                    jobj.getString("invoice_id"), jobj.getString("invoice_number"),
                                    jobj.getString("customer_id"), jobj.getString("unic_customer"), jobj.getString("user_name"),
                                    nullCheckFunction(jobj.getString("msg_on_statement")), dateDDMMYY(jobj.getString("invoice_date"))
                                    , jobj.getDouble("sgst_inv"), jobj.getDouble("cgst_inv"), jobj.getDouble("igst_inv"),
                                    jobj.getDouble("total_discount"), jobj.getDouble("cash_discount"),
                                    jobj.getDouble("other_charg"), jobj.getDouble("cash_div"), jobj.getDouble("total_taxable_value"),
                                    jobj.getDouble("subtotal"), jobj.getDouble("net_amount"), jobj.getDouble("balance_invo"), invoiceProductList));
                            totalSellingPrice = totalSellingPrice + jobj.getDouble("balance_invo");
                        }


                    }
                    initRecyclerView();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", Constant.DairyNameID)
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .addEncoded("customer_id", Constant.UserID)
                .addEncoded("table", "sale")
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getCustomerProductDetailsListAPI);

    }


    @Override
    public void onUpdateList(int position, String from) {

        Gson gson = new Gson();
        BeanPurchaseItem album = mList.get(position);
        String json = gson.toJson(album);
        sessionManager.setValueSession("album", json);
        Intent i = new Intent(mContext, BuySaleProductDetailsActivity.class);
        i.putExtra("FromWhere", "view");
        i.putExtra("type", "view");
        startActivity(i);
    }
}