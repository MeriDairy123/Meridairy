package b2infosoft.milkapp.com.Model;

public class RateCard {
    private Integer center;
    private Double clr;
    private Double fat;
    private Integer id;
    private Double rate;
    private Double snf;
    private String type = "C";

    public RateCard() {
        Double valueOf = Double.valueOf(0.0d);
        this.fat = valueOf;
        this.clr = valueOf;
        this.rate = valueOf;
    }

    public final Integer getCenter() {
        return this.center;
    }

    public final Double getClr() {
        return this.clr;
    }

    public final Double getFat() {
        return this.fat;
    }

    public final Integer getId() {
        return this.id;
    }

    public final Double getRate() {
        return this.rate;
    }

    public final Double getSnf() {
        return this.snf;
    }

    public final String getType() {
        return this.type;
    }

    public final void setCenter(Integer num) {
        this.center = num;
    }

    public final void setClr(Double d) {
        this.clr = d;
    }

    public final void setFat(Double d) {
        this.fat = d;
    }

    public final void setId(Integer num) {
        this.id = num;
    }

    public final void setRate(Double d) {
        this.rate = d;
    }

    public final void setSnf(Double d) {
        this.snf = d;
    }

    public final void setType(String str) {
      //  C1982f.m5984b(str, "<set-?>");
        this.type = str;
    }


}
