package b2infosoft.milkapp.com.Model;

/**
 * Created by Choudhary on 11-May-19.
 */

public class BeanMyAdvItem {
    public String advId = "";
    public String title = "";
    public String price = "";
    public String start_date = "";
    public String end_date = "";
    public String type = "";
    public String cities = "";
    public String description = "";
    public String image = "";
    public String thumb = "";
    public int status = 0;

    public BeanMyAdvItem(String advId, String title, String price, String start_date,
                         String end_date, String type, String cities,
                         String description, String image, String thumb, int status) {
        this.advId = advId;
        this.title = title;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.type = type;
        this.cities = cities;
        this.description = description;
        this.image = image;
        this.thumb = thumb;
        this.status = status;
    }

    public String getAdvId() {
        return advId;
    }

    public void setAdvId(String advId) {
        this.advId = advId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCities() {
        return cities;
    }

    public void setCities(String cities) {
        this.cities = cities;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


}
