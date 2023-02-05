package b2infosoft.milkapp.com.Model;

public class BeanPurchInvoiceProductItem {
    int qty = 0, tax_check = 0, discount_check = 0;
    private String product_id = "", category_id = "",
            name = "", product_group_item = "", item_brand = "";
    private double weight = 0, net_weight = 0, rate = 0, item_discount = 0, discountPrice = 0, tax = 0, taxPrice = 0,
            taxable_invo = 0, gross = 0;

    public BeanPurchInvoiceProductItem(String product_id, String category_id, String name, String product_group_item, String item_brand,
                                       int qty, int tax_check, int discount_check, double weight, double net_weight, double rate, double item_discount, double discountPrice,
                                       double tax, double taxPrice, double taxable_invo, double gross) {
        this.product_id = product_id;
        this.category_id = category_id;
        this.name = name;
        this.product_group_item = product_group_item;
        this.item_brand = item_brand;
        this.qty = qty;
        this.tax_check = tax_check;
        this.discount_check = discount_check;
        this.weight = weight;
        this.net_weight = net_weight;
        this.rate = rate;
        this.item_discount = item_discount;
        this.discountPrice = discountPrice;
        this.tax = tax;
        this.taxPrice = taxPrice;
        this.taxable_invo = taxable_invo;
        this.gross = gross;
    }


    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_group_item() {
        return product_group_item;
    }

    public void setProduct_group_item(String product_group_item) {
        this.product_group_item = product_group_item;
    }

    public String getItem_brand() {
        return item_brand;
    }

    public void setItem_brand(String item_brand) {
        this.item_brand = item_brand;
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

    public double getNet_weight() {
        return net_weight;
    }

    public void setNet_weight(double net_weight) {
        this.net_weight = net_weight;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getItem_discount() {
        return item_discount;
    }

    public void setItem_discount(double item_discount) {
        this.item_discount = item_discount;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(double taxPrice) {
        this.taxPrice = taxPrice;
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
}
