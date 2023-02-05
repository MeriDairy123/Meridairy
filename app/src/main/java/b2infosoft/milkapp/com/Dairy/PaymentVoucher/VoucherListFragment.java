package b2infosoft.milkapp.com.Dairy.PaymentVoucher;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment.ViewProductDetails;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase.AddPurchaseInvoiceSingleProdFragment;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanVoucherItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class VoucherListFragment extends Fragment implements UpdateList {
    private static final String TAG = "Product===>>>";

    Context mContext;
    Toolbar toolbar;
    TextView tvAdd;
    EditText et_Search;
    LinearLayout lvSearch;
    ArrayList<BeanVoucherItem> mList;
    VoucherItemAdapter adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    Fragment fragment = null;
    String strTable = "purchase";
    String strTitle = "", strUrlAddEdit = "", strUrlList = "";

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        lvSearch = view.findViewById(R.id.lvSearch);
        et_Search = view.findViewById(R.id.et_Search);
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        sessionManager = new SessionManager(mContext);
     lvSearch.setVisibility(View.VISIBLE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            strTitle = bundle.getString("title");
            strUrlAddEdit = bundle.getString("url_add");
            strUrlList = bundle.getString("url_list");
        }


        tvAdd.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getVoucherList();
                pullToRefresh.setRefreshing(false);
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new AddPurchaseInvoiceSingleProdFragment();
                goNextFragmentWithBackStack(mContext, fragment);
            }
        });

        toolbarManage();
        getVoucherList();
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
        adapter = new VoucherItemAdapter(mContext, mList, strUrlAddEdit, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                adapter.filterSearch(query.toString());


            }
        });
    }

    public void getVoucherList() {
        mList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);

                            mList.add(new BeanVoucherItem(
                                    jobj.getString("id"), jobj.getString("customer_id"),
                                    nullCheckFunction(jobj.getString("receipt_no")), jobj.getString("reffrence_no"),
                                    nullCheckFunction(jobj.getString("p_type")), jobj.getString("cr_name"),
                                    nullCheckFunction(jobj.getString("bank_name")), nullCheckFunction(jobj.getString("ac_no")),
                                    nullCheckFunction(jobj.getString("cheque_no")), nullCheckFunction(jobj.getString("description")),
                                    jobj.getString("image"), jobj.getString("receipt_date"), jobj.getDouble("dr")));
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
        serviceCaller.execute(strUrlList);

    }


    @Override
    public void onUpdateList(int position, String from) {
        Bundle bundle = new Bundle();
        if (from.equalsIgnoreCase("delete")) {
            mList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, mList.size());
        } else if (from.equalsIgnoreCase("details")) {
            Gson gson = new Gson();
            BeanVoucherItem beanAddProductItem = mList.get(position);
            String json = gson.toJson(beanAddProductItem);
            sessionManager.setValueSession("album", json);
            fragment = new ViewProductDetails();
            bundle.putString("title", strTitle);
            fragment.setArguments(bundle);

            // goNextFragmentWithBackStack(mContext, fragment);
        } else {

            Gson gson = new Gson();
            BeanVoucherItem album = mList.get(position);
            String json = gson.toJson(album);
            sessionManager.setValueSession("album", json);
            fragment = new AddPaymentVoucherFragment();
            bundle.putString("title", strTitle);
            bundle.putString("type", from);
            bundle.putString("url", strUrlAddEdit);
            bundle.putString("id", "id");
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }
    }
}
