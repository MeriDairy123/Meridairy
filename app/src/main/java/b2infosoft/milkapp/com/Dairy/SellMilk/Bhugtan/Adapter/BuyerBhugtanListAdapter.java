package b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.Bhugtan.TransactionOldHistoryFragment;
import b2infosoft.milkapp.com.Interface.BuyerCustomerListInterface;
import b2infosoft.milkapp.com.Model.BuyerCustomerDataListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;

/**
 * Created by Choudhary on 13-Aug-19.
 */

public class BuyerBhugtanListAdapter extends RecyclerView.Adapter<BuyerBhugtanListAdapter.MyViewHolder> {
    public Context mContext;
    ArrayList<BuyerCustomerDataListPojo> mList;
    ArrayList<BuyerCustomerDataListPojo> mListFilter = new ArrayList<>();
    SessionManager sessionManager;
    BuyerCustomerListInterface buyerCustomerListInterface;
    Bundle bundle;
    int position = 0;


    public BuyerBhugtanListAdapter(Context mContext, ArrayList<BuyerCustomerDataListPojo> mList, BuyerCustomerListInterface listInterface) {
        this.mContext = mContext;
        this.mList = mList;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        sessionManager = new SessionManager(mContext);
        buyerCustomerListInterface = listInterface;
    }


    @Override
    public BuyerBhugtanListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_buyer_bhugtan_list_row, parent, false);
        return new BuyerBhugtanListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BuyerBhugtanListAdapter.MyViewHolder holder, final int position) {


        holder.setIsRecyclable(false);
        holder.tvID.setText(mList.get(position).unic_customer);
        holder.tvName.setText(mList.get(position).name);

        if (!mList.get(position).total_milk.trim().equals("") && !mList.get(position).total_milk.trim().equals("-")) {
            double totalMilk = Double.parseDouble(mList.get(position).total_milk);
            holder.tvTotalWeight.setText("" + String.format("%.3f", totalMilk));

        }
        if (!mList.get(position).total_price.trim().equals("") && !mList.get(position).total_price.trim().equals("-")) {
            double totalMilk = Double.parseDouble(mList.get(position).total_price);
            holder.tvTotalPrice.setText("" + String.format("%.2f", totalMilk));
        }

        if (mList.get(position).isClicked.equals("true")) {
            holder.chkSelect.setChecked(true);
        } else {
            holder.chkSelect.setChecked(false);
        }

        holder.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mList.get(position).isClicked.equals("true")) {
                    holder.chkSelect.setChecked(false);
                    mList.get(position).isClicked = "false";
                } else {
                    holder.chkSelect.setChecked(true);
                    mList.get(position).isClicked = "true";
                }
            }
        });


        buyerCustomerListInterface.setDataListMain(mList);

    }


    public int getItemCount() {
        return mList.size();
    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BuyerCustomerDataListPojo wp : mListFilter) {
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

        public TextView tvID, tvName, tvTotalWeight, tvTotalPrice;
        CheckBox chkSelect;
        LinearLayout bottom_wrapper;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {
            super(view);

            tvID = view.findViewById(R.id.tvID);
            bottom_wrapper = view.findViewById(R.id.bottom_wrapper);
            tvName = view.findViewById(R.id.tvName);
            tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);

            chkSelect = view.findViewById(R.id.chkSelect);
            imgMoreDetail = view.findViewById(R.id.imgMoreDetail);


            tvID.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvTotalWeight.setOnClickListener(this);
            tvTotalPrice.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            position = getLayoutPosition();
            switch (view.getId()) {
                case R.id.tvID:
                case R.id.tvName:
                case R.id.tvTotalWeight:
                case R.id.tvTotalPrice:
                    Constant.FromWhere = "BuyerBhugtan";
                    bundle = new Bundle();
                    bundle.putString("CustomerId", mList.get(position).id);
                    bundle.putString("CustomerName", mList.get(position).name);
                    bundle.putString("CustomerFatherName", mList.get(position).father_name);
                    bundle.putString("unic_customer", mList.get(position).unic_customer);
                    bundle.putString("startDate", mList.get(position).from_date);
                    bundle.putString("endDate", mList.get(position).to_date);
                    Fragment fragment = new TransactionOldHistoryFragment();
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                    break;


            }


        }
    }
}
