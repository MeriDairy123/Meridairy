package b2infosoft.milkapp.com.Dairy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

/**
 * Created by Choudhary on 19-Jan-19.
 */

public class Notification_activity extends Activity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Toolbar toolbar;
    TextView tvNotifTitle, tvNotifBody;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticifation);
        mContext = Notification_activity.this;


        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        initView();
    }


    private void initView() {

        toolbar = findViewById(R.id.toolbar);

        tvNotifTitle = findViewById(R.id.tvNotifTitle);
        tvNotifBody = findViewById(R.id.tvNotifBody);
        toolbar.setTitle(getString(R.string.notification));
        sessionManager = new SessionManager(mContext);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent != null) {
            String messageBody = intent.getStringExtra("messageBody");

            tvNotifTitle.setText(intent.getStringExtra("messageTitle"));
            tvNotifBody.setText(Html.fromHtml(messageBody));
            tvNotifBody.setMovementMethod(LinkMovementMethod.getInstance());
            tvNotifBody.setMovementMethod(new ScrollingMovementMethod());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}