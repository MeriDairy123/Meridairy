package b2infosoft.milkapp.com.Model;

import android.content.Context;
import android.view.View;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Microsoft on 23-Aug-17.
 */

public class ProductListPojo {
    public String id = "";
    public String product_name = "";
    public String product_weight = "";
    public String product_price = "";
    public String dairy_id = "";
    public String product_qty = "";
    public String product_image = "";
    public int quentity;
    public double totPrice;

    public String path = "";

    public ProductListPojo(String id, String product_name, String product_weight, String product_price, String dairy_id, String product_qty, String product_image, int quentity, double totPrice) {
        this.id = id;
        this.product_name = product_name;
        this.product_weight = product_weight;
        this.product_price = product_price;
        this.dairy_id = dairy_id;
        this.product_qty = product_qty;
        this.product_image = product_image;
        this.quentity = quentity;
        this.totPrice = totPrice;
    }

    public void getProductDetail(final Context mContext, String userID, final String fromWhere, View view) {
        final ArrayList<ProductListPojo> listPojos = new ArrayList<>();

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);

                            listPojos.add(new ProductListPojo(object.getString("id"),
                                    object.getString("product_name"),
                                    object.getString("weight"),
                                    object.getString("product_price"),
                                    object.getString("dairy_id"),
                                    object.getString("qty"),
                                    object.getString("image"), 0, 0));
                        }
                        if (!listPojos.isEmpty()) {
                            if (fromWhere.equals("SaleProductAct")) {
                                //   SaleItemFragment saleItemFragment = new SaleItemFragment();
                                //   (saleItemFragment).setProductList(listPojos,view);
                            }
                        }
                    } else {
                        showToast(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", userID).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getProductListAPI);

    }

}
