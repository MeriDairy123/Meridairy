package b2infosoft.milkapp.com.adapter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanOfferBanerList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.Shopping.ProductDescriptionFagment;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;


public class BannerDasboardPagerAdapter extends PagerAdapter {
    Context mContext;

    ArrayList<BeanOfferBanerList> mList;
    LayoutInflater layoutInflater;


    public BannerDasboardPagerAdapter(Context context, ArrayList<BeanOfferBanerList> images) {
        this.mContext = context;
        this.mList = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.view_pager_item, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageView);
        TextView tvShop = itemView.findViewById(R.id.tvShop);


        String url = mList.get(position).thumb;


        Glide.with(mContext).load(url)
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .error(mContext.getDrawable(R.drawable.ic_shop))
                .into(imageView);


        container.addView(itemView);
        final int product_id = mList.get(position).product_id;
        if (product_id != 0) {
            tvShop.setVisibility(View.GONE);
        } else {
            tvShop.setVisibility(View.GONE);
        }
        //listening to image click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (product_id != 0) {

                    Bundle bundle = new Bundle();
                    Fragment fragment = new ProductDescriptionFagment();
                    bundle.putString("product_id", String.valueOf(product_id));
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);

                /*    Intent intent = new Intent(mContext, ProductDescriptionFagment.class);
                    intent.putExtra("product_id", String.valueOf(product_id));
                    mContext.startActivity(intent);*/
                }

                //  Toast.makeText(context, "you clicked image " + (position + dash1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}