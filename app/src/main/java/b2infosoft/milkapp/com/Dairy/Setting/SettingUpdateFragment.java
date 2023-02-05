package b2infosoft.milkapp.com.Dairy.Setting;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogMachineSetUp;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.resetConnection;
import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.DialogSMS_Setting;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.setMessageSetting;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.addDairyMsg;
import static b2infosoft.milkapp.com.appglobal.Constant.buyMilkRateSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.saleMilkRateSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.savemessagetolocal;
import static b2infosoft.milkapp.com.appglobal.Constant.smstendayssettingsave;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_MachineAuto;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_ReceiptMessageFontSize;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_ReceiptTitleFontSize;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_WhatsAppMSG;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_dairy_id;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_AutoFatStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BalancewWebSMS;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuffMinFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_CowMaxFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleMilkScreen;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.SessionLang;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlert;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.webservice.NetworkTask.GET_TASK;
import static b2infosoft.milkapp.com.webservice.NetworkTask.POST_TASK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass;
import b2infosoft.milkapp.com.Dairy.DairyDeshboardFragment;
import b2infosoft.milkapp.com.Dairy.MainActivity;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFragment;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.MilkEntrySMS.MessageDialogStatusListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;


/**
 * Created by Choudhary on 22-Oct-19.
 */

public class SettingUpdateFragment extends Fragment implements MessageDialogStatusListner, CompoundButton.OnCheckedChangeListener, FragmentBackPressListener, View.OnClickListener {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Toolbar toolbar;
    Spinner spinLang;
    SwitchCompat switchAutoFat, swichRate_kg, swichfat_kg, swichSNF, swichSellerBhugtan,swichSendTotal, swichBonus, swichPrint_Reciept, swichPrintinAll_lang, switchMachine, switchWhatsAppMsg;
    TextView toolbar_title, tvMachineName;
    SessionManager sessionManager;
    String selectedlang = "", selectedSMS = "", machineCode = "", buyScreen = "0", saleScreen = "0", autoFatStatus = "0", langEng = "en", langSpan = "es", langHi = "hi", langGuj = "gu", langMr = "mr", langPa = "pa", langTe = "te", langTa = "ta", langKa = "kn";

