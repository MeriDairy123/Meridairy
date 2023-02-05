package b2infosoft.milkapp.com.Model;

public class BeanVoucherItem {

    String invoice_id = "", customer_id = "", receipt_no = "", reffrence_no = "", p_type = "",
            ledger_name = "", bank_name = "", ac_no = "", cheque_no = "", description = "", image = "", receipt_date = "";
    double dr = 0;

    public BeanVoucherItem(String invoice_id, String customer_id, String receipt_no, String reffrence_no,
                           String p_type, String ledger_name, String bank_name, String ac_no, String cheque_no,
                           String description, String image, String receipt_date, double dr) {
        this.invoice_id = invoice_id;
        this.customer_id = customer_id;
        this.receipt_no = receipt_no;
        this.reffrence_no = reffrence_no;
        this.p_type = p_type;
        this.ledger_name = ledger_name;
        this.bank_name = bank_name;
        this.ac_no = ac_no;
        this.cheque_no = cheque_no;
        this.description = description;
        this.image = image;
        this.receipt_date = receipt_date;
        this.dr = dr;
    }

    public String getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(String invoice_id) {
        this.invoice_id = invoice_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getReceipt_no() {
        return receipt_no;
    }

    public void setReceipt_no(String receipt_no) {
        this.receipt_no = receipt_no;
    }

    public String getReffrence_no() {
        return reffrence_no;
    }

    public void setReffrence_no(String reffrence_no) {
        this.reffrence_no = reffrence_no;
    }

    public String getP_type() {
        return p_type;
    }

    public void setP_type(String p_type) {
        this.p_type = p_type;
    }

    public String getLedger_name() {
        return ledger_name;
    }

    public void setLedger_name(String ledger_name) {
        this.ledger_name = ledger_name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAc_no() {
        return ac_no;
    }

    public void setAc_no(String ac_no) {
        this.ac_no = ac_no;
    }

    public String getCheque_no() {
        return cheque_no;
    }

    public void setCheque_no(String cheque_no) {
        this.cheque_no = cheque_no;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReceipt_date() {
        return receipt_date;
    }

    public void setReceipt_date(String receipt_date) {
        this.receipt_date = receipt_date;
    }

    public double getDr() {
        return dr;
    }

    public void setDr(double dr) {
        this.dr = dr;
    }
}

