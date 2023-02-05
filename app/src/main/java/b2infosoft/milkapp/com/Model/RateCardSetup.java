package b2infosoft.milkapp.com.Model;

import android.graphics.RectF;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;






public class RateCardSetup {

    public static final String FORMAT_CLR = "clr";
    public static final String FORMAT_FAT = "fat";
    public static final String FORMAT_FAT_CLR = "fat_clr";
    public static final String FORMAT_FAT_SNF = "fat_snf";
    public static final String TYPE_EXCEL = "excel";
    public static final String TYPE_IPP = "ipp";
    public static final String TYPE_RPKG = "rpkg";
    private double base_amount;
    private String card_type = TYPE_IPP;
    private int center;
    private double clr_incr = 1.0d;
    private List<RateCardStep> clrs = new ArrayList();
    private List<String> csv_array = new ArrayList();
    private List<RateCardStep> fats = new ArrayList();
    private String format = FORMAT_FAT_SNF;
    private String key = "";
    private RateCardRpkg rpkg;
    private List<RateCardStep> snfs = new ArrayList();
    private String type = "C";

    /* compiled from: RateCardSetup.kt */
    public static final class Companion {
        private Companion() {
        }


    }

    private final HashMap<String, String> calculateIpp() {
        HashMap<String, String> hashMap = new HashMap<>();
        double d = this.base_amount;
        String str = this.format;
        int hashCode = str.hashCode();
        Double valueOf = Double.valueOf(0.0d);
        String str2 = "java.lang.String.format(locale, format, *args)";
        int i = 2;
        String str3 = "%.1f,%.1f";
        String str4 = "Locale.ENGLISH";
        int i2 = 1;
        int i3 = 0;
        switch (hashCode) {
            case -1076820221:
                String str5 = str3;
                if (str.equals(FORMAT_FAT_CLR) && this.fats.size() > 0 && this.clrs.size() > 0) {
                    double value = ((RateCardStep) this.fats.get(0)).getValue();
                    while (true) {
                        List<RateCardStep> list = this.fats;
                        if (value > ((RateCardStep) list.get(list.size() - 1)).getValue()) {
                            break;
                        } else {
                            d += Double.parseDouble(String.valueOf(Double.valueOf(get_increment_factor(value, this.fats))));
                            double value2 = ((RateCardStep) this.clrs.get(0)).getValue();
                            double d2 = d;
                            while (true) {
                                List<RateCardStep> list2 = this.clrs;
                                if (value2 <= ((RateCardStep) list2.get(list2.size() - 1)).getValue()) {
                                    d2 += get_increment_factor(value2, this.clrs);

                                    Locale locale = Locale.ENGLISH;

                                    Object[] objArr = {Double.valueOf(value), Double.valueOf(value2)};
                                    String format2 = String.format(locale, str5, Arrays.copyOf(objArr, objArr.length));

                                    hashMap.put(format2, String.valueOf(Double.valueOf(d2)));
                                    value2 = Double.parseDouble(String.valueOf(Double.valueOf(value2 + this.clr_incr)));
                                } else {
                                    value = Double.parseDouble( String.valueOf(Double.valueOf(value + 0.1d)));
                                }
                            }
                        }
                    }
                }
            case -1076804795:
                String str6 = str3;
                if (str.equals(FORMAT_FAT_SNF) && this.fats.size() > 0 && this.snfs.size() > 0) {
                    double value3 = ((RateCardStep) this.fats.get(0)).getValue();
                    while (true) {
                        List<RateCardStep> list3 = this.fats;
                        if (value3 > ((RateCardStep) list3.get(list3.size() - i2)).getValue()) {
                            break;
                        } else {
                            d += Double.parseDouble(String.valueOf(Double.valueOf(get_increment_factor(value3, this.fats))));
                            double value4 = ((RateCardStep) this.snfs.get(i3)).getValue();
                            double d3 = d;
                            while (true) {
                                List<RateCardStep> list4 = this.snfs;
                                if (value4 <= ((RateCardStep) list4.get(list4.size() - 1)).getValue()) {
                                    d3 += get_increment_factor(value4, this.snfs);

                                    Locale locale2 = Locale.ENGLISH;

                                    Object[] objArr2 = {Double.valueOf(value3), Double.valueOf(value4)};
                                    Object[] copyOf = Arrays.copyOf(objArr2, objArr2.length);
                                    String str7 = str6;
                                    String format3 = String.format(locale2, str7, copyOf);

                                    hashMap.put(format3, String.valueOf(Double.valueOf(d3)));
                                    value4 = Double.parseDouble(String.valueOf(Double.valueOf(value4 + 0.1d)));
                                    str6 = str7;
                                } else {
                                    String str8 = str6;
                                    value3 = Double.parseDouble(String.valueOf(Double.valueOf(value3 + 0.1d)));
                                    str6 = str8;
                                    i2 = 1;
                                    i3 = 0;
                                }
                            }
                        }
                    }
                } else {
                    return hashMap;
                }
                break;
            case 98601:
                if (str.equals(FORMAT_CLR) && !this.clrs.isEmpty()) {
                    double value5 = ((RateCardStep) this.clrs.get(0)).getValue();
                    while (true) {
                        List<RateCardStep> list5 = this.clrs;
                        if (value5 > ((RateCardStep) list5.get(list5.size() - 1)).getValue()) {
                            break;
                        } else {
                            d += get_increment_factor(value5, this.clrs);

                            Locale locale3 = Locale.ENGLISH;

                            Object[] objArr3 = new Object[i];
                            objArr3[0] = valueOf;
                            objArr3[1] = Double.valueOf(value5);
                            String format4 = String.format(locale3, str3, Arrays.copyOf(objArr3, objArr3.length));

                            hashMap.put(format4, String.valueOf(Double.valueOf(d)));
                            String str9 = str3;
                            value5 = Double.parseDouble(String.valueOf(Double.valueOf(value5 + this.clr_incr)));
                            str3 = str9;
                            i = 2;
                        }
                    }
                } else {
                    return hashMap;
                }
                break;
            case 101145:
                if (str.equals(FORMAT_FAT) && !this.fats.isEmpty()) {
                    double value6 = ((RateCardStep) this.fats.get(0)).getValue();
                    while (true) {
                        List<RateCardStep> list6 = this.fats;
                        if (value6 > ((RateCardStep) list6.get(list6.size() - 1)).getValue()) {
                            break;
                        } else {
                            d += Double.parseDouble(String.valueOf(Double.valueOf(get_increment_factor(value6, this.fats))));

                            Locale locale4 = Locale.ENGLISH;

                            Object[] objArr4 = {Double.valueOf(value6), valueOf};
                            String format5 = String.format(locale4, str3, Arrays.copyOf(objArr4, objArr4.length));

                            hashMap.put(format5, String.valueOf(Double.valueOf(d)));
                            value6 = Double.parseDouble(String.valueOf(Double.valueOf(value6 + 0.1d)));
                        }
                    }
                } else {
                    return hashMap;
                }
                break;
        }
        return hashMap;
    }

    private final RectF calculateIppBounds() {
        RectF rectF = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
        String str = this.format;
        switch (str.hashCode()) {
            case -1076820221:
                if (str.equals(FORMAT_FAT_CLR) && this.fats.size() > 0 && this.clrs.size() > 0) {
                    float value = (float) ((RateCardStep) this.fats.get(0)).getValue();
                    List<RateCardStep> list = this.fats;
                    float value2 = (float) ((RateCardStep) list.get(list.size() - 1)).getValue();
                    float value3 = (float) ((RateCardStep) this.clrs.get(0)).getValue();
                    List<RateCardStep> list2 = this.clrs;
                    rectF = new RectF(value, value2, value3, (float) ((RateCardStep) list2.get(list2.size() - 1)).getValue());
                    break;
                }
            case -1076804795:
                if (str.equals(FORMAT_FAT_SNF)) {
                    if (this.fats.size() > 0 && this.snfs.size() > 0) {
                        float value4 = (float) ((RateCardStep) this.fats.get(0)).getValue();
                        List<RateCardStep> list3 = this.fats;
                        float value5 = (float) ((RateCardStep) list3.get(list3.size() - 1)).getValue();
                        float value6 = (float) ((RateCardStep) this.snfs.get(0)).getValue();
                        List<RateCardStep> list4 = this.snfs;
                        rectF = new RectF(value4, value5, value6, (float) ((RateCardStep) list4.get(list4.size() - 1)).getValue());
                        break;
                    } else {
                        return rectF;
                    }
                }
                break;
            case 98601:
                if (str.equals(FORMAT_CLR)) {
                    if (!this.clrs.isEmpty()) {
                        float value7 = (float) ((RateCardStep) this.clrs.get(0)).getValue();
                        List<RateCardStep> list5 = this.clrs;
                        rectF = new RectF(0.0f, 0.0f, value7, (float) ((RateCardStep) list5.get(list5.size() - 1)).getValue());
                        break;
                    } else {
                        return rectF;
                    }
                }
                break;
            case 101145:
                if (str.equals(FORMAT_FAT)) {
                    if (!this.fats.isEmpty()) {
                        float value8 = (float) ((RateCardStep) this.fats.get(0)).getValue();
                        List<RateCardStep> list6 = this.fats;
                        rectF = new RectF(value8, (float) ((RateCardStep) list6.get(list6.size() - 1)).getValue(), 0.0f, 0.0f);
                        break;
                    } else {
                        return rectF;
                    }
                }
                break;
        }
        return rectF;
    }

