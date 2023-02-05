package b2infosoft.milkapp.com.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;



import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.customer_actvities.CustomerRegisterActivity;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public class ActivitySelectUserType extends AppCompatActivity implements View.OnClickListener {

    Context mContext;
    View view_CustomerDetail, layout_main;
    View View_Customer, View_Dairy;
    Toolbar toolbar;
    TextView toolbar_title, tvCustomerHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type);
        toolbar = findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(getString(R.string.SIGNUP));

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mContext = ActivitySelectUserType.this;
        layout_main = findViewById(R.id.layout_main);
        View_Customer = findViewById(R.id.ll_CustomerLogin);
        View_Dairy = findViewById(R.id.ll_DairyLogin);
        view_CustomerDetail = findViewById(R.id.view_CustomerDetail);
        tvCustomerHelp = findViewById(R.id.tvCustomerHelp);
        view_CustomerDetail.setVisibility(View.GONE);
        View_Customer.setOnClickListener(this);
        View_Dairy.setOnClickListener(this);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        intiViewUserType();
    }

    private void intiViewCustomerMessage() {
        toolbar_title.setText(getString(R.string.Customer));
        layout_main.setVisibility(View.GONE);
        view_CustomerDetail.setVisibility(View.VISIBLE);

        tvCustomerHelp.setTranslationY(-(20 * 100));
        tvCustomerHelp.setAlpha(0.5f);
        tvCustomerHelp.animate().alpha(1f).translationY(0).setDuration(1000).start();
    }

    private void intiViewUserType() {

        //Animation ll_signup
        toolbar_title.setText(getString(R.string.select));
        layout_main.setVisibility(View.VISIBLE);
        view_CustomerDetail.setVisibility(View.GONE);
        View_Customer.setTranslationX(-(10 * 100));
        View_Customer.setAlpha(0.5f);
        View_Customer.animate().alpha(1f).translationX(0).setDuration(1500).start();
        //Animation ll_CustomerLogin
        View_Dairy.setTranslationX(10 * 100);
        View_Dairy.setAlpha(0.5f);
        View_Dairy.animate().alpha(1f).translationX(0).setDuration(1500).start();
    }


    @Override
    public void onBackPressed() {
        if (view_CustomerDetail.getVisibility() == View.VISIBLE) {
            intiViewUserType();
        } else {
            UtilityMethod.goNextClass(mContext, LoginViaActivity.class);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_CustomerLogin:
                // intiViewCustomerMessage();
                startActivity(new Intent(mContext, CustomerRegisterActivity.class));

                break;
            case R.id.ll_DairyLogin:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;

        }
    }
}
