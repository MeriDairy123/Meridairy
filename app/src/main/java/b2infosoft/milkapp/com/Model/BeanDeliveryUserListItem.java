package b2infosoft.milkapp.com.Model;

/**
 * Created by Choudhary on 03/08/2019.
 */

public class BeanDeliveryUserListItem {

    public
    String id = "",
            uniq_customer = "",
            name = "",
            phone_number = "";
    public int status = 0;

    public BeanDeliveryUserListItem(String id, String uniq_customer, String name, String phone_number, int status) {
        this.id = id;
        this.uniq_customer = uniq_customer;
        this.name = name;
        this.phone_number = phone_number;
        this.status = status;
    }
}
