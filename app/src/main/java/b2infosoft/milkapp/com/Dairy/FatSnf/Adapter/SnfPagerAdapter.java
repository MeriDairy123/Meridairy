package b2infosoft.milkapp.com.Dairy.FatSnf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by u on 08-Jan-19.
 */

public class SnfPagerAdapter extends PagerAdapter {
    private static ArrayList<String> snfList;
    private static ArrayList<String> fatList;
    private static ArrayList<SnfFatListPojo> snfFatListPojos;
    Context mContext;
    LayoutInflater mLayoutInflater;
    SessionManager sessionManager;


    public SnfPagerAdapter(Context mContext, ArrayList<String> snfList, ArrayList<String> fatList,
                           ArrayList<SnfFatListPojo> snfFatListPojos) {
        this.mContext = mContext;
        this.snfList = snfList;
        this.fatList = fatList;
        System.out.println(" Constant.snfFatList===>>>>>>" + Constant.snfFatList.size());
        this.snfFatListPojos = snfFatListPojos;
        sessionManager = new SessionManager(mContext);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public int getCount() {
        return snfList.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == (object);
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        View view = mLayoutInflater.inflate(R.layout.fragment_message, container, false);
        RecyclerView recyclerRateList = view.findViewById(R.id.recyclerRateList);

        System.out.println("snfList===>>>>>>" + snfList.size());
        System.out.println("fatList===>>>>>>" + fatList.size());
        System.out.println(" snfFatListPojos===>>>>>>" + snfFatListPojos.size());
        System.out.println(" Constant.snfFatList===>>>>>>" + Constant.snfFatList.size());
        ArrayList<SnfFatListPojo> mList = new ArrayList<>();
        String snf = snfList.get(position);
        for (int f = 0; f < fatList.size(); f++) {
            String fat = fatList.get(f);
            for (int i = 0; i < snfFatListPojos.size(); i++) {
                SnfFatListPojo album = snfFatListPojos.get(i);
                //    System.out.println(" Pager ID==>> "+album.getId()+"   SNF==>> "+album.getSNF()+"  Fat==>> "+album.getFat()+"   Rate==>>"+album.getRate());

                if (snf.equals(snfFatListPojos.get(i).SNF)) {
                    if (fat.equals(album.getFat())) {
                        mList.add(album);
                        break;
                    }
                }
            }
        }


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerRateList.setHasFixedSize(true);
        SnfAdapter snfAdapter = new SnfAdapter(mContext, mList, fatList);
        recyclerRateList.setLayoutManager(mLayoutManager);
        recyclerRateList.setAdapter(snfAdapter);
        container.addView(view);
        return view;
    }


    public void destroyItem(ViewGroup container, int pos, @NotNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
