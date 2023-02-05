package b2infosoft.milkapp.com.customer_app.customer_pojo;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryDateActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.webservice.NetworkTask.POST_TASK;

/**
 * Created by u on 17-Oct-17.
 */

public class DairyMonthlyDataPojo {

    public String total_credit = "";
    public String total_milk = "";
    public String name = "";
    public String m = "";
    public String y = "";

    public DairyMonthlyDataPojo(String total_credit, String total_milk, String name, String m, String y) {
        this.total_credit = total_credit;
        this.total_milk = total_milk;
        this.name = name;
        this.m = m;
        this.y = y;
    }

    public static void getDairyMonthData(final Context mContext, String userID, String year, String dairyID) {
        final ArrayList<DairyMonthlyDataPojo> arrayList = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(mContext);
        NetworkTask caller = new NetworkTask(POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        //Log.d("CityId>>>>", jsonObject1.getString("id") + "  " + jsonObject1.getString("name"));
                        arrayList.add(new DairyMonthlyDataPojo(jsonObject1.getString("total_credit"), jsonObject1.getString("total_milk")
                                , jsonObject1.getString("name"), jsonObject1.getString("m"), jsonObject1.getString("y")));
                    }
                    if (!arrayList.isEmpty()) {
                        ((MilkEntryDateActivity) mContext).setDairyMonthDataList(arrayList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("userid", userID)
                .addEncoded("year", year)
                .addEncoded("dairy_id", dairyID)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getSellerCustomerMonthlyData);
    }
}
