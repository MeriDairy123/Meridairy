package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.AnimalMyUpload_ListingAdapter;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mListAnimalData;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

public class Fragment_My_Upload_Animal_Listing extends Fragment {
    View view;
    SessionManager sessionManager;
    TextView toolbar_title;
    Toolbar toolbar;
    Context mContext;
    RecyclerView animal_detail_recycle;

    List<BeanAnimalDashboard> mList;
    AnimalMyUpload_ListingAdapter myUpload_listingAdapter;
    String userId = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_animal_upload, container, false);
        mContext = getActivity();
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar_title.setText(mContext.getString(R.string.AnimalList));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        sessionManager = new SessionManager(mContext);
        userId = nullCheckFunction(sessionManager.getValueSesion(KEY_UserID));

        animal_detail_recycle = view.findViewById(R.id.animal_detail_recycle);

        mList = new ArrayList<>();

        if (!mListAnimalData.isEmpty()) {
            for (int i = 0; i < mListAnimalData.size(); i++) {

                if (userId.equalsIgnoreCase(mListAnimalData.get(i).getUser_id())) {
                    mList.add(mListAnimalData.get(i));
                }
            }
        }


        initRecycleViewAnimalData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void initRecycleViewAnimalData() {
        GridLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 2);
        animal_detail_recycle.setLayoutManager(linearLayoutManager);
        myUpload_listingAdapter = new AnimalMyUpload_ListingAdapter(mContext, mList);
        animal_detail_recycle.setAdapter(myUpload_listingAdapter);

    }


}


