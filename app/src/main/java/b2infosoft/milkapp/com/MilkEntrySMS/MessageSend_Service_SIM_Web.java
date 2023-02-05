package b2infosoft.milkapp.com.MilkEntrySMS;

import static b2infosoft.milkapp.com.appglobal.Constant.BuyMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.getSmsBalanceAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getSmsSettingAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.smsSettingAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_center_name;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BalancewWebSMS;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BonusYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FatYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_PhoneSMSApp;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_PurchaseBy;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RateYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SNFYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.installMeridairySMSApp;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.sendMessageTOAnotherApp;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertBox;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.POST_TASK;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import b2infosoft.milkapp.com.BuyPlan.SMSPlan_activity;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;
import b2infosoft.milkapp.com.webservice.NetworkTask;


public class MessageSend_Service_SIM_Web {
    private static final int TODO = 0;
    static MessageDialogStatusListner messageDialogStatusListner;
    static String meriDairyLink = "http:bit.ly/2Cyy2Oe";

    public static String MilkSMSContent(Context mContext, boolean smsAlwaysSend, String from, String Phone_number, String selectedName, String selectedDate, String shift, String rsPerKg, String actualFate, float snf, String weight, String totalBonus, String totalPayment, float f) {
        SessionManager sessionManager = new SessionManager(mContext);
        String smsContent = "", centerName = nullCheckFunction(sessionManager.getValueSesion(KEY_center_name));
        // SMS Label for all language
        String lableSmsName = mContext.getString(R.string.Name);
        String lableSmsdate = mContext.getString(R.string.Date);
        String lableSmsshift = mContext.getString(R.string.Shift);
        String lableSmsFat = mContext.getString(R.string.Fat);
        String lableSmsCLR = mContext.getString(R.string.SNF) + "/" + mContext.getString(R.string.CLR);
        String lableSmsrate = mContext.getString(R.string.Rate);
        String lableBonus = mContext.getString(R.string.Bonus);
        String lableSmsRs = mContext.getString(R.string.Rs);
        String lableSmsLtr = mContext.getString(R.string.Ltr);
        String lableSmsWeight = mContext.getString(R.string.Weight)+"/"+ mContext.getString(R.string.Quantity);


        String lableSmsInstallMeriDairy = mContext.getString(R.string.InstallMeriDairy);

        //if Null All FAT, SNF, RATE
        String strSession = "";
        if (shift.equalsIgnoreCase("M") || shift.equalsIgnoreCase("morning")) {
            strSession = mContext.getString(R.string.MORNING);
        } else {
            strSession = mContext.getString(R.string.EVENING);
        }
        if (sessionManager.getValueSesion(Key_FatYES).length() == 0) {
            sessionManager.setValueSession(Key_FatYES, ONE);
        }
        if (sessionManager.getValueSesion(SessionManager.Key_SNFYES).length() == 0) {
            sessionManager.setValueSession(SessionManager.Key_SNFYES, ONE);
        }
        if (sessionManager.getValueSesion(SessionManager.Key_RateYES).length() == 0) {
            sessionManager.setValueSession(SessionManager.Key_RateYES, ONE);
        }

        String YESFAT = "", YESRate = "", YESSNF = "", YESBonus = "",YESTotal="";
        if (sessionManager.getValueSesion(Key_FatYES).matches(ONE)) {
            YESFAT = lableSmsFat + "  : " + actualFate + "\n";
        }
        if (sessionManager.getValueSesion(SessionManager.Key_RateYES).matches(ONE)) {
            YESRate = lableSmsrate + " " + lableSmsRs + " : " + rsPerKg + " /" + lableSmsLtr + "\n";
        }
        if (sessionManager.getValueSesion(SessionManager.Key_SNFYES).matches(ONE)) {
            YESSNF = lableSmsCLR + " : " + snf + "\n";
        }

        if (sessionManager.getValueSesion(Key_BonusYES).matches(ONE)) {
            YESBonus = lableBonus + " : " + totalBonus + "\n";
        }
        String lablemessage = mContext.getString(R.string.message);
        String lableWeekTotal = mContext.getString(R.string.Week_Total_Rs);

        if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).matches(ONE)){
            YESTotal  = lableWeekTotal + " : " + f + "\n";
        }

        smsContent = lableSmsName + " : " + selectedName + "\n" + lableSmsdate + " : " + selectedDate + "\n" + lableSmsshift + " : " + strSession + "\n" + lableSmsWeight + " : " + weight + "\n" + YESFAT + YESSNF + YESRate + YESBonus + lableSmsRs + " : " + totalPayment + "\n" +YESTotal + lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(mContext, "saveGreatingSms");
                /*+ "\n" +
                lableSmsInstallMeriDairy + "  " +
                meriDairyLink;*/
        printLog("smsContent==smsAlwaysSend==", "" + smsAlwaysSend);
        printLog("smsContent==" + from, smsContent);


        printLog("smsFrom===" + from, sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting));

        if (smsContent.length() > 0) {
            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(ONE)) {
                if (smsAlwaysSend) {
                    if (sessionManager.getIntValueSesion(Key_PhoneSMSApp) == 1) {
                        SendFromPhoneApp(mContext, Phone_number, smsContent);
                    } else {
                        sendMessageTOAnotherApp(mContext, Phone_number, smsContent);
                    }
                }
            }
        }

        return smsContent;
    }


    public static void sellMilkEntryFixedPrice(Context context, String phoneNo, String name, String date, String shift, String weight, String ratePerLiter, String total, Float f) {
        SessionManager sessionManager = new SessionManager(context);

        Log.d(TAG, "sellMilkEntryFixedPrice: " + phoneNo + "//" + name + "//" + weight);
        BuyMilkBonusPrice = sessionManager.getFloatValueSession(SessionManager.Key_SaleMilkBonusRate);

        String lableSmsName = context.getString(R.string.Name);
        String lablemessage = context.getString(R.string.message);
        String weekPrice = context.getString(R.string.Week_Total_Rs);
        String lableSmsdate = context.getString(R.string.Date);
        String lableSmsshift = context.getString(R.string.Shift);
        String lableSmsrate = context.getString(R.string.Rate);
        String lableBonus = context.getString(R.string.Bonus);
        String lableSmsRs = context.getString(R.string.Rs);
        String lableSmsLtr = context.getString(R.string.Ltr);
        String lableSmsWeight = context.getString(R.string.Weight)+"/"+ context.getString(R.string.Quantity);

        String YESFAT = "", YESRate = "", YESSNF = "", YESBonus = "",YESTotal = "";
        if (sessionManager.getValueSesion(SessionManager.Key_RateYES).matches(ONE)) {
            YESRate = lableSmsrate + " " + lableSmsRs + " : " + ratePerLiter + " /" + lableSmsLtr + "\n";
        }
        if (sessionManager.getValueSesion(Key_BonusYES).matches(ONE)) {
            YESBonus = lableBonus + " : " + BuyMilkBonusPrice + "\n";
        }

        if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).matches(ONE)){
            YESTotal  = weekPrice + " : " + f + "\n";
        }

        String smsContent = lableSmsName + " : " + name + "\n" + lableSmsdate + " : " + date + "\n" + lableSmsshift + " : " + shift + "\n" + lableSmsWeight + " : " + weight + "\n" + YESRate + YESBonus + lableSmsRs + " : " + total + "\n" + YESTotal + lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(context, "saveGreatingSms");
        Log.d(TAG, "sellMilkEntryFixedPrice2131: " + smsContent.toString());
        if (smsContent.length() > 0) {
            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(ONE)) {
                if (true) {
                    if (sessionManager.getIntValueSesion(Key_PhoneSMSApp) == 1) {
                        SendFromPhoneApp(context, phoneNo, smsContent);
                    } else {
                        sendMessageTOAnotherApp(context, phoneNo, smsContent);
                    }
                }
            }
        }
    }

    public static String sellMilkEntryFixedPricebt(Context context, String customerd, String phoneNo, String name, String date, String shift, String weight, String ratePerLiter, String total, Float f) {
        SessionManager sessionManager = new SessionManager(context);

        Log.d(TAG, "sellMilkEntryFixedPrice: " + phoneNo + "//" + name + "//" + weight);
        BuyMilkBonusPrice = sessionManager.getFloatValueSession(SessionManager.Key_SaleMilkBonusRate);

        String lableSmsName = context.getString(R.string.Name);
        String lablemessage = context.getString(R.string.message);
        String weekPrice = context.getString(R.string.Week_Total_Rs);
        String lableSmsdate = context.getString(R.string.Date);
        String lableSmsshift = context.getString(R.string.Shift);
        String lableSmsrate = context.getString(R.string.Rate);
        String lableBonus = context.getString(R.string.Bonus);
        String lableSmsRs = context.getString(R.string.Rs);
        String lableSmsLtr = context.getString(R.string.Ltr);
        String lableWeight = context.getString(R.string.Weight);
        String lableQuantity = context.getString(R.string.Quantity);
        // + lableSmsdate + " : " + date + "\n"
        String currentTime = new SimpleDateFormat("HH:mm:ss aa", Locale.getDefault()).format(new Date());
        String strDeshDotLine = "-------------------------------";

        String YESTotal = "";

        if (sessionManager.getValueSesion(SessionManager.Key_SendTotaLYES).matches(ONE)){
            YESTotal  = weekPrice + " : " + f + "\n";
        }

        String smsContent = date + "  " + currentTime + "\n" + strDeshDotLine + "\n" + "ID   " + customerd + "   " + name + "\n" + strDeshDotLine + "\n" + lableSmsshift + " : " + shift + "\n" + lableWeight + "/" + lableQuantity + " : " + weight + "\n" + lableSmsrate + " " + lableSmsRs + " : " + ratePerLiter + " /" + lableSmsLtr + "\n" + lableBonus + " : " + BuyMilkBonusPrice + "\n" + strDeshDotLine + "\n" + lableSmsRs + " : " + total + "\n" + strDeshDotLine + "\n" + YESTotal + strDeshDotLine + "\n" + lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(context, "saveGreatingSms") + "\n" + "\n";
        Log.d(TAG, "sellMilkEntryFixedPrice2131: " + smsContent.toString());
        return smsContent;


    }

    public static String MilkSMSContentTendays(Context mContext, String from, String Phone_number, String selectedName, String fromDate, String toDate, String totalWeight, String totalAmount) {

        String smsContentTendays = "";
        SessionManager sessionManager = new SessionManager(mContext);
        String lableSmsName = mContext.getString(R.string.Name);

        String lableSmsFromdate = mContext.getString(R.string.Fromdate);
        String lableSmsTomdate = mContext.getString(R.string.Todate);
        String lableSmsTotalWeight = mContext.getString(R.string.Total_Weight);
        String lableSmsTotalAmount = mContext.getString(R.string.Total_Amount);
        String lableSmsInstallMeriDairy = mContext.getString(R.string.InstallMeriDairy);
        smsContentTendays = lableSmsName + " : " + selectedName + "\n" + lableSmsFromdate + " : " + fromDate + "\n" + lableSmsTomdate + " : " + toDate + "\n" + lableSmsTotalWeight + "  : " + totalWeight + "\n" + lableSmsTotalAmount + " : " + totalAmount + "\n" + lableSmsInstallMeriDairy + "\n" + "http:bit.ly/2Cyy2Oe";

        System.out.println("smsContentTendays=====" + smsContentTendays);
        System.out.println("Phone_number=====" + Phone_number);

        if (smsContentTendays.length() > 0) {
            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(ONE)) {
                if (sessionManager.getIntValueSesion(Key_PhoneSMSApp) == 1) {
                    SendFromPhoneApp(mContext, Phone_number, smsContentTendays);
                } else {
                    sendMessageTOAnotherApp(mContext, Phone_number, smsContentTendays);
                }

            }
        }
        return smsContentTendays;
    }

    static String SendFromPhoneApp(Context context, String phoneNo, String sms) {
        String status = "";
        // int simID = getDefaultSiM(context);
        // Log.d("simID==", String.valueOf(simID));
        System.out.println("phoneNo==" + phoneNo);
        System.out.println("sms==" + sms);
        String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";

        Uri uri = Uri.parse("smsto:" + phoneNo);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", sms);
        context.startActivity(intent);

        return status;
    }

    static String SENDSMS(Context context, String phoneNo, String sms) {
        String status = "";
        //   int simID = getDefaultSiM(context);
        // Log.d("simID==", String.valueOf(simID));
        System.out.println("phoneNo==" + phoneNo);
        System.out.println("sms==" + sms);
        String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
        try {
            PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED), 0);
            SmsManager smsManager = SmsManager.getDefault();
            int MAX_SMS_MESSAGE_LENGTH = 160;
            int length = sms.length();
            System.out.println("message length==" + length);
            if (length > MAX_SMS_MESSAGE_LENGTH) {
                ArrayList<String> messagelist = smsManager.divideMessage(sms);
                smsManager.sendMultipartTextMessage(phoneNo, null, messagelist, null, null);
            } else {
                smsManager.sendTextMessage(phoneNo, null, sms, sentPI, deliveredPI);
            }
        } catch (Exception e) {
            showToast(context, e.getMessage());
            e.printStackTrace();
        }
        return status;
    }

    public static int getDefaultSiM(Context context) {

        int defaultSIM = 0;
        SubscriptionManager manager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            defaultSIM = SubscriptionManager.getDefaultSmsSubscriptionId();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return TODO;
            }
        }


        System.out.println("smsDefaultSim==" + defaultSIM);

        return defaultSIM;
    }

    public static void DialogSMS_Setting(final Context mContext, final String from, MessageDialogStatusListner messageStatusListner) {
        messageDialogStatusListner = messageStatusListner;
        final SessionManager sessionManager = new SessionManager(mContext);

        String selectedSMS = "";
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_sms_selection_setting);
        dialog.getDismissWithAnimation();
        dialog.setCanceledOnTouchOutside(true);

        Button btnSave, btnCancel;
        ImageButton imageButton;


        final CheckBox checkboxSim, checkboxWeb, checkboxAlways, checkboxAsk, checkboxOff;

        // set the custom dialog components - text, image and button
        checkboxSim = dialog.findViewById(R.id.checkboxSim);
        imageButton = dialog.findViewById(R.id.smsClose);
        checkboxWeb = dialog.findViewById(R.id.checkboxWeb);
        checkboxAlways = dialog.findViewById(R.id.checkboxAlways);
        checkboxAsk = dialog.findViewById(R.id.checkboxAsk);
        checkboxOff = dialog.findViewById(R.id.checkboxOff);

        selectedSMS = sessionManager.getValueSesion(Key_SendSmsSetting);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if (selectedSMS.equalsIgnoreCase(ZERO)) {
            checkboxOff.setChecked(true);
            checkboxAlways.setChecked(false);
            checkboxAsk.setChecked(false);
            checkboxSim.setChecked(false);
            checkboxWeb.setChecked(false);
        } else if (selectedSMS.equalsIgnoreCase(ONE)) {
            checkboxSim.setChecked(true);
            checkboxWeb.setChecked(false);
            checkboxOff.setChecked(false);
        } else {
            checkboxWeb.setChecked(true);
            checkboxSim.setChecked(false);
            checkboxOff.setChecked(false);
        }

        if (sessionManager.getValueSesion(Key_smsAlwyasOn_ASk).equalsIgnoreCase(YES)) {
            checkboxAlways.setChecked(true);
            checkboxAsk.setChecked(false);
        } else if (sessionManager.getValueSesion(Key_smsAlwyasOn_ASk).equalsIgnoreCase(NO)) {
            checkboxAlways.setChecked(false);
            checkboxAsk.setChecked(true);
        }

        checkboxSim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    DialogSelectSMSApp(mContext, checkboxSim);
                    checkboxWeb.setChecked(false);
                    checkboxOff.setChecked(false);
                }


            }
        });

        checkboxWeb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                String smsBalance = "0";
                smsBalance = sessionManager.getValueSesion(Key_BalancewWebSMS);
                if (isChecked) {
                    if (!smsBalance.equalsIgnoreCase("0")) {
                        checkboxSim.setChecked(false);
                        checkboxOff.setChecked(false);
                    } else {
                        checkboxWeb.setChecked(false);
                        checkboxSim.setChecked(true);
                        checkboxOff.setChecked(false);
                        dialog.dismiss();
                        showToast(mContext, mContext.getString(R.string.messageBuy));
                        mContext.startActivity(new Intent(mContext, SMSPlan_activity.class));
                    }
                }
            }
        });
        // Ask ,Always,OFF
        checkboxAlways.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    if (checkboxSim.isChecked() || checkboxWeb.isChecked()) {
                        checkboxAsk.setChecked(false);
                        checkboxOff.setChecked(false);
                    } else {
                        checkboxAlways.setChecked(false);
                        showToast(mContext, mContext.getString(R.string.Please_Select_SIMorWeb));
                    }
                }

            }
        });
        checkboxAsk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    if (checkboxSim.isChecked() || checkboxWeb.isChecked()) {
                        checkboxAlways.setChecked(false);
                        checkboxOff.setChecked(false);
                    } else {
                        checkboxAsk.setChecked(false);
                        showToast(mContext, mContext.getString(R.string.Please_Select_SIMorWeb));
                    }

                }
            }
        });
        checkboxOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    checkboxAlways.setChecked(false);
                    checkboxAsk.setChecked(false);
                    checkboxSim.setChecked(false);
                    checkboxWeb.setChecked(false);
                }
            }
        });

        btnSave = dialog.findViewById(R.id.btnSave);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        // if button is clicked, close the custom dialog
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!checkboxSim.isChecked() && !checkboxWeb.isChecked() && !checkboxOff.isChecked()) {
                    showToast(mContext, mContext.getString(R.string.Please_Select_SIMorWeb));

                } else if (!checkboxOff.isChecked() && !checkboxAsk.isChecked() && !checkboxAlways.isChecked()) {
                    showToast(mContext, mContext.getString(R.string.Please_Select_AlwyasOrAsk));

                } else {
                    dialog.dismiss();

                    // Selection SIM-Web------------------------------------------------------
                    if (checkboxSim.isChecked()) {
                        sessionManager.setValueSession(Key_SendSmsSetting, ONE);
                    } else if (checkboxWeb.isChecked()) {
                        sessionManager.setValueSession(Key_SendSmsSetting, TWO);
                    }

                    // Selection SIM-Web -----------------------------------------------------
                    if (checkboxAlways.isChecked()) {
                        if (checkboxSim.isChecked() || checkboxWeb.isChecked()) {
                            sessionManager.setValueSession(Key_smsAlwyasOn_ASk, YES);
                        } else {
                            showToast(mContext, mContext.getString(R.string.Please_Select_SIMorWeb));
                        }
                    } else if (checkboxAsk.isChecked()) {
                        if (checkboxSim.isChecked() || checkboxWeb.isChecked()) {
                            sessionManager.setValueSession(Key_smsAlwyasOn_ASk, NO);
                        } else {
                            showToast(mContext, mContext.getString(R.string.Please_Select_SIMorWeb));
                        }
                    }

                    // Selection All Off-----------------------------------------------------------
                    if (checkboxOff.isChecked()) {
                        sessionManager.setValueSession(Key_SendSmsSetting, ZERO);
                        sessionManager.setValueSession(Key_smsAlwyasOn_ASk, "off");
                    }
                    if (from.equalsIgnoreCase("AddMilkEntry")) {
                        messageDialogStatusListner.Messagedialog(true);
                    } else if (from.equalsIgnoreCase("AddEntryDateTime")) {
                        messageDialogStatusListner.Messagedialog(true);
                    } else if (from.equalsIgnoreCase("SettingUpdate")) {
                        messageDialogStatusListner.Messagedialog(true);
                    }
                    setMessageSetting(mContext, POST_TASK);
                }
            }

        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private static void DialogSelectSMSApp(Context mContext, CheckBox checkboxSim) {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);

        ImageView imgClosed;
        TextView tvDialogTitle, tvChart, tvTable;
        imgClosed = dialog.findViewById(R.id.imgClosed);

        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvChart = dialog.findViewById(R.id.tvBuyproduct);
        tvTable = dialog.findViewById(R.id.tvViewEvent);
        tvDialogTitle.setText(mContext.getString(R.string.select) + " " + mContext.getString(R.string.message) + " " + mContext.getString(R.string.app));

        tvChart.setText(mContext.getString(R.string.phone) + " " + mContext.getString(R.string.message) + " " + mContext.getString(R.string.app));
        tvTable.setText(mContext.getString(R.string.app_name) + " " + mContext.getString(R.string.message) + " " + mContext.getString(R.string.app));
        SessionManager sessionManager = new SessionManager(mContext);
        int PhoneSMSApp = sessionManager.getIntValueSesion(Key_PhoneSMSApp);


        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        tvChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setIntValueSession(Key_PhoneSMSApp, 1);

                dialog.dismiss();

            }
        });
        tvTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (installMeridairySMSApp(mContext)) {
                    sessionManager.setIntValueSession(Key_PhoneSMSApp, 0);

                    dialog.dismiss();
                }

            }
        });


        dialog.show();
    }

    public static void getSMSBalance(final Context mContext, int taskMethod) {
        if (isNetworkAvaliable(mContext)) {
            final SessionManager sessionManag = new SessionManager(mContext);
            NetworkTask webServiceCaller = new NetworkTask(taskMethod, mContext, "Please wait...", false) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("balance_sms")) {
                            String balance = jsonObject.getString("balance_sms");
                            sessionManag.setValueSession(Key_BalancewWebSMS, balance);
                            String smsType = sessionManag.getValueSesion(Key_SendSmsSetting);

                            // if sms balance 0
                            if (balance.equalsIgnoreCase("0") && smsType.equalsIgnoreCase(TWO)) {
                                sessionManag.setValueSession(Key_BalancewWebSMS, balance);
                                sessionManag.setValueSession(Key_SendSmsSetting, ONE);
                                showAlertBox(mContext, mContext.getString(R.string.webSMSPlanExpireMessage));
                                setMessageSetting(mContext, POST_TASK);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            webServiceCaller.execute(getSmsBalanceAPI + sessionManag.getValueSesion(KEY_UserID));

            /*if (taskMethod == POST_TASK_Log) {
                webServiceCaller.addNameValuePair("id", sessionManag.getValueSesion(KEY_UserID));
                webServiceCaller.addNameValuePair("phone_number", sessionManag.getValueSesion(KEY_Mobile));
                webServiceCaller.execute(ApiSentSms);
            } else {
                webServiceCaller.execute(getSmsBalanceAPI + sessionManag.getValueSesion(KEY_UserID));
            }*/
        }
    }

    public static void setMessageSetting(final Context mContext, int taskMethod) {
        if (isNetworkAvaliable(mContext)) {
            final SessionManager sessionManag = new SessionManager(mContext);
            NetworkTask serviceCaller = new NetworkTask(taskMethod, mContext, "Please wait...", false) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            sessionManag.setValueSession(Key_SendSmsSetting, jsonObject.getString("sms"));
                            sessionManag.setValueSession(Key_SNFYES, jsonObject.getString("snf"));
                            sessionManag.setValueSession(Key_FatYES, jsonObject.getString("fat"));
                            sessionManag.setValueSession(Key_RateYES, jsonObject.getString("price_ltr"));
                            sessionManag.setValueSession(Key_PurchaseBy, jsonObject.getString("purchase_by"));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };


            if (taskMethod == POST_TASK) {
                RequestBody body = new FormEncodingBuilder().addEncoded("id", sessionManag.getValueSesion(KEY_UserID)).addEncoded("sms", sessionManag.getValueSesion(Key_SendSmsSetting)).addEncoded("fat", sessionManag.getValueSesion(Key_FatYES)).addEncoded("snf", sessionManag.getValueSesion(Key_SNFYES)).addEncoded("bonus", sessionManag.getValueSesion(Key_BonusYES)).addEncoded("price_ltr", sessionManag.getValueSesion(Key_RateYES)).addEncoded("purchase_by", sessionManag.getValueSesion(Key_PurchaseBy)).build();
                serviceCaller.addRequestBody(body);
                serviceCaller.execute(smsSettingAPI);

            } else {
                serviceCaller.execute(getSmsSettingAPI + sessionManag.getValueSesion(KEY_UserID));
            }
        }
    }
}
