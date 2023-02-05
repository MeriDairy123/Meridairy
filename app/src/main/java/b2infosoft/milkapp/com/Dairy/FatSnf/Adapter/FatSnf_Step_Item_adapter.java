package b2infosoft.milkapp.com.Dairy.FatSnf.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.RateCardStep;
import b2infosoft.milkapp.com.R;


/**
 * Created by Choudhary on 11/07/2019.
 */

public class FatSnf_Step_Item_adapter extends RecyclerView.Adapter<FatSnf_Step_Item_adapter.MyViewHolder> {

    UpdateList updateList;
    String from = "";
    private Context mContext;
    private List<RateCardStep> albumList;

    public FatSnf_Step_Item_adapter(Context context, List<RateCardStep> albumList, String from, UpdateList updateList) {
        this.mContext = context;
        this.albumList = albumList;
        this.from = from;
        this.updateList = updateList;

    }

    @Override
    public FatSnf_Step_Item_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fatsnf_step_rate_row_item, parent, false);
        return new FatSnf_Step_Item_adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FatSnf_Step_Item_adapter.MyViewHolder holder, final int position) {
        final RateCardStep album = albumList.get(position);
        holder.tvlableName.setText("" + album.getValue());
        holder.tvName.setText("" + album.getIncrement_by());
        if (position == albumList.size() - 1) {

            holder.imgRemove.setVisibility(View.VISIBLE);
            holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateList.onUpdateList(position, from);
                }
            });
        } else {
            holder.imgRemove.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvlableName, tvName;
        ImageView imgRemove;

        public MyViewHolder(View view) {
            super(view);
            imgRemove = view.findViewById(R.id.imgRemove);
            tvlableName = view.findViewById(R.id.tvlableName);
            tvName = view.findViewById(R.id.tvName);


        }

    }


}
