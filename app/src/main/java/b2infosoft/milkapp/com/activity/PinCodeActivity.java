package b2infosoft.milkapp.com.activity;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Model.BeanOfferBanerList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.Model.BeanOfferBanerList.getBannerOfferList;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.appglobal.Constant.FirstTime;
import static b2infosoft.milkapp.com.appglobal.Constant.getBanerAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getBannerSkipOrNot;
import static b2infosoft.milkapp.com.appglobal.Constant.tempMobileNumber;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerCustomText;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImage;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImageId;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_BannerImagePath;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyBuyFatType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyBuyMilkRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KeyIsOnline;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_AutoFatStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BalancewWebSMS;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BonusYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuffMinFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyBuffFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyCowFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_CowMaxFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FatYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_PrintReciept;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RateYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleBuffaloFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleCowFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellMilkPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_User_Status;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_skip_ad;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PinCodeActivity extends AppCompatActivity {

    Context mContext;
    Toolbar toolbar;
    SessionManager sessionManager;
    String pinNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_code);
        tempMobileNumber = "";
        mContext = PinCodeActivity.this;
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        if (FirstTime.equals("Yes")) {
            firstTimeLoadSetting();
        }
        initView();

      //  Key_skip_ad;

    }

    public void firstTimeLoadSetting() {
        // get SMS Balance
        sessionManager.setValueSession(Key_BalancewWebSMS, "0");
        // Get User Status plan
        sessionManager.setValueSession(Key_User_Status, "1");
        sessionManager.setIntValueSession(Key_RemainingDay, 2);
        sessionManager.setValueSession(Key_BuyMilkScreen, ZERO);
        sessionManager.setValueSession(Key_SaleMilkScreen, ZERO);
        sessionManager.setValueSession(Key_FatYES, ONE);
        sessionManager.setValueSession(Key_FatYES, ONE);
        sessionManager.setValueSession(Key_SNFYES, ONE);
        sessionManager.setValueSession(Key_RateYES, ONE);
        sessionManager.setValueSession(Key_BonusYES, ONE);
        sessionManager.setValueSession(Key_AutoFatStatus, ZERO);
        sessionManager.setFloatValueSession(Key_CowMaxFat, 5);
        sessionManager.setFloatValueSession(Key_BuffMinFat, 5);
        sessionManager.setValueSession(Key_SaleRateType, "5");
        sessionManager.setValueSession(Key_SaleFateType, "0");
        sessionManager.setValueSession(Key_smsAlwyasOn_ASk, YES);
        sessionManager.setValueSession(Key_PrintReciept, ZERO);
        sessionManager.setIntValueSession(KeyIsOnline, 1);
        //Clr
        sessionManager.setValueSession(SessionManager.Key_DevideFac, "4");
        sessionManager.setValueSession(SessionManager.Key_MultiFac, "0.20");
        sessionManager.setValueSession(SessionManager.Key_AddFac, "0.66");

        // getBuy Fat Milk Price
        sessionManager.setValueSession(KeyBuyMilkRateType, "1");
        sessionManager.setValueSession(KeyBuyFatType, "0");
        sessionManager.setFloatValueSession(Key_BuyFatPrice, 0);
        sessionManager.setFloatValueSession(Key_BuyCowFatPrice, 0);
        sessionManager.setFloatValueSession(Key_BuyBuffFatPrice, 0);

        sessionManager.setValueSession(Key_SellMilkPrice, "0.00");

        sessionManager.setFloatValueSession(Key_SaleFatPrice, 0);
        sessionManager.setFloatValueSession(Key_SaleCowFatPrice, 0);
        sessionManager.setFloatValueSession(Key_SaleBuffaloFatPrice, 0);

        sessionManager.setValueSession(Key_SendSmsSetting, ONE);
        sessionManager.setValueSession(Key_smsAlwyasOn_ASk, YES);
        getSMSBalance(mContext, NetworkTask.GET_TASK);
        // Check User Plan Expiry Status
        PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus(mContext);


    }




    public void setLocale(String lang) {
        Constant.LangLoaded = "Loaded";
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    private void initView() {
        if (Constant.LangLoaded.equals("")) {
            if (sessionManager != null) {
                if (sessionManager.getValueSesion(SessionManager.SessionLang).length() != 0) {
                    String lang = sessionManager.getValueSesion(SessionManager.SessionLang);
                    setLocale(lang);
                }
            }
        }
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.PIN_CODE));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pinNumber = nullCheckFunction(sessionManager.getValueSesion(SessionManager.PinNumber));
        System.out.println("pinNumber===" + pinNumber);
        if (pinNumber.length() > 0) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FragmentChangePin())
                    .commit();
        }
        getBannerOfferList(mContext, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
