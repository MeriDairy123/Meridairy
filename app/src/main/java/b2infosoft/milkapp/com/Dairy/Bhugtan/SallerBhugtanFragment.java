package b2infosoft.milkapp.com.Dairy.Bhugtan;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintBhugtanAllUserData;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintTenDaysReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.MilkSMSContentTendays;
import static b2infosoft.milkapp.com.MilkEntrySMS.MessageSend_Service_SIM_Web.getSMSBalance;
import static b2infosoft.milkapp.com.appglobal.Constant.BtnType;
import static b2infosoft.milkapp.com.appglobal.Constant.getTenDayMilkSellEntryAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.sendMultyMessageAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerBhugtanSetting;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.ONE;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.TWO;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfSubTitle_FontSize12;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTbHeader_FontSize10;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTbRow_FontSize8;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTitle_FontSize14;
import static b2infosoft.milkapp.com.useful.PDFUtills.addAppIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;
import static b2infosoft.milkapp.com.useful.PDFUtills.dairyNameIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.getCell;
import static b2infosoft.milkapp.com.useful.PDFUtills.getQrCodeBitmap;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openFolder;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openPdfFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import b2infosoft.milkapp.com.Dairy.Bhugtan.Adapter.SellerBhugtanListAdapter;
import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Interface.OnLoadMoreListener;
import b2infosoft.milkapp.com.Model.DismissDialog;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DownloadFileFromURL;
import b2infosoft.milkapp.com.useful.DownloadMultipleFileFromURL;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;


public class SallerBhugtanFragment extends Fragment implements View.OnClickListener, OnLoadMoreListener, DismissDialog, OnDownLoadListener {
    SellerBhugtanListAdapter milkHistoryAdapter;
    RecyclerView recycler_transactionList;
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView imgPrint;
    SessionManager sessionManager;
    String startDate = "", endDate = "";
    TextView tvtotalWeight, tvTotalAmount;
    TextView tvPrintPdfAndSend, tvPrint, tvSendMessage;
    ArrayList<TenDaysMilkSellHistory> bhugatanList = new ArrayList<>();
    ArrayList<TenDaysMilkSellHistory> mListTendayAllCustomerData = new ArrayList<>();
    ArrayList<TenDaysMilkSellHistory> mListTendayHistory = new ArrayList<>();

    Document document;
    File pdfFile;
    String filePath = "", folderName = "";
    String fileUrl;
    //ProgressBar progressBar;
    String Pdfcreted = "false";
    ProgressDialog progressDialog = null;
    ProgressBar ProgressBar01;
    int Totcount = 0, count = 0;
    DownloadFileFromURL downloadFileFromURL;
    DownloadMultipleFileFromURL downloadListener;

