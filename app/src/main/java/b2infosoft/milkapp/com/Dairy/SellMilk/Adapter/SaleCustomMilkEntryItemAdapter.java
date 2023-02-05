package b2infosoft.milkapp.com.Dairy.SellMilk.Adapter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.RefreshSaleEntryList;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printAddMilk_SingleEntryReciept;
import static b2infosoft.milkapp.com.Database.DatabaseHandler.getDbHelper;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.MilkSMSContent;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_QRCode;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyIsOnline;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 04/5/2019.
 */

public class SaleCustomMilkEntryItemAdapter extends RecyclerView.Adapter<SaleCustomMilkEntryItemAdapter.MyViewHolder> {
    private static final int MENU_PRINT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_SMS_ITEM = Menu.FIRST + 2;
    private static final int MENU_RECEIVE_ITEM = Menu.FIRST + 3;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 4;
    public Context mContext;
    public ArrayList<CustomerSaleMilkEntryList> viewEntryPojoArrayList;
    String name = "";
    RefreshSaleEntryList listner;
    SessionManager sessionManager;
    int itemPosition = 0;
    int MENU_PRINT = 0, MENU_EDIT = 1, MENU_SMS = 2, MENU_RECEIVE = 3, MENU_DELETE = 4,
            deliveryBoyid = 2;
    int isOnline = 0;
    String strWeight = "", strRate = "", strTotal = "";
    DatabaseHandler db;

