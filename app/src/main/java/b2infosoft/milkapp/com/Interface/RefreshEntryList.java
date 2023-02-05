package b2infosoft.milkapp.com.Interface;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.CustomerListPojo;

/**
 * Created by Microsoft on 25-Aug-17.
 */

public interface RefreshEntryList {

    public void refreshList(ArrayList<CustomerEntryListPojo> viewEntryPojoArrayList);

    public void refreshCustomerList(ArrayList<CustomerListPojo> viewEntryPojoArrayList);

}
