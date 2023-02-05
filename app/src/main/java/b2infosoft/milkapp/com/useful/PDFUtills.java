package b2infosoft.milkapp.com.useful;


import static android.os.Build.VERSION.SDK_INT;
import static b2infosoft.milkapp.com.appglobal.Constant.SelectedDate;
import static b2infosoft.milkapp.com.appglobal.Constant.strSession;
import static b2infosoft.milkapp.com.useful.MyApp.TAG;
import static b2infosoft.milkapp.com.useful.UtilityMethod.getFloatValuFromInputText;
import static b2infosoft.milkapp.com.useful.UtilityMethod.isExternalStorage;
import static b2infosoft.milkapp.com.useful.UtilityMethod.printLog;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import b2infosoft.milkapp.com.BuildConfig;
import b2infosoft.milkapp.com.Model.BeanUserTransaction;
import b2infosoft.milkapp.com.Model.CustomerEntryListPojo;
import b2infosoft.milkapp.com.Model.TenDaysMilkSellHistory;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

public class PDFUtills {
    public static int PdfTitle_FontSize14 = 14;
    public static int PdfSubTitle_FontSize12 = 12;
    public static int PdfTbHeader_FontSize10 = 10;
    public static int PdfTbRow_FontSize8 = 8;
    public static BaseFont pdfBaseFont;
    Context mContext;
    private static SessionManager sessionManager;

