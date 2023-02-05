package b2infosoft.milkapp.com.DeliveryBoy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanExtraOrder;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;

public class ExtraOrderItemAdapter extends RecyclerView.Adapter<ExtraOrderItemAdapter.MyViewHolder> {
    List<BeanExtraOrder> extraOrderDetailList;
    Context mContext;

    public ExtraOrderItemAdapter(Context mContext, List<BeanExtraOrder> extraOrderDetailLis) {
        this.mContext = mContext;
        this.extraOrderDetailList = extraOrderDetailLis;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_order_row_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {

        final BeanExtraOrder userAlbum = extraOrderDetailList.get(position);
        String url = BaseImageUrl + userAlbum.getImage();
        Glide.with(mContext)
                .load(url)
                .apply(new RequestOptions().override(50, 50))
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader))
                .into(myViewHolder.product_image);


        myViewHolder.tv_product_name.setText(userAlbum.getProduct_name());
        myViewHolder.tv_product_qty.setText(Integer.toString(userAlbum.getQty()));
        myViewHolder.tv_product_price.setText(mContext.getString(R.string.Rupee_symbol) + " " + Integer.toString(userAlbum.getTotal_price()));

    }

    @Override
    public int getItemCount() {
        return extraOrderDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView product_image;
        TextView tv_product_name, tv_product_qty, tv_product_price;

        public MyViewHolder(View itemView) {
            super(itemView);
            product_image = itemView.findViewById(R.id.image);
            tv_product_name = itemView.findViewById(R.id.tvTitle);
            tv_product_qty = itemView.findViewById(R.id.tvDesc);
            tv_product_price = itemView.findViewById(R.id.tvPrice);


        }
    }


}
