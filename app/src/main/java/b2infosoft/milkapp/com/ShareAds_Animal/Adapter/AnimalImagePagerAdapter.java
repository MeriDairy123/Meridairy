package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Fragment.FragmentViewAnimal_Image;


public class AnimalImagePagerAdapter extends PagerAdapter {
    Context mContext;

    ArrayList<String> IMAGES;
    LayoutInflater layoutInflater;
    String imguUrl = "";

    public AnimalImagePagerAdapter(Context context, ArrayList<String> images) {
        this.mContext = context;
        this.IMAGES = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View itemView = layoutInflater.inflate(R.layout.view_image_animal_item, view, false);
        assert itemView != null;
        final ImageView imageView = itemView.findViewById(R.id.animal_image);
        imguUrl = IMAGES.get(position);
        //  Glide.with(mContext).load(imguUrl).into(imageView);
        Glide.with(mContext).load(imguUrl)
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                imguUrl = IMAGES.get(position);
                bundle.putString("imgUrl", imguUrl);
                Fragment fragment = new FragmentViewAnimal_Image();
                fragment.setArguments(bundle);
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container_share_ads, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        view.addView(itemView, 0);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}