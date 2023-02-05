package b2infosoft.milkapp.com.Model;

/**
 * Created by Choudhary on 11-May-19.
 */

public class BeanCityAdvItem {
    public String id = "";
    public String cityName = "";
    public boolean selected = false;
    public int price = 0;
    public int city_count = 0;

    public BeanCityAdvItem(String id, String cityName, boolean selected, int price, int city_count) {
        this.id = id;
        this.cityName = cityName;
        this.selected = selected;
        this.price = price;
        this.city_count = city_count;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


    public int getCity_count() {
        return city_count;
    }

    public void setCity_count(int city_count) {
        this.city_count = city_count;
    }
}
