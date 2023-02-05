package b2infosoft.milkapp.com.Model;

/**
 * Created by u on 31-Jan-18.
 */

public class PlanPojo {

    private String Header_name, plan_0, plan_1, plan_2, plan_3;

    public PlanPojo(String header_name, String plan_0, String plan_1, String plan_2, String plan_3) {
        Header_name = header_name;
        this.plan_0 = plan_0;
        this.plan_1 = plan_1;
        this.plan_2 = plan_2;
        this.plan_3 = plan_3;
    }


    public String getHeader_name() {
        return Header_name;
    }

    public void setHeader_name(String header_name) {
        Header_name = header_name;
    }

    public String getPlan_0() {
        return plan_0;
    }

    public void setPlan_0(String plan_0) {
        this.plan_0 = plan_0;
    }

    public String getPlan_1() {
        return plan_1;
    }

    public void setPlan_1(String plan_1) {
        this.plan_1 = plan_1;
    }

    public String getPlan_2() {
        return plan_2;
    }

    public void setPlan_2(String plan_2) {
        this.plan_2 = plan_2;
    }

    public String getPlan_3() {
        return plan_3;
    }

    public void setPlan_3(String plan_3) {
        this.plan_3 = plan_3;
    }
}
