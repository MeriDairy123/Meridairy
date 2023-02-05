package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;


/**
 * Created by u on 15-Jan-18.
 */

public class TenDaysMilkSellHistory {
    public String id = "", name = "", father_name = "", phone_number = "", user_group_id = "", firebase_tocan = "",
            unic_customer = "", avg_fat = "", total_milk = "", avg_rate = "", grnd_total_amt = "", isChecked = "",
            url_code = "", from_date = "", to_date = "";
    public ArrayList<TenDaysMilkSellHistory> dataList;
    public String customerID = "", for_date = "", session = "", fat = "", snf = "", per_kg_price = "", total_price = "",
            entry_total_milk = "", shift = "", avg_fat_tenDays = "", total_milk_tenDays = "", opening_balance = "", avg_rate_tenDays = "",
            grnd_total_amt_tenDays = "", user_data_id = "", user_data_unic_customer = "", user_data_name = "", user_data_father_name = "",
            user_data_phone_number = "", pdf_url = "", total_bonus = "";
    public ArrayList<TenDaysMilkSellHistory> tenDaysList;
    public ArrayList<BeanUserTransaction> userTransactionsList;

    public TenDaysMilkSellHistory(String id, String name, String father_name, String phone_number, String user_group_id, String firebase_tocan, String unic_customer, String avg_fat, String total_milk, String avg_rate, String grnd_total_amt, ArrayList<TenDaysMilkSellHistory> dataList, String isChecked, String url_code
            , String from_date, String to_date, String pdf_url) {
        this.id = id;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.user_group_id = user_group_id;
        this.firebase_tocan = firebase_tocan;
        this.unic_customer = unic_customer;
        this.avg_fat = avg_fat;
        this.total_milk = total_milk;
        this.avg_rate = avg_rate;
        this.grnd_total_amt = grnd_total_amt;
        this.dataList = dataList;
        this.isChecked = isChecked;
        this.url_code = url_code;
        this.from_date = from_date;
        this.to_date = to_date;
        this.pdf_url = pdf_url;
    }


    public TenDaysMilkSellHistory(String avg_fat, String total_milk_tenDays, String avg_rate_tenDays,
                                  String opening_balance, String grnd_total_amt_tenDays, String user_data_id,
                                  String user_data_unic_customer,
                                  String user_data_name, String user_data_father_name, String user_data_phone_number, String pdf_url,
                                  ArrayList<TenDaysMilkSellHistory> tenDaysList) {
        this.avg_fat_tenDays = avg_fat;
        this.total_milk_tenDays = total_milk_tenDays;
        this.avg_rate_tenDays = avg_rate_tenDays;
        this.opening_balance = opening_balance;
        this.grnd_total_amt_tenDays = grnd_total_amt_tenDays;
        this.user_data_id = user_data_id;
        this.user_data_unic_customer = user_data_unic_customer;
        this.user_data_name = user_data_name;
        this.user_data_father_name = user_data_father_name;
        this.user_data_phone_number = user_data_phone_number;
        this.pdf_url = pdf_url;
        this.tenDaysList = tenDaysList;
    }


    public TenDaysMilkSellHistory(String avg_fat, String total_milk_tenDays, String avg_rate_tenDays,
                                  String opening_balance, String grnd_total_amt_tenDays, String user_data_id,
                                  String user_data_unic_customer, String user_data_name, String user_data_father_name, String user_data_phone_number,
                                  ArrayList<TenDaysMilkSellHistory> tenDaysList, ArrayList<BeanUserTransaction> userTransactionsList) {
        this.avg_fat_tenDays = avg_fat;
        this.total_milk_tenDays = total_milk_tenDays;
        this.avg_rate_tenDays = avg_rate_tenDays;
        this.opening_balance = opening_balance;
        this.grnd_total_amt_tenDays = grnd_total_amt_tenDays;
        this.user_data_id = user_data_id;
        this.user_data_unic_customer = user_data_unic_customer;
        this.user_data_name = user_data_name;
        this.user_data_father_name = user_data_father_name;
        this.user_data_phone_number = user_data_phone_number;
        this.tenDaysList = tenDaysList;
        this.userTransactionsList = userTransactionsList;
    }


    public TenDaysMilkSellHistory(String for_date, String session, String fat, String snf, String per_kg_price, String total_price, String total_milk, String shift, String total_bonus) {
        this.for_date = for_date;
        this.session = session;
        this.fat = fat;
        this.snf = snf;
        this.per_kg_price = per_kg_price;
        this.total_price = total_price;
        this.entry_total_milk = total_milk;
        this.shift = shift;
        this.total_bonus = total_bonus;
    }

