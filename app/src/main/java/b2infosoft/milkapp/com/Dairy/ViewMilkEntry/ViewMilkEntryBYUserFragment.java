package b2infosoft.milkapp.com.Dairy.ViewMilkEntry;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
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

import b2infosoft.milkapp.com.Dairy.Customer.AddCustomerFragment;
import b2infosoft.milkapp.com.Dairy.Customer.Fragment.CustomerListDialogFragment;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter.ViewMilkEntryAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DownloadFileFromURL;
import b2infosoft.milkapp.com.useful.PDFUtills;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintTenDaysReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.ViewUserEntryEndDate;
import static b2infosoft.milkapp.com.appglobal.Constant.ViewUserEntryStartDate;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTitle_FontSize14;
import static b2infosoft.milkapp.com.useful.PDFUtills.addAppIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.addBottomLine;
import static b2infosoft.milkapp.com.useful.PDFUtills.addQrCode;
import static b2infosoft.milkapp.com.useful.PDFUtills.addViewEntryTable;
import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;
import static b2infosoft.milkapp.com.useful.PDFUtills.dairyNameIcon;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isNetworkAvaliable;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openFolder;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class ViewMilkEntryBYUserFragment extends Fragment implements View.OnClickListener, OnCustomerListener, OnDownLoadListener {

    AutoCompleteTextView actv_CustomerID;
    TextView tvFathername, tvUserName, tvStartDate, tvEndDate, tvTotalWeight, tvTotalFat, tvFatRat, tvTotalAmt, tvTotalBonus;
    ImageView ivUserIcon;
    View lvUserInfo, lvStartDate, lvEndDate;
    Button btnSubmit;
    RecyclerView recyclerView;
    CircularProgressBar yourCircularProgressbar;
    Context mContext;
    String formattedDate = "", userGroupId = "3";
    Toolbar toolbar;
    TextView toolbar_title;
    SessionManager sessionManager;
    String pdfUrl = "", strFrom = "", filePath = "", folderName = "", dairyid = "", selectedName = "", selectedFatherName = "", selectedId = "",
            from_date = "", to_date = "";
    Double totalBonus = 0.0;
    Bundle bundle = null;
    String entryID = "", unic_customer = "";
    ImageView imgPrint;
    ArrayList<TenDaysMilkSellHistory> pdfEntryList = new ArrayList<>();
    ProgressDialog progressDialog = null;
    int Totcount = 0, count = 0;
    String printTypeDialog = "";
    Fragment fragment = null;
    View view;
    DownloadFileFromURL downloadFileFromURL;
    private int mYear = 0, mMonth = 0, mDay = 0;
    private String Pdfcreted = "";
    private String avg_fat_tenDays = "",opening_balance = "0", avg_rate_tenDays = "", total_milk_tenDays = "", grnd_total_amt_tenDays = "", user_data_name = "";
    private String user_data_unic_customer = "", user_data_phone_number = "";
    private Document document;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_user_all_entry, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        bundle = getArguments();
        strFrom = nullCheckFunction(bundle.getString("from"));
        userGroupId = bundle.getString("user_group_id");
        downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);

        initView();

        return view;
    }


    private void initView() {
        sessionManager = new SessionManager(mContext);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.View_All_Entry));
        imgPrint = view.findViewById(R.id.imgPrintRecp);
        lvUserInfo = view.findViewById(R.id.lvUserInfo);
        tvUserName = view.findViewById(R.id.tvUserName);
        lvStartDate = view.findViewById(R.id.lvStartDate);
        lvEndDate = view.findViewById(R.id.lvEndDate);
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        ivUserIcon = view.findViewById(R.id.ivUserIcon);
        tvFathername = view.findViewById(R.id.tvFathername);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        tvTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tvTotalFat = view.findViewById(R.id.tvTotalFat);
        tvFatRat = view.findViewById(R.id.tvFatRat);
        tvTotalAmt = view.findViewById(R.id.tvTotalAmt);
        tvTotalBonus = view.findViewById(R.id.tvTotalBonus);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        recyclerView = view.findViewById(R.id.recycler_allEntry);
        yourCircularProgressbar = view.findViewById(R.id.yourCircularProgressbar);

        imgPrint.setVisibility(View.VISIBLE);

        imgPrint.setOnClickListener(this);
        lvStartDate.setOnClickListener(this);
        lvEndDate.setOnClickListener(this);
        tvFathername.setOnClickListener(this);
        ivUserIcon.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        toolbar.setVisibility(View.GONE);
        toolbar.setNavigationIcon(R.drawable.back_arrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);


        getCustomerList();
    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {
        if (actv_CustomerID != null) {
            actv_CustomerID.setText(customerList.getUnic_customer());
            selectedId = customerList.getId();
            selectedName = customerList.getName();
            unic_customer = customerList.getUnic_customer();
            from_date = ViewUserEntryStartDate;
            to_date = ViewUserEntryEndDate;
            if (from_date.length() > 0 && to_date.length() > 0) {
                tvStartDate.setText(ViewUserEntryStartDate);
                tvEndDate.setText(ViewUserEntryEndDate);
                if (tvEndDate.getText().toString().length() > 0 && tvStartDate.getText().toString().length() > 0) {
                    getTenDaysData();
                }
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (tvStartDate.getText().toString().length() > 0 && tvEndDate.getText().toString().length() > 0 && selectedId != null && !tvFathername.getText().toString().equals("")) {
                    if (!unic_customer.equals("")) {
                        from_date = tvStartDate.getText().toString().trim();
                        to_date = tvEndDate.getText().toString().trim();
                        getTenDaysData();
                    }
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Incomplete_Entry));
                }
                break;
            case R.id.tvFathername:
            case R.id.ivUserIcon:
                ViewUserEntryStartDate = tvStartDate.getText().toString();
                ViewUserEntryEndDate = tvEndDate.getText().toString();
                FromWhere = "ViewUserEntry";
                fragment = new CustomerListDialogFragment(userGroupId, this);
                FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                ft.add(R.id.dairy_container, fragment);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.lvStartDate:
                getDate("StartDate");
                break;
            case R.id.lvEndDate:
                getDate("EndDate");
                break;
            case R.id.imgPrint:
                DialogPrint();
                break;
            case R.id.imgPrintRecp:
                DialogPrint();
                break;


        }
    }

    public void DialogPrint() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);
        dialog.setCancelable(false);
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
                printTypeDialog = "pdf";
                dialog.dismiss();
                printData();
            }
        });
        tv_print_reciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                        dialogBluetooth(mContext);
                    } else {
                        printTypeDialog = "reciept";
                        dialog.dismiss();
                        printData();
                    }
                } else {
                    showAlertWithTitle("Please enable Bluetooth device", mContext);
                    enableBluetooth();
                    dialogBluetooth(mContext);

                }
            }
        });

        dialog.show();
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

    private void printData() {

        if (pdfEntryList.size() == 0) {
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.You_have_no_entry_to_Print));
        } else {
            if (printTypeDialog.equalsIgnoreCase("pdf")) {
                Pdfcreted = "true";
                Totcount = pdfEntryList.size();
                Pdfcreted = "true";
                downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        filePath = user_data_unic_customer + "_" + user_data_name + "_" + System.currentTimeMillis() + ".pdf";
                        folderName = "MeriDairy/Seller All MilkEntry/";
                        if (userGroupId.equals("4")) {
                            folderName = "MeriDairy/Buyer All MilkEntry/";
                        }
                        try {
                            downloadFileFromURL.initURL(folderName, filePath);
                            downloadFileFromURL.execute(pdfUrl);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                }, 0);
            } else {
                pdfEntryList.get(0).setFrom_date(from_date);
                pdfEntryList.get(0).setTo_date(to_date);
                PrintTenDaysReciept(mContext, user_data_name, user_data_unic_customer, pdfEntryList);

            }

        }
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
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
                        Log.d("Month>>>", month);
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

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    public void setCustomerList(final ArrayList<CustomerListPojo> customerList) {
        if (customerList.isEmpty()) {
            FromWhere = "Dashboard";
            fragment = new AddCustomerFragment();
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Add_Customer));
            goNextFragmentWithBackStack(mContext, fragment);
        } else {
            actv_CustomerID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable actid) {
                    if (actid.toString().equals("")) {
                        selectedId = "";
                        unic_customer = "";
                        tvFathername.setText("");
                    } else {
                        for (int i = 0; i < customerList.size(); i++) {
                            if (customerList.get(i).user_group_id.equals(userGroupId)) {
                                if (actid.toString().equals(customerList.get(i).unic_customer)) {
                                    unic_customer = customerList.get(i).unic_customer;
                                    selectedId = customerList.get(i).getId();
                                    selectedName = customerList.get(i).name;
                                    selectedFatherName = customerList.get(i).father_name;
                                    System.out.println("strSelectedId>>>>" + selectedId + " " + unic_customer + " " + selectedFatherName + " " + selectedName);
                                    tvFathername.setText(customerList.get(i).name + " s/o " + customerList.get(i).father_name);
                                }
                            }
                        }
                    }
                }
            });
        }
    }



    public void setTransactionListAdapter(ArrayList<TenDaysMilkSellHistory> mList) {
        if (mList.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            yourCircularProgressbar.setVisibility(View.GONE);
            tvTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n0.0%");
            tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n0.000 " + mContext.getString(R.string.Ltr));
            tvTotalAmt.setText(mContext.getString(R.string.Total_Amount) + "\n" + " " + mContext.getString(R.string.Rs) + ". 0.00");
            tvFatRat.setText(mContext.getString(R.string.Fat_Rat) + "\n" + " " + mContext.getString(R.string.Rs) + " 0.00");
            tvTotalBonus.setText(mContext.getString(R.string.TOTAL_BONUS) + "\n" + " " + mContext.getString(R.string.Rs) + " 0.00");
            avg_fat_tenDays = "";
            total_milk_tenDays = "";
            grnd_total_amt_tenDays = "";
            avg_rate_tenDays = "";
            user_data_name = "";
            user_data_phone_number = "";
            user_data_unic_customer = "";
        } else {
            ArrayList<TenDaysMilkSellHistory> entryList = new ArrayList<>();
            entryList = mList.get(0).tenDaysList;
            if (pdfEntryList.size() == 0) {
                pdfEntryList = entryList;
            } else {
                pdfEntryList.clear();
                pdfEntryList = entryList;
            }

            recyclerView.setVisibility(View.VISIBLE);
            yourCircularProgressbar.setVisibility(View.GONE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setHasFixedSize(true);
            ViewMilkEntryAdapter customerListAdapter2 = new ViewMilkEntryAdapter(mContext, entryList);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(customerListAdapter2);
            customerListAdapter2.notifyDataSetChanged();
            totalBonus = 0d;
            for (int i = 0; i < entryList.size(); i++) {
                if (!entryList.get(i).total_bonus.equals("-") && !entryList.get(i).total_bonus.equals("")) {
                    totalBonus = totalBonus + Double.parseDouble(entryList.get(i).total_bonus);
                }
            }
            avg_fat_tenDays = mList.get(0).avg_fat_tenDays;
            total_milk_tenDays = mList.get(0).total_milk_tenDays;
            grnd_total_amt_tenDays = mList.get(0).grnd_total_amt_tenDays;
            opening_balance = mList.get(0).getOpening_balance();
            avg_rate_tenDays = mList.get(0).avg_rate_tenDays;
            user_data_name = mList.get(0).user_data_name;
            user_data_phone_number = mList.get(0).user_data_phone_number;
            user_data_unic_customer = mList.get(0).user_data_unic_customer;

            tvTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + mList.get(0).avg_fat_tenDays + "%");
            tvTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + /*String.format("%.3f", totalWeight)*/mList.get(0).total_milk_tenDays + " " + mContext.getString(R.string.Ltr));
            tvTotalAmt.setText((mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " +/*String.format("%.2f", totalAmt)*/mList.get(0).grnd_total_amt_tenDays));
            tvFatRat.setText((mContext.getString(R.string.Fat_Rat) + "\n" + mContext.getString(R.string.Rs) + " " + /*String.format("%.2f", fatRate)*/mList.get(0).avg_rate_tenDays));
            tvTotalBonus.setText((mContext.getString(R.string.TOTAL_BONUS) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totalBonus)/*mList.get(0).avg_rate_tenDays*/));
        }
    }

    public void createPDF(String userName, String unic_id, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String avFat, String avgRate) {

        filePath  = unic_id + "_" + userName + System.currentTimeMillis() + ".pdf";
       folderName="/MeriDairy/Seller All MilkEntry/";
       if (userGroupId.equals("4")){
           folderName="/MeriDairy/Buyer All MilkEntry/";
       }

        try {
            File pdfFile=createPDFFile(mContext,folderName,filePath);

            document = new Document(PageSize.A4);
            filePath=pdfFile.getAbsolutePath();
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
        } catch (IOException e) {
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

    public void addTitlePage(Document document, String unic_id, String userName, String mobile_no,
                             ArrayList<TenDaysMilkSellHistory> mList, String avgFat, String avgRate)
            throws DocumentException {
        // Create new Page in PDF
        Pdfcreted = "true";

        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, PdfTitle_FontSize14, Font.BOLD);
        addAppIcon(mContext,document);
        dairyNameIcon(mContext,sessionManager.getValueSesion(SessionManager.KEY_center_name),document);

        PdfPTable tableHead = new PdfPTable(2);
        tableHead.setWidthPercentage(100);
        tableHead.addCell(getCell("ID:- " + unic_id, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Name:- " + userName, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Mobile Number:- " + mobile_no, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("", PdfPCell.ALIGN_RIGHT));
        document.add(tableHead);
        //Date
        Paragraph prDate = new Paragraph();
        prDate.setSpacingAfter(4);
        prDate.setSpacingBefore(4);
        prDate.setFont(catFont);
        // Add item into Paragraph
        prDate.add(tvStartDate.getText().toString() + "   To   " + tvEndDate.getText().toString());
        prDate.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prDate);
      addViewEntryTable(document,mList, opening_balance );

       // bottom line
        addBottomLine(mContext,document);
        addQrCode(mContext,document);
        if (count == Totcount) {
            if (document != null) {
               openFolder(mContext,folderName);
                progressDialog.dismiss();
            }
        }

    }

    public void getCustomerList() {

        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerListByGroupId(userGroupId);
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    public void getTenDaysData() {
        final ArrayList<TenDaysMilkSellHistory> tenDaysMainList = new ArrayList<>();
        if (isNetworkAvaliable(mContext)) {
            NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
                @Override
                public void handleResponse(String response) {
                    try {
                        JSONArray mainJsonArray = new JSONArray(response);
                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject object = mainJsonArray.getJSONObject(i);
                            ArrayList<TenDaysMilkSellHistory> dataList = new ArrayList<>();
                            JSONArray mainArray = object.getJSONArray("main");
                              pdfUrl = object.getString("pdf_url");
                            for (int j = 0; j < mainArray.length(); j++) {
                                JSONObject dataObj = mainArray.getJSONObject(j);
                                dataList.add(new TenDaysMilkSellHistory(dataObj.getString("for_date"),
                                        dataObj.getString("session"), dataObj.getString("fat"),
                                        dataObj.getString("snf"), dataObj.getString("per_kg_price"),
                                        dataObj.getString("total_price"), dataObj.getString("total_milk"),
                                        dataObj.getString("shift"), dataObj.getString("total_bonus")
                                ));
                            }
                            JSONObject user_data = object.getJSONObject("user_data");

                            tenDaysMainList.add(new TenDaysMilkSellHistory(
                                    object.getString("avg_fat"), object.getString("total_milk"),
                                    object.getString("avg_rate"), object.getString("opening_balance"), object.getString("grnd_total_amt"),
                                    user_data.getString("id"), user_data.getString("unic_customer"),
                                    user_data.getString("name"), user_data.getString("father_name"),
                                    user_data.getString("phone_number"), pdfUrl,dataList));
                            pdfUrl = object.getString("pdf_url");
                        }

                        if (!tenDaysMainList.isEmpty()){
                        setTransactionListAdapter(tenDaysMainList);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            RequestBody body = new FormEncodingBuilder()
                    .addEncoded("from_date", from_date)
                    .addEncoded("to_date", to_date)
                    .addEncoded("dairy_id", dairyid)
                    .addEncoded("customer_ids", selectedId).build();
            caller.addRequestBody(body);

            caller.execute(Constant.getMultiSellerTenDaysDataAPI);
        }
    }


    @Override
    public void onDownLoadComplete(String folderName, File file) {
        UtilityMethod.openPdfFile(getActivity(), file);
    }
}
