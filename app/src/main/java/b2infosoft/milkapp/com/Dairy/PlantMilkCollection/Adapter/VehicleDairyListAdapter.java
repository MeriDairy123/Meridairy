package b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.BeanVehicleDairyItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;


/**
 * Created by B2infosoft on 8/19/2017.
 */

public class VehicleDairyListAdapter extends RecyclerView.Adapter<VehicleDairyListAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanVehicleDairyItem> mList = new ArrayList<>();
    public ArrayList<BeanVehicleDairyItem> mListFilter = new ArrayList<>();
    String fromWhere = "";
    SessionManager sessionManager;
    String dairyid;
    int pos = 0;
    UpdateList listener;

    public VehicleDairyListAdapter(Context mContext, ArrayList<BeanVehicleDairyItem> beanVehicleDairyItems,
                                   String fromWhere, UpdateList listener) {

        this.mContext = mContext;
        this.mList = beanVehicleDairyItems;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        this.fromWhere = fromWhere;
        this.listener = listener;
        sessionManager = new SessionManager(mContext);

        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
    }

    public void updateAdapter(ArrayList<BeanVehicleDairyItem> beanVehicleDairyItems) {
        this.mList = beanVehicleDairyItems;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        notifyDataSetChanged();
    }


    @Override
    public VehicleDairyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_list_row, parent, false);
        return new VehicleDairyListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VehicleDairyListAdapter.MyViewHolder holder, final int position) {
        BeanVehicleDairyItem album = mList.get(position);


        int sr = position + 1;
        holder.tvSr.setText("" + sr + ".");
        holder.tvId.setText(album.unic_customer);

        holder.tvName.setText(album.name);
        holder.tvFatherName.setText(album.father_name);

        String strAmount = nullCheckFunction(album.amount);
        if (strAmount.length() == 0) {
            strAmount = "0";
        }
        System.out.println("amount>>>" + album.amount);
        double amount = Double.parseDouble(strAmount);
        strAmount = String.format("%.2f", amount);
        holder.tvAmount.setText(strAmount);
        pos = position;

    }

    public int getItemCount() {
        return mList.size();
    }

    private void moveNextActivity(int pos) {
        listener.onUpdateList(pos, "next");
    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BeanVehicleDairyItem wp : mListFilter) {
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

            tvSr.setOnClickListener(this);
            tvId.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvFatherName.setOnClickListener(this);
            tvAmount.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();
            switch (view.getId()) {
                case R.id.tvSr:
                case R.id.tvId:
                case R.id.tvName:
                case R.id.tvAmount:
                case R.id.tvFatherName:
                    moveNextActivity(pos);
                    break;

            }
        }
    }


}
