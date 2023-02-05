package b2infosoft.milkapp.com.customer_app.customer_adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.customer_pojo.DairyNamePojo;
import b2infosoft.milkapp.com.customer_app.customer_pojo.MonthsEntryListPojo;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

public class DairyNameWithEntryAdapter extends RecyclerView.Adapter<DairyNameWithEntryAdapter.MyViewHolder> {
    public Context mContext;
   // public ArrayList<DairyNamePojo> mList = new ArrayList<>();
    int mDay, month, mYear, mMaxDay;
    String fromdate ,todate;
    RecyclerView recyclerView;
    String customer_id,id;
    private int expandedPosition = -1;

    public DairyNameWithEntryAdapter(Context mContext, String customer_id,String id, String fromdate,String todate, RecyclerView recyclerView) {
        this.mContext = mContext;
       // this.mList = DairyNameListPojos;
        this.customer_id = customer_id;
        this.id = id;
        this.fromdate = fromdate;
        this.todate = todate;
        this.month = month;
        this.mYear = mYear;
        this.recyclerView = recyclerView;
     //  mMaxDay = maxDay;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dairy_name_ten_days_entry_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
       /* holder.btnDairyName.setText(mList.get(position).dairy_name);
        if (mList.get(position).visibility == true) {
            holder.recyclerLayout.setVisibility(View.VISIBLE);
            holder.layoutHeader.setVisibility(View.VISIBLE);
            holder.bottom2.setVisibility(View.VISIBLE);
        }
        else {
            holder.recyclerLayout.setVisibility(View.GONE);
            holder.layoutHeader.setVisibility(View.GONE);
            holder.bottom2.setVisibility(View.GONE);
        }
        holder.btnDairyName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mList.get(position).visibility == false) {
                    setEntryList(position);
                    mList.get(position).visibility = true;
                } else {
                    mList.get(position).visibility = false;
                    holder.recyclerLayout.setVisibility(View.GONE);
                    holder.layoutHeader.setVisibility(View.GONE);
                    holder.bottom2.setVisibility(View.GONE);
                }

            }
        });*/



                setEntryList(holder);


    }

