package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by Choudhary on 12/01/2019.
 */

public class BeanUserLoginAccount {
    public String user_id = "", name = "", phone_number = "", user_tokens = "", user_group_id = "";

    public BeanUserLoginAccount(String user_id, String name, String phone_number, String user_tokens, String user_group_id) {
        this.user_id = user_id;
        this.name = name;
        this.phone_number = phone_number;
        this.user_tokens = user_tokens;
        this.user_group_id = user_group_id;
    }

    public static void LoginWithMultipleAccount(final Context mContext, String user_id, String user_group_id) {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {
                SessionManager sessionManager = new SessionManager(mContext);
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("login_status");
                    if (success.equals("success")) {
                        if (jsonObject.getString("user_group_id").equals("2")) {
                            Constant.FirstTime = "Yes";
                            Constant.UserID = jsonObject.getString("id");
                            sessionManager.createLoginDairyJson(jsonObject, "yes");
                            UtilityMethod.showAlertBoxwithIntent(mContext, mContext.getString(R.string.Login_Success), MainActivity.class);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_id", user_id)
                .addEncoded("user_group_id", user_group_id)
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.loginByQRCodeAPI);

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUser_tokens() {
        return user_tokens;
    }

    public void setUser_tokens(String user_tokens) {
        this.user_tokens = user_tokens;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }


}
