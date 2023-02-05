package b2infosoft.milkapp.com.Model;

public class BeanMessageUserItem {
    String id = "", name = "", unic_customer = "";
    double total_dr = 0, total_cr = 0, balance = 0;
    int status = 0;

    public BeanMessageUserItem(String id, String name, String unic_customer,
                               double total_dr, double total_cr, double balance, double status) {
        this.id = id;
        this.name = name;
        this.unic_customer = unic_customer;
        this.total_dr = total_dr;
        this.total_cr = total_cr;
        this.balance = balance;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnic_customer() {
        return unic_customer;
    }

    public void setUnic_customer(String unic_customer) {
        this.unic_customer = unic_customer;
    }

    public double getTotal_dr() {
        return total_dr;
    }

    public void setTotal_dr(double total_dr) {
        this.total_dr = total_dr;
    }

    public double getTotal_cr() {
        return total_cr;
    }

    public void setTotal_cr(double total_cr) {
        this.total_cr = total_cr;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
