package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DecimalDigitsInputFilter;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddCustomChartFragment extends Fragment {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvSave;
    SessionManager sessionManager;
    EditText ediFatFrom, ediFatTo, ediFatRate, ediSnfFrom, ediSnfTo, ediSnfRate;
    Button btnSave;
    //ArrayList<SnfFatListPojo> mList = new ArrayList<>();
    ArrayList<String> mSnfList = new ArrayList<>();
    ArrayList<String> mFatList = new ArrayList<>();
    TableLayout table;
    double fatFrom = 0.0d, fatTo = 0.0, fatRate = 0, snfFrom = 0, snfTo = 0, snfRate = 0, increasePoint = .1d;
    ProgressDialog progressDialog;
    Bundle bundle = null;
    String dairyId = "", chartCatId = "", chartType = "", snfFatCategory = "";
    View view;
    JsonArray jsonFatSNF;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_rate_card_add, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvSave = toolbar.findViewById(R.id.tvSave);
        ediFatFrom = view.findViewById(R.id.ediFatFrom);
        ediFatTo = view.findViewById(R.id.ediFatTo);
        ediFatRate = view.findViewById(R.id.ediFatRate);
        ediSnfFrom = view.findViewById(R.id.ediSnfFrom);
        ediSnfTo = view.findViewById(R.id.ediSnfTo);
        ediSnfRate = view.findViewById(R.id.ediSnfRate);
        btnSave = view.findViewById(R.id.btnSave);
        table = view.findViewById(R.id.table_excel);
        mSnfList = new ArrayList<>();
        mFatList = new ArrayList<>();

        Bundle bundle = getArguments();
        dairyId = bundle.getString("dairy_id");
        chartCatId = bundle.getString("catChartId");
        snfFatCategory = bundle.getString("snf_fat_category");
        chartType = bundle.getString("chartType");
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage(mContext.getString(R.string.Please_Wait));
        tvSave.setVisibility(View.VISIBLE);

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fatFrom < 2) {
                    showAlert("Enter valid Fat From grater than 2", mContext);
                    ediFatFrom.requestFocus();
                } else if (fatFrom > 17) {
                    showAlert("Fat should not be between 2 to 17", mContext);
                    ediFatFrom.requestFocus();
                } else if (fatTo == 0) {
                    ediFatFrom.requestFocus();
                    showAlert("Please Enter Fat To", mContext);
                } else if (fatTo <= fatFrom) {
                    showAlert("Fat To must be grater  Fat From", mContext);
                    ediFatFrom.clearFocus();
                    ediFatTo.requestFocus();
                } else if (fatTo > 17) {
                    showAlert("Fat To should not be grater than 17", mContext);
                    ediFatFrom.clearFocus();
                    ediFatTo.requestFocus();
                } else if (fatRate == 0) {
                    showAlert("Please Enter Rate!", mContext);
                    ediFatTo.clearFocus();
                    ediFatRate.requestFocus();
                } else if (snfFrom < 5) {
                    showAlert("Enter valid From SNF grater than 5", mContext);
                    ediFatRate.clearFocus();
                    ediSnfFrom.requestFocus();
                } else if (snfFrom > 10) {
                    showAlert(" SNF should not be between 5 to 10", mContext);
                    ediFatRate.clearFocus();
                    ediSnfFrom.requestFocus();
                } else if (snfTo <= snfFrom) {
                    ediSnfFrom.clearFocus();
                    ediSnfTo.requestFocus();
                    showAlert("SNF To must be grater SNF From", mContext);
                } else if (snfTo > 10) {
                    ediSnfFrom.clearFocus();
                    ediSnfTo.requestFocus();
                    showAlert("SNF To should not be grater than 10", mContext);
                } else if (snfRate <= 0) {
                    showAlert("Please Enter SNF Rate !", mContext);
                    ediSnfTo.clearFocus();
                    ediSnfRate.requestFocus();
                } else {
                    setListData();
                }

            }
        });

        initEditText();
        return view;
    }


    public void setListData() {
        progressDialog.show();
        mFatList = new ArrayList<>();
        mSnfList = new ArrayList<>();
        mFatList.add(" FAT/SNF ");
        mSnfList.add(" FAT/SNF ");
        // set fat Range
        System.out.println("fatFrom>>>" + fatFrom);
        System.out.println("fatTo>>>" + fatTo);
        for (double i = fatFrom; i <= fatTo; i = i + increasePoint) {
            mFatList.add(String.format("%.1f", i) + "");
            i = Double.parseDouble(String.format("%.1f", i));
        }
        // set SNF Range
        for (double j = snfFrom; j <= snfTo; j = j + increasePoint) {
            mSnfList.add(String.format("%.1f", j) + "");

        }

        showTableLayout();
    }

    @SuppressLint("ResourceType")
    public void showTableLayout() {
        table = view.findViewById(R.id.table_excel);
        table.removeAllViewsInLayout();


        int rows = mFatList.size();
        int colums = mSnfList.size();
        System.out.println("===rows====" + rows);
        System.out.println("=== columns ====" + colums);
        double fat = 0, snf = 0, fatSNFRate = 0;
        String strFat = "", strSnf = "";
        jsonFatSNF = new JsonArray();
        if (rows > 1 && colums > 1) {
            for (int i = 0; i < rows; i++) {
                strFat = mFatList.get(i);

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
                for (int j = 0; j < colums; j++) {
                    strSnf = mSnfList.get(j);
                    System.out.println("Fat===>>>" + strFat);
                    System.out.println("SNf===>>>" + strSnf);
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
                        fat = Double.parseDouble(mFatList.get(i));
                        snf = Double.parseDouble(mSnfList.get(j));
                        fatSNFRate = (fat / 100) * fatRate + (snf / 100) * snfRate;
                        String strfatSNFRate = String.format("%.2f", fatSNFRate);
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

            System.out.println("JsonFatSNF=====" + jsonFatSNF);
        }
        progressDialog.dismiss();

    }

    public void initEditText() {

        ediFatFrom.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediFatTo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediFatRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

        ediSnfFrom.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediSnfTo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
        ediSnfRate.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 2)});

        ediFatFrom.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                fatFrom = 0;
                String strText = editable.toString();
                if (strText.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");
                    fatFrom = Double.parseDouble(strText);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ediFatTo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                fatTo = 0;
                String strText = editable.toString();
                if (editable.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");


                    fatTo = Double.parseDouble(strText);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ediFatRate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                fatRate = 0;
                String strText = editable.toString();
                if (editable.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");


                    fatRate = Double.parseDouble(strText);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        ediSnfFrom.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                snfFrom = 0;
                String strText = editable.toString();
                if (editable.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");

                    snfFrom = Double.parseDouble(strText);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ediSnfTo.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                snfTo = 0;
                String strText = editable.toString();
                if (editable.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");
                    snfTo = Double.parseDouble(strText);
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ediSnfRate.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                snfRate = 0;
                String strText = editable.toString();
                if (editable.length() > 0 && !strText.startsWith(".")) {
                    strText = String.format(Locale.ENGLISH, strText);
                    strText = strText.replaceAll("[^\\d.]", "");


                    snfRate = Double.parseDouble(strText);
                }


            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    private void createChartList() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        showToast(mContext, mContext.getString(R.string.Updating_Success));

                        getDairyAllSNF_FATChart(mContext, chartType, true);
                        getActivity().onBackPressed();
                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            mFatList.remove(0);
            mSnfList.remove(0);
            String fatList = TextUtils.join(",", mFatList);
            String snfList = TextUtils.join(",", mSnfList);
            JsonObject mainObject = new JsonObject();
            mainObject.addProperty("type", chartType);
            mainObject.addProperty("dairy_id", dairyId);
            mainObject.addProperty("snf_fat_category", snfFatCategory);
            mainObject.addProperty("categorychart_id", chartCatId);
            mainObject.addProperty("fat_list", fatList);
            mainObject.addProperty("snf_list", snfList);
            mainObject.add("make_array", jsonFatSNF);
            StringBuilder sb = new StringBuilder();
            sb.append(mainObject.toString());
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("type", chartType)
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("snf_fat_category", snfFatCategory)
                    .addEncoded("categorychart_id", chartCatId)
                    .addEncoded("fat_list", fatList)
                    .addEncoded("snf_list", snfList)
                    .addEncoded("make_array", jsonFatSNF.toString())
                    .build();


            webServiceCaller.addRequestBody(body);
            webServiceCaller.execute(Constant.createFatRateChartAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
