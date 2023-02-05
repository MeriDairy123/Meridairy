package b2infosoft.milkapp.com.customer_app.customer_pojo;

public class BeanViewCalenderItem {
    public int day = 0;
    public String status = "";
    public String plan_name = "", weight = "", price = "", shift = "", qty = "", return_bottal = "";

    public BeanViewCalenderItem(int day, String status) {
        this.day = day;
        this.status = status;
    }

    public BeanViewCalenderItem(int day, String status, String plan_name, String weight, String price, String shift, String qty, String return_bottal) {
        this.day = day;
        this.status = status;
        this.plan_name = plan_name;
        this.weight = weight;
        this.price = price;
        this.shift = shift;
        this.qty = qty;
        this.return_bottal = return_bottal;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getReturn_bottal() {
        return return_bottal;
    }

    public void setReturn_bottal(String return_bottal) {
        this.return_bottal = return_bottal;
    }
}