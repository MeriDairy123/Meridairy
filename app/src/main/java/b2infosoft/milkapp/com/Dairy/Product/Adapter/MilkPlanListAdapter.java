package b2infosoft.milkapp.com.Dairy.Product.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.OnClickInAdapter;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDairyMilkPlan;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by Choudhary on 24-Aug-19.
 */

public class MilkPlanListAdapter extends RecyclerView.Adapter<MilkPlanListAdapter.MyViewHolder> {

    private static final int MENU_EDIT_ITEM = Menu.FIRST;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 1;
    public Context mContext;
    public ArrayList<BeanDairyMilkPlan> mList = new ArrayList<>();
    SessionManager sessionManager;
    OnClickInAdapter clickInAdapter;
    int MENU_EDIT = 0;
    int MENU_DELETE = 1;
    String userId = "";


    public MilkPlanListAdapter(Context mContext, ArrayList<BeanDairyMilkPlan> dairyMilkPlans, OnClickInAdapter onClickInAdapter) {
        this.mContext = mContext;
        this.mList = dairyMilkPlans;
        sessionManager = new SessionManager(mContext);
        userId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        clickInAdapter = onClickInAdapter;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_product_list_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        String weight = mContext.getString(R.string.Weight) + " : " + mList.get(position).weight;
        String price = mContext.getString(R.string.Rupee_symbol) + " " + mList.get(position).price;

        holder.tvItem.setText("" + mList.get(position).product_name);
        holder.tvProductWeight.setText(weight);
        holder.tvPrice.setText(price);


        holder.imgMoreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, position);
            }
        });


    }

    private void showPopup(View view, int position) {
        @SuppressLint("ResourceType") ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 0, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 1, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_EDIT_ITEM:
                        clickInAdapter.onClickEditInAdapter(mList.get(position).id, mList.get(position).product_name, mList.get(position).price, mList.get(position).weight, "", "", "", "", "", "", "");
                        break;

                    case MENU_DELETE_ITEM:
                        deleteItem(position);
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    private void deleteItem(int position) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                            @Override
                            public void handleResponse(String response) {

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String staus = jsonObject.getString("status");
                                    if (staus.equalsIgnoreCase("success")) {
                                        mList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, mList.size());
                                        Toast.makeText(mContext, mContext.getString(R.string.Product_Deleted_Successfully), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(mContext, mContext.getString(R.string.Product_Deleting_Failed), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("dairy_id", userId)
                                .addEncoded("id", mList.get(position).id).build();

                        caller.execute(Constant.deleteMilkPlan);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvItem, tvPrice, tvTotalAmt, tvProductWeight;
        ImageView imgMoreDetail, imgProduct;


        public MyViewHolder(View view) {
            super(view);
            tvItem = view.findViewById(R.id.tvItem);
            tvProductWeight = view.findViewById(R.id.tvProductWeight);
            tvPrice = view.findViewById(R.id.tvPrice);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgProduct.setVisibility(View.GONE);
        }

    }
}

