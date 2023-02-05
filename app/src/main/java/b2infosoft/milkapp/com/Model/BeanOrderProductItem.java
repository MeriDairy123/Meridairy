package b2infosoft.milkapp.com.Model;

public class BeanOrderProductItem {


    public int product_id = 0, qty = 0;
    public double price = 0, total_price = 0;
    public String order_id = "", product_name = "", category = "", payment_mode = "", image = "", thumb = "",
            description = "";

    public BeanOrderProductItem(int product_id, int qty, double price,
                                double total_price, String order_id,
                                String product_name, String category,
                                String payment_mode, String image,
                                String thumb, String description) {
        this.product_id = product_id;
        this.qty = qty;
        this.price = price;
        this.total_price = total_price;
        this.order_id = order_id;
        this.product_name = product_name;
        this.category = category;
        this.payment_mode = payment_mode;
        this.image = image;
        this.thumb = thumb;
        this.description = description;
    }
}
