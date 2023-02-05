package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by u on 13-Jan-18.
 */

public class ReceiveCashPojo {

    public String id = "";
    public String created_by = "";
    public String unic_customer = "";
    public String name = "";
    public String father_name = "";
    public String phone_number = "";
    public String user_group_id = "";
    public String firebase_tocan = "";
    public String total_morning_milk = "";
    public String total_evening_milk = "";
    public String total_morning_price = "";
    public String total_evening_price = "";

    public String milk_entries_id = "";
    public String entry_date = "";
    public String created_time = "";
    public String entry_date_str = "";
    public String products_name = "";
    public String total_price = "";
    public String type = "";
    public ArrayList<ReceiveCashPojo> transactionList;

    public ReceiveCashPojo(String id, String created_by, String unic_customer, String name, String father_name, String phone_number, String user_group_id, String firebase_tocan, String total_morning_milk, String total_evening_milk, String total_morning_price, String total_evening_price, ArrayList<ReceiveCashPojo> transactionList) {
        this.id = id;
        this.created_by = created_by;
        this.unic_customer = unic_customer;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.user_group_id = user_group_id;
        this.firebase_tocan = firebase_tocan;
        this.total_morning_milk = total_morning_milk;
        this.total_evening_milk = total_evening_milk;
        this.total_morning_price = total_morning_price;
        this.total_evening_price = total_evening_price;
        this.transactionList = transactionList;
    }


    public ReceiveCashPojo(String milk_entries_id, String entry_date, String created_time, String entry_date_str, String products_name, String total_price, String type) {
        this.milk_entries_id = milk_entries_id;
        this.entry_date = entry_date;
        this.created_time = created_time;
        this.entry_date_str = entry_date_str;
        this.products_name = products_name;
        this.total_price = total_price;
        this.type = type;
    }


    public static void getMonthlyMilkDetails(final Context mContext, String dairy_id, String user_group_id, String month, String year) {

        final ArrayList<ReceiveCashPojo> mainList = new ArrayList<ReceiveCashPojo>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        ArrayList<ReceiveCashPojo> transactionList = new ArrayList<ReceiveCashPojo>();
                        JSONObject mJsonObject = jsonArray.getJSONObject(i);
                        JSONObject milk_shell_entryObject = mJsonObject.getJSONObject("milk_shell_entry");
                        JSONArray transactions_dataArray = mJsonObject.getJSONArray("transactions_data");
                        for (int j = 0; j < transactions_dataArray.length(); j++) {
                            JSONObject transactions_dataObj = transactions_dataArray.getJSONObject(j);
                            transactionList.add(new ReceiveCashPojo(
                                    transactions_dataObj.getString("milk_entries_id"),
                                    transactions_dataObj.getString("entry_date"),
                                    transactions_dataObj.getString("created_time"),
                                    transactions_dataObj.getString("entry_date_str"),
                                    transactions_dataObj.getString("products_name"),
                                    transactions_dataObj.getString("total_price"),
                                    transactions_dataObj.getString("type")
                            ));
                        }

                        mainList.add(new ReceiveCashPojo(
                                mJsonObject.getString("id"),
                                mJsonObject.getString("created_by"),
                                mJsonObject.getString("unic_customer"),
                                mJsonObject.getString("name"),
                                mJsonObject.getString("father_name"),
                                mJsonObject.getString("phone_number"),
                                mJsonObject.getString("user_group_id"),
                                mJsonObject.getString("firebase_tocan"),
                                milk_shell_entryObject.getString("total_morning_milk"),
                                milk_shell_entryObject.getString("total_evening_milk"),
                                milk_shell_entryObject.getString("total_morning_price"),
                                milk_shell_entryObject.getString("total_evening_price"),
                                transactionList
                        ));
                    }
                    // ((ReceiveCashActivity) mContext).setTransactionListAdapter(mainList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("user_group_id", user_group_id)
                .addEncoded("month", month)
                .addEncoded("year", year).build();
        caller.addRequestBody(body);

        caller.execute(Constant.getMonthlyMilkSellEntryAPI);

    }


}
