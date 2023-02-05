package b2infosoft.milkapp.com.Model;

public class BeanPurchaseItemOld {

    String invoice_id = "", invoice_number = "", category_id = "", product_id = "", customer_id = "",
            name = "", msg_on_statement = "", item_brand = "", invoice_date = "";
    int qty = 0, tax_check = 0, discount_check = 0;
    double weight = 0, rate = 0, sgst_inv = 0, cgst_inv = 0, igst_inv = 0, item_discount = 0,
            discount_invo = 0, cash_discount = 0,
            other_charg = 0, net_amount = 0, taxable_invo = 0, gross = 0, tax = 0;

    public BeanPurchaseItemOld(String invoice_id, String invoice_number, String category_id, String product_id, String customer_id,
                               String name, String msg_on_statement, String item_brand, String invoice_date, int qty, int tax_check,
                               int discount_check, double weight, double rate, double sgst_inv, double cgst_inv, double igst_inv,
                               double item_discount, double discount_invo, double cash_discount,
                               double other_charg, double net_amount, double taxable_invo, double gross, double tax) {
        this.invoice_id = invoice_id;
        this.invoice_number = invoice_number;
        this.category_id = category_id;
        this.product_id = product_id;
        this.customer_id = customer_id;
        this.name = name;
        this.msg_on_statement = msg_on_statement;
        this.item_brand = item_brand;

        this.invoice_date = invoice_date;
        this.qty = qty;
        this.tax_check = tax_check;
        this.discount_check = discount_check;
        this.weight = weight;
        this.rate = rate;
        this.sgst_inv = sgst_inv;
        this.cgst_inv = cgst_inv;
        this.igst_inv = igst_inv;

        this.item_discount = item_discount;
        this.discount_invo = discount_invo;
        this.cash_discount = cash_discount;
        this.other_charg = other_charg;
        this.net_amount = net_amount;
        this.taxable_invo = taxable_invo;
        this.gross = gross;
        this.tax = tax;
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

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMsg_on_statement() {
        return msg_on_statement;
    }

    public void setMsg_on_statement(String msg_on_statement) {
        this.msg_on_statement = msg_on_statement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_brand() {
        return item_brand;
    }

    public void setItem_brand(String item_brand) {
        this.item_brand = item_brand;
    }


    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getTax_check() {
        return tax_check;
    }

    public void setTax_check(int tax_check) {
        this.tax_check = tax_check;
    }

    public int getDiscount_check() {
        return discount_check;
    }

    public void setDiscount_check(int discount_check) {
        this.discount_check = discount_check;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
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


    public double getItem_discount() {
        return item_discount;
    }

    public void setItem_discount(double item_discount) {
        this.item_discount = item_discount;
    }

    public double getDiscount_invo() {
        return discount_invo;
    }

    public void setDiscount_invo(double discount_invo) {
        this.discount_invo = discount_invo;
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

    public double getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(double net_amount) {
        this.net_amount = net_amount;
    }

    public double getTaxable_invo() {
        return taxable_invo;
    }

    public void setTaxable_invo(double taxable_invo) {
        this.taxable_invo = taxable_invo;
    }

    public double getGross() {
        return gross;
    }

    public void setGross(double gross) {
        this.gross = gross;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }
}
