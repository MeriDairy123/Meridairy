package b2infosoft.milkapp.com.Model;

import static com.paytm.pgsdk.easypay.manager.PaytmAssist.getContext;
import static b2infosoft.milkapp.com.Database.DatabaseHandler.getDbHelper;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.getSaleThreeMonthMilkEntryList;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.get20daysdata;
import static b2infosoft.milkapp.com.appglobal.Constant.get30daysdata;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyMilkEntry3MonthAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getcount30days;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.appglobal.Constant.updateBuyMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.uploadOfflineSaleEntryToServerAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FatYES;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

/**
 * Created by B2Infosoft on 8/3/2017.
 */

public class CustomerEntryListPojo {

    public Integer id = 0, onLineId = 0;
    public String customer_id = "", dairy_id = "", fat = "", entry_date = "",milk_entry_unicId="", per_kg_price = "", total_bonus = "", total_price = "", total_milk = "", shift = "", name = "", unic_customer = "", snf = "", clr = "", str_to_time = "", date_d = "", month_m = "", year_y = "", milkRateCategory = "";
    public Integer fatSnfCategory = 0, entryStatus = 0, srNo = 0;



    static ProgressDialog progressBar;
    //jsonObject1.getInt("snf_fat_categories")
    // Database
    public CustomerEntryListPojo(int id, int onlineId, String customer_id, String dairy_id, String name, String unic_customer, String shift, String entry_date, String fat, String snf, String clr, String per_kg_price, String total_milk, String total_bonus, String total_price, String milkRateCategory, Integer fatSnfCategory, String str_to_time, Integer entryStatus, String date_d, String month_m, String year_y,String milkentryUnicId) {
        this.id = id;
        this.onLineId = onlineId;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.name = name;
        this.unic_customer = unic_customer;
        this.shift = shift;
        this.entry_date = entry_date;
        this.fat = fat;
        this.snf = snf;
        this.clr = clr;
        this.per_kg_price = per_kg_price;
        this.total_milk = total_milk;
        this.total_bonus = total_bonus;
        this.total_price = total_price;
        this.milkRateCategory = milkRateCategory;
        this.fatSnfCategory = fatSnfCategory;
        this.str_to_time = str_to_time;
        this.entryStatus = entryStatus;
        this.date_d = date_d;
        this.month_m = month_m;
        this.year_y = year_y;
        this.milk_entry_unicId = milkentryUnicId;

    }

    // from APi
    public CustomerEntryListPojo(int onLineId, String customer_id, String dairy_id, String fat, String entry_date, String per_kg_price, String total_bonus, String total_price, String total_milk, String shift, String name, String unic_customer, String snf, String clr, String milkRateCategory, int fatSnfCategory, String date_d, String month_m, String year_y, int srNo) {

        this.onLineId = onLineId;
        this.customer_id = customer_id;
        this.dairy_id = dairy_id;
        this.fat = fat;
        this.entry_date = entry_date;
        this.per_kg_price = per_kg_price;
        this.total_bonus = total_bonus;
        this.total_price = total_price;
        this.total_milk = total_milk;
        this.shift = shift;
        this.name = name;
        this.unic_customer = unic_customer;
        this.snf = snf;
        this.clr = clr;
        this.milkRateCategory = milkRateCategory;
        this.fatSnfCategory = fatSnfCategory;
        this.date_d = date_d;
        this.month_m = month_m;
        this.year_y = year_y;
        this.srNo = srNo;
    }

