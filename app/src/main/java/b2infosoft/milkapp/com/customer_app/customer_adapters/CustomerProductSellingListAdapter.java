package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_pojo.CustomerProductListPojo;


/**
 * Created by Microsoft on 30-Aug-17.
 */

public class CustomerProductSellingListAdapter extends RecyclerView.Adapter<CustomerProductSellingListAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<CustomerProductListPojo> mList = new ArrayList<>();

    public CustomerProductSellingListAdapter(Context mContext, ArrayList<CustomerProductListPojo> productSellingListPojos) {
        this.mContext = mContext;
        this.mList = productSellingListPojos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_sale_product_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
      /*  String date = morningList.get(position).entry_date;
        String startDateString = "06/27/2007";
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Date startDate;

        try {
            startDate = df.parse(date);
            String newDateString = df.format(startDate);
            Log.d("newDateString", newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        holder.tvProductName.setText("  " + mList.get(position).product_name);
        holder.tvQuantity.setText(mList.get(position).qty);
        holder.tvTotalPrice.setText(mList.get(position).total_price);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvProductName, tvQuantity, tvTotalPrice;


        public MyViewHolder(View view) {
            super(view);
            tvProductName = view.findViewById(R.id.tvProductName);
            tvQuantity = view.findViewById(R.id.tvQuantity);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        }

    }
}