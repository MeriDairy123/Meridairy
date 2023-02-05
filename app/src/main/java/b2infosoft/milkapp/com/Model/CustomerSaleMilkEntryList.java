package b2infosoft.milkapp.com.Model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Database.DatabaseHandler.getDbHelper;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.getSaleMilkEntry3MonthAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.appglobal.Constant.updateSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.uploadOfflineSaleEntryToServerAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

/**
 * Created by B2infosoft on 8/3/2017.
 */

public class CustomerSaleMilkEntryList {

    public int id = 0, onLineId = 0;
    public String customer_id = "",
            dairy_id = "", fat = "", entry_date = "", per_kg_price = "", total_bonus = "", total_price = "",
            total_milk = "", shift = "", name = "", unic_customer = "", snf = "", clr = "", str_to_time = "", date_d = "", month_m = "", year_y = "",
            milkRateCategory = "";
    public Integer fatSnfCategory = 0, srNo = 0, deliveryBoyId = 0, entryStatus = 0;


    // Database
    public CustomerSaleMilkEntryList(int id, int onLineId, int deliveryBoyId, String customer_id, String dairy_id, String name, String unic_customer,
                                     String shift, String entry_date, String fat, String snf, String clr, String per_kg_price,
                                     String total_milk, String total_bonus, String total_price,
                                     String milkRateCategory, Integer fatSnfCategory, String str_to_time, Integer entryStatus,
                                     String date_d, String month_m, String year_y) {
        this.id = id;
        this.onLineId = onLineId;
        this.deliveryBoyId = deliveryBoyId;
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

    }

    // from APi
    public CustomerSaleMilkEntryList(int id, int onLineId, int deliveryBoyId, String customer_id, String dairy_id, String fat,
                                     String entry_date, String per_kg_price,
                                     String total_bonus, String total_price, String total_milk, String shift,
                                     String name, String unic_customer, String snf, String clr,
                                     String milkRateCategory, int fatSnfCategory, int srNo) {
        this.id = id;
        this.onLineId = onLineId;
        this.deliveryBoyId = deliveryBoyId;
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
        this.srNo = srNo;
        this.deliveryBoyId = deliveryBoyId;
    }

