package b2infosoft.milkapp.com.Dairy.SellMilk.Adapter;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_center_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerMilkWeekStatus;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFloatNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass;
import b2infosoft.milkapp.com.Dairy.SellMilk.BuyerMonthlyDetailsFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.RefreshList;
import b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web;
import b2infosoft.milkapp.com.Model.DailySaleMilkCustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by u on 06-Dec-17.
 */

public class DailySaleMilkAdapter extends RecyclerView.Adapter<DailySaleMilkAdapter.MyViewHolder> {
    private static final int MENU_EDIT_ITEM = Menu.FIRST;
    private static final int MENU_CANCEL_ITEM = Menu.FIRST + 1;
    private static final int MENU_PRINT_ITEM = Menu.FIRST + 2;
    public Context mContext;
    public ArrayList<DailySaleMilkCustomerListPojo> mList = new ArrayList<>();
    String name = "";
    RefreshList refreshList;
    SessionManager sessionManager;
    String session = "", date = "";
    Fragment fragment;
    String fetchStatus;
    String selectQuery;
    BluetoothAdapter badapter;
    Bundle bundle;
    int MENU_EDIT = 0;
    int MENU_CANCEL = 1;
    boolean is_active = false;
    MyViewHolder holderPosition;
    DatabaseHandler databaseHandler;
    Dialog dialog;
    float weight = 0;
    String milk_wt = "0", morning_milk = "0", evening_milk = "0";
    double totalPrice = 0;
    float weightMorng = 0, weightEvng = 0;
    int positionGlobal;

    public DailySaleMilkAdapter(Context mContext, ArrayList<DailySaleMilkCustomerListPojo> entryList, String session, String date, RefreshList listener) {
        this.mContext = mContext;
        this.mList = entryList;
        sessionManager = new SessionManager(mContext);
        this.session = session;
        this.date = date;
        refreshList = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_daily_buyer_milk_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holderPosition = holder;
        int srNo = position + 1;
        weightMorng = 0;
        weightEvng = 0;
        positionGlobal = position;
        holder.tvS_no.setText("" + srNo + ".");
        holder.tvId.setText(mList.get(position).unic_customer);
        holder.txtCustomerName.setText(mList.get(position).name);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);

