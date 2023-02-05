package b2infosoft.milkapp.com.Notification;

/**
 * Created by Choudhary Computer on 10/07/2019.
 */

public class BeanNotification_Item {
    String id;
    String title;
    String type;
    String description;

    public BeanNotification_Item(String id, String title, String type, String description) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
