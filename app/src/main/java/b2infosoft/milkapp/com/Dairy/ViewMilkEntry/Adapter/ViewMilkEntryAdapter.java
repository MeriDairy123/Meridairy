package b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;


public class ViewMilkEntryAdapter extends RecyclerView.Adapter<ViewMilkEntryAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<TenDaysMilkSellHistory> mList = new ArrayList<>();

    public ViewMilkEntryAdapter(Context mContext, ArrayList<TenDaysMilkSellHistory> transectionListPojoArrayList) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_milk_entry_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.tvDate.setText(mList.get(position).for_date + "");
        if (!mList.get(position).entry_total_milk.equals("-") && !mList.get(position).entry_total_milk.equals("")) {
            holder.tvWeight.setText("" + String.format("%.3f", Double.parseDouble(mList.get(position).entry_total_milk)));
        } else {
            holder.tvWeight.setText("---");
        }
        if (!mList.get(position).fat.equals("-")  ) {
            holder.tvFat.setText("     " + mList.get(position).fat + "/" + mList.get(position).snf);
        } else {
            holder.tvFat.setText("   ---");
        }
        if (!mList.get(position).per_kg_price.equals("-") && !mList.get(position).per_kg_price.equals("")) {
            holder.tvRate.setText("   " + String.format("%.2f", Double.parseDouble(mList.get(position).per_kg_price)));
        } else {
            holder.tvRate.setText("---");
        }
        if (!mList.get(position).total_price.equals("-") && !mList.get(position).total_price.equals("")) {
            holder.tvTotal.setText("" + String.format("%.2f", Double.parseDouble(mList.get(position).total_price)) + "  ");
        } else {
            holder.tvTotal.setText("---  ");
        }
        if (!mList.get(position).total_bonus.equals("-") && !mList.get(position).total_bonus.equals("")) {
            holder.tvBonus.setText("" + String.format("%.2f", Double.parseDouble(mList.get(position).total_bonus)));
        } else {
            holder.tvBonus.setText("---");
        }
        if (mList.get(position).shift.equals("morning")) {
            holder.imgShift.setImageResource(R.drawable.sun_icon);
        } else {
            holder.imgShift.setImageResource(R.drawable.evening);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate, tvWeight, tvFat, tvRate, tvTotal, tvBonus;
        ImageView imgShift;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvFat = view.findViewById(R.id.tvFat);
            tvRate = view.findViewById(R.id.tvRate);
            tvTotal = view.findViewById(R.id.tvTotal);
            tvBonus = view.findViewById(R.id.tvBonus);
            imgShift = view.findViewById(R.id.imgShift);

        }

    }
}
