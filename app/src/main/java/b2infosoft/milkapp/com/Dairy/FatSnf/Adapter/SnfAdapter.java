package b2infosoft.milkapp.com.Dairy.FatSnf.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.RequestBody;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.appglobal.Constant.snfFatList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;


/**
 * Created by u on 17-Mar-2020.
 */

public class SnfAdapter extends RecyclerView.Adapter<SnfAdapter.MyViewHolder> {
    public Context mContext;
    public ArrayList<SnfFatListPojo> mList;
    SessionManager sessionManager;
    String chartCatId = "", snfFatCategory = "1", chartType = "", dairyId = "";
    ArrayList<String> fatList;

    public SnfAdapter(Context mContext, ArrayList<SnfFatListPojo> snfFatListPojos, ArrayList<String> fatList) {

        if (snfFatListPojos.size() > 0) {
            Collections.sort(snfFatListPojos, new Comparator<SnfFatListPojo>() {
                @Override
                public int compare(SnfFatListPojo o1, SnfFatListPojo o2) {
                    return Float.compare(getFloatValuFromInputText(o1.Fat), getFloatValuFromInputText(o2.Fat));
                }
            });
        }
        this.mContext = mContext;
        this.mList = snfFatListPojos;
        this.fatList = fatList;
        sessionManager = new SessionManager(mContext);
        chartCatId = sessionManager.getValueSesion(SessionManager.Key_ChartId);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        chartType = sessionManager.getValueSesion(SessionManager.Key_ChartType);
        System.out.println(" Final fatList>>>" + fatList.size());
        System.out.println(" Final fatList Data>>>" + fatList.toString());
        System.out.println(" Final mList>>>" + mList.size());

        /*for (int i = 0;i < mList.size(); i++) {
            System.out.println(" Test Adapter ID==>> "+mList.get(i).getId()+"   SNF==>> "+mList.get(i).getSNF()+"  Fat==>> "+mList.get(i).getFat()+"   Rate==>>"+mList.get(i).getRate());
        }*/

        setHasStableIds(false);
    }


    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_fat_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.setIsRecyclable(false);
        SnfFatListPojo album = mList.get(position);
        String fat = fatList.get(position);
        holder.tvItem.setText(album.getFat());
        holder.tvPrice.setText(album.getRate());
        /*for (int i=0;  i<mList.size();i++) {
            if (fat.equals(mList.get(i).getFat())) {



                System.out.println(" Final Adapter SNF==>> " + album.getSNF() + "  Fat==>> " + album.getFat() + "   Rate==>>" + album.getRate());
        break;
            }
        }*/
        if (position == mList.size() - 1) {
            holder.btn_Update.setVisibility(View.VISIBLE);
        } else {
            holder.btn_Update.setVisibility(View.GONE);
        }
        holder.tvPrice.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
                System.out.println("id1====>>>" + mList.get(position).getId());
                System.out.println("SNF====>>>" + mList.get(position).getSNF());
                System.out.println("Fat====>>>" + mList.get(position).getFat());
                System.out.println("Category====>>>" + mList.get(position).getSnf_fat_category());
                System.out.println("===Rate=>>>" + s.toString());
                mList.get(position).setRate(s.toString());
                for (int i = 0; i < snfFatList.size(); i++) {
                    if (mList.get(position).getId().equals(snfFatList.get(i).getId())) {
                        System.out.println("id2====>>>" + mList.get(position).getId());
                        snfFatList.get(i).setRate(s.toString());
                        break;
                    }
                }
            }
        });


        holder.btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSNFFATValue();
            }

        });
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    public int getItemCount() {

        return mList.size();
    }

    private void updateSNFFATValue() {

        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        showToast(mContext, mContext.getString(R.string.Updating_Success));
                        getDairyAllSNF_FATChart(mContext, chartType, true);
                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            JsonObject mainObject = new JsonObject();
            JsonArray jsonFATSNFRateArray = new JsonArray();
            System.out.println("===snfFatRateList=>>>" + snfFatList.size());
            for (int i = 0; i < snfFatList.size(); i++) {

                JsonObject object = new JsonObject();
                snfFatCategory = snfFatList.get(i).getSnf_fat_category();
                object.addProperty("created_by", dairyId);
                object.addProperty("dairy_id", dairyId);
                object.addProperty("snf_fat_category", snfFatCategory);
                object.addProperty("snf", snfFatList.get(i).getSNF());
                object.addProperty("fat", snfFatList.get(i).getFat());
                object.addProperty("value", snfFatList.get(i).getRate());
                object.addProperty("categorychart_id", chartCatId);
                System.out.println("=  FAT SNF RATE=object=>>>" + object.toString());
                jsonFATSNFRateArray.add(object);
            }
            mainObject.addProperty("id", Constant.ctegory_ID);
            mainObject.addProperty("type", chartType);
            mainObject.addProperty("dairy_id", dairyId);
            mainObject.addProperty("snf_fat_category", snfFatCategory);
            mainObject.addProperty("categorychart_id", chartCatId);
            mainObject.add("make_array", jsonFATSNFRateArray);
      /*  String file=      chartType+   "_" +chartCatId + "_" + SelectedDate + System.currentTimeMillis() + ".txt";
           // createFileOnSDCard(  mContext,  "Chart", file,mainObject.toString());*/

            RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
            webServiceCaller.addRequestBody(body);
            webServiceCaller.execute(Constant.updateFatRateNewAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
