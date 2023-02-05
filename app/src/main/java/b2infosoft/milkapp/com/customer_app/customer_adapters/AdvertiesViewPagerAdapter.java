package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;

import b2infosoft.milkapp.com.Model.AdvBannerPojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by Microsoft on 5/11/2018.
 */
public class AdvertiesViewPagerAdapter extends PagerAdapter {

    ArrayList<AdvBannerPojo> advertiesPojosList;
    ArrayList<String> advertiesPojosList2;
    String pageIDsArray[];
    ViewPager pager;
    int count;
    int currentPage = 0;
    Timer timer;
    private Context mContext;


    public AdvertiesViewPagerAdapter(final ViewPager viewPager, ArrayList<AdvBannerPojo> advertiesPojos, Context context) {
        super();
        mContext = context;
        advertiesPojosList = advertiesPojos;
        pager = viewPager;

    }


    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.layout_restaurent_adverties_row, collection, false);
        ImageView imageView = layout.findViewById(R.id.imageView);
        // int pageId = Integer.parseInt(pageIDsArray[position]);
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.right_to_left);
        layout.startAnimation(animation);
        try {
            Glide.with(mContext).load(advertiesPojosList.get(currentPage).adlogo)
                    .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                    .error(R.color.color_light_white)

                    .into(imageView);
            if (currentPage >= advertiesPojosList.size() - 1)
                currentPage = 0;
            else {
                ++currentPage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //imageView.setImageResource(advertiesPojosList.get(position).Adv_image);
        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
