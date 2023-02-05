package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Adapter.ProductItemGroupAdapter;
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
 * Created by B2infosoft on 08/01/2020.
 */

public class ProdctItemGroupFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, UpdateList, View.OnKeyListener {

    Context mContext;

    Button btnSubmit;
    boolean edit = false;
    ProductItemGroupAdapter adapter;
    ArrayList<BeanBrandtItem> mList;
    ArrayList<BeanBrandtItem> categoryList;
    String categoryId = "", strListUrl = "", strAddUrl = "";
    int groupIdPos = 0;

    String strSelect = "", strTitle = "", strType = "add", strId = "", strItemCode = "", strName = "";
    Toolbar toolbar;
    TextView tvAdd;
    TextInputLayout tvItemCode;
    EditText ediItemCode, ediName;
    AppCompatSpinner spinGroup;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    View view, viewAdd, viewList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_brand, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        strSelect = mContext.getString(R.string.select) + " " + mContext.getString(R.string.group);

        Bundle bundle = getArguments();
        strTitle = bundle.getString("title");

        strListUrl = bundle.getString("list_url");
        strAddUrl = Constant.addProductItemGroupAPI;


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
        tvItemCode = view.findViewById(R.id.tvItemCode);
        ediItemCode = view.findViewById(R.id.ediItemCode);
        ediName = view.findViewById(R.id.ediName);
        spinGroup = view.findViewById(R.id.spinGroup);
        ediItemCode.setHint(mContext.getString(R.string.Item) + " " + mContext.getString(R.string.code));

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
                tvItemCode.setVisibility(View.VISIBLE);
                viewAdd.setVisibility(View.VISIBLE);
                viewList.setVisibility(View.GONE);
                tvAdd.setVisibility(View.GONE);
                spinGroup.setVisibility(View.VISIBLE);
                ediName.setText("");
                spinGroup.setSelection(0);
                strType = "add";
                toolbar.setTitle(mContext.getString(R.string.Add) + " " + strTitle);
                getCategory();

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                strName = ediName.getText().toString().trim();
                strItemCode = ediItemCode.getText().toString().trim();

                if (categoryId.length() == 0) {
                    showAlertBox(mContext, strSelect);
                } else if (strItemCode.length() == 0) {
                    showAlertBox(mContext, mContext.getString(R.string.Item) + " " + mContext.getString(R.string.code));
                } else if (strName.length() == 0) {
                    showAlertBox(mContext, mContext.getString(R.string.PleaseEnterName));
                } else {
                    addOrEditItemGroup();
                }
            }
        });

        geListData();

    }

    public void initRecyclerView() {
        adapter = new ProductItemGroupAdapter(mContext, mList, this, strAddUrl);
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
            tvItemCode.setVisibility(View.GONE);
            viewAdd.setVisibility(View.VISIBLE);
            viewList.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            strName = mList.get(position).name;
            strItemCode = mList.get(position).code;
            strId = mList.get(position).id;
            categoryId = mList.get(position).categoryId;
            strType = "edit";

            edit();
            getCategory();
        }

    }

    private void edit() {
        edit = true;
        toolbar.setTitle(mContext.getString(R.string.Edit) + " " + strTitle);
        ediItemCode.setText(strItemCode);
        ediName.setText(strName);
    }


    private void addOrEditItemGroup() {
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

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .addEncoded("item_group_id", strId)
                    .addEncoded("type", strType)
                    .addEncoded("code", strItemCode)
                    .addEncoded("item_group", strName)
                    .addEncoded("category_id", categoryId)
                    .build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(strAddUrl);

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
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            mList.add(new BeanBrandtItem(jobj.getString("id"),
                                    jobj.getString("item_group"), jobj.getString("category_id"),
                                    jobj.getString("category_name"), "code"));
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
        categoryList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<String> listItem = new ArrayList<>();
                    groupIdPos = 0;
                    categoryList.add(new BeanBrandtItem("", strSelect, ""));
                    listItem.add(strSelect);
                    if (jsonObject.getString("status").equals("success")) {

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            categoryList.add(new BeanBrandtItem(jobj.getString("id"),
                                    jobj.getString("category_name"), ""));
                            listItem.add(jobj.getString("category_name"));
                            if (categoryId.equalsIgnoreCase(jobj.getString("id"))) {
                                groupIdPos = i + 1;
                            }
                        }
                    }
                    initSpinGroup(listItem);
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

    private void initSpinGroup(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinGroup.setAdapter(spinAdapter);
        spinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    categoryId = categoryList.get(position).id;

                } else {
                    categoryId = "";
                    //   initBrandAndProduct();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinGroup.setSelection(groupIdPos);
    }

    public void toolBarManage() {
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
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
