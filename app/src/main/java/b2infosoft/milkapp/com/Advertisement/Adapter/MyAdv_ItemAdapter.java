package b2infosoft.milkapp.com.Advertisement.Adapter;


import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanMyAdvItem;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;


/**
 * Created by Choudhary on 16-05-19.
 */

public class MyAdv_ItemAdapter extends RecyclerView.Adapter<MyAdv_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanMyAdvItem> mList;  // for loading main list

    public MyAdv_ItemAdapter(Context mContext, ArrayList<BeanMyAdvItem> mlist) {
        this.mContext = mContext;
        this.mList = mlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.advertise_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BeanMyAdvItem album = mList.get(position);
        String url = BaseImageUrl + album.getThumb();
        Log.d("image path==product====", url);

        Glide.with(mContext)
                .load(url)
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .placeholder(R.drawable.ic_preloader)
                .into(holder.imgAdv);

        holder.tvTitleName.setText(" " + album.getTitle());
        holder.tvDesc.setText(" " + Html.fromHtml(album.getDescription()));

        String strPrice = getColoredSpanned(mContext.getString(R.string.rsSymbol) + "  ", "#FF5E57") + album.getPrice();
        holder.tvPrice.setText(Html.fromHtml(strPrice));
        if (album.getStatus() == 1) {
            holder.tvStatus.setText(mContext.getString(R.string.Success));
            holder.tvStatus.setBackgroundResource(R.drawable.btn_shape_green);
        } else {
            holder.tvStatus.setText(mContext.getString(R.string.failed));
            holder.tvStatus.setBackgroundResource(R.drawable.btn_shape_red);
        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTitleName, tvDesc,
                tvPrice, tvStatus;
        public ImageView imgAdv, imgNext;


        public MyViewHolder(View view) {
            super(view);

            imgNext = view.findViewById(R.id.imgNext);
            tvTitleName = view.findViewById(R.id.tvTitleName);
            imgAdv = view.findViewById(R.id.imgAdv);
            tvDesc = view.findViewById(R.id.tvDesc);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvStatus = view.findViewById(R.id.tvStatus);
            imgNext.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imgNext:
                    break;
            }
        }
    }

}
