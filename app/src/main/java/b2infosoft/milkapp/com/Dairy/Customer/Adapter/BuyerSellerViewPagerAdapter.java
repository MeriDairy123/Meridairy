package b2infosoft.milkapp.com.Dairy.Customer.Adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import b2infosoft.milkapp.com.Dairy.Customer.Fragment.FragmentCustomerBuyerList;
import b2infosoft.milkapp.com.Dairy.Customer.Fragment.FragmentCustomerSellerList;


public class BuyerSellerViewPagerAdapter extends FragmentPagerAdapter {

    private static int NumberofTabs = 2;

    public BuyerSellerViewPagerAdapter(FragmentManager manager, int NumberofTabs) {
        super(manager);
        this.NumberofTabs = NumberofTabs;

    }

    @Override
    public Fragment getItem(int position) {

        /*switch (position) {
            case 0:

                return new FragmentCustomerSellerList();
            case 1:

                return new FragmentCustomerBuyerList();
            default:
                return new FragmentCustomerSellerList();


        }*/

        return null;

    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return NumberofTabs;
    }


}