    TextView tvMessageSettig, tv_sim, tv_web, tv_off, tvMilkBuy, tvMilkSale, tvValue, tvValue2;
    ImageView img_sms_status, imgMinus, imgMinus2, imgAdd1, imgAdd2;
    RelativeLayout layout_SimSetting;
    Fragment fragment = null;
    RadioGroup rgBuy, rgSale;
    RadioButton rdBuyGeneral, rdBuyCustom, rdSaleGeneral, rdSaleCustom;
    TextInputEditText ediFatCow, ediFatBuffalo;
    Button btnUpdate, saveButton;
    float cowFat = 0, buffFat = 0;
    Integer titleFontSize = 20, messageFontSize = 18;
    boolean isMachineAuto = false, isWhatsAppMsg = false;
    EditText editText;
    View view, layoutCowBuff;
    BroadcastReceiver receiver;
    SharedPrefData sharedPrefData ;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting_update, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        sharedPrefData = new SharedPrefData();

        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initView();

            }
        };
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);

        spinLang = view.findViewById(R.id.spinLang);
        swichPrint_Reciept = view.findViewById(R.id.swichPrint_Reciept);
        swichPrintinAll_lang = view.findViewById(R.id.swichPrintinAll_lang);
        switchMachine = view.findViewById(R.id.switchMachine);
        switchWhatsAppMsg = view.findViewById(R.id.switchWhatsAppMsg);
        tvMachineName = view.findViewById(R.id.tvMachineName);
        LinearLayout lvChangeMachine = view.findViewById(R.id.lvChangeMachine);
        layout_SimSetting = view.findViewById(R.id.layout_SimSetting);
        switchAutoFat = view.findViewById(R.id.switchAutoFat);
        swichSellerBhugtan = view.findViewById(R.id.swichSellerBhugtan);
        swichRate_kg = view.findViewById(R.id.swichRate_kg);
        swichfat_kg = view.findViewById(R.id.swichfat_kg);
        swichSNF = view.findViewById(R.id.swichSNF);
        swichBonus = view.findViewById(R.id.swichBonus);
        swichSendTotal = view.findViewById(R.id.swichSendTotal);
        tvMessageSettig = view.findViewById(R.id.tvMessageSettig);
        tv_sim = view.findViewById(R.id.tv_sim);
        tv_web = view.findViewById(R.id.tv_web);
        tv_off = view.findViewById(R.id.tv_off);
        img_sms_status = view.findViewById(R.id.img_sms_status);
        tvMilkBuy = view.findViewById(R.id.tvMilkBuy);
        rgBuy = view.findViewById(R.id.rgBuy);
        rgSale = view.findViewById(R.id.rgSale);
        rdBuyGeneral = view.findViewById(R.id.rdBuyGeneral);
        rdBuyCustom = view.findViewById(R.id.rdBuyCustom);
        editText = view.findViewById(R.id.greatting_text);
        tvMilkSale = view.findViewById(R.id.tvMilkSale);
        rdSaleGeneral = view.findViewById(R.id.rdSaleGeneral);
        rdSaleCustom = view.findViewById(R.id.rdSaleCustom);
        ediFatCow = view.findViewById(R.id.ediFatCow);
        ediFatBuffalo = view.findViewById(R.id.ediFatBuffalo);
        layoutCowBuff = view.findViewById(R.id.layoutCowBuff);

        imgMinus = view.findViewById(R.id.imgMinus);
        imgMinus2 = view.findViewById(R.id.imgMinus2);
        imgAdd1 = view.findViewById(R.id.imgAdd1);
        imgAdd2 = view.findViewById(R.id.imgAdd2);
        tvValue = view.findViewById(R.id.tvValue);
        tvValue2 = view.findViewById(R.id.tvValue2);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        saveButton = view.findViewById(R.id.btnSaveStatus);
        layoutCowBuff.setVisibility(View.GONE);
        swichSellerBhugtan.setText(mContext.getString(R.string.SELLER_BHUGTAN) + " " + mContext.getString(R.string.USER_LIST) + " " + mContext.getString(R.string.Amount) + " >0");

        toolBarManage();
        swichPrintinAll_lang.setOnCheckedChangeListener(this);
        swichPrint_Reciept.setOnCheckedChangeListener(this);
        switchMachine.setOnCheckedChangeListener(this);
        switchWhatsAppMsg.setOnCheckedChangeListener(this);
        swichSellerBhugtan.setOnCheckedChangeListener(this);
        swichRate_kg.setOnCheckedChangeListener(this);
        swichSNF.setOnCheckedChangeListener(this);
        swichBonus.setOnCheckedChangeListener(this);
        swichSendTotal.setOnCheckedChangeListener(this);
        swichfat_kg.setOnCheckedChangeListener(this);
        switchAutoFat.setOnClickListener(this);
        switchAutoFat.setOnClickListener(this);

        rdBuyGeneral.setOnClickListener(this);
        rdBuyCustom.setOnClickListener(this);
        rdSaleGeneral.setOnClickListener(this);
        rdSaleCustom.setOnClickListener(this);

        imgMinus.setOnClickListener(this);
        imgAdd1.setOnClickListener(this);
        imgMinus2.setOnClickListener(this);
        imgAdd2.setOnClickListener(this);

        btnUpdate.setOnClickListener(this);
        saveButton.setOnClickListener(this);


        if (isConnected()) {
            setMessageSetting(mContext, GET_TASK);
            getSMSBalance(mContext, GET_TASK);
        }
        layout_SimSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSMS_Setting(mContext, "SettingUpdate", SettingUpdateFragment.this);
            }
        });
        tvMessageSettig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSMS_Setting(mContext, "SettingUpdate", SettingUpdateFragment.this);
            }
        });
        lvChangeMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMachineSetUp(mContext);
            }
        });

        editText.setText(sharedPrefData.retriveDataFromPrefrence(mContext,"saveGreatingSms"));

        spinLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (!selectedlang.matches(langEng)) {
                            setLocales(langEng);
                        }
                        break;
                    case 1:
                        if (!selectedlang.matches(langSpan)) {
                            setLocales(langSpan);
                        }
                        break;
                    case 2:
                        if (!selectedlang.matches(langHi)) {
                            setLocales(langHi);
                        }
                        break;
                    case 3:
                        if (!selectedlang.matches(langGuj)) {
                            setLocales(langGuj);
                        }
                        break;
                    case 4:
                        if (!selectedlang.matches(langMr)) {
                            setLocales(langMr);
                        }
                        break;
                    case 5:
                        if (!selectedlang.matches(langPa)) {
                            setLocales(langPa);
                        }
                        break;

                    case 6:
                        if (!selectedlang.matches(langTe)) {
                            setLocales(langTe);
                        }
                        break;
                    case 7:
                        if (!selectedlang.matches(langTa)) {
                            setLocales(langTa);
                        }
                        break;
                    case 8:
                        if (!selectedlang.matches(langKa)) {
                            setLocales(langKa);
                        }
                        break;
                    default:
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // TODO Auto-generated method stub

            }
        });
        if (sessionManager.getValueSesion(SessionLang).length() != 0) {
            selectedlang = sessionManager.getValueSesion(SessionLang);
        } else {
            selectedlang = langEng;
        }
        if (sessionManager.getIntValueSesion(KEY_ReceiptTitleFontSize) == 0) {
            sessionManager.setIntValueSession(KEY_ReceiptTitleFontSize, titleFontSize);
        } else {
            titleFontSize = sessionManager.getIntValueSesion(KEY_ReceiptTitleFontSize);
        }
        if (sessionManager.getIntValueSesion(KEY_ReceiptMessageFontSize) == 0) {
            sessionManager.setIntValueSession(KEY_ReceiptMessageFontSize, messageFontSize);
        } else {
            messageFontSize = sessionManager.getIntValueSesion(KEY_ReceiptMessageFontSize);
        }

        tvValue.setText(titleFontSize + "");
        tvValue2.setText(messageFontSize + "");


        if (selectedlang.equalsIgnoreCase(langEng)) {
            spinLang.setSelection(0);
        } else if (selectedlang.equalsIgnoreCase(langSpan)) {
            spinLang.setSelection(1);
        } else if (selectedlang.equalsIgnoreCase(langHi)) {
            spinLang.setSelection(2);
        } else if (selectedlang.equalsIgnoreCase(langGuj)) {
            spinLang.setSelection(3);
        } else if (selectedlang.equalsIgnoreCase(langMr)) {
            spinLang.setSelection(4);
        } else if (selectedlang.equalsIgnoreCase(langPa)) {
            spinLang.setSelection(5);
        } else if (selectedlang.equalsIgnoreCase(langTe)) {
            spinLang.setSelection(6);
        } else if (selectedlang.equalsIgnoreCase(langTa)) {
            spinLang.setSelection(7);
        } else if (selectedlang.equalsIgnoreCase(langKa)) {
            spinLang.setSelection(8);
        }


        toolbar_title.setText(getString(R.string.Setting));
        tvMilkBuy.setText(mContext.getString(R.string.Milk_Buy) + " " + mContext.getString(R.string.screen));
        tvMilkSale.setText(mContext.getString(R.string.MILK_Sale) + " " + mContext.getString(R.string.screen));
        switchAutoFat.setText(mContext.getString(R.string.automatic) + " " + mContext.getString(R.string.Fat));
        initView();
        return view;
    }


    private void initView() {

        saveMessageToLocal();
       PurchaseMilkEntryFragment.saveGreetingMessage(mContext,sessionManager,sharedPrefData);
        upDateSMSSetting();
        // Rate kg  Show

        if (sessionManager.getValueSesion(SessionManager.Key_RateYES) == null || sessionManager.getValueSesion(SessionManager.Key_RateYES).length() == 0) {
            swichRate_kg.setChecked(true);
            swichfat_kg.setChecked(true);
            swichSNF.setChecked(true);
            swichBonus.setChecked(false);
            swichSellerBhugtan.setChecked(false);
            sessionManager.setValueSession(Key_BuyMilkScreen, ZERO);
            sessionManager.setValueSession(Key_SaleMilkScreen, ZERO);
            sessionManager.setValueSession(SessionManager.Key_RateYES, ONE);

            sessionManager.setValueSession(SessionManager.Key_RateYES, ONE);
            sessionManager.setValueSession(SessionManager.Key_FatYES, ONE);
            sessionManager.setValueSession(SessionManager.Key_SNFYES, ONE);
            sessionManager.setValueSession(SessionManager.Key_BonusYES, NO);
            // Bluetooth printer reciept by default off
            swichPrint_Reciept.setChecked(false);
            sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);


        }
        buyScreen = sessionManager.getValueSesion(Key_BuyMilkScreen);
        saleScreen = sessionManager.getValueSesion(Key_SaleMilkScreen);
        autoFatStatus = sessionManager.getValueSesion(Key_AutoFatStatus);
        cowFat = sessionManager.getFloatValueSession(Key_CowMaxFat);
        buffFat = sessionManager.getFloatValueSession(Key_BuffMinFat);
        System.out.println("autoFatStatus>>>>>" + autoFatStatus);
        System.out.println("cowFat>>>>>" + cowFat);
        System.out.println("buffFat>>>>>" + buffFat);

        ediFatCow.setText(cowFat + "");
        ediFatBuffalo.setText(buffFat + "");

        machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);
        isMachineAuto = sessionManager.getBooleanValue(KEY_MachineAuto);
        isWhatsAppMsg = sessionManager.getBooleanValue(KEY_WhatsAppMSG);
        tvMachineName.setText(sessionManager.getValueSesion(SessionManager.KEY_MachineName));
        switchMachine.setChecked(isMachineAuto);
        switchWhatsAppMsg.setChecked(isWhatsAppMsg);
        if (buyScreen.equalsIgnoreCase(ONE)) {
            rdBuyCustom.setChecked(true);
        } else {
            rdBuyGeneral.setChecked(true);
        }
        if (saleScreen.equalsIgnoreCase(ONE)) {
            rdSaleCustom.setChecked(true);
        } else {
            rdSaleGeneral.setChecked(true);
        }
        if (autoFatStatus.equalsIgnoreCase(ONE)) {
            switchAutoFat.setChecked(true);
            layoutCowBuff.setVisibility(View.VISIBLE);
        } else {
            switchAutoFat.setChecked(false);
            layoutCowBuff.setVisibility(View.GONE);
        }
        if (sessionManager.getValueSesion(SessionManager.Key_RateYES).equalsIgnoreCase(ONE)) {
            swichRate_kg.setChecked(true);
        }
        if (sessionManager.getIntValueSesion(SessionManager.Key_SellerBhugtanSetting) != 0) {
            swichSellerBhugtan.setChecked(true);
        } else {
            swichSellerBhugtan.setChecked(false);
        }
        if (sessionManager.getValueSesion(SessionManager.Key_BonusYES).equalsIgnoreCase(ONE)) {
            swichBonus.setChecked(true);
        }
        // Fat kg  Show

        if (sessionManager.getValueSesion(SessionManager.Key_FatYES).equalsIgnoreCase(ONE)) {
            swichfat_kg.setChecked(true);
        }

        if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).equalsIgnoreCase(ONE)) {
            swichSendTotal.setChecked(true);
        }else{
            swichSendTotal.setChecked(false);
        }

        if (sessionManager.getValueSesion(SessionManager.Key_SNFYES).equals(ONE)) {
            swichSNF.setChecked(true);
        }

        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            swichPrint_Reciept.setChecked(true);
        } else {
            sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);
            swichPrint_Reciept.setChecked(false);
            resetConnection();
        }

        if (sessionManager.getValueSesion(SessionManager.Key_PrintAllLang).length() == 0) {
            sessionManager.setValueSession(SessionManager.Key_PrintAllLang, NO);
            swichPrintinAll_lang.setChecked(false);
        } else {
            String multPrint = sessionManager.getValueSesion(SessionManager.Key_PrintAllLang);
            if (multPrint.equalsIgnoreCase(YES)) {
                swichPrintinAll_lang.setChecked(true);
            } else {
                swichPrintinAll_lang.setChecked(false);
            }

        }

    }

    private void saveMessageToLocal() {
        Log.d("TAG", "saveGreetingMessage: "+sessionManager.getValueSesion("userID"));
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = savemessagetolocal+sessionManager.getValueSesion("userID");
        StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.d("TAG", "onResponse788: ");

                            String s= jsonObject.getString("data");
                            Log.d("TAG", "onResponse786578: "+s);

                            JSONObject jsonObjectt = new JSONObject(s);
                            String ss= jsonObjectt.getString("greeting_msg").toString();

                            Log.d("TAG", "onResponse786578: "+ss);


                            sharedPrefData.saveDataToPrefrence(mContext,"saveGreatingSms",ss);
                            editText.setText(ss);
                        } catch (JSONException e) {

                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("dairy_id", "310054");
                return params;
            }
        };
        queue.add(getRequest);


    }

    private void upDateSMSSetting() {
        selectedSMS = sessionManager.getValueSesion(Key_SendSmsSetting);
        if (selectedSMS.length() == 0) {
            selectedSMS = ONE;
            sessionManager.setValueSession(Key_SendSmsSetting, selectedSMS);
        }
        if (selectedSMS.equalsIgnoreCase(ZERO)) {
            tv_off.setVisibility(View.VISIBLE);
            tv_sim.setVisibility(View.GONE);
            tv_web.setVisibility(View.GONE);
            img_sms_status.setImageResource(R.drawable.ic_close);

        } else if (selectedSMS.equalsIgnoreCase(ONE)) {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.VISIBLE);
            tv_web.setVisibility(View.GONE);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);

        } else {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.GONE);
            tv_web.setVisibility(View.VISIBLE);
            String smsBalance = "";
            smsBalance = sessionManager.getValueSesion(Key_BalancewWebSMS);
            tv_web.setText(mContext.getString(R.string.web) + "  " + smsBalance);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);

        }


    }


    public void setLocales(String lang) {
        Constant.LangLoaded = "Loaded";
        sessionManager.setValueSession(SessionLang, lang);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(mContext, MainActivity.class);
        startActivity(refresh);
        ((Activity) mContext).finish();

    }


    public void toolBarManage() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            if (bundle.getString("from").equalsIgnoreCase("setting")) {
                toolbar.setVisibility(View.GONE);
            }
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();

                }
            });
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
        }
    }


    @Override
    public void OnFragmentBackPressListener() {
        fragment = new DairyDeshboardFragment();
        UtilityMethod.goNextFragmentWithBackStack(mContext, fragment);
    }


    @Override
    public void Messagedialog(boolean status) {
        upDateSMSSetting();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.swichPrint_Reciept:
                if (swichPrint_Reciept.isChecked()) {
                    if (isBluetoothHeadsetConnected()) {
                        if (mDevice == null || mSocket == null || mOutputStream == null) {
                            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
                            enableBluetooth(mContext);
                            dialogBluetooth(mContext);
                        }
                        sessionManager.setValueSession(SessionManager.Key_PrintReciept, YES);
                    } else {
                        resetConnection();
                        showAlertWithTitle(mContext.getString(R.string.Please_enable_Bluetooth_device), mContext);
                        enableBluetooth(mContext);
                        swichPrint_Reciept.setChecked(false);
                        dialogBluetooth(mContext);
                        swichPrint_Reciept.setChecked(true);
                    }
                } else {
                    sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);
                }

                break;
            case R.id.swichPrintinAll_lang:
                if (swichPrintinAll_lang.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_PrintAllLang, YES);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_PrintAllLang, NO);
                }
                break;
            case R.id.switchMachine:
                machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);

                if (buttonView.isChecked()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || !enableBluetooth(mContext)) {

                        sessionManager.setBooleanValue(KEY_MachineAuto, false);
                        resetConnection();
                        enableBluetooth(mContext);
                        BluetoothClass.dialogBluetooth(mContext);
                        buttonView.setChecked(false);
                    } else if (machineCode.isEmpty()) {

                        BluetoothClass.dialogMachineSetUp(mContext);
                        buttonView.setChecked(false);
                    } else {
                        sessionManager.setBooleanValue(KEY_MachineAuto, buttonView.isChecked());
                    }
                } else {
                    sessionManager.setBooleanValue(KEY_MachineAuto, buttonView.isChecked());
                }
                break;
            case R.id.switchWhatsAppMsg:
                sessionManager.setBooleanValue(KEY_WhatsAppMSG, buttonView.isChecked());
                break;
            case R.id.switchAutoFat:
                if (buttonView.isChecked()) {
                    autoFatStatus = ONE;
                    layoutCowBuff.setVisibility(View.VISIBLE);
                } else {
                    autoFatStatus = ZERO;
                    layoutCowBuff.setVisibility(View.GONE);
                }
                sessionManager.setValueSession(SessionManager.Key_AutoFatStatus, autoFatStatus);
                break;
            case R.id.swichSellerBhugtan:
                if (buttonView.isChecked()) {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerBhugtanSetting, 1);
                } else {
                    sessionManager.setIntValueSession(SessionManager.Key_SellerBhugtanSetting, 0);
                }

                break;
            case R.id.swichRate_kg:
                if (buttonView.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_RateYES, ONE);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_RateYES, ZERO);
                }
                setMessageSetting(mContext, POST_TASK);
                break;

                case R.id.swichSendTotal:
                if (swichSendTotal.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_SendTotaLYES, ONE);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_SendTotaLYES, ZERO);
                }
                    saveSmsSetting();

                break;

            case R.id.swichSNF:
                if (buttonView.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_SNFYES, ONE);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_SNFYES, ZERO);
                }
                setMessageSetting(mContext, POST_TASK);
                break;
            case R.id.swichfat_kg:
                if (buttonView.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_FatYES, ONE);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_FatYES, ZERO);
                }
                setMessageSetting(mContext, POST_TASK);
                break;

            case R.id.swichBonus:
                if (buttonView.isChecked()) {
                    sessionManager.setValueSession(SessionManager.Key_BonusYES, ONE);
                } else {
                    sessionManager.setValueSession(SessionManager.Key_BonusYES, ZERO);
                }
                setMessageSetting(mContext, POST_TASK);
                break;
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rdBuyGeneral:
                buyScreen = ZERO;
                updateBuyScreenSetting();
                break;
            case R.id.rdBuyCustom:
                buyScreen = ONE;
                updateBuyScreenSetting();
                break;
            case R.id.rdSaleGeneral:
                saleScreen = ZERO;
                updateSaleScreenSetting();
                break;
            case R.id.rdSaleCustom:
                saleScreen = ONE;
                updateSaleScreenSetting();
                break;
            case R.id.switchAutoFat:
                autoFatStatus = ZERO;
                if (switchAutoFat.isChecked()) {
                    autoFatStatus = ONE;
                }

                autoFatSetting();
                break;
            case R.id.btnUpdate:
                if (ediFatCow.getText().toString().trim().length() > 0) {
                    cowFat = getFloatValuFromInputText(ediFatCow.getText().toString().trim());
                }
                if (ediFatBuffalo.getText().toString().trim().length() > 0) {
                    buffFat = getFloatValuFromInputText(ediFatBuffalo.getText().toString().trim());
                }
                if (cowFat == 0 && buffFat == 0) {
                    showAlert(mContext.getString(R.string.Please_Enter_All_Field), mContext);
                } else {
                    autoFatSetting();
                }

                break;
            case R.id.btnSaveStatus:

                sendGreatingMessageToServer();

                break;
            case R.id.imgMinus:
                if (titleFontSize > 1) {
                    titleFontSize = titleFontSize - 1;
                    tvValue.setText(titleFontSize + "");
                    sessionManager.setIntValueSession(KEY_ReceiptTitleFontSize, titleFontSize);
                }

                break;
            case R.id.imgAdd1:

                if (titleFontSize < 35) {
                    titleFontSize = titleFontSize + 1;
                    tvValue.setText(titleFontSize + "");
                    sessionManager.setIntValueSession(KEY_ReceiptTitleFontSize, titleFontSize);
                }
                break;
            case R.id.imgMinus2:
                if (messageFontSize > 1) {
                    messageFontSize = messageFontSize - 1;
                    tvValue2.setText(messageFontSize + "");
                    sessionManager.setIntValueSession(KEY_ReceiptMessageFontSize, titleFontSize);
                }

                break;
            case R.id.imgAdd2:
                if (messageFontSize < 35) {
                    messageFontSize = messageFontSize + 1;
                    tvValue2.setText(messageFontSize + "");
                    sessionManager.setIntValueSession(KEY_ReceiptMessageFontSize, messageFontSize);
                }
                break;
        }

    }

    private void sendGreatingMessageToServer() {

        sharedPrefData.saveDataToPrefrence(mContext,"saveGreatingSms",editText.getText().toString());
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest postRequest = new StringRequest(Request.Method.POST,addDairyMsg, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response);
                    Toast.makeText(mContext, "Save Message Successfully", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    // Handle json exception as needed
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(mContext, "Failed To Save Message", Toast.LENGTH_SHORT).show();


            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                Log.d("TAG", "getParams123: "+sessionManager.getValueSesion("userID"));
                Log.d("TAG", "getParams124: "+editText.getText().toString());

                params.put("dairy_id",sessionManager.getValueSesion("userID"));
                params.put("dairy_sms", editText.getText().toString());
            //   params.put("user-token", sessionManager.getValueSesion("user-token"));


                // Pass more params as needed in your rest API
                // Example you may want to pass user input from EditText as a parameter
                // editText.getText().toString().trim()
                return params;
            }
        };

        // This adds the request to the request queue
        queue.add(postRequest);


    }

    private void saveSmsSetting() {
        @SuppressLint("StaticFieldLeak")
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {



            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("setting_status", sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES))
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(smstendayssettingsave);


    }

    private void updateBuyScreenSetting() {
        sessionManager.setValueSession(Key_BuyMilkScreen, buyScreen);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
            @Override
            public void handleResponse(String response) {

            }
        };

        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("screen_type", buyScreen).addEncoded("type", "screen").addEncoded("entry_type", "0").build();

        serviceCaller.addRequestBody(body);
        serviceCaller.execute(buyMilkRateSetting);


    }

    private void updateSaleScreenSetting() {
        sessionManager.setValueSession(Key_SaleMilkScreen, saleScreen);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {

            }
        };

        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("screen_type", saleScreen).addEncoded("type", "screen").addEncoded("entry_type", "0").build();

        serviceCaller.addRequestBody(body);
        serviceCaller.execute(saleMilkRateSetting);


    }

    private void autoFatSetting() {

        sessionManager.setValueSession(Key_AutoFatStatus, autoFatStatus);
        sessionManager.setFloatValueSession(Key_CowMaxFat, cowFat);
        sessionManager.setFloatValueSession(Key_BuffMinFat, buffFat);
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {

            }
        };

        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("status", autoFatStatus).addEncoded("cow", cowFat + "").addEncoded("buffalo", buffFat + "").addEncoded("type", "auto_fat").addEncoded("entry_type", "0").build();

        serviceCaller.addRequestBody(body);
        serviceCaller.execute(buyMilkRateSetting);


    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FIREBASE_REQ_ACCEPT));


    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }
}