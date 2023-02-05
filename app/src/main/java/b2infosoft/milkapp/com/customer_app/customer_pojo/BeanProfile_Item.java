package b2infosoft.milkapp.com.customer_app.customer_pojo;

/**
 * Created by Choudhary Computer on 24/06/2019.
 */

public class BeanProfile_Item {
    String strLable;
    String strname;

    public BeanProfile_Item(String strLable, String strname) {
        this.strLable = strLable;
        this.strname = strname;
    }

    public String getStrLable() {
        return strLable;
    }

    public void setStrLable(String strLable) {
        this.strLable = strLable;
    }

    public String getStrname() {
        return strname;
    }

    public void setStrname(String strname) {
        this.strname = strname;
    }
}
