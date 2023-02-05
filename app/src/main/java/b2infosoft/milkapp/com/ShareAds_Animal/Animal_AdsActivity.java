package b2infosoft.milkapp.com.ShareAds_Animal;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.AnimalCategoriesData;
import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Fragment.fragment_AnimalDashboard;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getAnimalCategoryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getAnimalsAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getStateAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalStateId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


/**
 * Created by Choudhary on 07-19-2018.
 */
public class Animal_AdsActivity extends AppCompatActivity {

    public static ArrayList<BeanAnimalDashboard> mListAnimalData = new ArrayList<>();
    public static ArrayList<StatePojo> statePojos = new ArrayList<>();
    public static ArrayList<AnimalCategoriesData> mainCategoryList = new ArrayList<>();
    Fragment fragment;
    Context mContext;
    SessionManager sessionManager;
    String stateId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_frame_contaner);
        mContext = Animal_AdsActivity.this;
        str_location_address = "";
        str_location_city = "";
        str_location_state = "";
        str_location_postCode = "";
        str_location_Latitude = "";
        str_location_Longitude = "";
        sessionManager = new SessionManager(mContext);
        stateId = nullCheckFunction(sessionManager.getValueSesion(KEY_AnimalStateId));
        if (isNetworkAvaliable(mContext)) {
            if (mainCategoryList.isEmpty()) {
                getAnimalCategory();
            }
            if (statePojos.size() == 0) {
                getAnimalStateList(mContext);
            }
            getAnimalData();

        } else {
            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
        }

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {

            dialogExit();
        }

    }

    public void dialogExit() {
        new AlertDialog.Builder(Animal_AdsActivity.this)
                .setMessage("Are you discard sharing product?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                       /* Intent i = new Intent(Animal_AdsActivity.this, CustomerDeshBoardActivity.class);
                        startActivity(i);*/
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getAnimalData() {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    System.out.println("response" + "==animal Data" + response);

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

                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                    moveToFragment();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceCaller.execute(getAnimalsAPI + "user_id=" + sessionManager.getValueSesion(KEY_UserID) + "&state_id=" + stateId);
    }

    private void getAnimalCategory() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        mainCategoryList = new ArrayList<>();
                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject categroyObj = jsonArray.getJSONObject(i);
                            mainCategoryList.add(new AnimalCategoriesData(
                                    categroyObj.getString("name"),
                                    categroyObj.getString("id"),
                                    BaseImageUrl + categroyObj.getString("image")));
                        }
                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        webServiceCaller.execute(getAnimalCategoryAPI);
    }

    private void moveToFragment() {

        fragment = new fragment_AnimalDashboard();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_share_ads, fragment).commit();
    }


    public void getAnimalStateList(final Context mContext) {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);

                    statePojos = new ArrayList<>();
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        statePojos.add(new StatePojo(object.getString("id"), object.getString("name")));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(getStateAPI);
    }

}
