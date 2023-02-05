package b2infosoft.milkapp.com.customer_app.customer_pojo;

/**
 * Created by B2infosoft on 19/07/2019.
 */

public class BeanDairyMilkPlan {

    public String id = "";
    public String product_name = "";
    public String weight = "";
    public String price = "";
    public String status = "";
    public int qty = 0;
    public boolean selected = false;

    public BeanDairyMilkPlan(String id, String product_name, String weight, String price,
                             String status, int qty, boolean selected) {
        this.id = id;
        this.product_name = product_name;
        this.weight = weight;
        this.price = price;
        this.status = status;
        this.qty = qty;
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
