package b2infosoft.milkapp.com.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 25/01/2019.
 */

public class DashboardItemAdapter extends RecyclerView.Adapter<DashboardItemAdapter.MyViewHolder> {

    private Context mContext;
    private OnClickInDashboardAdapter clickInDashboardAdapter;
    private List<Dashboard_item> albumList;

    public DashboardItemAdapter(Context mContext, List<Dashboard_item> albumList,
                                OnClickInDashboardAdapter onClickInDashboardAdapter) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.clickInDashboardAdapter = onClickInDashboardAdapter;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_row_item, parent, false);
        /*float margin = 1, rows = (float) 3.5;
        float h = parent.getHeight();
        h = (h - margin * 2) / rows;
        ViewGroup.LayoutParams params_new = itemView.getLayoutParams();
        params_new.height = (int) h;
        itemView.setLayoutParams(params_new);*/
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Dashboard_item album = albumList.get(position);
        holder.item_category_name.setText(album.getName());
        holder.image_category.setImageResource(album.getImage());
        holder.tvCount.setText(album.getCount());
        if (album.getId().equals("shopping") ||
                album.getId().equals("member_ship") || album.getId().equals("message")) {
            holder.tvCount.setVisibility(View.VISIBLE);

        } else {
            holder.tvCount.setVisibility(View.GONE);
        }

        holder.itemView.setTranslationY((-5 + position * 10));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(1000).start();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickInDashboardAdapter.onClickEditInAdapter(position);
            }
        });

        if (album.getName().equals(mContext.getResources().getString(R.string.Shopping))) {

            holder.item_category_name.setTextColor(mContext.getResources().getColor(R.color.colorDashboardText));
            holder.image_category.setColorFilter(mContext.getResources().getColor(R.color.colorDashboardIcon), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            holder.item_category_name.setTextColor(mContext.getResources().getColor(R.color.colorDashboardText));
            holder.image_category.setColorFilter(mContext.getResources().getColor(R.color.colorDashboardIcon), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView item_category_name, tvCount;
        public ImageView image_category;
        LinearLayout linearLayout;


        public MyViewHolder(View view) {
            super(view);

            item_category_name = view.findViewById(R.id.tvItemTitle);
            image_category = view.findViewById(R.id.image1);
            tvCount = view.findViewById(R.id.tvCount);
            linearLayout = view.findViewById(R.id.linear_layout);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

        }
    }


}
