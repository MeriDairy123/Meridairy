package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_ViewInvoiceDetails;
import b2infosoft.milkapp.com.customer_app.Interface.ItemPositionListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanInvoiceItem;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goBuyerNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


/**
 * Created by Choudhary on 18/07/2019.
 */

public class Invoice_Item_adapter extends RecyclerView.Adapter<Invoice_Item_adapter.MyViewHolder> {

    ItemPositionListner positionListner;
    private Context mContext;
    private List<BeanInvoiceItem> albumList;

    public Invoice_Item_adapter(Context context, List<BeanInvoiceItem> albumList, ItemPositionListner listner) {
        this.mContext = context;
        this.albumList = albumList;
        this.positionListner = listner;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeanInvoiceItem album = albumList.get(position);

        String packgeName = album.getStatus();
        String weight = nullCheckFunction(album.getWeight());
        String strPrice = nullCheckFunction(album.getAmount());
        String strtTitle = album.getMonth() + " " + album.getYear();
        if (weight.length() == 0) {
            weight = "0";
        }
        weight = mContext.getString(R.string.Weight) + " : " + weight;
        holder.tvTitle.setText(strtTitle);


        holder.tvWeight.setText(weight);

        if (strPrice.length() == 0) {
            strPrice = "0";
        }
        holder.tvPrice.setText(mContext.getString(R.string.Rupee_symbol) + " " + strPrice);

        if (packgeName.equalsIgnoreCase("unpaid") && !strPrice.startsWith("0")) {
            holder.btnPay.setVisibility(View.VISIBLE);
            holder.imgPaid.setVisibility(View.GONE);
        } else if (strPrice.startsWith("0")) {
            holder.btnPay.setVisibility(View.GONE);
            holder.imgPaid.setVisibility(View.GONE);
        } else {
            holder.imgPaid.setVisibility(View.VISIBLE);
            holder.btnPay.setVisibility(View.GONE);
        }
        holder.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionListner.onItemPosition(position);
            }
        });
        holder.layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("month_no", albumList.get(position).monthNo);
                bundle.putString("month_str", albumList.get(position).month);
                bundle.putString("year", albumList.get(position).year);
                Fragment fragment = new fragment_ViewInvoiceDetails();
                fragment.setArguments(bundle);
                goBuyerNextFragmentWithBackStack(mContext, fragment);
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvWeight, tvPrice, btnPay;
        ImageView imgPaid;
        View layoutTop;

        public MyViewHolder(View view) {
            super(view);

            layoutTop = view.findViewById(R.id.layoutTop);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            btnPay = view.findViewById(R.id.btnPay);
            imgPaid = view.findViewById(R.id.imgPaid);
        }

    }


}