    private void setEntryList(MyViewHolder holder) {

        final ArrayList<MonthsEntryListPojo> entryListPojos = new ArrayList<>();
        @SuppressLint("StaticFieldLeak")
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        @SuppressLint("StaticFieldLeak") JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        JSONArray morningArray = jsonObject1.getJSONArray("morning");
                        if (morningArray.length() != 0) {
                            for (int j = 0; j < morningArray.length(); j++) {
                                JSONObject jsonObj = morningArray.getJSONObject(j);
                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));

                            }
                        } else {
                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
                                    , "", "", ""
                                    , "", "", ""
                                    , "", "", "", "morning"));

                        }

                        JSONArray eveningArray = jsonObject1.getJSONArray("evening");
                        if (eveningArray.length() != 0) {
                            for (int j = 0; j < eveningArray.length(); j++) {
                                JSONObject jsonObj = eveningArray.getJSONObject(j);
                                entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), jsonObj.getString("id")
                                        , jsonObj.getString("customer_id"), jsonObj.getString("dairy_id"), jsonObj.getString("fat"), jsonObj.getString("snf")
                                        , jsonObj.getString("entry_date"), jsonObj.getString("per_kg_price"), jsonObj.getString("total_price")
                                        , jsonObj.getString("total_bonus"), jsonObj.getString("total_milk"), jsonObj.getString("shift")));

                            }
                        } else {
                            entryListPojos.add(new MonthsEntryListPojo(jsonObject1.getString("entry_date"), ""
                                    , "", "", "", "", ""
                                    , "", "", ""
                                    , "", "evening"));
                        }
                    }

                    if (!entryListPojos.isEmpty()) {
                        holder.bottom2.setVisibility(View.VISIBLE);
                        double totalWeight = 0d, totalPrice = 0d;
                        ArrayList<MonthsEntryListPojo> newEntryList = new ArrayList<>();

                        for (int i = 0; i < entryListPojos.size(); i++) {
                            if (!entryListPojos.get(i).total_milk.equals("")) {
                                totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
                            }
                            if (!entryListPojos.get(i).total_price.equals("")) {
                                totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
                            }
                            newEntryList.add(new MonthsEntryListPojo(
                                    entryListPojos.get(i).entry_date, entryListPojos.get(i).id,
                                    entryListPojos.get(i).customer_id, entryListPojos.get(i).dairy_id,
                                    entryListPojos.get(i).fat,entryListPojos.get(i).snf, entryListPojos.get(i).entry_date2,
                                    entryListPojos.get(i).per_kg_price, entryListPojos.get(i).total_price,
                                    entryListPojos.get(i).total_bonus, entryListPojos.get(i).total_milk, entryListPojos.get(i).shift
                            ));
                        }

//                        if (mDay >= 1 && mDay <= 10) {
//                            System.out.println("Days Data" + "getTenDaysData: " + "b/w 1-10");
//
//                            //  int size = mMaxDay * 2;
//                            for (int i = 0; i < 20; i++) {
//                                if (!entryListPojos.get(i).total_milk.equals("")) {
//                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
//                                }
//                                if (!entryListPojos.get(i).total_price.equals("")) {
//                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
//                                }
//                                newEntryList.add(new MonthsEntryListPojo(
//                                        entryListPojos.get(i).entry_date, entryListPojos.get(i).id,
//                                        entryListPojos.get(i).customer_id, entryListPojos.get(i).dairy_id,
//                                        entryListPojos.get(i).fat,entryListPojos.get(i).snf, entryListPojos.get(i).entry_date2,
//                                        entryListPojos.get(i).per_kg_price, entryListPojos.get(i).total_price,
//                                        entryListPojos.get(i).total_bonus, entryListPojos.get(i).total_milk, entryListPojos.get(i).shift
//                                ));
//                            }
//
//                        }
//                        else if (mDay >= 11 && mDay <= 20) {
//
//                            System.out.println("==getTenDaysData: " + "b/w 11-20");
//
//                            for (int i = 21; i < 40; i++) {
//                                if (!entryListPojos.get(i).total_milk.equals("")) {
//                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
//                                }
//                                if (!entryListPojos.get(i).total_price.equals("")) {
//                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
//                                }
//                                newEntryList.add(new MonthsEntryListPojo(
//                                        entryListPojos.get(i).entry_date, entryListPojos.get(i).id, entryListPojos.get(i).customer_id,
//                                        entryListPojos.get(i).dairy_id, entryListPojos.get(i).fat,entryListPojos.get(i).snf, entryListPojos.get(i).entry_date2,
//                                        entryListPojos.get(i).per_kg_price, entryListPojos.get(i).total_price, entryListPojos.get(i).total_bonus, entryListPojos.get(i).total_milk,
//                                        entryListPojos.get(i).shift
//                                ));
//
//                            }
//
//
//                        }
//                        else if (mDay >= 21 && mDay <= mMaxDay) {
//
//
//                            for (int i = 41; i < entryListPojos.size(); i++) {
//                                if (!entryListPojos.get(i).total_milk.equals("")) {
//                                    totalWeight = totalWeight + Double.parseDouble(entryListPojos.get(i).total_milk);
//                                }
//                                if (!entryListPojos.get(i).total_price.equals("")) {
//                                    totalPrice = totalPrice + Double.parseDouble(entryListPojos.get(i).total_price);
//                                }
//                                newEntryList.add(new MonthsEntryListPojo(
//                                        entryListPojos.get(i).entry_date, entryListPojos.get(i).id, entryListPojos.get(i).customer_id,
//                                        entryListPojos.get(i).dairy_id, entryListPojos.get(i).fat,entryListPojos.get(i).snf, entryListPojos.get(i).entry_date2,
//                                        entryListPojos.get(i).per_kg_price, entryListPojos.get(i).total_price,
//                                        entryListPojos.get(i).total_bonus,entryListPojos.get(i).total_milk, entryListPojos.get(i).shift
//                                ));
//                            }
//                        }


                      //  mList.get(position).visibility = true;
//                        holder.recyclerLayout.setVisibility(View.VISIBLE);
//                        holder.layoutHeader.setVisibility(View.VISIBLE);
//                        holder.bottom2.setVisibility(View.VISIBLE);

                        holder.tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + ": \n" + String.format("%.2f", totalWeight) + " " + mContext.getString(R.string.Ltr));
                        holder.tvTotalAmt.setText(mContext.getString(R.string.Total_Amount) + ": \n" + String.format("%.2f", totalPrice) + " " + mContext.getString(R.string.Rs));
                        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(mContext, 1);
                        MonthlyEntryListAdapter monthsListAdapter = new MonthlyEntryListAdapter(mContext, newEntryList);
                        holder.recycler_tenDaysEntry.setLayoutManager(mLayoutManager);
                        holder.recycler_tenDaysEntry.setAdapter(monthsListAdapter);
                        monthsListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        SessionManager sessionManager = new SessionManager(mContext);
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("customer_id", this.customer_id)
                .addEncoded("month", "" + fromdate)
                .addEncoded("year", "" + todate)
                .addEncoded("dairy_id", this.id)
                .addEncoded("user_group_id", sessionManager.getValueSesion(SessionManager.Key_UserGroupID))
                .addEncoded("phone_number", sessionManager.getValueSesion(SessionManager.KEY_Mobile))
                .build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMonthMilkEntry);


    }


    public int getItemCount() {
        return 1;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTotalWeight, tvTotalAmt;
        //Button btnDairyName;
        RecyclerView recycler_tenDaysEntry;
        LinearLayout bottom2, layoutHeader, recyclerLayout;


        public MyViewHolder(View view) {
            super(view);
            tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
            bottom2 = view.findViewById(R.id.bottom2);
            layoutHeader = view.findViewById(R.id.layoutHeader);
            recyclerLayout = view.findViewById(R.id.recyclerLayout);
            tvTotalAmt = view.findViewById(R.id.tvTotalAmt);
           // btnDairyName = view.findViewById(R.id.btnDairyName);
            recycler_tenDaysEntry = view.findViewById(R.id.recycler_tenDaysEntry);


        }


    }
}
