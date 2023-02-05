package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
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
 * Created by Choudhary on 17-Sept-19.
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
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_milk_entry_row, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //if (morningList.get(position).shift.equals("morning")) {
        // holder.tv_shift.setText(mList.get(position).shift);
        holder.setIsRecyclable(false);
        if (mList.get(position).shift.equals("morning")) {
            holder.imgShift.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sun_icon));
        } else {
            holder.imgShift.setImageDrawable(mContext.getResources().getDrawable(R.drawable.evening));
        }

        holder.tvDate.setText(method(mList.get(position).entry_date).trim());
        String strfat=mList.get(position).fat;
        String strSnf=mList.get(position).snf;

        if (!strfat.equals("")) {
            holder.tvFat.setText( strfat+"/"+ strSnf);

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

        }if (!mList.get(position).total_bonus.equals("")) {
            holder.tvBonus.setText("\t" + mList.get(position).total_bonus);

        } else {
            holder.tvBonus.setText("\t---");

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

        @Override
        public void onClick(View view) {


        }
    }
}
