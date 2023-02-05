package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Interface.OnClickCategoryListener;
import b2infosoft.milkapp.com.Model.AnimalCategoriesData;
import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.AnimalCategoriesAdapter;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.AnimalDashboard_ItemAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mListAnimalData;
import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mainCategoryList;
import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.statePojos;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getAnimalsAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalMainCategoryId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalStateId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalStateName;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragment_AnimalDashboard extends Fragment implements View.OnClickListener,
        OnClickCategoryListener {
    public AlertDialog.Builder dialogBuilder;
    public AlertDialog builder;
    View view;
    Toolbar toolbar;
    TextView toolbarTitle;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerviewCategory, recyclerView;
    FragmentActivity mContext;
    List<AnimalCategoriesData> mCategoryList;
    List<BeanAnimalDashboard> mList;
    AnimalCategoriesAdapter animalCategoriesAdapter;
    AnimalDashboard_ItemAdapter dashboard_itemAdapter;
    SessionManager sessionManager;
    TextView tvAnimalList;
    TextView tvAddAnimal;
    String animalCategory = "";
    String stateId = "", stateName = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_animal_dashboard, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        mCategoryList = new ArrayList<>();
        mList = new ArrayList<>();
        mList.addAll(mListAnimalData);
        if (sessionManager.getValueSesion(KEY_AnimalStateId) != null) {
            stateId = sessionManager.getValueSesion(KEY_AnimalStateId);
            stateName = sessionManager.getValueSesion(KEY_AnimalStateName);
        }

        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbarTitle);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(mContext.getString(R.string.Meri_Dairy));

        toolbarTitle.setText(stateName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        toolbarTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectStateDialog();
            }
        });
        tvAnimalList = view.findViewById(R.id.tvAnimalList);
        tvAnimalList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Fragment_My_Upload_Animal_Listing();
                FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_share_ads, fragment)
                        .addToBackStack(null).commit();
            }
        });

        initView();
        initRecycleViewCategory();
        //getAnimalCategory();

        //getAnimalData();
        initRecycleViewAnimalData();
        return view;
    }

    private void initView() {
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        recyclerviewCategory = view.findViewById(R.id.recyclerviewCategory);
        tvAddAnimal = view.findViewById(R.id.tvAddAnimal);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvAddAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentDetails();
            }
        });
        //setting an setOnRefreshListener on the SwipeDownLayout
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            int Refreshcounter = 1; //Counting how many times user have refreshed the layout

            @Override
            public void onRefresh() {
                //Here you can update your data from internet or from local SQLite data
                getAnimalData("");
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    private void fragmentDetails() {
        Fragment fragment = new Fragment_Animal_Categories();
        FragmentTransaction fragmentTransaction = mContext.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_share_ads, fragment)
                .addToBackStack(null).commit();
    }


    @Override
    public void onClick(View v) {
        fragmentDetails();
    }

    public void initRecycleViewCategory() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        recyclerviewCategory.setLayoutManager(mLayoutManager);
        animalCategoriesAdapter = new AnimalCategoriesAdapter(mContext, mainCategoryList, this);
        recyclerviewCategory.setAdapter(animalCategoriesAdapter);
    }

    public void initRecycleViewAnimalData() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 2);
        recyclerView.setLayoutManager(linearLayoutManager);
        dashboard_itemAdapter = new AnimalDashboard_ItemAdapter(mContext, mList);
        recyclerView.setAdapter(dashboard_itemAdapter);
        animalCategory = nullCheckFunction(sessionManager.getValueSesion(KEY_AnimalMainCategoryId));
        dashboard_itemAdapter.filterSearch(animalCategory);
    }


    private void getAnimalData(String stateId) {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        mListAnimalData = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject dataObj = jsonArray.getJSONObject(i);
                            mListAnimalData.add(new BeanAnimalDashboard(dataObj.getString("id"),
                                    dataObj.getString("user_id"), dataObj.getString("main_cat"),
                                    dataObj.getString("sub_cat"), dataObj.getString("tag_no"),
                                    dataObj.getString("nick_name"), dataObj.getString("year"),
                                    dataObj.getString("month"), dataObj.getString("gender"),
                                    dataObj.getString("castration"), dataObj.getString("lactation_no"),
                                    dataObj.getString("last_calving_occured"), dataObj.getString("milk_status"),
                                    dataObj.getString("peak_milk_yeild"), dataObj.getString("sex_of_calf"),
                                    dataObj.getString("calf_status"), dataObj.getString("inter_calving_period"),
                                    dataObj.getString("is_pragnant"), dataObj.getString("pragnant_month"),
                                    dataObj.getString("pragnant_day"), dataObj.getString("method_conception"),
                                    dataObj.getString("fmd_vaccination"), dataObj.getString("fmd_vaccination_period"),
                                    dataObj.getString("hs_vaccination"), dataObj.getString("hs_vaccination_period"),
                                    dataObj.getString("black_quarter_vaccination"), dataObj.getString("black_quarter_vaccination_period"),
                                    dataObj.getString("brucellousis_vaccination"), dataObj.getString("brucellousis_vaccination_period"),
                                    dataObj.getString("deworming"), dataObj.getString("deworming_period"),
                                    dataObj.getString("selling_price"), dataObj.getString("description"),
                                    dataObj.getString("contact"), dataObj.getString("image"),
                                    dataObj.getString("video")
                                    , dataObj.getString("category_name"), dataObj.getString("breed_name"),
                                    dataObj.getString("status"), dataObj.getString("state_id")
                                    , dataObj.getString("city_id"), dataObj.getString("state_name"), dataObj.getString("city_name")));

                        }
                        if (!mListAnimalData.isEmpty()) {
                            mList = new ArrayList<>();
                            mList.addAll(mListAnimalData);

                            initRecycleViewAnimalData();
                        }

                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceCaller.execute(getAnimalsAPI + "user_id=" + sessionManager.getValueSesion(KEY_UserID) + "&state_id=" + stateId);
    }

    @Override
    public void onClick(String id) {

        dashboard_itemAdapter.filterSearch(id);
    }


    private void selectStateDialog() {
        dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_list_item, null);
        dialogBuilder.setView(dialogView);
        TextView dialogTitle, tvCancel;
        ListView listView;

        listView = dialogView.findViewById(R.id.list);
        tvCancel = dialogView.findViewById(R.id.tvCancel);
        dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText(mContext.getResources().getString(R.string.Please_Select_State));
        SpinnerDialogAdapter spinnerAdapter = new SpinnerDialogAdapter(getActivity(), statePojos);
        listView.setAdapter(spinnerAdapter);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder = dialogBuilder.create();
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.show();
    }

    public class SpinnerDialogAdapter extends BaseAdapter {
        Context context;
        List<StatePojo> rowItems;
        public SpinnerDialogAdapter(Context context, List<StatePojo> items) {
            this.context = context;
            this.rowItems = items;
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.cardview_common_row_iteam, null);
                holder = new ViewHolder();
                holder.textName = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }
            holder.textName.setText(rowItems.get(position).sate_name);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                    toolbarTitle.setText(rowItems.get(position).sate_name);
                    stateId = rowItems.get(position).state_id;
                    stateName = rowItems.get(position).sate_name;
                    sessionManager.setValueSession(KEY_AnimalStateId, stateId);
                    sessionManager.setValueSession(KEY_AnimalStateName, stateName);
                    getAnimalData(stateId);
                }
            });
            return convertView;
        }

        @Override
        public int getCount() {
            return rowItems.size();
        }

        @Override
        public Object getItem(int position) {
            return rowItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return rowItems.indexOf(getItem(position));
        }

        public class ViewHolder {
            TextView textName;

        }


    }
}
