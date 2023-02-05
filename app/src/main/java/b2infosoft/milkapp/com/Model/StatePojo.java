package b2infosoft.milkapp.com.Model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Advertisement.UploadAdsActivity;
import b2infosoft.milkapp.com.activity.LoginActivity;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.activity.RegisterActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by B2infosoft on 7/27/2017.
 */

public class StatePojo {

    public String state_id = "";
    public String sate_name = "";

    public StatePojo(String state_id, String sate_name) {

        this.state_id = state_id;
        this.sate_name = sate_name;
    }


    public static void getStateList(final Context mContext, final String from) {

        final ArrayList<StatePojo> stateList = new ArrayList<StatePojo>();


        NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);


                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        // Log.d("StateId", object.getString("state_id"));
                        // Log.d("StateName", object.getString("sate_name"));
                        stateList.add(new StatePojo(object.getString("id"), object.getString("name")));
                    }

                    if (!stateList.isEmpty()) {
                        if (from.equalsIgnoreCase("Register")) {
                            ((RegisterActivity) mContext).setStateAdapter(stateList);
                        } else if (from.equalsIgnoreCase("loginAPI")) {
                            ((LoginActivity) mContext).setStateAdapter(stateList);
                        } else if (from.equalsIgnoreCase("Ads")) {
                            ((UploadAdsActivity) mContext).setStateAdapter(stateList);
                        } else {
                            ((LoginWithPasswordActivity) mContext).setStateAdapter(stateList);
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serviceCaller.execute(Constant.getStateAPI);
    }


}
