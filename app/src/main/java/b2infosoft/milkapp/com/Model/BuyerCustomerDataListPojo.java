package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;

/**
 * Created by u on 31-Jan-18.
 */

public class BuyerCustomerDataListPojo {

    public String id = "";
    public String created_by = "";
    public String adhar = "";
    public String milk_price = "";
    public String milk_shell_price = "";
    public String unic_customer = "";
    public String unic_customer_for_mobile = "";
    public String name = "";
    public String father_name = "";
    public String phone_number = "";
    public String user_group_id = "";
    public String firebase_tocan = "";
    public String message_status = "";
    public String url_code = "";
    public String from_date = "";
    public String to_date = "";
    public String total_price = "";
    public String total_milk = "";
    public String isClicked = "";
    public String txt = "";
    public String avg_total_milk = "";
    public String avg_grnd_total_amt = "";
    public String user_data_id = "";
    public String user_data_unic_customer = "";
    public String user_data_name = "";
    public String user_data_father_name = "";
    public String user_data_phone_number = "";
    public ArrayList<BuyerCustomerDataListPojo> pdfDataList;
    public String for_date = "";
    public String session = "";
    public String per_kg_price = "";
    public String entry_total_price = "";
    public String entry_total_milk = "";
    public String shift = "";

    public BuyerCustomerDataListPojo(String id, String created_by, String adhar, String milk_price, String milk_shell_price, String unic_customer, String unic_customer_for_mobile, String name, String father_name, String phone_number, String user_group_id, String firebase_tocan, String message_status, String url_code, String from_date, String to_date, String total_price, String total_milk, String isClicked, String txt) {
        this.id = id;
        this.created_by = created_by;
        this.adhar = adhar;
        this.milk_price = milk_price;
        this.milk_shell_price = milk_shell_price;
        this.unic_customer = unic_customer;
        this.unic_customer_for_mobile = unic_customer_for_mobile;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.user_group_id = user_group_id;
        this.firebase_tocan = firebase_tocan;
        this.message_status = message_status;
        this.url_code = url_code;
        this.from_date = from_date;
        this.to_date = to_date;
        this.total_price = total_price;
        this.total_milk = total_milk;
        this.isClicked = isClicked;
        this.txt = txt;
    }

    public BuyerCustomerDataListPojo(String avg_total_milk, String avg_grnd_total_amt, String user_data_id, String user_data_unic_customer, String user_data_name, String user_data_father_name, String user_data_phone_number, ArrayList<BuyerCustomerDataListPojo> pdfDataList) {
        this.avg_total_milk = avg_total_milk;
        this.avg_grnd_total_amt = avg_grnd_total_amt;
        this.user_data_id = user_data_id;
        this.user_data_unic_customer = user_data_unic_customer;
        this.user_data_name = user_data_name;
        this.user_data_father_name = user_data_father_name;
        this.user_data_phone_number = user_data_phone_number;
        this.pdfDataList = pdfDataList;
    }

    public BuyerCustomerDataListPojo(String for_date, String session, String per_kg_price, String entry_total_price, String entry_total_milk, String shift) {
        this.for_date = for_date;
        this.session = session;
        this.per_kg_price = per_kg_price;
        this.entry_total_price = entry_total_price;
        this.entry_total_milk = entry_total_milk;
        this.shift = shift;
    }
}
