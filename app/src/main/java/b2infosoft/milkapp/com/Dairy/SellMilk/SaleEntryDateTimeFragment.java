package b2infosoft.milkapp.com.Dairy.SellMilk;


import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogMachineSetUp;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.resetConnection;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.DialogSMS_Setting;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.SaleMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.getSaleFatMilkPriceAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getSellingMilkPriceAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.saleMilkRateSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.appglobal.Constant.updateBonusAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_MachineAuto;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BalancewWebSMS;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_RemainingDay;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleBuffaloFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleCowFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFatPrice;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SendSmsSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_User_Status;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ZERO;
import static b2infosoft.milkapp.com.useful.ConnectivityReceiver.isConnected;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.SMSPERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getCalanderDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.GET_TASK;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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

import java.util.Objects;

import b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass;
import b2infosoft.milkapp.com.BuyPlan.FragmentMembershipPlans;
import b2infosoft.milkapp.com.MilkEntrySMS.MessageDialogStatusListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.activity.WhatsappSettingSale;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;


public class SaleEntryDateTimeFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, MessageDialogStatusListner {
    RadioGroup rgFat;
    BroadcastReceiver receiver;
    private Context mContext;
    private TextView tvDate, btn_Morning, btn_Evening, btnUpdateBonus, btnUpdateFatRate;
    private TextInputEditText ediFatRate, ediFatRateCow, ediFatRateBuffalo, ediBonus;
    private RadioButton radioFixRate, radioBtnFat, radioBtnSNF, radioBtnCLR, rdFatGeneral, rdFatCowBuffalo;
    private Toolbar toolbar;
    private TextView tv_sim, tv_web, tv_off, txt_whatsapp_sale;
    private ImageButton whatsappSaleIb;

