package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;

/**
 * Created by u on 18-Jan-18.
 */

public class PdfDataListPojo {

    public String customerName = "";
    public String customerID = "";
    public String customerMobile = "";
    public String startDate = "";
    public String endDate = "";
    ArrayList<TenDaysMilkSellHistory> pdfList;

    public PdfDataListPojo(String customerName, String customerID, String customerMobile, String startDate, String endDate, ArrayList<TenDaysMilkSellHistory> pdfList) {
        this.customerName = customerName;
        this.customerID = customerID;
        this.customerMobile = customerMobile;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pdfList = pdfList;
    }


}
