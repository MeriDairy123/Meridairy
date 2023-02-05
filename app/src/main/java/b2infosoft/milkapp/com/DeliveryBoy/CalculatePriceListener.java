package b2infosoft.milkapp.com.DeliveryBoy;


import java.util.List;

import b2infosoft.milkapp.com.DeliveryBoy.Model.BeanUserPlan;

public interface CalculatePriceListener {
    void CalculatetotalPrice(double totalPrice, int totalWeight,
                             List<BeanUserPlan> mList);
}
