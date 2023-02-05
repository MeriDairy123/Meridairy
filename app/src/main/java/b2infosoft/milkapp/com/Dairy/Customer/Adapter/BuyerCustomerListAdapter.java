package b2infosoft.milkapp.com.Dairy.Customer.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.Bhugtan.TransactionOldHistoryFragment;
import b2infosoft.milkapp.com.Dairy.ChatActivity;
import b2infosoft.milkapp.com.Dairy.Customer.AddCustomerFragment;
import b2infosoft.milkapp.com.Dairy.Product.SaleItemFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.BuyerMonthlyDetailsFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.fragment_BuyerMilkPlan;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BuyerMilkCustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerMilkListAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by u on 05-Dec-17.
 */

public class BuyerCustomerListAdapter extends RecyclerView.Adapter<BuyerCustomerListAdapter.MyViewHolder> {
    private final int MENU_CHAT_ITEM = Menu.FIRST;
    private final int MENU_EDIT_MILK_ITEM = Menu.FIRST + 1;
    private final int MENU_MILK_PLAN_ITEM = Menu.FIRST + 2;
    private final int MENU_EDIT_PROFILE_ITEM = Menu.FIRST + 3;
    private final int MENU_DELETE_ITEM = Menu.FIRST + 4;
    public Context mContext;
    public ArrayList<BuyerMilkCustomerListPojo> mList = new ArrayList<>();
    public ArrayList<BuyerMilkCustomerListPojo> mListFilter = new ArrayList<>();
    String name = "";
    SessionManager sessionManager;
    int MENU_CHAT = 0;
    int MENU_EDIT_MILK = 1;
    int MENU_EDIT_PLAN = 2;
    int MENU_EDIT_PROFILE = 3;
    int MENU_DELETE = 4;
    Bundle bundle = null;
    Fragment fragment = null;
    Intent intent = null;
    int pos = 0;
    boolean is_active = false;
    RecyclerView.ViewHolder holderPosition = null;
    DatabaseHandler databaseHandler;
    Dialog dialog;

    public BuyerCustomerListAdapter(Context mContext, ArrayList<BuyerMilkCustomerListPojo> entryList) {
        this.mContext = mContext;
        this.mList = entryList;
        mListFilter = new ArrayList<>();
        this.mListFilter.addAll(mList);
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
    }

