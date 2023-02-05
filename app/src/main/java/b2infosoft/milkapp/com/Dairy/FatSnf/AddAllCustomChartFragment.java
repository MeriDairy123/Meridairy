package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Dairy.FatSnf.Adapter.FatSnf_Step_Item_adapter;
import b2infosoft.milkapp.com.Interface.UpdateList;
import b2infosoft.milkapp.com.Model.RateCardStep;
import b2infosoft.milkapp.com.Model.SnfFatListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DecimalDigitsInputFilter;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddAllCustomChartFragment extends Fragment implements UpdateList {

    Context mContext;
    Toolbar toolbar;
    TextView tvSave, toolbar_title, tvLabelStartPrice, tvStartPrice;
    Button btnSave;
    SessionManager sessionManager;
    ArrayList<SnfFatListPojo> mSnfList = new ArrayList<>();
    ArrayList<SnfFatListPojo> mFatList = new ArrayList<>();
    ArrayList<String> strFatList = new ArrayList<>();
    ArrayList<String> strSnfList = new ArrayList<>();
    TableLayout table;
    String dairyId = "", chartCatId = "", chartType = "", snfFatCategory = "";

    double basePrice = 0.0d, currentFat = 0.0d, currentSNF = 0.0d, fatFrom = 0.0d, fatTo = 0.0, fatRate = 0, snfFrom = 0, snfTo = 0, snfRate = 0.15, increasePoint = .1d;
    ProgressDialog progressDialog;
    ImageView imgFatAdd, imgSnfAdd;
    View view;
    AlertDialog alertDialog;
    RecyclerView listViewFat, listViewSnf;
    FatSnf_Step_Item_adapter fatStepItemAdapter;
    FatSnf_Step_Item_adapter snfStepItemAdapter;
    JsonArray jsonFatSNF;
    private List<RateCardStep> fats = new ArrayList();
    private List<RateCardStep> snfs = new ArrayList();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_all_rate_card_add, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        tvSave = toolbar.findViewById(R.id.tvSave);
        imgFatAdd = view.findViewById(R.id.imgAddFat);
        imgSnfAdd = view.findViewById(R.id.imgAddSnf);
        tvLabelStartPrice = view.findViewById(R.id.tvLabelStartPrice);
        tvStartPrice = view.findViewById(R.id.tvStartPrice);
        listViewFat = view.findViewById(R.id.listViewFat);
        listViewSnf = view.findViewById(R.id.listViewSnf);
        tvSave.setVisibility(View.VISIBLE);
        mSnfList = new ArrayList<>();
        mFatList = new ArrayList<>();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.Please_Wait));
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvLabelStartPrice.setText(mContext.getString(R.string.start) + " " + mContext.getString(R.string.Amount));
        Bundle bundle = getArguments();
        dairyId = bundle.getString("dairy_id");
        chartCatId = bundle.getString("catChartId");
        snfFatCategory = bundle.getString("snf_fat_category");
        chartType = bundle.getString("chartType");

        if (snfFatCategory.equals("2")) {
            toolbar_title.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Cow) + " " + mContext.getString(R.string.chart));
        } else {
            toolbar_title.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Buff) + " " + mContext.getString(R.string.chart));
        }

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jsonFatSNF != null && jsonFatSNF.size() > 0) {
                    createChartList();
                } else {
                    showAlert("Enter valid Fat SNF and Rate Value", mContext);
                }
            }
        });
        imgFatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFatDialog();
            }
        });
        tvStartPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPriceDialog();
            }
        });
        imgSnfAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSNFDialog();
            }
        });
        initFatListView();
        initSnfListView();
        return view;
    }

    public void initFatListView() {
        fatStepItemAdapter = new FatSnf_Step_Item_adapter(mContext, fats, "fat", this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        listViewFat.setLayoutManager(mLayoutManager);
        listViewFat.setAdapter(fatStepItemAdapter);

    }

    public void initSnfListView() {
        snfStepItemAdapter = new FatSnf_Step_Item_adapter(mContext, snfs, "snf", this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        listViewSnf.setLayoutManager(mLayoutManager);
        listViewSnf.setAdapter(snfStepItemAdapter);
    }

    public void showPriceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_price, viewGroup, false);
        TextView tvCancel, tvAddOk;
        EditText ediPrice;
        ediPrice = dialogView.findViewById(R.id.ediPrice);
        tvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvAddOk = dialogView.findViewById(R.id.tv_ok);

        builder.setView(dialogView);
        ediPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tvAddOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAddOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String rate = ediPrice.getText().toString().trim();

                        rate = rate.replaceAll("[^\\d.]", "0");

                        double r = 0;
                        r = Double.parseDouble(rate);
                        if (rate.length() == 0) {
                            ediPrice.requestFocus();
                            showAlert("Please Enter Rate", mContext);
                        } else if (r <= 0) {
                            ediPrice.requestFocus();
                            showAlert("Please Enter Valid Rate", mContext);
                        } else {
                            ediPrice.clearFocus();
                            rate = String.format("%.2f", r);

                            r = Double.parseDouble(rate);
                            basePrice = r;
                            tvStartPrice.setText("" + basePrice);
                            alertDialog.dismiss();

                            setTableData();
                        }

                    }
                });

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showFatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_fat_snf_rate_card_add_dialog, viewGroup, false);
        TextView tvTitle, tvCancel, tvAddOk;
        EditText ediStepTo, ediRate;
        tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        ediStepTo = dialogView.findViewById(R.id.ediStepTo);
        ediRate = dialogView.findViewById(R.id.ediRate);
        tvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvAddOk = dialogView.findViewById(R.id.tv_ok);
        tvTitle.setText(mContext.getString(R.string.Fat) + " " + mContext.getString(R.string.step));
        builder.setView(dialogView);

        ediStepTo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tvAddOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvAddOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String step = ediStepTo.getText().toString().trim();
                        String rate = ediRate.getText().toString().trim();
                        step = step.replaceAll("[^\\d.]", "0");
                        rate = rate.replaceAll("[^\\d.]", "0");
                        double s = 0, r = 0;
                        if (step.length() > 0 && !step.startsWith(".")) {
                            s = Double.parseDouble(step);
                        }
                        if (rate.length() > 0 && !rate.startsWith(".")) {
                            r = Double.parseDouble(rate);
                        }
                        if (step.length() == 0) {
                            showAlert("Please Enter Fat Step", mContext);
                            ediStepTo.requestFocus();
                        } else if (step.startsWith(".") || step.startsWith(" ")) {
                            ediStepTo.requestFocus();
                            showAlert("Please Enter Valid Fat Step", mContext);
                        } else if (s == 0 || s < 2) {
                            ediStepTo.requestFocus();
                            showAlert("Please Enter Valid Fat Step", mContext);
                        } else if (currentFat > 0 && s <= currentFat) {
                            ediStepTo.requestFocus();
                            showAlert("Fat Step " + "should be grater than " + currentFat, mContext);
                        } else if (s > 19) {
                            ediStepTo.requestFocus();
                            showAlert("Fat Step Must be" + " between 2.0 to 19", mContext);
                        } else if (rate.length() == 0) {
                            ediStepTo.clearFocus();
                            ediRate.requestFocus();
                            showAlert("Please Enter Rate", mContext);
                        } else if (rate.startsWith(".") || rate.startsWith(" ")) {
                            ediRate.requestFocus();
                            showAlert("Please Enter Valid Rate", mContext);
                        } else {
                            ediStepTo.clearFocus();
                            ediRate.clearFocus();
                            step = String.format("%.1f", s);
                            rate = String.format("%.2f", r);
                            s = Double.parseDouble(step);
                            r = Double.parseDouble(rate);
                            currentFat = s;
                            RateCardStep rateCardStep = new RateCardStep(s, r);
                            fats.add(rateCardStep);
                            fatStepItemAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();
                            setTableData();
                        }

                    }
                });

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public void showSNFDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        ViewGroup viewGroup = view.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_fat_snf_rate_card_add_dialog, viewGroup, false);
        TextView tvTitle, tvCancel, tvAddOk;
        EditText ediStepTo, ediRate;
        tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        ediStepTo = dialogView.findViewById(R.id.ediStepTo);
        ediRate = dialogView.findViewById(R.id.ediRate);

        tvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvAddOk = dialogView.findViewById(R.id.tv_ok);
        tvTitle.setText(mContext.getString(R.string.SNF) + " " + mContext.getString(R.string.step));
        builder.setTitle(mContext.getString(R.string.SNF));
        builder.setView(dialogView);

        ediStepTo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        tvAddOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String step = ediStepTo.getText().toString().trim();
                String rate = ediRate.getText().toString().trim();
                step = step.replaceAll("[^\\d.]", "");
                rate = rate.replaceAll("[^\\d.]", "");
                double s = 0, r = 0;
                if (step.length() > 0 && !step.startsWith(".")) {
                    s = Double.parseDouble(step);
                }
                if (rate.length() > 0 && !rate.startsWith(".")) {
                    r = Double.parseDouble(rate);
                }
                if (step.length() == 0) {
                    showAlert("Please Enter SNF Step", mContext);
                    ediStepTo.requestFocus();
                } else if (step.startsWith(".") || step.startsWith(" ")) {
                    ediStepTo.requestFocus();
                    showAlert("Please Enter Valid SNF Step", mContext);
                } else if (s == 0 || s < 4) {
                    ediStepTo.requestFocus();
                    showAlert("Please Enter SNF between 4.0 to 10", mContext);
                } else if (currentSNF > 0 && s <= currentSNF) {
                    ediStepTo.requestFocus();
                    showAlert("SNF Step " + currentSNF + "  should be grater than", mContext);
                } else if (s > 10) {
                    ediStepTo.requestFocus();
                    showAlert("SNF Step Must be" + " between 4.0 to 10", mContext);
                } else if (rate.length() == 0) {
                    ediStepTo.clearFocus();
                    ediRate.requestFocus();
                    showAlert("Please Enter Rate", mContext);
                } else if (rate.startsWith(".") || rate.startsWith(" ")) {
                    ediRate.requestFocus();
                    showAlert("Please Enter Valid Rate", mContext);
                } else {
                    ediStepTo.clearFocus();
                    ediRate.clearFocus();
                    step = String.format("%.1f", s);
                    rate = String.format("%.2f", r);
                    s = Double.parseDouble(step);
                    r = Double.parseDouble(rate);
                    currentSNF = s;
                    RateCardStep rateCardStep = new RateCardStep(s, r);
                    snfs.add(rateCardStep);
                    snfStepItemAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                    setTableData();
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void setTableData() {
        jsonFatSNF = new JsonArray();
        System.out.println("  init fats size>>>" + fats.size());
        System.out.println(" init  snfs size>>>" + snfs.size());

        table = view.findViewById(R.id.table_excel);
        table.removeAllViewsInLayout();
        mFatList = new ArrayList<>();
        mSnfList = new ArrayList<>();
        mFatList.add(new SnfFatListPojo(" FAT/SNF ", " FAT/SNF ", " FAT/SNF ", "0", ""));
        mSnfList.add(new SnfFatListPojo(" FAT/SNF ", " FAT/SNF ", " FAT/SNF ", "0", ""));
        System.out.println(" update fats size>>>" + fats.size());
        System.out.println(" update snfs size>>>" + snfs.size());
        if (!fats.isEmpty() && !snfs.isEmpty()) {
            if (fats.size() > 1 || snfs.size() > 1) {
                progressDialog.show();

                ((Activity) mContext).runOnUiThread(new Runnable() {
                    public void run() {
                        //Log.d("UI thread", "I am the UI thread");
                        viewTableData();
                    }
                });


            }
        }


    }

    @SuppressLint("ResourceType")
    private void viewTableData() {
        // set fat Range
        if (!fats.isEmpty()) {
            strFatList = new ArrayList<>();
            int fatSize = fats.size();
            for (int i = 0; i < fats.size(); i++) {
                if (i == 0) {
                    fatFrom = fats.get(i).getValue();
                } else {
                    fatFrom = fats.get(i).getValue() + increasePoint;
                }

                if (i < fatSize - 1) {
                    fatTo = fats.get(i + 1).getValue();
                } else {
                    fatTo = currentFat;
                }

                double fatValue = fats.get(i).getIncrement_by();

                for (double j = fatFrom; j <= fatTo; j = j + increasePoint) {
                    j = Double.parseDouble(String.format("%.1f", j));
                    strFatList.add(String.format("%.1f", j) + "");
                    mFatList.add(new SnfFatListPojo("", "", String.format("%.1f", j) + "", String.format("%.2f", fatValue), ""));

                }
            }
        }
        // set SNF Range
        if (!snfs.isEmpty()) {
            strSnfList = new ArrayList<>();
            int snfSize = snfs.size();
            for (int i = 0; i < snfs.size(); i++) {
                if (i == 0) {
                    snfFrom = snfs.get(i).getValue();
                } else {
                    snfFrom = snfs.get(i).getValue() + increasePoint;
                }

                if (i < snfSize - 1) {
                    snfTo = snfs.get(i + 1).getValue();
                } else {
                    snfTo = currentSNF;
                }
                double snfValue = snfs.get(i).getIncrement_by();
                for (double j = snfFrom; j <= snfTo; j = j + increasePoint) {
                    j = Double.parseDouble(String.format("%.1f", j));
                    strSnfList.add(String.format("%.1f", j) + "");
                    mSnfList.add(new SnfFatListPojo("", String.format("%.1f", j) + "", "",
                            String.format("%.2f", snfValue), ""));

                }
            }
        }

        int rows = mFatList.size();
        int colums = mSnfList.size();
        System.out.println("=== rows====" + rows);
        System.out.println("===colums====" + colums);
        System.out.println("===fat array====" + strFatList);
        System.out.println("===snf array====" + strSnfList);
        String strFat = "0", strSnf = "0";

        double fatRateValue = basePrice;
        double snfRateValue = basePrice;
        String strfatSNFRate = "0";
        for (int i = 0; i < rows; i++) {

            TableRow tr = new TableRow(mContext);
            tr.setId(10);
            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
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
            strFat = mFatList.get(i).getFat();
            if (i > 1) {
                fatRateValue = fatRateValue + Double.parseDouble(mFatList.get(i).getRate());
                System.out.println("Fat===>>>" + mFatList.get(i).getFat());
            }
            snfRateValue = fatRateValue;
            for (int j = 0; j < colums; j++) {
                System.out.println("Fat===>>>" + mFatList.get(i).getFat());
                System.out.println("SNf===>>>" + mSnfList.get(j).getSNF());

                strSnf = mSnfList.get(j).getSNF();
                tvCell = new TextView(mContext);
                tvCell.setTextSize(14);
                tvCell.setTextColor(Color.WHITE);
                tvCell.setGravity(Gravity.CENTER);
                tvCell.setPadding(5, 5, 5, 5);

                if (i == 0 && j < colums) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_blue));
                    tvCell.setText("  " + mSnfList.get(j).getSNF() + " ");
                } else if (j == 0 && i < rows) {
                    tvCell.setTypeface(Typeface.DEFAULT_BOLD);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape_primary_color));
                    tvCell.setText("  " + mFatList.get(i).getFat() + " ");
                } else if (j == 1 && i == 1) {
                    strfatSNFRate = String.format("%.2f", basePrice);
                    tvCell.setTypeface(Typeface.DEFAULT);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape));
                    tvCell.setText("  " + strfatSNFRate + " ");
                    JsonObject object = new JsonObject();
                    object.addProperty("fat", strFat);
                    object.addProperty("snf", strSnf);
                    object.addProperty("value", strfatSNFRate);
                    object.addProperty("created_by", dairyId);
                    object.addProperty("dairy_id", dairyId);
                    object.addProperty("snf_fat_category", snfFatCategory);
                    object.addProperty("categorychart_id", chartCatId);
                    jsonFatSNF.add(object);
                } else if (j == 1 && i > 1 && i < rows) {

                    strfatSNFRate = String.format("%.2f", fatRateValue);
                    tvCell.setTypeface(Typeface.DEFAULT);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape));
                    tvCell.setText("  " + strfatSNFRate + " ");
                    JsonObject object = new JsonObject();
                    object.addProperty("fat", strFat);
                    object.addProperty("snf", strSnf);
                    object.addProperty("value", strfatSNFRate);
                    object.addProperty("created_by", dairyId);
                    object.addProperty("dairy_id", dairyId);
                    object.addProperty("snf_fat_category", snfFatCategory);
                    object.addProperty("categorychart_id", chartCatId);
                    jsonFatSNF.add(object);
                } else {
                    snfRateValue = snfRateValue + Double.parseDouble(mSnfList.get(j).getRate());
                    tvCell.setTypeface(Typeface.DEFAULT);
                    tvCell.setBackground(mContext.getResources().getDrawable(R.drawable.cell_shape));
                    strfatSNFRate = String.format("%.2f", snfRateValue);
                    tvCell.setText("  " + strfatSNFRate + " ");
                    JsonObject object = new JsonObject();
                    object.addProperty("fat", strFat);
                    object.addProperty("snf", strSnf);
                    object.addProperty("value", strfatSNFRate);
                    object.addProperty("created_by", dairyId);
                    object.addProperty("dairy_id", dairyId);
                    object.addProperty("snf_fat_category", snfFatCategory);
                    object.addProperty("categorychart_id", chartCatId);
                    jsonFatSNF.add(object);
                }

                tr.addView(tvCell);
            }
            table.addView(tr);
        }

        System.out.println("jsonFatSNF=====" + jsonFatSNF);
        progressDialog.dismiss();
    }


    @Override
    public void onUpdateList(int position, String from) {
        System.out.println(" update position >>>" + position);
        System.out.println(" update fats size>>>" + fats.size());
        System.out.println(" update snfs size>>>" + snfs.size());
        if (from.equals("fat")) {
            fats.remove(position);
            if (fats.isEmpty()) {
                currentFat = 0;
            } else {
                currentFat = fats.get(fats.size() - 1).getValue();
            }
            fatStepItemAdapter.notifyDataSetChanged();
        } else {
            snfs.remove(position);
            if (snfs.isEmpty()) {
                currentSNF = 0;
            } else {
                currentSNF = snfs.get(snfs.size() - 1).getValue();
            }
            snfStepItemAdapter.notifyDataSetChanged();
        }

        setTableData();

    }

    private void createChartList() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Please_Wait), true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        showToast(mContext, mContext.getString(R.string.Updating_Success));
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            public void run() {
                                //Log.d("UI thread", "I am the UI thread");
                                getDairyAllSNF_FATChart(mContext, chartType, true);
                                getActivity().onBackPressed();
                            }
                        });

                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {

            String fatList = TextUtils.join(",", strFatList);
            String snfList = TextUtils.join(",", strSnfList);
            System.out.println("fatList>>>" + fatList);
            System.out.println("snfList>>>" + snfList);
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("type", chartType);
            mainObject.addProperty("dairy_id", dairyId);
            mainObject.addProperty("snf_fat_category", snfFatCategory);
            mainObject.addProperty("categorychart_id", chartCatId);
            mainObject.addProperty("fat_list", fatList.trim());
            mainObject.addProperty("snf_list", snfList.trim());
            mainObject.add("make_array", jsonFatSNF);
            StringBuilder sb = new StringBuilder();
            sb.append(mainObject.toString());
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("type", chartType)
                    .addEncoded("snf_fat_category", snfFatCategory)
                    .addEncoded("categorychart_id", chartCatId)
                    .addEncoded("fat_list", fatList.trim())
                    .addEncoded("snf_list", snfList.trim())
                    .addEncoded("make_array", jsonFatSNF.toString())
                    .build();
            webServiceCaller.addRequestBody(body);
            webServiceCaller.execute(Constant.createFatRateChartAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
