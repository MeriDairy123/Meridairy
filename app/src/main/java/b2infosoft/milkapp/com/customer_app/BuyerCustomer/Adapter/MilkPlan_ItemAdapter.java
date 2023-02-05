package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanMilkPlan;

/**
 * Created by Choudhary on 13-July-19.
 */

public class MilkPlan_ItemAdapter extends RecyclerView.Adapter<MilkPlan_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanMilkPlan> mList;
    // for loading main list

    public MilkPlan_ItemAdapter(Context mContext, ArrayList<BeanMilkPlan> mlist) {
        this.mContext = mContext;
        this.mList = mlist;


    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.plan_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BeanMilkPlan albam = mList.get(position);

        holder.tvTitle.setText(Html.fromHtml(albam.product_name));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ischecked = mList.get(position).isSelected();
                System.out.println("=id===" + mList.get(position).id + "==ischecked==" + ischecked);

                if (ischecked) {
                    holder.checkBox.setChecked(false);
                } else {
                    holder.checkBox.setChecked(true);
                }


            }
        });

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            checkBox = view.findViewById(R.id.checkBox);


        }


    }

}
