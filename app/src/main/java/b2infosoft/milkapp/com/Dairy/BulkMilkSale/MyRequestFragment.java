package b2infosoft.milkapp.com.Dairy.BulkMilkSale;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter.Request_item_adapter;
import b2infosoft.milkapp.com.Model.BeanUserRequestItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.getPlanRequestAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;


public class MyRequestFragment extends Fragment {
    View view;
    Toolbar toolbar;

    Context mContext;
    Request_item_adapter adapter;
    ArrayList<BeanUserRequestItem> mList;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;

    SessionManager sessionManager;
    String userId = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);
        mList = new ArrayList<>();
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        userId = sessionManager.getValueSesion(KEY_UserID);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.myRequest));
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserRequestList();
                pullToRefresh.setRefreshing(false);
            }
        });

        getUserRequestList();
        return view;
    }

    private void initRecyclerView() {
        adapter = new Request_item_adapter(mContext, mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void getUserRequestList() {
        mList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);


                            mList.add(new BeanUserRequestItem(jsonObject1.getString("id"), jsonObject1.getString("user_id"),
                                    jsonObject1.getString("name")
                                    , jsonObject1.getString("relationship_user_id"), jsonObject1.getString("type"),
                                    jsonObject1.getString("status"), jsonObject1.getString("created_at")
                            ));
                        }

                    }
                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", userId).build();
        caller.addRequestBody(body);

        caller.execute(getPlanRequestAPI);

    }


}
