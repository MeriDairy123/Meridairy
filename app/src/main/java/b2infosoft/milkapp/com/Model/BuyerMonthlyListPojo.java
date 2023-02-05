package b2infosoft.milkapp.com.Model;


/**
 * Created by u on 29-Dec-17.
 */

public class BuyerMonthlyListPojo {
    public String selling_date = "";
    public String customer_id = "";
    public String dairy_id = "";
    public String morning_milk = "";
    public String evening_milk = "";
    public String milk_rate = "";
    public String morning_sell_sts = "";
    public String evening_sell_sts = "";
    public String morning_rate = "";
    public String evening_rate = "";

    public BuyerMonthlyListPojo(String id, String customer_id, String dairy_id, String milk_perkg_price, String milk_total_price, String milk_wt, String morning_sell_sts, String evening_sell_sts, String morning_rate, String evening_rate) {
        this.selling_date = id;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.morning_milk = milk_perkg_price;
        this.evening_milk = milk_total_price;
        this.milk_rate = milk_wt;
        this.morning_sell_sts = morning_sell_sts;
        this.evening_sell_sts = evening_sell_sts;
        this.morning_rate = morning_rate;
        this.evening_rate = evening_rate;
    }
}
