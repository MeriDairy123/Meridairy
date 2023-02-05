package b2infosoft.milkapp.com.DeliveryBoy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.DeliveryBoy.CalculatePriceListener;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserPlan;
import b2infosoft.milkapp.com.R;


public class Plan_Item_Adapter extends RecyclerView.Adapter<Plan_Item_Adapter.MyViewHolder> {
    List<BeanUserPlan> userPlanLists;
    Context mContext;
    double price = 0, grossPrice = 0, planTotalPrice = 0;
    Integer grossWeight = 0, qtncount = 0;
    double plan_total_price = 0;
    CalculatePriceListener listener;
    List<BeanUserPlan> uploadPlanList;


    public Plan_Item_Adapter(Context mContext, List<BeanUserPlan> userPlanLists, CalculatePriceListener listener) {
        this.mContext = mContext;
        this.userPlanLists = userPlanLists;
        this.listener = listener;
        uploadPlanList = new ArrayList<>();
    }

    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.deliver_plan_row_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final BeanUserPlan userAlbum = userPlanLists.get(position);

        holder.tv_product_name.setText(userAlbum.getProduct_name());
        holder.tvQnItemCount.setText(userAlbum.getWeight());


        price = userAlbum.getPrice();
        String str_weight = userAlbum.getWeight();
        String[] spitStr = str_weight.split("\\|");
        String number = spitStr[0].replaceAll("[^0-9]", "");
        Integer weight = Integer.valueOf(number);

        planTotalPrice = weight * price;
        qtncount = weight;
        grossPrice = grossPrice + planTotalPrice;
        grossWeight = grossWeight + weight;


        System.out.println("==planTotalPrice====" + planTotalPrice);
        holder.tv_product_price.setText(mContext.getString(R.string.Rupee_symbol) + " " + Double.toString(planTotalPrice));


        holder.btn_Qtn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price = userAlbum.getPrice();
                qtncount = Integer.valueOf(holder.tvQnItemCount.getText().toString());

                qtncount = qtncount + 1;

                grossWeight = grossWeight + 1;
                grossPrice = grossPrice + price;
                plan_total_price = qtncount * price;
                String weight = String.valueOf(qtncount);
                uploadPlanList.get(position).setWeight(weight);

                System.out.println("==weight==" + weight);
                String qtn_value = Integer.toString(Integer.parseInt(weight));
                holder.tvQnItemCount.setText(qtn_value);

                System.out.println("==totalPrice===" + plan_total_price);
                String str_totalPrice = Double.toString(plan_total_price);
                holder.tv_product_price.setText(mContext.getString(R.string.Rupee_symbol) + " " + str_totalPrice);

                listener.CalculatetotalPrice(grossPrice, grossWeight, uploadPlanList);
            }


        });
        holder.btn_Qtn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                price = userAlbum.getPrice();
                qtncount = Integer.valueOf(holder.tvQnItemCount.getText().toString());
                if (qtncount > 0) {
                    qtncount = qtncount - 1;
                    plan_total_price = qtncount * price;
                    grossWeight = grossWeight - 1;
                    grossPrice = grossPrice - price;
                    String weight = String.valueOf(qtncount);
                    uploadPlanList.get(position).setWeight(weight);

                    System.out.println("==weight==" + weight);
                    String qtn_value = Integer.toString(Integer.parseInt(weight));
                    holder.tvQnItemCount.setText(qtn_value);

                    String str_totalPrice = Double.toString(plan_total_price);
                    System.out.println("==totalPrice===" + str_totalPrice);

                    holder.tv_product_price.setText(mContext.getString(R.string.Rupee_symbol) + " " + str_totalPrice);
                    listener.CalculatetotalPrice(grossPrice, grossWeight, uploadPlanList);
                }
            }


        });
        uploadPlanList.add(new BeanUserPlan(userAlbum.getId(), userAlbum.getProduct_name(), userAlbum.getWeight(), userAlbum.getPrice()));
        if (position == userPlanLists.size() - 1) {
            System.out.println("==planTotalPrice====" + grossPrice);

            listener.CalculatetotalPrice(grossPrice, grossWeight, uploadPlanList);
        }
    }

    @Override
    public int getItemCount() {
        return userPlanLists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_product_name, tv_product_price, tvQnItemCount;
        ImageView btn_Qtn_minus, btn_Qtn_plus;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tvProductName);
            tv_product_price = itemView.findViewById(R.id.tvPrice);
            tvQnItemCount = itemView.findViewById(R.id.tvItemCount);
            btn_Qtn_minus = itemView.findViewById(R.id.imgQtyMinus);
            btn_Qtn_plus = itemView.findViewById(R.id.imgQtyPlus);

        }

    }


}
