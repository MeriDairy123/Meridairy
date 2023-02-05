package b2infosoft.milkapp.com.Dairy.Product;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.Bhugtan.SignatureFragment;
import b2infosoft.milkapp.com.Dairy.Product.Adapter.SaleItemAdapter;
import b2infosoft.milkapp.com.Interface.TotalShowInterface;
import b2infosoft.milkapp.com.Model.ProductListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.adapter.DialogReportAdapter;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.SellProduct;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCalanderDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;


public class SaleItemFragment extends Fragment implements View.OnClickListener, TotalShowInterface {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    TextView tvDate;
    Button btnSale;
    SessionManager sessionManager;
    String CustomerID = "", CustomerName = "", FatherName = "", unic_customer = "", type = "";
    RecyclerView recycler_user_productList;
    TextView tvListTotal, tvID, tvName;
    ArrayList<ProductListPojo> mProductList;
    ArrayList<ProductListPojo> mainProductList = new ArrayList<>();
    SaleItemAdapter saleItemAdapter;
    Dialog dialog;
    int count = 0;
    String strdate = "";
    Fragment fragment = null;
    Bundle bundle = null;
    View view;
    String dairyId = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sale_item, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        mProductList = new ArrayList<>();
        mainProductList = new ArrayList<>();
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        initView(view);
        return view;
    }

    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvListTotal = view.findViewById(R.id.tvListTotal);
        tvID = view.findViewById(R.id.tvID);
        tvName = view.findViewById(R.id.tvName);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbar_title.setText(mContext.getString(R.string.SALE_PRODUCT));
        tvDate = view.findViewById(R.id.tvDate);
        btnSale = view.findViewById(R.id.btnSale);
        if (strdate.length() == 0) {
            strdate = getSimpleDate();

        }
        tvDate.setText(strdate);
        tvDate.setOnClickListener(this);
        setProductList();
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        getProductDetail();
        btnSale.setOnClickListener(this);
        bundle = getArguments();
        CustomerID = bundle.getString("CustomerId");
        CustomerName = bundle.getString("CustomerName");
        FatherName = bundle.getString("CustomerFatherName");
        unic_customer = bundle.getString("unic_customer");
        type = bundle.getString("type");
        tvID.setText("" + unic_customer);
        tvName.setText(CustomerName + " s/o " + FatherName);

        System.out.println("CustomerID>>>" + CustomerID);
    }


    public void setProductList() {
        recycler_user_productList = view.findViewById(R.id.recycler_user_productList);
        tvListTotal = view.findViewById(R.id.tvListTotal);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_user_productList.setLayoutManager(mLayoutManager);
        saleItemAdapter = new SaleItemAdapter(view.getContext(), mProductList, this);
        recycler_user_productList.setAdapter(saleItemAdapter);
        saleItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSale:
                strdate = tvDate.getText().toString().trim();
                if (!unic_customer.equals("") && !tvName.getText().toString().equals("") && !tvListTotal.getText().toString().equals("0.0")) {
                    generateItemReport();
                } else {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_your_item));
                }
                break;
            case R.id.tvDate:
                getCalanderDate(mContext, tvDate);
                break;
        }
    }

    private void generateItemReport() {
        Button btnConfirm;
        final RecyclerView recycler_reportList;
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_item_report);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        recycler_reportList = dialog.findViewById(R.id.recycler_reportList);
        ArrayList<ProductListPojo> mList = new ArrayList<ProductListPojo>();

        for (int i = 0; i < mainProductList.size(); i++) {
            if (mainProductList.get(i).quentity != 0) {
                mList.add(new ProductListPojo(mainProductList.get(i).id,
                        mainProductList.get(i).product_name,
                        mainProductList.get(i).product_weight,
                        mainProductList.get(i).product_price,
                        mainProductList.get(i).dairy_id,
                        mainProductList.get(i).product_qty,
                        mainProductList.get(i).product_image,
                        mainProductList.get(i).quentity,
                        mainProductList.get(i).totPrice));
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        DialogReportAdapter adapter = new DialogReportAdapter(view.getContext(), mList);
        recycler_reportList.setLayoutManager(mLayoutManager);
        recycler_reportList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        btnConfirm = dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saleProduct(mList);
                System.out.print("Confirm=======>>" + "Confirm");
            }
        });

        dialog.show();

    }


    public void getProductDetail() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        mProductList = new ArrayList<>();
                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);

                            mProductList.add(new ProductListPojo(object.getString("id"),
                                    object.getString("product_name"),
                                    object.getString("weight"),
                                    object.getString("product_price"),
                                    object.getString("dairy_id"),
                                    object.getString("qty"),
                                    object.getString("image"), 0, 0));
                        }

                        setProductList();
                    } else {
                        showAlertWithButton(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyId).build();
        caller.addRequestBody(body);

        caller.execute(Constant.getProductListAPI);

    }

    private void saleProduct(ArrayList<ProductListPojo> mList) {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        saleItemAdapter.notifyDataSetChanged();
                        dialog.dismiss();

                        fragment = new SignatureFragment();
                        bundle = new Bundle();
                        bundle.putString("CustomerId", CustomerID);
                        bundle.putString("CustomerName", CustomerName);
                        bundle.putString("CustomerFatherName", FatherName);
                        bundle.putString("unic_customer", unic_customer);
                        bundle.putString("type", "debit");
                        bundle.putString("transactionID", mainObject.getString("id"));
                        bundle.putString("fromwhere", "SaleITem");
                        fragment.setArguments(bundle);
                        goNextFragmentReplace(mContext, fragment);

                    } else {
                        showAlertWithButton(mContext, mContext.getString(R.string.Product_Selling_Fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            JSONArray productArray = new JSONArray();
            JSONObject mainObject = new JSONObject();
            for (int i = 0; i < mList.size(); i++) {
                count = 0;
                count = count + mList.get(i).quentity;
                JSONObject productData = new JSONObject();
                try {
                    productData.put("products_id", "" + mList.get(i).id);
                    productData.put("qty", "" + count);
                    productData.put("total_price", "" + mList.get(i).totPrice);
                    productData.put("products_name", "" + mList.get(i).product_name);
                    productArray.put(productData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            mainObject.put("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
            mainObject.put("total_price", tvListTotal.getText().toString());
            mainObject.put("transactions_date", strdate);
            mainObject.put("total_product", "" + mList.size());
            mainObject.put("customer_id", CustomerID);
            mainObject.put("list_data", productArray);

            RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
            caller.addRequestBody(body);
            caller.execute(SellProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void showTot(ArrayList<ProductListPojo> productListPojos) {
        mainProductList = productListPojos;
        double ddd = 0;
        for (int i = 0; i < productListPojos.size(); i++) {
            ddd = ddd + productListPojos.get(i).totPrice;
        }

        tvListTotal.setText("" + ddd);
    }


}
