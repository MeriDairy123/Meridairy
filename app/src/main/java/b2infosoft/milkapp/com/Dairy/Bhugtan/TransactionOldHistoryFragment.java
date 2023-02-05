package b2infosoft.milkapp.com.Dairy.Bhugtan;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
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
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import b2infosoft.milkapp.com.Dairy.Bhugtan.Adapter.TransectionsRowAdapter;
import b2infosoft.milkapp.com.Dairy.Customer.BuyerSellerCustomerListFragment;
import b2infosoft.milkapp.com.Dairy.Customer.CustomerSallerListFragment;
import b2infosoft.milkapp.com.Dairy.Customer.SaleMilkCustomerListFragment;
import b2infosoft.milkapp.com.Dairy.Product.SaleItemFragment;
import b2infosoft.milkapp.com.Database.DatabaseHandler;
import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Model.CustomerListPojo;
import b2infosoft.milkapp.com.Model.TransectionListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintTransactionReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere;
import static b2infosoft.milkapp.com.appglobal.Constant.FromWhere2;
import static b2infosoft.milkapp.com.appglobal.Constant.receiveAmountAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.goNextFragmentReplace;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class TransactionOldHistoryFragment extends Fragment implements
        View.OnClickListener, View.OnKeyListener, FragmentBackPressListener {
    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title, tvStartDate, tvEndDate;
    ImageView imgStartDate, imgEndDate, imgPrint;
    TransectionsRowAdapter transectionsRowAdapter;
    RecyclerView recyclerView;
    AutoCompleteTextView actv_CustomerID;
    TextView et_Cname_fathername, tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    Button btnSaleItem, btnPaynow, btnCashReceive;
    String formattedDate = "", previousMonthYear = "";
    ArrayList<TransectionListPojo> transactionList = new ArrayList<>();
    Dialog openDialog;
    EditText edtAmtType, edtAmount;
    SessionManager sessionManager;
    Button btnSubmit;
    String selectedName = "", selectedFatherName = "", dairy_id = "", selectedId = "", unic_customer = "";

    String Pdfcreted = "false";
    int Totcount = 0, count = 0;
    ProgressDialog progressDialog = null;
    Document document;
    double totalCredit = 0.0, totalDebit = 0.0, remain = 0.0;
    String remainAmt = "";
    Fragment fragment = null;
    Bundle bundle = null;
    View view;
    SearchableSpinner spinUser;
    private int mYear, mMonth, mDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_transaction_list, container, false);

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
        sessionManager = new SessionManager(mContext);
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
        spinUser = view.findViewById(R.id.spinUser);
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);
        et_Cname_fathername = view.findViewById(R.id.tvFathername);

        bundle = getArguments();
        if (bundle != null) {
            selectedId = nullCheckFunction(bundle.getString("CustomerId"));
            selectedName = nullCheckFunction(bundle.getString("CustomerName"));
            selectedFatherName = nullCheckFunction(bundle.getString("CustomerFatherName"));

            if (!selectedId.equals("") && !selectedName.equals("") && !selectedFatherName.equals("")) {
                actv_CustomerID.setEnabled(true);
                actv_CustomerID.setFocusable(true);
                actv_CustomerID.setFocusable(true);
                unic_customer = nullCheckFunction(bundle.getString("unic_customer"));
                actv_CustomerID.setText(unic_customer);
                et_Cname_fathername.setText(selectedName + " s/o " + selectedFatherName);
                formattedDate = getSimpleDate();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -1);
                previousMonthYear = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
                System.out.println("previousMonthYear====" + previousMonthYear);
                if (bundle.getString("startDate") != null && bundle.getString("endDate") != null) {
                    if (!bundle.getString("startDate").equals("") && !bundle.getString("endDate").equals("")) {
                        previousMonthYear = bundle.getString("startDate");
                        formattedDate = bundle.getString("endDate");
                        tvStartDate.setText(previousMonthYear);
                        tvEndDate.setText(formattedDate);
                        getTransactionList();
                    }
                } else {
                    tvStartDate.setText(previousMonthYear);
                    tvEndDate.setText(formattedDate);
                    getTransactionList();
                }
            } else {
                actv_CustomerID.setText("");
                et_Cname_fathername.setText("");
                getCustomerList();
            }
        }
        actv_CustomerID.setEnabled(true);
        actv_CustomerID.setFocusable(true);
        et_Cname_fathername.setEnabled(true);
        btnPaynow = view.findViewById(R.id.btnPaynow);
        btnSaleItem = view.findViewById(R.id.btnSaleItem);
        btnCashReceive = view.findViewById(R.id.btnCashReceive);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        btnCashReceive.setOnClickListener(this);
        btnSaleItem.setOnClickListener(this);
        btnPaynow.setOnClickListener(this);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        et_Cname_fathername.setOnClickListener(this);
        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvPaidAmount = view.findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = view.findViewById(R.id.tvRemaningAmount);

        if (FromWhere.equals("BuyerBhugtan")) {
            btnPaynow.setVisibility(View.GONE);
            // btnPaynow.setText("Print");
            btnSaleItem.setVisibility(View.GONE);
        }
        getCustomerList();

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
                        String startDate = tvStartDate.getText().toString().trim();
                        String endDate = tvEndDate.getText().toString().trim();
                        PrintTransactionReciept(getContext(), transactionList, startDate,
                                endDate, selectedName, unic_customer, totalCredit, totalDebit, remain);

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
//transactionList
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
                        createPDF();
                        // }
                    }
                }
            }, 3000);

        } else {
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.No_Data_Found));
        }
    }

    private void createPDF() {
        String FILE = "";
        if (FromWhere.equals("BuyerBhugtan")) {
            FILE = Environment.getExternalStorageDirectory().toString()
                    + "/MeriDairy/Buyer Transaction/" + unic_customer + "_" + selectedName + System.currentTimeMillis() + ".pdf";
            document = new Document(PageSize.A4);
            // Create Directory in External Storage
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/MeriDairy/Buyer Transaction/");
            myDir.mkdirs();
        } else {
            FILE = Environment.getExternalStorageDirectory().toString()
                    + "/MeriDairy/Seller Transaction/" + unic_customer + "_" + selectedName + System.currentTimeMillis() + ".pdf";
            document = new Document(PageSize.A4);
            // Create Directory in External Storage
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/MeriDairy/Seller Transaction/");
            myDir.mkdirs();
        }
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
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
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD)));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    private void addTitlePage(Document document) {
        try {
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
            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
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
            tableHead.addCell(getCell("ID" + " :- " + unic_customer, PdfPCell.ALIGN_LEFT));
            tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT));
            tableHead.addCell(getCell("Name" + ":- " + selectedName, PdfPCell.ALIGN_LEFT));
            tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
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

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4});
            table.setSpacingBefore(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Title", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Credit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Debit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);


            for (int i = 0; i < transactionList.size(); i++) {
                double total = 0;
                table.addCell(new Phrase(transactionList.get(i).entry_date + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).products_name + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

                if (transactionList.get(i).type.equals("credit") || transactionList.get(i).type.equals("receive")) {
                    table.addCell(new Phrase(transactionList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                } else {
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase(transactionList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                }


            }
            document.add(table);
            PdfPTable pTable = new PdfPTable(3);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pTable.addCell(new Phrase("Total Credit" + ": " + String.format("%.2f", totalCredit), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            pTable.addCell(new Phrase("Total Debit" + ": " + String.format("%.2f", totalDebit), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            pTable.addCell(new Phrase("Remain Rs" + ": " + String.format("%.2f", remain), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
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
            intent.setDataAndType(selectedUri, "application/pdf");
            if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                startActivity(intent);
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaleItem:
                if (!actv_CustomerID.getText().toString().equals("")) {

                    fragment = new SaleItemFragment();
                    bundle = new Bundle();
                    bundle.putString("unic_customer", unic_customer);
                    bundle.putString("CustomerId", selectedId);
                    bundle.putString("CustomerName", selectedName);

                    bundle.putString("CustomerFatherName", selectedFatherName);
                    bundle.putString("type", "debit");

                    fragment.setArguments(bundle);
                    goNextFragmentReplace(mContext, fragment);
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
                if (btnPaynow.getText().toString().equals("Print")) {
                } else {
                    remainAmt = String.format("%.2f", remain);
                    fragment = new PayAmountFragment();
                    bundle = new Bundle();
                    bundle.putString("CustomerId", selectedId);
                    bundle.putString("CustomerName", selectedName);
                    bundle.putString("type", "debit");
                    bundle.putString("unic_customer", unic_customer);
                    bundle.putString("CustomerFatherName", selectedFatherName);
                    bundle.putString("fullAmt", remainAmt);
                    bundle.putString("fromwhere", "TransactionHistory");
                    fragment.setArguments(bundle);
                    goNextFragmentReplace(mContext, fragment);
                }
                break;
            case R.id.btnCashReceive:
                openPayNowDialog("Recieve");
                break;
            case R.id.btnSubmit:
                if (!actv_CustomerID.getText().toString().equals("") && !tvStartDate.getText().toString().equals("")
                        && !tvEndDate.getText().toString().equals("") && !et_Cname_fathername.getText().toString().equals("")) {
                    previousMonthYear = tvStartDate.getText().toString();
                    formattedDate = tvEndDate.getText().toString();
                    getTransactionList();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                }
                break;
            case R.id.tvFathername:
                if (FromWhere.equals("CustomerSallerListFragment") || FromWhere.equals("Bhugtan")
                ) {
                    getActivity().onBackPressed();
                } else if (FromWhere.equals("Dashboard")) {
                    fragment = new CustomerSallerListFragment();
                    goNextFragmentReplace(mContext, fragment);
                } else if (FromWhere.equals("btnCustomer")) {
                    fragment = new BuyerSellerCustomerListFragment();
                    goNextFragmentReplace(mContext, fragment);
                } else {
                    FromWhere2 = "TransactionBuyer";
                    fragment = new SaleMilkCustomerListFragment();
                    goNextFragmentReplace(mContext, fragment);
                }
                break;
        }
    }

    public void setCustomerList(final ArrayList<CustomerListPojo> customerList) {
        actv_CustomerID = view.findViewById(R.id.actv_CustomerID);
        et_Cname_fathername = view.findViewById(R.id.tvFathername);

        actv_CustomerID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable actid) {
                transactionList = new ArrayList<>();
                setTransactionList(transactionList);
                if (actid.toString().equals("")) {
                    selectedId = "";
                    selectedName = "";
                    et_Cname_fathername.setText("");
                    unic_customer = "";

                } else {
                    System.out.println("actid====>>>" + actid.toString());
                    selectedName = "";
                    selectedId = "";
                    unic_customer = "";

                    for (int i = 0; i < customerList.size(); i++) {
                        if (actid.toString().equals(customerList.get(i).unic_customer)) {
                            if (!FromWhere.equals("BuyerBhugtan")) {
                                if (customerList.get(i).user_group_id.equals("3")) {
                                    selectedId = customerList.get(i).id;
                                    selectedName = customerList.get(i).name;
                                    selectedFatherName = customerList.get(i).father_name;
                                    unic_customer = customerList.get(i).unic_customer;
                                    System.out.println("strSelectedId===>>>>" + selectedId + " " + selectedFatherName + " " + selectedName);
                                    et_Cname_fathername.setText(customerList.get(i).name + " s/o " + customerList.get(i).father_name);
                                    getTransactionList();

                                }
                            } else if (customerList.get(i).user_group_id.equals("4")) {
                                {
                                    selectedId = customerList.get(i).id;
                                    selectedName = customerList.get(i).name;
                                    selectedFatherName = customerList.get(i).father_name;
                                    unic_customer = customerList.get(i).unic_customer;
                                    System.out.println("strSelectedId>>>>" + selectedId + " " + selectedFatherName + " " + selectedName);
                                    et_Cname_fathername.setText(customerList.get(i).name + " s/o " + customerList.get(i).father_name);
                                    getTransactionList();
                                }
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
                        //.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
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

    private void openPayNowDialog(final String type) {
        openDialog = new Dialog(mContext);
        openDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        openDialog.setContentView(R.layout.dialog_paynow);
        openDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Button btnPayAmt;
        edtAmtType = openDialog.findViewById(R.id.edtAmtType);
        edtAmount = openDialog.findViewById(R.id.edtAmount);
        btnPayAmt = openDialog.findViewById(R.id.btnPayAmt);
        if (type.equalsIgnoreCase("Recieve")) {
            btnPayAmt.setText(mContext.getString(R.string.CASH_RECEIVE));
        } else {
            btnPayAmt.setText(mContext.getString(R.string.Pay));

        }

        btnPayAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!actv_CustomerID.getText().toString().equals("") && !et_Cname_fathername.getText().toString().equals("") && !edtAmount.getText().toString().equals("") && !edtAmtType.getText().toString().equals("")) {
                    if (!type.equals("Pay")) {
                        receiveAmt();
                    }
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Do_Amount_Type_And_Amount_to_pay_amount));
                }
            }
        });
        openDialog.show();
    }

    private void receiveAmt() {
        // receive-payment?dairy_id=1&customer_id=2&pay_price=50
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        openDialog.dismiss();
                        fragment = new SignatureFragment();
                        bundle = new Bundle();
                        bundle.putString("CustomerId", selectedId);
                        bundle.putString("CustomerName", selectedName);
                        bundle.putString("CustomerFatherName", selectedFatherName);
                        bundle.putString("transactionID", mainObject.getString("id"));
                        bundle.putString("type", "receive");
                        bundle.putString("unic_customer", unic_customer);
                        bundle.putString("fromwhere", "TransactionHistory");
                        fragment.setArguments(bundle);
                        goNextFragmentReplace(mContext, fragment);

                    } else {
                        UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Amount_Paying_Fail));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID))
                .addEncoded("pay_price", edtAmount.getText().toString())
                .addEncoded("customer_id", selectedId).build();
        caller.addRequestBody(body);
        caller.execute(receiveAmountAPI);
    }

    public void setTransactionList(ArrayList<TransectionListPojo> listPojos) {

        recyclerView = view.findViewById(R.id.recyclerView);

        if (listPojos.isEmpty()) {
            recyclerView.setVisibility(View.INVISIBLE);
            tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + "\n" + mContext.getString(R.string.Rs) + " 0.00");
            tvPaidAmount.setText((mContext.getString(R.string.Total_Debit) + " \n" + mContext.getString(R.string.Rs) + " 0.00"));
            tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " \n" + mContext.getString(R.string.Rs) + "  0.00"));
        } else {
            transactionList = listPojos;

            recyclerView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            transectionsRowAdapter = new TransectionsRowAdapter(mContext, listPojos);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(transectionsRowAdapter);
            transectionsRowAdapter.notifyDataSetChanged();
            totalCredit = 0;
            totalDebit = 0;
            remain = 0;
            for (int i = 0; i < listPojos.size(); i++) {
                if (listPojos.get(i).type.equals("credit") || listPojos.get(i).type.equals("receive")) {
                    totalCredit = totalCredit + Double.parseDouble(listPojos.get(i).total_price);
                } else {
                    totalDebit = totalDebit + Double.parseDouble(listPojos.get(i).total_price);
                }
            }
            if (FromWhere.equals("BuyerBhugtan")) {
                remain = totalDebit - totalCredit;
            } else {
                remain = totalCredit - totalDebit;
            }
            tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalCredit));
            tvPaidAmount.setText(mContext.getString(R.string.Total_Debit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalDebit));
            tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " " + mContext.getString(R.string.Rs) + " \n" + String.format("%.2f", remain)));
        }
    }

    public void getTransactionList() {
        final ArrayList<TransectionListPojo> transectionListPojos = new ArrayList<>();
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject jsonObject1 = mainJsonArray.getJSONObject(i);
                        transectionListPojos.add(new TransectionListPojo(jsonObject1.getString("milk_entries_id"), jsonObject1.getString("entry_date")
                                , jsonObject1.getString("products_name"), jsonObject1.getString("total_price"), jsonObject1.getString("type"), jsonObject1.getString("transactions_ids"), jsonObject1.getString("entry_date_str")
                                , jsonObject1.getString("created_time"), jsonObject1.getString("total_bonus")));
                    }

                    setTransactionList(transectionListPojos);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_id", selectedId)
                .addEncoded("entry_date_from", previousMonthYear)
                .addEncoded("entry_date_to", formattedDate).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getTransaction);
    }

    public void getCustomerList() {
        DatabaseHandler databaseHandler = DatabaseHandler.getDbHelper(mContext);
        ArrayList<CustomerListPojo> mCustomerList = databaseHandler.getCustomerList();
        if (!mCustomerList.isEmpty()) {
            setCustomerList(mCustomerList);
        }
    }

    @Override
    public void OnFragmentBackPressListener() {

        System.out.println("Transaction back Press FromWhere= =>>" + FromWhere);

        if (FromWhere.equals("Dashboard")) {
            fragment = new CustomerSallerListFragment();
            goNextFragmentReplace(mContext, fragment);
        } else if (FromWhere.equals("btnCustomer")) {
            fragment = new BuyerSellerCustomerListFragment();
            goNextFragmentReplace(mContext, fragment);
        } else {
            requireActivity().onBackPressed();
        }
    }
}