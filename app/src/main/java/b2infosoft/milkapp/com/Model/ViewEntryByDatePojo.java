package b2infosoft.milkapp.com.Model;

/**
 * Created by Microsoft on 22-Aug-17.
 */

public class ViewEntryByDatePojo {
    public String id = "", customer_id = "", dairy_id = "", entry_date = "",
            shift = "", name = "", unic_customer = "";
    public double fat = 0, snf = 0, total_milk = 0, per_kg_price = 0, bonus = 0, total_price = 0;


    public ViewEntryByDatePojo(String id, String customer_id, String dairy_id, String entry_date, String shift, String name, String unic_customer,
                               double fat, double snf, double total_milk, double per_kg_price, double bonus, double total_price) {
        this.id = id;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.entry_date = entry_date;
        this.shift = shift;
        this.name = name;
        this.unic_customer = unic_customer;
        this.fat = fat;
        this.snf = snf;
        this.total_milk = total_milk;
        this.per_kg_price = per_kg_price;
        this.bonus = bonus;
        this.total_price = total_price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDairy_id() {
        return dairy_id;
    }

    public void setDairy_id(String dairy_id) {
        this.dairy_id = dairy_id;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSnf() {
        return snf;
    }

    public void setSnf(double snf) {
        this.snf = snf;
    }

    public double getTotal_milk() {
        return total_milk;
    }

    public void setTotal_milk(double total_milk) {
        this.total_milk = total_milk;
    }

    public double getPer_kg_price() {
        return per_kg_price;
    }

    public void setPer_kg_price(double per_kg_price) {
        this.per_kg_price = per_kg_price;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }
}
