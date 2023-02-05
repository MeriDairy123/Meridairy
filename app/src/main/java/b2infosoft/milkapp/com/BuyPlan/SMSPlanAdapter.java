package b2infosoft.milkapp.com.BuyPlan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.R;

public class SMSPlanAdapter extends RecyclerView.Adapter<SMSPlanAdapter.PlanHolder> {


    UpdateList listener;
    private List<BeanSMSPlan> mList;
    private Context context;

    public SMSPlanAdapter(Context context, List<BeanSMSPlan> planList, UpdateList listener) {
        this.context = context;
        this.mList = planList;
        this.listener = listener;
    }

    @Override
    public PlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_message_row, parent, false);
        return new PlanHolder(v);
    }

    @Override
    public void onBindViewHolder(final PlanHolder holder, final int position) {
        BeanSMSPlan album = mList.get(position);
        holder.tvPlanHeader.setText(album.title);
        holder.tvPlanPrice.setText(album.plan_cost + "/-");
        String validity = "Validity : " + album.validity;
        holder.tvValidity.setText(validity);

        holder.layoutPlan.setBackgroundColor(Color.parseColor(album.colorBackground));
        holder.tvPlanHeader.setBackgroundColor(Color.parseColor(album.colorHeader));
        holder.itemView.setTranslationY(-(10 + position * 100));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(1000).start();

        holder.btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onUpdateList(position, "");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class PlanHolder extends RecyclerView.ViewHolder {

        TextView btnPurchase, tvPlanHeader, tvPlanPrice, tvValidity;
        View layoutPlan;

        PlanHolder(View itemView) {
            super(itemView);
            layoutPlan = itemView.findViewById(R.id.layoutPlan);
            tvPlanHeader = itemView.findViewById(R.id.tvPlanHeader);
            tvPlanPrice = itemView.findViewById(R.id.tvPlanPrice);
            tvValidity = itemView.findViewById(R.id.tvValidity);
            btnPurchase = itemView.findViewById(R.id.btnPurchase);
        }


    }
}
