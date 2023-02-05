package b2infosoft.milkapp.com.activity;

import static b2infosoft.milkapp.com.appglobal.Constant.getSmsSettingData;
import static b2infosoft.milkapp.com.appglobal.Constant.setsmssettingdata;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import b2infosoft.milkapp.com.customer_app.customer_adapters.CustomListViewAdapter;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;

public class WhatsappSetting extends AppCompatActivity {
    CustomListViewAdapter customListViewAdapter;
    ListView listView;
    ArrayList<CustomerListPojo> mCustomerList;
    HashMap<Integer, String> hash_map;
    SwitchCompat switchCompat;
    SharedPrefData sharedPrefData;
    ProgressDialog progressDialog;
    public static RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp_setting);
        switchCompat = findViewById(R.id.whatsapOffOnWidget);
        setToggleButton();
        listenForToggel();
        sharedPrefData = new SharedPrefData();
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(this);
        mCustomerList = databaseHandler.getCustomerListByGroupId("3");
        hash_map = new HashMap<Integer, String>();
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
        getSmsSettingData();
    }


    private void setToggleButton() {
        if (SharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, "WhatsappBusinnesORWhatsapp").equals("on")) {
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
                    SharedPrefData.saveDataToPrefrence(WhatsappSetting.this, "WhatsappBusinnesORWhatsapp", "on");
                } else {
                    SharedPrefData.saveDataToPrefrence(WhatsappSetting.this, "WhatsappBusinnesORWhatsapp", "off");
                }

            }
        });
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


    public void backOnWhatsapp(View view) {
        onBackPressed();
    }

    private void setSmsSettingData() {


        //    loadingPB.setVisibility(View.VISIBLE);

        // creating a new variable for our request queue

        RequestQueue queue = Volley.newRequestQueue(WhatsappSetting.this);

        JSONArray jRootArray = new JSONArray();

        for (int i = 0; i < mCustomerList.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", mCustomerList.get(i).id);
                Log.d("TAG", "getParams: " + mCustomerList.get(i).id);

                if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxOff")) {
                    jsonObject1.put("status", 0);
                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxSms")) {
                    jsonObject1.put("status", 1);

                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxWhatsapp")) {
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


        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, setsmssettingdata, jRootArray, new Response.Listener<JSONArray>() {
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


    @Override
    public void onBackPressed() {
//     super.onBackPressed();
        finish();
    }

    private void getSmsSettingData() {
        progressDialog = new ProgressDialog(WhatsappSetting.this);
        progressDialog.show();
        // url to post our data
        String url = getSmsSettingData;

        //    loadingPB.setVisibility(View.VISIBLE);

        // creating a new variable for our request queue

        JSONArray jRootArray = new JSONArray();

        for (int i = 0; i < mCustomerList.size(); i++) {
            try {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("id", mCustomerList.get(i).id);
                jRootArray.put(jsonObject1);

                Log.d("TAG", "getParams1: " + jRootArray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestQueue queue = Volley.newRequestQueue(WhatsappSetting.this);
        Log.d("TAG", "11getSmsSettingData: " + jRootArray);

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
                            sharedPrefData.saveDataToPrefrence(WhatsappSetting.this, d, "checkBoxOff");
                        } else if (keyStatus.equals("1")) {
                            sharedPrefData.saveDataToPrefrence(WhatsappSetting.this, d, "checkBoxSms");
                        } else if (keyStatus.equals("2")) {
                            sharedPrefData.saveDataToPrefrence(WhatsappSetting.this, d, "checkBoxWhatsapp");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                listView = findViewById(R.id.lv);
                customListViewAdapter = new CustomListViewAdapter(WhatsappSetting.this, mCustomerList);
                listView.setAdapter(customListViewAdapter);

//                recyclerView = findViewById(R.id.defaultSmsSend);
//                recyclerView.setLayoutManager(new LinearLayoutManager(WhatsappSetting.this));
//                adapter = new SmsAdapter(WhatsappSetting.this, mCustomerList);
//                adapter.setClickListener(WhatsappSetting.this);
//                recyclerView.setAdapter(adapter);

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("TAG", "onErrorResponse: " + error.toString());
                Toast.makeText(WhatsappSetting.this, "error", Toast.LENGTH_SHORT).show();
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

    public static int seeItemsOnScreenFirstPostion() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        return firstVisiblePosition;
    }

    public static int seeItemsOnScreenlastPostion() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
        return lastVisiblePosition;
    }


}