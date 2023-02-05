package b2infosoft.milkapp.com.Dairy.Product;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.Product.Adapter.MilkPlanListAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnClickInAdapter;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDairyMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddBuyerMilkPlanFragment extends Fragment implements OnClickInAdapter {

    Context mContext;
    String productID = "", productName = "", productRate = "", productWeight = "", userID = "";
    Toolbar toolbar;
    TextView toolbar_title, tvUploadImage;
    TextView tvAdd;
    EditText et_Search;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    RecyclerView recycler_user_productList;
    Dialog dialog;
    EditText etProductName, etRate, etProductWeight;
    ImageView imgUploadProduct;
    Button btnAddProduct;
    SwipeRefreshLayout pullToRefreshBuyer;
    MilkPlanListAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        userID = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        initView(view);
        return view;
    }


    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        toolbar_title.setText(mContext.getString(R.string.Milk) + " " + mContext.getString(R.string.Plan));
        et_Search = view.findViewById(R.id.et_Search);
        pullToRefreshBuyer = view.findViewById(R.id.pullToRefresh);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add_Milk_Plan));
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productID = "";
                productRate = "";
                productRate = "";
                dialogAddOrEditProduct();
            }
        });
        getMilkPlan();
        toolbarManage();

        pullToRefreshBuyer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getMilkPlan();
                pullToRefreshBuyer.setRefreshing(false);
            }
        });
    }

    public void setProductList(ArrayList<BeanDairyMilkPlan> dairyMilkPlans) {

        recycler_user_productList = view.findViewById(R.id.recycler_user_productList);
        et_Search = view.findViewById(R.id.et_Search);

        initRecycleView(dairyMilkPlans);
        adapter.notifyDataSetChanged();
        recycler_user_productList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        addTextListener(dairyMilkPlans);
    }

    public void addTextListener(ArrayList<BeanDairyMilkPlan> dairyMilkPlans) {

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<BeanDairyMilkPlan> filterList = new ArrayList<>();
                for (int i = 0; i < dairyMilkPlans.size(); i++) {

                    final String name = dairyMilkPlans.get(i).product_name.toLowerCase();
                    final String prodPrice = dairyMilkPlans.get(i).price.toLowerCase();


                    if (name.contains(query) || prodPrice.contains(query)) {
                        BeanDairyMilkPlan listPojo = dairyMilkPlans.get(i);
                        filterList.add(listPojo);
                    }
                }
                initRecycleView(filterList);
            }
        });
    }

    private void initRecycleView(ArrayList<BeanDairyMilkPlan> mlist) {

        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new MilkPlanListAdapter(mContext, mlist, this);
        recycler_user_productList.setLayoutManager(mLayoutManager);
        recycler_user_productList.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // data set changed
    }

    @Override
    public void onClickEditInAdapter(String ID, String itemName, String itemPrice, String Weight, String Rate, String
            Total, String customerID, String unic_customer, String snf, String milk_category, String image) {

        productID = ID;
        productName = itemName;
        productRate = itemPrice;
        productWeight = Weight;

        dialogAddOrEditProduct();
    }


    public void dialogAddOrEditProduct() {

        dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_product);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imgUploadProduct = dialog.findViewById(R.id.imgProduct);

        tvUploadImage = dialog.findViewById(R.id.tvUploadImage);
        etProductName = dialog.findViewById(R.id.etProductName);
        etProductWeight = dialog.findViewById(R.id.etProductWeight);
        etRate = dialog.findViewById(R.id.etRate);

        btnAddProduct = dialog.findViewById(R.id.btnAddProduct);
        tvUploadImage.setVisibility(View.GONE);
        imgUploadProduct.setVisibility(View.GONE);
        if (!productID.equals("")) {
            btnAddProduct.setText(mContext.getString(R.string.UPDATE));
            etProductName.setText(productName);
            etRate.setText(productRate);
            etProductWeight.setText(productWeight);
        } else {
            btnAddProduct.setText(getString(R.string.Submit));
        }
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = etProductName.getText().toString();
                productWeight = etProductWeight.getText().toString();
                productRate = etRate.getText().toString();
                if (productName.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Product_Name));
                } else if (productWeight.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Weight));

                } else if (productRate.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Milk_Rate));

                } else {
                    addOrEditProduct();
                }

            }
        });

        dialog.show();
    }


    public void getMilkPlan() {
        final ArrayList<BeanDairyMilkPlan> milkPlanList = new ArrayList<>();

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);

                            milkPlanList.add(new BeanDairyMilkPlan(object.getString("id"),
                                    object.getString("product_name"),
                                    object.getString("weight"), object.getString("price"),
                                    "", 0, false));
                        }

                        setProductList(milkPlanList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", userID)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getMilkPlan);

    }


    private void addOrEditProduct() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {

                            if (productID.equalsIgnoreCase("")) {

                                UtilityMethod.showAlertBox(mContext, getString(R.string.Product_Added_Successfully));
                                getMilkPlan();
                            } else if (!productID.equalsIgnoreCase("")) {
                                productID = "";
                                UtilityMethod.showAlertBox(mContext, getString(R.string.Product_Updated_Successfully));
                                getMilkPlan();

                            }
                            productID = "";
                            productName = "";
                            productRate = "";
                            productWeight = "";

                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        } else {
                            if (productID.equals("")) {
                                UtilityMethod.showAlertWithButton(mContext, getString(R.string.Product_Adding_Failed));
                            } else {
                                UtilityMethod.showAlertWithButton(mContext, getString(R.string.Product_Updating_failed));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };


            if (productID.equals("")) {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", userID)
                        .addEncoded("product_name", productName)
                        .addEncoded("price", productRate)
                        .addEncoded("weight", productWeight)
                        .build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.addMilkPlan);

            } else {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("id", productID)
                        .addEncoded("dairy_id", userID)
                        .addEncoded("product_name", productName)
                        .addEncoded("price", productRate)
                        .addEncoded("weight", productWeight)
                        .build();
                serviceCaller.addRequestBody(body);

                serviceCaller.execute(Constant.updateMilkPlan);
            }


        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }


}