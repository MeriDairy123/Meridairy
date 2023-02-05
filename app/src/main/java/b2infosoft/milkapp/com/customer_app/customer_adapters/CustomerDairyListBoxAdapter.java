package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerTransactionDataActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryDateActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.ProductListActivity;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;

/**
 * Created by u on 08-Dec-17.
 */

public class CustomerDairyListBoxAdapter extends RecyclerView.Adapter<CustomerDairyListBoxAdapter.MyViewHolder> {
    public Context mContext;

    public ArrayList<DairyNamePojo> mList = new ArrayList<>();
    String type = "";

    public CustomerDairyListBoxAdapter(Context mContext, ArrayList<DairyNamePojo> DairyNameListPojos) {
        this.mContext = mContext;
        this.mList = DairyNameListPojos;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dairy_list_box, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tvDairyName.setText(mList.get(position).dairy_name);
        Random rand = new Random();
        int r = rand.nextInt(255);
        int g = rand.nextInt(255);
        int b = rand.nextInt(255);
        int randomColor = Color.rgb(r, g, b);

        if (position % 2 == 0) {
            holder.layout.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_blue));
        } else {
            holder.layout.setCardBackgroundColor(mContext.getResources().getColor(R.color.color_blue));
        }

        /* Drawable mDrawable = mContext.getResources().getDrawable(R.drawable.chat_icon);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(ContextCompat.getColor(mContext, R.color.color_white), PorterDuff.Mode.MULTIPLY));*/

        //holder.imgChat.setColorFilter(ContextCompat.getColor(mContext, R.color.color_white), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDairyName;
        ImageView imgChat;
        CardView layout;

        public MyViewHolder(View view) {
            super(view);

            tvDairyName = view.findViewById(R.id.tvDairyName);
            layout = view.findViewById(R.id.layout);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Intent intent = null;
            Constant.DairyNameID = mList.get(pos).id;
            Constant.DairyName = mList.get(pos).dairy_name;
            Constant.UserID = mList.get(pos).customer_id;
            Constant.SessionUserGroupID = "3";
            if (Constant.FromWhere.equals("TransactionList")) {
                mContext.startActivity(new Intent(mContext, CustomerTransactionDataActivity.class));
            } else if (Constant.FromWhere.equals("Product")) {
                mContext.startActivity(new Intent(mContext, ProductListActivity.class));
            } else if (Constant.FromWhere.equals("MilkEntry")) {
                mContext.startActivity(new Intent(mContext, MilkEntryDateActivity.class));
            }

        }
    }
}
