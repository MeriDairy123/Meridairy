package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfDashboard_Adapter;
import b2infosoft.milkapp.com.Dairy.Setting.ChartCategoryFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.BeanCatChartItem;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.GridSpacingItemDecoration;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.appglobal.Constant.BuyMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.getBonusAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateSnfHistoryAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_ChartId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_ChartType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FATListData;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RateChartList;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFListData;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentAddBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFloatNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class SnfFatChartFragment extends Fragment implements OnClickInDashboardAdapter {
    private static final String TAG = "SNF Chart";
    static View view;
    Spinner spinChartCat;
    Context mContext;
    Toolbar toolbar;
    ArrayList<SnfFatListPojo> snfFatListPojos = new ArrayList<>();
    ArrayList<Dashboard_item> dashboard_items;
    SnfDashboard_Adapter snfDashboard_adapter;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    Fragment fragment = null;
    ArrayList<BeanCatChartItem> beanCatChartItemList = new ArrayList<>();
    String catChartId = "", chartType = "", snfFatCategory = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fat_snf, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.SNF_FAT_Chart));
        toolbar.setVisibility(View.GONE);
        recyclerView = view.findViewById(R.id.recyclerView);
        spinChartCat = view.findViewById(R.id.spinChartCat);
        sessionManager = new SessionManager(mContext);
        Bundle bundle = getArguments();
        chartType = bundle.getString("type");

        recyclerViewUI();
        geCLRChartList();

        toolbarManage();
        return view;
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


    public int dpToPx(int dp) {
        Resources r = mContext.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void initSpinCatChart(ArrayList<String> catChartItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, catChartItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinChartCat.setAdapter(spinAdapter);
        spinChartCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    catChartId = beanCatChartItemList.get(position).id;
                    sessionManager.setValueSession(Key_ChartId, catChartId);
                } else {
                    catChartId = "";
                    sessionManager.setValueSession(Key_ChartId, catChartId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (catChartItem.size() > 1) {
            spinChartCat.setSelection(1);
        }

    }

    private void recyclerViewUI() {
        dashboard_items = new ArrayList<>();
        dashboard_items.add(new Dashboard_item("1", mContext.getResources().getString(R.string.COW_SNF), "", R.drawable.ic_cow, "#2CBF7B"));
        dashboard_items.add(new Dashboard_item("2", mContext.getResources().getString(R.string.BUFFALO_SNF), "", R.drawable.ic_buffalo, "#FF0800"));
        dashboard_items.add(new Dashboard_item("3", mContext.getString(R.string.upload) + " " + mContext.getString(R.string.chart), "", R.drawable.ic_snf_chart, "#1294F5"));
        dashboard_items.add(new Dashboard_item("4", mContext.getString(R.string.Add) + " " + mContext.getString(R.string.general), "", R.drawable.ic_add_chart_table, "#1294F5"));
        dashboard_items.add(new Dashboard_item("5", mContext.getString(R.string.Add) + " " + mContext.getString(R.string.advance) + " "
                + mContext.getString(R.string.chart), "", R.drawable.ic_advance_chart, "#1294F5"));
        dashboard_items.add(new Dashboard_item("6", mContext.getResources().getString(R.string.Add)+" "+mContext.getResources().getString(R.string.chart)+" "+mContext.getResources().getString(R.string.Category), "", R.drawable.ic_category, "#374C68"));
        dashboard_items.add(new Dashboard_item("7", mContext.getResources().getString(R.string.download_chart), "", R.drawable.ic_download, "#374C68"));

        snfDashboard_adapter = new SnfDashboard_Adapter(mContext, dashboard_items, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(0), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(snfDashboard_adapter);

    }

    @Override
    public void onClickEditInAdapter(int pos) {
        sessionManager.setValueSession(Key_ChartType, chartType);
        Constant.snfFatList = new ArrayList<>();
        sessionManager.removeValue(Key_RateChartList);
        if (pos == 0) {
            Constant.FromWhere = "Cow";
            if (catChartId.length() > 0) {
                // getCowCategory();
                snfFatCategory = "2";
                fragment = new SnfFatExcelFragmentNew();
                new PostClassGetFATSNFRateListFromDB(mContext).execute();
                getSNF_FATList();
            } else {
                showAlertWithButton(mContext, mContext.getString(R.string.selectChartCategory));
            }

        } else if (pos == 1) {
            Constant.FromWhere = "Buffalo";
            fragment = new SnfFatExcelFragmentNew();
            snfFatCategory = "1";
            new PostClassGetFATSNFRateListFromDB(mContext).execute();
            getSNF_FATList();
        } else if (pos == 2) {
            fragment = new ImportChartFragment();
            goNextFragmentAddBackStack(mContext, fragment);
        } else if (pos == 3) {
            if (catChartId.length() > 0) {
                dialogSelectAnimal("normal");
            } else {
                showAlertWithButton(mContext, mContext.getString(R.string.selectChartCategory));
            }

        } else if (pos == 4) {
            if (catChartId.length() > 0) {
                dialogSelectAnimal("advance");
            } else {
                showAlertWithButton(mContext, mContext.getString(R.string.selectChartCategory));
            }
        } else if (pos == 5) {
            if (isConnected()) {
                fragment = new ChartCategoryFragment();
                goNextFragmentAddBackStack(mContext, fragment);
            } else {
                showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));

            }
        }else if (pos == 6) {
            if (isConnected()) {
                getDairyAllSNF_FATChart(mContext, chartType, true);
            } else {
                showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));

            }
        }
    }

    public void DialogSelect() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);

        ImageView imgClosed;
        TextView tvChart, tvTable;
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvChart = dialog.findViewById(R.id.tvBuyproduct);
        tvTable = dialog.findViewById(R.id.tvViewEvent);
        tvChart.setText(mContext.getString(R.string.SNF_FAT_Chart));
        tvTable.setText(mContext.getString(R.string.SNF_FAT_Chart_Table));
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        tvChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                fragment = new SnfFatExcelFragment();
                UtilityMethod.goNextFragmentAddBackStack(mContext, fragment);

            }
        });
        tvTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                fragment = new SnfFatTableFragment();
                UtilityMethod.goNextFragmentAddBackStack(mContext, fragment);
            }
        });

        dialog.show();
    }

    public void getSNF_FATList() {
        sessionManager.removeValue(SessionManager.Key_SNFListData);
        sessionManager.removeValue(SessionManager.Key_FATListData);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating Your SNF Chart", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    ArrayList<String> snfList = new ArrayList<>();
                    ArrayList<String> fatList = new ArrayList<>();

                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        System.out.println("mainJsonArray==>>>" + mainJsonArray);
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);
                            Constant.ctegory_ID = object.getString("id");
                            snfList = new ArrayList<String>(Arrays.asList(object.getString("snf_list").split(",")));
                            fatList = new ArrayList<String>(Arrays.asList(object.getString("fat_list").split(",")));
                            System.out.println("fatListSize==>>>" + fatList.size());
                        }
                        if (!snfList.isEmpty()) {
                            sessionManager.setFATSNFListData(mContext, Key_SNFListData, snfList);
                        }
                        if (!fatList.isEmpty()) {
                            sessionManager.setFATSNFListData(mContext, Key_FATListData, fatList);
                        }
                    }
                    System.out.println("fatList==>>>" + fatList.size());
                    System.out.println("snfList=>>>" + snfList);
                    sessionManager.setValueSession(Key_ChartType, chartType);

                    if (!fatList.isEmpty() && !snfList.isEmpty() && !snfFatListPojos.isEmpty()) {
                        DialogSelect();
                    } else {
                        if (snfFatListPojos.isEmpty()) {
                            showToast(mContext, mContext.getString(R.string.UPDATE_SNF_CHART));
                        } else if (snfList.isEmpty()) {
                            showToast(mContext, mContext.getString(R.string.Please_Update_SNF_Fat));
                        } else if (fatList.isEmpty()) {
                            showToast(mContext, mContext.getString(R.string.Please_Update_SNF_Fat));
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("catChartId==>>>" + catChartId);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("type", chartType)
                .addEncoded("categorychart_id", catChartId)
                .addEncoded("snf_fat_category", snfFatCategory)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getSnfFatListNewAPI);
    }

    public void geCLRChartList() {
        beanCatChartItemList = new ArrayList<>();
        ArrayList<String> catChartItem = new ArrayList<>();
        String selectChart = mContext.getResources().getString(R.string.selectChartCategory);
        catChartItem.add(selectChart);
        catChartItem.add(mContext.getResources().getString(R.string.generalChart));
        beanCatChartItemList.add(new BeanCatChartItem("", "", "", selectChart));
        beanCatChartItemList.add(new BeanCatChartItem("0",
                mContext.getResources().getString(R.string.generalChart), "", mContext.getResources().getString(R.string.generalChart)));

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            catChartItem.add(jobj.getString("name"));
                            beanCatChartItemList.add(new BeanCatChartItem(jobj.getString("id"),
                                    jobj.getString("name"), "", ""));
                        }
                    }
                    initSpinCatChart(catChartItem);

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

        initSpinCatChart(catChartItem);
    }

    class PostClassGetFATSNFRateListFromDB extends AsyncTask<String, Void, Void> {

        private final Context context;
        ProgressDialog dialog;

        public PostClassGetFATSNFRateListFromDB(Context c) {
            this.context = c;
            snfFatListPojos = new ArrayList<>();
        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait....");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseHandler dbHandler = DatabaseHandler.getDbHelper(context);
                        snfFatListPojos = new ArrayList<>();

                        if (chartType.equalsIgnoreCase("buy")) {
                            snfFatListPojos = dbHandler.getBuyMilkFatSnfRateList(catChartId, snfFatCategory);
                        } else {
                            snfFatListPojos = dbHandler.getSaleMilkFatSnfRateList(catChartId, snfFatCategory);
                        }
                        System.out.println("chartType>>>" + chartType + "   snfFatListPojos===size>>>" + snfFatListPojos.size());

                        dialog.dismiss();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                dialog.dismiss();
                return null;
            }
            dialog.dismiss();
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    public void dialogSelectAnimal(String strFrom) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);

        ImageView imgClosed;
        TextView tvChart, tvTable;
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvChart = dialog.findViewById(R.id.tvBuyproduct);
        tvTable = dialog.findViewById(R.id.tvViewEvent);
        tvChart.setText(mContext.getString(R.string.Cow));
        tvTable.setText(mContext.getString(R.string.Buff));
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        tvChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snfFatCategory = "2";
                dialog.dismiss();
                addCustomChart(strFrom);

            }
        });
        tvTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snfFatCategory = "1";
                dialog.dismiss();

                addCustomChart(strFrom);
            }
        });

        dialog.show();
    }

    private void addCustomChart(String from) {
        Bundle bundle = new Bundle();
        bundle.putString("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
        bundle.putString("snf_fat_category", snfFatCategory);
        bundle.putString("catChartId", catChartId);
        bundle.putString("chartType", chartType);
        if (from.equals("normal")) {
            fragment = new AddCustomChartFragment();
        } else {
            fragment = new AddAllCustomChartFragment();
        }
        fragment.setArguments(bundle);
        goNextFragmentWithBackStack(mContext, fragment);

    }


    public static void getBonusPrice(Context mContext) {
        SessionManager sessionManager = new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonData = jsonObject.getJSONObject("data");
                    Constant.BuyMilkBonusPrice = nullCheckFloatNumber(jsonData.getString("bonus"));
                    Constant.SaleMilkBonusPrice = nullCheckFloatNumber(jsonData.getString("sale_milk_bonus"));

                    sessionManager.setFloatValueSession(SessionManager.Key_BuyMilkBonusRate, BuyMilkBonusPrice);
                    sessionManager.setFloatValueSession(SessionManager.Key_SaleMilkBonusRate, Constant.SaleMilkBonusPrice);
                    System.out.println(" Buy >>BuyMilkBonusPrice:>>" + BuyMilkBonusPrice);
                    System.out.println(" Sale>> BuyMilkBonusPrice:>>" + Constant.SaleMilkBonusPrice);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(getBonusAPI);
    }

    public static void getSnfStatus(Context mContext) {
        SessionManager sessionManager = new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait getting Snf status ...", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("no")) {

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
        serviceCaller.execute(updateSnfHistoryAPI);
    }

}


