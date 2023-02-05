package b2infosoft.milkapp.com.Dairy.FatSnf.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import b2infosoft.milkapp.com.Model.CowBuffaloSNFPojo;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

/**
 * Created by u on 18-Dec-17.
 */

public class SnfAdapter2 extends RecyclerView.Adapter<SnfAdapter2.MyViewHolder> {
    public Context mContext;
    public ArrayList<SnfFatListPojo> mList = new ArrayList<>();
    SessionManager sessionManager;

    public SnfAdapter2(Context mContext, ArrayList<SnfFatListPojo> customerListPojos) {
        this.mContext = mContext;
        this.mList = customerListPojos;
        sessionManager = new SessionManager(mContext);
    }

    public static void getSnfFatList(final Context mContext, String dairy_id, String id, final String type) {
        final ArrayList<SnfFatListPojo> mList = new ArrayList<>();
        final ArrayList<String> snfList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject mainObj = new JSONObject(response);
                    JSONArray mainJsonArray = mainObj.getJSONArray("mainData");
                    JSONObject snf_fat_array = mainObj.getJSONObject("snf_fat_array");
                    JSONArray snf_array = snf_fat_array.getJSONArray("snf_array");
                    for (int j = 0; j < snf_array.length(); j++) {
                        Log.d("SNF_list>>>", snf_array.getString(j));
                        snfList.add(snf_array.getString(j));
                    }

                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        mList.add(new SnfFatListPojo(object.getString("id"),
                                object.getString("snf"),
                                object.getString("fat"),
                                object.getString("value"),
                                object.getString("snf_fat_category")));
                    }
                    if (type.equals("Cow")) {
                        SessionManager sessionManager = new SessionManager(mContext);
                        sessionManager.clearArrayList(mContext, "cow");
                        sessionManager.saveCowList(mContext, mList);
                        //Constant.CowFatRateList = morningList;
                    } else if (type.equals("Buffalo")) {
                        SessionManager sessionManager = new SessionManager(mContext);
                        sessionManager.clearArrayList(mContext, "buffalo");
                        sessionManager.saveBuffaloList(mContext, mList);
                        //    Constant.BuffaloFatRateList = morningList;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("id", id).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getSnfFatListAPI);
    }

    private static String removeLastChar(String str) {
        return str.substring(0, str.length() - 1);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_fat_list_row2, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        holder.tvItem.setText(mList.get(position).Fat);
        holder.tvPrice.setText(mList.get(position).Rate);
        if (position == mList.size() - 1) {
            holder.btn_Update.setVisibility(View.VISIBLE);
        } else {
            holder.btn_Update.setVisibility(View.GONE);
        }


        holder.tvPrice.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                mList.get(position).Rate = s.toString();
                for (int i = 0; i < Constant.snfFatList.size(); i++) {
                    if (mList.get(position).id.equals(Constant.snfFatList.get(i).id)) {
                        Log.d("id2>>>", mList.get(position).id);
                        Constant.snfFatList.get(i).Rate = s.toString();
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

        holder.btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                    @Override
                    public void handleResponse(String response) {
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            if (mainObject.getString("status").equals("success")) {
                                getSNF_FATChart();
                                Toast.makeText(mContext, "Updating Success", Toast.LENGTH_SHORT).show();
                            } else {
                                UtilityMethod.showAlertBox(mContext, "Updating Fail");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                try {

                    JSONObject mainObject = new JSONObject();
                    Log.d("FromWhere>>>", Constant.FromWhere);
                    for (int i = 0; i < Constant.CowBuffelloSNF_List.size(); i++) {
                        JSONObject fat_snf_listObj = new JSONObject();
                        if (Constant.CowBuffelloSNF_List.get(i).ctegory_name.equals(Constant.FromWhere)) {
                            List<String> result = Arrays.asList(Constant.CowBuffelloSNF_List.get(i).snf_list.split("\\s*,\\s*"));
                            Collections.reverse(result);
                            JSONObject snfObj = new JSONObject();
                            for (int k = 0; k < result.size(); k++) {
                                JSONObject fatRateObj = new JSONObject();
                                //    Log.d("result>>>>", result.get(k));
                                String in = "No";
                                for (int j = 0; j < Constant.snfFatList.size(); j++) {
                                    if (result.get(k).equals(Constant.snfFatList.get(j).SNF)) {
                                        in = "Yes";
                                        Log.d("snfList>>>>", Constant.snfFatList.get(j).SNF);
                                        fatRateObj.put(Constant.snfFatList.get(j).Fat + "", "" + Constant.snfFatList.get(j).Rate);
                                    }
                                }
                                if (in.equals("Yes")) {
                                    in = "No";
                                    snfObj.put("" + result.get(k), "" + fatRateObj);
                                }
                                //  Log.d("snfObj>>>", snfObj.toString());
                            }
                            //fat_snf_listObj.put("fat_snf_list", snfObj);
                            mainObject.put("id", Constant.ctegory_ID);
                            mainObject.put("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
                            mainObject.put("fat_snf_list", snfObj);
                            Log.d("mainObj>>>", mainObject.toString());
                        }
                    }

                    RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                    caller.addRequestBody(body);

                    caller.execute(Constant.updateFatRateNewAPI);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getItemCount() {
        return mList.size();
    }

    private void getSNF_FATChart() {
        final ArrayList<CowBuffaloSNFPojo> snfFatList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating Your SNF Chart", true) {
            @Override
            public void handleResponse(String response) {
                Log.d("Response>>>>", response);
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        snfFatList.add(new CowBuffaloSNFPojo(object.getString("id"),
                                object.getString("snf_list"),
                                object.getString("fat_list"),
                                object.getString("snf_fat_category"),
                                object.getString("dairy_id"),
                                object.getString("created_by"),
                                object.getString("ctegory_name")));
                    }
                    Log.d("snfFatList>>>", "" + snfFatList.size());
                    if (!snfFatList.isEmpty()) {
                        sessionManager.clearArrayList(mContext, "snf");
                        sessionManager.saveSNFList(mContext, snfFatList);
                        // Constant.CowBuffelloSNF_List = snfFatList;
                        getSnfFatList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), snfFatList.get(0).id, "Buffalo");
                        getSnfFatList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), snfFatList.get(1).id, "Cow");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getSnfFatChartAPI);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvItem;
        EditText tvPrice;
        Button btn_Update;

        public MyViewHolder(View view) {
            super(view);
            tvPrice = view.findViewById(R.id.tvPrice);
            tvItem = view.findViewById(R.id.tvItem);
            btn_Update = view.findViewById(R.id.btn_Update);


        }
    }
}
