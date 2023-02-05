package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.AdvBannerPojo;
import b2infosoft.milkapp.com.R;


/**
 * Created by Microsoft on 23-Sep-17.
 */

public class CustomPagerAdapter extends PagerAdapter {

    ArrayList<AdvBannerPojo> bannerList;
    Context context;
    private Activity activity;

    public CustomPagerAdapter(Activity activity, ArrayList<AdvBannerPojo> bannerList, Context ctx) {

        this.activity = activity;
        this.bannerList = bannerList;
        context = ctx;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (activity).getLayoutInflater();
        View viewItem = inflater.inflate(R.layout.customer_layoutimage, container, false);
        ImageView imageView = viewItem.findViewById(R.id.image);
        Glide.with(context)
                .load(bannerList.get(position).adlogo)
                .into(imageView);
        (container).addView(viewItem);
        return viewItem;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        (container).removeView((View) object);
    }


    /*public void showImage(int imageUri) {
        //  Dialog builder = new Dialog(this);
        Dialog dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(imageUri);
       *//* dialog.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));*//*
        dialog.addContentView(imageView, new LinearLayout.LayoutParams(500, 800));
        dialog.show();
    }*/
}

