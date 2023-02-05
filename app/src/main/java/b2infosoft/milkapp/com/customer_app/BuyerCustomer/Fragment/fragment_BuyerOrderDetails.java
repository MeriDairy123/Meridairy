package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.okhttp.FormEncodingBuilder;
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

import b2infosoft.milkapp.com.Model.BeanOrderItem;
import b2infosoft.milkapp.com.Model.BeanOrderProductItem;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderCancelAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getOrderDeliveryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getShippingDetailsAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dataFormat;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getColoredSpanned;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isExternalStorage;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class fragment_BuyerOrderDetails extends Fragment {

    public static final int progress_bar_type = 0;
    Context mContext;
    ImageView imgRecieved_Pending, imgShipped, imgDelivered;
    View viewLine1, viewLine2;
    TextView tvOrderId, tvOrderDate, tvGrossTotalAmount;
    TextView tvRecieved_Pending, tvShipped, tvDelivered;
    TextView tvViewInvoice, tvTrack;
    View view;
    Toolbar toolbar;
    String imgUrl = "", OrderId = "", orderStatus = "", orderDate = "";
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_order_details, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        productItemList = new ArrayList<>();

        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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
        toolbar.setTitle(mContext.getString(R.string.order_Details));
        // Set Data
        Gson gson = new Gson();
        String json = sessionManager.getValueSesion("beanOrderItem");
        BeanOrderItem beanOrderItem = gson.fromJson(json, BeanOrderItem.class);
        productItemList = beanOrderItem.mOrderProductList;
        orderStatus = beanOrderItem.order_status;
        manageorderStatus(orderStatus);
        OrderId = beanOrderItem.order_id;
        getOrderDetails(mContext);

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
        tvShipped.setText(mContext.getString(R.string.shipped));
        if (orderStatus.equalsIgnoreCase("Received") || orderStatus.equalsIgnoreCase("Pending")) {

            tvRecieved_Pending.setText(orderStatus);
            if (orderStatus.equalsIgnoreCase("Pending")) {
                imgRecieved_Pending.setImageResource(R.drawable.ic_order_progress);
                imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.colorDarkOrange), android.graphics.PorterDuff.Mode.SRC_IN);

            } else {
                imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);
                imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            }

        } else if (orderStatus.equalsIgnoreCase("shipped")) {
            tvTrack.setVisibility(View.VISIBLE);
            tvTrack.setText(mContext.getString(R.string.TrackingOrder));
            imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);
            imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine1.setBackgroundResource(R.color.color_Green);
            imgShipped.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (orderStatus.equalsIgnoreCase("delivered")) {
            tvTrack.setVisibility(View.VISIBLE);
            tvTrack.setText(mContext.getString(R.string.printInvoice));
            imgRecieved_Pending.setImageResource(R.drawable.ic_confirm);

            imgRecieved_Pending.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine1.setBackgroundResource(R.color.color_Green);
            imgShipped.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);
            viewLine2.setBackgroundResource(R.color.color_Green);
            imgDelivered.setColorFilter(ContextCompat.getColor(mContext, R.color.color_Green), android.graphics.PorterDuff.Mode.SRC_IN);

        } else if (orderStatus.equalsIgnoreCase("Canceled")) {
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

    public void getOrderDetails(final Context mContext) {

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
            String userid = sessionManager.getValueSesion(SessionManager.KEY_CustomerUserID);
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("user_id", userid)
                    .addEncoded("order_id", OrderId)
                    .build();
            caller.addRequestBody(body);

            if (orderStatus.equalsIgnoreCase("shipped")) {

                caller.execute(getShippingDetailsAPI);
            } else if (orderStatus.equalsIgnoreCase("delivered")) {
                caller.execute(getOrderDeliveryAPI);
            } else if (orderStatus.equalsIgnoreCase("Canceled")) {
                caller.execute(getOrderCancelAPI);
            }
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
        RequestOptions requestOption = new RequestOptions()
                .placeholder(R.color.color_light_white).centerCrop();
        Glide.with(mContext)
                .load(fileURL)

                .thumbnail(Glide.with(mContext).load(R.drawable.preloader).apply(requestOption))

                .placeholder(R.drawable.ic_shop)
                .error(R.drawable.app_icon)

                .into(imginvoice);

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
                        Log.d("createNewFile==", invoiceFile.getAbsolutePath());
                        // DownloadPdfFile( mContext, fileURL,  invoiceFile);
                        new DownloadFileFromURL().execute(fileURL, invoiceFile.getName());

                    } else {
                        Log.d("file exist==", invoiceFile.getAbsolutePath());

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

          /*  holder.itemView.setTranslationY(-(100 + position * 100));
            holder.itemView.setAlpha(0.5f);
            holder.itemView.animate().alpha(1f).translationY(0).setDuration(700).start();
*/

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
            // Displaying downloaded image into image view
            // Reading image path from sdcard

        }

    }

}
