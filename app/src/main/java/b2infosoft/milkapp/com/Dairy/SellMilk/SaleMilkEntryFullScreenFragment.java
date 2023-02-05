package b2infosoft.milkapp.com.Dairy.SellMilk;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.Bhugtan.SignatureFragment;
import b2infosoft.milkapp.com.Dairy.Customer.Fragment.FragmentCustomerBuyerList;
import b2infosoft.milkapp.com.Dairy.Customer.Fragment.FragmentCustomerSellerList;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment;
import b2infosoft.milkapp.com.Dairy.SellMilk.Adapter.SaleCustomMilkEntryItemAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.RefreshSaleEntryList;
import b2infosoft.milkapp.com.Interface.milkEntryUploadListner;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.ConnectivityReceiver;
import b2infosoft.milkapp.com.useful.MyApp;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printSaleMilk_OneDayReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printSaleMilk_SingleEntryReciept;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.MilkSMSContent;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.Model.CustomerSaleMilkEntryList.uploadSaleMilkEntryToServer;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.SaleMilkBonusPrice;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.addSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.appglobal.Constant.updateSaleMilkEntryAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_AutoFatStatus;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_CowMaxFat;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleFateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SaleRateType;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_smsAlwyasOn_ASk;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.NO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.YES;
import static b2infosoft.milkapp.com.useful.UtilityMethod.PERMISSION_ALL;
import static b2infosoft.milkapp.com.useful.UtilityMethod.SMSPERMISSIONS;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentAddBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hasPermissions;
import static b2infosoft.milkapp.com.useful.UtilityMethod.hideKeyboard;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFloatNumber;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;


