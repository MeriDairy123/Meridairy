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
import b2infosoft.milkapp.com.customer_app.customer_actvities.ProductListActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;


/**
 * Created by Choudhary on 24-Nov-18.
 */

public class CustomerProductListPojo {

    public String insert_date = "";
    public String product_name = "";
    public String qty = "";
    public String total_price = "";

    public CustomerProductListPojo(String insert_date, String product_name, String qty, String total_price) {
        this.insert_date = insert_date;
        this.product_name = product_name;
        this.qty = qty;
        this.total_price = total_price;
    }


    public static void getProductList(final Context context, String dairyID, String userID, String user_group_id) {
        SessionManager sessionManager = new SessionManager(context);
        final ArrayList<CustomerProductListPojo> arrayList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, context, context.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        arrayList.add(new CustomerProductListPojo(jsonObject1.getString("insert_date"), jsonObject1.getString("product_name")
                                , jsonObject1.getString("qty"), jsonObject1.getString("total_price")));
                    }
                    ((ProductListActivity) context).setProductDataList(arrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", userID)
                .addEncoded("dairy_id", dairyID)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))

                .build();
        caller.addRequestBody(body);

        caller.execute(Constant.getProductList2);

    }

}
