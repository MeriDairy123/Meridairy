package b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.AddOrEditDeliveryBoyFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Employee_DeliveryBoy.DeliveryBoyUserListFragment;
import b2infosoft.milkapp.com.Model.BeanDeliveryBoyItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.deleteDeliverBoyAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Latitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_Longitude;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_address;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_city;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_postCode;
import static b2infosoft.milkapp.com.appglobal.Constant.str_location_state;
import static b2infosoft.milkapp.com.appglobal.Constant.updateDeliverBoyAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;


/**
 * Created by B2infosoft on 03/08/2019.
 */

public class DeliveryBoyListAdapter extends RecyclerView.Adapter<DeliveryBoyListAdapter.MyViewHolder> {

    private static final int MENU_CHAT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 2;
    public Context mContext;
    public ArrayList<BeanDeliveryBoyItem> mList = new ArrayList<>();
    public ArrayList<BeanDeliveryBoyItem> mListFilter = new ArrayList<>();
    String fromWhere = "";
    SessionManager sessionManager;
    String dairyid;
    int MENU_CHAT = 0;
    int MENU_EDIT = 1;
    int MENU_DELETE = 2;
    Bundle bundle = null;
    Fragment fragment = null;
    int pos = 0;

    public DeliveryBoyListAdapter(Context mContext, ArrayList<BeanDeliveryBoyItem> BeanDeliveryBoyItems) {

        this.mContext = mContext;
        this.mList = BeanDeliveryBoyItems;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
    }

    public void updateAdapter(ArrayList<BeanDeliveryBoyItem> BeanDeliveryBoyItems) {
        this.mList = BeanDeliveryBoyItems;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        notifyDataSetChanged();
    }


    @Override
    public DeliveryBoyListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.dairy_delivery_boy_row_item, parent, false);
        return new DeliveryBoyListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DeliveryBoyListAdapter.MyViewHolder holder, final int position) {

        int sr = position + 1;

        holder.tvName.setText(mList.get(position).name);
        holder.tvMobile.setText(mList.get(position).phone_number);
        holder.tvAddress.setText(mList.get(position).address);


        pos = position;
        if (mList.get(position).status == 1) {
            holder.chkStatus.setChecked(true);
        }

        holder.chkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.chkStatus.isChecked() != true) {
                    holder.chkStatus.setChecked(false);
                    updateStatus(false, position);
                } else {
                    holder.chkStatus.setChecked(true);
                    updateStatus(true, position);
                }
            }
        });
    }


    public int getItemCount() {
        return mList.size();
    }

    private void updateStatus(final boolean status, final int position) {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("success")) {
                        if (status) {
                            mList.get(position).status = 1;
                        } else {
                            mList.get(position).status = 0;
                        }

                        UtilityMethod.showAlertBox(mContext, "" + mContext.getString(R.string.Updating_Success));
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String strStatus = "0";
        if (status) {
            strStatus = "1";
        }

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyid)
                .addEncoded("id", mList.get(position).id)
                .addEncoded("status", strStatus)
                .addEncoded("name", mList.get(position).name)
                .addEncoded("address", mList.get(position).address)
                .build();
        caller.addRequestBody(body);

        caller.execute(updateDeliverBoyAPI);


    }

    private void deleteItem(int pos) {

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
                                    if (staus.equals("success")) {

                                        mList.remove(pos);
                                        notifyItemRemoved(pos);
                                        notifyItemRangeChanged(pos, mList.size());
                                        //    mItemManger.closeAllItems();
                                        Toast.makeText(mContext, mContext.getString(R.string.Entry_Deleted_Successfully), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(mContext, mContext.getString(R.string.Entry_Deleting_Failed), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("id", mList.get(pos).id).build();
                        caller.addRequestBody(body);
                        caller.execute(deleteDeliverBoyAPI);

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

    private void editItem(int pos) {

        str_location_address = "";
        str_location_city = "";
        str_location_state = "";
        str_location_postCode = "";
        str_location_Latitude = "";
        str_location_Longitude = "";
        fragment = new AddOrEditDeliveryBoyFragment();
        bundle = new Bundle();
        bundle.putString("id", mList.get(pos).id);
        bundle.putString("name", mList.get(pos).name);
        bundle.putString("fatherName", mList.get(pos).father_name);
        bundle.putString("mobile", mList.get(pos).phone_number);
        bundle.putString("address", mList.get(pos).address);

        fragment.setArguments(bundle);
        goNextFragmentWithBackStack(mContext, fragment);
    }

    private void showPopup(View v, int pos) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, v);
        popup.getMenu().add(MENU_CHAT, MENU_CHAT_ITEM, 0, mContext.getString(R.string.USER_LIST));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 1, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 2, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_CHAT_ITEM:

                        Gson gson = new Gson();
                        String json = gson.toJson(mList.get(pos));
                        sessionManager.setValueSession("beanDeliveryBoyItem", json);

                        fragment = new DeliveryBoyUserListFragment();
                        bundle = new Bundle();
                        int deliveryBoyId = Integer.parseInt(mList.get(pos).id);
                        bundle.putInt("id", deliveryBoyId);

                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
                        break;

                    case MENU_EDIT_ITEM:
                        editItem(pos);
                        break;

                    case MENU_DELETE_ITEM:
                        deleteItem(pos);
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    private void moveNextActivity(int pos) {

    }

    public void filterSearch(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BeanDeliveryBoyItem wp : mListFilter) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.phone_number.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvMobile, tvAddress;
        CheckBox chkStatus;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {

            super(view);
            tvName = view.findViewById(R.id.tvName);
            tvMobile = view.findViewById(R.id.tvMobile);
            tvAddress = view.findViewById(R.id.tvAddress);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            chkStatus = itemView.findViewById(R.id.checkBox);
            view.setOnClickListener(this);
            tvName.setOnClickListener(this);
            tvAddress.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();
            switch (view.getId()) {

                case R.id.tvName:
                    moveNextActivity(pos);
                    break;

                case R.id.tvAmount:
                    moveNextActivity(pos);
                    break;
                case R.id.tvFatherName:
                    moveNextActivity(pos);
                    break;
                case R.id.imgMoreDetail:
                    showPopup(view, pos);
                    break;
            }
        }

    }


}
