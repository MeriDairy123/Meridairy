package b2infosoft.milkapp.com.Dairy.ViewMilkEntry;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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

import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter.ViewEntryRecyclerViewAdapter;
import b2infosoft.milkapp.com.Dairy.ViewMilkEntry.Adapter.ViewEntryRecyclerViewAdapter2;
import b2infosoft.milkapp.com.Model.ViewEntryByDatePojo;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;
import b2infosoft.milkapp.com.useful.UtilityMethod;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.useful.UtilityMethod.getMonthList;


public class ViewEntryByDateFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    RecyclerView recyclerMorning, recyclerEvening;

    Toolbar toolbar;
    TextView toolbar_title;
    String formattedDate = "";
    TextView date;
    Button btnSubmit;
    TextView tv_MorningTotalWeight, tv_MorningTotalFat, tv_MorningTotalAmt,
            tvEveningTotalWeight, tvEveningTotalFat, tvEveningTotalAmt;
    View viewMorning, viewEvening;
    SessionManager sessionManager;
    ImageView imgPrint, imgRecieptPrint;
    ArrayList<ViewEntryByDatePojo> morningListPdf = new ArrayList<>();
    ArrayList<ViewEntryByDatePojo> eveningListPdf = new ArrayList<>();
    int Totcount = 0, count = 0;
    ProgressDialog progressDialog = null;
    Document document;
    String strType = "", strFromWhere = "", Pdfcreted = "false";
    TextView tv_EveningTotalBonus, tv_MorningTotalBonus;
    double morngTotfat = 0d, eveTotfat = 0, totalAmt = 0, totalWeightMorning = 0.0,
            totalWeightEvening = 0.0, avgFatMorning = 0.0, avgFatEvening = 0.0, totAmtMorning = 0.0,
            totAmtEvening = 0.0, totMorningBonus = 0, totEveningBonus = 0;

    View view;
    String FILE = "";
    private int mYear, mMonth, mDay;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_view_entry_by_date, container, false);

        mContext = getActivity();
        sessionManager = new SessionManager(mContext);
        Bundle bundle = getArguments();
        strType = bundle.getString("type");
        strFromWhere = bundle.getString("FromWhere");


        initView(view);
        return view;
    }

    private void initView(View view) {
        sessionManager = new SessionManager(mContext);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar_title = toolbar.findViewById(R.id.toolbar_title);
        imgPrint = toolbar.findViewById(R.id.imgPrint);
        toolbar_title.setText(mContext.getString(R.string.viewEntry));

        imgRecieptPrint = view.findViewById(R.id.imgRecieptPrint);
        viewMorning = view.findViewById(R.id.viewMorning);
        tv_MorningTotalWeight = viewMorning.findViewById(R.id.tvTotalWeight);
        tv_MorningTotalFat = viewMorning.findViewById(R.id.tvTotalFat);
        tv_MorningTotalAmt = viewMorning.findViewById(R.id.tvTotalAmt);
        tv_MorningTotalBonus = viewMorning.findViewById(R.id.tv_TotalBonus);

        viewEvening = view.findViewById(R.id.viewEvening);

        tvEveningTotalWeight = viewEvening.findViewById(R.id.tvTotalWeight);
        tvEveningTotalFat = viewEvening.findViewById(R.id.tvTotalFat);
        tvEveningTotalAmt = viewEvening.findViewById(R.id.tvTotalAmt);
        tv_EveningTotalBonus = viewEvening.findViewById(R.id.tv_TotalBonus);

        date = view.findViewById(R.id.date);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        date.setOnClickListener(this);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        date.setText(formattedDate);
        imgPrint.setVisibility(View.VISIBLE);

        imgPrint.setOnClickListener(this);
        imgRecieptPrint.setOnClickListener(this);
        if (strFromWhere.equals("tab")) {
            toolbar.setVisibility(View.GONE);
            imgRecieptPrint.setVisibility(View.VISIBLE);
        }
        getCustomerEntryList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID),
                date.getText().toString().trim(), "both", "ViewEntryByDate");


        toolbarManage();

    }

    public void getDate() {
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
                        day = checkDigit(dayOfMonth);
                        date.setText(day + "-" + month + "-" + year);
                        formattedDate = day + "-" + month + "-" + year;
                        //entryList = databaseHandler.getMilkBuyEntryRecords(formattedDate);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog2.show();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgRecieptPrint:
            case R.id.imgPrint:
                if (morningListPdf.size() != 0 || eveningListPdf.size() != 0) {
                    openDialog();
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.You_have_no_entry_to_Print));
                }

                break;
            case R.id.date:
                getDate();
                break;
            case R.id.btnSubmit:
                if (!date.getText().toString().equals("")) {
                    getCustomerEntryList(mContext, sessionManager.getValueSesion(SessionManager.KEY_UserID), date.getText().toString().trim(), "both", "ViewEntryByDate");
                } else {
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.Please_Select_Date));
                }
                break;
        }
    }

    private void openDialog() {

        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_pdf_options);
        dialog.show();
        Button btnPrintMorning = dialog.findViewById(R.id.btnPrintMorning);
        Button btnPrintEvening = dialog.findViewById(R.id.btnPrintEvening);

        btnPrintMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (morningListPdf.size() != 0) {
                    Totcount = morningListPdf.size();
                    int i = 0;
                    Pdfcreted = "true";
                    if (!progressDialog.isShowing()) {
                        // tvPrint.setVisibility(View.GONE);
                        // ProgressBar01.setVisibility(View.VISIBLE);
                        progressDialog.show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Pdfcreted.equals("true")) {
                                createPDF(morningListPdf, "Morning");
                            }
                        }
                    }, 3000);
                } else {
                    // Toast.makeText(mContext, mContext.getString(R.string.No_Data_of_Morning_List_to_Print),Toast.LENGTH_SHORT).show();
                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.No_Data_of_Morning_List_to_Print));
                }
            }
        });

        btnPrintEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (eveningListPdf.size() != 0) {
                    Totcount = eveningListPdf.size();

                    Pdfcreted = "true";
                    if (!progressDialog.isShowing()) {

                        progressDialog.show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Pdfcreted.equals("true")) {
                                createPDF(eveningListPdf, "Evening");
                                // }
                            }
                        }
                    }, 3000);
                } else {
                    //  Toast.makeText(mContext, mContext.getString(R.string.No_Data_of_Evening_List_to_Print),Toast.LENGTH_SHORT).show();

                    UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.No_Data_of_Evening_List_to_Print));
                }
            }
        });


    }

    private void createPDF(ArrayList<ViewEntryByDatePojo> pdfList, String session) {
        String folder = "BuyMilk/";
        if (strType.equals("sale")) {
            folder = "SaleMilk/";
        }


        FILE = Environment.getExternalStorageDirectory().toString()
                + "/MeriDairy/View Entry Date" + folder + session + "_" + date.getText().toString() +
                System.currentTimeMillis() + ".pdf";
        document = new Document(PageSize.A4);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/View Entry Date" + folder);
        myDir.mkdirs();


        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            // addMetaData(document);
            addTitlePage(document, pdfList, session);
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

    private void addTitlePage(Document document, ArrayList<ViewEntryByDatePojo> pdfList, String session) {
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
            tableHead.addCell(getCell("Date" + ":- " + date.getText().toString(), PdfPCell.ALIGN_LEFT));
            tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT));
            document.add(tableHead);


            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 4, 4, 4, 4, 4, 4});
            table.setSpacingBefore(4);
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(new Phrase("Sr.", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Name", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Fat/SNF", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Rate", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Bonus", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.addCell(new Phrase("Amount", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD)));
            table.setHeaderRows(1);

            for (int i = 0; i < pdfList.size(); i++) {
                double total = 0;
                table.addCell(new Phrase("" + (i + 1) + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getName() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getFat() + "-" + pdfList.get(i).getSnf(), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getTotal_milk() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getPer_kg_price() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getBonus() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                table.addCell(new Phrase(pdfList.get(i).getTotal_price() + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));

            }
            document.add(table);
            PdfPTable pTable = new PdfPTable(4);
            pTable.setWidthPercentage(100);
            pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            if (session.equals("Morning")) {
                pTable.addCell(new Phrase("Total Weight " + String.format("%.3f", totalWeightMorning), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Avg Fat " + String.format("%.2f", avgFatMorning), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Total Bonus " + String.format("%.2f", totMorningBonus), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Total Amount " + String.format("%.2f", totAmtMorning), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            } else {
                pTable.addCell(new Phrase("Total Weight " + String.format("%.3f", totalWeightEvening), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Avg Fat " + String.format("%.2f", avgFatEvening), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Total Bonus " + String.format("%.2f", totEveningBonus), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
                pTable.addCell(new Phrase("Total Amount " + String.format("%.2f", totAmtEvening), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            }

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

        } catch (Exception e) {

        }
        progressDialog.dismiss();
        if (document != null) {

            UtilityMethod.showAlertBox(mContext, mContext.getString(R.string.OPen_PDF));
            Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/MeriDairy/");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(selectedUri, "application" + "pdf");
            if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                startActivity(intent);
            }

        }
    }


    public void getCustomerEntryList(final Context mContext, String dairy_id, String entry_date, String shift, final String fromWhere) {
        morningListPdf = new ArrayList<>();
        eveningListPdf = new ArrayList<>();
        morngTotfat = 0d;
        eveTotfat = 0;
        totalAmt = 0;
        totalWeightMorning = 0.0;
        totalWeightEvening = 0.0;
        avgFatMorning = 0.0;
        avgFatEvening = 0.0;
        totAmtMorning = 0.0;
        totAmtEvening = 0.0;
        totMorningBonus = 0;
        totEveningBonus = 0;
        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait...", true) {
            @Override
            public void handleResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                        JSONObject jsnData = jsonObject.getJSONObject("data");

                        JSONArray morningArray = jsnData.getJSONArray("morning");
                        JSONArray eveningArray = jsnData.getJSONArray("evening");
                        for (int j = 0; j < morningArray.length(); j++) {
                            JSONObject morObj = morningArray.getJSONObject(j);
                            morningListPdf.add(new ViewEntryByDatePojo(morObj.getString("id"),
                                    morObj.getString("customer_id")
                                    , morObj.getString("dairy_id"),
                                    morObj.getString("entry_date"), morObj.getString("shift"),
                                    morObj.getString("name"), morObj.getString("unic_customer"),
                                    morObj.getDouble("fat"), morObj.getDouble("snf"),
                                    morObj.getDouble("total_milk"), morObj.getDouble("per_kg_price"),
                                    morObj.getDouble("total_bonus"), morObj.getDouble("total_price")));
                            morngTotfat = morngTotfat + morObj.getDouble("fat");
                            totalWeightMorning = totalWeightMorning + morObj.getDouble("total_milk");
                            totAmtMorning = totAmtMorning + morObj.getDouble("total_price");
                            totMorningBonus = totMorningBonus + morObj.getDouble("total_bonus");

                        }
                        for (int k = 0; k < eveningArray.length(); k++) {
                            JSONObject eveObj = eveningArray.getJSONObject(k);
                            eveningListPdf.add(new ViewEntryByDatePojo(eveObj.getString("id"),
                                    eveObj.getString("customer_id"), eveObj.getString("dairy_id"),
                                    eveObj.getString("entry_date"), eveObj.getString("shift"),
                                    eveObj.getString("name"), eveObj.getString("unic_customer"),
                                    eveObj.getDouble("fat"), eveObj.getDouble("snf"),
                                    eveObj.getDouble("total_milk"), eveObj.getDouble("per_kg_price"),
                                    eveObj.getDouble("total_bonus"), eveObj.getDouble("total_price")));

                            eveTotfat = eveTotfat + eveObj.getDouble("fat");
                            totalWeightEvening = totalWeightEvening + eveObj.getDouble("total_milk");
                            totAmtEvening = totAmtEvening + eveObj.getDouble("total_price");
                            totEveningBonus = totEveningBonus + eveObj.getDouble("total_bonus");
                        }

                    }
                    setCustomerEntryList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("entry_date", entry_date)
                .addEncoded("type", strType)
                .addEncoded("shift", shift).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getViewEntryBothShiftsAPI);


    }

    public void setCustomerEntryList() {


        viewMorning = view.findViewById(R.id.viewMorning);
        recyclerMorning = view.findViewById(R.id.recyclerViewMorning);
        recyclerEvening = view.findViewById(R.id.recyclerEvening);
        tv_MorningTotalFat = view.findViewById(R.id.tvTotalFat);
        tv_MorningTotalWeight = view.findViewById(R.id.tvTotalWeight);
        tv_MorningTotalAmt = view.findViewById(R.id.tvTotalAmt);
        tv_MorningTotalBonus = view.findViewById(R.id.tv_TotalBonus);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);


        ViewEntryRecyclerViewAdapter customerListAdapter = new ViewEntryRecyclerViewAdapter(mContext, morningListPdf);
        recyclerMorning.setLayoutManager(mLayoutManager);
        recyclerMorning.setAdapter(customerListAdapter);
        recyclerMorning.setHasFixedSize(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerMorning.setNestedScrollingEnabled(false);
        }
        avgFatMorning = morngTotfat / morningListPdf.size();
        tv_MorningTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.2f", avgFatMorning) + "%");
        tv_MorningTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeightMorning) + " " + mContext.getString(R.string.Ltr));
        tv_MorningTotalAmt.setText(mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totAmtMorning));
        tv_MorningTotalBonus.setText(mContext.getString(R.string.TOTAL_BONUS) + "\n" + mContext.getString(R.string.Rs) + " " + String.format("%.2f", totMorningBonus));


        avgFatEvening = eveTotfat / eveningListPdf.size();
        // tvEveningTotalFat.setText("Average Fat\n" + Math.ceil(avgFatEvening) + "%");
        tvEveningTotalFat.setText(mContext.getString(R.string.Average_Fat) + "\n" + String.format("%.2f", avgFatEvening) + "%");
        tvEveningTotalWeight.setText(mContext.getString(R.string.Total_Weight) + "\n" + String.format("%.3f", totalWeightEvening) + mContext.getString(R.string.Ltr));
        tvEveningTotalAmt.setText(mContext.getString(R.string.Total_Amount) + "\n" + mContext.getString(R.string.Rs) + String.format("%.2f", totAmtEvening));
        tv_EveningTotalBonus.setText(mContext.getString(R.string.TOTAL_BONUS) + "\n" + mContext.getString(R.string.Rs) + String.format("%.2f", totEveningBonus));
        ViewEntryRecyclerViewAdapter2 customerListAdapter2 = new ViewEntryRecyclerViewAdapter2(mContext, eveningListPdf);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerEvening.setLayoutManager(mLayoutManager);
        recyclerEvening.setAdapter(customerListAdapter2);
        customerListAdapter2.notifyDataSetChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerEvening.setNestedScrollingEnabled(false);
        }
        recyclerEvening.setHasFixedSize(true);

    }

    public void toolbarManage() {


        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


    }

}
