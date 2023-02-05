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

import b2infosoft.milkapp.com.Dairy.ChatActivity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.ProductListActivity;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;

/**
 * Created by u on 04-Dec-17.
 */

public class CustomerDairyListAdapter extends RecyclerView.Adapter<CustomerDairyListAdapter.MyViewHolder> {
    public Context mContext;
    public ArrayList<DairyNamePojo> mList = new ArrayList<>();
    String type = "";

    public CustomerDairyListAdapter(Context mContext, ArrayList<DairyNamePojo> DairyNameListPojos) {
        this.mContext = mContext;
        this.mList = DairyNameListPojos;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dairy_name_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tvDiaryName.setText(mList.get(position).dairy_name);
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

    }

    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDiaryName;
        ImageView imgChat;
        CardView layout;

        public MyViewHolder(View view) {
            super(view);

            tvDiaryName = view.findViewById(R.id.tvDiaryName);
            imgChat = view.findViewById(R.id.imgChat);
            layout = view.findViewById(R.id.layout);
            view.setOnClickListener(this);
            //imgChat.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            Intent intent;
            /*switch (view.getId()) {
                case R.id.imgChat:*/
            if (Constant.FromWhere.equals("Chat")) {
                Constant.FromWhere2 = "CustomerApp";
                intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("FRIEND_NAME", mList.get(pos).name);
                // intent.putExtra("IMAGE_PATH", morningList.get(pos).profile_image);
                intent.putExtra("FRIEND_id", mList.get(pos).id);
                intent.putExtra("unic_customer_for_mobile", mList.get(pos).unic_customer_for_mobile);
                intent.putExtra("firebase_tocan", mList.get(pos).firebase_tocan);
                intent.putExtra("FRIEND_mob", mList.get(pos).phone_number);
                mContext.startActivity(intent);
            } else if (Constant.FromWhere.equals("Product")) {
                mContext.startActivity(new Intent(mContext, ProductListActivity.class));
            }

        }
    }
}
