package b2infosoft.milkapp.com.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



/* compiled from: RateCardRpkg.kt */
public final class RateCardRpkg {
    private List<RateCardRate> clr_adjustments = new ArrayList();
    private List<RateCardRate> clr_rates = new ArrayList();
    private List<RateCardRate> fat_adjustments = new ArrayList();
    private List<RateCardRate> fat_rates = new ArrayList();
    private List<RateCardRate> snf_adjustments = new ArrayList();
    private List<RateCardRate> snf_rates = new ArrayList();

    public final double getClrAdjustment(double d) {
        List<RateCardRate> list = this.clr_adjustments;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));
        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add(rateCardRate);
        }
        return 0.0d;
    }

    public final double getClrRate(double d) {
        List<RateCardRate> list = this.clr_rates;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));


        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add (rateCardRate);
        }
        return 0.0d;
    }

    public final List<RateCardRate> getClr_adjustments() {
        return this.clr_adjustments;
    }

    public final List<RateCardRate> getClr_rates() {
        return this.clr_rates;
    }

    public final double getFatAdjustment(double d) {
        List<RateCardRate> list = this.fat_adjustments;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));
        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add(rateCardRate);
        }
        return 0.0d;
    }

    public final double getFatRate(double d) {
        List<RateCardRate> list = this.fat_rates;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));
        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add(rateCardRate);
        }
        return 0.0d;
    }

    public final List<RateCardRate> getFat_adjustments() {
        return this.fat_adjustments;
    }

    public final List<RateCardRate> getFat_rates() {
        return this.fat_rates;
    }

    public final double getSnfAdjustment(double d) {
        List<RateCardRate> list = this.snf_adjustments;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));
        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add(rateCardRate);
        }
        return 0.0d;
    }

    public final double getSnfRate(double d) {
        List<RateCardRate> list = this.snf_rates;
        ArrayList arrayList = new ArrayList(Collections.frequency(list, 10));
        for (RateCardRate rateCardRate : list) {
            if (d >= rateCardRate.getFrom() && d <= rateCardRate.getTo()) {
                return rateCardRate.getAmount();
            }
            arrayList.add(rateCardRate);
        }
        return 0.0d;
    }

    public final List<RateCardRate> getSnf_adjustments() {
        return this.snf_adjustments;
    }

    public final List<RateCardRate> getSnf_rates() {
        return this.snf_rates;
    }

    public final void setClr_adjustments(List<RateCardRate> list) {
       // C1982f.m5984b(list, "<set-?>");
        this.clr_adjustments = list;
    }

    public final void setClr_rates(List<RateCardRate> list) {
      //  C1982f.m5984b(list, "<set-?>");
        this.clr_rates = list;
    }

    public final void setFat_adjustments(List<RateCardRate> list) {
      //  C1982f.m5984b(list, "<set-?>");
        this.fat_adjustments = list;
    }

    public final void setFat_rates(List<RateCardRate> list) {
      //  C1982f.m5984b(list, "<set-?>");
        this.fat_rates = list;
    }

    public final void setSnf_adjustments(List<RateCardRate> list) {
      //  C1982f.m5984b(list, "<set-?>");
        this.snf_adjustments = list;
    }

    public final void setSnf_rates(List<RateCardRate> list) {
     //   C1982f.m5984b(list, "<set-?>");
        this.snf_rates = list;
    }
}