    public static void getOnlineSaleMilkEntryList(Context mContext) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SessionManager sessionManager = new SessionManager(mContext);
                DatabaseHandler db = sessionManager.db;
                db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
                NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), false) {
                    @Override
                    public void handleResponse(String response) {
                        try {
                            JSONArray mainJsonArray = new JSONArray(response);
                            if (mainJsonArray.length() > 0) {
                                db.addSaleMilkEntryFromAPI(mainJsonArray);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                RequestBody body = new FormEncodingBuilder()
                        .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                        .addEncoded("entry_date", SelectedDate)
                        .addEncoded("shift", strSession)
                        .build();
                caller.addRequestBody(body);
                caller.execute(getSaleMilkEntryAPI);
            }
        });
    }

    public static void getSaleThreeMonthMilkEntryList(Context mContext, boolean isShowDialog) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler db = getDbHelper(mContext);
        db.deleteAllSaleMilkEntry();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait) + " " + mContext.getString(R.string.MILK_ENTRY), isShowDialog) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("status")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    db.addSaleMilkEntryFromAPI(mainJsonArray);
                                }
                            }, 0);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        caller.addRequestBody(body);
        caller.execute(getSaleMilkEntry3MonthAPI);

    }

    public static void uploadSaleMilkEntryToServer(Context mContext, final String screenFrom, milkEntryUploadListner listner) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = getDbHelper(mContext);
        ArrayList<CustomerSaleMilkEntryList> mList = databaseHandler.getSaleMilkEntryOfflineEntry();
        int entrySze = mList.size();

        if (!mList.isEmpty()) {
            int addEntrytem = 0;
            int updateEntrytem = 0;
            for (int i = 0; i < mList.size(); i++) {
                int entryId = mList.get(i).getId();
                int onLineId = mList.get(i).getOnLineId();
                System.out.println("onLineId==>>>" + onLineId);
                System.out.println("EntryStatus==>>>" + mList.get(i).getEntryStatus());
                if (onLineId == 0 && mList.get(i).getEntryStatus() == 0) {
                    addEntrytem = addEntrytem + 1;
                    NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.SALE_MILK) + " " + mContext.getString(R.string.UploadingOnline), false) {
                        @Override
                        public void handleResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getString("status").equals("success")) {
                                    databaseHandler.updateSaleMilkEntryId("" + entryId, mainObject.getInt("id"));
                                } else {
                                    databaseHandler.deleteSaleMilkRecord(entryId + "");
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
                            productArray.put(productData);
                            System.out.println("productData==" + productData.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mainObject.put("make_array", productArray);
                        System.out.println("mainObject>>>" + mainObject.toString());
                        RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                        webServiceCaller.addRequestBody(body);
                        webServiceCaller.execute(Constant.uploadOfflineSaleEntryToServerAPI);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (onLineId > 0 && mList.get(i).entryStatus == 0) {
                    updateEntrytem = updateEntrytem + 1;
                    NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Update_Entry) + "...", true) {
                        @Override
                        public void handleResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String staus = jsonObject.getString("status");
                                if (staus.equalsIgnoreCase("success")) {
                                    databaseHandler.updateSaleMilkEntryId("" + entryId, onLineId);
                                } else {
                                    showToast(mContext, mContext.getString(R.string.Entry_Updating_Failed));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    RequestBody body = new FormEncodingBuilder()
                            .addEncoded("dairy_id", mList.get(i).getDairy_id())
                            .addEncoded("id", "" + mList.get(i).getOnLineId())
                            .addEncoded("customer_id", mList.get(i).getCustomer_id())
                            .addEncoded("entry_date", mList.get(i).getEntry_date())
                            .addEncoded("shift", mList.get(i).getShift())
                            .addEncoded("milk_category", mList.get(i).getMilkRateCategory())
                            .addEncoded("snf_fat_categories", "" + mList.get(i).getFat())
                            .addEncoded("fat", mList.get(i).getFat())
                            .addEncoded("snf", mList.get(i).getSnf())
                            .addEncoded("clr", mList.get(i).getClr())
                            .addEncoded("per_kg_price", mList.get(i).getPer_kg_price())
                            .addEncoded("total_milk", mList.get(i).getTotal_milk())
                            .addEncoded("total_bonus", mList.get(i).getTotal_bonus())
                            .addEncoded("total_price", mList.get(i).getTotal_price())
                            .build();
                    serviceCaller.addRequestBody(body);
                    serviceCaller.execute(updateSaleMilkEntryAPI);
                }
                if (i == entrySze - 1) {
                    if (addEntrytem > 0) {
                        UtilityMethod.showAlertBox(mContext, addEntrytem + " " + mContext.getString(R.string.MILK_Sale) + " " +
                                mContext.getString(R.string.Offline_Entry_Successfully_Uploaded_to_Server));
                    } else if (updateEntrytem > 0) {
                        UtilityMethod.showAlertBox(mContext, updateEntrytem + " " + mContext.getString(R.string.MILK_Sale) + " " +
                                mContext.getString(R.string.Entry_Update_Successfully));
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

    public static void uploadPlantSaleMilkEntryToServer(Context mContext, final String screenFrom) {
        SessionManager sessionManager = new SessionManager(mContext);
        DatabaseHandler databaseHandler = getDbHelper(mContext);
        ArrayList<CustomerSaleMilkEntryList> mList = databaseHandler.getPlantSaleMilkEntryRecords();
        if (!mList.isEmpty()) {

            NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.plantMilkCollection) + " " +
                    mContext.getString(R.string.UploadingOnline), false) {
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
                            productData.put("customer_id", "" + mList.get(i).getCustomer_id());
                            productData.put("dairy_id", "" + sessionManager.getValueSesion(SessionManager.KEY_PlantId));
                            productData.put("vehicle_id", "" + sessionManager.getValueSesion(SessionManager.KEY_UserID));
                            productData.put("fat", "" + mList.get(i).getFat());
                            productData.put("snf", "" + mList.get(i).getSnf());
                            productData.put("insert_date", "" + mList.get(i).getEntry_date());
                            productData.put("insert_date_in_str", "" + mList.get(i).getStr_to_time());
                            productData.put("total_bonus", "" + mList.get(i).getTotal_bonus());
                            productData.put("milk_perkg_price", "" + mList.get(i).getPer_kg_price());

                            productData.put("milk_wt", "" + mList.get(i).getTotal_milk());
                            productData.put("milk_total_price", "" + mList.get(i).getTotal_price());
                            productData.put("shift", "" + mList.get(i).getShift());
                            productData.put("milkRateCategory", "" + mList.get(i).getMilkRateCategory());
                            productData.put("snf_fat_categories", "" + mList.get(i).getFatSnfCategory());
                            productData.put("d", "" + mList.get(i).getDate_d());
                            productData.put("m", "" + mList.get(i).getMonth_m());
                            productData.put("y", "" + mList.get(i).getYear_y());
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

    public int getOnLineId() {
        return onLineId;
    }

    public void setOnLineId(int onLineId) {
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

    public Integer getFatSnfCategory() {
        return fatSnfCategory;
    }

    public void setFatSnfCategory(Integer fatSnfCategory) {
        this.fatSnfCategory = fatSnfCategory;
    }

    public Integer getSrNo() {
        return srNo;
    }

    public void setSrNo(Integer srNo) {
        this.srNo = srNo;
    }

    public Integer getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(Integer deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    public Integer getEntryStatus() {
        return entryStatus;
    }

    public void setEntryStatus(Integer entryStatus) {
        this.entryStatus = entryStatus;
    }
}
