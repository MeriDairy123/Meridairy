package b2infosoft.milkapp.com.BuyPlan;


import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.Interface.PlanListListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.appglobal.Constant.getChecksumFromArray;
import static b2infosoft.milkapp.com.appglobal.Constant.getPlanAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by u on 31-Jan-19.
 */

public class BeanSMSPlan {

    public String id, title, plan_day, plan_cost, plan_type, sms_credit, validity, colorHeader, colorBackground;

    public BeanSMSPlan(String id, String title, String plan_day, String plan_cost, String plan_type,
                       String sms_credit, String validity, String colorHeader, String colorBackground) {
        this.id = id;
        this.title = title;
        this.plan_day = plan_day;
        this.plan_cost = plan_cost;
        this.plan_type = plan_type;
        this.sms_credit = sms_credit;
        this.validity = validity;
        this.colorHeader = colorHeader;
        this.colorBackground = colorBackground;
    }

    public static void getPlan(final Context mContext, final String from, PlanListListener listListener) {
        SessionManager sessionManager=new SessionManager(mContext);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                ArrayList<BeanSMSPlan> mlist = new ArrayList<>();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (from.equalsIgnoreCase("membership")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject job = jsonArray.getJSONObject(i);
                            mlist.add(new BeanSMSPlan(job.getString("id"),
                                    job.getString("title"), job.getString("plan_day"),
                                    job.getString("plan_cost"), job.getString("plan_type"),
                                    job.getString("sms_credit"), job.getString("plan_day"),
                                    job.getString("plan_header_color"),
                                    job.getString("plan_background_color")));

                        }
                    }else {

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonOb = jsonArray.getJSONObject(i);
                        mlist.add(new BeanSMSPlan(jsonOb.getString("id"),
                                jsonOb.getString("title"), jsonOb.getString("plan_day"),
                                jsonOb.getString("plan_cost"), jsonOb.getString("plan_type"),
                                jsonOb.getString("sms_credit"), "Unlimited",
                                jsonOb.getString("plan_header_color"),
                                jsonOb.getString("plan_background_color"))
                        );

                    }
                    }
                    listListener.setPlan(mlist);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("plan_type", from)
                .addEncoded("user_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        caller.addRequestBody(body);
        caller.execute(getPlanAPI);

    }



    public static void generateCheckSum(Context mContext, String order_id, String customer_id, String amount, PaytmCheckSumListener listener) {
        if (isNetworkAvaliable(mContext)) {

            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                    "Please wait...", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String checksum = jsonObject.getString("CHECKSUMHASH").trim();
                        JSONObject objParam = jsonObject.getJSONObject("paramList");
                        MID = objParam.getString("MID");
                        INDUSTRY_TYPE_ID = objParam.getString("INDUSTRY_TYPE_ID");
                        CHANNEL_ID = objParam.getString("CHANNEL_ID");
                        WEBSITE = objParam.getString("WEBSITE");
                        listener.checkSumResult(checksum, objParam);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("ORDER_ID", order_id)
                    .addEncoded("CUST_ID", customer_id)
                    .addEncoded("TXN_AMOUNT", amount)
                    .addEncoded("CALLBACK_URL", CALLBACK_URL + order_id)
                    .build();
            caller.addRequestBody(body);
            caller.execute(getChecksumFromArray);

        }
        else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlan_day() {
        return plan_day;
    }

    public void setPlan_day(String plan_day) {
        this.plan_day = plan_day;
    }

    public String getPlan_cost() {
        return plan_cost;
    }

    public void setPlan_cost(String plan_cost) {
        this.plan_cost = plan_cost;
    }

    public String getPlan_type() {
        return plan_type;
    }

    public void setPlan_type(String plan_type) {
        this.plan_type = plan_type;
    }

    public String getSms_credit() {
        return sms_credit;
    }

    public void setSms_credit(String sms_credit) {
        this.sms_credit = sms_credit;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getColorHeader() {
        return colorHeader;
    }

    public void setColorHeader(String colorHeader) {
        this.colorHeader = colorHeader;
    }

    public String getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(String colorBackground) {
        this.colorBackground = colorBackground;
    }
}
