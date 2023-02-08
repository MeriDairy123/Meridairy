package b2infosoft.milkapp.com.sharedPreference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.DeliveryBoy.DeliveryBoyMainActivity;
import b2infosoft.milkapp.com.Model.BeanUserLoginAccount;
import b2infosoft.milkapp.com.Model.CowBuffaloSNFPojo;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.PinCodeActivity;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.CustomerBuyerDairyListActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerDeshBoardActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerUserGroupActivity;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.ShareAds_Animal.Animal_AdsActivity.mainCategoryList;
import static b2infosoft.milkapp.com.appglobal.Constant.BuyerFirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class SessionManager {
    public static final String user_token = "user-token",
            KEY_Notification = "notification_count", KEY_NotifType = "notification_type",
            Key_BuffaloList = "Buffalo_list", Key_CowList = "Cow_list",
            Key_RateChartList = "Rate_chart_list_data", Key_SNFList = "SNF_list",
            Key_ChartId = "SNF_Fat_chart_cat_id", Key_SNFListData = "SNF_listData",
            Key_FATListData = "FAT_listData", Key_all_user_group_id = "all_user_group_id",
            Key_unic_customer_for_mobile = "unic_customer_for_mobile", SessionLang = "lang",
            IS_LOGIN = "IsLoggedIn", Key_DevideFac = "devide_factor",
            Key_MultiFac = "multi_factor", Key_AddFac = "add_factor",
            Key_SellMilkPrice = "sale_milk_rate_for_fix",
            Key_BuyFatPrice = "buy_milk_fat_price", Key_BuyCowFatPrice = "buy_cow_fat_price", Key_BuyBuffFatPrice = "buy_buffalo_milk_fat_price",
            KeyBuyMilkRateType = "rate_type", KeyBuyFatType = "fat_type_buy",
            Key_SaleRateType = "sale_rate_type", Key_SaleFateType = "sale_fat_type",
            Key_SaleFatPrice = "sale_milk_fat_price", Key_SaleCowFatPrice = "sale_cow_fat_price", Key_SaleBuffaloFatPrice = "sale_buffalo_fat_price",
            KeyIsOnline = "is_online", Key_SendSmsSetting = "sms_setting_on_off", Key_PhoneSMSApp = "phone_sms_app",Key_skip_ad = "skip_ad",
            Key_BalancewWebSMS = "webSMsBalance", Key_smsAlwyasOn_ASk = "smsAlwyasOn_ASk", Key_SellerBhugtanSetting = "seller_bhugtan_setting", Key_FatYES = "fatkg_yes",
            Key_SNFYES = "snfkg_yes", Key_RateYES = "rate_yes", Key_BonusYES = "bonus_yes", Key_SendTotaLYES = "total_yes",
            Key_BuyMilkBonusRate = "buy_milk_bonus_rate", Key_SaleMilkBonusRate = "sale_milk_bonus_rate", Key_ChartType = "milk_chart_type", Key_PurchaseBy = "purchase_by",
            YES = "YES", NO = "NO", ZERO = "0", ONE = "1", TWO = "2", ONE_Weeek = "10 days", ONE_MONTH = "1 month",
            Key_BuyMilkScreen = "buy_milk_screen", Key_SaleMilkScreen = "sale_milk_screen",
            Key_AutoFatStatus = "auto_fat_status_selection", Key_CowMaxFat = "cow_max_fat", Key_BuffMinFat = "buff_min_fat",
            Key_PrintReciept = "PrintRecieptBluetooth",KEY_ReceiptTitleFontSize = "RecieptTitleFontSize",KEY_ReceiptMessageFontSize = "RecieptMessageFontSize",
            Key_PrintAllLang = "PrintAll_Lang", PinNumber = "pin", KEY_UserID = "userID", KEY_CustomerUserID = "customer_user_ID", KEY_PlantId = "plant_id", KEY_AdvtStatus = "advertisement_status", KEY_QRCode = "QRCode",
            KEY_center_name = "center_name", KEY_User_Type = "user_type", KEY_VehicleType = "vehicle_type",
            KEY_dairy_id = "dairy_id", KEY_dairy_name = "dairy_name",
            KEY_dairy_Mobile = "dairy_mobile", KEY_Mobile = "mob", KEY_Email = "pass",
            KEY_Adhar = "adhar", KEY_Address = "address", KEY_Name = "name", KEY_ProdCustomerList = "product_customer_list",
            KEY_ProCustomerSpinItem = "product_customer_list_spin",
            Key_SellerPdfFAT = "seller_fat", Key_SellerPdfSnf = "seller_snf", Key_SellerPdfclr = "seller_clr",
            Key_SellerPdfRate = "seller_rate", Key_SellerPdfFont = "seller_font_size", Key_SellerMilkWeekStatus = "seller_week_status",
            Key_deliveryboy_dairy="deliveryboy_dairy" ,

    Key_BuyerPdfFAT = "buyer_fat", Key_BuyerPdfSnf = "buyer_snf", Key_BuyerPdfclr = "buyer_clr", Key_BuyerPdfRate = "buyer_rate",
            Key_BuyerPdfFont = "buyer_font_size", Key_BuyerMilkWeekStatus = "buyer_week_status",

    KEY_FatherName = "f_name", Key_UserGroupID = "gID", Key_Status = "status",
            KEY_UserRegsDate = "user_regs_date", Key_pwd = "pwd", Key_RemainingDay = "remaining_day",
            KEY_Latitude = "latitude", KEY_Longitude = "longitude",
            Key_User_Status = "active_status", KEY_ProductAddDate = "product_add_date",
            KEY_MachineAuto = "machine_auto",KEY_WhatsAppMSG = "whatsapp_msg",KEY_MachineCode = "machine_code",KEY_MachineName = "machine_analyzer_name",
            Key_Start_Date = "start_date", Key_Expire_Date = "expire_date", KEY_BannerImagePath = "banner_image_path",
            KEY_BannerImageId = "banner_img_id", KEY_BannerImage = "banner_image", KEY_BannerCustomText = "custom_msg_banner",
            KEY_MIlkEntry_CustomerId = "KEY_MIlkEntry_CustomerId", KEY_MIlkEntry_CustomerName = "KEY_MIlkEntry_CustomerName",

    KEY_MIlkEntry_CustomerFatherName = "KEY_MIlkEntry_CustomerFatherName", KEY_MIlkEntry_UniqCustomer = "KEY_MIlkEntry_UniqCustomer",
            KEY_MIlkEntry_EntryType = "KEY_MIlkEntry_EntryType", KEY_MIlkEntry_EntryPrice = "KEY_MIlkEntry_EntryPrice",
            KEY_AnimalMainCategoryId = "main_id", KEY_AnimalSubCategoryId = "id", KEY_AnimalStateId = "animal_state_id", KEY_AnimalStateName = "animal_state_name";
    static final String MyPREFERENCES = "MeriDairyPref";
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    static Context mContext;
    private static SessionManager sessionManager;
    public DatabaseHandler db;

    public SessionManager(Context mContext) {
        SessionManager.mContext = mContext;
        db = DatabaseHandler.getDbHelper(mContext);
        sharedPreferences = mContext.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SessionManager getSessionManager(Context mContext) {
        SessionManager.mContext = mContext;
        if (sharedPreferences != null && editor != null) {
            editor = sharedPreferences.edit();
        } else {
            sessionManager = new SessionManager(mContext);
        }
        return sessionManager;

    }


    public void createLoginDairyJson(JSONObject jsonObject, String qrCode) {
        ArrayList loginUser = db.getLoginUserAccList();
        db.deleteDatabase(mContext);
        userLoginList(loginUser);
        editor.clear();
        FirstTime = "Yes";
        BuyerFirstTime = "";
        mainCategoryList = new ArrayList<>();
        try {
            editor.putBoolean(IS_LOGIN, true);
            editor.putString(KEY_UserID, jsonObject.getString("id"));
            editor.putString(KEY_Name, jsonObject.getString("name"));
            editor.putString(KEY_Email, jsonObject.getString("email"));
            editor.putString(KEY_Mobile, jsonObject.getString("phone_number"));
            editor.putString(KEY_dairy_name, jsonObject.getString("dairy_name"));
            editor.putString(KEY_FatherName, jsonObject.getString("father_name"));
            editor.putString(Key_UserGroupID, jsonObject.getString("user_group_id"));
            editor.putString(Key_all_user_group_id, jsonObject.getString("all_user_group_id"));
            editor.putString(KEY_center_name, jsonObject.getString("center_name"));
            editor.putString(KEY_User_Type, jsonObject.getString("type"));
            editor.putString(KEY_VehicleType, jsonObject.getString("vehicle_type"));
            editor.putString(KEY_PlantId, jsonObject.getString("plant_id"));
            editor.putString(KEY_AdvtStatus, jsonObject.getString(KEY_AdvtStatus));
            editor.putString(user_token, jsonObject.getString(user_token));
            editor.putString(KEY_QRCode, qrCode);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void updateLoginDairySession(String userID, String name, String email, String phone, String dairy_name,
                                        String father_name, String user_groupID, String center_name, String type,
                                        String vehicle_type, String plantId, String advrtisementStatus, String user_tokens, String qrCode) {
        ArrayList loginUser = db.getLoginUserAccList();
        db.deleteDatabase(mContext);
        userLoginList(loginUser);
        editor.clear();

        FirstTime = "Yes";
        BuyerFirstTime = "";
        mainCategoryList = new ArrayList<>();
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UserID, userID);
        editor.putString(KEY_Name, name);
        editor.putString(KEY_Email, email);
        editor.putString(KEY_Mobile, phone);
        editor.putString(KEY_dairy_name, dairy_name);
        editor.putString(KEY_FatherName, father_name);
        editor.putString(Key_UserGroupID, user_groupID);
        editor.putString(KEY_center_name, center_name);
        editor.putString(KEY_User_Type, type);
        editor.putString(KEY_VehicleType, vehicle_type);
        editor.putString(KEY_PlantId, plantId);
        editor.putString(KEY_AdvtStatus, advrtisementStatus);
        editor.putString(user_token, user_tokens);
        editor.putString(KEY_QRCode, qrCode);
        editor.apply();
    }


    public void createCustomerLoginSession(JSONObject jsonObject, String pwd, String qrCode) throws JSONException {
        ArrayList loginUser = db.getLoginUserAccList();
        db.deleteDatabase(mContext);
        userLoginList(loginUser);
        editor.clear();
        FirstTime = "Yes";
        BuyerFirstTime = "";
        mainCategoryList = new ArrayList<>();
        String all_user_group_id = jsonObject.getString("all_user_group_id");
        String userGroupId = jsonObject.getString("user_group_id");
        String dairyId = jsonObject.getString("dairy_id");


        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UserID, jsonObject.getString("id"));
        editor.putString(KEY_CustomerUserID, jsonObject.getString("id"));
        editor.putString(KEY_Name, jsonObject.getString("name"));
        editor.putString(KEY_FatherName, jsonObject.getString("father_name"));
        editor.putString(KEY_Mobile, jsonObject.getString("phone_number"));
        editor.putString(KEY_Address, jsonObject.getString("address"));
        editor.putString(Key_UserGroupID, userGroupId);

        editor.putString(Key_Status, jsonObject.getString("status"));
        editor.putString(Key_pwd, pwd);
        editor.putString(Key_Start_Date, jsonObject.getString("activate_start_date"));
        editor.putString(Key_all_user_group_id, all_user_group_id);
        editor.putString(Key_unic_customer_for_mobile, jsonObject.getString("unic_customer_for_mobile"));
        editor.putString(user_token, jsonObject.getString(user_token));
        editor.putString(KEY_QRCode, qrCode);
        editor.putString(KEY_dairy_id, dairyId);
        editor.putString(KEY_dairy_name, jsonObject.getString("dairy_name"));
        editor.putString(KEY_center_name, jsonObject.getString("center_name"));
        editor.putString(KEY_dairy_Mobile, jsonObject.getString("dairy_owner_phone_number"));

        List<String> userGroupIDList = Arrays.asList(all_user_group_id.split(","));
        System.out.println("userGroupIDList>>>" + all_user_group_id);
        showToast(mContext, mContext.getString(R.string.Login_Success));


        if (userGroupIDList.size() == 1) {
            if (userGroupIDList.get(0).equals("3")) {
                moveDeshboard(mContext, CustomerDeshBoardActivity.class);
            } else {
                moveDeshboard(mContext, CustomerBuyerDairyListActivity.class);
            }
            editor.apply();
        } else if (userGroupIDList.size() > 1) {
            if (all_user_group_id.contains("2")) {
                //   String mainId = jsonObject.getString("main_id");
                // editor.putString(KEY_UserID, mainId);
                editor.putString(Key_UserGroupID, "2");
                editor.apply();
                moveDeshboard(mContext, PinCodeActivity.class);
            } else {
                editor.apply();
                moveDeshboard(mContext, CustomerUserGroupActivity.class);
            }
        }


    }

    public void deliveryBoyLoginSession(JSONObject jsonObject, String pwd, String qrCode) throws JSONException {

        db.deleteDatabase(mContext);
        editor.clear();
        FirstTime = "Yes";
        BuyerFirstTime = "";
        mainCategoryList = new ArrayList<>();

        String all_user_group_id = jsonObject.getString("all_user_group_id");
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_UserID, jsonObject.getString("id"));
        editor.putString(KEY_UserID, jsonObject.getString("id"));
        editor.putString(KEY_Name, jsonObject.getString("name"));
        editor.putString(KEY_Email, jsonObject.getString("email"));
        editor.putString(KEY_FatherName, jsonObject.getString("father_name"));
        editor.putString(KEY_Mobile, jsonObject.getString("phone_number"));
        editor.putString(KEY_Address, jsonObject.getString("address"));
        editor.putString(KEY_dairy_id, jsonObject.getString("dairy_id"));
        editor.putString(KEY_dairy_name, jsonObject.getString("dairy_name"));
        editor.putString(KEY_center_name, jsonObject.getString("center_name"));
        editor.putString(Key_UserGroupID, jsonObject.getString("user_group_id"));
        editor.putString(Key_Status, jsonObject.getString("status"));
        editor.putString(Key_pwd, pwd);
        editor.putString(Key_all_user_group_id, all_user_group_id);
        editor.putString(Key_unic_customer_for_mobile, jsonObject.getString("unic_customer_for_mobile"));
        editor.putString(user_token, jsonObject.getString(user_token));
        editor.putString(KEY_QRCode, qrCode);
        editor.apply();
        showToast(mContext, mContext.getString(R.string.Login_Success));
        moveDeshboard(mContext, DeliveryBoyMainActivity.class);


    }

    private void userLoginList(ArrayList<BeanUserLoginAccount> loginUser) {

        if (!loginUser.isEmpty()) {
            for (int i = 0; i < loginUser.size(); i++) {
                db.addLoginAccount(loginUser.get(i).user_id, loginUser.get(i).name, loginUser.get(i).phone_number,
                        loginUser.get(i).user_group_id, loginUser.get(i).user_tokens);
            }

        }
    }

    public void moveDeshboard(Context context, Class className) {

        Intent intent = new Intent(context, className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    public String getValueSesion(String key) {
        String value = sharedPreferences.getString(key, "");
        value = nullCheckFunction(value);

        return value;
    }

    public void setValueSession(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public void setIntValueSession(String key, Integer value) {
        editor.putInt(key, value);
        editor.commit();
    }


    public Integer getIntValueSesion(String key) {
        return sharedPreferences.getInt(key, 0);

    }public void setBooleanValue(String key, Boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }


    public Boolean getBooleanValue(String key) {
        return  sharedPreferences.getBoolean(key, false);

    }

    public void setFloatValueSession(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public Float getFloatValueSession(String key) {
        float value = sharedPreferences.getFloat(key, 0);
        return value;
    }

    public void removeValue(String key) {
        editor.remove(key).apply();

    }

    public void clearUserPreference(Context mContext) {

        SessionManager.mContext = mContext;
        editor.clear();
        editor.commit();
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        FirstTime = "Yes";
        BuyerFirstTime = "";

        mainCategoryList = new ArrayList<>();
        editor.clear();
        editor.commit();
        db.deleteDatabase(mContext);

    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public void setFATSNFListData(Context context, String key, ArrayList<String> snfList) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(snfList);
        editor.putString(key, jsonCartList);
        editor.apply();
    }

    public ArrayList<String> getFATSNFListData(Context context, String key) {
        ArrayList<String> list = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(key)) {
            String jsonFavorites = sharedPreferences.getString(key, "");
            Gson gson = new Gson();
            list = gson.fromJson(jsonFavorites, new TypeToken<List<String>>() {
            }.getType());

        }
        return list;
    }

    public void saveRateChartList(Context context, ArrayList<SnfFatListPojo> snfFatListPojos) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(snfFatListPojos);
        editor.putString(Key_RateChartList, jsonCartList);
        editor.apply();
    }

    public ArrayList<SnfFatListPojo> getRateChartList(Context context) {
        List<SnfFatListPojo> mList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Key_RateChartList)) {
            String jsonRateList = sharedPreferences.getString(Key_RateChartList, "");
            Gson gson = new Gson();
            mList = gson.fromJson(jsonRateList, new TypeToken<List<SnfFatListPojo>>() {
            }.getType());
            System.out.println("SnfFatListPojoList=>>>" + mList.size());
        }
        return (ArrayList<SnfFatListPojo>) mList;
    }

    public void saveSNFList(Context context, ArrayList<CowBuffaloSNFPojo> snfList) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(snfList);
        editor.putString(Key_SNFList, jsonCartList);
        editor.apply();
    }

    public void saveBuffaloList(Context context, ArrayList<SnfFatListPojo> buffaloList) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(buffaloList);
        editor.putString(Key_BuffaloList, jsonCartList);
        editor.apply();
    }

    public void saveCowList(Context context, ArrayList<SnfFatListPojo> cowList) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonCartList = gson.toJson(cowList);
        editor.putString(Key_CowList, jsonCartList);
        editor.apply();
    }

    public void clearArrayList(Context context, String type) {
        if (type.equals("buffalo")) {
            editor.remove(Key_BuffaloList).commit();
        }
        if (type.equals("snf")) {
            editor.remove(Key_SNFList).commit();
        } else {
            editor.remove(Key_CowList).commit();

        }
    }


    public void checkDairyLoginAccount(JSONObject jsonObject) {


        String user_group_id = "";
        try {
            user_group_id = jsonObject.getString("user_group_id");

            // check already Login  user account
            List<BeanUserLoginAccount> beanUserLoginAccountList = db.getLoginUserAccList();
            boolean loginYes = false;
            if (!beanUserLoginAccountList.isEmpty()) {

                for (int i = 0; i < beanUserLoginAccountList.size(); i++) {
                    if (beanUserLoginAccountList.get(i).user_id.equalsIgnoreCase(UserID)) {
                        loginYes = true;
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.user_already_login_in_this_app_message));
                        ((Activity) mContext).finish();
                    }
                }
            }
            if (!loginYes) {
                db.addLoginAccount(UserID, jsonObject.getString("name"), jsonObject.getString("phone_number"),
                        user_group_id, jsonObject.getString(user_token));
                UtilityMethod.showToastIntent(mContext, mContext.getString(R.string.Please_Generate_PIN_Number), PinCodeActivity.class);
            } else if (user_group_id.equals("3") || user_group_id.equals("4")) {
                createCustomerLoginSession(jsonObject, "11111", "yes");

            } else if (user_group_id.equals("5")) {
                deliveryBoyLoginSession(jsonObject, "11111", "yes");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
