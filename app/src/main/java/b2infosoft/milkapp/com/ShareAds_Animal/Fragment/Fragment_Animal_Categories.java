package b2infosoft.milkapp.com.ShareAds_Animal.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paytm.pgsdk.easypay.utils.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Model.AnimalSubCategoriesData;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Adapter.AnimalSubCategoriesAdapter;
import b2infosoft.milkapp.com.ShareAds_Animal.BeanUploadImage;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.AnimalCatImgUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.AnimalName;
import static b2infosoft.milkapp.com.appglobal.Constant.getAnimalSubCategoryAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalMainCategoryId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class Fragment_Animal_Categories extends Fragment {
    public static ArrayList<BeanUploadImage> beanUploadImages = new ArrayList<BeanUploadImage>();
    View view;
    SessionManager sessionManager;
    Context mContext;
    RecyclerView animal_recycle_subcategories;
    AnimalSubCategoriesAdapter animalSubCategoriesAdapter;
    List<AnimalSubCategoriesData> subCategoryList;
    String mainCategoryId = "";
    Toolbar toolbar;
    TextView tvAnimalName;
    ImageView animal_image;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.animal_categories_fragment, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        subCategoryList = new ArrayList<>();
        beanUploadImages = new ArrayList<BeanUploadImage>();
        mainCategoryId = sessionManager.getValueSesion(KEY_AnimalMainCategoryId);

        tvAnimalName = view.findViewById(R.id.tvAnimalName);
        animal_image = view.findViewById(R.id.animal_image);
        animal_recycle_subcategories = view.findViewById(R.id.animal_recycle_subcategories);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setTitle(mContext.getResources().getString(R.string.Choose_Breed));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        tvAnimalName.setText(AnimalName);
        if (AnimalCatImgUrl.contains(".svg")) {
            //  SvgLoader.pluck().with((Activity) mContext).load(AnimalCatImgUrl, animal_image);
            // .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(AnimalCatImgUrl).into(animal_image);
        }
        getAnimalSubCtegory();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        beanUploadImages = new ArrayList<BeanUploadImage>();
    }

    private void getAnimalSubCtegory() {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("response", "==category" + response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        subCategoryList = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject categroyObj = jsonArray.getJSONObject(i);
                            subCategoryList.add(new AnimalSubCategoriesData(
                                    categroyObj.getString("name"),
                                    categroyObj.getString("id"),
                                    categroyObj.getString("main_id")));
                            initSubCategoryRecycleView();
                        }
                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        webServiceCaller.execute(getAnimalSubCategoryAPI + "main_id=" + mainCategoryId);

    }

    public void initSubCategoryRecycleView() {
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(mContext, 2);
        animal_recycle_subcategories.setLayoutManager(linearLayoutManager);
        animalSubCategoriesAdapter = new AnimalSubCategoriesAdapter(mContext, subCategoryList);
        animal_recycle_subcategories.setAdapter(animalSubCategoriesAdapter);
    }

}


