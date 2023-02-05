package b2infosoft.milkapp.com.Dairy.Product.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.Product.ProductDetailsFragment;
import b2infosoft.milkapp.com.Model.ProductSellingListPojo;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;

/**
 * Created by Microsoft on 30-Aug-17.
 */

public class ProductSellingListAdapter extends RecyclerView.Adapter<ProductSellingListAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<ProductSellingListPojo> mList = new ArrayList<>();
    Fragment fragment;
    Bundle bundle;
    String startDate = "", endDate = "";

    public ProductSellingListAdapter(Context mContext, ArrayList<ProductSellingListPojo> productSellingListPojos,
                                     String startDate, String endDate) {
        this.mContext = mContext;
        this.mList = productSellingListPojos;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sale_product_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvProductName.setText("  " + mList.get(position).products_name);
        holder.tvQuantity.setText(mList.get(position).total_qty);
        holder.tvTotalPrice.setText(mList.get(position).total_price);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName, tvQuantity, tvTotalPrice;


        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment = new ProductDetailsFragment();
                    bundle = new Bundle();
                    bundle.putString("ProductID", mList.get(getAdapterPosition()).products_id);
                    //  bundle.putString("StartDate",   new ViewSellProductListFragment( ).tvStartDate.getText().toString());
                    bundle.putString("StartDate", startDate);
                    bundle.putString("EndDate", endDate);
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
            });

        }

    }


}