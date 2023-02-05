package b2infosoft.milkapp.com.customer_app.customer_pojo;

import java.util.ArrayList;
import java.util.List;

public class BeanDashboardCalenderItem {

    public int day = 0;
    public String status = "";
    public List<BeanDeliveredMilkPlan> milkPlanList = new ArrayList<>();

    public BeanDashboardCalenderItem(int day, String status,
                                     List<BeanDeliveredMilkPlan> milkPlanList) {
        this.day = day;
        this.status = status;
        this.milkPlanList = milkPlanList;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BeanDeliveredMilkPlan> getMilkPlanList() {
        return milkPlanList;
    }

    public void setMilkPlanList(List<BeanDeliveredMilkPlan> milkPlanList) {
        this.milkPlanList = milkPlanList;
    }
}