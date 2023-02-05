package b2infosoft.milkapp.com.Shopping.Adapter;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.Interface.OnOrderRecievedListner;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.Model.BeanRecievedOrderItem;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dataFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.toTitleCase;


/**
 * Created by Choudhary on 11/03/2019.
 */

public class RecievedOrder_item_adapter extends RecyclerView.Adapter<RecievedOrder_item_adapter.MyViewHolder> {

    OnOrderRecievedListner listner;
    private Context mContext;
    private List<BeanRecievedOrderItem> albumList;
    private List<BeanRecievedOrderItem> arraylist;

    public RecievedOrder_item_adapter(Context mContext, List<BeanRecievedOrderItem> albumList, OnOrderRecievedListner listner) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.arraylist = new ArrayList<BeanRecievedOrderItem>();
        this.arraylist.addAll(albumList);
        this.listner = listner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_customer_order_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        BeanRecievedOrderItem album = albumList.get(position);

        final List<BeanOrderProductItem> productItem = album.mOrderProductList;

        String url = "";

        holder.tvCustomerName.setText(album.name);
        holder.tvCustomerMobileNo.setText(album.phone_number);
        holder.tvOrderId.setText("#" + album.order_id);
        String orderStatus = nullCheckFunction(album.order_status);
        if (!productItem.isEmpty()) {

            url = BaseImageUrl + album.mOrderProductList.get(0).thumb;
            Glide.with(mContext)
                    .load(url)
                    .apply(new RequestOptions().override(50, 50))
                    .error(R.drawable.app_icon)
                    .into(holder.image);
        }
        if (orderStatus.length() > 0) {
            orderStatus = toTitleCase(orderStatus);
        }
        if (orderStatus.equalsIgnoreCase("pending")) {
            holder.tvOrderStatus.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_orange));
        } else if (orderStatus.equalsIgnoreCase("shipped") || orderStatus.equalsIgnoreCase("received")) {
            holder.tvOrderStatus.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_blue));
        } else if (orderStatus.equalsIgnoreCase("canceled")) {
            holder.tvOrderStatus.setBackground(mContext.getResources().getDrawable(R.drawable.btn_redcolor_shape));
        } else if (orderStatus.equalsIgnoreCase("delivered")) {
            holder.tvOrderStatus.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_green));

        }

        holder.tvOrderStatus.setText(orderStatus);
        holder.tvOrderDate.setText(dataFormat(album.order_date));
        String grantTotal = getColoredSpanned("\u20B9  ", "#FF5E57") + album.grandtotal;

        holder.tvOrderPrice.setText(Html.fromHtml(grantTotal));


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

    public void filterSearch(String charText) {
        System.out.println("==search filter=======" + charText);
        charText = charText.toLowerCase(Locale.getDefault());
        albumList.clear();
        if (charText.length() == 0) {
            albumList.addAll(arraylist);
        } else {
            for (BeanRecievedOrderItem wp : arraylist) {
                if (wp.phone_number.toLowerCase(Locale.getDefault()).contains(charText)) {
                    albumList.add(wp);
                } else if (wp.order_status.toLowerCase(Locale.getDefault()).contains(charText)) {
                    albumList.add(wp);
                } else if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    albumList.add(wp);
                }

            }
        }
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrderId, tvCustomerName, tvCustomerMobileNo,
                tvOrderStatus, tvOrderDate, tvOrderPrice;
        public ImageView image;

        public MyViewHolder(View view) {
            super(view);


            tvOrderId = view.findViewById(R.id.tvOrderId);
            tvCustomerName = view.findViewById(R.id.tvCustomerName);
            tvCustomerMobileNo = view.findViewById(R.id.tvCustomerMobileNo);
            tvOrderStatus = view.findViewById(R.id.tvOrderStatus);
            tvOrderDate = view.findViewById(R.id.tvOrderDate);
            tvOrderPrice = view.findViewById(R.id.tvOrderPrice);
            image = view.findViewById(R.id.image);
        }

    }


}
