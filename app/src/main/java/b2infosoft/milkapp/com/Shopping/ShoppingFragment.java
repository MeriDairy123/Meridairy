package b2infosoft.milkapp.com.Shopping;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.ProductOnClickListner;
import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.Model.BeanProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.Adapter.ProductList_ItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getAllProductAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class ShoppingFragment extends Fragment implements View.OnClickListener, ProductOnClickListner {


    public ArrayList<BeanCartItem> mCartList;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    ProductList_ItemAdapter productListItemAdapter;
    ArrayList<BeanProductItem> mProductList;
    RecyclerView recyclerView;
    SwipeRefreshLayout pullToRefresh;
    TextView tvCart, tvCartItem, tvViewCart;
    View layoutHeaderCart, layoutCart;
    String labelinCart = " | item | Rs ";
    Fragment fragment = null;
    Bundle bundle = null;


    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_shop_product, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        mCartList = new ArrayList<>();


        initView();
        return view;

    }


    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbar_title.setText(getString(R.string.Shopping));
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        layoutHeaderCart = view.findViewById(R.id.layoutHeaderCart);
        tvCart = view.findViewById(R.id.tvCart);
        layoutCart = view.findViewById(R.id.layoutCart);
        tvCartItem = view.findViewById(R.id.tvCartItem);
        tvViewCart = view.findViewById(R.id.tvViewCart);

        layoutHeaderCart.setOnClickListener(this);
        tvViewCart.setOnClickListener(this);
        mProductList = new ArrayList<>();

        initRecyclerView();
        getProductList(mContext);
        onCartUpdate(0);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getProductList(mContext);
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void initRecyclerView() {

        productListItemAdapter = new ProductList_ItemAdapter(mContext, mProductList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(productListItemAdapter);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tvViewCart:
                viewCart();
                break;
            case R.id.layoutHeaderCart:
                viewCart();
                break;
        }
    }


    @Override
    public void onProductAdapterClick(BeanProductItem beanProductItem) {
        fragment = new ProductDescriptionFagment();
        bundle = new Bundle();
        bundle.putString("product_id", String.valueOf(beanProductItem.id));
        fragment.setArguments(bundle);
        goNextFragmentWithBackStack(mContext, fragment);
/*
     Intent intent = new Intent(mContext, ProductDescriptionFagment.class);
     intent.putExtra("product_id", String.valueOf(beanProductItem.id));
     mContext.startActivity(intent);*/

    }

    @Override
    public void onCartUpdate(int position) {
        mCartList = new ArrayList<>();
        mCartList = databaseHandler.getCartList();

        if (!mCartList.isEmpty()) {
            layoutCart.setVisibility(View.VISIBLE);
            layoutHeaderCart.setVisibility(View.VISIBLE);

            int item = mCartList.size();
            double totalAmount = 0;

            for (int i = 0; i < mCartList.size(); i++) {

                totalAmount = totalAmount + mCartList.get(i).totalPrice;

            }
            System.out.println("totalAmount=dashboard====" + totalAmount);


            tvCart.setText(Integer.toString(item));


            tvCartItem.setText(item + labelinCart + String.format("%.2f", totalAmount));


        } else {
            layoutCart.setVisibility(View.GONE);
            layoutHeaderCart.setVisibility(View.GONE);

        }
    }

    private void viewCart() {
        goNextFragmentWithBackStack(mContext, new CartFragment());

    }

    public void getProductList(final Context mContext) {

        if (isNetworkAvaliable(mContext)) {

            final ArrayList<BeanProductItem> beanProductItems = new ArrayList<BeanProductItem>();

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                            BaseImageUrl = jsonObject.getString("path");
                            for (int i = 0; i < dataJsonArray.length(); i++) {
                                JSONObject object = dataJsonArray.getJSONObject(i);

                                beanProductItems.add(new BeanProductItem(object.getInt("id"),
                                        object.getInt("min_qty"), object.getInt("qty"),
                                        object.getDouble("price"), object.getDouble("mrp"),
                                        object.getDouble("gst"),
                                        object.getString("title"), object.getString("type"),
                                        object.getString("image"), object.getString("thumb"),
                                        object.getString("status"), object.getString("description")));
                            }

                            if (!beanProductItems.isEmpty()) {
                                mProductList = new ArrayList<>();
                                System.out.println("=product==size===" + beanProductItems.size());
                                mProductList.addAll(beanProductItems);
                                initRecyclerView();

                            }
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            serviceCaller.execute(getAllProductAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }


}
