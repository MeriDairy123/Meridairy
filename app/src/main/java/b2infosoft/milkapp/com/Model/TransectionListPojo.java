package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getBhugationSetingAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.saveBhugationSettingAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfFAT;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfFont;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfSnf;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerPdfclr;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfFAT;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfFont;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfRate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfSnf;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerPdfclr;

/**
 * Created by B2infosoft on 8/11/2017.
 */

public class TransectionListPojo {

    public String milk_entries_id = "", entry_date = "" , products_name = "" , total_price = "" ,
     type = "" , transactions_ids = "" , entry_date_str = "" , created_time = "" ,
     total_bonus = "";

    public TransectionListPojo(String milk_entries_id, String entry_date, String products_name, String total_price, String type, String transactions_ids, String entry_date_str, String created_time, String total_bonus) {
        this.milk_entries_id = milk_entries_id;
        this.entry_date = entry_date;
        this.products_name = products_name;
        this.total_price = total_price;
        this.type = type;
        this.transactions_ids = transactions_ids;
        this.entry_date_str = entry_date_str;
        this.created_time = created_time;
        this.total_bonus = total_bonus;
    }
    public static void getBhugtanSetting(Context mContext,String from) {
        SessionManager sessionManager=new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        // showToast(getActivity(), jsonObject.getString("user_status_message"));
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                       if (from.equalsIgnoreCase("seller")) {
                           sessionManager.setIntValueSession(Key_SellerPdfSnf, jsonData.getInt("seller_snf"));
                           sessionManager.setIntValueSession(Key_SellerPdfFAT, jsonData.getInt("seller_fat"));
                           sessionManager.setIntValueSession(Key_SellerPdfclr, jsonData.getInt("seller_clr"));
                           sessionManager.setIntValueSession(Key_SellerPdfRate, jsonData.getInt("seller_rate"));
                           sessionManager.setValueSession(Key_SellerPdfFont, jsonData.getString("seller_font_size"));
                           sessionManager.setValueSession(Key_SellerMilkWeekStatus, jsonData.getString("seller_week_status"));
                       }else {
                           sessionManager.setIntValueSession(Key_BuyerPdfSnf, jsonData.getInt("buyer_snf"));
                           sessionManager.setIntValueSession(Key_BuyerPdfFAT, jsonData.getInt("buyer_fat"));
                           sessionManager.setIntValueSession(Key_BuyerPdfclr, jsonData.getInt("buyer_clr"));
                           sessionManager.setIntValueSession(Key_BuyerPdfRate, jsonData.getInt("buyer_rate"));
                           sessionManager.setValueSession(Key_BuyerPdfFont, jsonData.getString("buyer_font_size"));
                           sessionManager.setValueSession(Key_BuyerMilkWeekStatus, jsonData.getString("buyer_week_status"));
                       }
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(getBhugationSetingAPI);
    }

    public static void saveBhugtanSetting(Context mContext,String strType,String strField,String strFieldValue) {
        SessionManager sessionManager=new SessionManager(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        sessionManager.setValueSession(strType + strField, strFieldValue);
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("field_name", strType + strField)
                .addEncoded("entry_value", strFieldValue)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(saveBhugationSettingAPI);

    }

}
