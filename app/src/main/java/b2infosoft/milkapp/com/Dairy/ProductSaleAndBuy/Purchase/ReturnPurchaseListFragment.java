package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Purchase;

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

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment.InvoiceBuySaleDetailsFragment;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanPurchInvoiceProductItem;
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


public class ReturnPurchaseListFragment extends Fragment implements UpdateList {
    private static final String TAG = "Product===>>>";

    Context mContext;
    Toolbar toolbar;
    TextView tvAdd;
    ArrayList<BeanPurchaseItem> mList;
    ReturnPurchaseItemAdapter adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    Fragment fragment = null;
    String strTable = "purchase_return";
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        toolbar.setTitle(mContext.getString(R.string.Purchase) + " " + mContext.getString(R.string.strReturn));
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        sessionManager = new SessionManager(mContext);
        tvAdd.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.invoice));
        tvAdd.setVisibility(View.VISIBLE);

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInvoiceList();
                pullToRefresh.setRefreshing(false);
            }
        });
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment = new AddReturnPurchaseFragment();
                goNextFragmentWithBackStack(mContext, fragment);
            }
        });

        toolbarManage();
        getInvoiceList();
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
        adapter = new ReturnPurchaseItemAdapter(mContext, mList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void getInvoiceList() {
        mList = new ArrayList<>();
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
                .addEncoded("table", strTable)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getPurchaseInvoiceListAPI);

    }


    @Override
    public void onUpdateList(int position, String from) {
        if (from.equalsIgnoreCase("delete")) {
            mList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeRemoved(position, mList.size());
        } else if (from.equalsIgnoreCase("view")) {
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            BeanPurchaseItem album = mList.get(position);
            String json = gson.toJson(album);
            sessionManager.setValueSession("album", json);
            fragment = new InvoiceBuySaleDetailsFragment();
            bundle.putString("FromWhere", from);
            bundle.putString("type", from);
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        } else {
            Bundle bundle = new Bundle();
            Gson gson = new Gson();
            BeanPurchaseItem album = mList.get(position);
            String json = gson.toJson(album);
            sessionManager.setValueSession("album", json);
            fragment = new AddReturnPurchaseFragment();
            bundle.putString("FromWhere", from);
            bundle.putString("type", from);
            fragment.setArguments(bundle);
            goNextFragmentWithBackStack(mContext, fragment);
        }
    }
}
