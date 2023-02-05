package b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ListIterator;

import b2infosoft.milkapp.com.Dairy.SellMilk.Bhugtan.Adapter.BuyerBhugtanListAdapter;
import b2infosoft.milkapp.com.Interface.BuyerCustomerListInterface;
import b2infosoft.milkapp.com.Model.BuyerCustomerDataListPojo;
import b2infosoft.milkapp.com.Model.ListPojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.PrintMonthReciept;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.dialogBluetooth;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.isBluetoothHeadsetConnected;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mDevice;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mInputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mOutputStream;
import static b2infosoft.milkapp.com.BluetoothPrinter.BluetoothClass.mSocket;
import static b2infosoft.milkapp.com.Dairy.MainActivity.drawer;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerCustomerDataListAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.getBuyerCustomerDataListForPDFAPI;
import static b2infosoft.milkapp.com.appglobal.Constant.sendMultyMessageAPI;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getSimpleDate;
import static b2infosoft.milkapp.com.useful.UtilityMethod.nullCheckFunction;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showAlertWithTitle;
import static b2infosoft.milkapp.com.useful.UtilityMethod.showToast;
import static b2infosoft.milkapp.com.webservice.NetworkTask.JSONMediaType;

public class BuyerBhugtanFragment extends Fragment implements BuyerCustomerListInterface,
        View.OnClickListener {

    Context mContext;
    Toolbar toolbar;
    TextView toolbar_title;
    EditText et_Search;
    SessionManager sessionManager;

    String startDate = "", endDate = "";
    TextView tvPrintPdfAndSend, tvPrint, tvSendMessage;
    ArrayList<BuyerCustomerDataListPojo> bhugatanList;
    ArrayList<BuyerCustomerDataListPojo> tenDaysMainList;
    RecyclerView recycler_transactionList;
    BuyerBhugtanListAdapter milkHistoryAdapter;
    Document document;
    String Pdfcreted = "false";
    ProgressDialog progressDialog = null;
    ProgressBar ProgressBar01;
    int Totcount = 0, count = 0;
    ImageView imgLeft, imgRight;
    TextView tvMonth;
    String printTypeDialog = "";
    String dairyid = "";
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_bhugtan, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        bhugatanList = new ArrayList<>();
        tenDaysMainList = new ArrayList<>();
        dairyid = sessionManager.getValueSesion(SessionManager.KEY_UserID);

        initView();
        return view;
    }

    private void initView() {

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        toolbar_title.setText(mContext.getString(R.string.BUYER_BHUGTAN));
        et_Search = view.findViewById(R.id.et_Search);
        recycler_transactionList = view.findViewById(R.id.recycler_transactionList);
        tvPrintPdfAndSend = view.findViewById(R.id.tvPrintPdfAndSend);
        tvPrint = view.findViewById(R.id.tvPrint);
        tvSendMessage = view.findViewById(R.id.tvSendMessage);
        tvMonth = view.findViewById(R.id.tvMonth);
        ProgressBar01 = view.findViewById(R.id.ProgressBar01);

        imgLeft = view.findViewById(R.id.imgLeft);
        imgRight = view.findViewById(R.id.imgRight);
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DAY_OF_MONTH);

        startDate = getSimpleDate();
        tvPrintPdfAndSend.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
        tvSendMessage.setOnClickListener(this);
        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        getBuyerMilkData(mContext, startDate, "prv");

        toolbarManage();
    }

    public String removeLastChar(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPrintPdfAndSend:
                Constant.BtnType = "Print-Send";
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < bhugatanList.size(); i++) {
                    if (bhugatanList.get(i).isClicked.equals("true")) {
                        stringBuilder.append(bhugatanList.get(i).id + ",");
                    }
                }

                String customerID = removeLastChar(stringBuilder.toString());
                System.out.println("customerID====>>>" + customerID);
                if (!customerID.equals("")) {
                    getPdfList(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF_and_Send_Message));
                }


                break;
            case R.id.tvPrint:
                DialogPrint();
                break;
            case R.id.tvSendMessage:
                Constant.BtnType = "Send";
                stringBuilder = new StringBuilder();
                for (int i = 0; i < bhugatanList.size(); i++) {
                    if (bhugatanList.get(i).isClicked.equals("true")) {
                        stringBuilder.append(bhugatanList.get(i).id + ",");
                    }
                }
                customerID = removeLastChar(stringBuilder.toString());
                System.out.println("customerID====>>>" + customerID);
                if (!customerID.equals("")) {
                    sendMessage("SendMsg", new ArrayList<BuyerCustomerDataListPojo>());
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF_and_Send_Message));
                }


                break;
            case R.id.imgLeft:

                getBuyerMilkData(mContext, startDate, "prv");
                break;
            case R.id.imgRight:
                getBuyerMilkData(mContext, startDate, "nxt");
                break;
        }
    }


    public void addTextListener() {

        et_Search.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {
                String querText = query.toString().toLowerCase();
                milkHistoryAdapter.filterSearch(querText);

            }
        });


    }


    public void setPdfDataList() {

        if (Constant.BtnType.equals("Print")) {
            if (tenDaysMainList.size() > 0) {
                Totcount = tenDaysMainList.size();
                for (int i = 0; i < tenDaysMainList.size(); i++) {
                    final int finalI = i;
                    final ArrayList<BuyerCustomerDataListPojo> pdfList = tenDaysMainList.get(i).pdfDataList;
                    if (printTypeDialog.equalsIgnoreCase("pdf")) {
                        Pdfcreted = "true";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (Pdfcreted.equals("true")) {
                                    if (!progressDialog.isShowing()) {
                                        progressDialog.show();
                                    }
                                    createPDF(tenDaysMainList.get(finalI).avg_grnd_total_amt, tenDaysMainList.get(finalI).user_data_name,
                                            tenDaysMainList.get(finalI).user_data_unic_customer, tenDaysMainList.get(finalI).user_data_phone_number, pdfList);

                                }
                            }
                        }, 3000);


                    } else {
                        PrintMonthReciept(mContext, tenDaysMainList.get(i).user_data_name,
                                tenDaysMainList.get(i).user_data_unic_customer,
                               startDate,endDate, pdfList, tenDaysMainList.get(i).avg_grnd_total_amt);
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }


                }
            }

        } else if (Constant.BtnType.equals("Print-Send")) {
            Totcount = tenDaysMainList.size();
            for (int i = 0; i < tenDaysMainList.size(); i++) {
                sendMessage("SendPDF", tenDaysMainList);
                break;
            }

        } else if (Constant.BtnType.equals("Send")) {

        }


    }


    private void sendMessage(final String type, final ArrayList<BuyerCustomerDataListPojo> pdfDataList) {
        NetworkTask webServiceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Sending Message, Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {
                    JSONObject mainObject = new JSONObject(response);
                    if (mainObject.getString("status").equals("success")) {
                        // UtilityMethod.showAlertBox(mContext, mainObject.getString("message"));
                        showToast(mContext, mContext.getString(R.string.Message_Sent_Successfully));
                        if (type.equals("SendPDF")) {
                            if (pdfDataList.size() > 0) {
                                Totcount = pdfDataList.size();
                                count = 0;

                                for (int i = 0; i < pdfDataList.size(); i++) {
                                    Pdfcreted = "true";
                                    if (!progressDialog.isShowing()) {
                                        progressDialog.show();
                                    }
                                    final int finalI = i;
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Pdfcreted.equals("true")) {
                                                ArrayList<BuyerCustomerDataListPojo> pdfList = pdfDataList.get(finalI).pdfDataList;
                                                // for (int i = 0; i < pdfDataList.size(); i++) {
                                                createPDF(pdfDataList.get(finalI).avg_grnd_total_amt, pdfDataList.get(finalI).user_data_name, pdfDataList.get(finalI).user_data_unic_customer, pdfDataList.get(finalI).user_data_phone_number, pdfList);
                                                // }
                                            }
                                        }
                                    }, 3000);
                                }
                            }
                        }
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

                if (bhugatanList.get(i).isClicked.equals("true") && bhugatanList.get(i).url_code.length() == 0) {
                    in = "yes";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("dairy_id", sessionManager.getValueSesion(SessionManager.KEY_UserID));
                    jsonObject.put("customer_id", bhugatanList.get(i).id);
                    jsonObject.put("phone_number", bhugatanList.get(i).phone_number);
                    jsonArray.put(jsonObject);
                    System.out.println("=loop====in==>>>" + "" + in);
                }
            }
            System.out.println("in==>>>" + "" + in);
            if (in.equals("yes")) {
                JSONObject mainObject = new JSONObject();
                mainObject.put("mobile_customer_id_dairy_id", jsonArray);
                System.out.println("mainObject==>>>" + "" + mainObject.toString());
                RequestBody body = RequestBody.create(JSONMediaType, mainObject.toString());
                webServiceCaller.addRequestBody(body);
                webServiceCaller.execute(sendMultyMessageAPI);
            } else {
                if (type.equals("SendPDF")) {
                    if (pdfDataList.size() > 0) {
                        Totcount = pdfDataList.size();
                        int i = 0;
                        for (i = 0; i < pdfDataList.size(); i++) {
                            Pdfcreted = "true";
                            if (!progressDialog.isShowing()) {
                                // tvPrint.setVisibility(View.GONE);
                                // ProgressBar01.setVisibility(View.VISIBLE);
                                progressDialog.show();
                            }
                            final int finalI = i;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (Pdfcreted.equals("true")) {
                                        ArrayList<BuyerCustomerDataListPojo> pdfList = pdfDataList.get(finalI).pdfDataList;
                                        // for (int i = 0; i < pdfDataList.size(); i++) {
                                        createPDF(pdfDataList.get(finalI).avg_grnd_total_amt, pdfDataList.get(finalI).user_data_name, pdfDataList.get(finalI).user_data_unic_customer, pdfDataList.get(finalI).user_data_phone_number, pdfList);
                                        // }
                                    }
                                }
                            }, 3000);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createPDF(String total, String userName, String unic_id, String mobile_no, ArrayList<BuyerCustomerDataListPojo> mList) {
        String FILE = Environment.getExternalStorageDirectory().toString()
                + "/MeriDairy/Buyer/" + unic_id + "_" + userName + System.currentTimeMillis() + ".pdf";
        document = new Document(PageSize.A4);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/Buyer/");
        myDir.mkdirs();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();

            addTitlePage(document, unic_id, userName, mobile_no, mList, total);
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

    public void addTitlePage(Document document, String unic_id, String userName,
                             String mobile_no, ArrayList<BuyerCustomerDataListPojo> mList,
                             String grandTotal) throws DocumentException {
        // Create new Page in PDF
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
        tableHead.addCell(getCell("ID" + ":- " + unic_id, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Name" + ":- " + userName, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
        tableHead.addCell(getCell("Mobile No" + ":- " + mobile_no, PdfPCell.ALIGN_LEFT));
        tableHead.addCell(getCell("", PdfPCell.ALIGN_RIGHT));
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
        table.setWidths(new float[]{4, 4, 4, 4, 4});
        table.setSpacingBefore(4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("Date" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Morning" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Evening" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Total Amount" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.addCell(new Phrase("Signature" + "", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
        table.setHeaderRows(1);

        ArrayList<ListPojo> morningList = new ArrayList<>();
        ArrayList<ListPojo> eveningList = new ArrayList<>();
        ListIterator<BuyerCustomerDataListPojo> it = mList.listIterator();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).session.equals("Morning")) {
                morningList.add(new ListPojo(mList.get(i).for_date, mList.get(i).session, mList.get(i).entry_total_milk, mList.get(i).entry_total_price));
            } else if (mList.get(i).session.equals("Evening")) {
                eveningList.add(new ListPojo(mList.get(i).for_date, mList.get(i).session, mList.get(i).entry_total_milk, mList.get(i).entry_total_price));
            }
        }

        int maxSize = 0;

        int morningListSize = morningList.size();
        int eveningListSize = eveningList.size();

        if (morningListSize >= eveningListSize) {
            maxSize = morningListSize;
        } else {
            maxSize = eveningListSize;
        }
        double morningTotal = 0, evengTotal = 0;
        for (int i = 0; i < maxSize; i++) {
            double total = 0;
            String date = "";
            String morngMilk = "";
            String evengMilk = "";

            if (i < morningListSize) {
                date = morningList.get(i).date;
                morngMilk = morningList.get(i).milk;


                if (!morningList.get(i).price.equals("-") && !morningList.get(i).price.equals("")) {
                    total = total + Double.parseDouble(morningList.get(i).price);
                    morningTotal = morningTotal + Double.parseDouble(morningList.get(i).milk);
                }
            } else {
                morngMilk = "-";
            }
            if (i < eveningListSize) {
                date = eveningList.get(i).date;
                evengMilk = eveningList.get(i).milk;
                if (!eveningList.get(i).price.equals("-") && !eveningList.get(i).price.equals("")) {
                    total = total + Double.parseDouble(eveningList.get(i).price);
                    evengTotal = evengTotal + Double.parseDouble(eveningList.get(i).milk);

                }
            } else {
                evengMilk = "-";
            }

            table.addCell(new Phrase(date + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(morngMilk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(evengMilk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase("" + String.format("%.2f", total) + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase("", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

        }
        document.add(table);
        PdfPTable pTable = new PdfPTable(3);
        pTable.setWidthPercentage(100);
        double TotalAmount = Double.parseDouble(grandTotal);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

        pTable.addCell(new Phrase("M. Total Weight" + ": " + "" + String.format("%.3f", morningTotal), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
        pTable.addCell(new Phrase("E. Total Weight" + ": " + "" + String.format("%.3f", evengTotal), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));
        pTable.addCell(new Phrase("Total Amount" + ": " + "Rs " + String.format("%.2f", TotalAmount), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD)));

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
        if (count == Totcount) {
            if (document != null) {
                //tvPrint.setVisibility(View.VISIBLE);
                // ProgressBar01.setVisibility(View.GONE);
                UtilityMethod.showAlertWithButton(mContext, "" + mContext.getString(R.string.OPen_PDF));


            }
        }
        progressDialog.dismiss();
    }

    @Override
    public void setDataListMain(ArrayList<BuyerCustomerDataListPojo> mList) {
        bhugatanList = mList;
    }

    public void setDataList() {
        if (bhugatanList.size() != 0) {
            startDate = bhugatanList.get(0).from_date;
            endDate = bhugatanList.get(0).to_date;
            tvMonth.setText(bhugatanList.get(0).from_date + " To " + bhugatanList.get(0).to_date);
            milkHistoryAdapter = new BuyerBhugtanListAdapter(mContext, bhugatanList, this);
            recycler_transactionList.setLayoutManager(new GridLayoutManager(mContext, 1));
            recycler_transactionList.addItemDecoration(new DividerItemDecoration(mContext, LinearLayoutManager.VERTICAL));
            recycler_transactionList.setAdapter(milkHistoryAdapter);
            addTextListener();
        }

    }

    public void DialogPrint() {
        final Dialog dialog = new Dialog(mContext);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
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
                printTypeDialog = "pdf";
                Constant.BtnType = "Print";
                StringBuilder stringBuilder2 = new StringBuilder();
                for (int i = 0; i < bhugatanList.size(); i++) {
                    if (bhugatanList.get(i).isClicked.equals("true")) {
                        stringBuilder2.append(bhugatanList.get(i).id + ",");
                    }
                }
                String customerID2 = removeLastChar(stringBuilder2.toString());
                if (!customerID2.equals("")) {
                    getPdfList(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID2);
                } else {
                    UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF));
                }
                dialog.dismiss();
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
                        Constant.BtnType = "Print";
                        StringBuilder stringBuilder2 = new StringBuilder();
                        for (int i = 0; i < bhugatanList.size(); i++) {
                            if (bhugatanList.get(i).isClicked.equals("true")) {
                                stringBuilder2.append(bhugatanList.get(i).id + ",");
                            }
                        }
                        System.out.println("customerID>>>" + stringBuilder2.toString());
                        String customerID2 = removeLastChar(stringBuilder2.toString());
                        System.out.println("customerID>>>" + customerID2);
                        if (!customerID2.equals("")) {
                            getPdfList(mContext, bhugatanList.get(0).from_date, bhugatanList.get(0).to_date, sessionManager.getValueSesion(SessionManager.KEY_UserID), customerID2);
                        } else {
                            UtilityMethod.showAlertWithButton(mContext, mContext.getString(R.string.Please_Select_User_to_print_PDF));
                        }

                        dialog.dismiss();
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

    public void toolbarManage() {

        toolbar.setNavigationIcon(R.drawable.ic_nav_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });


    }

    public void getBuyerMilkData(final Context mContext, String from_date, String next_status) {
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {
                bhugatanList = new ArrayList<>();
                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        //JSONArray mainArray = object.getJSONArray("main");
                        bhugatanList.add(new BuyerCustomerDataListPojo(
                                object.getString("id"), object.getString("created_by"),
                                object.getString("adhar"), object.getString("milk_price"),
                                object.getString("milk_shell_price"), object.getString("unic_customer"),
                                object.getString("unic_customer_for_mobile"), object.getString("name"),
                                object.getString("father_name"), object.getString("phone_number"),
                                object.getString("user_group_id"), object.getString("firebase_tocan"),
                                object.getString("message_status"),
                                nullCheckFunction(object.getString("url_code")), object.getString("from_date"),
                                object.getString("to_date"), object.getString("total_price"),
                                object.getString("total_milk"), "false", "Get Cash"
                        ));
                    }

                    setDataList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()

                .addEncoded("date_from", from_date)
                .addEncoded("dairy_id", dairyid)
                .addEncoded("sts", next_status)
                .build();
        serviceCaller.addRequestBody(body);
        serviceCaller.execute(getBuyerCustomerDataListAPI);
    }

    public void getPdfList(final Context mContext, String from_date, String to_date, String dairy_id, String customer_ids) {

        tenDaysMainList = new ArrayList<>();
        NetworkTask serviceCaller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {

            @Override
            public void handleResponse(String response) {

                try {
                    JSONArray mainJsonArray = new JSONArray(response);
                    for (int i = 0; i < mainJsonArray.length(); i++) {
                        JSONObject object = mainJsonArray.getJSONObject(i);
                        ArrayList<BuyerCustomerDataListPojo> dataList = new ArrayList<>();
                        JSONArray mainArray = object.getJSONArray("main");

                        for (int j = 0; j < mainArray.length(); j++) {
                            JSONObject dataObj = mainArray.getJSONObject(j);
                            System.out.println("dataObj====Buyer====>>>" + dataObj.toString());

                            dataList.add(new BuyerCustomerDataListPojo(
                                    dataObj.getString("for_date"),
                                    dataObj.getString("session"), dataObj.getString("per_kg_price"),
                                    dataObj.getString("total_price"), dataObj.getString("total_milk"),
                                    dataObj.getString("shift")
                            ));
                        }
                        JSONObject user_data = object.getJSONObject("user_data");

                        tenDaysMainList.add(new BuyerCustomerDataListPojo(
                                object.getString("total_milk"),
                                object.getString("grnd_total_amt"),
                                user_data.getString("id"), user_data.getString("unic_customer"),
                                user_data.getString("name"), user_data.getString("father_name"),
                                user_data.getString("phone_number"),
                                dataList
                        ));
                    }


                    setPdfDataList();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("from_date", from_date)
                .addEncoded("to_date", to_date)
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_ids", customer_ids)
                .build();
        serviceCaller.addRequestBody(body);

        serviceCaller.execute(getBuyerCustomerDataListForPDFAPI);
    }


}
