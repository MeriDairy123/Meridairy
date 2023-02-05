package b2infosoft.milkapp.com.Dairy.Invoice;


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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Interface.CommonOnClickListener;
import b2infosoft.milkapp.com.Model.BeanDairyCustomerInvoiceItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 26/08/2019.
 */

public class CustomerInvoiceItemAdapter extends RecyclerView.Adapter<CustomerInvoiceItemAdapter.MyViewHolder> {
    private static final int MENU_VIEWENTRY_ITEM = Menu.FIRST;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 1;
    private static final int MENU_DETAILS_ITEM = Menu.FIRST + 2;
    public Context mContext;
    public ArrayList<BeanDairyCustomerInvoiceItem> mList;
    public ArrayList<BeanDairyCustomerInvoiceItem> mFilterList = new ArrayList<>();
    SessionManager sessionManager;
    CommonOnClickListener listener;
    int itemPosition = 0;
    int MENU_VIEWENTRY = 0;
    int MENU_DELETE = 1;
    int MENU_DETAILS = 2;

    public CustomerInvoiceItemAdapter(Context mContext, ArrayList<BeanDairyCustomerInvoiceItem> entryList, CommonOnClickListener listener) {
        this.mContext = mContext;
        this.mList = entryList;
        this.listener = listener;
        mFilterList = new ArrayList<>();
        mFilterList.addAll(mList);
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dairy_customer_invoice_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        BeanDairyCustomerInvoiceItem album = mList.get(position);
        holder.setIsRecyclable(false);
        String strWeight = String.format("%.3f", album.getWeight());
        String strAmount = String.format("%.2f", album.getAmount());
        holder.tvSr.setText(" " + album.getStartDate().substring(0, 2) + "-" + album.getEndDate().substring(0, 2));
        holder.tvName.setText(album.getUniqCustomerId() + ". " + album.getName());
        holder.tvWeight.setText(" " + strWeight);
        holder.tvAmount.setText(" " + strAmount);


    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mFilterList);
        } else {
            for (BeanDairyCustomerInvoiceItem wp : mFilterList) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getItemCount() {
        return mList.size();
    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_VIEWENTRY, MENU_VIEWENTRY_ITEM, 0, mContext.getString(R.string.viewEntry));
         popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 1, mContext.getString(R.string.Delete));
        popup.getMenu().add(MENU_DETAILS, MENU_DETAILS_ITEM, 2, mContext.getString(R.string.MORE_DETAILS));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case MENU_VIEWENTRY_ITEM:
                        listener.onAdapterItemClick(position, "viewEntry");
                        break;
                    case MENU_DELETE_ITEM:
                       deleteItem(position);
                        break;
                    case MENU_DETAILS_ITEM:
                        listener.onAdapterItemClick(position, "details");
                        break;
                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvSr, tvName, tvWeight, tvAmount;
        ImageView imgAction;

        public MyViewHolder(View view) {
            super(view);
            tvSr = view.findViewById(R.id.tvSr);
            tvName = view.findViewById(R.id.tvName);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvAmount = view.findViewById(R.id.tvAmount);
            imgAction = view.findViewById(R.id.imgAction);
            tvSr.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvWeight.setOnClickListener(this);
            tvAmount.setOnClickListener(this);
            imgAction.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            showPopup(view, getLayoutPosition());
        }
    }

    private void deleteItem(int pos) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkAvaliable(mContext)) {
                            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Delete) + "...", true) {
                                @Override
                                public void handleResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String staus = jsonObject.getString("status");
                                        if (staus.equalsIgnoreCase("success")) {
                                            listener.onAdapterItemClick(pos, "delete");
                                            showToast(mContext, jsonObject.getString("user_status_message"));
                                            notifyDataSetChanged();

                                        } else {
                                            showToast(mContext, jsonObject.getString("user_status_message"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            RequestBody body = new FormEncodingBuilder()
                                    .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                                    .addEncoded("id", mList.get(pos).getId())
                                   //.addEncoded("customer_id", mList.get(pos).getCustomerId())
                                    .addEncoded("type", "delete")

                                    .build();
                            caller.addRequestBody(body);
                            String url= Constant.generateSellerInvoiceAPI;
                            if (mList.get(pos).getUserGroupId().equals("4")){
                                url= Constant.generateBuyerInvoiceAPI;
                            }
                            caller.execute(url);

                        }
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}




