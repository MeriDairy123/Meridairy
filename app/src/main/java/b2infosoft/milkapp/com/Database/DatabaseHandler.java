package b2infosoft.milkapp.com.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.apache.commons.lang3.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import b2infosoft.milkapp.com.Model.BeanCartItem;
import b2infosoft.milkapp.com.Model.BeanDairySnfFatChart;
import b2infosoft.milkapp.com.Model.BeanOfferBanerList;
import b2infosoft.milkapp.com.Model.BeanUserLoginAccount;
import b2infosoft.milkapp.com.Model.BuyerMilkCustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.Model.MilkHistoryPojo;
import b2infosoft.milkapp.com.Model.MilkRatePojo;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.Notification.BeanNotification_Item;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanBuyerCartItem;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_ProductAddDate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RateChartList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.strDateToTimeStampDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.strDateToTimeStrempDDMMMYY;


/**
 * Created by Microsoft on 06-Jul-17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // Database Name
    private static final String DATABASE_NAME = "MilkDairyDB";
    private static final int DATABASE_VERSION = 11;

    //Table name
    private static final String Table_Customer = "tb_customer", Table_Rate = "rate", Table_Notification = "table_notification", Table_UserLoginAccount = "userlogin_account", Table_MilkBuyEntry = "milk_buy_entry", Table_MilkSaleEntry = "milk_sale_entry", Table_PlantMilkBuyEntry = "plant_milk_buy_entry", Table_PlantMilkSaleEntry = "plant_milk_sale_entry", Table_BuyerCustomer = "buyer_customer", Table_Cart = "tble_cart", Table_BannerOffer = "tble_banner_offer", Table_BuyerCart = "table_buyer_cart", Table_BuyMilkFATSNFChart = "table_dairy_all_chart_data", Table_SaleMilkFATSNFChart = "table_sale_milk_chart_data";


    // Add Table Columns names

    private static final String KEY_ProdId = "prod_id", KEY_Title = "title", KEY_MinQty = "min_qty", KEY_Qty = "qty", KEY_ItemQnt = "item_qnt", KEY_Mrp = "mrp", KEY_Price = "price", KEY_GST = "gst", KEY_TotalPrice = "total_price", KEY_Type = "product_type", KEY_Image = "image_url", KEY_Thumb = "thumb_url", KEY_Status = "status", KEY_Description = "description";

    private static final String KEY_ID = "id", KEY_LiveId = "entry_Online_id", KEY_DeliveryBoyId = "delivery_boy_id", KEY_Name = "name", KEY_Fname = "fname", KEY_Mobile = "mobile", KEY_AdhaarNo = "aadhar_no", KEY_Address = "address", KEY_Village = "village", KEY_AccountNo = "account_no", KEY_IFSC = "ifsc_code", KEY_BankName = "bank_name", KEY_LiveStatus = "live_status", KEY_unic_customer = "unic_customer", KEY_firebase_tocan = "firebase_tocan", KEY_Category_Chart_Id = "categorychart_id", KEY_user_token = "user_token", KEY_Amount = "amount", KEY_DairyID = "dairy_id", KEY_entry_type = "entry_type", KEY_UserGroupId = "user_group_id", KEY_unic_customer_for_mobile = "unic_customer_for_mobile", KEY_str_to_time = "str_to_time", KEY_date_d = "d", KEY_month_m = "m", KEY_year_y = "y", KEY_SNF = "snf", KEY_Fat = "fat", KEY_CLR = "clr", KEY_SNF_FAT_Category = "snf_fat_category", //Rate Table Column names
            KEY_MilkEntry_UnicId="milk_entry_unic_id",KEY_Rate = "rate", KEY_EntryID = "entry_id", KEY_CustomerID = "customer_id", KEY_Session = "session", KEY_Weight = "weight", KEY_TotalBonus = "total_bonus", KEY_Total = "total", KEY_Date = "date", KEY_milkRateCategory = "milk_rate_buy_category", KEY_FATSNFCategory = "fat_snf_category_milk_type", KEY_StatusON_OffLine = "online_offline", KeyMorning_milk = "morning_milk", KeyEvening_milk = "evening_milk", BuyerIs_active = "is_active", BuyerPrice_per_ltr = "price_per_ltr";
    private static DatabaseHandler instance;
    Context mContext;
    SessionManager sessionManager;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

    }

    public static synchronized DatabaseHandler getDbHelper(Context context) {
        if (instance == null) instance = new DatabaseHandler(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        // +++++++++ Login User Account Table +++++++++++++++++++++++++++++++++++++
        String createUserLoginAccTable = "CREATE TABLE " + Table_UserLoginAccount + "(" + KEY_ID + " TEXT ," + KEY_Name + " TEXT," + KEY_Mobile + " TEXT," + KEY_user_token + " TEXT," + KEY_UserGroupId + " TEXT" + ")";

        // ++++++++++++ Seller Customer Table +++++++++++++++++++++++++++++++++++++
        String createAddCustomerTable = "CREATE TABLE " + Table_Customer + "(" + KEY_ID + " TEXT ," + KEY_UserGroupId + " TEXT," + KEY_Category_Chart_Id + " TEXT," + KEY_unic_customer_for_mobile + " TEXT," + KEY_unic_customer + " TEXT," + KEY_Name + " TEXT," + KEY_Fname + " TEXT," + KEY_Mobile + " TEXT," + KEY_AdhaarNo + " TEXT," + KEY_Village + " TEXT," + KEY_Address + " TEXT," + KEY_Amount + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_Rate + " TEXT," + KEY_AccountNo + " TEXT," + KEY_IFSC + " TEXT," + KEY_BankName + " TEXT," + KEY_firebase_tocan + " TEXT" + ")";

        // +++++++++ Buyer Customer Table +++++++++++++++++++++++++++++++++++++
        String createBuyerCustomerTable = "CREATE TABLE " + Table_BuyerCustomer + "(" + KEY_ID + " TEXT ," + KEY_UserGroupId + " TEXT," + KEY_Category_Chart_Id + " TEXT," + KEY_unic_customer_for_mobile + " TEXT," + KEY_unic_customer + " TEXT," + BuyerIs_active + " INTEGER," + KEY_Name + " TEXT," + KEY_Fname + " TEXT," + KEY_Mobile + " TEXT," + KEY_AdhaarNo + " TEXT," + KEY_Village + " TEXT," + KEY_Address + " TEXT," + KeyMorning_milk + " TEXT," + KeyEvening_milk + " TEXT," + BuyerPrice_per_ltr + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_Rate + " TEXT," + KEY_AccountNo + " TEXT," + KEY_IFSC + " TEXT," + KEY_BankName + " TEXT," + KEY_firebase_tocan + " TEXT" + ")";


        // +++++++++ Buyer Customer Table +++++++++++++++++++++++++++++++++++++
        String createBuyssserCustomerTable = "CREATE TABLE " + Table_BuyerCustomer + "(" + KEY_ID + " INTEGER," + KEY_Name + " INTEGER," + KEY_Mobile + " TEXT," + BuyerIs_active + " INTEGER," + KEY_unic_customer + " TEXT," + KeyMorning_milk + " TEXT," + KeyEvening_milk + " TEXT," + BuyerPrice_per_ltr + KEY_milkRateCategory + " TEXT," + KEY_Rate + " TEXT," + KEY_AccountNo + " TEXT," + KEY_IFSC + " TEXT," + KEY_BankName + " TEXT," + KEY_UserGroupId + " TEXT," + KEY_Fname + " TEXT," + KEY_AdhaarNo + " TEXT," + KEY_Address + " TEXT," + KEY_Village + " TEXT," + KEY_unic_customer_for_mobile + " TEXT," + KEY_Category_Chart_Id + " TEXT," + KEY_firebase_tocan + " TEXT" + ")";


        String createRateTable = "CREATE TABLE " + Table_Rate + "(" + KEY_DairyID + " INTEGER," + KEY_Rate + " TEXT," + KEY_LiveStatus + " TEXT" + ")";
        // ++++++++++++== Notification Table== +++++++++++++++++++++++++++++++++++++
        String createNotificationTable = "CREATE TABLE " + Table_Notification + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_Title + " TEXT," + KEY_Type + " TEXT," + KEY_Description + " TEXT" + ")";

        //++++++++++++++++= Buy Milk Table  ===+++++++++++++++++++++++++++++++
        String createBuyMilkEntryTable = "CREATE TABLE " + Table_MilkBuyEntry + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LiveId + " TEXT," + KEY_CustomerID + " TEXT," + KEY_DairyID + " TEXT," + KEY_Name + " TEXT," + KEY_unic_customer + " TEXT," + KEY_Session + " TEXT," + KEY_Date + " TEXT," + KEY_Fat + " TEXT," + KEY_SNF + " TEXT," + KEY_CLR + " TEXT," + KEY_Rate + " TEXT," + KEY_Weight + " TEXT," + KEY_TotalBonus + " TEXT," + KEY_Total + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_FATSNFCategory + " INTEGER," + KEY_str_to_time + " TEXT," + KEY_date_d + " TEXT," + KEY_month_m + " TEXT," + KEY_year_y + " TEXT," + KEY_StatusON_OffLine + " INTEGER,"+KEY_MilkEntry_UnicId + " TEXT " + ")";

        //+++++++++++++====  Sale Milk Table ====+++++++++++++++++++++++++++++++++++++

        String createMilSaleEntryTable = "CREATE TABLE " + Table_MilkSaleEntry + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LiveId + " INTEGER," + KEY_DeliveryBoyId + " INTEGER," + KEY_CustomerID + " TEXT," + KEY_DairyID + " TEXT," + KEY_Name + " TEXT," + KEY_unic_customer + " TEXT," + KEY_Session + " TEXT," + KEY_Date + " TEXT," + KEY_Fat + " TEXT," + KEY_SNF + " TEXT," + KEY_CLR + " TEXT," + KEY_Weight + " TEXT," + KEY_TotalBonus + " TEXT," + KEY_Total + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_Rate + " TEXT," + KEY_FATSNFCategory + " INTEGER," + KEY_str_to_time + " TEXT," + KEY_date_d + " TEXT," + KEY_month_m + " TEXT," + KEY_year_y + " TEXT," + KEY_StatusON_OffLine + " INTEGER" + ")";

        //++++++++++++++++= Plant Milk Table  ===+++++++++++++++++++++++++++++++
        String createPlantMilkEntryTable = "CREATE TABLE " + Table_PlantMilkBuyEntry + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LiveId + " INTEGER," + KEY_CustomerID + " TEXT," + KEY_DairyID + " TEXT," + KEY_Name + " TEXT," + KEY_unic_customer + " TEXT," + KEY_Session + " TEXT," + KEY_Date + " TEXT," + KEY_Fat + " TEXT," + KEY_SNF + " TEXT," + KEY_CLR + " TEXT," + KEY_Weight + " TEXT," + KEY_Rate + " TEXT," + KEY_TotalBonus + " TEXT," + KEY_Total + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_FATSNFCategory + " INTEGER," + KEY_str_to_time + " TEXT," + KEY_date_d + " TEXT," + KEY_month_m + " TEXT," + KEY_year_y + " TEXT," + KEY_StatusON_OffLine + " INTEGER" + ")";

        //+++++++++++++==== Plant Sale Milk Table ====+++++++++++++++++++++++++++++++++++++
        String createPlantMilSaleEntryTable = "CREATE TABLE " + Table_PlantMilkSaleEntry + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_LiveId + " INTEGER," + KEY_DeliveryBoyId + " INTEGER," + KEY_CustomerID + " INTEGER," + KEY_DairyID + " INTEGER," + KEY_Session + " TEXT," + KEY_Fat + " TEXT," + KEY_Weight + " TEXT," + KEY_TotalBonus + " TEXT," + KEY_Total + " INTEGER," + KEY_Date + " TEXT," + KEY_milkRateCategory + " TEXT," + KEY_Rate + " TEXT," + KEY_SNF + " TEXT," + KEY_CLR + " TEXT," + KEY_Name + " TEXT," + KEY_unic_customer + " TEXT," + KEY_str_to_time + " TEXT," + KEY_FATSNFCategory + " INTEGER," + KEY_StatusON_OffLine + " INTEGER," + KEY_date_d + " TEXT," + KEY_month_m + " TEXT," + KEY_year_y + " TEXT" + ")";

        //+++++++++++++==== Cart Table ====+++++++++++++++++++++++++++++++++++++
        String createCartTable = "CREATE TABLE " + Table_Cart + "(" + KEY_ID + " INTEGER ," + KEY_MinQty + " INTEGER," + KEY_Qty + " INTEGER," + KEY_ItemQnt + " INTEGER," + KEY_Mrp + " REAL," + KEY_Price + " REAL," + KEY_GST + " REAL," + KEY_TotalPrice + " REAL," + KEY_Title + " TEXT," + KEY_Type + " TEXT," + KEY_Image + " TEXT," + KEY_Thumb + " TEXT," + KEY_Status + " TEXT," + KEY_Description + " TEXT" + ")";

        //+++++++++++++==== Banner Table ====+++++++++++++++++++++++++++++++++++++
        String createBannerTable = "CREATE TABLE " + Table_BannerOffer + "(" + KEY_ID + " INTEGER," + KEY_ProdId + " INTEGER," + KEY_Price + " REAL," + KEY_Thumb + " TEXT," + KEY_Image + " TEXT," + KEY_Title + " TEXT," + KEY_Description + " TEXT," + KEY_Type + " TEXT" + ")";
        //+++++++++++++==== Buyer Cart Table ====+++++++++++++++++++++++++++++++++++++
        String createBuyerCartTable = "CREATE TABLE " + Table_BuyerCart + "(" + KEY_ID + " TEXT," + KEY_Title + " TEXT," + KEY_Weight + " TEXT," + KEY_Image + " TEXT," + KEY_Price + " REAL," + KEY_TotalPrice + " REAL," + KEY_Qty + " INTEGER," + KEY_ItemQnt + " INTEGER," + KEY_ProductAddDate + " TEXT" + ")";
        //+++++++++++++==== Saller FAt SNF Chart Table ====+++++++++++++++++++++++++++++++++++++
        String createBuyMilkFATSNFChartTable = "CREATE TABLE " + Table_BuyMilkFATSNFChart + "(" + KEY_ID + " TEXT," + KEY_SNF + " TEXT," + KEY_Fat + " TEXT," + KEY_Rate + " TEXT," + KEY_SNF_FAT_Category + " TEXT," + KEY_Category_Chart_Id + " TEXT" + ")";
        //+++++++++++++==== Buyer FAT SNF Chart ====+++++++++++++++++++++++++++++++++++++
        String createSaleMilkFATSNFAllChartTable = "CREATE TABLE " + Table_SaleMilkFATSNFChart + "(" + KEY_ID + " TEXT," + KEY_SNF + " TEXT," + KEY_Fat + " TEXT," + KEY_Rate + " TEXT," + KEY_SNF_FAT_Category + " TEXT," + KEY_Category_Chart_Id + " TEXT" + ")";
        //+++++++++++++==== 10 days table ====+++++++++++++++++++++++++++++++++++++

        db.execSQL(createNotificationTable);
        db.execSQL(createUserLoginAccTable);
        db.execSQL(createAddCustomerTable);
        db.execSQL(createRateTable);
        db.execSQL(createBuyMilkEntryTable);
        db.execSQL(createMilSaleEntryTable);
        db.execSQL(createPlantMilkEntryTable);
        db.execSQL(createPlantMilSaleEntryTable);
        db.execSQL(createBuyerCustomerTable);
        db.execSQL(createCartTable);
        db.execSQL(createBannerTable);
        db.execSQL(createBuyerCartTable);
        db.execSQL(createBuyMilkFATSNFChartTable);
        db.execSQL(createSaleMilkFATSNFAllChartTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Table_UserLoginAccount);
        db.execSQL("DROP TABLE IF EXISTS " + Table_Customer);
        db.execSQL("DROP TABLE IF EXISTS " + Table_Rate);
        db.execSQL("DROP TABLE IF EXISTS " + Table_Notification);
        db.execSQL("DROP TABLE IF EXISTS " + Table_MilkBuyEntry);
        db.execSQL("DROP TABLE IF EXISTS " + Table_MilkSaleEntry);
        db.execSQL("DROP TABLE IF EXISTS " + Table_PlantMilkBuyEntry);
        db.execSQL("DROP TABLE IF EXISTS " + Table_PlantMilkSaleEntry);
        db.execSQL("DROP TABLE IF EXISTS " + Table_BuyerCustomer);
        db.execSQL("DROP TABLE IF EXISTS " + Table_Cart);
        db.execSQL("DROP TABLE IF EXISTS " + Table_BannerOffer);
        db.execSQL("DROP TABLE IF EXISTS " + Table_BuyerCart);
        db.execSQL("DROP TABLE IF EXISTS " + Table_BuyMilkFATSNFChart);
        db.execSQL("DROP TABLE IF EXISTS " + Table_SaleMilkFATSNFChart);
        onCreate(db);

    }


    public void addLoginAccount(String userid, String name, String phNo, String user_group_id, String userToken) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, userid);
        values.put(KEY_Name, name);
        values.put(KEY_Mobile, phNo);
        values.put(KEY_UserGroupId, user_group_id);
        values.put(KEY_user_token, userToken);
        // Inserting Row
        db.insert(Table_UserLoginAccount, null, values);
        db.close(); // Closing database connection
    }

    //Getting All loginAPI User
    public ArrayList<BeanUserLoginAccount> getLoginUserAccList() {
        ArrayList<BeanUserLoginAccount> mList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_UserLoginAccount;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int NoOfRows = (int) DatabaseUtils.queryNumEntries(db, Table_UserLoginAccount);
        if (NoOfRows > 0) {
            if (cursor.moveToFirst()) {
                do {
                    mList.add(new BeanUserLoginAccount(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_user_token))));
                } while (cursor.moveToNext());
            }
        }
        db.close();

        return mList;
    }


    //----------------------------Add Notification-------------------------

    public void addNotification(String title, String type, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_Title, title);
        values.put(KEY_Type, type);
        values.put(KEY_Description, description);
        // Inserting Row
        db.insert(Table_Notification, null, values);
        db.close(); // Closing database connection
    }

    //----------------------------Getting Notification-------------------------
    public ArrayList<BeanNotification_Item> getNotificationList() {
        ArrayList<BeanNotification_Item> mList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_Notification;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                mList.add(new BeanNotification_Item(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_Title)), cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_Type)), cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHandler.KEY_Description))));
            } while (cursor.moveToNext());

        }
        db.close();
        return mList;
    }

    public void deleteNotification(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseHandler.Table_Notification, DatabaseHandler.KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteNotificationTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_Notification);
        db.close();
    }

    // ---------------- Customer -----------------------------
    public void addCustomer(String id, String user_group_id, String categorychart_id, String unic_customer_for_mobile, String unic_customer, String name, String father_name, String phone_number, String adhar, String village, String address, String amount, String entry_type, String entry_price, String accountNo, String iFSC_Code, String bankName, String firebase_tocan) {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_UserGroupId, user_group_id);
        values.put(KEY_Category_Chart_Id, categorychart_id);
        values.put(KEY_unic_customer_for_mobile, unic_customer_for_mobile);
        values.put(KEY_unic_customer, unic_customer);
        values.put(KEY_Name, name);
        values.put(KEY_Fname, father_name);
        values.put(KEY_Mobile, phone_number);
        values.put(KEY_AdhaarNo, adhar);
        values.put(KEY_Village, village);
        values.put(KEY_Address, address);
        values.put(KEY_Amount, amount);
        values.put(KEY_milkRateCategory, entry_type);
        values.put(KEY_Rate, entry_price);
        values.put(KEY_AccountNo, accountNo);
        values.put(KEY_IFSC, iFSC_Code);
        values.put(KEY_BankName, bankName);
        values.put(KEY_firebase_tocan, firebase_tocan);

        // Inserting Row
        db.insert(Table_Customer, null, values);
        db.close(); // Closing database connection
    }

    public void updateCustomer(String customerID, String name, String fName, String phNo, String address, String aadhar) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Name, name);
        values.put(KEY_Fname, fName);
        values.put(KEY_Address, address);
        values.put(KEY_Mobile, phNo);
        values.put(KEY_AdhaarNo, aadhar);
        values.put(KEY_LiveStatus, 0);
        db.update(Table_Customer, values, "id = '" + customerID + "'", null);
        db.close();
    }

    //Getting All Customer
    public ArrayList<CustomerListPojo> getCustomerListByGroupId(String userGroupId) {
        ArrayList<CustomerListPojo> mList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_Customer + " WHERE " + KEY_UserGroupId + " = " + userGroupId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                mList.add(new CustomerListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer_for_mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fname)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AdhaarNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Village)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Address)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Amount)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AccountNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_IFSC)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_BankName)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_firebase_tocan))


                ));
            } while (cursor.moveToNext());
        }

        db.close();
        return mList;
    }

    public ArrayList<CustomerListPojo> getCustomerList() {
        ArrayList<CustomerListPojo> mList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_Customer;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {


                mList.add(new CustomerListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer_for_mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fname)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AdhaarNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Village)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Address)),

                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_Amount)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AccountNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_IFSC)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_BankName)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_firebase_tocan))


                ));

            } while (cursor.moveToNext());
        }

        db.close();
        return mList;
    }

    public CustomerListPojo getCustomerDetails(String customerId) {
        CustomerListPojo customerListPojo = customerListPojo = new CustomerListPojo("", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        String selectQuery = "SELECT  * FROM " + Table_Customer + " WHERE " + KEY_ID + " = " + customerId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                customerListPojo = new CustomerListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer_for_mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fname)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AdhaarNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Village)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Address)),

                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_Amount)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AccountNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_IFSC)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_BankName)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_firebase_tocan)));


            } while (cursor.moveToNext());
        }

        db.close();
        return customerListPojo;
    }

    public void deleteUserLoginId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DatabaseHandler.Table_UserLoginAccount, DatabaseHandler.KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteParticularCustomer(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(DatabaseHandler.Table_Customer, DatabaseHandler.KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteCustomer() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_Customer);
        db.close();
    }

    public void addBuyerCustomer(String id, String user_group_id, String categorychart_id, String unic_customer_for_mobile, String unic_customer, String is_active, String name, String father_name, String phone_number, String adhar, String village, String address, String morning_milk, String evening_milk, String price_per_ltr, String entry_type, String entry_price, String accountNo, String iFSC_Code, String bankName, String firebase_tocan) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_UserGroupId, user_group_id);
        values.put(KEY_Category_Chart_Id, categorychart_id);
        values.put(KEY_unic_customer_for_mobile, unic_customer_for_mobile);
        values.put(KEY_unic_customer, unic_customer);
        values.put(BuyerIs_active, is_active);
        values.put(KEY_Name, name);
        values.put(KEY_Fname, father_name);
        values.put(KEY_Mobile, phone_number);
        values.put(KEY_AdhaarNo, adhar);
        values.put(KEY_Village, village);
        values.put(KEY_Address, address);
        values.put(KeyMorning_milk, morning_milk);
        values.put(KeyEvening_milk, evening_milk);
        values.put(BuyerPrice_per_ltr, price_per_ltr);
        values.put(KEY_milkRateCategory, entry_type);
        values.put(KEY_Rate, entry_price);
        values.put(KEY_AccountNo, accountNo);
        values.put(KEY_IFSC, iFSC_Code);
        values.put(KEY_BankName, bankName);
        values.put(KEY_firebase_tocan, firebase_tocan);
        // Inserting Row
        db.insert(Table_BuyerCustomer, null, values);
        db.close(); // Closing database connection


    }


    public void deleteBuyerCustomer() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_BuyerCustomer);
        db.close();
    }

    public ArrayList<BuyerMilkCustomerListPojo> getBuyerListByGroupId(String userGroupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BuyerMilkCustomerListPojo> mBuyerList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_BuyerCustomer + " WHERE " + KEY_UserGroupId + " = " + userGroupId;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                mBuyerList.add(new BuyerMilkCustomerListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer_for_mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(BuyerIs_active)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fname)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AdhaarNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Village)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Address)), cursor.getString(cursor.getColumnIndexOrThrow(KeyMorning_milk)), cursor.getString(cursor.getColumnIndexOrThrow(KeyEvening_milk)), cursor.getString(cursor.getColumnIndexOrThrow(BuyerPrice_per_ltr)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AccountNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_IFSC)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_BankName)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_firebase_tocan))));
            } while (cursor.moveToNext());
        }
        db.close();

        return mBuyerList;
    }

    public ArrayList<BuyerMilkCustomerListPojo> getBuyerList() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<BuyerMilkCustomerListPojo> mBuyerList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_BuyerCustomer;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                mBuyerList.add(new BuyerMilkCustomerListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_UserGroupId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer_for_mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(BuyerIs_active)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fname)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Mobile)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AdhaarNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Village)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Address)), cursor.getString(cursor.getColumnIndexOrThrow(KeyMorning_milk)), cursor.getString(cursor.getColumnIndexOrThrow(KeyEvening_milk)), cursor.getString(cursor.getColumnIndexOrThrow(BuyerPrice_per_ltr)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_AccountNo)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_IFSC)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_BankName)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_firebase_tocan))));

            } while (cursor.moveToNext());
        }
        db.close();
        return mBuyerList;
    }

    // Buy Milk Entry

    public void addSingleBuyMilkEntry(int liveId, String dairyID, String customerID, String name, String unic_id, String session, String date, String fat, String snf, String clr, String entry_price, String weight, String totalBonus, String total, String milkRateCategory, Integer fatSnfCategory, Integer entryStatus,String MilkEntryUnicId) {

        String strToTime = strDateToTimeStrempDDMMMYY(date);


        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LiveId, liveId);
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_str_to_time, strToTime);
        contentValues.put(KEY_milkRateCategory, milkRateCategory);
        contentValues.put(KEY_FATSNFCategory, fatSnfCategory);
        contentValues.put(KEY_StatusON_OffLine, entryStatus);
        contentValues.put(KEY_date_d, "" + day);
        contentValues.put(KEY_month_m, "" + month);
        contentValues.put(KEY_year_y, "" + year);
        contentValues.put(KEY_MilkEntry_UnicId, "" + MilkEntryUnicId);
        db.insert(Table_MilkBuyEntry, null, contentValues);

        db.close();
    }


    public void addSingleBuyMilkEntry_L(String liveId, String dairyID, String customerID, String name, String unic_id, String session, String date, String fat, String snf, String clr, String entry_price, String weight, String totalBonus, String total, String milkRateCategory, Integer fatSnfCategory, Integer entryStatus,String MilkEntryUnicId) {

        String strToTime = strDateToTimeStrempDDMMMYY(date);


        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LiveId, liveId);
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_str_to_time, strToTime);
        contentValues.put(KEY_milkRateCategory, milkRateCategory);
        contentValues.put(KEY_FATSNFCategory, fatSnfCategory);
        contentValues.put(KEY_StatusON_OffLine, entryStatus);
        contentValues.put(KEY_date_d, "" + day);
        contentValues.put(KEY_month_m, "" + month);
        contentValues.put(KEY_year_y, "" + year);
        contentValues.put(KEY_MilkEntry_UnicId, "" + MilkEntryUnicId);
        
        db.insert(Table_MilkBuyEntry, null, contentValues);

        db.close();
    }




    public void getTenDaysEntry(){

    }

    public void addBuyMilkEntryFromAPI30Days(JSONArray jsonArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                 String milkEntryUniqueId = jObj.getString("milkEntryUniqueId");

                contentValues.put(KEY_LiveId, jObj.getInt("id"));
                contentValues.put(KEY_DairyID, jObj.getString("dairy_id"));
                contentValues.put(KEY_CustomerID, jObj.getString("customer_id"));
                contentValues.put(KEY_unic_customer, jObj.getString("unic_customer"));
                contentValues.put(KEY_Name, jObj.getString("name"));
                contentValues.put(KEY_Date, jObj.getString("entry_date"));
                contentValues.put(KEY_Session, jObj.getString("shift"));
                contentValues.put(KEY_Fat, jObj.getString("fat"));
                contentValues.put(KEY_SNF, jObj.getString("snf"));
                contentValues.put(KEY_CLR, jObj.getString("clr"));
                contentValues.put(KEY_Rate, jObj.getString("per_kg_price"));
                contentValues.put(KEY_Weight, jObj.getString("total_milk"));
                contentValues.put(KEY_TotalBonus, jObj.getString("total_bonus"));
                contentValues.put(KEY_Total, jObj.getString("total_price"));
                contentValues.put(KEY_milkRateCategory, jObj.getString("milk_category"));
                contentValues.put(KEY_FATSNFCategory, jObj.getInt("snf_fat_categories"));
                contentValues.put(KEY_str_to_time, jObj.getString("entry_date_str"));
                contentValues.put(KEY_StatusON_OffLine, 1);
                contentValues.put(KEY_date_d, jObj.getString("d"));
                contentValues.put(KEY_month_m, jObj.getString("m"));
                contentValues.put(KEY_year_y, jObj.getString("y"));
                contentValues.put(KEY_MilkEntry_UnicId, milkEntryUniqueId);

                db.insert(Table_MilkBuyEntry, null, contentValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            //do some insertions or whatever you need

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();


    }

    public void addBuyMilkEntryFromAPI(JSONArray jsonArray) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {

            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jObj = jsonArray.getJSONObject(i);
                contentValues.put(KEY_LiveId, jObj.getInt("id"));
                contentValues.put(KEY_DairyID, jObj.getString("dairy_id"));
                contentValues.put(KEY_CustomerID, jObj.getString("customer_id"));
                contentValues.put(KEY_unic_customer, jObj.getString("unic_customer"));
                contentValues.put(KEY_Name, jObj.getString("name"));
                contentValues.put(KEY_Session, jObj.getString("shift"));
                contentValues.put(KEY_Date, jObj.getString("entry_date"));
                contentValues.put(KEY_Fat, jObj.getString("fat"));
                contentValues.put(KEY_SNF, jObj.getString("snf"));
                contentValues.put(KEY_CLR, jObj.getString("clr"));
                contentValues.put(KEY_Rate, jObj.getString("per_kg_price"));
                contentValues.put(KEY_Weight, jObj.getString("total_milk"));
                contentValues.put(KEY_TotalBonus, jObj.getString("total_bonus"));
                contentValues.put(KEY_Total, jObj.getString("total_price"));
                contentValues.put(KEY_milkRateCategory, jObj.getString("milk_category"));
                contentValues.put(KEY_FATSNFCategory, jObj.getInt("snf_fat_categories"));
                contentValues.put(KEY_str_to_time, jObj.getString("entry_date_str"));
                contentValues.put(KEY_StatusON_OffLine, 1);
                contentValues.put(KEY_date_d, jObj.getString("d"));
                contentValues.put(KEY_month_m, jObj.getString("m"));
                contentValues.put(KEY_year_y, jObj.getString("y"));
                contentValues.put(KEY_MilkEntry_UnicId, jObj.getString("milkEntryUniqueId"));
                db.insert(Table_MilkBuyEntry, null, contentValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            //do some insertions or whatever you need

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();


    }

    public void addBothEntryFromFirebase(String strTable, JSONObject jObj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_LiveId, jObj.getInt("id"));
            contentValues.put(KEY_DairyID, jObj.getString("dairy_id"));
            contentValues.put(KEY_CustomerID, jObj.getString("customer_id"));
            contentValues.put(KEY_unic_customer, jObj.getString("unic_customer"));
            contentValues.put(KEY_Name, jObj.getString("name"));
            contentValues.put(KEY_Session, jObj.getString("shift"));
            contentValues.put(KEY_Fat, jObj.getString("fat"));
            contentValues.put(KEY_SNF, jObj.getString("snf"));
            contentValues.put(KEY_CLR, jObj.getString("clr"));
            contentValues.put(KEY_TotalBonus, jObj.getString("total_bonus"));
            contentValues.put(KEY_milkRateCategory, jObj.getString("milk_category"));
            contentValues.put(KEY_FATSNFCategory, jObj.getInt("snf_fat_categories"));
            contentValues.put(KEY_StatusON_OffLine, 1);
            contentValues.put(KEY_date_d, jObj.getString("d"));
            contentValues.put(KEY_month_m, jObj.getString("m"));
            contentValues.put(KEY_year_y, jObj.getString("y"));

            //Buy Milk
            if (strTable.equalsIgnoreCase("milk_buy_entry")) {
                contentValues.put(KEY_Date, jObj.getString("entry_date"));
                contentValues.put(KEY_Rate, jObj.getString("per_kg_price"));
                contentValues.put(KEY_Weight, jObj.getString("total_milk"));
                contentValues.put(KEY_Total, jObj.getString("total_price"));
                contentValues.put(KEY_str_to_time, jObj.getString("entry_date_str"));
            } else {
                //Sale Milk
                contentValues.put(KEY_DeliveryBoyId, jObj.getString("deliveryboy_id"));
                contentValues.put(KEY_Date, jObj.getString("insert_date"));
                contentValues.put(KEY_Rate, jObj.getString("milk_perkg_price"));
                contentValues.put(KEY_Weight, jObj.getString("milk_wt"));
                contentValues.put(KEY_Total, jObj.getString("milk_total_price"));
                contentValues.put(KEY_str_to_time, jObj.getInt("insert_date_in_str"));
            }
            db.insert(strTable, null, contentValues);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();


    }

    public Integer getEntryId(String strTable, String id) {
        Integer localId = 0;
        String selectQuery = "SELECT  * FROM " + strTable + " WHERE " + KEY_LiveId + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));

                break;
            } while (cursor.moveToNext());
        }
        db.close();

        return localId;
    }

    public Integer checkOnlineMilkEntry(String strTable, String id) {
        Integer localId = 0;
        String selectQuery = "SELECT  * FROM " + strTable + " WHERE " + KEY_LiveId + " = '" + id + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                localId = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));

                break;
            } while (cursor.moveToNext());
        }
        db.close();

        return localId;
    }

    public void updateBuyMilkEntryId(String entry_id, String liveEntryId,int total,int running) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_LiveId, liveEntryId);
            contentValues.put(KEY_StatusON_OffLine, 1);
            String where = KEY_ID + "=" + entry_id;
            myDatabase.update(Table_MilkBuyEntry, contentValues, where, null);

        }catch (Exception e) {
            e.printStackTrace();
        }

        myDatabase.close();

    }

    public void updateBuyMilkEntries(int entry_status, String liveEntryId,String milkentryunicID) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_LiveId, liveEntryId);
            contentValues.put(KEY_MilkEntry_UnicId, milkentryunicID);
            contentValues.put(KEY_StatusON_OffLine, entry_status);
            String where =  KEY_MilkEntry_UnicId + "=" + milkentryunicID;
            myDatabase.update(Table_MilkBuyEntry, contentValues, where, null);
        }catch (Exception e) {
            e.printStackTrace();
        }
        myDatabase.close();

    }

    public void updateBuyMilkEntry(int entry_id, int onLineId, String dairyID, String customerID, String name, String unic_id, String session, String fat, String snf, String clr, String weight, String total_bonus, String total, String date, String entry_price, String milk_category, Integer milkType, Integer entryStatus,String MilkentryunicId) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_LiveId, onLineId);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, total_bonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_FATSNFCategory, milkType);
        contentValues.put(KEY_StatusON_OffLine, entryStatus);
        contentValues.put(KEY_MilkEntry_UnicId, MilkentryunicId);
        String where = KEY_ID + "=" + entry_id;
        myDatabase.update(Table_MilkBuyEntry, contentValues, where, null);
        myDatabase.close();

    }

    public void updateBothEntryFromWeb(String strTableName, int entry_id, JSONObject jObj) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(KEY_LiveId, jObj.getInt("id"));
            contentValues.put(KEY_DairyID, jObj.getString("dairy_id"));
            contentValues.put(KEY_CustomerID, jObj.getString("customer_id"));
            contentValues.put(KEY_unic_customer, jObj.getString("unic_customer"));
            contentValues.put(KEY_Name, jObj.getString("name"));
            contentValues.put(KEY_Session, jObj.getString("shift"));
            contentValues.put(KEY_Fat, jObj.getString("fat"));
            contentValues.put(KEY_SNF, jObj.getString("snf"));
            contentValues.put(KEY_CLR, jObj.getString("clr"));
            contentValues.put(KEY_TotalBonus, jObj.getString("total_bonus"));
            contentValues.put(KEY_milkRateCategory, jObj.getString("milk_category"));
            contentValues.put(KEY_FATSNFCategory, jObj.getInt("snf_fat_categories"));
            contentValues.put(KEY_StatusON_OffLine, 1);


            //Buy Milk
            if (strTableName.equalsIgnoreCase("milk_buy_entry")) {
                contentValues.put(KEY_Date, jObj.getString("entry_date"));
                contentValues.put(KEY_Rate, jObj.getString("per_kg_price"));
                contentValues.put(KEY_Weight, jObj.getString("total_milk"));
                contentValues.put(KEY_Total, jObj.getString("total_price"));
                contentValues.put(KEY_str_to_time, jObj.getString("entry_date_str"));
            } else {
                //Sale Milk
                contentValues.put(KEY_DeliveryBoyId, jObj.getString("deliveryboy_id"));
                contentValues.put(KEY_Date, jObj.getString("insert_date"));
                contentValues.put(KEY_Rate, jObj.getString("milk_perkg_price"));
                contentValues.put(KEY_Weight, jObj.getString("milk_wt"));
                contentValues.put(KEY_Total, jObj.getString("milk_total_price"));
                contentValues.put(KEY_str_to_time, jObj.getInt("insert_date_in_str"));
            }
            String where = KEY_ID + "=" + entry_id;
            myDatabase.update(strTableName, contentValues, where, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        myDatabase.close();

    }

    public ArrayList<CustomerEntryListPojo> getMilkBuyEntryRecords(String session, String _date) {

        ArrayList<CustomerEntryListPojo> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_MilkBuyEntry;//+ " WHERE " + KEY_CustomerID + " = " + "1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkBuyFromCusor(cursor, mEntryList);
        db.close();
        return mEntryList;
    }

    public ArrayList<CustomerEntryListPojo> getMilkBuyEntryRecordsOffline() {
        ArrayList<CustomerEntryListPojo> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_MilkBuyEntry + " WHERE " + KEY_StatusON_OffLine + " = " + "0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkBuyFromCusor(cursor, mEntryList);

        db.close();
        return mEntryList;
    }

    public int countmilk_entry_tabledata() {
        String selectQuery = "SELECT  * FROM " + Table_MilkBuyEntry;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int a = cursor.getCount();
        db.close();
        return a;
    }

    public ArrayList<CustomerEntryListPojo> getBuyMilkOneDayEntry(String session, String _date) {

        ArrayList<CustomerEntryListPojo> mEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_MilkBuyEntry + " WHERE " + KEY_Session + " = '" + session + "' AND " + KEY_Date + " = '" + _date + "'";
        printLog("query", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        mEntryList = getMilkBuyFromCusor(cursor, mEntryList);
        printLog("db Buy mEntryList", "" + mEntryList.size());
        db.close();

        return mEntryList;
    }

    private ArrayList<CustomerEntryListPojo> getMilkBuyFromCusor(Cursor cursor, ArrayList<CustomerEntryListPojo> mEntryList) {

            if (cursor.moveToFirst()) {
                do {
                    mEntryList.add(new CustomerEntryListPojo(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LiveId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CustomerID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_DairyID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Session)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Date)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLR)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Weight)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_TotalBonus)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Total)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATSNFCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_str_to_time)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_StatusON_OffLine)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_date_d)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_month_m)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_year_y)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_MilkEntry_UnicId)))
                    );

                } while (cursor.moveToNext());
            }

        return mEntryList;
    }


    // Delete Buy Milk Entry
    public void deleteBuyMilkEntryRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_MilkBuyEntry, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteOnLineMilkEntry(String strFrom, String id) {
        String strTable = Table_MilkBuyEntry;
        if (strFrom.equals("sale")) {
            strTable = Table_MilkSaleEntry;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(strTable, KEY_LiveId + "=?", new String[]{id});
        db.close();
    }

    public void deleteAllBuyMilkEntryRecord() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_MilkBuyEntry);
        db.close();

    }

    public void deleteBuyMilkEntryMultipleRecord(String startDate, String endDate) {
        String fromTimeStemp = strDateToTimeStampDDMMYY(startDate);
        String endTimeStemp = strDateToTimeStampDDMMYY(endDate);
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQuery = "DELETE  FROM " + Table_MilkBuyEntry + " WHERE " + KEY_str_to_time + " BETWEEN '" + fromTimeStemp + "' AND '" + endTimeStemp + "'";
        System.out.println(" deleteQuery>>>>" + deleteQuery);

        db.execSQL(deleteQuery);
        db.close();
    }

    public void deleteMilkEntryShiftWise(String strFrom, String startDate, String shift) {
        if (isNetworkAvaliable(mContext)) {

            SQLiteDatabase db = this.getWritableDatabase();
            String strTable = Table_MilkBuyEntry;
            if (strFrom.equalsIgnoreCase("sale")) {
                strTable = Table_MilkSaleEntry;
            }

            String deleteQuery = "DELETE  FROM " + strTable + " WHERE " + KEY_Date + " = '" + startDate + "' AND " + KEY_Session + " = '" + shift + "'" + " AND " + KEY_LiveId + "!=" + "0";

            db.execSQL(deleteQuery);
            db.close();
        }
    }

    // Sale Milk Entry
    public void addSaleSingleMilkEntry(int onlineId, int deliveryBoyId, String dairyID, String customerID, String name, String unic_id, String date, String session, String fat, String snf, String clr, String entry_price, String weight, String totalBonus, String total, String milk_category, int fatSnfCategory, int entryStatus) {


        String strToTime = strDateToTimeStrempDDMMMYY(date);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_LiveId, onlineId);
        contentValues.put(KEY_DeliveryBoyId, deliveryBoyId);
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_str_to_time, strToTime);
        contentValues.put(KEY_FATSNFCategory, fatSnfCategory);
        contentValues.put(KEY_StatusON_OffLine, entryStatus);
        contentValues.put(KEY_date_d, "" + day);
        contentValues.put(KEY_month_m, "" + month);
        contentValues.put(KEY_year_y, "" + year);
        db.insert(Table_MilkSaleEntry, null, contentValues);

        db.close();
    }

    public void addSaleMilkEntryFromAPI(JSONArray jsonArray) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                contentValues.put(KEY_LiveId, jObj.getInt("id"));
                contentValues.put(KEY_DairyID, jObj.getString("dairy_id"));
                contentValues.put(KEY_DeliveryBoyId, jObj.getString("deliveryboy_id"));
                contentValues.put(KEY_CustomerID, jObj.getString("customer_id"));
                contentValues.put(KEY_Name, jObj.getString("name"));
                contentValues.put(KEY_unic_customer, jObj.getString("unic_customer"));
                contentValues.put(KEY_Date, jObj.getString("insert_date"));
                contentValues.put(KEY_Session, jObj.getString("shift"));
                contentValues.put(KEY_Fat, jObj.getString("fat"));
                contentValues.put(KEY_SNF, jObj.getString("snf"));
                contentValues.put(KEY_CLR, jObj.getString("clr"));
                contentValues.put(KEY_Rate, jObj.getString("milk_perkg_price"));
                contentValues.put(KEY_Weight, jObj.getString("milk_wt"));
                contentValues.put(KEY_TotalBonus, jObj.getString("total_bonus"));
                contentValues.put(KEY_Total, jObj.getString("milk_total_price"));
                contentValues.put(KEY_milkRateCategory, jObj.getInt("milk_category"));
                contentValues.put(KEY_FATSNFCategory, jObj.getInt("snf_fat_categories"));
                contentValues.put(KEY_str_to_time, jObj.getInt("insert_date_in_str"));
                contentValues.put(KEY_StatusON_OffLine, 1);
                contentValues.put(KEY_date_d, "" + jObj.getInt("d"));
                contentValues.put(KEY_month_m, "" + jObj.getInt("m"));
                contentValues.put(KEY_year_y, "" + jObj.getInt("y"));
                //---------------------- Inserting Row------------------------------------
                db.insert(Table_MilkSaleEntry, null, contentValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //do some insertions or whatever you need
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }


    public void updateSaleMilkEntry(int entry_id, int onLineId, String dairyID, String customerID, String name, String unic_id, String date, String session, String fat, String snf, String clr, String entry_price, String weight, String totalBonus, String total, String milk_category, Integer milkType, Integer entryStatus) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LiveId, onLineId);
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_FATSNFCategory, milkType);
        contentValues.put(KEY_StatusON_OffLine, entryStatus);
        String where = KEY_ID + "=" + entry_id;
        myDatabase.update(Table_MilkSaleEntry, contentValues, where, null);

        myDatabase.close();

    }

    public void updateSaleMilkEntryId(String entry_id, int liveEntryId) {
        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LiveId, liveEntryId);
        contentValues.put(KEY_StatusON_OffLine, 1);
        String where = KEY_ID + "=" + entry_id;
        myDatabase.update(Table_MilkSaleEntry, contentValues, where, null);
        myDatabase.close();

    }

    public ArrayList<CustomerSaleMilkEntryList> getSaleMilkEntryRecords() {
        ArrayList<CustomerSaleMilkEntryList> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_MilkSaleEntry;//+ " WHERE " + KEY_CustomerID + " = " + "1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        mEntryList = getMilkSaleFromCusor(cursor, mEntryList);
        // db.close();

        return mEntryList;
    }

    public ArrayList<CustomerSaleMilkEntryList> getSaleMilkEntryOfflineEntry() {

        ArrayList<CustomerSaleMilkEntryList> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + Table_MilkSaleEntry + " WHERE " + KEY_StatusON_OffLine + " = " + "0";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkSaleFromCusor(cursor, mEntryList);
        //  db.close();
        return mEntryList;
    }

    public ArrayList<CustomerSaleMilkEntryList> getSaleMilkOneDayEntry(String session, String _date) {

        ArrayList<CustomerSaleMilkEntryList> mEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_MilkSaleEntry + " WHERE " + KEY_Session + " = '" + session + "' AND " + KEY_Date + " = '" + _date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkSaleFromCusor(cursor, mEntryList);

        //   db.close();

        return mEntryList;
    }

    private ArrayList<CustomerSaleMilkEntryList> getMilkSaleFromCusor(Cursor cursor, ArrayList<CustomerSaleMilkEntryList> mEntryList) {

        if (cursor.moveToFirst()) {
            do {
                mEntryList.add(new CustomerSaleMilkEntryList(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LiveId)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_DeliveryBoyId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CustomerID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_DairyID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Session)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Date)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLR)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Weight)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_TotalBonus)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Total)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATSNFCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_str_to_time)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_StatusON_OffLine)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_date_d)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_month_m)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_year_y)))

                );
            } while (cursor.moveToNext());
        }

        return mEntryList;
    }

    public void deleteSaleMilkRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_MilkSaleEntry, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteAllSaleMilkEntry() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_MilkSaleEntry);
        db.close();
    }


    public void addPlantEntry(String dairyID, String customerID, String session, String fat, String snf, String clr, String weight, String totalBonus, String total, String date, String entry_price, String milkRateCategory, int fatSnfCategory, String name, String unic_id) {

        String strToTime = strDateToTimeStrempDDMMMYY(date);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_str_to_time, strToTime);
        contentValues.put(KEY_milkRateCategory, milkRateCategory);
        contentValues.put(KEY_FATSNFCategory, fatSnfCategory);
        contentValues.put(KEY_date_d, "" + day);
        contentValues.put(KEY_month_m, "" + month);
        contentValues.put(KEY_year_y, "" + year);

        db.insert(Table_PlantMilkBuyEntry, null, contentValues);

        db.close();
    }

    public ArrayList<CustomerEntryListPojo> getPlantEntryRecords() {
        ArrayList<CustomerEntryListPojo> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_PlantMilkBuyEntry;//+ " WHERE " + KEY_CustomerID + " = " + "1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                mEntryList.add(new CustomerEntryListPojo(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LiveId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CustomerID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_DairyID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Session)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Date)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLR)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Weight)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_TotalBonus)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Total)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATSNFCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_str_to_time)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_StatusON_OffLine)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_date_d)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_month_m)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_year_y)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_MilkEntry_UnicId)))

                );
            } while (cursor.moveToNext());
        }
        //  Log.e("morningList>>>", "" + mEntryList.size());
        //   db.close();
        return mEntryList;
    }

    public ArrayList<CustomerEntryListPojo> getPlantOneDayEntry(String session, String _date) {
        ArrayList<CustomerEntryListPojo> mEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_PlantMilkBuyEntry + " WHERE " + KEY_Session + " = '" + session + "' AND " + KEY_Date + " = '" + _date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                mEntryList.add(new CustomerEntryListPojo(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_LiveId)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CustomerID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_DairyID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Name)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_unic_customer)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Session)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Date)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_CLR)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Weight)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_TotalBonus)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Total)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_milkRateCategory)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATSNFCategory)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_str_to_time)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_StatusON_OffLine)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_date_d)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_month_m)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_year_y)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_MilkEntry_UnicId)))

                );
            } while (cursor.moveToNext());
        }

        // db.close();

        return mEntryList;
    }


    public void updatePlantMilkEntry(String entry_id, String dairyID, String customerID, String session, String fat, String snf, String clr, String weight, String totalBonus, String total, String date, String entry_price, String milk_category, Integer milkType, String name, String unic_id) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_FATSNFCategory, milkType);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        String where = KEY_ID + "=" + entry_id;
        myDatabase.update(Table_PlantMilkBuyEntry, contentValues, where, null);
        myDatabase.close();

    }

    // Delete Plant Milk Entry
    public void deletePlantEntryItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_PlantMilkBuyEntry, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deletePlantAllEntryRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_PlantMilkBuyEntry);
        db.close();

    }

    ////=====================Plant Sale Outlet Entry=====////////////////


    // Sale Milk Entry
    public void addPlantSaleMilkEntry(String dairyID, String customerID, String session, String fat, String snf, String clr, String weight, String totalBonus, String total, String date, String entry_price, String milk_category, int milkType, String name, String unic_id) {

        String strToTime = strDateToTimeStrempDDMMMYY(date);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        contentValues.put(KEY_str_to_time, strToTime);
        contentValues.put(KEY_FATSNFCategory, milkType);
        contentValues.put(KEY_date_d, "" + day);
        contentValues.put(KEY_month_m, "" + month);
        contentValues.put(KEY_year_y, "" + year);

        db.insert(Table_PlantMilkSaleEntry, null, contentValues);

        db.close();
    }

    public void updatePlantSaleMilkEntry(String entry_id, String dairyID, String customerID, String session, String fat, String snf, String clr, String weight, String totalBonus, String total, String date, String entry_price, String milk_category, Integer milkType, String name, String unic_id) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_CustomerID, customerID);
        contentValues.put(KEY_Session, session);
        contentValues.put(KEY_Fat, fat);
        contentValues.put(KEY_SNF, snf);
        contentValues.put(KEY_CLR, clr);
        contentValues.put(KEY_Weight, weight);
        contentValues.put(KEY_TotalBonus, totalBonus);
        contentValues.put(KEY_Total, total);
        contentValues.put(KEY_Date, date);
        contentValues.put(KEY_Rate, entry_price);
        contentValues.put(KEY_milkRateCategory, milk_category);
        contentValues.put(KEY_FATSNFCategory, milkType);
        contentValues.put(KEY_Name, name);
        contentValues.put(KEY_unic_customer, unic_id);
        String where = KEY_ID + "=" + entry_id;
        myDatabase.update(Table_PlantMilkSaleEntry, contentValues, where, null);

        myDatabase.close();

    }

    public ArrayList<CustomerSaleMilkEntryList> getPlantSaleMilkEntryRecords() {

        ArrayList<CustomerSaleMilkEntryList> mEntryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_PlantMilkSaleEntry;
        //+ " WHERE " + KEY_CustomerID + " = " + "1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkSaleFromCusor(cursor, mEntryList);

        //  db.close();

        return mEntryList;
    }

    public ArrayList<CustomerSaleMilkEntryList> getPlantSaleMilkOneDayEntry(String session, String _date) {

        ArrayList<CustomerSaleMilkEntryList> mEntryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Table_PlantMilkSaleEntry + " WHERE " + KEY_Session + " = '" + session + "' AND " + KEY_Date + " = '" + _date + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        mEntryList = getMilkSaleFromCusor(cursor, mEntryList);

        //  db.close();

        return mEntryList;
    }


    public void deletePlantSaleMilkRecord(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Table_PlantMilkSaleEntry, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteAllPlantSaleMilkEntry() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_PlantMilkSaleEntry);
        db.close();

    }


    public void addRateOfMilk(String dairyID, String rate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_DairyID, dairyID);
        contentValues.put(KEY_Rate, rate);
        contentValues.put(KEY_LiveStatus, 0);
        // Inserting Row
        db.insert(Table_Rate, null, contentValues);
        db.close(); // Closing database connection
    }

    public ArrayList<MilkRatePojo> getMilkRate(String dairyId) {
        ArrayList<MilkRatePojo> mRateList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_Rate + " WHERE " + KEY_DairyID + " = " + dairyId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                mRateList.add(new MilkRatePojo(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        db.close();
        return mRateList;
    }


    public ArrayList<MilkHistoryPojo> getMilkHistoryByTwoDate(String stDate, String enDate) {
        List<Date> dates = new ArrayList<Date>();

        String str_date = stDate;
        String end_date = enDate;

        SimpleDateFormat formatter, formatter2;

        formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter2 = new SimpleDateFormat("dd-MMM-yyyy");
        Date startDate = null;
        try {
            startDate = formatter.parse(str_date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Date endDate = null;
        try {
            endDate = formatter.parse(end_date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long interval = 24 * 1000 * 60 * 60; // 1 hour in millis
        long endTime = endDate.getTime(); // create your endtime here, possibly using Calendar or Date
        long curTime = startDate.getTime();
        while (curTime <= endTime) {
            dates.add(new Date(curTime));
            curTime += interval;
        }

        ArrayList<MilkHistoryPojo> morningList = new ArrayList<>();
        ArrayList<MilkHistoryPojo> eveningList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String morningSession = "morning";
        String eveningSession = "evening";
        for (int i = 0; i < dates.size(); i++) {
            Date lDate = dates.get(i);
            String ds = formatter2.format(lDate);
            // System.out.println(" Date is ..." + ds);
            Log.e("Date", ":== " + ds);
            Cursor cursorMorning = db.rawQuery("select date,session,date,sum(weight) as total_weight,sum(total) as total_price" + ",avg(fat) as avg_fat from entry where session = '" + morningSession + "' and date= '" + ds + "' group by date", null);

            Cursor cursorEvening = db.rawQuery("select date,session,date,sum(weight) as total_weight,sum(total) as total_price" + ",avg(fat) as avg_fat from entry where session = '" + eveningSession + "' and date= '" + ds + "' group by date", null);


            if (cursorMorning.moveToFirst()) {
                do {
                    String stDbDate = cursorMorning.getString(cursorMorning.getColumnIndexOrThrow(KEY_Date));
                    Log.e("stDbDate", stDbDate + " @@@@ " + cursorMorning.getString(cursorMorning.getColumnIndexOrThrow(KEY_Session)));

                    morningList.add(new MilkHistoryPojo(cursorMorning.getString(cursorMorning.getColumnIndexOrThrow("total_weight")), cursorMorning.getString(cursorMorning.getColumnIndexOrThrow("total_price")), cursorMorning.getString(cursorMorning.getColumnIndexOrThrow("avg_fat")), "", cursorMorning.getString(cursorMorning.getColumnIndexOrThrow("date")), cursorMorning.getString(cursorMorning.getColumnIndexOrThrow("session"))));

                } while (cursorMorning.moveToNext());
            } else {
                morningList.add(new MilkHistoryPojo("-", "-", "-", "-", ds, "morning"));
            }

            if (cursorEvening.moveToFirst()) {
                do {
                    String stDbDate = cursorEvening.getString(cursorEvening.getColumnIndexOrThrow(KEY_Date));
                    System.out.println("stDbDate===" + stDbDate + " @@@@ " + cursorEvening.getString(cursorEvening.getColumnIndexOrThrow(KEY_Session)));
                    eveningList.add(new MilkHistoryPojo(cursorEvening.getString(cursorEvening.getColumnIndexOrThrow("total_weight")), cursorEvening.getString(cursorEvening.getColumnIndexOrThrow("total_price")), cursorEvening.getString(cursorEvening.getColumnIndexOrThrow("avg_fat")), "", cursorEvening.getString(cursorEvening.getColumnIndexOrThrow("date")), cursorEvening.getString(cursorEvening.getColumnIndexOrThrow("session"))));

                } while (cursorMorning.moveToNext());
            } else {
                eveningList.add(new MilkHistoryPojo("-", "-", "-", "-", ds, "evening"));
            }
        }

        System.out.println("list>>>>" + "getMilkHistoryByTwoDate: " + morningList.size());
        System.out.println("list>>>>" + "getMilkHistoryByTwoDate: " + eveningList.size());


        ArrayList<MilkHistoryPojo> allAllList = new ArrayList<>();


        for (int i = 0; i < dates.size(); i++) {
            if (morningList.get(i).entry_date.equals(eveningList.get(i).entry_date)) {
                allAllList.add(new MilkHistoryPojo(morningList.get(i).total_milk, morningList.get(i).total_price, morningList.get(i).total_fat, morningList.get(i).dairy_id, morningList.get(i).entry_date, morningList.get(i).session));

                allAllList.add(new MilkHistoryPojo(eveningList.get(i).total_milk, eveningList.get(i).total_price, eveningList.get(i).total_fat, eveningList.get(i).dairy_id, eveningList.get(i).entry_date, eveningList.get(i).session));
            }
        }


        System.out.println("lallAllList===>>>>" + "getMilkHistoryByTwoDate: " + allAllList.size());

        return allAllList;
    }

    public void updateBuyerMilkData(String morningMilk, String eveningMilk, String price, String buyerID) {

        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KeyMorning_milk, morningMilk);
        contentValues.put(KeyEvening_milk, eveningMilk);
        contentValues.put(BuyerPrice_per_ltr, price);
        String where = KEY_ID + "=" + buyerID;
        myDatabase.update(Table_BuyerCustomer, contentValues, where, null);
        myDatabase.close();
    }


    public void deleteBuyerCustomer(String buyerID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_BuyerCustomer, KEY_ID + "=?", new String[]{buyerID});
        db.close();

    }


    //-------------- item Add to Dairy Buy Milk All Chart------------------------------
    public void addDairyBuyChart(ArrayList<BeanDairySnfFatChart> chartlist, String from) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (int i = 0; i < chartlist.size(); i++) {
            values.put(KEY_ID, chartlist.get(i).id);
            values.put(KEY_SNF, chartlist.get(i).SNF);
            values.put(KEY_Fat, chartlist.get(i).Fat);
            values.put(KEY_Rate, chartlist.get(i).Rate);
            values.put(KEY_SNF_FAT_Category, chartlist.get(i).snf_fat_category);
            values.put(KEY_Category_Chart_Id, chartlist.get(i).categorychart_id);

            //---------------------- Inserting Row------------------------------------
            db.insert(Table_BuyMilkFATSNFChart, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    }

    // Sale milk All Chart
    public void addDairySaleChart(ArrayList<BeanDairySnfFatChart> chartlist, String from) {
        SQLiteDatabase db = this.getWritableDatabase();

        String chartTable = Table_SaleMilkFATSNFChart;


        db.beginTransaction();
        ContentValues values = new ContentValues();
        for (int i = 0; i < chartlist.size(); i++) {
            values.put(KEY_ID, chartlist.get(i).id);
            values.put(KEY_SNF, chartlist.get(i).SNF);
            values.put(KEY_Fat, chartlist.get(i).Fat);
            values.put(KEY_Rate, chartlist.get(i).Rate);
            values.put(KEY_SNF_FAT_Category, chartlist.get(i).snf_fat_category);
            values.put(KEY_Category_Chart_Id, chartlist.get(i).categorychart_id);

            //---------------------- Inserting Row------------------------------------
            db.insert(Table_SaleMilkFATSNFChart, null, values);
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

    } //-------------- item Add to Dairy All Chart------------------------------

    public void addBuyMilkSNFChart(String id, String snf, String fat, String rate, String snf_fat_category, String category_Chart_Id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_SNF, snf);
        values.put(KEY_Fat, fat);
        values.put(KEY_Rate, rate);
        values.put(KEY_SNF_FAT_Category, snf_fat_category);
        values.put(KEY_Category_Chart_Id, category_Chart_Id);

        //---------------------- Inserting Row------------------------------------
        db.insert(Table_BuyMilkFATSNFChart, null, values);
        db.close();
    }

    //----------------------------Fat SNF Chart -------------------------
    public ArrayList<BeanDairySnfFatChart> getBuyFatSNFChartList() {
        ArrayList<BeanDairySnfFatChart> mList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_BuyMilkFATSNFChart;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                mList.add(new BeanDairySnfFatChart(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF_FAT_Category)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Category_Chart_Id))));
            } while (cursor.moveToNext());

        }

        db.close();
        return mList;
    }

    public float getBuyMilkFatSnfRateValue(String chart_cat_id, String fat, String snf, String snf_fat_cat) {
        float value = 0;

        // Select  Query Product id
        String selectQuery = "SELECT  * FROM " + Table_BuyMilkFATSNFChart + " WHERE " + KEY_Category_Chart_Id + " = '" + chart_cat_id + "' AND " + KEY_Fat + " = '" + fat + "' AND " + KEY_SNF + " = '" + snf + "' AND " + KEY_SNF_FAT_Category + " = '" + snf_fat_cat + "'";
        System.out.println("selectQuery=catChartId==>>>" + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_Rate));
                System.out.println("rate=db==>>>" + value);

            } while (cursor.moveToNext());

        }

        db.close();
        return value;
    }


    public ArrayList<SnfFatListPojo> getBuyMilkFatSnfRateList(String chart_cat_id, String snf_fat_cat) {

        ArrayList<SnfFatListPojo> snfFatListPojos = new ArrayList<>();
        sessionManager = new SessionManager(mContext);
        sessionManager.removeValue(Key_RateChartList);
        String selectQuery = "SELECT  * FROM " + Table_BuyMilkFATSNFChart + " WHERE " + KEY_Category_Chart_Id + " = '" + chart_cat_id + "' AND " + KEY_SNF_FAT_Category + " = '" + snf_fat_cat + "'";
        System.out.println("selectQuery=catChartId==>>>" + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                snfFatListPojos.add(new SnfFatListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF_FAT_Category))));


            } while (cursor.moveToNext());

        }

        db.close();

        sessionManager.saveRateChartList(mContext, snfFatListPojos);
        System.out.println(" Rate Chart List ==>>>" + snfFatListPojos.size());

        return snfFatListPojos;
    }

    //-------------- Delete Dairy All Chart------------------------------
    public void deleteBuyFatSnfChartTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_BuyMilkFATSNFChart);
        db.close();
    }

    // Sale Milk FAT SNF Chart


    //-------------- item Add to Dairy All Chart------------------------------
    public void addSaleMilkSNFChart(String id, String snf, String fat, String rate, String snf_fat_category, String category_Chart_Id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_SNF, snf);
        values.put(KEY_Fat, fat);
        values.put(KEY_Rate, rate);
        values.put(KEY_SNF_FAT_Category, snf_fat_category);
        values.put(KEY_Category_Chart_Id, category_Chart_Id);
        //---------------------- Inserting Row------------------------------------
        db.insert(Table_SaleMilkFATSNFChart, null, values);
        db.close();
    }

    public float getSaleMilkSNFRate(String chart_cat_id, String fat, String snf, String snf_fat_cat) {
        float value = 0;

        // Select  Query Product id
        String selectQuery = "SELECT  * FROM " + Table_SaleMilkFATSNFChart + " WHERE " + KEY_Category_Chart_Id + " = '" + chart_cat_id + "' AND " + KEY_Fat + " = '" + fat + "' AND " + KEY_SNF + " = '" + snf + "' AND " + KEY_SNF_FAT_Category + " = '" + snf_fat_cat + "'";
        System.out.println("selectQuery=catChartId==>>>" + selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getFloat(cursor.getColumnIndexOrThrow(KEY_Rate));
            } while (cursor.moveToNext());

        }

        db.close();
        return value;
    }

    // get Sale Milk Chart
    public ArrayList<SnfFatListPojo> getSaleMilkFatSnfRateList(String chart_cat_id, String snf_fat_cat) {

        ArrayList<SnfFatListPojo> snfFatListPojos = new ArrayList<>();
        sessionManager = new SessionManager(mContext);
        sessionManager.removeValue(Key_RateChartList);
        String selectQuery = "SELECT  * FROM " + Table_SaleMilkFATSNFChart + " WHERE " + KEY_Category_Chart_Id + " = '" + chart_cat_id + "' AND " + KEY_SNF_FAT_Category + " = '" + snf_fat_cat + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                snfFatListPojos.add(new SnfFatListPojo(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Fat)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Rate)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_SNF_FAT_Category))));


            } while (cursor.moveToNext());

        }

        db.close();

        sessionManager.saveRateChartList(mContext, snfFatListPojos);

        return snfFatListPojos;
    }

    //-------------- Delete Sale Milk Chart------------------------------
    public void deleteSaleMilkSNFChartTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_SaleMilkFATSNFChart);
        db.close();
    }
    //----------------- Cart -------------------------

    public void addCart(Integer id, Integer min_qty, Integer qty, Integer productValue, double mrp, double price, double gst, double totalPrice, String title, String type, String image, String thumb, String status, String description) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_MinQty, min_qty);
        values.put(KEY_Qty, qty);
        values.put(KEY_ItemQnt, productValue);
        values.put(KEY_Mrp, mrp);
        values.put(KEY_Price, price);
        values.put(KEY_GST, gst);
        values.put(KEY_TotalPrice, totalPrice);
        values.put(KEY_Title, title);
        values.put(KEY_Type, type);
        values.put(KEY_Image, image);
        values.put(KEY_Thumb, thumb);
        values.put(KEY_Status, status);
        values.put(KEY_Description, description);
        // Inserting Row
        db.insert(Table_Cart, null, values);
        db.close(); // Closing database connection
    }

    public void updateCart(Integer id, Integer productValue, double totalPrice) {
        SQLiteDatabase myDatabase = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ItemQnt, productValue);
        contentValues.put(KEY_TotalPrice, totalPrice);
        String where = KEY_ID + "=" + id;
        myDatabase.update(Table_Cart, contentValues, where, null);
        myDatabase.close();

    }

    public void deleteCartItem(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Table_Cart, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteCartTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_Cart);
        db.close();
    }

    public ArrayList<BeanCartItem> getCartList() {
        ArrayList<BeanCartItem> mcartList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_Cart;//
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                mcartList.add(new BeanCartItem(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MinQty)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_Qty)), cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ItemQnt)), cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Mrp)), cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_Price)), cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_GST)), cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_TotalPrice)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Title)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Type)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Image)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Thumb)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Status)), cursor.getString(cursor.getColumnIndexOrThrow(KEY_Description)))

                );

            } while (cursor.moveToNext());
        }
        db.close();
        return mcartList;
    }

    // Add Offer
    public void addBannerOffer(int id, int prodId, double price, String thumb, String image, String title, String desc, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_ProdId, prodId);
        values.put(KEY_Price, price);
        values.put(KEY_Thumb, thumb);
        values.put(KEY_Image, image);
        values.put(KEY_Title, title);
        values.put(KEY_Description, desc);
        values.put(KEY_Type, type);
        // Inserting Row
        db.insert(Table_BannerOffer, null, values);
        db.close(); // Closing database connection
    }

    //----------------------------Getting Offer-------------------------
    public ArrayList<BeanOfferBanerList> getBannerOfferList() {
        ArrayList<BeanOfferBanerList> mList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_BannerOffer;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                mList.add(new BeanOfferBanerList(cursor.getInt(0), cursor.getInt(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)));
            } while (cursor.moveToNext());

        }

        db.close();
        return mList;
    }

    //--------------------Delete Offer
    public void deleteBannerOffer() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + Table_BannerOffer);
        db.close();

    }

    // Buyer Customer Product Cart
    //-------------- item Add to cart-------------------------------
    public void addBuyerCart(String product_id, String product_name, String weight, String image, double price, double totalPrice, int qty, int itemQnt) {
        sessionManager = new SessionManager(mContext);

        String date = sessionManager.getValueSesion(KEY_ProductAddDate);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, product_id);
        values.put(KEY_Title, product_name);
        values.put(KEY_Weight, weight);
        values.put(KEY_Image, image);
        values.put(KEY_Price, price);
        values.put(KEY_TotalPrice, totalPrice);

        values.put(KEY_Qty, qty);
        values.put(KEY_ItemQnt, itemQnt);
        values.put(KEY_ProductAddDate, date);
        //---------------------- Inserting Row------------------------------------
        db.insert(Table_BuyerCart, null, values);
        db.close(); // Closing database connection
    }


    public void updateBuyerCart(String id, Integer itemQnt, Integer productQty, double totalPrice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ItemQnt, itemQnt);
        values.put(KEY_Qty, productQty);
        values.put(KEY_TotalPrice, totalPrice);
        db.update(Table_BuyerCart, values, KEY_ID + " = '" + id + "'", null);
        db.close();

    }

    //------Getting Cart List--------------------------------------
    public ArrayList<BeanBuyerCartItem> getBuyerCartList() {
        ArrayList<BeanBuyerCartItem> mList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Table_BuyerCart;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            do {
                mList.add(new BeanBuyerCartItem(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4), cursor.getDouble(5), cursor.getInt(6), cursor.getInt(7)));

            } while (cursor.moveToNext());

        }

        db.close();
        return mList;
    }

    public void deleteBuyerCartItem(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Table_BuyerCart, KEY_ID + "=?", new String[]{id});
        db.close();
    }

    public void deleteBuyerCartTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + Table_BuyerCart);
        db.close();
    }

    public void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }


}
