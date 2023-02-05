package b2infosoft.milkapp.com.Dairy.Product.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.TotalShowInterface;
import b2infosoft.milkapp.com.Model.ProductListPojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by Microsoft on 23-Aug-17.
 */

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<ProductListPojo> mList = new ArrayList<>();
    String name = "";
    int count = 0;
    double totPrice = 0;
    double pricePerItem = 0;
    TotalShowInterface totalShowInterface;
    String fromWhere = "";

    public SaleItemAdapter(Context mContext, ArrayList<ProductListPojo> transectionListPojoArrayList, TotalShowInterface listener) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
        totalShowInterface = listener;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sale_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvItem.setText(mList.get(position).product_name);
        holder.tvPrice.setText(mList.get(position).product_price);
        holder.tvItemCount.setText("" + mList.get(position).quentity);
        holder.tvTotalAmt.setText("" + mList.get(position).totPrice);
        holder.imgMines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = mList.get(position).quentity;
                totPrice = mList.get(position).totPrice;
                pricePerItem = Double.parseDouble(mList.get(position).product_price);

                if (count == 0) {
                    holder.tvItemCount.setText("" + 0);
                    holder.imgMines.setImageResource(R.drawable.disable_mines);
                } else {
                    count--;
                    mList.get(position).totPrice = totPrice - pricePerItem;
                    holder.tvItemCount.setText("" + count);
                    holder.imgMines.setImageResource(R.drawable.enable_mines);
                    mList.get(position).quentity = count;
                    holder.tvTotalAmt.setText("" + mList.get(position).totPrice);

                }
                totalShowInterface.showTot(mList);

            }
        });
        holder.imgPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = mList.get(position).quentity;
                totPrice = mList.get(position).totPrice;
                pricePerItem = Double.parseDouble(mList.get(position).product_price);
                count++;
                if (holder.tvItemCount.getText().toString().equals("0")) {
                    holder.imgMines.setImageResource(R.drawable.disable_mines);
                } else {

                    holder.imgMines.setImageResource(R.drawable.enable_mines);
                }
                mList.get(position).totPrice = totPrice + pricePerItem;
                holder.tvTotalAmt.setText("" + mList.get(position).totPrice);
                holder.tvItemCount.setText("" + count);
                mList.get(position).quentity = count;
                totalShowInterface.showTot(mList);

            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem, tvPrice, tvItemCount, tvTotalAmt;
        ImageView imgMines, imgPlus;
        LinearLayout qunty;


        public MyViewHolder(View view) {
            super(view);
            tvItem = view.findViewById(R.id.tvItem);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvItemCount = view.findViewById(R.id.tvItemCount);
            tvTotalAmt = view.findViewById(R.id.tvTotalAmt);
            imgMines = view.findViewById(R.id.imgMines);
            imgPlus = view.findViewById(R.id.imgPlus);
            qunty = view.findViewById(R.id.qunty);
        }

    }
}