    private final HashMap<String, String> calculateRpkg() {
        /*
            r38 = this;
            r0 = r38
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r2 = r0.rpkg
            if (r2 == 0) goto L_0x08b1
            r3 = 0
            if (r2 == 0) goto L_0x001d
            java.util.List r2 = r2.getFat_rates()
            if (r2 == 0) goto L_0x001d
            int r2 = r2.size()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x001e
        L_0x001d:
            r2 = r3
        L_0x001e:
            if (r2 == 0) goto L_0x08ac
            int r2 = r2.intValue()
            if (r2 > 0) goto L_0x0068
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r2 = r0.rpkg
            if (r2 == 0) goto L_0x0039
            java.util.List r2 = r2.getSnf_rates()
            if (r2 == 0) goto L_0x0039
            int r2 = r2.size()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x003a
        L_0x0039:
            r2 = r3
        L_0x003a:
            if (r2 == 0) goto L_0x0064
            int r2 = r2.intValue()
            if (r2 > 0) goto L_0x0068
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r2 = r0.rpkg
            if (r2 == 0) goto L_0x0055
            java.util.List r2 = r2.getClr_rates()
            if (r2 == 0) goto L_0x0055
            int r2 = r2.size()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            goto L_0x0056
        L_0x0055:
            r2 = r3
        L_0x0056:
            if (r2 == 0) goto L_0x0060
            int r2 = r2.intValue()
            if (r2 > 0) goto L_0x0068
            goto L_0x08b1
        L_0x0060:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r3
        L_0x0064:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r3
        L_0x0068:
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            r4 = 0
            java.lang.Double r6 = java.lang.Double.valueOf(r4)
            java.lang.String r7 = "min"
            r2.put(r7, r6)
            java.lang.Double r6 = java.lang.Double.valueOf(r4)
            java.lang.String r8 = "max"
            r2.put(r8, r6)
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            java.lang.Double r9 = java.lang.Double.valueOf(r4)
            r6.put(r7, r9)
            java.lang.Double r9 = java.lang.Double.valueOf(r4)
            r6.put(r8, r9)
            java.util.HashMap r9 = new java.util.HashMap
            r9.<init>()
            java.lang.Double r10 = java.lang.Double.valueOf(r4)
            r9.put(r7, r10)
            java.lang.Double r10 = java.lang.Double.valueOf(r4)
            r9.put(r8, r10)
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r10 = r0.rpkg
            java.lang.String r11 = "fat_rates[\"max\"]!!"
            java.lang.String r12 = "fat_rates[\"min\"]!!"
            r13 = 10
            if (r10 == 0) goto L_0x015a
            java.util.List r10 = r10.getFat_rates()
            if (r10 == 0) goto L_0x015a
            java.util.ArrayList r15 = new java.util.ArrayList
            int r14 = p087d.p088a.C1955k.m5946a(r10, r13)
            r15.<init>(r14)
            java.util.Iterator r10 = r10.iterator()
        L_0x00c4:
            boolean r14 = r10.hasNext()
            if (r14 == 0) goto L_0x015a
            java.lang.Object r14 = r10.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r14 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r14
            double r18 = r14.getFrom()
        L_0x00d4:
            double r20 = r14.getTo()
            int r22 = (r18 > r20 ? 1 : (r18 == r20 ? 0 : -1))
            if (r22 > 0) goto L_0x014e
            dairy.mobile.com.mobiledairy.e.u$a r13 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r3 = java.lang.Double.valueOf(r18)
            java.lang.String r3 = r13.mo13014e(r3)
            double r22 = r14.getAmount()
            java.lang.Double r13 = java.lang.Double.valueOf(r22)
            r2.put(r3, r13)
            java.lang.Object r3 = r2.get(r7)
            java.lang.Double r3 = (java.lang.Double) r3
            boolean r3 = p087d.p090c.p092b.C1982f.m5982a(r3, r4)
            if (r3 != 0) goto L_0x0116
            java.lang.Object r3 = r2.get(r7)
            if (r3 == 0) goto L_0x0111
            p087d.p090c.p092b.C1982f.m5980a(r3, r12)
            java.lang.Number r3 = (java.lang.Number) r3
            double r22 = r3.doubleValue()
            int r3 = (r18 > r22 ? 1 : (r18 == r22 ? 0 : -1))
            if (r3 >= 0) goto L_0x011d
            goto L_0x0116
        L_0x0111:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0116:
            java.lang.Double r3 = java.lang.Double.valueOf(r18)
            r2.put(r7, r3)
        L_0x011d:
            java.lang.Object r3 = r2.get(r8)
            if (r3 == 0) goto L_0x0149
            p087d.p090c.p092b.C1982f.m5980a(r3, r11)
            java.lang.Number r3 = (java.lang.Number) r3
            double r22 = r3.doubleValue()
            int r3 = (r18 > r22 ? 1 : (r18 == r22 ? 0 : -1))
            if (r3 <= 0) goto L_0x0137
            java.lang.Double r3 = java.lang.Double.valueOf(r18)
            r2.put(r8, r3)
        L_0x0137:
            r16 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r4 = r18 + r16
            r3 = 1
            double r18 = r0.round(r4, r3)
            r3 = 0
            r4 = 0
            r13 = 10
            goto L_0x00d4
        L_0x0149:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x014e:
            d.g r3 = p087d.C2018g.f3823a
            r15.add(r3)
            r3 = 0
            r4 = 0
            r13 = 10
            goto L_0x00c4
        L_0x015a:
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r3 = r0.rpkg
            if (r3 == 0) goto L_0x020d
            java.util.List r3 = r3.getSnf_rates()
            if (r3 == 0) goto L_0x020d
            java.util.ArrayList r4 = new java.util.ArrayList
            r5 = 10
            int r10 = p087d.p088a.C1955k.m5946a(r3, r5)
            r4.<init>(r10)
            java.util.Iterator r3 = r3.iterator()
        L_0x0173:
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L_0x020d
            java.lang.Object r5 = r3.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r5 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r5
            double r13 = r5.getFrom()
        L_0x0183:
            double r18 = r5.getTo()
            int r10 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
            if (r10 > 0) goto L_0x0203
            dairy.mobile.com.mobiledairy.e.u$a r10 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r15 = java.lang.Double.valueOf(r13)
            java.lang.String r10 = r10.mo13014e(r15)
            double r18 = r5.getAmount()
            java.lang.Double r15 = java.lang.Double.valueOf(r18)
            r6.put(r10, r15)
            java.lang.Object r10 = r6.get(r7)
            java.lang.Double r10 = (java.lang.Double) r10
            r15 = r11
            r18 = r12
            r11 = 0
            boolean r10 = p087d.p090c.p092b.C1982f.m5982a(r10, r11)
            if (r10 != 0) goto L_0x01cc
            java.lang.Object r10 = r6.get(r7)
            if (r10 == 0) goto L_0x01c7
            java.lang.String r11 = "snf_rates[\"min\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r10, r11)
            java.lang.Number r10 = (java.lang.Number) r10
            double r10 = r10.doubleValue()
            int r12 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r12 >= 0) goto L_0x01d3
            goto L_0x01cc
        L_0x01c7:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x01cc:
            java.lang.Double r10 = java.lang.Double.valueOf(r13)
            r6.put(r7, r10)
        L_0x01d3:
            java.lang.Object r10 = r6.get(r8)
            if (r10 == 0) goto L_0x01fe
            java.lang.String r11 = "snf_rates[\"max\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r10, r11)
            java.lang.Number r10 = (java.lang.Number) r10
            double r10 = r10.doubleValue()
            int r12 = (r13 > r10 ? 1 : (r13 == r10 ? 0 : -1))
            if (r12 <= 0) goto L_0x01ef
            java.lang.Double r10 = java.lang.Double.valueOf(r13)
            r6.put(r8, r10)
        L_0x01ef:
            r10 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r13 = r13 + r10
            r10 = 1
            double r13 = r0.round(r13, r10)
            r11 = r15
            r12 = r18
            goto L_0x0183
        L_0x01fe:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0203:
            r15 = r11
            r18 = r12
            d.g r5 = p087d.C2018g.f3823a
            r4.add(r5)
            goto L_0x0173
        L_0x020d:
            r15 = r11
            r18 = r12
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r3 = r0.rpkg
            java.lang.String r4 = "clr_rates[\"max\"]!!"
            java.lang.String r5 = "clr_rates[\"min\"]!!"
            if (r3 == 0) goto L_0x02c5
            java.util.List r3 = r3.getClr_rates()
            if (r3 == 0) goto L_0x02c5
            java.util.ArrayList r10 = new java.util.ArrayList
            r11 = 10
            int r12 = p087d.p088a.C1955k.m5946a(r3, r11)
            r10.<init>(r12)
            java.util.Iterator r3 = r3.iterator()
        L_0x022d:
            boolean r11 = r3.hasNext()
            if (r11 == 0) goto L_0x02c5
            java.lang.Object r11 = r3.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r11 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r11
            double r12 = r11.getFrom()
        L_0x023d:
            double r24 = r11.getTo()
            int r14 = (r12 > r24 ? 1 : (r12 == r24 ? 0 : -1))
            if (r14 > 0) goto L_0x02b8
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r19 = r3
            java.lang.Double r3 = java.lang.Double.valueOf(r12)
            java.lang.String r3 = r14.mo13014e(r3)
            double r24 = r11.getAmount()
            java.lang.Double r14 = java.lang.Double.valueOf(r24)
            r9.put(r3, r14)
            java.lang.Object r3 = r9.get(r7)
            java.lang.Double r3 = (java.lang.Double) r3
            r24 = r15
            r14 = 0
            boolean r3 = p087d.p090c.p092b.C1982f.m5982a(r3, r14)
            if (r3 != 0) goto L_0x0285
            java.lang.Object r3 = r9.get(r7)
            if (r3 == 0) goto L_0x0280
            p087d.p090c.p092b.C1982f.m5980a(r3, r5)
            java.lang.Number r3 = (java.lang.Number) r3
            double r22 = r3.doubleValue()
            int r3 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1))
            if (r3 >= 0) goto L_0x028c
            goto L_0x0285
        L_0x0280:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0285:
            java.lang.Double r3 = java.lang.Double.valueOf(r12)
            r9.put(r7, r3)
        L_0x028c:
            java.lang.Object r3 = r9.get(r8)
            if (r3 == 0) goto L_0x02b3
            p087d.p090c.p092b.C1982f.m5980a(r3, r4)
            java.lang.Number r3 = (java.lang.Number) r3
            double r22 = r3.doubleValue()
            int r3 = (r12 > r22 ? 1 : (r12 == r22 ? 0 : -1))
            if (r3 <= 0) goto L_0x02a6
            java.lang.Double r3 = java.lang.Double.valueOf(r12)
            r9.put(r8, r3)
        L_0x02a6:
            double r14 = r0.clr_incr
            double r12 = r12 + r14
            r3 = 1
            double r12 = r0.round(r12, r3)
            r3 = r19
            r15 = r24
            goto L_0x023d
        L_0x02b3:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x02b8:
            r19 = r3
            r24 = r15
            d.g r3 = p087d.C2018g.f3823a
            r10.add(r3)
            r3 = r19
            goto L_0x022d
        L_0x02c5:
            r24 = r15
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r10 = r0.rpkg
            if (r10 == 0) goto L_0x02d5
            java.util.List r10 = r10.getFat_adjustments()
            goto L_0x02d6
        L_0x02d5:
            r10 = 0
        L_0x02d6:
            if (r10 == 0) goto L_0x0359
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r10 = r0.rpkg
            if (r10 == 0) goto L_0x02eb
            java.util.List r10 = r10.getFat_adjustments()
            if (r10 == 0) goto L_0x02eb
            int r10 = r10.size()
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            goto L_0x02ec
        L_0x02eb:
            r10 = 0
        L_0x02ec:
            if (r10 == 0) goto L_0x0354
            int r10 = r10.intValue()
            if (r10 <= 0) goto L_0x0359
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r10 = r0.rpkg
            if (r10 == 0) goto L_0x0359
            java.util.List r10 = r10.getFat_adjustments()
            if (r10 == 0) goto L_0x0359
            java.util.ArrayList r11 = new java.util.ArrayList
            r12 = 10
            int r13 = p087d.p088a.C1955k.m5946a(r10, r12)
            r11.<init>(r13)
            java.util.Iterator r10 = r10.iterator()
        L_0x030d:
            boolean r12 = r10.hasNext()
            if (r12 == 0) goto L_0x0359
            java.lang.Object r12 = r10.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r12 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r12
            double r13 = r12.getFrom()
        L_0x031d:
            double r25 = r12.getTo()
            int r15 = (r13 > r25 ? 1 : (r13 == r25 ? 0 : -1))
            if (r15 > 0) goto L_0x034a
            dairy.mobile.com.mobiledairy.e.u$a r15 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r19 = r10
            java.lang.Double r10 = java.lang.Double.valueOf(r13)
            java.lang.String r10 = r15.mo13014e(r10)
            double r25 = r12.getAmount()
            java.lang.Double r15 = java.lang.Double.valueOf(r25)
            r3.put(r10, r15)
            r15 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r13 = r13 + r15
            r10 = 1
            double r13 = r0.round(r13, r10)
            r10 = r19
            goto L_0x031d
        L_0x034a:
            r19 = r10
            d.g r10 = p087d.C2018g.f3823a
            r11.add(r10)
            r10 = r19
            goto L_0x030d
        L_0x0354:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0359:
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x0367
            java.util.List r11 = r11.getSnf_adjustments()
            goto L_0x0368
        L_0x0367:
            r11 = 0
        L_0x0368:
            if (r11 == 0) goto L_0x03f2
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x037d
            java.util.List r11 = r11.getSnf_adjustments()
            if (r11 == 0) goto L_0x037d
            int r11 = r11.size()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            goto L_0x037e
        L_0x037d:
            r11 = 0
        L_0x037e:
            if (r11 == 0) goto L_0x03ed
            int r11 = r11.intValue()
            if (r11 <= 0) goto L_0x03f2
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x03f2
            java.util.List r11 = r11.getSnf_adjustments()
            if (r11 == 0) goto L_0x03f2
            java.util.ArrayList r12 = new java.util.ArrayList
            r13 = 10
            int r14 = p087d.p088a.C1955k.m5946a(r11, r13)
            r12.<init>(r14)
            java.util.Iterator r11 = r11.iterator()
        L_0x039f:
            boolean r13 = r11.hasNext()
            if (r13 == 0) goto L_0x03f2
            java.lang.Object r13 = r11.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r13 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r13
            double r14 = r13.getFrom()
        L_0x03af:
            double r25 = r13.getTo()
            int r19 = (r14 > r25 ? 1 : (r14 == r25 ? 0 : -1))
            if (r19 > 0) goto L_0x03e1
            r19 = r11
            dairy.mobile.com.mobiledairy.e.u$a r11 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r25 = r6
            java.lang.Double r6 = java.lang.Double.valueOf(r14)
            java.lang.String r6 = r11.mo13014e(r6)
            double r26 = r13.getAmount()
            java.lang.Double r11 = java.lang.Double.valueOf(r26)
            r10.put(r6, r11)
            r16 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r14 = r14 + r16
            r6 = 1
            double r14 = r0.round(r14, r6)
            r11 = r19
            r6 = r25
            goto L_0x03af
        L_0x03e1:
            r25 = r6
            r19 = r11
            d.g r6 = p087d.C2018g.f3823a
            r12.add(r6)
            r6 = r25
            goto L_0x039f
        L_0x03ed:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x03f2:
            r25 = r6
            java.util.HashMap r6 = new java.util.HashMap
            r6.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x0402
            java.util.List r11 = r11.getClr_adjustments()
            goto L_0x0403
        L_0x0402:
            r11 = 0
        L_0x0403:
            if (r11 == 0) goto L_0x0489
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x0418
            java.util.List r11 = r11.getClr_adjustments()
            if (r11 == 0) goto L_0x0418
            int r11 = r11.size()
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            goto L_0x0419
        L_0x0418:
            r11 = 0
        L_0x0419:
            if (r11 == 0) goto L_0x0484
            int r11 = r11.intValue()
            if (r11 <= 0) goto L_0x0489
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            if (r11 == 0) goto L_0x0489
            java.util.List r11 = r11.getClr_adjustments()
            if (r11 == 0) goto L_0x0489
            java.util.ArrayList r12 = new java.util.ArrayList
            r13 = 10
            int r13 = p087d.p088a.C1955k.m5946a(r11, r13)
            r12.<init>(r13)
            java.util.Iterator r11 = r11.iterator()
        L_0x043a:
            boolean r13 = r11.hasNext()
            if (r13 == 0) goto L_0x0489
            java.lang.Object r13 = r11.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r13 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r13
            double r14 = r13.getFrom()
        L_0x044a:
            double r19 = r13.getTo()
            int r26 = (r14 > r19 ? 1 : (r14 == r19 ? 0 : -1))
            if (r26 > 0) goto L_0x0478
            r19 = r11
            dairy.mobile.com.mobiledairy.e.u$a r11 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r20 = r10
            java.lang.Double r10 = java.lang.Double.valueOf(r14)
            java.lang.String r10 = r11.mo13014e(r10)
            double r26 = r13.getAmount()
            java.lang.Double r11 = java.lang.Double.valueOf(r26)
            r6.put(r10, r11)
            double r10 = r0.clr_incr
            double r14 = r14 + r10
            r10 = 1
            double r14 = r0.round(r14, r10)
            r11 = r19
            r10 = r20
            goto L_0x044a
        L_0x0478:
            r20 = r10
            r19 = r11
            d.g r10 = p087d.C2018g.f3823a
            r12.add(r10)
            r10 = r20
            goto L_0x043a
        L_0x0484:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0489:
            r20 = r10
            java.lang.String r10 = r0.format
            java.lang.String r11 = "fat_snf"
            boolean r10 = p087d.p090c.p092b.C1982f.m5983a(r10, r11)
            java.lang.String r12 = "%.1f,%.1f"
            java.lang.String r13 = "Locale.ENGLISH"
            if (r10 != 0) goto L_0x0707
            java.lang.String r10 = r0.format
            java.lang.String r11 = "fat"
            boolean r10 = p087d.p090c.p092b.C1982f.m5983a(r10, r11)
            if (r10 == 0) goto L_0x04a5
            goto L_0x0707
        L_0x04a5:
            java.lang.String r10 = r0.format
            java.lang.String r11 = "fat_clr"
            boolean r10 = p087d.p090c.p092b.C1982f.m5983a(r10, r11)
            if (r10 == 0) goto L_0x0630
            java.lang.Object r10 = r2.get(r7)
            if (r10 == 0) goto L_0x062b
            r11 = r18
            p087d.p090c.p092b.C1982f.m5980a(r10, r11)
            java.lang.Number r10 = (java.lang.Number) r10
            double r10 = r10.doubleValue()
        L_0x04c0:
            java.lang.Object r14 = r2.get(r8)
            if (r14 == 0) goto L_0x0626
            r15 = r24
            p087d.p090c.p092b.C1982f.m5980a(r14, r15)
            java.lang.Number r14 = (java.lang.Number) r14
            double r24 = r14.doubleValue()
            int r14 = (r10 > r24 ? 1 : (r10 == r24 ? 0 : -1))
            if (r14 > 0) goto L_0x08a1
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r24 = r15
            java.lang.Double r15 = java.lang.Double.valueOf(r10)
            java.lang.String r14 = r14.mo13014e(r15)
            java.lang.Object r14 = r2.get(r14)
            if (r14 == 0) goto L_0x0505
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r15 = java.lang.Double.valueOf(r10)
            java.lang.String r14 = r14.mo13014e(r15)
            java.lang.Object r14 = r2.get(r14)
            if (r14 == 0) goto L_0x0500
            java.lang.Number r14 = (java.lang.Number) r14
            double r14 = r14.doubleValue()
            r18 = r2
            goto L_0x0509
        L_0x0500:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0505:
            r18 = r2
            r14 = 0
        L_0x0509:
            dairy.mobile.com.mobiledairy.e.u$a r2 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r0 = java.lang.Double.valueOf(r10)
            java.lang.String r0 = r2.mo13014e(r0)
            java.lang.Object r0 = r3.get(r0)
            if (r0 == 0) goto L_0x0535
            dairy.mobile.com.mobiledairy.e.u$a r0 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r2 = java.lang.Double.valueOf(r10)
            java.lang.String r0 = r0.mo13014e(r2)
            java.lang.Object r0 = r3.get(r0)
            if (r0 == 0) goto L_0x0530
            java.lang.Number r0 = (java.lang.Number) r0
            double r28 = r0.doubleValue()
            goto L_0x0537
        L_0x0530:
            p087d.p090c.p092b.C1982f.m5979a()
            r0 = 0
            throw r0
        L_0x0535:
            r28 = 0
        L_0x0537:
            java.lang.Object r0 = r9.get(r7)
            if (r0 == 0) goto L_0x061f
            p087d.p090c.p092b.C1982f.m5980a(r0, r5)
            java.lang.Number r0 = (java.lang.Number) r0
            double r30 = r0.doubleValue()
        L_0x0546:
            java.lang.Object r0 = r9.get(r8)
            if (r0 == 0) goto L_0x0618
            p087d.p090c.p092b.C1982f.m5980a(r0, r4)
            java.lang.Number r0 = (java.lang.Number) r0
            double r32 = r0.doubleValue()
            int r0 = (r30 > r32 ? 1 : (r30 == r32 ? 0 : -1))
            if (r0 > 0) goto L_0x0607
            dairy.mobile.com.mobiledairy.e.u$a r0 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r2 = java.lang.Double.valueOf(r30)
            java.lang.String r0 = r0.mo13014e(r2)
            java.lang.Object r0 = r9.get(r0)
            if (r0 == 0) goto L_0x0585
            dairy.mobile.com.mobiledairy.e.u$a r0 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r2 = java.lang.Double.valueOf(r30)
            java.lang.String r0 = r0.mo13014e(r2)
            java.lang.Object r0 = r9.get(r0)
            if (r0 == 0) goto L_0x0580
            java.lang.Number r0 = (java.lang.Number) r0
            double r32 = r0.doubleValue()
            goto L_0x0587
        L_0x0580:
            p087d.p090c.p092b.C1982f.m5979a()
            r0 = 0
            throw r0
        L_0x0585:
            r32 = 0
        L_0x0587:
            dairy.mobile.com.mobiledairy.e.u$a r0 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r2 = java.lang.Double.valueOf(r30)
            java.lang.String r0 = r0.mo13014e(r2)
            java.lang.Object r0 = r6.get(r0)
            if (r0 == 0) goto L_0x05b3
            dairy.mobile.com.mobiledairy.e.u$a r0 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r2 = java.lang.Double.valueOf(r30)
            java.lang.String r0 = r0.mo13014e(r2)
            java.lang.Object r0 = r6.get(r0)
            if (r0 == 0) goto L_0x05ae
            java.lang.Number r0 = (java.lang.Number) r0
            double r34 = r0.doubleValue()
            goto L_0x05b5
        L_0x05ae:
            p087d.p090c.p092b.C1982f.m5979a()
            r0 = 0
            throw r0
        L_0x05b3:
            r34 = 0
        L_0x05b5:
            double r36 = r10 * r14
            double r32 = r32 * r30
            double r36 = r36 + r32
            r25 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r36 = r36 / r25
            double r34 = r28 + r34
            double r36 = r36 + r34
            d.c.b.m r0 = p087d.p090c.p092b.C1989m.f3807a
            java.util.Locale r0 = java.util.Locale.ENGLISH
            p087d.p090c.p092b.C1982f.m5980a(r0, r13)
            r32 = r14
            r2 = 2
            java.lang.Object[] r14 = new java.lang.Object[r2]
            r2 = 0
            java.lang.Double r15 = java.lang.Double.valueOf(r10)
            r14[r2] = r15
            java.lang.Double r2 = java.lang.Double.valueOf(r30)
            r15 = 1
            r14[r15] = r2
            int r2 = r14.length
            java.lang.Object[] r2 = java.util.Arrays.copyOf(r14, r2)
            java.lang.String r0 = java.lang.String.format(r0, r12, r2)
            java.lang.String r2 = "java.lang.String.format(locale, format, *args)"
            p087d.p090c.p092b.C1982f.m5980a(r0, r2)
            dairy.mobile.com.mobiledairy.e.u$a r2 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r14 = java.lang.Double.valueOf(r36)
            java.lang.String r2 = r2.mo13003c(r14)
            r1.put(r0, r2)
            r0 = r38
            double r14 = r0.clr_incr
            double r14 = r30 + r14
            r2 = 1
            double r30 = r0.round(r14, r2)
            r14 = r32
            goto L_0x0546
        L_0x0607:
            r2 = 1
            r14 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            r0 = r38
            double r10 = r10 + r14
            double r10 = r0.round(r10, r2)
            r2 = r18
            goto L_0x04c0
        L_0x0618:
            r0 = r38
            p087d.p090c.p092b.C1982f.m5979a()
            r2 = 0
            throw r2
        L_0x061f:
            r0 = r38
            r2 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r2
        L_0x0626:
            r2 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r2
        L_0x062b:
            r2 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r2
        L_0x0630:
            java.lang.String r2 = r0.format
            java.lang.String r3 = "clr"
            boolean r2 = p087d.p090c.p092b.C1982f.m5983a(r2, r3)
            if (r2 == 0) goto L_0x08a1
            java.lang.Object r2 = r9.get(r7)
            if (r2 == 0) goto L_0x0702
            p087d.p090c.p092b.C1982f.m5980a(r2, r5)
            java.lang.Number r2 = (java.lang.Number) r2
            double r2 = r2.doubleValue()
        L_0x0649:
            java.lang.Object r5 = r9.get(r8)
            if (r5 == 0) goto L_0x06fd
            p087d.p090c.p092b.C1982f.m5980a(r5, r4)
            java.lang.Number r5 = (java.lang.Number) r5
            double r10 = r5.doubleValue()
            int r5 = (r2 > r10 ? 1 : (r2 == r10 ? 0 : -1))
            if (r5 > 0) goto L_0x08a1
            dairy.mobile.com.mobiledairy.e.u$a r5 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            java.lang.String r5 = r5.mo13014e(r7)
            java.lang.Object r5 = r9.get(r5)
            if (r5 == 0) goto L_0x0688
            dairy.mobile.com.mobiledairy.e.u$a r5 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            java.lang.String r5 = r5.mo13014e(r7)
            java.lang.Object r5 = r9.get(r5)
            if (r5 == 0) goto L_0x0683
            java.lang.Number r5 = (java.lang.Number) r5
            double r10 = r5.doubleValue()
            goto L_0x068a
        L_0x0683:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0688:
            r10 = 0
        L_0x068a:
            dairy.mobile.com.mobiledairy.e.u$a r5 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            java.lang.String r5 = r5.mo13014e(r7)
            java.lang.Object r5 = r6.get(r5)
            if (r5 == 0) goto L_0x06b6
            dairy.mobile.com.mobiledairy.e.u$a r5 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            java.lang.String r5 = r5.mo13014e(r7)
            java.lang.Object r5 = r6.get(r5)
            if (r5 == 0) goto L_0x06b1
            java.lang.Number r5 = (java.lang.Number) r5
            double r14 = r5.doubleValue()
            goto L_0x06b8
        L_0x06b1:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x06b6:
            r14 = 0
        L_0x06b8:
            double r10 = r10 * r2
            r16 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r10 = r10 / r16
            double r10 = r10 + r14
            d.c.b.m r5 = p087d.p090c.p092b.C1989m.f3807a
            java.util.Locale r5 = java.util.Locale.ENGLISH
            p087d.p090c.p092b.C1982f.m5980a(r5, r13)
            r7 = 2
            java.lang.Object[] r14 = new java.lang.Object[r7]
            r7 = 0
            r15 = 0
            java.lang.Float r15 = java.lang.Float.valueOf(r15)
            r14[r7] = r15
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            r15 = 1
            r14[r15] = r7
            int r7 = r14.length
            java.lang.Object[] r7 = java.util.Arrays.copyOf(r14, r7)
            java.lang.String r5 = java.lang.String.format(r5, r12, r7)
            java.lang.String r7 = "java.lang.String.format(locale, format, *args)"
            p087d.p090c.p092b.C1982f.m5980a(r5, r7)
            dairy.mobile.com.mobiledairy.e.u$a r7 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r10 = java.lang.Double.valueOf(r10)
            java.lang.String r7 = r7.mo13003c(r10)
            r1.put(r5, r7)
            double r10 = r0.clr_incr
            double r2 = r2 + r10
            r5 = 1
            double r2 = r0.round(r2, r5)
            goto L_0x0649
        L_0x06fd:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0702:
            r1 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r1
        L_0x0707:
            r11 = r18
            java.lang.Object r4 = r2.get(r7)
            if (r4 == 0) goto L_0x08a7
            p087d.p090c.p092b.C1982f.m5980a(r4, r11)
            java.lang.Number r4 = (java.lang.Number) r4
            double r4 = r4.doubleValue()
        L_0x0718:
            java.lang.Object r6 = r2.get(r8)
            if (r6 == 0) goto L_0x08a2
            r9 = r24
            p087d.p090c.p092b.C1982f.m5980a(r6, r9)
            java.lang.Number r6 = (java.lang.Number) r6
            double r10 = r6.doubleValue()
            int r6 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r6 > 0) goto L_0x08a1
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r10 = java.lang.Double.valueOf(r4)
            java.lang.String r6 = r6.mo13014e(r10)
            java.lang.Object r6 = r2.get(r6)
            if (r6 == 0) goto L_0x0759
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r10 = java.lang.Double.valueOf(r4)
            java.lang.String r6 = r6.mo13014e(r10)
            java.lang.Object r6 = r2.get(r6)
            if (r6 == 0) goto L_0x0754
            java.lang.Number r6 = (java.lang.Number) r6
            double r10 = r6.doubleValue()
            goto L_0x075b
        L_0x0754:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x0759:
            r10 = 0
        L_0x075b:
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r14 = java.lang.Double.valueOf(r4)
            java.lang.String r6 = r6.mo13014e(r14)
            java.lang.Object r6 = r3.get(r6)
            if (r6 == 0) goto L_0x078b
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r14 = java.lang.Double.valueOf(r4)
            java.lang.String r6 = r6.mo13014e(r14)
            java.lang.Object r6 = r3.get(r6)
            if (r6 == 0) goto L_0x0786
            java.lang.Number r6 = (java.lang.Number) r6
            double r14 = r6.doubleValue()
            r18 = r2
            r6 = r25
            goto L_0x0791
        L_0x0786:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x078b:
            r18 = r2
            r6 = r25
            r14 = 0
        L_0x0791:
            java.lang.Object r2 = r6.get(r7)
            if (r2 == 0) goto L_0x089c
            r24 = r3
            java.lang.String r3 = "snf_rates[\"min\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r2, r3)
            java.lang.Number r2 = (java.lang.Number) r2
            double r2 = r2.doubleValue()
            r25 = r7
        L_0x07a6:
            java.lang.Object r7 = r6.get(r8)
            if (r7 == 0) goto L_0x0897
            r28 = r8
            java.lang.String r8 = "snf_rates[\"max\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r7, r8)
            java.lang.Number r7 = (java.lang.Number) r7
            double r7 = r7.doubleValue()
            int r29 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r29 > 0) goto L_0x0878
            dairy.mobile.com.mobiledairy.e.u$a r7 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r8 = java.lang.Double.valueOf(r2)
            java.lang.String r7 = r7.mo13014e(r8)
            java.lang.Object r7 = r6.get(r7)
            if (r7 == 0) goto L_0x07eb
            dairy.mobile.com.mobiledairy.e.u$a r7 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r8 = java.lang.Double.valueOf(r2)
            java.lang.String r7 = r7.mo13014e(r8)
            java.lang.Object r7 = r6.get(r7)
            if (r7 == 0) goto L_0x07e6
            java.lang.Number r7 = (java.lang.Number) r7
            double r7 = r7.doubleValue()
            r29 = r6
            goto L_0x07ef
        L_0x07e6:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x07eb:
            r29 = r6
            r7 = 0
        L_0x07ef:
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            r30 = r9
            java.lang.Double r9 = java.lang.Double.valueOf(r2)
            java.lang.String r6 = r6.mo13014e(r9)
            r9 = r20
            java.lang.Object r6 = r9.get(r6)
            if (r6 == 0) goto L_0x081f
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r0 = java.lang.Double.valueOf(r2)
            java.lang.String r0 = r6.mo13014e(r0)
            java.lang.Object r0 = r9.get(r0)
            if (r0 == 0) goto L_0x081a
            java.lang.Number r0 = (java.lang.Number) r0
            double r31 = r0.doubleValue()
            goto L_0x0821
        L_0x081a:
            p087d.p090c.p092b.C1982f.m5979a()
            r0 = 0
            throw r0
        L_0x081f:
            r31 = 0
        L_0x0821:
            double r33 = r4 * r10
            double r7 = r7 * r2
            double r33 = r33 + r7
            r6 = 4636737291354636288(0x4059000000000000, double:100.0)
            double r33 = r33 / r6
            double r31 = r14 + r31
            double r33 = r33 + r31
            d.c.b.m r0 = p087d.p090c.p092b.C1989m.f3807a
            java.util.Locale r0 = java.util.Locale.ENGLISH
            p087d.p090c.p092b.C1982f.m5980a(r0, r13)
            r8 = 2
            java.lang.Object[] r6 = new java.lang.Object[r8]
            r7 = 0
            java.lang.Double r19 = java.lang.Double.valueOf(r4)
            r6[r7] = r19
            java.lang.Double r7 = java.lang.Double.valueOf(r2)
            r8 = 1
            r6[r8] = r7
            int r7 = r6.length
            java.lang.Object[] r6 = java.util.Arrays.copyOf(r6, r7)
            java.lang.String r0 = java.lang.String.format(r0, r12, r6)
            java.lang.String r6 = "java.lang.String.format(locale, format, *args)"
            p087d.p090c.p092b.C1982f.m5980a(r0, r6)
            dairy.mobile.com.mobiledairy.e.u$a r6 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r7 = java.lang.Double.valueOf(r33)
            java.lang.String r6 = r6.mo13003c(r7)
            r1.put(r0, r6)
            r6 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r2 = r2 + r6
            r0 = r38
            double r2 = r0.round(r2, r8)
            r20 = r9
            r8 = r28
            r6 = r29
            r9 = r30
            goto L_0x07a6
        L_0x0878:
            r29 = r6
            r30 = r9
            r9 = r20
            r6 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            r8 = 1
            double r4 = r4 + r6
            double r4 = r0.round(r4, r8)
            r2 = r18
            r3 = r24
            r7 = r25
            r8 = r28
            r25 = r29
            r24 = r30
            goto L_0x0718
        L_0x0897:
            p087d.p090c.p092b.C1982f.m5979a()
            r1 = 0
            throw r1
        L_0x089c:
            r1 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r1
        L_0x08a1:
            return r1
        L_0x08a2:
            r1 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r1
        L_0x08a7:
            r1 = 0
            p087d.p090c.p092b.C1982f.m5979a()
            throw r1
        L_0x08ac:
            r1 = r3
            p087d.p090c.p092b.C1982f.m5979a()
            throw r1
        L_0x08b1:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: dairy.mobile.com.mobiledairy.model.RateCardSetup.calculateRpkg():java.util.HashMap");
    }


    private final RectF calculateRpkgBounds() {
        /*
            r23 = this;
            r0 = r23
            android.graphics.RectF r1 = new android.graphics.RectF
            r2 = 0
            r1.<init>(r2, r2, r2, r2)
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r3 = r0.rpkg
            if (r3 == 0) goto L_0x0529
            r4 = 0
            if (r3 == 0) goto L_0x001e
            java.util.List r3 = r3.getFat_rates()
            if (r3 == 0) goto L_0x001e
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            goto L_0x001f
        L_0x001e:
            r3 = r4
        L_0x001f:
            if (r3 == 0) goto L_0x0525
            int r3 = r3.intValue()
            if (r3 > 0) goto L_0x0069
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r3 = r0.rpkg
            if (r3 == 0) goto L_0x003a
            java.util.List r3 = r3.getSnf_rates()
            if (r3 == 0) goto L_0x003a
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            goto L_0x003b
        L_0x003a:
            r3 = r4
        L_0x003b:
            if (r3 == 0) goto L_0x0065
            int r3 = r3.intValue()
            if (r3 > 0) goto L_0x0069
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r3 = r0.rpkg
            if (r3 == 0) goto L_0x0056
            java.util.List r3 = r3.getClr_rates()
            if (r3 == 0) goto L_0x0056
            int r3 = r3.size()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            goto L_0x0057
        L_0x0056:
            r3 = r4
        L_0x0057:
            if (r3 == 0) goto L_0x0061
            int r3 = r3.intValue()
            if (r3 > 0) goto L_0x0069
            goto L_0x0529
        L_0x0061:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0065:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0069:
            java.util.HashMap r3 = new java.util.HashMap
            r3.<init>()
            r5 = 0
            java.lang.Double r7 = java.lang.Double.valueOf(r5)
            java.lang.String r8 = "min"
            r3.put(r8, r7)
            java.lang.Double r7 = java.lang.Double.valueOf(r5)
            java.lang.String r9 = "max"
            r3.put(r9, r7)
            java.util.HashMap r7 = new java.util.HashMap
            r7.<init>()
            java.lang.Double r10 = java.lang.Double.valueOf(r5)
            r7.put(r8, r10)
            java.lang.Double r10 = java.lang.Double.valueOf(r5)
            r7.put(r9, r10)
            java.util.HashMap r10 = new java.util.HashMap
            r10.<init>()
            java.lang.Double r11 = java.lang.Double.valueOf(r5)
            r10.put(r8, r11)
            java.lang.Double r11 = java.lang.Double.valueOf(r5)
            r10.put(r9, r11)
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r11 = r0.rpkg
            r15 = 10
            if (r11 == 0) goto L_0x0157
            java.util.List r11 = r11.getFat_rates()
            if (r11 == 0) goto L_0x0157
            java.util.ArrayList r2 = new java.util.ArrayList
            int r14 = p087d.p088a.C1955k.m5946a(r11, r15)
            r2.<init>(r14)
            java.util.Iterator r11 = r11.iterator()
        L_0x00c1:
            boolean r14 = r11.hasNext()
            if (r14 == 0) goto L_0x0157
            java.lang.Object r14 = r11.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r14 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r14
            double r16 = r14.getFrom()
        L_0x00d1:
            double r18 = r14.getTo()
            int r20 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r20 > 0) goto L_0x014c
            dairy.mobile.com.mobiledairy.e.u$a r15 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r12 = java.lang.Double.valueOf(r16)
            java.lang.String r12 = r15.mo13014e(r12)
            double r21 = r14.getAmount()
            java.lang.Double r13 = java.lang.Double.valueOf(r21)
            r3.put(r12, r13)
            java.lang.Object r12 = r3.get(r8)
            java.lang.Double r12 = (java.lang.Double) r12
            boolean r12 = p087d.p090c.p092b.C1982f.m5982a(r12, r5)
            if (r12 != 0) goto L_0x0114
            java.lang.Object r12 = r3.get(r8)
            if (r12 == 0) goto L_0x0110
            java.lang.String r13 = "fat_rates[\"min\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r12, r13)
            java.lang.Number r12 = (java.lang.Number) r12
            double r12 = r12.doubleValue()
            int r15 = (r16 > r12 ? 1 : (r16 == r12 ? 0 : -1))
            if (r15 >= 0) goto L_0x011b
            goto L_0x0114
        L_0x0110:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0114:
            java.lang.Double r12 = java.lang.Double.valueOf(r16)
            r3.put(r8, r12)
        L_0x011b:
            java.lang.Object r12 = r3.get(r9)
            if (r12 == 0) goto L_0x0148
            java.lang.String r13 = "fat_rates[\"max\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r12, r13)
            java.lang.Number r12 = (java.lang.Number) r12
            double r12 = r12.doubleValue()
            int r15 = (r16 > r12 ? 1 : (r16 == r12 ? 0 : -1))
            if (r15 <= 0) goto L_0x0137
            java.lang.Double r12 = java.lang.Double.valueOf(r16)
            r3.put(r9, r12)
        L_0x0137:
            r12 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r5 = r16 + r12
            r12 = 1
            double r16 = r0.round(r5, r12)
            r5 = 0
            r15 = 10
            goto L_0x00d1
        L_0x0148:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x014c:
            d.g r5 = p087d.C2018g.f3823a
            r2.add(r5)
            r5 = 0
            r15 = 10
            goto L_0x00c1
        L_0x0157:
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r2 = r0.rpkg
            if (r2 == 0) goto L_0x01ff
            java.util.List r2 = r2.getSnf_rates()
            if (r2 == 0) goto L_0x01ff
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = 10
            int r11 = p087d.p088a.C1955k.m5946a(r2, r6)
            r5.<init>(r11)
            java.util.Iterator r2 = r2.iterator()
        L_0x0170:
            boolean r6 = r2.hasNext()
            if (r6 == 0) goto L_0x01ff
            java.lang.Object r6 = r2.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r6 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r6
            double r11 = r6.getFrom()
        L_0x0180:
            double r13 = r6.getTo()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 > 0) goto L_0x01f8
            dairy.mobile.com.mobiledairy.e.u$a r13 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r14 = java.lang.Double.valueOf(r11)
            java.lang.String r13 = r13.mo13014e(r14)
            double r14 = r6.getAmount()
            java.lang.Double r14 = java.lang.Double.valueOf(r14)
            r7.put(r13, r14)
            java.lang.Object r13 = r7.get(r8)
            java.lang.Double r13 = (java.lang.Double) r13
            r14 = 0
            boolean r13 = p087d.p090c.p092b.C1982f.m5982a(r13, r14)
            if (r13 != 0) goto L_0x01c5
            java.lang.Object r13 = r7.get(r8)
            if (r13 == 0) goto L_0x01c1
            java.lang.String r14 = "snf_rates[\"min\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r13, r14)
            java.lang.Number r13 = (java.lang.Number) r13
            double r13 = r13.doubleValue()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x01cc
            goto L_0x01c5
        L_0x01c1:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x01c5:
            java.lang.Double r13 = java.lang.Double.valueOf(r11)
            r7.put(r8, r13)
        L_0x01cc:
            java.lang.Object r13 = r7.get(r9)
            if (r13 == 0) goto L_0x01f4
            java.lang.String r14 = "snf_rates[\"max\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r13, r14)
            java.lang.Number r13 = (java.lang.Number) r13
            double r13 = r13.doubleValue()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 <= 0) goto L_0x01e8
            java.lang.Double r13 = java.lang.Double.valueOf(r11)
            r7.put(r9, r13)
        L_0x01e8:
            r13 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r11 = r11 + r13
            r13 = 1
            double r11 = r0.round(r11, r13)
            goto L_0x0180
        L_0x01f4:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x01f8:
            d.g r6 = p087d.C2018g.f3823a
            r5.add(r6)
            goto L_0x0170
        L_0x01ff:
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r2 = r0.rpkg
            if (r2 == 0) goto L_0x02a4
            java.util.List r2 = r2.getClr_rates()
            if (r2 == 0) goto L_0x02a4
            java.util.ArrayList r5 = new java.util.ArrayList
            r6 = 10
            int r11 = p087d.p088a.C1955k.m5946a(r2, r6)
            r5.<init>(r11)
            java.util.Iterator r2 = r2.iterator()
        L_0x0218:
            boolean r6 = r2.hasNext()
            if (r6 == 0) goto L_0x02a4
            java.lang.Object r6 = r2.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r6 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r6
            double r11 = r6.getFrom()
        L_0x0228:
            double r13 = r6.getTo()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 > 0) goto L_0x029d
            dairy.mobile.com.mobiledairy.e.u$a r13 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r14 = java.lang.Double.valueOf(r11)
            java.lang.String r13 = r13.mo13014e(r14)
            double r14 = r6.getAmount()
            java.lang.Double r14 = java.lang.Double.valueOf(r14)
            r10.put(r13, r14)
            java.lang.Object r13 = r10.get(r8)
            java.lang.Double r13 = (java.lang.Double) r13
            r14 = 0
            boolean r13 = p087d.p090c.p092b.C1982f.m5982a(r13, r14)
            if (r13 != 0) goto L_0x026d
            java.lang.Object r13 = r10.get(r8)
            if (r13 == 0) goto L_0x0269
            java.lang.String r14 = "clr_rates[\"min\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r13, r14)
            java.lang.Number r13 = (java.lang.Number) r13
            double r13 = r13.doubleValue()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 >= 0) goto L_0x0274
            goto L_0x026d
        L_0x0269:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x026d:
            java.lang.Double r13 = java.lang.Double.valueOf(r11)
            r10.put(r8, r13)
        L_0x0274:
            java.lang.Object r13 = r10.get(r9)
            if (r13 == 0) goto L_0x0299
            java.lang.String r14 = "clr_rates[\"max\"]!!"
            p087d.p090c.p092b.C1982f.m5980a(r13, r14)
            java.lang.Number r13 = (java.lang.Number) r13
            double r13 = r13.doubleValue()
            int r15 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r15 <= 0) goto L_0x0290
            java.lang.Double r13 = java.lang.Double.valueOf(r11)
            r10.put(r9, r13)
        L_0x0290:
            double r13 = r0.clr_incr
            double r11 = r11 + r13
            r13 = 1
            double r11 = r0.round(r11, r13)
            goto L_0x0228
        L_0x0299:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x029d:
            d.g r6 = p087d.C2018g.f3823a
            r5.add(r6)
            goto L_0x0218
        L_0x02a4:
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x02b2
            java.util.List r5 = r5.getFat_adjustments()
            goto L_0x02b3
        L_0x02b2:
            r5 = r4
        L_0x02b3:
            if (r5 == 0) goto L_0x032d
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x02c8
            java.util.List r5 = r5.getFat_adjustments()
            if (r5 == 0) goto L_0x02c8
            int r5 = r5.size()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            goto L_0x02c9
        L_0x02c8:
            r5 = r4
        L_0x02c9:
            if (r5 == 0) goto L_0x0329
            int r5 = r5.intValue()
            if (r5 <= 0) goto L_0x032d
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x032d
            java.util.List r5 = r5.getFat_adjustments()
            if (r5 == 0) goto L_0x032d
            java.util.ArrayList r6 = new java.util.ArrayList
            r11 = 10
            int r12 = p087d.p088a.C1955k.m5946a(r5, r11)
            r6.<init>(r12)
            java.util.Iterator r5 = r5.iterator()
        L_0x02ea:
            boolean r11 = r5.hasNext()
            if (r11 == 0) goto L_0x032d
            java.lang.Object r11 = r5.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r11 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r11
            double r12 = r11.getFrom()
        L_0x02fa:
            double r14 = r11.getTo()
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 > 0) goto L_0x0323
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r15 = java.lang.Double.valueOf(r12)
            java.lang.String r14 = r14.mo13014e(r15)
            double r16 = r11.getAmount()
            java.lang.Double r15 = java.lang.Double.valueOf(r16)
            r2.put(r14, r15)
            r14 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r12 = r12 + r14
            r14 = 1
            double r12 = r0.round(r12, r14)
            goto L_0x02fa
        L_0x0323:
            d.g r11 = p087d.C2018g.f3823a
            r6.add(r11)
            goto L_0x02ea
        L_0x0329:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x032d:
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x033b
            java.util.List r5 = r5.getSnf_adjustments()
            goto L_0x033c
        L_0x033b:
            r5 = r4
        L_0x033c:
            if (r5 == 0) goto L_0x03b6
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x0351
            java.util.List r5 = r5.getSnf_adjustments()
            if (r5 == 0) goto L_0x0351
            int r5 = r5.size()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            goto L_0x0352
        L_0x0351:
            r5 = r4
        L_0x0352:
            if (r5 == 0) goto L_0x03b2
            int r5 = r5.intValue()
            if (r5 <= 0) goto L_0x03b6
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x03b6
            java.util.List r5 = r5.getSnf_adjustments()
            if (r5 == 0) goto L_0x03b6
            java.util.ArrayList r6 = new java.util.ArrayList
            r11 = 10
            int r12 = p087d.p088a.C1955k.m5946a(r5, r11)
            r6.<init>(r12)
            java.util.Iterator r5 = r5.iterator()
        L_0x0373:
            boolean r11 = r5.hasNext()
            if (r11 == 0) goto L_0x03b6
            java.lang.Object r11 = r5.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r11 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r11
            double r12 = r11.getFrom()
        L_0x0383:
            double r14 = r11.getTo()
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 > 0) goto L_0x03ac
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r15 = java.lang.Double.valueOf(r12)
            java.lang.String r14 = r14.mo13014e(r15)
            double r16 = r11.getAmount()
            java.lang.Double r15 = java.lang.Double.valueOf(r16)
            r2.put(r14, r15)
            r14 = 4591870180066957722(0x3fb999999999999a, double:0.1)
            double r12 = r12 + r14
            r14 = 1
            double r12 = r0.round(r12, r14)
            goto L_0x0383
        L_0x03ac:
            d.g r11 = p087d.C2018g.f3823a
            r6.add(r11)
            goto L_0x0373
        L_0x03b2:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x03b6:
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x03c4
            java.util.List r5 = r5.getClr_adjustments()
            goto L_0x03c5
        L_0x03c4:
            r5 = r4
        L_0x03c5:
            if (r5 == 0) goto L_0x043d
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x03da
            java.util.List r5 = r5.getClr_adjustments()
            if (r5 == 0) goto L_0x03da
            int r5 = r5.size()
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            goto L_0x03db
        L_0x03da:
            r5 = r4
        L_0x03db:
            if (r5 == 0) goto L_0x0439
            int r5 = r5.intValue()
            if (r5 <= 0) goto L_0x043d
            dairy.mobile.com.mobiledairy.model.RateCardRpkg r5 = r0.rpkg
            if (r5 == 0) goto L_0x043d
            java.util.List r5 = r5.getClr_adjustments()
            if (r5 == 0) goto L_0x043d
            java.util.ArrayList r6 = new java.util.ArrayList
            r11 = 10
            int r11 = p087d.p088a.C1955k.m5946a(r5, r11)
            r6.<init>(r11)
            java.util.Iterator r5 = r5.iterator()
        L_0x03fc:
            boolean r11 = r5.hasNext()
            if (r11 == 0) goto L_0x043d
            java.lang.Object r11 = r5.next()
            dairy.mobile.com.mobiledairy.model.RateCardRate r11 = (dairy.mobile.com.mobiledairy.model.RateCardRate) r11
            double r12 = r11.getFrom()
        L_0x040c:
            double r14 = r11.getTo()
            int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r16 > 0) goto L_0x0432
            dairy.mobile.com.mobiledairy.e.u$a r14 = dairy.mobile.com.mobiledairy.p102e.C2769u.f5496o
            java.lang.Double r15 = java.lang.Double.valueOf(r12)
            java.lang.String r14 = r14.mo13014e(r15)
            double r16 = r11.getAmount()
            java.lang.Double r15 = java.lang.Double.valueOf(r16)
            r2.put(r14, r15)
            r14 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            double r12 = r12 + r14
            r14 = 1
            double r12 = r0.round(r12, r14)
            goto L_0x040c
        L_0x0432:
            r14 = 1
            d.g r11 = p087d.C2018g.f3823a
            r6.add(r11)
            goto L_0x03fc
        L_0x0439:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x043d:
            java.lang.String r2 = r0.format
            java.lang.String r5 = "fat_snf"
            boolean r2 = p087d.p090c.p092b.C1982f.m5983a(r2, r5)
            if (r2 != 0) goto L_0x04db
            java.lang.String r2 = r0.format
            java.lang.String r5 = "fat"
            boolean r2 = p087d.p090c.p092b.C1982f.m5983a(r2, r5)
            if (r2 == 0) goto L_0x0453
            goto L_0x04db
        L_0x0453:
            java.lang.String r2 = r0.format
            java.lang.String r5 = "fat_clr"
            boolean r2 = p087d.p090c.p092b.C1982f.m5983a(r2, r5)
            if (r2 == 0) goto L_0x04a7
            android.graphics.RectF r1 = new android.graphics.RectF
            java.lang.Object r2 = r3.get(r8)
            if (r2 == 0) goto L_0x04a3
            java.lang.Number r2 = (java.lang.Number) r2
            double r5 = r2.doubleValue()
            float r2 = (float) r5
            java.lang.Object r3 = r3.get(r9)
            if (r3 == 0) goto L_0x049f
            java.lang.Number r3 = (java.lang.Number) r3
            double r5 = r3.doubleValue()
            float r3 = (float) r5
            java.lang.Object r5 = r10.get(r8)
            if (r5 == 0) goto L_0x049b
            java.lang.Number r5 = (java.lang.Number) r5
            double r5 = r5.doubleValue()
            float r5 = (float) r5
            java.lang.Object r6 = r10.get(r9)
            if (r6 == 0) goto L_0x0497
            java.lang.Number r6 = (java.lang.Number) r6
            double r6 = r6.doubleValue()
            float r4 = (float) r6
            r1.<init>(r2, r3, r5, r4)
            return r1
        L_0x0497:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x049b:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x049f:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x04a3:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x04a7:
            java.lang.String r2 = r0.format
            java.lang.String r3 = "clr"
            boolean r2 = p087d.p090c.p092b.C1982f.m5983a(r2, r3)
            if (r2 == 0) goto L_0x04da
            android.graphics.RectF r1 = new android.graphics.RectF
            java.lang.Object r2 = r10.get(r8)
            if (r2 == 0) goto L_0x04d6
            java.lang.Number r2 = (java.lang.Number) r2
            double r2 = r2.doubleValue()
            float r2 = (float) r2
            java.lang.Object r3 = r10.get(r9)
            if (r3 == 0) goto L_0x04d2
            java.lang.Number r3 = (java.lang.Number) r3
            double r3 = r3.doubleValue()
            float r3 = (float) r3
            r4 = 0
            r1.<init>(r4, r4, r2, r3)
            return r1
        L_0x04d2:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x04d6:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x04da:
            return r1
        L_0x04db:
            android.graphics.RectF r1 = new android.graphics.RectF
            java.lang.Object r2 = r3.get(r8)
            if (r2 == 0) goto L_0x0521
            java.lang.Number r2 = (java.lang.Number) r2
            double r5 = r2.doubleValue()
            float r2 = (float) r5
            java.lang.Object r3 = r3.get(r9)
            if (r3 == 0) goto L_0x051d
            java.lang.Number r3 = (java.lang.Number) r3
            double r5 = r3.doubleValue()
            float r3 = (float) r5
            java.lang.Object r5 = r7.get(r8)
            if (r5 == 0) goto L_0x0519
            java.lang.Number r5 = (java.lang.Number) r5
            double r5 = r5.doubleValue()
            float r5 = (float) r5
            java.lang.Object r6 = r7.get(r9)
            if (r6 == 0) goto L_0x0515
            java.lang.Number r6 = (java.lang.Number) r6
            double r6 = r6.doubleValue()
            float r4 = (float) r6
            r1.<init>(r2, r3, r5, r4)
            return r1
        L_0x0515:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0519:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x051d:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0521:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0525:
            p087d.p090c.p092b.C1982f.m5979a()
            throw r4
        L_0x0529:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: dairy.mobile.com.mobiledairy.model.RateCardSetup.calculateRpkgBounds():android.graphics.RectF");
    }

    public final double findRate(double d, double d2, double d3) {
        double d4 = d;
        double d5 = d2;
        double d6 = d3;
        String str = this.card_type;
        int hashCode = str.hashCode();
        String str2 = FORMAT_FAT;
        double d7 = 0.0d;
        if (hashCode != 104489) {
            if (hashCode != 3507226) {
                if (hashCode == 96948919) {
                    boolean equals = str.equals(TYPE_EXCEL);
                }
            } else if (str.equals(TYPE_RPKG)) {
                String str3 = this.format;
                int hashCode2 = str3.hashCode();
                if (hashCode2 != -1076820221) {
                    if (hashCode2 != -1076804795) {
                        if (hashCode2 == 101145) {
                            if (str3.equals(str2)) {
                                RateCardRpkg rateCardRpkg = this.rpkg;
                                if (rateCardRpkg != null) {
                                    if (d4 >= ((RateCardRate) rateCardRpkg.getFat_rates().get(0)).getFrom()) {
                                        RateCardRpkg rateCardRpkg2 = this.rpkg;
                                        if (rateCardRpkg2 != null) {
                                            List fat_rates = rateCardRpkg2.getFat_rates();
                                            RateCardRpkg rateCardRpkg3 = this.rpkg;
                                            if (rateCardRpkg3 == null) {

                                                throw null;
                                            } else if (d4 <= ((RateCardRate) fat_rates.get(rateCardRpkg3.getFat_rates().size() - 1)).getTo()) {
                                                RateCardRpkg rateCardRpkg4 = this.rpkg;
                                                if (rateCardRpkg4 != null) {
                                                    double fatRate = (rateCardRpkg4.getFatRate(d4) * d4) / 100.0d;
                                                    RateCardRpkg rateCardRpkg5 = this.rpkg;
                                                    if (rateCardRpkg5 != null) {
                                                        d7 = fatRate + rateCardRpkg5.getFatAdjustment(d4);
                                                    } else {

                                                        throw null;
                                                    }
                                                } else {

                                                    throw null;
                                                }
                                            }
                                        } else {

                                            throw null;
                                        }
                                    }
                                    return 0.0d;
                                }

                                throw null;
                            }
                        }
                        return d7;
                    } else if (str3.equals(FORMAT_FAT_SNF)) {
                        RateCardRpkg rateCardRpkg6 = this.rpkg;
                        if (rateCardRpkg6 != null) {
                            if (d4 >= ((RateCardRate) rateCardRpkg6.getFat_rates().get(0)).getFrom()) {
                                RateCardRpkg rateCardRpkg7 = this.rpkg;
                                if (rateCardRpkg7 != null) {
                                    List fat_rates2 = rateCardRpkg7.getFat_rates();
                                    RateCardRpkg rateCardRpkg8 = this.rpkg;
                                    if (rateCardRpkg8 == null) {

                                        throw null;
                                    } else if (d4 <= ((RateCardRate) fat_rates2.get(rateCardRpkg8.getFat_rates().size() - 1)).getTo()) {
                                        RateCardRpkg rateCardRpkg9 = this.rpkg;
                                        if (rateCardRpkg9 != null) {
                                            if (d5 >= ((RateCardRate) rateCardRpkg9.getSnf_rates().get(0)).getFrom()) {
                                                RateCardRpkg rateCardRpkg10 = this.rpkg;
                                                if (rateCardRpkg10 != null) {
                                                    List snf_rates = rateCardRpkg10.getSnf_rates();
                                                    RateCardRpkg rateCardRpkg11 = this.rpkg;
                                                    if (rateCardRpkg11 == null) {

                                                        throw null;
                                                    } else if (d5 <= ((RateCardRate) snf_rates.get(rateCardRpkg11.getSnf_rates().size() - 1)).getTo()) {
                                                        RateCardRpkg rateCardRpkg12 = this.rpkg;
                                                        if (rateCardRpkg12 != null) {
                                                            double fatRate2 = rateCardRpkg12.getFatRate(d4) * d4;
                                                            RateCardRpkg rateCardRpkg13 = this.rpkg;
                                                            if (rateCardRpkg13 != null) {
                                                                double snfRate = (fatRate2 + (rateCardRpkg13.getSnfRate(d5) * d5)) / 100.0d;
                                                                RateCardRpkg rateCardRpkg14 = this.rpkg;
                                                                if (rateCardRpkg14 != null) {
                                                                    double fatAdjustment = rateCardRpkg14.getFatAdjustment(d4);
                                                                    RateCardRpkg rateCardRpkg15 = this.rpkg;
                                                                    if (rateCardRpkg15 != null) {
                                                                        d7 = snfRate + fatAdjustment + rateCardRpkg15.getSnfAdjustment(d5);
                                                                        return d7;
                                                                    }

                                                                    throw null;
                                                                }

                                                                throw null;
                                                            }

                                                            throw null;
                                                        }

                                                        throw null;
                                                    }
                                                } else {

                                                    throw null;
                                                }
                                            }
                                            return 0.0d;
                                        }

                                        throw null;
                                    }
                                } else {

                                    throw null;
                                }
                            }
                            return 0.0d;
                        }

                        throw null;
                    }
                } else if (str3.equals(FORMAT_FAT_CLR)) {
                    RateCardRpkg rateCardRpkg16 = this.rpkg;
                    if (rateCardRpkg16 != null) {
                        if (d4 >= ((RateCardRate) rateCardRpkg16.getFat_rates().get(0)).getFrom()) {
                            RateCardRpkg rateCardRpkg17 = this.rpkg;
                            if (rateCardRpkg17 != null) {
                                List fat_rates3 = rateCardRpkg17.getFat_rates();
                                RateCardRpkg rateCardRpkg18 = this.rpkg;
                                if (rateCardRpkg18 == null) {

                                    throw null;
                                } else if (d4 <= ((RateCardRate) fat_rates3.get(rateCardRpkg18.getFat_rates().size() - 1)).getTo()) {
                                    RateCardRpkg rateCardRpkg19 = this.rpkg;
                                    if (rateCardRpkg19 != null) {
                                        if (d6 >= ((RateCardRate) rateCardRpkg19.getClr_rates().get(0)).getFrom()) {
                                            RateCardRpkg rateCardRpkg20 = this.rpkg;
                                            if (rateCardRpkg20 != null) {
                                                List clr_rates = rateCardRpkg20.getClr_rates();
                                                RateCardRpkg rateCardRpkg21 = this.rpkg;
                                                if (rateCardRpkg21 == null) {

                                                    throw null;
                                                } else if (d6 <= ((RateCardRate) clr_rates.get(rateCardRpkg21.getClr_rates().size() - 1)).getTo()) {
                                                    RateCardRpkg rateCardRpkg22 = this.rpkg;
                                                    if (rateCardRpkg22 != null) {
                                                        double fatRate3 = rateCardRpkg22.getFatRate(d4) * d4;
                                                        RateCardRpkg rateCardRpkg23 = this.rpkg;
                                                        if (rateCardRpkg23 != null) {
                                                            double clrRate = (fatRate3 + (d5 * rateCardRpkg23.getClrRate(d6))) / 100.0d;
                                                            RateCardRpkg rateCardRpkg24 = this.rpkg;
                                                            if (rateCardRpkg24 != null) {
                                                                double fatAdjustment2 = rateCardRpkg24.getFatAdjustment(d4);
                                                                RateCardRpkg rateCardRpkg25 = this.rpkg;
                                                                if (rateCardRpkg25 != null) {
                                                                    d7 = clrRate + fatAdjustment2 + rateCardRpkg25.getClrAdjustment(d6);
                                                                    return d7;
                                                                }

                                                                throw null;
                                                            }

                                                            throw null;
                                                        }

                                                        throw null;
                                                    }

                                                    throw null;
                                                }
                                            } else {

                                                throw null;
                                            }
                                        }
                                        return 0.0d;
                                    }

                                    throw null;
                                }
                            } else {

                                throw null;
                            }
                        }
                        return 0.0d;
                    }

                    throw null;
                }
            }
        } else if (str.equals(TYPE_IPP)) {
            double d8 = this.base_amount;
            if ( this.format.contains(str2)) {
                if (d4 >= ((RateCardStep) this.fats.get(0)).getValue()) {
                    List<RateCardStep> list = this.fats;
                    if (d4 <= ((RateCardStep) list.get(list.size() - 1)).getValue()) {
                        for (double value = ((RateCardStep) this.fats.get(0)).getValue(); value <= d4; value = round(value + 0.1d, 1)) {
                            d8 += get_increment_factor(value, this.fats);
                        }
                    }
                }
                return 0.0d;
            }
            if (this.format.contains("snf")) {
                if (d5 >= ((RateCardStep) this.snfs.get(0)).getValue()) {
                    List<RateCardStep> list2 = this.snfs;
                    if (d5 <= ((RateCardStep) list2.get(list2.size() - 1)).getValue()) {
                        for (double value2 = ((RateCardStep) this.snfs.get(0)).getValue(); value2 <= d5; value2 = round(value2 + 0.1d, 1)) {
                            d8 += get_increment_factor(value2, this.snfs);
                        }
                    }
                }
                return 0.0d;
            }
            if ( this.format.equals(FORMAT_CLR)) {
                if (d6 >= ((RateCardStep) this.clrs.get(0)).getValue()) {
                    List<RateCardStep> list3 = this.clrs;
                    if (d6 <= ((RateCardStep) list3.get(list3.size() - 1)).getValue()) {
                        d7 = d8;
                        for (double value3 = ((RateCardStep) this.clrs.get(0)).getValue(); value3 <= d6; value3 = round(value3 + this.clr_incr, 1)) {
                            d7 += get_increment_factor(value3, this.clrs);
                        }
                    }
                }
                return 0.0d;
            }
            d7 = d8;
            return d7;
        }
        d7 = 0.0d;
        return d7;
    }

    public final double getBase_amount() {
        return this.base_amount;
    }

    public final RectF getBounds() {
        if (this.card_type.equals(TYPE_IPP)) {
            return calculateIppBounds();
        }
        if (this.card_type.equals(TYPE_RPKG)) {
            return calculateRpkgBounds();
        }
        return new RectF(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public final String getCard_type() {
        return this.card_type;
    }

    public final int getCenter() {
        return this.center;
    }

    public final double getClr_incr() {
        return this.clr_incr;
    }

    public final List<RateCardStep> getClrs() {
        return this.clrs;
    }

    public final List<String> getCsv_array() {
        return this.csv_array;
    }

    public final List<RateCardStep> getFats() {
        return this.fats;
    }

    public final String getFormat() {
        return this.format;
    }

    public final String getHtml(RectF rectF, HashMap<String, String> hashMap) {
        RectF rectF2 = rectF;
        HashMap<String, String> hashMap2 = hashMap;

        if (hashMap.size() == 0) {
            return "<center><h3> No Rate Chart </h3></center>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table><thead><tr>");
        String str = "<th>";
        sb.append(str);
        String a =this.format+ "/" ;
        if (a != null) {
            String upperCase = a.toUpperCase();

            sb.append(upperCase);
            String str2 = "</th>";
            sb.append(str2);
            String str3 = this.format;
            String str4 = FORMAT_FAT;
            boolean a2 = str3.equals(str4);
            String str5 = FORMAT_CLR;
            if (a2 || this.format.equals(str5)) {
                sb.append("<th>RATE</th>");
            } else if (this.format.equals(FORMAT_FAT_SNF)) {
                float f = rectF2.right;
                while (f <= rectF2.bottom) {
                    sb.append(str);
                    double d = (double) f;
                    sb.append(Double.valueOf(d));
                    sb.append(str2);
                    
                    Double.isNaN(d);
                    f = Float.parseFloat(String.valueOf(Double.valueOf(d + 0.1d)));
                }
            } else if (this.format.equals(FORMAT_FAT_CLR)) {
                float f2 = rectF2.right;
                while (f2 <= rectF2.bottom) {
                    sb.append(str);
                    double d2 = (double) f2;
                    sb.append( Double.valueOf(d2 ));
                    sb.append(str2);

                    double d3 = this.clr_incr;
                    Double.isNaN(d2);
                    f2 = Float.parseFloat(String.valueOf(Double.valueOf(d2 + d3)));
                }
            }
            sb.append("</tr></thead>");
            sb.append("<tbody>");
            String str6 = "</tr>";
            String str7 = "java.lang.String.format(locale, format, *args)";
            String str8 = "%.1f,%.1f";
            String str9 = "Locale.ENGLISH";
            String str10 = "<tr>";
            String str11 = "</td>";
            String str12 = "<td>";
            if ( this.format.equals(str4)) {
                float f3 = rectF2.left;
                while (f3 <= rectF2.top) {
                    sb.append(str10);
                    sb.append(str12);
                    double d4 = (double) f3;
                    sb.append(Double.valueOf(d4));
                    sb.append(str11);
                    sb.append(str12);

                    Locale locale = Locale.ENGLISH;

                    String str13 = str9;
                    Object[] objArr = {Float.valueOf(f3), Float.valueOf(0.0f)};
                    String format2 = String.format(locale, str8, Arrays.copyOf(objArr, objArr.length));

                    sb.append((String) hashMap2.get(format2));
                    sb.append(str11);
                    sb.append(str6);

                    Double.isNaN(d4);
                    f3 = Float.parseFloat(String.valueOf(Double.valueOf(d4 + 0.1d)));
                    str9 = str13;
                }
            } else {
                String str14 = str9;
                if (this.format.equals(str5)) {
                    float f4 = rectF2.right;
                    while (f4 <= rectF2.bottom) {
                        sb.append(str10);
                        sb.append(str12);
                        double d5 = (double) f4;
                        sb.append(Double.valueOf(d5));
                        sb.append(str11);
                        sb.append(str12);

                        Locale locale2 = Locale.ENGLISH;

                        Object[] objArr2 = {Float.valueOf(0.0f), Float.valueOf(f4)};
                        String format3 = String.format(locale2, str8, Arrays.copyOf(objArr2, objArr2.length));
                        sb.append((String) hashMap2.get(format3));
                        sb.append(str11);
                        sb.append(str6);

                        Double.isNaN(d5);
                        f4 = Float.parseFloat(String.valueOf(Double.valueOf(d5 + 1.0d)));
                    }
                } else if (this.format.equals(FORMAT_FAT_SNF) ) {
                    float f5 = rectF2.left;
                    while (f5 <= rectF2.top) {
                        sb.append(str10);
                        sb.append(str12);
                        double d6 = (double) f5;
                        sb.append(Double.valueOf(d6));
                        sb.append(str11);
                        float f6 = rectF2.right;
                        while (f6 <= rectF2.bottom) {
                            sb.append(str12);

                            Locale locale3 = Locale.ENGLISH;

                            String str15 = str12;
                            Object[] objArr3 = {Float.valueOf(f5), Float.valueOf(f6)};
                            String format4 = String.format(locale3, str8, Arrays.copyOf(objArr3, objArr3.length));

                            sb.append((String) hashMap2.get(format4));
                            sb.append(str11);

                            String str16 = str7;
                            double d7 = (double) f6;
                            Double.isNaN(d7);
                            f6 = Float.parseFloat(String.valueOf(Double.valueOf(d7 + 0.1d)));
                            str7 = str16;
                            str12 = str15;
                        }
                        String str17 = str12;
                        String str18 = str7;

                        Double.isNaN(d6);
                        f5 = Float.parseFloat(String.valueOf(Double.valueOf(d6 + 0.1d )));
                        sb.append(str6);
                        str7 = str18;
                        str12 = str17;
                    }
                } else {
                    String str19 = str12;
                    String str20 = str7;
                    if ( this.format.equals( FORMAT_FAT_CLR)) {
                        float f7 = rectF2.left;
                        while (f7 <= rectF2.top) {
                            sb.append(str10);
                            String str21 = str19;
                            sb.append(str21);
                            double d8 = (double) f7;
                            sb.append(Double.valueOf(d8));
                            sb.append(str11);
                            float f8 = rectF2.right;
                            while (f8 <= rectF2.bottom) {
                                sb.append(str21);

                                Locale locale4 = Locale.ENGLISH;
                                String str22 = str14;

                                String str23 = str21;
                                Object[] objArr4 = {Float.valueOf(f7), Float.valueOf(f8)};
                                String format5 = String.format(locale4, str8, Arrays.copyOf(objArr4, objArr4.length));

                                sb.append((String) hashMap2.get(format5));
                                sb.append(str11);

                                double d9 = (double) f8;
                                String str24 = str11;
                                double d10 = this.clr_incr;
                                Double.isNaN(d9);
                                f8 = Float.parseFloat(String.valueOf(Double.valueOf(d9 + d10)));
                                rectF2 = rectF;
                                str11 = str24;
                                str21 = str23;
                                str14 = str22;
                            }
                            str19 = str21;
                            String str25 = str14;
                            String str26 = str11;

                            Double.isNaN(d8);
                            f7 = Float.parseFloat(String.valueOf(Double.valueOf(d8 + 0.1d)));
                            sb.append(str6);
                            rectF2 = rectF;
                            str14 = str25;
                        }
                    }
                }
            }
            sb.append("</tbody></table>");
            String sb2 = sb.toString();

            return sb2;
        }
        return  "null cannot be cast to non-null type java.lang.String" ;
    }

    public final String getKey() {
        return this.key;
    }

    public final HashMap<String, String> getRateCard() {
        if (  this.card_type.equals(TYPE_IPP)) {
            return calculateIpp();
        }
        if (this.card_type.equals(TYPE_RPKG)) {
            return calculateRpkg();
        }
        return new HashMap<>();
    }

    public final RateCardRpkg getRpkg() {
        return this.rpkg;
    }

    public final List<RateCardStep> getSnfs() {
        return this.snfs;
    }

    public final String getType() {
        return this.type;
    }

    public final double get_increment_factor(double d, List<RateCardStep> list) {

        for (RateCardStep rateCardStep :list) {
            if (d > rateCardStep.getValue()) {
                return rateCardStep.getIncrement_by();
            }
        }
        return 0.0d;
    }

    public final double round(double d, int i) {
        if (i >= 0) {
            BigDecimal scale = new BigDecimal(d).setScale(i, RoundingMode.HALF_UP);

            return scale.doubleValue();
        }
        throw new IllegalArgumentException();
    }

    public final void setBase_amount(double d) {
        this.base_amount = d;
    }

    public final void setCard_type(String str) {

        this.card_type = str;
    }

    public final void setCenter(int i) {
        this.center = i;
    }

    public final void setClr_incr(double d) {
        this.clr_incr = d;
    }

    public final void setClrs(List<RateCardStep> list) {

        this.clrs = list;
    }

    public final void setCsv_array(List<String> list) {

        this.csv_array = list;
    }

    public final void setFats(List<RateCardStep> list) {

        this.fats = list;
    }

    public final void setFormat(String str) {

        this.format = str;
    }

    public final void setKey(String str) {

        this.key = str;
    }

    public final void setRpkg(RateCardRpkg rateCardRpkg) {
        this.rpkg = rateCardRpkg;
    }

    public final void setSnfs(List<RateCardStep> list) {

        this.snfs = list;
    }

    public final void setType(String str) {

        this.type = str;
    }
}
