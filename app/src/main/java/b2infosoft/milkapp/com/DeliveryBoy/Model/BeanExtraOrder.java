package b2infosoft.milkapp.com.DeliveryBoy.Model;

public class BeanExtraOrder {
    public String id = "";
    public String user_id = "";
    public String order_id = "";
    public String order_date = "";
    public String product_id = "";
    public String product_name = "";
    public Integer qty = 0;
    public Integer price = 0;
    public Integer total_price = 0;
    public String payment_mode = "";
    public String status = "";
    public String image = "";

    public BeanExtraOrder(String id, String user_id, String order_id, String order_date, String product_id, String product_name,
                          Integer qty, Integer price, Integer total_price, String payment_mode, String status, String image) {
        this.id = id;
        this.user_id = user_id;
        this.order_id = order_id;
        this.order_date = order_date;
        this.product_id = product_id;
        this.product_name = product_name;
        this.qty = qty;
        this.price = price;
        this.total_price = total_price;
        this.payment_mode = payment_mode;
        this.status = status;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getTotal_price() {
        return total_price;
    }

    public void setTotal_price(Integer total_price) {
        this.total_price = total_price;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
