package b2infosoft.milkapp.com.Advertisement;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import b2infosoft.milkapp.com.Advertisement.Adapter.City_ItemAdapter;
import b2infosoft.milkapp.com.Advertisement.Adapter.City_SelectedItemAdapter;
import b2infosoft.milkapp.com.Interface.CitySelectkListner;
import b2infosoft.milkapp.com.Interface.PaytmCheckSumListener;
import b2infosoft.milkapp.com.Interface.TahsilSelectkListner;
import b2infosoft.milkapp.com.Model.BeanCityAdvItem;
import b2infosoft.milkapp.com.Model.StatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.Payment_Status_activity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BuyPlan.BeanSMSPlan.generateCheckSum;
import static b2infosoft.milkapp.com.appglobal.Constant.CALLBACK_URL;
import static b2infosoft.milkapp.com.appglobal.Constant.CHANNEL_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.INDUSTRY_TYPE_ID;
import static b2infosoft.milkapp.com.appglobal.Constant.MID;
import static b2infosoft.milkapp.com.appglobal.Constant.WEBSITE;
import static b2infosoft.milkapp.com.appglobal.Constant.advertisementAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getCitiesAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_GALLERY_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE_CAPTURE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.bundleToJson;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getImageUri;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getPath;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getStringSizeLengthFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.initOrderId;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.rotateImageIfRequired;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class UploadAdsActivity extends AppCompatActivity implements CitySelectkListner, PaytmCheckSumListener,
        View.OnClickListener, TahsilSelectkListner, PaytmPaymentTransactionCallback {


    public static final int REQUEST_IMAGE = 100;
    final int THUMBSIZE = 120;
    public ArrayList<BeanCityAdvItem> mList;
    public ArrayList<BeanCityAdvItem> beanCityAdvItems;
    EditText ediAdvTitle, ediAdvDesc;
    TextView tvStartDate, tvEndtDate, tvPrice;
    Activity mContext;
    SessionManager sessionManager;
    Toolbar toolbar;
    TextView toolbar_title;
    Spinner spinState;
    RecyclerView recyclerView, recyclerSelectCity;
    ImageView imgUpload;
    Button btnUpload;
    City_ItemAdapter city_itemAdapter;
    City_SelectedItemAdapter city_selectedItemAdapter;
    File imageFile;
    Uri photoURI = null;
    String filePath = "";
    Bitmap thumbImage;
    File filesDir;
    String stateId = "", title = "", price = "", startDate = "",
            endDate = "", tahsil = "", description = "", thumb = "";
    int selectedCitySize = 0, totalprice = 0;
    View layoutAdvertiseList, layoutAddNewAdv, layoutTitle, layoutList;
    String checksum = "", order_id = "", ad_id = "", validity = "",
            customer_id = "", STATUS = "", data = "";
    String type = "dairy_advertise";
    PaytmPGService Service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advertisement);

        mContext = UploadAdsActivity.this;
        sessionManager = new SessionManager(mContext);
        customer_id = sessionManager.getValueSesion(KEY_UserID);
        mList = new ArrayList<>();
        beanCityAdvItems = new ArrayList<>();
        selectedCitySize = beanCityAdvItems.size();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
    }


    private void initView() {

        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        ediAdvTitle = findViewById(R.id.ediAdvTitle);
        layoutTitle = findViewById(R.id.layoutTitle);
        layoutList = findViewById(R.id.layoutList);
        ediAdvDesc = findViewById(R.id.ediAdvDesc);

        spinState = findViewById(R.id.spinState);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndtDate = findViewById(R.id.tvEndtDate);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerSelectCity = findViewById(R.id.recyclerSelectCity);
        tvPrice = findViewById(R.id.tvPrice);
        imgUpload = findViewById(R.id.imgUpload);
        btnUpload = findViewById(R.id.btnUpload);

        layoutList.setVisibility(View.GONE);
        imgUpload.setOnClickListener(this);

        tvStartDate.setOnClickListener(this);
        tvEndtDate.setOnClickListener(this);

        btnUpload.setOnClickListener(this);
        toolbar_title.setText(getString(R.string.CreateAdvertise));
        tvStartDate.setText(getSimpleDate("StartDate"));
        tvEndtDate.setText(getSimpleDate("EndDate"));


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(mContext.getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initRecyclerView();
        StatePojo.getStateList(mContext, "Ads");
    }

    private void initRecyclerView() {
        city_itemAdapter = new City_ItemAdapter(mContext, mList, this, selectedCitySize);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(city_itemAdapter);
    }

    public void setStateAdapter(final ArrayList<StatePojo> stateList) {

        final ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.Please_Select_State));

        for (int i = 0; i < stateList.size(); i++) {
            list.add(stateList.get(i).sate_name);
        }

        ArrayAdapter<String> stateSpinnAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_item, list);
        stateSpinnAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinState.setAdapter(stateSpinnAdapter);


        spinState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String selectedState = parent.getItemAtPosition(position).toString();
                if (!selectedState.equals(getString(R.string.Please_Select_State))) {
                    stateId = stateList.get(position - 1).state_id;

                    getCityList(mContext);
                } else {
                    stateId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onCitySelect(BeanCityAdvItem album, boolean selected) {
        String id = "";
        String name = "";
        boolean idYes = false;
        int removePosition = 0;
        totalprice = 0;

        if (beanCityAdvItems.size() == 0) {
            // beanCityAdvItems.add(album);
            totalprice = totalprice + album.getPrice();
        } else {
            for (int i = 0; i < beanCityAdvItems.size(); i++) {

                id = beanCityAdvItems.get(i).id;
                name = beanCityAdvItems.get(i).cityName;
                if (album.id.equalsIgnoreCase(id) && album.cityName.equalsIgnoreCase(name)) {
                    idYes = true;
                    removePosition = i;
                }
                totalprice = totalprice + beanCityAdvItems.get(i).getPrice();

            }
        }

        if (selected && idYes) {
            showAlert("This city is already exists.", mContext);
        } else if (selected && idYes == false) {
            beanCityAdvItems.add(album);
            totalprice = totalprice + album.getPrice();
        } else if (selected == false && idYes) {

            beanCityAdvItems.remove(removePosition);
            totalprice = totalprice - album.getPrice();

        }
        selectedCitySize = beanCityAdvItems.size();
        if (selectedCitySize >= 5) {
            showAlert("You have add max 5 cities", mContext);

            city_itemAdapter.updateCitySize(selectedCitySize);
        }
        initRecyclerSelected();
        setCities();
        setPrice();
    }

    @Override
    public void onTahsilSelect(ArrayList<BeanCityAdvItem> mList) {
        beanCityAdvItems = mList;
        totalprice = 0;
        selectedCitySize = beanCityAdvItems.size();
        city_itemAdapter.updateCitySize(selectedCitySize);


        setCities();

        setPrice();

    }

    private void setCities() {
        totalprice = 0;
        tahsil = "";
        for (int i = 0; i < beanCityAdvItems.size(); i++) {
            if (tahsil.length() == 0) {
                tahsil = beanCityAdvItems.get(i).getId();
            } else {
                tahsil = tahsil + "," + beanCityAdvItems.get(i).getId();

            }
            totalprice = totalprice + beanCityAdvItems.get(i).getPrice();

        }
    }

    private void setPrice() {
        price = String.valueOf(totalprice);
        tvPrice.setText("Your Ads Price is :  " + price);

    }

    private void initRecyclerSelected() {
        System.out.println("beanCityAdvItems==size==" + beanCityAdvItems.size());

        city_selectedItemAdapter = new City_SelectedItemAdapter(mContext, beanCityAdvItems, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, true);
        recyclerSelectCity.setLayoutManager(mLayoutManager);
        recyclerSelectCity.setAdapter(city_selectedItemAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (layoutList.getVisibility() == View.VISIBLE) {
            layoutTitle.setVisibility(View.VISIBLE);
            layoutList.setVisibility(View.GONE);
            btnUpload.setText(R.string.Next);
        } else {
            onBackPressed();

        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.tvEndtDate:
                getDate("EndDate");
                break;
            case R.id.imgUpload:
                //   selectImage(mContext);
                captureImage();
                break;
            case R.id.btnUpload:


                startDate = tvStartDate.getText().toString();
                endDate = tvEndtDate.getText().toString();

                if (layoutTitle.getVisibility() == View.VISIBLE) {
                    title = ediAdvTitle.getText().toString();
                    description = ediAdvDesc.getText().toString();
                    if (title.length() == 0) {
                        ediAdvTitle.requestFocus();
                        showAlertBox(mContext, mContext.getString(R.string.TITLE) + "!");
                    } else if (description.length() == 0) {
                        ediAdvTitle.clearFocus();
                        showAlertBox(mContext, mContext.getString(R.string.Description) + "!");
                    } else if (filePath.length() == 0) {
                        showAlertBox(mContext, mContext.getString(R.string.PleaseUploadImageFile) + "!");

                    } else {
                        ediAdvTitle.clearFocus();
                        ediAdvDesc.clearFocus();
                        layoutTitle.setVisibility(View.GONE);
                        layoutList.setVisibility(View.VISIBLE);
                        btnUpload.setText(R.string.Submit);
                    }
                } else {

                    if (tahsil.length() == 0) {
                        showAlertBox(mContext, mContext.getString(R.string.Please_Select_City) + "!");
                    } else if (price.length() == 0) {
                        showAlertBox(mContext, mContext.getString(R.string.Price) + "!");
                    } else {
                        uploadAdvertisement();
                    }
                }
                break;
        }
    }

    private void showImagePickerOptions() {
        ImagePickerAcitvity.showImagePickerOptions(mContext, new ImagePickerAcitvity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerAcitvity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerAcitvity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(mContext, ImagePickerAcitvity.class);
        intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_GALLERY_IMAGE);
        // setting aspect ratio
        intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void captureImage() {

        Dexter.withActivity(mContext)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(android.R.string.selectAll);
        builder.setMessage(android.R.string.selectAll);
        builder.setPositiveButton(android.R.string.yes
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        openSettings();
                    }
                });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // OI FILE Manager
        Bitmap bitmap = null;
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_IMAGE) {
                Uri uri = data.getParcelableExtra("path");
                System.out.println("uri====" + uri);

                try {
                    filePath = uri.getPath();// getPath(uri.getPath(), UploadAdsActivity.this);
                    thumb = filePath;
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                    //  filePath = getPath(uri, UploadAdsActivity.this);
                    imgUpload.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        } else {
            Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
        }

    }

    public void AttatchedFile(Bitmap bitmap) {

        imageFile = new File(filesDir, "meri_dairy_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream os;
        try {
            imageFile.createNewFile();
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }


        filePath = imageFile.getPath();
        String fileSize = getStringSizeLengthFile(imageFile.length());
        try {
            bitmap = rotateImageIfRequired(bitmap, mContext, Uri.fromFile(imageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //   Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, MAX_WIDTH, MAX_HEIGHT, true);

        Uri tempUri = getImageUri(mContext, bitmap);
        fileSize = getStringSizeLengthFile(tempUri.getPath().length());
        System.out.println("attach" + "=====fileSize===new=====" + fileSize);
        filePath = getPath(tempUri, UploadAdsActivity.this);
        thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(filePath),
                THUMBSIZE, THUMBSIZE);
        tempUri = getImageUri(mContext, thumbImage);
        thumb = getPath(tempUri, UploadAdsActivity.this);
        imgUpload.setImageBitmap(bitmap);

    }

    public void getCityList(Context mContext) {

        if (isNetworkAvaliable(mContext)) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.GET_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    mList = new ArrayList<>();
                    try {
                        JSONObject object = new JSONObject(response);

                        if (object.getString("status").equalsIgnoreCase("success")) {
                            JSONArray mainJsonArray = object.getJSONArray("data");
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject objectData = mainJsonArray.getJSONObject(i);
                                mList.add(new BeanCityAdvItem(
                                        objectData.getString("id"),
                                        objectData.getString("name"),
                                        false, objectData.getInt("price"), objectData.getInt("city_count")));
                            }
                            initRecyclerView();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            serviceCaller.execute(getCitiesAPI +
                    "dairy_id=" + sessionManager.getValueSesion(KEY_UserID) +
                    "&state_id=" + stateId + "&start_date=" + stateId + "&end_date=" + endDate);

        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }

    private void uploadAdvertisement() {
        @SuppressLint("StaticFieldLeak") NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please Wait...", true) {
            @Override
            public void handleResponse(String response) throws JSONException {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        // showToast(mContext, jsonObject.getString("user_status_message"));
                        JSONObject jsonData = jsonObject.getJSONObject("data");
                        ad_id = jsonData.getString("id");
                      checsumGenerate();


                    } else {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        File imageFile = new File(filePath);
        File thumbFile = new File(thumb);

        multipartBuilder.addFormDataPart("dairy_id", sessionManager.getValueSesion(KEY_UserID));
        multipartBuilder.addFormDataPart("title", title);
        multipartBuilder.addFormDataPart("description", description);
        multipartBuilder.addFormDataPart("start_date", startDate);
        multipartBuilder.addFormDataPart("end_date", endDate);
        multipartBuilder.addFormDataPart("price", price);
        multipartBuilder.addFormDataPart("cities", tahsil);
        multipartBuilder.addFormDataPart("image", imageFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), imageFile));
        multipartBuilder.addFormDataPart("thumb", thumbFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), thumbFile));
        RequestBody body = multipartBuilder.build();
        webServiceCaller.addRequestBody(body);
        webServiceCaller.execute(advertisementAPI);

    }

    private void checsumGenerate() {
        Service = PaytmPGService.getProductionService();
        order_id = initOrderId() + "U" + customer_id;
        Service = PaytmPGService.getProductionService();
        generateCheckSum(mContext,order_id,customer_id,price,this);
    }

    @Override
    public void checkSumResult(String checkSum, JSONObject jsonObject) {
        this.checksum=checkSum;
        onStartTransaction();
    }
    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        int mYear = c2.get(Calendar.YEAR);
        int mMonth = c2.get(Calendar.MONTH);
        int mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";

                        day = checkDigit(dayOfMonth);
                        String formattedDate = day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                        if (from.equals("StartDate")) {
                            tvStartDate.setText(formattedDate);
                        } else {
                            tvEndtDate.setText(formattedDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public String getSimpleDate(String from) {
        String formattedDate = "";
        Calendar c = Calendar.getInstance();
        if (from.equalsIgnoreCase("EndDate")) {
            c.add(Calendar.DAY_OF_YEAR, 10);//  add 10 days in current date

        }
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        formattedDate = df.format(c.getTime());

        return formattedDate;
    }
    public void onStartTransaction() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", MID);
        paramMap.put("ORDER_ID", order_id);
        paramMap.put("CUST_ID", customer_id);
        paramMap.put("CHANNEL_ID", CHANNEL_ID);
        paramMap.put("TXN_AMOUNT", price);
        paramMap.put("WEBSITE", WEBSITE);
        paramMap.put("CALLBACK_URL", CALLBACK_URL + order_id);
        paramMap.put("INDUSTRY_TYPE_ID", INDUSTRY_TYPE_ID);
        paramMap.put("CHECKSUMHASH", checksum);
        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);
        Service.initialize(Order, null);
        Service.startPaymentTransaction(mContext, true, true, this);

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        data = bundleToJson(inResponse).toString();
        moveNextActivity();
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        showToast(mContext, "" + inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        System.out.println("checksum == cancel call back respon ==== " + inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        STATUS = "TXN_FAILURE";
        moveNextActivity();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        System.out.println("====Transaction Cancel :====" + inResponse);
        data = bundleToJson(inResponse).toString();
        STATUS = "TXN_FAILURE";
        moveNextActivity();

    }


    public void moveNextActivity() {

        if (data.length() > 0) {
            try {
                JSONObject object = new JSONObject(data);
                STATUS = object.getString("STATUS");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(mContext, Payment_Status_activity.class);
        i.putExtra("order_id", order_id);
        i.putExtra("amount", price);
        i.putExtra("STATUS", STATUS);
        i.putExtra("type", type);
        i.putExtra("data", data);
        i.putExtra("validity", validity);
        i.putExtra("ad_id", ad_id);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
