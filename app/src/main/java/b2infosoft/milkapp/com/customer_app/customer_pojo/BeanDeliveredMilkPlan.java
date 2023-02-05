package b2infosoft.milkapp.com.customer_app.customer_pojo;

public class BeanDeliveredMilkPlan {
    public String dairy_id = "", plan_id = "", planName = "", shift = "", weight = "",
            entry_date = "";
    int qty = 0;

    double price = 0, totalPrice = 0;

    public BeanDeliveredMilkPlan(String dairy_id, String plan_id, String planName,
                                 String shift, int qty,
                                 String entry_date, String weight, double price, double totalPrice) {
        this.dairy_id = dairy_id;
        this.plan_id = plan_id;
        this.planName = planName;
        this.shift = shift;
        this.qty = qty;
        this.entry_date = entry_date;
        this.weight = weight;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public String getDairy_id() {
        return dairy_id;
    }

    public void setDairy_id(String dairy_id) {
        this.dairy_id = dairy_id;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}