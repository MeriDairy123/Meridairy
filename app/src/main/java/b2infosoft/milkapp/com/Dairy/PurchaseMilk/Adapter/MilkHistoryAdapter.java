package b2infosoft.milkapp.com.Dairy.PurchaseMilk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.MilkHistoryPojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by b2andro on 11/18/2017.
 */

public class MilkHistoryAdapter extends RecyclerView.Adapter<MilkHistoryAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<MilkHistoryPojo> MilkHistoryPojos = new ArrayList<>();
    String name = "";

    public MilkHistoryAdapter(Context mContext, ArrayList<MilkHistoryPojo> MilkHistoryPojoArrayList) {
        this.mContext = mContext;
        this.MilkHistoryPojos = MilkHistoryPojoArrayList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_milk_history_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if (MilkHistoryPojos.get(position).total_milk.length() == 0 || MilkHistoryPojos.get(position).total_milk.equals("-")) {

            holder.tvTotalMilk.setText("-");
        } else {

            holder.tvTotalMilk.setText(
                    String.format("%.3f", Double.parseDouble(MilkHistoryPojos.get(position).total_milk)));
        }
        if (MilkHistoryPojos.get(position).total_price.length() == 0 || MilkHistoryPojos.get(position).total_price.equals("-")) {

            holder.tvTotalPrice.setText("-");
        } else {

            holder.tvTotalPrice.setText(String.format("%.2f", Double.parseDouble(MilkHistoryPojos.get(position).total_price)));
        }

        if (MilkHistoryPojos.get(position).total_fat.length() == 0 || MilkHistoryPojos.get(position).total_fat.equals("-")) {
            holder.tvTotalFat.setText("-");
        } else {
            holder.tvTotalFat.setText(String.format("%.2f", Double.parseDouble(MilkHistoryPojos.get(position).total_fat)));
        }
        if (MilkHistoryPojos.get(position).entry_date.length() == 0 || MilkHistoryPojos.get(position).entry_date.equals("-")) {

            holder.tvEntryDate.setText("-");
        } else {
            holder.tvEntryDate.setText(MilkHistoryPojos.get(position).entry_date);
        }
        if (MilkHistoryPojos.get(position).session.toLowerCase().equals("Morning".toLowerCase())) {

            holder.tvSession.setImageResource(R.drawable.sun_icon);
        } else {
            holder.tvSession.setImageResource(R.drawable.evening);
        }
    }


    @Override
    public int getItemCount() {
        return MilkHistoryPojos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTotalMilk, tvTotalPrice, tvTotalFat, tvEntryDate;
        public ImageView tvSession;

        public MyViewHolder(View view) {
            super(view);
            tvTotalMilk = view.findViewById(R.id.tvTotalMilk);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
            tvTotalFat = view.findViewById(R.id.tvTotalFat);
            tvEntryDate = view.findViewById(R.id.tvEntryDate);
            tvSession = view.findViewById(R.id.tvSession);

        }

    }


}