    TextView tvStartDate, tvEndDate;
    ImageView imgEndDate, imgStartDate;
    String formattedDate = "";
    Button btnSubmit;
    // Bluetoth Printer
    String printTypeDialog = "";
    String dairyid = "";
    View view;
    StringBuilder stringBuilder;
    String customerID = "";
    int viewAllCustomer = 0;
    private int mYear = 0, mMonth = 0, mDay = 0, mHour = 0, mMinute = 0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_seller_bhugtan, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        bhugatanList = new ArrayList<>();
        mListTendayAllCustomerData = new ArrayList<>();
        mListTendayHistory = new ArrayList<>();
        viewAllCustomer = sessionManager.getIntValueSesion(Key_SellerBhugtanSetting);
        downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
        downloadListener = new DownloadMultipleFileFromURL(this.mContext, this);
        initView();
        return view;

    }


    private void initView() {
        sessionManager = new SessionManager(mContext);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        imgPrint.setVisibility(View.VISIBLE);
        toolbar_title.setText(mContext.getString(R.string.SELLER_BHUGTAN));

        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });

        recycler_transactionList = view.findViewById(R.id.recycler_transactionList);
        tvtotalWeight = view.findViewById(R.id.tvtotalWeight);
        tvTotalAmount = view.findViewById(R.id.tvTotalAmount);
        tvPrintPdfAndSend = view.findViewById(R.id.tvPrintPdfAndSend);
        tvPrint = view.findViewById(R.id.tvPrint);
        tvSendMessage = view.findViewById(R.id.tvSendMessage);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);

        imgEndDate = view.findViewById(R.id.imgEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        ProgressBar01 = view.findViewById(R.id.ProgressBar01);


        btnSubmit = view.findViewById(R.id.btnSubmit);
        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        startDate = getSimpleDate();
        tvPrintPdfAndSend.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
        tvSendMessage.setOnClickListener(this);

        imgPrint.setOnClickListener(this);
        getTenDaysSellingHistory(mContext, startDate, endDate, "prv", "first");
    }

    public void setTransactionListAdapter() {
        double totalweight = 0, totalAmount = 0;
        for (int i = 0; i < mListTendayAllCustomerData.size(); i++) {
            if (!mListTendayAllCustomerData.get(i).total_milk.trim().equals("") && !mListTendayAllCustomerData.get(i).total_milk.trim().equals("-")) {
                totalweight = totalweight + Double.parseDouble(mListTendayAllCustomerData.get(i).total_milk);
            }

            if (!mListTendayAllCustomerData.get(i).grnd_total_amt.trim().equals("") && !mListTendayAllCustomerData.get(i).grnd_total_amt.trim().equals("-")) {
                totalAmount = totalAmount + Double.parseDouble(mListTendayAllCustomerData.get(i).grnd_total_amt);
            }

        }

        tvtotalWeight.setText(mContext.getResources().getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalweight));
        tvTotalAmount.setText(mContext.getResources().getString(R.string.Total_Amount) + "\n" + String.format("%.2f", totalAmount));
        milkHistoryAdapter = new SellerBhugtanListAdapter(mContext, mListTendayAllCustomerData, startDate, endDate, this);
        recycler_transactionList.setLayoutManager(new GridLayoutManager(mContext, 1));
        recycler_transactionList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
        recycler_transactionList.setAdapter(milkHistoryAdapter);


    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSendMessage:
                BtnType = "Send";

                if (checkIsUserSelected()) {
                    String sms = sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting);
                    if (sms.equals(ONE) || sms.equals(TWO)) {
                        if (sms.equals(ONE)) {
                            for (int i = 0; i < bhugatanList.size(); i++) {
                                if (bhugatanList.get(i).isChecked.equals("true")) {
                                    MilkSMSContentTendays(mContext, "SellerBhugtan", bhugatanList.get(i).phone_number, bhugatanList.get(i).name, bhugatanList.get(i).from_date, bhugatanList.get(i).to_date, bhugatanList.get(i).total_milk, bhugatanList.get(i).grnd_total_amt);

                                }

                            }
                        } else if (sms.equals(TWO)) {
                            sendMessage();
                        }
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.webSMSPlanExpireMessage));
                    }
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF_and_Send_Message));
                }

                break;
            case R.id.tvPrintPdfAndSend:
                BtnType = "Print-Send";
                if (checkIsUserSelected()) {
                    String sms = sessionManager.getValueSesion(SessionManager.Key_SendSmsSetting);
                    if (sms.equals(ONE) || sms.equals(TWO)) {
                        if (sms.equals(ONE)) {
                            for (int i = 0; i < bhugatanList.size(); i++) {
                                if (bhugatanList.get(i).isChecked.equals("true")) {
                                    MilkSMSContentTendays(mContext, "SellerBhugtan", bhugatanList.get(i).phone_number, bhugatanList.get(i).name, bhugatanList.get(i).from_date, bhugatanList.get(i).to_date, bhugatanList.get(i).total_milk, bhugatanList.get(i).grnd_total_amt);
                                }

                            }
                        } else if (sms.equals(TWO)) {
                            sendMessage();
                        }
                        getTenDaysData(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.webSMSPlanExpireMessage));
                    }
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF_and_Send_Message));
                }

                break;
            case R.id.tvPrint:
                BtnType = "Print";
                Log.d("TAG", "onClick5: ");
                DialogPrint("");
                break;
            case R.id.imgPrint:
                if (!mListTendayAllCustomerData.isEmpty()) {
                    DialogPrint("printImg");
                }
                break;


            case R.id.btnSubmit:
                if (!tvStartDate.getText().toString().equals("") && !tvEndDate.getText().toString().equals("")) {
                    getTenDaysSellingHistory(mContext, tvStartDate.getText().toString(), tvEndDate.getText().toString(), "nxt", "second");
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Enter_Start_Date_and_End_Date));
                }
                break;

            case R.id.imgStartDate:
                getDate("StartDate");
                break;
            case R.id.imgEndDate:
                getDate("EndDate");

                break;
            case R.id.tvStartDate:
                getDate("StartDate");

                break;
            case R.id.tvEndDate:
                getDate("EndDate");
                break;

        }
    }

    private boolean checkIsUserSelected() {
        boolean isSelected = false;
        customerID = "";
        stringBuilder = new StringBuilder();
        for (int i = 0; i < bhugatanList.size(); i++) {
            if (bhugatanList.get(i).isChecked.equals("true")) {
                isSelected = true;
                stringBuilder.append(bhugatanList.get(i).id + ",");

            }
        }
        customerID = removeLastChar(stringBuilder.toString());

        return isSelected;
    }


    public String removeLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }


    public void setTenDaysList() {
        if (BtnType.equals("Print")) {
            Log.d("0", "setTenDaysList1: ");
            createTendaysPDF(mListTendayHistory);
        } else if (BtnType.equals("Print-Send")) {
            Log.d("0", "setTenDaysList2: ");
            createTendaysPDF(mListTendayHistory);
        }
    }

    private void sendMessage() {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Sending Message, Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        showToast(mContext, mContext.getString(R.string.Message_Sent_Successfully));
                        getSMSBalance(mContext, GET_TASK);
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Sending_SMS_Fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        try {
            JSONArray jsonArray = new JSONArray();
            String in = "no";
            for (int i = 0; i < bhugatanList.size(); i++) {
                if (bhugatanList.get(i).isChecked.equals("true")) {
                    in = "yes";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
                    jsonObject.put("customer_id", bhugatanList.get(i).id);
                    jsonObject.put("phone_number", bhugatanList.get(i).phone_number);
                    jsonArray.put(jsonObject);

                }
            }

            if (in.equals("yes")) {
                JSONObject mainObject = new JSONObject();
                mainObject.put("mobile_customer_id_dairy_id", jsonArray);
                printLog("mainObject==>>>", "" + mainObject.toString());
                RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                webServiceCaller.addRequestBody(body);
                webServiceCaller.execute(sendMultyMessageAPI);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTendaysPDF(ArrayList<TenDaysMilkSellHistory> milkSellHistories) {
        if (milkSellHistories.size() > 0) {
            Totcount = milkSellHistories.size();
            count = 0;
            for (int i = 0; i < milkSellHistories.size(); i++) {
                Pdfcreted = "true";
                final int finalI = i;
                ArrayList<TenDaysMilkSellHistory> pdfList = milkSellHistories.get(finalI).tenDaysList;
                if (printTypeDialog.equalsIgnoreCase("pdf")) {
                    downloadListener = new DownloadMultipleFileFromURL(this.mContext, this);
                    folderName = "MeriDairy/Seller/";
                    filePath = milkSellHistories.get(i).getUser_data_unic_customer() + "_" + milkSellHistories.get(finalI).getUser_data_name() + System.currentTimeMillis() + ".pdf";
                    Log.d("TAG", "createTendaysPDF: " + filePath);
                    try {

                        pdfFile = createPDFFile(mContext, folderName, filePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Pdfcreted = PdfBoolean.TRUE;
                    count++;
                    try {
                        downloadListener.initURL(folderName, filePath, Totcount, count);
                        downloadListener.execute(new String[]{milkSellHistories.get(i).getPdf_url()});
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    pdfList.get(0).setFrom_date(startDate);
                    pdfList.get(0).setTo_date(endDate);
                    PrintTenDaysReciept(mContext, milkSellHistories.get(finalI).user_data_name, milkSellHistories.get(finalI).user_data_unic_customer, pdfList);
                }
            }

        /*    for (int i = 0; i < milkSellHistories.size(); i++) {

                Pdfcreted = "true";
                final int finalI = i;
                ArrayList<TenDaysMilkSellHistory> pdfList = milkSellHistories.get(finalI).tenDaysList;
                printLog("printTypeDialog=====>>>>>>" + printTypeDialog);
                if (printTypeDialog.equalsIgnoreCase("pdf")) {

                   *//*

                   if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                   new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Pdfcreted.equals("true")) {

                                //  for (int i = 0; i < tenDaysMainList.size(); i++) {
                                try {
                                    createPDF(milkSellHistories.get(finalI).user_data_name,
                                            milkSellHistories.get(finalI).user_data_unic_customer,
                                            milkSellHistories.get(finalI).user_data_phone_number, pdfList, milkSellHistories.get(finalI).avg_fat_tenDays,
                                            milkSellHistories.get(finalI).avg_rate_tenDays);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }, 3000);*//*
                } else {
                    pdfList.get(0).setFrom_date(startDate);
                    pdfList.get(0).setTo_date(endDate);
                    PrintTenDaysReciept(mContext, milkSellHistories.get(finalI).user_data_name,
                            milkSellHistories.get(finalI).user_data_unic_customer, pdfList);
                }
            }*/
        }
    }


    @Override
    public void onLoadMore(ArrayList<TenDaysMilkSellHistory> mList) {
        bhugatanList = mList;

    }


    @Override
    public void doDismis() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        openPdfFile(mContext, pdfFile);
    }


    public void DialogPrint(final String from) {
        final Dialog dialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);
        ImageView imgClosed;
        TextView tv_downloadPDF, tv_print_reciept;
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tv_downloadPDF = dialog.findViewById(R.id.tv_downloadPDF);
        tv_print_reciept = dialog.findViewById(R.id.tv_print_reciept);
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        tv_downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equalsIgnoreCase("printImg")) {
                    Log.d("TAG", "onClickif: ");

                    Totcount = 1;
                    count = 1;
                    filePath = sessionManager.getValueSesion(SessionManager.KEY_dairy_name) + "_" + System.currentTimeMillis() + ".pdf";
                    folderName = "MeriDairy/Seller/";

                    try {
                        downloadFileFromURL.initURL(folderName, filePath);
                        downloadFileFromURL.execute(fileUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                  /*  ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                createBhugtanRegisterPDF(mListTendayAllCustomerData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });*/

                } else {
                    Log.d("TAG", "onClickelse: ");
                    printTypeDialog = "pdf";
                    if (checkIsUserSelected()) {
                        getTenDaysData(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF));
                    }
                }
                dialog.dismiss();
            }
        });
        tv_print_reciept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                printLog("from", from);

                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                        dialogBluetooth(mContext);
                    } else {
                        if (from.equalsIgnoreCase("printImg")) {
                            PrintBhugtanAllUserData(mContext, mListTendayAllCustomerData);
                        } else {
                            printTypeDialog = "receipt";
                            printLog("checkIsUserSelected", "=" + checkIsUserSelected());
                            if (checkIsUserSelected()) {
                                getTenDaysData(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
                            } else {
                                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF));
                            }
                        }
                        dialog.dismiss();
                    }
                } else {
                    showAlertWithTitle("Please On Bluetooth of device", mContext);
                    enableBluetooth(mContext);
                    dialogBluetooth(mContext);
                }

            }
        });
        dialog.show();
    }


    public void createBhugtanRegisterPDF(ArrayList<TenDaysMilkSellHistory> mList) throws IOException {
        filePath = sessionManager.getValueSesion(SessionManager.KEY_dairy_name) + "_" + System.currentTimeMillis() + ".pdf";

        folderName = "MeriDairy/Seller/";
        pdfFile = createPDFFile(mContext, folderName, filePath);
        filePath = pdfFile.getAbsolutePath();
        document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile.getAbsolutePath()));
            // Open Document for Writting into document
            document.open();
            addTitlePageBhugtanAllUserPDF(document, mList);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.close();

    }

    public void addTitlePageBhugtanAllUserPDF(Document document, ArrayList<TenDaysMilkSellHistory> mList) throws DocumentException {
        // Create new Page in PDF
        count++;
        Pdfcreted = "true";

        addAppIcon(mContext, document);
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, PdfSubTitle_FontSize12, Font.BOLD);
        dairyNameIcon(mContext, sessionManager.getValueSesion(SessionManager.KEY_center_name), document);
        Image image = getQrCodeBitmap(mContext);

        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.setWidths(new float[]{9, 1,});
        tableHead.addCell(getCell("Center" + " :- " + sessionManager.getValueSesion(SessionManager.KEY_dairy_name) + "\n" + "Name" + ":- " + sessionManager.getValueSesion(SessionManager.KEY_Name) + "\n" + "Mobile No" + ":- " + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_LEFT, PdfTitle_FontSize14));
        tableHead.addCell(image);
        document.add(tableHead);

        //Date
        Paragraph prDate = new Paragraph();
        prDate.setSpacingAfter(4);
        prDate.setSpacingBefore(4);
        prDate.setFont(catFont);
        // Add item into Paragraph
        prDate.add(startDate + "   To   " + endDate);
        prDate.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prDate);
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 6, 4, 4, 4});
        table.setSpacingBefore(4);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("ID" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Name" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Weight" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Total Amount" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Signature" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));

        table.setHeaderRows(1);
        count = 0;
        Totcount = mList.size();
        printLog("Totcount", "== ==pdf===" + Totcount);

        double totalMilk = 0.0, TotalAmount = 0.0;
        for (int i = 0; i < mList.size(); i++) {
            count++;

            table.addCell(new Phrase(mList.get(i).unic_customer + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).name + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).grnd_total_amt + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase("", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));

            if (!mList.get(i).total_milk.equals("") && !mList.get(i).total_milk.equals("-")) {
                totalMilk = totalMilk + Double.parseDouble(mList.get(i).total_milk);
            }

            if (!mList.get(i).grnd_total_amt.equals("") && !mList.get(i).grnd_total_amt.equals("-")) {
                TotalAmount = TotalAmount + Double.parseDouble(mList.get(i).grnd_total_amt);
            }

        }

        document.add(table);
        PdfPTable pTable = new PdfPTable(2);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("Total Weight" + ": " + String.format("%.3f", totalMilk), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.BOLD)));
        pTable.addCell(new Phrase("Total Amount" + ": " + "Rs " + "" + String.format("%.2f", TotalAmount), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.BOLD)));
        document.add(pTable);

        Pdfcreted = "false";
        if (document != null) {
            progressDialog.dismiss();
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.OPen_PDF));
            openPdfFile(mContext, pdfFile);
        }

    }

    public void createPDF(String userName, String unic_id, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String avFat, String avgRate) throws IOException {


        filePath = unic_id + "_" + userName + System.currentTimeMillis() + ".pdf";
        folderName = "MeriDairy/Seller/";
        pdfFile = createPDFFile(mContext, folderName, filePath);
        document = new Document(PageSize.A4);
        filePath = pdfFile.getAbsolutePath();
        printLog("filePath===>>>>>>>>>>", filePath);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            // Open Document for Writting into document
            document.open();

            addTitlePage(document, unic_id, userName, mobile_no, mList, avFat, avgRate);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.close();

    }


    public void addTitlePage(Document document, String unic_id, String userName, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String avgFat, String avgRate) throws DocumentException {
        // Create new Page in PDF
        count++;
        Pdfcreted = "true";
        addAppIcon(mContext, document);
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, PdfSubTitle_FontSize12, Font.BOLD);
        dairyNameIcon(mContext, sessionManager.getValueSesion(SessionManager.KEY_center_name), document);
        Image imgQrCode = getQrCodeBitmap(mContext);

        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.setWidths(new float[]{9, 1,});
        tableHead.addCell(getCell("ID" + " :- " + unic_id + "\n" + "Name" + ":- " + userName + "\n" + "Mobile No" + ":- " + mobile_no, PdfPCell.ALIGN_LEFT, PdfTitle_FontSize14));
        tableHead.addCell(imgQrCode);
        document.add(tableHead);
        //Date
        Paragraph prDate = new Paragraph();
        prDate.setSpacingAfter(4);
        prDate.setSpacingBefore(4);
        prDate.setFont(catFont);
        // Add item into Paragraph
        prDate.add(startDate + "   To   " + endDate);
        prDate.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prDate);

        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 4, 4, 4, 6, 4, 4, 6});
        table.setSpacingBefore(4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("Date" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Session" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Fat" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("SNF" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Rs/Ltr" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Weight" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Bonus" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Total Amount" + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.setHeaderRows(1);

        double totalMilk = 0.0, AvgFat = 0.0, TotalFat = 0.0, TotalRate = 0.0, AvgRate = 0.0, TotalAmount = 0.0, TotalBonus = 0.0;
        int rateSize = 0, countData = 0;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).shift.equals("morning")) {
                table.addCell(new Phrase(mList.get(i).for_date + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            } else {
                table.addCell(new Phrase("", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            }
            table.addCell(new Phrase(mList.get(i).session + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).fat + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).snf + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).entry_total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_bonus + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));

            if (!mList.get(i).entry_total_milk.equals("") && !mList.get(i).entry_total_milk.equals("-")) {
                totalMilk = totalMilk + getFloatValuFromInputText(mList.get(i).entry_total_milk);
            }
            if (!mList.get(i).fat.equals("") && !mList.get(i).fat.equals("-")) {
                TotalFat = TotalFat + getFloatValuFromInputText(mList.get(i).fat) * getFloatValuFromInputText(mList.get(i).entry_total_milk);
            }
            if (!mList.get(i).per_kg_price.equals("") && !mList.get(i).per_kg_price.equals("-")) {
                TotalRate = TotalRate + getFloatValuFromInputText(mList.get(i).per_kg_price);
                rateSize = rateSize + 1;
            }
            if (!mList.get(i).total_price.equals("") && !mList.get(i).total_price.equals("-")) {
                TotalAmount = TotalAmount + getFloatValuFromInputText(mList.get(i).total_price);
            }
            if (!mList.get(i).total_bonus.equals("") && !mList.get(i).total_bonus.equals("-")) {
                TotalBonus = TotalBonus + getFloatValuFromInputText(mList.get(i).total_bonus);
            }
        }

        AvgFat = TotalFat / totalMilk;
        AvgRate = TotalRate / rateSize;
        document.add(table);
        PdfPTable pTable = new PdfPTable(5);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("Total Weight" + ": " + String.format("%.3f", totalMilk), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Avg Fat" + ": " + String.format("%.1f", AvgFat), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Avg Rate" + ": " + String.format("%.2f", AvgRate), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Bonus" + ":" + "Rs " + "" + String.format("%.2f", TotalBonus), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Amount" + ": " + "Rs " + "" + String.format("%.2f", TotalAmount), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        document.add(pTable);

        if (count == Totcount) {

            progressDialog.dismiss();
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.OPen_PDF));
            if (Totcount > 1) {
                openFolder(mContext, folderName);
            } else {
                openPdfFile(mContext, pdfFile);
            }

        }
    }

    public void getTenDaysSellingHistory(final Context mContext, String from_date, String to_date, String status, String type) {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("Success")) {
                        mListTendayAllCustomerData = new ArrayList<>();
                        fileUrl = jsonObject.getString("multi_pdf_url");
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (viewAllCustomer == 0) {
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                startDate = object.getString("from_date");
                                endDate = object.getString("to_date");
                                addTenDaysData(object);
                            }
                        } else {
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                startDate = object.getString("from_date");
                                endDate = object.getString("to_date");
                                if (getFloatValuFromInputText(object.getString("total_price")) != 0) {
                                    addTenDaysData(object);
                                }
                            }
                        }
                        tvStartDate.setText(startDate);
                        tvEndDate.setText(endDate);
                        setTransactionListAdapter();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };


        if (type.equals("first")) {
            RequestBody body = new FormEncodingBuilder().addEncoded("from_date", from_date).addEncoded("dairy_id", dairyid).addEncoded("next_status", status).build();
            caller.addRequestBody(body);
        } else {
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("from_date", from_date)
                    .addEncoded("dairy_id", dairyid)
                    .addEncoded("next_status", status)
                    .addEncoded("fd", from_date)
                    .addEncoded("td", to_date).build();
            caller.addRequestBody(body);
        }
        caller.execute(getTenDayMilkSellEntryAPI);
    }

    private void addTenDaysData(JSONObject object) throws JSONException {
        ArrayList<TenDaysMilkSellHistory> dataList = new ArrayList<>();
        startDate = object.getString("from_date");
        endDate = object.getString("to_date");
        mListTendayAllCustomerData.add(new TenDaysMilkSellHistory(object.getString("id"), object.getString("name"), object.getString("father_name"), object.getString("phone_number"), object.getString("user_group_id"), object.getString("firebase_tocan"), object.getString("unic_customer"), object.getString("avg_fat"), object.getString("total_milk"), object.getString("milk_price"), object.getString("total_price"), dataList, "false", object.getString("url_code"), object.getString("from_date"), object.getString("to_date"), object.getString("pdf_url")));
    }

    public void getTenDaysData(final Context mContext, String from_date, String to_date, String dairy_id, String customer_ids) {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    mListTendayHistory = new ArrayList<>();
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        ArrayList<TenDaysMilkSellHistory> dataList = new ArrayList<>();
                        JSONArray mainArray = object.getJSONArray("main");
                        String pdfUrl = object.getString("pdf_url");
                        for (int j = 0; j < mainArray.length(); j++) {
                            JSONObject dataObj = mainArray.getJSONObject(j);
                            dataList.add(new TenDaysMilkSellHistory(dataObj.getString("for_date"), dataObj.getString("session"), dataObj.getString("fat"), dataObj.getString("snf"), dataObj.getString("per_kg_price"), dataObj.getString("total_price"), dataObj.getString("total_milk"), dataObj.getString("shift"), dataObj.getString("total_bonus")));
                        }
                        JSONObject user_data = object.getJSONObject("user_data");
                        mListTendayHistory.add(new TenDaysMilkSellHistory(object.getString("avg_fat"), object.getString("total_milk"), object.getString("avg_rate"), object.getString("opening_balance"), object.getString("grnd_total_amt"), user_data.getString("id"), user_data.getString("unic_customer"), user_data.getString("name"), user_data.getString("father_name"), user_data.getString("phone_number"), pdfUrl, dataList));
                    }
                    setTenDaysList();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder().addEncoded("from_date", from_date)
                .addEncoded("to_date", to_date)
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_ids", customer_ids).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMultiSellerTenDaysDataAPI);
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                ArrayList<String> monthList = getMonthList();
                String month = "";
                for (int i = 0; i < monthList.size(); i++) {
                    if (monthOfYear == i) {
                        month = monthList.get(i);
                    }
                }
                String day = "";
                printLog("Month====>>>", month);
                day = checkDigit(dayOfMonth);
                formattedDate = day + "-" + checkDigit((monthOfYear + 1)) + "-" + year;
                if (from.equals("StartDate")) {
                    tvStartDate.setText(formattedDate);
                } else {
                    tvEndDate.setText(formattedDate);
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    @Override
    public void onDownLoadComplete(String folderName, File file) {
        printLog("", "Totcount>>>>>" + Totcount + "  \n count>>>>" + count);
        printLog("Downloaded file==" + file.getName(), file.getAbsolutePath());
        printLog("Downloaded file==" + file.getName(), file.exists() + "");

        if (Totcount > 1 && this.count >= Totcount) {
            Context context = this.mContext;
            UtilityMethod.showAlertWithButton(context, context.getString(R.string.OPen_PDF));
            UtilityMethod.openFolder(this.mContext, folderName);
        } else if (this.Totcount == 1) {
            if (file.exists()) {
                UtilityMethod.openPdfFile(this.mContext, file);
            }
        }
    }
}