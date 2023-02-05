package b2infosoft.milkapp.com.customer_app.customer_actvities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginActivity;
import b2infosoft.milkapp.com.activity.LoginViaActivity;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.customerRegisterAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextClass;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class CustomerRegisterActivity extends AppCompatActivity {

    Context mContext;
    TextInputLayout tvPhone, tvName;
    EditText etPhoneNumber, etName;
    Button btnSignup;
    SessionManager sessionManager;
    String refreshedToken = "";
    String phoneNumber = "", name = "", user_group_id = "";
    Toolbar toolbar;
    TextView toolbar_title;
    View layoutSelecCustomer, layout_phone;
    private RadioGroup radioType;
    private RadioButton radioSaller, radioBuyer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_register);
        mContext = CustomerRegisterActivity.this;
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.Customer) + " " + getString(R.string.Register));
        sessionManager = new SessionManager(mContext);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        layoutSelecCustomer = findViewById(R.id.layoutSelecCustomer);
        radioType = findViewById(R.id.radioType);
        radioSaller = findViewById(R.id.radioSaller);
        radioBuyer = findViewById(R.id.radioBuyer);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        tvPhone = findViewById(R.id.tvPhone);
        etName = findViewById(R.id.etName);
        tvName = findViewById(R.id.tvName);
        btnSignup = findViewById(R.id.btnSave);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioSaller:
                        user_group_id = "3";
                        break;
                    case R.id.radioBuyer:
                        user_group_id = "4";
                        break;

                }

            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = etPhoneNumber.getText().toString().trim();
                name = etName.getText().toString().trim();

                if (layoutSelecCustomer.getVisibility() == View.VISIBLE) {
                    if (user_group_id.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.PleaseSelectCustomerType));
                    } else {
                        layoutSelecCustomer.setVisibility(View.GONE);
                        tvPhone.setVisibility(View.VISIBLE);
                        tvPhone.requestFocus();
                    }
                } else if (tvPhone.getVisibility() == View.VISIBLE) {

                    if (phoneNumber.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_enter_valid_mobile_number));
                    } else if (phoneNumber.length() < 10) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.Please_enter_valid_mobile_number));
                    } else {
                        layoutSelecCustomer.setVisibility(View.GONE);
                        btnSignup.setText(mContext.getString(R.string.SIGNUP));
                        tvPhone.setVisibility(View.GONE);
                        tvName.setVisibility(View.VISIBLE);
                        tvPhone.clearFocus();
                        tvName.requestFocus();
                    }
                } else if (tvName.getVisibility() == View.VISIBLE) {
                    if (name.length() == 0) {
                        UtilityMethod.showAlertWithButton(mContext, getString(R.string.PleaseEnterName));
                    } else {
                        if (isNetworkAvaliable(mContext)) {
                            registerCustomer();
                        } else {
                            showToast(mContext, mContext.getResources().getString(R.string.you_are_not_connected_to_internet));
                        }
                    }
                }

            }

        });

    }


    @Override
    public void onBackPressed() {

        if (tvName.getVisibility() == View.VISIBLE) {
            tvName.setVisibility(View.GONE);
            tvPhone.setVisibility(View.VISIBLE);
            btnSignup.setText(mContext.getString(R.string.Next));
        } else if (tvPhone.getVisibility() == View.VISIBLE) {
            tvPhone.setVisibility(View.GONE);
            layoutSelecCustomer.setVisibility(View.VISIBLE);
        } else {
            UtilityMethod.goNextClass(mContext, LoginViaActivity.class);

        }

    }


    public void registerCustomer() {

        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Registering...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");

                    if (status.equalsIgnoreCase("success")) {
                        showToast(mContext, jsonObject.getString("user_status_message"));

                        goNextClass(mContext, LoginActivity.class);
                    } else {
                        JSONObject error = jsonObject.getJSONObject("error");
                        String message = error.getString("phone_number");
                        if (message.equalsIgnoreCase("The phone number has already been taken.")) {
                            tempMobileNumber = phoneNumber;
                            goNextClass(mContext, LoginActivity.class);
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, error.getString("phone_number"));

                            etPhoneNumber.requestFocus();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("phone_number", phoneNumber)
                .addEncoded("name", name)
                .addEncoded("firebase_tocan", refreshedToken)
                .addEncoded("user_group_id", user_group_id)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(customerRegisterAPI);

    }
}

