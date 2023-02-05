package b2infosoft.milkapp.com.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.sharedPreference.SessionManager;

/**
 * Created by u on 18-Jan-18.
 */

public class GeneratePDF_File {

    Document document;
    Context mContext;
    SessionManager sessionManager;


    public void setPdfFileData(ArrayList<PdfDataListPojo> mainDataList, Context ctx) {

        int count = 0;
        mContext = ctx;
        sessionManager = new SessionManager(mContext);
        DismissDialog dismissDialog = (DismissDialog) mContext;

        for (int i = 0; i < mainDataList.size(); i++) {
            count++;
            createPDF(mainDataList.get(i).customerName, mainDataList.get(i).customerID, mainDataList.get(i).customerMobile, mainDataList.get(i).startDate, mainDataList.get(i).endDate, mainDataList.get(i).pdfList);
        }
        if (count == mainDataList.size()) {
            dismissDialog.doDismis();
        }
    }


    public void createPDF(String userName, String unic_id, String mobile_no, String startDate, String endDate, ArrayList<TenDaysMilkSellHistory> mList) {


        String FILE = Environment.getExternalStorageDirectory().toString()
                + "/MeriDairy/" + unic_id + "_" + userName + ".pdf";
        document = new Document(PageSize.A4);
        // Create Directory in External Storage
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/MeriDairy/");
        myDir.mkdirs();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(FILE));
            // Open Document for Writting into document
            document.open();
            // User Define Method
            // addMetaData(document);
            addTitlePage(document, unic_id, userName, mobile_no, mList, startDate, endDate);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        document.close();

    }

    public void addTitlePage(Document document, String unic_id, String userName, String mobile_no, ArrayList<TenDaysMilkSellHistory> mList, String startDate, String endDate) throws DocumentException {
        // Create new Page in PDF

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
            image.scaleToFit(50, 50);
            document.add(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        Paragraph prHead = new Paragraph();
        prHead.setFont(catFont);
        prHead.setSpacingAfter(3);
        prHead.setSpacingBefore(3);
        // Add item into Paragraph
        if (sessionManager.getValueSesion(SessionManager.KEY_center_name).equals("")) {
            prHead.add("Meri Dairy");
        } else {
            prHead.add(sessionManager.getValueSesion(SessionManager.KEY_center_name));
        }
        prHead.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prHead);

        //ID
        Paragraph prID = new Paragraph();
        prID.setSpacingAfter(3);
        prID.setSpacingBefore(3);
        prID.setFont(catFont);
        // Add item into Paragraph
        prID.add("ID:- " + unic_id);
        prID.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(prID);
        //Name
        Paragraph prName = new Paragraph();
        prName.setSpacingAfter(3);
        prName.setSpacingBefore(3);
        prName.setFont(catFont);
        // Add item into Paragraph
        prName.add("Name:- " + userName);
        prName.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(prName);

        //Mobile

        Paragraph prMobile = new Paragraph();
        prMobile.setSpacingAfter(3);
        prMobile.setSpacingBefore(3);
        prMobile.setFont(catFont);
        // Add item into Paragraph
        prMobile.add("Mobile:- " + mobile_no);
        prMobile.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(prMobile);

        //Date

        Paragraph prDate = new Paragraph();
        prDate.setSpacingAfter(10);
        prDate.setSpacingBefore(10);
        prDate.setFont(catFont);
        // Add item into Paragraph
        prDate.add(startDate + "   To   " + endDate);
        prDate.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(prDate);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 4, 4, 4, 6, 4, 6});
        table.setSpacingBefore(10);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(new Phrase("Date", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("strSession", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("Fat", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("Snf", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("Rate Per/ltr", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("Weight", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.addCell(new Phrase("Total Amount", new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD)));
        table.setHeaderRows(1);

        for (int i = 0; i < mList.size(); i++) {
            table.addCell(new Phrase(mList.get(i).for_date + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).shift + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).fat + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).snf + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).per_kg_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_milk + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
            table.addCell(new Phrase(mList.get(i).total_price + "", new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL)));
        }
        document.add(table);
        PdfPTable pTable = new PdfPTable(4);
        pTable.setWidthPercentage(100);
        pTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        pTable.addCell(new Phrase("TOTAL MILK: " + "120 LTR", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        pTable.addCell(new Phrase("AVG. FAT: " + "6.9%", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        pTable.addCell(new Phrase("AVG. RATE: " + "20.00", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        pTable.addCell(new Phrase("TOTAL AMOUNT: RS. " + "8000", new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL)));
        document.add(pTable);

        // bottom line
        Paragraph bottomHead = new Paragraph();
        bottomHead.setFont(catFont);
        bottomHead.setSpacingAfter(3);
        bottomHead.setSpacingBefore(10);
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
            image2.scaleToFit(170, 170);
            image2.setSpacingBefore(10);
            document.add(image2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.newPage();
    }


}
