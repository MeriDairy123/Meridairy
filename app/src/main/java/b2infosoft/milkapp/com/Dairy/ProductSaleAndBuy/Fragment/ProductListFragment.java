package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter.ProductItemAdapter;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class ProductListFragment extends Fragment implements UpdateList {
    private static final String TAG = "Product===>>>";

    Context mContext;
    Toolbar toolbar;
    TextView tvAdd;
    ArrayList<BeanAddProductItem> mList;
    ProductItemAdapter adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    Fragment fragment = null;
    Bundle bundle = null;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        toolbar.setTitle(mContext.getString(R.string.Products));
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        sessionManager = new SessionManager(mContext);
        tvAdd.setVisibility(View.VISIBLE);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getProductList();
                pullToRefresh.setRefreshing(false);
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new AddNewProduct();
                goNextFragmentWithBackStack(mContext, fragment);
            }
        });

        toolbarManage();
        getProductList();
        return view;
    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void recyclerViewUI() {
        adapter = new ProductItemAdapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void getProductList() {
        mList = new ArrayList<>();
        @SuppressLint("StaticFieldLeak")
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            mList.add(new BeanAddProductItem(jobj.getString("id"),
                                    jobj.getString("name"), nullCheckFunction(jobj.getString("item_weight")),
                                    jobj.getString("discription_product"), jobj.getString("category"),
                                    jobj.getString("category_name"), jobj.getString("item_brand"),
                                    jobj.getString("brand_name"), jobj.getString("item_unit"), jobj.getString("unit_name"),
                                    jobj.getString("item_code"), jobj.getString("images"),
                                    jobj.getString("created_at"), jobj.getInt("low_stock_alert"),
                                    jobj.getInt("initial_quantity"), jobj.getInt("weightc"),
                                    jobj.getInt("tax_check"), jobj.getDouble("opening_rate"),
                                    jobj.getDouble("opening_amt"), jobj.getDouble("price"),
                                    jobj.getDouble("sales_price"), jobj.getDouble("tax"), jobj.getString("plan_status")));

                        }
                        recyclerViewUI();
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
        serviceCaller.execute(Constant.getUserProductListAPI);

    }


    @Override
    public void onUpdateList(int position, String from) {
        if (from.equalsIgnoreCase("delete")) {
            mList.remove(position);
            recyclerViewUI();
            //recyclerView.notify();
        } else if (from.equalsIgnoreCase("details")) {
            Gson gson = new Gson();
            BeanAddProductItem beanAddProductItem = mList.get(position);
            String json = gson.toJson(beanAddProductItem);
            sessionManager.setValueSession("beanAddProductItem", json);

            fragment = new ViewProductDetails();

            goNextFragmentWithBackStack(mContext, fragment);
        } else {
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            BeanAddProductItem beanAddProductItem = mList.get(position);
            String json = gson.toJson(beanAddProductItem);
            sessionManager.setValueSession("beanAddProductItem", json);

            fragment = new AddNewProduct();
            bundle.putString("type", from);
            bundle.putString("id", "id");
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }
    }
}
