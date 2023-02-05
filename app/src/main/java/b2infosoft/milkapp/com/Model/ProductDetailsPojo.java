package b2infosoft.milkapp.com.Model;


/**
 * Created by u on 27-Mar-18.
 */

public class ProductDetailsPojo {

    public String customer_id = "";
    public String transactions_id = "";
    public String products_id = "";
    public String products_name = "";
    public String products_qty = "";
    public String products_total_price = "";
    public String unic_customer = "";
    public String customer_name = "";
    public String phone_number = "";
    public String transactions_date = "";

    public ProductDetailsPojo(String customer_id, String transactions_id, String products_id, String products_name, String products_qty, String products_total_price, String unic_customer, String customer_name, String phone_number, String transactions_date) {
        this.customer_id = customer_id;
        this.transactions_id = transactions_id;
        this.products_id = products_id;
        this.products_name = products_name;
        this.products_qty = products_qty;
        this.products_total_price = products_total_price;
        this.unic_customer = unic_customer;
        this.customer_name = customer_name;
        this.phone_number = phone_number;
        this.transactions_date = transactions_date;
    }


}
