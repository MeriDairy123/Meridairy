package b2infosoft.milkapp.com.Dairy.Transactions;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;


public class TransactionTabFragment extends Fragment {
    TabLayout tabs;
    Toolbar toolbar;
    ViewPager viewPager;
    Adapter adapter;
    String strTitle = "";
    AppBarLayout appBarLayout;
    int pagerPos = 0;
    Context mContext;
    private List<String> mFragmentTitleList = new ArrayList<>();


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

        appBarLayout = view.findViewById(R.id.appbar);
        tabs = view.findViewById(R.id.result_tabs);
        viewPager = view.findViewById(R.id.viewpager);


        mContext = getActivity();
        strTitle = Objects.requireNonNull(getActivity()).getResources().getString(R.string.Transaction);

        System.out.println("strTitle>>>>" + strTitle);


        toolbar.setTitle(strTitle);
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
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
                toolbar.setTitle(mFragmentTitleList.get(position) + " " + strTitle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;

    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new UserTransactionFragment(), mContext.getString(R.string.my),
                "my_transaction");
        adapter.addFragment(new UserTransactionFragment(), mContext.getString(R.string.Customer),
                "user_customer");
        adapter.addFragment(new UserExpenceFragment(), mContext.getString(R.string.expenses),
                "expense_transaction");
        viewPager.setAdapter(adapter);


    }

    class Adapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title, String type) {
            Bundle arg = new Bundle();
            arg.putString("title", title);
            arg.putString("type", type);

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

