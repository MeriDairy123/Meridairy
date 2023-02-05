package b2infosoft.milkapp.com.ShareAds_Animal.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmadrosid.svgloader.SvgLoader;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.paytm.pgsdk.easypay.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.Model.BeanAnimalDashboard;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.ShareAds_Animal.Fragment.FragmentAnimalDetails;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nameFirstLatterCapitalize;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;

public class AnimalDashboard_ItemAdapter extends RecyclerView.Adapter<AnimalDashboard_ItemAdapter.MyViewHolder> {
    Context mContext;
    List<BeanAnimalDashboard> mList;
    List<BeanAnimalDashboard> mListFilter;
    SessionManager sessionManager;
    ImageView img_delete;
    String imgUrl = "";

    public AnimalDashboard_ItemAdapter(Context mContext, List<BeanAnimalDashboard> list) {
        this.mContext = mContext;
        this.mList = list;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.animal_dashboard_row_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        final BeanAnimalDashboard album = mList.get(position);
        final String name = nameFirstLatterCapitalize(album.getNick_name());
        viewHolder.tvTitle.setText(name);
        viewHolder.tvPrice.setText(nullCheckFunction(mContext.getString(R.string.Rupee_symbol) + "  " + album.getSelling_price()));

        Glide.with(mContext).load(album.getImage())
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .error(R.color.color_light_white)
                .into(viewHolder.animal_image);
        imgUrl = album.getImage();
        String[] imageUrl = imgUrl.split("\\|");
        imgUrl = "";
        if (imageUrl.length > 0) {
            imgUrl = BaseImageUrl + imageUrl[0];
        }
        String imageType = imageUrl[imageUrl.length - 1];
        if (imageType.equals("svg")) {
            SvgLoader.pluck().with((Activity) mContext).load(album.getImage(), viewHolder.animal_image);
            // .setPlaceHolder(R.mipmap.ic_launcher, R.mipmap.ic_launcher)
        } else {
            Glide.with(mContext).load(imgUrl).into(viewHolder.animal_image);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String json = gson.toJson(album);
                sessionManager.setValueSession("beanAnimalList", json);

                Fragment fragment = new FragmentAnimalDetails();
                FragmentTransaction transaction = ((FragmentActivity) mContext).getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.container_share_ads, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void filterSearch(String charText) {
        Log.d("==search filter=======", charText);
        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BeanAnimalDashboard wp : mListFilter) {
                if (wp.getMain_cat().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }

            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice;
        ImageView animal_image, img_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            animal_image = itemView.findViewById(R.id.image);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_delete.setVisibility(View.GONE);
        }
    }

}
