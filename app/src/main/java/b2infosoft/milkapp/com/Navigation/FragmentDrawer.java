package b2infosoft.milkapp.com.Navigation;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import b2infosoft.milkapp.com.Advertisement.fragmentMyAdvertisement;
import b2infosoft.milkapp.com.BuyPlan.PlanTabFragment;
import b2infosoft.milkapp.com.Dairy.BulkMilkSale.MyRequestFragment;
import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Dairy.Invoice.BuyerInvoiceListFragment;
import b2infosoft.milkapp.com.Dairy.Product.AddBuyerMilkPlanFragment;
import b2infosoft.milkapp.com.Dairy.Product.ViewSellProductListFragment;
import b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment.ProductDashboard;
import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.BuyerBhugtanFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.ReceiveCashFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.DeliveryBoyListFragment;
import b2infosoft.milkapp.com.Dairy.TermsConditionFragment;
import b2infosoft.milkapp.com.Dairy.Transactions.TransactionTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewMilkEntryTabFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewMilkHistoryFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.CustomerOrderRecievedFragment;
import b2infosoft.milkapp.com.Shopping.Myorder_Fragment;
import b2infosoft.milkapp.com.useful.RecyclerTouchListener;


public class FragmentDrawer extends Fragment {
    Context mContext;
    String[] menuArray;
    String App_Version = "";
    InputMethodManager inputMethodManager;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private List<String> titles = new ArrayList<>();
    private FragmentDrawerListener drawerListener;
    private ImageView prof_img;
    private TextView tv_NavUsername, tvAppVersion;

    public FragmentDrawer() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void refreshItems(Boolean x) {
        adapter.x = x;
        adapter.notifyDataSetChanged();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflating view layout
        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        recyclerView = layout.findViewById(R.id.drawerList);
        tvAppVersion = layout.findViewById(R.id.tvAppVersion);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mContext = getActivity();
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            App_Version = pInfo.versionName;
            System.out.println("App_Version========" + App_Version);

            tvAppVersion.setText("APP Version " + App_Version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        titles = new ArrayList<>();
        menuArray = getResources().getStringArray(R.array.nav_drawer_array);
        titles = Arrays.asList(menuArray);
        System.out.println("titles==size==" + titles.size());
        adapter = new NavigationDrawerAdapter(mContext, recyclerView, getData());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        prof_img = layout.findViewById(R.id.prof_img);

        prof_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        tv_NavUsername = layout.findViewById(R.id.tv_NavUsername);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                drawerListener.onDrawerItemSelected(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return layout;
    }

    public List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();
        List<NavInnerItem> subMenu;
        int x = 0;
        for (int i = 0; i < titles.size(); i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            subMenu = new ArrayList<>();
            navItem.setTitle(titles.get(i));
            switch (i) {
                case 0:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_home);
                    navItem.setFragment(new DairyDeshboardFragment());
                    break;
                case 1:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_user_profile_dairy);
                    break;
                case 2:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_advertising);
                    navItem.setFragment(new fragmentMyAdvertisement());
                    break;

                case 3:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_upgrade_account);
                    navItem.setFragment(new PlanTabFragment());
                    break;
                case 4:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_user_request);
                    navItem.setFragment(new MyRequestFragment());
                    break;
                case 5:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_delivery_man);
                    navItem.setFragment(new DeliveryBoyListFragment());
                    break;

                case 6:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_view_entry);
                    navItem.setFragment(new ViewMilkEntryTabFragment());

                    break;

                case 7:
                    navItem.setSubMenu(null);
                    navItem.setThumbnail(R.drawable.ic_milk_history);
                    navItem.setFragment(new ViewMilkHistoryFragment());
                    break;
                case 8:

                    subMenu.add(new NavInnerItem(mContext.getString(R.string.BUYER_BHUGTAN), R.drawable.ic_arrow_right, new BuyerBhugtanFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.RECEIVE_CASH), R.drawable.ic_arrow_right, new ReceiveCashFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.Buyer) + " " + mContext.getString(R.string.invoice), R.drawable.ic_arrow_right, new BuyerInvoiceListFragment()));
                    navItem.setSubMenu(subMenu);
                    navItem.setThumbnail(R.drawable.ic_buyer);
                    break;
                case 9:
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.MyOrder), R.drawable.ic_arrow_right, new Myorder_Fragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.Customers) + " " + mContext.getString(R.string.Order), R.drawable.ic_arrow_right, new CustomerOrderRecievedFragment()));
                    navItem.setThumbnail(R.drawable.ic_shopping);
                    navItem.setSubMenu(subMenu);
                    break;
                case 10:
                    //  subMenu.add(new NavInnerItem(mContext.getString(R.string.Add_Product), R.drawable.ic_arrow_right, new AddProductFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.Add_Milk_Plan), R.drawable.ic_arrow_right, new AddBuyerMilkPlanFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.Selling_History), R.drawable.ic_arrow_right, new ViewSellProductListFragment()));
                    navItem.setThumbnail(R.drawable.products);
                    navItem.setSubMenu(subMenu);
                    break;
                case 11:
                    navItem.setThumbnail(R.drawable.ic_product_sale_buy);
                    navItem.setFragment(new ProductDashboard());
                    navItem.setSubMenu(null);
                    break;

               /* case 12:
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.Update_Rate), R.drawable.ic_arrow_right, new RateUpdateTabFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.print_sms), R.drawable.ic_arrow_right, new SettingUpdateFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.SNF_FAT_Chart), R.drawable.ic_arrow_right, new ChartTabFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.chart) + " " + mContext.getString(R.string.Category), R.drawable.ic_arrow_right, new ChartCategoryFragment()));
                    subMenu.add(new NavInnerItem(mContext.getString(R.string.clrSetting), R.drawable.ic_arrow_right, new CLRSettingFragment()));

                    subMenu.add(new NavInnerItem(mContext.getString(R.string.ERASE_MILK_HISTORY), R.drawable.ic_arrow_right, new EraseEntryFragment()));
                    navItem.setThumbnail(R.drawable.ic_settings);
                    navItem.setSubMenu(subMenu);
                    break;*/

                case 12:
                    navItem.setThumbnail(R.drawable.ic_wallet);
                    navItem.setFragment(new TransactionTabFragment());
                    navItem.setSubMenu(null);
                    break;
                case 13:
                    navItem.setThumbnail(R.drawable.ic_document);
                    navItem.setFragment(new TermsConditionFragment());
                    navItem.setSubMenu(null);
                    break;

                case 14:
                    navItem.setThumbnail(R.drawable.ic_share);
                    navItem.setSubMenu(null);
                    break;
                    case 15:
                    navItem.setThumbnail(R.drawable.ic_logout_customer);
                    navItem.setSubMenu(null);
                    break;
            }

            data.add(navItem);

        }
        return data;
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }
}
