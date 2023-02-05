package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;

public class BeanRecievedOrderItem {

    public String order_id = "", transaction_id = "", order_status = "",
            order_date = "", type = "", invoice_url = "",
            customer_id = "", name = "", phone_number = "";
    public ArrayList<BeanOrderProductItem> mOrderProductList;

    public double grandtotal = 0;

    public BeanRecievedOrderItem(String order_id, String transaction_id, String order_status,
                                 String order_date, String type, String invoice_url,
                                 String customer_id, String name, String phone_number, double grandtotal, ArrayList<BeanOrderProductItem> mOrderProductList) {
        this.order_id = order_id;
        this.transaction_id = transaction_id;
        this.order_status = order_status;
        this.order_date = order_date;
        this.type = type;
        this.invoice_url = invoice_url;
        this.name = name;
        this.customer_id = customer_id;
        this.phone_number = phone_number;
        this.grandtotal = grandtotal;
        this.mOrderProductList = mOrderProductList;
    }
/*"id": 112,"order_id": "ORDER100085U75203","price": "1.00",
          "transaction_id": "20190822111212800110168022184122083",
          "status": "complete","user_id": "75203","type": "product","bankname": "ASBI",
          "payment_mode": "NB","bank_txn_id": "108652146180","currency": "INR",
          "gateway_name": "ASBI","gift_id": "0",
          "order_status": "canceled","created_at": "2019-08-22 17:20:13",
          "updated_at": "2019-08-23 11:16:27","phone_number": "9772631367",
          "name": "Bharti out buyer"*/
}
