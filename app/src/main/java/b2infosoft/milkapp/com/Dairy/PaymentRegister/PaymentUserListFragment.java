package b2infosoft.milkapp.com.Dairy.PaymentRegister;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Interface.OnRefreshPaymentRegister;
import b2infosoft.milkapp.com.Model.BeanTransactionUserItem;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DownloadFileFromURL;
import b2infosoft.milkapp.com.useful.DownloadMultipleFileFromURL;
import b2infosoft.milkapp.com.useful.PDFUtills;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintTenDaysWithTransReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.printBhugtanRegister;
import static b2infosoft.milkapp.com.appglobal.Constant.getUserTransactionBalanceAPI;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.Key_SellerBhugtanSetting;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfSubTitle_FontSize12;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTitle_FontSize14;
import static b2infosoft.milkapp.com.useful.PDFUtills.addAppIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.addTransactionTable;
import static b2infosoft.milkapp.com.useful.PDFUtills.addViewEntryTable;
import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;
import static b2infosoft.milkapp.com.useful.PDFUtills.dairyNameIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.getCell;
import static b2infosoft.milkapp.com.useful.PDFUtills.getQrCodeBitmap;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openFolder;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openPdfFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;


public class PaymentUserListFragment extends Fragment implements View.OnClickListener, OnRefreshPaymentRegister, OnDownLoadListener {
    private static final String TAG = "Product===>>>";

    Context mContext;
    Toolbar toolbar;
    EditText et_Search;
    View view;
    ImageView imgPrint, imgRecieptPrint;
    TextView tvPrint, tvTotalCredit, tvTotalDebit, tvRemainingAmount;
    ArrayList<BeanTransactionUserItem> mList;
    PaymentBhugtanListAdapter adapter;
    SwipeRefreshLayout pullToRefresh;
    RecyclerView recyclerView;
    SessionManager sessionManager;
    DownloadFileFromURL downloadFileFromURL;
    DownloadMultipleFileFromURL downloadListener;
    int viewAllCustomer = 0;
    String fileUrl = "", startDate = "", endDate = "", strFrom = "", strTitle = "", customerID = "", userGroupId = "";
    Document document;
    File pdfFile;
    String filePath = "", folderName = "";
    //ProgressBar progressBar;
    String Pdfcreted = "false", printTypeDialog = "receipt";
    ProgressDialog progressDialog = null;
    int Totcount = 0, count = 0;
    double totalCr = 0.0, totalDr = 0.0, totalBalance = 0.0;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment_bhugtan, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        imgPrint = toolbar.findViewById(R.id.imgPrint);

        et_Search = view.findViewById(R.id.et_Search);
        imgRecieptPrint = view.findViewById(R.id.imgRecieptPrint);
        recyclerView = view.findViewById(R.id.recyclerView);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        tvPrint = view.findViewById(R.id.tvPrint);
        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvTotalDebit = view.findViewById(R.id.tvTotalDebit);
        tvRemainingAmount = view.findViewById(R.id.tvRemainingAmount);

