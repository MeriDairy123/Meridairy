package b2infosoft.milkapp.com.DeliveryBoy.Fragment;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogMachineSetUp;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.resetConnection;
import static b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkDateTimeFragment.checkUserPlanExpiryStatus;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.DialogSMS_Setting;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.BuyMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.buyMilkRateSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.getCheckExpirePlanAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getMilkPriceAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.updateBonusAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_MachineAuto;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BalancewWebSMS;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_Expire_Date;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_Start_Date;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_User_Status;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCalanderDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentFromDeliveryBoy;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.webservice.NetworkTask.GET_TASK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputEditText;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass;
import b2infosoft.milkapp.com.BuyPlan.FragmentMembershipPlans;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFragment;
import b2infosoft.milkapp.com.MilkEntrySMS.MessageDialogStatusListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.WhatsappSetting;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

public class DeliveryBoyPurchaseMilkDateTimeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MessageDialogStatusListner {

    RadioGroup rgFat;
    ImageView img_sms_status;
    int userRemainingDay = 0, isOnline = 1;
    View view, layoutByFat, layout_SimSetting, layoutCowBuff, tvGeneralFat;
    Bundle bundle;
    BroadcastReceiver receiver;
    private Context mContext;
    private TextView tvDate, btn_Morning, btn_Evening;
    private String addOrSaleMilk = "0";
    private String userStatus = "0";
    private String selectedSMS = "";
    private String formattedDate = "";
    private String fatRate = "";
    private String cowFatRate = "";
    private String machineCode = "";
    private boolean isMachineAuto = false;
    private String buffFateRate = "";
    private String bonusType = "Buy";
    private String rateType = "";
    private String fatType = "";
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private SwitchCompat swichOnOffEntry, swichPrint_Reciept, switchMachine;
    private TextView tvSMSSetting, tv_sim, tv_web, tv_off;
    private ImageButton imageButtonWhsapp;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};
    private static String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_PRIVILEGED};


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.deliveryboy_fragment_add_entry_date_time, container, false);

        mContext = getActivity();

        checkUserPlanExpiryStatus(mContext);
        sessionManager = new SessionManager(mContext);
        if (!UtilityMethod.hasPermissionsAddEntry(mContext, UtilityMethod.ADD_ENTRY_PERMISSION)) {
            ActivityCompat.requestPermissions((Activity) mContext, UtilityMethod.ADD_ENTRY_PERMISSION, PERMISSION_ALL);
        }

        initView();
        if (isConnected()) {
            swichOnOffEntry.setChecked(true);
            swichOnOffEntry.setText(mContext.getString(R.string.Save_Data_Offline));
            isOnline = 1;

            setMilkEntryStatus();
        } else {
            isOnline = 0;
            swichOnOffEntry.setChecked(false);
            swichOnOffEntry.setText(mContext.getString(R.string.offline));

            setMilkEntryStatus();
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initAddMilk();

            }
        };

        clickOnWhsappImage();

        return view;

    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        swichOnOffEntry = view.findViewById(R.id.swichOnOffEntry);
        swichPrint_Reciept = view.findViewById(R.id.swichPrint_Reciept);
        switchMachine = view.findViewById(R.id.switchMachine);
        TextView tvChangeMachine = view.findViewById(R.id.tvChangeMachine);
        layout_SimSetting = view.findViewById(R.id.layout_SimSetting);
        tvSMSSetting = view.findViewById(R.id.tvSMSSetting);
        tv_sim = view.findViewById(R.id.tv_sim);
        imageButtonWhsapp = view.findViewById(R.id.whatappEntry);
        tv_web = view.findViewById(R.id.tv_web);
        tv_off = view.findViewById(R.id.tv_off);
        img_sms_status = view.findViewById(R.id.img_sms_status);
        btn_Morning = view.findViewById(R.id.btn_Morning);
        btn_Evening = view.findViewById(R.id.btn_Evening);
        tvDate = view.findViewById(R.id.tvDate);
        layoutByFat = view.findViewById(R.id.layoutByFat);
        layoutCowBuff = view.findViewById(R.id.layoutCowBuff);
        tvGeneralFat = view.findViewById(R.id.tvGeneralFat);
        rgFat = view.findViewById(R.id.rgFat);


        tvDate.setOnClickListener(this);
        btn_Morning.setOnClickListener(this);
        btn_Evening.setOnClickListener(this);

        formattedDate = getSimpleDate();
        tvDate.setText(formattedDate);

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
        toolbar.setTitle(mContext.getString(R.string.ADD_ENTRY));

        tvChangeMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMachineSetUp(mContext);
            }
        });


        swichOnOffEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline = 1;
                if (isConnected()) {
                    if (swichOnOffEntry.isChecked()) {
                        swichOnOffEntry.setChecked(true);
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.You_are_storing_Online_Mode));
                        swichOnOffEntry.setText(mContext.getString(R.string.Save_Data_Offline));
                    } else {
                        swichOnOffEntry.setChecked(false);
                        isOnline = 0;
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.NoInternetForSwitch));
                        swichOnOffEntry.setText(mContext.getString(R.string.offline));
                    }
                } else {
                    swichOnOffEntry.setChecked(false);
                    isOnline = 0;
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.NoInternetForSwitch));
                    swichOnOffEntry.setText(mContext.getString(R.string.offline));
                }
                setMilkEntryStatus();
            }
        });

        swichPrint_Reciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int permission1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN);
                if (permission1 != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_STORAGE, 1);
                } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATION, 1);
                } else {
                    if (swichPrint_Reciept.isChecked()) {

                        if (isBluetoothHeadsetConnected()) {
                            if (mDevice == null || mSocket == null || mOutputStream == null || !enableBluetooth()) {
                                resetConnection();
                                enableBluetooth();
                                BluetoothClass.dialogBluetooth(mContext);
                            }
                            sessionManager.setValueSession(SessionManager.Key_PrintReciept, YES);
                        } else {
                            resetConnection();
                            showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
                            enableBluetooth();
                            BluetoothClass.dialogBluetooth(mContext);
                            swichPrint_Reciept.setChecked(true);
                        }
                    } else {
                        resetConnection();
                        sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);
                    }
                }


            }
        });

        switchMachine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                int permission1 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                int permission2 = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN);
                if (permission1 != PackageManager.PERMISSION_GRANTED) {
                    // We don't have permission so prompt the user
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_STORAGE, 1);
                } else if (permission2 != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS_LOCATION, 1);
                } else {
                    Log.d("TAG", "onCheckedChanged1: ");
                    machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);

                    if (compoundButton.isChecked()) {
                        if (mDevice == null || mSocket == null || mOutputStream == null || !enableBluetooth()) {
                            resetConnection();
                            enableBluetooth();
                            BluetoothClass.dialogBluetooth(mContext);
                            switchMachine.setChecked(false);
                        } else if (machineCode.isEmpty()) {

                            BluetoothClass.dialogMachineSetUp(mContext);
                            switchMachine.setChecked(false);

                        } else {
                            sessionManager.setBooleanValue(KEY_MachineAuto, compoundButton.isChecked());
                        }
                    } else {
                        sessionManager.setBooleanValue(KEY_MachineAuto, compoundButton.isChecked());
                    }
                }
            }


        });

        machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);
        isMachineAuto = sessionManager.getBooleanValue(KEY_MachineAuto);
        switchMachine.setChecked(isMachineAuto);
        /*if (machineCode.length()==0&&isMachineAuto){
            dialogMachineSetUp(mContext);
        }else {
            switchMachine.setChecked(isMachineAuto);
        }*/
        layout_SimSetting.setOnClickListener(this);
        tvSMSSetting.setOnClickListener(this);
        initAddMilk();


    }

    private void initAddMilk() {
        BuyMilkBonusPrice = sessionManager.getFloatValueSession(SessionManager.Key_BuyMilkBonusRate);
        rateType = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KeyBuyMilkRateType));
        fatType = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KeyBuyFatType));
        fatRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyFatPrice));
        cowFatRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyCowFatPrice));
        buffFateRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyBuffFatPrice));
        checkRateType(rateType);
        checkFatType();


        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            swichPrint_Reciept.setChecked(true);
        } else {
            sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);
            swichPrint_Reciept.setChecked(false);
        }

        upDateSMSSetting();
    }

    private void upDateSMSSetting() {
        selectedSMS = nullCheckFunction(sessionManager.getValueSesion(Key_SendSmsSetting));
        if (selectedSMS.length() == 0) {
            selectedSMS = ONE;
            sessionManager.setValueSession(Key_SendSmsSetting, selectedSMS);
            sessionManager.setValueSession(Key_smsAlwyasOn_ASk, NO);
        }
        if (selectedSMS.equalsIgnoreCase(ZERO)) {
            tv_off.setVisibility(View.VISIBLE);
            tv_sim.setVisibility(View.GONE);
            tv_web.setVisibility(View.GONE);
            img_sms_status.setImageResource(R.drawable.ic_close);
        } else if (selectedSMS.equalsIgnoreCase(TWO)) {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.GONE);
            tv_web.setVisibility(View.VISIBLE);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);
            String smsBalance = "";
            smsBalance = sessionManager.getValueSesion(Key_BalancewWebSMS);
            tv_web.setText(mContext.getString(R.string.web) + "  " + smsBalance);

        } else {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.VISIBLE);
            tv_web.setVisibility(View.GONE);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);

        }


    }

    public boolean enableBluetooth() {
        try {
            BluetoothAdapter badapter = BluetoothAdapter.getDefaultAdapter();
            if (badapter != null) {
                badapter.enable();
                if (!badapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void checkRateType(String rateTyp) {
        rateType = rateTyp;
        if (!rateType.equals("")) {
            updateViewRateSetting(rateType);
        } else {
            sessionManager.setValueSession(SessionManager.KeyBuyMilkRateType, "1");
            rateType = "";
            updateMilkRateTypeSetting();
        }

    }

    private void checkFatType() {

//        if (fatType.equalsIgnoreCase("1")) {
//            tvGeneralFat.setVisibility(View.GONE);
//            layoutCowBuff.setVisibility(View.VISIBLE);
//        } else {
//            tvGeneralFat.setVisibility(View.VISIBLE);
//            layoutCowBuff.setVisibility(View.GONE);
//        }

        sessionManager.setValueSession(SessionManager.KeyBuyFatType, fatType);


    }

    private void updateViewRateSetting(String rateType) {
        if (!isNetworkAvaliable(mContext)) {
            sessionManager.setValueSession(SessionManager.KeyBuyMilkRateType, rateType);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvSMSSetting:
                Intent intent = new Intent(mContext, WhatsappSetting.class);
                startActivity(intent);
                //  DialogSMS_Setting(mContext, "AddEntryDateTime", this);
                break;
            case R.id.layout_SimSetting:
                DialogSMS_Setting(mContext, "AddEntryDateTime", this);
                break;
            case R.id.tvDate:
                getCalanderDate(mContext, tvDate);

                break;
            case R.id.btn_Morning:
                formattedDate = tvDate.getText().toString().trim();
                Constant.SelectedDate = "";
                Constant.SelectedDate = formattedDate;
                Constant.strSession = "";
                Constant.strSession = "morning";

                getUserStatus(addOrSaleMilk);

                break;
            case R.id.btn_Evening:

                formattedDate = tvDate.getText().toString().trim();
                Constant.SelectedDate = "";
                Constant.SelectedDate = formattedDate;
                Constant.strSession = "";
                Constant.strSession = "evening";

                getUserStatus(addOrSaleMilk);

                break;


        }
    }


    private void getUserStatus(String addOrSaleMilk) {
        // sessionManager.db.deleteMilkEntryShiftWise("buy", SelectedDate, strSession);
        userStatus = sessionManager.getValueSesion(Key_User_Status);
        userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);

        if (!tvDate.getText().toString().equals("")) {
            Fragment fragment = null;
//            if (userStatus.equals("0") || userRemainingDay <= 10) {
//                fragment = new FragmentMembershipPlans();
//                Bundle bundle = new Bundle();
//                bundle.putString("FromWhere", "DeliveryBoy");
//                bundle.putString("userStatus", userStatus);
//                bundle.putString("DeshBoardMilk", addOrSaleMilk);
//                fragment.setArguments(bundle);
//                goNextFragmentFromDeliveryBoy(mContext, fragment);
//            } else {
//                sessionManager.setValueSession("blutooth","not_connected");
//                fragment = new DeliveryBoyPurchaseMilkEntryFragment();
//                bundle = new Bundle();
//                bundle.putString("FromWhere", "AddEntryDate");
//                bundle.putString("userStatus", userStatus);
//                //  fragment.setArguments(bundle);
//                goNextFragmentFromDeliveryBoy(mContext, fragment);
//
//            }

            sessionManager.setValueSession("blutooth","not_connected");
            fragment = new DeliveryBoyPurchaseMilkEntryFragment();
            bundle = new Bundle();
            bundle.putString("FromWhere", "AddEntryDate");
            bundle.putString("userStatus", userStatus);
            //  fragment.setArguments(bundle);
            goNextFragmentFromDeliveryBoy(mContext, fragment);


        } else {
            Toast.makeText(mContext, getString(R.string.Select_Date), Toast.LENGTH_SHORT).show();
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.swichOnOffEntry:
                isOnline = 1;
                if (isConnected()) {
                    if (swichOnOffEntry.isChecked()) {
                        UtilityMethod.showAlertBox(mContext, getString(R.string.You_are_storing_Online_Mode));
                    } else {
                        swichOnOffEntry.setChecked(true);
                    }
                } else {
                    swichOnOffEntry.setChecked(false);
                    isOnline = 0;
                    UtilityMethod.showAlertBox(mContext, getString(R.string.NoInternetForSwitch));
                }
                setMilkEntryStatus();

                break;
        }
    }

    public void setMilkEntryStatus() {
        sessionManager.setIntValueSession(SessionManager.KeyIsOnline, isOnline);
    }

    public String md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void DialogSMS() {


        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_sms_alert);
        dialog.setCancelable(false);
        Button btnYes, btnNo;

        // set the custom dialog components - text, image and button
        btnYes = dialog.findViewById(R.id.btnYes);
        btnNo = dialog.findViewById(R.id.btnNo);

        // if button is clicked, close the custom dialog
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sessionManager.setValueSession(Key_SendSmsSetting, ONE);

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.setValueSession(Key_SendSmsSetting, ZERO);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void clickOnWhsappImage() {
        imageButtonWhsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WhatsappSetting.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void Messagedialog(boolean status) {
        upDateSMSSetting();
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

    private void updateMilkRateTypeSetting() {
        sessionManager.setValueSession(SessionManager.KeyBuyMilkRateType, rateType);
        sessionManager.setValueSession(SessionManager.KeyBuyFatType, fatType);
        sessionManager.setFloatValueSession(SessionManager.Key_BuyFatPrice, Float.parseFloat(fatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_BuyCowFatPrice, Float.parseFloat(cowFatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_BuyBuffFatPrice, Float.parseFloat(buffFateRate));

        if (isNetworkAvaliable(mContext)) {

            @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            if (rateType.equals("")) {
                                checkRateType(nullCheckFunction(jsonData.getString("entry_type")));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_dairy_id))
                    .addEncoded("entry_type", rateType)
                    .addEncoded("milk_price", fatRate)
                    .addEncoded("milk_price_cow", cowFatRate)
                    .addEncoded("milk_price_buffalo", buffFateRate)
                    .addEncoded("fat_type_buy", fatType).build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(buyMilkRateSetting);
        }
    }
}
