package b2infosoft.milkapp.com.Model;

/* compiled from: RateCardStep.kt */
public final class RateCardStep {
    private double increment_by;
    private double value;

    public RateCardStep(double d, double d2) {
        this.value = d;
        this.increment_by = d2;
    }

    public final double getIncrement_by() {
        return this.increment_by;
    }

    public final double getValue() {
        return this.value;
    }

    public final void setIncrement_by(double d) {
        this.increment_by = d;
    }

    public final void setValue(double d) {
        this.value = d;
    }
}
