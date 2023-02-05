package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDashboardCalenderItem;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDeliveredMilkPlan;


/**
 * Created by Choudhary on 27/08/2019.
 */

public class InvoiceMonthly_Details_Item_adapter extends RecyclerView.Adapter<InvoiceMonthly_Details_Item_adapter.MyViewHolder> {


    List<BeanDeliveredMilkPlan> milkPlanList = new ArrayList<>();
    private Context mContext;
    private List<BeanDashboardCalenderItem> albumList;

    public InvoiceMonthly_Details_Item_adapter(Context context, List<BeanDashboardCalenderItem> albumList) {
        this.mContext = context;
        this.albumList = albumList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_details_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        BeanDashboardCalenderItem album = albumList.get(position);
        milkPlanList = album.getMilkPlanList();
        double weight = 0, amount = 0;
        String packgeName = "";
        if (!milkPlanList.isEmpty()) {
            for (int i = 0; i < milkPlanList.size(); i++) {
                weight = weight + milkPlanList.get(i).getQty();
                amount = amount + milkPlanList.get(i).getTotalPrice();
                packgeName = milkPlanList.get(i).getPlanName();

            }
        }


        String strWeight = String.valueOf(weight);
        String strPrice = String.valueOf(amount);
        String strDay = Integer.toString(album.getDay());
        holder.tvDate.setText(strDay);

        holder.tvDesc.setText(packgeName);
        holder.tvWeight.setText(strWeight);

        if (strPrice.length() == 0) {
            strPrice = "0";
        }
        holder.tvPrice.setText(mContext.getString(R.string.Rupee_symbol) + " " + strPrice);

        holder.layoutTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                milkPlanList = albumList.get(position).getMilkPlanList();
                System.out.println("=milkPlanList=====" + milkPlanList.size());

                if (!milkPlanList.isEmpty()) {
                    dialogEventView();

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void dialogEventView() {

        DeliveredMilkPlan_Item_adapter adapter = null;

        Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_view_event);
        ImageView imgClosed;
        RecyclerView recyclerView;
        TextView tvDialogTitle, tv_emty;


        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tv_emty = dialog.findViewById(R.id.tv_emty);
        recyclerView = dialog.findViewById(R.id.recyclerView);

        imgClosed = dialog.findViewById(R.id.imgClosed);

        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvDialogTitle.setText(mContext.getString(R.string.ViewInvoice));

        recyclerView.setVisibility(View.VISIBLE);
        tv_emty.setVisibility(View.GONE);

        adapter = new DeliveredMilkPlan_Item_adapter(mContext, milkPlanList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);


        dialog.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate, tvWeight, tvDesc, tvPrice;
        View layoutTop;


        public MyViewHolder(View view) {
            super(view);
            layoutTop = view.findViewById(R.id.layoutTop);
            tvDate = view.findViewById(R.id.tvDate);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvDesc = view.findViewById(R.id.tvDesc);
            tvPrice = view.findViewById(R.id.tvPrice);
        }

    }

}
