package b2infosoft.milkapp.com.Model;


import java.util.ArrayList;


public class BeanOrderItem {

    public String order_id = "", transaction_id, order_status,
            order_date, invoice_url, type;

    public double grandtotal;

    public ArrayList<BeanOrderProductItem> mOrderProductList = new ArrayList<>();

    public BeanOrderItem(String order_id, String transaction_id, String order_status,
                         double grandtotal, String order_date, String invoice_url, String type,
                         ArrayList<BeanOrderProductItem> mOrderProductList) {
        this.order_id = order_id;
        this.transaction_id = transaction_id;
        this.order_status = order_status;
        this.grandtotal = grandtotal;
        this.order_date = order_date;
        this.invoice_url = invoice_url;
        this.type = type;
        this.mOrderProductList = mOrderProductList;
    }

}
