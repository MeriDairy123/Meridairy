package b2infosoft.milkapp.com.Model;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;


/**
 * Created by Choudahry on 06-Dec-19.
 */

public class DailySaleMilkCustomerListPojo {

    public String id = "", unic_customer = "", name = "",
            phone_number = "", morning_milk = "", evening_milk = "",
            price_per_ltr = "", is_active = "", sale_status = "";

    public DailySaleMilkCustomerListPojo(String id, String unic_customer, String name, String phone_number, String morning_milk, String evening_milk, String price_per_ltr, String is_active, String sale_status) {
        this.id = id;
        this.unic_customer = unic_customer;
        this.name = name;
        this.phone_number = phone_number;
        this.morning_milk = morning_milk;
        this.evening_milk = evening_milk;
        this.price_per_ltr = price_per_ltr;
        this.is_active = is_active;
        this.sale_status = sale_status;
    }

    public static void getDailyCustomerList(final Context mContext, String dairyID, String shift, String entry_date) {
        final ArrayList<DailySaleMilkCustomerListPojo> mList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                Log.d("Response>>>", response);
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        if (jsonObject1.getString("user_group_id").equals("4")) {
                            mList.add(new DailySaleMilkCustomerListPojo(jsonObject1.getString("id"), jsonObject1.getString("unic_customer")
                                    , jsonObject1.getString("name"), jsonObject1.getString("phone_number"), jsonObject1.getString("morning_milk"),
                                    jsonObject1.getString("evening_milk"), jsonObject1.getString("price_per_ltr"), jsonObject1.getString("is_active"), jsonObject1.getString("sale_status")));
                        }
                    }
                    if (!mList.isEmpty()) {
                        //  ((DailySaleMilkCustomerActivity) mContext).setEntryList(mList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyID)
                .addEncoded("entry_date", entry_date)
                .addEncoded("shift", shift)
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getDailySaleMilkCustomerListAPI);

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getMorning_milk() {
        return morning_milk;
    }

    public void setMorning_milk(String morning_milk) {
        this.morning_milk = morning_milk;
    }

    public String getEvening_milk() {
        return evening_milk;
    }

    public void setEvening_milk(String evening_milk) {
        this.evening_milk = evening_milk;
    }

    public String getPrice_per_ltr() {
        return price_per_ltr;
    }

    public void setPrice_per_ltr(String price_per_ltr) {
        this.price_per_ltr = price_per_ltr;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getSale_status() {
        return sale_status;
    }

    public void setSale_status(String sale_status) {
        this.sale_status = sale_status;
    }

}
