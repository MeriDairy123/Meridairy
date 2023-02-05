package b2infosoft.milkapp.com.Dairy.Bhugtan.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.Bhugtan.PayAmountFragment;
import b2infosoft.milkapp.com.Dairy.Bhugtan.TransactionOldHistoryFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.ViewSellerMilkEntryFragment;
import b2infosoft.milkapp.com.Interface.OnLoadMoreListener;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;


/**
 * Created by user on 15-Jan-18.
 */

public class SellerBhugtanListAdapter extends RecyclerView.Adapter<SellerBhugtanListAdapter.MyViewHolder> {

    private static final int MENU_PAY_ITEM = Menu.FIRST;
    private static final int MENU_TRANSACTION_ITEM = Menu.FIRST + 1;
    private static final int MENU_VIEW_ENTRY_ITEM = Menu.FIRST + 2;
    public Context mContext;
    ArrayList<TenDaysMilkSellHistory> mList;
    SessionManager sessionManager;
    String startDate = "", endDate = "";
    OnLoadMoreListener onLoadMoreListener;
    String type = "", fromwhere = "", unic_customer = "", name = "", selectedId = "", fatherName = "";
    String strTotalAmt = "";
    Bundle bundle;
    Fragment fragment;
    int MENU_PAY = 0;
    int MENU_TRANSACTION = 1;
    int MENU_VIEW_ENTRY = 2;
    int position = 0;

    public SellerBhugtanListAdapter(Context mContext, ArrayList<TenDaysMilkSellHistory> mList, String startDate, String endDate, OnLoadMoreListener listener) {
        this.mContext = mContext;
        this.mList = mList;
        sessionManager = new SessionManager(mContext);
        this.startDate = startDate;
        this.endDate = endDate;
        onLoadMoreListener = listener;
    }


    @Override
    public SellerBhugtanListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_bhugtan_list_row, parent, false);
        return new SellerBhugtanListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SellerBhugtanListAdapter.MyViewHolder holder, final int position) {
        holder.tvID.setText(mList.get(position).unic_customer);
        holder.tvName.setText(mList.get(position).name);
        if (!mList.get(position).total_milk.trim().equals("") && !mList.get(position).total_milk.trim().equals("-")) {
            double totalMilk = Double.parseDouble(mList.get(position).total_milk);
            holder.tvTotalWeight.setText("" + String.format("%.3f", totalMilk));

        }
        if (!mList.get(position).grnd_total_amt.trim().equals("") && !mList.get(position).grnd_total_amt.trim().equals("-")) {
            double totalamt = Double.parseDouble(mList.get(position).grnd_total_amt);
            strTotalAmt = String.format("%.2f", totalamt);
            holder.tvTotalPrice.setText("" + strTotalAmt);
        }

        if (mList.get(position).isChecked.equals("true")) {
            holder.chkSelect.setChecked(true);
        } else {
            holder.chkSelect.setChecked(false);
        }
        holder.chkSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mList.get(position).isChecked.equals("true")) {
                    holder.chkSelect.setChecked(false);
                    mList.get(position).isChecked = "false";
                    onLoadMoreListener.onLoadMore(mList);
                } else {
                    holder.chkSelect.setChecked(true);
                    mList.get(position).isChecked = "true";
                    onLoadMoreListener.onLoadMore(mList);
                }
            }
        });


    }


    private void showPopup(View view, int position) {
        @SuppressLint("ResourceType") ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_TRANSACTION, MENU_TRANSACTION_ITEM, 1, mContext.getString(R.string.Transaction));
        popup.getMenu().add(MENU_VIEW_ENTRY, MENU_VIEW_ENTRY_ITEM, 2, mContext.getString(R.string.viewEntry));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_PAY_ITEM:
                        // pay(position);
                        break;

                    case MENU_TRANSACTION_ITEM:

                        Constant.FromWhere = "Bhugtan";
                        bundle = new Bundle();
                        fragment = new TransactionOldHistoryFragment();
                        bundle.putString("CustomerId", mList.get(position).id);
                        bundle.putString("CustomerName", mList.get(position).name);
                        bundle.putString("CustomerFatherName", mList.get(position).father_name);
                        bundle.putString("unic_customer", mList.get(position).unic_customer);
                        bundle.putString("user_group_id", mList.get(position).user_group_id);
                        bundle.putString("startDate", mList.get(position).from_date);
                        bundle.putString("endDate", mList.get(position).to_date);
                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
                        break;

                    case MENU_VIEW_ENTRY_ITEM:
                        Constant.FromWhere = "Bhugtan";
                        fragment = new ViewSellerMilkEntryFragment();
                        bundle = new Bundle();
                        bundle.putString("CustomerId", mList.get(position).id);
                        bundle.putString("CustomerName", mList.get(position).name);
                        bundle.putString("CustomerFatherName", mList.get(position).father_name);
                        bundle.putString("unic_customer", mList.get(position).unic_customer);
                        bundle.putString("user_group_id", mList.get(position).user_group_id);
                        bundle.putString("startDate", mList.get(position).from_date);
                        bundle.putString("endDate", mList.get(position).to_date);
                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    public void pay(int position) {
        bundle = new Bundle();
        double totalamt = Double.parseDouble(mList.get(position).grnd_total_amt);
        strTotalAmt = String.format("%.2f", totalamt);
        type = "debit";
        fromwhere = "Bhugtan";
        selectedId = mList.get(position).id;
        unic_customer = mList.get(position).unic_customer;
        name = mList.get(position).name;
        fatherName = mList.get(position).father_name;

        Constant.FromWhere = "Bhugtan";
        fragment = new PayAmountFragment();
        bundle.putString("CustomerId", selectedId);
        bundle.putString("CustomerName", name);
        bundle.putString("type", type);
        bundle.putString("unic_customer", unic_customer);
        bundle.putString("CustomerFatherName", fatherName);
        bundle.putString("fullAmt", strTotalAmt);
        bundle.putString("fromwhere", fromwhere);
        fragment.setArguments(bundle);
        goNextFragmentReplace(mContext, fragment);
    }


    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvID, tvName, tvTotalWeight, tvTotalPrice;
        CheckBox chkSelect;
        View layoutUpper;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {
            super(view);
            layoutUpper = itemView.findViewById(R.id.layoutUpper);
            tvID = view.findViewById(R.id.tvID);
            tvName = view.findViewById(R.id.tvName);
            tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
            imgMoreDetail = view.findViewById(R.id.imgMoreDetail);
            chkSelect = view.findViewById(R.id.chkSelect);
            //view.setOnClickListener(this);

            layoutUpper.setOnClickListener(this::onClick);
            layoutUpper.setOnClickListener(this::onClick);
            imgMoreDetail.setOnClickListener(this::onClick);


        }


        public void onClick(View view) {

            position = getAdapterPosition();

            switch (view.getId()) {
                case R.id.layoutUpper:
                    //pay(position);
                    break;


                case R.id.imgMoreDetail:
                    showPopup(view, position);
                    break;

                default:
                    break;
            }

        }
    }


}
