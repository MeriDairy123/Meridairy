package b2infosoft.milkapp.com.Dairy.BulkMilkSale.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerTransactionDataActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.MilkEntryDateActivity;
import b2infosoft.milkapp.com.customer_app.customer_actvities.ProductListActivity;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;

/**
 * Created by Choudhary on 21-Sept-19.
 */

public class DairyNameListAdapter extends RecyclerView.Adapter<DairyNameListAdapter.MyViewHolder> {
    public Context mContext;

    public ArrayList<DairyNamePojo> mList = new ArrayList<>();

    public DairyNameListAdapter(Context mContext, ArrayList<DairyNamePojo> dairyNamePojos) {
        this.mContext = mContext;
        this.mList = dairyNamePojos;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dairy_name_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tvDiaryName.setText(mList.get(position).dairy_name);
    }

    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDiaryName;

        public MyViewHolder(View view) {
            super(view);

            tvDiaryName = view.findViewById(R.id.tvDiaryName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            System.out.println("ID==>>>" + mList.get(pos).id);
            Constant.DairyNameID = mList.get(pos).id;
            Constant.DairyName = mList.get(pos).dairy_name;
            Constant.UserID = mList.get(pos).customer_id;
            Constant.SessionUserGroupID = mList.get(pos).customer_user_group_id;
            if (Constant.FromWhere.equals("TransactionList")) {
                mContext.startActivity(new Intent(mContext, CustomerTransactionDataActivity.class));
            } else if (Constant.FromWhere.equals("Product")) {
                mContext.startActivity(new Intent(mContext, ProductListActivity.class));
            } else if (Constant.FromWhere.equals("MilkEntry")) {
                mContext.startActivity(new Intent(mContext, MilkEntryDateActivity.class));
            }

        }
    }
}
