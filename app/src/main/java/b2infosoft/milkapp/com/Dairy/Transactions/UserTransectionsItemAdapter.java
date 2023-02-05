package b2infosoft.milkapp.com.Dairy.Transactions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.R;

public class UserTransectionsItemAdapter extends RecyclerView.Adapter<UserTransectionsItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanUserTransaction> mList;


    public UserTransectionsItemAdapter(Context mContext, ArrayList<BeanUserTransaction> transectionListPojoArrayList) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_transection_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDate.setText(mList.get(position).getTransaction_date());
        holder.tvTitle.setText(mList.get(position).getParty_name());
        holder.tvVouchType.setText(mList.get(position).getDescription());
        holder.tvVoucherNo.setText(mList.get(position).getVoucher_no());
        System.out.println("Voucher Type>>>>" + mList.get(position).getVoucher_type());


        if (mList.get(position).getCr() > 0) {
            holder.tvCredit.setText(mList.get(position).getCr() + "");
            holder.tvDebit.setText("---");
        } else {
            holder.tvCredit.setText("---");
            holder.tvDebit.setText(mList.get(position).getDr() + "");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate, tvTitle, tvVouchType, tvVoucherNo, tvCredit, tvDebit;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvVouchType = view.findViewById(R.id.tvVouchType);
            tvVoucherNo = view.findViewById(R.id.tvVoucherNo);
            tvCredit = view.findViewById(R.id.tvCredit);
            tvDebit = view.findViewById(R.id.tvDebit);
        }


    }

}
