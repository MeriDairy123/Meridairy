package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfAdapter;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;

public class SnfFatRateFragment extends Fragment {

    View view;
    private Context mContext;

    private RecyclerView recyclerRateList;

    private ArrayList<SnfFatListPojo> mList = new ArrayList<>();
    private ArrayList<String> fatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        mList = new ArrayList<>();

        mContext = getActivity();
        recyclerRateList = view.findViewById(R.id.recyclerRateList);
        Bundle bundle = getArguments();
        Gson gson = new Gson();
        String json = bundle.getString("list");

        mList = gson.fromJson(json, new TypeToken<ArrayList<SnfFatListPojo>>() {
        }.getType());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerRateList.setHasFixedSize(true);
        SnfAdapter snfAdapter = new SnfAdapter(mContext, mList, fatList);
        recyclerRateList.setLayoutManager(mLayoutManager);
        recyclerRateList.setAdapter(snfAdapter);
        return view;
    }


}
