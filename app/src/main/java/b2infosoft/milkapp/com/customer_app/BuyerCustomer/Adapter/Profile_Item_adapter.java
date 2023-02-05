package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanProfile_Item;


/**
 * Created by Choudhary on 11/07/2019.
 */

public class Profile_Item_adapter extends RecyclerView.Adapter<Profile_Item_adapter.MyViewHolder> {

    private Context mContext;
    private List<BeanProfile_Item> albumList;

    public Profile_Item_adapter(Context context, List<BeanProfile_Item> albumList) {
        this.mContext = context;
        this.albumList = albumList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeanProfile_Item album = albumList.get(position);
        holder.tvlableName.setText(Html.fromHtml(album.getStrLable()));
        holder.tvName.setText(Html.fromHtml(album.getStrname()));
        holder.itemView.setTranslationY(-(100 + position * 100));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvlableName, tvName;

        public MyViewHolder(View view) {
            super(view);
            tvlableName = view.findViewById(R.id.tvlableName);
            tvName = view.findViewById(R.id.tvName);


        }

    }


}