    public SaleCustomMilkEntryItemAdapter(Context mContext, ArrayList<CustomerSaleMilkEntryList> entryList, RefreshSaleEntryList listner) {
        this.mContext = mContext;
        this.viewEntryPojoArrayList = entryList;
        this.listner = listner;
        sessionManager = new SessionManager(mContext);
        isOnline = sessionManager.getIntValueSesion(KeyIsOnline);
        db = getDbHelper(mContext);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.milk_entry_custom_entry_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        int srNo = viewEntryPojoArrayList.size() - position;
        CustomerSaleMilkEntryList milkEntryList = viewEntryPojoArrayList.get(position);
        holder.setIsRecyclable(false);
        holder.tvId.setText(" " + srNo + ".");
        holder.tvName.setText(" " + milkEntryList.unic_customer + "." + milkEntryList.name);
        holder.tvFat.setText(milkEntryList.fat + "/" + milkEntryList.snf);
        holder.tvWeight.setText("  " + milkEntryList.total_milk);
        holder.tvTotal.setText(" " + milkEntryList.total_price);
        holder.tvRate.setText(" " + milkEntryList.per_kg_price);

        strWeight = holder.tvWeight.getText().toString();
        strRate = holder.tvRate.getText().toString();
        strTotal = holder.tvTotal.getText().toString();
        deliveryBoyid = milkEntryList.deliveryBoyId;


        if (milkEntryList.getOnLineId() == 0 && milkEntryList.getEntryStatus() == 0) {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
        } else if (milkEntryList.getOnLineId() != 0 && milkEntryList.getEntryStatus() == 0) {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.color_PurpleLight));
        } else {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.color_bg));
        }
        /*if (deliveryBoyid == 0) {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
        } else {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.color_list_gray));
        }*/
    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_PRINT, MENU_PRINT_ITEM, 0, mContext.getString(R.string.Print));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 1, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_SMS, MENU_SMS_ITEM, 2, mContext.getString(R.string.Send_Message));
        popup.getMenu().add(MENU_RECEIVE, MENU_RECEIVE_ITEM, 3, mContext.getString(R.string.RECEIVE_CASH));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 4, mContext.getString(R.string.Delete));


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case MENU_PRINT_ITEM:
                        printReciept(position);
                        break;
                    case MENU_EDIT_ITEM:

                        listner.onClickEditInAdapter(position, viewEntryPojoArrayList.get(position).getId(), viewEntryPojoArrayList.get(position).getOnLineId(),
                                viewEntryPojoArrayList.get(position).name, viewEntryPojoArrayList.get(position).fat, strWeight, strRate,
                                strTotal, viewEntryPojoArrayList.get(position).customer_id, viewEntryPojoArrayList.get(position).unic_customer, viewEntryPojoArrayList.get(position).snf, viewEntryPojoArrayList.get(position).clr,
                                viewEntryPojoArrayList.get(position).milkRateCategory, viewEntryPojoArrayList.get(position).fatSnfCategory);
                        break;
                    case MENU_SMS_ITEM:
                        sendSMS(position);
                        break;
                    case MENU_RECEIVE_ITEM:
                        listner.receiveAmount(position);
                        break;
                    case MENU_DELETE_ITEM:
                        if (viewEntryPojoArrayList.get(position).getOnLineId() > 0) {
                            if (isNetworkAvaliable(mContext)) {
                                deleteItem(position);
                            } else {
                                showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                            }
                        } else {
                            deleteItem(position);
                        }
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
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (isNetworkAvaliable(mContext)) {
                            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                                @Override
                                public void handleResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String staus = jsonObject.getString("status");
                                        if (staus.equalsIgnoreCase("success")) {
                                            if (sessionManager.getValueSesion(KEY_QRCode).equalsIgnoreCase("yes")) {
                                                deleteEntryFromDB(position);
                                            }
                                        } else {
                                            showToast(mContext, mContext.getString(R.string.Entry_Deleting_Failed));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            RequestBody body = new FormEncodingBuilder()
                                    .addEncoded("id", "" + viewEntryPojoArrayList.get(position).getOnLineId()).build();
                            caller.addRequestBody(body);
                            caller.execute(Constant.deleteSaleMilkEntryAPI);
                        } else {
                            deleteEntryFromDB(position);
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

    private void deleteEntryFromDB(int position) {
        db.deleteSaleMilkRecord("" + viewEntryPojoArrayList.get(position).getId());
        viewEntryPojoArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, viewEntryPojoArrayList.size());
        listner.refreshSaleEntryList(viewEntryPojoArrayList);
        showToast(mContext, mContext.getString(R.string.Entry_Deleted_Successfully));
        notifyDataSetChanged();
    }

    private void printReciept(int position) {

        int milkType = viewEntryPojoArrayList.get(position).fatSnfCategory;
        int CustomerID = Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer);
        String selectedName = viewEntryPojoArrayList.get(position).name;
        String shift = viewEntryPojoArrayList.get(position).shift;
        String actualFate = viewEntryPojoArrayList.get(position).fat;
        float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).snf);
        String rsPerKg = viewEntryPojoArrayList.get(position).per_kg_price;
        String weight = viewEntryPojoArrayList.get(position).total_milk;
        String strTotalBonus = viewEntryPojoArrayList.get(position).total_bonus;
        String totalPayment = viewEntryPojoArrayList.get(position).total_price;


        if (isBluetoothHeadsetConnected()) {
            if (mDevice == null || mSocket == null || mOutputStream == null) {
                dialogBluetooth(mContext);
            } else {
                printAddMilk_SingleEntryReciept(mContext, milkType, CustomerID, selectedName, shift, actualFate,
                        snf, rsPerKg, weight, strTotalBonus, totalPayment,0.0f,"");
            }

        } else {
            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
            enableBluetooth();
            dialogBluetooth(mContext);
        }
    }


    public int getItemCount() {
        return viewEntryPojoArrayList.size();
    }


    public boolean enableBluetooth() {
        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    ((Activity) mContext).startActivityForResult(enableBluetooth, 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void AlertPrintReciept() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);

        builder.setMessage(mContext.getString(R.string.are_you_sure_wantot_print_reciept))
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        printReciept(itemPosition);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })

                .show();
    }

    private void sendSMS(int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String customerId = viewEntryPojoArrayList.get(position).getCustomer_id();

                        CustomerListPojo customerListPojo = db.getCustomerDetails(customerId);
                        String MobileNo = customerListPojo.phone_number;
                        String selectedName = viewEntryPojoArrayList.get(position).name;

                        String date = viewEntryPojoArrayList.get(position).entry_date;
                        String shift = viewEntryPojoArrayList.get(position).shift;
                        String actualFate = viewEntryPojoArrayList.get(position).fat;
                        float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).snf);
                        String rsPerKg = viewEntryPojoArrayList.get(position).per_kg_price;
                        String weight = viewEntryPojoArrayList.get(position).total_milk;
                        String bonus = viewEntryPojoArrayList.get(position).total_bonus;
                        String totalPayment = viewEntryPojoArrayList.get(position).total_price;


                        MilkSMSContent(mContext, true, "SaleMilkEnty", MobileNo, selectedName, date, shift, rsPerKg, actualFate, snf, weight, bonus, totalPayment,0.0f);
                        dialog.dismiss();
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvId, tvName, tvWeight, tvFat, tvRate, tvTotal;

        View viewPayment;


        public MyViewHolder(View view) {
            super(view);
            viewPayment = view.findViewById(R.id.viewPayment);
            tvId = view.findViewById(R.id.tvId);
            tvName = view.findViewById(R.id.tvName);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvFat = view.findViewById(R.id.tvFat);
            tvRate = view.findViewById(R.id.tvRate);
            tvTotal = view.findViewById(R.id.tvTotal);
            viewPayment.setOnClickListener(this);
            tvTotal.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            itemPosition = getAdapterPosition();
            deliveryBoyid = viewEntryPojoArrayList.get(itemPosition).deliveryBoyId;
            strWeight = viewEntryPojoArrayList.get(itemPosition).total_milk;
            strRate = viewEntryPojoArrayList.get(itemPosition).per_kg_price;
            strTotal = viewEntryPojoArrayList.get(itemPosition).total_price;

            switch (v.getId()) {

                case R.id.tvTotal:
                case R.id.viewPayment:
                    showPopup(v, getAdapterPosition());
                    break;
                default:
                    break;
            }
        }

    }

}




