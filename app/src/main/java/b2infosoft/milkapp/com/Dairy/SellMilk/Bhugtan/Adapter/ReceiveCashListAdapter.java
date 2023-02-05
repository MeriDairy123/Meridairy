package b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.Bhugtan.SignatureFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment;
import b2infosoft.milkapp.com.Model.ReceiveCashPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;


/**
 * Created by u on 12-Jan-19.
 */

public class ReceiveCashListAdapter extends RecyclerView.Adapter<ReceiveCashListAdapter.MyViewHolder> implements View.OnClickListener {
    private static final int MENU_PAY_ITEM = Menu.FIRST;
    public Context mContext;
    ArrayList<ReceiveCashPojo> mList;
    SessionManager sessionManager;
    Bundle bundle;
    Fragment fragment;
    int MENU_PAY = 0, positionItem = 0;

    public ReceiveCashListAdapter(Context mContext, ArrayList<ReceiveCashPojo> mList) {
        this.mContext = mContext;
        this.mList = mList;
        sessionManager = new SessionManager(mContext);
    }

    @Override
    public ReceiveCashListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_receive_cash_row, parent, false);
        return new ReceiveCashListAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReceiveCashListAdapter.MyViewHolder holder, final int position) {

        holder.tvID.setText(mList.get(position).unic_customer);
        holder.tvName.setText(mList.get(position).name);
        ArrayList<ReceiveCashPojo> transactionList = mList.get(position).transactionList;
        double creditPrice = 0.0;
        double totalAmt = Double.parseDouble(mList.get(position).total_morning_price) + Double.parseDouble(mList.get(position).total_evening_price);
        //double debitPrice =mList.get(position).name;
        for (int i = 0; i < transactionList.size(); i++) {
            if (transactionList.get(i).type.equals("credit")) {
                creditPrice = creditPrice + Double.parseDouble(transactionList.get(i).total_price);
            }
            if (transactionList.get(i).type.equals("receive")) {
                creditPrice = creditPrice + Double.parseDouble(transactionList.get(i).total_price);
            }
        }
        double totalPrice = totalAmt - creditPrice;
        double totalMilk = Double.parseDouble(mList.get(position).total_morning_milk) + Double.parseDouble(mList.get(position).total_evening_milk);
        holder.tvTotalWeight.setText("" + totalMilk);
        holder.tvTotalPrice.setText("" + String.format("%.2f", totalPrice));
        holder.itemView.setOnClickListener(this);

    }

    public int getItemCount() {
        return mList.size();
    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_PAY, MENU_PAY_ITEM, 0, mContext.getString(R.string.RECEIVE_CASH));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_PAY_ITEM:
                        bundle = new Bundle();
                        fragment = new PayOrReceiveAmountFragment();
                        bundle.putString("FromWhere", "PaymentRegister");
                        bundle.putString("CustomerId", mList.get(position).id);
                        bundle.putString("CustomerName", mList.get(position).name);
                        bundle.putString("CustomerFatherName", mList.get(position).father_name);
                        bundle.putString("unic_customer", mList.get(position).unic_customer);
                        bundle.putString("user_group_id", mList.get(position).user_group_id);
                        bundle.putString("type", "add");
                        bundle.putString("title", mContext.getString(R.string.recieve));
                        bundle.putString("url", Constant.addRecieptVoucherAPI);
                        break;

                    default:
                        break;

                }
                return false;

            }
        });

        popup.show();
    }

    private void recieveCash(int position) {
        FromWhere = "ReceiveCash";
        System.out.println("FromWhere====>>>" + FromWhere);

        Dialog openDialog = new Dialog(mContext);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.dialog_paynow);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnPayAmt;
        EditText edtAmtType, edtAmount;
        edtAmtType = openDialog.findViewById(R.id.edtAmtType);
        edtAmount = openDialog.findViewById(R.id.edtAmount);
        btnPayAmt = openDialog.findViewById(R.id.btnPayAmt);

        btnPayAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("patAmt====>>>" + "payAmt");
                if (!edtAmount.getText().toString().equals("") && !edtAmtType.getText().toString().equals("")) {
                    NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                        @Override
                        public void handleResponse(String response) {

                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getString("status").equals("success")) {

                                    openDialog.dismiss();
                                    fragment = new SignatureFragment();
                                    bundle = new Bundle();
                                    bundle.putString("CustomerId", mList.get(position).id);
                                    bundle.putString("CustomerFatherName", mList.get(position).father_name);
                                    bundle.putString("CustomerName", mList.get(position).name);
                                    bundle.putString("transactionID", mainObject.getString("id"));
                                    bundle.putString("type", "receive");
                                    bundle.putString("unic_customer", mList.get(position).unic_customer);
                                    bundle.putString("fromwhere", "ReceiveCash");
                                    fragment.setArguments(bundle);
                                    goNextFragmentReplace(mContext, fragment);
                                } else {
                                    UtilityMethod.showAlertWithButton(mContext, "Receiving Cash  Fail");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RequestBody body = new FormEncodingBuilder()
                            .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                            .addEncoded("pay_price", edtAmount.getText().toString())
                            .addEncoded("customer_id", mList.get(position).id).build();
                    caller.addRequestBody(body);
                    caller.execute(Constant.receiveAmountAPI);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, "Please Do Amount Type And Amount to get amount");
                }
            }
        });
        openDialog.show();
    }

    @Override
    public void onClick(View v) {

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvID, tvName, tvTotalWeight, tvTotalPrice, tvPay;
        ImageView imgMoreDetail;


        public MyViewHolder(View view) {
            super(view);
            imgMoreDetail = view.findViewById(R.id.imgMoreDetail);
            tvID = view.findViewById(R.id.tvID);
            tvName = view.findViewById(R.id.tvName);
            tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
            tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
            tvPay = view.findViewById(R.id.tvPay);
            imgMoreDetail.setOnClickListener(this);
            tvTotalPrice.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            positionItem = getAdapterPosition();

            switch (view.getId()) {

                case R.id.tvTotalPrice:
                case R.id.imgMoreDetail:
                    showPopup(view, positionItem);
                    break;
                default:
                    break;
            }

        }
    }
}
