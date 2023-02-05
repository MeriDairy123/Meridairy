package b2infosoft.milkapp.com.customer_app.customer_pojo;


public class BeanInvoiceMonthlyDetailsItem {

    public String id = "", dairy_id = "", user_id = "", month = "", year = "", details = "", type = "", amount = "", weight = "", status = "";


    public BeanInvoiceMonthlyDetailsItem(String id, String dairy_id, String user_id, String month, String year,
                                         String details, String type, String amount, String weight, String status) {
        this.id = id;
        this.dairy_id = dairy_id;
        this.user_id = user_id;
        this.month = month;
        this.year = year;
        this.details = details;
        this.type = type;
        this.amount = amount;
        this.weight = weight;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDairy_id() {
        return dairy_id;
    }

    public void setDairy_id(String dairy_id) {
        this.dairy_id = dairy_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}