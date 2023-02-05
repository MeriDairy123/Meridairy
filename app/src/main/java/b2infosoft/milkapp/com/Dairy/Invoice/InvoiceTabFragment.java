package b2infosoft.milkapp.com.Dairy.Invoice;

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
import b2infosoft.milkapp.com.appglobal.Constant;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;


public class InvoiceTabFragment extends Fragment {
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
        strTitle = getActivity().getResources().getString(R.string.invoice);
        System.out.println("strTitle>>>>" + strTitle);
        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.invoice));
        toolbar.setTitle(strTitle);
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));

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
                toolbar.setTitle(strTitle + " " + mFragmentTitleList.get(position));
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

        adapter.addFragment(new SallerInvoiceListFragment(), mContext.getString(R.string.Seller),
                Constant.getPayVoucherListAPI, Constant.addPayVoucherAPI);
        adapter.addFragment(new BuyerInvoiceListFragment(), mContext.getString(R.string.Buyer), Constant.getRecieptVoucherListAPI, Constant.addRecieptVoucherAPI);

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new GenerateInvoiceFragment();
                Bundle arg = new Bundle();
                arg.putString("type", "add");
                arg.putString("CustomerId", "");

                if (pagerPos == 0) {
                    arg.putString("from", "Seller");
                    arg.putString("user_group_id", "3");
                    arg.putString("title", mContext.getString(R.string.Seller));

                } else {
                    arg.putString("from", "Buyer");
                    arg.putString("user_group_id", "4");
                    arg.putString("title", mContext.getString(R.string.Buyer));

                }
                fragment.setArguments(arg);
                goNextFragmentWithBackStack(mContext, fragment);
            }
        });

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

        public void addFragment(Fragment fragment, String title, String urlList, String urlAdd) {
            Bundle arg = new Bundle();
            arg.putString("title", title);
            arg.putString("url_list", urlList);
            arg.putString("url_add", urlAdd);
            arg.putString("from", "tab");
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

