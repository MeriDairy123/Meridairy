package b2infosoft.milkapp.com.Dairy.Setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import b2infosoft.milkapp.com.Dairy.Setting.Adapter.ClrCatItemAdapter;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanCatChartItem;
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
 * Created by B2infosoft on 15/10/2019.
 */

public class ChartCategoryFragment extends Fragment implements FragmentManager.OnBackStackChangedListener, UpdateList, View.OnKeyListener {

    Context mContext;

    Button btnSubmit;
    boolean edit = false;
    ClrCatItemAdapter adapter;
    ArrayList<BeanCatChartItem> mList;

    String catChartLabel = "", strChartId = "", chartName = "", categoryId = "";
    Toolbar toolbar;
    TextView tvAdd;

    EditText ediName;
    AppCompatSpinner spinCat;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    View view, viewAddChart, viewChartList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crl_chart_category, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        return view;
    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        catChartLabel = mContext.getString(R.string.chart) + " " + mContext.getString(R.string.Category);
        toolbar.setTitle(catChartLabel);
        tvAdd.setVisibility(View.VISIBLE);
        toolBarManage();
        viewAddChart = view.findViewById(R.id.viewAdd);
        viewChartList = view.findViewById(R.id.viewList);
        ediName = view.findViewById(R.id.ediName);
        spinCat = view.findViewById(R.id.spinCategory);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        viewChartList.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.chart));
        viewAddChart.setVisibility(View.GONE);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit = false;
                categoryId = "";
                chartName = "";
                strChartId = "";
                viewAddChart.setVisibility(View.VISIBLE);
                viewChartList.setVisibility(View.GONE);
                tvAdd.setVisibility(View.GONE);
                ediName.setText("");

                addChart();
                spinCat.setSelection(0);
                toolbar.setTitle(mContext.getString(R.string.Add) + " " + catChartLabel);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chartName = ediName.getText().toString().trim();

                if (chartName.length() != 0) {
                    addOrEditClrSetting();
                } else {
                    showAlertBox(mContext, mContext.getString(R.string.PleaseEnterName));
                }

            }
        });

        geCLRChartList();

    }

    private void addChart() {

        spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    categoryId = String.valueOf(position);
                } else {
                    categoryId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initRecyclerView() {
        adapter = new ClrCatItemAdapter(mContext, mList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                geCLRChartList();
                pullToRefresh.setRefreshing(false);
            }
        });
        toolbar.setTitle(catChartLabel);
        tvAdd.setVisibility(View.VISIBLE);


    }

    @Override
    public void onUpdateList(int position, String from) {
        if (from.equals("edit") && !mList.isEmpty()) {
            viewAddChart.setVisibility(View.VISIBLE);
            viewChartList.setVisibility(View.GONE);
            tvAdd.setVisibility(View.GONE);
            chartName = mList.get(position).name;
            strChartId = mList.get(position).id;

            editChart();


        }

    }

    private void editChart() {
        edit = true;

        //addChart();
        toolbar.setTitle(mContext.getString(R.string.Edit) + " " + catChartLabel);
        ediName.setText(chartName);
        // int catId = Integer.parseInt(categoryId);
        //  spinCat.setSelection(catId);

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

                            viewChartList.setVisibility(View.VISIBLE);
                            viewAddChart.setVisibility(View.GONE);
                            geCLRChartList();

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
                        .addEncoded("name", chartName)
                        .addEncoded("categorychart_id", strChartId)
                        .build();
                serviceCaller.addRequestBody(body);
                //.addEncoded("category_id", categoryId)
                serviceCaller.execute(Constant.updateCategoryChartAPI);
            } else {
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                        .addEncoded("name", chartName)

                        .build();
                // .addEncoded("category_id", categoryId)
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.addCategoryChartAPI);
            }
        } else {
            showToast(getActivity(), mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public void geCLRChartList() {
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
                            mList.add(new BeanCatChartItem(jobj.getString("id"),
                                    jobj.getString("name"), "", ""));
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
        serviceCaller.execute(Constant.getCategoryChartAPI);


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
        if (viewAddChart.getVisibility() == View.VISIBLE) {
            viewAddChart.setVisibility(View.GONE);
            viewChartList.setVisibility(View.VISIBLE);
            tvAdd.setVisibility(View.VISIBLE);
            toolbar.setTitle(mContext.getString(R.string.clrChart));

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
