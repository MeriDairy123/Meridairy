package b2infosoft.milkapp.com.Model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;

/**
 * Created by Choudhary on 11-Dec-19.
 */

public class BeanDairySnfFatChart {
    public String id = "";
    public String SNF = "";
    public String Fat = "";
    public String Rate = "";
    public String snf_fat_category = "";
    public String categorychart_id = "";


    public BeanDairySnfFatChart(String id, String SNF, String fat, String rate,
                                String snf_fat_category, String categorychart_id) {
        this.id = id;
        this.SNF = SNF;
        Fat = fat;
        Rate = rate;
        this.snf_fat_category = snf_fat_category;
        this.categorychart_id = categorychart_id;
    }


    public static void getDairyAllSNF_FATChart(Context mContext, String type, boolean isDialog) {
        System.out.println("Chart of >>>" + type);
        SessionManager sessionManager = new SessionManager(mContext);
        ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Downloading chart It will take time So Please wait....");
        if (isDialog) {
            dialog.show();
        }
        DatabaseHandler db = DatabaseHandler.getDbHelper(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_wait_Updating_Your_SNF_Chart), isDialog) {
            @Override
            public void handleResponse(String response) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (isDialog) {
                                dialog.show();
                            }
                            // Database Object
                            JSONObject jobj = new JSONObject(response);
                            if (jobj.getString("status").equalsIgnoreCase("success")) {
                                ArrayList<BeanDairySnfFatChart> mList = new ArrayList<>();
                                JSONArray jsonArrayData = jobj.getJSONArray("data");
                                if (type.equalsIgnoreCase("buy")) {
                                    db.deleteBuyFatSnfChartTable();
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject object = jsonArrayData.getJSONObject(i);
                                        //    System.out.println("Buy Chart object>>>" + object.toString());
                                        mList.add(new BeanDairySnfFatChart(object.getString("id"),
                                                object.getString("snf"), object.getString("fat"),
                                                object.getString("value"), object.getString("snf_fat_category"),
                                                object.getString("categorychart_id")));
                                    }
                                    db.addDairyBuyChart(mList, type);

                                } else {
                                    db.deleteSaleMilkSNFChartTable();
                                    for (int i = 0; i < jsonArrayData.length(); i++) {
                                        JSONObject object = jsonArrayData.getJSONObject(i);
                                        // System.out.println("Sale Chart object>>>" + object.toString());
                                        mList.add(new BeanDairySnfFatChart(object.getString("id"),
                                                object.getString("snf"), object.getString("fat"),
                                                object.getString("value"), object.getString("snf_fat_category"),
                                                object.getString("categorychart_id")));

                                    }
                                    db.addDairySaleChart(mList, type);

                                }

                            }
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }

                    }
                });
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("type", type)
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(Constant.getDairyAllChartDataAPI);
    }

    public static void updateDairySNF_FATChart(Context mContext, String type, boolean isDialog) {
        System.out.println("Chart of >>>" + type);
        SessionManager sessionManager = new SessionManager(mContext);

        DatabaseHandler db = DatabaseHandler.getDbHelper(mContext);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_wait_Updating_Your_SNF_Chart), isDialog) {
            @Override
            public void handleResponse(String response) {

                try {

                    JSONObject jobj = new JSONObject(response);
                    if (jobj.getString("status").equalsIgnoreCase("success")) {
                        ArrayList<BeanDairySnfFatChart> mList = new ArrayList<>();
                        JSONArray jsonArrayData = jobj.getJSONArray("data");
                        if (type.equalsIgnoreCase("buy")) {
                            db.deleteBuyFatSnfChartTable();
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                JSONObject object = jsonArrayData.getJSONObject(i);
                                mList.add(new BeanDairySnfFatChart(object.getString("id"),
                                        object.getString("snf"), object.getString("fat"),
                                        object.getString("value"), object.getString("snf_fat_category"),
                                        object.getString("categorychart_id")));
                            }
                            db.addDairyBuyChart(mList, type);

                        } else {
                            db.deleteSaleMilkSNFChartTable();
                            for (int i = 0; i < jsonArrayData.length(); i++) {
                                JSONObject object = jsonArrayData.getJSONObject(i);
                                mList.add(new BeanDairySnfFatChart(object.getString("id"),
                                        object.getString("snf"), object.getString("fat"),
                                        object.getString("value"), object.getString("snf_fat_category"),
                                        object.getString("categorychart_id")));
                            }
                            db.addDairySaleChart(mList, type);

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .addEncoded("type", type)
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(Constant.getDairyAllChartDataAPI);
    }


    static class saveFATSNFRateListInDB extends AsyncTask<String, Void, Void> {

        private final Context context;
        DatabaseHandler db;
        JSONArray jsonArrayData;
        ProgressDialog dialog;

        public saveFATSNFRateListInDB(Context c, DatabaseHandler databaseHandler, JSONArray jsonArray) {
            this.context = c;
            this.db = databaseHandler;
            this.jsonArrayData = jsonArray;


        }

        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Please Wait....");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Database Object
                    try {

                        dialog.dismiss();

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();

                    }
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

}
