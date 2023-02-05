package b2infosoft.milkapp.com.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.activity.LoginActivity;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.activity.RegisterActivity;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getCityAPI;

/**
 * Created by B2infosoft on 7/29/2017.
 */

public class CityPojo {

    public String city_id = "";
    public String city_name = "";


    public CityPojo(String city_id, String city_name) {

        this.city_id = city_id;
        this.city_name = city_name;
    }

    public static void getCityList(final Context mContext, String state_id, final String from) {
        final ArrayList<CityPojo> cityList = new ArrayList<>();

        NetworkTask caller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);

                        System.out.println("CityId===>>>>" + jsonObject1.getString("id") + "  " + jsonObject1.getString("name"));
                        cityList.add(new CityPojo(jsonObject1.getString("id"), jsonObject1.getString("name")));
                    }
                    if (!cityList.isEmpty()) {
                        if (from.equalsIgnoreCase("Register")) {
                            ((RegisterActivity) mContext).setCityAdapter(cityList);
                        } else if (from.equalsIgnoreCase("loginAPI")) {
                            ((LoginActivity) mContext).setCityAdapter(cityList);
                        } else {
                            ((LoginWithPasswordActivity) mContext).setCityAdapter(cityList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        //   caller.addNameValuePair("state_id", state_id);
        caller.execute(getCityAPI.replace("@state_id", state_id));
    }

}