public class SaleMilkEntryFullScreenFragment extends Fragment
        implements RefreshSaleEntryList,
        View.OnClickListener, View.OnKeyListener, FragmentBackPressListener, milkEntryUploadListner, ConnectivityReceiver.ConnectivityReceiverListener {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    Context mContext;
    Toolbar toolbar;
    BroadcastReceiver receiver;
    AutoCompleteTextView tvAutoCustID;
    TextInputEditText ediFat, ediWeight, ediSNF, ediCLR;


    TextView toolbar_Date, toolbar_Shift, tvCustomerID,tvFathername, tv_RatePerKg, tvTotal;
    TextView tvID, tvWeight, tvRate, tvAmount, tvFat;
    Button btnSave;
    View view, layout_bottomView, layoutCLR, layoutSNF;
    String dairyId = "", userGroupId = "4", rateType = "1", fatType = "", sonOf = " / ", selectedName = "", catChartId = "",
            selectedFatherName = "", selectedPhone_number = "", strSelectedId = "", unic_customer = "",
            strEntry_type = "", strActualFate = "", strSNF = "", strTotalBonus = "", strTotalPayment = "", strWeight = "",
            strRsPerKg = "", printTypeDialog = "", inCase = "", milkRateCategory = "", pricePerLiter = "0.00", cowFatRate = "",
            buffFateRate = "", type = "", fromEdit = "", autoFatStatus = "";
    float autoSelectFateD = 5, fateD = 0, snf = 0, clr = 0, weight = 0, rsPerKg = 0, totalPrice = 0, rate = 0, entry_price = 0;
    double totalWeight = 0.0, avgFat = 0.0, totalFAT = 0, totalSNF = 0, avgSNF = 0, totalAmt = 0, bonusPrice = 0, totalPriceWithBonus = 0;
    float devideFactor = 0, multiplyFactor = 0, addFactor = 0;
    Integer entryID = 0, onLineEntryId = 0, deliveryBoyId = 0, fatSnfCategory = 0, entryStatus = 1, editPosition = 0, Totcount = 0, count = 0;
    DatabaseHandler databaseHandler;
    SessionManager sessionManager;
    Bundle bundle;
    RecyclerView recycler_entryList;
    TextView tvTotalWeight, tvAverageFat, tvAvgSNF, tvTotalAmount;
    TextView tvListTotalWeight, tvListAvgFat, tvListAvgSNF, tvListTotalAmount;
    SaleCustomMilkEntryItemAdapter milkEntryAdapter;
    ImageView imgSession, imgPrint;
    ArrayList<CustomerListPojo> CustomerList = new ArrayList<>();
    ArrayList<CustomerSaleMilkEntryList> milkEntryList = new ArrayList<>();
    CustomerListPojo customerItem;
    ProgressDialog progressDialog = null;
    Document document;
    String Pdfcreted = "false";
    String smsContent = "";
    boolean SmsAlwaysSend = true;
    LinearLayoutManager layoutManager;
    Object tag = "";
    BottomSheetDialog bottomSheetEntry;
    Fragment fragment;
    private RadioGroup radioType;
    private RadioButton radioCow, radioBuffalo;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_milk_custom_entry, container, false);
        inCase = "";
        mContext = getActivity();
        if (!hasPermissions(mContext, SMSPERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, SMSPERMISSIONS, PERMISSION_ALL);
        }
        sessionManager = new SessionManager(mContext);
        databaseHandler = DatabaseHandler.getDbHelper(mContext);
        dairyId = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        toolbar = view.findViewById(R.id.toolbar);
        imgSession = toolbar.findViewById(R.id.imgSession);
        imgPrint = toolbar.findViewById(R.id.imgPrint);

        toolbar_Date = toolbar.findViewById(R.id.toolbar_Date);
        toolbar_Date.setVisibility(View.VISIBLE);
        imgPrint.setVisibility(View.VISIBLE);
        toolbar_Shift = toolbar.findViewById(R.id.toolbar_Shift);
        toolbar_Shift.setVisibility(View.VISIBLE);
        toolbar.setTitle(mContext.getString(R.string.SALE_MILK));

        layout_bottomView = view.findViewById(R.id.layout_bottomView);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvAverageFat = view.findViewById(R.id.tvAverageFat);
        tvAvgSNF = view.findViewById(R.id.tvAvgSNF);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvAutoCustID = view.findViewById(R.id.actv_CustomerID);
        tvCustomerID = view.findViewById(R.id.tvCustomerID);
        tvFathername = view.findViewById(R.id.tvFathername);
        ediFat = view.findViewById(R.id.ediFat);
        ediWeight = view.findViewById(R.id.ediWeight);
        ediSNF = view.findViewById(R.id.ediSNF);
        ediCLR = view.findViewById(R.id.ediCLR);
        tv_RatePerKg = view.findViewById(R.id.tv_RatePerKg);
        tvTotal = view.findViewById(R.id.tv_Total);

        layoutCLR = view.findViewById(R.id.layoutCLR);
        layoutSNF = view.findViewById(R.id.layoutSNF);
        btnSave = view.findViewById(R.id.btnSave);
        radioType = view.findViewById(R.id.radioType);
        radioCow = view.findViewById(R.id.radioCow);
        radioBuffalo = view.findViewById(R.id.radioBuffalo);
        recycler_entryList = view.findViewById(R.id.recycler_entryList);

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
        layout_bottomView.setVisibility(View.VISIBLE);
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

        toolbar_Shift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelectShift();
            }
        });
        layout_bottomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBottomSheetEntryList();
            }
        });
        ediFat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ediFat.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            }
        });

        tvFathername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FromWhere = "SaleEntryCustom";
                goNextFragmentAddBackStack(mContext, FragmentCustomerBuyerList.newInstance(SaleMilkEntryFullScreenFragment.this,"SaleEntryCustom"));

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
                System.out.println("entry_price>>>" + entry_price);
                if (autoFatStatus.equals("1") && entry_price == 0) {
                    if (rateType.equals("1") || rateType.equals("4")) {
                        System.out.println("rateType>>>" + rateType);
                        System.out.println("autoSelectFateD>>>" + autoSelectFateD);
                        System.out.println("autoFatStatus>>>" + autoFatStatus);
                        if (fateD <= autoSelectFateD) {
                            radioCow.setChecked(true);
                        } else {
                            radioBuffalo.setChecked(true);
                        }
                    }
                }
                if (entry_price == 0 && rateType.equals("4") && tvAutoCustID.getText().length() > 0 && fateD > 0 && clr > 0 && weight> 0) {
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


                setTotalPrice();
                if (tv_RatePerKg.getText().toString().equals(mContext.getString(R.string.Rate_Not_Found)) || fateD == 0 || totalPrice == 0 || tvTotal.getText().toString().equals("Rs. 0")) {
                    showAlertWithButton(mContext, mContext.getString(R.string.Please_Add_Complete_Entry));
                } else {
                    if (!tvFathername.getText().toString().equals("") && !strActualFate.equals("") && !ediWeight.getText().toString().trim().equals("")) {
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
        autoFatStatus = nullCheckFunction(sessionManager.getValueSesion(Key_AutoFatStatus));
        autoSelectFateD = sessionManager.getFloatValueSession(Key_CowMaxFat);
        rateType = nullCheckFunction(sessionManager.getValueSesion(Key_SaleRateType));
        if (rateType.equals("0") || rateType.equals("")) {
            rateType = "1";
        }
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
            tvFathername.setText(bundle.getString("CustomerName") + sonOf + bundle.getString("CustomerFatherName"));
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

    public void deleteAllMilkEntry() {
        sessionManager.db.deleteMilkEntryShiftWise("sale", SelectedDate, strSession);
    }

    private void initview() {
        FromWhere = "SaleEntry";
        toolbar_Date.setText(SelectedDate);
        if (strSession.equals("evening")) {
            toolbar_Shift.setText("PM");
            imgSession.setBackgroundResource(R.drawable.evening);
        } else {
            toolbar_Shift.setText("AM");
            imgSession.setBackgroundResource(R.drawable.sun_icon);
        }


        getMilkEntryList();
        if (isNetworkAvaliable(getActivity()) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {
            onNetworkConnectionChanged(true);
            if (milkEntryList.isEmpty()) {
                getOnlineMilkEntryList();
            }
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
        if (isNetworkAvaliable(mContext) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {

            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String staus = jsonObject.getString("status");
                        if (staus.equalsIgnoreCase("success")) {
                            onLineEntryId = jsonObject.getInt("id");
                            databaseHandler.addSaleSingleMilkEntry(onLineEntryId, deliveryBoyId, sessionManager.getValueSesion(SessionManager.KEY_UserID),
                                    strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF,
                                    "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
                            ClearEditTextValue("update");
                            btnSave.setText(mContext.getString(R.string.Save));
                            getMilkEntryList();
                            showToast(mContext, mContext.getString(R.string.Entry_Added_Successfully));
                            if (sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting).equalsIgnoreCase(TWO)) {
                                getSMSBalance(mContext, GET_TASK);
                            }
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


            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("customer_id", strSelectedId)
                    .addEncoded("ask_sms", askAMS)
                    .addEncoded("entry_date", SelectedDate)
                    .addEncoded("shift", strSession)
                    .addEncoded("milk_category", milkRateCategory)
                    .addEncoded("snf_fat_categories", "" + fatSnfCategory)
                    .addEncoded("total_milk", strWeight)
                    .addEncoded("fat", strActualFate)
                    .addEncoded("snf", strSNF)
                    .addEncoded("clr", "" + clr)
                    .addEncoded("per_kg_price", "" + strRsPerKg)
                    .addEncoded("total_bonus", "" + strTotalBonus)
                    .addEncoded("total_price", strTotalPayment)
                    .build();

            serviceCaller.addRequestBody(body);

            serviceCaller.execute(addSaleMilkEntryAPI);


        } else {
            OffLineEntry();
        }
    }

    private void updateEntry() {
        entryStatus = 1;
        if (isNetworkAvaliable(mContext) && sessionManager.getIntValueSesion(SessionManager.KeyIsOnline) == 1) {
            NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Processing..", true) {
                @Override
                public void handleResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String staus = jsonObject.getString("status");
                        if (staus.equalsIgnoreCase("success")) {

                            databaseHandler.updateSaleMilkEntry(entryID, onLineEntryId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName,
                                    tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, strTotalBonus,
                                    strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
                            btnSave.setText(mContext.getString(R.string.Save));
                            ClearEditTextValue("update");
                            getMilkEntryList();
                            showToast(mContext, mContext.getString(R.string.Entry_Update_Successfully));

                        } else {
                            showToast(mContext, mContext.getString(R.string.Entry_Updating_Failed));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("dairy_id", dairyId)
                    .addEncoded("id", "" + onLineEntryId)
                    .addEncoded("customer_id", strSelectedId)
                    .addEncoded("entry_date", SelectedDate)
                    .addEncoded("shift", strSession)
                    .addEncoded("milk_category", milkRateCategory)
                    .addEncoded("snf_fat_categories", "" + fatSnfCategory)
                    .addEncoded("total_milk", strWeight)
                    .addEncoded("fat", strActualFate)
                    .addEncoded("snf", strSNF)
                    .addEncoded("clr", "" + clr)
                    .addEncoded("per_kg_price", "" + strRsPerKg)
                    .addEncoded("total_bonus", "" + strTotalBonus)
                    .addEncoded("total_price", strTotalPayment)
                    .build();
            serviceCaller.addRequestBody(body);
            serviceCaller.execute(updateSaleMilkEntryAPI);
        } else {

            entryStatus = 0;
            databaseHandler.updateSaleMilkEntry(entryID, onLineEntryId, sessionManager.getValueSesion(SessionManager.KEY_UserID), strSelectedId, selectedName,
                    tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF, "" + clr, strRsPerKg, strWeight, "" + strTotalBonus,
                    strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
            btnSave.setText(mContext.getString(R.string.Save));
            ClearEditTextValue("update");
            getMilkEntryList();
            showToast(mContext, mContext.getString(R.string.Entry_Update_Successfully));
        }
    }

    private void OffLineEntry() {

        entryStatus = 0;
        System.out.println("OffLine SaleEntry: " + "====" + strTotalBonus);
        System.out.println("Offline" + "==snf==" + snf);
        databaseHandler.addSaleSingleMilkEntry(onLineEntryId, deliveryBoyId, sessionManager.getValueSesion(SessionManager.KEY_UserID),
                strSelectedId, selectedName, tvAutoCustID.getText().toString(), SelectedDate, strSession, strActualFate, strSNF,
                "" + clr, strRsPerKg, strWeight, strTotalBonus, strTotalPayment, milkRateCategory, fatSnfCategory, entryStatus);
        ClearEditTextValue("update");
        btnSave.setText(mContext.getString(R.string.Save));
        getMilkEntryList();
    }

    private String getSNFFromCLR() {

        String snf = "0.0";
        float snfFloat = 0;
        snfFloat = (clr / devideFactor) + (fateD * multiplyFactor) + addFactor;
        System.out.println("clr==" + clr);
        System.out.println("devideFactor==" + devideFactor);
        System.out.println("multiplyFactor==" + multiplyFactor);
        System.out.println("addFactor==" + multiplyFactor);
        System.out.println("fateD==" + fateD);

        snf = String.format("%.1f", snfFloat);
        System.out.println("snf==" + snf);
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
        snf = 0;
        weight = 0;
        rsPerKg = 0;
        totalPrice = 0;
        rate = 0;
        bonusPrice = 0;
        strRsPerKg = "";
        strTotalBonus = "";
        tvAutoCustID.requestFocus();
        tvAutoCustID.setText("");
        tvFathername.setText("");
        ediWeight.setText("");
        ediFat.setText("");
        ediSNF.setText("");
        ediCLR.setText("");
        tv_RatePerKg.setText("");
        tvTotal.setText("");
        tvFathername.setEnabled(true);
        tvAutoCustID.setEnabled(true);
        entryID = 0;
        entryID = 0;
        onLineEntryId = 0;
        entryStatus = 1;
        deliveryBoyId = 0;
        unic_customer = "";
        btnSave.setText(mContext.getString(R.string.Save));
    }

    public void calculateMilkEntry(String from) {

        // By FAT SNF
        if (rateType.equals("1")) {
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

                if (rsPerKg > 0 && strEntry_type.equals("2") || strEntry_type.equals("3")) {
                    // Buy Milk Via Fix Rate
                    buyMilkFixRate();
                } else {
                    rate = getFloatValuFromInputText(pricePerLiter);

                    System.out.println("rate>>" + "" + rate);
                    if (!ediFat.getText().toString().trim().equals("") || !ediWeight.getText().toString().trim().equals("")) {
                        if (ediFat.getText().toString().equals(".") && ediFat.getText().toString().length() == 1 ||
                                ediWeight.getText().toString().equals(".") && ediWeight.getText().toString().length() == 1) {
                            tv_RatePerKg.setText("0.");
                        } else {
                            if (!ediFat.getText().toString().trim().equals("")) {
                                fateD = getFloatValuFromInputText(ediFat.getText().toString().trim());

                            }
                            System.out.println("fateD>>" + "" + fateD);
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
        totalWeight = 0.0;
        avgFat = 0.0;
        avgSNF = 0.0;
        totalFAT = 0;
        totalSNF = 0;
        totalAmt = 0;
        inCase = "";
        setDataAdapter();
        if (!milkEntryList.isEmpty()) {
            for (int i = 0; i < milkEntryList.size(); i++) {
                float totalMilk = getFloatValuFromInputText(milkEntryList.get(i).total_milk);
                totalWeight = totalWeight + totalMilk;
                totalFAT = totalFAT + getFloatValuFromInputText(milkEntryList.get(i).fat) * totalMilk;
                totalSNF = totalSNF + getFloatValuFromInputText(milkEntryList.get(i).snf);
                totalAmt = totalAmt + getFloatValuFromInputText(milkEntryList.get(i).total_price);
            }
            avgFat = totalFAT / milkEntryList.size();
        }
        if (totalSNF > 0) {
            avgSNF = totalSNF / milkEntryList.size();
        }
        tvAverageFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.1f", avgFat) + "%");
        tvAvgSNF.setText(mContext.getString(R.string.SNF) + "\n" + String.format("%.1f", avgSNF) + "%");
        tvTotalWeight.setText(mContext.getString(R.string.Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", (totalAmt)));

    }

    @Override
    public void onClickEditInAdapter(int editPos, int ID, int onlineId, String Name, String fateEdit, String Weight, String
            Rate, String Total, String customerID, String unic_customerID, String snfEdit, String strClr,
                                     String milk_category, int fatSnfCat) {
        if (bottomSheetEntry!=null){
            bottomSheetEntry.dismiss();}
        inCase = "Edit";
        entryID = ID;
        unic_customer = unic_customerID;
        tvAutoCustID.setText(unic_customerID);
        editPosition = editPos;
        onLineEntryId = onlineId;
        fatSnfCategory = fatSnfCat;
        strSelectedId = customerID;

        float weightEdit = nullCheckFloatNumber(Weight);
        fateEdit = nullCheckFunction(fateEdit);
        float clrEdit = nullCheckFloatNumber(strClr);
        snfEdit = nullCheckFunction(snfEdit);
        tvAutoCustID.setEnabled(false);
        tvFathername.setEnabled(false);
        fromEdit = "yes";
        btnSave.setText(mContext.getString(R.string.UPDATE));

        tvFathername.setText(Name);
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

    @Override
    public void refreshSaleEntryList(ArrayList<CustomerSaleMilkEntryList> entryList) {
        milkEntryList = new ArrayList<>();
        milkEntryList.addAll(entryList);
        totalFAT = 0d;
        totalAmt = 0d;
        totalWeight = 0.0;
        avgFat = 0.0;
        inCase = "";
        tvAutoCustID.setText("");
        tvFathername.setText("");
        ediWeight.setText("");
        tv_RatePerKg.setText("");
        tvTotal.setText("");
        tvFathername.setEnabled(true);
        tvAutoCustID.setEnabled(true);
        radioType.clearCheck();
        entryID = 0;
        onLineEntryId = 0;
        entryStatus = 1;
        deliveryBoyId = 0;
        unic_customer = "";
        totalWeight = 0.0;
        avgFat = 0.0;
        avgSNF = 0.0;
        totalFAT = 0;
        totalSNF = 0;
        totalAmt = 0;
        inCase = "";

        if (!milkEntryList.isEmpty()) {
            for (int i = 0; i < milkEntryList.size(); i++) {
                float totalMilk = getFloatValuFromInputText(milkEntryList.get(i).total_milk);
                totalWeight = totalWeight + totalMilk;
                totalFAT = totalFAT + getFloatValuFromInputText(milkEntryList.get(i).fat) * totalMilk;
                totalSNF = totalSNF + getFloatValuFromInputText(milkEntryList.get(i).snf);
                totalAmt = totalAmt + getFloatValuFromInputText(milkEntryList.get(i).total_price);
            }
            avgFat = totalFAT / milkEntryList.size();
        }
        if (totalSNF > 0) {
            avgSNF = totalSNF / milkEntryList.size();
        }
        tvAverageFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.1f", avgFat) + "%");
        tvAvgSNF.setText(mContext.getString(R.string.SNF) + "\n" + String.format("%.1f", avgSNF) + "%");
        tvTotalWeight.setText(mContext.getString(R.string.Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvTotalAmount.setText(mContext.getString(R.string.Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", (totalAmt)));


        if (bottomSheetEntry != null && bottomSheetEntry.isShowing()) {
            tvListAvgFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.1f", avgFat) + "%");
            tvListAvgSNF.setText(mContext.getString(R.string.SNF) + "\n" + String.format("%.1f", avgSNF) + "%");
            tvListTotalWeight.setText(mContext.getString(R.string.Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
            tvListTotalAmount.setText(mContext.getString(R.string.Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalAmt));
        }
    }

    @Override
    public void receiveAmount(int position) {

        for (int i = 0; i < CustomerList.size(); i++) {
            if (milkEntryList.get(position).unic_customer.equalsIgnoreCase(CustomerList.get(i).unic_customer)) {
                customerItem = CustomerList.get(i);
                if (bottomSheetEntry!=null){
                    bottomSheetEntry.dismiss();}
                customerItem = CustomerList.get(i);
                Bundle bundle=new Bundle();
                fragment = new PayOrReceiveAmountFragment();
                bundle.putString("FromWhere", "PaymentRegister");
                bundle.putString("CustomerId", customerItem.getId());
                bundle.putString("CustomerName", customerItem.getName());
                bundle.putString("unic_customer", customerItem.getUnic_customer());
                bundle.putString("CustomerFatherName", customerItem.getFather_name());
                bundle.putString("user_group_id", CustomerList.get(i).getUser_group_id());
                bundle.putString("type", "add");
                bundle.putString("title",  mContext.getString(R.string.recieve));
                bundle.putString("url", Constant.addRecieptVoucherAPI);
                fragment.setArguments(bundle);
                goNextFragmentWithBackStack(mContext, fragment);

                break;
            }

        }

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
                        tvFathername.setText("");
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
                                strSelectedId = mList.get(i).id;
                                unic_customer = mList.get(i).unic_customer;
                                selectedName = mList.get(i).name;
                                selectedFatherName = mList.get(i).father_name;
                                selectedPhone_number = mList.get(i).phone_number;
                                strEntry_type = mList.get(i).entry_type;
                                entry_price = getFloatValuFromInputText(mList.get(i).entry_price);
                                catChartId = mList.get(i).categorychart_id;

                                System.out.println("strSelectedId>>>>" + unic_customer + " " + strSelectedId + " " + selectedFatherName + "   strEntry_type== " + strEntry_type + "    " + selectedName);
                                tvFathername.setText(mList.get(i).name + sonOf + mList.get(i).father_name);
                                if (strEntry_type.equals("2") || strEntry_type.equals("3")) {
                                    entry_price = getFloatValuFromInputText(mList.get(i).entry_price);

                                    rsPerKg = entry_price;

                                    System.out.println("entry_price>>" + entry_price);
                                    System.out.println("rsPerKg>>" + rsPerKg);
                                    if (rsPerKg > 0) {
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
                                tvFathername.setText("");
                                // radioType.clearCheck();
                            }

                        }
                    }
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();

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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recycler_entryList.setHasFixedSize(true);
        milkEntryAdapter = new SaleCustomMilkEntryItemAdapter(mContext, milkEntryList, this);
        recycler_entryList.setLayoutManager(mLayoutManager);
        recycler_entryList.setAdapter(milkEntryAdapter);
        milkEntryAdapter.notifyDataSetChanged();
    }


    public void DialogSMS() {
        int CustomerID = Integer.parseInt(tvAutoCustID.getText().toString().trim());

        String Shift = "";

        if (strSession.equalsIgnoreCase("morning")) {
            Shift = "M";
        } else {
            Shift = "E";
        }
        smsContent = MilkSMSContent(mContext, SmsAlwaysSend, "SaleMilk", selectedPhone_number, selectedName, SelectedDate, Shift, strRsPerKg, strActualFate, snf, strWeight, strTotalBonus, strTotalPayment,0.0f);
        if (sessionManager.getValueSesion(SessionManager.Key_PrintReciept).equals(YES)) {
            printSaleMilk_SingleEntryReciept(mContext, fatSnfCategory, CustomerID, selectedName, Shift, strActualFate,
                    snf, strRsPerKg, strWeight, strTotalBonus, strTotalPayment,0.0f,"");
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


    public void DialogPrint() {
        final Dialog dialog = new Dialog(mContext);
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);

        ImageView imgClosed;
        TextView tv_downloadPDF, tv_print_reciept;

        // set the custom dialog components - text, image and button
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
                    enableBluetooth(mContext);
                    dialogBluetooth(mContext);

                }
            }
        });

        dialog.show();
    }


    public void AlertSMSSendAsk() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        //builder.setTitle(mContext.mContext.getString(R.string.Send_Message))
        builder.setMessage(mContext.getString(R.string.Are_You_Sure_Want_Send_SMS))
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        SmsAlwaysSend = false;
                        DialogSMS();
                    }
                })
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        SmsAlwaysSend = true;
                        DialogSMS();
                    }
                })


                .show();
    }

    public void AlertDuplicateEntry() {

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.Do_You_Want_duplicate_Entry))
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setPositiveButton(mContext.getString(R.string.yes), new DialogInterface.OnClickListener() {
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
        System.out.println("customerList>>>" + CustomerList.size());
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
            tvFathername.setEnabled(true);
            tvAutoCustID.setText("");
        } else {
            Objects.requireNonNull(getActivity()).onBackPressed();
        }
    }

    public void rateTye1FATSNFViewItem() {

        radioType.setVisibility(View.VISIBLE);
        tvCustomerID.setVisibility(View.GONE);
        layoutCLR.setVisibility(View.GONE);
        layoutSNF.setVisibility(View.VISIBLE);
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
            tvCustomerID.setVisibility(View.GONE);
            layoutCLR.setVisibility(View.GONE);
            layoutSNF.setVisibility(View.GONE);
        } else {
            pricePerLiter = String.valueOf(sessionManager.getFloatValueSession(SessionManager.Key_SaleFatPrice));
            tvCustomerID.setVisibility(View.VISIBLE);
            radioType.setVisibility(View.GONE);
            layoutCLR.setVisibility(View.GONE);
            layoutSNF.setVisibility(View.GONE);
        }

        ediFat.setEnabled(true);
        ediFat.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ediFat.setHint(mContext.getString(R.string.Fat));

    }

    public void rateTye3FixRateViewItem() {
        fatSnfCategory = 0;
        tvCustomerID.setVisibility(View.VISIBLE);
        radioType.setVisibility(View.GONE);
        layoutCLR.setVisibility(View.GONE);
        layoutSNF.setVisibility(View.GONE);
        // LayoutCLR.setVisibility(View.GONE);

    }

    public void rateTye4CLRViewItem() {
        radioType.setVisibility(View.VISIBLE);
        tvCustomerID.setVisibility(View.GONE);
        layoutCLR.setVisibility(View.VISIBLE);
        layoutSNF.setVisibility(View.VISIBLE);
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

        System.out.println("dairyId");
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairyId)
                .addEncoded("entry_date", SelectedDate)
                .addEncoded("shift", strSession)
                .build();
        caller.addRequestBody(body);
        caller.execute(getSaleMilkEntryAPI);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        //   System.out.println("keyCode=onKey===" + keyCode);
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
        FILE = Environment.getExternalStorageDirectory().toString()
                + "/MeriDairy/Sale Entry/" + strSession + "_" + SelectedDate + System.currentTimeMillis() + ".pdf";
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


            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("SR", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Name", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Fat/SNF", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Rate", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            //table.addCell(new Phrase("Bonus"  , new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Amount", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);


            for (int i = 0; i < pdfList.size(); i++) {

                table.addCell(new Phrase("" + (i + 1) + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).name + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).fat + "-" + pdfList.get(i).snf, new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                //  table.addCell(new Phrase(pdfList.get(i). + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

            }
            document.add(table);
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

            pTable.addCell(new Phrase("Total Weight " + String.format("%.3f", totalWeight), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            pTable.addCell(new Phrase("Avg Fat " + String.format("%.1f", avgFat), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
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
        if (document != null) {

            progressDialog.dismiss();
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
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
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
        BottomSheetDialog dialog = new BottomSheetDialog(Objects.requireNonNull(getActivity()));
        Objects.requireNonNull(dialog.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_select);
        ImageView imgClosed;
        TextView tvDialogTitle, tvBuyproduct, tvViewEvent;
        tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvBuyproduct = dialog.findViewById(R.id.tvBuyproduct);
        tvViewEvent = dialog.findViewById(R.id.tvViewEvent);
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tvDialogTitle.setText(mContext.getString(R.string.select) + " " + mContext.getString(R.string.Shift));
        tvBuyproduct.setText(mContext.getString(R.string.MORNING));
        tvViewEvent.setText(mContext.getString(R.string.EVENING));

        Drawable imgMon = mContext.getResources().getDrawable(R.drawable.sun_icon);
        Drawable imgEvg = mContext.getResources().getDrawable(R.drawable.evening);


        tvBuyproduct.setCompoundDrawablesWithIntrinsicBounds(imgMon, null, null, null);
        tvViewEvent.setCompoundDrawablesWithIntrinsicBounds(imgEvg, null, null, null);
        if (strSession.equals("evening")) {
            toolbar_Shift.setText("PM");
            imgSession.setBackgroundResource(R.drawable.evening);
        } else {
            toolbar_Shift.setText("AM");
            imgSession.setBackgroundResource(R.drawable.sun_icon);
        }

        // if button is clicked, close the custom dialog
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tvBuyproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strSession = "morning";
                dialog.dismiss();
                tvAutoCustID.setText("");
                deleteAllMilkEntry();
                initview();

            }
        });

        tvViewEvent.setOnClickListener(new View.OnClickListener() {
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

    public void dialogBottomSheetEntryList() {
        bottomSheetEntry = new BottomSheetDialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        Objects.requireNonNull(bottomSheetEntry.getWindow()).setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        bottomSheetEntry.setContentView(R.layout.dialog_bottom_sheet_view_milk_entry);
        RecyclerView recyclerView;
        Toolbar toolbar;

        ImageView imgPrint;
        toolbar = bottomSheetEntry.findViewById(R.id.toolbar);
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        recyclerView = bottomSheetEntry.findViewById(R.id.recycler_entryList);
        tvID = bottomSheetEntry.findViewById(R.id.tvID);
        tvFat = bottomSheetEntry.findViewById(R.id.tvFat);
        tvRate = bottomSheetEntry.findViewById(R.id.tvRate);
        tvWeight = bottomSheetEntry.findViewById(R.id.tvWeight);
        tvAmount = bottomSheetEntry.findViewById(R.id.tvAmount);


        tvListTotalWeight = bottomSheetEntry.findViewById(R.id.tvTotalWeight);
        tvListTotalAmount = bottomSheetEntry.findViewById(R.id.tvTotalAmount);
        tvListAvgFat = bottomSheetEntry.findViewById(R.id.tvAverageFat);
        tvListAvgSNF = bottomSheetEntry.findViewById(R.id.tvAvgSNF);
        tvID.setOnClickListener(this);
        tvFat.setOnClickListener(this);
        tvRate.setOnClickListener(this);
        tvAmount.setOnClickListener(this);
        tvWeight.setOnClickListener(this);
        imgPrint.setVisibility(View.VISIBLE);
        toolbar.setTitle(mContext.getString(R.string.View_All_Entry));
        toolbar.setNavigationIcon(mContext.getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetEntry.dismiss();
            }
        });
        imgPrint.setOnClickListener(this::onClick);

        recyclerView.setHasFixedSize(true);

        milkEntryAdapter = new SaleCustomMilkEntryItemAdapter(mContext, milkEntryList, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(milkEntryAdapter);
        tvListAvgFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.1f", avgFat) + "%");
        tvListAvgSNF.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.1f", avgSNF) + "%");
        tvListTotalWeight.setText(mContext.getString(R.string.Weight) + "\n" + String.format("%.3f", totalWeight) + " " + mContext.getString(R.string.Ltr));
        tvListTotalAmount.setText(mContext.getString(R.string.Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalAmt));

        bottomSheetEntry.show();
    }

    private void receiveCash() {

        Dialog openDialog = new Dialog(mContext);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.dialog_paynow);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnPayAmt;
        EditText edtAmtType, edtAmount;
        edtAmtType = openDialog.findViewById(R.id.edtAmtType);
        edtAmount = openDialog.findViewById(R.id.edtAmount);
        btnPayAmt = openDialog.findViewById(R.id.btnPayAmt);

        btnPayAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("patAmt====>>>" + "payAmt");
                if (!edtAmount.getText().toString().equals("") && !edtAmtType.getText().toString().equals("")) {
                    NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                        @Override
                        public void handleResponse(String response) {

                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getString("status").equals("success")) {

                                    openDialog.dismiss();
                                    fragment = new SignatureFragment();
                                    bundle = new Bundle();
                                    bundle.putString("CustomerId", customerItem.id);
                                    bundle.putString("CustomerFatherName", customerItem.father_name);
                                    bundle.putString("CustomerName", customerItem.name);
                                    bundle.putString("transactionID", mainObject.getString("id"));
                                    bundle.putString("type", "receive");
                                    bundle.putString("unic_customer", customerItem.unic_customer);
                                    bundle.putString("fromwhere", "SaleEntryCustom");
                                    fragment.setArguments(bundle);
                                    goNextFragmentReplace(mContext, fragment);
                                } else {
                                    UtilityMethod.showAlertWithButton(mContext, "Receiving Cash  Fail");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    RequestBody body = new FormEncodingBuilder()
                            .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                            .addEncoded("pay_price", edtAmount.getText().toString())
                            .addEncoded("customer_id", customerItem.id).build();
                    caller.addRequestBody(body);
                    caller.execute(Constant.receiveAmountAPI);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, "Please Do Amount Type And Amount to get amount");
                }
            }
        });
        openDialog.show();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==Activity.RESULT_OK&&requestCode==125566){
            if (data.getStringExtra("FromWhere").equalsIgnoreCase("CustomerList")){
                unic_customer=  UtilityMethod.nullCheckFunction(data.getStringExtra("unic_customer"));
                strEntry_type=  UtilityMethod.nullCheckFunction(data.getStringExtra("entry_type"));
                entry_price= UtilityMethod.nullCheckFloatNumber(data.getStringExtra("entry_price"));
                if (tvAutoCustID!=null){
                    tvAutoCustID.setText(unic_customer);
                }
            }
        }
    }
}