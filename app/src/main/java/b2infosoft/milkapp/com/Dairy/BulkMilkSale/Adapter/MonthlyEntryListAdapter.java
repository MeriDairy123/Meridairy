package b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.MonthsEntryListPojo;

/**
 * Created by Choudhary on 21-Sept-19.
 */

public class MonthlyEntryListAdapter extends RecyclerView.Adapter<MonthlyEntryListAdapter.MyViewHolder> {
    public Context mContext;

    public ArrayList<MonthsEntryListPojo> mList = new ArrayList<>();

    public MonthlyEntryListAdapter(Context mContext, ArrayList<MonthsEntryListPojo> MonthlyEntryListPojos) {
        this.mContext = mContext;
        this.mList = MonthlyEntryListPojos;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.customer_layout_months_milk_entry_list_row, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //if (morningList.get(position).shift.equals("morning")) {
        // holder.tv_shift.setText(mList.get(position).shift);
        holder.setIsRecyclable(false);
        if (mList.get(position).shift.equals("morning")) {
            holder.tv_shift.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sun_icon));
        } else {
            holder.tv_shift.setImageDrawable(mContext.getResources().getDrawable(R.drawable.evening));
        }
        Log.d("Date>>", method(mList.get(position).entry_date).trim());
        holder.tvDate.setText(method(mList.get(position).entry_date).trim());
        if (!mList.get(position).fat.equals("")) {
            holder.tvFat.setText("\t   " + mList.get(position).fat);
        } else {
            holder.tvFat.setText("\t   ---");
        }
        if (!mList.get(position).total_milk.equals("")) {
            holder.tvWeight.setText(mList.get(position).total_milk);

        } else {
            holder.tvWeight.setText("---");

        }
        if (!mList.get(position).per_kg_price.equals("")) {
            holder.tvRate.setText("\t" + mList.get(position).per_kg_price);

        } else {
            holder.tvRate.setText("\t---");

        }
        if (!mList.get(position).total_price.equals("")) {
            holder.tvTotal.setText(mList.get(position).total_price + "  ");

        } else {
            holder.tvTotal.setText("---  ");
        }
    }

    public String method(String str) {

        str = str.replace(str.substring(str.length() - 5), "");
        return str;
    }

    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDate, tvWeight, tvFat, tvRate, tvTotal;
        ImageView tv_shift;

        public MyViewHolder(View view) {
            super(view);

            tv_shift = view.findViewById(R.id.tv_shift);
            tvDate = view.findViewById(R.id.tvDate);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvFat = view.findViewById(R.id.tvFat);
            tvRate = view.findViewById(R.id.tvRate);
            tvTotal = view.findViewById(R.id.tvTotal);


        }

        @Override
        public void onClick(View view) {


        }
    }
}
