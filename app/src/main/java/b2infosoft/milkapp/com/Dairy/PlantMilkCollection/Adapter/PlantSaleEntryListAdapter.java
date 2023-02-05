package b2infosoft.milkapp.com.Dairy.PlantMilkCollection.Adapter;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.jetbrains.annotations.NotNull;
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
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

/**
 * Created by B2infosoft on 13/02/2020.
 */

public class PlantSaleEntryListAdapter extends RecyclerView.Adapter<PlantSaleEntryListAdapter.MyViewHolder> {


    private static final int MENU_PRINT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_SMS_ITEM = Menu.FIRST + 2;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 3;
    public Context mContext;
    public ArrayList<CustomerSaleMilkEntryList> viewEntryPojoArrayList = new ArrayList<>();
    String name = "";
    RefreshSaleEntryList refreshEntryList;
    SessionManager sessionManager;
    int itemPosition = 0;
    int MENU_PRINT = 0;
    int MENU_EDIT = 1;
    int MENU_SMS = 2;
    int MENU_DELETE = 3;
    String strWeight = "", strRate = "", strTotal = "";

    public PlantSaleEntryListAdapter(Context mContext, ArrayList<CustomerSaleMilkEntryList> entryList, RefreshSaleEntryList refreshEntryList) {
        this.mContext = mContext;
        this.viewEntryPojoArrayList = entryList;
        this.refreshEntryList = refreshEntryList;
        sessionManager = new SessionManager(mContext);

    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.milk_entry_custom_entry_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CustomerSaleMilkEntryList milkEntryList = viewEntryPojoArrayList.get(position);

        // int srNo = viewEntryPojoArrayList.size() - position;
        holder.setIsRecyclable(false);
        holder.tvId.setText(" " + milkEntryList.srNo + ".");
        holder.tvName.setText(" " + milkEntryList.unic_customer + ". " + milkEntryList.name);
        holder.tvFat.setText(milkEntryList.fat + "/" + milkEntryList.snf);
        holder.tvWeight.setText(" " + milkEntryList.total_milk);
        holder.tvTotal.setText(" " + milkEntryList.total_price);
        holder.tvRate.setText(" " + milkEntryList.per_kg_price);

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
                        System.out.println("fatSnfCategory===edit==" + viewEntryPojoArrayList.get(position).fatSnfCategory);
                        refreshEntryList.onClickEditInAdapter(position, viewEntryPojoArrayList.get(position).id, viewEntryPojoArrayList.get(position).getOnLineId(),
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
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (isNetworkAvaliable(mContext) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {
                            @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Delete) + "...", true) {
                                @Override
                                public void handleResponse(String response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String staus = jsonObject.getString("status");
                                        if (staus.equalsIgnoreCase("success")) {
                                            viewEntryPojoArrayList.remove(position);
                                            notifyItemRemoved(position);
                                            notifyItemRangeChanged(position, viewEntryPojoArrayList.size());
                                            refreshEntryList.refreshSaleEntryList(viewEntryPojoArrayList);
                                            Toast.makeText(mContext, mContext.getString(R.string.Entry_Deleted_Successfully), Toast.LENGTH_SHORT).show();
                                            notifyDataSetChanged();

                                        } else {
                                            Toast.makeText(mContext, mContext.getString(R.string.Entry_Deleting_Failed), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            RequestBody body = new FormEncodingBuilder()
                                    .addEncoded("id", "" + viewEntryPojoArrayList.get(position).getOnLineId())
                                    .addEncoded("plant_id", sessionManager.getValueSesion(SessionManager.KEY_PlantId))
                                    .build();
                            caller.addRequestBody(body);
                            caller.execute(Constant.deleteBuyMilkEntryAPI);
                        } else {
                            DatabaseHandler databaseHandler = getDbHelper(mContext);
                            if (databaseHandler.getPlantSaleMilkOneDayEntry(Constant.strSession, Constant.SelectedDate).size() != 0) {
                                databaseHandler.deletePlantSaleMilkRecord("" + viewEntryPojoArrayList.get(position).getId());
                                viewEntryPojoArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, viewEntryPojoArrayList.size());
                                refreshEntryList.refreshSaleEntryList(viewEntryPojoArrayList);
                                Toast.makeText(mContext, mContext.getString(R.string.Entry_Deleted_Successfully), Toast.LENGTH_SHORT).show();
                                notifyDataSetChanged();

                            }
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


    private void printReciept(int position) {

        int milkType = viewEntryPojoArrayList.get(position).fatSnfCategory;
        int CustomerID = Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer);

        String selectedName = viewEntryPojoArrayList.get(position).name;
        String date = viewEntryPojoArrayList.get(position).entry_date;
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

    private void sendSMS(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);


        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS))
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String customerId = viewEntryPojoArrayList.get(position).customer_id;
                        DatabaseHandler db = getDbHelper(mContext);
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


                        MilkSMSContent(mContext, true, "PlantSaleMilkEnty", MobileNo, selectedName, date, shift, rsPerKg, actualFate, snf, weight, bonus, totalPayment,0.0f);
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
            strWeight = viewEntryPojoArrayList.get(getAdapterPosition()).total_milk;
            strRate = viewEntryPojoArrayList.get(getAdapterPosition()).per_kg_price;
            strTotal = viewEntryPojoArrayList.get(getAdapterPosition()).total_price;
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




