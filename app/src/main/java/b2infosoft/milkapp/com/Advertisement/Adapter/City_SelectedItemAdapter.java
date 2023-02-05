package b2infosoft.milkapp.com.Advertisement.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.TahsilSelectkListner;
import b2infosoft.milkapp.com.Model.BeanCityAdvItem;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 11-May-19.
 */

public class City_SelectedItemAdapter extends RecyclerView.Adapter<City_SelectedItemAdapter.MyViewHolder> {

    public Context mContext;
    public ArrayList<BeanCityAdvItem> mList;  // for loading main list
    TahsilSelectkListner listner;

    public City_SelectedItemAdapter(Context mContext, ArrayList<BeanCityAdvItem> mlist,
                                    TahsilSelectkListner listner) {
        this.mContext = mContext;
        this.mList = mlist;
        this.listner = listner;
    }

    public void updateAdapter(ArrayList<BeanCityAdvItem> beanCityAdvItems) {
        this.mList = beanCityAdvItems;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.city_selected_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        BeanCityAdvItem albam = mList.get(position);
        holder.tvTitle.setText(albam.cityName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyDataSetChanged();
                listner.onTahsilSelect(mList);


            }
        });
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public ImageView imgClosed;

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            imgClosed = view.findViewById(R.id.imgClosed);

        }


    }

}