    private SessionManager sessionManager;
    private SwitchCompat swichOnOffEntry, swichPrint_Reciept, switchMachine;
    private ImageView img_sms_status;
    private RelativeLayout layout_SimSetting;
    private int userRemainingDay = 0, isOnline = 1;
    private String machineCode = "", formattedDate = "", selectedSMS = "", userStatus = "0", fatRate = "0", cowFatRate = "0", buffFateRate = "0", saleByFixRate = "Fix", bonusType = "sale", rateType, fatType;
    private View view, layoutByFat, layoutCowBuff, tvGeneralFat;
    private Fragment fragment = null;
    private Bundle bundle = null;
    boolean isMachineAuto = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sale_entry_date_time, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        saleByFixRate = "FixRate";
        if (!hasPermissions(mContext, SMSPERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, SMSPERMISSIONS, PERMISSION_ALL);
        }
        initView();
        if (isConnected()) {
            swichOnOffEntry.setChecked(true);
            swichOnOffEntry.setText(mContext.getString(R.string.Save_Data_Offline));
            isOnline = 1;
        } else {
            isOnline = 0;
            swichOnOffEntry.setChecked(false);
            swichOnOffEntry.setText(mContext.getString(R.string.offline));
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                saleMilk();

            }
        };

        return view;
    }

    private void initView() {
        toolbar = view.findViewById(R.id.toolbar);
        swichOnOffEntry = view.findViewById(R.id.swichOnOffEntry);
        swichPrint_Reciept = view.findViewById(R.id.swichPrint_Reciept);
        switchMachine = view.findViewById(R.id.switchMachine);
        TextView tvChangeMachine = view.findViewById(R.id.tvChangeMachine);
        layout_SimSetting = view.findViewById(R.id.layout_SimSetting);
        tv_sim = view.findViewById(R.id.tv_sim);
        txt_whatsapp_sale = view.findViewById(R.id.tvSMSSettingSale);
        whatsappSaleIb = view.findViewById(R.id.whatappEntrysale);

        tv_web = view.findViewById(R.id.tv_web);
        tv_off = view.findViewById(R.id.tv_off);
        img_sms_status = view.findViewById(R.id.img_sms_status);
        radioFixRate = view.findViewById(R.id.radioFixRate);
        radioBtnFat = view.findViewById(R.id.radioBtnFat);
        radioBtnSNF = view.findViewById(R.id.radioBtnSNF);
        radioBtnCLR = view.findViewById(R.id.radioBtnCLR);
        layoutByFat = view.findViewById(R.id.layoutByFat);

        layoutCowBuff = view.findViewById(R.id.layoutCowBuff);
        tvGeneralFat = view.findViewById(R.id.tvGeneralFat);
        rgFat = view.findViewById(R.id.rgFat);
        rdFatGeneral = view.findViewById(R.id.rdFatGeneral);
        rdFatCowBuffalo = view.findViewById(R.id.rdFatCowBuffalo);

        ediFatRate = view.findViewById(R.id.ediFatRate);
        ediFatRateCow = view.findViewById(R.id.ediFatRateCow);
        ediFatRateBuffalo = view.findViewById(R.id.ediFatRateBuffalo);
        btnUpdateFatRate = view.findViewById(R.id.btnUpdateFatRate);
        ediBonus = view.findViewById(R.id.ediBonus);
        btnUpdateFatRate = view.findViewById(R.id.btnUpdateFatRate);

        radioBtnCLR.setText(mContext.getString(R.string.CLR) + "/" + mContext.getString(R.string.Fat));

        radioFixRate.setOnClickListener(this);
        radioBtnFat.setOnClickListener(this);
        radioBtnSNF.setOnClickListener(this);
        radioBtnCLR.setOnClickListener(this);

        rdFatGeneral.setOnClickListener(this);
        rdFatCowBuffalo.setOnClickListener(this);
        rdFatCowBuffalo.setText(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Buff));
        ediFatRateCow.setHint(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Fat_Rat));
        ediFatRateBuffalo.setHint(mContext.getString(R.string.Buff) + "/" + mContext.getString(R.string.Fat_Rat));


        btn_Morning = view.findViewById(R.id.btn_Morning);
        btn_Evening = view.findViewById(R.id.btn_Evening);
        btnUpdateBonus = view.findViewById(R.id.btnUpdateBonus);
        tvDate = view.findViewById(R.id.tvDate);
        tvDate.setOnClickListener(this);

        formattedDate = getSimpleDate();
        tvDate.setText(formattedDate);
        btn_Morning.setOnClickListener(this);
        btn_Evening.setOnClickListener(this);
        btnUpdateFatRate.setOnClickListener(this);
        btnUpdateBonus.setOnClickListener(this);

         txt_whatsapp_sale.setOnClickListener(this);
         whatsappSaleIb.setOnClickListener(this);

        layout_SimSetting.setOnClickListener(this);
        toolbar.setTitle(mContext.getString(R.string.SALE_MILK));
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });
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
        });
        switchMachine.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
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
        });
        saleMilk();
        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            swichPrint_Reciept.setChecked(true);
        } else {
            sessionManager.setValueSession(SessionManager.Key_PrintReciept, NO);
            swichPrint_Reciept.setChecked(false);
        }

    }

    public static void getSaleFatMilkPrice(Context mContext) {
        final SessionManager sessionManager = new SessionManager(mContext);

        NetworkTask caller = new NetworkTask(GET_TASK, mContext, "Processing..", false) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        sessionManager.setValueSession(SessionManager.Key_SaleFatPrice, jsonObject.getString("fat_type_sale"));
                        sessionManager.setFloatValueSession(SessionManager.Key_SaleFatPrice, (float) jsonObject.getDouble("milk_price_sale"));
                        sessionManager.setFloatValueSession(Key_SaleCowFatPrice, (float) jsonObject.getDouble("milk_price_sale_cow"));
                        sessionManager.setFloatValueSession(SessionManager.Key_SaleBuffaloFatPrice, (float) jsonObject.getDouble("milk_price_sale_buffalo"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        caller.execute(getSaleFatMilkPriceAPI.replace("@dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)));
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

        } else if (selectedSMS.equalsIgnoreCase(ONE)) {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.VISIBLE);
            tv_web.setVisibility(View.GONE);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);

        } else if (selectedSMS.equalsIgnoreCase(TWO)) {
            tv_off.setVisibility(View.GONE);
            tv_sim.setVisibility(View.GONE);
            tv_web.setVisibility(View.VISIBLE);
            img_sms_status.setImageResource(R.drawable.ic_right_tick);
            String smsBalance = sessionManager.getValueSesion(Key_BalancewWebSMS);
            tv_web.setText(mContext.getString(R.string.web) + "  " + smsBalance);
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

    public void setMilkEntryStatus() {
        sessionManager.setIntValueSession(SessionManager.KeyIsOnline, isOnline);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.whatappEntrysale:
                Intent intent = new Intent(mContext, WhatsappSettingSale.class);
                startActivity(intent);
                break;
            case R.id.tvSMSSettingSale:
                Intent intentt = new Intent(mContext, WhatsappSettingSale.class);
                startActivity(intentt);
                break;


            case R.id.layout_SimSetting:
                DialogSMS_Setting(mContext, "AddEntryDateTime", this);
                break;
            case R.id.tvDate:
                getCalanderDate(mContext, tvDate);
                break;
            case R.id.btnUpdateBonus:
                if (!ediBonus.getText().toString().trim().equals("")) {
                    updateBonus();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_Bonus_Amount));
                }
                break;
            case R.id.btn_Morning:
                formattedDate = tvDate.getText().toString().trim();
                Constant.SelectedDate = formattedDate;
                Constant.strSession = "morning";
                getUserStatus();
                break;
            case R.id.btn_Evening:
                formattedDate = tvDate.getText().toString().trim();
                Constant.SelectedDate = formattedDate;
                Constant.strSession = "evening";
                getUserStatus();
                break;
            case R.id.btnUpdateFatRate:
                fatRate = ediFatRate.getText().toString();
                cowFatRate = ediFatRateCow.getText().toString();
                buffFateRate = ediFatRateBuffalo.getText().toString();

                if (rdFatGeneral.isChecked() && !fatRate.equals("") && !fatRate.equals("0") && !fatRate.equals("0.00")) {
                    if (cowFatRate.length() == 0) {
                        cowFatRate = "0.00";
                    }
                    if (buffFateRate.length() == 0) {
                        buffFateRate = "0.00";
                    }

                    updateMilkRateTypeSetting();
                } else if (rdFatCowBuffalo.isChecked() && !cowFatRate.equals("") && !buffFateRate.equals("")) {
                    if (fatRate.length() == 0) {
                        fatRate = "0.00";
                    }
                    updateMilkRateTypeSetting();
                } else {
                    UtilityMethod.showAlertBox(mContext, getString(R.string.Please_Enter_Milk_Rate));
                }

                break;
            case R.id.rdFatGeneral:
                fatType = "0";
                fatRate = ediFatRate.getText().toString();
                cowFatRate = ediFatRateCow.getText().toString();
                buffFateRate = ediFatRateBuffalo.getText().toString();
                if (fatRate.length() == 0) {
                    fatRate = "0.00";
                }
                if (cowFatRate.length() == 0) {
                    cowFatRate = "0.00";
                }
                if (buffFateRate.length() == 0) {
                    buffFateRate = "0.00";
                }
                checkFatType();
                updateMilkRateTypeSetting();
                break;
            case R.id.rdFatCowBuffalo:
                fatType = "1";
                fatRate = ediFatRate.getText().toString();
                cowFatRate = ediFatRateCow.getText().toString();
                buffFateRate = ediFatRateBuffalo.getText().toString();
                if (fatRate.length() == 0) {
                    fatRate = "0.00";
                }
                if (cowFatRate.length() == 0) {
                    cowFatRate = "0.00";
                }
                if (buffFateRate.length() == 0) {
                    buffFateRate = "0.00";
                }
                checkFatType();
                updateMilkRateTypeSetting();

                break;

            case R.id.radioBtnSNF:
                rateType = "1";
                updateMilkRateTypeSetting();
                updateViewRateSetting(rateType);
                break;
            case R.id.radioBtnFat:
                rateType = "2";
                updateMilkRateTypeSetting();
                updateViewRateSetting(rateType);
                break;
            case R.id.radioBtnCLR:
                rateType = "4";
                updateMilkRateTypeSetting();
                updateViewRateSetting(rateType);
                break;
            case R.id.radioFixRate:

                rateType = "5";
                updateMilkRateTypeSetting();
                updateViewRateSetting(rateType);
                break;


        }
    }

    public static void getSellMilkFixPrice(Context mContext) {
        final SessionManager sessionManager = new SessionManager(mContext);

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", false) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    sessionManager.setFloatValueSession(SessionManager.Key_SellMilkPrice, (float) jsonObject.getDouble("milk_shell_price"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).build();
        caller.addRequestBody(body);

        caller.execute(getSellingMilkPriceAPI);
    }

    private void checkRateType(String rateTyp) {
        rateType = rateTyp;
        if (!rateType.equals("")) {
            updateViewRateSetting(rateType);
        } else {
            rateType = "0";
            sessionManager.setValueSession(SessionManager.Key_SaleRateType, rateType);
            if (!isNetworkAvaliable(mContext)) {
                updateViewRateSetting(rateType);
            }
            updateMilkRateTypeSetting();
        }

    }

    private void checkFatType() {
        if (fatType.equalsIgnoreCase("1")) {
            rdFatCowBuffalo.setChecked(true);
            tvGeneralFat.setVisibility(View.GONE);
            layoutCowBuff.setVisibility(View.VISIBLE);
        } else {
            rdFatGeneral.setChecked(true);
            tvGeneralFat.setVisibility(View.VISIBLE);
            layoutCowBuff.setVisibility(View.GONE);
        }
        sessionManager.setValueSession(Key_SaleFateType, fatType);

    }

    private void getUserStatus() {
        sessionManager.db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
        if (sessionManager.getValueSesion(Key_User_Status).length() == 0) {
            userStatus = "1";
            sessionManager.setValueSession(Key_User_Status, userStatus);
            sessionManager.setIntValueSession(Key_RemainingDay, 10);
        } else {
            userStatus = sessionManager.getValueSesion(Key_User_Status);
            userRemainingDay = sessionManager.getIntValueSesion(Key_RemainingDay);
        }

        if (!formattedDate.equals("")) {
            if (userStatus.equals("0") || userRemainingDay <= 10) {
                fragment = new FragmentMembershipPlans();
                bundle = new Bundle();
                bundle.putString("FromWhere", "SaleEntryDate");
                bundle.putString("userStatus", userStatus);
                bundle.putString("DeshBoardMilk", saleByFixRate);
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);

            } else {

                if (saleByFixRate.equalsIgnoreCase("Fix")) {
                    fragment = new SaleMilkEntryFragment();
                    bundle = new Bundle();
                    bundle.putString("FromWhere", "SaleEntryDate");
                    bundle.putString("userStatus", userStatus);
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);

                } else if (saleByFixRate.equals("FixRate")) {

                    fragment = new DailySaleMilkFixRateFragment();
                    bundle = new Bundle();
                    bundle.putString("FromWhere", "SaleEntryDate");
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                }
            }

        } else {
            showToast(mContext, getString(R.string.Select_Date));
        }
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.swichOnOffEntry:
                if (isConnected()) {
                    if (swichOnOffEntry.isChecked()) {
                        sessionManager.setIntValueSession(SessionManager.KeyIsOnline, 1);
                        UtilityMethod.showAlertBox(mContext, getString(R.string.You_are_storing_Online_Mode));
                    } else {
                        sessionManager.setIntValueSession(SessionManager.KeyIsOnline, 0);
                        UtilityMethod.showAlertBox(mContext, getString(R.string.OfflineDataAlert));
                    }
                } else {
                    swichOnOffEntry.setChecked(false);
                    sessionManager.setIntValueSession(SessionManager.KeyIsOnline, 0);
                    UtilityMethod.showAlertBox(mContext, getString(R.string.NoInternetForSwitch));
                }
                break;
        }
    }

    @Override
    public void Messagedialog(boolean status) {
        upDateSMSSetting();
    }

    private void updateBonus() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        SaleMilkBonusPrice = Float.parseFloat(ediBonus.getText().toString());
                        sessionManager.setFloatValueSession(SessionManager.Key_SaleMilkBonusRate, Constant.SaleMilkBonusPrice);

                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Your_Bonus_is_Updated_Success));

                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("bonus", ediBonus.getText().toString().trim()).addEncoded("type", bonusType).build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(updateBonusAPI);


    }

    public void updateMilkRateTypeSetting() {
        sessionManager.setValueSession(Key_SaleRateType, rateType);
        sessionManager.setValueSession(SessionManager.Key_SaleFateType, fatType);
        sessionManager.setFloatValueSession(SessionManager.Key_SaleFatPrice, Float.parseFloat(fatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_SaleCowFatPrice, Float.parseFloat(cowFatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_SaleBuffaloFatPrice, Float.parseFloat(buffFateRate));
        if (isNetworkAvaliable(mContext)) {

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status").equals("success")) {
                            JSONObject jsonData = jsonObject.getJSONObject("data");
                            if (rateType.equals("0")) {
                                checkRateType(nullCheckFunction(jsonData.getString("entry_type")));
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID)).addEncoded("entry_type", rateType).addEncoded("milk_price_sale", fatRate).addEncoded("milk_price_sale_cow", cowFatRate).addEncoded("milk_price_sale_buffalo", buffFateRate).addEncoded("fat_type_sale", fatType).build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(saleMilkRateSetting);

        }
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

    private void saleMilk() {
        machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);
        isMachineAuto = sessionManager.getBooleanValue(KEY_MachineAuto);
        switchMachine.setChecked(isMachineAuto);
        SaleMilkBonusPrice = sessionManager.getFloatValueSession(SessionManager.Key_SaleMilkBonusRate);
        rateType = sessionManager.getValueSesion(Key_SaleRateType);
        fatType = sessionManager.getValueSesion(Key_SaleFateType);
        fatRate = String.valueOf(sessionManager.getFloatValueSession(Key_SaleFatPrice));
        cowFatRate = String.valueOf(sessionManager.getFloatValueSession(Key_SaleCowFatPrice));
        buffFateRate = String.valueOf(sessionManager.getFloatValueSession(Key_SaleBuffaloFatPrice));
        checkRateType(rateType);
        checkFatType();
        ediBonus.setText("" + SaleMilkBonusPrice);
        ediFatRate.setText(fatRate);
        ediFatRateCow.setText(cowFatRate);
        ediFatRateBuffalo.setText(buffFateRate);
        if (sessionManager.getValueSesion(Key_User_Status).length() == 0) {
            userStatus = "1";
            sessionManager.setValueSession(Key_User_Status, userStatus);
            sessionManager.setIntValueSession(Key_RemainingDay, 0);
        }
        upDateSMSSetting();
    }

    private void updateViewRateSetting(String rateType) {

        sessionManager.setValueSession(Key_SaleRateType, rateType);

        saleByFixRate = "Fix";
        if (rateType.equalsIgnoreCase("1")) {
            radioBtnSNF.setChecked(true);
            radioBtnFat.setChecked(false);
            radioBtnCLR.setChecked(false);
            layoutByFat.setVisibility(View.GONE);
        } else if (rateType.equalsIgnoreCase("2")) {
            radioBtnSNF.setChecked(false);
            radioBtnFat.setChecked(true);
            radioBtnCLR.setChecked(false);
            layoutByFat.setVisibility(View.VISIBLE);
        } else if (rateType.equalsIgnoreCase("4")) {
            radioBtnSNF.setChecked(false);
            radioBtnFat.setChecked(false);
            radioBtnCLR.setChecked(true);
            layoutByFat.setVisibility(View.GONE);
        } else {
            saleByFixRate = "FixRate";
            radioBtnSNF.setChecked(false);
            radioBtnFat.setChecked(false);
            radioBtnCLR.setChecked(false);
            layoutByFat.setVisibility(View.GONE);
        }
    }

}