        if (!mList.get(position).sale_status.equals("0")) {
            holder.chkStatus.setChecked(true);
            holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_list_gray));
        } else {
            holder.chkStatus.setChecked(false);
        }
        if (session.equals("evening")) {
            weightEvng = nullCheckFloatNumber(mList.get(position).evening_milk);
            if (weightEvng <= 0) {
                holder.chkStatus.setEnabled(false);
                holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
            } else {
                if (holder.chkStatus.isChecked()) {
                    holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_list_gray));

                } else {
                    holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));

                }
            }
            holder.tvWeight.setText("\t\t\t" + weightEvng);
        } else {
            weightMorng = nullCheckFloatNumber(mList.get(position).morning_milk);
            if (weightMorng <= 0) {
                holder.chkStatus.setEnabled(false);
                holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
            }
            holder.tvWeight.setText("\t\t\t" + weightMorng);
        }
        holder.chkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                weight = nullCheckFloatNumber(holder.tvWeight.getText().toString().trim());
                weightMorng = nullCheckFloatNumber(mList.get(position).morning_milk);
                weightEvng = nullCheckFloatNumber(mList.get(position).evening_milk);


                if (holder.chkStatus.isChecked() != true) {
                    holder.chkStatus.setChecked(false);
                    holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                    morning_milk = "0";
                    evening_milk = "0";
                    if (session.equals("evening")) {
                        morning_milk = String.valueOf(weightMorng);
                    } else {
                        evening_milk = mList.get(position).evening_milk;
                        evening_milk = String.valueOf(weightEvng);
                    }
                    deleteSaleMilkData(holder, position, refreshList, "");

                } else {

                    if (weight <= 0) {
                        holder.chkStatus.setChecked(false);
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Edit_Sale_Milk_Weight_It_not_to_be_0));
                    } else {
                        holder.chkStatus.setChecked(true);
                        updateStatus(true, position, holder, refreshList);
                        databaseHandler = DatabaseHandler.getDbHelper(mContext);
                        SQLiteDatabase db = databaseHandler.getReadableDatabase();
                        float totalBalanceFromLocalDb = 0;
                        String selectedDate = SelectedDate.substring(0, 2);
                        String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
                        String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());

                        totalPrice = Double.parseDouble(mList.get(position).price_per_ltr) * weight;

                        Log.d(TAG, "onClki121122: " + db.toString() + mList.get(position).unic_customer + "//" + String.valueOf(totalPrice) + "//" + selectedDate + "//" + selectedMonth + "//" + selectedYear);

                        fetchStatus = "entry";
                        if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
                            fetchDataFromLocal(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);

                        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {
                            fetchDataFromLocalFifteen(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {

                            fetchDataFromLocalMonth(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                        } else {
                            fetchDataFromLocal(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                        }


                    }
                }
            }
        });
    }

    private void deleteSaleMilkData(final MyViewHolder holder, final int position, final RefreshList refreshList, final String type) {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    //{"status":"success","id":3}
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        mList.get(position).sale_status = "0";

                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                        Toast.makeText(mContext, mContext.getString(R.string.Milk_Removed_Successfull), Toast.LENGTH_SHORT).show();
                        refreshList.refreshList(mList);
                        if (type.equals("Cancel")) {
                            if (mList.get(position).sale_status.equals("0")) {
                                NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
                                    @Override
                                    public void handleResponse(String response) {
                                        System.out.println("Response===Daily Sale Milk====>>>>" + response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            if (jsonObject.getString("status").equals("success")) {
                                                if (holder.chkStatus.isChecked() == true) {
                                                    holder.chkStatus.setChecked(false);
                                                }
                                                if (session.equals("evening")) {
                                                    mList.get(position).evening_milk = evening_milk;

                                                    holder.tvWeight.setText("0");
                                                    if (!mList.get(position).evening_milk.equals("0")) {

                                                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                                    } else {
                                                        //holder.chkStatus.setEnabled(false);
                                                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                                    }
                                                } else {
                                                    mList.get(position).morning_milk = morning_milk;
                                                    holder.tvWeight.setText("0");

                                                    if (!mList.get(position).morning_milk.equals("0")) {
                                                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                                    } else {
                                                        // holder.chkStatus.setEnabled(false);
                                                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                                    }
                                                }
                                                UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Cancel_Success));
                                                // goNextFragmentWithBackStack(mContext, new SaleMilkCustomerListFragment());
                                                //UtilityMethod.showAlertBoxwithIntent(mContext, "Updated Successfully", SaleMilkCustomerListActivity.class);
                                            } else {
                                                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Cancel_Failed));
                                                //  goNextFragmentWithBackStack(mContext, new SaleMilkCustomerListFragment());
                                            }
                                            notifyDataSetChanged();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("customer_id", mList.get(position).id).addEncoded("schedule_date", date).addEncoded("schedule_shift", session).addEncoded("evening_milk", evening_milk).addEncoded("morning_milk", morning_milk)

                                        .build();
                                serviceCaller.addRequestBody(body);
                                serviceCaller.execute(Constant.DailySaveMilkSchedule);


                            }
                        }
                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Milk_Removed_Successfull));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("id", mList.get(position).sale_status).build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.deleteSaleMilkAPI);
    }

    private void updateStatus(boolean status, final int position, final MyViewHolder holder, final RefreshList refreshList) {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    //{"status":"success","id":3}
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {


                        mList.get(position).sale_status = jsonObject.getString("id");
                        //  holder.chkStatus.setEnabled(false);

                        holder.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.color_list_gray));
                        showToast(mContext, mContext.getString(R.string.Milk_Sale_Successfull));


                        refreshList.refreshList(mList);
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Milk_Saling_failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        double pricePerLtr = Double.parseDouble(mList.get(position).price_per_ltr);
        double weight = 0, totAmt = 0;
        //if (mList.get(position).evening_milk.equals("")) {
        if (session.equals("evening")) {
            if (!mList.get(position).evening_milk.trim().equals("")) {
                weight = Double.parseDouble(mList.get(position).getEvening_milk());
            }
        } else {
            if (!mList.get(position).morning_milk.trim().equals("")) {
                weight = Double.parseDouble(mList.get(position).getMorning_milk());
            }
        }
        totAmt = weight * pricePerLtr;
        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("customer_id", mList.get(position).id)
                .addEncoded("milk_perkg_price", mList.get(position).price_per_ltr)
                .addEncoded("milk_total_price", "" + totAmt)
                .addEncoded("shift", session)
                .addEncoded("insert_date", date)
                .addEncoded("milk_wt", "" + weight).build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.addSellingMilkAPI);
    }

    public void fetchDataFromLocal(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear, Double recentBalance) {
        int todayDateInInt = Integer.parseInt(selectedDate);

        if (todayDateInInt >= 1 && todayDateInInt <= 10) {

            getTotalPriceBetweenTwoDates(customerId, 1, 11, selectedMonth, selectedYear, recentBalance);

        } else if (todayDateInInt >= 11 && todayDateInInt <= 20) {
            getTotalPriceBetweenTwoDates(customerId, 1, 21, selectedMonth, selectedYear, recentBalance);

        } else if (todayDateInInt >= 21 && todayDateInInt <= 31) {
            getTotalPriceBetweenTwoDates(customerId, 1, 32, selectedMonth, selectedYear, recentBalance);

        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }

    }


    public void fetchDataFromLocalFifteen(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear, Double recentBalance) {
        int todayDateInInt = Integer.parseInt(selectedDate);

        if (todayDateInInt >= 1 && todayDateInInt <= 15) {

            getTotalPriceBetweenTwoDates(customerId, 1, 16, selectedMonth, selectedYear, recentBalance);
        } else if (todayDateInInt >= 16 && todayDateInInt <= 31) {
            getTotalPriceBetweenTwoDates(customerId, 1, 32, selectedMonth, selectedYear, recentBalance);
        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }


    }

    public void fetchDataFromLocalMonth(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear, Double recentBalance) {

        int todayDateInInt = Integer.parseInt(selectedDate);
        ArrayList totalPriceArrayList = new ArrayList<>();

        if (todayDateInInt >= 1 && todayDateInInt <= 31) {
            getTotalPriceBetweenTwoDates(customerId, 1, 32, selectedMonth, selectedYear, recentBalance);

        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void getTotalPriceBetweenTwoDates(int dairyId, int startDate, int endDate, String seletedMonth, String selecteYear, double recentBalance) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        Log.d(TAG, "getTotalPriceBetweenTwoDates: ");

        String url = "https://meridairy.in/software/api/admin/get-users-total-sale-milk-price?dairy_id=" + dairyId + "&to_date=" + selecteYear + "-12-" + endDate + " 00:00:00&from_date=" + selecteYear + "-12-" + startDate + " 00:00:00&customer_id=" + dairyId + "";
        StringRequest getRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jo = null;
                try {
                    jo = new JSONObject(response);
                    String s = jo.getString("data");
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObj = jsonArray.getJSONObject(0);
                    String ss = jsonObj.getString("total_milk_sale");

                    if (ss.equals("null")){
                        ss = "0.0";
                    }

                    float result = Float.parseFloat(ss);
                    float f = (float) recentBalance;
                    float tbldb = result + f;

                    MessageSend_Service_SIM_Web mess = new MessageSend_Service_SIM_Web();

                    if (fetchStatus.equals("entry")) {
                        if (session.equalsIgnoreCase("morning")) {
                            mess.sellMilkEntryFixedPrice(mContext, mList.get(positionGlobal).phone_number, mList.get(positionGlobal).name, date, session, mList.get(positionGlobal).morning_milk, mList.get(positionGlobal).price_per_ltr, String.valueOf(totalPrice), tbldb);
                        } else if (session.equalsIgnoreCase("evening")) {
                            mess.sellMilkEntryFixedPrice(mContext, mList.get(positionGlobal).phone_number, mList.get(positionGlobal).name, date, session, mList.get(positionGlobal).evening_milk, mList.get(positionGlobal).price_per_ltr, String.valueOf(totalPrice), tbldb);
                        }
                    } else {
                        String msg = null;

                        if (session.equalsIgnoreCase("morning")) {
                            msg = mess.sellMilkEntryFixedPricebt(mContext, mList.get(positionGlobal).unic_customer, mList.get(positionGlobal).phone_number, mList.get(positionGlobal).name, date, session, mList.get(positionGlobal).morning_milk, mList.get(positionGlobal).price_per_ltr, String.valueOf(totalPrice), tbldb);
                        } else if (session.equalsIgnoreCase("evening")) {
                            msg = mess.sellMilkEntryFixedPricebt(mContext, mList.get(positionGlobal).unic_customer, mList.get(positionGlobal).phone_number, mList.get(positionGlobal).name, date, session, mList.get(positionGlobal).evening_milk, mList.get(positionGlobal).price_per_ltr, String.valueOf(totalPrice), tbldb);
                        }
                        BluetoothClass aClass = new BluetoothClass();
                        aClass.CommonPrintReciept(nullCheckFunction(sessionManager.getValueSesion(KEY_center_name)), msg);

                    }


                } catch (Exception e) {

                    Log.d(TAG, "getTotalPriceBetweenTwoDateserror: " + e.getMessage());
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };
        queue.add(getRequest);


    }

    private void cancelItem(int position, MyViewHolder holderPosition) {
        morning_milk = "0";
        evening_milk = "0";
        if (session.equals("evening")) {
            if (!mList.get(position).morning_milk.equals("")) {
                morning_milk = mList.get(position).morning_milk;
            }
        } else {
            if (!mList.get(position).evening_milk.equals("")) {
                evening_milk = mList.get(position).evening_milk;
            }
        }
        if (mList.get(position).sale_status.equals("0")) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            holderPosition.tvWeight.setText("0");
                            if (session.equals("evening")) {
                                mList.get(position).evening_milk = evening_milk;
                                if (!mList.get(position).evening_milk.equals("0")) {
                                    holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                } else {
                                    holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                }
                            } else {
                                mList.get(position).morning_milk = morning_milk;
                                if (!mList.get(position).morning_milk.equals("0")) {
                                    holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                } else {
                                    // holder.chkStatus.setEnabled(false);
                                    holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                }
                            }
                            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Cancel_Success));

                        } else {
                            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Cancel_Failed));

                        }
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("customer_id", mList.get(position).id).addEncoded("schedule_date", date).addEncoded("schedule_shift", session).addEncoded("evening_milk", evening_milk).addEncoded("morning_milk", morning_milk).build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(Constant.DailySaveMilkSchedule);

        } else {
            holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
            deleteSaleMilkData(holderPosition, position, refreshList, "Cancel");


        }

    }


    public int getItemCount() {
        return mList.size();
    }

    private void showPopup(View view, int position, boolean is_active, MyViewHolder holderPosition) {
        @SuppressLint("ResourceType") ContextThemeWrapper ctw = new ContextThemeWrapper(mContext, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(ctw, view);
        popup.getMenu().add(MENU_EDIT, MENU_EDIT_ITEM, 0, mContext.getString(R.string.Edit));
        popup.getMenu().add(MENU_CANCEL, MENU_CANCEL_ITEM, 1, mContext.getString(R.string.Cancel));
        popup.getMenu().add(MENU_CANCEL, MENU_PRINT_ITEM, 2, mContext.getString(R.string.Print));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case MENU_EDIT_ITEM:
                        System.out.println(" is_active=====>>>>>" + is_active);
                        editItem(position, is_active, holderPosition);
                        break;
                    case MENU_CANCEL_ITEM:
                        cancelItem(position, holderPosition);
                        break;
                    case MENU_PRINT_ITEM:
                        if (isBluetoothHeadsetConnected()) {
                            if (mDevice == null || mSocket == null || mOutputStream == null) {
                                dialogBluetooth(mContext);
                            }
                            SQLiteDatabase db = databaseHandler.getReadableDatabase();
                            float totalBalanceFromLocalDb = 0;
                            String selectedDate = SelectedDate.substring(0, 2);
                            String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
                            String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());

                            totalPrice = Double.parseDouble(mList.get(position).price_per_ltr) * weight;

                            Log.d(TAG, "onClki121122: " + db.toString() + mList.get(position).unic_customer + "//" + String.valueOf(totalPrice) + "//" + selectedDate + "//" + selectedMonth + "//" + selectedYear);

                            MessageSend_Service_SIM_Web mess = new MessageSend_Service_SIM_Web();
                            fetchStatus = "entrypopup";
                            if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
                                fetchDataFromLocal(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);

                            } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {
                                fetchDataFromLocalFifteen(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                            } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {

                                fetchDataFromLocalMonth(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                            } else {
                                fetchDataFromLocal(db, Integer.parseInt(mList.get(position).id), String.valueOf(totalPrice), selectedDate, selectedMonth, selectedYear, totalPrice);
                            }


                        } else {
                            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
                            enableBluetooth();
                            dialogBluetooth(mContext);
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

    public boolean enableBluetooth()
    {
        try {
            badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    Bundle bundle = new Bundle();
                    ActivityCompat.startActivityForResult((Activity) mContext, enableBluetooth, 0, bundle);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void editItem(int pos, boolean is_active, MyViewHolder holderPosition) {
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.dialog_update_buyer_entry);
        dialog.setTitle(mContext.getString(R.string.Update_Entry));
        dialog.show();
        TextInputLayout layoutMilkRate = dialog.findViewById(R.id.layoutMilkRate);
        TextInputLayout layoutEvening = dialog.findViewById(R.id.layoutEvening);
        TextInputLayout layoutMorning = dialog.findViewById(R.id.layoutMorning);
        View priceView = dialog.findViewById(R.id.priceView);
        View eveningView = dialog.findViewById(R.id.eveningView);
        View morningView = dialog.findViewById(R.id.morningView);
        final EditText edtMorningMilk = dialog.findViewById(R.id.ediMorningMilk);
        final EditText edtEveningMilk = dialog.findViewById(R.id.ediEveningMilk);
        final EditText edtMilkRate = dialog.findViewById(R.id.edtMilkRate);
        edtMilkRate.setVisibility(View.GONE);
        priceView.setVisibility(View.GONE);
        layoutMilkRate.setVisibility(View.GONE);

        if (session.equals("evening")) {
            edtMorningMilk.setVisibility(View.GONE);
            layoutMorning.setVisibility(View.GONE);
            morningView.setVisibility(View.GONE);
            edtEveningMilk.setText("" + weightEvng);

        } else {
            eveningView.setVisibility(View.GONE);
            layoutEvening.setVisibility(View.GONE);
            edtEveningMilk.setVisibility(View.GONE);
            if (!mList.get(pos).morning_milk.equals("null")) {
                edtMorningMilk.setText("" + weightMorng);
            }
        }
        if (!mList.get(pos).price_per_ltr.equals("null")) {
            edtMilkRate.setText(mList.get(pos).price_per_ltr);
        }
        Button btnNext = dialog.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                milk_wt = "0";
                morning_milk = "0";
                evening_milk = "0";
                totalPrice = 0;
                if (session.equals("evening")) {
                    milk_wt = edtEveningMilk.getText().toString().trim();
                    evening_milk = milk_wt;
                    morning_milk = String.valueOf(weightMorng);

                } else {
                    milk_wt = edtMorningMilk.getText().toString().trim();
                    morning_milk = milk_wt;
                    evening_milk = String.valueOf(weightEvng);
                }
                weight = nullCheckFloatNumber(milk_wt);
                if (weight <= 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Edit_Sale_Milk_Weight_It_not_to_be_0));
                } else {
                    totalPrice = Double.parseDouble(mList.get(pos).price_per_ltr) * weight;


                    if (is_active == true) {
                        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                            @Override
                            public void handleResponse(String response) {
                                try {
                                    dialog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("success")) {
                                        showToast(mContext, mContext.getString(R.string.Updating_Success));
                                        dialog.dismiss();
                                        if (session.equals("evening")) {
                                            mList.get(pos).setEvening_milk(evening_milk);

                                            holderPosition.tvWeight.setText("\t\t\t" + mList.get(pos).getEvening_milk());
                                            if (!holderPosition.tvWeight.getText().toString().trim().equals("0")) {
                                                holderPosition.chkStatus.setEnabled(true);
                                            } else {
                                                holderPosition.chkStatus.setEnabled(false);
                                            }
                                        } else {
                                            mList.get(pos).setMorning_milk(edtMorningMilk.getText().toString());
                                            holderPosition.tvWeight.setText("\t\t\t" + mList.get(pos).getMorning_milk());
                                            if (!holderPosition.tvWeight.getText().toString().equals("0")) {
                                                holderPosition.chkStatus.setEnabled(true);
                                            } else {
                                                holderPosition.chkStatus.setEnabled(false);
                                            }
                                        }
                                        System.out.println("holderPosition.tvWeight.getText()==>>>>>" + holderPosition.tvWeight.getText().toString());
                                        notifyDataSetChanged();
                                        refreshList.refreshList(mList);

                                    } else {
                                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("customer_id", mList.get(pos).id).addEncoded("milk_perkg_price", "" + mList.get(pos).price_per_ltr).addEncoded("shift", session).addEncoded("milk_wt", milk_wt).addEncoded("id", mList.get(pos).sale_status).addEncoded("milk_total_price", "" + totalPrice).build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.UpdateDailySaveMilkEntry);
                    } else {
                        @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                            @Override
                            public void handleResponse(String response) {
                                try {
                                    dialog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("status").equals("success")) {

                                        if (session.equals("evening")) {
                                            mList.get(pos).setEvening_milk(evening_milk);
                                            holderPosition.tvWeight.setText("\t\t\t" + mList.get(pos).getEvening_milk());
                                            if (!holderPosition.tvWeight.getText().toString().trim().equals("0")) {
                                                holderPosition.chkStatus.setEnabled(true);
                                                holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                            } else {
                                                holderPosition.chkStatus.setEnabled(false);
                                                holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                            }
                                        } else {
                                            mList.get(pos).setMorning_milk(edtMorningMilk.getText().toString());
                                            holderPosition.tvWeight.setText("\t\t\t" + mList.get(pos).getMorning_milk());
                                            if (!holderPosition.tvWeight.getText().toString().equals("0")) {
                                                holderPosition.chkStatus.setEnabled(true);
                                                holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                                            } else {
                                                holderPosition.chkStatus.setEnabled(false);
                                                holderPosition.parentLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorRed));
                                            }
                                        }
                                        refreshList.refreshList(mList);
                                        showToast(mContext, mContext.getString(R.string.Updating_Success));

                                        notifyDataSetChanged();

                                    } else {
                                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Updating_Failed));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };


                        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("customer_id", mList.get(pos).getId()).addEncoded("schedule_date", date).addEncoded("schedule_shift", session).addEncoded("evening_milk", evening_milk).addEncoded("morning_milk", morning_milk).build();
                        serviceCaller.addRequestBody(body);
                        serviceCaller.execute(Constant.DailySaveMilkSchedule);
                    }
                }
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCustomerName, tvId, tvS_no, tvStatus, tvWeight;
        CheckBox chkEvening, chkStatus;
        LinearLayout parentLayout;
        ImageView imgMoreDetail;
        LinearLayout bottom_wrapper;

        MyViewHolder(View view) {
            super(view);
            imgMoreDetail = itemView.findViewById(R.id.imgMoreDetail);
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName);
            tvId = itemView.findViewById(R.id.tvId);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvS_no = itemView.findViewById(R.id.tvS_no);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            chkStatus = itemView.findViewById(R.id.chkStatus);
            chkEvening = itemView.findViewById(R.id.chkEvening);

            parentLayout = itemView.findViewById(R.id.parentLayout);
            bottom_wrapper = itemView.findViewById(R.id.bottom_wrapper);
            txtCustomerName.setOnClickListener(this);
            tvId.setOnClickListener(this);
            tvWeight.setOnClickListener(this);
            tvS_no.setOnClickListener(this);
            imgMoreDetail.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            switch (view.getId()) {
                case R.id.txtCustomerName:
                case R.id.tvId:
                case R.id.tvWeight:
                case R.id.tvS_no:
                    fragment = new BuyerMonthlyDetailsFragment();
                    bundle = new Bundle();
                    bundle.putString("unic_customer", mList.get(getAdapterPosition()).unic_customer);
                    bundle.putString("CustomerId", mList.get(getAdapterPosition()).id);
                    bundle.putString("CustomerName", mList.get(getAdapterPosition()).name);
                    bundle.putString("userMobile", mList.get(getAdapterPosition()).phone_number);
                    bundle.putString("milkRate", mList.get(getAdapterPosition()).price_per_ltr);
                    bundle.putString("dairyID", sessionManager.getValueSesion(SessionManager.KEY_UserID));
                    bundle.putString("fromWhere", "DailySaleMilkAdapter");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                    break;
                case R.id.imgMoreDetail:
                    weightMorng = nullCheckFloatNumber(mList.get(position).morning_milk);
                    weightEvng = nullCheckFloatNumber(mList.get(position).evening_milk);
                    is_active = chkStatus.isChecked();
                    showPopup(view, position, is_active, holderPosition);
                    break;
            }
        }
    }


}
