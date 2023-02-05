package b2infosoft.milkapp.com.customer_app.customer_pojo;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerTransactionActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by u on 16-Oct-17.
 */

public class UserTransactionByDate {

    public String milk_entries_id = "";
    public String entry_date = "";
    public String created_time = "";
    public String entry_date_str = "";
    public String products_name = "";
    public String total_price = "";
    public String type = "";
    public String transactions_ids = "";

    public UserTransactionByDate(String milk_entries_id, String entry_date, String created_time, String entry_date_str, String products_name, String total_price, String type, String transactions_ids) {
        this.milk_entries_id = milk_entries_id;
        this.entry_date = entry_date;
        this.created_time = created_time;
        this.entry_date_str = entry_date_str;
        this.products_name = products_name;
        this.total_price = total_price;
        this.type = type;
        this.transactions_ids = transactions_ids;
    }

    public static void getUserTransactionList(final Context mContext, String dairy_id, String customer_id, String entry_date_from, String entry_date_to) {
        final ArrayList<UserTransactionByDate> arrayList = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(mContext);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        arrayList.add(new UserTransactionByDate(jsonObject1.getString("milk_entries_id"), jsonObject1.getString("entry_date")
                                , jsonObject1.getString("created_time"), jsonObject1.getString("entry_date_str"), jsonObject1.getString("products_name")
                                , jsonObject1.getString("total_price"), jsonObject1.getString("type"), jsonObject1.getString("transactions_ids")));
                    }
                    ((CustomerTransactionActivity) mContext).setTransactionDataList(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("entry_date_from", entry_date_from)
                .addEncoded("entry_date_to", entry_date_to)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getCustomerTransactionList);


    }
}
