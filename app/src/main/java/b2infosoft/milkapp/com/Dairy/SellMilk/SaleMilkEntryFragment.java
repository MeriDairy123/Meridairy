package b2infosoft.milkapp.com.Dairy.SellMilk;


import static android.location.Location.convert;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printSaleMilk_OneDayReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printSaleMilk_SingleEntryReciept;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.MilkSMSContent;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.Notification.MyFirebaseMsgService.FIREBASE_REQ_ACCEPT;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.SaleMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.addSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getTotalSaleMilkDataFromServer;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.appglobal.Constant.updateSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BonusYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_BuyerMilkWeekStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_FatYES;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.SMSPERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentAddBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hideKeyboard;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFloatNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass;
import b2infosoft.milkapp.com.BluetoothPrinter.ConnectedThread;
import b2infosoft.milkapp.com.Dairy.Customer.Fragment.FragmentCustomerBuyerList;
import b2infosoft.milkapp.com.Dairy.PurchaseMilk.PurchaseMilkEntryFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Adapter.SaleMilkEntryItemAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.BluetoothReceiveListener;
import b2infosoft.milkapp.com.Interface.DividerItemDecoration;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.RefreshSaleEntryList;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.sharedPreference.SharedPrefData;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.MyApp;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;


public class SaleMilkEntryFragment extends Fragment implements RefreshSaleEntryList, View.OnClickListener, View.OnKeyListener, FragmentBackPressListener, milkEntryUploadListner, ConnectivityReceiver.ConnectivityReceiverListener, BluetoothReceiveListener {
    public static SaleMilkEntryFragment newInstance(Fragment targetFragment, String from) {

        Bundle args = new Bundle();
        args.putString("from", from);

        SaleMilkEntryFragment fragment = new SaleMilkEntryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Toolbar toolbar;
    String selectQuery;
    AutoCompleteTextView tvAutoCustID;
    EditText ediFat, ediWeight, ediSNF, ediCLR;
    Button btnSave;
    TextView toolbar_Date, tvCnamefathername, tv_RatePerKg, tvTotal;
    RecyclerView recycler_entryList;
    TextView tvTotalWeight, tvAverageFat, tvTotalAmount;
    SaleMilkEntryItemAdapter milkEntryAdapter;
    ImageView imgSession, imgPrint;
    LinearLayout layoutFatRow;
    BroadcastReceiver receiver;
    View view, layout_bottomView;
    String dairyId = "", userGroupId = "4", rateType = "", fatType = "", sonOf = " / ", selectedName = "", catChartId = "", selectedFatherName = "", selectedPhone_number = "", strSelectedId = "", unic_customer = "", strEntry_type = "", strActualFate = "", strSNF = "0", strTotalBonus = "", strTotalPayment = "", strWeight = "", strRsPerKg = "", printTypeDialog = "", inCase = "", milkRateCategory = "", pricePerLiter = "0.00", cowFatRate = "", buffFateRate = "", type = "", fromEdit = "";
    float fateD = 0, snf = 0, clr = 0, weight = 0, rsPerKg = 0, entry_price = 0, totalPrice = 0, rate = 0;
    double bonusPrice = 0, totalPriceWithBonus = 0;
    float devideFactor = 0, multiplyFactor = 0, addFactor = 0;
    Integer entryID = 0, onLineEntryId = 0, deliveryBoyId = 0, fatSnfCategory = 0, entryStatus = 1, editPosition = 0, Totcount = 0, count = 0;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    Bundle bundle;
    ArrayList<CustomerListPojo> CustomerList = new ArrayList<>();
    ArrayList<CustomerSaleMilkEntryList> milkEntryList = new ArrayList<>();
    ProgressDialog progressDialog = null;
    Document document;
    String Pdfcreted = "false";
    TextView tvID, tvName, tvWeight, tvRate, tvAmount, tvFat;
    String smsContent = "", machineCode;

    boolean SmsAlwaysSend = true, isMachineEnable = false;
    LinearLayoutManager layoutManager;
    BluetoothAdapter badapter;
    Object tag = "";
    ProgressBar progressBar;
    SharedPrefData data;
    Fragment fragment;
    private RadioGroup radioType;
    private RadioButton radioCow, radioBuffalo;
    ConnectedThread connectedThread;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_milk_entry, container, false);
        inCase = "";
        mContext = getActivity();
        if (!hasPermissions(mContext, SMSPERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, SMSPERMISSIONS, PERMISSION_ALL);
        }
        data = new SharedPrefData();
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        isMachineEnable = sessionManager.getBooleanValue(SessionManager.KEY_MachineAuto);
        toolbar = view.findViewById(R.id.toolbar);
        imgSession = toolbar.findViewById(R.id.imgSession);
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        toolbar_Date = toolbar.findViewById(R.id.toolbar_Date);
        imgSession.setVisibility(View.VISIBLE);
        toolbar_Date.setVisibility(View.VISIBLE);
        imgPrint.setVisibility(View.VISIBLE);
        toolbar.setTitle(mContext.getString(R.string.SALE_MILK));

        layout_bottomView = view.findViewById(R.id.layout_bottomView);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvAverageFat = view.findViewById(R.id.tvAverageFat);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvAutoCustID = view.findViewById(R.id.actv_CustomerID);

        tvCnamefathername = view.findViewById(R.id.tvFathername);
        ediFat = view.findViewById(R.id.ediFat);
        ediWeight = view.findViewById(R.id.ediWeight);
        ediSNF = view.findViewById(R.id.ediSNF);
        ediCLR = view.findViewById(R.id.ediCLR);
        tv_RatePerKg = view.findViewById(R.id.tv_RatePerKg);
        tvTotal = view.findViewById(R.id.tv_Total);


        layoutFatRow = view.findViewById(R.id.layoutFatRow);
        btnSave = view.findViewById(R.id.btnSave);
        radioType = view.findViewById(R.id.radioType);
        radioCow = view.findViewById(R.id.radioCow);
        radioBuffalo = view.findViewById(R.id.radioBuffalo);
        progressBar = view.findViewById(R.id.progressBar);
        recycler_entryList = view.findViewById(R.id.recycler_entryList);
        tvFat = view.findViewById(R.id.tvFat);
        tvID = view.findViewById(R.id.tvID);
        tvName = view.findViewById(R.id.tvName);
        tvRate = view.findViewById(R.id.tvRate);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvAmount = view.findViewById(R.id.tvAmount);

        tvID.setOnClickListener(this);
        tvName.setOnClickListener(this);
        tvRate.setOnClickListener(this);
        tvAmount.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        imgPrint.setOnClickListener(this);

        tvAutoCustID.setOnKeyListener(this);
        ediWeight.setOnKeyListener(this);
        ediFat.setOnKeyListener(this);
        ediSNF.setOnKeyListener(this);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        radioCow.setSelected(true);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        PurchaseMilkEntryFragment.saveGreetingMessage(mContext, sessionManager, data);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentBackPressListener();
            }
        });


        toolbar_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(mContext, toolbar_Date);
            }
        });

        imgSession.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialogSelectShift();
            }
        });
        ediFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ediFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }
        });


        tvCnamefathername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FromWhere = "SaleEntry";
                goNextFragmentAddBackStack(mContext, FragmentCustomerBuyerList.newInstance(SaleMilkEntryFragment.this, "SaleEntry"));

