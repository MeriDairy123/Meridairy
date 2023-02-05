package b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.getPaymentCallBackAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.orderSaveAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

/**
 * Created by Choudhary on 20-July-19.
 */

public class fragment_PaymentStatus extends Fragment {

    Context mContext;
    ImageView imgStatus;
    TextView tvMessage;
    TextView tvAmount, tvOrderId, tvValidity;
    Button btnDone;
    String order_id = "", amount = "";
    SessionManager sessionManager;

    String type = "", STATUS = "TXN_FAILURE";
    String data = "";
    Bundle bundle;
    String invoiceId = "";
    JSONObject jsonOrder;
    View layoutValidity;
    DatabaseHandler db;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customer_payment_status, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        db = DatabaseHandler.getDbHelper(mContext);
        layoutValidity = view.findViewById(R.id.layoutValidity);
        imgStatus = view.findViewById(R.id.imgStatus);
        tvMessage = view.findViewById(R.id.tvMessage);
        tvAmount = view.findViewById(R.id.tvAmount);
        tvOrderId = view.findViewById(R.id.tvOrderId);
        tvValidity = view.findViewById(R.id.tvValidity);
        btnDone = view.findViewById(R.id.btnDone);

        bundle = getArguments();
        order_id = bundle.getString("order_id");
        amount = bundle.getString("amount");
        STATUS = bundle.getString("STATUS");
        type = bundle.getString("type");
        data = bundle.getString("data");


        if (type.equalsIgnoreCase("product")) {
            layoutValidity.setVisibility(View.GONE);

            String jsonObject = bundle.getString("jsonObject");
            try {
                jsonOrder = new JSONObject(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (STATUS.equalsIgnoreCase("TXN_SUCCESS")) {

                placeOrder();
                db.deleteBuyerCartTable();
            }
        }

        if (STATUS.equalsIgnoreCase("TXN_SUCCESS")) {
            imgStatus.setImageResource(R.drawable.ic_checked);
            tvMessage.setText(mContext.getString(R.string.PaymentSuccessful));
            tvMessage.setTextColor(getResources().getColor(R.color.color_Green));

        } else {
            imgStatus.setImageResource(R.drawable.ic_failure);
            tvMessage.setText(mContext.getString(R.string.PaymentFailed));
            tvMessage.setTextColor(getResources().getColor(R.color.colorRed));
            btnDone.setText(mContext.getString(R.string.TryAgain));
        }
        Animation animation = new AlphaAnimation((float) 0.7, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
        imgStatus.startAnimation(animation);

        tvAmount.setText(mContext.getString(R.string.Rupee_symbol) + " " + amount);
        tvOrderId.setText(order_id);

        SendOrderPaymentStatus();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    private void SendOrderPaymentStatus() {

        @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext,
                "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
            }
        };
        if (type.equalsIgnoreCase("invoice")) {
            invoiceId = bundle.getString("invoice_id");
        }
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("order_id", order_id)
                .addEncoded("STATUS", STATUS)
                .addEncoded("data", data)
                .addEncoded("type", type)
                .addEncoded("invoice_id", invoiceId)
                .build();
        caller.addRequestBody(body);
        caller.execute(getPaymentCallBackAPI);
    }

    private void placeOrder() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Submitting order", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            System.out.println("jsonOrder===" + jsonOrder.toString());
            RequestBody body = RequestBody.create(JSONMediaType, jsonOrder.toString());
            webServiceCaller.addRequestBody(body);
            webServiceCaller.execute(orderSaveAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}