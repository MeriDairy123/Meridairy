package b2infosoft.milkapp.com.customer_app.customer_pojo;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by Microsoft on 27-Sep-18.
 */

public class MonthsEntryListPojo {
    public String entry_date = "",
     id = "", customer_id = "", dairy_id = "", fat = "",
     snf = "", entry_date2 = "", per_kg_price = "", total_price = "", total_bonus = "", total_milk = "", shift = "";

    public MonthsEntryListPojo(String entry_date, String id, String customer_id, String dairy_id, String fat, String entry_date2,
                               String per_kg_price, String total_price, String total_milk, String shift) {
        this.entry_date = entry_date;
        this.id = id;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.fat = fat;
        this.snf = snf;
        this.entry_date2 = entry_date2;
        this.per_kg_price = per_kg_price;
        this.total_price = total_price;
        this.total_milk = total_milk;
        this.shift = shift;
        this.total_bonus = total_bonus;
    }

    public MonthsEntryListPojo(String entry_date, String id, String customer_id, String dairy_id, String fat, String snf, String entry_date2, String per_kg_price, String total_price, String total_bonus, String total_milk, String shift) {
        this.entry_date = entry_date;
        this.id = id;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.fat = fat;
        this.snf = snf;
        this.entry_date2 = entry_date2;
        this.per_kg_price = per_kg_price;
        this.total_price = total_price;
        this.total_bonus = total_bonus;
        this.total_milk = total_milk;
        this.shift = shift;
    }

    public static void getMonthsEntryList(final Context mContext, String dairy_id, String customer_id, String month, String year, final String fromWhere, boolean dialog) {

        final ArrayList<MonthsEntryListPojo> entryListPojos = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(mContext);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", dialog) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")){
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if(mainJsonArray.length()!=0){
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject jsonObj= mainJsonArray.getJSONObject(i);
                                entryListPojos.add(new MonthsEntryListPojo(jsonObj.getString("entry_date"), jsonObj.getString("id")
                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));
                            }
                        }else{
                            entryListPojos.add(new MonthsEntryListPojo("", ""
                                    , "", "", "", "", "", "", "", "", "", ""));
                        }

                        if (!entryListPojos.isEmpty()) {
                            if (fromWhere.equals("EntryMilkActivity")) {
                                ((MilkEntryActivity) mContext).setMonthEntryList(entryListPojos);

                            }
                        }


                    }

//
//                    JSONArray mainJsonArray = new JSONArray(response);
//                    for (int i = 0; i < mainJsonArray.length(); i++) {
//                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
//
//                        JSONArray morningArray = jsonObject1.getJSONArray("morning");
//                        if (morningArray.length() != 0) {
//                            for (int j = 0; j < morningArray.length(); j++) {
//                                JSONObject jsonObj= morningArray.getJSONObject(j);
//                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
//                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
//                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
//                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));
//                            }
//
//                        } else {
//                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
//                                    , "", "", "", "", "", "", "", "", "", "morning"));
//
//                        }
//
//                        JSONArray eveningArray = jsonObject1.getJSONArray("evening");
//                        if (eveningArray.length() != 0) {
//                            for (int j = 0; j < eveningArray.length(); j++) {
//                                JSONObject jsonObj = eveningArray.getJSONObject(j);
//                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
//                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
//                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
//                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));
//
//                            }
//                        } else {
//                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
//                                    , "", "", ""
//                                    , "", "", ""
//                                    , "", "", "", "evening"));
//                        }
//                    }
//                    if (!entryListPojos.isEmpty()) {
//                        if (fromWhere.equals("EntryMilkActivity")) {
//                            ((MilkEntryActivity) mContext).setMonthEntryList(entryListPojos);
//
//                        }
//                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", customer_id)
                .addEncoded("month", month)
                .addEncoded("year", year)
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        System.out.println("google>>>" + Constant.getMonthMilkEntryCustomerapp);
        caller.execute(Constant.getMonthMilkEntryCustomerapp);


    }


}