        Bundle bundle = getArguments();
        imgRecieptPrint.setVisibility(View.VISIBLE);
        toolbar.setTitle(mContext.getString(R.string.Payment_Register));
        viewAllCustomer = sessionManager.getIntValueSesion(Key_SellerBhugtanSetting);
        if (bundle != null) {
            strFrom = bundle.getString("FromWhere");
            strTitle = bundle.getString("title");
            userGroupId = bundle.getString("user_group_id");

        }
        downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
        downloadListener = new DownloadMultipleFileFromURL(this.mContext, this);
        if (strFrom.equals("tab")) {
            toolbar.setVisibility(View.GONE);
            //imgRecieptPrint.setVisibility(View.GONE);
        }
        imgPrint.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
        imgRecieptPrint.setOnClickListener(this);


        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getUserPaymentList();
                pullToRefresh.setRefreshing(false);
            }
        });


        toolbarManage();
        getUserPaymentList();

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        return view;
    }

    public void toolbarManage() {
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void recyclerViewUI() {
        adapter = new PaymentBhugtanListAdapter(mContext, mList,startDate,endDate,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        addTextListener();
        tvTotalCredit.setText(mContext.getString(R.string.Total_Credit)+"\n"+String.format("%.2f",totalCr));
        tvTotalDebit.setText(mContext.getString(R.string.Total_Debit)+"\n"+String.format("%.2f",totalDr));
        tvRemainingAmount.setText(mContext.getString(R.string.Total_Remaining)+"\n"+String.format("%.2f",totalBalance));

    }

    public void addTextListener() {

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();
                adapter.filterSearch(query.toString());


            }
        });
    }

    public void getUserPaymentList() {
        mList = new ArrayList<>();
        totalCr= 0.0; totalDr = 0.0; totalBalance = 0.0;
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..", false) {
            @Override
            public void handleResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        startDate=jsonObject.getString("from_date");
                        endDate = jsonObject.getString("to_date");
                        fileUrl = jsonObject.getString("pdf_url");
                        if (viewAllCustomer == 0) {
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);
                                if (jobj.getString("user_group_id").equals(userGroupId)) {
                                    mList.add(new BeanTransactionUserItem(jobj.getString("id"),
                                            jobj.getString("name"), nullCheckFunction(jobj.getString("unic_customer")),
                                            jobj.getString("user_group_id"), jobj.getDouble("total_dr"), jobj.getDouble("total_cr"),
                                            jobj.getDouble("balance")));
                                    totalCr = totalCr +  jobj.getDouble("total_cr") ;
                                    totalDr = totalDr + jobj.getDouble("total_dr");
                                }
                            }
                        } else {
                            for (int i = 0; i < jsonData.length(); i++) {
                                JSONObject jobj = jsonData.getJSONObject(i);

                                if (jobj.getDouble("balance") != 0 && jobj.getString("user_group_id").equals(userGroupId)) {
                                    mList.add(new BeanTransactionUserItem(jobj.getString("id"),
                                            jobj.getString("name"), nullCheckFunction(jobj.getString("unic_customer")),
                                            jobj.getString("user_group_id"), jobj.getDouble("total_dr"), jobj.getDouble("total_cr"),
                                            jobj.getDouble("balance")));
                                    totalCr = totalCr +  jobj.getDouble("total_cr") ;
                                    totalDr = totalDr + jobj.getDouble("total_dr");
                                }
                            }
                        }
                        totalBalance = totalDr - totalCr ;
                        recyclerViewUI();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("user_group_id",userGroupId)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(getUserTransactionBalanceAPI);

    }


    private boolean checkIsUserSelected() {
        boolean isSelected = false;
        customerID = "";
      StringBuilder   stringBuilder = new StringBuilder();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isChecked()) {
                isSelected = true;
                stringBuilder.append(mList.get(i).getId() + ",");

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
    @Override
    public void onClick(View view) {
        downloadFileFromURL = new DownloadFileFromURL(mContext, this);
        switch (view.getId()){
            case  R.id.imgRecieptPrint:
                if (!mList.isEmpty()){

                dialogPrint("printImg");}
                break;
                case  R.id.tvPrint:
                if (!mList.isEmpty()){

                dialogPrint("print");}
                break;
        }

    }
    public void dialogPrint(final String from) {
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
                    Totcount = 1;
                    count = 1;
                    filePath = sessionManager.getValueSesion(SessionManager.KEY_dairy_name) + "_" + System.currentTimeMillis() + ".pdf";
                    folderName = "MeriDairy/Seller Bhugtan/";
                    if (userGroupId.equals("4")) {
                        folderName = "MeriDairy/Buyer Bhugtan/";
                    }
                    try {
                        downloadFileFromURL.initURL(folderName, filePath);
                        downloadFileFromURL.execute(fileUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    printTypeDialog = "pdf";
                    if (checkIsUserSelected()) {
                        getTenDaysData(mContext, startDate,endDate, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
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
                System.out.println("from=====" + from);

                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                        dialogBluetooth(mContext);
                    } else {
                        if (from.equalsIgnoreCase("printImg")) {
                           printBhugtanRegister(mContext,mList);
                        } else {
                            printTypeDialog = "receipt";
                            System.out.println("checkIsUserSelected=====" + checkIsUserSelected());
                            if (checkIsUserSelected()) {
                                getTenDaysData(mContext, startDate, endDate, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
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
    public void createBhugtanRegisterPDF( ) {
        filePath =sessionManager.getValueSesion(SessionManager.KEY_dairy_name) + "_" + System.currentTimeMillis() + ".pdf";
          folderName= "MeriDairy/Seller Bhugtan/";
        document = new Document(PageSize.A4);
        if (userGroupId.equals("4")){
            folderName= "MeriDairy/Buyer Bhugtan/";
        }

        try {
            pdfFile = createPDFFile(mContext,folderName,filePath);

            filePath=pdfFile.getAbsolutePath();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            // Open Document for Writing into document
            document.open();
            addTitlePageBhugtanRegister(document, mList);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();

    }

    public void addTitlePageBhugtanRegister(Document document, ArrayList<BeanTransactionUserItem> mList)
            throws DocumentException {
        // Create new Page in PDF
         progressDialog.show();
        addAppIcon(mContext,document);
        dairyNameIcon(mContext,sessionManager.getValueSesion(SessionManager.KEY_center_name),document);
        Image image=getQrCodeBitmap(mContext);

        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.setWidths(new float[]{9, 1,});
        tableHead.addCell(getCell("Center" + " :- " + sessionManager.getValueSesion(SessionManager.KEY_dairy_name) +"\n"
                +"Name" + ":- " + sessionManager.getValueSesion(SessionManager.KEY_Name)+"\n"
                +"Mobile No" + ":- " + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_LEFT,PdfTitle_FontSize14));
        tableHead.addCell(image);
        document.add(tableHead);

        //Date

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 6, 4, 4, 4, 4});
        table.setSpacingBefore(4);

        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("ID" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Name" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Total Credit" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Total Debit" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Balance Amount" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Signature" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));

        table.setHeaderRows(1);
        count = 0;
        Totcount = mList.size();
        System.out.println("Totcount== ==pdf===" + Totcount);


        for (int i = 0; i < mList.size(); i++) {
            count++;

            table.addCell(new Phrase(mList.get(i).getUnic_customer() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).getName() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).getTotal_cr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).getTotal_dr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).getBalance() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase("", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

        }

        document.add(table);
        PdfPTable pTable = new PdfPTable(3);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("Gross Credit" + ": "+ "Rs " + String.format("%.2f", totalCr), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
        pTable.addCell(new Phrase("Gross Debit" + ": " + "Rs "+ String.format("%.2f", totalDr), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
        pTable.addCell(new Phrase("Total Amount" + ": " + "Rs " +  String.format("%.2f", totalBalance), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
        document.add(pTable);
        Pdfcreted = "false";

                progressDialog.dismiss();
                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.OPen_PDF));
              openPdfFile(mContext,pdfFile);


    }


    @Override
    public void onRefreshList(ArrayList<BeanTransactionUserItem> beanTransactionUserItems) {
        mList=beanTransactionUserItems;
    }


    public void getTenDaysData(final Context mContext, String from_date, String to_date, String dairy_id, String customer_ids) {

        @SuppressLint("StaticFieldLeak") NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        ArrayList<TenDaysMilkSellHistory> mListTendayHistory = new ArrayList<>();
                        JSONArray jsonData = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonData.length(); i++) {
                            JSONObject object = jsonData.getJSONObject(i);
                            ArrayList<TenDaysMilkSellHistory> dataList = new ArrayList<>();
                            ArrayList<BeanUserTransaction> userTransactions = new ArrayList<>();
                            JSONArray mainArray = object.getJSONArray("main");
                            for (int j = 0; j < mainArray.length(); j++) {
                                JSONObject dataObj = mainArray.getJSONObject(j);
                                dataList.add(new TenDaysMilkSellHistory(dataObj.getString("for_date"), dataObj.getString("session"),
                                        dataObj.getString("fat"), dataObj.getString("snf"), dataObj.getString("per_kg_price"), dataObj.getString("total_price"),
                                        dataObj.getString("total_milk"), dataObj.getString("shift"), dataObj.getString("total_bonus")
                                ));
                            }

                            JSONArray transArray = object.getJSONArray("transactions");
                            for (int k = 0; k < transArray.length(); k++) {
                                JSONObject dataObj = transArray.getJSONObject(k);
                                String trnsDate = dateDDMMYY(dataObj.getString("date"));
                                userTransactions.add(new BeanUserTransaction(
                                        dataObj.getString("id"), dataObj.getString("party_name"), trnsDate,
                                        dataObj.getString("voucher_type"), dataObj.getString("voucher_no"),
                                        dataObj.getString("particular"), dataObj.getString("signature_image"),
                                        dataObj.getDouble("dr"), dataObj.getDouble("cr")));
                            }

                            JSONObject user_data = object.getJSONObject("user_data");
                            mListTendayHistory.add(new TenDaysMilkSellHistory(
                                    object.getString("avg_fat"), object.getString("total_milk"),
                                    object.getString("avg_rate"), object.getString("opening_balance"),
                                    object.getString("grnd_total_amt"), user_data.getString("id"),
                                    user_data.getString("unic_customer"),
                                    user_data.getString("name"), user_data.getString("father_name"),
                                    user_data.getString("phone_number"), object.getString("pdf_url"),
                                    dataList, userTransactions
                            ));

                        }
                        createTendaysPDF(mListTendayHistory);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        RequestBody body = new FormEncodingBuilder()
                .addEncoded("from_date", from_date).addEncoded("to_date", to_date)
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_ids", customer_ids).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getMultiUserTenDaysEntryWithTransDataAPI);
    }

    private void createTendaysPDF(ArrayList<TenDaysMilkSellHistory> milkSellHistories)  throws IOException{
        if (milkSellHistories.size() > 0) {
            Totcount = milkSellHistories.size();
            count = 0;
            for (int i = 0; i < milkSellHistories.size(); i++) {
                Pdfcreted = "true";
                final int finalI = i;
                ArrayList<TenDaysMilkSellHistory> pdfList = milkSellHistories.get(finalI).getTenDaysList();
                ArrayList<BeanUserTransaction> userTransactions = milkSellHistories.get(finalI).getUserTransactionsList();
                if (printTypeDialog.equalsIgnoreCase("pdf")) {
                    downloadListener = new DownloadMultipleFileFromURL(this.mContext, this);
                    Pdfcreted = PdfBoolean.TRUE;
                    count++;
                    filePath = milkSellHistories.get(i).getUser_data_unic_customer() + "_" + milkSellHistories.get(finalI).getUser_data_name() + System.currentTimeMillis() + ".pdf";
                    folderName = "MeriDairy/Seller MilkEntry With Transaction/";
                    if (userGroupId.equals("4")) {
                        folderName = "MeriDairy/Buyer MilkEntry With Transaction/";
                    }

                    downloadListener.initURL(folderName, filePath, Totcount, count);
                    downloadListener.execute(new String[]{milkSellHistories.get(i).getPdf_url()});

                } else {
                    pdfList.get(0).setFrom_date(startDate);
                    pdfList.get(0).setTo_date(endDate);
                    PrintTenDaysWithTransReciept(mContext, milkSellHistories.get(finalI).getUser_data_name(),
                            milkSellHistories.get(finalI).getUser_data_unic_customer(), milkSellHistories.get(finalI).getOpening_balance(), pdfList, userTransactions);
                }
            }
        }
    }

    public void createCustomerPDF(String unic_id, String userName, String mobile_no, String openingBalance,
                                  ArrayList<TenDaysMilkSellHistory> mList, ArrayList<BeanUserTransaction> userTransactions) {
        filePath = unic_id + "_" + userName + System.currentTimeMillis() + ".pdf";
        folderName = "MeriDairy/Seller MilkEntry With Transaction/";
        document = new Document(PageSize.A4);
        if (userGroupId.equals("4")) {
            folderName = "MeriDairy/Buyer MilkEntry With Transaction/";
        }

        try {
            pdfFile = createPDFFile(mContext, folderName, filePath);
            filePath = pdfFile.getAbsolutePath();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            addTitlePage(document, unic_id, userName, mobile_no, openingBalance, mList, userTransactions);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();

    }


    public void addTitlePage(Document document, String unic_id, String userName,
                             String mobile_no,
                             String openingBalance, ArrayList<TenDaysMilkSellHistory> mList,
                             ArrayList<BeanUserTransaction> userTransactions) throws DocumentException {
        // Create new Page in PDF
        count++;
        Pdfcreted = "true";
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, PdfSubTitle_FontSize12, Font.BOLD);
        addAppIcon(mContext, document);

        dairyNameIcon(mContext, sessionManager.getValueSesion(SessionManager.KEY_center_name), document);
        Image imgQrCode = getQrCodeBitmap(mContext);

        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.setWidths(new float[]{9, 1,});
        tableHead.addCell(getCell("ID" + " :- " + unic_id + "\n" + "Name" + ":- " + userName + "\n" + "Mobile No" + ":- " + mobile_no, PdfPCell.ALIGN_LEFT, PdfSubTitle_FontSize12));
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

        addViewEntryTable(document, mList, openingBalance);
        //Date
        Paragraph trans = new Paragraph();
        trans.setSpacingAfter(3);
        trans.setSpacingBefore(3);
        trans.setFont(catFont);
        // Add Transaction into Paragraph
        trans.add("*** Transactions ***");
        trans.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(trans);
        addTransactionTable(document, userTransactions);
        if (count == Totcount) {
            if (document != null) {
                progressDialog.dismiss();
                UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.OPen_PDF));
                if (Totcount > 1) {
                    openFolder(mContext, folderName);
                } else {
                    openPdfFile(mContext, pdfFile);
                }
            }
        }
    }


    public void onDownLoadComplete(String str, File file) {
        System.out.println("Totcount>>>>>" + Totcount + "  \n count>>>>" + count);
        printLog( "Downloaded file=="+file.getName(), file.getAbsolutePath());
        printLog( "Downloaded file=="+file.getName(), file.exists()+"");

        if (Totcount > 1 && this.count >= Totcount) {
            Context context = this.mContext;
            UtilityMethod.showAlertWithButton(context, context.getString(R.string.OPen_PDF));
            UtilityMethod.openFolder(this.mContext, str);
        } else if (this.Totcount == 1) {
            if (file.exists()){
                UtilityMethod.openPdfFile(this.mContext, file);}
        }
    }

}
