package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 05-Jan-18.
 */

public class AdvBannerPojo {
    public String id = "";
    public String date_from = "";
    public String date_to = "";
    public String title = "";
    public String discription = "";
    public String location = "";
    public String latitude = "";
    public String longitude = "";
    public String adlogo = "";

    public AdvBannerPojo(String id, String date_from, String date_to, String title, String discription, String location, String latitude, String longitude, String adlogo) {
        this.id = id;
        this.date_from = date_from;
        this.date_to = date_to;
        this.title = title;
        this.discription = discription;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adlogo = adlogo;
    }
}
