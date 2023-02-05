package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 05-Dec-17.
 */

public class BuyerMilkCustomerListPojo {

    /* public String customer_id = "" , name = "",phone_number = "",is_active = "",
      unic_customer = "", price_per_ltr = "", entry_type = "", entry_price = "",  user_group_id = "", father_name = "",
      adhar = "", address = "", accountNo = "", iFSC_Code = "", bankName = "", categorychart_id = "",
      unic_customer_for_mobile = "", firebase_tocan = "";*/
    public String customer_id = "", user_group_id = "", categorychart_id = "", unic_customer_for_mobile = "",
            unic_customer = "", is_active, name = "", father_name = "", phone_number = "", adhar = "",
            village = "", address = "", morning_milk = "", evening_milk = "", price_per_ltr = "", entry_type = "",
            entry_price = "", accountNo = "", iFSC_Code = "", bankName = "", firebase_tocan = "";

    public BuyerMilkCustomerListPojo(String customer_id, String user_group_id, String categorychart_id,
                                     String unic_customer_for_mobile, String unic_customer, String is_active,
                                     String name, String father_name, String phone_number, String adhar,
                                     String village, String address, String morning_milk, String evening_milk,
                                     String price_per_ltr, String entry_type, String entry_price, String accountNo,
                                     String iFSC_Code, String bankName, String firebase_tocan) {
        this.customer_id = customer_id;
        this.user_group_id = user_group_id;
        this.categorychart_id = categorychart_id;
        this.unic_customer_for_mobile = unic_customer_for_mobile;
        this.unic_customer = unic_customer;
        this.is_active = is_active;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.adhar = adhar;
        this.village = village;
        this.address = address;
        this.morning_milk = morning_milk;
        this.evening_milk = evening_milk;
        this.price_per_ltr = price_per_ltr;
        this.entry_type = entry_type;
        this.entry_price = entry_price;
        this.accountNo = accountNo;
        this.iFSC_Code = iFSC_Code;
        this.bankName = bankName;
        this.firebase_tocan = firebase_tocan;
    }
}
