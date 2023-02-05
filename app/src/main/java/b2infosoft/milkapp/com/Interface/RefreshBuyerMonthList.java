package b2infosoft.milkapp.com.Interface;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BuyerMonthlyListPojo;

/**
 * Created by u on 05-Jan-18.
 */

public interface RefreshBuyerMonthList {

    public void refreshBuyerData(ArrayList<BuyerMonthlyListPojo> MorningmilkHistoryListPojos);
}
