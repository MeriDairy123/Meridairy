package b2infosoft.milkapp.com.Dairy.Customer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


/**
 * Created by B2infosoft on 8/19/2017.
 */

public class AllCustomerListAdapter extends RecyclerView.Adapter<AllCustomerListAdapter.MyViewHolder> {


    public Context mContext;
    public ArrayList<CustomerListPojo> mList = new ArrayList<>();
    public ArrayList<CustomerListPojo> mListFilter = new ArrayList<>();
    String fromWhere = "";
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    String dairyid;

    Bundle bundle = null;
    Fragment fragment = null;
    Intent intent = null;
    int pos = 0;
    OnCustomerListener customerListener;

    public AllCustomerListAdapter(Context mContext, ArrayList<CustomerListPojo> customerListPojos,
                                  String fromWhere, OnCustomerListener listener) {

        this.mContext = mContext;
        this.mList = customerListPojos;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        this.fromWhere = fromWhere;
        this.customerListener = listener;
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
    }

    public void updateAdapter(ArrayList<CustomerListPojo> customerListPojos) {
        this.mList = customerListPojos;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        notifyDataSetChanged();
    }


    @Override
    public AllCustomerListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.customer_row_item, parent, false);
        return new AllCustomerListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllCustomerListAdapter.MyViewHolder holder, final int position) {
        CustomerListPojo album = mList.get(position);


        int sr = position + 1;
        holder.tvSr.setText("" + sr + ".");
        holder.tvId.setText(album.unic_customer);

        holder.tvName.setText(album.name);
        holder.tvFatherName.setText(album.father_name);

        String strAmount = nullCheckFunction(album.amount);
        if (strAmount.length() == 0) {
            strAmount = "0";
        }

        double amount = Double.parseDouble(strAmount);
        strAmount = String.format("%.2f", amount);
        holder.tvAmount.setText(strAmount);
        pos = position;

    }


    public int getItemCount() {
        return mList.size();
    }


    private void moveNextActivity(int pos) {
        customerListener.onClickUser(mList.get(pos));
    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (CustomerListPojo wp : mListFilter) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.unic_customer.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvSr, tvId, tvName, tvFatherName, tvAmount;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {

            super(view);
            tvSr = view.findViewById(R.id.tvSr);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvFatherName = view.findViewById(R.id.tvFatherName);
            tvAmount = view.findViewById(R.id.tvAmount);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgMoreDetail.setVisibility(View.GONE);
            tvName.setOnClickListener(this);
            tvFatherName.setOnClickListener(this);
            tvAmount.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            pos = getLayoutPosition();
            switch (view.getId()) {
                case R.id.tvName:
                case R.id.tvAmount:
                case R.id.tvFatherName:
                    moveNextActivity(pos);
                    break;
                case R.id.imgMoreDetail:

                    break;
            }
        }
    }


}
