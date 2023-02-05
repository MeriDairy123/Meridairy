package b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Interface.OnClickInDashboardAdapter;
import b2infosoft.milkapp.com.Model.Dashboard_item;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 25/08/2019.
 */

public class BMC_Menu_Adapter extends RecyclerView.Adapter<BMC_Menu_Adapter.MyViewHolder> {

    public static String CommonHeader = "";
    String strMenuTransaction = "", strMenuEntry = "", strMenuProduct = "";
    private Context mContext;
    private OnClickInDashboardAdapter clickInDashboardAdapter;
    private List<Dashboard_item> albumList;

    public BMC_Menu_Adapter(Context mContext, List<Dashboard_item> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
        strMenuTransaction = mContext.getResources().getString(R.string.Transaction);
        strMenuEntry = mContext.getResources().getString(R.string.entry);
        strMenuProduct = mContext.getResources().getString(R.string.Product);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_dashboard_row_item, parent, false);
        return new MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Dashboard_item album = albumList.get(position);
        holder.item_category_name.setText(album.getName());
        holder.image_category.setImageResource(album.getImage());

        GradientDrawable drawable = (GradientDrawable) mContext.getResources().getDrawable(R.drawable.circle_shape);
        drawable.setColor(Color.parseColor(album.getColor()));
        holder.image_category.setBackground(drawable);

       /* holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String menuName = album.getName();
                if (menuName.equalsIgnoreCase(strMenuTransaction)) {
                    Constant.FromWhere = "TransactionList";
                    mContext.startActivity(new Intent(mContext, CustomerDairyListWithBoxActivity.class));
                } else if (menuName.equalsIgnoreCase(strMenuEntry)) {
                    FromWhere = "MilkEntry";
                    mContext.startActivity(new Intent(mContext, CustomerDairyListWithBoxActivity.class));
                } else if (menuName.equalsIgnoreCase(strMenuProduct)) {
                    FromWhere = "Product";
                    mContext.startActivity(new Intent(mContext, CustomerDairyListWithBoxActivity.class));
                }


            }
        });*/


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView item_category_name;
        public ImageView image_category;


        public MyViewHolder(View view) {
            super(view);

            item_category_name = view.findViewById(R.id.tvItemTitle);
            image_category = view.findViewById(R.id.image1);


        }

    }


}