    public void updaterAdapter(ArrayList<BuyerMilkCustomerListPojo> entryList) {

        this.mList = entryList;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_buyer_customer_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holderPosition = holder;
        pos = position;
        holder.setIsRecyclable(false);
        int srNo = position + 1;
        holder.tvS_no.setText("" + srNo + ".");
        holder.tvId.setText(mList.get(position).unic_customer);
        holder.txtCustomerName.setText(mList.get(position).name);
        if (mList.get(position).is_active.equals("1")) {
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

        holder.txtMobileNo.setText(mList.get(position).phone_number);

    }

    public void filterSearch(String charText) {
        System.out.println("==search filter===Buyer====" + charText);
        charText = charText.toLowerCase(Locale.getDefault());
        mList.clear();
        if (charText.length() == 0) {
            mList.addAll(mListFilter);
        } else {
            for (BuyerMilkCustomerListPojo wp : mListFilter) {
                if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                } else if (wp.unic_customer.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void updateStatus(boolean status, int position) {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Constant.StatusClicked = "true";
                    if (jsonObject.getString("status").equals("success")) {
                        if (status) {
                            mList.get(position).is_active = "1";
                        } else {
                            mList.get(position).is_active = "2";
                        }
                        updateBuyer();
                        UtilityMethod.showAlertBox(mContext, "" + mContext.getString(R.string.Updating_Success));
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        String is_active = "2";
        if (status) {
            is_active = "1";
        }
        //is_active=1&dairy_id=2&customer_id=4
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("customer_id", mList.get(position).customer_id)
                .addEncoded("is_active", is_active).build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(Constant.updateBuyerMilkEntryStatusAPI);


    }

    private void updateBuyer() {


        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() != 0) {
                            databaseHandler.deleteBuyerCustomer();
                        }
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject obj = mainJsonArray.getJSONObject(i);
                            mList.add(new BuyerMilkCustomerListPojo(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan")));

                            databaseHandler.addBuyerCustomer(obj.getString("id"),
                                    obj.getString("user_group_id"), obj.getString("categorychart_id"),
                                    obj.getString("unic_customer_for_mobile"), obj.getString("unic_customer"),
                                    obj.getString("is_active"), obj.getString("name"), obj.getString("father_name"),
                                    obj.getString("phone_number"), obj.getString("adhar"), obj.getString("village"),
                                    obj.getString("address"), obj.getString("morning_milk"), obj.getString("evening_milk"),
                                    obj.getString("price_per_ltr"), obj.getString("entry_type"), obj.getString("entry_price"),
                                    obj.getString("acno"), obj.getString("ifsc_code"), obj.getString("bank_name"),
                                    obj.getString("firebase_tocan"));
                        }
                        Constant.BuyerFirstTime = "Loaded";

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        caller.addRequestBody(body);
        caller.execute(getBuyerMilkListAPI);


    }

    private void showPopup(View v, int pos, boolean is_active) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, v);
        popup.getMenu().add(MENU_CHAT, MENU_CHAT_ITEM, 0, mContext.getString(R.string.Chat));
        popup.getMenu().add(MENU_EDIT_MILK, MENU_EDIT_MILK_ITEM, 1, mContext.getString(R.string.Edit_Milk));
        popup.getMenu().add(MENU_EDIT_PLAN, MENU_MILK_PLAN_ITEM, 2, mContext.getString(R.string.Milk) + " " + mContext.getString(R.string.Plan));

        popup.getMenu().add(MENU_EDIT_PROFILE, MENU_EDIT_PROFILE_ITEM, 3, mContext.getString(R.string.Edit_Profile));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 4, mContext.getString(R.string.Delete));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_CHAT_ITEM:
                        intent = new Intent(mContext, ChatActivity.class);
                        intent.putExtra("FRIEND_NAME", mList.get(pos).name);
                        // intent.putExtra("IMAGE_PATH", morningList.get(pos).profile_image);
                        intent.putExtra("unic_customer_for_mobile", mList.get(pos).unic_customer_for_mobile);
                        intent.putExtra("FRIEND_id", mList.get(pos).customer_id);
                        intent.putExtra("firebase_tocan", mList.get(pos).firebase_tocan);
                        intent.putExtra("FRIEND_mob", mList.get(pos).phone_number);
                        mContext.startActivity(intent);
                        break;

                    case MENU_EDIT_MILK_ITEM:
                        editMilk(pos, is_active);
                        break;
                    case MENU_MILK_PLAN_ITEM:
                        Fragment fragment = new fragment_BuyerMilkPlan();
                        bundle = new Bundle();

                        bundle.putString("CustomerID", mList.get(pos).customer_id);
                        //  intent.putExtra("unic_customer", mList.get(getAdapterPosition()).unic_customer);
                        bundle.putString("Name", mList.get(pos).name);
                        bundle.putString("FatherName", mList.get(pos).father_name);
                        bundle.putString("Address", mList.get(pos).address);
                        bundle.putString("Aadhar", mList.get(pos).adhar);
                        bundle.putString("Mobile", mList.get(pos).phone_number);
                        bundle.putString("unic_customer", mList.get(pos).unic_customer);
                        bundle.putString("rate", mList.get(pos).price_per_ltr);

                        bundle.putString("user_group_id", mList.get(pos).user_group_id);

                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
                        break;

                    case MENU_EDIT_PROFILE_ITEM:
                        fragment = new AddCustomerFragment();
                        bundle = new Bundle();
                        bundle.putString("from", "UserListAdapter");
                        bundle.putString("CustomerID", mList.get(pos).customer_id);
                        bundle.putString("unic_customer", mList.get(pos).unic_customer);
                        bundle.putString("user_group_id", mList.get(pos).user_group_id);
                        bundle.putString("category_chart_id", mList.get(pos).categorychart_id);
                        bundle.putString("Name", mList.get(pos).name);
                        bundle.putString("FatherName", mList.get(pos).father_name);
                        bundle.putString("Mobile", mList.get(pos).phone_number);
                        bundle.putString("Village", mList.get(pos).village);
                        bundle.putString("Address", mList.get(pos).address);
                        bundle.putString("Aadhar", mList.get(pos).adhar);
                        bundle.putString("entry_type", mList.get(pos).entry_type);
                        bundle.putString("rate", mList.get(pos).entry_price);
                        bundle.putString("accno", mList.get(pos).accountNo);
                        bundle.putString("ifsc_code", mList.get(pos).iFSC_Code);
                        bundle.putString("bank_name", mList.get(pos).bankName);
                        fragment.setArguments(bundle);
                        goNextFragmentWithBackStack(mContext, fragment);
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
                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                            @Override
                            public void handleResponse(String response) {
                                Log.d("response>>>>", response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String staus = jsonObject.getString("status");
                                    if (staus.equals("success")) {
                                        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
                                        databaseHandler.deleteBuyerCustomer(mList.get(pos).customer_id);
                                        mList.remove(pos);
                                        showToast(mContext, mContext.getString(R.string.Customer_Deleted_Successfully));
                                        notifyDataSetChanged();
                                    } else {
                                        showToast(mContext, mContext.getString(R.string.Customer_Deleting_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder()
                                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                                .addEncoded("id", mList.get(pos).customer_id)
                                .build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.deleteCustomerAPI);
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

    private void editMilk(int pos, boolean is_active) {
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_update_buyer_entry);
        dialog.setTitle(mContext.getString(R.string.Update_Entry));


        final TextInputLayout layoutMilkRate = dialog.findViewById(R.id.layoutMilkRate);
        View priceView = dialog.findViewById(R.id.priceView);
        final EditText edtMorningMilk = dialog.findViewById(R.id.ediMorningMilk);
        final EditText edtEveningMilk = dialog.findViewById(R.id.ediEveningMilk);
        final EditText edtMilkRate = dialog.findViewById(R.id.edtMilkRate);
        if (!mList.get(pos).morning_milk.equals("null")) {
            edtMorningMilk.setText(mList.get(pos).morning_milk);
        }
        if (!mList.get(pos).evening_milk.equals("null")) {
            edtEveningMilk.setText(mList.get(pos).evening_milk);
        }
        if (!mList.get(pos).price_per_ltr.equals("null")) {
            edtMilkRate.setText(mList.get(pos).price_per_ltr);
        }
        layoutMilkRate.setVisibility(View.VISIBLE);
        priceView.setVisibility(View.VISIBLE);
        Button btnNext = dialog.findViewById(R.id.btnNext);
        dialog.show();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                    @Override
                    public void handleResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status").equals("success")) {
                                dialog.dismiss();
                                mList.get(pos).morning_milk = edtMorningMilk.getText().toString();
                                mList.get(pos).evening_milk = edtEveningMilk.getText().toString();
                                mList.get(pos).price_per_ltr = edtMilkRate.getText().toString();
                                databaseHandler.updateBuyerMilkData(edtMorningMilk.getText().toString(), edtEveningMilk.getText().toString(), edtMilkRate.getText().toString(), mList.get(pos).customer_id);
                                UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Success));
                            } else {
                                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                String isActive = "2";
                if (is_active == true) {
                    isActive = "1";
                }

                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                        .addEncoded("customer_id", mList.get(pos).customer_id)
                        .addEncoded("morning_milk", edtMorningMilk.getText().toString())
                        .addEncoded("evening_milk", edtEveningMilk.getText().toString())
                        .addEncoded("price_per_ltr", edtMilkRate.getText().toString())
                        .addEncoded("is_active", isActive)
                        .build();
                serviceCaller.addRequestBody(body);

                serviceCaller.execute(Constant.updateBuyerMilkEntryAPI);
            }
        });
    }


    public int getItemCount() {
        return mList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtCustomerName, tvId, tvS_no, tvStatus, txtMobileNo;
        public CheckBox chkEvening, chkStatus;

        ImageView imgMoreDetail;

        public MyViewHolder(View view) {
            super(view);

            txtCustomerName = view.findViewById(R.id.txtCustomerName);
            tvId = view.findViewById(R.id.tvId);
            txtMobileNo = view.findViewById(R.id.txtMobileNo);
            tvS_no = view.findViewById(R.id.tvS_no);
            tvStatus = view.findViewById(R.id.tvStatus);
            chkStatus = view.findViewById(R.id.chkStatus);
            chkEvening = view.findViewById(R.id.chkEvening);
            imgMoreDetail = view.findViewById(R.id.imgMoreDetail);

            view.setOnClickListener(this);
            txtCustomerName.setOnClickListener(this);
            txtMobileNo.setOnClickListener(this);
            tvId.setOnClickListener(this);
            txtMobileNo.setOnClickListener(this);

            imgMoreDetail.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            pos = getLayoutPosition();
            switch (view.getId()) {

                case R.id.txtCustomerName:
                case R.id.txtMobileNo:
                case R.id.tvId:
                    bundle = new Bundle();
                    bundle.putString("unic_customer", mList.get(pos).unic_customer);
                    bundle.putString("CustomerId", mList.get(pos).customer_id);
                    bundle.putString("CustomerName", mList.get(pos).name);
                    bundle.putString("CustomerFatherName", mList.get(pos).father_name);
                    if (Constant.FromWhere2.equals("TransactionBuyer")) {
                        fragment = new TransactionOldHistoryFragment();
                        fragment.setArguments(bundle);
                        goNextFragmentReplace(mContext, fragment);
                    } else {
                        if (Constant.FromWhere.equals("ExistUser")) {
                            fragment = new SaleItemFragment();
                            fragment.setArguments(bundle);
                            goNextFragmentReplace(mContext, fragment);
                        } else {

                            fragment = new BuyerMonthlyDetailsFragment();
                            bundle.putString("userMobile", mList.get(pos).phone_number);
                            bundle.putString("milkRate", mList.get(pos).price_per_ltr);
                            bundle.putString("dairyID", sessionManager.getValueSesion(KEY_UserID));
                            bundle.putString("fromWhere", "DairyOwner");
                            fragment.setArguments(bundle);
                            goNextFragmentWithBackStack(mContext, fragment);
                        }
                    }

                    break;
                case R.id.imgMoreDetail:
                    is_active = chkStatus.isChecked();
                    showPopup(view, pos, is_active);
                    break;
            }

        }
    }
}
