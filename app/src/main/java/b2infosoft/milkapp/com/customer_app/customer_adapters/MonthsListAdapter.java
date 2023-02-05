package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryActivity;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyMonthlyDataPojo;

/**
 * Created by Microsoft on 27-Sep-17.
 */

public class MonthsListAdapter extends RecyclerView.Adapter<MonthsListAdapter.MyViewHolder> {
    public Context mContext;

    public ArrayList<DairyMonthlyDataPojo> mList = new ArrayList<>();

    public MonthsListAdapter(Context mContext, ArrayList<DairyMonthlyDataPojo> monthsListPojos) {
        this.mContext = mContext;
        this.mList = monthsListPojos;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.customer_row_milk_entry_date, parent, false);
        return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        if (position == 0) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#0794D2"));
        }
        if (position == 1) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#45464B"));
        }
        if (position == 2) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#906088"));

        }
        if (position == 3) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#F39359"));
        }
        if (position == 4) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#F97370"));
        }
        if (position == 5) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#5A86B5"));

        }
        if (position == 6) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#1BBC9B"));

        }
        if (position == 7) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#935E88"));

        }
        if (position == 8) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#45464B"));
        }
        if (position == 9) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#1BBC9B"));

        }
        if (position == 10) {
            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#F39357"));

        }
        if (position == 11) {

            holder.layoutMonth1.setBackgroundColor(Color.parseColor("#925F88"));

        }

        holder.btnMonth1.setText(mList.get(position).name);
        holder.totalAmountMonth1.setText(mList.get(position).total_credit);


    }


    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public LinearLayout layoutMonth1, layoutMonth2;
        public Button btnMonth1, btnMonth2;
        public TextView totalAmountMonth1, totalAmountMonth2;

        public MyViewHolder(View view) {
            super(view);
            layoutMonth1 = view.findViewById(R.id.layoutMonth1);
            btnMonth1 = view.findViewById(R.id.btnMonth1);
            totalAmountMonth1 = view.findViewById(R.id.totalAmountMonth1);
            layoutMonth1.setOnClickListener(this);
            btnMonth1.setOnClickListener(this);
            totalAmountMonth1.setOnClickListener(this);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {

            int pos = getAdapterPosition();
            switch (view.getId()) {
                case R.id.layoutMonth1:
                case R.id.btnMonth1:
                case R.id.totalAmountMonth1:
                    Constant.Month = "";
                    Constant.Year = "";
                    Constant.Month = mList.get(pos).m;
                    Constant.Year = mList.get(pos).y;
                    mContext.startActivity(new Intent(mContext, MilkEntryActivity.class));
                    break;


            }
        }
    }
}