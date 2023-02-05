package b2infosoft.milkapp.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.ProductListPojo;
import b2infosoft.milkapp.com.R;

/**
 * Created by Microsoft on 24-Aug-17.
 */

public class DialogReportAdapter extends RecyclerView.Adapter<DialogReportAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<ProductListPojo> mList = new ArrayList<>();

    public DialogReportAdapter(Context mContext, ArrayList<ProductListPojo> transectionListPojoArrayList) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_report_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvItemName.setText(" " + mList.get(position).product_name);
        holder.tvQnty.setText("" + mList.get(position).quentity);
        holder.tvTotalAmt.setText("" + mList.get(position).totPrice + "  ");
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItemName, tvQnty, tvTotalAmt, tvDebit;


        public MyViewHolder(View view) {
            super(view);
            tvItemName = view.findViewById(R.id.tvItemName);
            tvQnty = view.findViewById(R.id.tvQnty);
            tvTotalAmt = view.findViewById(R.id.tvTotalAmt);

        }

    }
}
