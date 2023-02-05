package b2infosoft.milkapp.com.ShareAds_Animal;

/**
 * Created by Choudhary on 24-10-2018.
 */

public class BeanUploadImage {

    String name;
    String path;

    public BeanUploadImage(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