//                goNextFragmentReplace(mContext, new FragmentCustomerBuyerList());

            }
        });
        ediWeight.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable ediWeigh) {
                weight = 0;
                strWeight = "0";
                if (ediWeigh.length() > 0) {

                    strWeight = ediWeight.getText().toString().trim();
                    weight = getFloatValuFromInputText(strWeight);
                }
                calculateMilkEntry("weight");

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        ediFat.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edifat) {
                fateD = 0;
                strActualFate = "0";
                if (edifat.length() > 0) {
                    strActualFate = ediFat.getText().toString().trim();
                    fateD = getFloatValuFromInputText(strActualFate);
                }
                if (strEntry_type.length() == 0 && rateType.equals("4") && tvAutoCustID.getText().length() > 0 && fateD > 0 && clr > 0 && weight > 0) {

                    String strSnf = getSNFFromCLR();
                    strSnf = strSnf.replaceAll("[^\\d.]", "");
                    ediSNF.setText(strSnf);
                }

                calculateMilkEntry("fat");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        ediCLR.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                clr = 0;
                if (s.length() > 0) {
                    clr = getFloatValuFromInputText(s.toString());
                } else {
                    clr = 0;
                }
                if (tvAutoCustID.getText().length() > 0 && ediFat.getText().length() > 0 && ediWeight.getText().length() > 0) {

                    fateD = getFloatValuFromInputText(ediFat.getText().toString().trim());
                    String strSnf = getSNFFromCLR();
                    strSnf = strSnf.replaceAll("[^\\d.]", "");
                    ediSNF.setText(strSnf);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        ediSNF.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable e) {
                snf = 0;
                strSNF = "0.0";
                if (e.length() > 0) {
                    strSNF = ediSNF.getText().toString().trim();
                    strSNF = strSNF.replaceAll("[^\\d.]", "");
                    snf = getFloatValuFromInputText(strSNF);
                }
                calculateMilkEntry("snf");
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.radioCow:
                                fatSnfCategory = 2;
                                if (fatType.equals("1")) {
                                    pricePerLiter = cowFatRate;
                                }
                                break;
                            case R.id.radioBuffalo:
                                fatSnfCategory = 1;
                                if (fatType.equals("1")) {
                                    pricePerLiter = buffFateRate;
                                }
                                break;
                        }


                        calculateMilkEntry("radio");
                    }
                });
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type = "sss";
                if (strEntry_type.equalsIgnoreCase("3")) {
                    strActualFate = "10.0";
                    fateD = 10;
                }
                if (strEntry_type.equalsIgnoreCase("2") || strEntry_type.equalsIgnoreCase("3")) {
                    milkRateCategory = strEntry_type;
                } else {
                    milkRateCategory = rateType;
                }

                System.out.println("et_Cname_fathername>>>" + tvCnamefathername);
                System.out.println("strSelectedCustomerId>>>" + strSelectedId);
                System.out.println("strEntry_type>>>" + strEntry_type);
                System.out.println("rateType>>>" + milkRateCategory);
                System.out.println("fatSnfCategory>>>" + fatSnfCategory);
                System.out.println("strWeight>>>" + strWeight);
                System.out.println("strFat>>>" + strActualFate);
                System.out.println("snf>>>" + snf);
                System.out.println("clr>>>" + clr);
                System.out.println("strRsPerKg>>>" + strRsPerKg);
                System.out.println("totalBonus>>>" + strTotalBonus);
                System.out.println("strTotalPayment>>>" + strTotalPayment);
                setTotalPrice();
                if (tv_RatePerKg.getText().toString().equals(mContext.getString(R.string.Rate_Not_Found)) || fateD == 0 || totalPrice == 0 || tvTotal.getText().toString().equals("Rs. 0")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Add_Complete_Entry));
                } else {
                    if (!tvCnamefathername.getText().toString().equals("") && !strActualFate.equals("") && !ediWeight.getText().toString().trim().equals("")) {
                        if (entryID == 0) {
                            if (rsPerKg == 0) {
                                showAlertWithButton(mContext, mContext.getString(R.string.Rate_Not_Found));
                            } else {
                                boolean duplicateEntry = false;
                                if (!milkEntryList.isEmpty()) {
                                    duplicateEntry = false;
                                    for (int i = 0; i < milkEntryList.size(); i++) {
                                        if (milkEntryList.get(i).customer_id.equalsIgnoreCase(strSelectedId)) {
                                            duplicateEntry = true;
                                            break;
                                        }
                                    }

                                }

                                if (duplicateEntry) {
                                    AlertDuplicateEntry();
                                } else {
                                    saveMilkEntry();
                                }
                            }
                        } else {
                            if (rsPerKg == 0) {
                                showAlertWithButton(mContext, mContext.getString(R.string.Rate_Not_Found));
                            } else {
                                updateEntry();
                                btnSave.setText(mContext.getString(R.string.Save));
                                UtilityMethod.hideKeyboardForFocusedView((Activity) mContext);
                            }
                        }

                    } else {
                        showAlertWithButton(mContext, mContext.getString(R.string.Please_Add_Complete_Entry));
                    }
                }
            }
        });
        getCustomerList();
        devideFactor = nullCheckFloatNumber(sessionManager.getValueSesion(SessionManager.Key_DevideFac));
        multiplyFactor = nullCheckFloatNumber(sessionManager.getValueSesion(SessionManager.Key_MultiFac));
        addFactor = nullCheckFloatNumber(sessionManager.getValueSesion(SessionManager.Key_AddFac));

        rateType = sessionManager.getValueSesion(Key_SaleRateType);
        fatType = sessionManager.getValueSesion(Key_SaleFateType);
        cowFatRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleCowFatPrice));
        buffFateRate = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleBuffaloFatPrice));
        if (fatType.equals("1")) {
            pricePerLiter = cowFatRate;
            radioCow.setSelected(true);
        } else {
            pricePerLiter = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleFatPrice));

        }

        fatSnfCategory = 2;
        bundle = getArguments();
        if (!rateType.equals("")) {
            System.out.println("RateType==>>>>>" + "" + rateType);
            if (rateType.equals("2")) {
                radioType.clearCheck();
                fatSnfCategory = 0;
                if (fatType.equals("1")) {
                    radioCow.setChecked(true);
                }
                rateTye2FatOnlyViewItem();
                if (pricePerLiter.length() == 0 || pricePerLiter.equals("0.00")) {
                    movetoRateUpdate(mContext.getString(R.string.Please_Enter_Milk_Rate));
                }
            } else if (rateType.equals("1")) {
                rateTye1FATSNFViewItem();
                ediSNF.setEnabled(true);
            } else if (rateType.equals("4")) {
                rateTye4CLRViewItem();
                ediSNF.setEnabled(false);
                if (devideFactor <= 0 || multiplyFactor <= 0 || addFactor <= 0) {
                    moveCLRSetting(mContext.getString(R.string.UPDATE) + " " + mContext.getString(R.string.clrSetting));
                }
            }
        } else {
            movetoRateUpdate(mContext.getString(R.string.Please_Update_Milk_Rate_Type));
        }
        if (bundle != null && bundle.getString("FromWhere").equals("CustomerList")) {
            strSelectedId = bundle.getString("CustomerId");
            selectedName = bundle.getString("CustomerName");
            selectedFatherName = bundle.getString("CustomerFatherName");
            catChartId = bundle.getString("category_chart_id");
            unic_customer = bundle.getString("unic_customer");
            strEntry_type = bundle.getString("entry_type");
            entry_price = nullCheckFloatNumber(bundle.getString("entry_price"));

            System.out.println("strSelectedId==>>>>" + strSelectedId + " Fname>>>> " + selectedFatherName + " Name " + selectedName);
            tvAutoCustID.setText(bundle.getString("unic_customer"));
            tvCnamefathername.setText(bundle.getString("CustomerName") + sonOf + bundle.getString("CustomerFatherName"));
            manageEntryTypeByFixRate();
        }
        initview();
        MyApp.getInstance().setConnectivityListener(this);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getMilkEntryList();
            }
        };
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isMachineEnable) {
            milkAnalyzer();
        }
    }

    public void deleteAllMilkEntry() {
        sessionManager.db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
    }

    private void initview() {
        getMilkEntryList();
        FromWhere = "SaleEntry";
        toolbar_Date.setText(SelectedDate);
        imgSession = toolbar.findViewById(R.id.imgSession);
        if (strSession.equals("evening")) {
            imgSession.setBackgroundResource(R.drawable.evening);
        } else {
            imgSession.setBackgroundResource(R.drawable.sun_icon);
        }


        if (isNetworkAvaliable(getActivity()) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {
            onNetworkConnectionChanged(true);
            if (milkEntryList.isEmpty()) {
                getOnlineMilkEntryList();
            }
        }


    }

    public void milkAnalyzer() {
        machineCode = sessionManager.getValueSesion(SessionManager.KEY_MachineCode);
        if (TextUtils.isEmpty(machineCode)) {
            showToast(mContext, mContext.getResources().getString(R.string.machine_set_up));
            BluetoothClass.dialogMachineSetUp(mContext);
            getActivity().onBackPressed();
        }

        printLog("init  milkAnalyzer", "automation");
        printLog("   machineCode", machineCode);
        printLog("milkAnalyzer ", isBluetoothHeadsetConnected() + "");
        if (mDevice == null || mSocket == null) {
            dialogBluetooth(mContext);
        } else {
            connectedThread = new ConnectedThread(mContext, mSocket, this);
            // connectedThread.sendCommand(machineCode);
            isBluetoothHeadsetConnected();
        }


    }

    private void manageEntryTypeByFixRate() {
        if (strEntry_type.equals("2") || strEntry_type.equals("3")) {
            rsPerKg = entry_price;

            if (rsPerKg == 0.0) {
                if (!rateType.equals("")) {
                    System.out.println("RateType>>>>>" + rateType);
                    if (rateType.equals("2")) {
                        rateTye2FatOnlyViewItem();
                        if (pricePerLiter.length() == 0 || pricePerLiter.startsWith("0")) {
                            movetoRateUpdate(mContext.getString(R.string.Please_Update_Milk_Rate_Type));
                        }
                    } else if (rateType.equals("1")) {
                        rateTye1FATSNFViewItem();
                    } else if (rateType.equals("4")) {
                        rateTye4CLRViewItem();
                        if (devideFactor <= 0 || multiplyFactor <= 0 || addFactor <= 0) {
                            moveCLRSetting(mContext.getString(R.string.UPDATE) + " " + mContext.getString(R.string.clrSetting));
                        }
                    }
                } else {
                    movetoRateUpdate(mContext.getString(R.string.Please_Update_Milk_Rate_Type));
                }

            } else {
                ediFat.setText("");
                if (strEntry_type.equals("2")) {
                    ediFat.setHint(mContext.getString(R.string.Fat));
                    ediFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    ediFat.setEnabled(true);
                } else {
                    ediFat.setHint(mContext.getString(R.string.Fixed_Price));
                    ediFat.setEnabled(false);
                    tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + " /" + mContext.getString(R.string.Ltr));

                }
                radioType.clearCheck();
                fatSnfCategory = 0;
                rateTye3FixRateViewItem();
            }

        } else {
            ediFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }
    }

    private void moveCLRSetting(String string) {
        UtilityMethod.showAlertBox(mContext, string);
       /* Bundle bundle = new Bundle();
        bundle.putString("from", "SaleMilk");
        fragment = new CLRSettingFragment();
        fragment.setArguments(bundle);
        goNextFragmentWithBackStack(mContext, fragment);*/
    }

    private void saveMilkEntry() {
        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            isBluetoothHeadsetConnected();
            if (mDevice == null || mSocket == null) {
                dialogBluetooth(mContext);
            } else {
                if (sessionManager.getValueSesion(Key_smsAlwyasOn_ASk).equalsIgnoreCase(NO)) {
                    AlertSMSSendAsk();
                } else {
                    SmsAlwaysSend = true;
                    DialogSMS();
                }
            }
        } else {
            if (sessionManager.getValueSesion(Key_smsAlwyasOn_ASk).equalsIgnoreCase(NO)) {
                AlertSMSSendAsk();
            } else {
                SmsAlwaysSend = true;
                DialogSMS();
            }
        }

    }

    private void addEntry() {
        entryStatus = 1;
        if (isNetworkAvaliable(mContext)) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String staus = jsonObject.getString("status");
                        if (staus.equalsIgnoreCase("success")) {
                            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(TWO)) {
                                getSMSBalance(mContext, GET_TASK);
                            }
                            btnSave.setText(mContext.getString(R.string.Save));
                            showToast(mContext, mContext.getString(R.string.Entry_Added_Successfully));
                            onLineEntryId = jsonObject.getInt("id");
                            databaseHandler.addSaleSingleMilkEntry(onLineEntryId, deliveryBoyId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);

                            getMilkEntryList();
                            // When Add Entry Successful
                            ClearEditTextValue("update");
                        } else {
                            showToast(mContext, mContext.getString(R.string.Entry_Adding_Failed));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            String askAMS = "0";

            if (SmsAlwaysSend) {
                askAMS = "1";
            }


            RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", dairyId).addEncoded("customer_id", strSelectedId).addEncoded("ask_sms", askAMS).addEncoded("entry_date", SelectedDate).addEncoded("shift", strSession).addEncoded("milk_category", milkRateCategory).addEncoded("snf_fat_categories", "" + fatSnfCategory).addEncoded("total_milk", strWeight).addEncoded("fat", strActualFate).addEncoded("snf", strSNF).addEncoded("clr", "" + clr).addEncoded("per_kg_price", "" + strRsPerKg).addEncoded("total_bonus", "" + strTotalBonus).addEncoded("total_price", strTotalPayment).build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(addSaleMilkEntryAPI);
        } else {
            OffLineEntry();
        }
    }

    private void updateEntry() {
        entryStatus = 1;
        if (isNetworkAvaliable(mContext)) {

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, mContext.getString(R.string.Update_Entry) + "...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String staus = jsonObject.getString("status");
                        if (staus.equalsIgnoreCase("success")) {
                            onLineEntryId = jsonObject.getInt("id");
                            databaseHandler.updateSaleMilkEntry(entryID, onLineEntryId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
                            btnSave.setText(mContext.getString(R.string.Save));
                            ClearEditTextValue("update");
                            getMilkEntryList();
                            showToast(mContext, mContext.getString(R.string.Entry_Update_Successfully));
                            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(TWO)) {
                                getSMSBalance(mContext, GET_TASK);
                            }
                        } else {
                            showToast(mContext, mContext.getString(R.string.Entry_Updating_Failed));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            String strSNF = "0.0";

            if (ediSNF.getText().toString().length() > 0) {
                strSNF = ediSNF.getText().toString();
            }
            RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", dairyId).addEncoded("id", "" + onLineEntryId).addEncoded("customer_id", strSelectedId).addEncoded("entry_date", SelectedDate).addEncoded("shift", strSession).addEncoded("milk_category", milkRateCategory).addEncoded("snf_fat_categories", "" + fatSnfCategory).addEncoded("total_milk", strWeight).addEncoded("fat", strActualFate).addEncoded("snf", strSNF).addEncoded("clr", "" + clr).addEncoded("per_kg_price", "" + strRsPerKg).addEncoded("total_bonus", "" + strTotalBonus).addEncoded("total_price", strTotalPayment).build();
            serviceCaller.addRequestBody(body);


            serviceCaller.execute(updateSaleMilkEntryAPI);
        } else {

            entryStatus = 0;
            databaseHandler.updateSaleMilkEntry(entryID, onLineEntryId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
            btnSave.setText(mContext.getString(R.string.Save));
            ClearEditTextValue("update");
            getMilkEntryList();
            showToast(mContext, mContext.getString(R.string.Entry_Update_Successfully));

        }
    }

    private void OffLineEntry() {
        entryStatus = 0;
        databaseHandler.addSaleSingleMilkEntry(onLineEntryId, deliveryBoyId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);

        getMilkEntryList();
        ClearEditTextValue("update");
    }

    private String getSNFFromCLR() {

        String snf = "0.0";
        float snfFloat = 0;
        snfFloat = (clr / devideFactor) + (fateD * multiplyFactor) + addFactor;
        snf = String.format("%.1f", snfFloat);
        return snf;
    }

    private void ClearEditTextValue(String from) {
        if (!from.equalsIgnoreCase("update")) {
            if (milkEntryList != null && milkEntryList.size() != 0) {
                setMilkEntryList();
            }
        }
        fateD = 0;
        clr = 0;
        onLineEntryId = 0;
        snf = 0;
        weight = 0;
        rsPerKg = 0;
        totalPrice = 0;
        rate = 0;
        bonusPrice = 0;
        entryStatus = 1;
        strRsPerKg = "";
        strTotalBonus = "";
        tvAutoCustID.requestFocus();
        tvAutoCustID.setText("");
        tvCnamefathername.setText("");
        ediWeight.setText("");
        ediFat.setText("");
        ediSNF.setText("");
        ediCLR.setText("");
        tv_RatePerKg.setText("");
        tvTotal.setText("");
        tvCnamefathername.setEnabled(true);
        tvAutoCustID.setEnabled(true);
        entryID = 0;
        unic_customer = "";
        btnSave.setText(mContext.getString(R.string.Save));
        if (connectedThread != null && isMachineEnable && mDevice != null && mSocket != null) {
            connectedThread.setTareMachine();
        }
    }

    public void calculateMilkEntry(String from) {

        // By FAT SNF
        if (rateType.equals("1")) {
            rsPerKg = entry_price;
            if (rsPerKg != 0.0 && strEntry_type.equals("2") || strEntry_type.equals("3")) {
                buyMilkFixRate();
            } else {
                rsPerKg = 0;
                if (radioBuffalo.isChecked()) {
                    fatSnfCategory = 1;
                    getRateChartValueFromDatabase();
                } else if (radioCow.isChecked()) {
                    fatSnfCategory = 2;
                    getRateChartValueFromDatabase();
                }
            }
        }
        // By Milk FAT Rate
        else if (rateType.equals("2")) {
            if (!fatType.equals("1")) {
                pricePerLiter = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleFatPrice));
            }
            if (pricePerLiter.length() == 0 || pricePerLiter.equals("0.00")) {
                movetoRateUpdate(mContext.getString(R.string.Please_Enter_Milk_Rate));
            } else {
                rsPerKg = entry_price;
                if (rsPerKg != 0.0 && strEntry_type.equals("2") || strEntry_type.equals("3")) {
                    // Buy Milk Via Fix Rate
                    buyMilkFixRate();
                } else {
                    rate = getFloatValuFromInputText(pricePerLiter);
                    System.out.println("rate>>" + "" + rate);
                    if (!ediFat.getText().toString().trim().equals("") || !ediWeight.getText().toString().trim().equals("")) {
                        if (ediFat.getText().toString().equals(".") && ediFat.getText().toString().length() == 1 || ediWeight.getText().toString().equals(".") && ediWeight.getText().toString().length() == 1) {
                            tv_RatePerKg.setText("0.");
                        } else {
                            if (!ediFat.getText().toString().trim().equals("")) {
                                fateD = getFloatValuFromInputText(ediFat.getText().toString().trim());
                            }
                            rsPerKg = (rate / 100) * fateD;
                            if (!ediWeight.getText().toString().trim().equals("")) {
                                weight = getFloatValuFromInputText(ediWeight.getText().toString().trim());
                                setTotalPrice();
                                // tvTotal.setText("Rs. " + new DecimalFormat("#.##").format(totalPrice));
                                tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + "/" + mContext.getString(R.string.Ltr));
                            } else {
                                tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + "/" + mContext.getString(R.string.Ltr));
                            }
                        }
                    }
                }
            }
        } else if (rateType.equals("4")) {
            rsPerKg = 0;
            rsPerKg = entry_price;
            if (rsPerKg > 0 && strEntry_type.equals("2") || strEntry_type.equals("3")) {
                buyMilkFixRate();
            } else {
                rsPerKg = 0;
                if (radioBuffalo.isChecked()) {
                    fatSnfCategory = 1;
                    getRateChartValueFromDatabase();
                } else if (radioCow.isChecked()) {
                    fatSnfCategory = 2;
                    getRateChartValueFromDatabase();
                }
            }
        } else {

            rsPerKg = 0;
        }
    }


    private void buyMilkFixRate() {
        // Buy  Milk Via fix Rate
        fatSnfCategory = 0;
        rateTye3FixRateViewItem();
        if (strEntry_type.equals("2")) {
            rate = rsPerKg;
            System.out.println("rate>>" + "" + rate);
            if (weight > 0 && fateD > 0) {
                System.out.println("fateD>>" + "" + fateD);
                rsPerKg = (rate / 100) * fateD;
                setTotalPrice();
                tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + "/" + mContext.getString(R.string.Ltr));
            } else {
                tv_RatePerKg.setText("0.");
            }
        } else {
            if (weight > 0) {
                setTotalPrice();
                tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + "/" + mContext.getString(R.string.Ltr));
            } else {
                tv_RatePerKg.setText("0.");
            }
        }

    }

    private void getRateChartValueFromDatabase() {

        if (!ediSNF.getText().toString().trim().equals("") && !ediFat.getText().toString().trim().equals("") && !ediWeight.getText().toString().trim().equals("")) {
            weight = getFloatValuFromInputText(ediWeight.getText().toString().trim());
            fateD = getFloatValuFromInputText(ediFat.getText().toString().trim());
            snf = getFloatValuFromInputText(ediSNF.getText().toString().trim());
            rsPerKg = databaseHandler.getSaleMilkSNFRate(catChartId, strActualFate, strSNF, String.valueOf(fatSnfCategory));
            System.out.println("rsPerKg===>>>" + rsPerKg);

            if (rsPerKg == 0) {
                tv_RatePerKg.setText(mContext.getString(R.string.Rate_Not_Found));
                tvTotal.setText("");
                totalPrice = 0;
                rsPerKg = 0;
            } else {
                tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + rsPerKg + " /" + mContext.getString(R.string.Ltr));

                weight = getFloatValuFromInputText(ediWeight.getText().toString().trim());

                setTotalPrice();

            }
        }
    }

    private void setTotalPrice() {
        System.out.println("FatRatePrice>>" + rate);
        totalPrice = weight * rsPerKg;
        bonusPrice = (weight * SaleMilkBonusPrice);
        System.out.println(" bonusPrice:= " + bonusPrice);

        totalPriceWithBonus = totalPrice + bonusPrice;

        strWeight = String.format("%.3f", weight);
        strTotalBonus = String.format("%.2f", bonusPrice);
        strRsPerKg = String.format("%.2f", rsPerKg);
        strTotalPayment = String.format("%.2f", totalPriceWithBonus);
        tvTotal.setText(mContext.getString(R.string.Rs) + " " + strTotalPayment);
    }

    public void setMilkEntryList() {


        recycler_entryList.setVisibility(View.VISIBLE);
        recycler_entryList.setHasFixedSize(true);

        bottomLayoutView();
        milkEntryAdapter = new SaleMilkEntryItemAdapter(mContext, milkEntryList, this);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycler_entryList.setLayoutManager(layoutManager);
        recycler_entryList.setAdapter(milkEntryAdapter);
        milkEntryAdapter.notifyDataSetChanged();
        double totalWeight = 0.0, avgFat = 0.0, Totfat = 0, totalAmt = 0;
        for (int i = 0; i < milkEntryList.size(); i++) {
            float totalMilk = getFloatValuFromInputText(milkEntryList.get(i).total_milk);
            totalWeight = totalWeight + totalMilk;
            Totfat = Totfat + getFloatValuFromInputText(milkEntryList.get(i).fat) * totalMilk;
            totalAmt = totalAmt + getFloatValuFromInputText(milkEntryList.get(i).total_price);

        }
        avgFat = Totfat / totalWeight;
        tvAverageFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.2f", avgFat) + "%");
        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", (totalAmt)));
        recycler_entryList.addItemDecoration(new DividerItemDecoration(mContext.getResources().getDrawable(R.drawable.divider)));
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClickEditInAdapter(int editPos, int ID, int onLineId, String Name, String fateEdit, String Weight, String Rate, String Total, String customerID, String unic_customerID, String snfEdit, String strClr, String milk_category, int fatSnfCat) {
        inCase = "Edit";
        entryID = ID;

        System.out.println("entryID>>>>" + entryID);
        unic_customer = unic_customerID;
        System.out.println("Rate====edit===" + Rate);
        System.out.println("Rate=unic_customerID===" + unic_customerID);

        tvAutoCustID.setText(unic_customerID);
        editPosition = editPos;
        fatSnfCategory = fatSnfCat;
        strSelectedId = customerID;
        onLineEntryId = onLineId;

        float weightEdit = nullCheckFloatNumber(Weight);
        fateEdit = nullCheckFunction(fateEdit);
        float clrEdit = nullCheckFloatNumber(strClr);
        snfEdit = nullCheckFunction(snfEdit);
        tvAutoCustID.setEnabled(false);
        tvCnamefathername.setEnabled(false);
        fromEdit = "yes";
        btnSave.setText(mContext.getString(R.string.UPDATE));


        System.out.println("Weight====edit==" + weightEdit);
        System.out.println("fateEdit====edit==" + fateEdit);
        System.out.println("clrEdit====edit==" + clrEdit);
        System.out.println("snfEdit====edit==" + snfEdit);

        tvCnamefathername.setText(Name);
        tvTotal.setText(Total);
        if (!Rate.equals("")) {
            rsPerKg = getFloatValuFromInputText(Rate);
        }
        this.milkRateCategory = milk_category;


        if (!strEntry_type.equals("3")) {
            ediFat.setText(fateEdit + "");
        }
        if (fatSnfCat == 1) {
            fatSnfCategory = 1;
            radioBuffalo.setChecked(true);
        } else if (fatSnfCat == 2) {
            fatSnfCategory = 2;
            radioCow.setChecked(true);

        }
        ediWeight.setText(weightEdit + "");
        ediCLR.setText(clrEdit + "");
        ediSNF.setText(snfEdit + "");
        tv_RatePerKg.setText(Rate);
    }

    public void setCustomerList(final ArrayList<CustomerListPojo> mList) {
        if (mList.isEmpty()) {
            FromWhere = "SaleEntry";
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Add_Customer));
            // goNextFragmentWithBackStack(mContext, new AddCustomerFragment());

        } else {

            tvAutoCustID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable actid) {
                    System.out.println("actid====" + actid);
                    System.out.println("rateType====" + rateType);
                    fateD = 0;
                    snf = 0;
                    clr = 0;
                    weight = 0;
                    rsPerKg = 0;
                    totalPrice = 0;
                    rate = 0;
                    strEntry_type = "";
                    entry_price = 0;
                    catChartId = "";
                    strWeight = "";
                    strActualFate = "";
                    strSNF = "0";
                    strRsPerKg = "";
                    ediFat.setText("");
                    ediFat.setHint(mContext.getString(R.string.Fat));
                    ediSNF.setText("");
                    ediCLR.setText("");
                    ediWeight.setText("");
                    tv_RatePerKg.setText("");


                    if (tvAutoCustID.getText().toString().trim().length() == 0) {
                        tvCnamefathername.setText("");
                        ediFat.setText("");
                        ediFat.setHint(mContext.getString(R.string.Fat));
                        ediSNF.setText("");
                        ediCLR.setText("");
                        ediWeight.setText("");
                        tv_RatePerKg.setText("");

                    } else {
                        ediWeight.setText("");
                        ediFat.setText("");
                        ediCLR.setText("");
                        ediSNF.setText("");
                        for (int i = 0; i < mList.size(); i++) {
                            if (actid.toString().trim().equals(mList.get(i).unic_customer)) {
                                printLog("sale machine  data", "");
                                if (connectedThread != null) {
                                    connectedThread.getDataFromMachine();
                                }
                                if (connectedThread != null && isMachineEnable && mDevice != null && mSocket != null) {
                                    printLog("sale machine  data", "");
                                    connectedThread.getDataFromMachine();
                                }
                                strSelectedId = mList.get(i).id;
                                unic_customer = mList.get(i).unic_customer;
                                selectedName = mList.get(i).name;
                                selectedFatherName = mList.get(i).father_name;
                                selectedPhone_number = mList.get(i).phone_number;
                                strEntry_type = mList.get(i).entry_type;
                                entry_price = getFloatValuFromInputText(mList.get(i).entry_price);
                                catChartId = mList.get(i).categorychart_id;

                                System.out.println("strSelectedId>>>>" + unic_customer + " " + strSelectedId + " " + selectedFatherName + "   strEntry_type== " + strEntry_type + "    " + selectedName);
                                tvCnamefathername.setText(mList.get(i).name + sonOf + mList.get(i).father_name);
                                if (strEntry_type.equals("2") || strEntry_type.equals("3")) {
                                    entry_price = getFloatValuFromInputText(mList.get(i).entry_price);
                                    rsPerKg = entry_price;
                                    System.out.println("entry_price>>" + entry_price);
                                    if (rsPerKg != 0.0) {
                                        tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + new DecimalFormat("#.##").format(rsPerKg) + "/" + getString(R.string.Ltr));
                                        if (strEntry_type.equals("2")) {
                                            ediFat.setHint(mContext.getString(R.string.Fat));
                                            ediFat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                                            ediFat.setEnabled(true);
                                        } else {
                                            ediFat.setHint(mContext.getString(R.string.Fixed_Price));
                                            ediFat.setEnabled(false);
                                        }
                                        rateTye3FixRateViewItem();
                                        break;
                                    }

                                } else {
                                    if (rateType.equals("2")) {
                                        rateTye2FatOnlyViewItem();
                                        tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " 0");
                                        break;
                                    } else if (rateType.equals("4")) {
                                        rateTye4CLRViewItem();
                                        break;

                                    } else {
                                        rateTye1FATSNFViewItem();

                                    }
                                    tv_RatePerKg.setText(mContext.getString(R.string.Rs) + " " + rsPerKg + " /" + mContext.getString(R.string.Ltr));
                                }

                                break;
                            } else {
                                strSelectedId = "";
                                unic_customer = "";
                                entry_price = 0;
                                rsPerKg = 0;
                                strEntry_type = "";
                                tvCnamefathername.setText("");
                                // radioType.clearCheck();
                            }

                        }
                    }
                }
            });
        }
    }

    @Override
    public void refreshSaleEntryList(ArrayList<CustomerSaleMilkEntryList> entryList) {
        milkEntryList = new ArrayList<>();
        milkEntryList.addAll(entryList);

        double Totfat = 0d;
        double totalAmt = 0d;
        double totalWeight = 0.0, avgFat = 0.0, totAmt = 0.0;
        inCase = "";
        if (!milkEntryList.isEmpty()) {
            for (int i = 0; i < milkEntryList.size(); i++) {
                Totfat = Totfat + Double.parseDouble(milkEntryList.get(i).fat);
                totalWeight = totalWeight + Double.parseDouble(milkEntryList.get(i).total_milk);
                totalAmt = totalAmt + Double.parseDouble(milkEntryList.get(i).total_price);
            }
        }
        bottomLayoutView();
        tvAutoCustID.setText("");
        tvCnamefathername.setText("");

        ediWeight.setText("");
        tv_RatePerKg.setText("");
        tvTotal.setText("");


        tvCnamefathername.setEnabled(true);
        tvAutoCustID.setEnabled(true);
        radioType.clearCheck();

        entryID = 0;
        unic_customer = "";
        avgFat = Totfat / milkEntryList.size();
        tvAverageFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.2f", avgFat) + "%");
        tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalAmt));
    }

    @Override
    public void receiveAmount(int position) {

    }


    public void onResume() {
        super.onResume();
        mContext = getActivity();
        LocalBroadcastManager.getInstance(mContext).registerReceiver((receiver), new IntentFilter(FIREBASE_REQ_ACCEPT));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(receiver);

    }

    @Override
    public void onClick(View view) {

        // Object tag;
        int backgroundId = R.drawable.ic_arrow_drop_up;
        int backgroundId2 = R.drawable.ic_arrow_drop_down;
        switch (view.getId()) {
            case R.id.imgPrint:
                DialogPrint();
                break;
            case R.id.tvID:
                if (tag != null && tag == "down") {
                    tag = "up";
                    if (milkEntryList.size() > 0) {
                        Collections.reverse(milkEntryList);
                    }
                } else {
                    tag = "down";
                    if (milkEntryList.size() > 0) {
                        Collections.reverse(milkEntryList);
                    }
                }
                setDataAdapter();
                break;

            case R.id.tvName:
                if (tag != null && tag == "up") {
                    tag = "down";
                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                        Collections.reverse(milkEntryList);
                    }
                } else {
                    tag = "up";
                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.name.compareTo(object2.name);
                            }
                        });
                    }
                }
                setDataAdapter();

                break;

            case R.id.tvWeight:
                //tag = "";

                if (tag != null && tag == "up") {
                    tag = "down";
                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.total_milk.compareTo(object2.total_milk);

                            }
                        });
                        Collections.reverse(milkEntryList);
                    }
                    System.out.println("Up==>>" + "" + backgroundId);
                } else {
                    System.out.println("Down===>>" + "====" + backgroundId2);
                    tag = "up";

                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.total_milk.compareTo(object2.total_milk);
                            }
                        });
                    }
                }
                setDataAdapter();
                break;
            case R.id.tvRate:

                //tag = "";

                if (tag != null && tag == "up") {
                    tag = "down";

                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.per_kg_price.compareTo(object2.per_kg_price);
                            }
                        });
                        Collections.reverse(milkEntryList);
                    }
                    System.out.println("Up==>>" + "" + backgroundId);
                } else {
                    System.out.println("Down==>>" + "" + backgroundId2);
                    tag = "up";
                    //  tvRate.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);
                    //Asc
                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.per_kg_price.compareTo(object2.per_kg_price);
                            }
                        });
                    }
                }
                setDataAdapter();
                break;

            case R.id.tvAmount:
                if (tag != null && tag == "up") {
                    tag = "down";
                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.total_price.compareTo(object2.total_price);
                            }
                        });
                        Collections.reverse(milkEntryList);
                    }
                    System.out.println("Up==>>" + backgroundId);
                } else {
                    System.out.println("Down==>>" + backgroundId2);
                    tag = "up";

                    if (milkEntryList.size() > 0) {
                        Collections.sort(milkEntryList, new Comparator<CustomerSaleMilkEntryList>() {
                            @Override
                            public int compare(final CustomerSaleMilkEntryList object1, final CustomerSaleMilkEntryList object2) {
                                return object1.total_price.compareTo(object2.total_price);
                            }
                        });
                    }
                }
                setDataAdapter();

                break;
        }
    }

    public void setDataAdapter() {
        recycler_entryList.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_entryList.setHasFixedSize(true);
        bottomLayoutView();
        milkEntryAdapter = new SaleMilkEntryItemAdapter(mContext, milkEntryList, this);
        recycler_entryList.setLayoutManager(mLayoutManager);
        recycler_entryList.setAdapter(milkEntryAdapter);
        milkEntryAdapter.notifyDataSetChanged();

    }

    private void bottomLayoutView() {

        if (milkEntryList.size() > 0) {
            layout_bottomView.setVisibility(View.VISIBLE);

        } else {
            layout_bottomView.setVisibility(View.GONE);
        }
    }

    public void DialogSMS() {
        int CustomerID = Integer.parseInt(tvAutoCustID.getText().toString().trim());

        String Shift = "";
        System.out.println("strSession===" + strSession);
        System.out.println("SelectedDate===" + SelectedDate);
        if (strSession.equalsIgnoreCase("morning")) {
            Shift = "M";
        } else {
            Shift = "E";
        }

        String weekPrice = mContext.getString(R.string.Week_Total_Rs);
        SQLiteDatabase db = this.databaseHandler.getReadableDatabase();
        float totalBalanceFromLocalDb = 0;

        if (Objects.equals(SelectedDate, "")){
            SelectedDate = toolbar_Date.getText().toString();
        }

        String selectedDate = SelectedDate.substring(0, 2);
        String selectedMonth = SelectedDate.substring(3, SelectedDate.length() - 5);
        String selectedYear = SelectedDate.substring(SelectedDate.length() - 4, SelectedDate.length());

        Log.d(TAG, "onClki121123: " + db.toString() + CustomerID + "//" + strTotalPayment + "//" + selectedDate + "//" + selectedMonth + "//" + selectedYear);


        if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("10 days")) {
            totalBalanceFromLocalDb = fetchDataFromLocal(db, CustomerID, strTotalPayment, selectedDate, selectedMonth, selectedYear);

        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("15 days")) {

            totalBalanceFromLocalDb = fetchDataFromLocalFifteen(db, CustomerID, strTotalPayment, selectedDate, selectedMonth, selectedYear);
        } else if (sessionManager.getValueSesion(Key_BuyerMilkWeekStatus).equals("1 month")) {

            totalBalanceFromLocalDb = fetchDataFromLocalMonth(db, CustomerID, strTotalPayment, selectedDate, selectedMonth, selectedYear);
        } else {
            totalBalanceFromLocalDb = fetchDataFromLocal(db, CustomerID, strTotalPayment, selectedDate, selectedMonth, selectedYear);
        }


        Log.d(TAG, "DialogSMS9798798797: " + totalBalanceFromLocalDb);

        if (SharedPrefData.retriveDataFromPrefrence(mContext, String.valueOf(CustomerID) + "sale").equals("checkBoxOff")) {
            Toast.makeText(mContext, "Please Select Sms Setting", Toast.LENGTH_SHORT).show();
        } else if (SharedPrefData.retriveDataFromPrefrence(mContext, String.valueOf(CustomerID) + "sale").equals("checkBoxSms")) {
            Log.d(TAG, "DialogSMS1: " + totalBalanceFromLocalDb);
            smsContent = MilkSMSContent(mContext, SmsAlwaysSend, "SaleMilk", selectedPhone_number, selectedName, SelectedDate, Shift, strRsPerKg, strActualFate, snf, strWeight, strTotalBonus, strTotalPayment, totalBalanceFromLocalDb);
        } else if (SharedPrefData.retriveDataFromPrefrence(mContext, String.valueOf(CustomerID) + "sale").equals("checkBoxWhatsapp")) {

            String lableSmsName = mContext.getString(R.string.Name);
            String lableSmsdate = mContext.getString(R.string.Date);
            String lableSmsshift = mContext.getString(R.string.Shift);
            String lableSmsRs = mContext.getString(R.string.Rs);
            String lableSmsLtr = mContext.getString(R.string.Ltr);
            String lableSmsFat = mContext.getString(R.string.Fat);
            String lableSmsrate = mContext.getString(R.string.Rate);
            String lableSmsCLR = mContext.getString(R.string.SNF) + "/" + mContext.getString(R.string.CLR);
            String lableSmsTotalPrice = mContext.getString(R.string.Week_Total_Rs);
            String lablemessage = mContext.getString(R.string.message);
            String lableBonus = mContext.getString(R.string.Bonus);
            String lableSmsWeight = mContext.getString(R.string.Weight) + "/" + mContext.getString(R.string.Quantity);
            String YESFAT = "", YESRate = "", YESSNF = "", YESBonus = "";
            if (sessionManager.getValueSesion(Key_FatYES).matches(ONE)) {
                YESFAT = lableSmsFat + "  : " + strActualFate + "\n";
            }
            if (sessionManager.getValueSesion(SessionManager.Key_RateYES).matches(ONE)) {
                YESRate = lableSmsrate + " " + lableSmsRs + " : " + strRsPerKg + " /" + lableSmsLtr + "\n";
            }
            if (sessionManager.getValueSesion(SessionManager.Key_SNFYES).matches(ONE)) {
                YESSNF = lableSmsCLR + " : " + snf + "\n";
            }

            if (sessionManager.getValueSesion(Key_BonusYES).matches(ONE)) {
                YESBonus = lableBonus + " : " + strTotalBonus + "\n";
            }

            //test for github

            String smsContent = lableSmsName + " : " + selectedName + "\n" + lableSmsdate + " : " + SelectedDate + "\n" + lableSmsshift + " : " + Shift + "\n" + lableSmsWeight + " : " + strWeight + "\n" + YESFAT + YESSNF + YESRate + YESBonus + lableSmsRs + " : " + strTotalPayment + "\n" + lableSmsTotalPrice + " : " + totalBalanceFromLocalDb + "\n" + lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(mContext, "saveGreatingSms");

            Log.d(TAG, "DialogSMS: wefqefqef" + smsContent);

            if (SharedPrefData.retriveDataFromPrefrence(mContext, "WhatsappBusinnesORWhatsapp").equals("on")) {
                if (isPackageInstalled(mContext, "com.whatsapp.w4b")) {
                    sendDataToWhatsappWeb(selectedPhone_number, "com.whatsapp.w4b", smsContent);
                } else {
                    Toast.makeText(mContext, "Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (isPackageInstalled(mContext, "com.whatsapp")) {
                    sendDataToWhatsapp(selectedPhone_number, "com.whatsapp", smsContent);
                } else {
                    Toast.makeText(mContext, "Whatsapp Not Installed", Toast.LENGTH_SHORT).show();
                }
            }


        } else {
            Toast.makeText(mContext, "whatsapp", Toast.LENGTH_SHORT).show();
        }

        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            String lablemessage = mContext.getString(R.string.message);
            printSaleMilk_SingleEntryReciept(mContext, fatSnfCategory, CustomerID, selectedName, Shift, strActualFate, snf, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, totalBalanceFromLocalDb,lablemessage + " : " + SharedPrefData.retriveDataFromPrefrence(mContext, "saveGreatingSms"));
        }
        addEntry();
        btnSave.setText(mContext.getString(R.string.Save));
        UtilityMethod.hideKeyboardForFocusedView((Activity) mContext);
    }

    public void PrintRecieptORPdf() {
        if (milkEntryList.size() != 0) {
            if (printTypeDialog.equalsIgnoreCase("pdf")) {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }
                Totcount = milkEntryList.size();
                Pdfcreted = "true";
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Pdfcreted.equals("true")) {
                            createPDF(milkEntryList);
                        }
                    }
                }, 3000);

            } else {
                printSaleMilk_OneDayReciept(mContext, milkEntryList);

            }
        } else {
            // Toast.makeText(mContext, mContext.getString(R.string.No_Data_of_Morning_List_to_Print),Toast.LENGTH_SHORT).show();
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.You_have_no_entry_to_Print));
        }
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return !list.isEmpty();
    }


    public void DialogPrint() {
        final Dialog dialog = new Dialog(mContext);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);
        ImageView imgClosed;
        TextView tv_downloadPDF, tv_print_reciept;
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tv_downloadPDF = dialog.findViewById(R.id.tv_downloadPDF);
        tv_print_reciept = dialog.findViewById(R.id.tv_print_reciept);
        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_downloadPDF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                printTypeDialog = "pdf";
                PrintRecieptORPdf();
                dialog.dismiss();
            }
        });
        tv_print_reciept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null) {
                        dialogBluetooth(mContext);
                    }
                    printTypeDialog = "reciept";
                    PrintRecieptORPdf();
                    dialog.dismiss();
                } else {
                    showAlertWithTitle(mContext.getString(R.string.PleaseON_Bluetooth_of_device), mContext);
                    enableBluetooth();
                    dialogBluetooth(mContext);
                }
            }
        });

        dialog.show();
    }

    private void sendDataToWhatsappWeb(String cxNumber, String packageName, String message) {
        String toNumber = "91" + cxNumber; // contains spaces.
        Log.d("TAG", "sendDataToWhatsapp25: " + cxNumber);
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage(packageName);
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }


    private void sendDataToWhatsapp(String cxNumber, String packageName, String message) {
        String toNumber = "91" + cxNumber; // contains spaces.
        toNumber = toNumber.replace("+", "").replace(" ", "");
        Intent i = new Intent(Intent.ACTION_VIEW);

        try {
            String url = "https://api.whatsapp.com/send?phone=" + toNumber + "&text=" + URLEncoder.encode(message, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            i.setPackage(packageName);
            mContext.startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float fetchDataFromLocal(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear) {
        int todayDateInInt = Integer.parseInt(selectedDate);



        ArrayList totalPriceArrayList = new ArrayList<>();

        if (todayDateInInt >= 1 && todayDateInInt <= 10) {
            String timestampinStringStart =convertDate("1", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("10", selectedMonth, selectedYear);
            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else if (todayDateInInt >= 11 && todayDateInInt <= 20) {
            String timestampinStringStart = convertDate("11", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("20", selectedMonth, selectedYear);
            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else if (todayDateInInt >= 21 && todayDateInInt <= 31) {
            String timestampinStringStart = convertDate("21", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("31", selectedMonth, selectedYear);
            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }

        Cursor cursor = s.rawQuery(selectQuery, null);
        float i1 = 0.0f, totalPrice = 0.0f, oldVariable = 0.0f;
        if (cursor.moveToFirst()) {
            do {
                totalPriceArrayList.add(cursor.getString(cursor.getColumnIndex("total")));
                totalPrice = (float) convert(cursor.getString(cursor.getColumnIndex("total")));
                i1 = oldVariable + totalPrice;
                oldVariable = i1;
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "fetchDataFromLocal222: " + oldVariable);
        float a = Float.parseFloat((recentAddedPrice));
        return oldVariable + a;
    }

    public float fetchDataFromLocalFifteen(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear) {
        int todayDateInInt = Integer.parseInt(selectedDate);

        // getTotalSaleMilkDataFromServer();


        ArrayList totalPriceArrayList = new ArrayList<>();

        if (todayDateInInt >= 1 && todayDateInInt <= 15) {
            String timestampinStringStart = convertDate("1", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("15", selectedMonth, selectedYear);
            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else if (todayDateInInt >= 16 && todayDateInInt <= 31) {
            String timestampinStringStart = convertDate("16", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("31", selectedMonth, selectedYear);

            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }


        Cursor cursor = s.rawQuery(selectQuery, null);
        float i1 = 0.0f, totalPrice = 0.0f, oldVariable = 0.0f;
        if (cursor.moveToFirst()) {
            do {
                totalPriceArrayList.add(cursor.getString(cursor.getColumnIndex("total")));
                totalPrice = (float) convert(cursor.getString(cursor.getColumnIndex("total")));
                i1 = oldVariable + totalPrice;
                oldVariable = i1;

            } while (cursor.moveToNext());
        }
        Log.d("TAG", "mohitfetchDataFromLocal: " + oldVariable);
        Log.d("TAG", "fetchDataFromLocal7: " + totalPriceArrayList);

        float a = Float.parseFloat((recentAddedPrice));
        return oldVariable + a;
    }

    private void getTotalSaleMilkDataFromServer() {
        String url = getTotalSaleMilkDataFromServer;

        //    loadingPB.setVisibility(View.VISIBLE);

        // creating a new variable for our request queue

        RequestQueue queue = Volley.newRequestQueue(mContext);

        JSONArray jRootArray = new JSONArray();

//        for (int i = 0; i < mCustomerList.size(); i++) {
//            try {
//                JSONObject jsonObject1 = new JSONObject();
//                jsonObject1.put("id", mCustomerList.get(i).id);
//                Log.d("TAG", "getParams: " + mCustomerList.get(i).id);
//
//                if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxOff")) {
//                    jsonObject1.put("status", 0);
//                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxSms")) {
//                    jsonObject1.put("status", 1);
//
//                } else if (sharedPrefData.retriveDataFromPrefrence(WhatsappSetting.this, String.valueOf(mCustomerList.get(i).unic_customer)).equals("checkBoxWhatsapp")) {
//                    jsonObject1.put("status", 2);
//                } else {
//                    jsonObject1.put("status", 0);
//                }
//                jRootArray.put(jsonObject1);
//
//                Log.d("TAG", "ResponseSendToServer: " + jRootArray.toString());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }


        JsonArrayRequest request_json = new JsonArrayRequest(Request.Method.POST, url, jRootArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //   onBackPressed();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.e("Error: ", volleyError.getMessage());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                // Add headers
                return headers;
            }

            //Important part to convert response to JSON Array Again
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                String responseString;
                JSONArray array = new JSONArray();
                if (response != null) {

                    try {
                        responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                        JSONObject obj = new JSONObject(responseString);
                        (array).put(obj);
                    } catch (Exception ex) {
                    }
                }
                //return array;
                return Response.success(array, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request_json);
    }


    public float fetchDataFromLocalMonth(SQLiteDatabase s, int customerId, String recentAddedPrice, String selectedDate, String selectedMonth, String selectedYear) {

        int todayDateInInt = Integer.parseInt(selectedDate);
        ArrayList totalPriceArrayList = new ArrayList<>();

        if (todayDateInInt >= 1 && todayDateInInt <= 31) {

            String timestampinStringStart = convertDate("1", selectedMonth, selectedYear);
            String timestampinStringEnd = convertDate("31", selectedMonth, selectedYear);
            selectQuery = "select * from milk_sale_entry where str_to_time between " + timestampinStringStart + " and " + timestampinStringEnd + " and unic_customer =" + customerId;
        } else {
            Toast.makeText(mContext, "Total Data Not Found", Toast.LENGTH_SHORT).show();
        }


        Cursor cursor = s.rawQuery(selectQuery, null);
        float i1 = 0.0f, totalPrice = 0.0f, oldVariable = 0.0f;
        if (cursor.moveToFirst()) {
            do {
                totalPriceArrayList.add(cursor.getString(cursor.getColumnIndex("total")));
                totalPrice = (float) convert(cursor.getString(cursor.getColumnIndex("total")));
                i1 = oldVariable + totalPrice;
                oldVariable = i1;

            } while (cursor.moveToNext());
        }
        Log.d("TAG", "mohitfetchDataFromLocal: " + oldVariable);
        Log.d("TAG", "fetchDataFromLocal7: " + totalPriceArrayList);

        float a = Float.parseFloat((recentAddedPrice));
        return oldVariable + a;
    }


    public boolean enableBluetooth() {
        try {
            badapter = BluetoothAdapter.getDefaultAdapter();
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

    public void AlertSMSSendAsk() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS)).setCancelable(false).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                SmsAlwaysSend = false;
                DialogSMS();
            }
        }).setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                SmsAlwaysSend = true;
                DialogSMS();
            }
        }).show();
    }

    private String convertDate(String ConvesionDate, String selectedMonth, String selectedYear) {
        String formattedDate = selectedMonth + "-" + selectedYear;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMM-yyyy");
        formattedDate = df.format(c.getTime());
        String timeStrmp = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
            Date date2 = sdf.parse(ConvesionDate + "-" + selectedMonth + "-" + selectedYear);
            timeStrmp = String.valueOf(date2.getTime() / 1000);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStrmp;
    }

    public void AlertDuplicateEntry() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.Do_You_Want_duplicate_Entry)).setCancelable(false).setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        }).setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                dialog.dismiss();
                saveMilkEntry();
            }
        }).show();
    }


    public void getMilkEntryList() {


        milkEntryList = databaseHandler.getSaleMilkOneDayEntry(strSession, SelectedDate);
        if (milkEntryList.size() != 0) {
            Collections.reverse(milkEntryList);
        }
        setMilkEntryList();

    }

    public void getCustomerList() {
        CustomerList = databaseHandler.getCustomerListByGroupId(userGroupId);
        setCustomerList(CustomerList);

    }

    private void movetoRateUpdate(String string) {
        UtilityMethod.showAlertBox(mContext, string);
        Objects.requireNonNull(getActivity()).onBackPressed();
    }


    @Override
    public void OnFragmentBackPressListener() {
        hideKeyboard(((Activity) mContext));
        if (inCase.equalsIgnoreCase("Edit")) {
            inCase = "";
            tvAutoCustID.setEnabled(true);
            tvCnamefathername.setEnabled(true);
            tvAutoCustID.setText("");
        } else {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    public void rateTye1FATSNFViewItem() {
        radioType.setVisibility(View.VISIBLE);
        ediCLR.setVisibility(View.GONE);
        ediSNF.setVisibility(View.VISIBLE);
        ediFat.setEnabled(true);
        ediSNF.setEnabled(true);
        ediFat.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ediSNF.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    public void rateTye2FatOnlyViewItem() {
        if (fatType.equals("0") && inCase.equals("")) {
            fatSnfCategory = 0;
        } else if (fatType.equals("0") && inCase.equals("Edit")) {
            fatSnfCategory = 0;
        } else if (fatType.equals("1")) {
            pricePerLiter = "";
        }
        if (fatType.equals("1")) {
            if (radioCow.isChecked()) {
                pricePerLiter = cowFatRate;
            } else {
                pricePerLiter = buffFateRate;
            }
            radioType.setVisibility(View.VISIBLE);
            ediCLR.setVisibility(View.GONE);
            ediSNF.setVisibility(View.GONE);
        } else {
            pricePerLiter = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleFatPrice));
            // LayoutCLR.setVisibility(View.GONE);
            radioType.setVisibility(View.GONE);
            ediCLR.setVisibility(View.GONE);
            ediSNF.setVisibility(View.GONE);
        }

        ediFat.setEnabled(true);
        ediFat.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ediFat.setHint(mContext.getString(R.string.Fat));

    }

    public void rateTye3FixRateViewItem() {
        fatSnfCategory = 0;
        radioType.setVisibility(View.GONE);
        ediCLR.setVisibility(View.GONE);
        ediSNF.setVisibility(View.GONE);
        // LayoutCLR.setVisibility(View.GONE);

    }

    public void rateTye4CLRViewItem() {
        radioType.setVisibility(View.VISIBLE);
        ediCLR.setVisibility(View.VISIBLE);
        ediSNF.setVisibility(View.VISIBLE);
        ediFat.setEnabled(true);
        ediFat.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ediFat.setHint(mContext.getString(R.string.Fat));
        ediSNF.setEnabled(false);

    }


    public void getOnlineMilkEntryList() {
        milkEntryList = new ArrayList<>();
        @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    JSONArray mainJsonArray = new JSONArray(response);
                    if (mainJsonArray.length() > 0) {
                        databaseHandler.addSaleMilkEntryFromAPI(mainJsonArray);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getMilkEntryList();
                        }
                    }, 1000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        RequestBody body = new FormEncodingBuilder().addEncoded("dairy_id", dairyId).addEncoded("entry_date", SelectedDate).addEncoded("shift", strSession).build();
        caller.addRequestBody(body);
        caller.execute(getSaleMilkEntryAPI);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        System.out.println("keyCode=onKey===" + keyCode);
        switch (keyCode) {

            case KeyEvent.KEYCODE_BACK:

                OnFragmentBackPressListener();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:

                return true;
            case KeyEvent.KEYCODE_ENTER:
                setEditTextFocusDown(keyCode, event);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                setEditTextFocusDown(keyCode, event);
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                setEditTextFocusDown(keyCode, event);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                setEditTextFocusDown(keyCode, event);
                return true;

        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void createPDF(ArrayList<CustomerSaleMilkEntryList> pdfList) {

        String FILE = "";
        FILE = Environment.getExternalStorageDirectory().toString() + "/MeriDairy/Sale Entry/" + strSession + "_" + SelectedDate + System.currentTimeMillis() + ".pdf";
        document = new Document(PageSize.A4);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/Sale Entry/");
        myDir.mkdirs();


        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            // addMetaData(document);
            addTitlePage(document, pdfList, strSession.toUpperCase());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        document.close();

    }


    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD)));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


    private void addTitlePage(Document document, ArrayList<CustomerSaleMilkEntryList> pdfList, String session) {
        try {
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            count++;
            Pdfcreted = "true";
            Drawable d = getResources().getDrawable(R.drawable.app_icon);
            BitmapDrawable bitDw = ((BitmapDrawable) d);
            Bitmap bmp = bitDw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image image = null;
            try {
                image = Image.getInstance(stream.toByteArray());
                // image.setAlignment(Image.ALIGN_TOP);
                image.setAlignment(Image.ALIGN_CENTER);
                image.scaleToFit(40, 40);
                document.add(image);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = null;
            if (sessionManager.getValueSesion(SessionManager.KEY_center_name).equals("")) {
                //prHead.add("मेरी डेयरी");
                bitmap = UtilityMethod.textAsBitmap("मेरी डेयरी", 70, mContext);
            } else {
                // bitmap = textAsBitmap("मेरी डेयरी", 70);
                bitmap = UtilityMethod.textAsBitmap("" + sessionManager.getValueSesion(SessionManager.KEY_center_name), 70, mContext);
            }


            //  bitmap = textAsBitmap("मेरी डेयरी", 20);


            Paragraph prHead = new Paragraph();
            prHead.setSpacingAfter(3);
            prHead.setSpacingBefore(3);
            ByteArrayOutputStream streamDairy2 = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, streamDairy2);
            Image imageDairy2 = null;
            try {
                imageDairy2 = Image.getInstance(streamDairy2.toByteArray());
                // image.setAlignment(Image.ALIGN_TOP);
                imageDairy2.setAlignment(Image.ALIGN_CENTER);
                imageDairy2.scaleToFit(100, 100);
                // document.add(imageDairy2);

                prHead.add(new Chunk(imageDairy2, 0, 0, true));
                prHead.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(prHead);
            } catch (IOException e) {
                e.printStackTrace();
            }

            PdfPTable tableHead = new PdfPTable(2);
            tableHead.setWidthPercentage(100);
            tableHead.addCell(getCell("Session" + " :-" + session, PdfPCell.ALIGN_LEFT));
            tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT));
            tableHead.addCell(getCell("Date" + ":- " + SelectedDate, PdfPCell.ALIGN_LEFT));
            tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
            document.add(tableHead);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4, 4, 4});
            table.setSpacingBefore(4);
            String SR = mContext.getString(R.string.Sr);
            String Name = mContext.getString(R.string.Name);
            String FATSNF = mContext.getString(R.string.Fat_SNF);
            String Weight = mContext.getString(R.string.Weight);
            String rate = mContext.getString(R.string.Rate);
            String Amount = mContext.getString(R.string.Amount);

            /*
            String HindiFont="fonts/devanagari.ttf";

            BaseFont unicode = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font=new Font(unicode,12,Font.BOLD,new BaseColor(50,205,50));
            Typeface  Hindi = Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
            */
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("SR", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Name", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Fat/SNF", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Rate", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            //table.addCell(new Phrase("Bonus"  , new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Amount", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);

            Double Totfat = 0d;
            Double totalAmt = 0d;
            double totalWeight = 0.0, avgFat = 0.0, totAmt = 0.0;
            for (int i = 0; i < pdfList.size(); i++) {

                Totfat = Totfat + getFloatValuFromInputText(pdfList.get(i).fat) * getFloatValuFromInputText(pdfList.get(i).total_milk);
                totalWeight = totalWeight + getFloatValuFromInputText(pdfList.get(i).total_milk);
                totalAmt = totalAmt + getFloatValuFromInputText(pdfList.get(i).total_price);

                table.addCell(new Phrase("" + (i + 1) + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).name + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).fat + "-" + pdfList.get(i).snf, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                //  table.addCell(new Phrase(pdfList.get(i). + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

            }
            avgFat = Totfat / totalWeight;

            document.add(table);
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            pTable.addCell(new Phrase("Total Weight " + String.format("%.3f", totalWeight), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            pTable.addCell(new Phrase("Avg Fat " + String.format("%.2f", avgFat), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            //pTable.addCell(new Phrase("Total Bonus " + String.format("%.2f", totMorningBonus), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            pTable.addCell(new Phrase("Total Amount " + String.format("%.2f", totalAmt), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

            document.add(pTable);
            // bottom line
            Paragraph bottomHead = new Paragraph();
            bottomHead.setFont(catFont);
            bottomHead.setSpacingAfter(3);
            bottomHead.setSpacingBefore(3);
            // Add item into Paragraph
            bottomHead.add("Meri Dairy Download App Now :");
            bottomHead.setAlignment(Paragraph.ALIGN_LEFT);
            document.add(bottomHead);

            Drawable d2 = getResources().getDrawable(R.drawable.qr_code);
            BitmapDrawable bitDw2 = ((BitmapDrawable) d2);
            Bitmap bmp2 = bitDw2.getBitmap();
            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
            bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
            Image image2 = null;
            try {
                image2 = Image.getInstance(stream2.toByteArray());
                // image.setAlignment(Image.ALIGN_TOP);
                image2.setAlignment(Image.ALIGN_LEFT);
                image2.scaleToFit(120, 120);
                image2.setSpacingBefore(3);
                document.add(image2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // document.newPage();


        } catch (Exception e) {

        }
        progressDialog.dismiss();
        if (document != null) {
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.OPen_PDF));
            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/MeriDairy/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "application" + "pdf");
           /* if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                startActivity(intent);
            } else {
            }*/

        }
    }

    public void getDate(Context mContext, final TextView tvdate) {
        Calendar c2 = Calendar.getInstance();
        int mYear, mMonth, mDay;
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String formattedDate = "";
                ArrayList<String> monthList = getMonthList();

                String month = "";
                for (int i = 0; i < monthList.size(); i++) {
                    if (monthOfYear == i) {
                        month = monthList.get(i);
                    }
                }
                String day = "";
                //  month = checkDigit(monthOfYear + 1);
                day = checkDigit(dayOfMonth);

                formattedDate = day + "-" + month + "-" + year;
                tvdate.setText(formattedDate);
                System.out.println("formattedDate====>>" + formattedDate);
                Constant.SelectedDate = formattedDate;
                tvAutoCustID.setText("");
                deleteAllMilkEntry();
                initview();
            }

        }, mYear, mMonth, mDay);

        datePickerDialog2.show();

    }


    private void dialogSelectShift() {
        Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);
        ImageView imgClosed;
        TextView tvDialogTitle, tvMorning, tvEvening;
        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvMorning = dialog.findViewById(R.id.tvBuyproduct);
        tvEvening = dialog.findViewById(R.id.tvViewEvent);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvDialogTitle.setText(mContext.getString(R.string.select) + " " + mContext.getString(R.string.Shift));
        tvMorning.setText(mContext.getString(R.string.MORNING));
        tvEvening.setText(mContext.getString(R.string.EVENING));

        Drawable imgMon = mContext.getResources().getDrawable(R.drawable.sun_icon);
        Drawable imgEvg = mContext.getResources().getDrawable(R.drawable.evening);


        tvMorning.setCompoundDrawablesWithIntrinsicBounds(imgMon, null, null, null);
        tvEvening.setCompoundDrawablesWithIntrinsicBounds(imgEvg, null, null, null);


        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSession = "morning";
                dialog.dismiss();
                tvAutoCustID.setText("");
                deleteAllMilkEntry();
                initview();

            }
        });

        tvEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSession = "evening";
                dialog.dismiss();
                tvAutoCustID.setText("");
                deleteAllMilkEntry();
                initview();
            }
        });

        dialog.show();
    }

    private void setEditTextFocusDown(int keyCode, KeyEvent event) {

        if (keyCode == 19) {

            if (tvAutoCustID.hasFocus() && event.getAction() == 0) {

            } else if (ediWeight.hasFocus() && event.getAction() == 0) {
                tvAutoCustID.requestFocus();
            } else if (ediFat.hasFocus() && event.getAction() == 0) {
                ediFat.clearFocus();
                ediWeight.requestFocus();
            } else if (ediCLR.hasFocus() && event.getAction() == 0) {
                ediCLR.clearFocus();
                ediFat.requestFocus();
            } else if (ediSNF.hasFocus() && event.getAction() == 0) {
                ediSNF.clearFocus();
                ediFat.requestFocus();

            } else if (btnSave.hasFocus() && event.getAction() == 0) {
                btnSave.clearFocus();

                if (ediFat.isEnabled()) {
                    ediFat.requestFocus();
                }
                if (ediSNF.getVisibility() != View.GONE && ediSNF.isEnabled()) {
                    ediSNF.requestFocus();
                } else if (ediCLR.getVisibility() != View.GONE) {
                    ediCLR.requestFocus();
                } else {
                    System.out.println("focus ===");
                    ediWeight.requestFocus();
                }

            }
        } else {
            //Focus Down  Enter key,Arrow Down
            if (tvAutoCustID.hasFocus() && event.getAction() == 0) {
                tvAutoCustID.clearFocus();
                ediWeight.requestFocus();
            } else if (ediWeight.hasFocus() && event.getAction() == 0) {
                if (strEntry_type.equals("3")) {
                    btnSave.requestFocus();
                } else {
                    ediFat.requestFocus();
                }

            } else if (ediFat.hasFocus() && event.getAction() == 0) {
                if (ediCLR.getVisibility() != View.GONE) {
                    ediCLR.requestFocus();

                } else if (ediSNF.getVisibility() != View.GONE && ediSNF.isEnabled()) {
                    ediSNF.requestFocus();

                } else {
                    btnSave.requestFocus();
                }
            } else if (ediCLR.hasFocus() && event.getAction() == 0) {

                btnSave.requestFocus();

            } else if (ediSNF.hasFocus() && event.getAction() == 0) {
                btnSave.requestFocus();

            }
        }
    }

    @Override
    public void onMilkEntryUploaded(String from) {
        getMilkEntryList();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            uploadSaleMilkEntryToServer(mContext, "SaleEntry", this);
        }
    }

    @Override
    public void onReceiveMachineData(String message, String fat, String weight, String snf, String clr) {
        if (ediFat != null) {
            ediFat.setText(fat);
            ediWeight.setText(weight);
            ediSNF.setText(snf);
            if (ediCLR.getVisibility() == View.VISIBLE) {
                ediCLR.setText(clr);
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 125566) {
            if (data.getStringExtra("FromWhere").equalsIgnoreCase("CustomerList")) {
                unic_customer = UtilityMethod.nullCheckFunction(data.getStringExtra("unic_customer"));
                strEntry_type = UtilityMethod.nullCheckFunction(data.getStringExtra("entry_type"));
                entry_price = UtilityMethod.nullCheckFloatNumber(data.getStringExtra("entry_price"));
                if (tvAutoCustID != null) {
                    tvAutoCustID.setText(unic_customer);
                }
            }
        }
    }


}