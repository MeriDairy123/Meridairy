package b2infosoft.milkapp.com.Dairy.Product;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import b2infosoft.milkapp.com.Dairy.Product.Adapter.ProductDetailsAdapter;
import b2infosoft.milkapp.com.Model.ProductDetailsPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class ProductDetailsFragment extends Fragment {

    private static final String TAG = "ProductDetailsFragment==";
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    RecyclerView recycler_product_detailsList;
    SessionManager sessionManager;
    String startDate = "", endDate = "", ProductID = "";
    TextView tvTotalSellingPrice;
    Fragment fragment = null;
    Bundle bundle = null;
    ProductDetailsAdapter productDetailsAdapter;
    ArrayList<ProductDetailsPojo> mProductDetailsList;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_details, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        mProductDetailsList = new ArrayList<>();
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView(view);
        return view;
    }

    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.Products));
        tvTotalSellingPrice = view.findViewById(R.id.tvTotalSellingPrice);
        bundle = getArguments();
        if (bundle != null) {
            startDate = bundle.getString("StartDate");
            endDate = bundle.getString("EndDate");
            ProductID = bundle.getString("ProductID");
            System.out.print(TAG + "startDate: " + startDate);
            System.out.print(TAG + "endDate: " + endDate);
            System.out.print(TAG + "ProductID: " + ProductID);
        }

        if (ConnectivityReceiver.isConnected()) {
            getProductDetails();

        } else {
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.No_inetnetConnection));
        }

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    public void setDetailsList() {
        recycler_product_detailsList = view.findViewById(R.id.recycler_product_detailsList);
        tvTotalSellingPrice = view.findViewById(R.id.tvTotalSellingPrice);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_product_detailsList.setHasFixedSize(true);
        recycler_product_detailsList.setLayoutManager(mLayoutManager);
        productDetailsAdapter = new ProductDetailsAdapter(mContext, mProductDetailsList);
        recycler_product_detailsList.setAdapter(productDetailsAdapter);

        double totalPrice = 0;
        if (mProductDetailsList.size() != 0) {
            for (int i = 0; i < mProductDetailsList.size(); i++) {
                if (!mProductDetailsList.get(i).products_total_price.equals("")) {
                    totalPrice = totalPrice + Double.parseDouble(mProductDetailsList.get(i).products_total_price);
                }
            }
        }

        tvTotalSellingPrice.setText(mContext.getString(R.string.Total_Selling_Price) + "  " + mContext.getString(R.string.rsSymbol) + "  " + String.format("%.2f", totalPrice));
    }

    public void getProductDetails() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    mProductDetailsList = new ArrayList<ProductDetailsPojo>();

                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        mProductDetailsList.add(new ProductDetailsPojo(
                                object.getString("customer_id"), object.getString("transactions_id"),
                                object.getString("products_id"), object.getString("products_name"),
                                object.getString("products_qty"), object.getString("products_total_price"),
                                object.getString("unic_customer"), object.getString("customer_name"),
                                object.getString("phone_number"), object.getString("transactions_date")));
                    }

                    setDetailsList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        SessionManager sessionManager = new SessionManager(mContext);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("in_date", startDate)
                .addEncoded("out_date", endDate)
                .addEncoded("product_id", ProductID).build();
        caller.addRequestBody(body);
        caller.execute(Constant.productDetailsListAPI);

    }

}
