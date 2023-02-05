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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.SnfPagerAdapter;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FATListData;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFListData;

public class SnfFatExcelFragmentNew extends Fragment {

    View view;
    Adapter adapter;
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
    private List<String> mFragmentTitleList = new ArrayList<>();

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
        adapter = new Adapter(getChildFragmentManager());
        setList();

        viewPager.setAdapter(adapter);
        return view;
    }

    public void setList() {
        snfList = sessionManager.getFATSNFListData(mContext, Key_SNFListData);
        fatList = sessionManager.getFATSNFListData(mContext, Key_FATListData);
        snfFatListPojos = sessionManager.getRateChartList(mContext);
        Constant.snfFatList = new ArrayList<>();
        Constant.snfFatList.clear();

        Constant.snfFatList.addAll(snfFatListPojos);

        for (int i = 0; i < snfList.size(); i++) {
            String keysnf = snfList.get(i);
            ArrayList<SnfFatListPojo> mList = new ArrayList<>();
            for (int j = 0; j < snfFatListPojos.size(); j++) {

                if (keysnf.equalsIgnoreCase(snfFatListPojos.get(j).SNF)) {
                    System.out.println("Fat >>>>" + snfFatListPojos.get(j).Fat);
                    mList.add(new SnfFatListPojo(snfFatListPojos.get(j).id, snfFatListPojos.get(j).SNF, snfFatListPojos.get(j).Fat,
                            snfFatListPojos.get(j).Rate, snfFatListPojos.get(j).snf_fat_category));

                    if (mList.size() == fatList.size()) {

                        Collections.sort(mList, new Comparator<SnfFatListPojo>() {
                            @Override
                            public int compare(final SnfFatListPojo object1, final SnfFatListPojo object2) {
                                return object1.Fat.compareTo(object2.Fat);
                            }
                        });
                        Gson gson = new Gson();
                        String json = gson.toJson(mList);

                        adapter.addFragment(new SnfFatRateFragment(), keysnf, json);
                        break;
                    }

                }
            }

        }

        tabLayout.setupWithViewPager(viewPager);


    }


    public class Adapter extends FragmentPagerAdapter {
        public List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragment(Fragment fragment, String title, String list) {
            Bundle arg = new Bundle();
            arg.putString("title", title);
            arg.putString("list", list);

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
