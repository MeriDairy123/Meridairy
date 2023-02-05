package b2infosoft.milkapp.com.Dairy.Customer.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.ChatActivity;
import b2infosoft.milkapp.com.Dairy.Customer.AddCustomerFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


/**
 * Created by B2infosoft on 8/19/2017.
 */

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private static final int MENU_CHAT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 2;
    public Context mContext;
    public ArrayList<CustomerListPojo> mList = new ArrayList<>();
    public ArrayList<CustomerListPojo> mListFilter = new ArrayList<>();
    String fromWhere = "";
    SessionManager sessionManager;
    DatabaseHandler databaseHandler;
    String dairyid;
    int MENU_CHAT = 0;
    int MENU_EDIT = 1;
    int MENU_DELETE = 2;
    Bundle bundle = null;
    Fragment fragment = null;
    Intent intent = null;
    int pos = 0;
    OnCustomerListener customerListener;

    public UserListAdapter(Context mContext, ArrayList<CustomerListPojo> customerListPojos, String fromWhere, OnCustomerListener listener) {

        this.mContext = mContext;
        this.mList = customerListPojos;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        this.fromWhere = fromWhere;
        this.customerListener = listener;
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
    }

    public void updateAdapter(ArrayList<CustomerListPojo> customerListPojos) {
        this.mList = customerListPojos;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        notifyDataSetChanged();
    }


    @Override
    public UserListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_list_row, parent, false);
        return new UserListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final UserListAdapter.MyViewHolder holder, final int position) {
        CustomerListPojo album = mList.get(position);


        int sr = position + 1;
        holder.tvSr.setText("" + sr + ".");
        holder.tvId.setText(album.unic_customer);

        holder.tvName.setText(album.name);
        holder.tvFatherName.setText(album.father_name);

        String strAmount = nullCheckFunction(album.amount);
        if (strAmount.length() == 0) {
            strAmount = "0";
        }
        System.out.println("amount>>>" + album.amount);
        double amount = Double.parseDouble(strAmount);
        strAmount = String.format("%.2f", amount);
        holder.tvAmount.setText(strAmount);
        pos = position;

    }


    public int getItemCount() {
        return mList.size();
    }

    private void deleteItem(int pos) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                    @Override
                    public void handleResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String staus = jsonObject.getString("status");
                            if (staus.equals("success")) {
                                databaseHandler.deleteParticularCustomer(mList.get(pos).id);
                                mList.remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, mList.size());
                                showToast(mContext, mContext.getString(R.string.Customer_Deleted_Successfully));
                            } else {
                                showToast(mContext, mContext.getString(R.string.Customer_Deleting_Failed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("id", mList.get(pos).id).build();
                caller.addRequestBody(body);

                caller.execute(Constant.deleteCustomerAPI);

            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    private void editItem(int pos) {
        fragment = new AddCustomerFragment();
        bundle = new Bundle();

        bundle.putString("from", "UserListAdapter");
        bundle.putString("CustomerID", mList.get(pos).id);
        bundle.putString("Name", mList.get(pos).name);
        bundle.putString("FatherName", mList.get(pos).father_name);
        bundle.putString("Village", mList.get(pos).village);
        bundle.putString("Address", mList.get(pos).address);
        bundle.putString("Aadhar", mList.get(pos).adhar);
        bundle.putString("Mobile", mList.get(pos).phone_number);
        bundle.putString("unic_customer", mList.get(pos).unic_customer);

        bundle.putString("user_group_id", mList.get(pos).user_group_id);
        bundle.putString("category_chart_id", mList.get(pos).categorychart_id);
        bundle.putString("entry_type", mList.get(pos).entry_type);
        bundle.putString("rate", mList.get(pos).entry_price);
        bundle.putString("accno", mList.get(pos).accountNo);
        bundle.putString("ifsc_code", mList.get(pos).iFSC_Code);
        bundle.putString("bank_name", mList.get(pos).bankName);
        fragment.setArguments(bundle);
        goNextFragmentWithBackStack(mContext, fragment);
    }

    private void showPopup(View v, int pos) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, v);
        popup.getMenu().add(MENU_CHAT, MENU_CHAT_ITEM, 0, mContext.getString(R.string.Chat));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 1, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 2, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_CHAT_ITEM:

                        intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("FRIEND_NAME", mList.get(pos).name);
                        intent.putExtra("unic_customer_for_mobile", mList.get(pos).unic_customer_for_mobile);
                        intent.putExtra("FRIEND_id", mList.get(pos).id);
                        intent.putExtra("firebase_tocan", mList.get(pos).firebase_tocan);
                        intent.putExtra("FRIEND_mob", mList.get(pos).phone_number);
                        mContext.startActivity(intent);
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
        customerListener.onClickUser(mList.get(pos));
    }

    public void filterSearch(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (CustomerListPojo wp : mListFilter) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.unic_customer.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvSr, tvId, tvName, tvFatherName, tvAmount;
        ImageView imgMoreDetail;

        public MyViewHolder(View view) {

            super(view);
            tvSr = view.findViewById(R.id.tvSr);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvFatherName = view.findViewById(R.id.tvFatherName);
            tvAmount = view.findViewById(R.id.tvAmount);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);

            tvName.setOnClickListener(this);
            tvFatherName.setOnClickListener(this);
            tvAmount.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            pos = getAdapterPosition();
            switch (view.getId()) {
                case R.id.tvName:
                case R.id.tvAmount:
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
