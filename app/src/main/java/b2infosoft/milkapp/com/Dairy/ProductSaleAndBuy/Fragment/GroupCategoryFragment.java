package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter.ProductGroupAdapter;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanBrandtItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 11/12/2019.
 */

public class GroupCategoryFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, UpdateList, View.OnKeyListener {

    Context mContext;

    Button btnSubmit;
    boolean edit = false;
    ProductGroupAdapter adapter;
    ArrayList<BeanBrandtItem> mList;
    String strListUrl = "", strAddUrl = "", strUpdateUrl = "";

    String strTitle = "", strType = "", strId = "", strName = "", categoryId = "";
    Toolbar toolbar;
    TextView tvAdd;

    EditText ediName;
    AppCompatSpinner spinCat;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    View view, viewAdd, viewList;
    String itemIdKEY = "id", itemNameKEY = "name";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crl_chart_category, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        Bundle bundle = getArguments();
        strTitle = bundle.getString("title");
        strType = bundle.getString("type");
        itemIdKEY = bundle.getString("id_key");
        itemNameKEY = bundle.getString("name_key");
        strListUrl = bundle.getString("list_url");
        strAddUrl = bundle.getString("add_url");
        strUpdateUrl = bundle.getString("edit_url");

        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        return view;
    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        toolbar.setTitle(strTitle);
        tvAdd.setVisibility(View.VISIBLE);
        toolBarManage();
        viewAdd = view.findViewById(R.id.viewAdd);
        viewList = view.findViewById(R.id.viewList);
        ediName = view.findViewById(R.id.ediName);
        spinCat = view.findViewById(R.id.spinCategory);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        viewList.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add));
        viewAdd.setVisibility(View.GONE);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = false;
                categoryId = "";
                strName = "";
                strId = "";
                viewAdd.setVisibility(View.VISIBLE);
                viewList.setVisibility(View.GONE);
                tvAdd.setVisibility(View.GONE);
                ediName.setText("");
                spinCat.setSelection(0);
                toolbar.setTitle(mContext.getString(R.string.Add) + " " + strTitle);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strName = ediName.getText().toString().trim();

                if (strName.length() != 0) {
                    addOrEditClrSetting();
                } else {
                    showAlertBox(mContext, mContext.getString(R.string.PleaseEnterName));
                }
            }
        });

        geListData();

    }

    public void initRecyclerView() {
        adapter = new ProductGroupAdapter(mContext, mList, this, strUpdateUrl);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                geListData();
                pullToRefresh.setRefreshing(false);
            }
        });
        toolbar.setTitle(strTitle);
        tvAdd.setVisibility(View.VISIBLE);


    }

    @Override
    public void onUpdateList(int position, String from) {
        if (from.equals("edit") && !mList.isEmpty()) {
            viewAdd.setVisibility(View.VISIBLE);
            viewList.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            strName = mList.get(position).name;
            strId = mList.get(position).id;
            edit();
        }

    }

    private void edit() {
        edit = true;
        toolbar.setTitle(mContext.getString(R.string.Edit) + " " + strTitle);
        ediName.setText(strName);
    }


    private void addOrEditClrSetting() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            showToast(getActivity(), jsonObject.getString("user_status_message"));
                            viewList.setVisibility(View.VISIBLE);
                            viewAdd.setVisibility(View.GONE);
                            geListData();
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            if (edit) {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                        .addEncoded(itemNameKEY, strName)
                        .addEncoded(itemIdKEY, strId)
                        .build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(strUpdateUrl);
            } else {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                        .addEncoded(itemNameKEY, strName)
                        .addEncoded(itemNameKEY, strName)
                        .build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(strAddUrl);
            }
        } else {
            showToast(getActivity(), mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public void geListData() {
        mList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        String nameKey = "name";
                        if (strType.equalsIgnoreCase("group")) {
                            nameKey = "category_name";
                        }

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            mList.add(new BeanBrandtItem(jobj.getString("id"),
                                    jobj.getString(nameKey), ""));
                        }
                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(strListUrl);


    }

    public void getCategory() {
        mList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        String nameKey = "name";
                        if (strType.equalsIgnoreCase("group")) {
                            nameKey = "category_name";
                        }
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            mList.add(new BeanBrandtItem(jobj.getString("id"),
                                    jobj.getString(nameKey), ""));
                        }
                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getProductGroupListAPI);


    }


    public void toolBarManage() {
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackStackChanged();

            }
        });

    }


    @Override
    public void onBackStackChanged() {
        if (viewAdd.getVisibility() == View.VISIBLE) {
            viewAdd.setVisibility(View.GONE);
            viewList.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.VISIBLE);
            toolbar.setTitle(strTitle);

        } else {
            getActivity().onBackPressed();
        }

    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackStackChanged();
                return true;
            }
        }
        return false;
    }
}
