package b2infosoft.milkapp.com.Shopping;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import b2infosoft.milkapp.com.Advertisement.ImagePickerAcitvity;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.Model.BeanRecievedOrderItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.dairyUpdateCustomersOrderStatusAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderCancelAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderDeliveryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getShippingDetailsAPI;
import static b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerOrderDetails.progress_bar_type;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_GALLERY_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.REQUEST_IMAGE_CAPTURE;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dataFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isExternalStorage;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showSettingsDialog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_CustRecievedOrderDetails extends Fragment implements View.OnClickListener {


    Context mContext;
    ImageView imgRecieved_Pending, imgShipped, imgDelivered, imgUploadOrder;

    View viewLine1, viewLine2;
    TextView tvActionStatus, tvOrderAction, tvOrderDecline, tvUploadImage;
    TextView tvOrderId, tvOrderDate, tvGrossTotalAmount;
    TextView tvRecieved_Pending, tvShipped, tvDelivered;
    TextView tvViewInvoice, tvTrack;
    View view, layoutOrderAction, layoutDialogImage;
    Toolbar toolbar;
    String imgUrl = "", strUpdateStatus = "", OrderId = "", orderStatus = "", orderDate = "";
    String tracking_id = "", shipping_details = "", shippingDate = "", updateShippingDate = "";
    String delivery_proof = "", deliveryDate = "";
    String cancelReason = "", cancelDate = "";
    RecyclerView recyclerView;
    List<BeanOrderProductItem> productItemList;

    Product_item_adapter adapter;

    File invoiceFile;
    ProgressDialog pDialog;
    String invoice_url = "";
    SessionManager sessionManager;
    String dairyId = "", customerId = "", customerName = "", strComment = "";
    Uri uri;
    String filePaths = "";
    Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customers_recieved_order_details, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        productItemList = new ArrayList<>();

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        layoutOrderAction = view.findViewById(R.id.layoutOrderAction);
        tvActionStatus = view.findViewById(R.id.tvActionStatus);
        tvOrderAction = view.findViewById(R.id.tvOrderAction);
        tvOrderDecline = view.findViewById(R.id.tvOrderDecline);


        imgRecieved_Pending = view.findViewById(R.id.imgRecieved_Pending);
        imgShipped = view.findViewById(R.id.imgShipped);
        imgDelivered = view.findViewById(R.id.imgDelivered);
        viewLine1 = view.findViewById(R.id.viewLine1);
        viewLine2 = view.findViewById(R.id.viewLine2);

        tvRecieved_Pending = view.findViewById(R.id.tvRecieved_Pending);
        tvShipped = view.findViewById(R.id.tvShipped);
        tvDelivered = view.findViewById(R.id.tvDelivered);
        tvViewInvoice = view.findViewById(R.id.tvViewInvoice);
        tvTrack = view.findViewById(R.id.tvTrack);

        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvOrderDate = view.findViewById(R.id.tvOrderDate);

        recyclerView = view.findViewById(R.id.recyclerView);
        tvGrossTotalAmount = view.findViewById(R.id.tvGrossTotalAmount);
        // Set Data
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanRecievedOrderItem");
        BeanRecievedOrderItem beanOrderItem = gson.fromJson(json, BeanRecievedOrderItem.class);
        customerId = beanOrderItem.customer_id;
        customerName = beanOrderItem.name;
        productItemList = beanOrderItem.mOrderProductList;
        orderStatus = beanOrderItem.order_status;
        manageorderStatus(orderStatus);
        OrderId = beanOrderItem.order_id;
        toolbar.setTitle(customerName + " " + mContext.getString(R.string.order_Details));
        tvOrderAction.setOnClickListener(this);
        tvOrderDecline.setOnClickListener(this);
        getOrderDetails();

        orderDate = beanOrderItem.order_date;
        invoice_url = beanOrderItem.invoice_url;
        tvOrderId.setText(Html.fromHtml("#" + OrderId));
        tvOrderDate.setText(Html.fromHtml(dataFormat(orderDate)));
        tvGrossTotalAmount.setText(Double.toString(beanOrderItem.grandtotal));

        tvTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialogOrderShip();
            }
        });
        tvViewInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(invoice_url));
                startActivity(i);
            }
        });
        adapter = new Product_item_adapter(mContext, productItemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }


    private void manageorderStatus(String orderStatus) {

        tvActionStatus.setText(mContext.getString(R.string.Order) + " " + mContext.getString(R.string.Action));
        tvShipped.setText(mContext.getString(R.string.shipped));
        System.out.println("Manage===orderStatus==" + orderStatus);
        if (orderStatus.equalsIgnoreCase("received") ||
                orderStatus.equalsIgnoreCase("Recieved") ||
                orderStatus.equalsIgnoreCase("Pending")) {

            tvRecieved_Pending.setText(orderStatus);
            if (orderStatus.equalsIgnoreCase("rending")) {
                tvOrderAction.setText(mContext.getString(R.string.Accept));
                tvOrderDecline.setVisibility(View.VISIBLE);
                imgRecieved_Pending.setImageResource(R.drawable.ic_order_progress);
                imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDarkOrange), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                tvOrderAction.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.shipped));
                tvOrderAction.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_blue));
                tvOrderDecline.setVisibility(View.GONE);

                imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);
                imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            }

        } else if (orderStatus.equalsIgnoreCase("shipped")) {
            tvOrderAction.setText(mContext.getString(R.string.Add) + " " + mContext.getString(R.string.delivered));
            tvOrderAction.setBackground(mContext.getResources().getDrawable(R.drawable.btn_shape_blue));
            tvOrderDecline.setVisibility(View.GONE);
            tvTrack.setVisibility(View.VISIBLE);
            tvTrack.setText(mContext.getString(R.string.TrackingOrder));
            imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);
            imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine1.setBackgroundResource(R.color.color_Green);
            imgShipped.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (orderStatus.equalsIgnoreCase("delivered")) {
            tvOrderAction.setVisibility(View.GONE);
            tvOrderDecline.setVisibility(View.GONE);
            tvActionStatus.setText(mContext.getString(R.string.Order) + " " + mContext.getString(R.string.delivered));
            tvTrack.setVisibility(View.VISIBLE);
            tvTrack.setText(mContext.getString(R.string.printInvoice));
            imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);

            imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine1.setBackgroundResource(R.color.color_Green);
            imgShipped.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine2.setBackgroundResource(R.color.color_Green);
            imgDelivered.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (orderStatus.equalsIgnoreCase("canceled")) {

            tvActionStatus.setText(mContext.getString(R.string.Order) + " " + mContext.getString(R.string.cancelled));
            tvShipped.setText(mContext.getString(R.string.cancelled));
            tvTrack.setVisibility(View.VISIBLE);
            tvTrack.setText(mContext.getString(R.string.cancelReason));
            imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);
            imgShipped.setImageResource(R.drawable.ic_failure);
            imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine1.setBackgroundResource(R.color.colorRed);
            //  imgShipped.setColorFilter(ContextCompat.getColor(mContext, R.color.colorRed), android.graphics.PorterDuff.Mode.SRC_IN);

        }
    }

    public void getOrderDetails() {

        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Order List data...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            JSONObject objectData = jsonObject.getJSONObject("data");
                            if (orderStatus.equalsIgnoreCase("shipped")) {
                                tracking_id = objectData.getString("tracking_id");
                                shipping_details = objectData.getString("shipping_details");
                                shippingDate = objectData.getString("created_at");
                                delivery_proof = objectData.getString("delivery_proof");
                                updateShippingDate = objectData.getString("updated_at");
                            } else if (orderStatus.equalsIgnoreCase("delivered")) {

                                delivery_proof = objectData.getString("delivery_proof");
                                deliveryDate = objectData.getString("created_at");
                            } else if (orderStatus.equalsIgnoreCase("Canceled")) {
                                cancelReason = objectData.getString("comment");
                                cancelDate = objectData.getString("created_at");
                            }

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("user_id", customerId)
                    .addEncoded("order_id", OrderId)
                    .build();
            caller.addRequestBody(body);


            if (orderStatus.equalsIgnoreCase("shipped")) {

                caller.execute(getShippingDetailsAPI);
            } else if (orderStatus.equalsIgnoreCase("delivered")) {
                caller.execute(getOrderDeliveryAPI);
            } else if (orderStatus.equalsIgnoreCase("canceled")) {
                caller.execute(getOrderCancelAPI);
            }
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }

    public void setOrderAction() {

        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                            orderStatus = strUpdateStatus;
                            filePaths = "";
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            manageorderStatus(orderStatus);
                            getOrderDetails();

                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            MultipartBuilder multipartBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);

            multipartBuilder.addFormDataPart("customer_id", customerId);
            multipartBuilder.addFormDataPart("order_id", OrderId);
            multipartBuilder.addFormDataPart("dairy_id", dairyId);
            multipartBuilder.addFormDataPart("order_status", strUpdateStatus);
            if (strUpdateStatus.equalsIgnoreCase("cancelled")) {
                multipartBuilder.addFormDataPart("comment", strComment);
            } else if (strUpdateStatus.equalsIgnoreCase("shipped")
                    || strUpdateStatus.equalsIgnoreCase("Delivered")) {

                if (strUpdateStatus.equalsIgnoreCase("shipped")) {
                    multipartBuilder.addFormDataPart("tracking_id", tracking_id);
                    multipartBuilder.addFormDataPart("shipping_details", strComment);
                } else if (strUpdateStatus.equalsIgnoreCase("delivered")) {
                    multipartBuilder.addFormDataPart("additional_details", strComment);
                }
                if (filePaths.length() > 0) {
                    File sourceFile = new File(filePaths);
                    multipartBuilder.addFormDataPart("signature_image", sourceFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), sourceFile));

                } else {
                    multipartBuilder.addFormDataPart("delivery_proof", filePaths);
                }

            }
            RequestBody body = multipartBuilder.build();
            caller.addRequestBody(body);


            caller.execute(dairyUpdateCustomersOrderStatusAPI);
        } else {
            showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
        }

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(mContext);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvOrderAction:
                strUpdateStatus = "";
                if (orderStatus.equalsIgnoreCase("pending")) {
                    strUpdateStatus = "received";

                    setOrderAction();
                } else {
                    dialogAddOrderStatus();
                }
                break;
            case R.id.tvOrderDecline:
                if (orderStatus.equalsIgnoreCase("pending")) {
                    dialogAddOrderStatus();
                }
                break;
        }

    }

    public void dialogAddOrderStatus() {
        TextView tvDialogTitle, ediTrackID, ediComment;
        Button btnSubmit;
        dialog = new Dialog(mContext, android.R.style.Theme_DeviceDefault_Dialog_Alert);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_receive_customer_order_action);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        strUpdateStatus = "";
        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        layoutDialogImage = dialog.findViewById(R.id.layoutImage);
        imgUploadOrder = dialog.findViewById(R.id.imgUploadOrder);

        tvUploadImage = dialog.findViewById(R.id.tvUploadImage);
        ediComment = dialog.findViewById(R.id.ediComment);
        ediTrackID = dialog.findViewById(R.id.ediTrackID);

        btnSubmit = dialog.findViewById(R.id.btnSubmit);
        if (orderStatus.equalsIgnoreCase("Pending")) {
            strUpdateStatus = "cancelled";

            ediComment.setHint(mContext.getString(R.string.cancelReason));
            layoutDialogImage.setVisibility(View.GONE);
            ediTrackID.setVisibility(View.GONE);

        } else if (orderStatus.equalsIgnoreCase("received")) {
            strUpdateStatus = "shipped";
            layoutDialogImage.setVisibility(View.VISIBLE);
            ediTrackID.setVisibility(View.VISIBLE);
        } else if (orderStatus.equalsIgnoreCase("Shipped")) {
            strUpdateStatus = "delivered";
            ediTrackID.setVisibility(View.GONE);
            layoutDialogImage.setVisibility(View.VISIBLE);

        }
        tvDialogTitle.setText(strUpdateStatus + " " + mContext.getString(R.string.Order));
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strComment = ediComment.getText().toString();
                tracking_id = ediTrackID.getText().toString();

                if (orderStatus.equalsIgnoreCase("pending")) {

                    if (strComment.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.cancelReason));
                    } else {
                        setOrderAction();
                    }
                } else if (orderStatus.equalsIgnoreCase("received")) {

                    if (tracking_id.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.TrackingId));
                    } else if (strComment.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Description));
                    } else {
                        setOrderAction();
                    }
                } else if (orderStatus.equalsIgnoreCase("shipped")) {

                    if (strComment.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Description));
                    } else {
                        setOrderAction();
                    }
                }

            }
        });

        imgUploadOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity((Activity) mContext)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {

                                    showImagePickerOptions(getActivity());
                                }
                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog(getActivity());
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

    public void showImagePickerOptions(Activity context) {
        ImagePickerAcitvity.showImagePickerOptions(context, new ImagePickerAcitvity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                Intent intent = new Intent(context, ImagePickerAcitvity.class);
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

            @Override
            public void onChooseGallerySelected() {
                Intent intent = new Intent(context, ImagePickerAcitvity.class);
                intent.putExtra(ImagePickerAcitvity.INTENT_IMAGE_PICKER_OPTION, REQUEST_GALLERY_IMAGE);
                // setting aspect ratio
                intent.putExtra(ImagePickerAcitvity.INTENT_LOCK_ASPECT_RATIO, true);
                intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
                intent.putExtra(ImagePickerAcitvity.INTENT_ASPECT_RATIO_Y, 1);
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
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                        Glide.with(this).load(bitmap)
                                .into(imgUploadOrder);
                        imgUploadOrder.setColorFilter(ContextCompat.getColor(mContext, android.R.color.transparent));
                        tvUploadImage.setText(mContext.getString(R.string.Edit));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void bottomDialogOrderShip() {

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity());
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.bottonsheet_order, null);

        mBottomSheetDialog.setContentView(sheetView);

        TextView tvClose;
        TextView tvDate, tvTrackId, tvLabelShipOrCancel, tvshipDetails, tvDelivery;
        View layoutShipOrCancelReason;
        ImageView imginvoice;
        tvClose = mBottomSheetDialog.findViewById(R.id.tvClose);
        tvDate = mBottomSheetDialog.findViewById(R.id.tvDate);
        tvTrackId = mBottomSheetDialog.findViewById(R.id.tvTrackId);
        layoutShipOrCancelReason = mBottomSheetDialog.findViewById(R.id.layoutShipOrCancelReason);
        tvLabelShipOrCancel = mBottomSheetDialog.findViewById(R.id.tvLabelShipOrCancel);
        tvshipDetails = mBottomSheetDialog.findViewById(R.id.tvshipDetails);
        tvDelivery = mBottomSheetDialog.findViewById(R.id.tvDelivery);
        imginvoice = mBottomSheetDialog.findViewById(R.id.imginvoice);
        String date = "", trackId = "", deliveryImagePath;
        String fileURL = BaseImageUrl + delivery_proof;
        System.out.println("delivery_proof======" + delivery_proof);
        if (delivery_proof.length() > 0 || !delivery_proof.equalsIgnoreCase("default.jpg")) {
            RequestOptions requestOption = new RequestOptions()
                    .placeholder(R.color.color_light_white).centerCrop();
            Glide.with(mContext)
                    .load(fileURL)

                    .thumbnail(Glide.with(mContext).load(R.drawable.preloader).apply(requestOption))

                    .placeholder(R.drawable.ic_shop)
                    .error(R.drawable.app_icon)

                    .into(imginvoice);
        } else {
            imginvoice.setVisibility(View.GONE);
        }
        String lableDate = mContext.getString(R.string.Date);

        if (orderStatus.equalsIgnoreCase("shipped")) {
            tvTrackId.setVisibility(View.VISIBLE);
            layoutShipOrCancelReason.setVisibility(View.VISIBLE);
            date = getColoredSpanned(lableDate + ": ", "#000000") + shippingDate;
            trackId = getColoredSpanned("Tracking id: ", "#000000") + tracking_id;

            tvDate.setText(Html.fromHtml(date));
            tvTrackId.setText(Html.fromHtml(trackId));
            tvLabelShipOrCancel.setText(mContext.getString(R.string.shipped) + " " + mContext.getString(R.string.Description) + ": ");
            tvshipDetails.setText(Html.fromHtml(shipping_details));
            tvDelivery.setVisibility(View.VISIBLE);
        } else if (orderStatus.equalsIgnoreCase("Delivered")) {
            date = getColoredSpanned(lableDate + ": ", "#000000") + deliveryDate;
            tvDate.setText(Html.fromHtml(date));
            tvDelivery.setVisibility(View.VISIBLE);
        } else if (orderStatus.equalsIgnoreCase("Canceled")) {

            layoutShipOrCancelReason.setVisibility(View.VISIBLE);
            date = getColoredSpanned(lableDate + ": ", "#000000") + cancelDate;
            tvDate.setText(Html.fromHtml(date));
            tvLabelShipOrCancel.setText(mContext.getString(android.R.string.cancel) + " " + mContext.getString(R.string.reason) + ": ");
            tvshipDetails.setText(Html.fromHtml(cancelReason));
        }
        tvDelivery.setVisibility(View.GONE);
        tvDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String folder_main = mContext.getString(R.string.app_name);
                boolean issdcard = false;
                issdcard = isExternalStorage();

                String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                String internalStorageDirectory = mContext.getFilesDir().toString();
                File filesDir;

                if (issdcard) {
                    filesDir = new File(extStorageDirectory, folder_main);
                } else {
                    filesDir = new File(internalStorageDirectory, folder_main);
                }
                if (!filesDir.exists()) {
                    filesDir.mkdirs();
                }
                invoiceFile = new File(filesDir, delivery_proof);
                try {
                    if (!invoiceFile.exists() || invoiceFile.length() == 0) {
                        invoiceFile.createNewFile();
                        // DownloadPdfFile( mContext, fileURL,  invoiceFile);
                        new DownloadFileFromURL().execute(fileURL, invoiceFile.getName());

                    }

                } catch (IOException e) {

                    e.printStackTrace();
                }

            }
        });
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();
            }
        });

        mBottomSheetDialog.show();
    }

    class Product_item_adapter extends RecyclerView.Adapter<Product_item_adapter.MyViewHolder> {

        private Context mContext;
        private List<BeanOrderProductItem> albumList;

        public Product_item_adapter(Context mContext, List<BeanOrderProductItem> albumList) {
            this.mContext = mContext;
            this.albumList = albumList;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_order_row_item, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final BeanOrderProductItem album = albumList.get(position);
            holder.tvTitle.setText(album.product_name);
            holder.tvDesc.setText("Quantity :  " + album.qty);
            holder.tvPrice.setText("\u20B9  " + album.total_price);

            String url = BaseImageUrl + album.image;

            Glide.with(mContext)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.preloader)
                    .into(holder.image);

        }

        @Override
        public int getItemCount() {
            return albumList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            public TextView tvTitle, tvDesc, tvPrice;
            public ImageView image;

            public MyViewHolder(View view) {
                super(view);
                tvTitle = view.findViewById(R.id.tvTitle);
                tvDesc = view.findViewById(R.id.tvDesc);
                image = view.findViewById(R.id.image);
                tvPrice = view.findViewById(R.id.tvPrice);
            }
        }


    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        public DownloadFileFromURL() {
        }

        /* Before starting background thread
                  Show Progress Bar Dialog
                  */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onCreateDialog(progress_bar_type);
        }

        /*
           Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... strings) {
            int count;
            try {
                URL url = new URL(strings[0]);
                String fileName = strings[1];
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();
                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(invoiceFile);

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /*
           Updating progress bar*/

        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

       /*
          After completing background task
          Dismiss the progress dialog*/

        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            pDialog.dismiss();
            showToast(mContext, "Download Successful ");
            showToast(mContext, invoiceFile.getAbsolutePath());


        }

    }

}
