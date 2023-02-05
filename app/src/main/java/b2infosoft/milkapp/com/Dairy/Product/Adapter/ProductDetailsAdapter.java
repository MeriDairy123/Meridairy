package b2infosoft.milkapp.com.Dairy.Product.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.ProductDetailsPojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by u on 27-Mar-18.
 */

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<ProductDetailsPojo> mList = new ArrayList<>();

    public ProductDetailsAdapter(Context mContext, ArrayList<ProductDetailsPojo> productSellingListPojos) {
        this.mContext = mContext;
        this.mList = productSellingListPojos;
    }

    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_details_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // String date[] = mList
        holder.tvDate.setText(mList.get(position).transactions_date);
        holder.tvName.setText(mList.get(position).customer_name);
        holder.tvQuantity.setText(mList.get(position).products_qty);
        holder.tvTotalAmount.setText(mList.get(position).products_total_price);
        holder.tvPName.setText(mList.get(position).products_name);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvName, tvQuantity, tvTotalAmount, tvPName;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvName = view.findViewById(R.id.tvName);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
            tvPName = view.findViewById(R.id.tvPName);
        }

    }
}
