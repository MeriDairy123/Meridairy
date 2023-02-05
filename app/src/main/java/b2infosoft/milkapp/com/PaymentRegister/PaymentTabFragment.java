package b2infosoft.milkapp.com.PaymentRegister;

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

import b2infosoft.milkapp.com.Dairy.Invoice.InvoiceTabFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentUserListFragment;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;


public class PaymentTabFragment extends Fragment {
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
        strTitle = getActivity().getResources().getString(R.string.Payment_Register);

        toolbar.setTitle(strTitle);
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.invoice));
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goNextFragmentWithBackStack(mContext, new InvoiceTabFragment());
            }
        });
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

    private void manageToolBar() {

        toolbar.setNavigationIcon(R.drawable.back_arrow);
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

        adapter.addFragment(new b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentUserListFragment(), mContext.getString(R.string.Seller), "3", "");
        adapter.addFragment(new PaymentUserListFragment(), mContext.getString(R.string.Buyer), "4", "");
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

        public void addFragment(Fragment fragment, String title, String user_groupId, String urlAdd) {
            Bundle bundle = new Bundle();
            bundle.putString("FromWhere", "tab");
            bundle.putString("title", title);
            bundle.putString("user_group_id", user_groupId);
            bundle.putString("url_add", urlAdd);
            fragment.setArguments(bundle);
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}

