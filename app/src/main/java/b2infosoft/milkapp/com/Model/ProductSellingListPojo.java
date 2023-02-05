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
 * Created by Microsoft on 30-Aug-17.
 */

public class ProductSellingListPojo {

    public String products_id = "";
    public String products_name = "";
    public String total_qty = "";
    public String total_price = "";


    public ProductSellingListPojo(String products_id, String products_name, String total_qty, String total_price) {

        this.products_id = products_id;
        this.products_name = products_name;
        this.total_qty = total_qty;
        this.total_price = total_price;

    }


    public static void getSaleProductList(final Context mContext, String dairy_id, final String startDate, String endDate) {
        final ArrayList<ProductSellingListPojo> mList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        mList.add(new ProductSellingListPojo
                                (jsonObject1.getString("products_id"),
                                        jsonObject1.getString("products_name"),
                                        jsonObject1.getString("total_qty"),
                                        jsonObject1.getString("total_price")));
                    }

                    //       ((ViewSellProductListActivity) mContext).setProductList(mList);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("in_date", startDate)
                .addEncoded("out_date", endDate).build();
        caller.addRequestBody(body);
        caller.execute(Constant.GetSaleProductList);


    }


}

