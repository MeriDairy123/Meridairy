package b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Interface.DeliveryBoyUserListner;
import b2infosoft.milkapp.com.Model.BeanDeliveryUserListItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by Choudhary on 03-08-19.
 */

public class DeliveryBoyUserListAdapter extends RecyclerView.Adapter<DeliveryBoyUserListAdapter.MyViewHolder> {
    public Context mContext;
    ArrayList<BeanDeliveryUserListItem> mList;
    ArrayList<BeanDeliveryUserListItem> mListFilter;
    SessionManager sessionManager;
    DeliveryBoyUserListner deliveryBoyUserListner;
    int deliveryBoyId = 0;
    int position = 0;


    public DeliveryBoyUserListAdapter(Context mContext, int deliveryBoyId, ArrayList<BeanDeliveryUserListItem> mList, DeliveryBoyUserListner listInterface) {
        this.mContext = mContext;
        this.deliveryBoyId = deliveryBoyId;
        this.mList = mList;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);

        sessionManager = new SessionManager(mContext);
        deliveryBoyUserListner = listInterface;
    }


    @Override
    public DeliveryBoyUserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.delivery_boy_user_list_row, parent, false);
        return new DeliveryBoyUserListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryBoyUserListAdapter.MyViewHolder holder, final int position) {


        holder.setIsRecyclable(false);
        holder.tvID.setText(mList.get(position).uniq_customer);
        holder.tvName.setText(mList.get(position).name);
        holder.txtMobileNo.setText(mList.get(position).phone_number);

        // holder.tvTotalPrice.setText("" + mList.get(position).total_price);


        if (mList.get(position).status == deliveryBoyId) {
            holder.chkStatus.setChecked(true);
        } else {
            holder.chkStatus.setChecked(false);
        }

        holder.chkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mList.get(position).status == deliveryBoyId) {
                    holder.chkStatus.setChecked(false);
                    mList.get(position).status = 0;
                } else {
                    holder.chkStatus.setChecked(true);
                    mList.get(position).status = deliveryBoyId;
                }
                deliveryBoyUserListner.onAdapterClick(mList);
            }
        });


    }

    public void filterSearch(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BeanDeliveryUserListItem wp : mListFilter) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.phone_number.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.uniq_customer.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvID, tvName, txtMobileNo;
        CheckBox chkStatus;

        public MyViewHolder(View view) {
            super(view);

            tvID = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            txtMobileNo = view.findViewById(R.id.txtMobileNo);
            chkStatus = view.findViewById(R.id.chkStatus);

        }

        @Override
        public void onClick(View view) {
            position = getAdapterPosition();
        }
    }
}
