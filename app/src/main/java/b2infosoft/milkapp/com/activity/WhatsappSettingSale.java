package b2infosoft.milkapp.com.activity;

import static b2infosoft.milkapp.com.appglobal.Constant.getSmsSettingDataWhatsappsale;
import static b2infosoft.milkapp.com.appglobal.Constant.setSmsSettingData;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomListViewAdapterSale;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;

public class WhatsappSettingSale extends AppCompatActivity {
    CustomListViewAdapterSale customListViewAdapterSale;
    ListView listView;
    ArrayList<CustomerListPojo> mCustomerList;
    HashMap<Integer, String> hash_map;
    SwitchCompat switchCompat;
    SharedPrefData sharedPrefData;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_setting_sale);
        switchCompat = findViewById(R.id.whatsapOffOnWidgett);
        setToggleButton();
        listenForToggel();
        sharedPrefData = new SharedPrefData();
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(this);
        mCustomerList = databaseHandler.getCustomerListByGroupId("4");
        hash_map = new HashMap<Integer, String>();
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
        getSmsSettingData();
    }

    private void setToggleButton() {
        if (SharedPrefData.retriveDataFromPrefrence(WhatsappSettingSale.this, "WhatsappBusinnesORWhatsapp").equals("on")) {
            switchCompat.setChecked(true);
        } else {
            switchCompat.setChecked(false);
        }
    }

    private void listenForToggel() {

        switchCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompat.isChecked()) {
                    SharedPrefData.saveDataToPrefrence(WhatsappSettingSale.this, "WhatsappBusinnesORWhatsapp", "on");
                } else {
                    SharedPrefData.saveDataToPrefrence(WhatsappSettingSale.this, "WhatsappBusinnesORWhatsapp", "off");
                }

            }
        });
    }

    public void backOnWhatsapp(View view) {
        onBackPressed();
    }


    private void setCustomerList(ArrayList<CustomerListPojo> mCustomerList) {

        for (int i = 0; i < mCustomerList.size(); i++) {
            String selectedName = mCustomerList.get(i).name;
            String unic_customer = mCustomerList.get(i).unic_customer;
            String idddd = mCustomerList.get(i).id;

            Log.d("TAG", "setCustomerList1: " + idddd);
        }
    }


    public void saveDefaultMsgData(View view) {
        boolean stopMultipleClick = true;
        if (stopMultipleClick) {
            stopMultipleClick = false;
            setSmsSettingData();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean stopMultipleClick = true;
                }
            }, 1000);
        }
    }


    private void setSmsSettingData() {
        String url = setSmsSettingData;

        //    loadingPB.setVisibility(View.VISIBLE);

        // creating a new variable for our request queue

        RequestQueue queue = Volley.newRequestQueue(WhatsappSettingSale.this);

        JSONArray jRootArray = new JSONArray();

        for (int i = 0; i < mCustomerList.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", mCustomerList.get(i).id);
                Log.d("TAG", "getParams: " + mCustomerList.get(i).id);

                if (sharedPrefData.retriveDataFromPrefrence(WhatsappSettingSale.this, String.valueOf(mCustomerList.get(i).unic_customer + "sale")).equals("checkBoxOff")) {
                    jsonObject1.put("status", 0);
                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSettingSale.this, String.valueOf(mCustomerList.get(i).unic_customer + "sale")).equals("checkBoxSms")) {
                    jsonObject1.put("status", 1);
                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSettingSale.this, String.valueOf(mCustomerList.get(i).unic_customer + "sale")).equals("checkBoxWhatsapp")) {
                    jsonObject1.put("status", 2);
                } else {
                    jsonObject1.put("status", 0);
                }


                jRootArray.put(jsonObject1);

                Log.d("TAG", "ResponseSendToServer: " + jRootArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, jRootArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e("Error: ", volleyError.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // Add headers
                return headers;
            }

            //Important part to convert response to JSON Array Again
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                JSONArray array = new JSONArray();
                if (response != null) {

                    try {
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject obj = new JSONObject(responseString);
                        (array).put(obj);
                    } catch (Exception ex) {
                    }
                }
                //return array;
                return Response.success(array, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }

    private void getSmsSettingData() {
        progressDialog = new ProgressDialog(WhatsappSettingSale.this);
        progressDialog.show();
        // url to post our data
        String url = getSmsSettingDataWhatsappsale;

        //    loadingPB.setVisibility(View.VISIBLE);

        // creating a new variable for our request queue

        JSONArray jRootArray = new JSONArray();

        for (int i = 0; i < mCustomerList.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", mCustomerList.get(i).id);
                jRootArray.put(jsonObject1);
                Log.d("TAG", "mgetParams1: " + jRootArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestQueue queue = Volley.newRequestQueue(WhatsappSettingSale.this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, jRootArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.dismiss();
                Log.d("TAG", "ResponseFromGettingServer: " + response);
                getDataInArrayList();


                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = response.getJSONObject(i);
                        int id = Integer.parseInt(jsonObj.getString("id"));

                        String d = hash_map.get(id);
                        Log.d("TAG", "onResponse: " + id + "//" + d);
                        String keyStatus = jsonObj.getString("service_status");

                        if (keyStatus.equals("0")) {
                            Log.d(TAG, "onResponse99999: ");
                            sharedPrefData.saveDataToPrefrence(WhatsappSettingSale.this, d + "sale", "checkBoxOff");
                        } else if (keyStatus.equals("1")) {
                            Log.d(TAG, "onResponse88888: ");
                            sharedPrefData.saveDataToPrefrence(WhatsappSettingSale.this, d + "sale", "checkBoxSms");
                        } else if (keyStatus.equals("2")) {
                            Log.d(TAG, "onResponse77777: ");
                            sharedPrefData.saveDataToPrefrence(WhatsappSettingSale.this, d + "sale", "checkBoxWhatsapp");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                listView = findViewById(R.id.lv);
                customListViewAdapterSale = new CustomListViewAdapterSale(WhatsappSettingSale.this, mCustomerList);
                listView.setAdapter(customListViewAdapterSale);

//                recyclerView = findViewById(R.id.defaultSmsSend);
//                recyclerView.setLayoutManager(new LinearLayoutManager(WhatsappSetting.this));
//                adapter = new SmsAdapter(WhatsappSetting.this, mCustomerList);
//                adapter.setClickListener(WhatsappSetting.this);
//                recyclerView.setAdapter(adapter);

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("TAG", "onErrorResponse: " + error.toString());
                Toast.makeText(WhatsappSettingSale.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void getDataInArrayList() {

        for (int i = 0; i < mCustomerList.size(); i++) {
            Log.d("TAG", "onResponse7777: " + mCustomerList.get(i).unic_customer);
            Log.d("TAG", "onResponse8888: " + mCustomerList.get(i).id);
            hash_map.put(Integer.valueOf(mCustomerList.get(i).id), mCustomerList.get(i).unic_customer);
        }

    }


}