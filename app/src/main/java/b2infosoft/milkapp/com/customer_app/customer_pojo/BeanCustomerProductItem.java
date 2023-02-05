package b2infosoft.milkapp.com.customer_app.customer_pojo;

/**
 * Created by B2infosoft on 15/02/2019.
 */

public class BeanCustomerProductItem {

    public String id = "";
    public String product_name = "";
    public String weight = "";
    public String image = "";
    public double price = 0;
    public int qty = 0;


    public BeanCustomerProductItem(String id, String product_name, String weight, String image,
                                   double price, int qty) {
        this.id = id;
        this.product_name = product_name;
        this.weight = weight;
        this.image = image;
        this.price = price;
        this.qty = qty;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }


}
