package b2infosoft.milkapp.com.DeliveryBoy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import b2infosoft.milkapp.com.DeliveryBoy.Fragment.DeliveryBoyHomeFragment;
import b2infosoft.milkapp.com.DeliveryBoy.Fragment.fragmentDeliveryBoyProfile;
import b2infosoft.milkapp.com.DeliveryBoy.Fragment.fragmentDeliveryNotification;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerDashboard;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_DESCRIPTION;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_NAME;

public class DeliveryBoyMainActivity extends AppCompatActivity {

    Fragment fragment;
    Intent intent;
    String selected_frag = "";
    Context mContext;
    SessionManager sessionManager;
    BroadcastReceiver receiver;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = new DeliveryBoyHomeFragment();
                    fragmentset(fragment);
                    return true;
                case R.id.navigation_profile:
                    fragment = new fragmentDeliveryBoyProfile();
                    fragmentset(fragment);
                    return true;
                case R.id.navigation_notifications:
                    fragment = new fragmentDeliveryNotification();
                    fragmentset(fragment);

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleveryboy_main);
        mContext = DeliveryBoyMainActivity.this;
        sessionManager = new SessionManager(this);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        intent = getIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(getString(R.string.notification_channel_id), FCMCHANNEL_NAME, importance);
            mChannel.setDescription(FCMCHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        fragment = new DeliveryBoyHomeFragment();


        if (intent.getExtras() == null) {
            selected_frag = getString(R.string.Home);
            fragment = new DeliveryBoyHomeFragment();
        } else {

            selected_frag = getString(R.string.notification);
            fragment = new fragmentDeliveryNotification();
            Bundle bundle = new Bundle();
            bundle.putString("from", "dashboard");

        }
        fragmentset(fragment);

    }

    private void fragmentset(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {


        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            // fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {

            backButtonHandler();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    @Override
    protected void onResume() {
        super.onResume();


        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT));

    }

    private void backButtonHandler() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.Exyt_App))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.OK),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();

                            }
                        })
                .setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        if (!selected_frag.equalsIgnoreCase(getString(R.string.Home))) {
            Fragment fragment = new fragment_BuyerDashboard();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).commit();
            selected_frag = getString(R.string.Home);

        } else {

            dialog.show();
        }

    }

}
