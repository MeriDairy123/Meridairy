package b2infosoft.milkapp.com.Advertisement;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Advertisement.Adapter.MyAdv_ItemAdapter;
import b2infosoft.milkapp.com.Model.BeanMyAdvItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getdairyAdvertisementAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class fragmentMyAdvertisement extends Fragment implements View.OnClickListener {


    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView imgAddNew;
    SessionManager sessionManager;
    ArrayList<BeanMyAdvItem> beanMyAdvItems;
    RecyclerView recyclerView;
    MyAdv_ItemAdapter adaper;
    Fragment fragment = null;
    Bundle bundle = null;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_my_advertisement, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        beanMyAdvItems = new ArrayList<>();

        initView();
        return view;

    }


    private void initView() {

        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        imgAddNew = toolbar.findViewById(R.id.imgAdd);
        toolbar_title.setText(getString(R.string.MyAdvertisement));
        recyclerView = view.findViewById(R.id.recyclerView);

        imgAddNew.setVisibility(View.VISIBLE);
        imgAddNew.setOnClickListener(this);

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        initRecyclerView();
        getmyAdvertisementList(mContext);

    }

    private void initRecyclerView() {

        adaper = new MyAdv_ItemAdapter(mContext, beanMyAdvItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adaper);

    }

    public void setAdapterData(ArrayList<BeanMyAdvItem> myAdvItems) {
        beanMyAdvItems = myAdvItems;
        initRecyclerView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imgAdd:
                Intent intent = new Intent(mContext, UploadAdsActivity.class);
                intent.putExtra("FromWhere", "Deshboard");
                startActivity(intent);
                break;
            case R.id.layoutHeaderCart:
                break;
        }
    }

    public void getmyAdvertisementList(final Context mContext) {
        SessionManager sessionManager = new SessionManager(mContext);

        if (isNetworkAvaliable(mContext)) {

            final ArrayList<BeanMyAdvItem> myAdvItems = new ArrayList<BeanMyAdvItem>();

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext,
                    "Please wait...", true) {
                @Override
                public void handleResponse(String response) {


                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            BaseImageUrl = jsonObject.getString("path");
                            JSONArray dataJsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < dataJsonArray.length(); i++) {
                                JSONObject object = dataJsonArray.getJSONObject(i);
                                myAdvItems.add(new BeanMyAdvItem(object.getString("id"),
                                        object.getString("title"), object.getString("price"),
                                        object.getString("start_date"), object.getString("start_date"),
                                        object.getString("type"),
                                        object.getString("cities"), object.getString("description"),
                                        object.getString("image"), object.getString("thumb"),
                                        object.getInt("status")));
                            }
                            if (!myAdvItems.isEmpty()) {
                                setAdapterData(myAdvItems);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            serviceCaller.execute(getdairyAdvertisementAPI + "dairy_id=" + sessionManager.getValueSesion(KEY_UserID));
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }


}
