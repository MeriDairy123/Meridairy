package b2infosoft.milkapp.com.Interface;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;

/**
 * Created by Microsoft on 25-July-19.
 */

public interface RefreshSaleEntryList {

    public void refreshSaleEntryList(ArrayList<CustomerSaleMilkEntryList> entryLists);

    public void receiveAmount(int position);

    void onClickEditInAdapter(int position, int ID, int liveId, String Name, String Fat,
                              String Weight, String Rate, String Total, String customerID,
                              String unic_customer, String snf, String clr, String milk_category, int fatSnfCat);

}
