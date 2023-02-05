package b2infosoft.milkapp.com.Dairy.Bhugtan.Adapter;

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
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.TransectionListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.timeFormat12Hour;

public class TransectionsRowAdapter extends RecyclerView.Adapter<TransectionsRowAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<TransectionListPojo> transectionListPojos = new ArrayList<>();


    public TransectionsRowAdapter(Context mContext, ArrayList<TransectionListPojo> transectionListPojoArrayList) {
        this.mContext = mContext;
        this.transectionListPojos = transectionListPojoArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.transections_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvDate.setText(transectionListPojos.get(position).entry_date);
        holder.tvTitle.setText(transectionListPojos.get(position).products_name);


        if (transectionListPojos.get(position).type.equals("credit") || transectionListPojos.get(position).type.equals("receive")) {
            holder.tvCredit.setText(transectionListPojos.get(position).total_price);
            holder.tvDebit.setText("---");
        } else {
            holder.tvCredit.setText("---");
            holder.tvDebit.setText(transectionListPojos.get(position).total_price);
        }
    }

    @Override
    public int getItemCount() {
        return transectionListPojos.size();
    }

    private void openPayNowDialog(int pos) {
        TextView tvTimeDate;
        final Dialog openDialog;
        openDialog = new Dialog(mContext);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.dialog_open_signature);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        openDialog.setCancelable(false);
        tvTimeDate = openDialog.findViewById(R.id.tvTimeDate);

        String date = mContext.getString(R.string.Date) + ":-  " + transectionListPojos.get(pos).entry_date;
        String time = ":-  " + timeFormat12Hour(transectionListPojos.get(pos).created_time);
        tvTimeDate.setText(date + "\n" + mContext.getString(R.string.Time) + time);
        Button btnBack;
        final ImageView imgSign = openDialog.findViewById(R.id.imgSign);

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Glide.with(mContext).load(jsonObject.getString("image_url"))
                            .thumbnail(Glide.with(mContext).load(R.drawable.preloader)).into(imgSign);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        System.out.println("transaction_id===>>" + transectionListPojos.get(pos).transactions_ids);
        System.out.println("type====>>" + transectionListPojos.get(pos).type);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("transaction_id", transectionListPojos.get(pos).transactions_ids)
                .addEncoded("type", transectionListPojos.get(pos).type).build();
        caller.addRequestBody(body);

        caller.execute(Constant.GetSignature);
        btnBack = openDialog.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog.dismiss();
            }
        });

        openDialog.show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvDate, tvTitle, tvCredit, tvDebit;

        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tvDate);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvCredit = view.findViewById(R.id.tvCredit);
            tvDebit = view.findViewById(R.id.tvDebit);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int pos = getLayoutPosition();
            if (transectionListPojos.get(pos).type.equals("debit")) {
                openPayNowDialog(pos);
            }
        }
    }
}
