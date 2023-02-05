package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;

public class BeanPurchaseItem {

    private String invoice_id = "", invoice_number = "", customer_id = "", unicCustomer = "", user_name = "",
            msg_on_statement = "", invoice_date = "";
    private double sgst_inv = 0, cgst_inv = 0, igst_inv = 0, total_discount = 0,
            cash_discount = 0, other_charg = 0, cash_div = 0, total_taxable_value = 0, subtotal = 0, net_amount = 0, balance_invo = 0;
    private ArrayList<BeanPurchInvoiceProductItem> invoiceProductItems = new ArrayList<>();

    public BeanPurchaseItem(String invoice_id, String invoice_number, String customer_id, String unicCustomer, String user_name,
                            String msg_on_statement, String invoice_date, double sgst_inv, double cgst_inv,
                            double igst_inv, double total_discount, double cash_discount, double other_charg, double cash_div,
                            double total_taxable_value, double subtotal, double net_amount, double balance_invo,
                            ArrayList<BeanPurchInvoiceProductItem> invoiceProductItems) {
        this.invoice_id = invoice_id;
        this.invoice_number = invoice_number;
        this.customer_id = customer_id;
        this.unicCustomer = unicCustomer;
        this.user_name = user_name;
        this.msg_on_statement = msg_on_statement;
        this.invoice_date = invoice_date;
        this.sgst_inv = sgst_inv;
        this.cgst_inv = cgst_inv;
        this.igst_inv = igst_inv;
        this.total_discount = total_discount;

        this.cash_discount = cash_discount;
        this.cash_div = cash_div;
        this.other_charg = other_charg;
        this.total_taxable_value = total_taxable_value;
        this.subtotal = subtotal;
        this.net_amount = net_amount;
        this.balance_invo = balance_invo;
        this.invoiceProductItems = invoiceProductItems;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getUnicCustomer() {
        return unicCustomer;
    }

    public void setUnicCustomer(String unicCustomer) {
        this.unicCustomer = unicCustomer;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMsg_on_statement() {
        return msg_on_statement;
    }

    public void setMsg_on_statement(String msg_on_statement) {
        this.msg_on_statement = msg_on_statement;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public double getSgst_inv() {
        return sgst_inv;
    }

    public void setSgst_inv(double sgst_inv) {
        this.sgst_inv = sgst_inv;
    }

    public double getCgst_inv() {
        return cgst_inv;
    }

    public void setCgst_inv(double cgst_inv) {
        this.cgst_inv = cgst_inv;
    }

    public double getIgst_inv() {
        return igst_inv;
    }

    public void setIgst_inv(double igst_inv) {
        this.igst_inv = igst_inv;
    }

    public double getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(double total_discount) {
        this.total_discount = total_discount;
    }


    public double getCash_discount() {
        return cash_discount;
    }

    public void setCash_discount(double cash_discount) {
        this.cash_discount = cash_discount;
    }

    public double getOther_charg() {
        return other_charg;
    }

    public void setOther_charg(double other_charg) {
        this.other_charg = other_charg;
    }

    public double getTotal_taxable_value() {
        return total_taxable_value;
    }

    public void setTotal_taxable_value(double total_taxable_value) {
        this.total_taxable_value = total_taxable_value;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(double net_amount) {
        this.net_amount = net_amount;
    }

    public double getCash_div() {
        return cash_div;
    }

    public void setCash_div(double cash_div) {
        this.cash_div = cash_div;
    }

    public double getBalance_invo() {
        return balance_invo;
    }

    public void setBalance_invo(double balance_invo) {
        this.balance_invo = balance_invo;
    }

    public ArrayList<BeanPurchInvoiceProductItem> getInvoiceProductItems() {
        return invoiceProductItems;
    }

    public void setInvoiceProductItems(ArrayList<BeanPurchInvoiceProductItem> invoiceProductItems) {
        this.invoiceProductItems = invoiceProductItems;
    }
}
