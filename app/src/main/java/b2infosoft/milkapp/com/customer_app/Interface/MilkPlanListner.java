package b2infosoft.milkapp.com.customer_app.Interface;


import java.util.ArrayList;

import b2infosoft.milkapp.com.customer_app.customer_pojo.BeanDairyMilkPlan;

public interface MilkPlanListner {
    void onAdapterClick(ArrayList<BeanDairyMilkPlan> mList);
}
