package b2infosoft.milkapp.com.Dairy.Transactions;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.List;
import java.util.Locale;

import b2infosoft.milkapp.com.Interface.FragmentBackPressListener;
import b2infosoft.milkapp.com.Interface.OnDownLoadListener;
import b2infosoft.milkapp.com.Interface.OnTransactionListener;
import b2infosoft.milkapp.com.Model.BeanCustomerList;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.DownloadFileFromURL;
import b2infosoft.milkapp.com.useful.PDFUtills;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintUserTransactionReceipt;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.enableBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.Model.BeanUserTransaction.getPaymentTransactionList;
import static b2infosoft.milkapp.com.appglobal.Constant.getAllCustomer;
import static b2infosoft.milkapp.com.sharedPreference.SessionManager.KEY_UserID;
import static b2infosoft.milkapp.com.useful.PDFUtills.createPDFFile;
import static b2infosoft.milkapp.com.useful.UtilityMethod.checkDigit;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithButton;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;

public class UserTransactionFragment extends Fragment implements
        View.OnClickListener, View.OnKeyListener, FragmentBackPressListener, OnDownLoadListener, OnTransactionListener {
    Context mContext;
    Toolbar toolbar;
    TextView tvStartDate, tvEndDate;
    ImageView imgStartDate, imgEndDate, imgPrint;
    UserTransectionsItemAdapter adapter;
    RecyclerView recyclerView;

    TextView tvTotalCredit, tvPaidAmount, tvRemaningAmount;
    String formattedDate = "", previousMonthYear = "", strFromDate = "", strEndDate = "";
    ArrayList<BeanUserTransaction> transactionList = new ArrayList<>();
    ArrayList<BeanCustomerList> userList = new ArrayList<>();
    DownloadFileFromURL downloadFileFromURL;
    SessionManager sessionManager;
    Button btnSubmit;
    String fileUrl = "", userGroupId = "", strType = "", selectedName = "", userMobileNo = "", dairy_id = "", selectedId = "", unic_customer = "";

    String Pdfcreted = "false";
    int Totcount = 0, count = 0;
    ProgressDialog progressDialog = null;
    Document document;
    double totalCredit = 0.0, totalDebit = 0.0, remain = 0.0;

    Bundle bundle = null;
    View view, layoutSpin;
    SearchableSpinner spinUser;
    private int mYear, mMonth, mDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_transactions, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
        initView();
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(this);
        return view;
    }

    private void initView() {
        sessionManager = new SessionManager(mContext);
        dairy_id = sessionManager.getValueSesion(SessionManager.KEY_UserID);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        toolbar = view.findViewById(R.id.toolbar);
        imgPrint = view.findViewById(R.id.imgPrintRecp);

        toolbar.setVisibility(View.GONE);
        imgPrint.setVisibility(View.VISIBLE);

        layoutSpin = view.findViewById(R.id.layoutSpin);
        spinUser = view.findViewById(R.id.spinUser);
        tvStartDate = view.findViewById(R.id.tvStartDate);
        tvEndDate = view.findViewById(R.id.tvEndDate);
        imgStartDate = view.findViewById(R.id.imgStartDate);
        imgEndDate = view.findViewById(R.id.imgEndDate);

        bundle = getArguments();
        if (bundle != null) {
            selectedId = nullCheckFunction(bundle.getString("title"));
            strType = nullCheckFunction(bundle.getString("type"));
            if (strType.equalsIgnoreCase("my_transaction")) {
                layoutSpin.setVisibility(View.GONE);
                userMobileNo = sessionManager.getValueSesion(SessionManager.KEY_Mobile);
                selectedName = sessionManager.getValueSesion(SessionManager.KEY_Name);
                selectedId = dairy_id;
                unic_customer = selectedId;
               getUserTransaction();
            } else {
                layoutSpin.setVisibility(View.VISIBLE);
                getUser();
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        strFromDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(cal.getTime());
        strEndDate = getSimpleDate();
        tvStartDate.setText(strFromDate);
        tvEndDate.setText(strEndDate);

        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);

        imgStartDate.setOnClickListener(this);
        imgEndDate.setOnClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvTotalCredit = view.findViewById(R.id.tvTotalCredit);
        tvPaidAmount = view.findViewById(R.id.tvPaidAmount);
        tvRemaningAmount = view.findViewById(R.id.tvRemaningAmount);


        imgPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printReceipt();

            }
        });

    }

    public void printReceipt() {
        if (!transactionList.isEmpty()) {
            DialogPrintOrPdf();
        } else {
            showAlertWithButton(mContext, mContext.getString(R.string.No_Data_Found));
        }
    }

    public void getUser() {

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Loading Customer List...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    List<String> spinItem = new ArrayList<>();
                    userList = new ArrayList<>();
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        if (mainJsonArray.length() > 0) {
                            for (int i = 0; i < mainJsonArray.length(); i++) {
                                JSONObject object = mainJsonArray.getJSONObject(i);
                                spinItem.add(object.getString("unic_customer") + ". " + object.getString("name"));
                                userList.add(new BeanCustomerList(object.getString("id"), object.getString("name")
                                        , object.getString("unic_customer"), object.getString("user_group_id")));
                            }
                        }
                    }
                    initUser(spinItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", sessionManager.getValueSesion(KEY_UserID))
                .build();
        caller.addRequestBody(body);

        caller.execute(getAllCustomer);
    }

    private void initUser(List<String> spinItem) {


        ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, spinItem);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinUser.setAdapter(spinAdapter);

        spinUser.setTitle(mContext.getString(R.string.Select) + " " + mContext.getString(R.string.Customer));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            spinUser.setAutofillHints(mContext.getString(R.string.Search_Here) + " " + mContext.getString(R.string.Customer));
        }
        spinUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedId = userList.get(position).getId();
                unic_customer = userList.get(position).getUnic_customer();
                selectedName = userList.get(position).getName();
                userGroupId = userList.get(position).getUser_group_id();
                getUserTransaction();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
    }
    private void getUserTransaction() {
        getPaymentTransactionList(mContext,dairy_id,selectedId,strType,strFromDate,strEndDate,this);
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


    public void DialogPrintOrPdf() {
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
        // if button is clicked, close the custom dialog
        tv_downloadPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                try {
                    printPdf();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                        String billTitle = "  Transactions ";
                        if (strType.equalsIgnoreCase("my_transaction")) {
                            billTitle = "My Transactions";
                        }
                        PrintUserTransactionReceipt(getContext(), billTitle, transactionList, strFromDate,
                                strEndDate, selectedName, unic_customer, totalCredit, totalDebit, remain);

                    }
                } else {
                    showAlertWithTitle(mContext.getString(R.string.Please_enable_Bluetooth_device), mContext);
                    enableBluetooth(mContext);
                    dialogBluetooth(mContext);
                }
            }
        });

        dialog.show();
    }


    private void printPdf()throws IOException {
        if (transactionList.size() > 0) {
            Totcount = transactionList.size();
            Pdfcreted = "true";
            downloadFileFromURL = new DownloadFileFromURL(this.mContext, this);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    String folder;

                    String filePath = unic_customer + "_" + selectedName + System.currentTimeMillis() + ".pdf";
                    if (userGroupId.equals("4")) {
                        folder = "MeriDairy/Buyer Transaction/";
                    } else {
                        folder = userGroupId.equals("3") ? "MeriDairy/Seller Transaction/" : "MeriDairy/My Transaction/";
                    }
                    try {
                        downloadFileFromURL.initURL(folder, filePath);
                        downloadFileFromURL.execute(fileUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }, 0);


        } else {
            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.No_Data_Found));
        }
    }

    private void createPDF() {

        String filePath = unic_customer + "_" + selectedName + System.currentTimeMillis() + ".pdf";

        String folderName="MeriDairy/My Transaction/";
        if (userGroupId.equals("4")) {
            folderName="MeriDairy/Buyer Transaction/";
        }else if (userGroupId.equals("4"))   {
            folderName="MeriDairy/Seller Transaction/";
        }

        try {
            File pfdFile = createPDFFile(mContext,folderName,filePath);

            document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pfdFile.getAbsolutePath()));
            document.open();
            addTitlePage(document);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            System.out.println("file not Found>>>>>"+ e.getMessage());
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            System.out.println("DocumentException===>>>>>"+ e.getMessage());
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

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4, 4, 4});
            table.setSpacingBefore(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Title", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Payment Type", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Voucher No.", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Credit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Debit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);

            System.out.println("Transaction List>>>"+transactionList.size());

            for (int i = 0; i < transactionList.size(); i++) {

                table.addCell(new Phrase(transactionList.get(i).getTransaction_date() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getParty_name() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_type() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_no() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));


                if (transactionList.get(i).getCr() > 0) {
                    table.addCell(new Phrase(transactionList.get(i).getCr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                } else {
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase(transactionList.get(i).getDr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
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
    private void addTitlePageOld(Document document) {
        try {
            System.out.println("Transaction List>>>"+transactionList.size());
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
            prDate.add(strFromDate + "   To   " + strEndDate);
            prDate.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(prDate);

            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4, 4, 4,4});
            table.setSpacingBefore(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Title", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Payment Type", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Voucher No.", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Description", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Credit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Debit", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);


            for (int i = 0; i < transactionList.size(); i++) {
                table.addCell(new Phrase(transactionList.get(i).getTransaction_date() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getParty_name() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_type() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getVoucher_no() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(transactionList.get(i).getDescription() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));


                if (transactionList.get(i).getCr() > 0) {
                    table.addCell(new Phrase(transactionList.get(i).getCr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                } else {
                    table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                    table.addCell(new Phrase(transactionList.get(i).getDr() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
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

            case R.id.imgStartDate:
            case R.id.tvStartDate:
                getDate("StartDate");
                break;
            case R.id.imgEndDate:
            case R.id.tvEndDate:
                getDate("EndDate");

                break;


            case R.id.btnSubmit:
                if (!selectedId.equals("") && !tvStartDate.getText().toString().equals("")
                        && !tvEndDate.getText().toString().equals("")) {
                    previousMonthYear = tvStartDate.getText().toString();
                    formattedDate = tvEndDate.getText().toString();
                    getUserTransaction();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Enter_All_Field));
                }
                break;

        }
    }


    public void initRecyclerView() {

        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        adapter = new UserTransectionsItemAdapter(mContext, transactionList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        remain = totalCredit - totalDebit;
        tvTotalCredit.setText(mContext.getString(R.string.Total_Credit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalCredit));
        tvPaidAmount.setText(mContext.getString(R.string.Total_Debit) + " " + mContext.getString(R.string.Rs) + "\n" + String.format("%.2f", totalDebit));
        tvRemaningAmount.setText((mContext.getString(R.string.Remaining) + " " + mContext.getString(R.string.Rs) + " \n" + String.format("%.2f", remain)));

    }





    @Override
    public void OnFragmentBackPressListener() {
        getActivity().onBackPressed();

    }

    public void getDate(final String from) {
        Calendar c2 = Calendar.getInstance();
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
                        day = checkDigit(dayOfMonth);

                        formattedDate = day + "-" + month + "-" + year;
                        if (from.equals("StartDate")) {
                            strFromDate = formattedDate;
                            tvStartDate.setText(strFromDate);
                        } else {
                            strEndDate = formattedDate;
                            tvEndDate.setText(strEndDate);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    @Override
    public void onDownLoadComplete(String folderName, File file) {
        UtilityMethod.openPdfFile(this.mContext, file);
    }

    @Override
    public void setTransactionList(ArrayList<BeanUserTransaction> transactionList, double totalCredit, double totalDebit, String fileUrl) {
        this.transactionList=transactionList;
        this.totalCredit=totalCredit;
        this.totalDebit=totalDebit;
        this.fileUrl=fileUrl;
        initRecyclerView();
    }
}