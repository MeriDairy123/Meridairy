package b2infosoft.milkapp.com.Dairy.Setting;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.R;


public class SettingTabFragment extends Fragment {
    private final List<String> mFragmentTitleList = new ArrayList<>();
    TabLayout tabs;
    Toolbar toolbar;
    TextView tvAdd;
    ViewPager viewPager;
    Adapter adapter;
    String strTitle = "";
    AppBarLayout appBarLayout;
    int pagerPos = 0;

    Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tab_helper, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        appBarLayout = view.findViewById(R.id.appbar);
        tabs = view.findViewById(R.id.result_tabs);
        viewPager = view.findViewById(R.id.viewpager);
        mContext = getActivity();
        strTitle = getActivity().getResources().getString(R.string.Setting);
        System.out.println("strTitle>>>>" + strTitle);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        toolbar.setTitle(strTitle);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagerPos = position;
                //   toolbar.setTitle(strTitle + " " + mFragmentTitleList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());

        adapter.addFragment(new SettingUpdateFragment(), "setting", mContext.getString(R.string.Print_And_Send_Message));
        adapter.addFragment(new BuyRateFragment(), "setting", mContext.getString(R.string.Milk_Buy));
        adapter.addFragment(new SaleRateFragment(), "setting", mContext.getString(R.string.MILK_Sale));
        adapter.addFragment(new CLRSettingFragment(), "setting", mContext.getString(R.string.CLR));
        adapter.addFragment(new BhugtanSettingFragment(), "seller_", mContext.getString(R.string.SELLER_BHUGTAN));
        adapter.addFragment(new BuyerBhugtanSettingFragment(), "buyer_", mContext.getString(R.string.BUYER_BHUGTAN));
        adapter.addFragment(new EraseBuyMilkEntryFragment(), "setting", mContext.getString(R.string.ERASE_MILK_HISTORY));
        viewPager.setAdapter(adapter);


    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String from, String title) {
            Bundle arg = new Bundle();
            arg.putString("title", title);
            arg.putString("from", from);
            fragment.setArguments(arg);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

