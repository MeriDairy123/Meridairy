package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getVehicleCustomer;

/**
 * Created by 9166900279 on 10/02/2020.
 */

public class BeanVehicleDairyItem {


    public String id = "", user_group_id = "", categorychart_id = "", unic_customer_for_mobile = "", unic_customer = "", name = "", father_name = "", phone_number = "", adhar = "",
            village = "", address = "", center_name = "", amount = "", entry_type = "",
            entry_price = "", bonus = "", commission_price = "", accountNo = "", iFSC_Code = "", bankName = "", firebase_tocan = "";

    public BeanVehicleDairyItem(String id, String user_group_id, String categorychart_id,
                                String unic_customer_for_mobile, String unic_customer, String name,
                                String father_name, String phone_number, String adhar, String village, String address,
                                String center_name, String amount, String entry_type, String entry_price, String bonus, String commission_price,
                                String accountNo, String iFSC_Code, String bankName, String firebase_tocan) {
        this.id = id;
        this.user_group_id = user_group_id;
        this.categorychart_id = categorychart_id;
        this.unic_customer_for_mobile = unic_customer_for_mobile;
        this.unic_customer = unic_customer;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.adhar = adhar;
        this.village = village;
        this.address = address;
        this.center_name = center_name;
        this.amount = amount;
        this.entry_type = entry_type;
        this.entry_price = entry_price;
        this.commission_price = commission_price;
        this.accountNo = accountNo;
        this.iFSC_Code = iFSC_Code;
        this.bankName = bankName;
        this.firebase_tocan = firebase_tocan;
    }

    public static void getVehicleDairyList(Context mContext, String userGroupId, boolean isDialog) {
        if (UtilityMethod.isNetworkAvaliable(mContext)) {
            SessionManager sessionManager = new SessionManager(mContext);
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", isDialog) {
                @Override
                public void handleResponse(String response) {
                    try {
                        ArrayList<BeanVehicleDairyItem> vehicleDairyItems = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                            JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                            if (mainJsonArray.length() > 0) {

                                for (int i = 0; i < mainJsonArray.length(); i++) {
                                    JSONObject obj = mainJsonArray.getJSONObject(i);
                                    vehicleDairyItems.add(new BeanVehicleDairyItem(obj.getString("Userid"), userGroupId,
                                            obj.getString("categorychart_id"), "", obj.getString("unic_customer"),
                                            obj.getString("name"), obj.getString("father_name"), obj.getString("phone_number"), obj.getString("adhar"),
                                            "", obj.getString("address"), obj.getString("center_name"), obj.getString("remaing_amount"),
                                            obj.getString("entry_type"), obj.getString("entry_price"), "", obj.getString("commission_price"),
                                            "", "", "",
                                            obj.getString("firebase_tocan")));

                                }
                            }
                        }
                        Gson gson = new Gson();
                        String json = gson.toJson(vehicleDairyItems);
                        sessionManager.setValueSession("beanVehicleDairy" + userGroupId, json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("vehicle_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_PlantId))
                    .addEncoded("vehicle_type", sessionManager.getValueSesion(SessionManager.KEY_VehicleType))
                    .addEncoded("user_group_id", userGroupId)
                    .build();
            caller.addRequestBody(body);
            caller.execute(getVehicleCustomer);

        }
    }

}
