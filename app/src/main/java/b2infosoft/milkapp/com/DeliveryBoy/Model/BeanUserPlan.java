package b2infosoft.milkapp.com.DeliveryBoy.Model;

public class BeanUserPlan {
    public String id = "";
    public String product_name = "";
    public String weight = "";
    public double price = 0;

    public BeanUserPlan(String id, String product_name, String weight, double price) {
        this.id = id;
        this.product_name = product_name;
        this.weight = weight;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
