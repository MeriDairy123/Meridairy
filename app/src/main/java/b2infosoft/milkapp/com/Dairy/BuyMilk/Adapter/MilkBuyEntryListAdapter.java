package b2infosoft.milkapp.com.Dairy.BuyMilk.Adapter;

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
import android.widget.ImageView;
import android.widget.TextView;

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

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.RefreshBuyMilkEntryList;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
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
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 8/5/2018.
 */

public class MilkBuyEntryListAdapter extends RecyclerView.Adapter<MilkBuyEntryListAdapter.MyViewHolder> {


    private static final int MENU_PRINT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_SMS_ITEM = Menu.FIRST + 2;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 3;
    public Context mContext;
    public ArrayList<CustomerEntryListPojo> viewEntryPojoArrayList = new ArrayList<>();
    String name = "";
    RefreshBuyMilkEntryList refreshEntryList;
    SessionManager sessionManager;
    int itemPosition = 0;
    int MENU_PRINT = 0;
    int MENU_EDIT = 1;
    int MENU_SMS = 2;
    int MENU_DELETE = 3;
    String strWeight = "", strRate = "", strTotal = "";
    int listSize = 0;
    DatabaseHandler db;

    public MilkBuyEntryListAdapter(Context mContext, ArrayList<CustomerEntryListPojo> entryList, RefreshBuyMilkEntryList refreshEntryList) {
        this.mContext = mContext;
        this.viewEntryPojoArrayList = entryList;
        this.refreshEntryList = refreshEntryList;
        sessionManager = new SessionManager(mContext);
        listSize = entryList.size();
        db = getDbHelper(mContext);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.milk_add_entry_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CustomerEntryListPojo milkEntryList = viewEntryPojoArrayList.get(position);

        int srNo = viewEntryPojoArrayList.size() - position;
        if (milkEntryList.getOnLineId() == 0 && milkEntryList.getEntryStatus() == 0) {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
        } else if (milkEntryList.getOnLineId() != 0 && milkEntryList.getEntryStatus() == 0) {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.color_PurpleLight));
        } else {
            holder.viewPayment.setBackgroundColor(mContext.getResources().getColor(R.color.color_bg));
        }
        holder.setIsRecyclable(false);
        holder.tvId.setText(" " + srNo + ".");
        holder.tvName.setText(" " + milkEntryList.unic_customer + "." + milkEntryList.name);
        holder.tvFat.setText(milkEntryList.fat + "/" + milkEntryList.snf);
        holder.tvWeight.setText(" " + milkEntryList.getTotal_milk());
        holder.tvTotal.setText(" " + milkEntryList.getTotal_price());
        holder.tvRate.setText(" " + milkEntryList.getPer_kg_price());
         holder.imgType.setImageResource( R.drawable.ic_cow);
        if (milkEntryList.getFatSnfCategory().equals("1")){
            holder.imgType.setImageResource( R.drawable.ic_buffalo);
        }

    }

    private void showPopup(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_PRINT, MENU_PRINT_ITEM, 0, mContext.getString(R.string.Print));
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 1, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_SMS, MENU_SMS_ITEM, 2, mContext.getString(R.string.Send_Message));
        popup.getMenu().add(MENU_DELETE, MENU_DELETE_ITEM, 3, mContext.getString(R.string.Delete));
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case MENU_PRINT_ITEM:
                        printReciept(position);
                        break;
                    case MENU_EDIT_ITEM:
                        refreshEntryList.onClickEditInAdapter(position, viewEntryPojoArrayList.get(position).getId(),
                                viewEntryPojoArrayList.get(position).getOnLineId(),
                                viewEntryPojoArrayList.get(position).getMilk_entry_unicId(),
                                viewEntryPojoArrayList.get(position).name, viewEntryPojoArrayList.get(position).fat,
                                strWeight, strRate, strTotal, viewEntryPojoArrayList.get(position).customer_id
                                , viewEntryPojoArrayList.get(position).unic_customer, viewEntryPojoArrayList.get(position).snf,
                                viewEntryPojoArrayList.get(position).clr, viewEntryPojoArrayList.get(position).milkRateCategory,
                                viewEntryPojoArrayList.get(position).fatSnfCategory);
                        break;
                    case MENU_SMS_ITEM:
                        sendSMS(position);
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
                        // continue with delete
                        if (isNetworkAvaliable(mContext)) {
                            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Delete) + "...", true) {
                                @Override
                                public void handleResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String staus = jsonObject.getString("status");
                                        if (staus.equalsIgnoreCase("success")) {
                                            if (sessionManager.getValueSesion(KEY_QRCode).equalsIgnoreCase("yes")) {
                                                deleteFromEntryFromDb(position);
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
                                    .addEncoded("id", "" + viewEntryPojoArrayList.get(position).getOnLineId())
                                    .build();
                            caller.addRequestBody(body);
                            caller.execute(Constant.deleteBuyMilkEntryAPI);
                        } else {
                            deleteFromEntryFromDb(position);
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

    private void deleteFromEntryFromDb(int position) {
        db.deleteBuyMilkEntryRecord("" + viewEntryPojoArrayList.get(position).getId());
        viewEntryPojoArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, viewEntryPojoArrayList.size());
        refreshEntryList.refreshList(viewEntryPojoArrayList);
        showToast(mContext, mContext.getString(R.string.Entry_Deleted_Successfully));
        notifyDataSetChanged();

    }

    private void printReciept(int position) {

        int milkType = viewEntryPojoArrayList.get(position).getFatSnfCategory();
        int CustomerID = Integer.parseInt(viewEntryPojoArrayList.get(position).getUnic_customer());
        String selectedName = viewEntryPojoArrayList.get(position).getName();
        String date = viewEntryPojoArrayList.get(position).getEntry_date();
        String shift = viewEntryPojoArrayList.get(position).getShift();
        String actualFate = viewEntryPojoArrayList.get(position).getFat();
        float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).getSnf());
        String rsPerKg = viewEntryPojoArrayList.get(position).getPer_kg_price();
        String weight = viewEntryPojoArrayList.get(position).getTotal_milk();
        String totalBonus = viewEntryPojoArrayList.get(position).getTotal_bonus();
        String totalPayment = viewEntryPojoArrayList.get(position).getTotal_price();

        if (isBluetoothHeadsetConnected()) {
            if (mDevice == null || mSocket == null || mOutputStream == null) {
                dialogBluetooth(mContext);
            } else {

                printAddMilk_SingleEntryReciept(mContext, milkType, CustomerID, selectedName, shift, actualFate,
                        snf, rsPerKg, weight, totalBonus, totalPayment,0.0f,"");
            }

        } else {
            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
            enableBluetooth();
            dialogBluetooth(mContext);

        }


    }

    private void sendSMS(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String customerId = viewEntryPojoArrayList.get(position).customer_id;
                        CustomerListPojo customerListPojo = db.getCustomerDetails(customerId);
                        String MobileNo = customerListPojo.phone_number;
                        String selectedName = viewEntryPojoArrayList.get(position).name;
                        String date = viewEntryPojoArrayList.get(position).entry_date;
                        String shift = viewEntryPojoArrayList.get(position).getShift();
                        String actualFate = viewEntryPojoArrayList.get(position).fat;
                        float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).snf);
                        String rsPerKg = viewEntryPojoArrayList.get(position).per_kg_price;
                        String weight = viewEntryPojoArrayList.get(position).total_milk;
                        String bonus = viewEntryPojoArrayList.get(position).total_milk;
                        String totalPayment = viewEntryPojoArrayList.get(position).total_price;


                        MilkSMSContent(mContext, true, "AddMilkEnty", MobileNo, selectedName, date, shift, rsPerKg, actualFate, snf, weight, bonus, totalPayment,0.0f);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })

                .show();

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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvFat, tvWeight, tvTotal, tvId, tvRate;

        View viewPayment;
        ImageView imgType,imgMoreDetail;


        public MyViewHolder(View view) {
            super(view);
            viewPayment = view.findViewById(R.id.viewPayment);
            tvName = view.findViewById(R.id.tvName);
            tvFat = view.findViewById(R.id.tvFat);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvTotal = view.findViewById(R.id.tvTotal);
            tvId = view.findViewById(R.id.tvId);
            tvRate = view.findViewById(R.id.tvRate);
            imgType = view.findViewById(R.id.imgType);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            viewPayment.setOnClickListener(this);
            tvFat.setOnClickListener(this);
            tvRate.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
            tvTotal.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            itemPosition = getLayoutPosition();
            strWeight = viewEntryPojoArrayList.get(itemPosition).getTotal_milk();
            strRate = viewEntryPojoArrayList.get(itemPosition).getPer_kg_price();
            strTotal = viewEntryPojoArrayList.get(itemPosition).getTotal_price();
            switch (v.getId()) {
                case R.id.viewPayment:
                case R.id.tvFat:
                case R.id.tvRate:
                case R.id.imgMoreDetail:
                case R.id.tvTotal:
                    showPopup(v, itemPosition);
                    break;

                default:
                    break;
            }
        }
    }
}