    public static void getOnlineBuyMilkEntryList(Context mContext) {

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(mContext);
                DatabaseHandler db = sessionManager.db;

                NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), false) {
                    @SuppressLint("StaticFieldLeak")
                    @Override
                    public void handleResponse(String response) {

                        try {
                            JSONArray mainJsonArray = new JSONArray(response);
                            if (mainJsonArray.length() > 0) {
                                db.addBuyMilkEntryFromAPI(mainJsonArray);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("entry_date", SelectedDate).addEncoded("shift", strSession).build();
                caller.addRequestBody(body);
                caller.execute(getBuyMilkEntryAPI);

            }
        });
    }

    public static void getBuyThreeMonthMilkEntryList(Context mContext, String from, boolean isDailog) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler db = getDbHelper(mContext);
        db.deleteAllBuyMilkEntryRecord();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait) + " " + mContext.getString(R.string.MILK_ENTRY), isDailog) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (from.equalsIgnoreCase("")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    db.addBuyMilkEntryFromAPI(jsonArray);
                                    if (FirstTime.equalsIgnoreCase("yes")) {
                                        getSaleThreeMonthMilkEntryList(mContext, true);
                                    }
                                }
                            }, 0);
                        }

                    } else {
                        if (FirstTime.equalsIgnoreCase("yes")) {
                            getSaleThreeMonthMilkEntryList(mContext, true);
                        }
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).build();
        caller.addRequestBody(body);
        caller.execute(getBuyMilkEntry3MonthAPI);

    }

    public static void uploadEntryToServerOld(Context mContext, final String screenFrom, milkEntryUploadListner listner) {

        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerEntryListPojo> mList = databaseHandler.getMilkBuyEntryRecordsOffline();

        System.out.println("uploadEntryToServer list>>>>" + mList.size());
        int OfflineListSize = mList.size();

        if (!mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                int entryId = mList.get(i).getId();
                int running = i;
                NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.UploadingOnline), false) {
                    @Override
                    public void handleResponse(String response) {
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            if (mainObject.getString("status").equalsIgnoreCase("success")) {
                                databaseHandler.updateBuyMilkEntryId(String.valueOf(entryId),String.valueOf(mainObject.getInt("id")),OfflineListSize,running );

                            } else {
                                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                try {
                    JSONArray productArray = new JSONArray();
                    JSONObject mainObject = new JSONObject();
                    JSONObject productData = new JSONObject();
                    try {
                        productData.put("customer_id", "" + mList.get(i).getCustomer_id());
                        productData.put("dairy_id", "" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                        productData.put("shift", "" + mList.get(i).shift);
                        productData.put("entry_date", "" + mList.get(i).getEntry_date());
                        productData.put("entry_date_str", "" + mList.get(i).getStr_to_time());
                        productData.put("fat", "" + mList.get(i).getFat());
                        productData.put("snf", "" + mList.get(i).getSnf());
                        productData.put("clr", "" + mList.get(i).getClr());
                        productData.put("per_kg_price", "" + mList.get(i).getPer_kg_price());
                        productData.put("total_milk", "" + mList.get(i).getTotal_milk());
                        productData.put("total_bonus", "" + mList.get(i).getTotal_bonus());
                        productData.put("total_price", "" + mList.get(i).getTotal_price());
                        productData.put("milk_category", "" + mList.get(i).getMilkRateCategory());
                        productData.put("snf_fat_categories", "" + mList.get(i).getFatSnfCategory());
                        productData.put("d", "" + mList.get(i).getDate_d());
                        productData.put("m", "" + mList.get(i).getMonth_m());
                        productData.put("y", "" + mList.get(i).getYear_y());
                        productArray.put(productData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mainObject.put("make_array", productArray);
                    System.out.println("mainObject>>>" + mainObject.toString());


                    RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                    webServiceCaller.addRequestBody(body);
                    if (!mList.isEmpty()) {
                        webServiceCaller.execute(Constant.uploadOfflineBuyMilkEntryToServerAPI);
                    }
                    if (i == OfflineListSize - 1) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listner.onMilkEntryUploaded(screenFrom);
                                UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Milk_Buy) + " " + mContext.getString(R.string.Offline_Entry_Successfully_Uploaded_to_Server));

                            }
                        }, 3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void uploadEntryToServer(Context mContext, final String screenFrom, milkEntryUploadListner listner) {

        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = getDbHelper(mContext);
        ArrayList<CustomerEntryListPojo> mList = databaseHandler.getMilkBuyEntryRecordsOffline();
        int entrySze = mList.size();

       //String ts = String.valueOf(System.currentTimeMillis());
        String ts = "";
        String milkEntryUniqueId= "";

        if (!mList.isEmpty()) {
            int addEntrytem = 0, updateEntrytem = 0;

            for (int i = 0; i <mList.size(); i++) {
                int entryId = mList.get(i).getId();
                int onLineId = mList.get(i).getOnLineId();
                ts = String.valueOf(System.currentTimeMillis());
                ts = ts.substring(ts.length() - 4);
                milkEntryUniqueId = mList.get(i).customer_id+ts;

                System.out.println("onLineId==>>>" + onLineId);
                System.out.println("EntryStatus==>>>" + mList.get(i).getEntryStatus());
                if (onLineId == 0 && mList.get(i).getEntryStatus() == 0) {
                    addEntrytem = addEntrytem + 1;
                    if (i==0){
                        sessionManager.setValueSession("progressbar","off");
                    }else{
                        sessionManager.setValueSession("progressbar","on");
                    }
                    int totalsize = mList.size();
                    int running = i;
                    addListenerOnButtonClick(addEntrytem,mList.size(),mContext,mContext.getString(R.string.UploadingOnline));

                    NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Milk_Buy) + " " + mContext.getString(R.string.UploadingOnline), false) {
                        @Override
                        public void handleResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getString("message").equals("success")) {
                                    //DatabaseHandler databaseHandler = getDbHelper(mContext);
                                    databaseHandler.updateBuyMilkEntryId(String.valueOf(entryId),String.valueOf(mainObject.getString("id")),totalsize,running);

                                } else {
                                    //databaseHandler.deleteBuyMilkEntryRecord(entryId + "");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    try {
                        JSONArray productArray = new JSONArray();
                        JSONObject mainObject = new JSONObject();
                        JSONObject productData = new JSONObject();

                        try {
                            productData.put("customer_id", "" + mList.get(i).customer_id);
                            productData.put("dairy_id", "" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                            productData.put("fat", "" + mList.get(i).fat);
                            productData.put("snf", "" + mList.get(i).snf);
                            productData.put("clr", "" + mList.get(i).clr);
                            productData.put("entry_date", "" + mList.get(i).entry_date);
                            productData.put("entry_date_str", "" + mList.get(i).str_to_time);
                            productData.put("per_kg_price", "" + mList.get(i).per_kg_price);
                            productData.put("total_bonus", "" + mList.get(i).total_bonus);
                            productData.put("total_price", "" + mList.get(i).total_price);
                            productData.put("total_milk", "" + mList.get(i).total_milk);
                            productData.put("shift", "" + mList.get(i).shift);
                            productData.put("milk_category", "" + mList.get(i).milkRateCategory);
                            productData.put("snf_fat_categories", "" + mList.get(i).fatSnfCategory);
                            productData.put("d", "" + mList.get(i).date_d);
                            productData.put("m", "" + mList.get(i).month_m);
                            productData.put("y", "" + mList.get(i).year_y);

                         //   productData.put("milkEntryUniqueId", "" + ts.toString() + mList.get(i).customer_id);
                            productData.put("milkEntryUniqueId", "" + milkEntryUniqueId);

                            productArray.put(productData);
                            System.out.println("productData==" + productData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mainObject.put("make_array", productArray);
                        System.out.println("mainObject24964>>>" + mainObject.toString());
                        RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                        webServiceCaller.addRequestBody(body);
                        webServiceCaller.execute(Constant.uploadOfflineBuyMilkEntryToServerAPI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (onLineId > 0 && mList.get(i).entryStatus == 0) {
                    updateEntrytem = updateEntrytem + 1;
                    int totalsize = entrySze;
                    int running = i;

                    NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Update_Entry) + "...", true) {
                        @Override
                        public void handleResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String staus = jsonObject.getString("status");
                                if (staus.equalsIgnoreCase("success")) {
                                    databaseHandler.updateBuyMilkEntryId("" + entryId, String.valueOf(onLineId),totalsize,running);
                                } else {
                                    showToast(mContext, mContext.getString(R.string.Entry_Updating_Failed));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", mList.get(i).getDairy_id()).addEncoded("id", "" + mList.get(i).getOnLineId()).addEncoded("customer_id", mList.get(i).getCustomer_id()).addEncoded("entry_date", mList.get(i).getEntry_date()).addEncoded("shift", mList.get(i).getShift()).addEncoded("milk_category", mList.get(i).getMilkRateCategory()).addEncoded("snf_fat_categories", "" + mList.get(i).getFat()).addEncoded("fat", mList.get(i).getFat()).addEncoded("snf", mList.get(i).getSnf()).addEncoded("clr", mList.get(i).getClr()).addEncoded("per_kg_price", mList.get(i).getPer_kg_price()).addEncoded("total_milk", mList.get(i).getTotal_milk()).addEncoded("total_bonus", mList.get(i).getTotal_bonus()).addEncoded("total_price", mList.get(i).getTotal_price()).build();
                    serviceCaller.addRequestBody(body);

                    serviceCaller.execute(updateBuyMilkEntryAPI);
                }
                if (i == entrySze - 1) {
                    if (addEntrytem > 0) {
                       //UtilityMethod.showAlertBox(mContext, addEntrytem + " " + mContext.getString(R.string.Milk_Buy) + " " + mContext.getString(R.string.Offline_Entry_Successfully_Uploaded_to_Server));

                    } else if (updateEntrytem > 0) {
                       // UtilityMethod.showAlertBox(mContext, updateEntrytem + " " + mContext.getString(R.string.Milk_Buy) + " " + mContext.getString(R.string.Entry_Update_Successfully));

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listner.onMilkEntryUploaded(screenFrom);
                        }
                    }, 3000);
                }


            }

        }
    }



    public static void addListenerOnButtonClick(int progressBarStatus, int fileSize,Context mcontext,String Msg) {
        SessionManager sessionManager = new SessionManager(mcontext);
                // creating progress bar dialog
                //reset progress bar and filesize status
        if (sessionManager.getValueSesion("progressbar").equals("off")){
            progressBar = new ProgressDialog(mcontext);
            progressBar.setCancelable(true);
            progressBar.setMessage(Msg);
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setMax(100);
            progressBar.show();
        }


                int abc = (int) ((progressBarStatus * 100) / fileSize);
                // progressBar.setProgress(0);


        int finalProgressBarStatus = progressBarStatus;
        new Thread(new Runnable() {
                    public void run() {

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // Updating the progress bar
                            progressBar.setProgress(abc);

                        if (finalProgressBarStatus ==fileSize-1){
                            try {
                                Thread.sleep(1000);
                                progressBar.setProgress(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            // close the progress bar dialog
                            progressBar.dismiss();
                        }
                    }
                }).start();
            }//end of onClick method


    public static void matchcountdata(Context mContext, boolean isDailog) {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler db = getDbHelper(mContext);
        int countdata = db.countmilk_entry_tabledata();
       // db.deleteAllBuyMilkEntryRecord();

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), isDailog) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        db.deleteAllBuyMilkEntryRecord();
                        int pagecount = jsonObject.getInt("pageCount");
                        for(int  i=1;i<=pagecount;i++){
                            if (i==1){
                                sessionManager.setValueSession("progressbar","off");
                            }else{
                                sessionManager.setValueSession("progressbar","on");
                            }
                            addListenerOnButtonClick(i,pagecount+1,mContext,"Loading Data...");
                            get30daysdata(mContext,false,String.valueOf(i),formattedDate);
                        }
                        //JSONArray jsonArray = jsonObject.getJSONArray("data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("date", formattedDate)
                .addEncoded("count", String.valueOf(countdata))
                .build();
        caller.addRequestBody(body);
        caller.execute(getcount30days);
    }


    public static void get30daysdata(Context mContext, boolean isDailog,String page,String date) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler db = getDbHelper(mContext);
        int countdata = db.countmilk_entry_tabledata();

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), isDailog) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {

                        String page = jsonObject.getString("page");
                        String pageCount = jsonObject.getString("pageCount");

                        JSONObject jsonArray = jsonObject.getJSONObject("datas");
                        JSONArray mainJsonArray = null;
                       // JSONArray ja_data = jsonObj.getJSONArray("data");
                        mainJsonArray = jsonArray.getJSONArray("data");

                        if (mainJsonArray.length() > 0) {
                            db.addBuyMilkEntryFromAPI30Days(mainJsonArray);
                        }
                       // JSONArray data =  new JSONArray(jsonArray.get("data"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("date", date)
                .addEncoded("page", page)
                .build();
        caller.addRequestBody(body);
        caller.execute(get30daysdata);
    }


    public static void get20daysdata(Context mContext, boolean isDailog,String date){
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler db = getDbHelper(mContext);
        int countdata = db.countmilk_entry_tabledata();
        db.deleteAllBuyMilkEntryRecord();

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), isDailog) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {


                        //JSONObject jsonArray = jsonObject.getJSONObject("datas");
                        JSONArray mainJsonArray = null;
                        // JSONArray ja_data = jsonObj.getJSONArray("data");
                        mainJsonArray = jsonObject.getJSONArray("datas");

                        if (mainJsonArray.length() > 0) {
                            db.addBuyMilkEntryFromAPI30Days(mainJsonArray);
                        }
                        // JSONArray data =  new JSONArray(jsonArray.get("data"));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("date", date)
                .build();
        caller.addRequestBody(body);
        caller.execute(get20daysdata);
    }




    // checking how much file is downloaded and updating the filesize
    public int doOperation(int fileSize) {
        //The range of ProgressDialog starts from 0 to 10000
        while (fileSize <= 10000) {
            fileSize++;
            if (fileSize == 1000) {
                return 10;
            } else if (fileSize == 2000) {
                return 20;
            } else if (fileSize == 3000) {
                return 30;
            } else if (fileSize == 4000) {
                return 40; // you can add more else if
            }
         /* else {
                return 100;
            }*/
        }//end of while
        return 100;
    }//end of doOperation





    public static void uploadPlantMilkEntryToServer(Context mContext, final String screenFrom) {

        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerEntryListPojo> mList = databaseHandler.getPlantEntryRecords();

        System.out.println("uploadPlantEntryToServer list>>>>" + mList.size());
        if (!mList.isEmpty()) {
            NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Uploading Online,Please wait...", false) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        if (mainObject.getString("status").equals("success")) {
                            databaseHandler.deletePlantAllEntryRecords();
                            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Offline_Entry_Successfully_Uploaded_to_Server));

                        } else {
                            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            try {
                JSONArray productArray = new JSONArray();
                JSONObject mainObject = new JSONObject();
                if (!mList.isEmpty()) {
                    for (int i = 0; i < mList.size(); i++) {
                        JSONObject productData = new JSONObject();

                        try {
                            productData.put("customer_id", "" + mList.get(i).customer_id);
                            productData.put("dairy_id", "" + sessionManager.getValueSesion(SessionManager.KEY_PlantId));
                            productData.put("vehicle_id", "" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                            productData.put("fat", "" + mList.get(i).fat);
                            productData.put("snf", "" + mList.get(i).snf);
                            productData.put("clr", "" + mList.get(i).clr);
                            productData.put("entry_date", "" + mList.get(i).entry_date);
                            productData.put("entry_date_str", "" + mList.get(i).str_to_time);
                            productData.put("per_kg_price", "" + mList.get(i).per_kg_price);
                            productData.put("per_kg_price", "" + mList.get(i).per_kg_price);
                            productData.put("total_bonus", "" + mList.get(i).total_bonus);
                            productData.put("total_price", "" + mList.get(i).total_price);
                            productData.put("total_milk", "" + mList.get(i).total_milk);
                            productData.put("shift", "" + mList.get(i).shift);
                            productData.put("milk_category", "" + mList.get(i).milkRateCategory);
                            productData.put("snf_fat_categories", "" + mList.get(i).fatSnfCategory);
                            productData.put("d", "" + mList.get(i).date_d);
                            productData.put("m", "" + mList.get(i).month_m);
                            productData.put("y", "" + mList.get(i).year_y);
                            productArray.put(productData);
                            System.out.println("productData==" + productData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mainObject.put("make_array", productArray);
                System.out.println("mainObject>>>" + mainObject.toString());


                RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                webServiceCaller.addRequestBody(body);
                if (!mList.isEmpty()) {
                    webServiceCaller.execute(Constant.uploadOfflineBuyMilkEntryToServerAPI);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void uploadPlantSaleMilkEntryToServer(Context mContext, final String screenFrom) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerSaleMilkEntryList> mList = databaseHandler.getPlantSaleMilkEntryRecords();
        if (!mList.isEmpty()) {
            NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.UploadingOnline), false) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        if (mainObject.getString("status").equals("success")) {
                            databaseHandler.deletePlantAllEntryRecords();
                            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Offline_Entry_Successfully_Uploaded_to_Server));
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Entry_Uploading_Failed_Please_Try_again));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            try {
                JSONArray productArray = new JSONArray();
                JSONObject mainObject = new JSONObject();
                if (!mList.isEmpty()) {
                    for (int i = 0; i < mList.size(); i++) {

                        JSONObject productData = new JSONObject();
                        System.out.println("entry_date==" + mList.get(i).entry_date);
                        System.out.println("str_to_time==" + mList.get(i).str_to_time);
                        try {
                            productData.put("customer_id", "" + mList.get(i).customer_id);
                            productData.put("dairy_id", "" + sessionManager.getValueSesion(SessionManager.KEY_PlantId));
                            productData.put("vehicle_id", "" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                            productData.put("fat", "" + mList.get(i).fat);
                            productData.put("snf", "" + mList.get(i).snf);
                            productData.put("insert_date", "" + mList.get(i).entry_date);
                            productData.put("insert_date_in_str", "" + mList.get(i).str_to_time);
                            productData.put("total_bonus", "" + mList.get(i).total_bonus);
                            productData.put("milk_perkg_price", "" + mList.get(i).per_kg_price);
                            productData.put("milk_total_price", "" + mList.get(i).total_price);
                            productData.put("milk_wt", "" + mList.get(i).total_milk);
                            productData.put("shift", "" + mList.get(i).shift);
                            productData.put("milkRateCategory", "" + mList.get(i).milkRateCategory);
                            productData.put("snf_fat_categories", "" + mList.get(i).fatSnfCategory);
                            productData.put("d", "" + mList.get(i).date_d);
                            productData.put("m", "" + mList.get(i).month_m);
                            productData.put("y", "" + mList.get(i).year_y);
                            productArray.put(productData);
                            System.out.println("productData==" + productData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mainObject.put("make_array", productArray);
                System.out.println("mainObject>>>>" + mainObject.toString());

                RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                webServiceCaller.addRequestBody(body);
                if (!mList.isEmpty()) {
                    webServiceCaller.execute(uploadOfflineSaleEntryToServerAPI);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOnLineId() {
        return onLineId;
    }

    public void setOnLineId(int onLineId) {
        this.onLineId = onLineId;
    }

    public void setOnLineId(Integer onLineId) {
        this.onLineId = onLineId;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getDairy_id() {
        return dairy_id;
    }

    public void setDairy_id(String dairy_id) {
        this.dairy_id = dairy_id;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getEntry_date() {
        return entry_date;
    }

    public void setEntry_date(String entry_date) {
        this.entry_date = entry_date;
    }

    public String getPer_kg_price() {
        return per_kg_price;
    }

    public void setPer_kg_price(String per_kg_price) {
        this.per_kg_price = per_kg_price;
    }

    public String getTotal_bonus() {
        return total_bonus;
    }

    public void setTotal_bonus(String total_bonus) {
        this.total_bonus = total_bonus;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getTotal_milk() {
        return total_milk;
    }

    public void setTotal_milk(String total_milk) {
        this.total_milk = total_milk;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }

    public String getClr() {
        return clr;
    }

    public void setClr(String clr) {
        this.clr = clr;
    }

    public String getStr_to_time() {
        return str_to_time;
    }

    public void setStr_to_time(String str_to_time) {
        this.str_to_time = str_to_time;
    }

    public String getDate_d() {
        return date_d;
    }

    public void setDate_d(String date_d) {
        this.date_d = date_d;
    }

    public String getMonth_m() {
        return month_m;
    }

    public void setMonth_m(String month_m) {
        this.month_m = month_m;
    }

    public String getYear_y() {
        return year_y;
    }

    public void setYear_y(String year_y) {
        this.year_y = year_y;
    }

    public String getMilkRateCategory() {
        return milkRateCategory;
    }

    public void setMilkRateCategory(String milkRateCategory) {
        this.milkRateCategory = milkRateCategory;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }

    public Integer getFatSnfCategory() {
        return fatSnfCategory;
    }

    public void setFatSnfCategory(Integer fatSnfCategory) {
        this.fatSnfCategory = fatSnfCategory;
    }

    public int getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }

    public String getMilk_entry_unicId() {
        return milk_entry_unicId;
    }

    public void setMilk_entry_unicId(String milk_entry_unicId) {
        this.milk_entry_unicId = milk_entry_unicId;
    }

}
