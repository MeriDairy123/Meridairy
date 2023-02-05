package b2infosoft.milkapp.com.Dairy.FatSnf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Model.BeanCatChartItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.RealPathUtil;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.appglobal.Constant.downloadSampleChartFileAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_ChartType;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.downloadFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

/**
 * Created by B2infosoft on 02/12/2019.
 */

public class ImportChartFragment extends Fragment implements View.OnClickListener, OnDownLoadListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Context mContext;
    Toolbar toolbar;
    TextView tvUploadFile, tvDownloadSample;
    AppCompatSpinner spinChart, spinCat;
    Button btnSubmit;
    ArrayList<BeanCatChartItem> beanCatChartItemList;
    File fileChart;
    String catChartLabel = "", snfFatCategory = "", strChartId = "", chartFileName = "";
    SessionManager sessionManager;
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_upload_chart_file, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        initView();
        return view;
    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        tvDownloadSample = view.findViewById(R.id.tvDownloadSample);
        tvUploadFile = view.findViewById(R.id.tvUploadFile);
        spinCat = view.findViewById(R.id.spinCategory);
        spinChart = view.findViewById(R.id.spinChartCat);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        catChartLabel = mContext.getString(R.string.upload) + " " + mContext.getString(R.string.chart);
        toolbar.setTitle(catChartLabel);
        toolBarManage();
        tvDownloadSample.setOnClickListener(this);
        tvUploadFile.setOnClickListener(this);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (snfFatCategory.length() != 0 && strChartId.length() != 0 && chartFileName.length() != 0) {
                    uploadChartFile();
                } else {
                    showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                }

            }
        });
        spinCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    snfFatCategory = String.valueOf(position);
                } else {
                    snfFatCategory = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        geCLRChartList();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedFileURI = Objects.requireNonNull(data).getData();
                String tempPath = RealPathUtil.getPath(mContext, selectedFileURI);
                String ext = tempPath.substring(tempPath.lastIndexOf("."));
                printLog("ext===>>>" , ext);
                printLog("tempPath===>>>" , tempPath);
                if (ext.startsWith(".xl") || ext.equalsIgnoreCase(".xlt") || ext.equalsIgnoreCase(".xlsx") ||
                        ext.equalsIgnoreCase(".csv") || ext.equalsIgnoreCase(".xla") || ext.equalsIgnoreCase(".xls")) {
                    fileChart = new File(tempPath);
                   printLog("File :>>> " , fileChart.getName());
                    printLog("File path :>>> " , fileChart.getPath());
                    printLog("File Size :>>> " ,""+fileChart.length());
                    chartFileName = fileChart.getName();
                    tvUploadFile.setText(chartFileName);
                } else {
                    showToast(mContext, "Please upload Excel File Only");
                }
            }
        }
    }

    public void geCLRChartList() {
        beanCatChartItemList = new ArrayList<>();
        ArrayList<String> catChartItem = new ArrayList<>();
        String selectChart = mContext.getResources().getString(R.string.selectChartCategory);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        catChartItem.add(selectChart);
                        catChartItem.add(mContext.getResources().getString(R.string.generalChart));
                        beanCatChartItemList.add(new BeanCatChartItem("", "", "", selectChart));
                        beanCatChartItemList.add(new BeanCatChartItem("0",
                                mContext.getResources().getString(R.string.generalChart), "", mContext.getResources().getString(R.string.generalChart)));

                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject jobj = jsonData.getJSONObject(i);
                            catChartItem.add(jobj.getString("name"));
                            beanCatChartItemList.add(new BeanCatChartItem(jobj.getString("id"),
                                    jobj.getString("name"), "", ""));
                        }
                    }
                    initSpinCatChart(catChartItem);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(Constant.getCategoryChartAPI);


    }

    private void initSpinCatChart(ArrayList<String> catChartItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, catChartItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinChart.setAdapter(spinAdapter);
        spinChart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    strChartId = beanCatChartItemList.get(position).id;
                } else {
                    strChartId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void toolBarManage() {
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Objects.requireNonNull(getActivity()).onBackPressed();

            }
        });

    }

    private void uploadChartFile() {
        if (isNetworkAvaliable(mContext)) {
            @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            showToast(getActivity(), jsonObject.getString("user_status_message"));
                            getDairyAllSNF_FATChart(mContext, sessionManager.getValueSesion(Key_ChartType), true);
                            Objects.requireNonNull(getActivity()).onBackPressed();

                        } else {
                            UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            printLog("dairyId>>>" , sessionManager.getValueSesion(SessionManager.KEY_UserID));
            printLog("strChartId>>>" , strChartId);
            printLog("snfFatCategory>>>" , snfFatCategory);
            printLog("chartType>>>" , sessionManager.getValueSesion(Key_ChartType));
            printLog("fileChart>>>" , fileChart.getPath());
            MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
            multipartBuilder.addFormDataPart("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
            multipartBuilder.addFormDataPart("type", sessionManager.getValueSesion(Key_ChartType));
            multipartBuilder.addFormDataPart("categorychart_id", strChartId);
            multipartBuilder.addFormDataPart("snf_fat_category", snfFatCategory);

            multipartBuilder.addFormDataPart("file", fileChart.getName(), RequestBody.create(MediaType.parse("*/*"), fileChart));


            RequestBody body = multipartBuilder.build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(Constant.uploadFatSnfFileAPI);

        } else {
            showToast(getActivity(), mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDownloadSample:
                if (!hasPermissions(mContext, PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
                }

                downloadFile(mContext, downloadSampleChartFileAPI, "ChartSample");

                break;
            case R.id.tvUploadFile:
                String[] mimeTypes =
                        {"application/mswordapplication/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                                "application/vnd.ms-excel"};

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                   /*for (int i=0;i<mimeTypes.length;i++) {
                       intent.setType(mimeTypes[i]);
                   }*/
                intent.setType("application/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a Excel File to Upload"),
                            1);
                } catch (android.content.ActivityNotFoundException ex) {
                    showToast(getActivity(), "Please install a File Manager.");
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    showToast(mContext, "Permission Granted");

                } else {
                    // Permission Denied
                    showToast(mContext, "Permission Denied");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onDownLoadComplete(String folderName, File file) {

    }
}
