package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanPurchaseItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by Microsoft on 14-OCT-2020.
 */

public class CustomerSaleProductItemAdapter extends RecyclerView.Adapter<CustomerSaleProductItemAdapter.MyViewHolder> {


    public Context mContext;
    public ArrayList<BeanPurchaseItem> mList = new ArrayList<>();
    SessionManager sessionManager;
    UpdateList clickInAdapter;

    public CustomerSaleProductItemAdapter(Context mContext, ArrayList<BeanPurchaseItem> transectionListPojoArrayList, UpdateList onClickInAdapter) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
        sessionManager = new SessionManager(mContext);
        clickInAdapter = onClickInAdapter;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String weight = mContext.getString(R.string.invoice) + "  : #" + mList.get(position).getInvoice_number();
        String price = mContext.getString(R.string.Rupee_symbol) + " " + mList.get(position).getBalance_invo();
        String qty = mContext.getString(R.string.Quantity) + " : " + mList.get(position).getInvoiceProductItems().size();

        holder.tvItem.setText("" + mList.get(position).getUser_name());
        holder.tvProductWeight.setText(weight);
        holder.tvProductQty.setText(qty);
        holder.tvPrice.setText(price);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickInAdapter.onUpdateList(position, "view");


            }
        });


    }


    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItem, tvPrice, tvProductQty, tvProductWeight;
        ImageView imgMoreDetail, imgProduct;


        public MyViewHolder(View view) {
            super(view);
            tvItem = view.findViewById(R.id.tvItem);
            tvProductWeight = view.findViewById(R.id.tvProductWeight);
            tvProductQty = view.findViewById(R.id.tvProductQty);
            tvPrice = view.findViewById(R.id.tvPrice);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgProduct.setVisibility(View.GONE);
            imgMoreDetail.setVisibility(View.GONE);
        }

    }
}

