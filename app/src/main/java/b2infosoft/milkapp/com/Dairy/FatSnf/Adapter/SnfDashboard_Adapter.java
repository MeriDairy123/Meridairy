package b2infosoft.milkapp.com.Dairy.FatSnf.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
 * Created by Choudhary on 25/07/2019.
 */

public class SnfDashboard_Adapter extends RecyclerView.Adapter<SnfDashboard_Adapter.MyViewHolder> {


    private Context mContext;
    private OnClickInDashboardAdapter clickInDashboardAdapter;
    private List<Dashboard_item> albumList;

    public SnfDashboard_Adapter(Context mContext, List<Dashboard_item> albumList,
                                OnClickInDashboardAdapter onClickInDashboardAdapter) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.clickInDashboardAdapter = onClickInDashboardAdapter;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fat_snf_dashboard_row_item, parent, false);
        return new MyViewHolder(itemView);


    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Dashboard_item album = albumList.get(position);
        holder.item_category_name.setText(album.getName());
        holder.image_category.setImageResource(album.getImage());

        GradientDrawable drawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.dashboard_circle_shap);
        drawable.setColor(Color.parseColor(album.getColor()));
        holder.image_category.setBackground(drawable);
        holder.item_category_name.setTextColor(mContext.getResources().getColor(R.color.color_blue));


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView item_category_name;
        public ImageView image_category;
        LinearLayout linearLayout;


        public MyViewHolder(View view) {
            super(view);

            item_category_name = view.findViewById(R.id.tvItemTitle);
            image_category = view.findViewById(R.id.image1);
            linearLayout = view.findViewById(R.id.linearLayout);
            linearLayout.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            clickInDashboardAdapter.onClickEditInAdapter(position);
        }
    }


}
