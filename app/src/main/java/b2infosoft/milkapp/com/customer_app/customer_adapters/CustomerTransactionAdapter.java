package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.UserTransactionByDate;

/**
 * Created by u on 17-Oct-17.
 */

public class CustomerTransactionAdapter extends RecyclerView.Adapter<CustomerTransactionAdapter.MyViewHolder> {
    public Context mContext;

    public ArrayList<UserTransactionByDate> mList = new ArrayList<>();
    String type = "";

    public CustomerTransactionAdapter(Context mContext, ArrayList<UserTransactionByDate> CustomerTransactionPojos) {
        this.mContext = mContext;
        this.mList = CustomerTransactionPojos;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.customer_layout_transaction_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (mList.get(position).type.equals("debit")) {
            holder.tvDebit.setText(mList.get(position).total_price);
            holder.tvCredit.setText("---");
        } else {
            holder.tvCredit.setText(mList.get(position).total_price);
            holder.tvDebit.setText("---");
        }
        holder.tvTitle.setText(mList.get(position).products_name);
        holder.tvDate.setText(mList.get(position).entry_date);
    }

    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDebit, tvCredit, tvTitle, tvDate;

        public MyViewHolder(View view) {
            super(view);
            tvDebit = view.findViewById(R.id.tvDebit);
            tvCredit = view.findViewById(R.id.tvCredit);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDate = view.findViewById(R.id.tvDate);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();

        }
    }
}
