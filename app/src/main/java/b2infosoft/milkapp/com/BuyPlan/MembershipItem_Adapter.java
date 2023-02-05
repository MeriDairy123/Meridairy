package b2infosoft.milkapp.com.BuyPlan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.R;

public class MembershipItem_Adapter extends RecyclerView.Adapter<MembershipItem_Adapter.PlanHolder> {

    private List<BeanSMSPlan> mList;
    private Context context;
    private UpdateList listener;

    public MembershipItem_Adapter(Context context, List<BeanSMSPlan> planList, UpdateList listener) {
        this.context = context;
        this.mList = planList;
        this.listener = listener;

    }

    @NotNull
    @Override
    public PlanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_membership_row, parent, false);
        return new PlanHolder(v);
    }


    @Override
    public void onBindViewHolder(final PlanHolder holder, final int position) {
        BeanSMSPlan album = mList.get(position);
        holder.tvPlanHeader.setText(album.title);
        holder.tvPlanPrice.setText(album.plan_cost + "/-");
        holder.layoutPlan.setBackgroundColor(Color.parseColor(album.colorBackground));
        holder.tvPlanHeader.setBackgroundColor(Color.parseColor(album.colorHeader));
        String validity = context.getResources().getString(R.string.validity) + " : " + album.validity + " " + context.getResources().getString(R.string.day);
        holder.tvValidity.setText(validity);
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

        TextView btnPurchase, tvPlanHeader, tvValidity, tvPlanPrice;
        View layoutPlan;

        PlanHolder(View itemView) {
            super(itemView);
            layoutPlan = itemView.findViewById(R.id.layoutPlan);
            tvPlanHeader = itemView.findViewById(R.id.tvPlanHeader);
            tvValidity = itemView.findViewById(R.id.tvValidity);
            tvPlanPrice = itemView.findViewById(R.id.tvPlanPrice);
            btnPurchase = itemView.findViewById(R.id.btnPurchase);
        }
    }
}
