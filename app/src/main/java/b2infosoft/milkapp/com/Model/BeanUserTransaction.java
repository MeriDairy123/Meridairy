package b2infosoft.milkapp.com.Model;

import android.content.Context;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import b2infosoft.milkapp.com.Interface.OnTransactionListener;
import b2infosoft.milkapp.com.R;
import b2infosoft.milkapp.com.appglobal.Constant;
import b2infosoft.milkapp.com.webservice.NetworkTask;

import static b2infosoft.milkapp.com.appglobal.Constant.BaseImageUrl;
import static b2infosoft.milkapp.com.useful.UtilityMethod.dateDDMMYY;

/**
 * Created by B2infosoft on 03/01/2020.
 */

public class BeanUserTransaction {

    public String id = "", party_name = "", transaction_date = "", voucher_type = "",
            voucher_no = "", description = "", signature_image = "";
    public double dr = 0, cr = 0;

    public BeanUserTransaction(String id, String party_name, String transaction_date, String voucher_type, String voucher_no,
                               String description, String signature_image, double dr, double cr) {
        this.id = id;
        this.party_name = party_name;
        this.transaction_date = transaction_date;
        this.voucher_type = voucher_type;
        this.voucher_no = voucher_no;
        this.description = description;
        this.signature_image = signature_image;
        this.dr = dr;
        this.cr = cr;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getParty_name() {
        return party_name;
    }

    public void setParty_name(String party_name) {
        this.party_name = party_name;
    }

    public String getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(String transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getVoucher_type() {
        return voucher_type;
    }

    public void setVoucher_type(String voucher_type) {
        this.voucher_type = voucher_type;
    }

    public String getVoucher_no() {
        return voucher_no;
    }

    public void setVoucher_no(String voucher_no) {
        this.voucher_no = voucher_no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSignature_image() {
        return signature_image;
    }

    public void setSignature_image(String signature_image) {
        this.signature_image = signature_image;
    }

    public double getDr() {
        return dr;
    }

    public void setDr(double dr) {
        this.dr = dr;
    }

    public double getCr() {
        return cr;
    }

    public void setCr(double cr) {
        this.cr = cr;
    }


    public static void getPaymentTransactionList(Context mContext, String dairy_id, String customerId, String type, String strFromDate
            , String strEndDate, OnTransactionListener listener) {

        ArrayList<BeanUserTransaction> transactionList = new ArrayList<>();

        NetworkTask caller = new NetworkTask(NetworkTask.POST_TASK, mContext, "Please wait..."+mContext.getString(R.string.Transaction), true) {
            @Override
            public void handleResponse(String response) {
                double totalCredit = 0,totalDebit = 0;
                String fileUrl="";
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONArray mainJsonArray = jsonObject.getJSONArray("data");
                        BaseImageUrl = jsonObject.getString("path");
                        fileUrl = jsonObject.getString("pdf_url");
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
                    listener.setTransactionList(transactionList,totalCredit,totalDebit,fileUrl);

                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.setTransactionList(transactionList,totalCredit,totalDebit,fileUrl);
                }

            }
        };
        RequestBody body = new FormEncodingBuilder()
                .addEncoded("dairy_id", dairy_id)
                .addEncoded("customer_id", customerId)
                .addEncoded("type", type)
                .addEncoded("from_date", strFromDate)
                .addEncoded("to_date", strEndDate).build();
        caller.addRequestBody(body);
        caller.execute(Constant.getUserTransactionAPI);
    }
}
