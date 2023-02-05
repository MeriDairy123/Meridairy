package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import b2infosoft.milkapp.com.Interface.OnClickCategoryListener;
import b2infosoft.milkapp.com.Model.AnimalCategoriesData;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.AnimalCatImgUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.AnimalName;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_AnimalMainCategoryId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nameFirstLatterCapitalize;

public class AnimalCategoriesAdapter extends RecyclerView.Adapter<AnimalCategoriesAdapter.MyViewHolder> {
    Context mContext;
    List<AnimalCategoriesData> mainCategoryList;

    OnClickCategoryListener listener;
    SessionManager sessionManager;
    String selectCatPosition = "1";
    Drawable mDrawableGray, mDrawableRed;

    public AnimalCategoriesAdapter(Context mContext, List<AnimalCategoriesData> mainCategoryList, OnClickCategoryListener listener) {
        this.mContext = mContext;
        this.mainCategoryList = mainCategoryList;
        this.listener = listener;
        sessionManager = new SessionManager(mContext);
        // Drawable
        mDrawableGray = mContext.getResources().getDrawable(R.drawable.imagecircle_background);
        mDrawableRed = mContext.getResources().getDrawable(R.drawable.imagecircle_background);
        mDrawableRed.setColorFilter(new PorterDuffColorFilter(mContext.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN));

        if (sessionManager.getValueSesion(KEY_AnimalMainCategoryId) != null) {
            selectCatPosition = sessionManager.getValueSesion(KEY_AnimalMainCategoryId);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.animal_categories_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        final AnimalCategoriesData album = mainCategoryList.get(position);
        String name = nameFirstLatterCapitalize(album.getAnimalname());
        viewHolder.animal_name.setText(name);


        // Glide.with(mContext).load(album.getAnimalImage()).into(viewHolder.animal_image);
        Glide.with(mContext).load(album.getAnimalImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .error(R.color.color_light_white)
                .into(viewHolder.animal_image);

        Log.d("image====", album.getAnimalImage());
        Log.d("BaseImageUrl====", BaseImageUrl);
        String[] imageUrl = album.getAnimalImage().split("\\.");
        if (selectCatPosition.equalsIgnoreCase(album.getCategoryid())) {
            viewHolder.animal_name.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            viewHolder.animal_image.setBackground(mDrawableRed);
            ;

            sessionManager.setValueSession(KEY_AnimalMainCategoryId, album.getCategoryid());
            AnimalCatImgUrl = album.getAnimalImage();
            AnimalName = album.getAnimalname();
            listener.onClick(album.getCategoryid());
        } else {
            viewHolder.animal_name.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            viewHolder.animal_image.setBackground(mDrawableGray);
            ;

        }

        String imageType = imageUrl[imageUrl.length - 1];

       /* if (imageType.equals("svg")) {
            SvgLoader.pluck().with((Activity) mContext).load(album.getAnimalImage(), viewHolder.animal_image);
            // .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        } else {*/
        Glide.with(mContext).load(album.getAnimalImage()).into(viewHolder.animal_image); //}

        if (name.equalsIgnoreCase("cow")) {
            getSubCategory(album.getCategoryid());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCatPosition = album.getCategoryid();
                sessionManager.setValueSession(KEY_AnimalMainCategoryId, album.getCategoryid());
                AnimalCatImgUrl = album.getAnimalImage();
                AnimalName = album.getAnimalname();
                getSubCategory(album.getCategoryid());

                notifyDataSetChanged();
            }


        });
    }


    private void getSubCategory(String categoryid) {
        listener.onClick(categoryid);
    }

    @Override
    public int getItemCount() {
        return mainCategoryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView animal_name;
        ImageView animal_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            animal_name = itemView.findViewById(R.id.animal_name);
            animal_image = itemView.findViewById(R.id.animal_image);
        }
    }
}
