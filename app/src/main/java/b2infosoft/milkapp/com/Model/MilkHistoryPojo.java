package b2infosoft.milkapp.com.Model;

/**
 * Created by b2andro on 11/18/2017.
 */

public class MilkHistoryPojo {

    public String total_milk = "";
    public String total_price = "";
    public String total_fat = "";
    public String dairy_id = "";
    public String entry_date = "";
    public String session = "";

    public MilkHistoryPojo(String total_milk, String total_price, String total_fat, String dairy_id, String entry_date, String session_) {
        this.total_milk = total_milk;
        this.total_price = total_price;
        this.total_fat = total_fat;
        this.dairy_id = dairy_id;
        this.entry_date = entry_date;
        this.session = session_;
    }


}
