package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.Interface.MilkPlanListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDairyMilkPlan;


/**
 * Created by Choudhary on 19-July-19.
 */

public class UpdateMilkPlan_ItemAdapter extends RecyclerView.Adapter<UpdateMilkPlan_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanDairyMilkPlan> mList;
    MilkPlanListner milkPlanListner;
    String one = "1";
    int qty = 0;

    public UpdateMilkPlan_ItemAdapter(Context mContext, ArrayList<BeanDairyMilkPlan> mlist,

                                      MilkPlanListner listner) {
        this.mContext = mContext;
        this.mList = mlist;
        this.milkPlanListner = listner;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.uplate_milk_plan_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BeanDairyMilkPlan albam = mList.get(position);
        String weight = mContext.getString(R.string.Weight) + " : " + albam.getWeight();
        String price = albam.getPrice();
        qty = albam.getQty();
        holder.tvTitle.setText(albam.getProduct_name());

        holder.tvWeight.setText(weight);
        holder.tvPrice.setText(mContext.getString(R.string.Rupee_symbol) + " " + price);
        boolean isSelected = false;
        if (albam.getStatus().equals(one)) {
            isSelected = true;
        }
        if (isSelected) {

            holder.imgSelected.setVisibility(View.VISIBLE);
            holder.checkBox.setVisibility(View.GONE);
            holder.layoutButton.setVisibility(View.GONE);
        } else {
            holder.imgSelected.setVisibility(View.GONE);
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.layoutButton.setVisibility(View.VISIBLE);
        }
        holder.tvQty.setText(Integer.toString(qty));


        holder.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qty = Integer.parseInt(holder.tvQty.getText().toString().trim());
                qty = qty + 1;
                holder.tvQty.setText(Integer.toString(qty));
                System.out.println("===imgMinus===qty====" + qty);
                mList.get(position).setQty(qty);

                milkPlanListner.onAdapterClick(mList);
            }
        });
        holder.imgMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                qty = Integer.parseInt(holder.tvQty.getText().toString().trim());

                if (qty > 1) {
                    qty = qty - 1;
                    holder.tvQty.setText(Integer.toString(qty));
                    System.out.println("===imgMinus===qty====" + qty);
                    mList.get(position).setQty(qty);

                }
                milkPlanListner.onAdapterClick(mList);

            }
        });


        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String plan = mList.get(position).getProduct_name();

                if (isChecked) {
                    //  showToast(mContext, plan + " Plan Added");
                    mList.get(position).setSelected(true);
                    milkPlanListner.onAdapterClick(mList);

                } else {
                    mList.get(position).setSelected(false);
                    milkPlanListner.onAdapterClick(mList);
                    //    showToast(mContext, plan + " Plan Removed!");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvWeight, tvPrice, tvQty;
        public CheckBox checkBox;
        View layoutButton;
        ImageView imgSelected, imgAdd, imgMinus;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            checkBox = view.findViewById(R.id.checkBox);
            imgSelected = view.findViewById(R.id.imgSelected);
            layoutButton = view.findViewById(R.id.layoutButton);
            tvQty = view.findViewById(R.id.tvQty);
            imgAdd = view.findViewById(R.id.imgAdd);
            imgMinus = view.findViewById(R.id.imgMinus);
        }

    }

}
