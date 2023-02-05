package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfPagerAdapter;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FATListData;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFListData;

public class SnfFatExcelFragment extends Fragment {

    View view;
    private Context mContext;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private SessionManager sessionManager;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SnfPagerAdapter snfPagerAdapter;
    private ArrayList<SnfFatListPojo> snfFatListPojos;
    private ArrayList<String> snfList = new ArrayList<>();
    private ArrayList<String> fatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_snf_fat_excel, container, false);
        snfFatListPojos = new ArrayList<>();
        snfList = new ArrayList<>();
        fatList = new ArrayList<>();
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabs);
        viewPager = view.findViewById(R.id.viewpager);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        if (Constant.FromWhere.equalsIgnoreCase("Cow")) {
            toolbar_title.setText(mContext.getString(R.string.SNF_FAT_CHART) + " " + mContext.getString(R.string.Cow));
        } else {
            toolbar_title.setText(mContext.getString(R.string.SNF_FAT_CHART) + " " + mContext.getString(R.string.Buff));
        }

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        sessionManager = new SessionManager(mContext);
        viewPager = view.findViewById(R.id.viewpager);
        tabLayout = view.findViewById(R.id.tabs);
        setList();


        return view;
    }

    public void setList() {
        snfList = sessionManager.getFATSNFListData(mContext, Key_SNFListData);
        fatList = sessionManager.getFATSNFListData(mContext, Key_FATListData);
        snfFatListPojos = sessionManager.getRateChartList(mContext);

        Constant.snfFatList = new ArrayList<>();
        Constant.snfFatList.clear();
        System.out.println("snfList>>>>>" + snfList);
        System.out.println("fatList>>>>>" + fatList.size());
        System.out.println("snfFatListPojos size>>>>>" + snfFatListPojos.size());


        Constant.snfFatList.addAll(snfFatListPojos);
        for (int i = 0; i < snfList.size(); i++) {
            String key = snfList.get(i);
            tabLayout.addTab(tabLayout.newTab().setText("" + key));
        }

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Objects.requireNonNull(tabLayout.getTabAt(position)).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        snfPagerAdapter = new SnfPagerAdapter(mContext, snfList, fatList, snfFatListPojos);
        viewPager.setAdapter(snfPagerAdapter);

        Objects.requireNonNull(viewPager.getAdapter()).notifyDataSetChanged();
    }

}
