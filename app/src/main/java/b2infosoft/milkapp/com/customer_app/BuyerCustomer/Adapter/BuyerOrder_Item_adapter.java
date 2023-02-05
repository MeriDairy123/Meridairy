package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import b2infosoft.milkapp.com.Interface.OrderOnClickListner;
import b2infosoft.milkapp.com.Model.BeanOrderItem;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dataFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;


/**
 * Created by Choudhary on 19/07/2019.
 */

public class BuyerOrder_Item_adapter extends RecyclerView.Adapter<BuyerOrder_Item_adapter.MyViewHolder> {

    OrderOnClickListner listner;
    private Context mContext;
    private List<BeanOrderItem> albumList;

    public BuyerOrder_Item_adapter(Context mContext, List<BeanOrderItem> albumList, OrderOnClickListner listner) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.listner = listner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeanOrderItem album = albumList.get(position);

        final List<BeanOrderProductItem> productItem = albumList.get(position).mOrderProductList;
        String url = "";
        if (!productItem.isEmpty()) {
            url = BaseImageUrl + album.mOrderProductList.get(0).image;
        } else {
            url = BaseImageUrl + "invoice.png";
        }

        holder.tvOrderTitle.setText("#" + album.order_id);
        holder.tvOrderStatus.setText(album.order_status);
        holder.tvOrderDate.setText(dataFormat(album.order_date));
        String grantTotal = getColoredSpanned("\u20B9  ", "#FF5E57") + album.grandtotal;

        holder.tvOrderPrice.setText(Html.fromHtml(grantTotal));

        RequestOptions requestOption = new RequestOptions()
                .placeholder(R.color.color_light_white).centerCrop();
        Glide.with(mContext)
                .load(url)
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader).apply(requestOption))
                //   .apply(requestOption)
                .error(R.drawable.app_icon)
                .into(holder.image);

        holder.itemView.setTranslationY(-(100 + position * 100));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onAdapterClick(album);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrderTitle, tvOrderStatus, tvOrderDate, tvOrderPrice;
        public ImageView image, imgNext;

        public MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            tvOrderTitle = view.findViewById(R.id.tvOrderTitle);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvOrderDate = view.findViewById(R.id.tvOrderDate);
            tvOrderPrice = view.findViewById(R.id.tvOrderPrice);
            imgNext = view.findViewById(R.id.imgNext);
            imgNext.setVisibility(View.VISIBLE);
        }

    }


}
