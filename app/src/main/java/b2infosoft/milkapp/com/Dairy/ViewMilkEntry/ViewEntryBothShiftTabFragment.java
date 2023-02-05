package b2infosoft.milkapp.com.Dairy.ViewMilkEntry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


public class ViewEntryBothShiftTabFragment extends Fragment {
    private final List<String> mFragmentTitleList = new ArrayList<>();
    TabLayout tabs;
    Toolbar toolbar;

    ViewPager viewPager;
    Adapter adapter;
    String strTitle = "";
    AppBarLayout appBarLayout;
    int pagerPos = 0;

    Context mContext;
    BroadcastReceiver receiver;

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
        strTitle = mContext.getResources().getString(R.string.View_All_Entry);

        toolbar.setTitle(strTitle);
        manageToolBar();


        setupViewPager(viewPager);
        tabs.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagerPos = position;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        return view;

    }


    public void manageToolBar() {

        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    // Add Fragments to Tabs
    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new ViewEntryBothShiftOneDayFragment(), mContext.getString(R.string.Seller),
                "3", "buy");
        adapter.addFragment(new ViewEntryBothShiftOneDayFragment(), mContext.getString(R.string.Buyer), "4", "sale");
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

        public void addFragment(Fragment fragment, String title, String userGroupId, String type) {
            Bundle arg = new Bundle();
            arg.putString("title", title);
            arg.putString("type", type);
            arg.putString("user_group_id", userGroupId);

            arg.putString("FromWhere", "tab");
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

