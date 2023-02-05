package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfDashboard_Adapter;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase.PurchaseListFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase.ReturnPurchaseListFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Sale.SaleListFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Sale.SaleReturnListFragment;
import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class ProductDashboard extends Fragment implements OnClickInDashboardAdapter {
    private static final String TAG = "Product>>>";

    Context mContext;
    Toolbar toolbar;
    ArrayList<Dashboard_item> dashboard_items;
    SnfDashboard_Adapter snfDashboard_adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    Fragment fragment = null;


    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.Product));
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        sessionManager = new SessionManager(mContext);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
            }
        });
        recyclerViewUI();
        toolbarManage();
        getUserList();
        return view;
    }

    public void toolbarManage() {
        if (getArguments() != null) {
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }

    private void recyclerViewUI() {
        dashboard_items = new ArrayList<>();
        String product = mContext.getResources().getString(R.string.Product);
        String purchase = mContext.getResources().getString(R.string.Purchase);
        String purchases = mContext.getResources().getString(R.string.product_SaleAndBuy);

        dashboard_items.add(new Dashboard_item("1", product + " " + mContext.getResources().getString(R.string.list), "",
                R.drawable.ic_list_icon, "#2CBF7B"));
        dashboard_items.add(new Dashboard_item("2", product + " " + mContext.getResources().getString(R.string.group), "",
                R.drawable.ic_category, "#FF0800"));
        dashboard_items.add(new Dashboard_item("3", product + " " + mContext.getResources().getString(R.string.Item), "",
                R.drawable.left_products, "#FF0800"));
        dashboard_items.add(new Dashboard_item("4", product + " " + mContext.getResources().getString(R.string.brand), "",
                R.drawable.products, "#1294F5"));
        dashboard_items.add(new Dashboard_item("5", product + " " + purchase, "",
                R.drawable.ic_purchasing_invoice, "#374C68"));
        dashboard_items.add(new Dashboard_item("6", purchase + " " + mContext.getResources().getString(R.string.strReturn), "",
                R.drawable.ic_purchase_return, "#374C68"));
        dashboard_items.add(new Dashboard_item("7", product + " " + mContext.getResources().getString(R.string.Sale), "",
                R.drawable.ic_product_sale, "#374C68"));
        dashboard_items.add(new Dashboard_item("8", mContext.getResources().getString(R.string.Sale) + " " + mContext.getResources().getString(R.string.strReturn), "",
                R.drawable.ic_sale_return, "#374C68"));

        snfDashboard_adapter = new SnfDashboard_Adapter(mContext, dashboard_items, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(snfDashboard_adapter);
    }


    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void getUserList() {
        if (isNetworkAvaliable(mContext)) {
            ArrayList<BeanTransactionUserItem> beanUserItems = new ArrayList<>();

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
                @Override
                public void handleResponse(String response) {
                    try {
                        String selectLabel = mContext.getString(R.string.select) + " ";
                        JSONObject jsonObject = new JSONObject(response);
                        ArrayList<String> listItem = new ArrayList<>();
                        listItem.add(selectLabel + mContext.getString(R.string.Customer));

                        beanUserItems.add(new BeanTransactionUserItem("",
                                selectLabel + mContext.getString(R.string.Customer), "", "", 0, 0, 0));
                        if (jsonObject.getString("status").equals("success")) {
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                String userType = mContext.getString(R.string.Seller);
                                if (jobj.getInt("user_group_id") == 4) {
                                    userType = mContext.getString(R.string.Buyer);
                                }

                                listItem.add(jobj.getString("unic_customer") + ". " + jobj.getString("name") + " (" + userType + ")");
                                beanUserItems.add(new BeanTransactionUserItem(jobj.getString("id"),
                                        jobj.getString("name"), nullCheckFunction(jobj.getString("unic_customer")), nullCheckFunction(jobj.getString("user_group_id")),
                                        jobj.getDouble("total_dr"), jobj.getDouble("total_cr"),
                                        jobj.getDouble("balance")));

                            }

                            Gson gson = new Gson();
                            String json = gson.toJson(beanUserItems);
                            String jsonSpin = gson.toJson(listItem);
                            sessionManager.setValueSession(SessionManager.KEY_ProdCustomerList, json);
                            sessionManager.setValueSession(SessionManager.KEY_ProCustomerSpinItem, jsonSpin);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(Constant.getUserTransactionBalanceAPI);
        } else {

        }
    }

    @Override
    public void onClickEditInAdapter(int position) {
        Bundle bundle = new Bundle();
        fragment = null;
        if (position == 0) {
            fragment = new ProductListFragment();
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 1) {
            fragment = new GroupCategoryFragment();
            bundle.putString("title", dashboard_items.get(position).getName());
            bundle.putString("type", "group");
            bundle.putString("id_key", "id");
            bundle.putString("name_key", "name");
            bundle.putString("list_url", Constant.getProductGroupListAPI);
            bundle.putString("add_url", Constant.addProductGroupAPI);
            bundle.putString("edit_url", Constant.updateProductGroupAPI);
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 2) {
            fragment = new ProdctItemGroupFragment();
            bundle.putString("title", dashboard_items.get(position).getName());
            bundle.putString("type", "item");
            bundle.putString("list_url", Constant.getProdItemGroupListAPI);
            bundle.putString("add_url", Constant.addProductItemGroupAPI);
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 3) {
            fragment = new BrandFragment();
            bundle.putString("title", dashboard_items.get(position).getName());
            bundle.putString("type", "brand");
            bundle.putString("id_key", "id");
            bundle.putString("name_key", "name");
            bundle.putString("list_url", Constant.getProductBrandListAPI);
            bundle.putString("add_url", Constant.addProductBrandAPI);
            bundle.putString("edit_url", Constant.updateProductBrandAPI);
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 4) {
            fragment = new PurchaseListFragment();
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 5) {
            fragment = new ReturnPurchaseListFragment();
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 6) {
            fragment = new SaleListFragment();
            goNextFragmentWithBackStack(mContext, fragment);
        } else if (position == 7) {
            fragment = new SaleReturnListFragment();
            goNextFragmentWithBackStack(mContext, fragment);
        }

    }
}