    public TenDaysMilkSellHistory(String avg_fat, String total_milk_tenDays, String avg_rate_tenDays,
                                  String opening_balance, String grnd_total_amt_tenDays, String user_data_id,
                                  String user_data_unic_customer, String user_data_name, String user_data_father_name,
                                  String user_data_phone_number, String pdf_url,
                                  ArrayList<TenDaysMilkSellHistory> tenDaysList,
                                  ArrayList<BeanUserTransaction> userTransactionsList) {
        this.avg_fat_tenDays = avg_fat;
        this.total_milk_tenDays = total_milk_tenDays;
        this.avg_rate_tenDays = avg_rate_tenDays;
        this.opening_balance = opening_balance;
        this.grnd_total_amt_tenDays = grnd_total_amt_tenDays;
        this.user_data_id = user_data_id;
        this.user_data_unic_customer = user_data_unic_customer;
        this.user_data_name = user_data_name;
        this.user_data_father_name = user_data_father_name;
        this.user_data_phone_number = user_data_phone_number;
        this.pdf_url = pdf_url;
        this.tenDaysList = tenDaysList;
        this.userTransactionsList = userTransactionsList;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUser_group_id() {
        return user_group_id;
    }

    public void setUser_group_id(String user_group_id) {
        this.user_group_id = user_group_id;
    }

    public String getFirebase_tocan() {
        return firebase_tocan;
    }

    public void setFirebase_tocan(String firebase_tocan) {
        this.firebase_tocan = firebase_tocan;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public String getAvg_fat() {
        return avg_fat;
    }

    public void setAvg_fat(String avg_fat) {
        this.avg_fat = avg_fat;
    }

    public String getTotal_milk() {
        return total_milk;
    }

    public void setTotal_milk(String total_milk) {
        this.total_milk = total_milk;
    }

    public String getAvg_rate() {
        return avg_rate;
    }

    public void setAvg_rate(String avg_rate) {
        this.avg_rate = avg_rate;
    }

    public String getGrnd_total_amt() {
        return grnd_total_amt;
    }

    public void setGrnd_total_amt(String grnd_total_amt) {
        this.grnd_total_amt = grnd_total_amt;
    }

    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }

    public String getUrl_code() {
        return url_code;
    }

    public void setUrl_code(String url_code) {
        this.url_code = url_code;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public ArrayList<TenDaysMilkSellHistory> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<TenDaysMilkSellHistory> dataList) {
        this.dataList = dataList;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getFor_date() {
        return for_date;
    }

    public void setFor_date(String for_date) {
        this.for_date = for_date;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }

    public String getPer_kg_price() {
        return per_kg_price;
    }

    public void setPer_kg_price(String per_kg_price) {
        this.per_kg_price = per_kg_price;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getEntry_total_milk() {
        return entry_total_milk;
    }

    public void setEntry_total_milk(String entry_total_milk) {
        this.entry_total_milk = entry_total_milk;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getAvg_fat_tenDays() {
        return avg_fat_tenDays;
    }

    public void setAvg_fat_tenDays(String avg_fat_tenDays) {
        this.avg_fat_tenDays = avg_fat_tenDays;
    }

    public String getTotal_milk_tenDays() {
        return total_milk_tenDays;
    }

    public void setTotal_milk_tenDays(String total_milk_tenDays) {
        this.total_milk_tenDays = total_milk_tenDays;
    }

    public String getAvg_rate_tenDays() {
        return avg_rate_tenDays;
    }

    public void setAvg_rate_tenDays(String avg_rate_tenDays) {
        this.avg_rate_tenDays = avg_rate_tenDays;
    }

    public String getOpening_balance() {
        return opening_balance;
    }

    public void setOpening_balance(String opening_balance) {
        this.opening_balance = opening_balance;
    }

    public String getGrnd_total_amt_tenDays() {
        return grnd_total_amt_tenDays;
    }

    public void setGrnd_total_amt_tenDays(String grnd_total_amt_tenDays) {
        this.grnd_total_amt_tenDays = grnd_total_amt_tenDays;
    }

    public String getUser_data_id() {
        return user_data_id;
    }

    public void setUser_data_id(String user_data_id) {
        this.user_data_id = user_data_id;
    }

    public String getUser_data_unic_customer() {
        return user_data_unic_customer;
    }

    public void setUser_data_unic_customer(String user_data_unic_customer) {
        this.user_data_unic_customer = user_data_unic_customer;
    }

    public String getUser_data_name() {
        return user_data_name;
    }

    public void setUser_data_name(String user_data_name) {
        this.user_data_name = user_data_name;
    }

    public String getUser_data_father_name() {
        return user_data_father_name;
    }

    public void setUser_data_father_name(String user_data_father_name) {
        this.user_data_father_name = user_data_father_name;
    }

    public String getUser_data_phone_number() {
        return user_data_phone_number;
    }

    public void setUser_data_phone_number(String user_data_phone_number) {
        this.user_data_phone_number = user_data_phone_number;
    }

    public String getTotal_bonus() {
        return total_bonus;
    }

    public void setTotal_bonus(String total_bonus) {
        this.total_bonus = total_bonus;
    }

    public ArrayList<TenDaysMilkSellHistory> getTenDaysList() {
        return tenDaysList;
    }

    public void setTenDaysList(ArrayList<TenDaysMilkSellHistory> tenDaysList) {
        this.tenDaysList = tenDaysList;
    }

    public ArrayList<BeanUserTransaction> getUserTransactionsList() {
        return userTransactionsList;
    }

    public void setUserTransactionsList(ArrayList<BeanUserTransaction> userTransactionsList) {
        this.userTransactionsList = userTransactionsList;
    }

    public String getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(String pdf_url) {
        this.pdf_url = pdf_url;
    }
}
