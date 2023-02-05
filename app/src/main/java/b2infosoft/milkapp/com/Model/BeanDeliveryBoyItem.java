package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;

/**
 * Created by Choudhary on 02/08/2019.
 */

public class BeanDeliveryBoyItem {

    public String id = "";
    public String name = "";
    public String father_name = "";
    public String phone_number = "";
    public String address = "";
    public int status = 0;
    public double latitude = 0, longitude = 0;

    public ArrayList<BeanDeliveryUserListItem> deliveryUserListItems;


    public BeanDeliveryBoyItem(String id, String name, String father_name, String phone_number, String address, int status, double latitude, double longitude, ArrayList<BeanDeliveryUserListItem> deliveryUserListItems) {
        this.id = id;
        this.name = name;
        this.father_name = father_name;
        this.phone_number = phone_number;
        this.address = address;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.deliveryUserListItems = deliveryUserListItems;
    }
}
