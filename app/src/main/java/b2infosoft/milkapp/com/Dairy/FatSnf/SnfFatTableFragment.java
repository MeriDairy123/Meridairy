package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FATListData;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFListData;

public class SnfFatTableFragment extends Fragment {

    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;

    ArrayList<SnfFatListPojo> mList = new ArrayList<>();
    ArrayList<SnfFatListPojo> snfFatListPojos = new ArrayList<>();
    ArrayList<String> mSnfList = new ArrayList<>();
    ArrayList<String> mFatList = new ArrayList<>();
    TableLayout table;
    double basePrice = 21.09, baseSNF = 0, lastSNF = 0, snfDef = 0.15, snfPreviousPrice = 0,
            baseFAT = 0, lastFAT = 0, fatDef = 0.20, fatPreviousPrice = 0;
    ProgressDialog progressDialog;
    Fragment fragment = null;
    Bundle bundle = null;

    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_snf_fat_table, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        table = view.findViewById(R.id.table_excel);

        mList = new ArrayList<>();
        mSnfList = new ArrayList<>();
        mFatList = new ArrayList<>();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.Please_Wait));

        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        if (Constant.FromWhere.equals("Cow")) {
            toolbar_title.setText(mContext.getString(R.string.SNF_FAT_CHART) + " " + mContext.getString(R.string.Cow));
        } else {
            toolbar_title.setText(mContext.getString(R.string.SNF_FAT_CHART) + " " + mContext.getString(R.string.Buff));
        }


        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

            setListData();
        return view;
    }

    @SuppressLint("ResourceType")

    public void setList(ArrayList<SnfFatListPojo> snfFatList, ArrayList<String> snfList,
                        ArrayList<String> fatList) {

        snfList = sessionManager.getFATSNFListData(mContext, Key_SNFListData);
        fatList = sessionManager.getFATSNFListData(mContext, Key_FATListData);
        snfFatListPojos = sessionManager.getRateChartList(mContext);
        progressDialog.show();
        mList.add(new SnfFatListPojo("", "FAT/SNF", "FAT/SNF", "", ""));
        mList.addAll(snfFatList);
        mSnfList.add(" FAT/SNF ");
        mSnfList.addAll(snfList);

        for (int i = 0; i < snfList.size(); i++) {
            String key = snfList.get(i);
        }
        Constant.snfFatList = snfFatList;

        showTableLayout();
    }

    public void setListData() {
        progressDialog.show();
        snfFatListPojos = new ArrayList<>();
        ArrayList<String> snfList = sessionManager.getFATSNFListData(mContext, Key_SNFListData);
        ArrayList<String> fatList = sessionManager.getFATSNFListData(mContext, Key_FATListData);
        snfFatListPojos = sessionManager.getRateChartList(mContext);

        Constant.snfFatList = new ArrayList<>();
        Constant.snfFatList.addAll(snfFatListPojos);
        mFatList.add(" FAT/SNF ");
        mFatList.addAll(fatList);
        mSnfList.add(" FAT/SNF ");
        mSnfList.addAll(snfList);

        mList.add(new SnfFatListPojo("", "FAT/SNF", "FAT/SNF", "", ""));
        mList.addAll(snfFatListPojos);
        Constant.snfFatList = snfFatListPojos;

        showTableLayout();
    }

    @SuppressLint("ResourceType")
    public void setChart() {

        basePrice = 21.09;
        baseSNF = 7.5;
        lastSNF = 9.0;
        snfDef = 0.18;
        snfPreviousPrice = 0;
        baseFAT = 2.9;
        lastFAT = 10.0;
        fatDef = 0.27;
        fatPreviousPrice = 0;


        double rows = lastFAT;
        double colums = lastSNF;
        System.out.println("=== mFatList.size()====" + rows);
        System.out.println("=== mSnfList.size()====" + colums);
        String fat = "";
        String snf = "";
        String rate = "";
        DecimalFormat df = new DecimalFormat("#.0");

        fatPreviousPrice = basePrice + fatDef;
        for (double i = baseFAT; i <= rows; i = i + 0.1) {


            TableRow tr = new TableRow(mContext);
            tr.setId(10);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            // Cell
            TextView tvCell;
            tvCell = new TextView(mContext);
            tvCell.setTextSize(14);
            tvCell.setTextColor(Color.WHITE);
            tvCell.setGravity(Gravity.CENTER);
            tvCell.setPadding(5, 5, 5, 5);
            tvCell.setTextSize(14);
            tvCell.setTypeface(Typeface.DEFAULT_BOLD);
            tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_primary_color));
            for (double j = baseSNF; j <= colums; j = j + 0.1) {

                System.out.println("Fat===>>>" + i);
                System.out.println("SNf===>>>" + j);
                df = new DecimalFormat("#.0");
                fat = df.format(i);
                snf = df.format(j);

                tvCell = new TextView(mContext);
                tvCell.setTextSize(14);
                tvCell.setTextColor(Color.WHITE);
                tvCell.setGravity(Gravity.CENTER);
                tvCell.setPadding(5, 5, 5, 5);
                if (i == baseFAT && j == baseSNF) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_blue));
                    tvCell.setText(" FAT/SNF ");
                } else if (i == baseFAT && j < colums) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_blue));
                    tvCell.setText("  " + snf + " ");
                } else if (j == baseSNF && i < rows) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_primary_color));
                    tvCell.setText("  " + fat + " ");
                } else {

                    if (i == baseFAT + 0.1 && j == baseSNF + 0.1) {
                        fatPreviousPrice = basePrice;
                        snfPreviousPrice = fatPreviousPrice;
                    } else if (j == baseSNF + 0.1 && i < rows) {
                        fatPreviousPrice = fatPreviousPrice + fatDef;
                        snfPreviousPrice = fatPreviousPrice;
                    } else {
                        snfPreviousPrice = snfPreviousPrice + snfDef;
                    }


                    tvCell.setTypeface(Typeface.DEFAULT);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape));
                    df = new DecimalFormat("#.00");
                    rate = df.format(snfPreviousPrice);

                    tvCell.setText("  " + rate + " ");
                }

                tr.addView(tvCell);
            }
            table.addView(tr);
        }
        progressDialog.dismiss();
    }

    @SuppressLint("ResourceType")
    public void showTableLayout() {

        int rows = mFatList.size();
        int colums = mSnfList.size();
        System.out.println("=== mFatList.size()====" + rows);
        System.out.println("=== mSnfList.size()====" + colums);
        String fat = "";
        String snf = "";
        String rate = "";
        for (int i = 0; i < rows; i++) {
            fat = mFatList.get(i);

            TableRow tr = new TableRow(mContext);
            tr.setId(10);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            // Cell
            TextView tvCell;
            tvCell = new TextView(mContext);
            tvCell.setTextSize(14);
            tvCell.setTextColor(Color.WHITE);
            tvCell.setGravity(Gravity.CENTER);
            tvCell.setPadding(5, 5, 5, 5);
            tvCell.setTextSize(14);
            tvCell.setTypeface(Typeface.DEFAULT_BOLD);
            tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_primary_color));
            for (int j = 0; j < colums; j++) {
                snf = mSnfList.get(j);
                System.out.println("Fat===>>>" + mFatList.get(i));
                System.out.println("SNf===>>>" + mFatList.get(i));
                System.out.println("mList=Fat==>>>" + mList.get(j).Fat);
                System.out.println("mList==SNF=>>>" + mList.get(j).SNF);
                System.out.println("mList==Rate=>>>" + mList.get(j).Rate);

                tvCell = new TextView(mContext);
                tvCell.setTextSize(14);
                tvCell.setTextColor(Color.WHITE);
                tvCell.setGravity(Gravity.CENTER);
                tvCell.setPadding(5, 5, 5, 5);
                if (i == 0 && j < colums) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_blue));
                    tvCell.setText("  " + mSnfList.get(j) + " ");
                } else if (j == 0 && i < rows) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_primary_color));
                    tvCell.setText("  " + mFatList.get(i) + " ");
                } else {
                    tvCell.setTypeface(Typeface.DEFAULT);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape));
                    for (int k = 1; k < mList.size(); k++) {
                        String mfat = mList.get(k).Fat;
                        String msnf = mList.get(k).SNF;
                        rate = mList.get(k).Rate;
                        if (mfat.equalsIgnoreCase(fat) && msnf.equalsIgnoreCase(snf)) {
                            tvCell.setText("  " + rate + " ");
                        }
                    }
                }

                tr.addView(tvCell);
            }
            table.addView(tr);
        }
        progressDialog.dismiss();
    }


    public void getSnfFatTable(final Context mContext, String dairy_id, String id, final String type) {

        final ArrayList<SnfFatListPojo> mList = new ArrayList<>();
        final ArrayList<String> snfList = new ArrayList<>();
        final ArrayList<String> fatList = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObj = new JSONObject(response);
                    JSONArray mainJsonArray = mainObj.getJSONArray("mainData");
                    JSONObject snf_fat_array = mainObj.getJSONObject("snf_fat_array");
                    JSONArray fat_array = snf_fat_array.getJSONArray("fat_array");
                    JSONArray snf_array = snf_fat_array.getJSONArray("snf_array");

                    System.out.println("FAT_list===>>>" + fat_array.toString());
                    System.out.println("SNF_list===>>>" + snf_array.toString());

                    mFatList.add(" FAT/SNF ");
                    for (int fat = 0; fat < fat_array.length(); fat++) {
                        mFatList.add(fat_array.getString(fat).trim());
                    }

                    for (int j = 0; j < snf_array.length(); j++) {
                        // System.out.println("SNF_list===>>>"+ snf_array.getString(j));
                        snfList.add(snf_array.getString(j).trim());
                        String snf = snf_array.getString(j).trim();

                        for (int k = 0; k < fat_array.length(); k++) {
                            fatList.add(fat_array.getString(k).trim());
                            String fat = fat_array.getString(k).trim();
                            String value = "", snf_fat_cate = "", id = "";
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                if (object.getString("snf").trim().equals(snf.trim())) {
                                    if (object.getString("fat").trim().equals(fat)) {
                                        value = object.getString("value");
                                        snf_fat_cate = object.getString("snf_fat_category");
                                        id = object.getString("id");
                                        break;
                                    } else {
                                        value = "";
                                        snf_fat_cate = "";
                                        id = "";
                                        continue;
                                    }
                                }
                            }

                            mList.add(new SnfFatListPojo(id,
                                    snf, fat, value, snf_fat_cate));
                        }
                    }
                    System.out.println("mList====" + mList.size());
                    setList(mList, snfList, fatList);

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

}
