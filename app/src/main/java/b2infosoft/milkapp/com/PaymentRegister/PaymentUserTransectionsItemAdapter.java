package b2infosoft.milkapp.com.PaymentRegister;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.R;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;

public class PaymentUserTransectionsItemAdapter extends RecyclerView.Adapter<PaymentUserTransectionsItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanUserTransaction> mList;


    public PaymentUserTransectionsItemAdapter(Context mContext, ArrayList<BeanUserTransaction> transectionListPojoArrayList) {
        this.mContext = mContext;
        this.mList = transectionListPojoArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_payment_transection_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDate.setText(mList.get(position).getTransaction_date());


        holder.tvVoucherNo.setText(mList.get(position).getVoucher_no());
        holder.tvVouchType.setText(mList.get(position).getDescription());
        System.out.println("Voucher Type>>>>" + mList.get(position).getVoucher_type());


        if (mList.get(position).getCr() > 0) {
            holder.tvCredit.setText(mList.get(position).getCr() + "");
            holder.tvDebit.setText("---");
        } else {
            holder.tvCredit.setText("---");
            holder.tvDebit.setText(mList.get(position).getDr() + "");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mList.get(position).getSignature_image().length() > 0) {
                    openPayNowDialog(position);
                }
            }
        });
    }

    private void openPayNowDialog(int pos) {
        TextView tvTimeDate;
        final Dialog openDialog;
        openDialog = new Dialog(mContext);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.dialog_open_signature);
        Objects.requireNonNull(openDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        openDialog.setCancelable(false);
        tvTimeDate = openDialog.findViewById(R.id.tvTimeDate);

        String date = mContext.getString(R.string.Date) + ":-  " + mList.get(pos).getTransaction_date();
        //   String time = ":-  " + timeFormat12Hour(mList.get(pos).getTransaction_date());
        tvTimeDate.setText(date + "\n");
        Button btnBack;
        ImageView imgSign = openDialog.findViewById(R.id.imgSign);

        Glide.with(mContext).load(BaseImageUrl + mList.get(pos).getSignature_image())
                .thumbnail(Glide.with(mContext).load(R.drawable.preloader)).into(imgSign);
        btnBack = openDialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });

        openDialog.show();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate, tvVouchType, tvVoucherNo, tvCredit, tvDebit;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);

            tvVouchType = view.findViewById(R.id.tvVouchType);
            tvVoucherNo = view.findViewById(R.id.tvVoucherNo);
            tvCredit = view.findViewById(R.id.tvCredit);
            tvDebit = view.findViewById(R.id.tvDebit);
        }


    }

}
