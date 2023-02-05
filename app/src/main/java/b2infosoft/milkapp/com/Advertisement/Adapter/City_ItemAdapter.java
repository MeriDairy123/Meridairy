package b2infosoft.milkapp.com.Advertisement.Adapter;


import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.CitySelectkListner;
import b2infosoft.milkapp.com.Model.BeanCityAdvItem;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 11-May-19.
 */

public class City_ItemAdapter extends RecyclerView.Adapter<City_ItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanCityAdvItem> mList;
    int selectedCitySize = 0;
    // for loading main list

    CitySelectkListner citySelectkListner;

    public City_ItemAdapter(Context mContext, ArrayList<BeanCityAdvItem> mlist,
                            CitySelectkListner listner, int selectedCitySize) {
        this.mContext = mContext;

        this.mList = mlist;
        this.citySelectkListner = listner;
        this.selectedCitySize = selectedCitySize;
    }

    public void updateCitySize(int selectedCitySize) {

        this.selectedCitySize = selectedCitySize;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.city_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final BeanCityAdvItem albam = mList.get(position);
        int city_count = albam.getCity_count();

        holder.tvTitle.setText(Html.fromHtml(albam.cityName));
        if (city_count >= 5) {
            holder.itemView.setEnabled(false);
            holder.checkBox.setEnabled(false);

        } else {
            holder.itemView.setEnabled(true);
            holder.checkBox.setEnabled(true);
        }


        if (selectedCitySize >= 5) {
            holder.itemView.setEnabled(false);
            holder.checkBox.setEnabled(false);

        } else {
            holder.itemView.setEnabled(true);
            holder.checkBox.setEnabled(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean ischecked = mList.get(position).selected;
                    if (ischecked) {
                        mList.get(position).setSelected(false);
                        holder.checkBox.setChecked(false);
                    } else {
                        holder.checkBox.setChecked(true);
                        mList.get(position).setSelected(true);

                    }


                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        citySelectkListner.onCitySelect(albam, true);

                    } else {
                        citySelectkListner.onCitySelect(albam, false);
                    }

                }
            });

        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            checkBox = view.findViewById(R.id.checkBox);


        }


    }

}
