package b2infosoft.milkapp.com.Model;

public class MachineData {

    private String weight="";
    private String fat="";
    private String snf="";
    private String clr="";
    private String den="";

    public MachineData() {
    }

    public MachineData(String weight, String fat, String snf, String clr, String den) {
        this.weight = weight;
        this.fat = fat;
        this.snf = snf;
        this.clr = clr;
        this.den = den;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }

    public String getClr() {
        return clr;
    }

    public void setClr(String clr) {
        this.clr = clr;
    }

    public String getDen() {
        return den;
    }

    public void setDen(String den) {
        this.den = den;
    }
}
