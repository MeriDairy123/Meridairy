package b2infosoft.milkapp.com.Model;

/* compiled from: RateCardRate.kt */
public final class RateCardRate {
    private double amount;
    private double from;

    /* renamed from: to */
    private double to;

    public RateCardRate(double d, double d2, double d3) {
        this.from = d;
        this.to = d2;
        this.amount = d3;
    }

    public final double getAmount() {
        return this.amount;
    }

    public final double getFrom() {
        return this.from;
    }

    public final double getTo() {
        return this.to;
    }

    public final void setAmount(double d) {
        this.amount = d;
    }

    public final void setFrom(double d) {
        this.from = d;
    }

    public final void setTo(double d) {
        this.to = d;
    }
}
