package b2infosoft.milkapp.com.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus;
import static b2infosoft.milkapp.com.appglobal.Constant.getPaymentCallBackAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.orderSaveAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextClass;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

/**
 * Created by Choudhary on 27-FEB-19.
 */

public class Payment_Status_activity extends Activity {

    Context mContext;
    ImageView imgStatus;
    TextView tvMessage;
    TextView tvAmount, tvOrderId, tvValidity;
    Button btnDone;
    String order_id = "", validity = "", amount = "";
    SessionManager sessionManager;

    String CUST_ID = "", ad_id = "", type = "", STATUS = "TXN_FAILURE";
    String data = "";
    Intent intent;
    JSONObject jsonOrder;
    View layoutValidity;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_status);
        mContext = Payment_Status_activity.this;
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);
        db = DatabaseHandler.getDbHelper(mContext);
        layoutValidity = findViewById(R.id.layoutValidity);
        imgStatus = findViewById(R.id.imgStatus);
        tvMessage = findViewById(R.id.tvMessage);
        tvAmount = findViewById(R.id.tvAmount);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvValidity = findViewById(R.id.tvValidity);
        btnDone = findViewById(R.id.btnDone);
        intent = getIntent();
        order_id = intent.getStringExtra("order_id");
        amount = intent.getStringExtra("amount");
        STATUS = intent.getStringExtra("STATUS");
        type = intent.getStringExtra("type");
        data = intent.getStringExtra("data");
        if (type.equalsIgnoreCase("product")) {
            layoutValidity.setVisibility(View.GONE);
            String jsonObject = intent.getStringExtra("jsonObject");
            try {
                jsonOrder = new JSONObject(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (STATUS.equalsIgnoreCase("TXN_SUCCESS")) {
                placeOrder();
                db.deleteCartTable();
            }
        } else if (type.equalsIgnoreCase("dairy_advertise")) {
            ad_id = intent.getStringExtra("ad_id");
            layoutValidity.setVisibility(View.GONE);
        } else {
            layoutValidity.setVisibility(View.VISIBLE);
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
        if (type.equalsIgnoreCase("sms")) {
            tvValidity.setText("UnLimited");
        } else {
            validity = intent.getStringExtra("validity");
            tvValidity.setText(validity + " " + mContext.getString(R.string.day));
        }
        SendOrderPaymentStatus();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextClass(mContext, MainActivity.class);
            }
        });
    }

    private void SendOrderPaymentStatus() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", false) {
            @Override
            public void handleResponse(String response) {
                checkUserPlanExpiryStatus(mContext);
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("order_id", order_id)
                .addEncoded("STATUS", STATUS)
                .addEncoded("data", data)
                .addEncoded("type", type)
                .addEncoded("ad_id", ad_id)
                .build();
        caller.addRequestBody(body);
        caller.execute(getPaymentCallBackAPI);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (STATUS.equalsIgnoreCase("TXN_FAILURE")) {

        } else {
            goNextClass(mContext, MainActivity.class);
        }
    }

    private void placeOrder() {
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Submitting order", false) {
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

            RequestBody body = RequestBody.create(JSONMediaType, jsonOrder.toString());
            caller.addRequestBody(body);
            caller.execute(orderSaveAPI);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}