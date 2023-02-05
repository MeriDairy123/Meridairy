package b2infosoft.milkapp.com.Dairy.Setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.FatSnf.SnfFatChartFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.Model.BeanDairySnfFatChart.getDairyAllSNF_FATChart;
import static b2infosoft.milkapp.com.Model.CustomerEntryListPojo.uploadEntryToServer;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.BuyMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.buyMilkRateSetting;
import static b2infosoft.milkapp.com.appglobal.Constant.updateBonusAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFloatNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

public class BuyRateFragment extends Fragment implements View.OnClickListener, milkEntryUploadListner {

    Context mContext;
    EditText ediFatRate, ediFatRateCow, ediFatRateBuffalo, ediBonus;
    Button btnUpdateBonus, btnRefreshEntry, btn_Refresh;
    String bonusType = "Buy", rateType, fatRate = "", cowFatRate = "", buffFateRate = "", fatType = "";
    ;
    Toolbar toolbar;
    TextView btnUpdateFatRate;
    KeyListener mKeyListener;
    RadioGroup rgFat;
    SessionManager sessionManager;
    BroadcastReceiver receiver;
    View view, layoutByFat, layoutCowBuff, tvGeneralFat;
    private RadioButton radioBtnFat, radioBtnSNF, radioBtnCLR, rdFatGeneral, rdFatCowBuffalo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buy_milkrate_setting, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);

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

        toolbar.setTitle(mContext.getString(R.string.Update_Rate));

        sessionManager = new SessionManager(mContext);
        toolBarManage();

        ediBonus = view.findViewById(R.id.ediBonus);
        btnUpdateFatRate = view.findViewById(R.id.btnUpdateFatRate);
        btnUpdateBonus = view.findViewById(R.id.btnUpdateBonus);
        btnRefreshEntry = view.findViewById(R.id.btnRefreshEntry);
        btn_Refresh = view.findViewById(R.id.btn_Refresh);
        radioBtnSNF = view.findViewById(R.id.radioBtnSNF);


        radioBtnFat = view.findViewById(R.id.radioBtnFat);
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


        radioBtnCLR.setText(mContext.getString(R.string.CLR) + "/" + mContext.getString(R.string.Fat));
        radioBtnFat.setOnClickListener(this);
        radioBtnSNF.setOnClickListener(this);
        radioBtnCLR.setOnClickListener(this);

        rdFatGeneral.setOnClickListener(this);
        rdFatCowBuffalo.setOnClickListener(this);
        rdFatCowBuffalo.setText(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Buff));
        ediFatRateCow.setHint(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Fat_Rat));
        ediFatRateBuffalo.setHint(mContext.getString(R.string.Buff) + "/" + mContext.getString(R.string.Fat_Rat));

        btnUpdateBonus.setOnClickListener(this);
        btnUpdateFatRate.setOnClickListener(this);
        btnRefreshEntry.setOnClickListener(this);
        btn_Refresh.setOnClickListener(this);
        initView();
        return view;
    }


    private void initView() {

        BuyMilkBonusPrice = sessionManager.getFloatValueSession(SessionManager.Key_BuyMilkBonusRate);
        ediBonus.setText("" + BuyMilkBonusPrice);
        rateType = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KeyBuyMilkRateType));
        fatType = nullCheckFunction(sessionManager.getValueSesion(SessionManager.KeyBuyFatType));
        fatRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyFatPrice));
        cowFatRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyCowFatPrice));
        buffFateRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_BuyBuffFatPrice));


        rdFatCowBuffalo.setText(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Buff));
        ediFatRateCow.setHint(mContext.getString(R.string.Cow) + "/" + mContext.getString(R.string.Fat_Rat));
        ediFatRateBuffalo.setHint(mContext.getString(R.string.Buff) + "/" + mContext.getString(R.string.Fat_Rat));
        ediFatRate.setText(fatRate);
        ediFatRateCow.setText(cowFatRate);
        ediFatRateBuffalo.setText(buffFateRate);
        checkRateType(rateType);
        checkFatType();


    }


    private void checkRateType(String rateTyp) {
        rateType = rateTyp;
        if (!rateType.equals("")) {
            updateViewRateSetting(rateType);
        } else {
            rateType = "0";
            updateMilkRateTypeSetting();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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

            case R.id.btnUpdateBonus:
                if (!ediBonus.getText().toString().trim().equals("")) {
                    updateBonus();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_Bonus_Amount));
                }
                break;
            case R.id.btnRefreshEntry:
                DatabaseHandler db = DatabaseHandler.getDbHelper(mContext);
                if (isNetworkAvaliable(mContext)) {
                    if (db.getMilkBuyEntryRecordsOffline().size() > 0) {
                        uploadEntryToServer(mContext, "setting", this);
                    }
                } else {
                    showToast(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                }
                break;
            case R.id.btn_Refresh:
                if (isNetworkAvaliable(mContext)) {
                    SnfFatChartFragment.getBonusPrice(mContext);
                    getDairyAllSNF_FATChart(mContext, "buy", true);
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.you_are_not_connected_to_internet));
                }
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


    }

    private void updateViewRateSetting(String rateType) {
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
        }
    }

    private void updateBonus() {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...Updating", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        BuyMilkBonusPrice = Float.parseFloat(ediBonus.getText().toString());
                        JSONObject jsonData = jsonObject.getJSONObject("data");

                        sessionManager.setFloatValueSession(SessionManager.Key_BuyMilkBonusRate, Constant.BuyMilkBonusPrice);
                        sessionManager.setFloatValueSession(SessionManager.Key_SaleMilkBonusRate, nullCheckFloatNumber(jsonData.getString("sale_milk_bonus")));

                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Your_Bonus_is_Updated_Success));
                    } else {
                        UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Updating_Failed));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("bonus", ediBonus.getText().toString().trim())
                .addEncoded("type", bonusType)
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(updateBonusAPI);


    }


    public void toolBarManage() {

        if (getArguments() != null) {
            toolbar.setNavigationIcon(R.drawable.back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Objects.requireNonNull(getActivity()).onBackPressed();

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

    private void updateMilkRateTypeSetting() {
        sessionManager.setValueSession(SessionManager.KeyBuyMilkRateType, rateType);
        sessionManager.setValueSession(SessionManager.KeyBuyFatType, fatType);
        sessionManager.setFloatValueSession(SessionManager.Key_BuyFatPrice, Float.parseFloat(fatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_BuyCowFatPrice, Float.parseFloat(cowFatRate));
        sessionManager.setFloatValueSession(SessionManager.Key_BuyBuffFatPrice, Float.parseFloat(buffFateRate));

        @SuppressLint("StaticFieldLeak") NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", true) {
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

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("entry_type", rateType)
                .addEncoded("milk_price", fatRate)
                .addEncoded("milk_price_cow", cowFatRate)
                .addEncoded("milk_price_buffalo", buffFateRate)
                .addEncoded("fat_type_buy", fatType)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(buyMilkRateSetting);


    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver),
                new IntentFilter(FIREBASE_REQ_ACCEPT)
        );


    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    @Override
    public void onMilkEntryUploaded(String from) {
        //getBuyThreeMonthMilkEntryList(mContext, "", true);
    }
}
