package b2infosoft.milkapp.com.Model;

public class BeanAddProductItem {
    String id = "", name = "", item_weight = "", discription_product = "", item_group = "", group_name = "",
            item_brand = "", brand_name = "", item_unit = "", unit_name = "", item_code = "", images = "", created_at = "", plan_status = "0";
    int low_stock_alert = 0, initial_quantity = 0, weightc = 0, tax_check = 0;
    double opening_rate = 0, opening_amt = 0, price = 0, sales_price = 0, tax = 0;

    public BeanAddProductItem(String id, String name, String item_weight, String discription_product,
                              String item_group, String group_name, String item_brand,
                              String brand_name, String item_unit, String unit_name, String item_code, String images, String created_at,
                              int low_stock_alert, int initial_quantity, int weightc, int tax_check, double opening_rate,
                              double opening_amt, double price, double sales_price, double tax, String plan_status) {
        this.id = id;
        this.name = name;
        this.item_weight = item_weight;
        this.discription_product = discription_product;
        this.item_group = item_group;
        this.group_name = group_name;
        this.item_brand = item_brand;
        this.brand_name = brand_name;
        this.item_unit = item_unit;
        this.unit_name = unit_name;
        this.item_code = item_code;
        this.images = images;
        this.created_at = created_at;
        this.low_stock_alert = low_stock_alert;
        this.initial_quantity = initial_quantity;
        this.weightc = weightc;
        this.tax_check = tax_check;
        this.opening_rate = opening_rate;
        this.opening_amt = opening_amt;
        this.price = price;
        this.sales_price = sales_price;
        this.tax = tax;
        this.plan_status = plan_status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem_weight() {
        return item_weight;
    }

    public void setItem_weight(String item_weight) {
        this.item_weight = item_weight;
    }

    public String getDiscription_product() {
        return discription_product;
    }

    public void setDiscription_product(String discription_product) {
        this.discription_product = discription_product;
    }

    public String getItem_group() {
        return item_group;
    }

    public void setItem_group(String item_group) {
        this.item_group = item_group;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getItem_brand() {
        return item_brand;
    }

    public void setItem_brand(String item_brand) {
        this.item_brand = item_brand;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getItem_unit() {
        return item_unit;
    }

    public void setItem_unit(String item_unit) {
        this.item_unit = item_unit;
    }

    public String getUnit_name() {
        return unit_name;
    }

    public void setUnit_name(String unit_name) {
        this.unit_name = unit_name;
    }

    public String getItem_code() {
        return item_code;
    }

    public void setItem_code(String item_code) {
        this.item_code = item_code;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getLow_stock_alert() {
        return low_stock_alert;
    }

    public void setLow_stock_alert(int low_stock_alert) {
        this.low_stock_alert = low_stock_alert;
    }

    public int getInitial_quantity() {
        return initial_quantity;
    }

    public void setInitial_quantity(int initial_quantity) {
        this.initial_quantity = initial_quantity;
    }

    public int getWeightc() {
        return weightc;
    }

    public void setWeightc(int weightc) {
        this.weightc = weightc;
    }

    public int getTax_check() {
        return tax_check;
    }

    public void setTax_check(int tax_check) {
        this.tax_check = tax_check;
    }

    public double getOpening_rate() {
        return opening_rate;
    }

    public void setOpening_rate(double opening_rate) {
        this.opening_rate = opening_rate;
    }

    public double getOpening_amt() {
        return opening_amt;
    }

    public void setOpening_amt(double opening_amt) {
        this.opening_amt = opening_amt;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSales_price() {
        return sales_price;
    }

    public void setSales_price(double sales_price) {
        this.sales_price = sales_price;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getPlan_status() {
        return plan_status;
    }

    public void setPlan_status(String plan_status) {
        this.plan_status = plan_status;
    }
}
