package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getCustomerListAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_id;

/**
 * Created by B2infosoft on 8/3/2017.
 */

public class CustomerListPojo {


    public String id = "", user_group_id = "", categorychart_id = "", unic_customer_for_mobile = "", unic_customer = "",
            name = "", father_name = "", phone_number = "", adhar = "",
            village = "", address = "", amount = "", entry_type = "",
            entry_price = "", accountNo = "", iFSC_Code = "", bankName = "", firebase_tocan = "";

    public CustomerListPojo(String id, String user_group_id, String categorychart_id, String unic_customer_for_mobile,
                            String unic_customer, String name, String father_name, String phone_number, String adhar,
                            String village, String address, String amount, String entry_type, String entry_price,
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
        this.amount = amount;
        this.entry_type = entry_type;
        this.entry_price = entry_price;
        this.accountNo = accountNo;
        this.iFSC_Code = iFSC_Code;
        this.bankName = bankName;
        this.firebase_tocan = firebase_tocan;
    }

    public static void addCustomerListInDatabase(Context mContext, boolean dialogYesNo) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Loading Customer List...", dialogYesNo) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            if (databaseHandler.getCustomerList().size() != 0) {
                                databaseHandler.deleteCustomer();
                            }
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject obj = mainJsonArray.getJSONObject(i);
                                databaseHandler.addCustomer(obj.getString("Userid"),

                                        obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                        obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                        obj.getString("name"), obj.getString("father_name"), obj.getString("phone_number"),
                                        obj.getString("adhar"), obj.getString("village"), obj.getString("address"),
                                        obj.getString("remaing_amount"), obj.getString("entry_type"), obj.getString("entry_price"),
                                        obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"), obj.getString("firebase_tocan"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_group_id", "3")
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        caller.addRequestBody(body);

        caller.execute(getCustomerListAPI);
    }

    public static void addCustomerInDbfromDeliveryboy(Context mContext, boolean dialogYesNo) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Loading Customer List...", dialogYesNo) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            if (databaseHandler.getCustomerList().size() != 0) {
                                databaseHandler.deleteCustomer();
                            }
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject obj = mainJsonArray.getJSONObject(i);
                                databaseHandler.addCustomer(obj.getString("Userid"),

                                        obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                        obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                        obj.getString("name"), obj.getString("father_name"), obj.getString("phone_number"),
                                        obj.getString("adhar"), obj.getString("village"), obj.getString("address"),
                                        obj.getString("remaing_amount"), obj.getString("entry_type"), obj.getString("entry_price"),
                                        obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"), obj.getString("firebase_tocan"));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_group_id", "3")
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_dairy_id))
                .build();
        caller.addRequestBody(body);

        caller.execute(getCustomerListAPI);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }

    public String getCategorychart_id() {
        return categorychart_id;
    }

    public void setCategorychart_id(String categorychart_id) {
        this.categorychart_id = categorychart_id;
    }

    public String getUnic_customer_for_mobile() {
        return unic_customer_for_mobile;
    }

    public void setUnic_customer_for_mobile(String unic_customer_for_mobile) {
        this.unic_customer_for_mobile = unic_customer_for_mobile;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFather_name() {
        return father_name;
    }

    public void setFather_name(String father_name) {
        this.father_name = father_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAdhar() {
        return adhar;
    }

    public void setAdhar(String adhar) {
        this.adhar = adhar;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEntry_type() {
        return entry_type;
    }

    public void setEntry_type(String entry_type) {
        this.entry_type = entry_type;
    }

    public String getEntry_price() {
        return entry_price;
    }

    public void setEntry_price(String entry_price) {
        this.entry_price = entry_price;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getiFSC_Code() {
        return iFSC_Code;
    }

    public void setiFSC_Code(String iFSC_Code) {
        this.iFSC_Code = iFSC_Code;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getFirebase_tocan() {
        return firebase_tocan;
    }

    public void setFirebase_tocan(String firebase_tocan) {
        this.firebase_tocan = firebase_tocan;
    }
}
