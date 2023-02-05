package b2infosoft.milkapp.com.Dairy.Product;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
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
import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.Dairy.Product.Adapter.ProductListAdapter2;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnClickInAdapter;
import b2infosoft.milkapp.com.Interface.RefreshProductsList;
import b2infosoft.milkapp.com.Model.ProductListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCameraIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getGalleryIntent;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showSettingsDialog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class AddProductFragment extends Fragment implements OnClickInAdapter, RefreshProductsList {

    Context mContext;
    String productID = "", productName = "", productRate = "", productWeight = "", product_image, userID = "";
    Toolbar toolbar;
    TextView toolbar_title, tvUploadImage;
    TextView tvAdd;
    EditText et_Search;
    Uri uri;
    Bitmap bitmap;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    RecyclerView recycler_user_productList;
    Dialog dialog;
    EditText etProductName, etRate, etProductWeight;
    ImageView imgUploadProduct;
    Button btnAddProduct;
    SwipeRefreshLayout pullToRefresh;
    ProductListAdapter2 adapter;
    RecyclerView.LayoutManager mLayoutManager;

    View view;
    String filePaths = "";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_product, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        userID = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        initView(view);
        return view;
    }


    private void initView(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        tvAdd = toolbar.findViewById(R.id.tvAdd);
        toolbar_title.setText(getString(R.string.Product));
        et_Search = view.findViewById(R.id.et_Search);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);


        tvAdd.setVisibility(View.VISIBLE);
        tvAdd.setText(mContext.getString(R.string.Add_Product));
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productID = "";
                productRate = "";
                productRate = "";
                dialogAddOrEditProduct();
            }
        });
        getProductDetail();
        toolbarManage();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getProductDetail();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    public void setProductList(final ArrayList<ProductListPojo> productList) {

        recycler_user_productList = view.findViewById(R.id.recycler_user_productList);
        et_Search = view.findViewById(R.id.et_Search);

        if (productList.isEmpty()) {
            UtilityMethod.showAlert(getString(R.string.Products_List_is_Empty_Please_add_Any_Products), mContext);
        }


        initRecycleView(productList);
        adapter.notifyDataSetChanged();

        //  recycler_user_productList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.divider)));
        recycler_user_productList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.e("RecyclerView", "onScrollStateChanged");
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        addTextListener(productList);
    }

    public void addTextListener(final ArrayList<ProductListPojo> productListPojos) {

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();

                final ArrayList<ProductListPojo> filterList = new ArrayList<>();
                for (int i = 0; i < productListPojos.size(); i++) {

                    final String name = productListPojos.get(i).product_name.toLowerCase();
                    final String prodPrice = productListPojos.get(i).product_price.toLowerCase();


                    if (name.contains(query) || prodPrice.contains(query)) {
                        ProductListPojo listPojo = productListPojos.get(i);
                        filterList.add(listPojo);
                    }
                }
                initRecycleView(filterList);
            }
        });
    }

    private void initRecycleView(ArrayList<ProductListPojo> mlist) {

        mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new ProductListAdapter2(mContext, mlist, this, this);
        recycler_user_productList.setLayoutManager(mLayoutManager);
        recycler_user_productList.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // data set changed
    }

    @Override
    public void onClickEditInAdapter(String ID, String itemName, String itemPrice, String Weight, String Rate, String
            Total, String customerID, String unic_customer, String snf, String milk_category, String image) {

        productID = ID;
        productName = itemName;
        productRate = itemPrice;
        productWeight = Weight;
        product_image = image;
        dialogAddOrEditProduct();
    }

    @Override
    public void refreshProductList(ArrayList<ProductListPojo> viewProductsListPojoArrayList) {
        getProductDetail();
        productID = "";
        productName = "";
        productRate = "";
        productWeight = "";
    }


    public void dialogAddOrEditProduct() {

        dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_product);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imgUploadProduct = dialog.findViewById(R.id.imgProduct);

        tvUploadImage = dialog.findViewById(R.id.tvUploadImage);
        etProductName = dialog.findViewById(R.id.etProductName);
        etProductWeight = dialog.findViewById(R.id.etProductWeight);
        etRate = dialog.findViewById(R.id.etRate);

        btnAddProduct = dialog.findViewById(R.id.btnAddProduct);
        if (!productID.equals("")) {
            btnAddProduct.setText(mContext.getString(R.string.UPDATE));

            etProductName.setText(productName);
            etRate.setText(productRate);
            etProductWeight.setText(productWeight);
            String filePaths = BaseImageUrl + product_image;
            uri = Uri.parse(filePaths);
            Glide.with(mContext).load(uri).error(R.drawable.app_icon).into(imgUploadProduct);

        } else {
            imgUploadProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });

            btnAddProduct.setText(getString(R.string.Add_Product));
        }
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productName = etProductName.getText().toString();
                productWeight = etProductWeight.getText().toString();
                productRate = etRate.getText().toString();
                if (productID.length() == 0 && filePaths.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.PleaseUploadImageFile));
                } else if (productName.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Product_Name));
                } else if (productWeight.length() == 0) {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Weight));

                } else {
                    addOrEditProduct();
                }

            }
        });

        imgUploadProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


        dialog.show();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode====" + requestCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (data != null) {
                uri = data.getParcelableExtra("path");
                if (uri != null) {
                    try {
                        filePaths = uri.getPath();
                        bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        Glide.with(this).load(bitmap)
                                .into(imgUploadProduct);
                        imgUploadProduct.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
                        tvUploadImage.setText(mContext.getString(R.string.Edit));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /////////////////////////////////////////////

    public void getProductDetail() {
        final ArrayList<ProductListPojo> listPojos = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        BaseImageUrl = jsonObject.getString("path");
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);

                            listPojos.add(new ProductListPojo(object.getString("id"),
                                    object.getString("product_name"),
                                    object.getString("weight"),
                                    object.getString("product_price"),
                                    object.getString("dairy_id"),
                                    object.getString("qty"),
                                    object.getString("image"), 0, 0));
                        }


                        setProductList(listPojos);


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", userID)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(Constant.getProductListAPI);
    }

    private void addOrEditProduct() {
        if (isNetworkAvaliable(mContext)) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {

                            if (productID.equalsIgnoreCase("")) {

                                UtilityMethod.showAlertBox(mContext, getString(R.string.Product_Added_Successfully));
                                getProductDetail();
                            } else if (!productID.equalsIgnoreCase("")) {
                                productID = "";
                                UtilityMethod.showAlertBox(mContext, getString(R.string.Product_Updated_Successfully));
                                getProductDetail();

                            }
                            productID = "";
                            productName = "";
                            productRate = "";
                            productWeight = "";

                            if (dialog != null) {
                                dialog.dismiss();
                            }
                        } else {
                            if (productID.equals("")) {
                                UtilityMethod.showAlertWithButton(mContext, getString(R.string.Product_Adding_Failed));
                            } else {
                                UtilityMethod.showAlertWithButton(mContext, getString(R.string.Product_Updating_failed));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            multipartBuilder.addFormDataPart("dairy_id", userID);
            multipartBuilder.addFormDataPart("product_name", productName);
            multipartBuilder.addFormDataPart("product_price", productRate);
            multipartBuilder.addFormDataPart("weight", productWeight);
            multipartBuilder.addFormDataPart("qty", "1");

            if (productID.equals("")) {

                File sourceFile = new File(filePaths);
                multipartBuilder.addFormDataPart("image", sourceFile.getName(),
                        RequestBody.create(MediaType.parse("image/jpeg"),
                                sourceFile));

                RequestBody body = multipartBuilder.build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.addProductAPI);

            } else {
                if (filePaths.length() > 0) {
                    File sourceFile = new File(filePaths);
                    multipartBuilder.addFormDataPart("image", sourceFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), sourceFile));

                } else {
                    multipartBuilder.addFormDataPart("image", product_image);
                }

                multipartBuilder.addFormDataPart("product_id", productID);
                RequestBody body = multipartBuilder.build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(Constant.editProductAPI);
            }


        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }
    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

    }


}