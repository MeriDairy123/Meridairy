package b2infosoft.milkapp.com.Dairy.Setting;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

import static b2infosoft.milkapp.com.Model.TransectionListPojo.getBhugtanSetting;
import static b2infosoft.milkapp.com.Model.TransectionListPojo.saveBhugtanSetting;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;

public class BhugtanSettingFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    Context mContext;
    RadioGroup rgSellerList, rgFont;
    SwitchCompat switchShowFat, switchShoSnf, switchShowClr, switchShowRate;
    RadioButton rdFontSmall, rdFontLarge, rdListSmall, rdLitsMid, rdListLarge;
    SessionManager sessionManager;
    String strType = "seller_", strField = "fat", strFielddValue = "",
            rdList = "", rdFontSize = "";
    BroadcastReceiver receiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bhugtan_setting, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

        //toolbar = view.findViewById(R.id.toolbar);
        rgSellerList = view.findViewById(R.id.rgsellerlist);
        rgFont = view.findViewById(R.id.rgFont);
        rdFontSmall = view.findViewById(R.id.rdSmall);
        rdFontLarge = view.findViewById(R.id.rdLarge);
        rdListSmall = view.findViewById(R.id.rb_smallList);
        rdLitsMid = view.findViewById(R.id.rb_midList);
        rdListLarge = view.findViewById(R.id.rb_largeList);
        switchShowFat = view.findViewById(R.id.switchShoFat);
        switchShoSnf = view.findViewById(R.id.switchShowSNF);
        switchShowClr = view.findViewById(R.id.switch_clr_kg);
        switchShowRate = view.findViewById(R.id.switch_Rat);

        switchShowFat.setOnCheckedChangeListener(this);
        switchShoSnf.setOnCheckedChangeListener(this);
        switchShowClr.setOnCheckedChangeListener(this);
        switchShowRate.setOnCheckedChangeListener(this);

        rdFontSmall.setOnClickListener(this);
        rdFontLarge.setOnClickListener(this);
        rdListSmall.setOnClickListener(this);
        rdLitsMid.setOnClickListener(this);
        rdListLarge.setOnClickListener(this);

        switchShowFat.setOnClickListener(this);
        switchShoSnf.setOnClickListener(this);
        switchShowClr.setOnClickListener(this);
        switchShowRate.setOnClickListener(this);
        getBhugtanSetting(mContext,"seller");
        itemView();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                itemView();
            }
        };
        return view;
    }

    private void itemView() {
        String buyerFontSize = sessionManager.getValueSesion(SessionManager.Key_SellerPdfFont);
        if (sessionManager.getIntValueSesion(SessionManager.Key_SellerPdfFAT) == 1) {
            switchShowFat.setChecked(true);
        } else {
            switchShowFat.setChecked(false);
        }
        if (sessionManager.getIntValueSesion(SessionManager.Key_SellerPdfSnf) == 1) {
            switchShoSnf.setChecked(true);
        } else {
            switchShoSnf.setChecked(false);
        }
        if (sessionManager.getIntValueSesion(SessionManager.Key_SellerPdfclr) == 1) {
            switchShowClr.setChecked(true);
        } else {
            switchShowClr.setChecked(false);
        }

        if (sessionManager.getIntValueSesion(SessionManager.Key_SellerPdfRate) == 1) {
            switchShowRate.setChecked(true);
        } else {
            switchShowRate.setChecked(false);
        }

        if (buyerFontSize.equals("large")) {
            rdFontLarge.setChecked(true);
        } else {
            rdFontSmall.setChecked(true);
        }
        milkWeekStatus();
    }

    public void milkWeekStatus() {
        String milkBillStatus = sessionManager.getValueSesion(Key_SellerMilkWeekStatus);
        switch (milkBillStatus) {
            case "10 days":
                rdListSmall.setChecked(true);
                break;
            case "15 days":
                rdLitsMid.setChecked(true);
                break;
            case "1 month":
                rdListLarge.setChecked(true);
                break;

            default:
                break;
        }

    }

    public void onCheckedChanged(CompoundButton buttonView, boolean checkedId) {
        switch (buttonView.getId()) {
            case R.id.switchShoFat:
                if (buttonView.isChecked()) {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfFAT, 1);
                } else {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfFAT, 0);
                }
                break;
            case R.id.switchShowSNF:
                if (buttonView.isChecked()) {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfSnf, 1);
                } else {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfSnf, 0);
                }
                break;
            case R.id.switch_clr_kg:
                if (buttonView.isChecked()) {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfclr, 1);
                } else {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfclr, 0);
                }
                break;
            case R.id.switch_Rat:
                if (buttonView.isChecked()) {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfRate, 1);
                } else {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerPdfRate, 0);
                }
                break;
        }

    }

    @Override
    public void onClick(View view) {
        strFielddValue = "";
        switch (view.getId()) {
            case R.id.rb_smallList:
                strField = "week_status";
                if (rdListSmall.isChecked()) {
                    strFielddValue = "10 days";
                }
                break;
            case R.id.rb_midList:
                strField = "week_status";
                if (rdLitsMid.isChecked()) {
                    strFielddValue = "15 days";
                }
                break;
            case R.id.rb_largeList:
                strField = "week_status";
                if (rdListLarge.isChecked()) {
                    strFielddValue = "1 month";
                }
                break;
            case R.id.rdLarge:
                strField = "font_size";
                if (rdFontLarge.isChecked()) {
                    strFielddValue = "large";
                }
                break;
            case R.id.rdSmall:
                strField = "font_size";
                if (rdFontSmall.isChecked()) {
                    strFielddValue = "small";
                }
                break;
            case R.id.switchShoFat:
                strField = "fat";
                if (switchShowFat.isChecked()) {
                    strFielddValue = ONE;
                } else
                    strFielddValue = ZERO;
                break;
            case R.id.switchShowSNF:
                strField = "snf";
                if (switchShoSnf.isChecked()) {
                    strFielddValue = ONE;
                } else {
                    strFielddValue = ZERO;
                }
                break;
            case R.id.switch_clr_kg:
                strField = "clr";
                if (switchShowClr.isChecked()) {
                    strFielddValue = ONE;
                } else
                    strFielddValue = ZERO;
                break;
            case R.id.switch_Rat:
                strField = "rate";
                if (switchShowRate.isChecked()) {
                    strFielddValue = ONE;
                } else
                    strFielddValue = ZERO;
                break;

        }
        if (strFielddValue.length() > 0) {
            saveBhugtanSetting(mContext,strType , strField, strFielddValue);
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }
}