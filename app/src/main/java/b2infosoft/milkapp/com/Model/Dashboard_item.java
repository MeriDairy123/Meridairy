package b2infosoft.milkapp.com.Model;

/**
 * Created by Choudhary  on 03/26/2019.
 */

public class Dashboard_item {
    String id = "";
    String name;
    String count;
    Integer image;
    String Color;

    public Dashboard_item(String id, String name, String count, Integer image, String color) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.image = image;
        Color = color;
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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }
}
