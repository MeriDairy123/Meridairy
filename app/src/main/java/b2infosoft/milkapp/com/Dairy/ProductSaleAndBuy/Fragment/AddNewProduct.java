package b2infosoft.milkapp.com.Dairy.ProductSaleAndBuy.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.Model.BeanAddProductItem;
import b2infosoft.milkapp.com.Model.BeanBrandtItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCameraIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getGalleryIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showSettingsDialog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddNewProduct extends Fragment implements View.OnClickListener {
    private static final String TAG = "Product>>>";

    Context mContext;
    Toolbar toolbar;

    Spinner spinGroup, spinBrand, spinUnit;
    EditText ediName, ediWeight, ediPrice, ediSalePrice, ediTax, ediLowStockQuantity, ediOpeningQuantity,
            ediOpeningRate, ediOpeningAmount, ediDescription;
    TextView tvUploadPhoto;
    CheckBox chkboxWeight, chkboxTax;
    Button btnSubmit;
    String strType = "add", productId = "", groupId = "", brandId = "", unitId = "", strName = "",
            strWeight = "", strSalePrice = "", strDescription = "", filePath = "";
    int lowStockQuantity = 0, openingQuantity = 0, weightChecked = 0, taxChecked = 0;
    int groupIdPos = 0, brandIdPos = 0, unitIdPos = 0;
    double openingRate = 0, openingAmount = 0, price = 0, salePrice = 0, tax = 0;
    File file;

    SessionManager sessionManager;

    ArrayList<BeanBrandtItem> groupItem = new ArrayList<>();
    ArrayList<BeanBrandtItem> brandItems = new ArrayList<>();
    ArrayList<BeanBrandtItem> unitItem = new ArrayList<>();

    Bundle bundle = null;
    View view;
    BeanAddProductItem beanAddProductItem;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_product_add, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.Product));

        toolbarManage();
        initView();
        return view;
    }

    private void initView() {
        spinGroup = view.findViewById(R.id.spinGroup);
        spinBrand = view.findViewById(R.id.spinBrand);
        spinUnit = view.findViewById(R.id.spinUnit);
        ediName = view.findViewById(R.id.ediName);
        ediWeight = view.findViewById(R.id.ediWeight);
        ediPrice = view.findViewById(R.id.ediPrice);
        ediSalePrice = view.findViewById(R.id.ediSalePrice);
        ediTax = view.findViewById(R.id.ediTax);
        ediLowStockQuantity = view.findViewById(R.id.ediLowStockQuantity);
        ediOpeningQuantity = view.findViewById(R.id.ediOpeningQuantity);
        ediOpeningRate = view.findViewById(R.id.ediOpeningRate);
        ediDescription = view.findViewById(R.id.ediDescription);
        ediOpeningAmount = view.findViewById(R.id.ediOpeningAmount);
        tvUploadPhoto = view.findViewById(R.id.tvUploadPhoto);
        chkboxWeight = view.findViewById(R.id.chkboxWeight);
        chkboxTax = view.findViewById(R.id.chkboxTax);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        ediTax.setHint(mContext.getString(R.string.tax) + " (%)");
        ediSalePrice.setText("0");
        tvUploadPhoto.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        chkboxWeight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                weightChecked = 0;
                if (b) {
                    weightChecked = 1;
                }
            }
        });
        chkboxTax.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                taxChecked = 0;
                if (b) {
                    taxChecked = 1;
                }

                ediTax.setText(tax + "");
            }
        });

        ediTax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                tax = 0;
                if (editable.length() > 0) {
                    tax = Double.parseDouble(editable.toString());
                }

                calculateTax();
            }
        });
        ediPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                price = 0;
                if (editable.length() > 0) {
                    price = Double.parseDouble(editable.toString());
                }
                calculateTax();
            }
        });
        ediOpeningRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                openingRate = 0;
                if (editable.length() > 0) {
                    openingRate = Double.parseDouble(editable.toString());
                }
                calculateOpeningAmount();
            }
        });
        ediOpeningQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                openingQuantity = 0;
                if (editable.length() > 0) {
                    openingQuantity = Integer.parseInt(editable.toString());
                }
                calculateOpeningAmount();
            }
        });


        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            String json = sessionManager.getValueSesion("beanAddProductItem");
            beanAddProductItem = gson.fromJson(json, BeanAddProductItem.class);
            strType = bundle.getString("type");
            toolbar.setTitle(mContext.getString(R.string.Edit) + " " + mContext.getString(R.string.Product));
            editProduct();
        }
        getProductGroupOrBrand("group", Constant.getProductGroupListAPI);

        getProductGroupOrBrand("unit", Constant.getUnitListAPI);


    }

    private void editProduct() {
        groupId = beanAddProductItem.getItem_group();
        brandId = beanAddProductItem.getItem_brand();
        unitId = beanAddProductItem.getItem_unit();
        productId = beanAddProductItem.getId();
        ediName.setText(beanAddProductItem.getName());
        ediWeight.setText(beanAddProductItem.getItem_weight());
        ediTax.setText(beanAddProductItem.getTax() + "");
        ediPrice.setText(beanAddProductItem.getPrice() + "");
        ediSalePrice.setText(beanAddProductItem.getSales_price() + "");
        ediLowStockQuantity.setText(beanAddProductItem.getLow_stock_alert() + "");
        ediOpeningQuantity.setText(beanAddProductItem.getInitial_quantity() + "");
        ediOpeningRate.setText(beanAddProductItem.getOpening_rate() + "");
        ediOpeningAmount.setText(beanAddProductItem.getOpening_amt() + "");
        ediDescription.setText(beanAddProductItem.getDiscription_product());
        tvUploadPhoto.setText(beanAddProductItem.getImages());
        if (beanAddProductItem.getTax_check() == 1) {
            chkboxTax.setChecked(true);
        }
        if (beanAddProductItem.getWeightc() == 1) {
            chkboxWeight.setChecked(true);
        }

    }

    private void calculateTax() {
        salePrice = 0;
        if (chkboxTax.isChecked()) {
            if (tax > 0 && price > 0) {
                double taxPrice = price / (tax + 100) * tax;
                salePrice = price - taxPrice;
            }
        } else {
            salePrice = price;
        }
        ediSalePrice.setText(new DecimalFormat("#.##").format(salePrice));

    }

    private void calculateOpeningAmount() {
        openingAmount = 0;
        if (openingRate > 0 && openingQuantity > 0) {
            openingAmount = openingRate * openingQuantity;
        }
        ediOpeningAmount.setText(new DecimalFormat("#.##").format(openingAmount));

    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void getProductGroupOrBrand(String strType, String strListUrl) {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    String strSelect = mContext.getString(R.string.select) + " ";
                    ArrayList<String> listItem = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        if (strType.equalsIgnoreCase("group")) {
                            System.out.println("groupId===" + groupId);
                            groupItem = new ArrayList<>();
                            strSelect = strSelect + mContext.getString(R.string.group);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            listItem.add(strSelect);
                            groupItem.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("category_name"));
                                groupItem.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("category_name"), jobj.getString("code")));

                                if (groupId.equalsIgnoreCase(jobj.getString("id"))) {
                                    groupIdPos = i + 1;
                                }
                            }
                            initSpinGroup(listItem);


                        } else if (strType.equalsIgnoreCase("brand")) {
                            brandItems = new ArrayList<>();
                            strSelect = strSelect + mContext.getString(R.string.brand);
                            System.out.println("brandId===" + brandId);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            listItem.add(strSelect);
                            brandItems.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("name"));
                                if (brandId.equalsIgnoreCase(jobj.getString("id"))) {
                                    brandIdPos = i + 1;
                                }
                                brandItems.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("name"), ""));
                            }

                            initSpinBrand(listItem);

                        } else if (strType.equalsIgnoreCase("unit")) {
                            unitItem = new ArrayList<>();
                            strSelect = strSelect + mContext.getString(R.string.unit);
                            System.out.println("unitId===" + unitId);
                            JSONArray jsonData = jsonObject.getJSONArray("data");
                            listItem.add(strSelect);
                            unitItem.add(new BeanBrandtItem("", strSelect, ""));
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                listItem.add(jobj.getString("unit_name"));
                                if (unitId.equalsIgnoreCase(jobj.getString("id"))) {
                                    unitIdPos = i + 1;
                                }
                                unitItem.add(new BeanBrandtItem(jobj.getString("id"),
                                        jobj.getString("unit_name"), ""));
                            }
                            initSpinUnit(listItem);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("category_id", groupId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(strListUrl);

    }

    private void initSpinGroup(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinGroup.setAdapter(spinAdapter);
        spinGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    groupId = groupItem.get(position).id;
                    getProductGroupOrBrand("brand", Constant.getProductBrandListAPI);
                } else {
                    groupId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinGroup.setSelection(groupIdPos);
    }

    private void initSpinBrand(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinBrand.setAdapter(spinAdapter);
        spinBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    brandId = brandItems.get(position).id;
                } else {
                    brandId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinBrand.setSelection(brandIdPos);

    }

    private void initSpinUnit(ArrayList<String> listItem) {

        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, listItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinUnit.setAdapter(spinAdapter);
        spinUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    unitId = unitItem.get(position).id;
                } else {
                    unitId = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinUnit.setSelection(unitIdPos);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                Uri uri = data.getParcelableExtra("path");
                if (uri != null) {
                    try {
                        filePath = uri.getPath();
                        file = new File(filePath);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        tvUploadPhoto.setText(file.getName());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvUploadPhoto:
                Dexter.withActivity((Activity) mContext)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {

                                    showImagePickerOptions();
                                }
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog(((Activity) mContext));
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

                break;
            case R.id.btnSubmit:
                strName = ediName.getText().toString().trim();
                strWeight = ediWeight.getText().toString().trim();
                strSalePrice = ediSalePrice.getText().toString().trim();

                String select = mContext.getString(R.string.select) + " ";
                if (groupId.length() == 0) {
                    showAlertWithButton(mContext, select + mContext.getString(R.string.group));
                } else if (brandId.length() == 0) {
                    showAlertWithButton(mContext, select + mContext.getString(R.string.brand));
                } else if (unitId.length() == 0) {
                    showAlertWithButton(mContext, select + mContext.getString(R.string.unit));
                } else if (strName.length() == 0) {
                    showAlertWithButton(mContext, mContext.getString(R.string.PleaseEnterName));
                } else if (strWeight.length() == 0) {
                    ediName.clearFocus();
                    ediWeight.requestFocus();
                    showAlertWithButton(mContext, mContext.getString(R.string.Weight));
                } else {
                    addProduct();
                }
        }
    }

    private void showImagePickerOptions() {
        ImagePickerAcitvity.showImagePickerOptions(mContext, new ImagePickerAcitvity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                Intent intent = getCameraIntent(getActivity());
                startActivityForResult(intent, REQUEST_IMAGE);
            }

            @Override
            public void onChooseGallerySelected() {
                Intent intent = getGalleryIntent(getActivity());
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
    }

    public void addProduct() {

        @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        showToast(mContext, jsonObject.getString("user_status_message"));
                        getActivity().onBackPressed();
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, jsonObject.getString("user_status_message"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        multipartBuilder.addFormDataPart("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
        multipartBuilder.addFormDataPart("type", strType);
        multipartBuilder.addFormDataPart("item_group", groupId);
        multipartBuilder.addFormDataPart("item_brand", brandId);
        multipartBuilder.addFormDataPart("item_unit", unitId);
        multipartBuilder.addFormDataPart("name", strName);
        multipartBuilder.addFormDataPart("item_weight", strWeight);
        multipartBuilder.addFormDataPart("weightc", weightChecked + "");
        multipartBuilder.addFormDataPart("sales_old", price + "");
        multipartBuilder.addFormDataPart("sales_price", strSalePrice);
        multipartBuilder.addFormDataPart("tax", tax + "");
        multipartBuilder.addFormDataPart("tax_check", taxChecked + "");

        multipartBuilder.addFormDataPart("low_stock_alert", lowStockQuantity + "");
        multipartBuilder.addFormDataPart("opening_rate", openingRate + "");
        multipartBuilder.addFormDataPart("initial_quantity", openingQuantity + "");
        multipartBuilder.addFormDataPart("opening_amt", openingAmount + "");
        multipartBuilder.addFormDataPart("discription_product", strDescription + "");
        if (strType.equalsIgnoreCase("edit")) {
            multipartBuilder.addFormDataPart("product_id", productId);
        }
        if (file != null) {
            multipartBuilder.addFormDataPart("images", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file));
        }
        RequestBody body = multipartBuilder.build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.addUserProductAPI);
    }

}
