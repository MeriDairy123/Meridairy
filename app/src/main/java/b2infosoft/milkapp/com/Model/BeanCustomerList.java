package b2infosoft.milkapp.com.Model;

/**
 * Created by B2infosoft on 03/03/2019.
 */

public class BeanCustomerList {


    public String id = "";
    public String name = "";
    public String father_name = "";
    public String amount = "";
    public String adhar = "";
    public String phone_number = "";
    public String address = "";
    public String unic_customer = "";
    public String firebase_tocan = "";
    public String entry_type = "";
    public String entry_price = "";
    public String user_group_id = "";
    public String categorychart_id = "";
    public String unic_customer_for_mobile = "";

    public BeanCustomerList(String id, String name, String father_name, String amount,
                            String adhar, String phone_number, String address, String unic_customer,
                            String firebase_tocan, String entry_type, String entry_price, String user_group_id,
                            String categorychart_id, String unic_customer_for_mobile) {
        this.id = id;
        this.name = name;
        this.father_name = father_name;
        this.amount = amount;
        this.adhar = adhar;
        this.phone_number = phone_number;
        this.address = address;
        this.unic_customer = unic_customer;
        this.firebase_tocan = firebase_tocan;
        this.entry_type = entry_type;
        this.entry_price = entry_price;
        this.user_group_id = user_group_id;
        this.categorychart_id = categorychart_id;
        this.unic_customer_for_mobile = unic_customer_for_mobile;
    }


    public BeanCustomerList(String id, String name, String unic_customer, String user_group_id) {
        this.id = id;
        this.name = name;
        this.unic_customer = unic_customer;
        this.user_group_id = user_group_id;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAdhar() {
        return adhar;
    }

    public void setAdhar(String adhar) {
        this.adhar = adhar;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public String getFirebase_tocan() {
        return firebase_tocan;
    }

    public void setFirebase_tocan(String firebase_tocan) {
        this.firebase_tocan = firebase_tocan;
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
}
