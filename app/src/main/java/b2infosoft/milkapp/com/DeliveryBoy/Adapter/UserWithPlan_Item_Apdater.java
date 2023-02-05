package b2infosoft.milkapp.com.DeliveryBoy.Adapter;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.DeliveryBoy.CalculatePriceListener;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanExtraOrder;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserItem;
import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserPlan;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.deliverSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class UserWithPlan_Item_Apdater extends RecyclerView.Adapter<UserWithPlan_Item_Apdater.MyViewHolder> implements CalculatePriceListener {
    Context mContext;
    List<BeanUserItem> userLists;
    List<BeanUserItem> mListFilter;

    Dialog dialog;
    TextView tv_shift, tv_milk_price;
    Button dialogButtonclose;
    RecyclerView recycle_extra_order, recycle_plan_list;
    String strTotalWeight = "", strTotalPrice = "";
    Button btn_save;
    LinearLayout extraOrderLaoyut;
    String Milkshift = "", user_planId = "", plan_name = "", qty = "", user_id = "", delivery_status = "", product_name = "";
    String date = "", pendingBottle = "";
    List<BeanExtraOrder> extraOrderDetailList;
    List<BeanUserPlan> userPlanLists;
    Integer Itenposition = 0;
    Toolbar toolbar;
    JSONArray jsonPlanArray;
    LinearLayout deliveredLayout;
    RelativeLayout relativeLayout;
    SessionManager sessionManager;
    String deliveryboy_id = "";

    public UserWithPlan_Item_Apdater(Context mContext, List<BeanUserItem> userLists, String milkshift) {
        this.mContext = mContext;
        this.Milkshift = milkshift;
        this.userLists = userLists;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(userLists);
        jsonPlanArray = new JSONArray();
        sessionManager = new SessionManager(mContext);
        deliveryboy_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
    }

    public void updateApdater(List<BeanUserItem> userLists) {
        this.userLists = userLists;
        notifyDataSetChanged();

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.delivery_customer_row_item, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        BeanUserItem userAlbum = userLists.get(position);

        String user_name = userAlbum.getUser_name();
        String first_wordC_name = user_name.substring(0, 1).toUpperCase() + user_name.substring(1);
        myViewHolder.textView_userName.setText(first_wordC_name);
        myViewHolder.tv_mobile.setText(userAlbum.getPhone_number());
        delivery_status = userAlbum.getMilk_status_delivery();

        if (delivery_status.equalsIgnoreCase("1")) {
            myViewHolder.textView_status.setText(mContext.getString(R.string.delivered));
            myViewHolder.textView_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_checked, 0, 0, 0);

        } else if (delivery_status.equalsIgnoreCase("0")) {
            myViewHolder.textView_status.setText(mContext.getString(R.string.Vacation));
            Drawable img = mContext.getResources().getDrawable(R.drawable.ic_failure);
            img.setBounds(0, 10, 40, 50);
            myViewHolder.textView_status.setCompoundDrawables(img, null, null, null);
        } else {
            myViewHolder.textView_status.setText(mContext.getString(R.string.pending));
            myViewHolder.textView_status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_order_progress, 0, 0, 0);
            myViewHolder.imgProfile.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onClick(View v) {
                    Itenposition = position;
                    getUserDetails();
                }
            });
            myViewHolder.textView_userName.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onClick(View v) {
                    Itenposition = position;
                    getUserDetails();
                }
            });
            myViewHolder.tv_mobile.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onClick(View v) {
                    Itenposition = position;
                    getUserDetails();
                }
            });
            myViewHolder.textView_status.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onClick(View v) {
                    Itenposition = position;
                    getUserDetails();
                }
            });

        }

        myViewHolder.imgDirection.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + userLists.get(position).getLatitude() + "," + userLists.get(position).longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                try {
                    mContext.startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mContext.startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        showToast(mContext, "Please install a maps application");
                    }
                }
            }
        });

    }

    private void getUserDetails() {

        BeanUserItem userItem = userLists.get(Itenposition);
        userPlanLists = userLists.get(Itenposition).getUserPlanLists();
        user_planId = userItem.getUser_planId();
        delivery_status = userItem.getMilk_status_delivery();
        user_id = userItem.getUser_id();
        date = getSimpleDate();

        extraOrderDetailList = new ArrayList<>();
        qty = pendingBottle;
        if (!userItem.getOrderDetailList().isEmpty()) {
            extraOrderDetailList.addAll(userItem.getOrderDetailList());
        }

        if (delivery_status.equalsIgnoreCase("none")) {
            viewDailog();
        }
    }


    public int getItemCount() {
        return userLists.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void viewDailog() {
        dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_delivery_boy_product_add_entry);

        dialogButtonclose = dialog.findViewById(R.id.btn_cancel);
        btn_save = dialog.findViewById(R.id.btn_save);

        tv_milk_price = dialog.findViewById(R.id.tv_milk_price);
        tv_shift = dialog.findViewById(R.id.tv_shift);

        extraOrderLaoyut = dialog.findViewById(R.id.extraOrderLaoyut);
        deliveredLayout = dialog.findViewById(R.id.deliveredLayout);
        relativeLayout = dialog.findViewById(R.id.relativeLayout);

        toolbar = dialog.findViewById(R.id.toolbar);

        toolbar.setTitle(mContext.getString(R.string.Add_Milk_Entry));

        recycle_extra_order = dialog.findViewById(R.id.recycle_extra_order);
        ExtraOrderItemAdapter adapter = new ExtraOrderItemAdapter(mContext, extraOrderDetailList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recycle_extra_order.setLayoutManager(linearLayoutManager);
        recycle_extra_order.setAdapter(adapter);

        recycle_plan_list = dialog.findViewById(R.id.recycle_plan_list);
        Plan_Item_Adapter planListAdpter = new Plan_Item_Adapter(mContext, userPlanLists, this);
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext);
        recycle_plan_list.setLayoutManager(linearLayout);
        recycle_plan_list.setAdapter(planListAdpter);

        if (!extraOrderDetailList.isEmpty()) {
            extraOrderLaoyut.setVisibility(View.VISIBLE);
        } else {
            extraOrderLaoyut.setVisibility(View.GONE);
        }

        if (Milkshift.equalsIgnoreCase("m")) {
            tv_shift.setText(mContext.getString(R.string.MORNING));
        } else {
            tv_shift.setText(mContext.getString(R.string.EVENING));
        }

        dialogButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getString(R.string.Cancel))
                        .setMessage(mContext.getString(R.string.are_you_sure_want_cancel_entry))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                dialog.dismiss();

            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });


        dialog.show();
    }

    public void CalculatetotalPrice(double totalPrice, int totalweight, List<BeanUserPlan> uploadPlanList) {
        strTotalPrice = Double.toString(totalPrice);
        strTotalWeight = Integer.toString(totalweight);
        tv_milk_price.setText(mContext.getString(R.string.Rupee_symbol) + " " + strTotalPrice);
        jsonPlanArray = new JSONArray();
        System.out.println("==uploadPlanList===" + uploadPlanList.size());

        for (int i = 0; i < uploadPlanList.size(); i++) {
            try {
                if (!uploadPlanList.get(i).getWeight().equalsIgnoreCase("0")) {
                    JSONObject planObject = new JSONObject();
                    planObject.put("plan_id", uploadPlanList.get(i).getId());
                    planObject.put("plan_name", uploadPlanList.get(i).getProduct_name());
                    planObject.put("weight", uploadPlanList.get(i).getWeight());
                    planObject.put("price", uploadPlanList.get(i).getPrice());
                    jsonPlanArray.put(planObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public void validation() {

        addMilkData();
    }

    public void addMilkData() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {
                try {


                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }

                        showToast(mContext, jsonObject.getString("user_status_message"));
                        userLists.get(Itenposition).setMilk_status_delivery("1");
                        notifyDataSetChanged();

                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("user_id", user_id)
                .addEncoded("deliveryboy_id", deliveryboy_id)
                .addEncoded("entry_date", date)
                .addEncoded("milk_entry", String.valueOf(jsonPlanArray)).build();
        caller.addRequestBody(body);

        caller.execute(deliverSaleMilkEntryAPI);


    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        userLists.clear();
        if (charText.length() == 0) {
            userLists.addAll(mListFilter);
        } else {
            for (BeanUserItem wp : mListFilter) {
                if (wp.getPhone_number().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userLists.add(wp);
                } else if (wp.getUser_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userLists.add(wp);
                } else if (wp.getUser_id().toLowerCase(Locale.getDefault()).contains(charText)) {
                    userLists.add(wp);
                }
            }
        }
        updateApdater(userLists);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_userName, textView_status, tv_mobile;
        ImageView imgProfile, imgDirection;
        CardView customer_row;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgDirection = itemView.findViewById(R.id.imgDirection);
            textView_userName = itemView.findViewById(R.id.tv_userName);
            textView_status = itemView.findViewById(R.id.tvStatus);
            tv_mobile = itemView.findViewById(R.id.tv_Mobile);

            customer_row = itemView.findViewById(R.id.customer_row);
        }
    }


}
