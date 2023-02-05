package b2infosoft.milkapp.com.Dairy.SellMilk.Adapter;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printAddMilk_SingleEntryReciept;
import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFragment.isPackageInstalled;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.MilkSMSContent;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_QRCode;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyIsOnline;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerMilkWeekStatus;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Dairy.SellMilk.SaleMilkEntryFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.RefreshSaleEntryList;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by B2infosoft on 04/5/2019.
 */

public class SaleMilkEntryItemAdapter extends RecyclerView.Adapter<SaleMilkEntryItemAdapter.MyViewHolder> {
    private static final int MENU_PRINT_ITEM = Menu.FIRST;
    private static final int MENU_EDIT_ITEM = Menu.FIRST + 1;
    private static final int MENU_SMS_ITEM = Menu.FIRST + 2;
    private static final int MENU_DELETE_ITEM = Menu.FIRST + 3;
    public Context mContext;
    public ArrayList<CustomerSaleMilkEntryList> viewEntryPojoArrayList;

    RefreshSaleEntryList listner;
    SessionManager sessionManager;
    int itemPosition = 0;
    int MENU_PRINT = 0;
    int MENU_EDIT = 1;
    int MENU_SMS = 2;
    int MENU_DELETE = 3;
    int deliveryBoyid = 2;
    int isOnline = 0;
    String strWeight = "", strRate = "", strTotal = "";
    DatabaseHandler db;
    DatabaseHandler databaseHandler;

    public SaleMilkEntryItemAdapter(Context mContext, ArrayList<CustomerSaleMilkEntryList> entryList, RefreshSaleEntryList listner) {
        this.mContext = mContext;
        this.viewEntryPojoArrayList = entryList;
        this.listner = listner;
        sessionManager = new SessionManager(mContext);
        isOnline = sessionManager.getIntValueSesion(KeyIsOnline);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.milk_add_entry_row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        CustomerSaleMilkEntryList milkEntryList = viewEntryPojoArrayList.get(position);
        int srNo = viewEntryPojoArrayList.size() - position;
        deliveryBoyid = milkEntryList.deliveryBoyId;

        holder.imgMoreDetail.setEnabled(true);
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
        holder.tvWeight.setText("  " + milkEntryList.total_milk);
        holder.tvTotal.setText(" " + milkEntryList.total_price);
        holder.tvRate.setText(" " + milkEntryList.per_kg_price);
        strWeight = holder.tvWeight.getText().toString();
        strRate = holder.tvRate.getText().toString();
        strTotal = holder.tvTotal.getText().toString();


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

                        listner.onClickEditInAdapter(position, viewEntryPojoArrayList.get(position).id, viewEntryPojoArrayList.get(position).onLineId, viewEntryPojoArrayList.get(position).name, viewEntryPojoArrayList.get(position).fat, strWeight, strRate, strTotal, viewEntryPojoArrayList.get(position).customer_id, viewEntryPojoArrayList.get(position).unic_customer, viewEntryPojoArrayList.get(position).snf, viewEntryPojoArrayList.get(position).clr, viewEntryPojoArrayList.get(position).milkRateCategory, viewEntryPojoArrayList.get(position).fatSnfCategory);
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


    @SuppressLint("RestrictedApi")
    private void popUpWithIcon(View view, int position) {
        ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);
        @SuppressLint("RestrictedApi") MenuBuilder menuBuilder = new MenuBuilder(mContext);
        MenuInflater inflater = new MenuInflater(mContext);
        inflater.inflate(R.menu.entry_menu, menuBuilder);
        @SuppressLint("RestrictedApi") MenuPopupHelper optionsMenu = new MenuPopupHelper(mContext, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);

        // Set Item Click Listener
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.opt1:
                        printReciept(position);
                        return true;
                    case R.id.opt2:
                        listner.onClickEditInAdapter(position, viewEntryPojoArrayList.get(position).id, viewEntryPojoArrayList.get(position).onLineId, viewEntryPojoArrayList.get(position).name, viewEntryPojoArrayList.get(position).fat, strWeight, strRate, strTotal, viewEntryPojoArrayList.get(position).customer_id, viewEntryPojoArrayList.get(position).unic_customer, viewEntryPojoArrayList.get(position).snf, viewEntryPojoArrayList.get(position).clr, viewEntryPojoArrayList.get(position).milkRateCategory, viewEntryPojoArrayList.get(position).fatSnfCategory);
                        return true;
                    case R.id.opt3:
                        sendSMS(position);
                        return true;
                    case R.id.opt4:
                        if (viewEntryPojoArrayList.get(position).getOnLineId() > 0) {
                            if (isNetworkAvaliable(mContext)) {
                                deleteItem(position);
                            } else {
                                showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                            }
                        } else {
                            deleteItem(position);
                        }
                        return true;
                    case R.id.opt5:
                        String lableSmsName = mContext.getString(R.string.Name);
                        String lableSmsdate = mContext.getString(R.string.Date);
                        String lableSmsshift = mContext.getString(R.string.Shift);
                        String lableSmsRs = mContext.getString(R.string.Rs);
                        String lableSmsRate = mContext.getString(R.string.Rate);
                        String lableSmsLtr = mContext.getString(R.string.Ltr);
                        String lableSmsFat = mContext.getString(R.string.Fat);
                        String lableSmsSNF = mContext.getString(R.string.SNF);
                        String lableSmsClr = mContext.getString(R.string.CLR);
                        String lableSmsBonus = mContext.getString(R.string.Bonus);
                        String lableSmsTotalPrice = mContext.getString(R.string.Week_Total_Rs);
                        String lableSmsWeight = mContext.getString(R.string.Weight)+"/"+ mContext.getString(R.string.Quantity);
                        String lablemessage = mContext.getString(R.string.message);
                        SaleMilkEntryFragment saleMilkEntryFragment = new SaleMilkEntryFragment();
                        SQLiteDatabase dbb = databaseHandler.getReadableDatabase();
                        Log.d("TAG", "cxCheck2: " + Integer.parseInt(viewEntryPojoArrayList.get(position).customer_id));
                        String selectedDate = SelectedDate.substring(0, 2);
                        String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
                        String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());
                        Log.d("TAG", "onMenuItemSelected26: " + SelectedDate);

