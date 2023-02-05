package b2infosoft.milkapp.com.DeliveryBoy.Model;


import java.util.ArrayList;
import java.util.List;

public class BeanUserItem {

    public String user_id = "", user_name = "", phone_number = "",
            user_email = "", address = "", user_planId = "", dairyId = "",
            milk_planId = "", milk_plan_name = "", milk_entry_date = "",
            milk_shift = "", milk_status_delivery = "", milk_qty = "";
    public Integer milk_weight = 0, milk_price = 0;

    public double latitude = 0, longitude = 0;

    public List<BeanExtraOrder> orderDetailList = new ArrayList<>();
    public List<BeanUserPlan> userPlanLists = new ArrayList<>();

    public BeanUserItem(String user_id, String user_name, String phone_number,
                        String user_email, String address, String user_planId,
                        String dairyId, String milk_planId, String milk_plan_name,
                        String milk_entry_date, String milk_shift,
                        String milk_status_delivery, String milk_qty, Integer milk_weight, Integer milk_price, double latitude, double longitude,
                        List<BeanExtraOrder> orderDetailList, List<BeanUserPlan> userPlanLists) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.phone_number = phone_number;
        this.user_email = user_email;
        this.address = address;
        this.user_planId = user_planId;
        this.dairyId = dairyId;
        this.milk_planId = milk_planId;
        this.milk_plan_name = milk_plan_name;
        this.milk_entry_date = milk_entry_date;
        this.milk_weight = milk_weight;
        this.milk_shift = milk_shift;
        this.milk_status_delivery = milk_status_delivery;
        this.milk_qty = milk_qty;
        this.milk_price = milk_price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderDetailList = orderDetailList;
        this.userPlanLists = userPlanLists;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUser_planId() {
        return user_planId;
    }

    public void setUser_planId(String user_planId) {
        this.user_planId = user_planId;
    }

    public String getDairyId() {
        return dairyId;
    }

    public void setDairyId(String dairyId) {
        this.dairyId = dairyId;
    }

    public String getMilk_planId() {
        return milk_planId;
    }

    public void setMilk_planId(String milk_planId) {
        this.milk_planId = milk_planId;
    }

    public String getMilk_plan_name() {
        return milk_plan_name;
    }

    public void setMilk_plan_name(String milk_plan_name) {
        this.milk_plan_name = milk_plan_name;
    }

    public String getMilk_entry_date() {
        return milk_entry_date;
    }

    public void setMilk_entry_date(String milk_entry_date) {
        this.milk_entry_date = milk_entry_date;
    }

    public String getMilk_shift() {
        return milk_shift;
    }

    public void setMilk_shift(String milk_shift) {
        this.milk_shift = milk_shift;
    }

    public String getMilk_status_delivery() {
        return milk_status_delivery;
    }

    public void setMilk_status_delivery(String milk_status_delivery) {
        this.milk_status_delivery = milk_status_delivery;
    }

    public String getMilk_qty() {
        return milk_qty;
    }

    public void setMilk_qty(String milk_qty) {
        this.milk_qty = milk_qty;
    }

    public Integer getMilk_weight() {
        return milk_weight;
    }

    public void setMilk_weight(Integer milk_weight) {
        this.milk_weight = milk_weight;
    }

    public Integer getMilk_price() {
        return milk_price;
    }

    public void setMilk_price(Integer milk_price) {
        this.milk_price = milk_price;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<BeanExtraOrder> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(List<BeanExtraOrder> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public List<BeanUserPlan> getUserPlanLists() {
        return userPlanLists;
    }

    public void setUserPlanLists(List<BeanUserPlan> userPlanLists) {
        this.userPlanLists = userPlanLists;
    }
}

