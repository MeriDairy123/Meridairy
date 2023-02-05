package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDeliveredMilkPlan;


/**
 * Created by Choudhary on 10/07/2019.
 */

public class DeliveredMilkPlan_Item_adapter extends RecyclerView.Adapter<DeliveredMilkPlan_Item_adapter.MyViewHolder> {

    private Context mContext;
    private List<BeanDeliveredMilkPlan> albumList;

    public DeliveredMilkPlan_Item_adapter(Context context, List<BeanDeliveredMilkPlan> albumList) {
        this.mContext = context;
        this.albumList = albumList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buyer_delivered_plan_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeanDeliveredMilkPlan album = albumList.get(position);

        String weight = mContext.getString(R.string.Quantity) + " : " + album.getWeight();

        String strPrice = String.valueOf(album.getTotalPrice());

        holder.tvTitle.setText(album.getPlanName());

        holder.tvWeight.setText(weight);


        holder.tvPrice.setText(mContext.getString(R.string.Rupee_symbol) + " " + strPrice);


      /*  holder.itemView.setTranslationY(-(100 + position * 100));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();

*/
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvWeight, tvPrice;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
        }

    }


}
