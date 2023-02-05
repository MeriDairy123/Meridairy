package b2infosoft.milkapp.com.PaymentRegister;

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
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.Customer.Fragment.CustomerListDialogFragment;
import b2infosoft.milkapp.com.Dairy.Invoice.ViewUserMilkEntryFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment;
import b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentUserTransectionsItemAdapter;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OnCustomerListener;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintUserTransactionReceipt;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfSubTitle_FontSize12;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTbHeader_FontSize10;
import static b2infosoft.milkapp.com.useful.PDFUtills.PdfTbRow_FontSize8;
import static b2infosoft.milkapp.com.useful.PDFUtills.addAppIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;
import static b2infosoft.milkapp.com.useful.PDFUtills.dairyNameIcon;
import static b2infosoft.milkapp.com.useful.PDFUtills.getQrCodeBitmap;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentWithBackStack;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.openPdfFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class PaymentTransactionHistoryFragment extends Fragment implements
        View.OnClickListener, View.OnKeyListener, FragmentBackPressListener, OnCustomerListener {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvStartDate, tvEndDate;
    ImageView imgStartDate, imgEndDate, imgPrint;
    b2infosoft.milkapp.com.Dairy.PaymentRegister.PaymentUserTransectionsItemAdapter adapter;
    RecyclerView recyclerView;
    AutoCompleteTextView actv_CustomerID;
    TextView tvFathername, tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    Button btnViewEntry, btnPaynow, btnCashReceive;
    String type = "user_transaction", strEndDate = "", strFromDate = "";
    ArrayList<BeanUserTransaction> transactionList = new ArrayList<>();
    Dialog openDialog;
    EditText edtAmtType, edtAmount;
    SessionManager sessionManager;
    Button btnSubmit;
    File pdfFile;
    String folderName = "", filePath = "", selectedName = "", selectedFatherName = "", dairy_id = "", userGroupId = "3", selectedId = "", unic_customer = "";

    String Pdfcreted = "false";
    int Totcount = 0, count = 0;
    ProgressDialog progressDialog = null;
    Document document;
    double totalCredit = 0.0, totalDebit = 0.0, remain = 0.0;
    String remainAmt = "";
    Fragment fragment = null;
    Bundle bundle = null;
    View view;

    private int mYear, mMonth, mDay;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_payment_transaction_history, container, false);
        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);

        return view;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                OnFragmentBackPressListener();
                return true;
            }
        }
        return false;
    }

    private void initView() {

        dairy_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnFragmentBackPressListener();
            }
        });

        imgPrint = toolbar.findViewById(R.id.imgPrint);
        imgPrint.setVisibility(View.VISIBLE);
        toolbar_title.setText(mContext.getString(R.string.TRANSACTION_HISTORY));
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        tvFathername = view.findViewById(R.id.tvFathername);
        tvFathername.setOnClickListener(this);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        strFromDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
        strEndDate = getSimpleDate();
        tvStartDate.setText(strFromDate);
        tvEndDate.setText(strEndDate);
        bundle = getArguments();

        if (bundle != null) {
            selectedId = nullCheckFunction(bundle.getString("CustomerId"));
            userGroupId = nullCheckFunction(bundle.getString("user_group_id"));
            selectedName = nullCheckFunction(bundle.getString("CustomerName"));
            unic_customer = nullCheckFunction(bundle.getString("unic_customer"));
            strFromDate = nullCheckFunction(bundle.getString("startDate"));
            strEndDate = nullCheckFunction(bundle.getString("endDate"));
            tvStartDate.setText(strFromDate);
            tvEndDate.setText(strEndDate);
            getCustomerList();

            if (!selectedId.equals("") && !selectedName.equals("")) {
                actv_CustomerID.setText(unic_customer);
                getTransactionList();

            } else {
                actv_CustomerID.setText("");
                tvFathername.setText("");

            }
        }
        actv_CustomerID.setEnabled(true);
        actv_CustomerID.setFocusable(true);
        tvFathername.setEnabled(true);
        btnPaynow = view.findViewById(R.id.btnPaynow);
        btnViewEntry = view.findViewById(R.id.btnViewEntry);
        btnCashReceive = view.findViewById(R.id.btnCashReceive);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        btnCashReceive.setOnClickListener(this);
        btnViewEntry.setOnClickListener(this);
        btnPaynow.setOnClickListener(this);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        tvFathername.setOnClickListener(this);
        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvPaidAmount = view.findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = view.findViewById(R.id.tvRemaningAmount);


        imgPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!transactionList.isEmpty()) {
                    DialogPrintOrPdf();
                } else {
                    showAlertWithButton(mContext, mContext.getString(R.string.No_Data_Found));
                }
            }
        });

    }

    public void DialogPrintOrPdf() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.dialog_print_document);
        dialog.setCancelable(false);
        ImageView imgClosed;
        TextView tv_downloadPDF, tv_print_reciept;

        // set the custom dialog components - text, image and button
        imgClosed = dialog.findViewById(R.id.imgClosed);
        tv_downloadPDF = dialog.findViewById(R.id.tv_downloadPDF);
        tv_print_reciept = dialog.findViewById(R.id.tv_print_reciept);
        imgClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        tv_downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                printPdf();
            }
        });
        tv_print_reciept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isBluetoothHeadsetConnected()) {
                    if (mDevice == null || mSocket == null || mOutputStream == null || mInputStream == null) {
                        dialogBluetooth(mContext);
                    } else {
                        dialog.dismiss();
                        strFromDate = tvStartDate.getText().toString().trim();
                        strEndDate = tvEndDate.getText().toString().trim();
                        PrintUserTransactionReceipt(getContext(), "Transactions", transactionList, strFromDate,
                                strEndDate, selectedName, unic_customer, totalCredit, totalDebit, remain);

                    }
                } else {
                    showAlertWithTitle(mContext.getString(R.string.Please_enable_Bluetooth_device), mContext);
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

    private void printPdf() {
        if (transactionList.size() > 0) {
            Totcount = transactionList.size();
            int i = 0;
            Pdfcreted = "true";
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Pdfcreted.equals("true")) {
                        try {
                            createPDF();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // }
                    }
                }
            }, 3000);

        } else {
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.No_Data_Found));
        }
    }

    private void createPDF() throws IOException {
        filePath = unic_customer + "_" + selectedName + System.currentTimeMillis() + ".pdf";
        folderName = "";
        if (userGroupId.equals("4")) {
            folderName = "MeriDairy/Buyer Transaction/";
        } else {
            folderName = "MeriDairy/Seller Transaction/";
        }
        pdfFile = createPDFFile(mContext, folderName, filePath);
        document = new Document(PageSize.A4);
        filePath = pdfFile.getAbsolutePath();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            // addMetaData(document);
            addTitlePage(document);
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
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, PdfSubTitle_FontSize12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);

        return cell;
    }

    private void addTitlePage(Document document) {
        try {
            count++;
            Pdfcreted = "true";
            addAppIcon(mContext, document);
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, PdfSubTitle_FontSize12, Font.BOLD);
            dairyNameIcon(mContext, sessionManager.getValueSesion(SessionManager.KEY_center_name), document);
            Image image = getQrCodeBitmap(mContext);

            PdfPTable tableHead = new PdfPTable(2);
            tableHead.setWidthPercentage(100);
            tableHead.setWidths(new float[]{9, 1,});

            tableHead.addCell(getCell("ID" + " :- " + unic_customer + "\n" + "Name" + ":- " + selectedName, PdfPCell.ALIGN_LEFT));
            tableHead.addCell(image);
            document.add(tableHead);


            Paragraph prDate = new Paragraph();
            prDate.setSpacingAfter(4);
            prDate.setSpacingBefore(4);
            prDate.setFont(catFont);
            // Add item into Paragraph
            prDate.add(strFromDate + "   To   " + strEndDate);
            prDate.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(prDate);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4, 4, 4, 4});
            table.setSpacingBefore(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Title", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Payment Type", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Voucher No.", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Description", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Credit", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.addCell(new Phrase("Debit", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
            table.setHeaderRows(1);


            for (int i = 0; i < transactionList.size(); i++) {
                table.addCell(new Phrase(transactionList.get(i).getTransaction_date() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getParty_name() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_type() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_no() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getDescription() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));


                if (transactionList.get(i).getCr() > 0) {
                    table.addCell(new Phrase(transactionList.get(i).getCr() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                } else {
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(transactionList.get(i).getDr() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                }


            }
            document.add(table);
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pTable.addCell(new Phrase("Total Credit" + ": " + String.format("%.2f", totalCredit), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            pTable.addCell(new Phrase("Total Debit" + ": " + String.format("%.2f", totalDebit), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            pTable.addCell(new Phrase("Remain Rs" + ": " + String.format("%.2f", remain), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            document.add(pTable);

            // addQrCode(mContext,document);


        } catch (Exception e) {

        }
        if (document != null) {

            progressDialog.dismiss();
            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.OPen_PDF));
            openPdfFile(mContext, pdfFile);
          /* // Uri selectedUri = Uri.fromFile(pdfFile);
            Uri selectedUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider",pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //intent.setDataAndType(selectedUri, "application/pdf");
            intent.setData(selectedUri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                startActivity(intent);
            }*/

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewEntry:
                if (!actv_CustomerID.getText().toString().equals("")) {
                    bundle = new Bundle();
                    fragment = new ViewUserMilkEntryFragment();
                    bundle.putString("FromWhere", "Transaction");
                    bundle.putString("CustomerId", selectedId);
                    bundle.putString("unic_customer", unic_customer);
                    bundle.putString("CustomerName", selectedName);
                    bundle.putString("CustomerFatherName", selectedFatherName);
                    bundle.putString("user_group_id", userGroupId);
                    bundle.putString("startDate", strEndDate);
                    bundle.putString("endDate", strEndDate);

                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Customer));
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
            case R.id.btnPaynow:
                if (!actv_CustomerID.getText().toString().equals("")) {
                    bundle = new Bundle();
                    fragment = new b2infosoft.milkapp.com.Dairy.PaymentRegister.PayOrReceiveAmountFragment();
                    bundle.putString("FromWhere", "PaymentRegister");
                    bundle.putString("CustomerId", selectedId);
                    bundle.putString("unic_customer", unic_customer);
                    bundle.putString("CustomerName", selectedName);
                    bundle.putString("title", mContext.getString(R.string.Pay));
                    bundle.putString("type", "add");
                    bundle.putString("url", Constant.addPayVoucherAPI);
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Customer));
                }
                break;
            case R.id.btnCashReceive:
                if (!actv_CustomerID.getText().toString().equals("")) {
                    bundle = new Bundle();
                    fragment = new PayOrReceiveAmountFragment();
                    bundle.putString("FromWhere", "PaymentRegister");
                    bundle.putString("CustomerId", selectedId);
                    bundle.putString("unic_customer", unic_customer);
                    bundle.putString("CustomerName", selectedName);
                    bundle.putString("type", "add");
                    bundle.putString("title", mContext.getString(R.string.recieve));
                    bundle.putString("url", Constant.addRecieptVoucherAPI);
                    fragment.setArguments(bundle);
                    goNextFragmentWithBackStack(mContext, fragment);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_Customer));
                }
                break;
            case R.id.btnSubmit:
                if (!actv_CustomerID.getText().toString().equals("") && !tvStartDate.getText().toString().equals("")
                        && !tvEndDate.getText().toString().equals("") && !tvFathername.getText().toString().equals("")) {
                    strFromDate = tvStartDate.getText().toString();
                    strEndDate = tvEndDate.getText().toString();
                    getTransactionList();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                }
                break;

            case R.id.tvFathername:
                fragment = new CustomerListDialogFragment(userGroupId, this);
                FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
                ft.add(R.id.dairy_container, fragment);
                ft.addToBackStack(null);
                ft.commit();

                break;
        }
    }

    public void setCustomerList(final ArrayList<CustomerListPojo> customerList) {
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        tvFathername = view.findViewById(R.id.tvFathername);

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
                    selectedName = "";
                    tvFathername.setText("");
                    unic_customer = "";

                } else {
                    selectedName = "";
                    selectedId = "";
                    unic_customer = "";

                    for (int i = 0; i < customerList.size(); i++) {
                        if (actid.toString().equals(customerList.get(i).unic_customer)) {

                            if (customerList.get(i).user_group_id.equals(userGroupId)) {
                                selectedId = customerList.get(i).id;
                                selectedName = customerList.get(i).name;
                                selectedFatherName = customerList.get(i).father_name;
                                unic_customer = customerList.get(i).unic_customer;
                                System.out.println("strSelectedId===>>>>" + selectedId + " " + selectedFatherName + " " + selectedName);
                                tvFathername.setText(customerList.get(i).name + " s/o " + customerList.get(i).father_name);


                            }

                        }
                    }

                }
            }
        });
    }

    public void getDate(final String from) {
        final Calendar c2 = Calendar.getInstance();
        mYear = c2.get(Calendar.YEAR);
        mMonth = c2.get(Calendar.MONTH);
        mDay = c2.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog2 = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        ArrayList<String> monthList = getMonthList();
                        String month = "";
                        for (int i = 0; i < monthList.size(); i++) {
                            if (monthOfYear == i) {
                                month = monthList.get(i);
                            }
                        }
                        String day = "";
                        System.out.println("Month====>>>" + month);
                        day = checkDigit(dayOfMonth);
                        String formattedDate = day + "-" + month + "-" + year;
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

    public void initRecyclerView() {

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new PaymentUserTransectionsItemAdapter(mContext, transactionList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        remain = totalCredit - totalDebit;
        tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalCredit));
        tvPaidAmount.setText(mContext.getString(R.string.Total_Debit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalDebit));
        tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " " + mContext.getString(R.string.Rs) + " \n" + String.format("%.2f", remain)));

    }

    public void getTransactionList() {
        transactionList = new ArrayList<>();
        totalCredit = 0;
        totalDebit = 0;
        remain = 0;
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        BaseImageUrl = jsonObject.getString("path");

                        for (int i = 0; i < mainJsonArray.length(); i++) {
                            JSONObject jsobj = mainJsonArray.getJSONObject(i);

                            String trnsDate = dateDDMMYY(jsobj.getString("date"));
                            if (jsobj.getDouble("cr") != 0 || jsobj.getDouble("dr") != 0) {
                                transactionList.add(new BeanUserTransaction(
                                        jsobj.getString("id"), jsobj.getString("party_name"), trnsDate,
                                        jsobj.getString("voucher_type"), jsobj.getString("voucher_no"),
                                        jsobj.getString("particular"), jsobj.getString("signature_image"),
                                        jsobj.getDouble("dr"), jsobj.getDouble("cr")));
                                totalCredit = totalCredit + jsobj.getDouble("cr");
                                totalDebit = totalDebit + jsobj.getDouble("dr");
                            }
                        }
                    }

                    initRecyclerView();
                } catch (JSONException e) {
                    e.printStackTrace();
                    initRecyclerView();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_id", selectedId)
                .addEncoded("type", type)
                .addEncoded("from_date", strFromDate)
                .addEncoded("to_date", strEndDate).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getUserTransactionAPI);
    }

    public void getCustomerList() {
        ArrayList<CustomerListPojo> mCustomerList = new ArrayList<>();
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);
        mCustomerList = databaseHandler.getCustomerListByGroupId(userGroupId);
        System.out.println("customerList= in List POJO=>>" + mCustomerList.size());

        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    @Override
    public void OnFragmentBackPressListener() {

        getActivity().onBackPressed();

    }

    @Override
    public void onClickUser(CustomerListPojo customerList) {
        if (actv_CustomerID != null) {
            actv_CustomerID.setText(customerList.getUnic_customer());
            selectedId = customerList.getId();
            selectedName = customerList.getName();
            unic_customer = customerList.getUnic_customer();

            if (strFromDate.length() > 0 && strEndDate.length() > 0) {
                tvStartDate.setText(strFromDate);
                tvEndDate.setText(strEndDate);
                if (tvEndDate.getText().toString().length() > 0 && tvStartDate.getText().toString().length() > 0) {
                    getTransactionList();
                }
            }
        }
    }
}