                        float f = 0;
                        if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
                            f = saleMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {
                            f = saleMilkEntryFragment.fetchDataFromLocalFifteen(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {
                            f = saleMilkEntryFragment.fetchDataFromLocalMonth(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                        } else {
                            f = saleMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                        }


                        String smsContent = lableSmsName + " : " + viewEntryPojoArrayList.get(position).name + "\n" + lableSmsdate + " : " + viewEntryPojoArrayList.get(position).entry_date + "\n" + lableSmsshift + " : " + viewEntryPojoArrayList.get(position).shift + "\n" + lableSmsWeight + " : " + viewEntryPojoArrayList.get(position).total_milk + "\n" + lableSmsFat + " : " + viewEntryPojoArrayList.get(position).fat + "\n" + lableSmsSNF + "/" + lableSmsClr + " : " + viewEntryPojoArrayList.get(position).snf + "\n" + lableSmsRate + " " + lableSmsRs + " : " + viewEntryPojoArrayList.get(position).per_kg_price + " / " + lableSmsLtr + "\n" + lableSmsBonus + " : " + viewEntryPojoArrayList.get(position).total_bonus + "\n" + lableSmsRs + " : " + viewEntryPojoArrayList.get(position).total_price + "\n" + lableSmsTotalPrice + " : " + f + "\n" + lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(mContext, "saveGreatingSms");


                        PackageManager pm = ctw.getPackageManager();
                        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(ctw);
                        ArrayList<CustomerListPojo> mList = databaseHandler.getCustomerList();
                        Log.d("TAG", "onMenuItemSelected25: " + smsContent);

                        if (SharedPrefData.retriveDataFromPrefrence(mContext, "WhatsappBusinnesORWhatsapp").equals("on")) {
                            if (isPackageInstalled(mContext, "com.whatsapp.w4b")) {
                                //    sendDataToWhatsappp("00", "com.whatsapp.w4b", smsContent);
                                sendDataToWhatsappWeb("00", "com.whatsapp.w4b", smsContent);
                            } else {
                                Toast.makeText(mContext, "Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            if (isPackageInstalled(mContext, "com.whatsapp")) {
                                sendDataToWhatsappp("00", "com.whatsapp", smsContent);
                            } else {
                                Toast.makeText(mContext, "Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
                            }
                        }


//                        try {
//                            Intent waIntent = new Intent(Intent.ACTION_SEND);
//                            waIntent.setType("text/plain");
//                            //PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//                            // waIntent.setPackage("com.whatsapp");
//                            waIntent.putExtra(Intent.EXTRA_TEXT, smsContent);
//                            ctw.startActivity(Intent.createChooser(waIntent, "Share with"));
//
//                        } catch (Exception e) {
//                            Toast.makeText(ctw, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
//                        }
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onMenuModeChange(MenuBuilder menu) {

            }

            private void sendDataToWhatsappp(String cxNumber, String packageName, String message) {
                String toNumber = "91" + cxNumber; // contains spaces.
                toNumber = toNumber.replace("+", "").replace(" ", "");
                Intent sendIntent = new Intent("android.intent.action.MAIN");
                sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setPackage(packageName);
                sendIntent.setType("text/plain");
                mContext.startActivity(sendIntent);
            }
        });


        // Display the menu
        optionsMenu.show();
    }

    private void sendDataToWhatsappWeb(String cxNumber, String packageName, String message) {
        String toNumber = "91" + cxNumber; // contains spaces.
        Log.d("TAG", "sendDataToWhatsapp25: " + cxNumber);
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(packageName);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

    private void deleteItem(int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Delete_Entry)).setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                if (isNetworkAvaliable(mContext) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {
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
                    RequestBody body = new FormEncodingBuilder().addEncoded("id", "" + viewEntryPojoArrayList.get(position).getOnLineId()).build();
                    caller.addRequestBody(body);
                    caller.execute(Constant.deleteSaleMilkEntryAPI);
                } else {

                    deleteEntryFromDB(position);


                }
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void deleteEntryFromDB(int position) {
        databaseHandler.deleteSaleMilkRecord("" + viewEntryPojoArrayList.get(position).getId());
        viewEntryPojoArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, viewEntryPojoArrayList.size());
        listner.refreshSaleEntryList(viewEntryPojoArrayList);
        showToast(mContext, mContext.getString(R.string.Entry_Deleted_Successfully));
        notifyDataSetChanged();
    }

    private void printReciept(int position) {

        int milkType = viewEntryPojoArrayList.get(position).getFatSnfCategory();
        int CustomerID = Integer.parseInt(viewEntryPojoArrayList.get(position).getUnic_customer());
        String selectedName = viewEntryPojoArrayList.get(position).getName();
        String shift = viewEntryPojoArrayList.get(position).getShift();
        String actualFate = viewEntryPojoArrayList.get(position).getFat();
        float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).getSnf());
        String rsPerKg = viewEntryPojoArrayList.get(position).getPer_kg_price();
        String weight = viewEntryPojoArrayList.get(position).getTotal_milk();
        String strTotalBonus = viewEntryPojoArrayList.get(position).getTotal_bonus();
        String totalPayment = viewEntryPojoArrayList.get(position).getTotal_price();

        if (isBluetoothHeadsetConnected()) {
            if (mDevice == null || mSocket == null || mOutputStream == null) {
                dialogBluetooth(mContext);
            } else {


                SaleMilkEntryFragment purchaseMilkEntryFragment = new SaleMilkEntryFragment();
                SQLiteDatabase dbb = databaseHandler.getReadableDatabase();
                Log.d("TAG", "cxCheck2: " + Integer.parseInt(viewEntryPojoArrayList.get(position).customer_id));
                String selectedDate = SelectedDate.substring(0, 2);
                String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
                String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());
                Log.d("TAG", "onMenuItemSelected26: " + SelectedDate);


                float f = 0;
                if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocalFifteen(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocalMonth(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else {
                    f = purchaseMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                }
                String lablemessage = mContext.getString(R.string.message);


                printAddMilk_SingleEntryReciept(mContext, milkType, CustomerID, selectedName, shift, actualFate, snf, rsPerKg, weight, strTotalBonus, totalPayment, f, lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(mContext, "saveGreatingSms"));
            }

        } else {
            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
            enableBluetooth(mContext);
            dialogBluetooth(mContext);
        }
    }


    public int getItemCount() {
        return viewEntryPojoArrayList.size();
    }

    private void sendSMS(int position) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS)).setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String customerId = viewEntryPojoArrayList.get(position).getCustomer_id();
                DatabaseHandler db = DatabaseHandler.getDbHelper(mContext);
                CustomerListPojo customerListPojo = db.getCustomerDetails(customerId);

                String MobileNo = customerListPojo.phone_number;
                String selectedName = viewEntryPojoArrayList.get(position).getName();
                String date = viewEntryPojoArrayList.get(position).getEntry_date();
                String shift = viewEntryPojoArrayList.get(position).getShift();
                String actualFate = viewEntryPojoArrayList.get(position).getFat();
                float snf = Float.parseFloat(viewEntryPojoArrayList.get(position).getSnf());
                String rsPerKg = viewEntryPojoArrayList.get(position).getPer_kg_price();
                String weight = viewEntryPojoArrayList.get(position).getTotal_milk();
                String bonus = viewEntryPojoArrayList.get(position).getTotal_bonus();
                String totalPayment = viewEntryPojoArrayList.get(position).total_price;



                SaleMilkEntryFragment purchaseMilkEntryFragment = new SaleMilkEntryFragment();
                SQLiteDatabase dbb = databaseHandler.getReadableDatabase();
                Log.d("TAG", "cxCheck2: " + Integer.parseInt(viewEntryPojoArrayList.get(position).customer_id));
                String selectedDate = SelectedDate.substring(0, 2);
                String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
                String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());
                Log.d("TAG", "onMenuItemSelected26: " + SelectedDate);


                float f = 0;
                if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocalFifteen(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {
                    f = purchaseMilkEntryFragment.fetchDataFromLocalMonth(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                } else {
                    f = purchaseMilkEntryFragment.fetchDataFromLocal(dbb, Integer.parseInt(viewEntryPojoArrayList.get(position).unic_customer), "0.0f", selectedDate, selectedMonth, selectedYear);
                }
                String lablemessage = mContext.getString(R.string.message);


                MilkSMSContent(mContext, true, "SaleMilkEnty", MobileNo, selectedName, date, shift, rsPerKg, actualFate, snf, weight, bonus, totalPayment, f);
                dialog.dismiss();
            }
        }).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();

    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvFat, tvWeight, tvTotal, tvId, tvRate;

        View viewPayment;
        ImageView imgMoreDetail;


        public MyViewHolder(View view) {
            super(view);
            viewPayment = view.findViewById(R.id.viewPayment);
            tvName = view.findViewById(R.id.tvName);
            tvFat = view.findViewById(R.id.tvFat);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvTotal = view.findViewById(R.id.tvTotal);
            tvId = view.findViewById(R.id.tvId);
            tvRate = view.findViewById(R.id.tvRate);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            viewPayment.setOnClickListener(this);
            tvTotal.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            itemPosition = getLayoutPosition();
            deliveryBoyid = viewEntryPojoArrayList.get(itemPosition).deliveryBoyId;
            strWeight = viewEntryPojoArrayList.get(itemPosition).total_milk;
            strRate = viewEntryPojoArrayList.get(itemPosition).per_kg_price;
            strTotal = viewEntryPojoArrayList.get(itemPosition).total_price;

            switch (v.getId()) {
                case R.id.imgMoreDetail:
                case R.id.tvTotal:
                case R.id.viewPayment:
                    //  showPopup(v, itemPosition);
                    popUpWithIcon(v, itemPosition);
                    break;
                default:
                    break;
            }
        }

    }

}