    public PDFUtills(Context context) {
        this.mContext = context;
        sessionManager = new SessionManager(mContext);
        try {
            pdfBaseFont = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BaseFont getPdfBaseFont(Context context) {
        try {
            pdfBaseFont = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return pdfBaseFont;

    }

    public static ProgressDialog getProgressDialog(Context mContext) {
        ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(mContext.getString(R.string.Printing_Please_Wait));
        progressDialog.setCancelable(false);

        return progressDialog;

    }

    public static void addAppIcon(Context mContext, Document document) {
        Drawable d = mContext.getResources().getDrawable(R.drawable.app_icon);
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
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }

    }

    public static void dairyNameIcon(Context mContext, String dairyCenterName, Document document) {
        Bitmap bitmap = null;
        if (dairyCenterName.equals("")) {
            bitmap = UtilityMethod.textAsBitmap("मेरी डेयरी", 30, mContext);
        } else {
            bitmap = UtilityMethod.textAsBitmap("" + dairyCenterName, 30, mContext);
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
        } catch (IOException | BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public static void addQrCode(Context mContext, Document document) {
        Drawable d2 = mContext.getResources().getDrawable(R.drawable.qr_code);
        BitmapDrawable bitDw2 = ((BitmapDrawable) d2);
        Bitmap bmp2 = bitDw2.getBitmap();
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image2 = null;
        try {
            image2 = Image.getInstance(stream2.toByteArray());
            image2.setAlignment(Image.ALIGN_RIGHT);

            image2.scaleToFit(30, 30);
            image2.setSpacingBefore(3);
            document.add(image2);
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }

    public static Image getQrCodeBitmap(Context mContext) {
        Drawable d2 = mContext.getResources().getDrawable(R.drawable.qr_code);
        BitmapDrawable bitDw2 = ((BitmapDrawable) d2);
        Bitmap bmp2 = bitDw2.getBitmap();
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bmp2.setWidth(30);
            bmp2.setHeight(30);
        }
*/
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        bmp2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
        Image image2 = null;
        try {
            image2 = Image.getInstance(stream2.toByteArray());
            image2.setAlignment(Image.ALIGN_RIGHT);
            image2.scaleToFit(20, 20);
            image2.setScaleToFitHeight(true);
            image2.setSpacingBefore(2);


        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
        return image2;
    }

    public static PdfPTable getPageHeading(Document document, int column) throws DocumentException {
        PdfPTable tableHead = new PdfPTable(column);
        tableHead.setWidthPercentage(100);
        tableHead.addCell(getCell("ID" + ":- " + "unic_id", PdfPCell.ALIGN_LEFT, PdfSubTitle_FontSize12));
        tableHead.addCell(getCell("" + " sessionManager.getValueSesion(SessionManager.KEY_Name)", PdfPCell.ALIGN_RIGHT, PdfSubTitle_FontSize12));
        tableHead.addCell(getCell("Name" + ":- " + "userName", PdfPCell.ALIGN_LEFT, PdfSubTitle_FontSize12));
        tableHead.addCell(getCell("" + "sessionManager.getValueSesion(SessionManager.KEY_Mobile)", PdfPCell.ALIGN_RIGHT, PdfSubTitle_FontSize12));
        tableHead.addCell(getCell("Mobile No" + ":- " + "mobile_no", PdfPCell.ALIGN_LEFT, PdfSubTitle_FontSize12));
        tableHead.addCell(getCell("", PdfPCell.ALIGN_RIGHT, PdfSubTitle_FontSize12));
        document.add(tableHead);
        return tableHead;
    }

    public static File createPDFFile(Context mContext, String folderName, String fileName) throws IOException {
        File pdfFile;
        String extStorageDirectory = Environment.DIRECTORY_DOWNLOADS;
        String internalStorageDirectory = Environment.DIRECTORY_DOWNLOADS;
        boolean issdcard = isExternalStorage();

        File folder;
        /*if (issdcard) {
            folder = new File(extStorageDirectory, folderName);
        } else {
            folder = new File(internalStorageDirectory, folderName);
        }*/
        printLog("pdffile== internalStorageDirectory=", internalStorageDirectory);

        pdfFile = createFolderAndFileNEW(mContext, folderName, fileName);

        /*folder = new File(internalStorageDirectory, folderName);
        printLog("pdffile== folder=",folder.getAbsolutePath());

        if (!folder.exists()) {
            folder.mkdirs();
        }
        pdfFile = new File(folder, fileName);
        pdfFile.createNewFile();
        printLog("pdffile===",pdfFile.getAbsolutePath());*/

        return pdfFile;
    }

    private static File createFolderAndFile(Context mContext, String folderName, String fileName) {


//  File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+folderName);
        // File folder = new File(Environment.getRootDirectory().getAbsolutePath() + "/" + folderName);

        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                mContext.startActivity(getpermission);
            }
        }


        File folder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Log.d(TAG, "createFolderAndFileifffff: ");
          //  folder = new File(mContext.getExternalFilesDir(null).toString() + "/" + folderName);
            folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);

        } else {
            Log.d(TAG, "createFolderAndFileelseeee: ");
            folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
        }
        folder.mkdirs();
        File file = new File(folder, fileName);
        Log.d(TAG, "createFolderAndFile11111: "+file.getAbsolutePath());
        try {
            file.createNewFile();
            printLog("pdf createdddd", file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            printLog("error", e.getMessage());
        }


        printLog("is file" + file.getName(), file.exists() + "");
        return file;
    }


    private static File createFolderAndFileNEW(Context mContext, String folderName, String fileName) {


//  File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+folderName);
        // File folder = new File(Environment.getRootDirectory().getAbsolutePath() + "/" + folderName);

//        if (Build.VERSION.SDK_INT >= 30){
//            if (!Environment.isExternalStorageManager()){
//                Intent getpermission = new Intent();
//                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                mContext.startActivity(getpermission);
//            }
//        }


        File folder = null;

        folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/"+folderName);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//            Log.d(TAG, "createFolderAndFileifffff: ");
//            //  folder = new File(mContext.getExternalFilesDir(null).toString() + "/" + folderName);
//            folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
//
//        } else {
//            Log.d(TAG, "createFolderAndFileelseeee: ");
//            folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);
//        }



        folder.mkdirs();
        File file = new File(folder, fileName);
        Log.d(TAG, "createFolderAndFile11111: "+file.getAbsolutePath());
        try {
            file.createNewFile();
            printLog("pdf createdddd", file.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            printLog("error", e.getMessage());
        }


        printLog("is file" + file.getName(), file.exists() + "");
        return file;
    }


    public static boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private static void createFile(Context mContext, Uri pickerInitialUri) {


        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");


        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        ((Activity) mContext).startActivity(intent);
    }

    public static PdfPCell getHeadCell(String text, int alignment, int size) {
        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.TIMES_ROMAN, size, Font.BOLD)));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static PdfPCell getCell(String text, int alignment, int textsize) {

        PdfPCell cell = new PdfPCell(new Phrase(text, new Font(Font.FontFamily.TIMES_ROMAN, textsize, Font.BOLD)));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public static void milkEntryPDFReport(Context mContext, String folderName, ArrayList<CustomerEntryListPojo> pdfList) {
        String FILE = Environment.getExternalStorageDirectory().toString() + "/MeriDairy/" + folderName + "/" + strSession + "_" + SelectedDate + System.currentTimeMillis() + ".pdf";
        Document document = new Document(PageSize.A4);
        ProgressDialog progressDialog = getProgressDialog(mContext);
        progressDialog.show();
        SessionManager sessionManager = new SessionManager(mContext);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/" + folderName + "/");
        myDir.mkdirs();
        System.out.println("myDir>>>>" + myDir.getPath());


        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            try {
                Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
                Drawable d = mContext.getResources().getDrawable(R.drawable.app_icon);
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
                tableHead.addCell(getCell("Session" + " :-" + strSession, PdfPCell.ALIGN_LEFT, 16));
                tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Name), PdfPCell.ALIGN_RIGHT, 16));
                tableHead.addCell(getCell("Date" + ":- " + SelectedDate, PdfPCell.ALIGN_LEFT, 16));
                tableHead.addCell(getCell("" + sessionManager.getValueSesion(SessionManager.KEY_Mobile), PdfPCell.ALIGN_RIGHT, 16));
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

                double Totfat = 0d, totalAmt = 0d, totalWeight = 0.0, avgFat = 0.0, totAmt = 0.0;
                for (int i = 0; i < pdfList.size(); i++) {

                    Totfat = Totfat + getFloatValuFromInputText(pdfList.get(i).fat) * getFloatValuFromInputText(pdfList.get(i).total_milk);
                    totalWeight = totalWeight + getFloatValuFromInputText(pdfList.get(i).total_milk);
                    totalAmt = totalAmt + getFloatValuFromInputText(pdfList.get(i).total_price);

                    table.addCell(new Phrase("" + (i + 1) + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(pdfList.get(i).name + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(pdfList.get(i).fat + "-" + pdfList.get(i).snf, new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(pdfList.get(i).total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(pdfList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    //  table.addCell(new Phrase(pdfList.get(i). + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                    table.addCell(new Phrase(pdfList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));

                }
                avgFat = Totfat / totalWeight;
                if (avgFat > 10) {
                    avgFat = Math.round(avgFat);
                }

                document.add(table);
                PdfPTable pTable = new PdfPTable(3);
                pTable.setWidthPercentage(100);
                pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

                pTable.addCell(new Phrase("Total Weight " + String.format("%.3f", totalWeight), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                pTable.addCell(new Phrase("Avg Fat " + avgFat, new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.NORMAL)));
                //pTable.addCell(new Phrase("Total Bonus " + String.format("%.2f", totMorningBonus), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                pTable.addCell(new Phrase("Total Amount " + String.format("%.2f", totalAmt), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));

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

                Drawable d2 = mContext.getResources().getDrawable(R.drawable.qr_code);
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
                Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/MeriDairy/" + folderName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(selectedUri, "application" + "pdf");
           /* if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
                startActivity(intent);
            } else {
            }*/

            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        document.close();

    }

    public static void addBottomLine(Context mContext, Document document) throws DocumentException {
        Paragraph bottomHead = new Paragraph();
        bottomHead.setFont(new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.NORMAL));
        bottomHead.setSpacingAfter(3);
        bottomHead.setSpacingBefore(3);
        // Add item into Paragraph
        bottomHead.add("Meri Dairy Download App Now :");
        bottomHead.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(bottomHead);

    }


    public static int addViewEntryTable(Document document, ArrayList<TenDaysMilkSellHistory> mList, String opening_balance) throws DocumentException {
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 4, 4, 4, 6, 4, 4, 6});
        table.setSpacingBefore(4);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Session", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Fat", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("SNF", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Rs/Ltr", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Bonus", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.addCell(new Phrase("Total Amount", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbHeader_FontSize10, Font.BOLD)));
        table.setHeaderRows(1);

        double totalMilk = 0.0, AvgFat = 0.0, TotalFat = 0.0, TotalRate = 0.0, AvgRate = 0.0, TotalAmount = 0.0, TotalBonus = 0.0;
        int count = 0, rateSize = 0;

        for (int i = 0; i < mList.size(); i++) {
            count = count + 1;
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
        //   pTable.addCell(new Phrase("Opening Balance" + ":" + "Rs " + "" +   opening_balance , new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Amount" + ": " + "Rs " + "" + String.format("%.2f", TotalAmount), new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        document.add(pTable);

        return count;
    }


    public static void addTransactionTable(Document document, ArrayList<BeanUserTransaction> transactionList) throws DocumentException {

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


        double totalCredit = 0, totalDebit = 0, remain = 0;

        for (int i = 0; i < transactionList.size(); i++) {
            table.addCell(new Phrase(transactionList.get(i).getTransaction_date() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(transactionList.get(i).getParty_name() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(transactionList.get(i).getVoucher_type() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(transactionList.get(i).getVoucher_no() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            table.addCell(new Phrase(transactionList.get(i).getDescription() + "", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));


            if (transactionList.get(i).getCr() > 0) {
                totalCredit = totalCredit + transactionList.get(i).getCr();
                table.addCell(new Phrase(String.format("%.2f", totalCredit + transactionList.get(i).getCr()), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
            } else {
                table.addCell(new Phrase("---", new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                table.addCell(new Phrase(String.format("%.2f", totalCredit + transactionList.get(i).getDr()), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
                totalDebit = totalDebit + transactionList.get(i).getDr();
            }
        }
        remain = totalDebit - totalCredit;
        document.add(table);
        PdfPTable pTable = new PdfPTable(3);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("Total Credit" + ": " + String.format("%.2f", totalCredit), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
        pTable.addCell(new Phrase("Total Debit" + ": " + String.format("%.2f", totalDebit), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
        pTable.addCell(new Phrase("Remain Rs" + ": " + String.format("%.2f", remain), new Font(Font.FontFamily.TIMES_ROMAN, PdfTbRow_FontSize8, Font.NORMAL)));
        document.add(pTable);

    }


    public static void openPdfFile(Context context, File file) {
        printLog("fileSize ===>>>", file.getAbsolutePath());
        printLog("fileSize ===>>>", "" + file.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);


        Intent intent = new Intent();

        intent.setType("application/pdf");
        if (Build.VERSION.SDK_INT >= 24) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(file.getAbsolutePath()), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            //  Uri uriForFile = FileProvider.getUriForFile(context, "b2infosoft.milkapp.com.provider", file);

            //  intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(file.getAbsolutePath()));
        } else {
            intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.parse(file.getAbsolutePath()), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");

        }
        if (intent.resolveActivityInfo(context.getPackageManager(), 0) != null) {
            ((Activity) context).startActivity(intent);
        }
    }

    public static void openPdfFile2(Context mContext, File pdfFile) {
        printLog("fileSize ===>>>", pdfFile.getAbsolutePath());
        printLog("fileSize ===>>>", "" + pdfFile.length() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);

        System.out.println("pdfFile===>>>>>" + pdfFile.getAbsoluteFile());
        Uri selectedUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (SDK_INT > Build.VERSION_CODES.P) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("application/pdf");
            intent.setData(selectedUri);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, selectedUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else if (SDK_INT >= Build.VERSION_CODES.N && SDK_INT <= Build.VERSION_CODES.P) {
            intent.setData(selectedUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setDataAndType(Uri.parse(pdfFile.getAbsolutePath()), "application/pdf");
            intent = Intent.createChooser(intent, "Open File");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        if (intent.resolveActivityInfo(mContext.getPackageManager(), 0) != null) {
            ((Activity) mContext).startActivity(intent);
        }

    }
}
