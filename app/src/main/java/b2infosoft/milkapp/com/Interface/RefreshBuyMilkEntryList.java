package b2infosoft.milkapp.com.Interface;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;

/**
 * Created by Microsoft on 25-July-19.
 */

public interface RefreshBuyMilkEntryList {

    void refreshList(ArrayList<CustomerEntryListPojo> viewEntryPojoArrayList);

    void payReceive(int position, String from);

    void onClickEditInAdapter(int position, int id, int liveId,String milk_entry_unicid ,String Name, String Fat,
                              String Weight, String Rate, String Total, String customerID,
                              String unic_customer, String snf, String clr, String milk_category, int fatSnfCategory);


}
