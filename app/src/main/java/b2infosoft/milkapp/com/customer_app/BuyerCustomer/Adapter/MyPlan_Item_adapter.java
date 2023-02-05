package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.Interface.MyPlanListner;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.updateMilkPlanAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by Choudhary on 03-July-19.
 */

public class MyPlan_Item_adapter extends RecyclerView.Adapter<MyPlan_Item_adapter.MyViewHolder> {

    Dialog dialog;
    MyPlanListner myPlanListner;
    String planId = "", strPlanName = "", strWeight = "", strPrice = "", strShift = "", strStatus = "";
    SessionManager sessionManager;
    String from = "", userGroupId = "4";
    private Context mContext;
    private List<BeanMilkPlan> albumList;

    public MyPlan_Item_adapter(Context context, List<BeanMilkPlan> albumList) {
        this.mContext = context;
        this.albumList = albumList;
        sessionManager = new SessionManager(mContext);
        from = "";

    }

    public MyPlan_Item_adapter(Context context, List<BeanMilkPlan> albumList, MyPlanListner listner, String from) {
        this.mContext = context;
        this.albumList = albumList;
        sessionManager = new SessionManager(mContext);
        this.myPlanListner = listner;
        this.from = from;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_plan_row_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeanMilkPlan album = albumList.get(position);

        String weight = mContext.getString(R.string.Weight) + " : " + album.getWeight();
        String shift = "Shift : " + album.getShift();

        String price = album.getPrice();
        holder.tvTitle.setText(album.getProduct_name());

        holder.tvWeight.setText(weight);
        holder.tvShift.setText(shift);

        holder.tvPrice.setText(mContext.getString(R.string.Rupee_symbol) + " " + price);
        holder.itemView.setTranslationY(-(100 + position * 100));
        holder.itemView.setAlpha(0.5f);
        holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();

        holder.planView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                planId = album.getId();
                strPlanName = album.getProduct_name();
                strWeight = album.getWeight();
                strPrice = album.getPrice();
                strShift = album.getShift();
                strStatus = album.getStatus();
                dialogEventView();
            }
        });

    }

    public void dialogEventView() {

        dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_update_milk_plan);
        ImageView imgClosed;

        TextView tvTitle, tvShift;
        RadioGroup radioGroup;
        RadioButton rdbEnable, rdbDisable;
        EditText ediMilkWeight, ediMilkPrice;
        Button btnUpdate;


        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvShift = dialog.findViewById(R.id.tvShift);
        radioGroup = dialog.findViewById(R.id.radioGroup);
        rdbEnable = dialog.findViewById(R.id.rdbEnable);
        rdbDisable = dialog.findViewById(R.id.rdbDisable);

        ediMilkWeight = dialog.findViewById(R.id.ediMilkWeight);
        ediMilkPrice = dialog.findViewById(R.id.ediMilkPrice);
        btnUpdate = dialog.findViewById(R.id.btnUpdate);


        strShift = mContext.getString(R.string.Shift) + " : " + strShift;
        tvTitle.setText(strPlanName);
        tvShift.setText(strShift);
        ediMilkWeight.setText(strWeight);
        ediMilkPrice.setText(strPrice);
        if (from.equalsIgnoreCase("Dairy")) {
            ediMilkPrice.setEnabled(true);
        } else {
            ediMilkPrice.setEnabled(false);
        }
        if (strStatus.equalsIgnoreCase("1")) {
            rdbEnable.setChecked(true);
        } else {
            rdbDisable.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rdbEnable:
                        strStatus = "1";
                        break;
                    case R.id.rdbDisable:
                        strStatus = "0";
                        break;
                }

            }
        });

        imgClosed = dialog.findViewById(R.id.imgClosed);

        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strWeight = ediMilkWeight.getText().toString();
                strPrice = ediMilkPrice.getText().toString();
                if (strWeight.length() == 0) {
                    ediMilkWeight.requestFocus();
                    showToast(mContext, mContext.getString(R.string.error_field_required));

                } else if (strWeight.startsWith("0")) {
                    ediMilkWeight.requestFocus();
                    showToast(mContext, mContext.getString(R.string.Please_Edit_Sale_Milk_Weight_It_not_to_be_0));
                } else if (strPrice.startsWith("0")) {
                    ediMilkPrice.requestFocus();
                    showToast(mContext, mContext.getString(R.string.Please_Enter_Amount));
                } else {
                    ediMilkPrice.clearFocus();
                    ediMilkWeight.clearFocus();
                    updatePlan(mContext);
                }
            }
        });


        dialog.show();
    }

    public void updatePlan(final Context mContext) {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Updating Plan...", true) {
                @Override
                public void handleResponse(String response) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equalsIgnoreCase("success")) {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                            dialog.dismiss();
                            myPlanListner.refreshList();

                        } else {
                            showToast(mContext, jsonObject.getString("user_status_message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("customer_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                    .addEncoded("id", planId)
                    .addEncoded("weight", strWeight)
                    .addEncoded("price", strPrice)
                    .addEncoded("status", strStatus)
                    .addEncoded("user_group_id", userGroupId)
                    .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                    .build();

            caller.addRequestBody(body);

            caller.execute(updateMilkPlanAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvWeight, tvShift, tvPrice;
        View imgNext, planView;

        public MyViewHolder(View view) {
            super(view);

            planView = view.findViewById(R.id.planView);
            imgNext = view.findViewById(R.id.imgNext);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvShift = view.findViewById(R.id.tvShift);
            tvPrice = view.findViewById(R.id.tvPrice);

            if (from.length() == 0) {
                imgNext.setVisibility(View.GONE);
            } else {
                imgNext.setVisibility(View.VISIBLE);
            }
        }

    }

}
