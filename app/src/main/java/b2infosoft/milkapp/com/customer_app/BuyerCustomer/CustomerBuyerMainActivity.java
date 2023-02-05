package b2infosoft.milkapp.com.customer_app.BuyerCustomer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import b2infosoft.milkapp.com.Notification.NotificationFragment;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.LoginWithPasswordActivity;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerDairyList;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerDashboard;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_BuyerProfile;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_Buyer_OrderList;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_MilkPlan;
import b2infosoft.milkapp.com.customer_app.BuyerCustomer.Fragment.fragment_ViewInvoice;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;

import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_DESCRIPTION;
import static b2infosoft.milkapp.com.appglobal.Constant.FCMCHANNEL_NAME;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Mobile;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_Name;


public class CustomerBuyerMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static DrawerLayout mDrawer;
    Context mContext;
    NavigationView navigationView;
    TextView tvMobile, tvName;
    ActionBarDrawerToggle toggle;

    Fragment fragment;
    String selected_frag = "";
    Intent intent;
    View headerView;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_customer_dashboard);
        mContext = CustomerBuyerMainActivity.this;
        Constant.SessionUserGroupID = "4";
        sessionManager = new SessionManager(mContext);
        sessionManager.setValueSession(SessionManager.Key_UserGroupID, "4");
        mContext = CustomerBuyerMainActivity.this;
        intent = getIntent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        headerView = navigationView.getHeaderView(0);

        tvMobile = headerView.findViewById(R.id.tvMobileNo);
        tvName = headerView.findViewById(R.id.tvUsername);

        tvName.setText(sessionManager.getValueSesion(KEY_Name));
        tvMobile.setText(sessionManager.getValueSesion(KEY_Mobile));

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawers();
                fragment = new fragment_BuyerProfile();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(null).commit();
            }
        });
        toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        selected_frag = getString(R.string.Home);
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


        if (intent.getExtras() == null) {
            selected_frag = getString(R.string.Home);
            fragment = new fragment_BuyerDashboard();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment).commit();
        } else {
            selected_frag = getString(R.string.notification);
            Bundle bundle = new Bundle();
            bundle.putString("from", "dashboard");
            fragment = new NotificationFragment();
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.container_body, fragment).commit();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(getString(R.string.notification_channel_id), FCMCHANNEL_NAME, importance);
            mChannel.setDescription(FCMCHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

        }


    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        fragment = null;
        switch (item.getItemId()) {

            case R.id.nav_home:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.Home))) {
                    selected_frag = getString(R.string.Home);
                    fragment = new fragment_BuyerDashboard();
                }
                break;
            case R.id.nav_milkHistory:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.MILK_HISTORY))) {
                    selected_frag = getString(R.string.MILK_HISTORY);
                    fragment = new fragment_BuyerDairyList();
                }
                break;
            case R.id.nav_buymilk_plan:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.MyPlan))) {
                    selected_frag = getString(R.string.MyPlan);
                    fragment = new fragment_MilkPlan();
                }
                break;
            case R.id.nav_MyOrder:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.MyOrder))) {
                    selected_frag = getString(R.string.MyOrder);
                    fragment = new fragment_Buyer_OrderList();
                }
                break;
            case R.id.nav_view_invoice:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.ViewInvoice))) {
                    selected_frag = getString(R.string.ViewInvoice);
                    fragment = new fragment_ViewInvoice();
                }
                break;
            case R.id.nav_profile:
                if (!selected_frag.equalsIgnoreCase(getString(R.string.Profile))) {
                    selected_frag = getString(R.string.Profile);
                    fragment = new fragment_BuyerProfile();
                }
                break;

            case R.id.nav_logout:
                mDrawer.closeDrawers();
                Logout();
                break;

        }

        mDrawer.closeDrawer(GravityCompat.START);
        replaceFragment();
        return true;
    }

    public void replaceFragment() {

        if (fragment != null) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //clear popstatck
                    /*FragmentManager fm = getSupportFragmentManager();
                     if (fm.getBackStackEntryCount()>0) {
                        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }*/
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container_body, fragment).commit();

                }
            }, 275);
        }
    }

    public void Logout() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(mContext.getString(R.string.Are_You_Sure_Want_To_Logout))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                tempMobileNumber = sessionManager.getValueSesion(KEY_Mobile);
                                sessionManager.logoutUser();
                                UtilityMethod.goNextClass(mContext, LoginWithPasswordActivity.class);


                            }
                        })
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                fm.popBackStack();
                // fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {

                backButtonHandler();
            }

        }


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}
