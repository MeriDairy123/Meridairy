package b2infosoft.milkapp.com.customer_app.customer_pojo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerDairyListActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDairyListActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDairyListWithBoxActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDeshBoardActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getAllDairyNameAPI;

/**
 * Created by u on 16-Oct-17.
 */

public class DairyNamePojo {

    public static final Parcelable.Creator<DairyNamePojo> CREATOR = new Parcelable.Creator<DairyNamePojo>() {
        @Override
        public DairyNamePojo createFromParcel(Parcel in) {
            return new DairyNamePojo(in);
        }

        @Override
        public DairyNamePojo[] newArray(int size) {
            return new DairyNamePojo[size];
        }
    };
    public String id = "", name = "", dairy_name = "", center_name = "",
            unic_customer_for_mobile = "", unic_customer = "", phone_number = "", firebase_tocan = "", customer_id = "", customer_user_group_id = "";
    public boolean visibility = false;


    protected DairyNamePojo(Parcel in) {
        id = in.readString();
        visibility = in.readByte() != 0x00;
    }


    public DairyNamePojo(String id, String name, String dairy_name, String center_name,
                         String unic_customer_for_mobile, String unic_customer,
                         String phone_number, String firebase_tocan, String customer_id, String customer_user_group_id, boolean visibility) {
        this.id = id;
        this.name = name;
        this.dairy_name = dairy_name;
        this.center_name = center_name;
        this.unic_customer_for_mobile = unic_customer_for_mobile;
        this.unic_customer = unic_customer;
        this.phone_number = phone_number;
        this.firebase_tocan = firebase_tocan;
        this.customer_id = customer_id;
        this.customer_user_group_id = customer_user_group_id;
        this.visibility = visibility;
    }

    public static void getDairyNameList(final Context mContext, String userID, final String className) {
        SessionManager sessionManager = new SessionManager(mContext);

        final ArrayList<DairyNamePojo> dairyNamePojos = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        dairyNamePojos.add(new DairyNamePojo(jsonObject1.getString("id"), jsonObject1.getString("name")
                                , jsonObject1.getString("dairy_name"), jsonObject1.getString("center_name"),
                                  jsonObject1.getString("unic_customer_for_mobile"), jsonObject1.getString("unic_customer")
                                , jsonObject1.getString("phone_number"), jsonObject1.getString("firebase_tocan")
                                , jsonObject1.getString("customer_id")
                                , jsonObject1.getString("user_group_id"), false));
                    }
                    if (!dairyNamePojos.isEmpty()) {
                        if (className.equals("CustomerDeshboard")) {
                            ((CustomerDeshBoardActivity) mContext).setDairyNameList(dairyNamePojos);
                        } else if (className.equals("CustomerDairyListWithBoxActivity")) {
                            ((CustomerDairyListWithBoxActivity) mContext).setDairyNameList(dairyNamePojos);
                        } else if (className.equals("BuyerCustomerDairyList")) {
                            ((CustomerBuyerDairyListActivity) mContext).setDairyNameList(dairyNamePojos);
                        } else {
                            ((CustomerDairyListActivity) mContext).setDairyNameList(dairyNamePojos);
                        }
                    }else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("userid", userID)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(getAllDairyNameAPI);

    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (visibility ? 0x01 : 0x00));
    }


}
