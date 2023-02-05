package b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.ViewEntryByDatePojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by B2infosoft on 8/3/2017.
 */

public class ViewEntryRecyclerViewAdapter extends RecyclerView.Adapter<ViewEntryRecyclerViewAdapter.MyViewHolder> {
    public Context mContext;
    public ArrayList<ViewEntryByDatePojo> mList;
    String name = "";

    public ViewEntryRecyclerViewAdapter(Context mContext, ArrayList<ViewEntryByDatePojo> entryList) {
        this.mContext = mContext;
        this.mList = entryList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_entry_row_item, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.etName.setText(mList.get(position).unic_customer + "." + mList.get(position).name);
        int pos = position + 1;
        holder.etId.setText("" + pos + ".");
        holder.etFat.setText(String.format("%.1f", mList.get(position).getFat())+"/"+String.format("%.1f", mList.get(position).getSnf()));
        holder.etWeight.setText(String.format("%.3f", mList.get(position).getTotal_milk()));
        holder.etTotal.setText(String.format("%.2f", mList.get(position).getTotal_price()));
        holder.etRate.setText(String.format("%.2f", mList.get(position).getPer_kg_price()));

        holder.tvBonus.setText(String.format("%.2f", mList.get(position).getBonus()));
    }


    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView etName, etFat, etWeight, etTotal, etRate, etId, tvBonus;

        public MyViewHolder(View view) {
            super(view);
            etName = view.findViewById(R.id.etName);
            etFat = view.findViewById(R.id.etFat);
            etWeight = view.findViewById(R.id.etWeight);
            etTotal = view.findViewById(R.id.etTotal);
            etRate = view.findViewById(R.id.etRate);
            etId = view.findViewById(R.id.etId);
            tvBonus = view.findViewById(R.id.tvBonus);
        }

    }
}

