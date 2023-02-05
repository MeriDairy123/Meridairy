package b2infosoft.milkapp.com.BluetoothPrinter;

public class Reciept_Item {

    private String Id;
    private String Date;
    private String FAT;
    private String SNF;
    private String QT;
    private String Amount;
    private String ShiftMorning;
    private String ShiftEvening;
    private String Title;
    private String Credit;
    private String Debit;
    private String RemainAmount;


    public Reciept_Item(String id, String date, String FAT,
                        String SNF, String QT, String amount, String shiftMorning, String shiftEvening) {
        Id = id;
        Date = date;
        this.FAT = FAT;
        this.SNF = SNF;
        this.QT = QT;
        Amount = amount;
        ShiftMorning = shiftMorning;
        ShiftEvening = shiftEvening;
    }


    public Reciept_Item(String date, String title, String credit, String debit) {
        Date = date;
        Title = title;
        Credit = credit;
        Debit = debit;
    } public Reciept_Item(String date, String title, String credit, String debit, String remainAmount) {
        Date = date;
        Title = title;
        Credit = credit;
        Debit = debit;
        RemainAmount = remainAmount;
    }


    public Reciept_Item(String id, String qt, String amount) {
        Id = id;
        QT = qt;
        Amount = amount;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFAT() {
        return FAT;
    }

    public void setFAT(String FAT) {
        this.FAT = FAT;
    }

    public String getSNF() {
        return SNF;
    }

    public void setSNF(String SNF) {
        this.SNF = SNF;
    }

    public String getQT() {
        return QT;
    }

    public void setQT(String QT) {
        this.QT = QT;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getShiftMorning() {
        return ShiftMorning;
    }

    public void setShiftMorning(String shiftMorning) {
        ShiftMorning = shiftMorning;
    }

    public String getShiftEvening() {
        return ShiftEvening;
    }

    public void setShiftEvening(String shiftEvening) {
        ShiftEvening = shiftEvening;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getCredit() {
        return Credit;
    }

    public void setCredit(String credit) {
        Credit = credit;
    }

    public String getDebit() {
        return Debit;
    }

    public void setDebit(String debit) {
        Debit = debit;
    }

    public String getRemainAmount() {
        return RemainAmount;
    }

    public void setRemainAmount(String remainAmount) {
        RemainAmount = remainAmount;
    }
}
