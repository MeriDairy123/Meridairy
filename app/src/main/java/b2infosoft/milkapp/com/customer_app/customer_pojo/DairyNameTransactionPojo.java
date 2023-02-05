package b2infosoft.milkapp.com.customer_app.customer_pojo;

import java.util.ArrayList;

/**
 * Created by u on 10-Jan-18.
 */

public class DairyNameTransactionPojo {

    public String id = "";
    public String dairy_name = "";
    public String center_name = "";
    public String phone_number = "";
    public String dairy_firebase_tocan = "";
    public String customerID = "";
    public String customer_unic_customer = "";
    public String customer_user_group_id = "";
    public String customer_firebase_tocan = "";
    public ArrayList<DairyNameTransactionPojo> this_month_transactionsList;
    public ArrayList<DairyNameTransactionPojo> previous_month_transactionsList;
    public ArrayList<DairyNameTransactionPojo> all_month_transactionsList;
    public String milk_entries_id = "";
    public String entry_date = "";
    public String created_time = "";
    public String entry_date_str = "";
    public String products_name = "";
    public String total_price = "";
    public String type = "";

    public DairyNameTransactionPojo(String id, String dairy_name, String center_name, String phone_number, String dairy_firebase_tocan, String customerID, String customer_unic_customer, String customer_user_group_id, String customer_firebase_tocan, ArrayList<DairyNameTransactionPojo> this_month_transactionsList, ArrayList<DairyNameTransactionPojo> previous_month_transactionsList, ArrayList<DairyNameTransactionPojo> all_month_transactionsList) {
        this.id = id;
        this.dairy_name = dairy_name;
        this.center_name = center_name;
        this.phone_number = phone_number;
        this.dairy_firebase_tocan = dairy_firebase_tocan;
        this.customerID = customerID;
        this.customer_unic_customer = customer_unic_customer;
        this.customer_user_group_id = customer_user_group_id;
        this.customer_firebase_tocan = customer_firebase_tocan;
        this.this_month_transactionsList = this_month_transactionsList;
        this.previous_month_transactionsList = previous_month_transactionsList;
        this.all_month_transactionsList = all_month_transactionsList;
    }

    public DairyNameTransactionPojo(String milk_entries_id, String entry_date, String created_time, String entry_date_str, String products_name, String total_price, String type) {
        this.milk_entries_id = milk_entries_id;
        this.entry_date = entry_date;
        this.created_time = created_time;
        this.entry_date_str = entry_date_str;
        this.products_name = products_name;
        this.total_price = total_price;
        this.type = type;
    }


}
